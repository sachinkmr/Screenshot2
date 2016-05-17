package sachin.bws.helpers;

import java.util.Map;

/**
 *
 * @author Sachin
 */
public class Config {
	public static final String BROWSER_TYPE;
	public static final String ConfigDevUrl;
	public static final String ConfigDevUsername;
	public static final String ConfigDevPassword;
	public static final String IEServerLocation;
	public static final String ChromeServerLocation;
	public static final String PhantomJSLocation;
	public static final int TIMEOUT;
	public static final int THREADS_TO_CRAWL;

	static {
		Map<String, String> map=new ExcelManager().readConfigData();
		BROWSER_TYPE=map.get("Browser");
		TIMEOUT=Integer.parseInt(map.get("Timeout"))*1000;
		THREADS_TO_CRAWL=Integer.parseInt(map.get("ThreadsToCrawlSite"));
		ConfigDevUrl=map.get("ConfigDevUrl");
		ConfigDevUsername=map.get("ConfigDevUsername");
		ConfigDevPassword=map.get("ConfigDevPassword");
		IEServerLocation=map.get("IEServerLocation");
		ChromeServerLocation=map.get("ChromeServerLocation");
		PhantomJSLocation=map.get("PhantomJSLocation");
	}
}
