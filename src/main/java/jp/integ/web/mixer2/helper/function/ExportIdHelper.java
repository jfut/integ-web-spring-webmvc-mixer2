package jp.integ.web.mixer2.helper.function;

import java.io.IOException;
import java.util.Map;

import javax.xml.namespace.QName;

import jp.integ.web.mixer2.helper.DataAttribute;
import jp.integ.web.mixer2.util.Mixer2Util;

import org.mixer2.jaxb.xhtml.Html;
import org.mixer2.jaxb.xhtml.Title;
import org.mixer2.util.CastUtil;
import org.mixer2.xhtml.AbstractJaxb;
import org.mixer2.xhtml.exception.TagTypeUnmatchException;
import org.seasar.util.convert.BooleanConversionUtil;
import org.seasar.util.lang.StringUtil;

/**
 * レイアウト機能用のコンテンツをエクスポートするヘルパー機能です。
 * 
 * @author Jun Futagawa
 */
public class ExportIdHelper extends FunctionIdHelper {

	//
	// ファンクション属性
	//

	/** デフォルトのファンクション名です。 */
	private static final String DEFAULT_NAME = "M_EXPORT";

	/** ソースを表す data-* 属性名です。 */
	private final DataAttribute DATA_SRC = new DataAttribute("src");

	/** ソースを表す data-* 属性名です。 */
	private final DataAttribute DATA_ADD_PREFIX = new DataAttribute(
		"add-prefix");

	/** ソースを表す data-* 属性名です。 */
	private final DataAttribute DATA_ADD_SUFFIX = new DataAttribute(
		"add-suffix");

	/** 使用する data-* 属性の配列です。 */
	private final DataAttribute[] dataAttributes = {
		DATA_SRC,
		DATA_ADD_PREFIX,
		DATA_ADD_SUFFIX };

	// レイアウト側の属性名です。

	/** レイアウト側の接頭辞を表す data-* 属性名です。 */
	private final DataAttribute DATA_LAYOUT_PREFIX =
		new DataAttribute("prefix");

	/** レイアウト側の接尾辞を表す data-* 属性名です。 */
	private final DataAttribute DATA_LAYOUT_SUFFIX =
		new DataAttribute("suffix");

	/** レイアウト側で使用する data-* 属性の配列です。 */
	private final DataAttribute[] dataLayoutAttributes = {
		DATA_LAYOUT_PREFIX,
		DATA_LAYOUT_SUFFIX };

	//
	// オプション
	//

	/**
	 * インスタンスを作成します。
	 */
	public ExportIdHelper() {
		super(DEFAULT_NAME);
	}

	/**
	 * インスタンスを作成します。
	 * 
	 * @param styleId
	 *            対象 ID 属性名
	 * @param dataSrc
	 *            ソースを表す data-* 属性名
	 * @param dataAddPrefix
	 *            接頭辞を足すかどうかを表す data-* 属性名
	 * @param dataAddSuffix
	 *            接尾辞を足すかどうかを表す data-* 属性名
	 * @param dataLayoutPrefix
	 *            レイアウト側の接頭辞を表す data-* 属性名
	 * @param dataLayoutSuffix
	 *            レイアウト側の接尾辞を表す data-* 属性名
	 */
	public ExportIdHelper(String styleId, String dataSrc, String dataAddPrefix,
			String dataAddSuffix, String dataLayoutPrefix,
			String dataLayoutSuffix) {
		super(styleId);
		DATA_SRC.dataQName = new QName(DATA_PREFIX + dataSrc);
		DATA_ADD_PREFIX.dataQName = new QName(DATA_PREFIX + dataAddPrefix);
		DATA_ADD_SUFFIX.dataQName = new QName(DATA_PREFIX + dataAddSuffix);
		DATA_LAYOUT_PREFIX.dataQName =
			new QName(DATA_PREFIX + dataLayoutPrefix);
		DATA_LAYOUT_SUFFIX.dataQName =
			new QName(DATA_PREFIX + dataLayoutSuffix);
	}

