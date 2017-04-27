package com.virtualdusk.zezgn.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.util.ArrayMap;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.plus.Plus;
import com.google.gson.JsonElement;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import com.virtualdusk.zezgn.FCM.MyFirebaseInstanceIDService;
import com.virtualdusk.zezgn.Model.RegisterINRequest;
import com.virtualdusk.zezgn.Model.SignInRequest;
import com.virtualdusk.zezgn.R;
import com.virtualdusk.zezgn.Utilities.Constant;
import com.virtualdusk.zezgn.Utilities.SaveRecords;
import com.virtualdusk.zezgn.activity.Setting.RegisterSocial;
import com.virtualdusk.zezgn.api.RegisterApi;
import com.virtualdusk.zezgn.api.SignInApi;
import com.virtualdusk.zezgn.instagram.instagram_data.InstagramApp;
import com.virtualdusk.zezgn.utils.ConnectionDetector;

/**
 * Created by Amit Sharma on 9/26/2016.
 */
//@TargetApi(Build.VERSION_CODES.LOLLIPOP_MR1)
public class LoginActivity extends AbstractActivity implements View.OnClickListener,
        GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    //CarrierMessagingService.ResultCallback<People.LoadPeopleResult>

    private static String STR_FCM_TOKEN = "";
    private InstagramApp mApp;
    ProgressDialog progress_dialog;

    private EditText edUserName, edPassword;
    private Button btnSignIn, btnForgotPassword, btnRegister;
    private ImageButton imgBtnFb, imgBtnGoogle, imgBtnInstagram;
    byte[] bytess = null;

    private String mUserName, mPassword, mUserId;

    private CallbackManager callbackManager;
    private LoginButton loginButton;
    private String mFacebookAccesstoken;
    private TextView lblSkip;

    private int request_code;
    GoogleApiClient google_api_client;
    GoogleApiAvailability google_api_availability;
    SignInButton signIn_btn;
    private static final int SIGN_IN_CODE = 0, RC_SIGN_IN = 7;
    private static final int PROFILE_PIC_SIZE = 120;
    private ConnectionResult connection_result;
    private boolean is_signInBtn_clicked;
    private boolean is_intent_inprogress;
    String loginwith = null;
    private String strSocialEmailAddress = "", strSocialFullName = "", strSocialFirstName = "", strSocialLastName = "";

    public static HashMap<String, String> userInfoHashmap = new HashMap<String, String>();
    private SharedPreferences sharedPreferences;

    private Handler handler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == InstagramApp.WHAT_FINALIZE) {
                userInfoHashmap = mApp.getUserInfo();
                if (userInfoHashmap != null) {
                    strSocialFullName = userInfoHashmap.get(InstagramApp.TAG_USERNAME);
                    strSocialEmailAddress = strSocialFullName + "@endivesoftware.com";
                    mApp.resetAccessToken();
                    Log.d(TAG, "handleMessage: strSocialFullName " + strSocialFullName + "strSocialEmailAddress " + strSocialEmailAddress);
                    registerViaRetrofit("i");
                } else {
                    Log.d(TAG, "handleMessage: userinfohash null");
                }

            } else if (msg.what == InstagramApp.WHAT_FINALIZE) {
                Toast.makeText(LoginActivity.this, "Check your network.",
                        Toast.LENGTH_SHORT).show();
            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.login);
        sharedPreferences = this.getAppSharedPreferences();
        STR_FCM_TOKEN = MyFirebaseInstanceIDService.refreshedToken;

        if (STR_FCM_TOKEN == null || STR_FCM_TOKEN.equalsIgnoreCase("")) {

            STR_FCM_TOKEN = sharedPreferences.getString(Constant.FCM_TOKEN, "");
        }

        Log.d(TAG, "onCreate: STR_FCM_TOKEN " + STR_FCM_TOKEN);

        SaveRecords.saveToPreference(Constant.FCM_TOKEN, STR_FCM_TOKEN, LoginActivity.this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.app_background_color));
        }
        progress_dialog = new ProgressDialog(LoginActivity.this, R.style.MyTheme);
        progress_dialog.setCancelable(false);
        progress_dialog.setIndeterminateDrawable(getResources().getDrawable(R.drawable.my_progress_indeterminate));

        // find resources which is used in Login Screen
        signIn_btn = (SignInButton) findViewById(R.id.GsignIn);

        findRes();
        // used to initilize the Instagram app
        buidNewGoogleApiClient();

        imgBtnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                if (google_api_client.isConnected()) {
//                    google_api_client.connect();
//                }


                loginwith = "gplus";
                //  Toast.makeText(LoginActivity.this, "start sign process", Toast.LENGTH_SHORT).show();
//                signIn_btn.setPressed(true);
//                signIn_btn.invalidate();
//                signIn_btn.setPressed(false);
//                signIn_btn.invalidate();
//                gPlusSignIn();

                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(google_api_client);
                startActivityForResult(signInIntent, RC_SIGN_IN);
                google_api_client.connect();

            }
        });
    }

    private void buidNewGoogleApiClient() {

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

//        google_api_client =  new GoogleApiClient.Builder(this)
//
//                .addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this)
//                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
//                .addScope(Plus.SCOPE_PLUS_LOGIN)
//                .addScope(Plus.SCOPE_PLUS_PROFILE)
//                .build();

        google_api_client = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        signIn_btn.setSize(SignInButton.SIZE_STANDARD);
        signIn_btn.setScopes(new Scope[]{Plus.SCOPE_PLUS_LOGIN});
    }

    private void gPlusSignIn() {
        if (!google_api_client.isConnecting()) {
            Log.d("user connected", "connected");
            is_signInBtn_clicked = true;
            progress_dialog.show();
            resolveSignInError();

        } else {
            Log.d("user not connected", "not connected");
            google_api_client.connect();
        }
    }

    private void resolveSignInError() {
        try {
            if (connection_result != null && connection_result.hasResolution()) {
                try {
                    is_intent_inprogress = true;
                    connection_result.startResolutionForResult(LoginActivity.this, SIGN_IN_CODE);
                } catch (IntentSender.SendIntentException e) {
                    is_intent_inprogress = false;
                    google_api_client.connect();
                }
            } else {
                Log.d(TAG, "resolveSignInError: connection null");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    protected void onStart() {
        super.onStart();

    }

    protected void onStop() {
        super.onStop();
        if (google_api_client.isConnected()) {
            google_api_client.disconnect();
        }
    }

    private void initializeInstagram() {
        mApp = new InstagramApp(this, Constant.CLIENT_ID,
                Constant.CLIENT_SECRET, Constant.CALLBACK_URL);
        mApp.setListener(new InstagramApp.OAuthAuthenticationListener() {

            @Override
            public void onSuccess() {
                Log.e(TAG, "onSuccess: Instagram Disconnect");

                mApp.fetchUserName(handler);
            }

            @Override
            public void onFail(String error) {
                Toast.makeText(LoginActivity.this, error, Toast.LENGTH_SHORT)
                        .show();
            }
        });


        if (mApp.hasAccessToken()) {
            // tvSummary.setText("Connected as " + mApp.getUserName());
            Log.e(TAG, "onSuccess: Instagram Disconnect");
            mApp.fetchUserName(handler);

        }
    }

    // it is the buttons click event in Login Screen

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSignIn:
                mUserName = edUserName.getText().toString();
                mPassword = edPassword.getText().toString();

                if (mUserName.length() == 0) {
                    show("Please enter email address");
                }
//                if(!ValidationMethod.emailValidation(mUserName)){
//                    showToast(getString(R.string.valid_email_essential));
//                }
                else if (mPassword.length() == 0) {
                    showToast(getString(R.string.password_essential));
                } else if (mPassword.length() < 6) {
                    showToast(getString(R.string.password_essential_min));
                } else {
                    if (new ConnectionDetector(LoginActivity.this).isConnectingToInternet()) {
//                        new LogInTask().execute();
                        Log.e(TAG, "onClick: " + "Connecting");
                        progress_dialog.show();
                        LoginViaRetrofit();
                    } else {
                        showToast(getString(R.string.no_internet));
                    }
                }

                break;
            case R.id.btnForgotPassword:
                startActivity(new Intent(LoginActivity.this, ForgotPassword.class));
                break;
            case R.id.btnRegister:
                startActivity(new Intent(LoginActivity.this, Register.class));

                break;

            case R.id.imgBtnInstagram:
                initializeInstagram();
                connectOrDisconnectUser();
                break;

            case R.id.lblSkip:
                SaveRecords.saveToPreference(getString(R.string.key_user_id), "-1", LoginActivity.this);
                SaveRecords.saveToPreference(getString(R.string.key_notification_on), " ", LoginActivity.this);
                SaveRecords.saveToPreference(getString(R.string.key_name), " ", LoginActivity.this);
                SaveRecords.saveToPreference(getString(R.string.key_email), " ", LoginActivity.this);
                SaveRecords.saveToPreference(getString(R.string.key_mobile), " ", LoginActivity.this);
                SaveRecords.saveToPreference(getString(R.string.key_profile_image), " ", LoginActivity.this);
                Intent i = new Intent(this,Home.class);
                i.putExtra("move","");
                startActivity(i);
                LoginActivity.this.finish();
                break;


        }
    }


    private void showToast(String message) {
        Toast.makeText(LoginActivity.this, "" + message, Toast.LENGTH_SHORT).show();
    }

    public static final String TAG = "Retrofit";

    private void LoginViaRetrofit() {
        //Creating an object of our api interface
        Retrofit restAdapter = SignInApi.retrofit;
        SignInApi registerApi = restAdapter.create(SignInApi.class);

        Call<JsonElement> call = registerApi.createUserViaJSON(getSignInJSON());

        call.enqueue(new Callback<JsonElement>() {

            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                Log.e(TAG, "onResponse: " + response.body());
                progress_dialog.dismiss();

                parseResponse(response.body().toString(), "login", "");
            }

            @Override
            public void onFailure(Call<JsonElement> result, Throwable t) {
                Log.e(TAG, "onFailure: " + result.toString());
            }
        });


    }

    @Override
    protected void onPause() {
        super.onPause();
//        AppEventsLogger.deactivateApp(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "onActivityResult: requestCode " + requestCode);
        if (loginwith.equals("facebook")) {
            callbackManager.onActivityResult(requestCode, resultCode, data);
            try {
                if( AccessToken.getCurrentAccessToken() == null){
                    Toast.makeText(LoginActivity.this,"Something went wrong",Toast.LENGTH_LONG).show();
                }else{
                    AccessToken accesstoken = AccessToken.getCurrentAccessToken();
                    mFacebookAccesstoken = accesstoken.getToken();
                    Log.e("AccessToken", "" + mFacebookAccesstoken);
                }

            } catch (Exception e) {


                e.printStackTrace();
            }
        } else {
            if (requestCode == RC_SIGN_IN) {
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                handleSignInResult(result);
            } else if (requestCode == SIGN_IN_CODE) {
                request_code = requestCode;
                if (resultCode != RESULT_OK) {
                    is_signInBtn_clicked = false;
                    progress_dialog.dismiss();
                }
                is_intent_inprogress = false;

                if (!google_api_client.isConnecting()) {
                    google_api_client.connect();
                }
            }
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();

            Log.e(TAG, "display name: " + acct.getDisplayName());

            strSocialFullName = acct.getDisplayName();
//            String personPhotoUrl = acct.getPhotoUrl().toString();
            strSocialEmailAddress = acct.getEmail();

            Log.e(TAG, "Name: " + strSocialFullName + ", email: " + strSocialEmailAddress);

            Log.d(TAG, "getProfileInfo: strSocialFullName " + strSocialFullName + "strSocialEmailAddress " + strSocialEmailAddress);

            registerViaRetrofit("g");


        } else {
            // Signed out, show unauthenticated UI.

            Auth.GoogleSignInApi.signOut(google_api_client).setResultCallback(
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status status) {
                            Log.d(TAG, "onResult: signed out");
                        }
                    });
        }
    }


    private FacebookCallback<LoginResult> mCallBack = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {

            progress_dialog.dismiss();

            GraphRequest request = GraphRequest.newMeRequest(
                    loginResult.getAccessToken(),
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(
                                JSONObject object,
                                GraphResponse response) {

                            Log.e("response: ", response + "");

                            try {
                                strSocialEmailAddress = object.getString("email").toString();
                                strSocialFullName = object.getString("name").toString();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


//                            Bundle b=new Bundle();
//                            b.putInt("status",3);
//                            b.putString("email_address",email_address);
//                            b.putString("name",name);
//                            Intent i =new Intent(LoginActivity.this, RegisterSocial.class);
//                            i.putExtras(b);
//                            startActivity(i);

                            registerViaRetrofit("fb");



                        }
                    });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,name,email,gender, birthday");
            request.setParameters(parameters);
            request.executeAsync();
        }

        @Override
        public void onCancel() {
            progress_dialog.dismiss();
        }

        @Override
        public void onError(FacebookException e) {
            progress_dialog.dismiss();
        }
    };

    private void registerViaRetrofit(final String loginType) { // type will be fb,g,i(facebook, google, instagram)
        //Creating an object of our api interface
        Retrofit restAdapter = RegisterApi.retrofit;
        RegisterApi registerApi = restAdapter.create(RegisterApi.class);
        progress_dialog.show();


        Call<JsonElement> call = registerApi.createUserViaJSON(getRegisorJSON());

        call.enqueue(new Callback<JsonElement>() {

            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                Log.e(TAG, "onResponse: " + response.body());

                progress_dialog.dismiss();
                parseResponse(response.body().toString(), "social", loginType);

            }

            @Override
            public void onFailure(Call<JsonElement> result, Throwable t) {
                Log.e(TAG, "onFailure: " + result.toString());
                progress_dialog.dismiss();
            }
        });


