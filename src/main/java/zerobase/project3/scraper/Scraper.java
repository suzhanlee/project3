package zerobase.project3.scraper;

import zerobase.project3.model.Company;
import zerobase.project3.model.ScrapedResult;


public interface Scraper {

  Company scrapCompanyByTicker(String ticker);
  ScrapedResult scrap(Company company);




}
