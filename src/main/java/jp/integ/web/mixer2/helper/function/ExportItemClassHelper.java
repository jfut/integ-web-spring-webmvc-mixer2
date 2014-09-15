package jp.integ.web.mixer2.helper.function;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.xml.namespace.QName;

import jp.integ.web.mixer2.helper.DataAttribute;
import jp.integ.web.util.WebAppUtil;

import org.mixer2.util.CastUtil;
import org.mixer2.xhtml.AbstractJaxb;
import org.seasar.util.collection.CollectionsUtil;
import org.seasar.util.convert.IntegerConversionUtil;
import org.seasar.util.lang.StringUtil;

/**
 * 値をエクスポートし、別途実行する {@link ExportItemMergeClassHelper} で値を出力します。
 * 
 * <pre>
 * <link class="M_LINK M_EXPORT_ITEM" data-tag="head"
 * 		rel="stylesheet" type="text/css" href="../../theme/main/main.css" />
 * <link class="M_LINK M_EXPORT_ITEM" data-tag="head" data-tagIndex="0" data-contentIndex="0"
 * 		rel="stylesheet" type="text/css" href="../../theme/main/main.css" />
 * <div class="M_EXPORT_ITEM" data-tag="body" data-tagIndex="0">
 * 		...</div>
 * </pre>
 * 
 * @author Jun Futagawa
 */
public class ExportItemClassHelper extends FunctionClassHelper implements
		FunctionRequestHelper {

	//
	// ファンクション属性
	//

	/** デフォルトのファンクション名です。 */
	private static final String DEFAULT_NAME = "M_EXPORT_ITEM";

	/** タグ名を表す data-* 属性名です。 */
	private final DataAttribute DATA_TAG = new DataAttribute("tag");

	/** タグのインデックスを表す data-* 属性名です。 */
	private final DataAttribute DATA_TAG_INDEX = new DataAttribute("tagIndex");

	/** コンテンツのインデックスを表す data-* 属性名です。 */
	private final DataAttribute DATA_CONTENT_INDEX = new DataAttribute(
		"contentIndex");

	/** 使用する data-* 属性の配列です。 */
	private final DataAttribute[] dataAttributes = {
		DATA_TAG,
		DATA_TAG_INDEX,
		DATA_CONTENT_INDEX };

	//
	// オプション
	//

	public static final String EXPORT_ITEM_ADD_KEY = "EXPORT_ITEM_ADD_KEY";

	/**
	 * インスタンスを作成します。
	 */
	public ExportItemClassHelper() {
		super(DEFAULT_NAME);
	}

	/**
	 * インスタンスを作成します。
	 * 
	 * @param styleClass
	 *            対象クラス名
	 * @param dataTag
	 *            data-* 属性名
	 * @param dataTagIndex
	 *            data-* 属性名
	 * @param dataContentIndex
	 *            data-* 属性名
	 */
	public ExportItemClassHelper(String styleClass, String dataTag,
			String dataTagIndex, String dataContentIndex) {
		super(styleClass);
		DATA_TAG.dataQName = new QName(DATA_PREFIX + dataTag);
		DATA_TAG_INDEX.dataQName = new QName(DATA_PREFIX + dataTagIndex);
		DATA_CONTENT_INDEX.dataQName =
			new QName(DATA_PREFIX + dataContentIndex);
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

	@Override
	public <T extends AbstractJaxb> T replace(String path, String templatePath,
			T parent, HttpServletRequest request) {
		List<AbstractJaxb> tagList = parent.getDescendants(name);
		if (request != null && 0 < tagList.size()) {
			List<ExportItem> exportItemDtoList;
			Object exportItem = request.getAttribute(EXPORT_ITEM_ADD_KEY);
			if (exportItem != null) {
				exportItemDtoList = CastUtil.cast(exportItem);
			} else {
				exportItemDtoList = CollectionsUtil.newArrayList();
			}
			for (AbstractJaxb tag : tagList) {
				// 対象タグの data-* 属性の名前と値のマップを取得します。
				Map<String, String> dataAttributeMap =
					super.setupDataAttributeMap(tag, dataAttributes);

				String tagName = dataAttributeMap.get(DATA_TAG.name);

				String value;
				value = dataAttributeMap.get(DATA_TAG_INDEX.name);
				int tagIndex = 0;
				if (StringUtil.isNumber(value) == true) {
					tagIndex = IntegerConversionUtil.toPrimitiveInt(value);
				}
				value = dataAttributeMap.get(DATA_CONTENT_INDEX.name);
				int contentIndex = Integer.MAX_VALUE;
				if (StringUtil.isNumber(value) == true) {
					contentIndex = IntegerConversionUtil.toPrimitiveInt(value);
				}
				if (StringUtil.isEmpty(tagName) == false) {
					exportItemDtoList.add(new ExportItem(
						tagName,
						tagIndex,
						contentIndex,
						tag));
				}
				// 該当箇所を取り除きます。
				parent.remove(tag);
			}
			request.setAttribute(EXPORT_ITEM_ADD_KEY, exportItemDtoList);
		}
		return parent;
	}

}
