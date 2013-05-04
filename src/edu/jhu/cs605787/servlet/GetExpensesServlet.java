package edu.jhu.cs605787.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.jhu.cs605787.entity.Expense;

/**
 * Servlet for serving Expense Management requests. This servlet contains member
 * variables to maintain track of expenses in a thread safe manner. However,
 * consistency can be considered "eventual" in that a change can be made while a
 * current getData request is processing, but the changes will not be reflected
 * in the current getData request.
 * 
 * @author Jay
 */
public class GetExpensesServlet extends HttpServlet {

	private static final long serialVersionUID = -7842201737256999231L;
	private final Map<String, List<Expense>> dataMap = new HashMap<String, List<Expense>>();

	// Utilize classes from the Concurrent package rather than using
	// synchronized blocks
	private final AtomicInteger curId = new AtomicInteger();
	private List<String> categories = new CopyOnWriteArrayList<String>(
			new String[] { "Food", "Gas", "Car" });

	/**
	 * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
	 * methods.
	 * 
	 * @param request
	 *            servlet request
	 * @param response
	 *            servlet response
	 * @throws ServletException
	 *             if a servlet-specific error occurs
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	protected void processRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		// Prevent IE caching by setting headers
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Cache-Control", "no-cache");

		// Retrieve parameters
		String action = request.getParameter("action");
		// TODO add in checking to ensure period is not null and error check
		// other areas of this class. For example should validate that the date
		// of the expense is in the same month as the period it is being added
		// to.
		String period = request.getParameter("period");

		if (action == null) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST,
					"No action specified. Action is required.");
		} else if (action.equals("getData")) {
			/*
			 * Thread.sleep calls are placed inside specific calls to simulate
			 * server processing data and allows UI to display spinner
			 */
			try {
				Thread.sleep(1000);
			} catch (InterruptedException ex) {
			}

			List<Expense> list = dataMap.get(period);
			response.setContentType("application/json");
			if (list != null) {
				JSONArray array = new JSONArray();
				for (Expense e : list) {
					array.put(e.toJSONObject());
				}
				out.append(array.toString());
			}
		} else if (action.equals("addData")) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException ex) {
			}

			String error = "";
			String line = request.getParameter("line");
			Expense expense = null;
			if (line != null) {
				try {
					JSONObject object = new JSONObject(line);
					DateFormat formatter = new SimpleDateFormat("MM-dd-yyyy");
					Date date = null;
					date = formatter.parse(object.getString("date"));
					float amount = (float) object.optDouble("amount", 0);
					int id = curId.getAndIncrement();
					String category = object.optString("category");

					if (category != null && !category.isEmpty()) {
						if (!categories.contains(category)) {
							categories.add(category);
						}
					}

					expense = new Expense(id, date,
							object.optString("description"), category,
							object.optString("subCategory"), amount,
							object.optString("store"));
					List<Expense> list = dataMap.get(period);
					list.add(expense);
				} catch (ParseException e) {
					error = "Invalid date specified";
				} catch (JSONException e) {
					error = "Improperly formatted input";
				}
			} else {
				error = "No data received from client";
			}

			if (!error.isEmpty()) {
				response.setContentType("text/plain;charset=UTF-8");
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				out.append(error);
			} else {
				response.setContentType("application/json");
				out.append(expense.toJSONObject().toString());
			}
		} else if (action.equals("autoComplete")) {
			String value = request.getParameter("value");
			JSONArray arr = new JSONArray();
			if (value != null && !value.isEmpty()) {
				value = value.toLowerCase();
				for (String cat : categories) {
					if (cat.toLowerCase().contains(value)) {
						arr.put(cat);
					}
				}
			}
			out.append(arr.toString());
		} else {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST,
					"Unknown action specified.");
		}
	}

	@Override
	public void init() {
		List<Expense> list = null;

		// Get current date and add items
		Calendar now = new GregorianCalendar();
		SimpleDateFormat formatter = new SimpleDateFormat("MMM yyyy");
		list = new ArrayList<Expense>(2);
		list.add(new Expense(1, new Date(), "Desc 1", "Food", "Non-Alcoholic",
				53.65f, "Ray's the Steaks"));
		list.add(new Expense(2, new Date(), "Desc 2", "Food", "Non-Alcoholic",
				73.01f, "Melting Pot"));
		dataMap.put(formatter.format(now.getTime()),
				new CopyOnWriteArrayList<Expense>(list));

		// Subtract 1 month and repeat above steps 3 times
		now.add(Calendar.MONTH, -1);
		list = new ArrayList<Expense>(2);
		list.add(new Expense(3, now.getTime(), "Desc 3", "Gas", "Regular",
				99.07f, "Sunoco"));
		list.add(new Expense(4, now.getTime(), "Desc 4", "Food",
				"Non-Alcoholic", 4.99f, "20 piece nuggets"));
		dataMap.put(formatter.format(now.getTime()),
				new CopyOnWriteArrayList<Expense>(list));

		now.add(Calendar.MONTH, -1);
		list = new ArrayList<Expense>(2);
		list.add(new Expense(5, now.getTime(), "Desc 5", "Car", "Rental",
				253.65f, "Enterprise"));
		list.add(new Expense(6, now.getTime(), "Desc 6", "Gas", "Premium",
				73.01f, "Exxon"));
		dataMap.put(formatter.format(now.getTime()),
				new CopyOnWriteArrayList<Expense>(list));

		now.add(Calendar.MONTH, -1);
		list = new ArrayList<Expense>(2);
		list.add(new Expense(7, now.getTime(), "Desc 7", "Food",
				"Non-Alcoholic", 7.23f, "Chipotle"));
		list.add(new Expense(8, now.getTime(), "Desc 8", "Food",
				"Non-Alcoholic", 6.03f, "Five Guys"));
		dataMap.put(formatter.format(now.getTime()),
				new CopyOnWriteArrayList<Expense>(list));

		curId.set(9);
	}

	/**
	 * Handles the HTTP <code>GET</code> method.
	 * 
	 * @param request
	 *            servlet request
	 * @param response
	 *            servlet response
	 * @throws ServletException
	 *             if a servlet-specific error occurs
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}

	/**
	 * Handles the HTTP <code>POST</code> method.
	 * 
	 * @param request
	 *            servlet request
	 * @param response
	 *            servlet response
	 * @throws ServletException
	 *             if a servlet-specific error occurs
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}

	/**
	 * Returns a short description of the servlet.
	 * 
	 * @return a String containing servlet description
	 */
	@Override
	public String getServletInfo() {
		return "Servlet to get Expenses";
	}
}
