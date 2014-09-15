package jp.integ.web.mixer2.helper.function;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.xml.namespace.QName;

import jp.integ.web.mixer2.helper.DataAttribute;
import jp.integ.web.mixer2.helper.XhtmlHelper;
import jp.integ.web.util.MessageUtil;
import jp.integ.web.util.WebAppUtil;

import org.mixer2.xhtml.AbstractJaxb;
import org.seasar.util.exception.FieldNotFoundRuntimeException;
import org.seasar.util.lang.StringUtil;

/**
 * リソースの値を出力します。
 * 
 * <pre>
 * <ul>
 *   <li><span class="M_MESSAGE" data-key="valid.required">dummy.resource1</span></li>
 *   <li><span class="M_MESSAGE" data-key="valid.required" data-args="foo">dummy.resource2</span></li>
 *   <li><span class="M_MESSAGE" data-key="valid.maxLength" data-args="foo,bar">dummy.resource3</span></li>
 *   <li><span class="M_MESSAGE" data-key="valid.maxLength" data-args="foo,bar" data-language="en">dummy.resource4</span></li>
 *   <li><span class="M_MESSAGE" data-key="no-key">dummy.resource5</span></li>
 *   <li><span class="M_MESSAGE" data-name="file-not-found" data-key="valid.maxLength">dummy.resource6</span></li>
 * </ul>
 * </pre>
 * 
 * @author Jun Futagawa
 */
public class MessageClassHelper extends FunctionClassHelper implements
		FunctionRequestHelper {

	//
	// ファンクション属性
	//

	/** デフォルトのファンクション名です。 */
	private static final String DEFAULT_NAME = "M_MESSAGE";

	/** リソースファイル名を表す data-* 属性名です。 */
	private final DataAttribute DATA_NAME = new DataAttribute("name");

	/** リソース名を表す data-* 属性名です。 */
	private final DataAttribute DATA_KEY = new DataAttribute("key");

	/** リソースの引数を表す data-* 属性名です。 */
	private final DataAttribute DATA_ARGS = new DataAttribute("args");

	/** リソースのロケールのための言語を表す data-* 属性名です。 */
	private final DataAttribute DATA_LANGUAGE = new DataAttribute("language");

	/** 使用する data-* 属性の配列です。 */
	private final DataAttribute[] dataAttributes = {
		DATA_NAME,
		DATA_KEY,
		DATA_ARGS,
		DATA_LANGUAGE };

	//
	// オプション
	//

	public static final String DELIMITER = ",";

	/**
	 * インスタンスを作成します。
	 */
	public MessageClassHelper() {
		super(DEFAULT_NAME);
	}

	/**
	 * インスタンスを作成します。
	 * 
	 * @param styleClass
	 *            対象クラス名
	 * @param dataName
	 *            data-* 属性名
	 * @param dataKey
	 *            data-* 属性名
	 * @param dataArgs
	 *            data-* 属性名
	 * @param dataLanguage
	 *            data-* 属性名
	 */
	public MessageClassHelper(String styleClass, String dataName,
			String dataKey, String dataArgs, String dataLanguage) {
		super(styleClass);
		DATA_NAME.dataQName = new QName(DATA_PREFIX + dataName);
		DATA_KEY.dataQName = new QName(DATA_PREFIX + dataKey);
		DATA_ARGS.dataQName = new QName(DATA_PREFIX + dataArgs);
		DATA_LANGUAGE.dataQName = new QName(DATA_PREFIX + dataLanguage);
	}

	@Override
	public <T extends AbstractJaxb> T replace(String path, String templatePath,
			T parent) {
		HttpServletRequest request = WebAppUtil.getRequest();
		if (request == null) {
			return parent;
		}
		return replace(path, templatePath, parent, request);
	}

	/**
	 * リソースの値を出力します。
	 */
	@Override
	public <T extends AbstractJaxb> T replace(String path, String templatePath,
			T html, HttpServletRequest request) {
		List<AbstractJaxb> abstractJaxbList = html.getDescendants(name);
		for (AbstractJaxb abstractJaxb : abstractJaxbList) {
			// 対象タグの data-* 属性の名前と値のマップを取得します。
			Map<String, String> dataAttributeMap =
				super.setupDataAttributeMap(abstractJaxb, dataAttributes);
			try {
				// リソース名を取得します。
				String dataName = dataAttributeMap.get(DATA_NAME.name);
				if (StringUtil.isEmpty(dataName) == true) {
					dataName = MessageUtil.DEFAULT_RESOURCE_NAME;
				}

				// キー名を取得します。
				String dataKey = dataAttributeMap.get(DATA_KEY.name);
				// リソース値を取得します。
				if (StringUtil.isEmpty(dataKey) == false) {
					// ロケールを作成します。
					Locale locale = WebAppUtil.getRequest().getLocale();
					String languageValue =
						dataAttributeMap.get(DATA_LANGUAGE.name);
					if (StringUtil.isEmpty(languageValue) == false) {
						locale = new Locale(languageValue);
					}

					// 引数を取得します。
					Object[] args = null;
					String argsValue = dataAttributeMap.get(DATA_ARGS.name);
					if (argsValue != null) {
						args = argsValue.split(DELIMITER);
					}
					// リソース値を組み立てます。
					String replaceContent =
						MessageUtil.getText(dataName, locale, dataKey, args);
					if (replaceContent != null) {
						replaceContent =
							WebAppUtil.toEscapedString(replaceContent);
						// content の内容を置換します。
						XhtmlHelper.replaceContent(abstractJaxb, replaceContent);
					}
				}
			} catch (FieldNotFoundRuntimeException e) {
				// do nothing
			} finally {
				abstractJaxb.removeCssClass(name);
			}
		}
		return html;
	}

}
