package com.rafalcendrowski.AccountApplication.payment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.server.core.Relation;

import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Relation(collectionRelation = "payments")
public class PaymentDto {
    Long id;
    @NotEmpty
    @Email
    @Pattern(regexp = ".*@acme\\.com")
    private String employee;
    @NotEmpty
    @Pattern(regexp = "(1[0-2]|0[1-9])-\\d\\d\\d\\d")
    private String period;
    @Min(0L)
    private Long salary;

    public PaymentDto(Payment payment) {
        this.id = payment.getId();
        this.employee = payment.getEmployee().getUsername();
        this.period = payment.getPeriod();
        this.salary = payment.getSalary();
    }

    public static PaymentDto of(Payment payment) {
        return new PaymentDto(payment);
    }
}
