package jp.integ.web.mixer2.helper.function;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.List;

import jp.integ.web.mixer2.helper.XhtmlHelper;
import jp.integ.web.mixer2.util.Mixer2Util;

import org.junit.Before;
import org.junit.Test;
import org.mixer2.jaxb.xhtml.Html;
import org.mixer2.jaxb.xhtml.P;

/**
 * @author Jun Futagawa
 */
public class AppNameClassHelperTest {

	@Before
	public void setUp() {
		Mixer2Util.setupMixer2EngineByNewInstance();
	}

	@Test
	public void testReplace() {
		AppNameClassHelper helper = new AppNameClassHelper();
		String content =
			"<p>test1</p><p class=\""
				+ helper.getName()
				+ "\">test2</p><p>test3</p>";
		Html html = XhtmlHelper.getHtmlByContent(content);
		html = helper.replace(null, null, html);
		List<P> pList = html.getDescendants(P.class);

		assertThat(pList.size(), is(3));
		assertThat(
			String.valueOf(pList.get(1).getContent().get(0)),
			is("Integ Web for Spring Web MVC Mixer2"));
	}

}