//        registerApi.createUser(first_name, Last_name, email_id, country_id, Password_, getString(R.string.device_type_android), android_id, getString(R.string.login_type_email), callback);

    }

    private RegisterINRequest getRegisorJSON() {

        Log.d(TAG, "getRegisorJSON: STR_FCM_TOKEN " + STR_FCM_TOKEN);
        RegisterINRequest registerInData = new RegisterINRequest();
        if(strSocialFullName != null){
            String[] sd = strSocialFullName.split("\\s+");
            Log.d(TAG, "getRegisorJSON: email " + strSocialEmailAddress);
            if (sd.length == 1) {
                registerInData.setFname(sd[0] + "");
                registerInData.setLname(" ");
                strSocialFirstName = sd[0] + "";
                strSocialLastName = "";

                Log.d(TAG, "getRegisorJSON: fname :  " + sd[0] + " lname : " + "");
            } else if (sd.length == 2) {
                registerInData.setFname(sd[0] + "");
                registerInData.setLname(sd[1] + "");
                strSocialFirstName = sd[0] + "";
                strSocialLastName = sd[1] + "";

                Log.d(TAG, "getRegisorJSON: fname :  " + sd[0] + " lname : " + sd[1]);
            }


        }else{
            registerInData.setFname(" ");
            registerInData.setLname(" ");
            strSocialFirstName = " ";
            strSocialLastName = " ";
        }

        registerInData.setEmail(strSocialEmailAddress);
        registerInData.setDevice_type("android");
        registerInData.setDevice_token(STR_FCM_TOKEN);
        registerInData.setSocial_register("true");

        Map<String, String> paramsJsonObject = new ArrayMap<>();
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), (new JSONObject(paramsJsonObject)).toString());
        return registerInData;
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "onConnected: ");
        is_signInBtn_clicked = false;
        // Get user's information and set it into the layout
        progress_dialog.dismiss();
        //getProfileInfo();


    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspended: ");

        google_api_client.connect();
    }

