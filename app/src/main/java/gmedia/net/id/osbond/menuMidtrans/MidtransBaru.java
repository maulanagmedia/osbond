package gmedia.net.id.osbond.menuMidtrans;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.midtrans.sdk.corekit.callback.TransactionFinishedCallback;
import com.midtrans.sdk.corekit.core.MidtransSDK;
import com.midtrans.sdk.corekit.core.TransactionRequest;
import com.midtrans.sdk.corekit.core.themes.CustomColorTheme;
import com.midtrans.sdk.corekit.models.BankType;
import com.midtrans.sdk.corekit.models.CustomerDetails;
import com.midtrans.sdk.corekit.models.ItemDetails;
import com.midtrans.sdk.corekit.models.snap.CreditCard;
import com.midtrans.sdk.corekit.models.snap.TransactionResult;
import com.midtrans.sdk.uikit.SdkUIFlowBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import gmedia.net.id.osbond.ApiVolley;
import gmedia.net.id.osbond.MainActivity;
import gmedia.net.id.osbond.URL;

public class MidtransBaru extends AppCompatActivity implements TransactionFinishedCallback {
    private Button buttonUiKit;
    private String harga;
    private long hargaLong;
    private int hargaInt, jumlahPaketInt, jumlahInt;
    private ArrayList<ItemDetails> itemDetailsArrayList;
    private String posisiCabang, namaPaket, jumlahPaket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle intentExtra = getIntent().getExtras();
        if (intentExtra != null) {
            hargaInt = intentExtra.getInt("harga");
            namaPaket = intentExtra.getString("nama paket");
            jumlahPaket = intentExtra.getString("jumlah");
            jumlahInt = Integer.parseInt(jumlahPaket);
            hargaLong = hargaInt * jumlahInt;
            jumlahPaketInt = Integer.parseInt(jumlahPaket);

//            hargaInt = Integer.parseInt(harga);
        }
        // SDK initiation for UIflow
        String client_key = SdkConfig.MERCHANT_CLIENT_KEY;
        String base_url = SdkConfig.MERCHANT_BASE_CHECKOUT_URL;

        SdkUIFlowBuilder.init()
                .setClientKey(client_key) // client_key is mandatory
                .setContext(this) // context is mandatory
                .setTransactionFinishedCallback(this) // set transaction finish callback (sdk callback)
                .setMerchantBaseUrl(base_url) //set merchant url
                .enableLog(true) // enable sdk log
                .setColorTheme(new CustomColorTheme("#FFE51255", "#B61548", "#FFE51255")) // will replace theme on snap theme on MAP
                .buildSDK();
        MidtransSDK.getInstance().setTransactionRequest(initTransactionRequest());
        MidtransSDK.getInstance().startPaymentUiFlow(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private TransactionRequest initTransactionRequest() {

        // Create new Transaction Request
        TransactionRequest transactionRequestNew = new
                TransactionRequest(System.currentTimeMillis() + "", hargaLong);

        //set customer details
        transactionRequestNew.setCustomerDetails(initCustomerDetails());


        // set item details
        ItemDetails itemDetails = new ItemDetails("1", hargaInt, jumlahPaketInt, namaPaket);

        // Add item details into item detail list.
        itemDetailsArrayList = new ArrayList<>();
        itemDetailsArrayList.add(itemDetails);
        transactionRequestNew.setItemDetails(itemDetailsArrayList);


        // Create creditcard options for payment
        CreditCard creditCard = new CreditCard();

        creditCard.setSaveCard(false); // when using one/two click set to true and if normal set to  false

//        this methode deprecated use setAuthentication instead
//        creditCard.setSecure(true); // when using one click must be true, for normal and two click (optional)

        creditCard.setAuthentication(CreditCard.AUTHENTICATION_TYPE_3DS);

        // noted !! : channel migs is needed if bank type is BCA, BRI or MyBank
//        creditCard.setChannel(CreditCard.MIGS); //set channel migs
        creditCard.setBank(BankType.BCA); //set spesific acquiring bank

        transactionRequestNew.setCreditCard(creditCard);

        return transactionRequestNew;
    }

    private CustomerDetails initCustomerDetails() {
        //define customer detail (mandatory for coreflow)
        CustomerDetails mCustomerDetails = new CustomerDetails();
        mCustomerDetails.setPhone("085310102020");
        mCustomerDetails.setFirstName("Bayu Wicaksono");
        mCustomerDetails.setEmail("bayuw1995@gmail.com");
        return mCustomerDetails;
    }

    @Override
    public void onTransactionFinished(TransactionResult result) {
        if (result.getResponse() != null) {
            switch (result.getStatus()) {
                case TransactionResult.STATUS_SUCCESS:
//                    Toast.makeText(this, "Transaction Finished. ID: " + result.getResponse().getTransactionId(), Toast.LENGTH_LONG).show();
                    String status = result.getResponse().getStatusCode();
                    if (status.equals("200")) {
                        if (MainActivity.posisiPaket == 2) {
                            posisiCabang = MainActivity.idCabang;
                        } else {
                            posisiCabang = "0";
                        }
                        String jumlah = MainActivity.jumlahKuponDoku.getText().toString();
                        final JSONObject jBody = new JSONObject();
                        try {
                            jBody.put("paket", String.valueOf(MainActivity.posisiPaket));
                            jBody.put("cabang", posisiCabang);
                            jBody.put("jumlah", jumlah);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        ApiVolley request = new ApiVolley(MidtransBaru.this, jBody, "POST", URL.urlTopUpMidtrans, "", "", 0, new ApiVolley.VolleyCallback() {
                            @Override
                            public void onSuccess(String result) {
                                try {
                                    JSONObject object = new JSONObject(result);
                                    String status = object.getJSONObject("metadata").getString("status");
                                    String message = object.getJSONObject("metadata").getString("message");
                                    if (status.equals("200")) {
                                        Toast.makeText(MidtransBaru.this, message, Toast.LENGTH_LONG).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onError(String result) {
                                Toast.makeText(getApplicationContext(), "Terjadi Kesalahan", Toast.LENGTH_LONG).show();
                            }
                        });
                    }

                    Intent intent = new Intent(MidtransBaru.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                case TransactionResult.STATUS_PENDING:
                    Toast.makeText(this, "Transaction Pending. ID: " + result.getResponse().getTransactionId(), Toast.LENGTH_LONG).show();
                    break;
                case TransactionResult.STATUS_FAILED:
                    Toast.makeText(this, "Transaction Failed. ID: " + result.getResponse().getTransactionId() + ". Message: " + result.getResponse().getStatusMessage(), Toast.LENGTH_LONG).show();
                    break;
            }
            result.getResponse().getValidationMessages();
        } else if (result.isTransactionCanceled()) {
            Toast.makeText(this, "Transaction Canceled", Toast.LENGTH_LONG).show();
        } else {
            if (result.getStatus().equalsIgnoreCase(TransactionResult.STATUS_INVALID)) {
                Toast.makeText(this, "Transaction Invalid", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Transaction Finished with failure.", Toast.LENGTH_LONG).show();
            }
        }
    }
}
