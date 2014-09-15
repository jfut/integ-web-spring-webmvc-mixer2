package jp.integ.web.util;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * リソースを使ったメッセージに関するユーティリティクラスです。
 * 
 * @author Jun Futagawa
 */
public class MessageUtil {

	/** デフォルトのメッセージリソース名です。 */
	public static final String DEFAULT_RESOURCE_NAME = "messages";

	/**
	 * メッセージテキストを返します。
	 * 
	 * @param key
	 *            キー
	 * @param args
	 *            引数
	 * @return メッセージテキスト
	 */
	public static String getText(final String key, final Object... args) {
		Locale locale = WebAppUtil.getRequest().getLocale();
		return getText(DEFAULT_RESOURCE_NAME, locale, key, args);
	}

	/**
	 * メッセージテキストを返します。
	 * 
	 * @param locale
	 *            {@link Locale}
	 * @param key
	 *            キー
	 * @param args
	 *            引数
	 * @return メッセージテキスト
	 */
	public static String getText(final Locale locale, final String key,
			final Object... args) {
		return getText(DEFAULT_RESOURCE_NAME, locale, key, args);
	}

	/**
	 * メッセージテキストを返します。
	 * 
	 * @param name
	 *            リソース名
	 * @param locale
	 *            {@link Locale}
	 * @param key
	 *            キー
	 * @param args
	 *            引数
	 * @return メッセージテキスト
	 */
	public static String getText(final String name, Locale locale,
			final String key, final Object... args) {
		if (locale == null) {
			locale = Locale.ENGLISH;
		}
		try {
			ResourceBundle bundle =
				ResourceBundle.getBundle(
					name,
					locale,
					Thread.currentThread().getContextClassLoader());
			String simpleMessage = bundle.getString(key);
			if (simpleMessage == null) {
				return null;
			} else {
				return MessageFormat.format(simpleMessage, args);
			}
		} catch (MissingResourceException e) {
			return null;
		}
	}

	//
	// getTextOrElse
	//

	/**
	 * メッセージテキストを返します。
	 * 
	 * @param key
	 *            キー
	 * @param elseText
	 *            メッセージテキストが null の場合に返すデフォルト文字列
	 * @param args
	 *            引数
	 * @return メッセージテキスト
	 */
	public static String getTextOrElse(final String key, String elseText,
			final Object... args) {
		Locale locale = WebAppUtil.getRequest().getLocale();
		return getTextOrElse(DEFAULT_RESOURCE_NAME, locale, key, elseText, args);
	}

	/**
	 * メッセージテキストを返します。
	 * 
	 * @param locale
	 *            {@link Locale}
	 * @param key
	 *            キー
	 * @param elseText
	 *            メッセージテキストが null の場合に返すデフォルト文字列
	 * @param args
	 *            引数
	 * @return メッセージテキスト
	 */
	public static String getTextOrElse(final Locale locale, final String key,
			final String elseText, final Object... args) {
		return getTextOrElse(DEFAULT_RESOURCE_NAME, locale, key, elseText, args);
	}

	/**
	 * メッセージテキストを返します。
	 * 
	 * @param name
	 *            リソース名
	 * @param locale
	 *            {@link Locale}
	 * @param key
	 *            キー
	 * @param elseText
	 *            メッセージテキストが null の場合に返すデフォルト文字列
	 * @param args
	 *            引数
	 * @return メッセージテキスト
	 */
	public static String getTextOrElse(final String name, Locale locale,
			final String key, final String elseText, final Object... args) {
		String text = getText(name, locale, key, args);
		if (text == null) {
			return elseText;
		} else {
			return text;
		}
	}

}
