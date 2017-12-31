package com.vmetry.lib;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.regex.Pattern;

public class Helper {
	public static Properties prop = null;
	public static int noOfDataset = 0;

	public static void initProperty() throws IOException {
		String path = null;
		FileInputStream fis = null;

		if (prop == null) {
			path = System.getProperty("user.dir") + File.separator + "src" + File.separator + "test" + File.separator
					+ "java" + File.separator + "com" + File.separator + "vmetry" + File.separator + "config"
					+ File.separator + "config.properties";
			prop = new Properties();
			try {
				fis = new FileInputStream(path);
				prop.load(fis);
				path = new String();
			} catch (Exception e) {
				System.out.println("Exception:" + e.getClass().getSimpleName());
			} finally {
				fis.close();
			}
		}
	}

	public static boolean validatePreCondition(String browser) {
		DriverBase.initDriver(browser);
		DriverBase.get(prop.getProperty("AUT_URL").trim());
		if (DriverBase.getPageTitle().trim().equalsIgnoreCase(prop.getProperty("AUT_Title").trim())) {
			return true;
		} else {
			return false;
		}
	}

	protected boolean validateWithRegex(String pattern, String input) {
		return Pattern.matches(pattern, input);
	}

	protected String generateEMailID() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyyHHmmssSSS");
		String email, datePattern;

		datePattern = dateFormat.format(new Date());
		email = "automail." + datePattern + "@yopmail.com";
		return email;
	}
}
