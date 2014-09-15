package jp.integ.web.mixer2.helper.function;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import jp.integ.web.mixer2.helper.DataAttribute;
import jp.integ.web.mixer2.util.Mixer2Util;

import org.mixer2.jaxb.xhtml.Div;
import org.mixer2.jaxb.xhtml.Html;
import org.mixer2.xhtml.AbstractJaxb;
import org.mixer2.xhtml.exception.TagTypeUnmatchException;
import org.seasar.util.log.Logger;

/**
 * コンテンツをインクルードするヘルパー機能です。
 * 
 * @author Jun Futagawa
 */
public class IncludeClassHelper extends FunctionClassHelper {

	/** ロガーです。 */
	private static final Logger logger =
		Logger.getLogger(IncludeClassHelper.class);

	//
	// ファンクション属性
	//

	/** デフォルトのファンクション名です。 */
	private static final String DEFAULT_NAME = "M_INCLUDE";

	/** ソース名を表す data-* 属性名です。 */
	private final DataAttribute DATA_SRC = new DataAttribute("src");

	/** 使用する data-* 属性の配列です。 */
	private final DataAttribute[] dataAttributes = { DATA_SRC };

	//
	// オプション
	//

	/**
	 * インスタンスを作成します。
	 */
	public IncludeClassHelper() {
		super(DEFAULT_NAME);
	}

	/**
	 * インスタンスを作成します。
	 * 
	 * @param styleClass
	 *            対象クラス名
	 * @param dataSrc
	 *            ソース名を表す data-* 属性名
	 */
	public IncludeClassHelper(String styleClass, String dataSrc) {
		super(styleClass);
		DATA_SRC.dataQName = new QName(DATA_PREFIX + dataSrc);
	}

	/**
	 * コンポーネントを処理します。
	 * 
	 * <pre>
	 * - ベースページ側
	 * <div class="COMPONENT" data-src="/component/layout/sidebar.html">...</div>
	 * - コンポーネントページ側
	 * <div id="COMPONENT">...</div>
	 * </pre>
	 * 
	 * TODO: 処理を高速化する場合、コンポーネントページのキャッシュ化を検討します。
	 */
	@Override
	public <T extends AbstractJaxb> T replace(String path, String templatePath,
			T parent) {
		List<Div> divIncludeList = parent.getDescendants(name, Div.class);
		String includeTemplatePath;
		Html includeHtml;
		for (Div divInclude : divIncludeList) {
			// 対象タグの data-* 属性の名前と値のマップを取得します。
			Map<String, String> dataAttributeMap =
				super.setupDataAttributeMap(divInclude, dataAttributes);

			includeTemplatePath = dataAttributeMap.get(DATA_SRC.name);
			if (logger.isDebugEnabled() == true) {
				logger.debug("templatePath: "
					+ templatePath
					+ ", include: "
					+ includeTemplatePath);
			}
			try {
				// インクルードするテンプレートファイルを読み込みます。
				includeHtml = Mixer2Util.getHtml(includeTemplatePath);
				// テンプレートページへ事前処理用ヘルパー群を適用します。
				FunctionsHelper.getDefaultPreInstance().replaceAll(
					path,
					includeTemplatePath,
					includeHtml);
				try {
					// コンポーネントタグの入れ替え処理を実行します。
					divInclude.getContent().clear();
					Div includeHtmlDiv = includeHtml.getById(name, Div.class);
					if (includeHtmlDiv != null) {
						divInclude.getContent().addAll(
							includeHtmlDiv.getContent());
					}
				} catch (TagTypeUnmatchException e) {
					// do nothing.
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return parent;
	}

}
