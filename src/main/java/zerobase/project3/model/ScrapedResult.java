package zerobase.project3.model;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import zerobase.project3.persist.entity.DividendEntity;

@Data
@AllArgsConstructor
public class ScrapedResult {

  private Company company;
  private List<Dividend> dividends; // 한 회사는 여러개의 배당금 정보를 가지고 있음

  public ScrapedResult() {
    this.dividends = new ArrayList<>();
  }

}
