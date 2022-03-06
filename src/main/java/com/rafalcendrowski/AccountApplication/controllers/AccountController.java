package com.rafalcendrowski.AccountApplication.controllers;

import com.rafalcendrowski.AccountApplication.payment.Payment;
import com.rafalcendrowski.AccountApplication.payment.PaymentService;
import com.rafalcendrowski.AccountApplication.user.User;
import com.rafalcendrowski.AccountApplication.user.UserService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.util.*;

@RestController
@RequestMapping("/api/acct")
public class AccountController {

    @Autowired
    UserService userService;

    @Autowired
    PaymentService paymentService;

    @Transactional
    @PostMapping("/payments")
    public Map<String, String> addPayrolls(@Valid @RequestBody PaymentList<PaymentBody> payments) {
        for(PaymentBody paymentBody : payments) {
            User employee = userService.loadByUsername(paymentBody.getEmployee());
            if (paymentService.hasPayment(employee, paymentBody.getPeriod())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Payment for %s in %s already exists".formatted(paymentBody.getEmployee(), paymentBody.getPeriod()));
            } else {
                Payment payment = new Payment(employee, paymentBody.getPeriod(), paymentBody.getSalary());
                employee.addPayment(payment);
                paymentService.savePayment(payment);
                userService.updateUser(employee);
            }
        }
        return Map.of("status", "Added successfully");
    }

    @Transactional
    @PutMapping("/payments")
    public Map<String, String> updatePayroll(@Valid @RequestBody PaymentBody paymentBody) {
        User employee = userService.loadByUsername(paymentBody.getEmployee());
        Payment payment = paymentService.loadByEmployeeAndPeriod(employee, paymentBody.getPeriod());
        payment.setSalary(paymentBody.getSalary());
        paymentService.savePayment(payment);
        return Map.of("status", "Updated successfully");
    }

    @Transactional
    @DeleteMapping("/payments")
    public Map<String, String> deletePayroll(@Valid @RequestBody PaymentBody paymentBody) {
        User employee = userService.loadByUsername(paymentBody.getEmployee());
        Payment payment = paymentService.loadByEmployeeAndPeriod(employee, paymentBody.getPeriod());
        employee.removePayment(payment);
        paymentService.deletePayment(payment);
        userService.updateUser(employee);
        return Map.of("status", "Deleted successfully");
    }
}

@Data
class PaymentBody {
    @NotEmpty
    @Email
    @Pattern(regexp = ".*@acme\\.com")
    private String employee;
    @NotEmpty
    @Pattern(regexp = "(1[0-2]|0[1-9])-\\d\\d\\d\\d")
    private String period;
    @Min(0L)
    private Long salary;
}

class PaymentList<E> implements List<E>{
    @Valid
    List<E> paymentList;

    public PaymentList() {
        this.paymentList = new ArrayList<E>();
    }

    public List<E> getPaymentList() {
        return paymentList;
    }

    public void setPaymentList(List<E> paymentList) {
        this.paymentList = paymentList;
    }

    @Override
    public int size() {
        return paymentList.size();
    }

    @Override
    public boolean isEmpty() {
        return paymentList.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return paymentList.contains(o);
    }

    @Override
    public Iterator<E> iterator() {
        return paymentList.iterator();
    }

    @Override
    public Object[] toArray() {
        return paymentList.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return paymentList.toArray(a);
    }

    @Override
    public boolean add(E e) {
        return paymentList.add(e);
    }

    @Override
    public boolean remove(Object o) {
        return paymentList.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return paymentList.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        return paymentList.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        return paymentList.addAll(index, c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return paymentList.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return paymentList.retainAll(c);
    }

    @Override
    public void clear() {
        paymentList.clear();
    }

    @Override
    public E get(int index) {
        return paymentList.get(index);
    }

    @Override
    public E set(int index, E element) {
        return paymentList.set(index, element);
    }

    @Override
    public void add(int index, E element) {
        paymentList.add(index, element);
    }

    @Override
    public E remove(int index) {
        return paymentList.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return paymentList.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return paymentList.lastIndexOf(o);
    }

    @Override
    public ListIterator<E> listIterator() {
        return paymentList.listIterator();
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        return paymentList.listIterator(index);
    }

    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        return paymentList.subList(fromIndex, toIndex);
    }
}
