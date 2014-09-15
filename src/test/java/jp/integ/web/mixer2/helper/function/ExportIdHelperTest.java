package jp.integ.web.mixer2.helper.function;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.List;

import jp.integ.web.mixer2.util.Mixer2Util;

import org.junit.Before;
import org.junit.Test;
import org.mixer2.jaxb.xhtml.Div;
import org.mixer2.jaxb.xhtml.Html;
import org.mixer2.jaxb.xhtml.P;
import org.mixer2.jaxb.xhtml.Title;
import org.mixer2.util.CastUtil;
import org.mixer2.xhtml.AbstractJaxb;
import org.mixer2.xhtml.exception.TagTypeUnmatchException;

/**
 * @author Jun Futagawa
 */
public class ExportIdHelperTest {

	@Before
	public void setUp() {
		Mixer2Util.setupMixer2EngineByNewInstance();
	}

	@Test
	public void testReplaceExport() {
		ExportIdHelper helper = new ExportIdHelper();

		String layoutHtmlString =
			"<!DOCTYPE html><html lang=\"ja\" xmlns=\"http://www.w3.org/1999/xhtml\">"
				+ "<head><title>レイアウト</title></head><body>"
				+ "<div id=\""
				+ helper.getName()
				+ "\">"
				+ "ここに埋め込まれます。"
				+ "</div>"
				+ "</body></html>";
		Html layoutHtml =
			Mixer2Util.getMixer2Engine().loadHtmlTemplate(layoutHtmlString);

		String contentHtmlString =
			"<!DOCTYPE html><html lang=\"ja\" xmlns=\"http://www.w3.org/1999/xhtml\">"
				+ "<head><title>コンテンツタイトル</title></head><body>"
				+ "<div id=\""
				+ helper.getName()
				+ "\">"
				+ "<div id=\"content\"><p>エクスポートするコンテンツ</p></div>"
				+ "</div>"
				+ "</body></html>";
		Html contentHtml =
			Mixer2Util.getMixer2Engine().loadHtmlTemplate(contentHtmlString);
		AbstractJaxb targetTag = contentHtml.getById(helper.getName());
		Html html = null;
		try {
			html =
				helper.replaceExport(null, contentHtml, targetTag, layoutHtml);
		} catch (TagTypeUnmatchException e) {
			e.printStackTrace();
			fail();
		}
		// System.out.println(Mixer2Util.getHtmlString(html));

		// title
		Title title = html.getDescendants(Title.class).get(0);
		assertThat(title.getContent(), is("コンテンツタイトル"));

		// content
		List<Div> divList = html.getDescendants(Div.class);
		assertThat(divList.size(), is(2));

		Div contentDiv = html.getById("content");
		P p = CastUtil.cast(contentDiv.getContent().get(0));
		assertThat(String.valueOf(p.getContent().get(0)), is("エクスポートするコンテンツ"));
	}

	@Test
	public void testReplaceExport_PrefixAndSuffix() {
		ExportIdHelper helper = new ExportIdHelper();

		String layoutHtmlString =
			"<!DOCTYPE html><html lang=\"ja\" xmlns=\"http://www.w3.org/1999/xhtml\">"
				+ "<head><title data-prefix=\"PREFIX_STRING: \" data-suffix=\" | SUFFIX_STRING\">レイアウト</title></head><body>"
				+ "<div id=\""
				+ helper.getName()
				+ "\">"
				+ "ここに埋め込まれます。"
				+ "</div>"
				+ "</body></html>";
		Html layoutHtml =
			Mixer2Util.getMixer2Engine().loadHtmlTemplate(layoutHtmlString);

		String contentHtmlString =
			"<!DOCTYPE html><html lang=\"ja\" xmlns=\"http://www.w3.org/1999/xhtml\">"
				+ "<head><title>コンテンツタイトル</title></head><body>"
				+ "<div id=\""
				+ helper.getName()
				+ "\">"
				+ "<div id=\"content\"><p>エクスポートするコンテンツ</p></div>"
				+ "</div>"
				+ "</body></html>";
		Html contentHtml =
			Mixer2Util.getMixer2Engine().loadHtmlTemplate(contentHtmlString);
		AbstractJaxb targetTag = contentHtml.getById(helper.getName());
		Html html = null;
		try {
			html =
				helper.replaceExport(null, contentHtml, targetTag, layoutHtml);
		} catch (TagTypeUnmatchException e) {
			e.printStackTrace();
			fail();
		}
		// System.out.println(Mixer2Util.getHtmlString(html));

		// title
		Title title = html.getDescendants(Title.class).get(0);
		assertThat(
			title.getContent(),
			is("PREFIX_STRING: コンテンツタイトル | SUFFIX_STRING"));

		// content
		List<Div> divList = html.getDescendants(Div.class);
		assertThat(divList.size(), is(2));

		Div contentDiv = html.getById("content");
		P p = CastUtil.cast(contentDiv.getContent().get(0));
		assertThat(String.valueOf(p.getContent().get(0)), is("エクスポートするコンテンツ"));
	}

