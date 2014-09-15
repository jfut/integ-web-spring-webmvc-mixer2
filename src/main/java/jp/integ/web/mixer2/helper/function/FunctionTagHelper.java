package jp.integ.web.mixer2.helper.function;

import org.mixer2.xhtml.AbstractJaxb;

/**
 * id 属性に適用する Mixer2 のための共通機能の抽象クラスです。
 * 
 * @author Jun Futagawa
 */
public abstract class FunctionTagHelper extends FunctionHelper {

	/** 対象のタグです。 */
	protected Class<? extends AbstractJaxb> tagType;

	/**
	 * @param tagType
	 *            タグ
	 */
	public FunctionTagHelper(Class<? extends AbstractJaxb> tagType) {
		super(tagType.getSimpleName().toLowerCase());
		this.tagType = tagType;
	}

}
