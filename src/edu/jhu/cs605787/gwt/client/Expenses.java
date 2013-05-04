package edu.jhu.cs605787.gwt.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.CalendarUtil;
import com.google.gwt.user.datepicker.client.DateBox;

/**
 * GWT Entry point class for the Expenses applications. This creates the GWT UI
 * and defines the actions.
 * 
 * @author Jay
 * 
 */
public class Expenses implements EntryPoint {

	private static final String SERVLET_URL = "/Expenses/GetExpensesServlet";

	final CellTable<Expense> table = new CellTable<Expense>();
	final List<Expense> list = new ArrayList<Expense>();
	final ListBox months = new ListBox();
	final Image img = new Image();
	final DateBox date = new DateBox();
	final TextBox description = new TextBox();
	final SuggestBox category = new SuggestBox(
			new ExpenseCategorySuggestOracle(SERVLET_URL
					+ "?action=autoComplete&value="));
	final TextBox subCategory = new TextBox();
	final TextBox amount = new TextBox();
	final TextBox store = new TextBox();
	final Grid form = new Grid(7, 2);

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		Panel p = createForm();
		RootPanel.get().add(p);

		// Initial data load
		months.fireEvent(new MyChangeEvent());
	}

	/**
	 * This method creates the Form layout for user input on the page.
	 * 
	 * @return Panel contained all of the elements for user input
	 */
	public Panel createForm() {
		final Panel p = new FlowPanel();
		// Populate date picker with last 4 months
		Date now = new Date();
		months.addItem(DateTimeFormat.getFormat("MMM yyyy").format(now));
		months.setSelectedIndex(0);
		CalendarUtil.addMonthsToDate(now, -1);
		months.addItem(DateTimeFormat.getFormat("MMM yyyy").format(now));
		CalendarUtil.addMonthsToDate(now, -1);
		months.addItem(DateTimeFormat.getFormat("MMM yyyy").format(now));
		CalendarUtil.addMonthsToDate(now, -1);
		months.addItem(DateTimeFormat.getFormat("MMM yyyy").format(now));

		months.addChangeHandler(new ChangeHandler() {
			public void onChange(ChangeEvent event) {
				loadData(months.getValue(months.getSelectedIndex()));
			}
		});

		// Create Form
		date.setFormat(new DateBox.DefaultFormat(DateTimeFormat
				.getFormat("MM-dd-yyyy")));
		final Label dateLabel = new Label("Date Bought:");
		final Label descriptionLabel = new Label("Description:");
		final Label categoryLabel = new Label("Category:");
		final Label subCategoryLabel = new Label("Sub Category:");
		final Label amountLabel = new Label("Amount:");

		// Disallow non numeric input
		amount.addKeyPressHandler(new KeyPressHandler() {
			@Override
			public void onKeyPress(KeyPressEvent event) {
				char c = (char) event.getUnicodeCharCode();
				if ((!Character.isDigit(c)) && (c != (char) KeyCodes.KEY_TAB)
						&& (c != (char) KeyCodes.KEY_BACKSPACE)
						&& (c != (char) KeyCodes.KEY_DELETE)
						&& (c != (char) KeyCodes.KEY_ENTER)
						&& (c != (char) KeyCodes.KEY_HOME)
						&& (c != (char) KeyCodes.KEY_END)
						&& (c != (char) KeyCodes.KEY_LEFT)
						&& (c != (char) KeyCodes.KEY_UP)
						&& (c != (char) KeyCodes.KEY_RIGHT)
						&& (c != (char) KeyCodes.KEY_DOWN)) {
					amount.cancelKey();
				}
			}
		});

		final Label storeLabel = new Label("Store:");

		form.setVisible(false);
		form.setWidget(0, 0, dateLabel);
		form.setWidget(0, 1, date);
		form.setWidget(1, 0, descriptionLabel);
		form.setWidget(1, 1, description);
		form.setWidget(2, 0, categoryLabel);
		form.setWidget(2, 1, category);
		form.setWidget(3, 0, subCategoryLabel);
		form.setWidget(3, 1, subCategory);
		form.setWidget(4, 0, amountLabel);
		form.setWidget(4, 1, amount);
		form.setWidget(5, 0, storeLabel);
		form.setWidget(5, 1, store);

		// Hide/show link
		final Anchor a = new Anchor("Add new Record");
		a.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (form.isVisible()) {
					form.setVisible(false);
					a.setText("Add new Record");
				} else {
					form.setVisible(true);
					a.setText("Hide form");
				}
			}
		});

		// Image
		img.setUrl("images/ajax-loader.gif");
		img.setVisible(false);

		final Button submit = new Button("Submit");
		submit.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				addData(months.getValue(months.getSelectedIndex()));
			}
		});

		final Button clear = new Button("Clear Form");
		clear.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				clearForm();
			}
		});

		Panel hPanel = new HorizontalPanel();
		hPanel.add(submit);
		hPanel.add(clear);
		form.setWidget(6, 1, hPanel);

		// Table
		TextColumn<Expense> id = new TextColumn<Expense>() {
			@Override
			public String getValue(Expense e) {
				return String.valueOf(e.getExpense_id());
			}
		};
		table.addColumn(id, "ID");
		TextColumn<Expense> date_bought = new TextColumn<Expense>() {
			@Override
			public String getValue(Expense e) {
				Date d = e.getDate_bought();
				return DateTimeFormat.getFormat("MMM dd yyyy").format(d);
			}
		};
		table.addColumn(date_bought, "Date Bought");
		TextColumn<Expense> description_col = new TextColumn<Expense>() {
			@Override
			public String getValue(Expense e) {
				return e.getDescription();
			}
		};
		table.addColumn(description_col, "Description");
		TextColumn<Expense> cat = new TextColumn<Expense>() {
			@Override
			public String getValue(Expense e) {
				return e.getCategory();
			}
		};
		table.addColumn(cat, "Category");
		TextColumn<Expense> subCat = new TextColumn<Expense>() {
			@Override
			public String getValue(Expense e) {
				return e.getSubCategory();
			}
		};
		table.addColumn(subCat, "Sub Category");
		TextColumn<Expense> amount_col = new TextColumn<Expense>() {
			@Override
			public String getValue(Expense e) {
				return String.valueOf(NumberFormat.getFormat("#.##").format(
						e.getAmount()));
			}
		};
		table.addColumn(amount_col, "Amount");
		TextColumn<Expense> store_col = new TextColumn<Expense>() {
			@Override
			public String getValue(Expense e) {
				return e.getStore();
			}
		};
		table.addColumn(store_col, "Store");
		table.getLoadingIndicator().setVisible(false);

		// Add items to panel
		p.add(months);
		p.add(new HTML("<br/>"));
		p.add(a);
		p.add(form);
		p.add(new HTML("<br/>"));
		p.add(img);
		p.add(table);

		return p;
	}

	/**
	 * This method iterates over the form and clears all TextBox values and all
	 * DateBox values
	 */
	public void clearForm() {
		Iterator<Widget> iter = form.iterator();
		while (iter.hasNext()) {
			Widget w = iter.next();
			if (w instanceof TextBox) {
				((TextBox) w).setText("");
			} else if (w instanceof DateBox) {
				((DateBox) w).setValue(null);
			} else if (w instanceof SuggestBox) {
				((SuggestBox) w).setValue("");
			}
		}
	}

	/**
	 * This method loads the data for the specified month into the table for
	 * display
	 * 
	 * @param monthStr
	 *            The month to load data for represented as a String
	 */
	public void loadData(String monthStr) {
		img.setVisible(true);
		String url = SERVLET_URL + "?action=getData&period=" + monthStr;
		RequestBuilder rb = new RequestBuilder(RequestBuilder.POST,
				URL.encode(url));
		rb.setCallback(getLoadCallBack());
		try {
			rb.send();
		} catch (RequestException e) {
			Window.alert("Error occuured communicating with the server");
			img.setVisible(false);
		}
	}

	/**
	 * This method is used to create a new CallBack to parse a getData call from
	 * the Servlet
	 * 
	 * @return new RequestCallBack object to handle the response
	 */
	private RequestCallback getLoadCallBack() {
		return new RequestCallback() {

			@Override
			public void onResponseReceived(Request request, Response response) {
				if (response.getStatusCode() == 200) {
					String json = response.getText();
					JSONValue val = JSONParser.parseStrict(json);
					if (val.isArray() != null) {
						list.clear();
						JSONArray arr = val.isArray();
						for (int i = 0; i < arr.size(); i++) {
							if (arr.get(i).isObject() != null)
								list.add(new Expense(arr.get(i).isObject()));
						}
						// List populated, build table
						table.setRowData(list);
					}
				} else {
					Window.alert("Error retrieving data");
				}
				img.setVisible(false);
			}

			@Override
			public void onError(Request request, Throwable exception) {
				Window.alert("Error occurred while trying to load data.");
				img.setVisible(false);
			}
		};
	}

	/**
	 * This method is used to send the data back to the server and it will be
	 * added if the server request was succesful.
	 * 
	 * @param monthStr
	 *            The month to add data for represented as a String
	 */
	private void addData(String monthStr) {
		img.setVisible(true);
		if (date.getValue() == null) {
			Window.alert("You must select a date");
			img.setVisible(false);
			return;
		}

		String url = SERVLET_URL + "?action=addData&period=" + monthStr
				+ "&line=";
		JSONObject ob = new JSONObject();
		ob.put("date", new JSONString(date.getTextBox().getText()));
		if (amount.getText() != null && !amount.getText().isEmpty())
			ob.put("amount", new JSONNumber(Float.valueOf(amount.getText())));
		else
			ob.put("amount", new JSONNumber(0));
		ob.put("category", new JSONString(category.getText()));
		ob.put("subCategory", new JSONString(subCategory.getText()));
		ob.put("store", new JSONString(store.getText()));
		ob.put("description", new JSONString(description.getText()));

		url += ob.toString();

		RequestBuilder rb = new RequestBuilder(RequestBuilder.POST,
				URL.encode(url));
		rb.setCallback(getAddCallBack());
		try {
			rb.send();
		} catch (RequestException e) {
			Window.alert("Error occurred communicating with the server");
			img.setVisible(false);
		}
	}

	/**
	 * This method is used to create a new CallBack to parse a addData response
	 * from the Servlet
	 * 
	 * @return new RequestCallBack object to handle the response
	 */
	private RequestCallback getAddCallBack() {
		return new RequestCallback() {

			@Override
			public void onResponseReceived(Request request, Response response) {
				if (response.getStatusCode() == 200) {
					JSONValue val = JSONParser.parseStrict(response.getText());
					if (val.isObject() != null) {
						list.add(new Expense(val.isObject()));
						clearForm();
						table.setRowData(list);
					}
				} else {
					if (response.getText() != null
							&& !response.getText().isEmpty()) {
						Window.alert(response.getText());
					} else {
						Window.alert("Server error");
					}
				}
				img.setVisible(false);
			}

			@Override
			public void onError(Request request, Throwable exception) {
				Window.alert("Error adding item");
				img.setVisible(false);
			}
		};
	}

	// Class to fire a change event
	class MyChangeEvent extends ChangeEvent {
	}
}
