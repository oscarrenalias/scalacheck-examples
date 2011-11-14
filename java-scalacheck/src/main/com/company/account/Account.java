package com.company.account;

/**
 * Implementation of a simple bank account class
 */
public class Account {
  
  public final static int GOLD_AGE = 50;
  public final static double GOLD_BALANCE = 10000;
  public final static double STD_INTEREST = .02;
  public final static double GOLD_INTEREST = .03;
  
  private int id;
  private int age;
  private double balance;
  
  public Account(int id, int age, double balance) {
    this.id = id;
    this.age = age;
    this.balance = balance;
  }
  
  public double getBalance() {
    return(balance);
  }
  
  public int getAge() {
    return(age);
  }
  
  public void deposit(double amt) {
    assert (amt > 0);
    balance += amt;
  }
  
  public void withdraw(double amt) throws InsufficientFundsException {
    assert(amt > 0);
    if (amt <= this.balance) 
      balance -= amt;
    else 
     throw new InsufficientFundsException();
  }

  public double getRate() {
    if (balance < Account.GOLD_BALANCE && age < Account.GOLD_AGE) 
      return(Account.STD_INTEREST);
    else 
      return(Account.GOLD_INTEREST);
  }

    public void creditInterest() {
    deposit(getRate() * balance);
  }
}