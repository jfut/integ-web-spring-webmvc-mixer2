/*
 * Copyright Integsystem Corporation.
 * All Rights Reserved.
 */
package jp.integ.web.mixer2.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import jp.integ.web.spring.util.SpringBeanUtil;

import org.mixer2.Mixer2Engine;
import org.mixer2.jaxb.xhtml.Html;
import org.mixer2.xhtml.AbstractJaxb;
import org.seasar.util.io.ResourceHandler;
import org.seasar.util.io.ResourceTraversalUtil;
import org.seasar.util.io.ResourceUtil;
import org.seasar.util.lang.StringUtil;
import org.seasar.util.log.Logger;

/**
 * Mixer2 に関するユーティリティです。
 * 
 * @author Jun Futagawa
 */
public class Mixer2Util implements ResourceHandler {

	/** ロガーです。 */
	protected static Logger logger = Logger.getLogger(Mixer2Util.class);

	/** {@link Mixer2Engine} のインスタンスです。 */
	private static Mixer2Engine mixer2Engine = null;

	/** ビューテンプレートディレクトリのパスです。 */
	private static String VIEW_TEMPLATE_DIRECTORY_PATH = null;

	/** ビューテンプレートディレクトリを探す際に使用するリソースファイル名です。 */
	// . にすると Jenkins などの CI 製品でのテスト実行時に http://... に展開されるため、
	// 実在するファイルを指定する必要があります。
	private static final String BUILDDIR_RESOURCE_FILE_NAME = "logback.xml";

	/** ビューテンプレートディレクトリを探す際に使用するキーファイル名です。 */
	private static final String SERACH_VIEW_TEMPLATE_DIRECTORY_FILE_NAME =
		"index.html";

	/**
	 * {@link Mixer2Engine} を返します。
	 * 
	 * @return {@link Mixer2Engine}
	 */
	public static Mixer2Engine getMixer2Engine() {
		if (mixer2Engine == null) {
			setupMixer2EngineBySpring();
			mixer2Engine = getMixer2Engine();
		}
		return mixer2Engine;
	}

	/**
	 * {@link Mixer2Engine} を設定します。
	 * 
	 * @param mixer2Engine
	 *            {@link Mixer2Engine}
	 */
	public static void setMixer2Engine(Mixer2Engine mixer2Engine) {
		Mixer2Util.mixer2Engine = mixer2Engine;
	}

	/**
	 * {@link Mixer2Engine} のインスタンスを new でセットアップします。
	 * 
	 * @return {@link Mixer2Engine}
	 */
	public static void setupMixer2EngineByNewInstance() {
		setupMixer2EngineByNewInstance(false);
	}

	/**
	 * {@link Mixer2Engine} のインスタンスを new でセットアップします。
	 * 
	 * @param forceCreate
	 *            新しいインスタンスを再生成するかどうか
	 * @return {@link Mixer2Engine}
	 */
	public static void setupMixer2EngineByNewInstance(boolean forceCreate) {
		if (forceCreate == true || mixer2Engine == null) {
			setMixer2Engine(new Mixer2Engine());
		}
	}

	/**
	 * {@link Mixer2Engine} のインスタンスを Spring でセットアップします。
	 * 
	 * @return {@link Mixer2Engine}
	 */
	public static void setupMixer2EngineBySpring() {
		setupMixer2EngineBySpring(false);
	}

	/**
	 * {@link Mixer2Engine} のインスタンスを Spring でセットアップします。
	 * 
	 * @param forceCreate
	 *            新しいインスタンスを再生成するかどうか
	 * @return {@link Mixer2Engine}
	 */
	public static void setupMixer2EngineBySpring(boolean force) {
		if (force == true || mixer2Engine == null) {
			setMixer2Engine(SpringBeanUtil.getBean(Mixer2Engine.class));
		}
	}

