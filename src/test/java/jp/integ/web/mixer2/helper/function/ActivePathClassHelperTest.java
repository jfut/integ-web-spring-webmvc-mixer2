package jp.integ.web.mixer2.helper.function;

import static org.junit.Assert.*;

import jp.integ.web.mixer2.helper.XhtmlHelper;
import jp.integ.web.mixer2.util.Mixer2Util;

import org.junit.Before;
import org.junit.Test;
import org.mixer2.jaxb.xhtml.Html;
import org.mixer2.jaxb.xhtml.Li;

/**
 * @author Jun Futagawa
 */
public class ActivePathClassHelperTest {

	@Before
	public void setUp() {
		Mixer2Util.setupMixer2EngineByNewInstance();
	}

	@Test
	public void testMatch() {
		if ("/foo/bar/hoge.html".matches("^/foo/bar/$") == true) {
			fail();
		} else {
			assertTrue(true);
		}
		if ("/foo/bar/hoge.html".matches("^/foo/bar/.*$") == true) {
			assertTrue(true);
		} else {
			fail();
		}
	}

	@Test
	public void testReplace() {
		ActivePathClassHelper helper = new ActivePathClassHelper();

		StringBuilder content = new StringBuilder("<ul>");
		content.append("<li id=\"test1\" class=\""
			+ helper.getName()
			+ "\" data-path=\"/foo/bar/\">アクティブリンク</li>");
		content.append("<li id=\"test2\" class=\""
			+ helper.getName()
			+ "\" data-path=\"/foo/bar/.*\">正規表現指定アクティブリンク</li>");
		content.append("</ul>");

		// /foo/bar/
		Html html = XhtmlHelper.getHtmlByContent(content.toString());
		html = helper.replace("/foo/bar/", null, html);
		Li test1Li = html.getById("test1");
		if (test1Li.getCssClass().contains(
			ActivePathClassHelper.DEFAULT_STYLE_CLASS)) {
			assertTrue(true);
		} else {
			fail();
		}
		Li test2Li = html.getById("test2");
		if (test2Li.getCssClass().contains(
			ActivePathClassHelper.DEFAULT_STYLE_CLASS)) {
			assertTrue(true);
		} else {
			fail();
		}

		// /foo/bar/hoge.html
		html = XhtmlHelper.getHtmlByContent(content.toString());
		html = helper.replace("/foo/bar/hoge.html", null, html);
		test1Li = html.getById("test1");
		if (test1Li.getCssClass().contains(
			ActivePathClassHelper.DEFAULT_STYLE_CLASS)) {
			fail();
		} else {
			assertTrue(true);
		}
		test2Li = html.getById("test2");
		if (test2Li.getCssClass().contains(
			ActivePathClassHelper.DEFAULT_STYLE_CLASS)) {
			assertTrue(true);
		} else {
			fail();
		}

		// /foo/bar/hoge1/hoge2.html
		html = XhtmlHelper.getHtmlByContent(content.toString());
		html = helper.replace("/foo/bar/hoge1/hoge2.html", null, html);
		test1Li = html.getById("test1");
		if (test1Li.getCssClass().contains(
			ActivePathClassHelper.DEFAULT_STYLE_CLASS)) {
			fail();
		} else {
			assertTrue(true);
		}
		test2Li = html.getById("test2");
		if (test2Li.getCssClass().contains(
			ActivePathClassHelper.DEFAULT_STYLE_CLASS)) {
			assertTrue(true);
		} else {
			fail();
		}
	}

	@Test
	public void testReplaceWithStyleClass() {
		ActivePathClassHelper helper = new ActivePathClassHelper();

		String styleClass = "foo-style";

		StringBuilder content = new StringBuilder("<ul>");
		content.append("<li id=\"test1\" class=\""
			+ helper.getName()
			+ "\" data-path=\"/foo/bar/\""
			+ " data-style-class=\""
			+ styleClass
			+ "\">アクティブリンク</li>");
		content.append("<li id=\"test2\" class=\""
			+ helper.getName()
			+ "\" data-path=\"/foo/bar/.*\""
			+ " data-style-class=\""
			+ styleClass
			+ "\">正規表現指定アクティブリンク</li>");
		content.append("</ul>");

		// /foo/bar/
		Html html = XhtmlHelper.getHtmlByContent(content.toString());
		html = helper.replace("/foo/bar/", null, html);
		Li test1Li = html.getById("test1");
		if (test1Li.getCssClass().contains(styleClass)) {
			assertTrue(true);
		} else {
			fail();
		}
		Li test2Li = html.getById("test2");
		if (test2Li.getCssClass().contains(styleClass)) {
			assertTrue(true);
		} else {
			fail();
		}

		// /foo/bar/hoge.html
		html = XhtmlHelper.getHtmlByContent(content.toString());
		html = helper.replace("/foo/bar/hoge.html", null, html);
		test1Li = html.getById("test1");
		if (test1Li.getCssClass().contains(styleClass)) {
			fail();
		} else {
			assertTrue(true);
		}
		test2Li = html.getById("test2");
		if (test2Li.getCssClass().contains(styleClass)) {
			assertTrue(true);
		} else {
			fail();
		}

		// /foo/bar/hoge1/hoge2.html
		html = XhtmlHelper.getHtmlByContent(content.toString());
		html = helper.replace("/foo/bar/hoge1/hoge2.html", null, html);
		test1Li = html.getById("test1");
		if (test1Li.getCssClass().contains(styleClass)) {
			fail();
		} else {
			assertTrue(true);
		}
		test2Li = html.getById("test2");
		if (test2Li.getCssClass().contains(styleClass)) {
			assertTrue(true);
		} else {
			fail();
		}
	}

}
