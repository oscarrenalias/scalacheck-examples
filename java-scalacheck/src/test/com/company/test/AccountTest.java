package com.company.test;

import org.junit.Test;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import com.company.account.*;

/**
 * This code is the equivalent of AccountSpecification but written for JUnit 4
 * and without the automatic generation of test data
 */
public class AccountTest {

    private static double DEFAULT_DELTA = 0;

    @Test
    public void testDeposit() {
        // simple deposit
        Account acct = new Account(1, 1, 10); // id, age, balance
        double oldBalance = acct.getBalance();
        double amt = 100;
        acct.deposit(amt);
        assertEquals(acct.getBalance(), oldBalance + amt, DEFAULT_DELTA);
    }

    @Test
    public void testDepositWithNegativeBalance() {
        // deposit when we start with a negative balance
        Account acct = new Account(1, 1, -100); // id, age, balance
        double amt = 100;
        acct.deposit(amt);
        assertEquals(acct.getBalance(), 0, DEFAULT_DELTA);
    }

    @Test
    public void withdrawNormal() throws InsufficientFundsException {
        Account acct = new Account(1, 1, 100);
        double oldBalance = acct.getBalance();
        double amt = 50;
        acct.withdraw(amt);
        assertEquals(acct.getBalance(), oldBalance - amt, DEFAULT_DELTA);
    }

    @Test
    public void withdrawOverdraft() {
        Account acct = new Account(1, 1, 100);
        double amt = 50;

        boolean exception = true;
        try {
            acct.withdraw(amt);
        } catch(Exception ex) {
            exception = ex instanceof InsufficientFundsException;
        }
        assertTrue("InsufficientFundsException should have been thrown", exception);
    }

    @Test
    public void Rate_lowBalance_lowAge() {
        Account acct = new Account(1, Account.GOLD_AGE - 1, Account.GOLD_BALANCE - 0.01);
        assertEquals(Account.STD_INTEREST, acct.getRate(), 0);
    }

    @Test
    public void rateHighBalance() {
        Account acct = new Account(1, 1, Account.GOLD_BALANCE);
        assertEquals(Account.GOLD_INTEREST, acct.getRate(), DEFAULT_DELTA);
    }

    @Test
    public void rateHighAge() {
        Account acct = new Account(1, Account.GOLD_AGE, 100);
        assertEquals(Account.GOLD_INTEREST, acct.getRate(), DEFAULT_DELTA);
    }

    @Test
    public void creditInterest() {
        Account acct = new Account(1,1,100);
        double oldBalance = acct.getBalance();
        acct.creditInterest();
        assertEquals(acct.getBalance(), oldBalance + ( oldBalance * acct.getRate()), DEFAULT_DELTA);
    }
}