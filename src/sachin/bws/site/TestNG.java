package sachin.bws.site;

import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import sachin.bws.selenium.WebDriverBuilder;

public class TestNG {
	WebDriverBuilder builder;
	WebDriver driver;
	Site site;

	@BeforeSuite(alwaysRun = true)
	public void setUp() {
		site = new SiteBuilder("http://lipton-uat.unileversolutions.com").setTimeout(120).setUsername("wlnonproduser")
				.setPassword("Pass@word11").build();
		builder = new WebDriverBuilder();
		driver = builder.getDriver(site);
	}

	@Test
	public void test() {
		driver.get(site.getUrl());
	}

	@AfterSuite(alwaysRun = true)
	public void tearDown() {
		 builder.destroy();
		WebDriverBuilder.killServers();
	}

}
