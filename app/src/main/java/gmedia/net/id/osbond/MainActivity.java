package gmedia.net.id.osbond;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.midtrans.sdk.corekit.callback.TransactionFinishedCallback;
import com.midtrans.sdk.corekit.core.LocalDataHandler;
import com.midtrans.sdk.corekit.core.MidtransSDK;
import com.midtrans.sdk.corekit.core.TransactionRequest;
import com.midtrans.sdk.corekit.core.themes.CustomColorTheme;
import com.midtrans.sdk.corekit.models.BankType;
import com.midtrans.sdk.corekit.models.CustomerDetails;
import com.midtrans.sdk.corekit.models.ItemDetails;
import com.midtrans.sdk.corekit.models.UserAddress;
import com.midtrans.sdk.corekit.models.UserDetail;
import com.midtrans.sdk.corekit.models.snap.CreditCard;
import com.midtrans.sdk.corekit.models.snap.TransactionResult;
import com.midtrans.sdk.uikit.SdkUIFlowBuilder;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cz.intik.overflowindicator.OverflowPagerIndicator;
import cz.intik.overflowindicator.SimpleSnapHelper;
import gmedia.net.id.osbond.EKupon.EKupon;
import gmedia.net.id.osbond.ImageSlider.Custom;
import gmedia.net.id.osbond.ImageSlider.ImageSliderAdapter;
import gmedia.net.id.osbond.ImageSlider.MyViewPager;
import gmedia.net.id.osbond.MasterPaketDoku.RecyclerViewDokuAdapter;
import gmedia.net.id.osbond.menuMidtrans.SetGetMasterPaketBaru;
import gmedia.net.id.osbond.menuMidtrans.SdkConfig;
import me.relex.circleindicator.CircleIndicator;

