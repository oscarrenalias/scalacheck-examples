package com.company.ai.scala

/**
 * This file implements the same functionality as the equivalent Java class, but
 * implemented in Scala.
 *
 * Please note that the class being tested with ScalaCheck is the Java version, not
 * this one.
 */
case class InsufficientFundsException() extends Exception

object Account {
  val GOLD_AGE : Int = 50
  val GOLD_BALANCE : Double = 10000
  val STD_INTEREST : Double = .02
  val GOLD_INTEREST : Double = .03
}

case class Account(id: Int, var age: Int, private var _balance: Double) {
    // id and age are positive integers

    import Account._

    def balance = _balance

    def deposit(amt: Double) = {
        assert(amt > 0)
        _balance += amt
    }

    def withdraw(amt: Double) = {
        assert(amt > 0)
        if (amt <= _balance) _balance -= amt
        else throw InsufficientFundsException()
    }

    def rate = if (balance < GOLD_BALANCE && age < GOLD_AGE) STD_INTEREST
               else GOLD_INTEREST

    def creditInterest() = deposit(rate * balance)
}