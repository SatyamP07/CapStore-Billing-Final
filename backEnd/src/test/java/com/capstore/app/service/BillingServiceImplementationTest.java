package com.capstore.app.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.Date;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.capstore.app.exceptions.CouponException;
import com.capstore.app.exceptions.InsufficientProductQuantityException;
import com.capstore.app.exceptions.NoProductSelectedException;
import com.capstore.app.models.Cart;
import com.capstore.app.models.Coupon;
import com.capstore.app.models.CustomerDetails;
import com.capstore.app.models.Order;
import com.capstore.app.models.Product;
import com.capstore.app.models.Transaction;
import com.capstore.app.models.UserAddress;
import com.capstore.app.repository.CouponsRepository;
import com.capstore.app.repository.CustomerRepository;
import com.capstore.app.repository.OrderRepository;
import com.capstore.app.repository.ProductRepository;
import com.capstore.app.repository.TransactionRepository;
import com.capstore.app.repository.UserAddressRepository;

@RunWith(SpringRunner.class)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@SpringBootTest
public class BillingServiceImplementationTest {

	@Autowired
	private BillingServiceImplementation billingService;
	
	@Autowired
	CouponsRepository couponsRepository;
	
	@Autowired
	OrderRepository orderRepository;
	
	@Autowired
	CustomerRepository customerRepository;
	
	@Autowired
	UserAddressRepository userAddressRepository;
	
	@Autowired
	ProductRepository productRepository;
	
	@Autowired
	TransactionRepository transactionRepository;
	
	public int getCustomerId() {
		CustomerDetails customer1 = new CustomerDetails("Customer1", "customer1", "customer1", "customer1@gmail.com", "Customer", true, "Favourite Football team?", "Bayern", "9873426712", null, null, null, null, null, null, null, null);
		customer1 = customerRepository.save(customer1);
		return customer1.getUserId();   
	}
	
	public int getCompleteCustomerId() {
		Product product1 = new Product("Product1", "Some Location", 500, 4, 2, "Brand1", 30, "Product Info", 5, "Category1", true, true, true);
	    Product product2 = new Product("Product2", "Some Location1", 550, 3, 2, "Brand2", 20, "Product Info2", 10, "Category2", true, true, true);
	    Product product3 = new Product("Product3", "Some Location2", 505, 4, 2, "Brand3", 13, "Product Info3", 0, "Category3", true, true, true);

	    product1 = productRepository.save(product1);
	    product2 = productRepository.save(product2);
	    product3 = productRepository.save(product3);
		
		Cart cart1 = new Cart("Cart", 4, product1.getProductId());
		Cart cart2 = new Cart("Cart", 5, product2.getProductId());
		Cart cart3 = new Cart("Cart", 3, product3.getProductId());
		
		Set<Cart> allcarts = new HashSet<>();
		allcarts.add(cart1);
		allcarts.add(cart2);
		allcarts.add(cart3);
		CustomerDetails customer1 = new CustomerDetails("Customer1", "customer1", "customer1", "customer1@gmail.com", "Customer", true, "Favourite Football team?", "Bayern", "9873426712", null, null, null, null, null, null, null, null);
		customer1.setcC(allcarts);
		customer1 = customerRepository.save(customer1);
		return customer1.getUserId();   
	}
	
	public int getCompleteCustomerIdLowQuantity() {
		Product product1 = new Product("Product1", "Some Location", 500, 4, 2, "Brand1", 1, "Product Info", 5, "Category1", true, true, true);
	    Product product2 = new Product("Product2", "Some Location1", 550, 3, 2, "Brand2", 20, "Product Info2", 10, "Category2", true, true, true);
	    Product product3 = new Product("Product3", "Some Location2", 505, 4, 2, "Brand3", 30, "Product Info3", 0, "Category3", true, true, true);

	    product1 = productRepository.save(product1);
	    product2 = productRepository.save(product2);
	    product3 = productRepository.save(product3);
		
		Cart cart1 = new Cart("Cart", 4, product1.getProductId());
		Cart cart2 = new Cart("Cart", 5, product2.getProductId());
		Cart cart3 = new Cart("Cart", 3, product3.getProductId());
		
		Set<Cart> allcarts = new HashSet<>();
		allcarts.add(cart1);
		allcarts.add(cart2);
		allcarts.add(cart3);
		CustomerDetails customer1 = new CustomerDetails("Customer1", "customer1", "customer1", "customer1@gmail.com", "Customer", true, "Favourite Football team?", "Bayern", "9873426712", null, null, null, null, null, null, null, null);
		customer1.setcC(allcarts);
		customer1 = customerRepository.save(customer1);
		return customer1.getUserId();   
	}
	
