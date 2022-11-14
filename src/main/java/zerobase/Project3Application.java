package zerobase;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableCaching
public class Project3Application {

	public static void main(String[] args) {
		SpringApplication.run(Project3Application.class, args);

//		YahooFinanceScrapper scrapper = new YahooFinanceScrapper();
////		var result = scrapper.scrap(Company.builder().ticker("O").build());
////		System.out.println("result = " + result);
//
//		var result = scrapper.scrapCompanyByTicker("MMM");
//		System.out.println("result = " + result);



	}
}
