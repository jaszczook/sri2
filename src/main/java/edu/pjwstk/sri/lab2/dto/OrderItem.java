package edu.pjwstk.sri.lab2.dto;

import java.io.Serializable;

import edu.pjwstk.sri.lab2.model.Product;

public class OrderItem implements Serializable {
	private Product product;
	private Integer amount;

	public OrderItem(Product product, Integer amount) {
		this.product = product;
		this.amount = amount;
	}

	public Product getProduct() {
		return product;
	}
	public void setProduct(Product product) {
		this.product = product;
	}
	public Integer getAmount() {
		return amount;
	}
	public void setAmount(Integer amount) {
		this.amount = amount;
	}
}
