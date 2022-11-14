package zerobase.project3.service;

import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.Trie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import zerobase.project3.model.Company;
import zerobase.project3.model.ScrapedResult;
import zerobase.project3.persist.CompanyRepository;
import zerobase.project3.persist.DividendRepository;
import zerobase.project3.persist.entity.CompanyEntity;
import zerobase.project3.persist.entity.DividendEntity;
import zerobase.project3.scraper.Scraper;

@Service
@AllArgsConstructor
public class CompanyService {

  private final Trie trie; // 꼭 싱글톤으로 해줘야함. 여러 방법중 하나인 bean으로 관리해주기 사용
  private final Scraper yahooFinanceScraper;
  private final CompanyRepository companyRepository;
  private final DividendRepository dividendRepository;

  public Company save(String ticker) {
    boolean exists = this.companyRepository.existsByTicker(ticker);
    if(exists) {
      throw new RuntimeException("already exists ticker -> " + ticker);
    }
    return this.storeCompanyAndDividend(ticker);
  }

  private Company storeCompanyAndDividend(String ticker) {
    // ticker 를 기준으로 회사를 스크래핑
    Company company = this.yahooFinanceScraper.scrapCompanyByTicker(ticker);
    if (ObjectUtils.isEmpty(company)) {
      throw new RuntimeException("failed to scrap ticker -> " + ticker);
    }

    //해당 회사가 존재할 경우, 회사의 배당금 정보를 스크래핑

    ScrapedResult scrapedResult = this.yahooFinanceScraper.scrap(company);

    // 스크래핑 결과를 저장해서 저장한 company 정보를 반환
    CompanyEntity companyEntity = this.companyRepository.save(new CompanyEntity(company));
    List<DividendEntity> dividendEntities = scrapedResult.getDividends().stream()
                                                          .map(e -> new DividendEntity(companyEntity.getId(), e))
                                                          .collect(Collectors.toList());

    this.dividendRepository.saveAll(dividendEntities);
    return company;
  }

  public Page<CompanyEntity> getAllCompany(Pageable pageable) {
    return this.companyRepository.findAll(pageable);
  }

  public void addAutocompleteKeyWord(String keyword) { //trie 에 넣기
    this.trie.put(keyword, null);
  }

  public List<String> autocomplete(String keyword) { //trie 에서 단어를 찾아오는 로직
   return (List<String>) this.trie.prefixMap(keyword).keySet()
       .stream()
//       .limit(10) //limit으로 가져오는 수 제한
       .collect(Collectors.toList());

  }

  public void deleteAutocompleteKeyword(String keyword) { //trie 에서 데이터 삭제
    this.trie.remove(keyword);
  }

  public List<String> getCompanyNamesByKeyword(String keyword) {
    Pageable limit = PageRequest.of(0,10);
    Page<CompanyEntity> companyEntities = this.companyRepository.findByNameStartingWithIgnoreCase(
        keyword, limit);
    return companyEntities.stream().map(e -> e.getName())
        .collect(Collectors.toList());
  }
}
