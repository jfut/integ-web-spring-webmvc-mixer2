package jp.integ.web.spring.view;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jp.integ.web.mixer2.helper.function.FunctionsHelper;
import jp.integ.web.util.WebAppUtil;

import org.mixer2.jaxb.xhtml.Html;
import org.mixer2.spring.webmvc.AbstractMixer2XhtmlView;

/**
 * Mixer2 に対応した抽象ビュークラスです。
 * 
 * @author Jun Futagawa
 */
public abstract class AbstractGenericView extends AbstractMixer2XhtmlView {

	/** renderedHtml を設定しておくリクエスト属性名です。 */
	public static final String RENDERED_HTML_KEY =
		AbstractGenericView.class.getName() + ".renderedHtml";

	@Override
	protected void renderMergedOutputModel(Map<String, Object> model,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String path = WebAppUtil.getPath();
		String templatePath = getUrl();
		Html templateHtml = prerender(model, request, response);

		/** デフォルトの事前処理用共通ヘルパー群を適用します。 */
		templateHtml =
			FunctionsHelper.getDefaultPreInstance().replaceAll(
				path,
				templatePath,
				templateHtml);

		/** アプリケーション固有のレンダリング処理を適用します。 */
		Html renderedHtml = renderHtml(templateHtml, model, request, response);

		/** デフォルトの事後処理用共通ヘルパー群を適用します。 */
		renderedHtml =
			FunctionsHelper.getDefaultPostInstance().replaceAll(
				path,
				templatePath,
				renderedHtml);

		postrender(model, request, response, renderedHtml);
	}

	/**
	 * 事前処理を実行し、テンプレートとなる {@link Html} を返します。
	 * 
	 * @param model
	 *            モデル
	 * @param request
	 *            リクエスト
	 * @param response
	 *            レスポンス
	 * @return {@link Html}
	 * @throws Exception
	 */
	protected Html prerender(Map<String, Object> model,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return mixer2Engine.checkAndLoadHtmlTemplate(resourceLoader.getResource(
			getUrl()).getInputStream());
	}

	/**
	 * 事後処理を実行します。
	 * 
	 * @param model
	 *            モデル
	 * @param request
	 *            リクエスト
	 * @param response
	 *            レスポンス
	 * @param html
	 *            {@link Html}
	 * @throws Exception
	 */
	protected void postrender(Map<String, Object> model,
			HttpServletRequest request, HttpServletResponse response, Html html)
			throws Exception {
		response.setContentType(getContentType());
		responseHtml(html, response);
		if (logger.isDebugEnabled() == true) {
			request.setAttribute(RENDERED_HTML_KEY, html);
		}
	}

	/**
	 * レンダリング後の {@link Html} を返します。
	 * 
	 * @param request
	 *            リクエスト
	 * @return {@link Html}
	 */
	public Html getRenderedHtml(HttpServletRequest request) {
		return (Html)request.getAttribute(RENDERED_HTML_KEY);
	}

}
