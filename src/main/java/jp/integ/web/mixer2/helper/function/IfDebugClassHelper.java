package jp.integ.web.mixer2.helper.function;

import java.util.List;

import org.mixer2.xhtml.AbstractJaxb;
import org.seasar.util.log.Logger;

/**
 * デバッグ表示欄を作成・削除するヘルパー機能です。
 * デバッグモード時にのみ表示欄を作成します。
 * 
 * @author Jun Futagawa
 */
public class IfDebugClassHelper extends FunctionClassHelper {

	//
	// ファンクション属性
	//

	/** デフォルトのファンクション名です。 */
	private static final String DEFAULT_NAME = "M_IF_DEBUG";

	//
	// オプション
	//

	/** ルートロガーです。 */
	private static final Logger rootLogger = Logger.getLogger(Object.class);

	/**
	 * インスタンスを作成します。
	 */
	public IfDebugClassHelper() {
		super(DEFAULT_NAME);
	}

	/**
	 * インスタンスを作成します。
	 * 
	 * @param styleClass
	 *            対象クラス名
	 */
	public IfDebugClassHelper(String styleClass) {
		super(styleClass);
	}

	/**
	 * デバッグモードではない時はタグごと削除します。
	 */
	@Override
	public <T extends AbstractJaxb> T replace(String path, String templatePath,
			T parent) {
		// デバッグモードではない時はタグごと削除します。
		if (rootLogger.isDebugEnabled() == false) {
			parent.removeDescendants(name);
			return parent;
		} else {
			List<AbstractJaxb> abstractJaxbList = parent.getDescendants(name);
			for (AbstractJaxb abstractJaxb : abstractJaxbList) {
				// 対象タグの data-* 属性の名前と値のマップを取得します。
				super.setupDataAttributeMap(abstractJaxb);
			}
			return parent;
		}
	}

}
