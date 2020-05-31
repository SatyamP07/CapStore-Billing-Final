package com.capstore.app.service;

import java.io.FileOutputStream;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.capstore.app.exceptions.CouponException;
import com.capstore.app.exceptions.InsufficientProductQuantityException;
import com.capstore.app.exceptions.NoProductSelectedException;
import com.capstore.app.models.Cart;
import com.capstore.app.models.Coupon;
import com.capstore.app.models.CustomerDetails;
import com.capstore.app.models.MerchantDetails;
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
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

@Service
public class BillingServiceImplementation implements BillingServiceInterface {
	
	Logger logger = LoggerFactory.getLogger(BillingServiceImplementation.class);

	@Autowired
	OrderRepository orderRepository;
	
	@Autowired
	CustomerRepository customerRepository;
	
	@Autowired
	UserAddressRepository userAddressRepository; 
	
	@Autowired
	ProductRepository productRepository;
	
	@Autowired
	CouponsRepository couponsRepository;
	
	@Autowired
	TransactionRepository transactionRepository;
	
	
	@Override
	public List<UserAddress> getAllAddresses(int userId){
		CustomerDetails customer=customerRepository.findById(userId).get();
		List<UserAddress> addresses= new ArrayList<UserAddress>(customer.getAddresses());
		logger.info("Returned all addresses for userId " + userId);
		return addresses;
	}
	
	@Override
	public void saveNewAddress(int userId,UserAddress address) {
		CustomerDetails customer=customerRepository.findById(userId).get();
		Set<UserAddress> addresses = customer.getAddresses();
		addresses.add(address);
		customer.setAddresses(addresses);
		customerRepository.save(customer);
		logger.info("Saved new address successfully for userId " + userId);
	}
	
	@Override
	public void deleteAddress(int addressId) {
		userAddressRepository.deleteById(addressId);
		logger.info("Deleted address successfully with addressId " + addressId);
	}
	
	@Override
	public void generateInvoice(int orderId, int customerId, double couponDiscount) {
		Order order = orderRepository.findById(orderId).get();
		CustomerDetails customer = customerRepository.findById(customerId).get();
		UserAddress customerAddress = userAddressRepository.findById(order.getAddressId()).get();
		Map<Integer, Integer> products = order.getProducts();
		Transaction transaction = order.getTransaction();
		createPdf(customer, order, transaction, products, customerAddress, couponDiscount);
		logger.info("Invoice generated successfully for orderId " + orderId);
	}
	
	
	@Override
	public Order isCouponValid(String couponCode, Order order) { 
		
		Optional<Coupon> couponOptional = couponsRepository.findByCouponCode(couponCode);
		if(couponOptional.isPresent()) {
			Coupon coupon = couponOptional.get();
			

			Date startDate = coupon.getCouponStartDate();
			Date endDate = coupon.getCouponEndDate();
			
			long millis = System.currentTimeMillis();
			Date currentDate = new Date(millis);
			
			if(currentDate.after(startDate) && currentDate.before(endDate)) {
				if(order.getOrderAmount()>coupon.getCouponMinOrderAmount()) {
					double currentAmount = order.getOrderAmount();
					double afterDiscountAmount = currentAmount - coupon.getCouponAmount();
					
					order.setOrderAmount(afterDiscountAmount);
					return order;
				}
				else
					throw new CouponException("Order amount less than minimum amount needed!");
			}
			else
				throw new CouponException("Coupon has expired or not started!");
		}
		else {
			throw new CouponException("Coupon code invalid!");
		}
	}

	public void createPdf(CustomerDetails customer, Order order, Transaction transaction, 
				Map<Integer, Integer> products, UserAddress customerAddress, double couponAmount) {
		Document document = new Document();
		String filename = "src/main/resources/Invoice-Order-Id-" + order.getOrderId() + ".pdf";
		Rectangle rect = new Rectangle(PageSize.LETTER);
		document.setPageSize(rect);
		
		Font[] fonts = {
				new Font(),
				new Font(Font.FontFamily.COURIER, 20, Font.BOLD, new BaseColor(234, 198, 75)),
				new Font(Font.FontFamily.COURIER, 15, Font.BOLD),
				new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.BOLD),
				new Font(Font.FontFamily.TIMES_ROMAN, 11)
		};
		
