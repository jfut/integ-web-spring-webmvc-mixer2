package jp.integ.web.mixer2.helper.function;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.mixer2.jaxb.xhtml.Pre;
import org.mixer2.xhtml.AbstractJaxb;
import org.seasar.util.log.Logger;

/**
 * ヘルパー群クラスです。
 * 
 * @author Jun Futagawa
 */
public class FunctionsHelper {

	/** ロガーです。 */
	protected static Logger logger = Logger.getLogger(FunctionsHelper.class);

	/** デフォルトの事前処理用共通ヘルパー群です。 */
	private static FunctionsHelper preInstance;

	/** デフォルトの事後処理用共通ヘルパー群です。 */
	private static FunctionsHelper postInstance;

	/** タグに関するヘルパー群です。 */
	private Map<String, FunctionHelper> tagHelperMap =
		new LinkedHashMap<String, FunctionHelper>();

	/** id に関するヘルパー群です。 */
	private Map<String, FunctionIdHelper> idHelperMap =
		new LinkedHashMap<String, FunctionIdHelper>();

	/** class に関するヘルパー群です。 */
	private Map<String, FunctionClassHelper> classHelperMap =
		new LinkedHashMap<String, FunctionClassHelper>();

	/**
	 * デフォルトの事前処理用共通ヘルパー群を取得します。
	 * 
	 * @return デフォルトの事前処理用共通ヘルパー群
	 */
	public static FunctionsHelper getDefaultPreInstance() {
		return getDefaultPreInstance(false);
	}

	/**
	 * デフォルトの事前処理用共通ヘルパー群を取得します。
	 * 
	 * @param initialize
	 *            初期化指定
	 * @return デフォルトの事前処理用共通ヘルパー群
	 */
	public static synchronized FunctionsHelper getDefaultPreInstance(
			boolean initialize) {
		if (preInstance == null || initialize == true) {
			preInstance = new FunctionsHelper();
			preInstance.registerDefaultPreAll();
		}
		return preInstance;
	}

	/**
	 * デフォルトの事後処理用共通ヘルパー群を取得します。
	 * 
	 * @return デフォルトの事後処理用共通ヘルパー群
	 */
	public static FunctionsHelper getDefaultPostInstance() {
		return getDefaultPostInstance(false);
	}

	/**
	 * デフォルトの事後処理用共通ヘルパー群を取得します。
	 * 
	 * @param initialize
	 *            初期化指定
	 * @return デフォルトの事後処理用共通ヘルパー群
	 */
	public static synchronized FunctionsHelper getDefaultPostInstance(
			boolean initialize) {
		if (postInstance == null || initialize == true) {
			postInstance = new FunctionsHelper();
			postInstance.registerDefaultPostAll();
		}
		return postInstance;
	}

	/**
	 * デフォルトの事後処理用ヘルパーを登録します。
	 */
	public void registerDefaultPreAll() {
		//
		// classHelper を登録します。
		//

		// タグを削除する系の機能は、削除する中にある機能を
		// 無駄に実行しないように先に実行します。
		{
			// タグごと削除するヘルパー機能を登録します。
			registerClassHelper(new DeleteClassHelper());
			// デバッグ表示欄を作成・削除するヘルパー機能を登録します。
			registerClassHelper(new IfDebugClassHelper());
		}

		registerClassHelper(new LinkClassHelper());

		// レイアウトに関する classHelper を登録します。
		// パス調整機能などの他の機能が処理された後に処理するために、
		// 後半に実行するようにします。
		registerClassHelper(new IncludeClassHelper());
		// 別途、HTML 文字列出力前に対応する ExportItemMergeClassHelper を
		// 呼び出すことで値を埋め込みます。
		registerClassHelper(new ExportItemClassHelper());

		// リクエストを出力・利用するヘルパーは最後に実行するようにします。
		registerClassHelper(new IfClassHelper());

		//
		// idHelper を登録します。
		//

		// ExportIdHelper より先に ExportItemClassHelper を実行しないと
		// テンプレートの M_EXPORT 範囲外に書かれた記述が反映されません。
		registerIdHelper(new ExportIdHelper());

		//
		// tagHelper を登録します。
		//

		registerTagHelper(new EmptyTagHelper(Pre.class));
	}

