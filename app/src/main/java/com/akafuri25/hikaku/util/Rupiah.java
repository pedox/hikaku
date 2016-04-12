package com.akafuri25.hikaku.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

/**
 * Created by pedox on 4/12/16.
 */
public class Rupiah {

    int money;

    public int getMoney() {
        return money;
    }

    public static String parse(double money) {
        DecimalFormat decimalFormat = (DecimalFormat) DecimalFormat.getCurrencyInstance();
        DecimalFormatSymbols formatRp = new DecimalFormatSymbols();
        formatRp.setCurrencySymbol("Rp. ");
        formatRp.setMonetaryDecimalSeparator(',');
        formatRp.setGroupingSeparator('.');
        decimalFormat.setDecimalFormatSymbols(formatRp);
        return decimalFormat.format(money);
    }
}