//    private void getProfileInfo() {
//        Log.d(TAG, "getProfileInfo: ");
//
//        try {
//
//            if (Plus.PeopleApi.getCurrentPerson(google_api_client) != null) {
//                Person currentPerson = Plus.PeopleApi.getCurrentPerson(google_api_client);
//                setPersonalInfo(currentPerson);
//
//                strSocialFullName = currentPerson.getDisplayName();
//                String personPhotoUrl = currentPerson.getImage().getUrl();
//                strSocialEmailAddress = Plus.AccountApi.getAccountName(google_api_client);
//
//                gPlusSignOut();
////                Log.d(TAG, "getProfileInfo: strSocialFullName " + strSocialFullName + "strSocialEmailAddress " + strSocialEmailAddress);
////
////                registerViaRetrofit("g");
//
//            } else {
//                Toast.makeText(getApplicationContext(),
//                        "No Personal info mention", Toast.LENGTH_LONG).show();
//
////                Bundle b = new Bundle();
////                b.putInt("status", 2);
////                b.putString("personName", "  ");
////                b.putString("email", "");
////                Intent i = new Intent(LoginActivity.this, RegisterSocial.class);
////                i.putExtras(b);
////                startActivity(i);
//
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }

//    private void setPersonalInfo(Person currentPerson) {
//
//        String personName = currentPerson.getDisplayName();
//        String personPhotoUrl = currentPerson.getImage().getUrl();
//        String email = Plus.AccountApi.getAccountName(google_api_client);
//
//        Log.e("Name...", personName);
//        Log.e("email...", email);
//
//       /* TextView   user_name = (TextView) findViewById(R.id.userName);
//        user_name.setText("Name: "+personName);
//        TextView gemail_id = (TextView)findViewById(R.id.emailId);
//        gemail_id.setText("Email Id: " +email);
//        setProfilePic(personPhotoUrl);
//        progress_dialog.dismiss();
//        Toast.makeText(this, "Person information is shown!", Toast.LENGTH_LONG).show();*/
//    }

    private void gPlusSignOut() {
        if (google_api_client.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(google_api_client);
            google_api_client.disconnect();
            //  google_api_client.connect();
        }
    }
    /*@Override
    public void onConnectionFailed(ConnectionResult result) {

        connection_result = result;
        if (!result.hasResolution()) {
            google_api_availability.getErrorDialog(this, result.getErrorCode(),request_code).show();
            return;
        }

        if (!is_intent_inprogress) {

            connection_result = result;

            if (is_signInBtn_clicked) {

                resolveSignInError();
            }
        }

    }*/

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Always assign result first
        connection_result = result;

        if (!result.hasResolution()) {
            GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this,
                    0).show();
            return;
        }

        if (!is_intent_inprogress) {

            if (is_signInBtn_clicked) {
                // The user has already clicked 'sign-in' so we attempt to
                // resolve all
                // errors until the user is signed in, or they cancel.
                resolveSignInError();
            }
        }
    }

