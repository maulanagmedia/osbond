package gmedia.net.id.osbond;

import android.graphics.Bitmap;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

/**
 * Created by Bayu on 19/03/2018.
 */

public class EncodeBitmapToString {
    public static String convert(Bitmap bitmap)
    {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);
    }
}
