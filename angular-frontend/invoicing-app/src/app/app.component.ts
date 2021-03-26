import { Component } from '@angular/core';
import { Company } from "./company";

@Component({
    selector: 'app-root',
    templateUrl: './app.component.html',
    styleUrls: ['./app.component.css']
})

export class AppComponent {

    companies: Company[] = [
        new Company(
            "111-111-11-11",
            "ul. First 1",
            "First Ltd.",
            1111.11,
            111.11
        ),
        new Company(
            "222-222-22-22",
            "ul. Second 2",
            "Second Ltd.",
            2222.22,
            222.22
        )
    ];

    newCompany: Company = new Company("", "", "", 0, 0);

    addCompany() {
        this.companies.push(this.newCompany);
        this.newCompany = new Company("", "", "", 0, 0);
    }

    deleteCompany(companyToDelete: Company) {
        this.companies = this.companies.filter(company => company !== companyToDelete);
    }

    triggerUpdate(company: Company) {
        company.editedCompany = new Company(
            company.taxIdentificationNumber,
            company.address,
            company.name,
            company.healthInsurance,
            company.pensionInsurance
        )

        company.editMode = true
    }

    cancelCompanyUpdate(company: Company) {
        company.editMode = false;
    }

    updateCompany(updatedCompany: Company) {
        updatedCompany.taxIdentificationNumber = updatedCompany.editedCompany.taxIdentificationNumber
        updatedCompany.address = updatedCompany.editedCompany.address
        updatedCompany.name = updatedCompany.editedCompany.name
        updatedCompany.healthInsurance = updatedCompany.editedCompany.healthInsurance
        updatedCompany.pensionInsurance = updatedCompany.editedCompany.pensionInsurance
        
        updatedCompany.editMode = false;
    }
}
