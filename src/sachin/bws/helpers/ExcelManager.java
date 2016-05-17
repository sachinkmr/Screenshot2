package sachin.bws.helpers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import sachin.bws.site.Site;
import sachin.bws.site.SiteBuilder;

/**
 *
 * @author Sachin
 */
public class ExcelManager {

	private XSSFSheet sheet;
	private XSSFWorkbook workbook;

	public ExcelManager() {
		FileInputStream f = null;
		try {
			f = new FileInputStream(new File("Config.xlsx"));
			this.workbook = new XSSFWorkbook(f);
			this.sheet = workbook.getSheet("Config");
		} catch (FileNotFoundException ex) {
			Logger.getLogger(ExcelManager.class.getName()).log(Level.WARN, null, ex);
		} catch (IOException ex) {
			Logger.getLogger(ExcelManager.class.getName()).log(Level.WARN, null, ex);
		} finally {
			try {
				f.close();
			} catch (IOException ex) {
				Logger.getLogger(ExcelManager.class.getName()).log(Level.WARN, null, ex);
			}
		}
	}

	/**
	 * This method is used to kill all the processes running all web driver
	 * server if any is running as system processes.
	 *
	 * @return map containing of config keys and value
	 **/
	public Map<String, String> readConfigData() {
		Iterator<Row> rowIterator = sheet.rowIterator();
		DataFormatter df = new DataFormatter();
		Map<String, String> map = new HashMap<>();
		while (rowIterator.hasNext()) {
			Row row = rowIterator.next();
			if (row.getRowNum() != 0) {
				map.put(df.formatCellValue(row.getCell(0)), df.formatCellValue(row.getCell(1)).toString());
			}
		}

		return map;
	}
	/**
	 * This method is used to kill all the processes running all web driver
	 * server if any is running as system processes.
	 *
	 * @return map containing of config keys and value
	 **/
	public List<Site> getSiteName() {
		this.sheet = workbook.getSheet("Sites");
		Iterator<Row> rowIterator = sheet.rowIterator();
		DataFormatter df = new DataFormatter();
		List<Site> list = new ArrayList<>();
		while (rowIterator.hasNext()) {
			Row row = rowIterator.next();
			if (row.getRowNum() != 0) {
				String a[]=df.formatCellValue(row.getCell(4)).split("/");
				Site site=new SiteBuilder(df.formatCellValue(row.getCell(0)))
						.setUsername(df.formatCellValue(row.getCell(1)))
						.setPassword(df.formatCellValue(row.getCell(2)))
						.setFolderName(df.formatCellValue(row.getCell(5)))
						.setUserAgent(df.formatCellValue(row.getCell(3)))
						.setViewPortHeight(a.length==1?0:Integer.parseInt(a[1]))
						.setViewPortWidth(a.length==1?0:Integer.parseInt(a[0]))
						.setCrawling(Boolean.parseBoolean(df.formatCellValue(row.getCell(6)).toLowerCase()))
						.build();
				list.add(site);
			}
		}
		try {
			workbook.close();
		} catch (IOException ex) {
			Logger.getLogger(ExcelManager.class.getName()).log(Level.WARN, null, ex);
		}
		return list;
	}
}
