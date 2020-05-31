package com.capstore.app.models;

import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "transaction")
public class Transaction {

	@Id
	@GeneratedValue
	@Column(name = "transaction_id")
	private int transactionId;
	
    @Column(name = "transaction_date")
    private Date transactionDate;
   
    @Column(name = "transaction_money")
    private double transactionMoney;
   
    @Column(name = "transaction_method")
    private String transactionMethod;  //(“Credit”,”Debit”,”UPI”,”Wallet”)
   
    @Column(name = "transaction_status")
    private String transactionStatus;   //(“Success”,”Fail”,”Pending”)
    
    @Column(name = "order_id")
    private int orderId;

    
	public int getOrderId() {
		return orderId;
	}

	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}

	public int getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(int transactionId) {
		this.transactionId = transactionId;
	}

	public Date getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(Date transactionDate) {
		this.transactionDate = transactionDate;
	}


	public double getTransactionMoney() {
		return transactionMoney;
	}

	public void setTransactionMoney(double transactionMoney) {
		this.transactionMoney = transactionMoney;
	}

	public String getTransactionMethod() {
		return transactionMethod;
	}

	public void setTransactionMethod(String transactionMethod) {
		this.transactionMethod = transactionMethod;
	}

	public String getTransactionStatus() {
		return transactionStatus;
	}

	public void setTransactionStatus(String transactionStatus) {
		this.transactionStatus = transactionStatus;
	}

	
	public Transaction(Date transactionDate, double transactionMoney, String transactionMethod,
			String transactionStatus) {
		super();
		this.transactionDate = transactionDate;
		this.transactionMoney = transactionMoney;
		this.transactionMethod = transactionMethod;
		this.transactionStatus = transactionStatus;
	}

	public Transaction() {
		super();
		// TODO Auto-generated constructor stub
	}    
}
