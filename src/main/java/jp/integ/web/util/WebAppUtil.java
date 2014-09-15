package jp.integ.web.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspApplicationContext;
import javax.servlet.jsp.JspFactory;

import jp.integ.web.servlet.EmptyServlet;

import org.seasar.util.lang.StringUtil;
import org.seasar.util.log.Logger;
import org.springframework.mock.web.MockServletConfig;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Web アプリケーションに関するユーティリティクラスです。
 * 
 * @author Jun Futagawa
 */
public class WebAppUtil {

	/** ロガーです。 */
	protected static Logger logger = Logger.getLogger(WebAppUtil.class);

	/** パスからインデックスファイルを省略する真偽値です。 */
	public static final boolean REMOVE_INDEX = true;

	/** パスからインデックスファイルを省略しない真偽値です。 */
	public static final boolean NOT_REMOVE_INDEX = false;

	//
	// request
	//

	/**
	 * リクエストを返します。
	 * 
	 * @return リクエスト
	 */
	public static HttpServletRequest getRequest() {
		ServletRequestAttributes sra =
			(ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
		HttpServletRequest request = null;
		if (sra != null) {
			request = sra.getRequest();
		}
		return request;
	}

	/**
	 * リクエストから指定された属性名に対応する値を返します。
	 * 
	 * @param name
	 *            リクエスト属性名
	 * @return リクエストの対応する値
	 */
	public static <T> T getRequestValue(String name) {
		return getRequestValue(getRequest(), name);
	}

	/**
	 * リクエストから指定された属性名に対応する値を返します。
	 * 
	 * @param name
	 *            リクエスト属性名
	 * @return リクエストの対応する値
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getRequestValue(HttpServletRequest request, String name) {
		if (request == null) {
			return null;
		}
		return (T)request.getAttribute(name);
	}

	/**
	 * 指定されたリクエスト属性名に値を設定します。
	 * 
	 * @param name
	 *            リクエスト属性名
	 * @param value
	 *            値
	 */
	public static void setRequestValue(String name, Object value) {
		HttpServletRequest request = getRequest();
		if (request == null) {
			return;
		}
		request.setAttribute(name, value);
	}

	/**
	 * 指定されたリクエスト属性名の値を削除します。
	 * 
	 * @param name
	 *            リクエスト属性名
	 */
	public static void removeRequestValue(String name) {
		HttpServletRequest request = getRequest();
		if (request == null) {
			return;
		}
		request.removeAttribute(name);
	}

	//
	// session
	//

	/**
	 * セッションを返します。
	 * 
	 * @return セッション
	 */
	public static HttpSession getSession() {
		HttpServletRequest request = getRequest();
		if (request == null) {
			return null;
		}
		return request.getSession();
	}

	/**
	 * セッションから指定された属性名に対応する値を返します。
	 * 
	 * @param name
	 *            セッション属性名
	 * @return セッションの対応する値
	 */
	public static <T> T getSessionValue(String name) {
		return getSessionValue(getSession(), name);
	}

	/**
	 * セッションから指定された属性名に対応する値を返します。
	 * 
	 * @param session
	 *            セッション
	 * @param name
	 *            セッション属性名
	 * @return セッションの対応する値
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getSessionValue(HttpSession session, String name) {
		if (session == null) {
			return null;
		}
		return (T)session.getAttribute(name);
	}

	//
	// request and session
	//

	/**
	 * 指定されたスコープにある属性名の値を返します。
	 * 
	 * @param scope
	 *            スコープ
	 * @param name
	 *            属性名
	 * @return 値
	 */
	public static Object getRequestSessionValue(HttpServletRequest request,
			String scope, String name) {
		// スコープが session の場合、セッションに属性名があるかどうかを返します。
		if ("session".equals(scope)) {
			return request.getSession().getAttribute(name);
		}
		// スコープが request の場合、リクエストに属性名があるかどうかを返します。
		else if ("request".equals(scope)) {
			return request.getAttribute(name);
		}
		// 上記以外のスコープ時のデフォルトの挙動です。
		else {
			// リクエストに属性名が存在する場合は、true を返します。
			Object value = getRequestSessionValue(request, "request", name);
			if (value != null) {
				return value;
			}
			// リクエストに存在しない場合は、セッションに属性名が存在するかどうかを返します。
			else {
				return getRequestSessionValue(request, "session", name);
			}
		}
	}

	/** 単純なリクエスト・セッション値取得を表す正規表現です。 */
	private static final Pattern SIMPLE_EXPRESSION_PATTERN =
		Pattern.compile("^\\s*\\$\\{\\s*(sessionScope\\.|requestScope\\.)?\\s*(\\w*)\\s*\\}\\s*$");

	/**
	 * リクエストセッションを利用可能な EL 式を評価した値を返します。
	 * 
	 * @param request
	 *            リクエスト
	 * @param expression
	 *            EL 式
	 * @return 評価結果
	 */
	public static Object getRequestSessionValue(HttpServletRequest request,
			String expression) {
		if (expression == null) {
			return null;
		}
		// expression を評価します。
		Matcher matcher = SIMPLE_EXPRESSION_PATTERN.matcher(expression);
		// 単純なリクエスト・セッション値の取得である場合、
		// パフォーマンスを良くするために EL 式を評価しないで値を返します。
		if (matcher.find() == true) {
			String scope = matcher.group(1);
			String name = matcher.group(2);
			if (StringUtil.isEmpty(scope) == false) {
				if ("sessionScope.".equals(scope) == true) {
					return getSessionValue(request.getSession(), name);
				} else {
					return getRequestValue(request, name);
				}
			}
			return getRequestSessionValue(request, null, name);
		}
		// 複雑な EL 式の場合、評価した値を返します。
		else {
			return getRequestSessionELValue(request, expression);
		}
	}

	/** 単純なリクエスト・セッション値の存在確認を表す正規表現です。 */
	private static final Pattern SIMPLE_EXIST_EXPRESSION_PATTERN =
		Pattern.compile("^\\s*\\$\\{\\s*(!)?(empty)?\\s*(\\sessionScope.|requestScope\\.)?\\s*(\\w*)\\s*\\}\\s*$");

	/**
	 * リクエストセッションを利用可能な EL 式を評価した値を返します。
	 * 
	 * @param name
	 *            リクエスト属性名
	 * @return 存在するかどうか
	 */
	public static boolean getRequestSessionValueAsBoolean(
			HttpServletRequest request, String expression) {
		if (expression == null) {
			return false;
		}
		// expression を評価します。
		Matcher matcher = SIMPLE_EXIST_EXPRESSION_PATTERN.matcher(expression);
		// 単純なリクエスト・セッション値の存在評価である場合、
		// パフォーマンスを良くするために EL 式を評価しないで値を返します。
		if (matcher.find() == true) {
			String reverse = matcher.group(1);
			String scope = matcher.group(3);
			String name = matcher.group(4);
			Object simpleObject = null;
			// スコープ指定がある場合、そのスコープの値を評価します。
			if (StringUtil.isEmpty(scope) == false) {
				if ("sessionScope.".equals(scope) == true) {
					simpleObject = getSessionValue(request.getSession(), name);
				} else {
					simpleObject = getRequestValue(request, name);
				}
			} else {
				simpleObject = getRequestSessionValue(request, null, name);
			}
			boolean isEmpty = true;
			// 値がオブジェクトがあり、中身が空文字ではない場合、結果を false にします。
			if (simpleObject != null
				&& "".equals(simpleObject.toString()) == false) {
				isEmpty = false;
			}
			// 否定記号がある場合、結果を反転させます。
			if ("!".equals(reverse)) {
				isEmpty = !isEmpty;
			}
			return isEmpty;
		}
		// 複雑な EL 式の場合、評価した値を返します。
		else {
			Object result = getRequestSessionELValue(request, expression);
			if (result instanceof Boolean) {
				return ((Boolean)result).booleanValue();
			} else {
				return false;
			}
		}
	}

	/**
	 * リクエストセッションを利用可能な EL 式を評価した値を返します。
	 * 
	 * @param request
	 *            リクエスト
	 * @param expression
	 *            EL 式
	 * @return 評価結果
	 */
	public static Object getRequestSessionELValue(HttpServletRequest request,
			String expression) {
		final JspFactory jspFactory = JspFactory.getDefaultFactory();
		if (jspFactory == null) {
			return null;
		}
		final ServletContext servletContext = new MockServletContext();
		// SingletonS2Container.getComponent(ServletContext.class);
		final JspApplicationContext jspApplicationContext =
			jspFactory.getJspApplicationContext(servletContext);
		final MockServletConfig servletConfig =
			new MockServletConfig(servletContext);
		final Servlet servlet = new EmptyServlet();
		try {
			servlet.init(servletConfig);
			ExpressionFactory ef = jspApplicationContext.getExpressionFactory();
			ELContext elContext =
				jspFactory.getPageContext(
					servlet,
					request,
					null,
					null,
					true,
					8192,
					true).getELContext();
			ValueExpression ve =
				(ValueExpression)ef.createValueExpression(
					elContext,
					expression,
					Object.class);
			return ve.getValue(elContext);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	//
	// path
	//

	/**
	 * パスを返します。
	 * 
	 * @return パス
	 */
	public static String getPath() {
		return getPath(getRequest());
	}

	/**
	 * 要求の URI からコンテキストパスを除いたパスを返します。
	 * 
	 * @param request
	 *            要求
	 * @return コンテキストパスを除いたパス
	 */
	public static String getPath(final HttpServletRequest request) {
		if (request == null) {
			return null;
		}
		final StringBuilder builder = new StringBuilder();
		builder.append(request.getServletPath());
		final String pathInfo = request.getPathInfo();
		if (pathInfo != null) {
			builder.append(pathInfo);
		}
		return builder.toString();
	}

	/**
	 * 指定された 2 つのパスから現在のパスからのターゲットパスへの相対パスを計算して返します。
	 * 
	 * @param path
	 *            現在のパス
	 * @param targetPath
	 *            ターゲットパス
	 * @return 現在のパスからのターゲットパスへの相対パス
	 */
	public static String toRelativePath(String path, String targetPath) {
		return toRelativePath(path, "", targetPath, NOT_REMOVE_INDEX);
	}

	/**
	 * 指定された 2 つのパスから現在のパスからのターゲットパスへの相対パスを計算して返します。
	 * 
	 * @param path
	 *            現在のパス
	 * @param targetPath
	 *            ターゲットパス
	 * @return 現在のパスからのターゲットパスへの相対パス
	 */
	public static String toRelativePath(String path, String targetPath,
			boolean removeIndex) {
		return toRelativePath(path, "", targetPath, removeIndex);
	}

	/**
	 * 指定された 3 つのパスから現在のパスからのターゲットパスへの相対パスを計算して返します。
	 * 
	 * @param path
	 *            現在のパス
	 * @param basePath
	 *            計算元となるベースパス
	 * @param targetPath
	 *            ターゲットパス
	 * @param removeIndex
	 *            インデックスファイルを省略するかどうか
	 * @return 現在のパスからのターゲットパスへの相対パス
	 */
	public static String toRelativePath(String path, String basePath,
			String targetPath) {
		return toRelativePath(path, basePath, targetPath, NOT_REMOVE_INDEX);
	}

	/** 重複している / を表す正規表現です。 */
	private static final Pattern PATH_DUPLICATE_SLASH_PATTERN =
		Pattern.compile("(/)+");

	/** 静的な相対パスを表す正規表現です。 */
	private static final Pattern PATH_STATIC_RELATIVE_PATTERN =
		Pattern.compile("^(|\\.|\\./.*)$");

	/** 動的な相対パスを表す正規表現です。 */
	private static final Pattern PATH_DYNAMIC_RELATIVE_PATTERN =
		Pattern.compile("^(/|\\./)");

	/** 相対的な親ディレクトリを表す正規表現です。 */
	private static final Pattern PATH_RELATIVE_PARENT_PATTERN =
		Pattern.compile("\\.\\./");

	/** インデックスファイルを表す正規表現です。 */
	private static final Pattern PATH_INDEX_FILE_PATTERN =
		Pattern.compile("index\\..*$");

	/**
	 * 指定された 3 つのパスから現在のパスからのターゲットパスへの相対パスを計算して返します。
	 * 
	 * @param path
	 *            現在のパス
	 * @param basePath
	 *            計算元となるベースパス
	 * @param targetPath
	 *            ターゲットパス
	 * @param removeIndex
	 *            インデックスファイルを省略するかどうか
	 * @return 現在のパスからのターゲットパスへの相対パス
	 */
	public static String toRelativePath(String path, String basePath,
			String targetPath, boolean removeIndex) {
		if (basePath == null) {
			return targetPath;
		}
		if (logger.isDebugEnabled() == true) {
			logger.debug("getRelativePath(): [path: "
				+ path
				+ "], [templatePath: "
				+ basePath
				+ "], targetPath: ["
				+ targetPath
				+ "]");
		}

		// 重複している / を 1 つの / に変換します。
		path = PATH_DUPLICATE_SLASH_PATTERN.matcher(path).replaceAll("/");
		basePath =
			PATH_DUPLICATE_SLASH_PATTERN.matcher(basePath).replaceAll("/");
		targetPath =
			PATH_DUPLICATE_SLASH_PATTERN.matcher(targetPath).replaceAll("/");

		// 静的な相対パスはそのまま返します。
		if (PATH_STATIC_RELATIVE_PATTERN.matcher(targetPath).matches() == true) {
			return targetPath;
		}

		// 動的な相対パスを計算します。
		path = PATH_DYNAMIC_RELATIVE_PATTERN.matcher(path).replaceAll("");
		basePath =
			PATH_DYNAMIC_RELATIVE_PATTERN.matcher(basePath).replaceAll("");
		targetPath =
			PATH_DYNAMIC_RELATIVE_PATTERN.matcher(targetPath).replaceAll("");

		// basePath を基に先頭の共通部分をカットします。
		String[] cutPaths = basePath.split("/");
		int cutEvaluatePathsLength = cutPaths.length;
		int cutLength;
		for (int i = 0; i < cutEvaluatePathsLength; i++) {
			String cutPath = cutPaths[i] + "/";
			cutLength = cutPath.length();
			if (path.startsWith(cutPath) && basePath.startsWith(cutPath)) {
				path = path.substring(cutLength);
				basePath = basePath.substring(cutLength);
			} else {
				break;
			}
		}

		// targetPath を基に先頭の共通部分をカットします。
		cutPaths = targetPath.split("/");
		cutEvaluatePathsLength = cutPaths.length;
		for (int i = 0; i < cutEvaluatePathsLength; i++) {
			String cutPath = cutPaths[i] + "/";
			cutLength = cutPath.length();
			if (StringUtil.isEmpty(basePath) == true
				&& path.startsWith(cutPath)
				&& targetPath.startsWith(cutPath)) {
				path = path.substring(cutLength);
				targetPath = targetPath.substring(cutLength);
			} else if (path.startsWith(cutPath)
				&& basePath.startsWith(cutPath)
				&& targetPath.startsWith(cutPath)) {
				path = path.substring(cutLength);
				basePath = basePath.substring(cutLength);
				targetPath = targetPath.substring(cutLength);
			} else {
				break;
			}
		}

		// パスを / で区切った配列の要素数を取得します。
		int pathDepth = path.split("/").length;
		if (path.endsWith("/") == true) {
			pathDepth++;
		}
		int basePathDepth = basePath.split("/").length;
		if (basePath.endsWith("/") == true) {
			basePathDepth++;
		}
		int targetPathDepth = targetPath.split("/").length;
		if (targetPath.endsWith("/") == true) {
			targetPathDepth++;
		}

		// パスを比較して相対パスを組み立てます。
		int diff;
		// ベースパスから現在のパスの差分だけ、../ を取り除きます。
		diff = basePathDepth - pathDepth;
		for (int i = 0; i < diff; i++) {
			targetPath =
				PATH_RELATIVE_PARENT_PATTERN.matcher(targetPath).replaceFirst(
					"");
		}
		// ベースパスから現在のパスの差分だけ、../ を追加します。
		diff = pathDepth - basePathDepth;
		for (int i = 0; i < diff; i++) {
			targetPath = "../" + targetPath;
		}

		// 現在のパスとベースパスが同じ場合、
		// 現在のパスからターゲットパスの差分だけ、../ を追加します。
		if (pathDepth == basePathDepth) {
			diff = pathDepth - targetPathDepth;
			for (int i = 0; i < diff; i++) {
				targetPath = "../" + targetPath;
			}
		}

		// インデックスファイルの除去が true の場合、
		// インデックスファイル名を削除します。
		if (removeIndex == true) {
			targetPath =
				PATH_INDEX_FILE_PATTERN.matcher(targetPath).replaceAll("");
		}

		return targetPath;
	}

	//
	// html
	//

	/**
	 * エスケープされた文字列を返します。
	 * 
	 * @param string
	 *            文字列
	 * @return エスケープされた文字列
	 */
	public static String toEscapedString(String string) {
		if (string == null) {
			return "null";
		}
		return string.replaceAll("&", "&quot;").replaceAll("<", "&lt;").replaceAll(
			">",
			"&gt;").replaceAll("\"", "&amp;").replaceAll("\\\\", "&#39;");
	}

}
