package zerobase.project3.scheduler;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import zerobase.project3.model.Company;
import zerobase.project3.model.ScrapedResult;
import zerobase.project3.persist.CompanyRepository;
import zerobase.project3.persist.DividendRepository;
import zerobase.project3.persist.entity.CompanyEntity;
import zerobase.project3.persist.entity.DividendEntity;
import zerobase.project3.scraper.Scraper;

@Slf4j
@Component
@AllArgsConstructor
public class ScraperScheduler {

//  @Scheduled(cron = "0/5 * * * * *") //연도는 생략 가능
//  public void test() {
//
//    System.out.println(" now -> " + System.currentTimeMillis());
//
//  }

  private final CompanyRepository companyRepository;
  private final Scraper yahooFinanceScraper;
  private final DividendRepository dividendRepository;

  // 일정 주기마다 수행
  @Scheduled(cron = "${scheduler.scrap.yahoo}") //매일 정각에 실행된다.
  public void yahooFinanceScheduling() {
    log.info("scraping scheduler is started");
    // 저장된 회사 목록을 조회
    List<CompanyEntity> companies = this.companyRepository.findAll();

    //회사마다 배당금 정보를 새로 스크래핑
    for (var company : companies) {
      log.info("scraping scheduler is started -> " + company.getName()); //로그 남기기

      ScrapedResult scrapedResult = this.yahooFinanceScraper.scrap(Company.builder()
          .name(company.getName())
          .ticker(company.getTicker()).build());

      // 스크래핑한 배당금 정보중 데이터베이스에 없는 값은 저장
      scrapedResult.getDividends().stream()
          //dividend모델을 디비든 엔티티로 매핑
          .map(e -> new DividendEntity(company.getId(), e))
          //엘리먼트를 하나씩 디비든 레파지토리에 삽입
          .forEach(e -> {
            boolean exists = this.dividendRepository.existsByCompanyIdAndDate(e.getCompanyId(),
                e.getDate());
            if(!exists) {
              this.dividendRepository.save(e);
            }
          });

      //연속적으로 스크래핑 대상 사이트 서버에 요청을 날리지 않도록 일시정지
      try {
        Thread.sleep(3000); //3초를 의미 3초간 스레드 정지후 진행 후 3초 정지 반복
        //wait은 스레드를 대기 상태로 빠뜨림(스스로 다시 작업 진행재개 되지 않은)
        //wait -> notify, notifyAll 메서드를 호출할 때까지 자동으로 깨지 않음.
      } catch (InterruptedException e) { // 스레드끼리 인터럽트를 주고 받을때 인터럽트를 받는 스레드가
        //blocking 될 수 있는 상태가 될때 위의 exception 이 발생
//        e.printStackTrace(); //단지 위의 예외가 발생했을때 오류를 표시해주는것만드로는 부족
        Thread.currentThread().interrupt(); //그래서 옆과 같이 처리함

      }
    }


  }

}