	@Test
	public void testReplaceExport_PrefixAndSuffix_AddPrefix_False() {
		ExportIdHelper helper = new ExportIdHelper();

		String layoutHtmlString =
			"<!DOCTYPE html><html lang=\"ja\" xmlns=\"http://www.w3.org/1999/xhtml\">"
				+ "<head><title data-prefix=\"PREFIX_STRING: \" data-suffix=\" | SUFFIX_STRING\">レイアウト</title></head><body>"
				+ "<div id=\""
				+ helper.getName()
				+ "\">"
				+ "ここに埋め込まれます。"
				+ "</div>"
				+ "</body></html>";
		Html layoutHtml =
			Mixer2Util.getMixer2Engine().loadHtmlTemplate(layoutHtmlString);

		String contentHtmlString =
			"<!DOCTYPE html><html lang=\"ja\" xmlns=\"http://www.w3.org/1999/xhtml\">"
				+ "<head><title data-add-prefix=\"false\">コンテンツタイトル</title></head><body>"
				+ "<div id=\""
				+ helper.getName()
				+ "\">"
				+ "<div id=\"content\"><p>エクスポートするコンテンツ</p></div>"
				+ "</div>"
				+ "</body></html>";

		Html contentHtml =
			Mixer2Util.getMixer2Engine().loadHtmlTemplate(contentHtmlString);
		AbstractJaxb targetTag = contentHtml.getById(helper.getName());
		Html html = null;
		try {
			html =
				helper.replaceExport(null, contentHtml, targetTag, layoutHtml);
		} catch (TagTypeUnmatchException e) {
			e.printStackTrace();
			fail();
		}
		// System.out.println(Mixer2Util.getHtmlString(html));

		// title
		Title title = html.getDescendants(Title.class).get(0);
		assertThat(title.getContent(), is("コンテンツタイトル | SUFFIX_STRING"));

		// content
		List<Div> divList = html.getDescendants(Div.class);
		assertThat(divList.size(), is(2));

		Div contentDiv = html.getById("content");
		P p = CastUtil.cast(contentDiv.getContent().get(0));
		assertThat(String.valueOf(p.getContent().get(0)), is("エクスポートするコンテンツ"));
	}

	@Test
	public void testReplaceExport_PrefixAndSuffix_AddSuffix_False() {
		ExportIdHelper helper = new ExportIdHelper();

		String layoutHtmlString =
			"<!DOCTYPE html><html lang=\"ja\" xmlns=\"http://www.w3.org/1999/xhtml\">"
				+ "<head><title data-prefix=\"PREFIX_STRING: \" data-suffix=\" | SUFFIX_STRING\">レイアウト</title></head><body>"
				+ "<div id=\""
				+ helper.getName()
				+ "\">"
				+ "ここに埋め込まれます。"
				+ "</div>"
				+ "</body></html>";
		Html layoutHtml =
			Mixer2Util.getMixer2Engine().loadHtmlTemplate(layoutHtmlString);

		String contentHtmlString =
			"<!DOCTYPE html><html lang=\"ja\" xmlns=\"http://www.w3.org/1999/xhtml\">"
				+ "<head><title data-add-suffix=\"false\">コンテンツタイトル</title></head><body>"
				+ "<div id=\""
				+ helper.getName()
				+ "\">"
				+ "<div id=\"content\"><p>エクスポートするコンテンツ</p></div>"
				+ "</div>"
				+ "</body></html>";

		Html contentHtml =
			Mixer2Util.getMixer2Engine().loadHtmlTemplate(contentHtmlString);
		AbstractJaxb targetTag = contentHtml.getById(helper.getName());
		Html html = null;
		try {
			html =
				helper.replaceExport(null, contentHtml, targetTag, layoutHtml);
		} catch (TagTypeUnmatchException e) {
			e.printStackTrace();
			fail();
		}
		// System.out.println(Mixer2Util.getHtmlString(html));

		// title
		Title title = html.getDescendants(Title.class).get(0);
		assertThat(title.getContent(), is("PREFIX_STRING: コンテンツタイトル"));

		// content
		List<Div> divList = html.getDescendants(Div.class);
		assertThat(divList.size(), is(2));

		Div contentDiv = html.getById("content");
		P p = CastUtil.cast(contentDiv.getContent().get(0));
		assertThat(String.valueOf(p.getContent().get(0)), is("エクスポートするコンテンツ"));
	}

}