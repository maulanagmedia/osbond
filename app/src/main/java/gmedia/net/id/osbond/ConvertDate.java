package gmedia.net.id.osbond;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ConvertDate {
    public static String convert(String fromFormatDate, String toFormatDate, String inputan) {
        DateFormat inputFormat = new SimpleDateFormat(fromFormatDate);
        DateFormat outputFormat = new SimpleDateFormat(toFormatDate);
        String inputDateStr = inputan;
        Date date = null;
        try {
            date = inputFormat.parse(inputDateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String outputDateStr = outputFormat.format(date);
        return outputDateStr;
    }
}
