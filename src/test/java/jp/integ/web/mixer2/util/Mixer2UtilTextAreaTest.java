package jp.integ.web.mixer2.util;

import jp.integ.web.mixer2.helper.XhtmlHelper;

import org.junit.Before;
import org.junit.Test;
import org.mixer2.jaxb.xhtml.Html;

/**
 * @author Jun Futagawa
 */
public class Mixer2UtilTextAreaTest {

	@Before
	public void setUp() {
		Mixer2Util.setupMixer2EngineByNewInstance();
	}

	@Test
	public void testGetHtml() {
		Html html =
			XhtmlHelper.getHtmlByContent("<p>test p1</p><textarea></textarea><p>test p2</p>");
		System.out.println("# html: " + html.toString());
		System.out.println(Mixer2Util.getHtmlString(html));
	}

}