	/**
	 * テンプレートパスのファイルに対応する Html を返します。
	 * <p>
	 * テンプレートファイルのタイムスタンプを使用して HashMap
	 * にキャッシュして、キャッシュヒットした場合は、Html.copy(Html.class)
	 * を返す実装は、遅くてメモリ消費も大きかったため削除しました。
	 * </p>
	 * 
	 * @param templatePath
	 *            テンプレートパス
	 * @return Html オブジェクト
	 * @throws IOException
	 */
	public static Html getHtml(String templatePath) throws IOException {
		if (StringUtil.isEmpty(templatePath) == true) {
			logger.log("EIntegWeb_0001", "null");
			throw new IOException("Template path is null.");
		}
		// 先頭の / を削除します。
		templatePath = templatePath.replaceAll("^/*", "");
		// 新しい Html を作成して返します。
		mixer2Engine = getMixer2Engine();
		String templateFullPath =
			Mixer2Util.getViewTemplateDirectoryPath() + templatePath;
		File templateFile = new File(templateFullPath);
		if (templateFile.exists() == true) {
			// ファイルが存在する場合は、ファイルから読み込みます。
			if (logger.isDebugEnabled() == true) {
				logger.log("DIntegWeb_0002", templateFullPath);
			}
			return mixer2Engine.loadHtmlTemplate(templateFile);
		} else {
			// ファイルが存在しない場合は、リソースから読み込みます。
			if (ResourceUtil.isExist(templatePath)) {
				if (logger.isDebugEnabled() == true) {
					logger.log("DIntegWeb_0003", templatePath);
				}
				InputStream inputStream =
					ResourceUtil.getResourceAsStream(templatePath);
				return mixer2Engine.loadHtmlTemplate(inputStream);
			}
			logger.log("EIntegWeb_0001", templatePath);
			throw new IOException(templatePath + " is not found.");
		}
	}

	/**
	 * 指定された {@link AbstractJaxb} の HTML 文字列表現を返します。
	 * 
	 * @param tag
	 *            {@link AbstractJaxb}
	 * @return HTML 文字列表現
	 */
	public static <T extends AbstractJaxb> String getHtmlString(T tag) {
		mixer2Engine = getMixer2Engine();
		return mixer2Engine.saveToString(tag);
	}

	/**
	 * ビューテンプレートディレクトリのパスを返します。
	 * 
	 * @return ビューテンプレートディレクトリのパス
	 */
	public static String getViewTemplateDirectoryPath() {
		// 初回呼び出し時に初期化します。
		if (VIEW_TEMPLATE_DIRECTORY_PATH == null) {
			File buildDir =
				ResourceUtil.getBuildDir(BUILDDIR_RESOURCE_FILE_NAME);
			// 本番環境時のパス (/) を設定します。
			if (buildDir.getParent().endsWith("WEB-INF")) {
				// WEB-INF/classes -> WEB-INF
				VIEW_TEMPLATE_DIRECTORY_PATH =
					buildDir.getParentFile().getParent() + File.separator;
			}
			// 開発環境時のパスを設定します。
			// 再帰的に target フォルダ内から探します。
			else {
				// Mixer2Util.processResource() が実行され、探し出します。
				ResourceTraversalUtil.forEach(
					buildDir.getParentFile(),
					new Mixer2Util());
			}
			logger.log("IIntegWeb_0001", VIEW_TEMPLATE_DIRECTORY_PATH);
		}
		return VIEW_TEMPLATE_DIRECTORY_PATH;
	}

	/**
	 * テンプレートディレクトリを探します。
	 * 最も階層の浅いディレクトリに index.html があるディレクトリを
	 * テンプレートディレクトリとします。
	 */
	@Override
	public void processResource(String path, InputStream is) {
		logger.log("DIntegWeb_0001", path);
		if (path.endsWith(SERACH_VIEW_TEMPLATE_DIRECTORY_FILE_NAME)) {
			String newPath =
				ResourceUtil.getBuildDir(BUILDDIR_RESOURCE_FILE_NAME).getParent()
					+ File.separator
					+ path;
			String currentPath =
				VIEW_TEMPLATE_DIRECTORY_PATH
					+ "/"
					+ SERACH_VIEW_TEMPLATE_DIRECTORY_FILE_NAME;
			if (VIEW_TEMPLATE_DIRECTORY_PATH == null
				|| newPath.length() < currentPath.length()) {
				VIEW_TEMPLATE_DIRECTORY_PATH =
					newPath.replace(
						SERACH_VIEW_TEMPLATE_DIRECTORY_FILE_NAME,
						"");
			}
		}
	}

}
