package jp.integ.web.spring.util;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.DispatcherServlet;

/**
 * Spring が管理している Bean のインスタンスを取得するユーティリティクラスです。
 * Bean 定義を XML ファイルではなく、Spring Boot などのアノテーション使用時にも動作します。
 * HttpServletRequest に依存しているため、Web Application 以外での使用は考慮していません。
 * 
 * @author Jun Futagawa
 */
public class SpringBeanUtil {

	/** {@link ApplicationContext} です。 */
	private ApplicationContext applicationContext;

	/** 唯一のインスタンスです。 */
	private static SpringBeanUtil instance;

	/**
	 * インスタンスを作成します。
	 */
	public SpringBeanUtil() {
		setupApplicationContext();
	}

	/**
	 * {@link ApplicationContext} をセットアップします。
	 */
	protected void setupApplicationContext() {
		HttpServletRequest request = getRequest();
		if (request == null) {
			throw new UnsupportedOperationException();
		}
		setApplicationContext((ApplicationContext)request.getAttribute(DispatcherServlet.WEB_APPLICATION_CONTEXT_ATTRIBUTE));
	}

	/**
	 * 唯一のインスタンスを返します。
	 * 
	 * @return {@link SpringBeanUtil}
	 */
	public static synchronized SpringBeanUtil getInstance() {
		if (instance == null) {
			instance = new SpringBeanUtil();
		}
		return instance;
	}

	/**
	 * {@link ApplicationContext} を返します。
	 * 
	 * @return {@link ApplicationContext}
	 */
	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	/**
	 * {@link ApplicationContext} を設定します。
	 */
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	/**
	 * 指定したクラスの Spring が管理しているインスタンスを返します。
	 * 
	 * @param clazz
	 *            クラス
	 * @return Spring が管理しているインスタンス
	 */
	public static <T> T getBean(Class<T> clazz) {
		return getInstance().applicationContext.getBean(clazz);
	}

	/**
	 * Spring が管理している {@link HttpServletRequest} を返します。
	 * 
	 * @return {@link HttpServletRequest}
	 */
	protected static HttpServletRequest getRequest() {
		ServletRequestAttributes servletRequestAttributes =
			(ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
		HttpServletRequest request = null;
		if (servletRequestAttributes != null) {
			request = servletRequestAttributes.getRequest();
		}
		return request;
	}

}
