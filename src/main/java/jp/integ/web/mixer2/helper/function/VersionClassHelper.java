package jp.integ.web.mixer2.helper.function;

import java.util.List;

import jp.integ.web.mixer2.helper.XhtmlHelper;
import jp.integ.web.util.MavenPomUtil;

import org.mixer2.xhtml.AbstractJaxb;

/**
 * バージョンを出力するヘルパー機能です。
 * 
 * @author Jun Futagawa
 */
public class VersionClassHelper extends FunctionClassHelper {

	//
	// ファンクション属性
	//

	/** デフォルトのファンクション名です。 */
	private static final String DEFAULT_NAME = "M_VERSION";

	//
	// オプション
	//

	/**
	 * インスタンスを作成します。
	 */
	public VersionClassHelper() {
		this(DEFAULT_NAME);
	}

	/**
	 * インスタンスを作成します。
	 * 
	 * @param styleClass
	 *            対象クラス名
	 */
	public VersionClassHelper(String styleClass) {
		super(styleClass);
	}

	/**
	 * バージョンを出力します。
	 */
	@Override
	public <T extends AbstractJaxb> T replace(String path, String templatePath,
			T parent) {
		List<AbstractJaxb> abstractJaxbList = parent.getDescendants(name);
		for (AbstractJaxb abstractJaxb : abstractJaxbList) {
			// 対象タグの data-* 属性の名前と値のマップを取得します。
			super.setupDataAttributeMap(abstractJaxb);
			// コンテンツの値を入れ替えます。
			XhtmlHelper.replaceContent(
				abstractJaxb,
				MavenPomUtil.getValue("version", 0));
			abstractJaxb.removeCssClass(name);
		}
		return parent;
	}

}
