package edu.jhu.cs605787.gwt.client;

import java.util.Date;

import com.google.gwt.json.client.JSONObject;

/**
 * GWT Entity class for an Expense
 * 
 * @author Jay
 */
class Expense {
	private int expense_id;
	private Date date_bought;
	private String description;
	private String category;
	private String subCategory;
	private float amount;
	private String store;

	public Expense(JSONObject obj) {
		expense_id = (int) obj.get("expense_id").isNumber().doubleValue();
		date_bought = new Date((long) obj.get("date_bought").isNumber()
				.doubleValue());
		description = obj.get("description").isString().stringValue();
		category = obj.get("category").isString().stringValue();
		subCategory = obj.get("subCategory").isString().stringValue();
		amount = (float) obj.get("amount").isNumber().doubleValue();
		store = obj.get("store").isString().stringValue();
	}

	public int getExpense_id() {
		return expense_id;
	}

	public void setExpense_id(int expense_id) {
		this.expense_id = expense_id;
	}

	public Date getDate_bought() {
		return date_bought;
	}

	public void setDate_bought(Date date_bought) {
		this.date_bought = date_bought;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getSubCategory() {
		return subCategory;
	}

	public void setSubCategory(String subCategory) {
		this.subCategory = subCategory;
	}

	public float getAmount() {
		return amount;
	}

	public void setAmount(float amount) {
		this.amount = amount;
	}

	public String getStore() {
		return store;
	}

	public void setStore(String store) {
		this.store = store;
	}
}
