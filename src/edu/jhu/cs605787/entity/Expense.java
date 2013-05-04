package edu.jhu.cs605787.entity;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This is the Expense entity which represents a single expense in our mock
 * expense reporting system
 * 
 * @author Jay
 */
public class Expense {

	private int expense_id;
	private Date date_bought;
	private String description;
	private String category;
	private String subCategory;
	private float amount;
	private String store;

	private static final String SEPERATOR = ";";

	public Expense(int expense_id, Date date_bought, String description,
			String category, String subCategory, float amount, String store) {
		this.expense_id = expense_id;
		this.date_bought = date_bought;
		this.description = description;
		this.category = category;
		this.subCategory = subCategory;
		this.amount = amount;
		this.store = store;
	}

	public float getAmount() {
		return amount;
	}

	public void setAmount(float amount) {
		this.amount = amount;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
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

	public int getExpense_id() {
		return expense_id;
	}

	public void setExpense_id(int expense_id) {
		this.expense_id = expense_id;
	}

	public String getStore() {
		return store;
	}

	public void setStore(String store) {
		this.store = store;
	}

	public String getSubCategory() {
		return subCategory;
	}

	public void setSubCategory(String subCategory) {
		this.subCategory = subCategory;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Expense other = (Expense) obj;
		if (this.expense_id != other.expense_id) {
			return false;
		}
		if (this.date_bought != other.date_bought
				&& (this.date_bought == null || !this.date_bought
						.equals(other.date_bought))) {
			return false;
		}
		if ((this.description == null) ? (other.description != null)
				: !this.description.equals(other.description)) {
			return false;
		}
		if ((this.category == null) ? (other.category != null) : !this.category
				.equals(other.category)) {
			return false;
		}
		if ((this.subCategory == null) ? (other.subCategory != null)
				: !this.subCategory.equals(other.subCategory)) {
			return false;
		}
		if (Float.floatToIntBits(this.amount) != Float
				.floatToIntBits(other.amount)) {
			return false;
		}
		if ((this.store == null) ? (other.store != null) : !this.store
				.equals(other.store)) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 17 * hash + this.expense_id;
		hash = 17 * hash
				+ (this.date_bought != null ? this.date_bought.hashCode() : 0);
		hash = 17 * hash
				+ (this.description != null ? this.description.hashCode() : 0);
		hash = 17 * hash
				+ (this.category != null ? this.category.hashCode() : 0);
		hash = 17 * hash
				+ (this.subCategory != null ? this.subCategory.hashCode() : 0);
		hash = 17 * hash + Float.floatToIntBits(this.amount);
		hash = 17 * hash + (this.store != null ? this.store.hashCode() : 0);
		return hash;
	}

	@Override
	public String toString() {
		return "Expense{" + "expense_id=" + expense_id + ", date_bought="
				+ date_bought + ", description=" + description + ", category="
				+ category + ", subCategory=" + subCategory + ", amount="
				+ amount + ", store=" + store + '}';
	}

	/**
	 * This method takes the expense object and constructs a string where each
	 * item is separated by a ';' character
	 * 
	 * @return semicolon separated string of this object's values
	 */
	public String semicolonSeperatedValues() {
		StringBuilder builder = new StringBuilder();
		builder.append(this.expense_id);
		builder.append(SEPERATOR);
		builder.append(this.date_bought);
		builder.append(SEPERATOR);
		builder.append(this.description);
		builder.append(SEPERATOR);
		builder.append(this.category);
		builder.append(SEPERATOR);
		builder.append(this.subCategory);
		builder.append(SEPERATOR);
		builder.append(this.amount);
		builder.append(SEPERATOR);
		builder.append(this.store);
		return builder.toString();
	}

	/**
	 * This method creates a JSONObject from the values contained within this
	 * class
	 * 
	 * @return JSONObject containing the values found in the class
	 */
	public JSONObject toJSONObject() {
		JSONObject expense = new JSONObject();
		try {
			expense.put("expense_id", this.expense_id);
			expense.put("date_bought", this.date_bought.getTime());
			expense.put("description", this.description);
			expense.put("category", this.category);
			expense.put("subCategory", this.subCategory);
			expense.put("amount", this.amount);
			expense.put("store", this.store);
		} catch (JSONException ex) {
			Logger.getLogger(Expense.class.getName()).log(Level.SEVERE, null,
					ex);
		}

		return expense;
	}
}
