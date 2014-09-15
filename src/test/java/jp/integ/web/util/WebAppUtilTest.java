package jp.integ.web.util;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspFactory;

import org.apache.jasper.runtime.JspFactoryImpl;
import org.junit.Before;
import org.junit.Test;
import org.seasar.util.collection.CollectionsUtil;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * @author Jun Futagawa
 */
public class WebAppUtilTest {

	@Before
	public void setUp() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(
			request));
	}

	@Test
	public void testGetRequest() throws Exception {
		assertNotNull(WebAppUtil.getRequest());
	}

	@Test
	public void testGetRequestValue() throws Exception {
		HttpServletRequest request = WebAppUtil.getRequest();
		assertThat(WebAppUtil.getRequestValue(request, null), is(nullValue()));
		assertThat(WebAppUtil.getRequestValue(request, ""), is(nullValue()));

		assertThat(
			WebAppUtil.getRequestValue(request, "not_found1"),
			is(nullValue()));

		request.setAttribute("foo", "bar");
		String result = WebAppUtil.getRequestValue(request, "foo");
		assertThat(result, is("bar"));

		assertThat(
			WebAppUtil.getRequestValue(request, "not_found2"),
			is(nullValue()));
	}

	// request and session

	@Test
	public void testGetRequestSessionValue() {
		// テスト時に EL 式の評価実行に必要です。
		JspFactory.setDefaultFactory(new JspFactoryImpl());

		// request
		HttpServletRequest request = WebAppUtil.getRequest();
		{
			request.setAttribute("foo", "request-foo\"<br />\"");
			request.setAttribute("bar", "request-bar");
			List<String> hogeList = CollectionsUtil.newArrayList();
			hogeList.add("request-hoge1");
			hogeList.add("request-hoge2");
			request.setAttribute("hogeList", hogeList);
		}

		// session
		HttpSession session = request.getSession();
		{
			session.setAttribute("foo", "session-foo\"<br />\"");
			session.setAttribute("hoge", "session-hoge");
			List<String> hogeList = CollectionsUtil.newArrayList();
			hogeList.add("session-hoge1");
			hogeList.add("session-hoge2");
			session.setAttribute("hogeList", hogeList);
		}

		// scope: defult (requestScope, sessionScope), null
		assertThat(
			String.valueOf(WebAppUtil.getRequestSessionValue(request, null)),
			is("null"));
		assertThat(
			String.valueOf(WebAppUtil.getRequestSessionValue(request, "")),
			is(""));
		assertThat(
			String.valueOf(WebAppUtil.getRequestSessionValue(request, "${}")),
			is("null"));

		// scope: defult (requestScope, sessionScope), request: foo, bar,
		// hogeList
		assertThat(String.valueOf(WebAppUtil.getRequestSessionValue(
			request,
			"${ foo }")), is("request-foo\"<br />\""));
		assertThat(String.valueOf(WebAppUtil.getRequestSessionValue(
			request,
			"aa${ bar }bb")), is("aarequest-barbb"));
		assertThat(String.valueOf(WebAppUtil.getRequestSessionValue(
			request,
			"${ hoge }")), is("session-hoge"));
		assertThat(String.valueOf(WebAppUtil.getRequestSessionValue(
			request,
			"${ hogeList }")), is("[request-hoge1, request-hoge2]"));

		// scope: requestScope, request: foo, bar, hogeList
		assertThat(String.valueOf(WebAppUtil.getRequestSessionValue(
			request,
			"${ requestScope.foo }")), is("request-foo\"<br />\""));
		assertThat(String.valueOf(WebAppUtil.getRequestSessionValue(
			request,
			"aa${ requestScope.bar }bb")), is("aarequest-barbb"));
		assertThat(String.valueOf(WebAppUtil.getRequestSessionValue(
			request,
			"${ requestScope.hoge }")), is("null"));
		assertThat(
			String.valueOf(WebAppUtil.getRequestSessionValue(
				request,
				"${ requestScope.hogeList }")),
			is("[request-hoge1, request-hoge2]"));

		// scope: sessionScope, request: foo, bar, hogeList
		assertThat(String.valueOf(WebAppUtil.getRequestSessionValue(
			request,
			"${ sessionScope.foo }")), is("session-foo\"<br />\""));
		assertThat(String.valueOf(WebAppUtil.getRequestSessionValue(
			request,
			"aa${ sessionScope.bar }bb")), is("aabb"));
		assertThat(String.valueOf(WebAppUtil.getRequestSessionValue(
			request,
			"${ sessionScope.hoge }")), is("session-hoge"));
		assertThat(
			String.valueOf(WebAppUtil.getRequestSessionValue(
				request,
				"${ sessionScope.hogeList }")),
			is("[session-hoge1, session-hoge2]"));
	}

	@Test
	public void testGetRequestSessionValuePerfomance() {
		// テスト時に EL 式の評価実行に必要です。
		JspFactory.setDefaultFactory(new JspFactoryImpl());

		// request
		HttpServletRequest request = WebAppUtil.getRequest();
		{
			request.setAttribute("foo", "request-foo");
			request.setAttribute("bar", "request-bar");
			List<String> hogeList = CollectionsUtil.newArrayList();
			hogeList.add("request-hoge1");
			hogeList.add("request-hoge2");
			request.setAttribute("hogeList", hogeList);
		}

		// session
		HttpSession session = request.getSession();
		{
			session.setAttribute("foo", "session-foo");
			session.setAttribute("hoge", "session-hoge");
			List<String> hogeList = CollectionsUtil.newArrayList();
			hogeList.add("session-hoge1");
			hogeList.add("session-hoge2");
			session.setAttribute("hogeList", hogeList);
		}

		// type: (defult), request: foo, bar, hogeList
		Object fooValue = null;
		Object barValue = null;

		for (int i = 0; i < 1; i++) {
			fooValue = WebAppUtil.getRequestSessionValue(request, "${ foo }");
			barValue =
				WebAppUtil.getRequestSessionValue(request, "aa${ bar }bb");
		}

		assertThat(String.valueOf(fooValue), is("request-foo"));
		assertThat(String.valueOf(barValue), is("aarequest-barbb"));
	}

	@Test
	public void testGetRequestSessionELValueAsBoolen() throws Exception {
		// テスト時に EL 式の評価実行に必要です。
		JspFactory.setDefaultFactory(new JspFactoryImpl());

		// request
		HttpServletRequest request = WebAppUtil.getRequest();
		{
			request.setAttribute("foo", "request-foo");
			List<String> hogeList = CollectionsUtil.newArrayList();
			hogeList.add("request-hoge1");
			hogeList.add("request-hoge2");
			request.setAttribute("hogeList", hogeList);
		}

		// session
		HttpSession session = request.getSession();
		{
			List<String> hogeList = CollectionsUtil.newArrayList();
			hogeList.add("session-hoge1");
			hogeList.add("session-hoge2");
			session.setAttribute("hogeList", hogeList);
		}

		boolean result;

		// miss expression
		result = WebAppUtil.getRequestSessionValueAsBoolean(request, "${ }");
		assertThat(result, is(true));

		// not found attribute
		result =
			WebAppUtil.getRequestSessionValueAsBoolean(
				request,
				"${ empty notFound }");
		assertThat(result, is(true));
		result =
			WebAppUtil.getRequestSessionValueAsBoolean(
				request,
				"${ !empty notFound }");
		assertThat(result, is(false));

		// attribute
		result =
			WebAppUtil.getRequestSessionValueAsBoolean(
				request,
				"${ empty foo }");
		assertThat(result, is(false));
		result =
			WebAppUtil.getRequestSessionValueAsBoolean(
				request,
				"${ !empty foo }");
		assertThat(result, is(true));
		result =
			WebAppUtil.getRequestSessionValueAsBoolean(
				request,
				"${ empty requestScope.foo }");
		assertThat(result, is(false));
		result =
			WebAppUtil.getRequestSessionValueAsBoolean(
				request,
				"${ !empty requestScope.foo }");
		assertThat(result, is(true));
		result =
			WebAppUtil.getRequestSessionValueAsBoolean(
				request,
				"${ empty sessionScope.foo }");
		assertThat(result, is(true));
		result =
			WebAppUtil.getRequestSessionValueAsBoolean(
				request,
				"${ !empty sessionScope.foo }");
		assertThat(result, is(false));

		result =
			WebAppUtil.getRequestSessionValueAsBoolean(
				request,
				"${ empty hogeList }");
		assertThat(result, is(false));
		result =
			WebAppUtil.getRequestSessionValueAsBoolean(
				request,
				"${ !empty hogeList }");
		assertThat(result, is(true));
		result =
			WebAppUtil.getRequestSessionValueAsBoolean(
				request,
				"${ empty hogeList }");
		assertThat(result, is(false));
		result =
			WebAppUtil.getRequestSessionValueAsBoolean(
				request,
				"${ !empty hogeList }");
		assertThat(result, is(true));

		// mathematics
		result =
			WebAppUtil.getRequestSessionValueAsBoolean(request, "${ 1 == 1 }");
		assertThat(result, is(true));
		result =
			WebAppUtil.getRequestSessionValueAsBoolean(request, "${ 1 != 1 }");
		assertThat(result, is(false));
	}

	@Test
	public void testToRelativePath2() {
		String path;

		// ベースパスが空の場合のテストです。
		path = WebAppUtil.toRelativePath("/foo/", "", "/bar/hoge1.html");
		assertThat(path, is("../bar/hoge1.html"));
	}

	@Test
	public void testToRelativePath3() {
		String path;

		// basePath が null の場合のテストです。
		path = WebAppUtil.toRelativePath(null, null, null);
		assertThat(path, is(nullValue()));

		path = WebAppUtil.toRelativePath(null, null, "test.html");
		assertThat(path, is("test.html"));

		// 重複している / を 1 つの / に変換します。
		path =
			WebAppUtil.toRelativePath(
				"/mixer2//function.jsp",
				"//mixer2/template///function.html",
				"..///prettify/prettify.js");
		assertThat(path, is("prettify/prettify.js"));

		// targetPath が静的な相対パスのテストです。
		path =
			WebAppUtil.toRelativePath("/foo/bar/edit", "/foo/bar/edit.html", "");
		assertThat(path, is(""));

		path =
			WebAppUtil.toRelativePath(
				"/foo/bar/edit",
				"/foo/bar/edit.html",
				".");
		assertThat(path, is("."));

		path =
			WebAppUtil.toRelativePath(
				"/foo/bar/edit",
				"/foo/bar/edit.html",
				"./");
		assertThat(path, is("./"));

		path =
			WebAppUtil.toRelativePath(
				"/foo/bar/5/edit",
				"/foo/bar/edit.html",
				"");
		assertThat(path, is(""));

		path =
			WebAppUtil.toRelativePath(
				"/foo/bar/5/edit",
				"/foo/bar/edit.html",
				".");
		assertThat(path, is("."));

		path =
			WebAppUtil.toRelativePath(
				"/foo/bar/5/edit",
				"/foo/bar/edit.html",
				"./delete");
		assertThat(path, is("./delete"));

		// targetPath が動的な相対パスのテストです。

		path =
			WebAppUtil.toRelativePath("/", "index.html", "theme/main/main.css");
		assertThat(path, is("theme/main/main.css"));

		path =
			WebAppUtil.toRelativePath("/", "/index.html", "theme/main/main.css");
		assertThat(path, is("theme/main/main.css"));

		path =
			WebAppUtil.toRelativePath(
				"/",
				"component/layout/layout.html",
				"../../theme/main/main.css");
		assertThat(path, is("theme/main/main.css"));

		path =
			WebAppUtil.toRelativePath(
				"/",
				"component/layout/layout.html",
				"../../theme/main/main.css");
		assertThat(path, is("theme/main/main.css"));

		path =
			WebAppUtil.toRelativePath(
				"/",
				"/component/layout/layout.html",
				"../../theme/main/main.css");
		assertThat(path, is("theme/main/main.css"));

		path =
			WebAppUtil.toRelativePath(
				"/foo",
				"component/layout/layout.html",
				"../../theme/main/main.css");
		assertThat(path, is("theme/main/main.css"));

		path =
			WebAppUtil.toRelativePath(
				"/foo",
				"/component/layout/layout.html",
				"../../theme/main/main.css");
		assertThat(path, is("theme/main/main.css"));

		path =
			WebAppUtil.toRelativePath(
				"/foo/",
				"component/layout/layout.html",
				"../../theme/main/main.css");
		assertThat(path, is("../theme/main/main.css"));

		path =
			WebAppUtil.toRelativePath(
				"/foo/",
				"/component/layout/layout.html",
				"../../theme/main/main.css");
		assertThat(path, is("../theme/main/main.css"));

		path =
			WebAppUtil.toRelativePath(
				"/foo/bar",
				"component/layout/layout.html",
				"../../theme/main/main.css");
		assertThat(path, is("../theme/main/main.css"));

		path =
			WebAppUtil.toRelativePath(
				"/foo/bar",
				"/component/layout/layout.html",
				"../../theme/main/main.css");
		assertThat(path, is("../theme/main/main.css"));

		path =
			WebAppUtil.toRelativePath(
				"/foo/bar/",
				"component/layout/layout.html",
				"../../theme/main/main.css");
		assertThat(path, is("../../theme/main/main.css"));

		path =
			WebAppUtil.toRelativePath(
				"/foo/bar/",
				"/component/layout/layout.html",
				"../../theme/main/main.css");
		assertThat(path, is("../../theme/main/main.css"));

		path =
			WebAppUtil.toRelativePath(
				"/mixer2/function.jsp",
				"/mixer2/template/function.html",
				"../prettify/prettify.js");
		assertThat(path, is("prettify/prettify.js"));

		// アクションからの相対リンクテストです。
		path =
			WebAppUtil.toRelativePath(
				"/foo/bar/hoge1/",
				"/foo/bar/hoge1/",
				"page/");
		assertThat(path, is("page/"));

		path = WebAppUtil.toRelativePath("/foo/bar/page/5", "/foo/bar/", "10");
		assertThat(path, is("../10"));

		// ベースパスが空の場合のテストです。
		path = WebAppUtil.toRelativePath("/foo/", "", "/bar/hoge1.html");
		assertThat(path, is("../bar/hoge1.html"));

		path =
			WebAppUtil.toRelativePath(
				"/foo/bar/hoge1/hoge2",
				"",
				"/foo/bar/hoge");
		assertThat(path, is("../hoge"));

		path =
			WebAppUtil.toRelativePath("/foo/bar/hoge1/hoge2", "", "/foo/hoge");
		assertThat(path, is("../../hoge"));

		path =
			WebAppUtil.toRelativePath("/foo/bar/hoge1/hoge2/", "", "/foo/hoge");
		assertThat(path, is("../../../hoge"));

		path = WebAppUtil.toRelativePath("/foo/bar", "", "/foo/bar/hoge");
		assertThat(path, is("bar/hoge"));

		path =
			WebAppUtil.toRelativePath("/foo/bar", "", "/foo/bar/hoge1/hoge2");
		assertThat(path, is("bar/hoge1/hoge2"));

		path =
			WebAppUtil.toRelativePath("/foo/bar/", "", "/foo/bar/hoge1/hoge2");
		assertThat(path, is("hoge1/hoge2"));
	}

	@Test
	public void testToRelativePath4() {
		String path;

		path =
			WebAppUtil.toRelativePath(
				"/foo/bar/",
				"",
				"/foo/bar/hoge1/hoge2/index.html",
				WebAppUtil.NOT_REMOVE_INDEX);
		assertThat(path, is("hoge1/hoge2/index.html"));

		path =
			WebAppUtil.toRelativePath(
				"/foo/bar/",
				"",
				"/foo/bar/hoge1/hoge2/index.html",
				WebAppUtil.REMOVE_INDEX);
		assertThat(path, is("hoge1/hoge2/"));
	}

}
