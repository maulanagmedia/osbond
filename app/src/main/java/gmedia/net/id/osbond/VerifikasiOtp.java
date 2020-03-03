package gmedia.net.id.osbond;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.CountDownTimer;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VerifikasiOtp extends AppCompatActivity {
    private TextView txtTimeReverse, txtOTP;
    public static final String OTP_REGEX = "[0-9]{1,4}";
    private String otp = "", noTelp = "", expired = "";
    private long menit;
    private Button btnVerifikasi, btnKirimUlang;
    private CountDownTimer countDownTimer;
    private EditText et1, et2, et3, et4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verifikasi_otp);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Verifikasi");
        actionBar.setDisplayHomeAsUpEnabled(true);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            noTelp = (String) bundle.get("noTelp");
            expired = (String) bundle.get("expired");
        }
        menit = Long.parseLong(expired);
        initUI();
        initAction();
    }

    private void initUI() {
        txtTimeReverse = (TextView) findViewById(R.id.txtCountDownMinutes);
        btnVerifikasi = (Button) findViewById(R.id.btnVerifikasiOTP);
        btnKirimUlang = (Button) findViewById(R.id.btnKirimUlangNoTelp);
//        txtOTP = (TextView) findViewById(R.id.txtOTP);
        et1 = (EditText) findViewById(R.id.editTeksOTP1);
        et2 = (EditText) findViewById(R.id.editTeksOTP2);
        et3 = (EditText) findViewById(R.id.editTeksOTP3);
        et4 = (EditText) findViewById(R.id.editTeksOTP4);
    }

    private void initAction() {
        //penghitung waktu mundur (setingan milisecond)
        countDownTimer = new CountDownTimer(menit * 60 * 1000, 1000) { // adjust the milli seconds here

            public void onTick(long millisUntilFinished) {
                txtTimeReverse.setText("" + String.format("0%d:%d",
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
            }

            public void onFinish() {
                txtTimeReverse.setText("OTP is expired!");
            }
        }.start();
        et1.addTextChangedListener(new GenericTextWatcher(et1));
        et2.addTextChangedListener(new GenericTextWatcher(et2));
        et3.addTextChangedListener(new GenericTextWatcher(et3));
        et4.addTextChangedListener(new GenericTextWatcher(et4));

        btnVerifikasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(VerifikasiOtp.this);
                dialog.setContentView(R.layout.loading);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
                String OTPfromEditText = et1.getText().toString() + et2.getText().toString() + et3.getText().toString() + et4.getText().toString();
                final JSONObject jBody = new JSONObject();
                try {
                    jBody.put("otp", OTPfromEditText);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ApiVolley request = new ApiVolley(VerifikasiOtp.this, jBody, "POST", URL.urlValidasiOTP, "", "", 0, new ApiVolley.VolleyCallback() {
                    @Override
                    public void onSuccess(String result) {
                        dialog.dismiss();
                        try {
                            JSONObject object = new JSONObject(result);
                            String status = object.getJSONObject("metadata").getString("status");
                            String message = object.getJSONObject("metadata").getString("message");
                            if (status.equals("200")) {
                                Toast.makeText(VerifikasiOtp.this, message, Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(VerifikasiOtp.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(VerifikasiOtp.this, message, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(String result) {
                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Terjadi Kesalahan", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
        btnKirimUlang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(VerifikasiOtp.this);
                dialog.setContentView(R.layout.loading);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
                final JSONObject jBody = new JSONObject();
                try {
                    jBody.put("nomor", noTelp);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ApiVolley request = new ApiVolley(VerifikasiOtp.this, jBody, "POST", URL.urlKirimOTP, "", "", 0, new ApiVolley.VolleyCallback() {
                    @Override
                    public void onSuccess(String result) {
                        dialog.dismiss();
                        try {
                            JSONObject object = new JSONObject(result);
                            String status = object.getJSONObject("metadata").getString("status");
                            String message = object.getJSONObject("metadata").getString("message");
                            if (status.equals("200")) {
                                Toast.makeText(VerifikasiOtp.this, message, Toast.LENGTH_LONG).show();
                                countDownTimer.cancel();
                                countDownTimer = new CountDownTimer(2 * 60 * 1000, 1000) { // adjust the milli seconds here

                                    public void onTick(long millisUntilFinished) {
                                        txtTimeReverse.setText("" + String.format("0%d:%d",
                                                TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                                                TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
                                    }

                                    public void onFinish() {
                                        txtTimeReverse.setText("OTP is expired!");
                                    }
                                }.start();
                            } else {
                                Toast.makeText(VerifikasiOtp.this, message, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(String result) {
                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Terjadi Kesalahan", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equalsIgnoreCase("otp")) {
                final String message = intent.getStringExtra("message");
                Log.d("pesan", message);
                Pattern pattern = Pattern.compile(OTP_REGEX);
                Matcher matcher = pattern.matcher(message);
                while (matcher.find()) {
                    otp = matcher.group();
                }
                Log.d("otp anda", otp);
//                Toast.makeText(VerifikasiOtp.this, "noTelp: " + noTelp, Toast.LENGTH_LONG).show();
                //pemanggil textWatcher Edit Text
                et1.setText(otp.substring(0));
                et2.setText(otp.substring(1));
                et3.setText(otp.substring(2));
                et4.setText(otp.substring(3));
            }
        }
    };

    /*@Override
    public void onResume() {
        LocalBroadcastManager.getInstance(this).
                registerReceiver(receiver, new IntentFilter("otp"));
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public class GenericTextWatcher implements TextWatcher {
        private View view;

        private GenericTextWatcher(View view) {
            this.view = view;
        }

        @Override
        public void afterTextChanged(Editable editable) {
            // TODO Auto-generated method stub
            String text = editable.toString();
            switch (view.getId()) {
                case R.id.editTeksOTP1:
                    if (text.length() == 1) {
                        et2.requestFocus();
                        break;
                    } else if (text.length() == 0) {
                        break;
                    }
                case R.id.editTeksOTP2:

                    if (text.length() == 1) {
                        et3.requestFocus();
                        break;
                    } else if (text.length() == 0) {
                        et1.requestFocus();
                        et1.setSelection(et1.getText().length());
                        break;
                    }
                case R.id.editTeksOTP3:

                    if (text.length() == 1) {
                        et4.requestFocus();
                        break;
                    } else if (text.length() == 0) {
                        et2.requestFocus();
                        et2.setSelection(et2.getText().length());
                        break;
                    }

                case R.id.editTeksOTP4:
                    if (text.length() == 0) {
                        et3.requestFocus();
                        et3.setSelection(et3.getText().length());
                        break;
                    }
                    et4.setSelection(et4.getText().length());
            }
        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            // TODO Auto-generated method stub

        }
    }
}