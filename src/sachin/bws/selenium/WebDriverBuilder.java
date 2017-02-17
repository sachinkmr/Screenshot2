package sachin.bws.selenium;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.codec.binary.Base64;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerDriverLogLevel;
import org.openqa.selenium.ie.InternetExplorerDriverService;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.BrowserMobProxyServer;
import net.lightbody.bmp.client.ClientUtil;
import net.lightbody.bmp.filters.RequestFilter;
import net.lightbody.bmp.util.HttpMessageContents;
import net.lightbody.bmp.util.HttpMessageInfo;
import sachin.bws.exceptions.BWSException;
import sachin.bws.helpers.Config;
import sachin.bws.site.Site;

/**
 *
 * @author Sachin
 */
public class WebDriverBuilder {
	private BrowserMobProxy proxy;
	private boolean proxyCheck;
	private WebDriver driver;
	Proxy seleniumProxy;

	// block to set path of all the driver exes and servers
	static {
		System.setProperty("webdriver.chrome.driver", Config.ChromeServerLocation);
		System.setProperty("webdriver.ie.driver", Config.IEServerLocation);
		System.setProperty("phantomjs.binary.path", Config.PhantomJSLocation);
		System.setProperty("webdriver.gecko.driver", Config.GeckoDriverLocation);
	}

	public WebDriver getFirefoxDriver(Site site) {
		try {

			LoggingPreferences logPrefs = new LoggingPreferences();
			logPrefs.enable(LogType.BROWSER, java.util.logging.Level.ALL);
			DesiredCapabilities caps = DesiredCapabilities.firefox();
			caps.setJavascriptEnabled(true);
			caps.setCapability("takesScreenshot", true);
			caps.setCapability(CapabilityType.LOGGING_PREFS, logPrefs);
			if (site.hasAuthentication()) {
				this.authenticate(site.getUsername(), site.getPassword());
				caps.setCapability(CapabilityType.PROXY, seleniumProxy);
			}
			FirefoxProfile fp = new FirefoxProfile();
			fp.setPreference("network.proxy.http", "localhost");
			fp.setPreference("network.proxy.http_port", proxy.getPort());
			fp.setPreference("network.proxy.ssl", "localhost");
			fp.setPreference("network.proxy.ssl_port", proxy.getPort());
			fp.setPreference("network.proxy.type", 1);
			fp.setPreference("network.proxy.no_proxies_on", "");
			fp.setPreference("general.useragent.override", site.getUserAgent());
			caps.setCapability(FirefoxDriver.PROFILE, fp);
			driver = new FirefoxDriver(caps);
			if (site.getViewPortHeight() > 0 && site.getViewPortWidth() > 0) {
				Dimension s = new Dimension(site.getViewPortWidth(), site.getViewPortHeight());
				driver.manage().window().setSize(s);
			} else {
				driver.manage().window().maximize();
			}
			driver.manage().timeouts().implicitlyWait(Config.TIMEOUT, TimeUnit.MILLISECONDS);
		} catch (Exception ex) {
			Logger.getLogger(WebDriverBuilder.class.getName()).log(Level.SEVERE, null, ex);
		}
		return driver;
	}