public class MainActivity extends RuntimePermissionsActivity implements TransactionFinishedCallback {
    //    private static ViewPager mPager;
    private static int currentPage = 0;
    //    private static final Integer[] XMEN = {R.drawable.promo_slide, R.drawable.foto_profile_coba, R.drawable.promo_slide, R.drawable.kupon_merah};
    private ArrayList<Custom> imageSlider;
    private MyViewPager sliderView;
    private RelativeLayout menuProfile, menuEKupon, menuQRUser, menuPayment;
    private CircleIndicator indicator;
    private static final int REQUEST_PERMISSIONS = 20;
    private Proses proses;
    private boolean doubleBackToExitPressedOnce = false;
    private Spinner dropdownPilihKupon, dropdownPilihJam;
    public static EditText inputan;
    private List<SetGetMasterPaket> masterPaket;
    private List<SetGetMasterCabang> masterCabang;
    private ArrayAdapter<SetGetMasterCabang> adapterCabang;
    private ArrayAdapter<SetGetMasterPaket> adapterPaket;
    private String invoiceNumber;
    private JSONObject respongetTokenSDK;
    private String jsonRespon;
    private TelephonyManager telephonyManager;
    private String version, latestVersion, link;
    private RecyclerView recyclerView;
    private RecyclerViewDokuAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<SetGetMasterPaketBaru> menuPaket;
    private int gambar[] =
            {
                    R.drawable.paket_gold,
                    R.drawable.paket_silver,
                    R.drawable.paket_gold,
                    R.drawable.paket_silver
            };
    private String paket[] =
            {
                    "GOLD PACKAGES",
                    "SILVER PACKAGES",
                    "GOLD PACKAGES",
                    "SILVER PACKAGES",
            };
    private String keterangan[] =
            {
                    "Bisa digunakan ke semua cabang",
                    "Hanya Bisa Digunakan ke Cabang Tertentu",
                    "Bisa digunakan ke semua cabang",
                    "Hanya Bisa Digunakan ke Cabang Tertentu"
            };
    private String harga[] =
            {
                    "Rp 70.000",
                    "Rp 40.000",
                    "Rp 70.000",
                    "Rp 40.000"
            };
    public static LinearLayout layoutDPCabang;
    public static boolean kosong = false;
    public static SetGetMasterPaketBaru selectedPaket;
    public static EditText jumlahKuponDoku;
    private ChangeToRupiah changeToRupiah;
    private int nomSubTotalInt;
    public static String idCabang = "0";
    public static String idPaket = "0";
    public static int posisiPaket = 0;
    private String namaPaket, jumlahPaket;
    private ArrayList<ItemDetails> itemDetailsArrayList;
    private String token;
    public static int state = 0;
    private String getNotif = "", getRegister = "";
    public static boolean fromNotifToEKupon = false;
    private String idPembelianMidtrans = "", namaPaketPembelianMidtrans = "", hargaPembelianMidtrans = "";
    private String namaMidtrans = "", emailMidtrans = "", noTelpMidtrans = "", settingFreeToken = "", isVerifyFreeToken = "";
    public static Boolean showPopUpGetFreeToken = false;
    private PopUpGetFreeToken popUpGetFreeToken;
    private boolean updateRequired;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(MainActivity.this);
        FirebaseMessaging.getInstance().subscribeToTopic("osbond");
        token = FirebaseInstanceId.getInstance().getToken();
//        Log.d("token", token);
        proses = new Proses(this);
        popUpGetFreeToken = new PopUpGetFreeToken(this);
        changeToRupiah = new ChangeToRupiah();
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                /*ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED ||*/
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
//            SettingPromo.openCamera.setEnabled(true);
            super.requestAppPermissions(new
                            String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            /*Manifest.permission.RECEIVE_SMS,
                            Manifest.permission.READ_SMS,*/
                            Manifest.permission.READ_PHONE_STATE},
                    R.string.runtime_permissions_txt, REQUEST_PERMISSIONS);
        }
        menuProfile = (RelativeLayout) findViewById(R.id.profile);
        menuEKupon = (RelativeLayout) findViewById(R.id.eKupon);
        menuQRUser = (RelativeLayout) findViewById(R.id.QRUser);
        menuPayment = (RelativeLayout) findViewById(R.id.menuDOKU);
        sliderView = (MyViewPager) findViewById(R.id.pager);
        indicator = (CircleIndicator) findViewById(R.id.indicator);
        imageSlider();
        sliderView.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentPage = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        menuProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Profile.class);
                startActivity(intent);
            }
        });
        menuEKupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EKupon.class);
                startActivity(intent);
            }
        });
        menuQRUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, QRUser.class);
                startActivity(intent);
            }
        });
        menuPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(MainActivity.this);
                dialog.setContentView(R.layout.popup_menu_doku_baru);
                dropdownPilihKupon = (Spinner) dialog.findViewById(R.id.dropdownJenisKuponEmail);
                dropdownPilihJam = (Spinner) dialog.findViewById(R.id.dropdownJumlahJamEmail);
                RelativeLayout btnSave = (RelativeLayout) dialog.findViewById(R.id.btnSaveMidtrans);
//                RelativeLayout btnCancel = (RelativeLayout) dialog.findViewById(R.id.btnCancel);
                layoutDPCabang = (LinearLayout) dialog.findViewById(R.id.layoutDropdownCabang);
