package jp.integ.web.mixer2.helper.function;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspFactory;

import jp.integ.web.mixer2.helper.XhtmlHelper;
import jp.integ.web.mixer2.util.Mixer2Util;
import jp.integ.web.util.WebAppUtil;

import org.apache.jasper.runtime.JspFactoryImpl;
import org.junit.Before;
import org.junit.Test;
import org.mixer2.jaxb.xhtml.Html;
import org.mixer2.jaxb.xhtml.Span;
import org.seasar.util.collection.CollectionsUtil;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * @author Jun Futagawa
 */
public class IfClassHelperTest {

	@Before
	public void setUp() {
		Mixer2Util.setupMixer2EngineByNewInstance();
		MockHttpServletRequest request = new MockHttpServletRequest();
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(
			request));

		// テスト時に EL 式の評価実行に必要です。
		JspFactory.setDefaultFactory(new JspFactoryImpl());
	}

	@Test
	public void testReplace() {
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

		IfClassHelper helper = new IfClassHelper();
		String content;
		Html html;
		Span span;

		// null
		content =
			"<p>test1</p>"
				+ "<span id=\"foo\" class=\""
				+ helper.getName()
				+ "\">test2</span>"
				+ "<p>test3</p>";
		html = XhtmlHelper.getHtmlByContent(content);
		html = helper.replace(null, null, html, request);
		// System.out.println(Mixer2Util.getHtmlString(html));
		span = html.getById("foo");
		assertThat(span, is(nullValue()));

		// scope: defult (requestScope, sessionScope), null, default, excapeXml
		content =
			"<p>test1</p>"
				+ "<span id=\"noEL\" class=\""
				+ helper.getName()
				+ "\" data-test=\"noEL\">test2</span>"
				+ "<p>test3</p>";
		html = XhtmlHelper.getHtmlByContent(content);
		html = helper.replace(null, null, html, request);
		// System.out.println(Mixer2Util.getHtmlString(html));
		span = html.getById("noEL");
		assertThat(span, is(nullValue()));

		// scope: defult (requestScope, sessionScope), request: foo, bar,
		// hogeList
		content =
			"<p>test1</p>" + "<span id=\"foo1\" class=\""
				+ helper.getName()
				+ "\" data-test=\"${ empty foo }\">test11</span>"
				+ "<span id=\"foo2\" class=\""
				+ helper.getName()
				+ "\" data-test=\"${ !empty foo }\">test12</span>"
				+ "<span id=\"bar1\" class=\""
				+ helper.getName()
				+ "\" data-test=\"${ empty bar }\">test21</span>"
				+ "<span id=\"bar2\" class=\""
				+ helper.getName()
				+ "\" data-test=\"${ !empty bar }\">test22</span>"
				+ "<span id=\"hoge1\" class=\""
				+ helper.getName()
				+ "\" data-test=\"${ empty hoge }\">test31</span>"
				+ "<span id=\"hoge2\" class=\""
				+ helper.getName()
				+ "\" data-test=\"${ !empty hoge }\">test32</span>"
				+ "<span id=\"hogeList1\" class=\""
				+ helper.getName()
				+ "\" data-test=\"${ empty hogeList }\">test41</span>"
				+ "<span id=\"hogeList2\" class=\""
				+ helper.getName()
				+ "\" data-test=\"${ !empty hogeList }\">test42</span>"
				+ "<p>test3</p>";
		html = XhtmlHelper.getHtmlByContent(content);
		html = helper.replace(null, null, html, request);
		// System.out.println(Mixer2Util.getHtmlString(html));
		span = html.getById("foo1");
		assertThat(span, is(nullValue()));
		span = html.getById("foo2");
		assertThat(String.valueOf(span.getContent().get(0)), is("test12"));
		span = html.getById("bar1");
		assertThat(span, is(nullValue()));
		span = html.getById("bar2");
		assertThat(String.valueOf(span.getContent().get(0)), is("test22"));
		span = html.getById("hoge1");
		assertThat(span, is(nullValue()));
		span = html.getById("hoge2");
		assertThat(String.valueOf(span.getContent().get(0)), is("test32"));
		span = html.getById("hogeList1");
		assertThat(span, is(nullValue()));
		span = html.getById("hogeList2");
		assertThat(String.valueOf(span.getContent().get(0)), is("test42"));

		// scope: requestScope, request: foo, bar, hogeList
		content =
			"<p>test1</p>" + "<span id=\"foo1\" class=\""
				+ helper.getName()
				+ "\" data-test=\"${ empty requestScope.foo }\">test11</span>"
				+ "<span id=\"foo2\" class=\""
				+ helper.getName()
				+ "\" data-test=\"${ !empty requestScope.foo }\">test12</span>"
				+ "<span id=\"bar1\" class=\""
				+ helper.getName()
				+ "\" data-test=\"${ empty requestScope.bar }\">test21</span>"
				+ "<span id=\"bar2\" class=\""
				+ helper.getName()
				+ "\" data-test=\"${ !empty requestScope.bar }\">test22</span>"
				+ "<span id=\"hoge1\" class=\""
				+ helper.getName()
				+ "\" data-test=\"${ empty requestScope.hoge }\">test31</span>"
				+ "<span id=\"hoge2\" class=\""
				+ helper.getName()
				+ "\" data-test=\"${ !empty requestScope.hoge }\">test32</span>"
				+ "<span id=\"hogeList1\" class=\""
				+ helper.getName()
				+ "\" data-test=\"${ empty requestScope.hogeList }\">test41</span>"
				+ "<span id=\"hogeList2\" class=\""
				+ helper.getName()
				+ "\" data-test=\"${ !empty requestScope.hogeList }\">test42</span>"
				+ "<p>test3</p>";
		html = XhtmlHelper.getHtmlByContent(content);
		html = helper.replace(null, null, html, request);
		// System.out.println(Mixer2Util.getHtmlString(html));
		span = html.getById("foo1");
		assertThat(span, is(nullValue()));
		span = html.getById("foo2");
		assertThat(String.valueOf(span.getContent().get(0)), is("test12"));
		span = html.getById("bar1");
		assertThat(span, is(nullValue()));
		span = html.getById("bar2");
		assertThat(String.valueOf(span.getContent().get(0)), is("test22"));
		span = html.getById("hoge1");
		assertThat(String.valueOf(span.getContent().get(0)), is("test31"));
		span = html.getById("hoge2");
		assertThat(span, is(nullValue()));
		span = html.getById("hogeList1");
		assertThat(span, is(nullValue()));
		span = html.getById("hogeList2");
		assertThat(String.valueOf(span.getContent().get(0)), is("test42"));

		// scope: sessionScope, request: foo, bar, hogeList
		content =
			"<p>test1</p>" + "<span id=\"foo1\" class=\""
				+ helper.getName()
				+ "\" data-test=\"${ empty sessionScope.foo }\">test11</span>"
				+ "<span id=\"foo2\" class=\""
				+ helper.getName()
				+ "\" data-test=\"${ !empty sessionScope.foo }\">test12</span>"
				+ "<span id=\"bar1\" class=\""
				+ helper.getName()
				+ "\" data-test=\"${ empty sessionScope.bar }\">test21</span>"
				+ "<span id=\"bar2\" class=\""
				+ helper.getName()
				+ "\" data-test=\"${ !empty sessionScope.bar }\">test22</span>"
				+ "<span id=\"hoge1\" class=\""
				+ helper.getName()
				+ "\" data-test=\"${ empty sessionScope.hoge }\">test31</span>"
				+ "<span id=\"hoge2\" class=\""
				+ helper.getName()
				+ "\" data-test=\"${ !empty sessionScope.hoge }\">test32</span>"
				+ "<span id=\"hogeList1\" class=\""
				+ helper.getName()
				+ "\" data-test=\"${ empty sessionScope.hogeList }\">test41</span>"
				+ "<span id=\"hogeList2\" class=\""
				+ helper.getName()
				+ "\" data-test=\"${ !empty sessionScope.hogeList }\">test42</span>"
				+ "<p>test3</p>";
		html = XhtmlHelper.getHtmlByContent(content);
		html = helper.replace(null, null, html, request);
		// System.out.println(Mixer2Util.getHtmlString(html));
		span = html.getById("foo1");
		assertThat(span, is(nullValue()));
		span = html.getById("foo2");
		assertThat(String.valueOf(span.getContent().get(0)), is("test12"));
		span = html.getById("bar1");
		assertThat(String.valueOf(span.getContent().get(0)), is("test21"));
		span = html.getById("bar2");
		assertThat(span, is(nullValue()));
		span = html.getById("hoge1");
		assertThat(span, is(nullValue()));
		span = html.getById("hoge2");
		assertThat(String.valueOf(span.getContent().get(0)), is("test32"));
		span = html.getById("hogeList1");
		assertThat(span, is(nullValue()));
		span = html.getById("hogeList2");
		assertThat(String.valueOf(span.getContent().get(0)), is("test42"));
	}

}
