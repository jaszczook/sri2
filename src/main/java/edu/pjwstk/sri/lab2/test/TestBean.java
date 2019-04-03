package edu.pjwstk.sri.lab2.test;

import edu.pjwstk.sri.lab2.dao.CategoryDao;
import edu.pjwstk.sri.lab2.dao.ProductDao;
import edu.pjwstk.sri.lab2.dto.OrderItem;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;

@Named("testBean")
@RequestScoped
public class TestBean implements Serializable {

	@Inject
	private CategoryDao categoryDao;

	@Inject
	private ProductDao productDao;

	@Inject
	private Client1 client1;

	@Inject
	private Client2 client2;

	public TestBean() {
	}

	public void test() {
		testCaching();
		testShoppingCart();
	}

	private void testCaching() {
		System.out.println("\nCACHING TEST");

		System.out.println("Original test category: " + categoryDao.findById(1009L));

		System.out.println("Deleting category...");
		categoryDao.deleteById(1009L);

		System.out.println("Getting category after deletion (it should still be in cache): " + categoryDao.findById(1009L));
		try {
			System.out.println("Sleep for 15s, to make sure that cache will update data");
			Thread.sleep(15 * 1000);
		} catch (InterruptedException e) {
			System.out.println("Something went wrong during caching test!");
		}
		System.out.println("Getting category after sleep (it should NOT be in cache): " + categoryDao.findById(1009L));
	}

	private void testShoppingCart() {
		singleClientBuySuccessfully();
		resetShoppingCarts();
		singleClientTryToAddTooBigAmount();
		resetShoppingCarts();
		singleClientTryToUpdateTooBigAmount();
		resetShoppingCarts();
		twoClientsDifferentCarts();
		resetShoppingCarts();
		twoClientsConcurrentBuy();
	}

	private void singleClientBuySuccessfully() {
		System.out.println("\nSINGLE CLIENT BUY TEST");

		System.out.println("Original test product: " + productDao.findById(2000L).getName() + "(" + productDao.findById(2000L).getStock() + ")");
		System.out.println("Original test product: " + productDao.findById(2001L).getName() + "(" + productDao.findById(2001L).getStock() + ")");

		System.out.println("Adding the product to the cart, " + productDao.findById(2000L).getName() + "(2)");
		client1.shoppingCart.addItemToShoppingCart(2000L, 2);
		System.out.println("Adding the product to the cart, " + productDao.findById(2001L).getName() + "(2)");
		client1.shoppingCart.addItemToShoppingCart(2001L, 2);

		displayClient1Cart();

		System.out.println("Test product after adding to the cart: " + productDao.findById(2000L).getName() + "(" + productDao.findById(2000L).getStock() + ")");
		System.out.println("Test product after adding to the cart: " + productDao.findById(2001L).getName() + "(" + productDao.findById(2001L).getStock() + ")");

		System.out.println("Placing the order...");
		client1.shoppingCart.placeAnOrder();

		System.out.println("Test product after placing the order: " + productDao.findById(2000L).getName() + "(" + productDao.findById(2000L).getStock() + ")");
		System.out.println("Test product after placing the order: " + productDao.findById(2001L).getName() + "(" + productDao.findById(2001L).getStock() + ")");

		displayClient1Cart();
	}

	private void singleClientTryToAddTooBigAmount() {
		System.out.println("\nSINGLE CLIENT TRY TO ADD TOO BIG AMOUNT TEST");

		System.out.println("Original test product: " + productDao.findById(2000L).getName() + "(" + productDao.findById(2000L).getStock() + ")");

		System.out.println("Adding the product to the cart, " + productDao.findById(2000L).getName() + "(10)");
		client1.shoppingCart.addItemToShoppingCart(2000L, 10);

		displayClient1Cart();
	}

	private void singleClientTryToUpdateTooBigAmount() {
		System.out.println("\nSINGLE CLIENT TRY TO UPDATE TOO BIG AMOUNT TEST");

		System.out.println("Original test product: " + productDao.findById(2000L).getName() + "(" + productDao.findById(2000L).getStock() + ")");

		System.out.println("Adding the product to the cart, " + productDao.findById(2000L).getName() + "(1)");
		client1.shoppingCart.addItemToShoppingCart(2000L, 1);

		System.out.println("Updating the product in the cart, " + productDao.findById(2000L).getName() + "(20)");
		client1.shoppingCart.updateAmount(2000L, 20);

		displayClient1Cart();
	}

	private void twoClientsDifferentCarts() {
		System.out.println("\nTWO CLIENTS DIFFERENT CARTS TEST");

		System.out.println("Original test product for client1: " + productDao.findById(2000L).getName() + "(" + productDao.findById(2000L).getStock() + ")");
		System.out.println("Original test product for client2: " + productDao.findById(2006L).getName() + "(" + productDao.findById(2006L).getStock() + ")");

		System.out.println("Adding the product to the Client1's cart, " + productDao.findById(2000L).getName() + "(1)");
		client1.shoppingCart.addItemToShoppingCart(2000L, 1);

		System.out.println("Adding the product to the Client2's cart, " + productDao.findById(2006L).getName() + "(1)");
		client2.shoppingCart.addItemToShoppingCart(2006L, 1);

		displayClient1Cart();
		displayClient2Cart();
	}

	private void twoClientsConcurrentBuy() {
		System.out.println("\nTWO CLIENTS CONCURRENT BUY TEST");

		System.out.println("Original test product: " + productDao.findById(2006L).getName() + "(" + productDao.findById(2006L).getStock() + ")");

		System.out.println("Adding the product to the Client1's cart, " + productDao.findById(2006L).getName() + "(4) - maximum available amount");
		client1.shoppingCart.addItemToShoppingCart(2006L, 4);

		System.out.println("Adding the product to the Client2's cart, " + productDao.findById(2006L).getName() + "(1)");
		client2.shoppingCart.addItemToShoppingCart(2006L, 1);

		displayClient1Cart();
		displayClient2Cart();

		System.out.println("Placing the order for Client1...");
		client1.shoppingCart.placeAnOrder();

		System.out.println("Test product after placing the order: " + productDao.findById(2001L).getName() + "(" + productDao.findById(2001L).getStock() + ")");

		System.out.println("Placing the order for Client2...");
		client2.shoppingCart.placeAnOrder();

		displayClient1Cart();
		displayClient2Cart();
	}

	private void resetShoppingCarts() {
		client1.shoppingCart.removeAllItemsFromShoppingCart();
		client2.shoppingCart.removeAllItemsFromShoppingCart();
	}

	private void displayClient1Cart() {
		System.out.println("Client1's cart:");
		for (OrderItem orderItem : client1.shoppingCart.getShoppingCart()) {
			System.out.println(orderItem.getProduct().getName() + "(" + orderItem.getAmount() + ")");
		}
	}

	private void displayClient2Cart() {
		System.out.println("Client2's cart:");
		for (OrderItem orderItem : client2.shoppingCart.getShoppingCart()) {
			System.out.println(orderItem.getProduct().getName() + "(" + orderItem.getAmount() + ")");
		}
	}
}
