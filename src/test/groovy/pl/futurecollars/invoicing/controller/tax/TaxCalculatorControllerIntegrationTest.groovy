package pl.futurecollars.invoicing.controller.tax

import pl.futurecollars.invoicing.controller.AbstractControllerTest
import pl.futurecollars.invoicing.model.Car
import pl.futurecollars.invoicing.model.Invoice
import pl.futurecollars.invoicing.model.InvoiceEntry
import spock.lang.Unroll

import static pl.futurecollars.invoicing.helpers.TestHelpers.company

@Unroll
class TaxCalculatorControllerIntegrationTest extends AbstractControllerTest {

    def "zeros are returned when there are no invoices in the system"() {
        when:
        def taxCalculatorResponse = calculateTax("0")

        then:
        taxCalculatorResponse.income == 0
        taxCalculatorResponse.costs == 0
        taxCalculatorResponse.earnings == 0
        taxCalculatorResponse.collectedVat == 0
        taxCalculatorResponse.paidVat == 0
        taxCalculatorResponse.vatToReturn == 0
    }

    def "zeros are returned when tax id is not matching"() {
        given:
        addUniqueInvoices(10)

        when:
        def taxCalculatorResponse = calculateTax("no_match")

        then:
        taxCalculatorResponse.income == 0
        taxCalculatorResponse.costs == 0
        taxCalculatorResponse.earnings == 0
        taxCalculatorResponse.collectedVat == 0
        taxCalculatorResponse.paidVat == 0
        taxCalculatorResponse.vatToReturn == 0
    }

    def "sum of all products is returned when tax id is matching"() {
        given:
        addUniqueInvoices(10)

        when:
        def taxCalculatorResponse = calculateTax("5")

        then:
        taxCalculatorResponse.income == 15000
        taxCalculatorResponse.costs == 0
        taxCalculatorResponse.earnings == 15000
        taxCalculatorResponse.collectedVat == 1200.0
        taxCalculatorResponse.paidVat == 0
        taxCalculatorResponse.vatToReturn == 1200.0

        when:
        taxCalculatorResponse = calculateTax("10")

        then:
        taxCalculatorResponse.income == 55000
        taxCalculatorResponse.costs == 0
        taxCalculatorResponse.earnings == 55000
        taxCalculatorResponse.collectedVat == 4400.0
        taxCalculatorResponse.paidVat == 0
        taxCalculatorResponse.vatToReturn == 4400.0

        when:
        taxCalculatorResponse = calculateTax("15")

        then:
        taxCalculatorResponse.income == 0
        taxCalculatorResponse.costs == 15000
        taxCalculatorResponse.earnings == -15000
        taxCalculatorResponse.collectedVat == 0
        taxCalculatorResponse.paidVat == 1200.0
        taxCalculatorResponse.vatToReturn == -1200.0
    }

    def "correct values are returned when company was buyer and seller"() {
        given:
        addUniqueInvoices(15) // sellers: 1-15, buyers: 10-25, 10-15 overlapping

        when:
        def taxCalculatorResponse = calculateTax("12")

        then:
        taxCalculatorResponse.income == 78000
        taxCalculatorResponse.costs == 3000
        taxCalculatorResponse.earnings == 75000
        taxCalculatorResponse.collectedVat == 6240.0
        taxCalculatorResponse.paidVat == 240.0
        taxCalculatorResponse.vatToReturn == 6000.0
    }

    def "tax is calculated correctly when car is used for personal purposes"() {
        given:
        def invoice = Invoice.builder()
                .seller(company(1))
                .buyer(company(2))
                .entries(List.of(
                        InvoiceEntry.builder()
                                .vatValue(BigDecimal.valueOf(23.45))
                                .netPrice(BigDecimal.valueOf(100))
                                .expenseRelatedToCar(
                                        Car.builder()
                                                .personalUse(true)
                                                .build()
                                )
                                .build()
                ))
                .build()

        addInvoiceAndReturnId(invoice)

        when:
        def taxCalculatorResponse = calculateTax(invoice.getSeller().getTaxIdentificationNumber())

        then: "no proportion - it applies only when you are the buyer"
        taxCalculatorResponse.income == 100
        taxCalculatorResponse.costs == 0
        taxCalculatorResponse.earnings == 100
        taxCalculatorResponse.collectedVat == 23.45
        taxCalculatorResponse.paidVat == 0
        taxCalculatorResponse.vatToReturn == 23.45

        when:
        taxCalculatorResponse = calculateTax(invoice.getBuyer().getTaxIdentificationNumber())

        then: "proportion applied - it applies when you are the buyer"
        taxCalculatorResponse.income == 0
        taxCalculatorResponse.costs == 111.73
        taxCalculatorResponse.earnings == -111.73
        taxCalculatorResponse.collectedVat == 0
        taxCalculatorResponse.paidVat == 11.72
        taxCalculatorResponse.vatToReturn == -11.72
    }

}
