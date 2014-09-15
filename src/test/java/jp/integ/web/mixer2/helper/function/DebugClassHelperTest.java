package jp.integ.web.mixer2.helper.function;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.List;

import jp.integ.web.mixer2.helper.XhtmlHelper;
import jp.integ.web.mixer2.util.Mixer2Util;

import org.junit.Before;
import org.junit.Test;
import org.mixer2.jaxb.xhtml.A;
import org.mixer2.jaxb.xhtml.Div;
import org.mixer2.jaxb.xhtml.Html;

/**
 * @author Jun Futagawa
 */
public class DebugClassHelperTest {

	@Before
	public void setUp() {
		Mixer2Util.setupMixer2EngineByNewInstance();
	}

	@Test
	public void testReplace() {
		DebugClassHelper helper = new DebugClassHelper();
		String content =
			"<p>test1</p><div class=\""
				+ helper.getName()
				+ "\">test2</div><p>test3</p>";
		Html html = XhtmlHelper.getHtmlByContent(content);
		html = helper.replace(null, null, html);
		// System.out.println(Mixer2Util.getHtmlString(html));

		List<Div> divList = html.getDescendants(Div.class);
		assertThat(divList.size(), is(5));

		Div div;

		div = divList.get(0);
		assertThat(String.valueOf(div.getCssClass().get(0)), is("panel-group"));

		div = divList.get(1);
		assertThat(String.valueOf(div.getCssClass().get(0)), is("panel"));
		assertThat(String.valueOf(div.getCssClass().get(1)), is("panel-info"));

		div = divList.get(2);
		assertThat(
			String.valueOf(div.getCssClass().get(0)),
			is("panel-heading"));
		A a = div.getDescendants(A.class).get(0);
		assertThat(
			String.valueOf(a.getContent().get(0)),
			is(DebugClassHelper.DEFAULT_TITLE));
		assertThat(a.getHref().startsWith("#debug_"), is(true));

		div = divList.get(3);
		assertThat(
			String.valueOf(div.getCssClass().get(0)),
			is("panel-collapse"));
		assertThat(String.valueOf(div.getCssClass().get(1)), is("collapse"));

		div = divList.get(4);
		assertThat(String.valueOf(div.getCssClass().get(0)), is("panel-body"));
		assertThat(String.valueOf(div.getContent().get(0)), is("test2"));
	}

	@Test
	public void testReplaceExcludeCssClass() {
		DebugClassHelper helper = new DebugClassHelper();
		String content =
			"<p>test1</p><div id=\"otherId1\" class=\""
				+ helper.getName()
				+ " otherClass1\">test2</div><p>test3</p>"
				+ "<div id=\"otherId2\" class=\""
				+ helper.getName()
				+ " otherClass2\">test4</div><p>test5</p>";
		Html html = XhtmlHelper.getHtmlByContent(content);
		html = helper.replace(null, null, html);
		// System.out.println(Mixer2Util.getHtmlString(html));

		Div div;
		div = html.getById("otherId1");
		assertThat(div.getId(), is("otherId1"));
		assertThat(div.getCssClass().size(), is(2));
		assertThat(div.getCssClass().get(0), is("otherClass1"));
		assertThat(div.getCssClass().get(1), is("panel-group"));

		div = html.getById("otherId2");
		assertThat(div.getId(), is("otherId2"));
		assertThat(div.getCssClass().size(), is(2));
		assertThat(div.getCssClass().get(0), is("otherClass2"));
		assertThat(div.getCssClass().get(1), is("panel-group"));
	}

	@Test
	public void testReplace_title() {
		DebugClassHelper helper = new DebugClassHelper();
		String title = "other title";
		String content =
			"<p>test1</p><div class=\""
				+ helper.getName()
				+ "\" "
				+ "data-title=\""
				+ title
				+ "\""
				+ ">test2</div><p>test3</p>";
		Html html = XhtmlHelper.getHtmlByContent(content);
		html = helper.replace(null, null, html);
		// System.out.println(Mixer2Util.getHtmlString(html));

		List<Div> divList = html.getDescendants(Div.class);
		assertThat(divList.size(), is(5));

		Div div;

		div = divList.get(0);
		assertThat(String.valueOf(div.getCssClass().get(0)), is("panel-group"));

		div = divList.get(1);
		assertThat(String.valueOf(div.getCssClass().get(0)), is("panel"));
		assertThat(String.valueOf(div.getCssClass().get(1)), is("panel-info"));

		div = divList.get(2);
		assertThat(
			String.valueOf(div.getCssClass().get(0)),
			is("panel-heading"));
		A a = div.getDescendants(A.class).get(0);
		assertThat(String.valueOf(a.getContent().get(0)), is(title));
		assertThat(a.getHref().startsWith("#debug_"), is(true));

		div = divList.get(3);
		assertThat(
			String.valueOf(div.getCssClass().get(0)),
			is("panel-collapse"));
		assertThat(String.valueOf(div.getCssClass().get(1)), is("collapse"));

		div = divList.get(4);
		assertThat(String.valueOf(div.getCssClass().get(0)), is("panel-body"));
		assertThat(String.valueOf(div.getContent().get(0)), is("test2"));
	}

}
