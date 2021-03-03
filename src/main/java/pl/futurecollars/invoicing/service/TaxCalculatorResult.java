package pl.futurecollars.invoicing.service;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class TaxCalculatorResult {

  private final BigDecimal income;
  private final BigDecimal costs;
  private final BigDecimal earnings;

  private final BigDecimal collectedVat;
  private final BigDecimal paidVat;
  private final BigDecimal vatToReturn;

}
