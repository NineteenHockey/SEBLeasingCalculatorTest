package firstPackage;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.hamcrest.core.SubstringMatcher;
import org.junit.AfterClass;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.Select;

import org.junit.Test;

public class SeleniumTest {
	
	
	
	public static WebDriver driver = null;
	
	@BeforeClass
	public static void initializeSelenium() throws InterruptedException
	{
		System.setProperty("webdriver.gecko.driver","C:\\WebDriver\\geckodriver.exe");
		driver=new FirefoxDriver();
		//driver.get("https://www.seb.ee/eng/loan-and-leasing/leasing/car-leasing#calculator");
		driver.get("https://www.seb.ee/sites/default/files/web/calc/calc_soidukiliising.html#eng");
		Thread.sleep(5000);
		//driver.findElement(By.className("accept-selected")).click();
		//driver.findElement(By.cssSelector("h3.b")).click();
		//List<WebElement> calculators = driver.findElements(By.className("b"));
		//System.out.println(calculators.get(0).getText());
		Thread.sleep(10000);
	}
	
	public static String getLeasingMonthlyPayment(String type, String price, String payment, boolean pct, String period, String interest, String residual) throws InterruptedException
	{
		String typeId;
		if (type.equals("Operating"))
			typeId="calc08-type01";
		else if (type.equals("Financial"))
			typeId="calc08-type02";
		else
			typeId="calc08-type03";
		driver.findElement(By.id(typeId)).click();
		driver.findElement(By.id("calc08-sum")).clear();
		driver.findElement(By.id("calc08-sum")).sendKeys(price);
		
		driver.findElement(By.id("calc08-deposit")).clear();
		driver.findElement(By.id("calc08-deposit")).sendKeys(payment);
		Select units = new Select (driver.findElement(By.id("calc08-deposit-type")));
		if (pct)
			units.selectByVisibleText("%");
		else
			units.selectByVisibleText("euros");
		
		Select months = new Select (driver.findElement(By.id("calc08-period")));
		months.selectByVisibleText(period);
		driver.findElement(By.id("calc08-int")).clear();
		driver.findElement(By.id("calc08-int")).sendKeys(interest);
		driver.findElement(By.id("calc08-salvage-value")).clear();
		driver.findElement(By.id("calc08-salvage-value")).sendKeys(residual);
		Thread.sleep(2000);
		//String rslt = driver.findElement(By.id("monthly-result")).getText();
		String result = driver.findElement(By.cssSelector("#monthly-result span")).getText();
		result=result.replace(',', '.');
		return result;
	}

	@Test
	public void changeVehiclePriceOperatingLeasing() throws InterruptedException {
		String r = getLeasingMonthlyPayment("Operating","10000","10",true,"60","3.5","25");
		float higherPrice = Float.parseFloat(r);
		float lowerPrice = Float.parseFloat(getLeasingMonthlyPayment("Operating","9000","10",true,"60","3.5","25"));
		assertEquals("Monthly payment with lower price is bigger!",true,higherPrice>lowerPrice);
		
	}
	
	@Test
	public void changeDownpaymentFinancialLeasing() throws InterruptedException {
		String r = getLeasingMonthlyPayment("Financial","10000","10",true,"60","3.5","25");
		float lowerDownpayment = Float.parseFloat(r);
		float higherDownpayment = Float.parseFloat(getLeasingMonthlyPayment("Financial","10000","15",true,"60","3.5","25"));
		assertEquals("Monthly payment with higher downpayment is bigger!",true,lowerDownpayment>higherDownpayment);
		
	}
	
	@Test
	public void changeContractPeriodNoVATLeasing() throws InterruptedException {
		String r = getLeasingMonthlyPayment("FinancialNoVAT","10000","10",true,"60","3.5","25");
		float longerPeriod = Float.parseFloat(r);
		float shorterPeriod = Float.parseFloat(getLeasingMonthlyPayment("FinancialNoVAT","10000","10",true,"48","3.5","25"));
		assertEquals("Monthly payment with longer period is bigger!",true,shorterPeriod>longerPeriod);
		
	} 
	
	@AfterClass
	public static void closeSelenium()
	{
		driver.close();
	}
	

}
