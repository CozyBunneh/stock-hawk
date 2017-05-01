package com.udacity.stockhawk.data;

import android.icu.math.BigDecimal;
import android.os.Parcel;
import android.os.Parcelable;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Julia Mattjus
 */
@AllArgsConstructor
@Data public class StockHistory implements Parcelable {
    private String timeInMillis;
    private String close;

    /**
     * Get the close value for this history as a BigDecimal
     *
     * @return
     */
    public BigDecimal getCloseValue() {
        return new BigDecimal(close.replaceAll(",", ""));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.timeInMillis);
        dest.writeString(this.close);
    }

    protected StockHistory(Parcel in) {
        this.timeInMillis = in.readString();
        this.close = in.readString();
    }

    public static final Parcelable.Creator<StockHistory> CREATOR = new Parcelable.Creator<StockHistory>() {
        @Override
        public StockHistory createFromParcel(Parcel source) {
            return new StockHistory(source);
        }

        @Override
        public StockHistory[] newArray(int size) {
            return new StockHistory[size];
        }
    };
}
