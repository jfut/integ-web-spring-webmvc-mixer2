package jp.integ.web.mixer2.helper.function;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import jp.integ.web.mixer2.helper.XhtmlHelper;
import jp.integ.web.util.WebAppUtil;

import org.mixer2.util.CastUtil;
import org.mixer2.xhtml.AbstractJaxb;
import org.mixer2.xhtml.TagEnum;

/**
 * {@link ExportItemClassHelper} でエクスポートした値を出力します。
 * 
 * @author Jun Futagawa
 */
public class ExportItemMergeClassHelper extends FunctionClassHelper implements
		FunctionRequestHelper {

	//
	// ファンクション属性
	//

	/** デフォルトのファンクション名です。このヘルパークラスはファンクション名を使用しません。 */
	private static final String DEFAULT_NAME = "M_EXPORT_ITEM_MERGE_NO_USE";

	/**
	 * インスタンスを作成します。
	 */
	public ExportItemMergeClassHelper() {
		super(DEFAULT_NAME);
	}

	public ExportItemMergeClassHelper(String styleClass) {
		super(styleClass);
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
		return replace(parent, request);
	}

	public static <T extends AbstractJaxb> T replace(T parent,
			HttpServletRequest request) {
		if (request == null) {
			return parent;
		}

		Object exportItem =
			request.getAttribute(ExportItemClassHelper.EXPORT_ITEM_ADD_KEY);
		if (exportItem == null) {
			return parent;
		}

		List<ExportItem> exportItemDtoList = CastUtil.cast(exportItem);
		for (ExportItem exportItemDto : exportItemDtoList) {
			Class<AbstractJaxb> tagClass =
				TagEnum.valueOf(exportItemDto.tagName.toUpperCase()).getTagClass();
			List<AbstractJaxb> tagList = parent.getDescendants(tagClass);
			if (tagList != null && 0 < tagList.size()) {
				AbstractJaxb tag;
				if (exportItemDto.tagIndex <= 0) {
					tag = tagList.get(0);
				} else if (exportItemDto.tagIndex < tagList.size()) {
					tag = tagList.get(exportItemDto.tagIndex);
				} else {
					tag = tagList.get(tagList.size() - 1);
				}
				XhtmlHelper.addContent(
					tag,
					exportItemDto.contentIndex,
					exportItemDto.tag);
			}
		}

		request.removeAttribute(ExportItemClassHelper.EXPORT_ITEM_ADD_KEY);
		return parent;
	}

}
