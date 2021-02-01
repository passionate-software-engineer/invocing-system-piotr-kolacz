package pl.futurecollars.invoicing.model;

import com.google.common.base.MoreObjects;
import java.math.BigDecimal;
import java.util.Objects;

public class InvoiceEntry {

  private String description;
  private BigDecimal price;
  private BigDecimal vatValue;
  private Vat vatRate;

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public BigDecimal getPrice() {
    return price;
  }

  public void setPrice(BigDecimal price) {
    this.price = price;
  }

  public BigDecimal getVatValue() {
    return vatValue;
  }

  public void setVatValue(BigDecimal vatValue) {
    this.vatValue = vatValue;
  }

  public Vat getVatRate() {
    return vatRate;
  }

  public void setVatRate(Vat vatRate) {
    this.vatRate = vatRate;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    InvoiceEntry that = (InvoiceEntry) o;
    return Objects.equals(description, that.description) && Objects.equals(price, that.price)
        && Objects.equals(vatValue, that.vatValue) && vatRate == that.vatRate;
  }

  @Override
  public int hashCode() {
    return Objects.hash(description, price, vatValue, vatRate);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("description", description)
        .add("price", price)
        .add("vatValue", vatValue)
        .add("vatRate", vatRate)
        .toString();
  }
}
