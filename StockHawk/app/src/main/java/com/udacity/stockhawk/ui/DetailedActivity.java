package com.udacity.stockhawk.ui;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.PersistableBundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.common.collect.Lists;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.data.Stock;
import com.udacity.stockhawk.data.StockHistory;
import com.udacity.stockhawk.utilities.JsonUtility;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class DetailedActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Stock> {

    private static final String LIFECYCLE_CALLBACKS_TEXT_KEY = "callbacks";
    private static final int STOCK_LOADER_ID = 1;

    private static final int INDEX_STOCK_ID = 0;
    private static final int INDEX_STOCK_SYMBOL = 1;
    private static final int INDEX_STOCK_PRICE = 2;
    private static final int INDEX_STOCK_ABSOLUTE_CHANGE = 3;
    private static final int INDEX_STOCK_PERCENTAGE_CHANGE = 4;
    private static final int INDEX_STOCK_HISTORY = 5;

    private String mStockSymbol;
    private Stock mStock;

    @BindView(R.id.tv_detail_error_message_display) TextView mErrorMessageDisplay;
    @BindView(R.id.pb_detail_loading_indicator) ProgressBar mLoadingIndicator;
    @BindView(R.id.chart) LineChart chart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed);

        ButterKnife.bind(this);

        if(savedInstanceState != null && savedInstanceState.containsKey(LIFECYCLE_CALLBACKS_TEXT_KEY)) {
            mStock = savedInstanceState.getParcelable(LIFECYCLE_CALLBACKS_TEXT_KEY);
            mStockSymbol = mStock.getSymbol();
            initChart();
            showStock();
        } else {
            Intent intentThatStartedThisActivity = getIntent();

            if(intentThatStartedThisActivity != null) {
                mStockSymbol = intentThatStartedThisActivity.getStringExtra(Intent.EXTRA_UID);
                Timber.d("onCreate mStockSymbol: " + mStockSymbol);

                getSupportLoaderManager().initLoader(STOCK_LOADER_ID, null, this);
            }
        }
    }

    private void setLoading() {
        chart.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mLoadingIndicator.setVisibility(View.VISIBLE);
    }

    private void showStock() {
        chart.setVisibility(View.VISIBLE);
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mLoadingIndicator.setVisibility(View.INVISIBLE);
    }

    private void showError() {
        chart.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
        mLoadingIndicator.setVisibility(View.INVISIBLE);
    }

    @Override
    public Loader<Stock> onCreateLoader(int id, Bundle args) {
        final Context context = this;

        return new AsyncTaskLoader<Stock>(context) {

            Stock mTaskData = null;

            @Override
            protected void onStartLoading() {
                setLoading();

                if (mTaskData != null) {
                    deliverResult(mTaskData);
                } else {
                    forceLoad();
                }
            }

            @Override
            public Stock loadInBackground() {
                Cursor cursor = context.getContentResolver().query(
                    Contract.Quote.makeUriForStock(mStockSymbol),
                    null,
                    null,
                    null,
                    null,
                    null
                );

                try {
                    if (cursor == null || !cursor.moveToFirst()) {
                        return null;
                    }

                    Integer id = cursor.getInt(INDEX_STOCK_ID);
                    String symbol = cursor.getString(INDEX_STOCK_SYMBOL);
                    Double price = cursor.getDouble(INDEX_STOCK_PRICE);
                    Double absoluteChange = cursor.getDouble(INDEX_STOCK_ABSOLUTE_CHANGE);
                    Double percentageChange = cursor.getDouble(INDEX_STOCK_PERCENTAGE_CHANGE);
                    String historyString = cursor.getString(INDEX_STOCK_HISTORY);
                    List<StockHistory> history = parseHistory(historyString);

                    return new Stock(id, symbol, price, absoluteChange, percentageChange, history);
                } finally {
                    if(cursor != null) {
                        cursor.close();
                    }
                }
            }

            public void deliverResult(Stock stock) {
                mTaskData = stock;
                super.deliverResult(stock);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Stock> loader, Stock data) {
        if (data == null) {
            showError();
        } else {
            mStock = data;
            initChart();
            showStock();
        }
    }

    @Override
    public void onLoaderReset(Loader<Stock> loader) {
        mStock = null;
        mStockSymbol = null;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Timber.d("onSaveInstanceState mStock:" + JsonUtility.toJson(mStock));

        if(mStock != null) {
            outState.putParcelable(LIFECYCLE_CALLBACKS_TEXT_KEY, mStock);
        }
    }

    private void initChart() {
        if(mStock.getHistory() == null) {
            return;
        }

        List<Entry> entries = new ArrayList<>();
        List<Float> yValues = new ArrayList<>();
        for(StockHistory stockHistory : Lists.reverse(mStock.getHistory())) {
            Float x = Float.parseFloat(stockHistory.getTimeInMillis());
            Float y = Float.parseFloat(stockHistory.getClose());
            yValues.add(y);
            Timber.d("initChart x \"" + x + "\" y \"" + y + "\"");
            entries.add(new Entry(x, y));
            //entries.add(new Entry(Float.parseFloat(stockHistory.getClose()), Float.parseFloat(stockHistory.getTimeInMillis())));
        }

        LineDataSet dataSet = new LineDataSet(entries, mStockSymbol);
        dataSet.setColor(Color.GREEN);
        dataSet.setValueTextColor(Color.DKGRAY);

        LineData lineData = new LineData(dataSet);
        chart.setFitsSystemWindows(true);

        chart.getXAxis().setDrawLabels(false);
        YAxis left = chart.getAxisLeft();

        left.setAxisMaximum(Collections.max(yValues));
        left.setAxisMinimum(Collections.min(yValues));
        chart.getAxisRight().setEnabled(false);

        chart.setBackgroundColor(Color.WHITE);
        chart.setData(lineData);
        chart.invalidate();
    }

    private List<StockHistory> parseHistory(String historyString) {
        Pattern pattern = Pattern.compile("(\\d{13}, \\d+(\\.\\d*)?)");

        Matcher matcher = pattern.matcher(historyString);

        List<StockHistory> history = new ArrayList<>();
        while(matcher.find()) {
            Timber.d("parseHistory - found match: " + matcher.group());
            String[] splitHistory = matcher.group().split(", ");
            StockHistory stockHistory = new StockHistory(splitHistory[0], splitHistory[1]);
            history.add(stockHistory);
        }

        return history;
    }
}
