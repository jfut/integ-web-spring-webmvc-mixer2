package jp.integ.web.mixer2.helper;

import java.util.List;
import java.util.UUID;

import jp.integ.web.mixer2.util.Mixer2Util;

import org.mixer2.Mixer2Engine;
import org.mixer2.jaxb.xhtml.Html;
import org.mixer2.util.CastUtil;
import org.mixer2.xhtml.AbstractJaxb;
import org.seasar.util.beans.BeanDesc;
import org.seasar.util.beans.MethodDesc;
import org.seasar.util.beans.factory.BeanDescFactory;
import org.seasar.util.log.Logger;

/**
 * XHTML 用のヘルパークラスです。
 * 
 * @author Jun Futagawa
 */
public class XhtmlHelper {

	/** ロガーです。 */
	protected static Logger logger = Logger.getLogger(XhtmlHelper.class);

	/** HTML のプレフィックスです。 */
	public static final String HTML_PREFIX =
		"<!DOCTYPE html><html xmlns=\"http://www.w3.org/1999/xhtml\"><head><meta charset=\"utf-8\" /></head><body>";

	/** HTML のサフィックスです。 */
	public static final String HTML_SUFFIX = "</body></html>";

	public static Html getHtmlByContent(String tagName, String content) {
		return getHtmlByContent(tagName, null, null, content);
	}

	public static Html getHtmlByContentWithId(String tagName, String tagId,
			String content) {
		return getHtmlByContent(tagName, tagId, null, content);
	}

	public static Html getHtmlByContentWithClass(String tagName,
			String tagClass, String content) {
		return getHtmlByContent(tagName, null, tagClass, content);
	}

	public static Html getHtmlByContent(String tagName, String tagId,
			String tagClass, String content) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("<").append(tagName);
		if (tagId != null) {
			buffer.append(" id=\"").append(tagId).append("\"");
		}
		if (tagClass != null) {
			buffer.append(" class=\"").append(tagId).append("\"");
		}
		buffer.append(">");
		buffer.append(content);
		buffer.append("</").append(tagName).append(">");
		return getHtmlByContent(buffer.toString());
	}

	public static Html getHtmlByContent(String content) {
		Mixer2Engine mixer2Engine = Mixer2Util.getMixer2Engine();
		StringBuilder builder = new StringBuilder(HTML_PREFIX);
		builder.append(content).append(HTML_SUFFIX);
		if (logger.isDebugEnabled() == true) {
			String debugString = builder.toString();
			if (255 < debugString.length()) {
				debugString = debugString.substring(0, 255);
			}
			logger.log("DIntegWeb_0004", debugString);
		}
		return mixer2Engine.loadHtmlTemplate(builder);
	}

	public static <T extends AbstractJaxb> T getAbstractJaxbByContent(
			Class<T> clazz, String content) {
		String tagName = clazz.getSimpleName().toLowerCase();
		String tagId = UUID.randomUUID().toString();
		for (int i = 0; i < 256; i++) {
			if (content.contains(tagId) == false) {
				break;
			}
			tagId = UUID.randomUUID().toString();
		}
		Html html = getHtmlByContent(tagName, tagId, null, content);
		AbstractJaxb tag = html.getById(tagId);
		tag.setId(null);
		return tag.cast(clazz);
	}

	//
	// content 操作に関する関数です。
	//

	public static void addContent(AbstractJaxb abstractJaxb, Object content) {
		addContent(abstractJaxb, Integer.MAX_VALUE, content);
	}

	public static void addContent(AbstractJaxb abstractJaxb, int contentIndex,
			Object content) {
		List<Object> contentList = getContent(abstractJaxb);
		if (contentList == null) {
			return;
		}

		if (contentIndex <= 0) {
			contentIndex = 0;
		} else if (contentList.size() < contentIndex) {
			contentIndex = contentList.size();
		}

		if (content == null) {
			contentList.add(contentIndex, "");
		} else if (content instanceof List) {
			List<Object> contentAddList = CastUtil.cast(content);
			contentList.addAll(contentIndex, contentAddList);
		} else if (content instanceof AbstractJaxb) {
			contentList.add(contentIndex, content);
		} else {
			contentList.add(contentIndex, content.toString());
		}
	}

	/**
	 * {@link AbstractJaxb} リストのコンテンツを指定したコンテンツに置き換えます。
	 * 
	 * @param abstractJaxbList
	 *            {@link AbstractJaxb} のリスト
	 * @param replaceContent
	 *            置き換えるコンテンツ
	 */
	public static void replaceContent(List<AbstractJaxb> abstractJaxbList,
			Object replaceContent) {
		for (AbstractJaxb abstractJaxb : abstractJaxbList) {
			replaceContent(abstractJaxb, replaceContent);
		}
	}

	/**
	 * {@link AbstractJaxb} のコンテンツを指定したコンテンツに置き換えます。
	 * 
	 * @param abstractJaxb
	 *            {@link AbstractJaxb}
	 * @param replaceContent
	 *            置き換えるコンテンツ
	 */
	public static void replaceContent(AbstractJaxb abstractJaxb,
			Object replaceContent) {
		List<Object> content = getContent(abstractJaxb);
		if (content != null) {
			content.clear();
			if (replaceContent != null) {
				if (replaceContent instanceof List) {
					List<Object> replaceContentList =
						CastUtil.cast(replaceContent);
					content.addAll(replaceContentList);
				} else if (replaceContent instanceof AbstractJaxb) {
					content.add(replaceContent);
				} else {
					content.add(replaceContent.toString());
				}
			}
		}
	}

	/**
	 * TODO: 高速化する場合はリフレクションの使用を辞めます。
	 * 
	 * @param abstractJaxb
	 */
	public static List<Object> getContent(AbstractJaxb abstractJaxb) {
		BeanDesc beanDesc =
			BeanDescFactory.getBeanDesc(abstractJaxb.getClass());
		if (beanDesc.hasMethodDesc("getContent")) {
			MethodDesc methodDesc = beanDesc.getMethodDesc("getContent");
			return methodDesc.invoke(abstractJaxb);
		}
		return null;
	}

}
