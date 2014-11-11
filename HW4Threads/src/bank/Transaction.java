package bank;

import java.math.BigDecimal;

public class Transaction {

	private int fromAccount;
	private int toAccount;
	private BigDecimal amount;
	
	public Transaction(int fromAccount, int toAccount, BigDecimal amount) {
		this.fromAccount = fromAccount;
		this.toAccount = toAccount;
		this.amount = amount;
	}

	public int getFromAccount() {
		return fromAccount;
	}

	public int getToAccount() {
		return toAccount;
	}

	public BigDecimal getAmount() {
		return amount;
	}
	
	

}
