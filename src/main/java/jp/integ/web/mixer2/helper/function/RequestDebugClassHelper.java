package jp.integ.web.mixer2.helper.function;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.namespace.QName;

import jp.integ.web.mixer2.helper.ComponentBootstrapHelper;
import jp.integ.web.mixer2.helper.DataAttribute;
import jp.integ.web.mixer2.util.Mixer2Util;
import jp.integ.web.util.WebAppUtil;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.mixer2.jaxb.xhtml.Div;
import org.mixer2.jaxb.xhtml.Html;
import org.mixer2.jaxb.xhtml.P;
import org.mixer2.jaxb.xhtml.Strong;
import org.mixer2.xhtml.AbstractJaxb;
import org.mixer2.xhtml.TagCreator;
import org.mixer2.xhtml.exception.TagTypeUnmatchException;
import org.seasar.util.collection.CollectionsUtil;
import org.seasar.util.lang.StringUtil;
import org.seasar.util.net.UuidUtil;

/**
 * リクエスト情報の値をすべて出力します。
 * 
 * @author Jun Futagawa
 */
public class RequestDebugClassHelper extends FunctionClassHelper implements
		FunctionRequestHelper {

	//
	// ファンクション属性
	//

	/** デフォルトのファンクション名です。 */
	private static final String DEFAULT_NAME = "M_REQUEST_DEBUG";

	/** タイトル名を表す data-* 属性名です。 */
	private final DataAttribute DATA_TITLE = new DataAttribute("title");

	/** 使用する data-* 属性の配列です。 */
	private final DataAttribute[] dataAttributes = { DATA_TITLE };

	//
	// オプション
	//

	/** テンプレートファイルのパスです。 */
	private static final String REQUEST_DEBUG_PART_TEMPLATE =
		"/integ/mixer2/request/debug_part.html";

	private static final String HERE_REQUEST_DEBUG_ID = "HERE.request.debug";

	public static final String DEFAULT_TITLE = "[REQUEST DEBUG]";

	/**
	 * インスタンスを作成します。
	 */
	public RequestDebugClassHelper() {
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
	public RequestDebugClassHelper(String styleClass, String dataTitleName) {
		super(styleClass);
		DATA_TITLE.dataQName = new QName(DATA_PREFIX + dataTitleName);
	}

	@Override
	public <T extends AbstractJaxb> T replace(String path, String templatePath,
			T parent) {
		HttpServletRequest request = WebAppUtil.getRequest();
		if (request == null) {
			return parent;
		}
		return replace(path, templatePath, parent, request);
	}

	/**
	 * リクエスト情報の値をすべて出力します。
	 */
	@Override
	public <T extends AbstractJaxb> T replace(String path, String templatePath,
			T parent, HttpServletRequest request) {
		List<AbstractJaxb> tagList = parent.getDescendants(name);
		if (tagList.size() == 0) {
			return parent;
		}

		for (AbstractJaxb abstractJaxb : tagList) {
			// 対象タグの data-* 属性の名前と値のマップを取得します。
			Map<String, String> dataAttributeMap =
				super.setupDataAttributeMap(abstractJaxb, dataAttributes);

			// title を処理します。
			String title = dataAttributeMap.get(DATA_TITLE.name);
			if (StringUtil.isEmpty(title) == true) {
				title = DEFAULT_TITLE;
			}

			// デバッグ欄を作成します。デバッグ蘭ごとに accordion-body の id を
			// 変えるため、毎回呼び出す必要があります。
			Div debugDiv =
				getRequestDebugDiv(
					path,
					title,
					createRequestDebugContentDiv(request, name).getContent());
			// 置換前のタグが持つ id と class を置換後のタグへ設定します。
			debugDiv.setId(abstractJaxb.getId());
			debugDiv.getCssClass().addAll(0, abstractJaxb.getCssClass());
			// コンテンツを入れ替えます。
			try {
				parent.replace(abstractJaxb, debugDiv);
			} catch (TagTypeUnmatchException e) {
				// do nothing.
			}
		}
		return parent;
	}

	/**
	 * リクエストデバッグの Div タグを作成して返します。
	 * 
	 * @param path
	 *            パス
	 * @param title
	 *            タイトル
	 * @param content
	 *            コンテンツ
	 * @return リクエストデバッグの Div タグ
	 */
	public static Div getRequestDebugDiv(String path, String title,
			List<Object> content) {
		Div debugDiv = TagCreator.div();
		Html debugPartHtml;
		try {
			// テンプレートファイルを読み込みます。
			debugPartHtml = Mixer2Util.getHtml(REQUEST_DEBUG_PART_TEMPLATE);
			// テンプレートページへ事前処理用ヘルパー群を適用します。
			FunctionsHelper.getDefaultPreInstance().replaceAll(
				path,
				REQUEST_DEBUG_PART_TEMPLATE,
				debugPartHtml);

			debugDiv =
				debugPartHtml.getById(HERE_REQUEST_DEBUG_ID).getDescendants(
					Div.class).get(1);
			debugDiv.setId(null);
			String uuid = UuidUtil.create();

			DebugClassHelper.setupCollapseDiv(debugDiv, uuid, title, content);
		} catch (IOException e) {
			// do nothing
		}
		return debugDiv;
	}

	/**
	 * リクエストのデバッグ情報のコンテンツ Div を作成して返します。
	 * 
	 * @param request
	 *            リクエスト
	 * @param styleId
	 *            タグ ID
	 * @return リクエストのデバッグ情報のコンテンツ Div
	 */
	public static Div createRequestDebugContentDiv(HttpServletRequest request,
			String styleId) {
		// リクエスト情報のコンテンツを作成します。
		Div requestDebugContentDiv = TagCreator.divWithId(styleId);

		Enumeration<?> names;
		List<String> nameList;
		Object valueObject;

		// request の出力タグを作成します。
		P requestP = TagCreator.p();
		{
			Strong attributeStrong = TagCreator.strong();
			attributeStrong.getContent().add("Request Attribute");
			requestP.getContent().add(attributeStrong);
		}
		requestDebugContentDiv.getContent().add(requestP);
		Div requestCollapseAccordiionDiv =
			ComponentBootstrapHelper.collapsePanelGroupDiv();
		{
			names = request.getAttributeNames();
			nameList = CollectionsUtil.newArrayList();
			while (names.hasMoreElements()) {
				nameList.add(String.valueOf(names.nextElement()));
			}
			Collections.sort(nameList);
			for (String name : nameList) {
				valueObject = request.getAttribute(name);
				requestCollapseAccordiionDiv.getContent().add(
					createValueCollapseGroupDiv(name, valueObject));
			}
		}
		requestDebugContentDiv.getContent().add(requestCollapseAccordiionDiv);

		// session の出力タグを作成します。
		HttpSession session = request.getSession();
		P sessionP = TagCreator.p();
		{
			Strong attributeStrong = TagCreator.strong();
			attributeStrong.getContent().add("Session Attribute");
			sessionP.getContent().add(attributeStrong);
		}
		requestDebugContentDiv.getContent().add(sessionP);
		Div sessionCollapseAccordiionDiv =
			ComponentBootstrapHelper.collapsePanelGroupDiv();
		{
			names = session.getAttributeNames();
			nameList = CollectionsUtil.newArrayList();
			while (names.hasMoreElements()) {
				nameList.add(String.valueOf(names.nextElement()));
			}
			Collections.sort(nameList);
			for (String name : nameList) {
				valueObject = session.getAttribute(name);
				sessionCollapseAccordiionDiv.getContent().add(
					createValueCollapseGroupDiv(name, valueObject));
			}
		}
		requestDebugContentDiv.getContent().add(sessionCollapseAccordiionDiv);

		return requestDebugContentDiv;
	}

	/**
	 * デバッグ情報のグループ Div を作成して返します。
	 * 
	 * @param name
	 *            名前
	 * @param value
	 *            値
	 * @return デバッグ情報のグループ Div
	 */
	public static Div createValueCollapseGroupDiv(String name, Object value) {
		// コンテンツを作成します。
		String title =
			name + ": " + WebAppUtil.toEscapedString(String.valueOf(value));
		// 詳細表示の Collapse group を作成します。
		String id = "request_" + UuidUtil.create();
		Div collapseGroupDiv =
			ComponentBootstrapHelper.collapsePanelDiv(
				id,
				title,
				WebAppUtil.toEscapedString(ToStringBuilder.reflectionToString(value)),
				"panel-info");
		return collapseGroupDiv;
	}

}
