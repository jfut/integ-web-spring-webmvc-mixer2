package jp.integ.web.mixer2.helper.function;

import org.mixer2.xhtml.AbstractJaxb;

/**
 * 何もしないタグ機能の実装クラスです。
 * 
 * @author Jun Futagawa
 */
public class EmptyTagHelper extends FunctionTagHelper {

	/**
	 * インスタンスを作成します。
	 * 
	 * @param tagType
	 *            対象タグ
	 */
	public EmptyTagHelper(Class<? extends AbstractJaxb> tagType) {
		super(tagType);
	}

	@Override
	public <T extends AbstractJaxb> T replace(String path, String templatePath,
			T parent) {
		return parent;
	}

}
