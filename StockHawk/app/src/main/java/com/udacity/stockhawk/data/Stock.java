package com.udacity.stockhawk.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Julia Mattjus
 */
@AllArgsConstructor
@Data public class Stock implements Parcelable {
    private Integer id;
    private String symbol;
    private Double price;
    private Double absoluteChange;
    private Double percentageChange;
    private List<StockHistory> history;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeString(this.symbol);
        dest.writeValue(this.price);
        dest.writeValue(this.absoluteChange);
        dest.writeValue(this.percentageChange);
        dest.writeTypedList(this.history);
    }

    protected Stock(Parcel in) {
        this.id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.symbol = in.readString();
        this.price = (Double) in.readValue(Double.class.getClassLoader());
        this.absoluteChange = (Double) in.readValue(Double.class.getClassLoader());
        this.percentageChange = (Double) in.readValue(Double.class.getClassLoader());
        this.history = in.createTypedArrayList(StockHistory.CREATOR);
    }

    public static final Creator<Stock> CREATOR = new Creator<Stock>() {
        @Override
        public Stock createFromParcel(Parcel source) {
            return new Stock(source);
        }

        @Override
        public Stock[] newArray(int size) {
            return new Stock[size];
        }
    };
}