		try {
			PdfWriter.getInstance(document, new FileOutputStream(filename));
			document.open();
			
			addHeader(document, fonts, order);
			document.add(new Paragraph(""));
			
			addCustomerDetails(document, fonts, customer, customerAddress);
			document.add(new Paragraph(""));
			
			addOrderDetails(document, fonts, order, transaction);
			document.add(new Paragraph("\n\n"));
			
			addProductDetails(document, fonts, products, order, couponAmount);
			
			document.close();
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void addHeader(Document document, Font[] fonts, Order order) throws Exception {
		PdfPTable header = new PdfPTable(2);
		PdfPCell headerCell = new PdfPCell();
		header.setWidthPercentage(100);
		headerCell.setBorder(Rectangle.NO_BORDER);
		headerCell.setVerticalAlignment(Element.ALIGN_BOTTOM);
		headerCell.setPadding(10);
		
		//header
		headerCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		headerCell.setPhrase(new Paragraph("CapStore", fonts[1]));
		header.addCell(headerCell);
		
		headerCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		headerCell.setPhrase(new Paragraph("Invoice-order-" + order.getOrderId()));
		header.addCell(headerCell);

		document.add(header);
	}

	public void addCustomerDetails(Document document, Font[] fonts, CustomerDetails customer, UserAddress customerAddress) throws Exception {
		PdfPTable detailsTable = new PdfPTable(2);
		PdfPCell detailsCell = new PdfPCell();
		detailsTable.setWidthPercentage(100);
		detailsCell.setPadding(10);
		detailsCell.setBorder(Rectangle.NO_BORDER);
		detailsCell.setPhrase(new Paragraph(""));
		detailsTable.addCell(detailsCell);
		
		detailsCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		Paragraph customerPara = new Paragraph();
		customerPara.setFont(fonts[2]);
		customerPara.add("Shipping Address:\n");
		customerPara.setFont(fonts[0]);
		customerPara.add(customer.getName() + "\n");
		customerPara.add(customerAddress.getAddress_line1()  + "\n");
		customerPara.add(customerAddress.getAddress_line2()  + "\n");
		customerPara.add(customerAddress.getLandmark()  + "\n");
		customerPara.add(customerAddress.getDistrict()  + ", " + customerAddress.getState() + "\n");
		customerPara.add(customerAddress.getPinCode()  + "\n");
		customerPara.add("Mobile Number: " + customer.getPhoneNumber()  + "\n");
		customerPara.add("E-Mail: " + customer.geteMail());
		detailsCell.setPhrase(customerPara);
		detailsTable.addCell(detailsCell);
		
		document.add(detailsTable);
	}

	public void addOrderDetails(Document document, Font[] fonts, Order order, Transaction transaction) throws Exception {
		PdfPTable orderTable = new PdfPTable(1);
		PdfPCell orderCell = new PdfPCell();
		orderTable.setWidthPercentage(100);
		orderCell.setBorder(Rectangle.NO_BORDER);
		orderCell.setPadding(10);
		Paragraph orderPara = new Paragraph();
		orderPara.setFont(fonts[2]);
		orderPara.add("Order-Id: ");
		orderPara.setFont(fonts[0]);
		orderPara.add("" + order.getOrderId());
		orderCell.setPhrase(orderPara);
		
		orderPara.setFont(fonts[2]);
		orderPara.add("\nOrder Date: ");
		orderPara.setFont(fonts[0]);
		orderPara.add(""  + transaction.getTransactionDate());
		orderCell.setPhrase(orderPara);
		
		orderTable.addCell(orderCell);
		
		document.add(orderTable);
	}

	public void addProductDetails(Document document, Font[] fonts, Map<Integer, Integer> products, Order order, double couponAmount) throws Exception {
		PdfPTable productTable = new PdfPTable(10);
		PdfPCell productCell = new PdfPCell();
		productTable.setWidthPercentage(100);
		productCell.setPadding(5);
		productCell.setPhrase(new Paragraph("Sr.no", fonts[3]));
		productTable.addCell(productCell);
		productCell.setColspan(5);
		productCell.setPhrase(new Paragraph("Discription", fonts[3]));
		productTable.addCell(productCell);
		productCell.setColspan(1);
		productCell.setPhrase(new Paragraph("Unit Price", fonts[3]));
		productTable.addCell(productCell);
		productCell.setPhrase(new Paragraph("Discount", fonts[3]));
		productTable.addCell(productCell);
		productCell.setPhrase(new Paragraph("Qty", fonts[3]));
		productTable.addCell(productCell);
		productCell.setPhrase(new Paragraph("Total Amount", fonts[3]));
		productTable.addCell(productCell);
		
		int count =1;
		double sum = 0;
		Set productIds = products.entrySet();
		Iterator it = productIds.iterator();
		
	    while(it.hasNext()){
	    	Map.Entry me = (Map.Entry)it.next();
	    	Product p = productRepository.findById((Integer)me.getKey()).get();

			productCell.setPhrase(new Paragraph("" + count, fonts[4]));
			productTable.addCell(productCell);
			productCell.setColspan(5);
			productCell.setPhrase(new Paragraph("" + p.getProductBrand() + " "+ p.getProductName() + " " + p.getProductCategory() + " " 
					+ p.getProductInfo(), fonts[4]));
			productTable.addCell(productCell);
			productCell.setColspan(1);
			productCell.setPhrase(new Paragraph("" + p.getProductPrice(), fonts[4]));
			productTable.addCell(productCell);
			productCell.setPhrase(new Paragraph("" + p.getDiscount() + "%", fonts[4]));
			productTable.addCell(productCell);
			productCell.setPhrase(new Paragraph("" + me.getValue().toString(), fonts[4]));
			productTable.addCell(productCell);
			System.out.println(p.getDiscountedPrice()*(Integer)me.getValue());
			productCell.setPhrase(new Paragraph("" + (p.getDiscountedPrice()*(Integer)me.getValue()), fonts[4]));
			productTable.addCell(productCell);
			count++;
			sum += p.getDiscountedPrice()*(Integer)me.getValue();
		}
		
		productCell.setColspan(9);
		productCell.setPhrase(new Paragraph("Total", fonts[3]));
		productTable.addCell(productCell);
		
		productCell.setColspan(1);
		productCell.setPhrase(new Paragraph("" + sum, fonts[4]));
		productTable.addCell(productCell);
		
		productCell.setColspan(9);
		productCell.setPhrase(new Paragraph("Coupon Discount", fonts[3]));
		productTable.addCell(productCell);
		
		productCell.setColspan(1);
		productCell.setPhrase(new Paragraph("-" + couponAmount, fonts[4]));
		productTable.addCell(productCell);
		
		productCell.setColspan(9);
		productCell.setPhrase(new Paragraph("Grand Total", fonts[3]));
		productTable.addCell(productCell);
		
		productCell.setColspan(1);
		productCell.setPhrase(new Paragraph("" + (sum - couponAmount), fonts[4]));
		productTable.addCell(productCell);
		
		document.add(productTable);
	}


	@Override
	public Order getOrderById(int orderId) {
		logger.info("Returned order with orderId " + orderId);
		return orderRepository.findById(orderId).get();
	}
	
	@Override
	public boolean checkAvailability(int userId)
	{
	
		CustomerDetails cust1 = customerRepository.getOne(userId);
		List<Cart> setcart = new ArrayList<>(cust1.getcC());
		if (setcart.size() == 0)
		{
			return false;
		}
		else {
			for (Cart cart : setcart) {
				int pId = cart.getProductId();
				int selectedquantity = cart.getQuantity();
				
				Product product = productRepository.getOne(pId);
				if(product.getNoOfProducts() < selectedquantity) {
					logger.error("Expection Occured: " + product.getProductName()+"has less than "+ selectedquantity + " items");
					throw new InsufficientProductQuantityException(product.getProductName()+"has less than "+ selectedquantity + " items");
				}
			}
		
			return true;
		}
	}
	
	@Override
	public List<Order> getDelivaryStatus(int userId)
	{
		CustomerDetails cust = customerRepository.getOne(userId);
		Set<Order> orderSet = cust.getOrders();
		
		List<Order> orderList = new ArrayList<Order>();
		for(Order o : orderSet)
			orderList.add(o);
		
		for (Order order : orderList) {
			String success ="Success";
			if(!order.getOrderStatus().equals("pending")) {
				if (success.equals(order.getTransaction().getTransactionStatus()))
				{
					Date ordertime = order.getTransaction().getTransactionDate();
					java.util.Date currenttime = Calendar.getInstance().getTime();
						
					long diff = ordertime.getTime()- currenttime.getTime();
					int diffDays = (int) (diff / (24 * 60 * 60 * 1000));
					if (-diffDays<1)
					{
						order.setOrderStatus("confirmed");
					}
					else if(-diffDays >1 && -diffDays <5)
					{
						order.setOrderStatus("shipped");
					}
					else if(-diffDays>=5)
					{
						order.setOrderStatus("delivered");
					}				
				}
			}	
			orderRepository.save(order);	
		}
		orderSet = cust.getOrders();
		
		orderList = new ArrayList<Order>();
		for(Order o : orderSet)
			orderList.add(o);
		logger.info("Returned delivery statuses");
		return orderList;
	}
	
	@Override
	public Order updateOrderAddress(int orderId, int addressId) {
		Order order = orderRepository.findById(orderId).get();
		order.setAddressId(addressId);
		logger.info("Updated address succesfully");
		return orderRepository.save(order);
	}

	@Override
	public Transaction doTransaction(int orderId, double couponDiscount, String transactionType) {
		Order order = orderRepository.findById(orderId).get();
		Transaction transaction = new Transaction(new Date(new java.util.Date().getTime()), order.getOrderAmount()+couponDiscount, transactionType, "Success");
		transaction = transactionRepository.save(transaction);
		order.setOrderStatus("confirmed");
		order.setTransaction(transaction);
		orderRepository.save(order);
		logger.info("Returned transaction");
		deliveryUpdate(orderId);
		return transaction;
	}

	@Override
	public Order createOrder(CustomerDetails customer) {
		if(checkAvailability(customer.getUserId())) {

			Set<Cart> allcarts = customer.getcC();
			Map<Integer, Integer> productMap = new HashMap<Integer, Integer>();
			
			double sum = 0;
			for(Cart cart : allcarts) {
				productMap.put(cart.getProductId(), cart.getQuantity());			
				sum += (productRepository.findById(cart.getProductId()).get().getDiscountedPrice())*(cart.getQuantity());
			
			}
			Order order = new Order(sum, "pending", productMap, 0, customer.getUserId(), null);
			order = orderRepository.save(order);
			logger.info("Created Order");
			return order;
		}
		else {
			logger.error("Exception Occured: NoProductSelected");
			throw new NoProductSelectedException("This user " + customer.getUserId() +  " has no products selected in the cart");
		}
	}

	@Override
	public double getTotalRevenue() {
		List<Transaction> allTransactions = transactionRepository.findAll();
		double revenue = allTransactions.stream().mapToDouble((transaction) -> transaction.getTransactionMoney()).sum();
		logger.info("Returned total revenue");
		return revenue;
	}
	
	@Override
	public void deliveryUpdate(int orderId) {
		Optional<Order> order = orderRepository.findById(orderId);
		Order orderTemp;
		Optional<Product> product;
		Product productTemp;
		if(order.isPresent()) {
			orderTemp = order.get();
				if(orderTemp.getOrderStatus().equalsIgnoreCase("confirmed")) {
					Map<Integer, Integer> orderProducts = orderTemp.getProducts();
						Set<Integer> orderProductId = orderProducts.keySet();
						for(Integer key : orderProductId) {
							int orderProductQty = orderProducts.get(key);
							product = productRepository.findById(key);
							productTemp = product.get();
							int diff = productTemp.getNoOfProducts() - orderProductQty;
								if(diff<=0) {
									logger.error("Exception Occured: InsufficeientProductQuantityException for ProductId " + key);
									throw new InsufficientProductQuantityException("Product Qty Cannot Meet Cart's Demands");
								}
								else {
									productTemp.setNoOfProducts(diff);	
								}
								logger.info("Inventory Updated for PorductId: " + key);
								productRepository.save(productTemp);
						}
				}
		}
			
	}

	@Override
	public List<Product> getAllProductsWithUserId(int userId) {
		List<Product> products = new ArrayList<>();
		Set<Cart> carts = customerRepository.findById(userId).get().getcC(); 
		List<Cart> cartsList = new ArrayList<>(carts);
		for(Cart cart: cartsList)
			products.add(productRepository.findById(cart.getProductId()).get());
		return products;
	}
	
}
