package pl.futurecollars.invoicing.model;

import com.google.common.base.MoreObjects;
import java.math.BigDecimal;

public enum Vat {

  VAT_23(23),
  VAT_8(8),
  VAT_7(7),
  VAT_5(5),
  VAT_0(0),
  VAT_ZW(0);

  private final BigDecimal rate;

  Vat(int rate) {
    this.rate = BigDecimal.valueOf(rate);
  }

  public BigDecimal getRate() {
    return rate;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("rate", rate)
        .toString();
  }
}
