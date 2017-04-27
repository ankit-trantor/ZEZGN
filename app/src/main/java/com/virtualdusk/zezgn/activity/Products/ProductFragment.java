package com.virtualdusk.zezgn.activity.Products;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonElement;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import com.virtualdusk.zezgn.CategoryActivity;
import com.virtualdusk.zezgn.MyApplication;
import com.virtualdusk.zezgn.R;
import com.virtualdusk.zezgn.Utilities.Constant;
import com.virtualdusk.zezgn.Utilities.MarshmallowPermissions;
import com.virtualdusk.zezgn.Utilities.Utilities;
import com.virtualdusk.zezgn.activity.CategoryScreen.CategoryFragment;
import com.virtualdusk.zezgn.activity.CustomDesignActivity;
import com.virtualdusk.zezgn.activity.DesignActivity;
import com.virtualdusk.zezgn.activity.Home;
import com.virtualdusk.zezgn.activity.UpdateFragments;
import com.virtualdusk.zezgn.api.AddCustomDesignApi;
import com.virtualdusk.zezgn.utils.helper.AppUtils;

import static android.R.attr.id;

/**
 * Created by Amit Sharma on 10/13/2016.
 */
public class ProductFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "ProductFragment";
    View v;
    private String mId, strKey;
    private RelativeLayout RL_Tshirt, RL_Cup, RL_Mobile;
    private String strDesignImageUrl = "", userChoosenTask;
    public static String strFromproduct = "", strProductId;
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    public static Bitmap mBitmap;

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
    private String strDesignText = "";
    static final Integer READ_EXST = 0x4;
    static final int REQUEST_TAKE_PHOTO = 11;
    private String mCurrentPhotoPath;
    private Uri photoURI;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        v = inflater.inflate(R.layout.choose_product, container, false);
        intializeViews(v);

        mUtilities = new Utilities();
        DesignActivity mDesignActivity = new DesignActivity();
        mDesignActivity.setTitle("CUSTOMIZE");

        try {

            strKey = getArguments().getString("key", "");
            mId = getArguments().getString("id", "");
            strDesignText = getArguments().getString("design_text", "");
            strDesignImageUrl = getArguments().getString("design_img", "");

        } catch (Exception e) {
            e.printStackTrace();
        }

        sharedPreferences = getActivity().getSharedPreferences(AppUtils.PREF_NAME, Context.MODE_PRIVATE);
        mUserId = sharedPreferences.getString(getString(R.string.key_user_id), "");

        if (mUserId.equalsIgnoreCase("-1")) {
            mUserId = mUtilities.getDeviceId(getActivity());
        }
        mUtilities = new Utilities();

        return v;
    }

    private void intializeViews(View v) {

        RL_Tshirt = (RelativeLayout) v.findViewById(R.id.rlTShirt);
        RL_Cup = (RelativeLayout) v.findViewById(R.id.rlCup);
        RL_Mobile = (RelativeLayout) v.findViewById(R.id.rlMobile);

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
        if (strKey.equalsIgnoreCase("category")) {
            strFromproduct = "cat";
            selectImage();
        } else {

            Intent mIntent = new Intent(getActivity(), CustomDesignActivity.class);
            mIntent.putExtra("product_id", id);
            mIntent.putExtra("design_id", mId);
            mIntent.putExtra("design_text", strDesignText);
            mIntent.putExtra("design_image", strDesignImageUrl);
            startActivity(mIntent);
        }

    }


    private void selectImage() {

        // this will open dialog to add image from camera or gallery
        final CharSequence[] items = {"Text Only", "Gallery", "Zezign designs", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Choose Image");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {


                if (items[item].equals("Text")) {
                    userChoosenTask = "Text";
                   // if (result)
                    dialog.dismiss();

                } else if (items[item].equals("Gallery")) {
                    userChoosenTask = "Gallery";
                    //boolean result = MyApplication.checkPermission(getActivity());
                    //if (result)
                    dialog.dismiss();
                        galleryIntent();
                } else if (items[item].equals("Zezign designs")) {
                    Home mHome = new Home();
                    mHome.setTitle("CATEGORIES");
                    strFromproduct = "cat";
                    Home.strKey = "product";
                    getFragmentManager().beginTransaction().add(R.id.frame, new CategoryFragment()).addToBackStack(null).commit();

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void cameraIntent() {

        MarshmallowPermissions mPermission = new MarshmallowPermissions(getActivity());

        if (!mPermission.checkPermissionForCamera()) {
            mPermission.requestPermissionForCamera();
            cameraIntent();
        } else {
            if (!mPermission.checkPermissionForExternalStorage()) {
                mPermission.requestPermissionForExternalStorage();
            } else {
                dispatchTakePictureIntent();
            }
        }


    }

    private void galleryIntent() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Check Permissions Now
            askForPermission(Manifest.permission.READ_EXTERNAL_STORAGE,READ_EXST);
        }
        else
        {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);//
            startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
        }
    }
    private void askForPermission(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(getActivity(), permission) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), permission)) {

                //In this case I am just asking the permission again
                ActivityCompat.requestPermissions(getActivity(), new String[]{permission}, requestCode);
                galleryIntent();
            } else {

                ActivityCompat.requestPermissions(getActivity(), new String[]{permission}, requestCode);
                galleryIntent();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(ActivityCompat.checkSelfPermission(getActivity(), permissions[0]) == PackageManager.PERMISSION_GRANTED) {
            // switch (requestCode) {
            galleryIntent();
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_TAKE_PHOTO)
                onCaptureImageResult();
        }
    }

    private void onCaptureImageResult() {
        //  Bitmap thumbnail = (Bitmap) data.getExtras().get("data");

        FileOutputStream fo;
        try {

            Bitmap thumbnail = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), photoURI);
            int imageHeight = thumbnail.getHeight();
            int imageWidth = thumbnail.getWidth();
            mBitmap = thumbnail;
            Log.d(TAG, "onCaptureImageResult: thumbnail imageHeight " + imageHeight + " imageWidth " + imageWidth);
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            //mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

            File destination = new File(Environment.getExternalStorageDirectory(),
                    System.currentTimeMillis() + ".png");


            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();

            String path = MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), thumbnail, "Title", null);
            Uri selectedImageUri = Uri.parse(path);

            uploadPicDialog("camera", "camera");
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {

        Bitmap bm = null;
        String strGalleryPath = "";
        int h = 0, w = 0;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), data.getData());
                h = bm.getHeight();
                w = bm.getWidth();
                strGalleryPath = manageBitmap(bm);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        uploadPicDialog(strGalleryPath, "gallery",h,w);
    }

    private String manageBitmap(Bitmap bitmap) {

        String strGalleryPath = "";
        if (bitmap != null) {

            // R = Required, O = original , H = height, W = width
            int R_H = 200, R_W = 200;
            int newH = 0, newW;
            int O_H = bitmap.getHeight();
            int O_W = bitmap.getWidth();

            if (strProductId.equalsIgnoreCase("1")) {
                R_H = getResources().getDimensionPixelSize(R.dimen.tshirt_area_height);
                R_W = getResources().getDimensionPixelSize(R.dimen.tshirt_area_width);
            } else if (strProductId.equalsIgnoreCase("2")) {
                R_H = getResources().getDimensionPixelSize(R.dimen.phone_area_height);
                R_W = getResources().getDimensionPixelSize(R.dimen.phone_area_width);
            } else if (strProductId.equalsIgnoreCase("3")) {
                R_H = getResources().getDimensionPixelSize(R.dimen.cup_area_height);
                R_W = getResources().getDimensionPixelSize(R.dimen.cup_area_width);
            }
            Log.d(TAG, "onCreate: O_H " + O_H + "O_W " + O_W);
            Log.d(TAG, "onCreate: R_W " + R_W);
            float N_H;

            float xScale = ((float) R_W) / O_W;
            float yScale = ((float) R_H) / O_H;
            float scale = (xScale <= yScale) ? xScale : yScale;
            Log.i("Test", "xScale = " + Float.toString(xScale));
            Log.i("Test", "yScale = " + Float.toString(yScale));
            Log.i("Test", "scale = " + Float.toString(scale));

            //This code use for camera Quality

            // Create a matrix for the scaling and add the scaling data
          /*  Matrix matrix = new Matrix();
            matrix.postScale(scale, scale);

            // Create a new bitmap and convert it to a format understood by the ImageView
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, O_W, O_H, matrix, true);*/

            int h = bitmap.getHeight();
            int w = bitmap.getWidth();

          //  Log.d(TAG, "onCreate: newH " + newH + " newW " + newW);
/*

            double newH_D = h*0.1;
            h = h - (int) newH_D;

            double newW_D = w*0.1;
            w = w - (int) newW_D;
*/

            bitmap = Bitmap.createScaledBitmap(bitmap, w, h, false);

            try {
                File destination = new File(Environment.getExternalStorageDirectory(),
                        System.currentTimeMillis() + ".png");

                FileOutputStream fo;
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                destination.createNewFile();
                fo = new FileOutputStream(destination);
                fo.write(bytes.toByteArray());
                fo.close();

                String path = MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), bitmap, "Title", null);
                Uri selectedImageUri = Uri.parse(path);
                strGalleryPath = AppUtils.getRealPathFromURI(getActivity(), selectedImageUri);
                mBitmap = bitmap;
            } catch (Exception e) {
                e.printStackTrace();
            }

            Log.e(TAG, "initializeViews: bitmap not null");


        } else {
            Log.e(TAG, "initializeViews: bitmap null");
        }

        return strGalleryPath;
    }

    private void uploadPicDialog(final String strGalleryPath, String ImageType) {


        Log.e(TAG, "onSelectFromGalleryResult: strGalleryPath " + strGalleryPath);

        if (strGalleryPath == null) {


            mUtilities.showDialog(getActivity(), "Please select image from gallery", "OK");

        } else {
            final Dialog dialog = new Dialog(getActivity(), android.R.style.Theme_Black_NoTitleBar);
            dialog.setContentView(R.layout.upload_style_design);
            dialog.show();

            IvDesignPic = (ImageView) dialog.findViewById(R.id.ivDesignPic);
            IvDialogBack = (ImageView) dialog.findViewById(R.id.ivBack);
            TvDialogTitle = (TextView) dialog.findViewById(R.id.tvTitle);
            TvImageQuality = (TextView) dialog.findViewById(R.id.tvImageQuality);
            EtDesignName = (EditText) dialog.findViewById(R.id.etDesignName);
            BtnClose = (Button) dialog.findViewById(R.id.btnClose);
            BtnContiue = (Button) dialog.findViewById(R.id.btnContinue);
            ViewPoor = (View) dialog.findViewById(R.id.viewPoor);
            ViewGood = (View) dialog.findViewById(R.id.viewGood);
            ViewFair = (View) dialog.findViewById(R.id.viewFair);

            tvCart = (TextView) dialog.findViewById(R.id.tvCartCount);
            if (Home.CartCount != 0) {
                tvCart.setText(Home.CartCount + "");
            } else {
                tvCart.setVisibility(View.GONE);
            }

            TvDialogTitle.setText("UPLOAD DESIGN");
            IvDialogBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            IvDesignPic.setImageBitmap(mBitmap);
            int imageHeight = mBitmap.getHeight();
            int imageWidth = mBitmap.getWidth();
            Log.d(TAG, "uploadPicDialog: imageHeight " + imageHeight + "imageWidth " + imageWidth);

            if (imageWidth < 150 || imageHeight < 150) {
                strQuality = "POOR!!";
                ViewFair.setBackgroundColor(Color.parseColor("#898989"));
                ViewGood.setBackgroundColor(Color.parseColor("#898989"));
                ViewPoor.setBackgroundColor(Color.parseColor("#CC1100"));

            } else if ((imageWidth >= 150 && imageWidth < 500) || (imageHeight >= 150 && imageHeight < 500)) {
                strQuality = "FAIR!!";
                ViewFair.setBackgroundColor(Color.parseColor("#FFA824"));
                ViewGood.setBackgroundColor(Color.parseColor("#898989"));
                ViewPoor.setBackgroundColor(Color.parseColor("#898989"));
            } else {
                strQuality = "GOOD!!";
                ViewFair.setBackgroundColor(Color.parseColor("#898989"));
                ViewGood.setBackgroundColor(Color.parseColor("#385E0F"));
                ViewPoor.setBackgroundColor(Color.parseColor("#898989"));

            }

            TvImageQuality.setText("Your image quality is " + strQuality);
            BtnClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            BtnContiue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    strDesignTitle = EtDesignName.getText().toString().trim();
                    if (strDesignTitle.isEmpty()) {
                        Toast.makeText(getActivity(), "Please enter your design title", Toast.LENGTH_LONG).show();
                    } else {

                        if (strGalleryPath.equalsIgnoreCase("camera")) {
                            String Path = manageBitmap(mBitmap);
                            addDesignViaRetrofit(Path);
                        } else {
                            addDesignViaRetrofit(strGalleryPath);
                        }

                        dialog.dismiss();

                    }

                }
            });
        }


    }
    private void uploadPicDialog(final String strGalleryPath, String ImageType, int height, int width) {


        Log.e(TAG, "onSelectFromGalleryResult: strGalleryPath " + strGalleryPath);

        if (strGalleryPath == null) {


            mUtilities.showDialog(getActivity(), "Please select image from gallery", "OK");

        } else {
            final Dialog dialog = new Dialog(getActivity(), android.R.style.Theme_Black_NoTitleBar);
            dialog.setContentView(R.layout.upload_style_design);
            dialog.show();

            IvDesignPic = (ImageView) dialog.findViewById(R.id.ivDesignPic);
            IvDialogBack = (ImageView) dialog.findViewById(R.id.ivBack);
            TvDialogTitle = (TextView) dialog.findViewById(R.id.tvTitle);
            TvImageQuality = (TextView) dialog.findViewById(R.id.tvImageQuality);
            EtDesignName = (EditText) dialog.findViewById(R.id.etDesignName);
            BtnClose = (Button) dialog.findViewById(R.id.btnClose);
            BtnContiue = (Button) dialog.findViewById(R.id.btnContinue);
            ViewPoor = (View) dialog.findViewById(R.id.viewPoor);
            ViewGood = (View) dialog.findViewById(R.id.viewGood);
            ViewFair = (View) dialog.findViewById(R.id.viewFair);

            tvCart = (TextView) dialog.findViewById(R.id.tvCartCount);
            if (Home.CartCount != 0) {
                tvCart.setText(Home.CartCount + "");
            } else {
                tvCart.setVisibility(View.GONE);
            }

            TvDialogTitle.setText("UPLOAD DESIGN");
            IvDialogBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            IvDesignPic.setImageBitmap(mBitmap);
            //int imageHeight = mBitmap.getHeight();
            //int imageWidth = mBitmap.getWidth();
            Log.d(TAG, "uploadPicDialog: imageHeight " + height + "imageWidth " + width);

            if (width < 150 || height < 150) {
                strQuality = "POOR!!";
                ViewFair.setBackgroundColor(Color.parseColor("#898989"));
                ViewGood.setBackgroundColor(Color.parseColor("#898989"));
                ViewPoor.setBackgroundColor(Color.parseColor("#CC1100"));

            } else if ((width >= 150 && width < 500) || (height >= 150 && height < 500)) {
                strQuality = "FAIR!!";
                ViewFair.setBackgroundColor(Color.parseColor("#FFA824"));
                ViewGood.setBackgroundColor(Color.parseColor("#898989"));
                ViewPoor.setBackgroundColor(Color.parseColor("#898989"));
            } else {
                strQuality = "GOOD!!";
                ViewFair.setBackgroundColor(Color.parseColor("#898989"));
                ViewGood.setBackgroundColor(Color.parseColor("#385E0F"));
                ViewPoor.setBackgroundColor(Color.parseColor("#898989"));

            }

            TvImageQuality.setText("Your image quality is " + strQuality);
            BtnClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            BtnContiue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    strDesignTitle = EtDesignName.getText().toString().trim();
                    if (strDesignTitle.isEmpty()) {
                        Toast.makeText(getActivity(), "Please enter your design title", Toast.LENGTH_LONG).show();
                    } else {

                        if (strGalleryPath.equalsIgnoreCase("camera")) {
                            String Path = manageBitmap(mBitmap);
                            addDesignViaRetrofit(Path);
                        } else {
                            addDesignViaRetrofit(strGalleryPath);
                        }

                        dialog.dismiss();

                    }

                }
            });
        }


    }

    private void addDesignViaRetrofit(String strGalleryPath) {


        mUtilities.showProgressDialog(getActivity(), "Adding Design...");

        Retrofit restAdapter = AddCustomDesignApi.retrofit;
        AddCustomDesignApi addCustomDesignApi = restAdapter.create(AddCustomDesignApi.class);

        MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
        File file_mine = new File(strGalleryPath);
        Log.e(TAG, "strGalleryPath: " + strGalleryPath);

        RequestBody title = RequestBody.create(MediaType.parse("text/plain"), strDesignTitle);
        RequestBody design_img = RequestBody.create(MEDIA_TYPE_PNG, file_mine);
        RequestBody user_id = RequestBody.create(MediaType.parse("text/plain"), mUserId);

        Call<JsonElement> call = addCustomDesignApi.addDesign(title, design_img, user_id);

        call.enqueue(new Callback<JsonElement>() {

            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (response.body() != null) {
                    Log.d(TAG, "onResponse: " + response.body());
                    mUtilities.hideProgressDialog();
                    parseAddDesignResponse(response.body().toString());
                }

            }

            @Override
            public void onFailure(Call<JsonElement> result, Throwable t) {
                mUtilities.hideProgressDialog();
                Log.e(TAG, "onFailure: " + result.toString());
            }
        });
    }

    private void parseAddDesignResponse(String response) {

        if (response != null) {

            try {
                JSONObject mJObj = new JSONObject(response);
                JSONObject mJsonObj = mJObj.getJSONObject("response");

                if (mJsonObj != null) {

                    if (mJsonObj.has("message") && mJsonObj.getString("message") != null) {
                        strMessage = mJsonObj.getString("message").toString().trim();
                    }

                    if (mJsonObj.has("code") && mJsonObj.getString("code") != null
                            && mJsonObj.getString("code").toString().equalsIgnoreCase("200")) {


                        if (mJsonObj.has("data")) {
                            JSONObject mDataJsonobj = mJsonObj.getJSONObject("data");
                            if (mDataJsonobj != null) {
                                if (mDataJsonobj.has("id") && mDataJsonobj.getString("id") != null) {
                                    strDesignId = mDataJsonobj.getString("id").toString();
                                    Log.d(TAG, "strDesignId " + strDesignId);

                                    Intent mIntent = new Intent(getActivity(), CustomDesignActivity.class);
                                    Log.d(TAG, "parseAddDesignResponse: strProductId " + strProductId);
                                    Log.d(TAG, "parseAddDesignResponse: mId " + mId);
                                    mIntent.putExtra("product_id", strProductId);
                                    mIntent.putExtra("design_id", strDesignId);
                                    mIntent.putExtra("get_bitmap", true);
                                    startActivity(mIntent);

                                }
                            }
                        }
                    } else {
                        Toast.makeText(getActivity(), strMessage, Toast.LENGTH_LONG).show();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (Exception ex) {
                // Error occurred while creating the File
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                photoURI = FileProvider.getUriForFile(getActivity(),
                        "com.virtualdusk.zezgn.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private File createImageFile() throws Exception {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";

        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

}