	@Override
	public <T extends AbstractJaxb> T replace(String path, String templatePath,
			T parent) {
		// コンテンツ領域を取得します。
		AbstractJaxb targetTag = parent.getById(name);
		if (targetTag != null) {
			// 対象タグの data-* 属性の名前と値のマップを取得します。
			Map<String, String> dataAttributeMap =
				super.setupDataAttributeMap(targetTag, dataAttributes);
			// コンテンツ領域を入れ替える時に使用するため、
			// 削除した属性 ID 名を再度設定します。
			targetTag.setId(name);

			String layoutTemplatePath = dataAttributeMap.get(DATA_SRC.name);
			if (StringUtil.isEmpty(layoutTemplatePath) == false) {
				// レイアウトページを取得します。
				try {
					// テンプレートファイルを読み込みます。
					Html layoutHtml = Mixer2Util.getHtml(layoutTemplatePath);

					// テンプレートページへ事前処理用ヘルパー群を適用します。
					FunctionsHelper.getDefaultPreInstance().replaceAll(
						path,
						layoutTemplatePath,
						layoutHtml);

					return replaceExport(path, parent, targetTag, layoutHtml);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (TagTypeUnmatchException e) {
					e.printStackTrace();
				}
			}
		}
		return parent;
	}

	/**
	 * コンテンツ領域をレイアウト HTML へ埋め込んで返します。
	 * 
	 * @param path
	 * @param parent
	 * @param targetTag
	 * @param layoutHtml
	 * @return
	 * @throws TagTypeUnmatchException
	 */
	protected <T extends AbstractJaxb> T replaceExport(String path, T parent,
			AbstractJaxb targetTag, Html layoutHtml)
			throws TagTypeUnmatchException {

		// コンテンツ側のタイトル文字列を取得します。
		Title title = parent.getDescendants(Title.class).get(0);
		String titleString = title.getContent();

		Map<String, String> dataTitleAttributeMap =
			super.setupDataAttributeMap(title, dataAttributes);

		// レイアウト側のタイトルを取得します。
		Title layoutTitle = layoutHtml.getDescendants(Title.class).get(0);

		// レイアウト側の対象タグの data-* 属性の名前と値のマップを取得します。
		Map<String, String> dataLayoutTitleAttributeMap =
			super.setupDataAttributeMap(layoutTitle, dataLayoutAttributes);

		// 接頭辞を使用する場合、レイアウト側に定義された接頭辞を追加します。
		String layoutPrefix = "";
		final Boolean addPrefix =
			BooleanConversionUtil.toBoolean(dataTitleAttributeMap.get(DATA_ADD_PREFIX.name));
		// デフォルト値として add-prefix 指定がない場合、接頭辞を追加します。
		if (addPrefix == null || addPrefix.booleanValue() == true) {
			// レイアウト側から接頭辞を取得します。
			layoutPrefix =
				dataLayoutTitleAttributeMap.get(DATA_LAYOUT_PREFIX.name);
			if (layoutPrefix == null) {
				layoutPrefix = "";
			}
		}
		// 接尾辞を使用する場合、レイアウト側に定義された接頭辞を追加します。
		String layoutSuffix = "";
		final Boolean addSuffix =
			BooleanConversionUtil.toBoolean(dataTitleAttributeMap.get(DATA_ADD_SUFFIX.name));
		// デフォルト値として add-prefix 指定がない場合、接尾辞を追加します。
		if (addSuffix == null || addSuffix.booleanValue() == true) {
			// レイアウト側から接尾辞を取得します。
			layoutSuffix =
				dataLayoutTitleAttributeMap.get(DATA_LAYOUT_SUFFIX.name);
			if (layoutSuffix == null) {
				layoutSuffix = "";
			}
		}

		// 接頭辞と接尾辞を組み込んだタイトルを設定します。
		layoutTitle.setContent(layoutPrefix + titleString + layoutSuffix);

		// コンテンツ領域を入れ替えます。
		AbstractJaxb abstractJaxb = layoutHtml.getById(name);
		if (abstractJaxb != null) {
			layoutHtml.replaceById(name, targetTag);
			layoutHtml.getById(name).setId(null);
		}
		return CastUtil.cast(layoutHtml);
	}

}