	public WebDriver getHeadLessDriver(Site site) {
		DesiredCapabilities caps = DesiredCapabilities.phantomjs();
		caps.setJavascriptEnabled(true);
		caps.setCapability("takesScreenshot", true);
		caps.setCapability(PhantomJSDriverService.PHANTOMJS_PAGE_SETTINGS_PREFIX, "Y");
		caps.setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS, addCommandLineArguments());
		caps.setCapability(CapabilityType.PROXY, seleniumProxy);
		caps.setCapability("phantomjs.page.settings.userAgent", site.getUserAgent());
		if (site.hasAuthentication()) {
			caps.setCapability("phantomjs.page.settings.userName", site.getUsername());
			caps.setCapability("phantomjs.page.settings.password", site.getPassword());
		}
		driver = new PhantomJSDriver(caps);
		if (site.getViewPortHeight() > 0 && site.getViewPortWidth() > 0) {
			Dimension s = new Dimension(site.getViewPortWidth(), site.getViewPortHeight());
			driver.manage().window().setSize(s);
		} else {
			driver.manage().window().maximize();
		}
		driver.manage().timeouts().implicitlyWait(Config.TIMEOUT, TimeUnit.MILLISECONDS);
		return driver;
	}

	public WebDriver getIEDriver(Site site) {
		LoggingPreferences logPrefs = new LoggingPreferences();
		logPrefs.enable(LogType.BROWSER, java.util.logging.Level.ALL);
		DesiredCapabilities capabilitiesIE = DesiredCapabilities.internetExplorer();
		capabilitiesIE.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
		capabilitiesIE.setCapability(InternetExplorerDriver.IGNORE_ZOOM_SETTING, true);
		capabilitiesIE.setCapability(InternetExplorerDriver.IE_ENSURE_CLEAN_SESSION, true);
		capabilitiesIE.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
		capabilitiesIE.setCapability(CapabilityType.LOGGING_PREFS, logPrefs);
		if (site.hasAuthentication()) {
			this.authenticate(site.getUsername(), site.getPassword());
			capabilitiesIE.setCapability(CapabilityType.PROXY, seleniumProxy);
		}
		String exe = "Resources" + File.separator + "servers" + File.separator + "IEDriverServer.exe";
		InternetExplorerDriverService.Builder serviceBuilder = new InternetExplorerDriverService.Builder();
		serviceBuilder.usingAnyFreePort();
		serviceBuilder.usingDriverExecutable(new File(exe));
		serviceBuilder.withLogLevel(InternetExplorerDriverLogLevel.WARN);
		serviceBuilder.withLogFile(new File("Logs\\logFile.txt"));
		InternetExplorerDriverService service = serviceBuilder.build();
		driver = new InternetExplorerDriver(service, capabilitiesIE);
		if (site.getViewPortHeight() > 0 && site.getViewPortWidth() > 0) {
			Dimension s = new Dimension(site.getViewPortWidth(), site.getViewPortHeight());
			driver.manage().window().setSize(s);
		} else {
			driver.manage().window().maximize();
		}
		driver.manage().timeouts().implicitlyWait(Config.TIMEOUT, TimeUnit.MILLISECONDS);
		return driver;
	}

	public WebDriver getChromeDriver(Site site) {
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--user-agent=" + site.getUserAgent());
		LoggingPreferences logPrefs = new LoggingPreferences();
		logPrefs.enable(LogType.BROWSER, java.util.logging.Level.ALL);
		DesiredCapabilities capabilities = DesiredCapabilities.chrome();
		capabilities.setJavascriptEnabled(true);
		capabilities.setCapability(CapabilityType.LOGGING_PREFS, logPrefs);
		if (site.hasAuthentication()) {
			this.authenticate(site.getUsername(), site.getPassword());
			capabilities.setCapability(CapabilityType.PROXY, seleniumProxy);
		}
		capabilities.setCapability("chrome.switches", Arrays.asList("--ignore-certificate-errors"));
		capabilities.setCapability(ChromeOptions.CAPABILITY, options);
		driver = new ChromeDriver(capabilities);
		if (site.getViewPortHeight() > 0 && site.getViewPortWidth() > 0) {
			Dimension s = new Dimension(site.getViewPortWidth(), site.getViewPortHeight());
			driver.manage().window().setSize(s);
		} else {
			driver.manage().window().maximize();
		}
		driver.manage().timeouts().implicitlyWait(Config.TIMEOUT, TimeUnit.MILLISECONDS);
		return driver;
	}

	/**
	 * This method is used to kill all the processes running internet explorer
	 * server if any is running as system processes.
	 *
	 **/
	static void killIEService() {
		String serviceName = "IEDriverServer.exe";
		try {
			if (ProcessKiller.isProcessRunning(serviceName)) {
				ProcessKiller.killProcess(serviceName);
			}
		} catch (Exception ex) {
			Logger.getLogger(WebDriverBuilder.class.getName()).log(Level.WARNING, null, ex);
		}
	}

	/**
	 * This method is used to kill all the processes running chrome server if
	 * any is running as system processes.
	 *
	 **/
	static void killChromeService() {
		String serviceName = "chromedriver.exe";
		try {
			if (ProcessKiller.isProcessRunning(serviceName)) {
				ProcessKiller.killProcess(serviceName);
			}
		} catch (Exception ex) {
			Logger.getLogger(WebDriverBuilder.class.getName()).log(Level.WARNING, null, ex);
		}
	}

	/**
	 * This method is used to kill all the processes running phantomjs driver if
	 * any is running as system processes.
	 *
	 **/
	static void killPhantomJS() {
		String serviceName = "phtantomjs.exe";
		try {
			if (ProcessKiller.isProcessRunning(serviceName)) {
				ProcessKiller.killProcess(serviceName);
			}
		} catch (Exception ex) {
			Logger.getLogger(WebDriverBuilder.class.getName()).log(Level.WARNING, null, ex);
		}
	}

	/**
	 * This method is used to kill all the processes running all web driver
	 * server if any is running as system processes.
	 *
	 **/
	public void destroy() {
		if (proxy != null) {
			proxy.stop();
		}
		if (null != driver) {
			driver.quit();
		}

	}

	/**
	 * This method is used to get webDriver Instance based on config file
	 *
	 * @return EventFiringWebDriver instance
	 **/
	public WebDriver getDriver(Site site) {
		if (Config.BROWSER_TYPE.equalsIgnoreCase("Firefox")) {
			return this.getFirefoxDriver(site);
		} else if (Config.BROWSER_TYPE.equalsIgnoreCase("Chrome")) {
			this.getChromeDriver(site);
		} else if (Config.BROWSER_TYPE.equalsIgnoreCase("IE")) {
			this.getIEDriver(site);
		} else if (Config.BROWSER_TYPE.equalsIgnoreCase("Phantom")) {
			this.getHeadLessDriver(site);
		}
		return driver;
	}

	/**
	 * This method is used to set param in phantomJS
	 *
	 * @return ArrayList instance
	 **/

	private ArrayList<String> addCommandLineArguments() {
		ArrayList<String> cliArgsCap = new ArrayList<String>();
		cliArgsCap.add("--ignore-ssl-errors=yes");
		return cliArgsCap;
	}

	/**
	 * This method is used to get running proxy instance
	 *
	 * @return BrowserMobProxy instance
	 **/
	public BrowserMobProxy getProxy() throws Exception {
		if (proxyCheck) {
			return proxy;
		} else {
			throw new BWSException("Please use webdriver instance with proxy first method to get a proxy");
		}
	}

	public WebDriver getIEDriver() {
		DesiredCapabilities capabilitiesIE = DesiredCapabilities.internetExplorer();
		capabilitiesIE.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
		capabilitiesIE.setCapability(InternetExplorerDriver.IGNORE_ZOOM_SETTING, true);
		capabilitiesIE.setCapability(InternetExplorerDriver.IE_ENSURE_CLEAN_SESSION, true);
		capabilitiesIE.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
		String exe = "Resources" + File.separator + "servers" + File.separator + "IEDriverServer.exe";
		InternetExplorerDriverService.Builder serviceBuilder = new InternetExplorerDriverService.Builder();
		serviceBuilder.usingAnyFreePort();
		serviceBuilder.usingDriverExecutable(new File(exe));
		serviceBuilder.withLogLevel(InternetExplorerDriverLogLevel.WARN);
		serviceBuilder.withLogFile(new File("Logs\\logFile.txt"));
		InternetExplorerDriverService service = serviceBuilder.build();
		driver = new InternetExplorerDriver(service, capabilitiesIE);
		driver.manage().window().maximize();
		driver.manage().timeouts().implicitlyWait(Config.TIMEOUT, TimeUnit.MILLISECONDS);
		return driver;
	}

	/**
	 * This method is used to kill all server instances
	 *
	 *
	 **/
	public static void killServers() {
		killChromeService();
		killIEService();
		killPhantomJS();
	}

	private void authenticate(String username, String password) {
		proxyCheck = true;
		proxy = new BrowserMobProxyServer();
		proxy.setHostNameResolver(ClientUtil.createDnsJavaResolver());
		proxy.setHostNameResolver(ClientUtil.createNativeCacheManipulatingResolver());
		proxy.addRequestFilter(new RequestFilter() {
			final String login = username + ":" + password;
			final String base64login = new String(Base64.encodeBase64(login.getBytes()));

			@Override
			public HttpResponse filterRequest(HttpRequest request, HttpMessageContents arg1, HttpMessageInfo arg2) {
				request.headers().add("Authorization", "Basic " + base64login);
				return null;
			}
		});
		proxy.start(0);
		seleniumProxy = ClientUtil.createSeleniumProxy(proxy);
	}
}