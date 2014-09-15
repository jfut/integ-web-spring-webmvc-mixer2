package jp.integ.web.util;

import java.io.File;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.seasar.util.io.InputStreamUtil;
import org.seasar.util.io.ResourceHandler;
import org.seasar.util.io.ResourceTraversalUtil;
import org.seasar.util.io.ResourceUtil;
import org.seasar.util.log.Logger;
import org.seasar.util.xml.SAXParserFactoryUtil;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Maven アプリケーションに関するユーティリティです。
 * 
 * @author Jun Futagawa
 */
public class MavenPomUtil implements ResourceHandler {

	/** ロガーです。 */
	protected static Logger logger = Logger.getLogger(MavenPomUtil.class);

	/** pom.xml のパスです。 */
	private static String POM_FILE_PATH = null;

	/** pom.xml のファイル名です。 */
	private static final String POM_FILE_NAME = "pom.xml";

	/** 値のマップです。 */
	private static Map<String, String> valueMap =
		new ConcurrentHashMap<String, String>();

	/** pom.xml を探す際に使用する起点リソースファイルパスです。 */
	// このクラスで検索すると、別アプリケーションからライブラリとして読み込んで
	// 実行した際にリソースファイルのパスが設定されてしまうため、
	// 各アプリケーションが必ず持つファイルを使用して検索する必要があります。
	// . にすると Jenkins などの CI 製品でのテスト実行時に http://... に展開されるため、
	// 実在するファイルを指定する必要があります。
	private static final String BUILD_DIR_RESOURCE_FILE_PATH = "logback.xml";

	/**
	 * pom.xml から指定された名前の 0 番目の値を返します。
	 * 
	 * @param name
	 *            名前
	 * @return pom.xml の対応する値
	 */
	public static String getValue(String name) {
		return getValue(name, 0);
	}

	/**
	 * pom.xml から指定された名前のインデックス番目の値を返します。
	 * 
	 * @param name
	 *            名前
	 * @param index
	 *            インデックス
	 * @return pom.xml の対応する値
	 */
	public static String getValue(String name, int index) {
		if (name == null) {
			return null;
		}
		String key = name + "_" + index;
		String value = valueMap.get(key);
		// 初回呼び出し時に初期化します。
		if (value == null) {
			File buildDir =
				ResourceUtil.getBuildDir(BUILD_DIR_RESOURCE_FILE_PATH);
			// 検索ルートです。
			File searchRootDirFile;
			// 本番環境時の検索ルート (/) を設定します。
			if (buildDir.getParent().endsWith("WEB-INF")) {
				// /WEB-INF/classes -> /META-INF
				searchRootDirFile =
					new File(
						buildDir.getParentFile().getParentFile(),
						"META-INF");
				// 再帰的にフォルダ内から探します。
				// MavenAppVersionUtil.processResource() が実行され、
				// POM_FILE_PATH に相対パスが保存されます。
				ResourceTraversalUtil.forEach(
					searchRootDirFile,
					new MavenPomUtil());

				// pom.xml ファイルをパースします。
				parseVersion(valueMap, new File(
					searchRootDirFile,
					POM_FILE_PATH), name, index);
			}
			// 開発環境時の検索ルートを設定します。
			else {
				// /app_dir/target/test-classes -> /app_dir/target
				searchRootDirFile = buildDir.getParentFile();
				// /app_dir/pom.xml
				File pomFile =
					new File(searchRootDirFile.getParentFile(), POM_FILE_NAME);
				if (pomFile.exists() == true) {
					// pom.xml ファイルが見つかった場合、パースします。
					parseVersion(valueMap, pomFile, name, index);
				}
			}

			// 何度も実行しないようにバージョンを決定できなかった場合は、
			// バージョンを空文字にします。
			value = valueMap.get(key);
			if (value == null) {
				valueMap.put(key, "");
			}
		}
		return value;
	}

	/**
	 * 指定された pom.xml をパースして、バージョンを取得・設定します。
	 * 
	 * @param pomFilePath
	 *            pom.xml ファイルのパス
	 */
	public static void parseVersion(final Map<String, String> valueMap,
			final File pomFile, final String name, final int index) {
		logger.log("IIntegWeb_0011", pomFile.getAbsolutePath());
		final String key = name + "_" + index;
		try {
			SAXParserFactory spf = SAXParserFactoryUtil.newInstance();
			SAXParserFactoryUtil.setXIncludeAware(spf, true);
			spf.setNamespaceAware(true);
			SAXParser parser = SAXParserFactoryUtil.newSAXParser(spf);

			InputSource is = new InputSource(InputStreamUtil.create(pomFile));

			parser.parse(is, new DefaultHandler() {
				private boolean isVersion = false;

				private int count = 0;

				@Override
				public void startElement(String uri, String localName,
						String qName, Attributes attributes)
						throws SAXException {
					// 一番最初に見つかったバージョンをアプリケーションのバージョンとして採用します。
					if (name.equals(qName) == true) {
						if (key.equals(qName + "_" + count)) {
							isVersion = true;
						}
						count++;
					}
				}

				@Override
				public void characters(char[] ch, int offset, int length) {
					if (isVersion == true) {
						valueMap.put(key, new String(ch, offset, length));
						isVersion = false;
					}
				}
			});
		} catch (Exception e) {
			if (logger.isDebugEnabled() == true) {
				valueMap.put(key, e.getMessage());
			}
		}
		logger.log("IIntegWeb_0012", valueMap.get(key));
	}

	/**
	 * pom.xml ファイルを探します。
	 */
	@Override
	public void processResource(String path, InputStream is) {
		logger.log("DIntegWeb_0011", path);
		if (path.endsWith(POM_FILE_NAME) == true) {
			POM_FILE_PATH = path;
		}
	}

}
