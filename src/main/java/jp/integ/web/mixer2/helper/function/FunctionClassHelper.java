package jp.integ.web.mixer2.helper.function;

import java.util.Map;

import jp.integ.web.mixer2.helper.DataAttribute;

import org.mixer2.xhtml.AbstractJaxb;

/**
 * class 属性に適用する Mixer2 のための共通機能の抽象クラスです。
 * 
 * @author Jun Futagawa
 */
public abstract class FunctionClassHelper extends FunctionHelper {

	/**
	 * @param styleClass
	 *            対象クラス属性名
	 */
	public FunctionClassHelper(String styleClass) {
		super(styleClass);
	}

	/**
	 * 対象タグの data-* 属性の名前と値のマップを作成して返します。
	 * また、対象タグのクラス属性名と data-* 属性を削除します。
	 * 
	 * @param abstractJaxb
	 *            対象タグ
	 * @param dataAttributes
	 *            {@link DataAttribute} の配列
	 * @return 対象タグの data-* 属性の名前と値のマップ
	 */
	@Override
	public Map<String, String> setupDataAttributeMap(AbstractJaxb abstractJaxb,
			DataAttribute... dataAttributes) {
		// 対象タグの data-* 属性の名前と値のマップを作成します。
		Map<String, String> dataAttributeMap =
			super.setupDataAttributeMap(abstractJaxb, dataAttributes);

		// 不要な対象クラス属性名と data-* 属性を削除します。
		abstractJaxb.removeCssClass(super.name);

		return dataAttributeMap;
	}

}
