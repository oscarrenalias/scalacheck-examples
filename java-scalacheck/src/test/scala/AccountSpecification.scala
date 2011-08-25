package com.company.test

import com.company.account.Account
import com.company.account.InsufficientFundsException
import org.scalacheck.Prop._
import org.scalacheck.{Prop, Arbitrary, Gen, Properties}

/**
 * Custom data generator for the Account class
 */
object GenAccount {

	import com.company.account.Account._

	val MAX_ID: Int = 999999
	val MAX_AGE: Int = 200
	val MAX_BALANCE: Double = 10 * GOLD_BALANCE

	/**
	 * This method takes care of generating new objects of the Account class, using
	 * random values defined within a certain range
	 */
	def genAccount(maxId: Int, maxAge: Int, maxBalance: Double): Gen[Account] = for {
		id <- Gen.choose(0, maxId)
		age <- Gen.choose(0, maxAge)
		balance <- Gen.choose(0, maxBalance)
	} yield new Account(id, age, balance)

	/**
	 * The Arbitrary generator creates test data of any type.
	 * This must be defined as an implicit value or function and imported into the scope of the 
	 * ScalaCheck tests so that ScalaCheck can generate test data of the required type
	 */
	implicit val arbAccount: Arbitrary[Account] =
		Arbitrary(genAccount(MAX_ID, MAX_AGE, MAX_BALANCE))
}


object AccountSpecification extends Properties("Account") {

	import com.company.account.Account._
	import GenAccount._

	val genAcctAmt: Gen[(Account, Double)] = for {
		acct <- Arbitrary.arbitrary[Account]
		amt <- Gen.choose(0.01, MAX_BALANCE)
	} yield (acct, amt)

	property("Deposit") = forAll(genAcctAmt) {
		case (acct: Account, amt: Double) =>
			val oldBalance = acct.getBalance()
			acct.deposit(amt)
			acct.getBalance() == oldBalance + amt
	}

	property("Withdraw-normal") = forAll(genAcctAmt) {
		case (acct: Account, amt: Double) =>
			amt <= acct.getBalance() ==> {
				val oldBalance = acct.getBalance()
				acct.withdraw(amt)
				acct.getBalance() == oldBalance - amt
			}
	}

	// This test can be done more elegantly in combination with ScalaTest
	//
	property("Withdraw-overdraft") = forAll(genAcctAmt) {
		case (acct: Account, amt: Double) =>
			amt > acct.getBalance() ==> {
				val oldBalance = acct.getBalance()
				if (amt <= oldBalance) {
					acct.withdraw(amt)
					acct.getBalance == oldBalance - amt
				}
				else {
					Prop.throws(acct.withdraw(amt), classOf[InsufficientFundsException]) && acct.getBalance() == oldBalance
				}
			}
	}

	property("Rate-lowBalance, lowAge") = {
		val gen = genAccount(MAX_ID, GOLD_AGE - 1, GOLD_BALANCE - .01)

		forAll(gen) {
			acct: Account => acct.getRate() == STD_INTEREST
		}
	}

	property("Rate-highBalance") = forAll {
		acct: Account =>
			acct.getBalance() >= GOLD_BALANCE ==>
							(acct.getRate() == GOLD_INTEREST)
	}

	property("Rate-highAge") = forAll {
		acct: Account =>
			acct.getAge() >= GOLD_AGE ==>
							(acct.getRate() == GOLD_INTEREST)
	}

	property("CreditInterest") = forAll {
		acct: Account =>
			val oldBalance = acct.getBalance()
			acct.creditInterest()
			acct.getBalance() == oldBalance + (oldBalance * acct.getRate())
	}
}

/**
 * Used for running the tests from the command line, as there are no JUnit
 * compatible runners for ScalaCheck (could be done if ScalaCheck is used
 * from ScalaTest
 */
object Runner {
	val rnd = new java.util.Random(100)
	//val parms = org.scalacheck.Test.Params(75,500,0,20,rnd,1,20)
	val parms = org.scalacheck.Test.Params(75, 500, 0, 20, rnd, 1)

	def apply() = {
		AccountSpecification.check(parms)
	}

	def main(args: Array[String]) = apply()
}