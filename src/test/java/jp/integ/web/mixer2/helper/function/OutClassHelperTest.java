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
public class OutClassHelperTest {

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

		OutClassHelper helper = new OutClassHelper();
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
		assertThat(span.getContent().size(), is(0));

		// scope: defult (requestScope, sessionScope), null, default, excapeXml
		content =
			"<p>test1</p>"
				+ "<span id=\"noEL\" class=\""
				+ helper.getName()
				+ "\" data-value=\"noEL\">test2</span>"
				+ "<span id=\"noName1\" class=\""
				+ helper.getName()
				+ "\" data-value=\"${ noName1 }\">test2</span>"
				+ "<span id=\"noName2\" class=\""
				+ helper.getName()
				+ "\" data-value=\"${ noName2 }\" data-default=\"defaultValue\">test2</span>"
				+ "<p>test3</p>";
		html = XhtmlHelper.getHtmlByContent(content);
		html = helper.replace(null, null, html, request);
		// System.out.println(Mixer2Util.getHtmlString(html));
		span = html.getById("noEL");
		assertThat(String.valueOf(span.getContent().get(0)), is("noEL"));
		span = html.getById("noName1");
		assertThat(span.getContent().size(), is(0));
		span = html.getById("noName2");
		assertThat(String.valueOf(span.getContent().get(0)), is("defaultValue"));

		// scope: defult (requestScope, sessionScope), request: foo, bar,
		// hogeList
		content =
			"<p>test1</p>"
				+ "<span id=\"foo\" class=\""
				+ helper.getName()
				+ "\" data-value=\"${ foo }\">test2</span>"
				+ "<span id=\"fooNoEscape\" class=\""
				+ helper.getName()
				+ "\" data-value=\"${ foo }\" data-escapeXml=\"false\">test2</span>"
				+ "<span id=\"bar\" class=\""
				+ helper.getName()
				+ "\" data-value=\"aa${ bar }bb\">test2</span>"
				+ "<span id=\"hoge\" class=\""
				+ helper.getName()
				+ "\" data-value=\"${ hoge }\">test2</span>"
				+ "<span id=\"hogeList\" class=\""
				+ helper.getName()
				+ "\" data-value=\"${ hogeList }\">test2</span>"
				+ "<p>test3</p>";
		html = XhtmlHelper.getHtmlByContent(content);
		html = helper.replace(null, null, html, request);
		// System.out.println(Mixer2Util.getHtmlString(html));
		span = html.getById("foo");
		assertThat(
			String.valueOf(span.getContent().get(0)),
			is("request-foo&amp;&lt;br /&gt;&amp;"));
		span = html.getById("fooNoEscape");
		assertThat(
			String.valueOf(span.getContent().get(0)),
			is("request-foo\"<br />\""));
		span = html.getById("bar");
		assertThat(
			String.valueOf(span.getContent().get(0)),
			is("aarequest-barbb"));
		span = html.getById("hoge");
		assertThat(String.valueOf(span.getContent().get(0)), is("session-hoge"));
		span = html.getById("hogeList");
		assertThat(
			String.valueOf(span.getContent().get(0)),
			is("[request-hoge1, request-hoge2]"));

		// scope: requestScope, request: foo, bar, hogeList
		content =
			"<p>test1</p>"
				+ "<span id=\"foo\" class=\""
				+ helper.getName()
				+ "\" data-value=\"${ requestScope.foo }\">test2</span>"
				+ "<span id=\"fooNoEscape\" class=\""
				+ helper.getName()
				+ "\" data-value=\"${ requestScope.foo }\" data-escapeXml=\"false\">test2</span>"
				+ "<span id=\"bar\" class=\""
				+ helper.getName()
				+ "\" data-value=\"aa${ requestScope.bar }bb\">test2</span>"
				+ "<span id=\"hoge\" class=\""
				+ helper.getName()
				+ "\" data-value=\"${ requestScope.hoge }\">test2</span>"
				+ "<span id=\"hogeList\" class=\""
				+ helper.getName()
				+ "\" data-value=\"${ requestScope.hogeList }\">test2</span>"
				+ "<p>test3</p>";
		html = XhtmlHelper.getHtmlByContent(content);
		html = helper.replace(null, null, html, request);
		// System.out.println(Mixer2Util.getHtmlString(html));
		span = html.getById("foo");
		assertThat(
			String.valueOf(span.getContent().get(0)),
			is("request-foo&amp;&lt;br /&gt;&amp;"));
		span = html.getById("fooNoEscape");
		assertThat(
			String.valueOf(span.getContent().get(0)),
			is("request-foo\"<br />\""));
		span = html.getById("bar");
		assertThat(
			String.valueOf(span.getContent().get(0)),
			is("aarequest-barbb"));
		span = html.getById("hoge");
		assertThat(span.getContent().size(), is(0));
		span = html.getById("hogeList");
		assertThat(
			String.valueOf(span.getContent().get(0)),
			is("[request-hoge1, request-hoge2]"));

		// scope: sessionScope, request: foo, bar, hogeList
		content =
			"<p>test1</p>"
				+ "<span id=\"foo\" class=\""
				+ helper.getName()
				+ "\" data-value=\"${ sessionScope.foo }\">test2</span>"
				+ "<span id=\"fooNoEscape\" class=\""
				+ helper.getName()
				+ "\" data-value=\"${ sessionScope.foo }\" data-escapeXml=\"false\">test2</span>"
				+ "<span id=\"bar\" class=\""
				+ helper.getName()
				+ "\" data-value=\"aa${ sessionScope.bar }bb\">test2</span>"
				+ "<span id=\"hoge\" class=\""
				+ helper.getName()
				+ "\" data-value=\"${ sessionScope.hoge }\">test2</span>"
				+ "<span id=\"hogeList\" class=\""
				+ helper.getName()
				+ "\" data-value=\"${ sessionScope.hogeList }\">test2</span>"
				+ "<p>test3</p>";
		html = XhtmlHelper.getHtmlByContent(content);
		html = helper.replace(null, null, html, request);
		// System.out.println(Mixer2Util.getHtmlString(html));
		span = html.getById("foo");
		assertThat(
			String.valueOf(span.getContent().get(0)),
			is("session-foo&amp;&lt;br /&gt;&amp;"));
		span = html.getById("fooNoEscape");
		assertThat(
			String.valueOf(span.getContent().get(0)),
			is("session-foo\"<br />\""));
		span = html.getById("bar");
		assertThat(String.valueOf(span.getContent().get(0)), is("aabb"));
		span = html.getById("hoge");
		assertThat(String.valueOf(span.getContent().get(0)), is("session-hoge"));
		span = html.getById("hogeList");
		assertThat(
			String.valueOf(span.getContent().get(0)),
			is("[session-hoge1, session-hoge2]"));
	}

}
