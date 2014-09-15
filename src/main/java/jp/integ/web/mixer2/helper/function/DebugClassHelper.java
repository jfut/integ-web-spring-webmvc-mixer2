package jp.integ.web.mixer2.helper.function;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import jp.integ.web.mixer2.helper.DataAttribute;
import jp.integ.web.mixer2.util.Mixer2Util;

import org.mixer2.jaxb.xhtml.A;
import org.mixer2.jaxb.xhtml.Div;
import org.mixer2.jaxb.xhtml.Html;
import org.mixer2.xhtml.AbstractJaxb;
import org.mixer2.xhtml.TagCreator;
import org.mixer2.xhtml.exception.TagTypeUnmatchException;
import org.seasar.util.lang.StringUtil;
import org.seasar.util.log.Logger;
import org.seasar.util.net.UuidUtil;

/**
 * デバッグ情報を作成・削除するヘルパー機能です。
 * デバッグモード時にのみ情報を作成します。
 * 
 * @author Jun Futagawa
 */
public class DebugClassHelper extends FunctionClassHelper {

	/** ルートロガーです。 */
	private static final Logger rootLogger = Logger.getLogger(Object.class);

	//
	// ファンクション属性
	//

	/** デフォルトのファンクション名です。 */
	private static final String DEFAULT_NAME = "M_DEBUG";

	/** デフォルトの追加するタイトル名です。 */
	public static final String DEFAULT_TITLE = "[DEBUG]";

	/** フィールド前を表す data-* 属性名です。 */
	private final DataAttribute DATA_TITLE = new DataAttribute("title");

	/** 使用する data-* 属性の配列です。 */
	private final DataAttribute[] dataAttributes = { DATA_TITLE };

	//
	// オプション
	//

	/** テンプレートファイルのパスです。 */
	private static final String DEBUG_PART_TEMPLATE =
		"/integ/mixer2/debug/debug_part.html";

	private static final String HERE_DEBUG_ID = "HERE.debug";

	/**
	 * インスタンスを作成します。
	 */
	public DebugClassHelper() {
		super(DEFAULT_NAME);
	}

	/**
	 * インスタンスを作成します。
	 * 
	 * @param styleClass
	 *            対象クラス名
	 * @param dataTitleName
	 *            data-* 属性名
	 */
	public DebugClassHelper(String styleClass, String dataTitleName) {
		super(styleClass);
		DATA_TITLE.dataQName = new QName(DATA_PREFIX + dataTitleName);
	}

	@Override
	public <T extends AbstractJaxb> T replace(String path, String templatePath,
			T parent) {
		if (rootLogger.isDebugEnabled() == false) {
			parent.removeDescendants(name);
			return parent;
		}

		List<Div> divList = parent.getDescendants(name);
		for (Div div : divList) {
			// 対象タグの data-* 属性の名前と値のマップを取得します。
			Map<String, String> dataAttributeMap =
				super.setupDataAttributeMap(div, dataAttributes);

			// title を処理します。
			String title = dataAttributeMap.get(DATA_TITLE.name);
			if (StringUtil.isEmpty(title) == true) {
				title = DEFAULT_TITLE;
			}

			// デバッグ欄を作成します。デバッグ箇所ごとに accordion-body の id を
			// 変えるため、毎回呼び出す必要があります。
			Div debugDiv = getDebugDiv(path, title, div.getContent());
			// 置換前のタグが持つ id と class を置換後のタグへ設定します。
			debugDiv.setId(div.getId());
			debugDiv.getCssClass().addAll(0, div.getCssClass());
			// コンテンツを入れ替えます。
			try {
				parent.replace(div, debugDiv);
			} catch (TagTypeUnmatchException e) {
				// do nothing.
			}
		}

		return parent;
	}

	public static Div getDebugDiv(String path, String title,
			List<Object> content) {
		Div debugDiv = TagCreator.div();
		Html debugPartHtml;
		try {
			// テンプレートファイルを読み込みます。
			debugPartHtml = Mixer2Util.getHtml(DEBUG_PART_TEMPLATE);
			// テンプレートページへ事前処理用ヘルパー群を適用します。
			FunctionsHelper.getDefaultPreInstance().replaceAll(
				path,
				DEBUG_PART_TEMPLATE,
				debugPartHtml);

			debugDiv =
				debugPartHtml.getById(HERE_DEBUG_ID).getDescendants(Div.class).get(
					1);
			debugDiv.setId(null);
			String uuid = UuidUtil.create();

			// コンテンツを組み立てます。
			setupCollapseDiv(debugDiv, uuid, title, content);
		} catch (IOException e) {
			// do nothing
		}
		return debugDiv;
	}

	public static void setupCollapseDiv(Div panelCollapseDiv, String id,
			String title, List<Object> content) {
		// toggle を処理します。
		A panelTitleA = panelCollapseDiv.getById("HERE.panel-title");
		panelTitleA.setId(null);
		panelTitleA.getContent().clear();
		panelTitleA.getContent().add(title);
		panelTitleA.setHref("#debug_" + id);

		// body を処理します。
		Div bodyDiv = panelCollapseDiv.getById("HERE.panel-collapse");
		bodyDiv.setId("debug_" + id);

		// inner を処理します。
		Div panelBodyDiv = bodyDiv.getById("HERE.panel-body");
		panelBodyDiv.setId(null);
		panelBodyDiv.getContent().clear();
		panelBodyDiv.getContent().addAll(content);
	}

}