//    @Override
//    public void onReceiveResult(People.LoadPeopleResult loadPeopleResult) throws RemoteException {
//
//    }

    private class DownloadImage extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Bitmap doInBackground(String... URL) {

            String imageURL = URL[0];

            Bitmap bitmap = null;
            try {
                // Download Image from URL
                InputStream input = new URL(imageURL).openStream();
                // Decode Bitmap
                bitmap = BitmapFactory.decodeStream(input);
                Log.e("bitmap1111..", "" + bitmap);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            // Set the bitmap into ImageView
            //imageview.setImageBitmap(result);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            result.compress(Bitmap.CompressFormat.PNG, 100, stream);
            bytess = stream.toByteArray();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        callbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton) findViewById(R.id.loginfb_button);

        loginButton.setReadPermissions("public_profile", "email", "user_friends");


        //	AppEventsLogger.activateApp(this);
        imgBtnFb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginwith = "facebook";
                loginButton.performClick();
                loginButton.setPressed(true);
                loginButton.setReadPermissions("public_profile", "email", "user_friends");
                loginButton.invalidate();
                loginButton.registerCallback(callbackManager, mCallBack);
                loginButton.setPressed(false);
                loginButton.invalidate();
            }
        });
    }

    private SignInRequest getSignInJSON() {

        Log.d(TAG, "STR_FCM_TOKEN getSignInJSON " + STR_FCM_TOKEN);
        SignInRequest signInData = new SignInRequest();
        signInData.setEmail(mUserName);
        signInData.setPassword(mPassword);
        signInData.setDevice_type("android");
        signInData.setDevice_token(STR_FCM_TOKEN);
        signInData.setSocial_register("false");

        Map<String, String> paramsJsonObject = new ArrayMap<>();
        paramsJsonObject.put("first_name", mUserName);


        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), (new JSONObject(paramsJsonObject)).toString());


        return signInData;
    }

    private void parseResponse(String response, String apiName, String loginType) {
        try {

            JSONObject jsnon = new JSONObject(response);
            String response1 = jsnon.getString("response");
            JSONObject jsonObject = new JSONObject(response1);
            String status = jsonObject.getString("code");
            String message = jsonObject.getString("message");
            String data_ = jsonObject.optString("data");
            if (status.equals("200") || status.equals("500")) {
                //  show(message);

                if(data_ != null && !data_.isEmpty()){
                    JSONObject data = new JSONObject(data_);

                    String id = data.getString("id");
                    String email = data.getString("email");
                    String fname = data.getString("fname");
                    String lname = data.getString("lname");
                    String gender = data.getString("gender");
                    String age_group = data.getString("age_group");
                    String confirmation_code = data.getString("confirmation_code");
                    String contact_no = data.getString("contact_no");
                    String address = data.getString("address");
                    String city = data.getString("city");
                    String state = data.getString("state");
                    String country_id = data.getString("country_id");
                    String zipcode = data.getString("zipcode");
                    String profile_pic = data.getString("profile_pic");
                    String role = data.getString("role");
                    //   String social_register=data.getString("social_register");
                    //  String status_=data.getString("status");
                    //   String is_block=data.getString("is_block");
                    String device_type = data.getString("device_type");
                    String device_token = data.getString("device_token");
                    String notification_on = data.getString("notification_on");
                    boolean check_status = data.getBoolean("status");


                    SaveRecords.saveToPreference(getString(R.string.key_notification_on), notification_on, LoginActivity.this);
                    SaveRecords.saveToPreference(getString(R.string.key_name), (fname + " " + lname), LoginActivity.this);
                    SaveRecords.saveToPreference(getString(R.string.key_email), email, LoginActivity.this);
                    SaveRecords.saveToPreference(getString(R.string.key_mobile), contact_no, LoginActivity.this);
                    SaveRecords.saveToPreference(getString(R.string.key_profile_image), profile_pic, LoginActivity.this);

                    if (status.equalsIgnoreCase("200") && apiName.equalsIgnoreCase("login")) {
                        SaveRecords.saveToPreference(getString(R.string.key_user_id), id, LoginActivity.this);

                        Intent i = new Intent(this,Home.class);
                        i.putExtra("move","");
                        startActivity(i);

                        LoginActivity.this.finish();
                    } else if ((status.equalsIgnoreCase("200") || status.equalsIgnoreCase("500")) && !check_status && apiName.equalsIgnoreCase("social")) {

                        if (loginType.equalsIgnoreCase("fb")) {
                            Bundle b = new Bundle();
                            b.putInt("status", 3);
                            b.putString("user_id", id);
                            b.putString("email_address", strSocialEmailAddress);
                            b.putString("name", strSocialFullName);
                            b.putString("first_name", strSocialFirstName);
                            b.putString("last_name", strSocialLastName);
                            Intent i = new Intent(LoginActivity.this, RegisterSocial.class);
                            i.putExtras(b);
                            startActivity(i);
                        } else if (loginType.equalsIgnoreCase("g")) {
                            Bundle b = new Bundle();
                            b.putInt("status", 2);
                            b.putString("user_id", id);
                            b.putString("email_address", strSocialEmailAddress);
                            b.putString("name", strSocialFullName);
                            b.putString("first_name", strSocialFirstName);
                            b.putString("last_name", strSocialLastName);
                            Intent i = new Intent(LoginActivity.this, RegisterSocial.class);
                            i.putExtras(b);
                            startActivity(i);
                        } else if (loginType.equalsIgnoreCase("i")) {
                            Bundle b = new Bundle();
                            b.putInt("status", 1);
                            b.putString("user_id", id);
                            b.putString("email_address", strSocialEmailAddress);
                            b.putString("name", strSocialFullName);
                            b.putString("first_name", strSocialFirstName);
                            b.putString("last_name", strSocialLastName);
                            Intent i = new Intent(LoginActivity.this, RegisterSocial.class);
                            i.putExtras(b);
                            startActivity(i);
                        }


                        LoginActivity.this.finish();
                    } else if (status.equalsIgnoreCase("500") && check_status && apiName.equalsIgnoreCase("social")) {
                        SaveRecords.saveToPreference(getString(R.string.key_user_id), id, LoginActivity.this);
                        Intent i = new Intent(this,Home.class);
                        i.putExtra("move","");
                        startActivity(i);

                        LoginActivity.this.finish();

                    }
                    else if(status.equalsIgnoreCase("500") && check_status == false)
                    {
                        Intent i = new Intent(LoginActivity.this, OTPVerifyActivity.class);
                        i.putExtra("user_id",id);
                        startActivity(i);
                        LoginActivity.this.finish();
                    }
                }else{
                    show(message);
                }
            } else {
                show(message);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void show(String message) {
        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
    }


    private void findRes() {

        edUserName = (EditText) findViewById(R.id.edUserName);
        edPassword = (EditText) findViewById(R.id.edPassword);

        edPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {


            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.toString().contains(" ")){
                    showToast("Space is not allowed in password");
                   edPassword.setText("");
                }

            }
        });


        btnSignIn = (Button) findViewById(R.id.btnSignIn);
        btnForgotPassword = (Button) findViewById(R.id.btnForgotPassword);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        lblSkip = (TextView) findViewById(R.id.lblSkip);
        lblSkip.setOnClickListener(this);

        imgBtnFb = (ImageButton) findViewById(R.id.imgBtnFb);
        imgBtnGoogle = (ImageButton) findViewById(R.id.imgBtnGoogle);
        imgBtnInstagram = (ImageButton) findViewById(R.id.imgBtnInstagram);

    }

    private void connectOrDisconnectUser() {
        if (mApp.hasAccessToken()) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(
                    LoginActivity.this);
            builder.setMessage("Disconnect from Instagram?")
                    .setCancelable(false)
                    .setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    mApp.resetAccessToken();
                                    Log.e(TAG, "onClick:Instagram Connect");
                                }
                            })
                    .setNegativeButton("No",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    dialog.cancel();
                                }
                            });
            final AlertDialog alert = builder.create();
            alert.show();
        } else {
            mApp.authorize();

//updateUI();


//            displayInfoDialogView();
        }
    }

    private Handler mHandler = new Handler();

    private void updateUI() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                while (true) {
                    try {
                        Thread.sleep(10000);
                        mHandler.post(new Runnable() {

                            @Override
                            public void run() {
                                // TODO Auto-generated method stub
                                // Write your code here to update the UI.
                                displayInfoDialogView();

                            }
                        });
                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                }
            }
        }).start();
    }


    public static Context context;

    public void displayInfoDialogView() {

//        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
//                context);
//        alertDialog.setTitle("Profile Info");
//
//        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View view = inflater.inflate(R.layout.profile_view, null);
//        alertDialog.setView(view);
//        ImageView ivProfile = (ImageView) view
//                .findViewById(R.id.ivProfileImage);
//        TextView tvName = (TextView) view.findViewById(R.id.tvUserName);
//        TextView tvNoOfFollwers = (TextView) view
//                .findViewById(R.id.tvNoOfFollowers);
//        TextView tvNoOfFollowing = (TextView) view
//                .findViewById(R.id.tvNoOfFollowing);
//        new ImageLoader(context).DisplayImage(
//                userInfoHashmap.get(InstagramApp.TAG_PROFILE_PICTURE),
//                ivProfile);
//        tvName.setText(userInfoHashmap.get(InstagramApp.TAG_USERNAME));
//        tvNoOfFollowing.setText(userInfoHashmap.get(InstagramApp.TAG_FOLLOWS));
//        tvNoOfFollwers.setText(userInfoHashmap
//                .get(InstagramApp.TAG_FOLLOWED_BY));
//        alertDialog.create().show();

//        Bundle b=new Bundle();
//        b.putInt("status",1);


//        Intent i =new Intent(LoginActivity.this, RegisterSocial.class);
//        i.putExtras(b);
//        startActivity(i);
//        LoginActivity.this.finish();

    }


}
