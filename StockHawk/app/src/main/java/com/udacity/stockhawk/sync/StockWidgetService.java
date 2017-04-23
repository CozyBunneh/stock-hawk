package com.udacity.stockhawk.sync;

import android.content.Intent;
import android.widget.RemoteViewsService;

import com.udacity.stockhawk.data.WidgetDataProvider;

/**
 * @author Julia Mattjus
 */
public class StockWidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new WidgetDataProvider(this, intent);
    }
}
