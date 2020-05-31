package com.capstore.app.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.capstore.app.exceptions.InsufficientProductQuantityException;
import com.capstore.app.models.CustomerDetails;
import com.capstore.app.models.Order;
import com.capstore.app.models.Product;
import com.capstore.app.models.Transaction;
import com.capstore.app.models.UserAddress;

@Service
public interface BillingServiceInterface {

	void generateInvoice(int orderId, int customerId, double couponDiscount);
	Order isCouponValid(String couponCode, Order order);
	List<UserAddress> getAllAddresses(int userId);
	void saveNewAddress(int userId,UserAddress address);
	void deleteAddress(int addressId);
	Order getOrderById(int orderId);
	
	public boolean checkAvailability(int userId);
	public List<Order> getDelivaryStatus(int userId);
	
	Order updateOrderAddress(int orderId, int addressId);
	Transaction doTransaction(int orderId, double couponDiscount, String transactionType);
	
	Order createOrder(CustomerDetails customer);
	void deliveryUpdate(int orderId);
	
	double getTotalRevenue();
	
	List<Product> getAllProductsWithUserId(int userId);
}
