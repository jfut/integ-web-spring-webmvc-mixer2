package jp.integ.web.mixer2.helper.function;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import jp.integ.web.mixer2.helper.XhtmlHelper;
import jp.integ.web.mixer2.util.Mixer2Util;
import jp.integ.web.util.WebAppUtil;

import org.junit.Before;
import org.junit.Test;
import org.mixer2.jaxb.xhtml.A;
import org.mixer2.jaxb.xhtml.Div;
import org.mixer2.jaxb.xhtml.Html;
import org.seasar.util.collection.CollectionsUtil;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * @author Jun Futagawa
 */
public class RequestDebugClassHelperTest {

	@Before
	public void setUp() {
		Mixer2Util.setupMixer2EngineByNewInstance();
		MockHttpServletRequest request = new MockHttpServletRequest();
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(
			request));
	}

	@Test
	public void testReplace() {
		// request
		HttpServletRequest request = WebAppUtil.getRequest();
		{
			request.setAttribute("foo", "bar");
			request.setAttribute("bar1", "foo");
			List<String> hogeList = CollectionsUtil.newArrayList();
			hogeList.add("hoge1");
			hogeList.add("hoge2");
			request.setAttribute("hogeList", hogeList);
		}

		// session
		HttpSession session = request.getSession();
		{
			session.setAttribute("foo", "bar");
			List<String> hogeList = CollectionsUtil.newArrayList();
			hogeList.add("hoge1");
			hogeList.add("hoge2");
			session.setAttribute("hogeList", hogeList);
		}

		RequestDebugClassHelper helper = new RequestDebugClassHelper();
		String content =
			"<p>test1</p><div class=\""
				+ helper.getName()
				+ "\">test2</div><p>test3</p>";
		Html html = XhtmlHelper.getHtmlByContent(content);
		html = helper.replace(null, null, html);
		// System.out.println(Mixer2Util.getHtmlString(html));

		List<Div> divList = html.getDescendants(Div.class);
		assertThat(divList.size(), is(27));

		Div div;

		div = divList.get(0);
		assertThat(String.valueOf(div.getCssClass().get(0)), is("panel-group"));

		div = divList.get(1);
		assertThat(String.valueOf(div.getCssClass().get(0)), is("panel"));
		div = divList.get(1);
		assertThat(String.valueOf(div.getCssClass().get(1)), is("panel-info"));

		div = divList.get(2);
		assertThat(
			String.valueOf(div.getCssClass().get(0)),
			is("panel-heading"));
		A a = div.getDescendants(A.class).get(0);
		assertThat(a.getHref().startsWith("#debug_"), is(true));

		div = divList.get(3);
		assertThat(
			String.valueOf(div.getCssClass().get(0)),
			is("panel-collapse"));
		assertThat(String.valueOf(div.getCssClass().get(1)), is("collapse"));

		div = divList.get(4);
		assertThat(String.valueOf(div.getCssClass().get(0)), is("panel-body"));

		//
		// title
		//

		div = divList.get(0);
		Div requestDebugCollapseDiv =
			div.getDescendants("panel-group", Div.class).get(0);
		assertThat(
			String.valueOf(requestDebugCollapseDiv.getDescendants(A.class).get(
				0).getContent().get(0)),
			is(RequestDebugClassHelper.DEFAULT_TITLE));

		Div attributeDiv = div.getDescendants("panel-group", Div.class).get(1);
		{
			List<Div> panelDivList =
				attributeDiv.getDescendants("panel", Div.class);
			for (Div panelDiv : panelDivList) {
				assertThat(panelDiv.getCssClass().get(0), is("panel"));
				assertThat(panelDiv.getCssClass().get(1), is("panel-info"));
			}

			assertThat(
				String.valueOf(attributeDiv.getDescendants(A.class).get(0).getContent().get(
					0)),
				startsWith("bar1: foo"));
			assertThat(
				String.valueOf(attributeDiv.getDescendants(A.class).get(1).getContent().get(
					0)),
				startsWith("foo: bar"));
			assertThat(
				String.valueOf(attributeDiv.getDescendants(A.class).get(2).getContent().get(
					0)),
				startsWith("hogeList: [hoge1, hoge2]"));
		}

		Div sessionDiv = div.getDescendants("panel-group", Div.class).get(2);
		{
			List<Div> panelDivList =
				attributeDiv.getDescendants("panel", Div.class);
			for (Div panelDiv : panelDivList) {
				assertThat(panelDiv.getCssClass().get(0), is("panel"));
				assertThat(panelDiv.getCssClass().get(1), is("panel-info"));
			}

			assertThat(
				String.valueOf(sessionDiv.getDescendants(A.class).get(0).getContent().get(
					0)),
				startsWith("foo: bar"));
			assertThat(
				String.valueOf(sessionDiv.getDescendants(A.class).get(1).getContent().get(
					0)),
				startsWith("hogeList: [hoge1, hoge2]"));
		}
	}

	@Test
	public void testReplaceExcludeCssClass() {
		// request
		HttpServletRequest request = WebAppUtil.getRequest();
		{
			request.setAttribute("foo", "bar");
			request.setAttribute("bar1", "foo");
			List<String> hogeList = CollectionsUtil.newArrayList();
			hogeList.add("hoge1");
			hogeList.add("hoge2");
			request.setAttribute("hogeList", hogeList);
		}

		RequestDebugClassHelper helper = new RequestDebugClassHelper();
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

}
