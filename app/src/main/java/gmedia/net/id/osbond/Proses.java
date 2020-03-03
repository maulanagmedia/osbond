package gmedia.net.id.osbond;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;


public class Proses {
    private Context context;
    private Dialog dialog;
    public Proses(Context context) {
        this.context=context;
        dialog= new Dialog(context);
        dialog.setContentView(R.layout.loading);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);
    }

    public void ShowDialog() {
        dialog.show();
    }
    public void DismissDialog(){
        dialog.dismiss();
    }

}
