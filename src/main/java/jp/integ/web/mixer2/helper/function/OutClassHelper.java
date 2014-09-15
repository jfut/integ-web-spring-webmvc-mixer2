package jp.integ.web.mixer2.helper.function;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.xml.namespace.QName;

import jp.integ.web.mixer2.helper.DataAttribute;
import jp.integ.web.mixer2.helper.XhtmlHelper;
import jp.integ.web.util.WebAppUtil;

import org.mixer2.xhtml.AbstractJaxb;
import org.seasar.util.exception.FieldNotFoundRuntimeException;
import org.seasar.util.lang.StringUtil;

/**
 * リクエスト情報が持つ指定された EL 式の評価結果を出力します。
 * 
 * <pre>
 * <span class="M_OUT" data-value="${ javax.servlet.forward.servlet_path }">/dummy/</span>
 * </pre>
 * 
 * @author Jun Futagawa
 */
public class OutClassHelper extends FunctionClassHelper implements
		FunctionRequestHelper {

	//
	// ファンクション属性
	//

	/** デフォルトのファンクション名です。 */
	private static final String DEFAULT_NAME = "M_OUT";

	/** EL 式の値を表す data-* 属性名です。 */
	private final DataAttribute DATA_VALUE = new DataAttribute("value");

	/** value が null の場合の値を表す data-* 属性名です。 */
	private final DataAttribute DATA_DEFAULT = new DataAttribute("default");

	/** HTML の特殊文字を置換するかしないかを表す data-* 属性名です。 */
	private final DataAttribute DATA_ESCAPE_XML =
		new DataAttribute("escapeXml");

	/** 使用する data-* 属性の配列です。 */
	private final DataAttribute[] dataAttributes = {
		DATA_VALUE,
		DATA_DEFAULT,
		DATA_ESCAPE_XML };

	//
	// オプション
	//

	/**
	 * インスタンスを作成します。
	 */
	public OutClassHelper() {
		super(DEFAULT_NAME);
	}

	/**
	 * インスタンスを作成します。
	 * 
	 * @param styleClass
	 *            対象クラス名
	 * @param dataName
	 *            data-* 属性名
	 * @param dataScopeName
	 *            data-* 属性名
	 * @param dataEscapeXml
	 *            data-* 属性名
	 */
	public OutClassHelper(String styleClass, String dataName,
			String dataScopeName, String dataEscapeXml) {
		super(styleClass);
		DATA_VALUE.dataQName = new QName(DATA_PREFIX + dataName);
		DATA_DEFAULT.dataQName = new QName(DATA_PREFIX + dataScopeName);
		DATA_ESCAPE_XML.dataQName = new QName(DATA_PREFIX + dataEscapeXml);
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
	 * EL 式の評価結果を出力します。
	 */
	@Override
	public <T extends AbstractJaxb> T replace(String path, String templatePath,
			T html, HttpServletRequest request) {
		List<AbstractJaxb> abstractJaxbList = html.getDescendants(name);
		for (AbstractJaxb abstractJaxb : abstractJaxbList) {
			// 対象タグの data-* 属性の名前と値のマップを取得します。
			Map<String, String> dataAttributeMap =
				super.setupDataAttributeMap(abstractJaxb, dataAttributes);
			try {
				// 出力する文字列オブジェクトです。
				Object replaceContent = null;

				// data-* 属性 の値を取得します。
				String dataValue = dataAttributeMap.get(DATA_VALUE.name);
				if (dataValue != null) {
					String dataEscapeXml =
						dataAttributeMap.get(DATA_ESCAPE_XML.name);
					boolean dataEscapeXmlAsBoolean = true;
					if (StringUtil.isEmpty(dataEscapeXml) == false) {
						dataEscapeXmlAsBoolean =
							Boolean.parseBoolean(dataEscapeXml);
					}

					// EL 式を評価します。
					if (StringUtil.isEmpty(dataValue) == false) {
						replaceContent =
							WebAppUtil.getRequestSessionValue(
								request,
								dataValue);
						// data-value の評価結果が null の場合、data-default の値を出力します。
						if (replaceContent == null) {
							replaceContent =
								dataAttributeMap.get(DATA_DEFAULT.name);
						}
						// 結果が null ではなく、data-escapeXml の値が true の場合、
						// HTML 文字をエスケープします。
						if (replaceContent != null
							&& dataEscapeXmlAsBoolean == true) {
							replaceContent =
								WebAppUtil.toEscapedString(replaceContent.toString());
						}
					}
				}
				// content の内容を置換します。
				XhtmlHelper.replaceContent(abstractJaxb, replaceContent);
			} catch (FieldNotFoundRuntimeException e) {
				// do nothing
			} finally {
				abstractJaxb.removeCssClass(name);
			}
		}
		return html;
	}

}
