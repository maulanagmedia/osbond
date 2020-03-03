package gmedia.net.id.osbond.utils;

import androidx.multidex.MultiDexApplication;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

/**
 * Created by Shinmaul on 3/27/2018.
 */

public class CustomApplication extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
    }
}
