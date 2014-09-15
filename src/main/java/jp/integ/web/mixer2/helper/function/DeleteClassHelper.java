package jp.integ.web.mixer2.helper.function;

import org.mixer2.xhtml.AbstractJaxb;

/**
 * タグごと削除するヘルパー機能です。
 * 
 * @author Jun Futagawa
 */
public class DeleteClassHelper extends FunctionClassHelper {

	//
	// ファンクション属性
	//

	/** デフォルトのファンクション名です。 */
	private static final String DEFAULT_NAME = "M_DELETE";

	//
	// オプション
	//

	/**
	 * インスタンスを作成します。
	 */
	public DeleteClassHelper() {
		this(DEFAULT_NAME);
	}

	/**
	 * インスタンスを作成します。
	 * 
	 * @param styleClass
	 *            対象クラス名
	 */
	public DeleteClassHelper(String styleClass) {
		super(styleClass);
	}

	@Override
	public <T extends AbstractJaxb> T replace(String path, String templatePath,
			T parent) {
		parent.removeDescendants(name);
		return parent;
	}

}
