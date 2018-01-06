package com.vmetry.lib;

import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.vmetry.reporter.CustomReporter;

public class DriverBase {
	final private static int TIME_OUT = 10;
	public static WebDriver driver;
	public static Logger logger;
	private static WebDriverWait wait;

	public void initLogs(String suiteName, Class<?> className) {
		PatternLayout layout;
		String conversionPattern;
		FileAppender fileAppender;

		logger = Logger.getLogger(suiteName + "." + className.getSimpleName());
		layout = new PatternLayout();
		fileAppender = new FileAppender();
		conversionPattern = "[%t] %p [LINE No.:%L] %d{dd/MM/yyyy HH:mm:ss} %m%n";
		layout.setConversionPattern(conversionPattern);
		fileAppender.setFile(System.getProperty("user.dir") + File.separator + "target" + File.separator
				+ CustomReporter.reports + File.separator + CustomReporter.resultFolderName + File.separator + "Logs"
				+ File.separator + className.getSimpleName() + ".log");
		fileAppender.setLayout(layout);
		fileAppender.activateOptions();
		logger.addAppender(fileAppender);
	}
	
	public static void initDriver(String browser) {
		DesiredCapabilities capability;
		ChromeOptions chromeOpt;
		if (browser.equalsIgnoreCase("FireFox")) {
			if (System.getProperty("os.name").trim().equalsIgnoreCase("Mac OS X")) {
				driver = new FirefoxDriver();
				System.setProperty("webdriver.gecko.driver",
						System.getProperty("user.dir") + File.separator + "src" + File.separator + "test"
								+ File.separator + "resources" + File.separator + "Drivers" + File.separator
								+ "geckodriver os x");
			} else if (System.getProperty("os.name").trim().toUpperCase().contains("WINDOWS")) {
				if (System.getProperty("os.arch").trim().toUpperCase().contains("64")) {
					System.setProperty("webdriver.gecko.driver",
							System.getProperty("user.dir") + File.separator + "src" + File.separator + "test"
									+ File.separator + "resources" + File.separator + "Drivers" + File.separator
									+ "geckodriver win x64.exe");
				} else {
					System.setProperty("webdriver.gecko.driver",
							System.getProperty("user.dir") + File.separator + "src" + File.separator + "test"
									+ File.separator + "resources" + File.separator + "Drivers" + File.separator
									+ "geckodriver win x86");
				}
				driver = new FirefoxDriver();
			}
		} else if (browser.equalsIgnoreCase("Chrome") || browser.equalsIgnoreCase("Google Chrome")) {
			chromeOpt = new ChromeOptions();
			capability = DesiredCapabilities.chrome();
			chromeOpt.addArguments("--always-authorize-plugins=true");
			chromeOpt.addArguments("--disable-extensions");
			capability.setCapability(ChromeOptions.CAPABILITY, chromeOpt);
			if (System.getProperty("os.name").trim().equalsIgnoreCase("Mac OS X")) {
				driver = new ChromeDriver(capability);
				System.setProperty("webdriver.chrome.driver",
						System.getProperty("user.dir") + File.separator + "src" + File.separator + "test"
								+ File.separator + "resources" + File.separator + "Drivers" + File.separator
								+ "chromedriver");
			} else if (System.getProperty("os.name").trim().toUpperCase().contains("WINDOWS")) {
				System.setProperty("webdriver.chrome.driver",
						System.getProperty("user.dir") + File.separator + "src" + File.separator + "test"
								+ File.separator + "resources" + File.separator + "Drivers" + File.separator
								+ "chromedriver.exe");
				driver = new ChromeDriver(capability);
			}
		} else if (browser.equalsIgnoreCase("IE") || browser.equalsIgnoreCase("Internet Explorer")) {
			if (System.getProperty("os.arch").trim().toUpperCase().contains("64")) {
				System.setProperty("webdriver.ie.driver",
						System.getProperty("user.dir") + File.separator + "src" + File.separator + "test"
								+ File.separator + "resources" + File.separator + "Drivers" + File.separator
								+ "IEDriverServer x64.exe");
			} else {
				System.setProperty("webdriver.ie.driver",
						System.getProperty("user.dir") + File.separator + "src" + File.separator + "test"
								+ File.separator + "resources" + File.separator + "Drivers" + File.separator
								+ "IEDriverServer x32.exe");
			}
			driver = new InternetExplorerDriver();
		} else if (browser.equalsIgnoreCase("Edge") || browser.equalsIgnoreCase("Microsoft Edge")
				|| browser.equalsIgnoreCase("Spartan")) {
			System.setProperty("webdriver.edge.driver",
					System.getProperty("user.dir") + File.separator + "src" + File.separator + "test" + File.separator
							+ "resources" + File.separator + "Drivers" + File.separator + "MicrosoftWebDriver.exe");
			driver = new EdgeDriver();
		}
		wait = new WebDriverWait(driver, TIME_OUT);
	}


