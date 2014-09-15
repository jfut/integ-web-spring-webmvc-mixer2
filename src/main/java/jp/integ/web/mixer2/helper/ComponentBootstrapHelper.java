package jp.integ.web.mixer2.helper;

import java.util.List;

import org.mixer2.jaxb.xhtml.A;
import org.mixer2.jaxb.xhtml.Div;
import org.mixer2.jaxb.xhtml.H4;
import org.mixer2.util.CastUtil;
import org.mixer2.xhtml.AbstractJaxb;
import org.mixer2.xhtml.TagCreator;

/**
 * Twitter Bootstrap 用のコンポーネントヘルパーです。
 * 
 * @author Jun Futagawa
 */
public class ComponentBootstrapHelper {

	//
	// Collapse
	//
	// <div class="panel-group">
	// <div class="panel panel-default">
	// <div class="panel-heading">
	// <h4 class="panel-title">
	// <a id="HERE.panel-title" data-toggle="collapse" data-parent="#accordion"
	// href="#HERE.collapse">
	// [DEBUG]
	// </a>
	// </h4>
	// </div>
	// <div id="HERE.panel-collapse" class="panel-collapse collapse">
	// <div id="HERE.panel-body" class="panel-body">
	// dummy.debug
	// </div>
	// </div>
	// </div>
	// </div><!-- /.panel-group -->
	//

	/**
	 * Collapse を作成して返します。
	 * 
	 * @param id
	 *            ユニークな ID
	 * @param title
	 *            タイトル
	 * @param content
	 *            コンテンツ
	 * @param panelCssStyle
	 *            panel の CSS スタイル
	 * @return Collapse
	 */
	public static Div collapseAccordionDiv(String id, String title,
			Object content, String... panelCssStyle) {
		Div collapseDiv = collapsePanelGroupDiv();
		{
			Div collapsePanelDiv =
				collapsePanelDiv(id, title, content, panelCssStyle);
			collapseDiv.getContent().add(collapsePanelDiv);
		}
		return collapseDiv;
	}

	/**
	 * Collapse Panel Group を作成して返します。
	 * 
	 * @return Collapse Panel Group
	 */
	public static Div collapsePanelGroupDiv() {
		Div collapsePanelGroupDiv = TagCreator.div();
		collapsePanelGroupDiv.addCssClass("panel-group");
		return collapsePanelGroupDiv;
	}

	/**
	 * Collapse panel を作成して返します。
	 * 
	 * @param id
	 *            ユニークな ID
	 * @param title
	 *            タイトル
	 * @param content
	 *            コンテンツ
	 * @param panelCssStyle
	 *            panel の CSS スタイル
	 * @return Collapse Panel
	 */
	public static Div collapsePanelDiv(String id, String title, Object content,
			String... panelCssStyle) {
		Div collapsePanelDiv = TagCreator.div();
		collapsePanelDiv.addCssClass("panel");
		if (panelCssStyle.length == 0) {
			collapsePanelDiv.addCssClass("panel-default");
		} else {
			for (String cssStyle : panelCssStyle) {
				collapsePanelDiv.addCssClass(cssStyle);
			}
		}
		{
			Div accordionHeadingDiv = TagCreator.div();
			accordionHeadingDiv.addCssClass("panel-heading");
			{
				H4 panelTitleH4 = TagCreator.h4();
				panelTitleH4.addCssClass("panel-title");
				{
					A toggleA = TagCreator.a();
					toggleA.setData("toggle", "collapse");
					toggleA.setData("parent", "#accordion");
					toggleA.setHref("#" + id);
					toggleA.getContent().add(title);
					panelTitleH4.getContent().add(toggleA);
				}
				accordionHeadingDiv.getContent().add(panelTitleH4);
			}
			collapsePanelDiv.getContent().add(accordionHeadingDiv);
			Div collapseDiv = TagCreator.div();
			collapseDiv.setId(id);
			collapseDiv.addCssClass("panel-collapse");
			collapseDiv.addCssClass("collapse");
			{
				Div panelBodyDiv = TagCreator.div();
				panelBodyDiv.addCssClass("panel-body");
				{
					if (content == null) {
						panelBodyDiv.getContent().add("null");
					} else if (content instanceof List) {
						List<Object> contentList = CastUtil.cast(content);
						panelBodyDiv.getContent().addAll(contentList);
					} else if (content instanceof AbstractJaxb) {
						panelBodyDiv.getContent().add(content);
					} else {
						panelBodyDiv.getContent().add(content.toString());
					}
				}
				collapseDiv.getContent().add(panelBodyDiv);
			}
			collapsePanelDiv.getContent().add(collapseDiv);
		}
		return collapsePanelDiv;
	}

}
