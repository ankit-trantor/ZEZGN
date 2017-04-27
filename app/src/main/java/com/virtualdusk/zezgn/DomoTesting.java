package com.virtualdusk.zezgn;

import android.*;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

import com.virtualdusk.zezgn.Utilities.Constant;
import com.virtualdusk.zezgn.Utilities.Utilities;
import com.virtualdusk.zezgn.activity.CategoryScreen.CategoryFragment;
import com.virtualdusk.zezgn.activity.CustomDesignActivity;
import com.virtualdusk.zezgn.activity.DesignActivity;
import com.virtualdusk.zezgn.activity.Home;
import com.virtualdusk.zezgn.utils.helper.AppUtils;

public class DomoTesting extends AppCompatActivity implements View.OnClickListener{

    Button button2;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 1;
    public static int a = 0;
    static final Integer CAMERA = 0x5;
    private static final String TAG = "ProductFragment";
    View v;
    private RelativeLayout RL_Tshirt, RL_Cup, RL_Mobile;
    public static String strFromproduct = "", strProductId;
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    public static Bitmap mBitmap;
    private String strDesignImageUrl = "", userChoosenTask;
    private ImageView IvDesignPic, IvDialogBack;
    private TextView TvImageQuality, TvDialogTitle, tvCart;
    private View ViewPoor, ViewFair, ViewGood;
    private EditText EtDesignName;
    private Button BtnClose, BtnContiue;
    private String strQuality = "", strDesignTitle = "";
    private Utilities mUtilities;
    private SharedPreferences sharedPreferences;
    private String mUserId = "";
    private String strMessage = "", strDesignId = "";

    static final int REQUEST_TAKE_PHOTO = 11;
    private String mCurrentPhotoPath;
    private Uri photoURI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_domo_testing);

        intializeViews();

        mUtilities = new Utilities();
        DesignActivity mDesignActivity = new DesignActivity();
        mDesignActivity.setTitle("CUSTOMIZE");

        sharedPreferences = getSharedPreferences(AppUtils.PREF_NAME, Context.MODE_PRIVATE);
        mUserId = sharedPreferences.getString(getString(R.string.key_user_id), "");

        if (mUserId.equalsIgnoreCase("-1")) {
            mUserId = mUtilities.getDeviceId(getApplicationContext());
        }
        mUtilities = new Utilities();

       /* button2 = (Button) findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    // Check Permissions Now
                    askForPermission(android.Manifest.permission.CAMERA, CAMERA);
                } else {
                    startCamera1();
                }
            }
        });*/
    }
    private void intializeViews() {

        RL_Tshirt = (RelativeLayout) findViewById(R.id.rlTShirt);
        RL_Cup = (RelativeLayout)findViewById(R.id.rlCup);
        RL_Mobile = (RelativeLayout)findViewById(R.id.rlMobile);

        RL_Tshirt.setOnClickListener(this);
        RL_Cup.setOnClickListener(this);
        RL_Mobile.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.rlTShirt:
                Constant.SaveButton = "yes";
                callActivity("1");
                break;

            case R.id.rlCup:
                Constant.SaveButton = "yes";
                callActivity("3");
                break;

            case R.id.rlMobile:
                Constant.SaveButton = "yes";
                callActivity("2");
                break;
        }
    }
    private void callActivity(String id) {
        strProductId = id;
        selectImage();
    }

    private void selectImage() {

        // this will open dialog to add image from camera or gallery
        final CharSequence[] items = {"Camera", "Gallery", "Zezign designs", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(DomoTesting.this);
        builder.setTitle("Choose Image");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
               // boolean result = MyApplication.checkPermission(DomoTesting.this);

                if (items[item].equals("Camera")) {
                    userChoosenTask = "Camera";
                   // if (result)
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        // Check Permissions Now
                        askForPermission(android.Manifest.permission.CAMERA, CAMERA);
                    } else {
                        startCamera1();
                    }
                } else if (items[item].equals("Gallery")) {
                    userChoosenTask = "Gallery";
                  //  if (result)
                         //galleryIntent();
                } else if (items[item].equals("Zezign designs")) {
                    Home mHome = new Home();
                    mHome.setTitle("CATEGORIES");
                    strFromproduct = "cat";
                    Home.strKey = "product";
                   // getFragmentManager().beginTransaction().add(R.id.frame, new CategoryFragment()).addToBackStack(null).commit();

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }
    private void startCamera1() {
        // Checking camera availability
        final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, setImageUri());
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }
    private void askForPermission(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {

                //In this case I am just asking the permission again
                ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);

            } else {

                ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(ActivityCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_GRANTED){
            // switch (requestCode) {
            //Location
                startCamera1();

        }else{
            Toast.makeText(this, "Please allow permissions", Toast.LENGTH_SHORT).show();
        }
    }
    public Uri setImageUri() {
        Uri imgUri;
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            File file = new File(Environment.getExternalStorageDirectory() + "/DCIM/", getString(R.string.app_name) + Calendar.getInstance().getTimeInMillis() + ".jpg");
            imgUri = Uri.fromFile(file);
        } else {
            File file = new File(getFilesDir(), getString(R.string.app_name) + Calendar.getInstance().getTimeInMillis() + ".jpg");
            imgUri = Uri.fromFile(file);
        }
        return imgUri;
    }


}