	public Order getOrder(int userId) {
		Order order = new Order(500, "pending", null, 1, userId, null);
		return order;
	}
	
	public Order getLowAmountOrder(int userId) {
		Order order = new Order(200, "pending", null, 2, userId, null);
		return order;
	}
	
	public Coupon getCoupon() {
		String start = "2020-04-22";
		String end = "2020-06-31";
		
		Date startDate = Date.valueOf(start);
		Date endDate = Date.valueOf(end);
		Coupon coupon = new Coupon("abcde", endDate, startDate, 100, 250, "Merchant");

		return coupon;
	}
	
	public Coupon getFailedCoupon() {
		String start = "2020-04-22";
		String end = "2020-05-15";
		
		Date startDate = Date.valueOf(start);
		Date endDate = Date.valueOf(end);
		Coupon coupon = new Coupon("failed", endDate, startDate, 100, 250, "Merchant");

		return coupon;
	}
	
	@Test
	public void testIsCouponValid() {
		Order order = getOrder(getCustomerId());
		order = orderRepository.save(order);
		
		Coupon coupon = getCoupon();
		coupon = couponsRepository.save(coupon);
		
		Order finalOrderObj = new Order();
		
		finalOrderObj = billingService.isCouponValid("abcde", order);			//isCouponValid takes two parameters coupon code as well as order object	
		
		assertThat(finalOrderObj.getOrderAmount()).isEqualTo(getOrder(getCustomerId()).getOrderAmount() - 100);
	}
	
	//Wrong coupon code entered so test fails and returns null
	@Test
	public void testIsCouponValidFail1() {
		Order order = getOrder(getCustomerId());
		orderRepository.save(order);
		
		Coupon coupon = getCoupon();
		couponsRepository.save(coupon);
		
		Exception exception = assertThrows(CouponException.class, () -> billingService.isCouponValid("qwerty", order));
		String expectedMessage = "Coupon code invalid!"; 
		String actualMessage = exception.getMessage();
		assertEquals(expectedMessage, actualMessage);
	}
	
	//Minimum order amount is less than what is needed for coupon so test fails and returns null as minimum amount needed is 250 
	@Test
	public void testIsCouponValidFail2() {
		Order order = getLowAmountOrder(getCustomerId());
		orderRepository.save(order);
		
		Coupon coupon = getCoupon();
		coupon = couponsRepository.save(coupon);
		
		Exception exception = assertThrows(CouponException.class, () -> billingService.isCouponValid("abcde", order));
		String expectedMessage = "Order amount less than minimum amount needed!"; 
		String actualMessage = exception.getMessage();
		assertEquals(expectedMessage, actualMessage);
	}
	
	//Date is after end date end coupon so test fails and returns null
	@Test
	public void testIsCouponValidFail3() {
		Order order = getOrder(getCustomerId());
		orderRepository.save(order);
		
		Coupon coupon = getFailedCoupon();
		couponsRepository.save(coupon);
		
		Exception exception = assertThrows(CouponException.class, () -> billingService.isCouponValid("failed", order));
		String expectedMessage = "Coupon has expired or not started!"; 
		String actualMessage = exception.getMessage();
		assertEquals(expectedMessage, actualMessage);
		
	}

	@Test
	public void testAddNewAddress() {
	CustomerDetails customerDetail = new CustomerDetails("Roxane", "roxy", "roxy3996", "roxy@gmail.com", "User", true, "What is your petname", "Roxy", "3456789012", "2345678901", "rox@gmail.com", "India", null, null, null, null, null);
	customerDetail=customerRepository.save(customerDetail);
	UserAddress address=new UserAddress("address_line1", "address_line2", "district", "state", "landmark",111111);
	int previousSize=0;
	billingService.saveNewAddress(customerDetail.getUserId(), address);
	CustomerDetails updatedCustomer=customerRepository.findById(customerDetail.getUserId()).get();
	int newSize=updatedCustomer.getAddresses().size();
	assertEquals(newSize-1,previousSize);
	}

	@Test
	public void testdeleteCustomeraddress() {
	CustomerDetails customerDetail = new CustomerDetails("Roxane", "roxy", "roxy3996", "roxy@gmail.com", "User", true, "What is your petname", "Roxy", "3456789012", "2345678901", "rox@gmail.com", "India", null, null, null, null, null);
	UserAddress address=new UserAddress("address_line1", "address_line2", "district", "state", "landmark",111111);
	customerRepository.save(customerDetail);

	Set<UserAddress> addressSet = new HashSet<UserAddress>();
	   addressSet.add(address);
	   customerDetail.setAddresses(addressSet);
	   customerDetail=customerRepository.save(customerDetail);
	   
	   int addressId=customerDetail.getAddresses().iterator().next().getAddressId();
	   billingService.deleteAddress(addressId);
	   UserAddress address1=userAddressRepository.findById(addressId).orElse(null);
	   assertEquals(null, address1);    
	}

