package com.rafalcendrowski.AccountApplication.controllers;

import com.rafalcendrowski.AccountApplication.models.PaymentModelAssembler;
import com.rafalcendrowski.AccountApplication.payment.Payment;
import com.rafalcendrowski.AccountApplication.payment.PaymentDto;
import com.rafalcendrowski.AccountApplication.payment.PaymentService;
import com.rafalcendrowski.AccountApplication.user.User;
import com.rafalcendrowski.AccountApplication.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/payments")
public class PaymentsController {

    @Autowired
    UserService userService;

    @Autowired
    PaymentService paymentService;

    @Autowired
    PaymentModelAssembler paymentModelAssembler;

    @GetMapping
    public CollectionModel<EntityModel<PaymentDto>> getPayments() {
        List<EntityModel<PaymentDto>> payments = paymentService.loadAllPayments().stream()
                .map(paymentModelAssembler::toModel).toList();
        return CollectionModel.of(payments,
                linkTo(methodOn(PaymentsController.class).getPayments()).withSelfRel(),
                linkTo(methodOn(PaymentsController.class).getPaymentByUserAndPeriod(null)).withRel("search"),
                linkTo(methodOn(PaymentsController.class).getPaymentsByUsername(null)).withRel("search"),
                linkTo(methodOn(PaymentsController.class).getPaymentsByUserId(null)).withRel("search"));
    }

    @GetMapping("/{id}")
    public EntityModel<PaymentDto> getPayment(@PathVariable Long id) {
        Payment payment = paymentService.loadById(id);
        return paymentModelAssembler.toModel(payment);
    }

    @GetMapping("/user/{userId}")
    public CollectionModel<EntityModel<PaymentDto>> getPaymentsByUserId(@PathVariable Long userId) {
        User employee = userService.loadById(userId);
        List<EntityModel<PaymentDto>> payments = paymentService.loadByEmployee(employee).stream()
                .map(paymentModelAssembler::toModel).toList();
        return  CollectionModel.of(payments,
                linkTo(methodOn(PaymentsController.class).getPaymentsByUserId(userId)).withSelfRel(),
                linkTo(methodOn(UserController.class).getUser(userId)).withRel("user"),
                linkTo(methodOn(PaymentsController.class).getPayments()).withRel("payments"));
    }

    @GetMapping("/find")
    public EntityModel<PaymentDto> getPaymentByUserAndPeriod(@Valid @RequestBody PaymentDto paymentDto) {
        User employee = userService.loadByUsername(paymentDto.getEmployee());
        Payment payment = paymentService.loadByEmployeeAndPeriod(employee, paymentDto.getPeriod());
        return paymentModelAssembler.toModel(payment);
    }

    @GetMapping("/find/{username}")
    public CollectionModel<EntityModel<PaymentDto>> getPaymentsByUsername(@PathVariable String username) {
        User employee = userService.loadByUsername(username);
        List<EntityModel<PaymentDto>> payments = paymentService.loadByEmployee(employee).stream()
                .map(paymentModelAssembler::toModel).toList();
        return CollectionModel.of(payments,
                linkTo(methodOn(PaymentsController.class).getPaymentsByUsername(username)).withSelfRel(),
                linkTo(methodOn(PaymentsController.class).getPayments()).withRel("payments"));
    }

    @Transactional
    @PostMapping
    public CollectionModel<EntityModel<PaymentDto>> addPayrolls(@Valid @RequestBody PaymentList<PaymentDto> payments) {
        List<Payment> paymentList = new ArrayList<>();
        for(PaymentDto paymentDto : payments) {
            User employee = userService.loadByUsername(paymentDto.getEmployee());
            if (paymentService.hasPayment(employee, paymentDto.getPeriod())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Payment for %s in %s already exists".formatted(paymentDto.getEmployee(), paymentDto.getPeriod()));
            } else {
                Payment payment = new Payment(employee, paymentDto.getPeriod(), paymentDto.getSalary());
                paymentList.add(payment);
                employee.addPayment(payment);
                paymentService.savePayment(payment);
                userService.updateUser(employee);
            }
        }
        List<EntityModel<PaymentDto>> entityList = paymentList.stream()
                .map(paymentModelAssembler::toModel).toList();
        return CollectionModel.of(entityList,
                linkTo(methodOn(PaymentsController.class).getPayments()).withRel("payments"));
    }

    @PutMapping
    public EntityModel<PaymentDto> updatePayroll(@Valid @RequestBody PaymentDto paymentDto) {
        User employee = userService.loadByUsername(paymentDto.getEmployee());
        Payment payment = paymentService.loadByEmployeeAndPeriod(employee, paymentDto.getPeriod());
        payment.setSalary(paymentDto.getSalary());
        paymentService.savePayment(payment);
        return paymentModelAssembler.toModel(payment);
    }

    @PutMapping("/{id}")
    public EntityModel<PaymentDto> updatePayroll(@PathVariable Long id, @RequestBody Long salary) {
        if (salary < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Salary must be a non-negative number");
        }
        Payment payment = paymentService.loadById(id);
        payment.setSalary(salary);
        paymentService.savePayment(payment);
        return paymentModelAssembler.toModel(payment);
    }

    @DeleteMapping
    public ResponseEntity<?> deletePayroll(@Valid @RequestBody PaymentDto paymentBody) {
        User employee = userService.loadByUsername(paymentBody.getEmployee());
        Payment payment = paymentService.loadByEmployeeAndPeriod(employee, paymentBody.getPeriod());
        employee.removePayment(payment);
        paymentService.deletePayment(payment);
        userService.updateUser(employee);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePayroll(@PathVariable Long id) {
        Payment payment = paymentService.loadById(id);
        User employee = payment.getEmployee();
        employee.removePayment(payment);
        paymentService.deletePayment(payment);
        userService.updateUser(employee);
        return ResponseEntity.noContent().build();
    }
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
