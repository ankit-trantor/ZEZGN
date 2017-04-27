package com.virtualdusk.zezgn.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;

import com.virtualdusk.zezgn.R;
import com.virtualdusk.zezgn.Profile.EditUserProfileFragment;


// call when image capture from camera
public class GetProfilePicPopUp extends Activity {

    private LinearLayout gallery_layout, cancel, camera_layout;
    private static final int CAMERA_PHOTO = 1;
    private static final int Gallery_PHOTO = 2;
    private ProgressDialog progress_dialog;
    private String imgPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_profile_pic_pop_up);

        camera_layout = (LinearLayout) findViewById(R.id.camera_layout);
        gallery_layout = (LinearLayout) findViewById(R.id.gallery_layout);
        cancel = (LinearLayout) findViewById(R.id.cancel);

        progress_dialog = new ProgressDialog(GetProfilePicPopUp.this);
        progress_dialog.setMessage(getString(R.string.please_wait));

        camera_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCamera();
            }
        });

        gallery_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImageFromGallery();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GetProfilePicPopUp.this.finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        progress_dialog = new ProgressDialog(GetProfilePicPopUp.this);
        progress_dialog.setMessage(getString(R.string.please_wait));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == CAMERA_PHOTO && resultCode == RESULT_OK) {
                AddPhoto.bytess = new byte[102400];
                AddPhoto.bytess = new byte[102400];
                AddPhoto.mImage = getImagePath();

                EditUserProfileFragment.bytess = new byte[102400];
                EditUserProfileFragment.mImage = getImagePath();
                setCapturedImage(getImagePath());

                finish();
            } else if (requestCode == Gallery_PHOTO && resultCode == RESULT_OK) {
                AddPhoto.bytess = new byte[102400];
                AddPhoto.bytess = new byte[102400];
                Uri selectedImage = data.getData();
                //finish();
                Bitmap bitmap;
                try {
                    bitmap = getBitmap(selectedImage);
                    String picturePath = getRealPathFromURI(selectedImage, this);
                    Bitmap scaledBitmap = ExifUtil.rotateBitmap(picturePath, bitmap);

                    AddPhoto.mImage = picturePath;
                    EditUserProfileFragment.mImage = picturePath;
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                    AddPhoto.user_pic_bitmapp = bitmap;
                    AddPhoto.bytess = outputStream.toByteArray();
                    AddPhoto.bytess = outputStream.toByteArray();


                //    EditUserProfileFragment.user_pic_bitmapp = bitmap;
               //     EditUserProfileFragment.bytess = outputStream.toByteArray();


                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                finish();
            }
        } catch (Exception e) {

        }
    }

    public String getRealPathFromURI(Uri contentURI, Activity context) {
        String[] projection = {MediaStore.Images.Media.DATA};
        @SuppressWarnings("deprecation")
        Cursor cursor = context.managedQuery(contentURI, projection, null,
                null, null);
        if (cursor == null)
            return null;
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        if (cursor.moveToFirst()) {
            String s = cursor.getString(column_index);
            // cursor.close();
            return s;
        }
        // cursor.close();
        return null;
    }

    public static Bitmap scaleDown(Bitmap realImage, float maxImageSize,
                                   boolean filter) {
        float ratio = Math.min(
                (float) maxImageSize / realImage.getWidth(),
                (float) maxImageSize / realImage.getHeight());
        int width = Math.round((float) ratio * realImage.getWidth());
        int height = Math.round((float) ratio * realImage.getHeight());

        Bitmap newBitmap = Bitmap.createScaledBitmap(realImage, width,
                height, filter);
        return newBitmap;
    }

    public void selectImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, Gallery_PHOTO);
    }

    private void startCamera() {
        // Checking camera availability
        if (!isDeviceSupportCamera()) {
            Toast.makeText(getApplicationContext(),
                    "Sorry! Your device doesn't support camera",
                    Toast.LENGTH_LONG).show();
        } else {
            final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, setImageUri());
            startActivityForResult(intent, CAMERA_PHOTO);
        }

    }

    /**
     * Checking device has camera hardware or not
     */
    private boolean isDeviceSupportCamera() {
        if (getApplicationContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }


    private Bitmap getBitmap(Uri uri) {

        InputStream in = null;
        try {
            final int IMAGE_MAX_SIZE = 1200000; // 1.2MP
            in = getContentResolver().openInputStream(uri);

            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(in, null, o);
            in.close();


            int scale = 1;
            while ((o.outWidth * o.outHeight) * (1 / Math.pow(scale, 2)) >
                    IMAGE_MAX_SIZE) {
                scale++;
            }


            Bitmap b = null;
            in = getContentResolver().openInputStream(uri);
            if (scale > 1) {
                scale--;
                // scale to max possible inSampleSize that still yields an image
                // larger than target
                o = new BitmapFactory.Options();
                o.inSampleSize = scale;
                b = BitmapFactory.decodeStream(in, null, o);

                // resize to desired dimensions
                int height = b.getHeight();
                int width = b.getWidth();


                double y = Math.sqrt(IMAGE_MAX_SIZE
                        / (((double) width) / height));
                double x = (y / height) * width;

                Bitmap scaledBitmap = Bitmap.createScaledBitmap(b, (int) x,
                        (int) y, true);
                b.recycle();
                b = scaledBitmap;

                System.gc();
            } else {
                b = BitmapFactory.decodeStream(in);
            }
            in.close();


            return b;
        } catch (IOException e) {
            return null;
        }

    }


    private String rotateImage(int degree, String imagePath) {

        if (degree <= 0) {
            return imagePath;
        }
        try {
            Bitmap b = BitmapFactory.decodeFile(imagePath);

            Matrix matrix = new Matrix();
            if (b.getWidth() > b.getHeight()) {
                matrix.setRotate(degree);
                b = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(),
                        matrix, true);
            }

            FileOutputStream fOut = new FileOutputStream(imagePath);
            String imageName = imagePath.substring(imagePath.lastIndexOf("/") + 1);
            String imageType = imageName.substring(imageName.lastIndexOf(".") + 1);

            FileOutputStream out = new FileOutputStream(imagePath);
            if (imageType.equalsIgnoreCase("png")) {
                b.compress(Bitmap.CompressFormat.PNG, 100, out);
            } else if (imageType.equalsIgnoreCase("jpeg") || imageType.equalsIgnoreCase("jpg")) {
                b.compress(Bitmap.CompressFormat.JPEG, 100, out);
            }
            fOut.flush();
            fOut.close();

            b.recycle();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imagePath;
    }

    private void setCapturedImage(final String imagePath) {
        new AsyncTask<Void, Void, String>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progress_dialog.show();
            }

            @Override
            protected String doInBackground(Void... params) {
                try {
                    Log.e("start", "start");
                    return imagePath;
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                return imagePath;
            }

            @Override
            protected void onPostExecute(String imagePath) {
                super.onPostExecute(imagePath);
                // progress_dialog.dismiss();
                Bitmap bn = decodeFile(imagePath);
                AddPhoto.mImage = imagePath;
                Bitmap bitmap = null;
                try {
                    bitmap = modifyOrientation(bn, imagePath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                AddPhoto.user_pic_bitmapp = bitmap;
           //     EditUserProfileFragment.user_pic_bitmapp = bitmap;

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

                AddPhoto.user_pic_bitmapp.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                AddPhoto.bytess = outputStream.toByteArray();

             //   EditUserProfileFragment.user_pic_bitmapp.compress(Bitmap.CompressFormat.JPEG, 30, outputStream);
            //    EditUserProfileFragment.bytess = outputStream.toByteArray();

                finish();

            }
        }.execute();
    }

    public Bitmap decodeFile(String path) {
        try {
            // Decode deal_image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, o);
            // The new size we want to scale to
            final int REQUIRED_SIZE = 1024;

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while (o.outWidth / scale / 2 >= REQUIRED_SIZE && o.outHeight / scale / 2 >= REQUIRED_SIZE)
                scale *= 2;
            // Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeFile(path, o2);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getAbsolutePath(Uri uri) {
        if (Build.VERSION.SDK_INT >= 19) {
            String id = "";
            if (uri.getLastPathSegment().split(":").length > 1)
                id = uri.getLastPathSegment().split(":")[1];
            else if (uri.getLastPathSegment().split(":").length > 0)
                id = uri.getLastPathSegment().split(":")[0];
            if (id.length() > 0) {
                final String[] imageColumns = {MediaStore.Images.Media.DATA};
                final String imageOrderBy = null;
                Uri tempUri = getUri();
                Cursor imageCursor = getContentResolver().query(tempUri, imageColumns, MediaStore.Images.Media._ID + "=" + id, null, imageOrderBy);
                if (imageCursor.moveToFirst()) {
                    return imageCursor.getString(imageCursor.getColumnIndex(MediaStore.Images.Media.DATA));
                } else {
                    return null;
                }
            } else {
                return null;
            }
        } else {
            String[] projection = {MediaStore.MediaColumns.DATA};
            Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
            if (cursor != null) {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                cursor.moveToFirst();
                return cursor.getString(column_index);
            } else
                return null;
        }

    }

    private Uri getUri() {
        String state = Environment.getExternalStorageState();
        if (!state.equalsIgnoreCase(Environment.MEDIA_MOUNTED))
            return MediaStore.Images.Media.INTERNAL_CONTENT_URI;

        return MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    }

    public Uri setImageUri() {
        Uri imgUri;
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            File file = new File(Environment.getExternalStorageDirectory() + "/DCIM/", getString(R.string.app_name) + Calendar.getInstance().getTimeInMillis() + ".jpg");
            imgUri = Uri.fromFile(file);
            imgPath = file.getAbsolutePath();
        } else {
            File file = new File(getFilesDir(), getString(R.string.app_name) + Calendar.getInstance().getTimeInMillis() + ".jpg");
            imgUri = Uri.fromFile(file);
            this.imgPath = file.getAbsolutePath();
        }
        return imgUri;
    }

    public String getImagePath() {
        return imgPath;
    }

    public static Bitmap rotate(Bitmap bitmap, float degrees) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    public static Bitmap modifyOrientation(Bitmap bitmap, String image_absolute_path) throws IOException {
        ExifInterface ei = new ExifInterface(image_absolute_path);
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotate(bitmap, 90);

            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotate(bitmap, 180);

            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotate(bitmap, 270);

            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                return flip(bitmap, true, false);

            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                return flip(bitmap, false, true);

            default:
                return bitmap;
        }
    }


    public static Bitmap flip(Bitmap bitmap, boolean horizontal, boolean vertical) {
        Matrix matrix = new Matrix();
        matrix.preScale(horizontal ? -1 : 1, vertical ? -1 : 1);

        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);


    }
}





