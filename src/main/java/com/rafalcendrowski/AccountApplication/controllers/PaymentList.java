package com.rafalcendrowski.AccountApplication.controllers;

import jakarta.validation.Valid;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Setter
@Getter
class PaymentList<E> implements List<E> {
    @Valid
    List<E> paymentList;

    public PaymentList() {
        this.paymentList = new ArrayList<E>();
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
