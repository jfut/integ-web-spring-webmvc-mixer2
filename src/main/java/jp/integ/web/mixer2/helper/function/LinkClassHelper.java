package jp.integ.web.mixer2.helper.function;

import java.util.List;

import javax.xml.namespace.QName;

import jp.integ.web.util.WebAppUtil;

import org.mixer2.jaxb.xhtml.A;
import org.mixer2.jaxb.xhtml.Applet;
import org.mixer2.jaxb.xhtml.Area;
import org.mixer2.jaxb.xhtml.Base;
import org.mixer2.jaxb.xhtml.Blockquote;
import org.mixer2.jaxb.xhtml.Del;
import org.mixer2.jaxb.xhtml.Embed;
import org.mixer2.jaxb.xhtml.Form;
import org.mixer2.jaxb.xhtml.Iframe;
import org.mixer2.jaxb.xhtml.Img;
import org.mixer2.jaxb.xhtml.Input;
import org.mixer2.jaxb.xhtml.Ins;
import org.mixer2.jaxb.xhtml.Link;
import org.mixer2.jaxb.xhtml.Q;
import org.mixer2.jaxb.xhtml.Script;
import org.mixer2.jaxb.xhtml.Source;
import org.mixer2.jaxb.xhtml.Track;
import org.mixer2.xhtml.AbstractJaxb;
import org.mixer2.xhtml.TagEnum;

/**
 * リンクのアドレスを解決するヘルパー機能です。
 * 
 * @author Jun Futagawa
 */
public class LinkClassHelper extends FunctionClassHelper {

	//
	// ファンクション属性
	//

	/** デフォルトのファンクション名です。 */
	private static final String DEFAULT_NAME = "M_LINK";

	//
	// オプション
	//

	// タグの型ごとに使用する data-* 属性名が決まります。

	private static final QName QNAME_HREF = new QName(DATA_PREFIX + "href");

	private static final QName QNAME_SRC = new QName(DATA_PREFIX + "src");

	private static final QName QNAME_ACTION = new QName(DATA_PREFIX + "action");

	private static final QName QNAME_CODEBASE = new QName(DATA_PREFIX
		+ "codebase");

	private static final QName QNAME_CITE = new QName(DATA_PREFIX + "cite");

	/**
	 * インスタンスを作成します。
	 */
	public LinkClassHelper() {
		this(DEFAULT_NAME);
	}

	/**
	 * インスタンスを作成します。
	 * 
	 * @param styleClass
	 *            対象クラス名
	 */
	public LinkClassHelper(String styleClass) {
		super(styleClass);
	}

	/**
	 * * リンク TAG を処理します。リンク先をパスに併せて調整します。
	 * data-* が指定されている場合は、その値を用いて調整します。
	 * data-* が指定されていない場合は、オリジナルの値を用いて調整します。
	 */
	public <T extends AbstractJaxb> T replace(String path, String templatePath,
			T parent) {
		List<AbstractJaxb> abstractJaxbList = parent.getDescendants(name);
		for (AbstractJaxb abstractJaxb : abstractJaxbList) {
			replaceValue(path, templatePath, abstractJaxb);
			abstractJaxb.removeCssClass(name);
		}
		return parent;
	}

