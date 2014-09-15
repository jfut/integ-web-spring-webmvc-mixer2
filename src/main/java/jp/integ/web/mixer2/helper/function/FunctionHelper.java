package jp.integ.web.mixer2.helper.function;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.integ.web.mixer2.helper.DataAttribute;

import org.mixer2.xhtml.AbstractJaxb;

/**
 * Mixer2 のための共通機能インタフェースです。
 * 
 * @author Jun Futagawa
 */
public abstract class FunctionHelper {

	/** data-* 属性のプレフィックスです。 */
	public static final String DATA_PREFIX = "data-";

	/** aria-* 属性のプレフィックスです。 */
	public static final String ARIA_PREFIX = "aria-";

	/** 値の正規表現です。 */
	public static final Pattern DEFAULT_VALUE_PATTERN =
		Pattern.compile("([\\w-]+),?");

	/** 値グループの正規表現です。 */
	public static final Pattern DEFAULT_VALUE_GROUP_PATTERN =
		Pattern.compile("\\[([\\w-,]+)\\],?");

	/** 機能名です。 */
	protected final String name;

	/**
	 * インスタンスを作成します。
	 * 
	 * @param name
	 *            機能名
	 */
	public FunctionHelper(String name) {
		this.name = name;
	}

	/**
	 * 機能名を返します。
	 * 
	 * @return 機能名
	 */
	public String getName() {
		return name;
	}

	/**
	 * HTML の置換処理を実行します。
	 * 
	 * @param path
	 *            処理パス
	 * @param templatePath
	 *            テンプレートパス
	 * @param parent
	 *            対象の基底タグ
	 * @return 置換されたタグ
	 */
	public abstract <T extends AbstractJaxb> T replace(String path,
			String templatePath, T parent);

	/**
	 * 対象タグの data-* 属性の名前と値のマップを作成して返します。
	 * また、対象タグの data-* 属性を削除します。
	 * 
	 * @param abstractJaxb
	 *            対象タグ
	 * @param dataAttributes
	 *            {@link DataAttribute} の配列
	 * @return 対象タグの data-* 属性の名前と値のマップ
	 */
	public Map<String, String> setupDataAttributeMap(AbstractJaxb abstractJaxb,
			DataAttribute... dataAttributes) {
		// data-* 属性の値をすべて値マップを作成します。
		Map<String, String> dataAttributeMap =
			new HashMap<String, String>(dataAttributes.length);
		for (DataAttribute dataAttribute : dataAttributes) {
			dataAttributeMap.put(
				dataAttribute.name,
				abstractJaxb.getOtherAttributes().get(dataAttribute.dataQName));
		}

		// 不要な data-* 属性を削除します。
		for (DataAttribute dataAttribute : dataAttributes) {
			abstractJaxb.getOtherAttributes().remove(dataAttribute.dataQName);
		}
		return dataAttributeMap;
	}

	/**
	 * 指定したインデックスの値を持つ data-* 属性の名前と値のマップを返します。
	 * 
	 * @param dataAttributeMap
	 *            data-* 属性の名前と値のマップ
	 * @param index
	 *            インデックス
	 * @return 指定したインデックスの値を持つ data-* 属性の名前と値のマップ
	 */
	public Map<String, String> setupSingleDataAttributeMap(
			Map<String, String> dataAttributeMap, int index) {
		Map<String, String> singleDataAttributeMap =
			new HashMap<String, String>(dataAttributeMap.size());
		for (String dataName : dataAttributeMap.keySet()) {
			String dataValue = getDataValue(dataAttributeMap, dataName, index);
			singleDataAttributeMap.put(dataName, dataValue);
		}
		return singleDataAttributeMap;
	}

	/**
	 * 指定されたインデックス位置にある値を返します。
	 * 
	 * <pre>
	 * [value1-1,value1-2],[value2-1,value2-2],[value3-1,value3-2]
	 * value1,value2,value3
	 * </pre>
	 * 
	 * @param dataAttributeMap
	 *            data-* 属性の名前と値のマップ
	 * @param dataName
	 *            data-* 属性名
	 * @param index
	 * @return 値
	 */
	protected String getDataValue(Map<String, String> dataAttributeMap,
			String dataName, int index) {
		return getDataValue(
			dataAttributeMap,
			dataName,
			index,
			DEFAULT_VALUE_GROUP_PATTERN,
			DEFAULT_VALUE_PATTERN);
	}

	/**
	 * 指定されたインデックス位置にある値を返します。
	 * 
	 * @param dataAttributeMap
	 *            data-* 属性の名前と値のマップ
	 * @param dataName
	 *            　data-* 属性名
	 * @param index
	 *            　インデックス
	 * @param valueGroupPattern
	 *            グループの区切り文字
	 * @param valuePattern
	 *            値の区切り文字
	 * @return 値
	 */
	protected String getDataValue(Map<String, String> dataAttributeMap,
			String dataName, int index, Pattern valueGroupPattern,
			Pattern valuePattern) {
		// 値を取得します。
		String originalDataValue = dataAttributeMap.get(dataName);
		if (originalDataValue == null) {
			return originalDataValue;
		}

		// グループの区切り文字で分割します。
		String dataValue =
			getIndexValue(originalDataValue, valueGroupPattern, index);

		if (originalDataValue.equals(dataValue) == true) {
			// グループの区切り文字が無い場合、値の区切り文字で分割します。
			dataValue = getIndexValue(originalDataValue, valuePattern, index);
		}

		return dataValue;
	}

	/**
	 * 指定された正規表現にマッチするインデックス位置にある値を返します。
	 * 指定されたインデックス位置が範囲外の場合、マッチした最後の値を返します。
	 * 
	 * @param value
	 *            値
	 * @param pattern
	 *            　値の正規表現
	 * @param index
	 *            　インデックス
	 * @return 値
	 */
	protected String getIndexValue(String value, Pattern pattern, int index) {
		/** 値グループの正規表現です。 */
		Matcher matcher = pattern.matcher(value);
		int length = 0;
		while (matcher.find()) {
			value = matcher.group(1);
			if (length == index) {
				break;
			}
			length++;
		}
		return value;
	}

}
