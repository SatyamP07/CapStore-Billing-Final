package com.capstore.app.controller;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.capstore.app.models.Cart;
import com.capstore.app.models.CustomerDetails;
import com.capstore.app.models.Order;
import com.capstore.app.models.Product;
import com.capstore.app.models.Transaction;
import com.capstore.app.models.UserAddress;
import com.capstore.app.repository.CustomerRepository;
import com.capstore.app.repository.OrderRepository;
import com.capstore.app.service.BillingServiceInterface;

@RestController
@RequestMapping("/CapStore/Billing")
@CrossOrigin(origins = "http://localhost:4200" ,allowedHeaders = "*")
public class BillingController {

	Logger logger = LoggerFactory.getLogger(BillingController.class);
	
	@Autowired
	BillingServiceInterface billingService;
	
	@Autowired
	CustomerRepository customerRepository;
	
	@GetMapping("/download/{orderId}/{customerId}/{couponId}")
	public ResponseEntity downloadInvoice (@PathVariable("orderId") int orderId, @PathVariable("customerId") int customerId,@PathVariable("couponId") int couponId,
					HttpServletResponse res) throws Exception {
		logger.info("Requested DownloadInvoice for OrderId " + orderId);
		billingService.generateInvoice(orderId, customerId, couponId);
		Thread.sleep(2000);
		String fileName = "Invoice-Order-Id-" + orderId + ".pdf";
		res.setHeader("Content-Desposition", "attachment; fileName " + fileName);
		res.getOutputStream().write(contentOf(fileName));
		return ResponseEntity.ok().build();
	}
	
	@PutMapping("/applyCoupon/{couponCode}")
	public ResponseEntity<Order> applyCoupon(@RequestBody Order order, @PathVariable String couponCode) {
		logger.info("Requested ApplyCoupon for CouponCode " + couponCode);
		return ResponseEntity.ok().body(billingService.isCouponValid(couponCode, order));
	}
  
	
	@GetMapping("/addresses/{userId}")
	public ResponseEntity<List<UserAddress>> getAllAddresses(@PathVariable int userId){
		logger.info("Requested getAllAddresses for userId " + userId);
		return ResponseEntity.ok().body(billingService.getAllAddresses(userId));
	}
	
	@PostMapping("/address/create/{userId}")
	public ResponseEntity<List<UserAddress>> saveNewAddress(@PathVariable int userId, @RequestBody UserAddress address){
		logger.info("Requested saveNewAddress for userId " + userId);
		billingService.saveNewAddress(userId, address);
		return  ResponseEntity.ok().build();
	}
	
	@DeleteMapping("/address/delete/{userId}/{addressId}")
	public ResponseEntity<List<UserAddress>> deleteAddress(@PathVariable int userId, @PathVariable int addressId){
		logger.info("Requested deleteAddress for userId " + userId);
		billingService.deleteAddress(addressId);
		return  ResponseEntity.ok().body(billingService.getAllAddresses(userId));
	}

	private byte[] contentOf(String fileName) throws Exception {
		return Files.readAllBytes(Paths.get(getClass().getClassLoader().getResource(fileName).toURI()));
	}
	
	@GetMapping("/order/{orderId}")
	public ResponseEntity<Order> getOrder(@PathVariable("orderId") int orderId) {
		logger.info("Requested getOrder for orderId " + orderId);
		return ResponseEntity.ok().body(billingService.getOrderById(orderId));
	} 
	
	@GetMapping("/getDelivaryStatus/{userId}" )
	public ResponseEntity<List<Order>> getDelivaryStatus(@PathVariable int userId)
	{
		logger.info("Requested getDeliveryStatus for userId " + userId);
		return ResponseEntity.ok().body(billingService.getDelivaryStatus(userId));
	}
	
	@PatchMapping("/address/update")
	public ResponseEntity updateOrderAddress(@RequestBody Order order) {
		logger.info("Requested updateOrderAddress for orderId " + order.getOrderId());
		billingService.updateOrderAddress(order.getOrderId(), order.getAddressId());
	return ResponseEntity.ok().build();
	}
	
	@GetMapping("/transaction/{orderId}/{discount}/{transactionType}")
	public ResponseEntity<Transaction> doTransaction(@PathVariable("orderId")int orderId,@PathVariable double discount, @PathVariable String transactionType) {
		logger.info("Requested doTransaction for orderId " + orderId);
		return ResponseEntity.ok().body(billingService.doTransaction(orderId, discount, transactionType));
	}
	
	@PostMapping("/order/create")
	public ResponseEntity<Order> createOrder(@RequestBody CustomerDetails customer) {
		logger.info("Requested createOrder for userId " + customer.getUserId());
		return ResponseEntity.ok().body(billingService.createOrder(customer));
	}
	
	@GetMapping("/customer/{userId}")
	public ResponseEntity<CustomerDetails> getCustomerById(@PathVariable("userId") int userId) {
		logger.info("Requested getCustomerById for userId " + userId);
		return ResponseEntity.ok().body(customerRepository.findById(userId).get());
	}
	
	@GetMapping("/admin/revenue")
	public ResponseEntity<Double> getTotalRevenue() {
		logger.info("Requested getTotalRevenue");
		return ResponseEntity.ok().body(billingService.getTotalRevenue());
	}
	
	@GetMapping("/{userId}/products")
	public ResponseEntity<List<Product>> getProductById(@PathVariable("userId")int userId){
		logger.info("Requested products of userId " + userId);
		return ResponseEntity.ok().body(billingService.getAllProductsWithUserId(userId));
	}
}
