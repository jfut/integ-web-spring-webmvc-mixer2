package jp.integ.web.mixer2.helper.function;

import javax.servlet.http.HttpServletRequest;

import org.mixer2.xhtml.AbstractJaxb;

/**
 * {@link HttpServletRequest} を使用する機能インタフェースです。
 * 
 * @author Jun Futagawa
 */
public interface FunctionRequestHelper {

	/**
	 * 置換処理を実行します。
	 * 
	 * @param path
	 *            処理パス
	 * @param templatePath
	 *            テンプレートパス
	 * @param parent
	 *            基底タグ
	 * @param request
	 *            リクエスト
	 * @return 置換されたタグ
	 */
	public <T extends AbstractJaxb> T replace(String path, String templatePath,
			T parent, HttpServletRequest request);

}