//                inputan = (EditText) dialog.findViewById(R.id.inputanEmail);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.setCanceledOnTouchOutside(false);
                layoutManager = new LinearLayoutManager(MainActivity.this, LinearLayout.HORIZONTAL, false);
                recyclerView = (RecyclerView) dialog.findViewById(R.id.rvView);
                recyclerView.setLayoutManager(layoutManager);
                final OverflowPagerIndicator overflowPagerIndicator = dialog.findViewById(R.id.indicatorPopupMenu);
                jumlahKuponDoku = (EditText) dialog.findViewById(R.id.inputanJumlahKuponDoku);
                ApiVolley requestPaket = new ApiVolley(MainActivity.this, new JSONObject(), "GET", URL.MasterPaket, "", "", 0, new ApiVolley.VolleyCallback() {
                    @Override
                    public void onSuccess(String result) {
                        menuPaket = new ArrayList<>();
                        try {
                            JSONObject object = new JSONObject(result);
                            String status = object.getJSONObject("metadata").getString("status");
                            if (status.equals("200")) {
                                JSONArray response = object.getJSONArray("response");
                                for (int i = 0; i < response.length(); i++) {
                                    JSONObject isi = response.getJSONObject(i);
                                    menuPaket.add(new SetGetMasterPaketBaru(
                                            isi.getString("id"),
                                            isi.getString("image"),
                                            isi.getString("label"),
                                            isi.getString("keterangan"),
                                            isi.getString("harga")
                                    ));
                                }
                                recyclerView.setAdapter(null);
                                adapter = new RecyclerViewDokuAdapter(MainActivity.this, menuPaket);
                                recyclerView.setAdapter(adapter);
                                overflowPagerIndicator.attachToRecyclerView(recyclerView);
                                new SimpleSnapHelper(overflowPagerIndicator).attachToRecyclerView(recyclerView);
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
                ApiVolley requestCabang = new ApiVolley(MainActivity.this, new JSONObject(), "GET", URL.MasterCabang, "", "", 0, new ApiVolley.VolleyCallback() {
                    @Override
                    public void onSuccess(String result) {
                        masterCabang = new ArrayList<>();
                        try {
                            JSONObject object = new JSONObject(result);
                            String status = object.getJSONObject("metadata").getString("status");
                            if (status.equals("200")) {
                                JSONArray response = object.getJSONArray("response");
                                for (int i = 0; i < response.length(); i++) {
                                    JSONObject isiMasterCabang = response.getJSONObject(i);
                                    masterCabang.add(new SetGetMasterCabang(
                                            isiMasterCabang.getString("id"),
                                            isiMasterCabang.getString("cabang")
                                    ));
                                }
//                                android.R.layout.simple_spinner_item
                                adapterCabang = new ArrayAdapter<SetGetMasterCabang>(MainActivity.this, R.layout.layout_simple_list, masterCabang);
                                dropdownPilihJam.setAdapter(adapterCabang);
//                                dropdownPilihKupon.setSelection(0, true);
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
                /*btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });*/
                btnSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (kosong) {
                            Toast.makeText(getApplicationContext(), "Silahkan Pilih Paket", Toast.LENGTH_LONG).show();
                            return;
                        } else if (jumlahKuponDoku.getText().toString().equals("")) {
                            jumlahKuponDoku.setError("Mohon Di Isi");
                            jumlahKuponDoku.requestFocus();
                            return;
                        }
                        dialog.dismiss();
                        try {
                            prepareGetDataTopUp();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
//                        prepareGetDataTopUp();
                    }
                });
                dialog.show();
            }
        });
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            getRegister = (String) bundle.get("register");
            getNotif = bundle.getString("notif", "");
            if (getRegister == null) {
                if (getNotif == null) {

                } else {
                    state = 1;
                    Intent intentNotif = new Intent(this, EKupon.class);
                    startActivity(intentNotif);
                }

            } else {
                getRegister = "";
                getOneKuponGold(getRegister);

            }
        }
        getFcmId();
//        getFreeToken();

    }

    private void getFreeToken() {
//        final ArrayList<ModelGetNoTelp> noTelp = new ArrayList<>();
//        final ModelGetNoTelp modelGetNoTelp = null;
        ApiVolley request = new ApiVolley(MainActivity.this, new JSONObject(), "GET", URL.urlKuponGratis, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject object = new JSONObject(result);
                    String status = object.getJSONObject("metadata").getString("status");
                    String message = object.getJSONObject("metadata").getString("message");
                    if (status.equals("200")) {
                        JSONObject response = object.getJSONObject("response");
                        settingFreeToken = response.getString("setting");
                        isVerifyFreeToken = response.getString("verify");
                        Log.d("getFreeToken", settingFreeToken);
                        if (settingFreeToken.equals("1")) {
                            if (isVerifyFreeToken.equals("0")) {
                                MainActivity.inputan.setText("");
                                popUpGetFreeToken.ShowDialog();
                            }
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
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


    @Override
    protected void onResume() {
        super.onResume();
        checkVersion();
        getFreeToken();
        FirebaseApp.initializeApp(MainActivity.this);
        if (state == 1) {
            state = 0;
            if (getNotif.equals("1")) {
                getNotif = "";
                fromNotifToEKupon = true;
                Intent intent = new Intent(MainActivity.this, EKupon.class);
                startActivity(intent);
            }
        }
    }


    private void getFcmId() {
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.loading);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        final JSONObject jBody = new JSONObject();
        try {
            jBody.put("fcm_id", token);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ApiVolley request = new ApiVolley(MainActivity.this, jBody, "POST", URL.urlUpdateFcm, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                dialog.dismiss();
                try {
                    JSONObject object = new JSONObject(result);
                    String status = object.getJSONObject("metadata").getString("status");
                    String message = object.getJSONObject("metadata").getString("message");
                    if (status.equals("200")) {
                        Log.d("token", token);
                    } else {
                        Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String result) {
                dialog.dismiss();
                Toast.makeText(MainActivity.this, "fcm_id not refreshed", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void prepareGetDataTopUp() {
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.loading);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        idPaket = "0";
        idCabang = "0";
        /*if (selectedPaket == null) {
            Toast.makeText(MainActivity.this, "coba lagi", Toast.LENGTH_LONG).show();
            return;
        }*/
        idPaket = selectedPaket.getId();
        if (idPaket.equals("1")) {
            idCabang = "0";
        } else {
            SetGetMasterCabang setGetMasterCabang = (SetGetMasterCabang) dropdownPilihJam.getSelectedItem();
            idCabang = setGetMasterCabang.getId();
        }
        final JSONObject jBody = new JSONObject();
        try {
            jBody.put("id_paket", idPaket);
            jBody.put("id_cabang", idCabang);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ApiVolley request = new ApiVolley(MainActivity.this, jBody, "POST", URL.urlGetDataTopUp, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                dialog.dismiss();
                try {
                    JSONObject object = new JSONObject(result);
                    String status = object.getJSONObject("metadata").getString("status");
                    String message = object.getJSONObject("metadata").getString("message");
                    if (status.equals("200")) {
                        JSONObject response = object.getJSONObject("response");
                        idPembelianMidtrans = response.getString("id");
                        namaPaketPembelianMidtrans = response.getString("name");
                        hargaPembelianMidtrans = response.getString("price");
                        popupVerifikasiDoku();
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

    private void popupVerifikasiDoku() {
        proses.ShowDialog();
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.popup_verifikasi_doku);
        ImageView icon = (ImageView) dialog.findViewById(R.id.iconVerifDoku);
        TextView paket = (TextView) dialog.findViewById(R.id.txtPaketVerifDoku);
        TextView keterangan = (TextView) dialog.findViewById(R.id.txtKeteranganVerifDoku);
        TextView harga = (TextView) dialog.findViewById(R.id.txtHargaVerifDoku);
        TextView jumlahKupon = (TextView) dialog.findViewById(R.id.txtJumlahKuponVerifDoku);
        final TextView subtotal = (TextView) dialog.findViewById(R.id.subTotalVerifDoku);
        RelativeLayout bayar = (RelativeLayout) dialog.findViewById(R.id.goToPembayaranDoku);
        if (selectedPaket != null) {
            Picasso.with(MainActivity.this).load(selectedPaket.getIcon()).into(icon);
            paket.setText(namaPaketPembelianMidtrans);
            keterangan.setText(selectedPaket.getKeterangan());
            harga.setText(changeToRupiah.ChangeToRupiahFormat(hargaPembelianMidtrans));
            jumlahKupon.setText(jumlahKuponDoku.getText().toString());
            double nomHarga = changeToRupiah.parseNullDouble(selectedPaket.getHarga());
            double nomJumlahKupon = changeToRupiah.parseNullDouble(jumlahKupon.getText().toString());
            double nomSubTotal = nomHarga * nomJumlahKupon;
//            nomSubTotalInt = (int) nomSubTotal;
            subtotal.setText(changeToRupiah.ChangeToRupiahFormat(changeToRupiah.doubleToStringFull(nomSubTotal)));
            namaPaket = paket.getText().toString();
            jumlahPaket = jumlahKupon.getText().toString();
            nomSubTotalInt = Integer.parseInt(selectedPaket.getHarga());
        }
        bayar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                prepareGetDataProfile();
            }
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        proses.DismissDialog();
    }

    private TransactionRequest initTransactionRequest() {

        long hargaLong;
        int jumlahInt;
        jumlahInt = Integer.parseInt(jumlahPaket);
        hargaLong = nomSubTotalInt * jumlahInt;
        // Create new Transaction Request
        TransactionRequest transactionRequestNew = new
                TransactionRequest(System.currentTimeMillis() + "", hargaLong);
        //set customer details
        transactionRequestNew.setCustomerDetails(initCustomerDetails());


        // set item details
        ItemDetails itemDetails = new ItemDetails(idPembelianMidtrans, nomSubTotalInt, jumlahInt, namaPaket);

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

    private void prepareGetDataProfile() {
        proses.ShowDialog();
        ApiVolley request = new ApiVolley(MainActivity.this, new JSONObject(), "GET", URL.ViewProfile, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                proses.DismissDialog();
                try {
                    JSONObject object = new JSONObject(result);
                    String status = object.getJSONObject("metadata").getString("status");
                    String message = object.getJSONObject("metadata").getString("message");
                    if (status.equals("200")) {
                        JSONObject response = object.getJSONObject("response");
                        namaMidtrans = response.getString("profile_name");
                        emailMidtrans = response.getString("email");
                        noTelpMidtrans = response.getString("no_telp");
                        String client_key = SdkConfig.MERCHANT_CLIENT_KEY;
                        String base_url = SdkConfig.MERCHANT_BASE_CHECKOUT_URL;
                        SdkUIFlowBuilder.init()
                                .setClientKey(client_key) // client_key is mandatory
                                .setContext(MainActivity.this) // context is mandatory
                                .setTransactionFinishedCallback(MainActivity.this) // set transaction finish callback (sdk callback)
                                .setMerchantBaseUrl(base_url) //set merchant url
                                .enableLog(true) // enable sdk log
                                .setColorTheme(new CustomColorTheme("#FFE51255", "#B61548", "#FFE51255")) // will replace theme on snap theme on MAP
                                .buildSDK();
                        MidtransSDK.getInstance().setTransactionRequest(initTransactionRequest());
                        MidtransSDK.getInstance().startPaymentUiFlow(MainActivity.this);
                    } else {
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
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

    private CustomerDetails initCustomerDetails() {
        //define customer detail (mandatory for coreflow)
        UserDetail userDetail = LocalDataHandler.readObject("user_details", UserDetail.class);
        if (userDetail == null) {
            userDetail = new UserDetail();
            userDetail.setUserFullName(namaMidtrans);
            userDetail.setEmail(emailMidtrans);
            userDetail.setPhoneNumber(noTelpMidtrans);
            // set user ID as identifier of saved card (can be anything as long as unique),
            // randomly generated by SDK if not supplied
            userDetail.setUserId("budi-6789");
            ArrayList<UserAddress> userAddresses = new ArrayList<>();
            UserAddress userAddress = new UserAddress();
            userAddress.setAddress("Jalan Andalas Gang Sebelah No. 1");
            userAddress.setCity("Jakarta");
            userAddress.setAddressType(com.midtrans.sdk.corekit.core.Constants.ADDRESS_TYPE_BOTH);
            userAddress.setZipcode("12345");
            userAddress.setCountry("IDN");
            userAddresses.add(userAddress);
            userDetail.setUserAddresses(userAddresses);
            LocalDataHandler.saveObject("user_details", userDetail);
        }
        CustomerDetails mCustomerDetails = new CustomerDetails();
        mCustomerDetails.setPhone("085310102020");
        mCustomerDetails.setFirstName("Budi Laksono");
        mCustomerDetails.setEmail("example@gmail.com");
        return mCustomerDetails;
    }


    @Override
    public void onTransactionFinished(TransactionResult result) {
        if (result.getResponse() != null) {
            switch (result.getStatus()) {
                case TransactionResult.STATUS_SUCCESS:
                    Toast.makeText(this, "Transaction Finished", Toast.LENGTH_LONG).show();
                    /*String status = result.getResponse().getStatusCode();
                    if (status.equals("200")) {
                        if (posisiPaket == 2) {
                            idCabang = idCabang;
                        } else {
                            idCabang = "0";
                        }
                        String jumlah = jumlahKuponDoku.getText().toString();
                        final JSONObject jBody = new JSONObject();
                        try {
                            jBody.put("paket", String.valueOf(posisiPaket));
                            jBody.put("cabang", idCabang);
                            jBody.put("jumlah", jumlah);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        ApiVolley request = new ApiVolley(MainActivity.this, jBody, "POST", URL.urlTopUpMidtrans, "", "", 0, new ApiVolley.VolleyCallback() {
                            @Override
                            public void onSuccess(String result) {
                                try {
                                    JSONObject object = new JSONObject(result);
                                    String status = object.getJSONObject("metadata").getString("status");
                                    String message = object.getJSONObject("metadata").getString("message");
                                    if (status.equals("200")) {
                                        Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
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
                    }*/
                    break;
                case TransactionResult.STATUS_PENDING:
                    Toast.makeText(this, "Transaksi Berhasil, Segera Lakukan Pembayaran", Toast.LENGTH_LONG).show();
                    break;
                case TransactionResult.STATUS_FAILED:
                    Toast.makeText(this, "Transaksi Gagal, Silahkan Ulangi Beberapa Saat Lagi", Toast.LENGTH_LONG).show();
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

   /* private ArrayList<SetGetMasterPaketBaru> prepareDataMenuPaketDoku() {
        ArrayList<SetGetMasterPaketBaru> rvData = new ArrayList<>();
        for (int i = 0; i < gambar.length; i++) {
            SetGetMasterPaketBaru isi = new SetGetMasterPaketBaru();
            isi.setIcon(gambar[i]);
            isi.setPaket(paket[i]);
            isi.setKeterangan(keterangan[i]);
            isi.setHarga(harga[i]);
            rvData.add(isi);
        }
        return rvData;
    }*/

    private void getOneKuponGold(String register) {
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.popup_kupon_gratis);
        RelativeLayout btnOK = dialog.findViewById(R.id.btnOK);
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        /*final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        };

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                handler.removeCallbacks(runnable);
            }
        });

        handler.postDelayed(runnable, 3000);*/
    }


    @Override
    public void onPermissionsGranted(int requestCode) {

    }

    @TargetApi(Build.VERSION_CODES.M)
    private void imageSlider() {
        proses.ShowDialog();
        ApiVolley request = new ApiVolley(this, new JSONObject(), "GET", URL.Slider, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                proses.DismissDialog();
                imageSlider = new ArrayList<>();
                try {
                    JSONObject object = new JSONObject(result);
                    String status = object.getJSONObject("metadata").getString("status");
                    if (status.equals("200")) {
                        JSONArray response = object.getJSONArray("response");
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject isi = response.getJSONObject(i);
                            imageSlider.add(new Custom(
                                    isi.getString("picture")
                            ));
                        }
                        sliderView.setAdapter(null);
                        sliderView.setAdapter(new ImageSliderAdapter(MainActivity.this, imageSlider));
                        indicator.setViewPager(sliderView);
                        final Handler handler = new Handler();
                        final Runnable Update = new Runnable() {
                            public void run() {
                                if (currentPage == imageSlider.size()) {
                                    currentPage = 0;
                                }
                                sliderView.setCurrentItem(currentPage++, true);
                            }
                        };
                        Timer swipeTimer = new Timer();
                        swipeTimer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                handler.post(Update);
                            }
                        }, 0, 2500);
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
        /*for (int i = 0; i < XMEN.length; i++) {
            XMENArray.add(XMEN[i]);
        }*/


        // Auto start of viewpager

    }

    private void checkVersion() {

        PackageInfo pInfo = null;
        version = "";

        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        version = pInfo.versionName;
//        getSupportActionBar().setSubtitle(getResources().getString(R.string.app_name) + " v "+ version);
        latestVersion = "";
        link = "";

        ApiVolley request = new ApiVolley(MainActivity.this, new JSONObject(), "GET", URL.upVersions, "", "", 0, new ApiVolley.VolleyCallback() {

            @Override
            public void onSuccess(String result) {

                JSONObject responseAPI;
                try {
                    responseAPI = new JSONObject(result);
                    String status = responseAPI.getJSONObject("metadata").getString("status");

                    if (status.equals("200")) {
                        latestVersion = responseAPI.getJSONObject("response").getString("build_version");
                        link = responseAPI.getJSONObject("response").getString("link_update");
                        updateRequired = ((responseAPI.getJSONObject("response").getString("wajib")).equals("1")) ? true : false;
                        if (!version.trim().equals(latestVersion.trim()) && link.length() > 0) {
                            final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            if (updateRequired) {
                                builder.setIcon(R.mipmap.ic_launcher)
                                        .setTitle("Update")
                                        .setMessage("Versi terbaru " + latestVersion + " telah tersedia, mohon download versi terbaru.")
                                        .setPositiveButton("Update Sekarang", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                                                startActivity(browserIntent);
                                            }
                                        })
                                        .setCancelable(false)
                                        .show();
                            } else {
                                builder.setIcon(R.mipmap.ic_launcher)
                                        .setTitle("Update")
                                        .setMessage("Versi terbaru " + latestVersion + " telah tersedia, mohon download versi terbaru.")
                                        .setPositiveButton("Update Sekarang", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                                                startActivity(browserIntent);
                                            }
                                        })
                                        .setNegativeButton("Update Nanti", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        }).show();
                            }

                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String result) {

                Toast.makeText(getApplicationContext(), "Terjadi kesalahan saat memuat data", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startMain);
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Klik sekali lagi untuk keluar", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }
}

//                menuPaket = prepareDataMenuPaketDoku();

                /*ApiVolley requestPaket = new ApiVolley(MainActivity.this, new JSONObject(), "GET", URL.MasterPaket, "", "", 0, new ApiVolley.VolleyCallback() {
                    @Override
                    public void onSuccess(String result) {
                        masterPaket = new ArrayList<>();
                        try {
                            JSONObject object = new JSONObject(result);
                            String status = object.getJSONObject("metadata").getString("status");
                            if (status.equals("200")) {
                                JSONArray response = object.getJSONArray("response");
                                for (int i = 0; i < response.length(); i++) {
                                    JSONObject isiMasterCabang = response.getJSONObject(i);
                                    masterPaket.add(new SetGetMasterPaket(
                                            isiMasterCabang.getString("flag"),
                                            isiMasterCabang.getString("keterangan")
                                    ));
                                }
//                                android.R.layout.simple_spinner_item
                                adapterPaket = new ArrayAdapter<SetGetMasterPaket>(MainActivity.this, R.layout.layout_simple_list, masterPaket);
                                dropdownPilihKupon.setAdapter(adapterPaket);
                                dropdownPilihKupon.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                                        Toast.makeText(getApplicationContext(),parent.getItemAtPosition(position).toString(),Toast.LENGTH_LONG).show();
                                        if (position == 1) {
                                            layoutDPCabang.setVisibility(View.VISIBLE);
                                        } else {
                                            layoutDPCabang.setVisibility(View.GONE);
                                        }
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {

                                    }
                                });
//                                dropdownPilihKupon.setSelection(0, true);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(String result) {
                        Toast.makeText(getApplicationContext(), "Terjadi Kesalahan", Toast.LENGTH_LONG).show();
                    }
                });*/
