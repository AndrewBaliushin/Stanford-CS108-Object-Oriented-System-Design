package bank;

import java.math.BigDecimal;

public class Account {
	
	private int id;
	private BigDecimal currentBalance;
	private int numOfTransactions = 0;
	
	public Account(int id, BigDecimal currentBalance) {
		this.id = id;
		this.currentBalance = currentBalance;
	}
	
	@Override
	public String toString() {
		return "Account ID:" + id + "; Balance: " + currentBalance +
				"; num Of transactions: " + numOfTransactions;
	}
	
	public synchronized void withdrawMoney(BigDecimal amount) {
		currentBalance.subtract(amount);
		numOfTransactions++;
	}
	
	public synchronized void depositMoney(BigDecimal amount) {
		currentBalance.add(amount);
		numOfTransactions++;
	}
	
	@Override
	public int hashCode() {
		return id;
	}
	
}
