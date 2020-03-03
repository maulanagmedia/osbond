package gmedia.net.id.osbond;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;

public class ChangeToRupiah {
    public ChangeToRupiah() {

    }

    public Double parseNullDouble(String s) {
        double result = 0;
        if (s != null && s.length() > 0) {
            try {
                result = Double.parseDouble(s);
            } catch (Exception e) {
                e.printStackTrace();

            }
        }
        return result;
    }

    public String ChangeToRupiahFormat(String number) {

        double value = parseNullDouble(number);

        NumberFormat format = NumberFormat.getCurrencyInstance();
        DecimalFormatSymbols symbols = ((DecimalFormat) format).getDecimalFormatSymbols();

        symbols.setCurrencySymbol("Rp ");
        ((DecimalFormat) format).setDecimalFormatSymbols(symbols);
        format.setMaximumFractionDigits(0);

        String hasil = String.valueOf(format.format(value));

        return hasil;
    }

    public String doubleToStringFull(Double number) {
        return String.format("%s", number).replace(",", ".");
    }
}
