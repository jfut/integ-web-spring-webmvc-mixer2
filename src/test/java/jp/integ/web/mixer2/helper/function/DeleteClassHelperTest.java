package jp.integ.web.mixer2.helper.function;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import jp.integ.web.mixer2.helper.XhtmlHelper;
import jp.integ.web.mixer2.util.Mixer2Util;

import org.junit.Before;
import org.junit.Test;
import org.mixer2.jaxb.xhtml.Html;
import org.mixer2.jaxb.xhtml.Span;

/**
 * @author Jun Futagawa
 */
public class DeleteClassHelperTest {

	@Before
	public void setUp() {
		Mixer2Util.setupMixer2EngineByNewInstance();
	}

	@Test
	public void testReplace() {
		DeleteClassHelper helper = new DeleteClassHelper();
		String content;
		Html html;
		Span span;

		content =
			"<p>test1</p>"
				+ "<span id=\"foo\" class=\""
				+ helper.getName()
				+ "\">test2</span>"
				+ "<p>test3</p>";

		html = XhtmlHelper.getHtmlByContent(content);
		html = helper.replace(null, null, html);
		// System.out.println(Mixer2Util.getHtmlString(html));

		span = html.getById("foo");
		assertThat(span, is(nullValue()));
	}

}
