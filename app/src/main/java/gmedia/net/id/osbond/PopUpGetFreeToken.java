package gmedia.net.id.osbond;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class PopUpGetFreeToken {
    private Context context;
    private Dialog dialogFreeToken;

    public PopUpGetFreeToken(Context context) {
        this.context = context;
        dialogFreeToken = new Dialog(context);
        dialogFreeToken.setContentView(R.layout.pop_up_kupon_gratis_baru);
        dialogFreeToken.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogFreeToken.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialogFreeToken.setCanceledOnTouchOutside(false);
        MainActivity.inputan = (EditText) dialogFreeToken.findViewById(R.id.inputanNoHPopupGratisToken);
//        MainActivity.inputan.setText("");
        RelativeLayout btnOke = (RelativeLayout) dialogFreeToken.findViewById(R.id.btnOkePopupGratisToken);
        btnOke.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MainActivity.inputan.getText().toString().equals("")) {
                    MainActivity.inputan.setError("Mohon di isi");
                    MainActivity.inputan.requestFocus();
                    return;
                } else {
                    MainActivity.showPopUpGetFreeToken = true;
                    prepareSendOTP();
                    dialogFreeToken.dismiss();
                }
            }
        });
        ImageView btnSkip = (ImageView) dialogFreeToken.findViewById(R.id.skip);
        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogFreeToken.dismiss();
            }
        });
    }

    public void ShowDialog() {
        dialogFreeToken.show();
    }

    public void DismissDialog() {
        dialogFreeToken.dismiss();
    }

    private void prepareSendOTP() {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.loading);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        final JSONObject jBody = new JSONObject();
        try {
            jBody.put("nomor", MainActivity.inputan.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ApiVolley request = new ApiVolley(context, jBody, "POST", URL.urlKirimOTP, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                dialog.dismiss();
                try {
                    JSONObject object = new JSONObject(result);
                    String status = object.getJSONObject("metadata").getString("status");
                    String message = object.getJSONObject("metadata").getString("message");
                    if (status.equals("200")) {
                        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(context, VerifikasiOtp.class);
                        intent.putExtra("noTelp", MainActivity.inputan.getText().toString());
                        intent.putExtra("expired", object.getJSONObject("response").getString("expired"));
                        ((Activity) context).startActivity(intent);
                    } else {
                        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String result) {
                dialog.dismiss();
                Toast.makeText(context, "Terjadi Kesalahan", Toast.LENGTH_LONG).show();
            }
        });
    }
}