	public static void get(String url) {
		driver.get(url);
		driver.manage().window().maximize();
		driver.manage().timeouts().pageLoadTimeout(60, TimeUnit.SECONDS);
	}

	public static String getPageTitle() {
		return driver.getTitle();
	}

	public void clear(String locType, String identifier) {
		if (isElementPresent(locType, identifier)) {
			if (locType.trim().equalsIgnoreCase("XPATH"))
				driver.findElement(By.xpath(identifier)).clear();
			else if (locType.trim().equalsIgnoreCase("CSS"))
				driver.findElement(By.cssSelector(identifier)).clear();
			else if (locType.trim().equalsIgnoreCase("ID"))
				driver.findElement(By.id(identifier)).clear();
			else if (locType.trim().equalsIgnoreCase("NAME"))
				driver.findElement(By.name(identifier)).clear();
			else if (locType.trim().equalsIgnoreCase("CLASSNAME"))
				driver.findElement(By.className(identifier)).clear();
		} else
			System.out.println("Element " + identifier + " not found");
	}

	public void click(String locType, String identifier) {
		if (locType.trim().equalsIgnoreCase("XPATH")) {
			wait.until(ExpectedConditions.elementToBeClickable(By.xpath(identifier)));
			driver.findElement(By.xpath(identifier)).click();
		} else if (locType.trim().equalsIgnoreCase("CSS")) {
			wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(identifier)));
			driver.findElement(By.cssSelector(identifier)).click();
		} else if (locType.trim().equalsIgnoreCase("ID")) {
			wait.until(ExpectedConditions.elementToBeClickable(By.id(identifier)));
			driver.findElement(By.id(identifier)).click();
		} else if (locType.trim().equalsIgnoreCase("NAME")) {
			wait.until(ExpectedConditions.elementToBeClickable(By.name(identifier)));
			driver.findElement(By.name(identifier)).click();
		} else if (locType.trim().equalsIgnoreCase("LINK")) {
			wait.until(ExpectedConditions.elementToBeClickable(By.linkText(identifier)));
			driver.findElement(By.linkText(identifier)).click();
		} else if (locType.trim().equalsIgnoreCase("PLINK")) {
			wait.until(ExpectedConditions.elementToBeClickable(By.partialLinkText(identifier)));
			driver.findElement(By.partialLinkText(identifier)).click();
		} else if (locType.trim().equalsIgnoreCase("TAG")) {
			wait.until(ExpectedConditions.elementToBeClickable(By.tagName(identifier)));
			driver.findElement(By.tagName(identifier)).click();
		} else if (locType.trim().equalsIgnoreCase("CLASSNAME")) {
			wait.until(ExpectedConditions.elementToBeClickable(By.className(identifier)));
			driver.findElement(By.className(identifier)).click();
		}
	}

	public void sendKeys(String locType, String identifier, String data) {
		if (isElementPresent(locType, identifier)) {
			if (locType.trim().equalsIgnoreCase("XPATH"))
				driver.findElement(By.xpath(identifier)).sendKeys(data);
			else if (locType.trim().equalsIgnoreCase("CSS"))
				driver.findElement(By.cssSelector(identifier)).sendKeys(data);
			else if (locType.trim().equalsIgnoreCase("ID"))
				driver.findElement(By.id(identifier)).sendKeys(data);
			else if (locType.trim().equalsIgnoreCase("NAME"))
				driver.findElement(By.name(identifier)).sendKeys(data);
			else if (locType.trim().equalsIgnoreCase("CLASSNAME"))
				driver.findElement(By.className(identifier)).sendKeys(data);
		} else
			System.out.println("Element " + identifier + " not found");
	}

	public void select(String locType, String identifier, String input) {
		WebElement element;
		Select dropDown;
		List<WebElement> slctElements, options;
		String multiInput[] = null;
		boolean multiInputFlag = false;
		int attempts = 0;

		if (input.contains(",")) {
			multiInput = input.split(",");
			multiInputFlag = true;
		}

		if (isElementPresent(locType, identifier)) {
			if (locType.trim().equalsIgnoreCase("XPATH")) {
				while (attempts < 3) {
					try {
						wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(identifier)));
						element = driver.findElement(By.xpath(identifier));
						dropDown = new Select(element);
						options = dropDown.getOptions();
						if (dropDown.isMultiple()) {
							slctElements = dropDown.getAllSelectedOptions();
							if (slctElements.size() > 0)
								dropDown.deselectAll();
						}
						if (multiInputFlag) {
							for (int multiIndex = 0; multiIndex < multiInput.length; multiIndex++) {
								for (int index = 0; index < options.size(); index++) {
									if (options.get(index).getText().trim()
											.equalsIgnoreCase(multiInput[multiIndex].trim())) {
										dropDown.selectByIndex(index);
										break;
									}
								}
							}
						} else {
							for (int index = 0; index < options.size(); index++) {
								if (options.get(index).getText().trim().equalsIgnoreCase(input)) {
									dropDown.selectByIndex(index);
									break;
								} else if (options.get(index).getText().trim().toUpperCase()
										.contains(input.toUpperCase()))
									dropDown.selectByIndex(index);
							}
						}
						break;
					} catch (StaleElementReferenceException e) {
						System.out.println("Exception:" + e.getClass().getSimpleName());
						logger.fatal("Exception:" + e.getClass().getSimpleName());
						element = null;
					}
					attempts++;
				}
			} else if (locType.trim().equalsIgnoreCase("CSS")) {
				while (attempts < 3) {
					try {
						wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(identifier)));
						element = driver.findElement(By.cssSelector(identifier));
						dropDown = new Select(element);
						options = dropDown.getOptions();
						if (dropDown.isMultiple()) {
							slctElements = dropDown.getAllSelectedOptions();
							if (slctElements.size() > 0)
								dropDown.deselectAll();
						}
						if (multiInputFlag) {
							for (int multiIndex = 0; multiIndex < multiInput.length; multiIndex++) {
								for (int index = 0; index < options.size(); index++) {
									if (options.get(index).getText().trim()
											.equalsIgnoreCase(multiInput[multiIndex].trim())) {
										dropDown.selectByIndex(index);
										break;
									}
								}
							}
						} else {
							for (int index = 0; index < options.size(); index++) {
								if (options.get(index).getText().trim().equalsIgnoreCase(input)) {
									dropDown.selectByIndex(index);
									break;
								} else if (options.get(index).getText().trim().toUpperCase()
										.contains(input.toUpperCase()))
									dropDown.selectByIndex(index);
							}
						}
						break;
					} catch (StaleElementReferenceException e) {
						System.out.println("Exception:" + e.getClass().getSimpleName());
						logger.fatal("Exception:" + e.getClass().getSimpleName());
						element = null;
					}
					attempts++;
				}
			} else if (locType.trim().equalsIgnoreCase("ID")) {
				while (attempts < 3) {
					try {
						wait.until(ExpectedConditions.presenceOfElementLocated(By.id(identifier)));
						element = driver.findElement(By.id(identifier));
						dropDown = new Select(element);
						options = dropDown.getOptions();
						if (dropDown.isMultiple()) {
							slctElements = dropDown.getAllSelectedOptions();
							if (slctElements.size() > 0)
								dropDown.deselectAll();
						}
						if (multiInputFlag) {
							for (int multiIndex = 0; multiIndex < multiInput.length; multiIndex++) {
								for (int index = 0; index < options.size(); index++) {
									if (options.get(index).getText().trim()
											.equalsIgnoreCase(multiInput[multiIndex].trim())) {
										dropDown.selectByIndex(index);
										break;
									}
								}
							}
						} else {
							for (int index = 0; index < options.size(); index++) {
								if (options.get(index).getText().trim().equalsIgnoreCase(input)) {
									dropDown.selectByIndex(index);
									break;
								} else if (options.get(index).getText().trim().toUpperCase()
										.contains(input.toUpperCase()))
									dropDown.selectByIndex(index);
							}
						}
						break;
					} catch (StaleElementReferenceException e) {
						System.out.println("Exception:" + e.getClass().getSimpleName());
						logger.fatal("Exception:" + e.getClass().getSimpleName());
						element = null;
					}
					attempts++;
				}
			} else if (locType.trim().equalsIgnoreCase("NAME")) {
				while (attempts < 3) {
					try {
						wait.until(ExpectedConditions.presenceOfElementLocated(By.name(identifier)));
						element = driver.findElement(By.name(identifier));
						dropDown = new Select(element);
						options = dropDown.getOptions();
						if (dropDown.isMultiple()) {
							slctElements = dropDown.getAllSelectedOptions();
							if (slctElements.size() > 0)
								dropDown.deselectAll();
						}
						if (multiInputFlag) {
							for (int multiIndex = 0; multiIndex < multiInput.length; multiIndex++) {
								for (int index = 0; index < options.size(); index++) {
									if (options.get(index).getText().trim()
											.equalsIgnoreCase(multiInput[multiIndex].trim())) {
										dropDown.selectByIndex(index);
										break;
									}
								}
							}
						} else {
							for (int index = 0; index < options.size(); index++) {
								if (options.get(index).getText().trim().equalsIgnoreCase(input)) {
									dropDown.selectByIndex(index);
									break;
								} else if (options.get(index).getText().trim().toUpperCase()
										.contains(input.toUpperCase()))
									dropDown.selectByIndex(index);
							}
						}
						break;
					} catch (StaleElementReferenceException e) {
						System.out.println("Exception:" + e.getClass().getSimpleName());
						logger.fatal("Exception:" + e.getClass().getSimpleName());
						element = null;
					}
					attempts++;
				}
			}

		} else {
			System.out.println("Element " + identifier + " not found");
		}
	}

	public void check(String locType, String identifier, String act) {
		WebElement chkBox;

		if (isElementPresent(locType, identifier)) {
			if (locType.trim().equalsIgnoreCase("XPATH")) {
				chkBox = driver.findElement(By.xpath(identifier));
				if (chkBox.isDisplayed()) {
					if (chkBox.isEnabled()) {
						if (act.equalsIgnoreCase("Check")) {
							if (!chkBox.isSelected())
								click(locType, identifier);
						} else if (act.equalsIgnoreCase("UnCheck")) {
							if (chkBox.isSelected())
								click(locType, identifier);
						}
					}
				}
			} else if (locType.trim().equalsIgnoreCase("CSS")) {
				chkBox = driver.findElement(By.cssSelector(identifier));
				if (chkBox.isDisplayed()) {
					if (chkBox.isEnabled()) {
						if (act.equalsIgnoreCase("Check")) {
							if (!chkBox.isSelected())
								click(locType, identifier);
						} else if (act.equalsIgnoreCase("UnCheck")) {
							if (chkBox.isSelected())
								click(locType, identifier);
						}
					}
				}
			} else if (locType.trim().equalsIgnoreCase("ID")) {
				chkBox = driver.findElement(By.id(identifier));
				if (chkBox.isDisplayed()) {
					if (chkBox.isEnabled()) {
						if (act.equalsIgnoreCase("Check")) {
							if (!chkBox.isSelected())
								click(locType, identifier);
						} else if (act.equalsIgnoreCase("UnCheck")) {
							if (chkBox.isSelected())
								click(locType, identifier);
						}
					}
				}
			} else if (locType.trim().equalsIgnoreCase("NAME")) {
				chkBox = driver.findElement(By.name(identifier));
				if (chkBox.isDisplayed()) {
					if (chkBox.isEnabled()) {
						if (act.equalsIgnoreCase("Check")) {
							if (!chkBox.isSelected())
								click(locType, identifier);
						} else if (act.equalsIgnoreCase("UnCheck")) {
							if (chkBox.isSelected())
								click(locType, identifier);
						}
					}
				}
			}
		} else
			System.out.println("Element " + identifier + " not found");
	}

	public String getTextPresent(String locType, String identifier) {
		String text = null;
		if (isElementPresent(locType, identifier)) {
			if (locType.trim().equalsIgnoreCase("XPATH"))
				text = driver.findElement(By.xpath(identifier)).getText().trim();
			else if (locType.trim().equalsIgnoreCase("CSS"))
				text = driver.findElement(By.cssSelector(identifier)).getText().trim();
			else if (locType.trim().equalsIgnoreCase("ID"))
				text = driver.findElement(By.id(identifier)).getText().trim();
			else if (locType.trim().equalsIgnoreCase("NAME"))
				text = driver.findElement(By.name(identifier)).getText().trim();
			else if (locType.trim().equalsIgnoreCase("CLASSNAME"))
				text = driver.findElement(By.className(identifier)).getText().trim();
		} else
			return "";
		return text;
	}

	public void acceptAlert() {
		Alert wdAlert;

		wdAlert = driver.switchTo().alert();
		wdAlert.accept();
	}

	public void dismissAlert() {
		Alert wdAlert;

		wdAlert = driver.switchTo().alert();
		wdAlert.dismiss();
	}

	public void switchToFrame(String locType, String identifier) {
		WebElement element;

		if (locType.trim().equalsIgnoreCase("XPATH")) {
			element = driver.findElement(By.xpath(identifier));
			wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(element));
		} else if (locType.trim().equalsIgnoreCase("CSS")) {
			element = driver.findElement(By.cssSelector(identifier));
			wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(element));
		} else if (locType.trim().equalsIgnoreCase("NAME")) {
			element = driver.findElement(By.name(identifier));
			wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(identifier));
		} else if (locType.trim().equalsIgnoreCase("ID")) {
			element = driver.findElement(By.id(identifier));
			wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(identifier));
		} else if (locType.trim().equalsIgnoreCase("TAG")) {
			element = driver.findElement(By.tagName(identifier));
			wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(element));
		} else if (locType.trim().equalsIgnoreCase("CLASSNAME")) {
			element = driver.findElement(By.className(identifier));
			wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(element));
		}
	}

	public void switchToDefaultContent() {
		driver.switchTo().defaultContent();
	}

	public boolean isTextPresent(String locType, String identifier, String text) {
		if (locType.trim().equalsIgnoreCase("XPATH")) {
			try {
				wait.until(ExpectedConditions.textToBePresentInElementLocated(By.xpath(identifier), text));
				return true;
			} catch (TimeoutException e) {
				System.out.println("Exception:" + e.getClass().getSimpleName());
				logger.error("Exception:" + e.getClass().getSimpleName());
				return false;
			}
		} else if (locType.trim().equalsIgnoreCase("CSS")) {
			try {
				wait.until(ExpectedConditions.textToBePresentInElementLocated(By.cssSelector(identifier), text));
				return true;
			} catch (TimeoutException e) {
				System.out.println("Exception:" + e.getClass().getSimpleName());
				logger.error("Exception:" + e.getClass().getSimpleName());
				return false;
			}
		} else if (locType.trim().equalsIgnoreCase("ID")) {
			try {
				wait.until(ExpectedConditions.textToBePresentInElementLocated(By.id(identifier), text));
				return true;
			} catch (TimeoutException e) {
				System.out.println("Exception:" + e.getClass().getSimpleName());
				logger.error("Exception:" + e.getClass().getSimpleName());
				return false;
			}
		} else if (locType.trim().equalsIgnoreCase("NAME")) {
			try {
				wait.until(ExpectedConditions.textToBePresentInElementLocated(By.name(identifier), text));
				return true;
			} catch (TimeoutException e) {
				System.out.println("Exception:" + e.getClass().getSimpleName());
				logger.error("Exception:" + e.getClass().getSimpleName());
				return false;
			}
		} else if (locType.trim().equalsIgnoreCase("CLASSNAME")) {
			try {
				wait.until(ExpectedConditions.textToBePresentInElementLocated(By.className(identifier), text));
				return true;
			} catch (TimeoutException e) {
				System.out.println("Exception:" + e.getClass().getSimpleName());
				logger.error("Exception:" + e.getClass().getSimpleName());
				return false;
			}
		}
		return false;
	}

	public boolean isElementPresent(String locType, String identifier) {
		if (locType.trim().equalsIgnoreCase("XPATH")) {
			try {
				wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(identifier)));
				if (driver.findElements(By.xpath(identifier)).size() > 0)
					return true;
				else
					return false;
			} catch (Exception e) {
				System.out.println("Exception:" + e.getClass().getSimpleName());
				logger.fatal("Exception:" + e.getClass().getSimpleName());
				return false;
			}

		} else if (locType.trim().equalsIgnoreCase("CSS")) {
			try {
				wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(identifier)));
				if (driver.findElements(By.cssSelector(identifier)).size() > 0)
					return true;
				else
					return false;
			} catch (Exception e) {
				System.out.println("Exception:" + e.getClass().getSimpleName());
				logger.fatal("Exception:" + e.getClass().getSimpleName());
				return false;
			}

		} else if (locType.trim().equalsIgnoreCase("ID")) {
			try {
				wait.until(ExpectedConditions.presenceOfElementLocated(By.id(identifier)));
				if (driver.findElements(By.id(identifier)).size() > 0)
					return true;
				else
					return false;
			} catch (Exception e) {
				System.out.println("Exception:" + e.getClass().getSimpleName());
				logger.fatal("Exception:" + e.getClass().getSimpleName());
				return false;
			}
		} else if (locType.trim().equalsIgnoreCase("NAME")) {
			try {
				wait.until(ExpectedConditions.presenceOfElementLocated(By.name(identifier)));
				if (driver.findElements(By.name(identifier)).size() > 0)
					return true;
				else
					return false;
			} catch (Exception e) {
				System.out.println("Exception:" + e.getClass().getSimpleName());
				logger.fatal("Exception:" + e.getClass().getSimpleName());
				return false;
			}
		} else if (locType.trim().equalsIgnoreCase("LINK")) {
			try {
				wait.until(ExpectedConditions.presenceOfElementLocated(By.linkText(identifier)));
				if (driver.findElements(By.linkText(identifier)).size() > 0)
					return true;
				else
					return false;
			} catch (Exception e) {
				System.out.println("Exception:" + e.getClass().getSimpleName());
				logger.fatal("Exception:" + e.getClass().getSimpleName());
				return false;
			}

		} else if (locType.trim().equalsIgnoreCase("PLINK")) {
			try {
				wait.until(ExpectedConditions.presenceOfElementLocated(By.partialLinkText(identifier)));
				if (driver.findElements(By.partialLinkText(identifier)).size() > 0)
					return true;
				else
					return false;
			} catch (Exception e) {
				System.out.println("Exception:" + e.getClass().getSimpleName());
				logger.fatal("Exception:" + e.getClass().getSimpleName());
				return false;
			}

		} else if (locType.trim().equalsIgnoreCase("TAG")) {
			try {
				wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName(identifier)));
				if (driver.findElements(By.tagName(identifier)).size() > 0)
					return true;
				else
					return false;
			} catch (Exception e) {
				System.out.println("Exception:" + e.getClass().getSimpleName());
				logger.fatal("Exception:" + e.getClass().getSimpleName());
				return false;
			}
		} else if (locType.trim().equalsIgnoreCase("CLASSNAME")) {
			try {
				wait.until(ExpectedConditions.presenceOfElementLocated(By.className(identifier)));
				if (driver.findElements(By.className(identifier)).size() > 0)
					return true;
				else
					return false;
			} catch (Exception e) {
				System.out.println("Exception:" + e.getClass().getSimpleName());
				logger.fatal("Exception:" + e.getClass().getSimpleName());
				return false;
			}
		} else
			return false;
	}

	public void close() {
		driver.close();
	}

	public void quit() {
		driver.quit();
	}
}