	/**
	 * デフォルトの事後処理用ヘルパーを登録します。
	 */
	public void registerDefaultPostAll() {
		//
		// classHelper を登録します。
		//

		// タグを削除する系の機能は、削除する中にある機能を
		// 無駄に実行しないように先に実行します。
		{
			// タグごと削除するヘルパー機能を登録します。
			registerClassHelper(new DeleteClassHelper());
			// デバッグ表示欄を作成・削除するヘルパー機能です。
			registerClassHelper(new IfDebugClassHelper());
			// デバッグ情報を作成・削除するヘルパー機能を登録します。
			registerClassHelper(new DebugClassHelper());
		}

		registerClassHelper(new LinkClassHelper());
		registerClassHelper(new ActivePathClassHelper());

		// レイアウトに関する classHelper を登録します。
		// パス調整機能などの他の機能が処理された後に処理するために、
		// 後半に実行するようにします。
		registerClassHelper(new IncludeClassHelper());
		// 別途、HTML 文字列出力前に対応する ExportItemMergeClassHelper を
		// 呼び出すことで値を埋め込みます。
		registerClassHelper(new ExportItemClassHelper());

		// リクエストを出力・利用するヘルパーは最後に実行するようにします。
		registerClassHelper(new IfClassHelper());
		registerClassHelper(new OutClassHelper());
		registerClassHelper(new MessageClassHelper());
		registerClassHelper(new RequestDebugClassHelper());

		// アプリケーション名を出力するヘルパー機能です。
		registerClassHelper(new AppNameClassHelper());
		// バージョンを出力するヘルパー機能です。
		registerClassHelper(new VersionClassHelper());

		// ExportItemClassHelper でエクスポートした値を出力します。
		// ExportItemClassHelper より後に実行する必要があります。
		registerClassHelper(new ExportItemMergeClassHelper());

		//
		// idHelper を登録します。
		//

		// ExportIdHelper より先に ExportItemClassHelper を実行しないと
		// テンプレートの M_EXPORT 範囲外に書かれた記述が反映されません。
		registerIdHelper(new ExportIdHelper());

		//
		// tagHelper を登録します。
		//

		registerTagHelper(new EmptyTagHelper(Pre.class));
	}

	public void registerTagHelper(FunctionTagHelper helper) {
		if (logger.isDebugEnabled() == true) {
			logger.debug("registerTagHelper: "
				+ helper.getName()
				+ ", helperClass: "
				+ helper.getClass());
		}
		if (tagHelperMap.containsKey(helper.getName()) != true) {
			tagHelperMap.put(helper.getName(), helper);
		} else {
			throw new IllegalArgumentException("Duplicate helper name: "
				+ helper.getName());
		}
	}

	public void registerIdHelper(FunctionIdHelper helper) {
		if (logger.isDebugEnabled() == true) {
			logger.debug("registerIdHelper: "
				+ helper.getName()
				+ ", helperClass: "
				+ helper.getClass());
		}
		if (idHelperMap.containsKey(helper.getName()) != true) {
			idHelperMap.put(helper.getName(), helper);
		} else {
			throw new IllegalArgumentException("Duplicate helper name: "
				+ helper.getName());
		}
	}

	public void registerClassHelper(FunctionClassHelper helper) {
		if (logger.isDebugEnabled() == true) {
			logger.debug("registerClassHelper: "
				+ helper.getName()
				+ ", helperClass: "
				+ helper.getClass());
		}
		if (classHelperMap.containsKey(helper.getName()) != true) {
			classHelperMap.put(helper.getName(), helper);
		} else {
			throw new IllegalArgumentException("Duplicate helper name: "
				+ helper.getName());
		}
	}

	public <T extends AbstractJaxb> T replaceAll(String path,
			String templatePath, T parent) {
		return replaceAll(path, templatePath, parent, null);
	}

	public <T extends AbstractJaxb> T replaceAll(String path,
			String templatePath, T parent, HttpServletRequest request) {
		// 汎用性が高いヘルパーから処理します。
		// classHelper を処理します。
		parent =
			replaceAll(path, templatePath, parent, request, classHelperMap);
		// idHelper を処理します。
		parent = replaceAll(path, templatePath, parent, request, idHelperMap);
		// tagHelperMap を処理します。
		parent = replaceAll(path, templatePath, parent, request, tagHelperMap);
		return parent;
	}

	public <T extends AbstractJaxb> T replaceAll(String path,
			String templatePath, T parent, HttpServletRequest request,
			Map<String, ? extends FunctionHelper> helperMap) {
		for (FunctionHelper helper : helperMap.values()) {
			if (logger.isDebugEnabled() == true) {
				logger.debug("apply helper: "
					+ helper.getName()
					+ ", "
					+ helper.getClass().getName());
			}
			// リクエストが指定されている場合、FunctionRequestHelper は引数に加えて処理します。
			if (request != null && helper instanceof FunctionRequestHelper) {
				parent =
					FunctionRequestHelper.class.cast(helper).replace(
						path,
						templatePath,
						parent,
						request);
			} else {
				parent = helper.replace(path, templatePath, parent);
			}
		}
		return parent;
	}

}
