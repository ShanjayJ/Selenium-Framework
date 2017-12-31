package com.vmetry.reporter;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.testng.IAnnotationTransformer;
import org.testng.IConfigurationListener;
import org.testng.IExecutionListener;
import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.IReporter;
import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;
import org.testng.annotations.ITestAnnotation;
import org.testng.xml.XmlSuite;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;
import com.vmetry.lib.Helper;
import com.vmetry.lib.DriverBase;

public class CustomReporter extends TestListenerAdapter implements IExecutionListener, IAnnotationTransformer,
		ISuiteListener, ITestListener, IConfigurationListener, IInvokedMethodListener, IReporter {
	private File htmlRptPath, snagPath;
	private String screenshotPath;

	public static ExtentReports htmlReport;
	public static ExtentTest htmlRptTest;
	public static String reports, resultFolderName;
	public static String rptPath;
	public static int dsIndex;

	public void onExecutionStart() {

		Date today;
		SimpleDateFormat dateFormat;
		String dateStr, time, resultFilePath;
		File src, dest;
		Map<String, String> sysInfo = new HashMap<String, String>();

		dsIndex = 1;
		if (Helper.prop == null) {
			try {
				Helper.initProperty();
			} catch (IOException e) {
				System.out.println("Exception:" + e.getClass().getSimpleName());
			}
		}

		if (reports == null) {
			today = new Date();
			dateFormat = new SimpleDateFormat("EEE, MMM d, yyyy");
			dateStr = dateFormat.format(today);
			reports = dateStr;

			if (resultFolderName == null) {
				dateFormat = new SimpleDateFormat("hh:mm, a");
				time = dateFormat.format(today);
				resultFolderName = time.replace(":", "_");
				rptPath = System.getProperty("user.dir") + File.separator + "target" + File.separator + reports
						+ File.separator + resultFolderName;
			}

			// Create a directory to store HTML report
			htmlRptPath = new File(rptPath + File.separator + "Reports");

			if (!htmlRptPath.exists())
				htmlRptPath.mkdirs();

			if (htmlRptPath.exists()) {
				screenshotPath = rptPath + File.separator + "Reports" + File.separator + "Screen Shots";
				snagPath = new File(screenshotPath);
				if (!snagPath.exists())
					snagPath.mkdirs();
			}

			resultFilePath = rptPath + File.separator + "Reports" + File.separator + "Report.html";

			src = new File(System.getProperty("user.dir") + File.separator + "src" + File.separator + "test"
					+ File.separator + "resources" + File.separator + "Files Store" + File.separator
					+ "HTML Report Template.html");

			if (src.exists()) {
				dest = new File(resultFilePath);
				try {
					FileUtils.copyFile(src, dest);
				} catch (Exception e) {
					System.out.println("Exception:" + e.getClass().getName());
				}
			}
		}

		try {
			if (htmlRptPath.exists() && snagPath.exists()) {
				sysInfo.put("Selenium Version", Helper.prop.getProperty("Selenium_Version"));
				sysInfo.put("Selenium Sub Version", Helper.prop.getProperty("Selenium_Sub_Version"));
				htmlReport = new ExtentReports((rptPath + File.separator + "Reports" + File.separator + "Report.html"),
						true);
				htmlReport.config().reportName("").reportHeadline(Helper.prop.getProperty("Report_Header"));
				htmlReport.addSystemInfo(sysInfo);
			}
		} catch (Exception e) {
			System.out.println("Exception:" + e.getClass().getName());
		}
	}

	public void onStart(ISuite suite) {
		// TODO Auto-generated method stub

	}

	/**
	 * Invoked each time before a test will be invoked. The ITestResult is only
	 * partially filled with the references to class, method, start millis and
	 * status.
	 * 
	 * @param result
	 *            The partially filled ITestResult
	 */
	@Override
	public void onTestStart(ITestResult result) {
		// TODO Auto-generated method stub
		super.onTestStart(result);
	}

	/**
	 * This method will invoked each time a method call is passed.
	 * 
	 * @param method
	 *            Method name which is about to get invoked
	 * @param testResult
	 *            The partially filled ITestResult
	 * 
	 */
	public void beforeInvocation(IInvokedMethod method, ITestResult testResult) {
		// TODO Auto-generated method stub

	}

	/**
	 * This method is implemented to report the Pass condition of a Test Case.
	 * 
	 * @param tr
	 *            This parameter is passed to report the test result.
	 */
	@Override
	public void onTestSuccess(ITestResult tr) {

		String snagTestRslt = null;
		File srcSnag;

		try {
			if (Helper.noOfDataset > 1) {
				snagTestRslt = screenshotPath + File.separator + " " + tr.getMethod().getMethodName().substring(2) + " "
						+ dsIndex + ".jpg";
				htmlRptTest.log(LogStatus.PASS, "TestCase " + tr.getMethod().getMethodName().substring(2) + " Dataset "
						+ dsIndex++ + " Completed Successfully");
			} else if (Helper.noOfDataset == 1) {
				snagTestRslt = screenshotPath + File.separator + tr.getMethod().getMethodName().substring(2) + ".jpg";
				htmlRptTest.log(LogStatus.PASS,
						"TestCase " + tr.getMethod().getMethodName().substring(2) + " Completed Successfully");
			}
			srcSnag = ((TakesScreenshot) DriverBase.driver).getScreenshotAs(OutputType.FILE);
			FileUtils.copyFile(srcSnag, new File(snagTestRslt));

			htmlRptTest.log(LogStatus.INFO, "Snapshot below:" + htmlRptTest.addScreenCapture(snagTestRslt));
		} catch (Exception e) {
			System.out.println("Exception:" + e.getClass().getSimpleName());
		} finally {
			if (DriverBase.driver != null) {
				try {
					Thread.sleep(1500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					System.out.println("Exception:" + e.getClass().getSimpleName());
				}
				DriverBase.driver.quit();
			}
		}
	}

	/**
	 * This method is implemented to report the error or exception on failure of
	 * a Test Case.
	 * 
	 * @param tr
	 *            This parameter is passed to report the test result.
	 */
	@Override
	public void onTestFailure(ITestResult tr) {
		String snagTestRslt = null;
		File srcSnag;

		try {
			if (Helper.noOfDataset > 1) {
				snagTestRslt = screenshotPath + File.separator + " " + tr.getMethod().getMethodName().substring(2) + " "
						+ dsIndex + ".jpg";
				htmlRptTest.log(LogStatus.FAIL, "TestCase " + tr.getMethod().getMethodName().substring(2) + " Dataset "
						+ dsIndex++ + " Fail." + tr.getThrowable().getLocalizedMessage());
			} else if (Helper.noOfDataset == 1) {
				snagTestRslt = screenshotPath + File.separator + tr.getMethod().getMethodName().substring(2) + ".jpg";
				htmlRptTest.log(LogStatus.FAIL, "TestCase " + tr.getMethod().getMethodName().substring(2) + " Fail."
						+ tr.getThrowable().getLocalizedMessage());
			}
			srcSnag = ((TakesScreenshot) DriverBase.driver).getScreenshotAs(OutputType.FILE);
			FileUtils.copyFile(srcSnag, new File(snagTestRslt));
			htmlRptTest.log(LogStatus.INFO, "Snapshot below:" + htmlRptTest.addScreenCapture(snagTestRslt));
		} catch (Exception e) {
			System.out.println("Exception:" + e.getClass().getSimpleName());
		} finally {
			if (DriverBase.driver != null) {
				try {
					Thread.sleep(1500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					System.out.println("Exception:" + e.getClass().getSimpleName());
				}
				DriverBase.driver.quit();
			}
		}
	}

	/**
	 * This method is implemented to report the skipping of a Test Case.
	 * 
	 * @param tr
	 *            This parameter is passed to report the test result.
	 */
	@Override
	public void onTestSkipped(ITestResult tr) {
		// TODO Auto-generated method stub
		// super.onTestSkipped(tr);
		htmlRptTest.log(LogStatus.SKIP, tr.getThrowable().toString());
	}

	/**
	 * This method will be invoked by TestNG to give you a chance to modify a
	 * TestNG annotation read from your test classes. You can change the values
	 * you need by calling any of the setters on the ITest interface. Note that
	 * only one of the three parameters testClass, testConstructor and
	 * testMethod will be non-null.
	 * 
	 * @param annotation
	 *            The annotation that was read from your test class.
	 * @param testClass
	 *            If the annotation was found on a class, this parameter
	 *            represents this class (null otherwise).
	 * @param testConstructor
	 *            If the annotation was found on a constructor, this parameter
	 *            represents this constructor (null otherwise).
	 * @param testMethod
	 *            If the annotation was found on a method, this parameter
	 *            represents this method (null otherwise).
	 * 
	 */
	@SuppressWarnings("rawtypes")
	public void transform(ITestAnnotation annotation, Class testClass, Constructor testConstructor, Method testMethod) {
		// TODO Auto-generated method stub
	}

	/**
	 * This method will invoked after a method is called.
	 * 
	 * @param method
	 *            Method name which is about to get invoked
	 * @param testResult
	 *            The partially filled ITestResult
	 * 
	 */
	public void afterInvocation(IInvokedMethod method, ITestResult testResult) {
		// TODO Auto-generated method stub
	}

	/**
	 * Invoked after all the tests have run and all their Configuration methods
	 * have been called.
	 * 
	 * @param suite
	 *            This parameter hold the Suite which is under execution
	 * 
	 */
	public void onFinish(ISuite suite) {
		// TODO Auto-generated method stub
	}

	public void generateReport(List<XmlSuite> xmlSuites, List<ISuite> suites, String outputDirectory) {
		// TODO Auto-generated method stub

	}

	/**
	 * Invoked once all the suites have been run.
	 * 
	 */
	public void onExecutionFinish() {
		// TODO Auto-generated method stub
	}
}
