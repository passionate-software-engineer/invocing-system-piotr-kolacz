package pl.futurecollars.invoicing.controller.tax;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import pl.futurecollars.invoicing.service.TaxCalculatorService;

@RestController
@AllArgsConstructor
public class TaxCalculatorController implements TaxCalculatorApi {

  private final TaxCalculatorService taxService;

  @Override
  public TaxCalculatorResponse calculateTaxes(String taxIdentificationNumber) {
    return TaxCalculatorResponse.builder()
        .income(taxService.income(taxIdentificationNumber))
        .costs(taxService.costs(taxIdentificationNumber))
        .earnings(taxService.getEarnings(taxIdentificationNumber))
        .incomingVat(taxService.incomingVat(taxIdentificationNumber))
        .outgoingVat(taxService.outgoingVat(taxIdentificationNumber))
        .vatToReturn(taxService.getVatToReturn(taxIdentificationNumber))
        .build();
  }
}
