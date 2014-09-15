package jp.integ.web.mixer2.helper;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.mixer2.jaxb.xhtml.A;
import org.mixer2.jaxb.xhtml.Div;
import org.mixer2.jaxb.xhtml.H4;
import org.seasar.util.collection.CollectionsUtil;

/**
 * @author Jun Futagawa
 */
public class ComponentBootstrapHelperTest {

	@Test
	public void testCollapseDiv() {
		String id = "foo";
		String title = "title1";
		List<String> contentList = CollectionsUtil.newArrayList(2);
		contentList.add("value1");
		contentList.add("value2");

		Div collapseDiv =
			ComponentBootstrapHelper.collapseAccordionDiv(
				id,
				title,
				contentList);
		{
			assertThat(
				String.valueOf(collapseDiv.getDescendants(
					"panel-title",
					H4.class).get(0).getDescendants(A.class).get(0).getContent().get(
					0)),
				startsWith("title1"));
			assertThat(
				String.valueOf(collapseDiv.getDescendants(
					"panel-body",
					Div.class).get(0).getContent().get(0)),
				startsWith("value1"));
			assertThat(
				String.valueOf(collapseDiv.getDescendants(
					"panel-body",
					Div.class).get(0).getContent().get(1)),
				startsWith("value2"));
		}
	}

}
