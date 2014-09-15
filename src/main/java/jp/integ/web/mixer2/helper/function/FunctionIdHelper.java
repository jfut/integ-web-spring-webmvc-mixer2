package jp.integ.web.mixer2.helper.function;

import java.util.Map;

import jp.integ.web.mixer2.helper.DataAttribute;

import org.mixer2.xhtml.AbstractJaxb;

/**
 * id 属性に適用する Mixer2 のための共通機能の抽象クラスです。
 * 
 * @author Jun Futagawa
 */
public abstract class FunctionIdHelper extends FunctionHelper {

	/**
	 * インスタンスを作成します。
	 * 
	 * @param styleId
	 *            対象 ID 属性名
	 */
	public FunctionIdHelper(String styleId) {
		super(styleId);
	}

	/**
	 * 対象タグの data-* 属性の名前と値のマップを作成して返します。
	 * また、対象タグの ID 属性名と data-* 属性を削除します。
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

		// 不要な対象 ID 属性名と data-* 属性を削除します。
		abstractJaxb.setId(null);

		return dataAttributeMap;
	}

}
