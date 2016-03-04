package be.bewweb.StopWatch.Utils;

import java.math.BigDecimal;

/**
 * Created by Quentin on 15-02-16.
 */
public class Number {
    /**
     * Round to certain number of decimals
     *
     * @param d
     * @param decimalPlace
     * @return
     */
    public static float round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }
}
