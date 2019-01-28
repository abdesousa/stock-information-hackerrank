package com.hackerrank;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class StockInformation {

	public static void main(String[] args) throws IOException {

		openAndClosePrices("1-January-2000", "22-February-2000", "Monday");
	}

	/*
	 * Complete the function below.
	 */

	static void openAndClosePrices(String firstDate, String lastDate, String weekDay) {

		int total_pages;
		int page = 1;
		List<StockData> stockInIntervalList = new ArrayList<StockData>();
		try {
			do {

				Stock stock = invokeEndpoint(page);
				total_pages = stock.total_pages;

				for (StockData stockData : stock.data) {

					if (isInTheInterval(stockData.date, firstDate, lastDate)
							&& getWeekDate(stockData.date).equalsIgnoreCase(weekDay)) {
						stockInIntervalList.add(stockData);
					}

					if (isDateAfter(stockData.date, lastDate)) {
						break;
					}
				}
				page++;

			} while (page < total_pages);

			stockInIntervalList.forEach(System.out::println);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static Stock invokeEndpoint(int page) throws IOException {

		String url = "https://jsonmock.hackerrank.com/api/stocks?page=" + page;

		String content = fetchContent(url);

		GsonBuilder builder = new GsonBuilder();
		builder.setPrettyPrinting();

		Gson gson = builder.create();

		return gson.fromJson(content, Stock.class);
	}

	private static String getWeekDate(String baseStrDate) {
		Date baseDate = convertStringToDate(baseStrDate);
		SimpleDateFormat simpleDateformat = new SimpleDateFormat("EEEE", Locale.US);
		return simpleDateformat.format(baseDate);
	}

	private static Date convertStringToDate(String strDate) {

		DateFormat df = new SimpleDateFormat("dd-MMMMM-yyyy", Locale.US);
		Date date = null;
		try {
			date = df.parse(strDate);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return date;
	}

	private static boolean isDateAfter(String dStr1, String dStr2) {

		Date d1 = convertStringToDate(dStr1);
		Date d2 = convertStringToDate(dStr2);

		return d1.after(d2);

	}

	private static boolean isInTheInterval(String baseStrDate, String startStrDate, String endStrDate) {

		Date startDate = convertStringToDate(startStrDate);
		Date endDate = convertStringToDate(endStrDate);
		Date baseDate = convertStringToDate(baseStrDate);

		return baseDate.after(startDate) && baseDate.before(endDate);

	}

	private static String fetchContent(String uri) throws IOException {

		final int OK = 200;
		URL url = new URL(uri);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();

		int responseCode = connection.getResponseCode();
		if (responseCode == OK) {
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			return response.toString();
		}

		return null;
	}

	static class Stock {

		private int page;
		private int per_page;
		private int total;
		private int total_pages;

		private List<StockData> data;

		public Stock() {
			super();

		}

		@Override
		public String toString() {
			return "Stock [page=" + page + ", per_page=" + per_page + ", total=" + total + ", total_pages="
					+ total_pages + ", data=" + data + "]";
		}
	}

	class StockData {

		private String date;
		private float open;
		private float high;
		private float low;
		private float close;

		public StockData() {
			super();
		}

		@Override
		public String toString() {
			//17-January-2000 5617.7 5404.07

			return date + " " + open + " " + low;
		}

	}

}