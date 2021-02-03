package pl.futurecollars.invoicing.model;

import com.google.common.base.MoreObjects;
import java.util.Objects;

public class Company {

  private String taxIdentificationNumber;
  private String address;
  private String name;

  public Company(String taxIdentificationNumber, String address, String name) {
    this.taxIdentificationNumber = taxIdentificationNumber;
    this.address = address;
    this.name = name;
  }

  public String getTaxIdentificationNumber() {
    return taxIdentificationNumber;
  }

  public void setTaxIdentificationNumber(String taxIdentificationNumber) {
    this.taxIdentificationNumber = taxIdentificationNumber;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Company company = (Company) o;
    return Objects.equals(taxIdentificationNumber, company.taxIdentificationNumber)
        && Objects.equals(address, company.address)
        && Objects.equals(name, company.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(taxIdentificationNumber, address, name);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("taxIdentificationNumber", taxIdentificationNumber)
        .add("address", address)
        .add("name", name)
        .toString();
  }
}
