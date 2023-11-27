package bildMinerX;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.stream.Stream;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class App {

	public static void main(String[] args) {
		
		String startYear;
		String startMonth;
		String startDay;
		
		String endYear;
		String endMonth;
		String endDay;
		
		ArrayList<LocalDate> dateList = new ArrayList<>();
		
		WebElement articleContainer;
		List<WebElement> articles;
	
		
		String articleTime;
		String articleCategory;
		String articleKicker;
		String articleHeadline;
		String articleUrl;
		
		String authorName;
		Boolean hasAuthor = false;
		
		int testCounter = 1;
		
		//Browser starten
		WebDriver driver = new ChromeDriver();
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
		
		Scanner sc = new Scanner(System.in);
		
		System.out.println("Define the timespan of articles ");
		System.out.println("From year: (Format: yyyy)");
		
		startYear = sc.nextLine();
		
		System.out.println("From month: (Format: mm)");
		
		startMonth = sc.nextLine();
		
		System.out.println("From day: (Format: dd)");
		
		startDay = sc.nextLine();
		
		System.out.println("Until year: (Format: yyyy)");
		
		endYear = sc.nextLine();
		
		System.out.println("Until month: (Format: mm)");
		
		endMonth = sc.nextLine();
		
		System.out.println("Until day: (Format: dd)");
		
		endDay = sc.nextLine();
		
		LocalDate start = LocalDate.parse(startYear + "-" + startMonth + "-" + startDay),
		          end   = LocalDate.parse(endYear + "-" + endMonth + "-" + endDay);

		// 4 days between (end is inclusive in this example)
		Stream.iterate(start, date -> date.plusDays(1))
		        .limit(ChronoUnit.DAYS.between(start, end) + 1)
		        .forEach(l -> dateList.add(l));
		
		driver.get("https://www.bild.de/themen/uebersicht/archiv/archiv-82532020.bild.html");
		wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(By.id("sp_message_iframe_940625")));
		new WebDriverWait(driver, Duration.ofSeconds(20)).until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"notice\"]/div[3]/div[2]/div[2]/button"))).click();
		driver.switchTo().parentFrame();
		
		for(LocalDate l: dateList) {
			driver.get("https://www.bild.de/themen/uebersicht/archiv/archiv-82532020.bild.html" + "?archiveDate=" + l.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
			
			
			articleContainer = driver.findElement(By.cssSelector(".stage-feed__viewport"));
			articles = articleContainer.findElements(By.xpath("./child::*"));

			for(int i = 0; i < articles.size(); i++) {
				if(testCounter >= 10) {
					break;
				}
				
				driver.get("https://www.bild.de/themen/uebersicht/archiv/archiv-82532020.bild.html" + "?archiveDate=" + l.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
				
				articleContainer = driver.findElement(By.cssSelector(".stage-feed__viewport"));
				articles = articleContainer.findElements(By.xpath("./child::*"));
				
				articleTime = articles.get(i).findElement(By.cssSelector("time")).getText();
				articleCategory = articles.get(i).findElement(By.cssSelector(".stage-feed-item__channel")).getText(); //TODO Kategorien filtern
				articleKicker = articles.get(i).findElement(By.cssSelector(".stage-feed-item__kicker")).getText();
				articleHeadline = articles.get(i).findElement(By.cssSelector(".stage-feed-item__headline")).getText();
				articleUrl = articles.get(i).findElement(By.cssSelector("a")).getAttribute("href");
				
				System.out.println(articleTime + "\n" +  articleCategory + "\n" + articleKicker + "\n" + articleHeadline + "\n" + articleUrl + "\n");
				
				authorName = "";
				driver.get(articleUrl);
				
				
				//Artikel sind alle in <p> und <h3> eingebettet - <b> auch?
				
				try {
					authorName = driver.findElement(By.xpath("//*[@id='main']/article/div[3]/span[2]")).getText(); //TODO Manchmal 2 NAmen Otto Walkes Und Peter Fritz 
					hasAuthor = true;
					System.out.print("Has an Author: " + hasAuthor.toString() + "\n name: " + authorName + "\n");
					
					
				}catch(NoSuchElementException e) {
					hasAuthor = false;
					System.out.print("Has an Author: " + hasAuthor.toString() + "\n");
				}
				
				testCounter++;
			}
			
			testCounter = 1;
			System.out.println();
			System.out.println("############################################################");
			System.out.println(l.toString());
			System.out.println("############################################################");
			System.out.println();
			
		}
		
		
		
		
//		DateTimeFormatter formatter = DateTimeFormatter.ofPattern( "dd/MM/uuuu" );
//		List < LocalDate > dates =
//		        List
//		                .of( "23/01/2021" , "17/02/2021" )
//		                .stream()
//		                .map(
//		                        ( String s ) -> LocalDate.parse( s , formatter )
//		                )
//		                .toList();
//
//		System.out.println( "dates = " + dates );
//		driver.quit();
	}

}
