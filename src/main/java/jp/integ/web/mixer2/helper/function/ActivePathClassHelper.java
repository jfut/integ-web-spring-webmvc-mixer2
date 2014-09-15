package jp.integ.web.mixer2.helper.function;

import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import jp.integ.web.mixer2.helper.DataAttribute;

import org.mixer2.xhtml.AbstractJaxb;
import org.seasar.util.lang.StringUtil;

/**
 * 現在のパスと data-* 属性の値が同じ場合、指定の CSS クラスを追加します。
 * 
 * <pre>
 * <li class="M_ACTIVE_PATH" data-path="/foo/bar/">アクティブリンク</li>
 * <li class="M_ACTIVE_PATH" data-path="/foo/bar/*">正規表現指定アクティブリンク</li>
 * </pre>
 * 
 * @author Jun Futagawa
 */
public class ActivePathClassHelper extends FunctionClassHelper {

	//
	// ファンクション属性
	//

	/** デフォルトのファンクション名です。 */
	private static final String DEFAULT_NAME = "M_ACTIVE_PATH";

	/** デフォルトの追加する CSS 名です。 */
	public static final String DEFAULT_STYLE_CLASS = "active";

	/** パス名を表す data-* 属性名です。 */
	private final DataAttribute DATA_PATH = new DataAttribute("path");

	/** 追加する CSS 名を表す data-* 属性名です。 */
	private final DataAttribute DATA_STYLE_CLASS = new DataAttribute(
		"style-class");

	/** 使用する data-* 属性の配列です。 */
	private final DataAttribute[] dataAttributes = {
		DATA_PATH,
		DATA_STYLE_CLASS };

	//
	// オプション
	//

	/**
	 * インスタンスを作成します。
	 */
	public ActivePathClassHelper() {
		super(DEFAULT_NAME);
	}

	/**
	 * インスタンスを作成します。
	 * 
	 * @param styleClass
	 *            対象クラス名
	 * @param dataPathName
	 *            パス名を表す data-* 属性名
	 * @param dataStyleClass
	 *            追加する CSS 名を表す data-* 属性名
	 */
	public ActivePathClassHelper(String styleClass, String dataPathName,
			String dataStyleClass) {
		super(styleClass);
		DATA_PATH.dataQName = new QName(DATA_PREFIX + dataPathName);
		DATA_STYLE_CLASS.dataQName = new QName(DATA_PREFIX + dataStyleClass);
	}

	/**
	 * 現在のパスと data-* 属性の値が同じ場合、指定の CSS クラスを追加します。
	 */
	@Override
	public <T extends AbstractJaxb> T replace(String path, String templatePath,
			T parent) {
		List<AbstractJaxb> abstractJaxbList = parent.getDescendants(name);
		for (AbstractJaxb abstractJaxb : abstractJaxbList) {
			// 対象タグの data-* 属性の名前と値のマップを取得します。
			Map<String, String> dataAttributeMap =
				super.setupDataAttributeMap(abstractJaxb, dataAttributes);

			String dataPathRegex =
				"^" + dataAttributeMap.get(DATA_PATH.name) + "$";
			String styleClass = dataAttributeMap.get(DATA_STYLE_CLASS.name);
			if (StringUtil.isEmpty(styleClass) == true) {
				styleClass = DEFAULT_STYLE_CLASS;
			}

			// path が dataPathRegex とマッチする場合、CSS 名を追加します。
			if (StringUtil.isEmpty(dataPathRegex) == false
				&& StringUtil.isEmpty(path) == false) {
				if (path.matches(dataPathRegex) == true) {
					abstractJaxb.addCssClass(styleClass);
				}
			}
		}
		return parent;
	}

}
