package com.capstore.app;

import java.sql.Date;
import java.text.SimpleDateFormat;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.capstore.app.models.Cart;
import com.capstore.app.models.Coupon;
import com.capstore.app.models.CustomerDetails;
import com.capstore.app.models.MerchantDetails;
import com.capstore.app.models.Order;
import com.capstore.app.models.Product;

import com.capstore.app.repository.CouponsRepository;
import com.capstore.app.models.ProductFeedback;
import com.capstore.app.models.Transaction;
import com.capstore.app.models.User;
import com.capstore.app.models.UserAddress;
import com.capstore.app.repository.CustomerRepository;
import com.capstore.app.repository.MerchantRepository;
import com.capstore.app.repository.OrderRepository;
import com.capstore.app.repository.ProductRepository;
import com.capstore.app.service.BillingServiceInterface;
import com.capstore.app.repository.ProductFeedbackRepository;
import com.capstore.app.repository.UserRepository;


@SpringBootApplication
public class Application implements CommandLineRunner {

	@Autowired
	private CustomerRepository customerRepository;
	
	@Autowired
	private MerchantRepository merchantRepository;
	
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private CouponsRepository couponsRepository;
	
	@Autowired
	private OrderRepository orderRepository; 
	
	@Autowired
	private ProductRepository productRepository;
	
	@Autowired
	private BillingServiceInterface billingService;
	
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
	
	@Autowired
	private ProductFeedbackRepository productFeedbackRepository;


	@Override
	public void run(String... args) throws Exception {
		// TODO Auto-generated method stub
		
		//merchant created

    MerchantDetails merchant1 = new MerchantDetails("Merchant 1", "merchant1", "m1234", "merchant1@gmail.com", "Merchant", true, "What is your age", "21", "7895421237", null, null, null, null, null, null, true, 4);
    MerchantDetails merchant2 = new MerchantDetails("Merchant 2", "merchant2", "m2234", "merchant2@gmail.com", "Merchant", true, "What is your age", "21", "7895221222", null, null, null, null, null, null, true, 3);

    merchantRepository.save(merchant1);
    merchantRepository.save(merchant2);

    //user created
    CustomerDetails customer1 = new CustomerDetails("Customer1", "customer1", "customer1", "customer1@gmail.com", "Customer", true, "Favourite Football team?", "Bayern", "9873426712", null, null, null, null, null, null, null, null);
    customer1 = customerRepository.save(customer1);    

    //add product to merchants
    Product product1 = new Product("Product1", "Some Location", 500, 4, 2, "Brand1", 100, "Product Info", 5, "Category1", true, true, true);
    Product product2 = new Product("Product2", "Some Location1", 550, 3, 2, "Brand2", 120, "Product Info2", 10, "Category2", true, true, true);
    Product product3 = new Product("Product3", "Some Location2", 505, 4, 2, "Brand3", 130, "Product Info3", 0, "Category3", true, true, true);

    product1 = productRepository.save(product1);
    product2 = productRepository.save(product2);
    product3 = productRepository.save(product3);


    Set<Product> merchant1Product = new HashSet<Product>();
    merchant1Product.add(product1);
    merchant1Product.add(product2);

    Set<Product> merchant2Product = new HashSet<Product>();
    merchant2Product.add(product3);

    merchant1.setProducts(merchant1Product);
    merchant2.setProducts(merchant2Product);

    merchantRepository.save(merchant1);
    merchantRepository.save(merchant2);

    //add user address
    UserAddress address= new UserAddress("ABC", "DEF", "Indore", "MP", "Peepal ka ped", 12345);    
    Set<UserAddress> addressSet = new HashSet<UserAddress>();
    addressSet.add(address);
    customer1.setAddresses(addressSet);

    int customerAddressId = customer1.getAddresses().iterator().next().getAddressId();
    Cart cart1 = new Cart("Cart", 1, product1.getProductId());
	Cart cart2 = new Cart("Cart", 2, product2.getProductId());
	Cart cart3 = new Cart("Cart", 1, product3.getProductId());
	
	Set<Cart> allcarts = new HashSet<>();
	allcarts.add(cart1);
	allcarts.add(cart2);
	allcarts.add(cart3);
	
	customer1.setcC(allcarts);
    customer1 = customerRepository.save(customer1);


    // add merchant address
    UserAddress address1 = new UserAddress("CAB", "EWS", "Pune", "Maharashtra", "Corona Gali", 32145);
    Set<UserAddress> addressSet1 = new HashSet<UserAddress>();
    addressSet1.add(address1);
    merchant1.setAddresses(addressSet1);
    merchantRepository.save(merchant1);

    Map<Integer, Integer> products = new HashMap<>();
    products.put(product1.getProductId(), 1);
    products.put(product2.getProductId(), 2);
    products.put(product3.getProductId(), 2);

    long DAY_IN_MS = 1000 * 60 * 60 * 24;
    Transaction transaction = new Transaction(new Date(new java.util.Date().getTime()) , 1500, "UPI", "Success");
    Transaction transaction1 = new Transaction(new Date(new java.util.Date().getTime() - 2*DAY_IN_MS) , 2000, "UPI", "Success");
    Transaction transaction2 = new Transaction(new Date(new java.util.Date().getTime() - 7*DAY_IN_MS) , 1000, "UPI", "Success");
    
    Order order = new Order(1000, "confirmed", products, customerAddressId, customer1.getUserId(), transaction);
    Order order1 = new Order(500,"confirmed", products, customerAddressId, customer1.getUserId(), transaction1);
    Order order2 = new Order(1500,"confirmed", products, customerAddressId, customer1.getUserId(), transaction2);
    order = orderRepository.save(order);
    order1 = orderRepository.save(order1);
    order2 = orderRepository.save(order2);
        
        //--------------------------------------------------------------------------------------
		//-----APPLYING COUPONS------//
		
		String start = "2020-04-22";
		String end = "2020-06-31";
		
		Date startDate = Date.valueOf(start);
		Date endDate = Date.valueOf(end);
		Coupon coupon = new Coupon("abc", endDate, startDate, 100, 250, "Merchant");
		couponsRepository.save(coupon);
		coupon.setCouponCode("abcd");
		coupon.setCouponEndDate(Date.valueOf("2020-05-30"));
		couponsRepository.save(coupon);
}
	
}
