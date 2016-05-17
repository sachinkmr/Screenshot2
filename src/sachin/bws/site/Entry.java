package sachin.bws.site;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


import sachin.bws.helpers.ExcelManager;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author sku202
 */
public class Entry {
	public static void main(String... ar) {
		try {
			List<Site> list = new ExcelManager().getSiteName();
			for (Site BWSsite : list) {
				System.out.println("Running For: " + BWSsite.getUrl());
					if (BWSsite.isRunning()) {
						new Finder(BWSsite).go(BWSsite.getFolderName());
					} else {
						System.out.println("Site is not running. Giving " + BWSsite.getStatusCode()
								+ " status code. Please verify");
					}
					if (BWSsite.isRunning()) {
						new Finder(BWSsite).go(BWSsite.getFolderName());
					} else {
						System.out.println("Site is not running. Giving " + BWSsite.getStatusCode()
								+ " status code. Please verify");
					}
					if (BWSsite.isRunning()) {
						new Finder(BWSsite).go(BWSsite.getFolderName());
					} else {
						System.out.println("Site is not running. Giving " + BWSsite.getStatusCode()
								+ " status code. Please verify");
					}
					if (BWSsite.isRunning()) {
						new Finder(BWSsite).go(BWSsite.getFolderName());
					} else {
						System.out.println("Site is not running. Giving " + BWSsite.getStatusCode()
								+ " status code. Please verify");
					}
				System.out.println("====================================================================");
			}
		} catch (Exception e) {
			Logger.getLogger(Finder.class.getName()).log(Level.SEVERE, null, e);
		}
	}
}
