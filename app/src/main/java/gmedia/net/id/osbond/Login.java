package gmedia.net.id.osbond;

import android.app.Dialog;
import android.content.Intent;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;


public class Login extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private LinearLayout loginFacebook, loginGmail;
    private LoginButton btnFacebook;
    public static GoogleApiClient googleApiClient;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private CallbackManager mCallbackManager;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private SessionManager session;
    private String jenisLogin = "FACEBOOK";
    private final String TAG = "Login";
    private boolean signUpFlag = false;
    private static final int RC_SIGN_IN_GOOGLE = 9001;
    private Dialog dialog;
    private String refreshToken = "";
    private Proses proses;
    private boolean doubleBackToExitPressedOnce = false;
    private EditText inputPhoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        proses = new Proses(Login.this);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        session = new SessionManager(Login.this);
        InitFirebaseSetting.getFirebaseSetting(Login.this);
        refreshToken = FirebaseInstanceId.getInstance().getToken();
        initGoogleAuth();
        initFirebaseAuth();
        initUI();
        initFacebookAuth();
    }

    private void initGoogleAuth() {

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .enableAutoManage(this, this)
                .addOnConnectionFailedListener(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        googleApiClient.connect();
    }

    private void initFirebaseAuth() {

        FirebaseApp.initializeApp(this);
        auth = FirebaseAuth.getInstance();

        // logout
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.getBoolean("logout", false)) {
                try {
                    auth.signOut();
                    LoginManager.getInstance().logOut();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid() + " " + user.getEmail() + " " + user.getDisplayName());
                    if (session.isSaved()) {
                        jenisLogin = session.getUserInfo(SessionManager.TAG_JENIS);
                    }

                    postSuccessLogin();
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };
    }

    private void initUI() {
        loginFacebook = (LinearLayout) findViewById(R.id.loginFacebook);
        loginGmail = (LinearLayout) findViewById(R.id.loginGmail);
        btnFacebook = (LoginButton) findViewById(R.id.loginButtonFacebook);
        loginFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                LoginManager.getInstance().logOut();
                btnFacebook.performClick();
                signUpFlag = true;
            }
        });
        loginGmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                signIn();
                signUpFlag = true;
            }
        });
    }

    private void signIn() {
        googleApiClient.clearDefaultAccountAndReconnect();
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN_GOOGLE);
    }

    private void initFacebookAuth() {

        mCallbackManager = CallbackManager.Factory.create();
        btnFacebook.setReadPermissions("email", "public_profile");
        btnFacebook.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                jenisLogin = "FACEBOOK";
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "onError: " + error.toString());
            }
        });

    }

    private void handleFacebookAccessToken(AccessToken token) {

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            proses.DismissDialog();
                            Toast.makeText(Login.this, "Authentication failed. Your account already linked by Google login",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Google login
        if (requestCode == RC_SIGN_IN_GOOGLE) {

            jenisLogin = "GOOGLE";
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {

                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
                proses.ShowDialog();
            } else {

                Log.d(TAG, "onAuthStateChanged:signed_out");
                // User not Authenticated
            }
        } else {

            // FB Login
            if (resultCode == -1) {

                jenisLogin = "FACEBOOK";
                mCallbackManager.onActivityResult(requestCode, resultCode, data);
                proses.ShowDialog();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            proses.DismissDialog();
                            Toast.makeText(Login.this, "Authentication failed. Your account already linked by facebook login",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    private void postSuccessLogin() {
        if (user != null) {
            String uid = "";
            try {
                uid = user.getUid();
                user.getUid();
            } catch (Exception e) {
                e.printStackTrace();
                uid = "";
            }

            if (!uid.equals("")) {
                JSONObject jBody = new JSONObject();
                try {
                    jBody.put("uid", user.getUid());
                    Log.d("uid", user.getUid());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ApiVolley request = new ApiVolley(Login.this, jBody, "POST", URL.Auth, "", "", 0, new ApiVolley.VolleyCallback() {
                    @Override
                    public void onSuccess(String result) {
                        try {
                            JSONObject object = new JSONObject(result);
                            String status = object.getJSONObject("metadata").getString("status");
                            String message = object.getJSONObject("metadata").getString("message");
                            if (status.equals("200")) {
                                JSONObject response = object.getJSONObject("response");
                                String token = response.getString("token");
                                session.createLoginSession(user.getUid(),
                                        (user.getEmail() != null && user.getEmail().length() > 0) ? user.getEmail() : session.getEmail(),
                                        jenisLogin, "1");
                                session.updateToken(token);
                                Toast.makeText(Login.this, message, Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(Login.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                if (user != null) {
                                    registerUser();
                                }
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
            } else {
                Toast.makeText(Login.this, "Harap login kembali", Toast.LENGTH_LONG).show();
                proses.DismissDialog();
            }
        } else {
            Toast.makeText(Login.this, "Harap login kembali", Toast.LENGTH_LONG).show();
            proses.DismissDialog();
        }
    }

    private void registerUser() {
        JSONObject jBody = new JSONObject();
        try {
            jBody.put("uid", user.getUid());
            jBody.put("email", user.getEmail());
            jBody.put("profile_name", user.getDisplayName());
            jBody.put("foto", String.valueOf(user.getPhotoUrl()));
            jBody.put("type", jenisLogin);
            jBody.put("fcm_id", refreshToken);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ApiVolley request = new ApiVolley(Login.this, jBody, "POST", URL.Register, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                proses.DismissDialog();
                try {
                    JSONObject object = new JSONObject(result);
                    String status = object.getJSONObject("metadata").getString("status");
                    String message = object.getJSONObject("metadata").getString("message");
                    if (status.equals("200")) {
                        JSONObject response = object.getJSONObject("response");
                        String token = response.getString("token");
                        session.createLoginSession(user.getUid(),
                                (user.getEmail() != null && user.getEmail().length() > 0) ? user.getEmail() : session.getEmail(),
                                jenisLogin,
                                "1");
                        session.updateToken(token);
                        Toast.makeText(Login.this, message, Toast.LENGTH_LONG).show();
                        String register = "register";
                        Intent intent = new Intent(Login.this, MainActivity.class);
//                        intent.putExtra("register",register);
                        startActivity(intent);
                        finish();
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
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        proses.DismissDialog();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        proses.DismissDialog();
    }

    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            auth.removeAuthStateListener(mAuthListener);
        }
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
