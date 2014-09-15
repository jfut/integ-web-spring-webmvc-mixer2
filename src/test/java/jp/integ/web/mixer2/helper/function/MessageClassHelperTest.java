package jp.integ.web.mixer2.helper.function;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import jp.integ.web.mixer2.helper.XhtmlHelper;
import jp.integ.web.mixer2.util.Mixer2Util;

import org.junit.Before;
import org.junit.Test;
import org.mixer2.jaxb.xhtml.Html;
import org.mixer2.jaxb.xhtml.P;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * @author Jun Futagawa
 */
public class MessageClassHelperTest {

	@Before
	public void setUp() {
		Mixer2Util.setupMixer2EngineByNewInstance();
		MockHttpServletRequest request = new MockHttpServletRequest();
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(
			request));
	}

	@Test
	public void testReplace() {
		MessageClassHelper helper = new MessageClassHelper();
		String content =
			"<p>test1</p><p id=\"test1\" class=\""
				+ helper.getName()
				+ "\" data-name=\"IntegWeb_Messages\""
				+ " data-key=\"D0001\""
				+ " data-args=\"args message\""
				+ " data-language=\"ja\">test2</p><p>test3</p>";
		Html html = XhtmlHelper.getHtmlByContent(content);
		html = helper.replace(null, null, html);
		// System.out.println(Mixer2Util.getHtmlString(html));

		P test1P = html.getById("test1");
		assertThat(
			String.valueOf(test1P.getContent().get(0)),
			is("ビューテンプレートディレクトリ検索: args message"));
	}

}
