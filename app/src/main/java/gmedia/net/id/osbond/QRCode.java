package gmedia.net.id.osbond;

import android.app.Dialog;
import android.content.Intent;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

//import scanner.IntentIntegrator;
//import scanner.IntentResult;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

public class QRCode extends AppCompatActivity {

    private String linkID;
    private ImageView imgQRCode;
    private Dialog dialog;
    private Proses proses;
    private Button btnKirim;
    public static IntentResult resultScanBarcode;
    private String idKupon = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qrcode);
        initUI();
        initAction();
    }

    private void initUI() {
        proses = new Proses(QRCode.this);
        imgQRCode = (ImageView) findViewById(R.id.imgQRCode);
        btnKirim = (Button) findViewById(R.id.btnKirim);
    }

    private void initAction() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("QR CODE");
        actionBar.setDisplayHomeAsUpEnabled(true);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            idKupon = bundle.getString("id", "");
            if (idKupon.equals("")) {
                Log.d("error ID", "uid kosong");
                Toast.makeText(this, "error ID", Toast.LENGTH_LONG).show();
            }
        } else {
            Log.d("error ID", "bundle kosong");
        }
        prepareDataQRCode();
        btnKirim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openScanBarcode();
            }
        });
    }

    private void openScanBarcode() {

        IntentIntegrator integrator = new IntentIntegrator(QRCode.this);
        integrator.setOrientationLocked(true);
        //integrator.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES);
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        resultScanBarcode = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (resultScanBarcode != null) {

            if (resultScanBarcode.getContents() == null) {
                Toast.makeText(getApplicationContext(), "Silahkan Scan Ulang", Toast.LENGTH_LONG).show();
            } else {
                prepareDataKirim();
            }
        } else {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void prepareDataKirim() {
        proses.ShowDialog();
        Log.d("isiID", idKupon);
        Log.d("isiToken", resultScanBarcode.getContents());
        final JSONObject jBody = new JSONObject();
        try {
            jBody.put("id_kupon", idKupon);
            jBody.put("uid_user", resultScanBarcode.getContents());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ApiVolley request = new ApiVolley(QRCode.this, jBody, "POST", URL.urlKirimVoucher, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                proses.DismissDialog();
                try {
                    JSONObject object = new JSONObject(result);
                    String status = object.getJSONObject("metadata").getString("status");
                    String message = object.getJSONObject("metadata").getString("message");
                    if (status.equals("200")) {
                        Toast.makeText(QRCode.this, "Berhasil", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(QRCode.this, message, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String result) {
                proses.DismissDialog();
                Toast.makeText(QRCode.this, "Terjadi Kesalahan", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void prepareDataQRCode() {
        proses.ShowDialog();
       /* Bundle save = getIntent().getExtras();
        if (save != null) {
            linkID = save.getString("id", "");
        }*/
        final JSONObject jBody = new JSONObject();
        try {
            jBody.put("id_kupon", idKupon);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ApiVolley request = new ApiVolley(QRCode.this, jBody, "POST", URL.QRCode, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                proses.DismissDialog();
                try {
                    JSONObject object = new JSONObject(result);
                    String status = object.getJSONObject("metadata").getString("status");
                    if (status.equals("200")) {
                        JSONObject response = object.getJSONObject("response");
                        Picasso.with(QRCode.this).load(response.getString("url")).resize(512, 512)
                                .into(imgQRCode);
//                        Toast.makeText(getApplicationContext(),linkID,Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String result) {
                proses.DismissDialog();
                Toast.makeText(getApplicationContext(), "Terjadi Kesalahan", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            /*case R.id.settings:
                onBackPressed();
                return true;*/
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /*MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);*/
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
