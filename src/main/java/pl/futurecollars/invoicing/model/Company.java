package pl.futurecollars.invoicing.model;

import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Company {

  @ApiModelProperty(value = "Tax identification number", required = true, example = "552-168-66-00")
  private String taxIdentificationNumber;

  @ApiModelProperty(value = "Company address", required = true, example = "ul. Mazowiecka 134, 32-525 Radzionkow")
  private String address;

  @ApiModelProperty(value = "Company name", required = true, example = "Invoice House Ltd.")
  private String name;

  @ApiModelProperty(value = "Company cars")
  private List<Car> cars;

  @ApiModelProperty(value = "Pension insurance amount", required = true, example = "1328.75")
  private BigDecimal pensionInsurance;

  @ApiModelProperty(value = "Health insurance amount", required = true, example = "458.34")
  private BigDecimal healthInsurance;

}
