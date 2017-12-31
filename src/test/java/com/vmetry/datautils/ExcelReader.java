package com.vmetry.datautils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.testng.SkipException;

public class ExcelReader {
	private static Object[][] data;
	private static XSSFWorkbook workbook;
	private static XSSFSheet sheet;
	private static FileInputStream fis = null;

	public static boolean verifyRunMode(String tcName) throws Exception {
		String path, runmode, testcase;
		int totTestCases = 0;

		path = System.getProperty("user.dir") + File.separator + "src" + File.separator + "test" + File.separator
				+ "java" + File.separator + "com" + File.separator + "vmetry" + File.separator + "datautils"
				+ File.separator + "InputController.xlsx";
		try {
			fis = new FileInputStream(path);
			workbook = new XSSFWorkbook(fis);
			sheet = workbook.getSheet("Home");
			totTestCases = sheet.getLastRowNum();
			for (int i = 1; i <= totTestCases; i++) {
				runmode = sheet.getRow(i).getCell(2).getStringCellValue();
				testcase = sheet.getRow(i).getCell(0).getStringCellValue();
				if (testcase.equalsIgnoreCase(tcName) & runmode.equalsIgnoreCase("Y")) {
					return true;
				}
			}
		} catch (Exception e) {
			System.out.println("Exception:" + e.getClass().getSimpleName());
		} finally {
			fis.close();
		}
		throw new SkipException("Skiping the TestCase " + tcName);
	}

	public static Object[][] selectSingleDataOrMulitiData(String tcName) throws Exception {
		String path, testcase, runForMultiData;
		int totTestCases = 0;

		path = System.getProperty("user.dir") + File.separator + "src" + File.separator + "test" + File.separator
				+ "java" + File.separator + "com" + File.separator + "vmetry" + File.separator + "datautils"
				+ File.separator + "InputController.xlsx";
		try {
			fis = new FileInputStream(path);
			workbook = new XSSFWorkbook(fis);
			sheet = workbook.getSheet("Home");
			totTestCases = sheet.getLastRowNum();
			for (int i = 1; i <= totTestCases; i++) {
				testcase = sheet.getRow(i).getCell(0).getStringCellValue();
				if (testcase.equalsIgnoreCase(tcName)) {
					runForMultiData = sheet.getRow(i).getCell(3).getStringCellValue();
					if (runForMultiData.equalsIgnoreCase("Y")) {
						getMultiData(tcName);
						break;
					} else {
						getSingleData(tcName);
						break;
					}
				}
			}
		} catch (Exception e) {
			System.out.println("Exception:" + e.getClass().getSimpleName());
		} finally {
			fis.close();
		}
		return data;
	}

	public static Object[][] getSingleData(String tcName) throws Exception {
		String path;
		int numOfFields = 0;

		path = System.getProperty("user.dir") + File.separator + "src" + File.separator + "test" + File.separator
				+ "java" + File.separator + "com" + File.separator + "vmetry" + File.separator + "datautils"
				+ File.separator + "InputController.xlsx";
		try {
			fis = new FileInputStream(path);
			workbook = new XSSFWorkbook(fis);
			sheet = workbook.getSheet(tcName);
			numOfFields = sheet.getLastRowNum();
			data = new Object[1][numOfFields];
			for (int i = 0; i < numOfFields; i++) {
				sheet.getRow(i + 1).getCell(1).setCellType(CellType.STRING);
				data[0][i] = sheet.getRow(i + 1).getCell(1).getStringCellValue();
			}
		} catch (Exception e) {
			System.out.println("Exception:" + e.getClass().getSimpleName());
		} finally {
			fis.close();
		}
		return data;
	}

	public static Object[][] getMultiData(String tcName) throws FileNotFoundException, IOException {
		String path;
		int totRows = 0, totFields = 0;

		path = System.getProperty("user.dir") + File.separator + "src" + File.separator + "test" + File.separator
				+ "java" + File.separator + "com" + File.separator + "vmetry" + File.separator + "datautils"
				+ File.separator + "MultiData.xlsx";
		try {
			fis = new FileInputStream(path);
			workbook = new XSSFWorkbook(fis);
			sheet = workbook.getSheet(tcName);
			totRows = sheet.getLastRowNum();
			totFields = sheet.getRow(0).getLastCellNum() - 1;
			data = new Object[totFields][totRows];
			for (int i = 0; i < totRows; i++) {
				for (int j = 0; j < totFields; j++) {
					sheet.getRow(i + 1).getCell(j + 1).setCellType(CellType.STRING);
					data[j][i] = sheet.getRow(i + 1).getCell(j + 1).getStringCellValue();
				}
			}
		} catch (Exception e) {
			System.out.println("Exception:" + e.getClass().getSimpleName());
		} finally {
			fis.close();
		}
		return data;
	}
}