	@Test
	public void testgetAllAddressOfCustomer() {
	CustomerDetails customerDetail = new CustomerDetails("Roxane", "roxy", "roxy3996", "roxy@gmail.com", "User", true, "What is your petname", "Roxy", "3456789012", "2345678901", "rox@gmail.com", "India", null, null, null, null, null);
	UserAddress address1=new UserAddress("address_line1", "address_line2", "district", "state", "landmark",111111);
	UserAddress address2=new UserAddress("ABC", "PQR", "Pune", "MH", "Line No 1",111111);
	UserAddress address3=new UserAddress("XYZ", "PQR", "Mumbai", "MH", "Line No 1",111111);
	customerRepository.save(customerDetail);

	Set<UserAddress> addressSet = new HashSet<UserAddress>();
	   addressSet.add(address1);
	   addressSet.add(address2);
	   addressSet.add(address3);
	   customerDetail.setAddresses(addressSet);
	   customerDetail=customerRepository.save(customerDetail);
	   
	   List<UserAddress> addresses=billingService.getAllAddresses(customerDetail.getUserId());
	   int size=addresses.size();
	   
	   assertEquals(3,size);

	}

	@Test
	public void testupdateOrderAddressId() {

	CustomerDetails customerDetail = new CustomerDetails("Roxane", "roxy", "roxy3996", "roxy@gmail.com", "User", true, "What is your petname", "Roxy", "3456789012", "2345678901", "rox@gmail.com", "India", null, null, null, null, null);
	customerDetail=customerRepository.save(customerDetail);
	Order order=new Order(1000.0,"pending",null,0,customerDetail.getUserId(), null);
	order=orderRepository.save(order);
	UserAddress address=new UserAddress("address_line1", "address_line2", "district", "state", "landmark",111111);
	address=userAddressRepository.save(address);

	billingService.updateOrderAddress(order.getOrderId(),address.getAddressId());
	Order updatedOrder=orderRepository.findById(order.getOrderId()).get();
	assertEquals(address.getAddressId(),updatedOrder.getAddressId());
	}
	
	@Test
	public void testCheckAvailabilitySuccess() {
		CustomerDetails customer = customerRepository.findById(getCompleteCustomerId()).get();
		boolean availability = billingService.checkAvailability(customer.getUserId());
		
		assertEquals(availability, true);
	}
	
	@Test
	public void testCheckAvailabilityFail1() {
		CustomerDetails customer = customerRepository.findById(getCustomerId()).get();
		boolean availability = billingService.checkAvailability(customer.getUserId());
		assertEquals(availability, false);
	}
	
	@Test
	public void testCheckAvailabilityFail2() {
		CustomerDetails customer = customerRepository.findById(getCompleteCustomerIdLowQuantity()).get();
		assertThatThrownBy(() -> {billingService.checkAvailability(customer.getUserId());}).isInstanceOf(InsufficientProductQuantityException.class);
	}

	@Test
	public void testDoTransaction() {
		Order order = getOrder(getCustomerId());
		order = orderRepository.save(order);
		assertNotNull(billingService.doTransaction(order.getOrderId(), 100, "COD"));
	}
	
	@Test
	public void testGetOrderById() {
		Order order = getOrder(getCustomerId());
		order = orderRepository.save(order);
		assertNotNull(billingService.getOrderById(order.getOrderId()));
	}
	
	@Test
	public void testCreateOrderSuccess() {
		CustomerDetails customer = customerRepository.findById(getCompleteCustomerId()).get();
		assertNotNull(billingService.createOrder(customer));
	}
	
	@Test
	public void testCreateOrderFail() {
		CustomerDetails customer = customerRepository.findById(getCustomerId()).get();
		assertThatThrownBy(() -> billingService.createOrder(customer)).isInstanceOf(NoProductSelectedException.class);
	}
	
	@Test
	public void testGetTotalRevenue() {
		List<Transaction> transactions = transactionRepository.findAll();
		double total = 0;
		for(Transaction transaction: transactions)
			total += transaction.getTransactionMoney();	   
	    assertEquals(total, billingService.getTotalRevenue());
	    
	}
	
}
