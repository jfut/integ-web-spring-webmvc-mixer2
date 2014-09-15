package jp.integ.web.mixer2.helper.function;

import org.mixer2.xhtml.AbstractJaxb;

/**
 * エクスポートするアイテムです。
 * 
 * @author Jun Futagawa
 */
public class ExportItem {

	/** タグ名です。 */
	public String tagName;

	/** タグのインデックスです。 */
	public int tagIndex;

	/** コンテンツのインデックスです。 */
	public int contentIndex;

	/** 対象の {@link AbstractJaxb} です。 */
	public AbstractJaxb tag;

	/**
	 * インスタンスを作成します。
	 * 
	 * @param tagName
	 *            タグ名
	 * @param tagIndex
	 *            タグのインデックス
	 * @param contentIndex
	 *            コンテンツのインデックス
	 * @param tag
	 *            対象の {@link AbstractJaxb}
	 */
	public ExportItem(String tagName, int tagIndex, int contentIndex,
			AbstractJaxb tag) {
		this.tagName = tagName;
		this.tagIndex = tagIndex;
		this.contentIndex = contentIndex;
		this.tag = tag;
	}

}
