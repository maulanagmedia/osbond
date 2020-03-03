package gmedia.net.id.osbond;

import android.app.Activity;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by Bayu on 29/01/2018.
 */

public class HideKeyboard {
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }
}
