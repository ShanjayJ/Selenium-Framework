package com.vmetry.pages;

import com.vmetry.lib.DriverBase;
import com.vmetry.locators.HomeLocator;

public class HomePage extends HomeLocator {
	DriverBase driver;

	public HomePage(DriverBase driver) {
		// TODO o-generated constructor stub
		super();
		this.driver = driver;
	}

	public void bookTaxi(String name, String mobNumb, String pickUp, String drop, String vehType) {
		driver.sendKeys("ID", NAME_ID, name);
		driver.sendKeys("ID", MOB_NUMB_ID, mobNumb);
		driver.sendKeys("ID", PICK_UP_ID, pickUp);
		driver.sendKeys("ID", DROP_ID, drop);
		driver.select("NAME", CAR_TYPE_NAME, vehType);
//		driver.click("CSS", BOOK_BTN_CSS);
	}
	
	/*protected void validateLoginSuccess(String loginUser) {
	s_assert.assertTrue(driver.isElementPresent("CK_SignIn_LogOut_xpath"),
			DriverBase.prop.getProperty("CK_Generic_ErrorMsg").trim());
	s_assert.assertTrue(validateLoggedUser(loginUser),
			DriverBase.prop.getProperty("CK_SignIn_InvalidUser_Msg").trim() + " "
					+ DriverBase.prop.getProperty("CK_Expected_Const").trim() + loginUser + " "
					+ DriverBase.prop.getProperty("CK_Actual_Const").trim()
					+ driver.getDisplayedText("CK_SignIn_LoggedUser_xpath"));
}

*/

}
