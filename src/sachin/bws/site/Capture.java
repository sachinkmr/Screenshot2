/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sachin.bws.site;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.support.events.EventFiringWebDriver;

import sachin.bws.selenium.EventHandler;
import sachin.bws.selenium.WebDriverBuilder;

/**
 *
 * @author sku202
 */
public class Capture {

	WebDriverBuilder builder;
	EventFiringWebDriver driver;
	Site site;
	String dir;

	public Capture(String dir, Site site) {
		this.dir = dir;
		this.site = site;
		builder = site.hasAuthentication() ? new WebDriverBuilder(site.getUsername(), site.getPassword())
				: new WebDriverBuilder();
		driver = new EventFiringWebDriver(builder.getDriver(site));
		EventHandler handler = new EventHandler();
		driver.register(handler);
	}

	@SuppressWarnings("unchecked")
	public void takeScreen(Map<String, LinkInfo> map, String unique, String dir2) {
		Set<String> set = map.keySet();
		for (String url : set) {
			try {
				LinkInfo link = map.get(url);
				if (site.hasAuthentication()) {
					driver.navigate().to(site.setUrlWithAuth(url));
				} else {
					driver.navigate().to(url);
				}
				driver.navigate().to(url);
				// System.out.println("Capturing screenshot: " + url);
				driver.getScreenshotAs(OutputType.FILE);
				File saveTo = new File(dir, url.hashCode() + ".png");
				File scrFile = driver.getScreenshotAs(OutputType.FILE);
				FileUtils.copyFile(scrFile, saveTo);
				link.setScreenshot(scrFile.getAbsolutePath());
				System.out.println("Screenshot captured: " + url);
			} catch (Exception ex) {
				Logger.getLogger(Capture.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		try {
			ObjectInputStream inp = new ObjectInputStream(
					new BufferedInputStream(new FileInputStream(new File(dir2 + File.separator + unique + ".bin"))));
			Map<String, Object> siteInfo = (Map<String, Object>) inp.readObject();
			inp.close();
			siteInfo.put("map", map);
			ObjectOutputStream op = new ObjectOutputStream(
					new BufferedOutputStream(new FileOutputStream(new File(dir2 + File.separator + unique + ".bin"))));
			op.writeObject(siteInfo);
			op.close();
		} catch (IOException | ClassNotFoundException ex) {
			Logger.getLogger(Capture.class.getName()).log(Level.SEVERE, null, ex);
		}
		builder.destroy();
	}
}
