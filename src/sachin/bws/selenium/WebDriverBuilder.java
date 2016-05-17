package sachin.bws.selenium;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
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

	public WebDriverBuilder(String username,String password) {
		super();
		proxyCheck = true;
        proxy = new BrowserMobProxyServer();
        proxy.setHostNameResolver(ClientUtil.createDnsJavaResolver());
        proxy.setHostNameResolver(ClientUtil.createNativeCacheManipulatingResolver());
//        proxy.addFirstHttpFilterFactory(new RequestFilterAdapter.FilterSource(filter, 16777216));
        proxy.addRequestFilter(new RequestFilter(){
        	final String login = username + ":" + password;
            final String base64login = new String(Base64.encodeBase64(login.getBytes()));
			@Override
			public HttpResponse filterRequest(HttpRequest request, HttpMessageContents arg1, HttpMessageInfo arg2) {
				request.headers().add("Authorization", "Basic " + base64login);
				return null;
			}});
        proxy.start(0);
        seleniumProxy = ClientUtil.createSeleniumProxy(proxy);
	}

	public WebDriverBuilder() {
	}

	// block to set path of all the driver exes and servers
	static {
		System.setProperty("webdriver.chrome.driver",Config.ChromeServerLocation); // setting chrome driver path
		System.setProperty("webdriver.ie.driver",Config.IEServerLocation); // setting IE driver path
		System.setProperty("phantomjs.binary.path",Config.PhantomJSLocation);
	}

	public WebDriver getFirefoxDriver(Site site) {
        try {
            FirefoxProfile ffp = new FirefoxProfile();
    		ffp.setPreference("general.useragent.override", site.getUserAgent());
    		DesiredCapabilities caps = DesiredCapabilities.firefox();
    		caps.setJavascriptEnabled(true);
    		caps.setCapability(FirefoxDriver.PROFILE, ffp);
    		caps.setCapability("takesScreenshot", true);
    		if(site.hasAuthentication())
    		caps.setCapability(CapabilityType.PROXY, seleniumProxy);
    		// User Name & Password Settings
    		driver = new FirefoxDriver(caps);
    		if (site.getViewPortHeight() > 0 && site.getViewPortWidth() > 0) {
    			Dimension s = new Dimension(site.getViewPortWidth(), site.getViewPortHeight());
    			driver.manage().window().setSize(s);
    		} else {
    			driver.manage().window().maximize();
    		}
    		driver.manage().timeouts().implicitlyWait(Config.TIMEOUT, TimeUnit.MILLISECONDS);
        } catch (Exception ex) {
            Logger.getLogger(WebDriverBuilder.class.getName()).log(Level.WARN, null, ex);
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
        driver=new PhantomJSDriver(caps);
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
		DesiredCapabilities capabilitiesIE = DesiredCapabilities.internetExplorer();
		capabilitiesIE.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
		capabilitiesIE.setCapability(InternetExplorerDriver.IGNORE_ZOOM_SETTING, true);
		capabilitiesIE.setCapability(InternetExplorerDriver.IE_ENSURE_CLEAN_SESSION, true);
		capabilitiesIE.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
//		proxyCheck = true;
//        if(null!=proxy) proxy.stop();
//		proxy = new BrowserMobProxyServer();
//        proxy.setHostNameResolver(ClientUtil.createDnsJavaResolver());
//        proxy.setHostNameResolver(ClientUtil.createNativeCacheManipulatingResolver());
////        proxy.addFirstHttpFilterFactory(new RequestFilterAdapter.FilterSource(filter, 16777216));
//        proxy.addRequestFilter(new RequestFilter(){
//        	final String login = site.getUsername() + ":" + site.getPassword();
//            final String base64login = new String(Base64.encodeBase64(login.getBytes()));
//			@Override
//			public HttpResponse filterRequest(HttpRequest request, HttpMessageContents arg1, HttpMessageInfo arg2) {
//				if(site.hasAuthentication())
//				request.headers().add("Authorization", "Basic " + base64login);
//				request.headers().add("user-agent",site.getUserAgent());
//				return null;
//			}});
//        proxy.start(0);
        seleniumProxy = ClientUtil.createSeleniumProxy(proxy);
		capabilitiesIE.setCapability(CapabilityType.PROXY, seleniumProxy);
//		capabilitiesIE.setCapability(InternetExplorerDriver.IE_USE_PRE_PROCESS_PROXY, seleniumProxy);
        String exe = "Resources" + File.separator + "servers" + File.separator + "IEDriverServer.exe";
        InternetExplorerDriverService.Builder serviceBuilder = new InternetExplorerDriverService.Builder();
        serviceBuilder.usingAnyFreePort(); // This specifies that sever can pick any available free port to start
        serviceBuilder.usingDriverExecutable(new File(exe)); //Tell it where you server exe is
        serviceBuilder.withLogLevel(InternetExplorerDriverLogLevel.WARN); //Specifies the log level of the server
        serviceBuilder.withLogFile(new File("Logs\\logFile.txt")); //Specify the log file. Change it based on your system
        InternetExplorerDriverService service = serviceBuilder.build(); //Create a driver service and pass it to Internet explorer driver instance
        driver = new InternetExplorerDriver(service,capabilitiesIE);
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
		options.addArguments("--user-agent="+site.getUserAgent());
		DesiredCapabilities capabilities = DesiredCapabilities.chrome();
		capabilities.setJavascriptEnabled(true);
		if(site.hasAuthentication())
		capabilities.setCapability(CapabilityType.PROXY, seleniumProxy);
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
			Logger.getLogger(WebDriverBuilder.class.getName()).log(Level.WARN, null, ex);
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
			Logger.getLogger(WebDriverBuilder.class.getName()).log(Level.WARN, null, ex);
		}
	}
	/**
	 * This method is used to kill all the processes running phantomjs driver if
	 * any is running as system processes.
	 *
	 **/
	static  void killPhantomJS() {
        String serviceName = "phtantomjs.exe";
        try {
            if (ProcessKiller.isProcessRunning(serviceName)) {
                ProcessKiller.killProcess(serviceName);
            }
        } catch (Exception ex) {
            Logger.getLogger(WebDriverBuilder.class.getName()).log(Level.WARN, null, ex);
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
		if(null!=driver){
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
		}
		else if (Config.BROWSER_TYPE.equalsIgnoreCase("Phantom")) {
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
        cliArgsCap.add("--ignore-ssl-errors=yes"); // parameter to access https page
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
        serviceBuilder.usingAnyFreePort(); // This specifies that sever can pick any available free port to start
        serviceBuilder.usingDriverExecutable(new File(exe)); //Tell it where you server exe is
        serviceBuilder.withLogLevel(InternetExplorerDriverLogLevel.WARN); //Specifies the log level of the server
        serviceBuilder.withLogFile(new File("Logs\\logFile.txt")); //Specify the log file. Change it based on your system
        InternetExplorerDriverService service = serviceBuilder.build(); //Create a driver service and pass it to Internet explorer driver instance
        driver = new InternetExplorerDriver(service,capabilitiesIE);
//        InternetExplorerDriver driver = new InternetExplorerDriver(service);
//        System.setProperty("webdriver.ie.driver", service);
//        WebDriver driver = new InternetExplorerDriver();
		driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Config.TIMEOUT, TimeUnit.MILLISECONDS);
        return driver;
    }


	/**
	 * This method is used to kill all server instances
	 *
	 *
	 **/
	public static void killServers(){
		killChromeService();
		killIEService();
		killPhantomJS();
	}

}