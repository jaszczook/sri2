package edu.pjwstk.sri.lab2.shared;

import edu.pjwstk.sri.lab2.dao.ProductDao;
import edu.pjwstk.sri.lab2.dto.OrderItem;
import edu.pjwstk.sri.lab2.model.Product;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.EJBContext;
import javax.ejb.Stateful;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Stateful
public class ShoppingCart {

	@Resource
	private EJBContext ejbContext;

	@EJB
	private ProductDao productDao;

	private HashMap<Long, OrderItem> orderItemHashMap;

	@PostConstruct
	private void init() {
		orderItemHashMap = new HashMap<Long, OrderItem>();
	}

	public List<OrderItem> getShoppingCart() {
		return new ArrayList<OrderItem>(orderItemHashMap.values());
	}

	public void addItemToShoppingCart(Long itemId, Integer amount) {

		OrderItem orderItem = createItemOrder(itemId, amount);

		if (checkAvailability(orderItem.getProduct(), amount)) {
			orderItemHashMap.put(itemId, orderItem);
		} else {
			System.out.println("Adding the item to the shopping cart has failed");
		}
	}

	public void removeItemFromShoppingCart(Long itemId) {
		orderItemHashMap.remove(itemId);
	}

	public void removeAllItemsFromShoppingCart() {
		orderItemHashMap.clear();
	}

	public void updateAmount(Long itemId, Integer newAmount) {
		if (checkAvailability(itemId, newAmount)) {
			orderItemHashMap.get(itemId).setAmount(newAmount);
		} else {
			System.out.println("Updating the item in the shopping cart has failed");
		}
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void placeAnOrder() {
		try {
			for (Map.Entry<Long, OrderItem> orderItemEntry : orderItemHashMap.entrySet()) {
				if (checkAvailability(orderItemEntry.getKey(), orderItemEntry.getValue().getAmount())) {
					buyItem(orderItemEntry.getKey(), orderItemEntry.getValue().getAmount());
				} else {
					throw new RuntimeException("There is not enough items in stock (" + orderItemEntry.getValue().getProduct().getName() + ")");
				}
			}
		} catch (RuntimeException e) {
			ejbContext.setRollbackOnly();
			System.out.println("The error occurred while placing the order");
			return;
		}

		orderItemHashMap.clear();
		System.out.println("The order has been placed");
	}

	private OrderItem createItemOrder(Long itemId, Integer amount) {
		return new OrderItem(productDao.findById(itemId), amount);
	}

	private Boolean checkAvailability(Long itemId, Integer amount) {

		Product product = productDao.findById(itemId);

		if (product.getStock() < amount) {
			System.out.println("There is not enough items in stock (" + product.getName() + ")" + "(" + amount + ")");

			return Boolean.FALSE;
		}

		return Boolean.TRUE;
	}

	private Boolean checkAvailability(Product product, Integer amount) {
		if (product.getStock() < amount) {
			System.out.println("There is not enough items in stock (" + product.getName() + ")" + "(" + amount + ")");

			return Boolean.FALSE;
		}

		return Boolean.TRUE;
	}

	@TransactionAttribute(TransactionAttributeType.MANDATORY)
	private void buyItem(Long itemId, Integer amount) {

		Product product = productDao.findById(itemId);

		product.setStock(product.getStock() - amount);
		productDao.update(product);
	}
}
