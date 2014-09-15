package jp.integ.web.mixer2.helper.function;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import jp.integ.web.mixer2.helper.XhtmlHelper;
import jp.integ.web.mixer2.util.Mixer2Util;

import org.junit.Before;
import org.junit.Test;
import org.mixer2.jaxb.xhtml.Div;
import org.mixer2.jaxb.xhtml.Html;
import org.mixer2.jaxb.xhtml.Pre;
import org.seasar.util.lang.SystemPropertyUtil;

/**
 * @author Jun Futagawa
 */
public class EmptyTagHelperTest {

	@Before
	public void setUp() {
		Mixer2Util.setupMixer2EngineByNewInstance();
	}

	@Test
	public void testReplace() {
		EmptyTagHelper helper = new EmptyTagHelper(Pre.class);
		String content;
		Html html;

		String lineSeparator = SystemPropertyUtil.LINE_SEPARATOR;
		content =
			"<div id=\"all\">"
				+ lineSeparator
				+ "<p>test1</p>"
				+ lineSeparator
				+ "<pre class=\""
				+ helper.getName()
				+ "\">test2</pre>"
				+ lineSeparator
				+ "<p>test3</p>"
				+ lineSeparator
				+ "</div>";

		html = XhtmlHelper.getHtmlByContent(content);
		html = helper.replace(null, null, html);
		// System.out.println(Mixer2Util.getHtmlString(html));

		Div div = html.getById("all");
		assertThat(Mixer2Util.getHtmlString(div), is(content));
	}

}
