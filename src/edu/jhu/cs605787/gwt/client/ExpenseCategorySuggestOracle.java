package edu.jhu.cs605787.gwt.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.SuggestOracle;

/**
 * Custom SuggestOracle class to be used for calling a service to get category
 * suggestion
 * 
 * @author Jay
 */
class ExpenseCategorySuggestOracle extends SuggestOracle {
	private String url;

	public ExpenseCategorySuggestOracle(String url) {
		super();
		this.url = url;
	}

	@Override
	public void requestSuggestions(Request request, Callback callback) {
		RequestBuilder rb = new RequestBuilder(RequestBuilder.GET, url
				+ request.getQuery());
		final Callback localCb = callback;
		final Request localReq = request;
		try {
			rb.sendRequest(null, new RequestCallback() {
				@Override
				public void onResponseReceived(
						com.google.gwt.http.client.Request request,
						com.google.gwt.http.client.Response response) {
					List<Suggestion> list = new ArrayList<Suggestion>();
					if (response.getStatusCode() == 200) {
						String json = response.getText();
						JSONValue val = JSONParser.parseStrict(json);
						JSONArray arr = val.isArray();
						if (arr != null) {
							for (int i = 0; i < arr.size(); i++) {
								list.add(new CustomSuggestion(arr.get(i)
										.isString().stringValue()));
							}
						}
					}
					SuggestOracle.Response r = new SuggestOracle.Response();
					r.setSuggestions(list);
					localCb.onSuggestionsReady(localReq, r);
				}

				@Override
				public void onError(com.google.gwt.http.client.Request request,
						Throwable exception) {
					//Ignore the error and allow the input to continue
				}
			});
		} catch (RequestException e) {
			// Ignore errors with autocomplete
		}
	}

	class CustomSuggestion implements Suggestion {
		private String mySuggestion;

		public CustomSuggestion(String str) {
			mySuggestion = str;
		}

		@Override
		public String getDisplayString() {
			return mySuggestion;
		}

		@Override
		public String getReplacementString() {
			return mySuggestion;
		}
	}
}