	protected void replaceValue(String path, String templatePath,
			AbstractJaxb target) {
		String value = null;
		TagEnum tagEnum =
			TagEnum.valueOf(target.getClass().getSimpleName().toUpperCase());
		switch (tagEnum) {
		// href
		case A:
			A a = (A)target;
			if (target.getOtherAttributes().containsKey(QNAME_HREF) == true) {
				value = target.getOtherAttributes().get(QNAME_HREF);
				target.getOtherAttributes().remove(QNAME_HREF);
			} else {
				value = a.getHref();
			}
			if (value != null) {
				value = WebAppUtil.toRelativePath(path, templatePath, value);
				a.setHref(value);
			}
			break;
		case AREA:
			Area area = (Area)target;
			if (target.getOtherAttributes().containsKey(QNAME_HREF) == true) {
				value = target.getOtherAttributes().get(QNAME_HREF);
				target.getOtherAttributes().remove(QNAME_HREF);
			} else {
				value = area.getHref();
			}
			if (value != null) {
				value = WebAppUtil.toRelativePath(path, templatePath, value);
				area.setHref(value);
			}
			break;
		case BASE:
			Base base = (Base)target;
			if (target.getOtherAttributes().containsKey(QNAME_HREF) == true) {
				value = target.getOtherAttributes().get(QNAME_HREF);
				target.getOtherAttributes().remove(QNAME_HREF);
			} else {
				value = base.getHref();
			}
			if (value != null) {
				value = WebAppUtil.toRelativePath(path, templatePath, value);
				base.setHref(value);
			}
			break;
		case LINK:
			Link link = (Link)target;
			if (target.getOtherAttributes().containsKey(QNAME_HREF) == true) {
				value = target.getOtherAttributes().get(QNAME_HREF);
				target.getOtherAttributes().remove(QNAME_HREF);
			} else {
				value = link.getHref();
			}
			if (value != null) {
				value = WebAppUtil.toRelativePath(path, templatePath, value);
				link.setHref(value);
			}
			break;
		// src
		case EMBED:
			Embed embed = (Embed)target;
			if (target.getOtherAttributes().containsKey(QNAME_SRC) == true) {
				value = target.getOtherAttributes().get(QNAME_SRC);
				target.getOtherAttributes().remove(QNAME_SRC);
			} else {
				value = embed.getSrc();
			}
			if (value != null) {
				value = WebAppUtil.toRelativePath(path, templatePath, value);
				embed.setSrc(value);
			}
			break;
		case IFRAME:
			Iframe iframe = (Iframe)target;
			if (target.getOtherAttributes().containsKey(QNAME_SRC) == true) {
				value = target.getOtherAttributes().get(QNAME_SRC);
				target.getOtherAttributes().remove(QNAME_SRC);
			} else {
				value = iframe.getSrc();
			}
			if (value != null) {
				value = WebAppUtil.toRelativePath(path, templatePath, value);
				iframe.setSrc(value);
			}
			break;
		case IMG:
			Img img = (Img)target;
			if (target.getOtherAttributes().containsKey(QNAME_SRC) == true) {
				value = target.getOtherAttributes().get(QNAME_SRC);
				target.getOtherAttributes().remove(QNAME_SRC);
			} else {
				value = img.getSrc();
			}
			value = WebAppUtil.toRelativePath(path, templatePath, value);
			img.setSrc(value);
			break;
		case INPUT:
			Input input = (Input)target;
			if (target.getOtherAttributes().containsKey(QNAME_SRC) == true) {
				value = target.getOtherAttributes().get(QNAME_SRC);
				target.getOtherAttributes().remove(QNAME_SRC);
			} else {
				value = input.getSrc();
			}
			if (value != null) {
				value = WebAppUtil.toRelativePath(path, templatePath, value);
				input.setSrc(value);
			}
			break;
		case SCRIPT:
			Script script = (Script)target;
			if (target.getOtherAttributes().containsKey(QNAME_SRC) == true) {
				value = target.getOtherAttributes().get(QNAME_SRC);
				target.getOtherAttributes().remove(QNAME_SRC);
			} else {
				value = script.getSrc();
			}
			if (value != null) {
				value = WebAppUtil.toRelativePath(path, templatePath, value);
				script.setSrc(value);
			}
			break;
		case SOURCE:
			Source source = (Source)target;
			if (target.getOtherAttributes().containsKey(QNAME_SRC) == true) {
				value = target.getOtherAttributes().get(QNAME_SRC);
				target.getOtherAttributes().remove(QNAME_SRC);
			} else {
				value = source.getSrc();
			}
			if (value != null) {
				value = WebAppUtil.toRelativePath(path, templatePath, value);
				source.setSrc(value);
			}
			break;
		case TRACK:
			Track track = (Track)target;
			if (target.getOtherAttributes().containsKey(QNAME_SRC) == true) {
				value = target.getOtherAttributes().get(QNAME_SRC);
				target.getOtherAttributes().remove(QNAME_SRC);
			} else {
				value = track.getSrc();
			}
			if (value != null) {
				value = WebAppUtil.toRelativePath(path, templatePath, value);
				track.setSrc(value);
			}
			break;
		// action
		case FORM:
			Form form = (Form)target;
			if (target.getOtherAttributes().containsKey(QNAME_ACTION) == true) {
				value = target.getOtherAttributes().get(QNAME_ACTION);
				target.getOtherAttributes().remove(QNAME_ACTION);
			} else {
				value = form.getAction();
			}
			if (value != null) {
				value = WebAppUtil.toRelativePath(path, templatePath, value);
				form.setAction(value);
			}
			break;
		// codebase
		case APPLET:
			Applet applet = (Applet)target;
			if (target.getOtherAttributes().containsKey(QNAME_CODEBASE) == true) {
				value = target.getOtherAttributes().get(QNAME_CODEBASE);
				target.getOtherAttributes().remove(QNAME_CODEBASE);
			} else {
				value = applet.getCodebase();
			}
			if (value != null) {
				value = WebAppUtil.toRelativePath(path, templatePath, value);
				applet.setCodebase(value);
			}
			break;
		case OBJECT:
			org.mixer2.jaxb.xhtml.Object object =
				(org.mixer2.jaxb.xhtml.Object)target;
			if (target.getOtherAttributes().containsKey(QNAME_CODEBASE) == true) {
				value = target.getOtherAttributes().get(QNAME_CODEBASE);
				target.getOtherAttributes().remove(QNAME_CODEBASE);
			} else {
				value = object.getCodebase();
			}
			if (value != null) {
				value = WebAppUtil.toRelativePath(path, templatePath, value);
				object.setCodebase(value);
			}
			break;
		// cite
		case BLOCKQUOTE:
			Blockquote blockquote = (Blockquote)target;
			if (target.getOtherAttributes().containsKey(QNAME_CITE) == true) {
				value = target.getOtherAttributes().get(QNAME_CITE);
				target.getOtherAttributes().remove(QNAME_CITE);
			} else {
				value = blockquote.getCite();
			}
			if (value != null) {
				value = WebAppUtil.toRelativePath(path, templatePath, value);
				blockquote.setCite(value);
			}
			break;
		case DEL:
			Del del = (Del)target;
			if (target.getOtherAttributes().containsKey(QNAME_CITE) == true) {
				value = target.getOtherAttributes().get(QNAME_CITE);
				target.getOtherAttributes().remove(QNAME_CITE);
			} else {
				value = del.getCite();
			}
			if (value != null) {
				value = WebAppUtil.toRelativePath(path, templatePath, value);
				del.setCite(value);
			}
			break;
		case INS:
			Ins ins = (Ins)target;
			if (target.getOtherAttributes().containsKey(QNAME_CITE) == true) {
				value = target.getOtherAttributes().get(QNAME_CITE);
				target.getOtherAttributes().remove(QNAME_CITE);
			} else {
				value = ins.getCite();
			}
			if (value != null) {
				value = WebAppUtil.toRelativePath(path, templatePath, value);
				ins.setCite(value);
			}
			break;
		case Q:
			Q q = (Q)target;
			if (target.getOtherAttributes().containsKey(QNAME_CITE) == true) {
				value = target.getOtherAttributes().get(QNAME_CITE);
				target.getOtherAttributes().remove(QNAME_CITE);
			} else {
				value = q.getCite();
			}
			if (value != null) {
				value = WebAppUtil.toRelativePath(path, templatePath, value);
				q.setCite(value);
			}
			break;
		default:
			break;
		}
	}

}
