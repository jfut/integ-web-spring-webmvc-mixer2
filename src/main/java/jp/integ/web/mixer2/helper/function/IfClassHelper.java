package jp.integ.web.mixer2.helper.function;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.xml.namespace.QName;

import jp.integ.web.mixer2.helper.DataAttribute;
import jp.integ.web.util.WebAppUtil;

import org.mixer2.xhtml.AbstractJaxb;
import org.seasar.util.exception.FieldNotFoundRuntimeException;

/**
 * EL 式の評価結果によってコンテンツを表示・削除します。
 * 
 * <pre>
 * <span class="M_IF" data-test="${ !empty attributeName }">dummy.content</span>
 * </pre>
 * 
 * @author Jun Futagawa
 */
public class IfClassHelper extends FunctionClassHelper implements
		FunctionRequestHelper {

	//
	// ファンクション属性
	//

	/** デフォルトのファンクション名です。 */
	private static final String DEFAULT_NAME = "M_IF";

	/** EL 式の評価式を表す data-* 属性名です。 */
	private final DataAttribute DATA_TEST = new DataAttribute("test");

	/** 使用する data-* 属性の配列です。 */
	private final DataAttribute[] dataAttributes = { DATA_TEST };

	//
	// オプション
	//

	/**
	 * インスタンスを作成します。
	 */
	public IfClassHelper() {
		super(DEFAULT_NAME);
	}

	/**
	 * インスタンスを作成します。
	 * 
	 * @param styleClass
	 *            対象クラス名
	 * @param dataTest
	 *            data-* 属性名
	 */
	public IfClassHelper(String styleClass, String dataTest) {
		super(styleClass);
		DATA_TEST.dataQName = new QName(DATA_PREFIX + dataTest);
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
	 * 評価結果が true の場合、コンテンツを表示します。
	 * 評価結果が false の場合、コンテンツを削除します。
	 */
	@Override
	public <T extends AbstractJaxb> T replace(String path, String templatePath,
			T parent, HttpServletRequest request) {
		List<AbstractJaxb> abstractJaxbList = parent.getDescendants(name);
		for (AbstractJaxb abstractJaxb : abstractJaxbList) {
			// 対象タグの data-* 属性の名前と値のマップを取得します。
			Map<String, String> dataAttributeMap =
				super.setupDataAttributeMap(abstractJaxb, dataAttributes);
			try {
				// data-* 属性 の値を取得します。
				String dataTest = dataAttributeMap.get(DATA_TEST.name);

				// EL 式を評価します。
				boolean result =
					WebAppUtil.getRequestSessionValueAsBoolean(
						request,
						dataTest);
				// 評価結果が false の場合、コンテンツを削除します。
				if (result == false) {
					parent.remove(abstractJaxb);
				}
			} catch (FieldNotFoundRuntimeException e) {
				// do nothing
			} finally {
				abstractJaxb.removeCssClass(name);
			}
		}
		return parent;
	}

}
