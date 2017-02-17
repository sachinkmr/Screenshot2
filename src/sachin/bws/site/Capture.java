/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sachin.bws.site;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.logging.LogEntry;

import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.Screenshot;
import ru.yandex.qatools.ashot.shooting.ShootingStrategies;
import sachin.bws.selenium.WebDriverBuilder;

/**
 *
 * @author sku202
 */
public class Capture {

	WebDriverBuilder builder;
	WebDriver driver;
	Site site;
	String dir;
	AShot shot;

	public Capture(String dir, Site site) {
		this.dir = dir;
		this.site = site;
		builder = new WebDriverBuilder();
		driver = builder.getDriver(site);
		shot = new AShot().shootingStrategy(ShootingStrategies.viewportPasting(500));
	}

	@SuppressWarnings("unchecked")
	public void takeScreen(Map<String, LinkInfo> map, String unique, String dir2) {
		Set<String> set = map.keySet();
		for (String url : set) {
			try {
				LinkInfo link = map.get(url);
				driver.navigate().to(url);
				File saveTo = new File(dir, url.hashCode() + ".jpg");
				// saveTo.mkdirs();
				takeScreen(saveTo);
				link.setScreenshot(saveTo.getAbsolutePath());
				File console = new File(dir, url.hashCode() + ".txt");
				readConsole(console);
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
		// if (driver.manage().window().getSize().getWidth() < 1200) {
		// driver.findElements(By.className("bws-mobile-menu")).get(0).click();
		// File saveTo = new File(dir, "DrawerMenu.jpg");
		// saveTo.mkdirs();
		// takeScreen(saveTo);
		// }
		builder.destroy();
	}

	private void readConsole(File console) {
		try {
			List<LogEntry> logs = driver.manage().logs().get("browser").getAll();
			for (LogEntry log : logs) {
				FileUtils.write(console, log.getLevel() + " : " + log.getMessage() + "\n", "utf-8", true);
			}
		} catch (IOException e) {
			Logger.getLogger(Capture.class.getName()).log(Level.WARNING, null, e);
		}

	}

	private void takeScreen(File saveTo) {
		if (driver instanceof ChromeDriver) {
			Screenshot screenshot = shot.shootingStrategy(ShootingStrategies.viewportPasting(100))
					.takeScreenshot(driver);
			saveTo.mkdirs();
			BufferedImage image = screenshot.getImage();
			image.flush();
			try {
				ImageIO.write(image, "JPG", saveTo);
			} catch (IOException ex) {
				Logger.getLogger(Capture.class.getName()).log(Level.SEVERE, null, ex);
			}
		} else {
			// WebDriver augmentedDriver = new Augmenter().augment(driver);
			File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
			try {
				FileUtils.copyFile(scrFile, saveTo);
				FileUtils.forceDelete(scrFile);
			} catch (IOException ex) {
				Logger.getLogger(Capture.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}
}
