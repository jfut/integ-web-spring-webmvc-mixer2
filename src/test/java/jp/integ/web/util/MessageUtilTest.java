package jp.integ.web.util;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * @author Jun Futagawa
 */
public class MessageUtilTest {

	@Before
	public void setUp() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(
			request));
	}

	@Test
	public void testGetText() {
		MockHttpServletRequest request =
			(MockHttpServletRequest)WebAppUtil.getRequest();

		// default
		assertThat(MessageUtil.getText("notFound"), is(nullValue()));
		assertThat(
			MessageUtil.getText("noLocale", "value0", "value1"),
			is("noLocale: test val0: value0, val1: value1."));
		assertThat(
			MessageUtil.getText("common", "value0", "value1"),
			is("common: test val0: value0, val1: value1."));
		assertThat(
			MessageUtil.getText("enLocale", "value0", "value1"),
			is("enLocale: test val0: value0, val1: value1."));
		assertThat(
			MessageUtil.getText("jaLocale", "value0", "value1"),
			is(nullValue()));

		// english
		request.setPreferredLocales(Arrays.asList(Locale.ENGLISH));
		assertThat(MessageUtil.getText("notFound"), is(nullValue()));
		assertThat(
			MessageUtil.getText("common", "value0", "value1"),
			is("common: test val0: value0, val1: value1."));
		assertThat(
			MessageUtil.getText("enLocale", "value0", "value1"),
			is("enLocale: test val0: value0, val1: value1."));
		assertThat(
			MessageUtil.getText("jaLocale", "value0", "value1"),
			is(nullValue()));

		// japanese
		request.setPreferredLocales(Arrays.asList(Locale.JAPANESE));
		assertThat(MessageUtil.getText("notFound"), is(nullValue()));
		assertThat(
			MessageUtil.getText("common", "value0", "value1"),
			is("共通: テスト val0: value0, val1: value1."));
		assertThat(
			MessageUtil.getText("enLocale", "value0", "value1"),
			is(nullValue()));
		assertThat(
			MessageUtil.getText("jaLocale", "value0", "value1"),
			is("jaLocale: テスト val0: value0, val1: value1."));

		// german
		request.setPreferredLocales(Arrays.asList(Locale.GERMAN));
		assertThat(MessageUtil.getText("notFound"), is(nullValue()));
		assertThat(
			MessageUtil.getText("common", "value0", "value1"),
			is("共通: テスト val0: value0, val1: value1."));
		assertThat(
			MessageUtil.getText("enLocale", "value0", "value1"),
			is(nullValue()));
		assertThat(
			MessageUtil.getText("jaLocale", "value0", "value1"),
			is("jaLocale: テスト val0: value0, val1: value1."));
	}

	@Test
	public void testGetTextOrElse() {
		MockHttpServletRequest request =
			(MockHttpServletRequest)WebAppUtil.getRequest();

		// default
		assertThat(
			MessageUtil.getTextOrElse("notFound", "elseText"),
			is("elseText"));
		assertThat(MessageUtil.getTextOrElse(
			"noLocale",
			"elseText",
			"value0",
			"value1"), is("noLocale: test val0: value0, val1: value1."));
		assertThat(
			MessageUtil.getTextOrElse("common", "elseText", "value0", "value1"),
			is("common: test val0: value0, val1: value1."));
		assertThat(MessageUtil.getTextOrElse(
			"enLocale",
			"elseText",
			"value0",
			"value1"), is("enLocale: test val0: value0, val1: value1."));
		assertThat(MessageUtil.getTextOrElse(
			"jaLocale",
			"elseText",
			"value0",
			"value1"), is("elseText"));

		// english
		request.setPreferredLocales(Arrays.asList(Locale.ENGLISH));
		assertThat(
			MessageUtil.getTextOrElse("notFound", "elseText"),
			is("elseText"));
		assertThat(
			MessageUtil.getTextOrElse("common", "elseText", "value0", "value1"),
			is("common: test val0: value0, val1: value1."));
		assertThat(MessageUtil.getTextOrElse(
			"enLocale",
			"elseText",
			"value0",
			"value1"), is("enLocale: test val0: value0, val1: value1."));
		assertThat(MessageUtil.getTextOrElse(
			"jaLocale",
			"elseText",
			"value0",
			"value1"), is("elseText"));

		// japanese
		request.setPreferredLocales(Arrays.asList(Locale.JAPANESE));
		assertThat(
			MessageUtil.getTextOrElse("notFound", "elseText"),
			is("elseText"));
		assertThat(
			MessageUtil.getTextOrElse("common", "elseText", "value0", "value1"),
			is("共通: テスト val0: value0, val1: value1."));
		assertThat(MessageUtil.getTextOrElse(
			"enLocale",
			"elseText",
			"value0",
			"value1"), is("elseText"));
		assertThat(MessageUtil.getTextOrElse(
			"jaLocale",
			"elseText",
			"value0",
			"value1"), is("jaLocale: テスト val0: value0, val1: value1."));

		// german
		request.setPreferredLocales(Arrays.asList(Locale.GERMAN));
		assertThat(
			MessageUtil.getTextOrElse("notFound", "elseText"),
			is("elseText"));
		assertThat(
			MessageUtil.getTextOrElse("common", "elseText", "value0", "value1"),
			is("共通: テスト val0: value0, val1: value1."));
		assertThat(MessageUtil.getTextOrElse(
			"enLocale",
			"elseText",
			"value0",
			"value1"), is("elseText"));
		assertThat(MessageUtil.getTextOrElse(
			"jaLocale",
			"elseText",
			"value0",
			"value1"), is("jaLocale: テスト val0: value0, val1: value1."));
	}

}
