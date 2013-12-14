package edu.feup.stockportfolio.network;

import android.accounts.NetworkErrorException;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

import edu.feup.stockportfolio.client.StockData;

public class StockNetworkUtilities extends NetworkUtilities{

    private static final String BASE_URL_SINGLE = BASE_URL + "finance.yahoo.com/d/quotes?";
    private static final String BASE_URL_HISTORY = BASE_URL + "ichart.finance.yahoo.com/table.txt?";
    private static final String SINGLE_FORMAT_QUERY = "f=snl1c1p2o0h0g0va2j1";

    /*  *format query parameters*
        s  = codename
        n  = formal name
        l1 = quote value
        c1 = change
        p2 = change percentage
        o0 = open
        h0 = high
        g0 = low
        v  = volume
        a2 = avg volume
        j1 = market cap
    */

    public static void refresh_today(StockData company) throws Exception {

        String uri = BASE_URL_SINGLE + SINGLE_FORMAT_QUERY +"&s=";
        uri += company.get_company();
        String response = get(uri);
        if (response == null) {
            throw new NullPointerException();
        }
        Log.d("REPLY",response);

        company.populate_today(response.split("(,)(?=(?:[^\"]|\"[^\"]*\")*$)"));
    }

    public static void refresh_all_today(ArrayList<StockData> companies) {
        String uri = BASE_URL_SINGLE + SINGLE_FORMAT_QUERY +"&s=";

        for(StockData company : companies){
           uri += company.get_company()+",";
        }

        String response = get(uri);
        if (response == null) {
            throw new NullPointerException();
        }
        Log.d("REPLY",response);
        String[] split_data = response.split("\n");

        for(int i=0; i<companies.size(); ++i){
            try {
                companies.get(i).populate_today(split_data[i].split("(,)(?=(?:[^\"]|\"[^\"]*\")*$)"));
            } catch (Exception e) {
                //TODO company does not exist (why is it added?)
                e.printStackTrace();
            }
        }
    }

    public static void refresh_history(StockData company){
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        String month_start = "&a="+ (month==0 ? 11 : month-1);
        String day_start = "&b="+day;
        String year_start = "&c="+ (month==0 ? year-1 : year);
        String month_end = "&d="+ month;
        String day_end = "&e="+ day;
        String year_end = "&f="+ year;
        String periodicity = "&g="+ "d";

        String uri = BASE_URL_HISTORY+month_start+day_start+year_start+
                month_end+day_end+year_end+periodicity+"&s="+company.get_company();
        Log.d("REPLY",uri);
        String response = get(uri);
        if (response == null) {
            throw new NullPointerException();
        }
        Log.d("REPLY",response);

        //FIRST LINE IS HEADER, DATA STARTS ON SECOND LINE!
        String[] lines = response.split("\n");
        company.populate_history(lines," - -" + day);
    }

    public static StockData new_stock(String company, int quantity){
        StockData sd = new StockData(company, quantity);
        try{
            StockNetworkUtilities.refresh_today(sd);
        } catch (Exception e){
            return null;
        }

        return sd;
    }
}
