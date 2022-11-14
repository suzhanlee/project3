package zerobase.project3.scraper;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import zerobase.project3.model.Company;
import zerobase.project3.model.Dividend;
import zerobase.project3.model.ScrapedResult;
import zerobase.project3.model.constants.Month;

@Component
public class YahooFinanceScraper implements Scraper {
  private static final String STATISTICS_URL = "https://finance.yahoo.com/quote/%s/history?period1=%d&period2=%d&interval=1mo";
  //문자는 s 숫자는 d

  private static final String SUMMARY_URL = "https://finance.yahoo.com/quote/%s?p=%s";
  private static final long START_TIME = 86400;


  @Override
  public ScrapedResult scrap(Company company) {

    var scrapResult = new ScrapedResult();
    scrapResult.setCompany(company);

    try {
//      long start = 0;
      long now = System.currentTimeMillis() / 1000;
      String url = String.format(STATISTICS_URL, company.getTicker(), START_TIME, now);
      Connection connection = Jsoup.connect(url);
      Document document = connection.get();
      Elements parsingDivs = document.getElementsByAttributeValue("data-test", "historical-prices");
      Element tableElement = parsingDivs.get(0);
//			System.out.println("tableElement = " + tableElement);

      Element tbody = tableElement.children().get(1);

      List<Dividend> dividends = new ArrayList<>();
      for (Element e : tbody.children()) {
        String txt = e.text();
        if (!txt.endsWith("Dividend")) {
          continue;
        }
//        System.out.println("txt = " + txt);
        String[] splits = txt.split(" ");
        int month = Month.strToNumber(splits[0]);
        int day = Integer.parseInt(splits[1].replace(",", ""));
        int year = Integer.parseInt(splits[2]);
        String dividend = splits[3];

        if(month < 0) {
          throw new RuntimeException("Unexpected Month enum value -> " + splits[0]);
        }

        dividends.add(Dividend.builder()
            .date(LocalDateTime.of(year,month,day,0,0))
            .dividend(dividend)
            .build());
      }
      scrapResult.setDividends(dividends);


    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    return scrapResult;

  }

  @Override
  public Company scrapCompanyByTicker(String ticker) {
    String url = String.format(SUMMARY_URL,ticker,ticker);

    try {
      Document document = Jsoup.connect(url).get();
      Element titleEle = document.getElementsByTag("h1").get(0);

      System.out.println("titleEle = " + titleEle);
      String title = titleEle.text().split(" ()")[1].trim();
//      System.out.println("title = " + title);

      return Company.builder()
                    .ticker(ticker)
                    .name(title)
                    .build();

    } catch (IOException e) {
      e.printStackTrace();

    }
    return null;

  }




}
