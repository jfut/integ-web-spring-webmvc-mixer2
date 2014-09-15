package jp.integ.web.mixer2.helper;

import javax.xml.namespace.QName;

/**
 * data-* 属性クラスです。
 * 
 * @author Jun Futagawa
 */
public class DataAttribute {

	/** data-* 属性のプレフィックスです。 */
	public static final String DATA_PREFIX = "data-";

	/** data-* 属性名です。 */
	public final String name;

	/** data-* 属性名に対応する {@link QName} です。 */
	public QName dataQName;

	/**
	 * インスタンスを作成します。
	 * 
	 * @param name
	 *            data-* 属性名
	 */
	public DataAttribute(String name) {
		this.name = name;
		this.dataQName = new QName(DATA_PREFIX + name);
	}

}
