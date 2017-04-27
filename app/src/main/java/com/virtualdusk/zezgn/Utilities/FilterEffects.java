package com.virtualdusk.zezgn.Utilities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.AsyncTask;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.renderscript.ScriptIntrinsicConvolve3x3;
import android.util.Log;
import android.widget.ImageView;

import com.virtualdusk.zezgn.InterfaceClasses.ReturnFilteredBitmap;
import com.virtualdusk.zezgn.R;
import com.virtualdusk.zezgn.activity.CustomDesignActivity;


/**
 * Created by bhart.gupta on 28-Oct-16.
 */

public class FilterEffects {

    private Context mContext;
    private static final float BITMAP_SCALE = 0.4f;
    private static final float BLUR_RADIUS = 7.5f;
    private Bitmap bitmap, sourceBitmap;
    private ImageView ImgFilter;
    private ReturnFilteredBitmap mReturnFilteredBitmap;
    float[] matrix_sharpen =
            {0, -1, 0,
                    -1, 5, -1,
                    0, -1, 0};

    double[][] EmbossConfig = new double[][]{
            {-1, 0, -1},
            {0, 4, 0},
            {-1, 0, -1}
    };


    public FilterEffects(Context mContext) {
        this.mContext = mContext;

    }

    public Bitmap applyfilter(String effectName, Bitmap sourceBitmap, ImageView ImgFilter,ReturnFilteredBitmap mReturnFilteredBitmap) {

        this.sourceBitmap = null;
        this.ImgFilter = null;
        this.sourceBitmap = sourceBitmap;

        this.ImgFilter = ImgFilter;
        this.mReturnFilteredBitmap = mReturnFilteredBitmap;
        Log.d("effectName ", effectName);


        if (effectName.equalsIgnoreCase(mContext.getResources().getString(R.string.none))) {
            Log.d("filter", "none");
            createNoFilter();

        } else if (effectName.equalsIgnoreCase(mContext.getResources().getString(R.string.sketch))) {
            Log.d("filter", "sketch");
            sketch();

        } else if (effectName.equalsIgnoreCase(mContext.getResources().getString(R.string.tint))) {
            Log.d("filter", "tint");
            tint();

        } else if (effectName.equalsIgnoreCase(mContext.getResources().getString(R.string.vignette))) {
            Log.d("filter", "vignette");
            vignette();

        } else if (effectName.equalsIgnoreCase(mContext.getResources().getString(R.string.linear))) {
            Log.d("filter", "linear");
            linearGradient();

        } else if (effectName.equalsIgnoreCase(mContext.getResources().getString(R.string.saturation))) {
            Log.d("filter", "saturation");
            saturation();

        } else if (effectName.equalsIgnoreCase(mContext.getResources().getString(R.string.sepia))) {
            Log.d("filter", "createSepia");
            createSepia();

        } else if (effectName.equalsIgnoreCase(mContext.getResources().getString(R.string.greyscale))) {
            Log.d("filter", "createGreyscale");
            createGreyscale();


        } else if (effectName.equalsIgnoreCase(mContext.getResources().getString(R.string.blur))) {
            Log.d("filter", "blur");
            blur();

        } else if (effectName.equalsIgnoreCase(mContext.getResources().getString(R.string.invert))) {
            Log.d("filter", "createInvertedBitmap");
            createInvertedBitmap();

        } else if (effectName.equalsIgnoreCase(mContext.getResources().getString(R.string.sharpen))) {
            Log.d("filter", "sharpen");
            sharpen();

        } else if (effectName.equalsIgnoreCase(mContext.getResources().getString(R.string.emboss))) {
            Log.d("filter", "emboss");
            sharpen();

        } else if (effectName.equalsIgnoreCase(mContext.getResources().getString(R.string.black_n_white))) {
            Log.d("filter", "black_white");
            blackNWhite();

        } else if (effectName.equalsIgnoreCase(mContext.getResources().getString(R.string.alpha_blue))) {
            Log.d("filter", "alpha_blue");
            createAlphaBlue();

        } else if (effectName.equalsIgnoreCase(mContext.getResources().getString(R.string.alpha_pink))) {
            Log.d("filter", "alpha_pink");
            createAlphaPink();

        } else {
            createNoFilter();
        }
//        else if (effectName.equalsIgnoreCase(mContext.getResources().getString(R.string.binary))) {
//            Log.d("filter", "binary");
//            createBinaryBitmap();
//
//        }



        return bitmap;
    }

    private void createNoFilter() {
        ImgFilter.setImageBitmap(sourceBitmap);
    }

    public void createSepia() {

        new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                ColorMatrix colorMatrix_Sepia = new ColorMatrix();
                colorMatrix_Sepia.setSaturation(0);

                ColorMatrix colorScale = new ColorMatrix();
                colorScale.setScale(1, 1, 0.8f, 1);

                colorMatrix_Sepia.postConcat(colorScale);

                ColorFilter ColorFilter_Sepia = new ColorMatrixColorFilter(
                        colorMatrix_Sepia);

                bitmap = Bitmap.createBitmap(sourceBitmap.getWidth(), sourceBitmap.getHeight(),
                        Bitmap.Config.RGB_565);
                Canvas canvas = new Canvas(bitmap);

                Paint paint = new Paint();

                paint.setColorFilter(ColorFilter_Sepia);
                canvas.drawBitmap(sourceBitmap, 0, 0, paint);
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                ImgFilter.setImageBitmap(bitmap);
                if(mReturnFilteredBitmap != null)
                mReturnFilteredBitmap.returnFilteredBitmap(bitmap);
                //CustomDesignActivity.IV_WorkspaceImage.setImageBitmap(bitmap);
                //CustomDesignActivity.mBitmap = bitmap;

            }
        }.execute();

    }


    public void blur() {


        new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {

                int width = Math.round(sourceBitmap.getWidth() * BITMAP_SCALE);
                int height = Math.round(sourceBitmap.getHeight() * BITMAP_SCALE);

                bitmap = Bitmap.createScaledBitmap(sourceBitmap, width, height, false);
                Bitmap outputBitmap = Bitmap.createBitmap(bitmap);

                RenderScript rs = RenderScript.create(mContext);
                ScriptIntrinsicBlur theIntrinsic = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
                Allocation tmpIn = Allocation.createFromBitmap(rs, bitmap);
                Allocation tmpOut = Allocation.createFromBitmap(rs, outputBitmap);
                theIntrinsic.setRadius(BLUR_RADIUS);
                theIntrinsic.setInput(tmpIn);
                theIntrinsic.forEach(tmpOut);
                tmpOut.copyTo(outputBitmap);
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                ImgFilter.setImageBitmap(bitmap);
                if(mReturnFilteredBitmap != null)
                mReturnFilteredBitmap.returnFilteredBitmap(bitmap);
                //CustomDesignActivity.IV_WorkspaceImage.setImageBitmap(bitmap);
                //// CustomDesignActivity.mBitmap = bitmap;
            }

        }.execute();


    }

    public void createGreyscale() {


        new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {

                int width, height;
                height = sourceBitmap.getHeight();
                width = sourceBitmap.getWidth();

                bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
                Canvas c = new Canvas(bitmap);
                Paint paint = new Paint();
                ColorMatrix cm = new ColorMatrix();
                cm.setSaturation(0);
                ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
                paint.setColorFilter(f);
                c.drawBitmap(sourceBitmap, 0, 0, paint);

                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                ImgFilter.setImageBitmap(bitmap);
                if(mReturnFilteredBitmap != null)
                mReturnFilteredBitmap.returnFilteredBitmap(bitmap);
                //CustomDesignActivity.IV_WorkspaceImage.setImageBitmap(bitmap);
                //// CustomDesignActivity.mBitmap = bitmap;


            }
        }.execute();

    }

    private void createInvertedBitmap() {

        new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {

                ColorMatrix colorMatrix_Inverted =
                        new ColorMatrix(new float[]{
                                -1, 0, 0, 0, 255,
                                0, -1, 0, 0, 255,
                                0, 0, -1, 0, 255,
                                0, 0, 0, 1, 0});

                ColorFilter ColorFilter_Sepia = new ColorMatrixColorFilter(
                        colorMatrix_Inverted);

                bitmap = Bitmap.createBitmap(sourceBitmap.getWidth(), sourceBitmap.getHeight(),
                        Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);

                Paint paint = new Paint();

                paint.setColorFilter(ColorFilter_Sepia);
                canvas.drawBitmap(sourceBitmap, 0, 0, paint);
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                ImgFilter.setImageBitmap(bitmap);
                if(mReturnFilteredBitmap != null)
                mReturnFilteredBitmap.returnFilteredBitmap(bitmap);
                //CustomDesignActivity.IV_WorkspaceImage.setImageBitmap(bitmap);
                //// CustomDesignActivity.mBitmap = bitmap;

            }
        }.execute();

    }

    private void sharpen() {

        new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {

                bitmap = Bitmap.createBitmap(sourceBitmap.getWidth(),
                        sourceBitmap.getHeight(), sourceBitmap.getConfig());

                RenderScript renderScript = RenderScript.create(mContext);

                Allocation input = Allocation.createFromBitmap(renderScript, sourceBitmap);
                Allocation output = Allocation.createFromBitmap(renderScript, bitmap);

                ScriptIntrinsicConvolve3x3 convolution = ScriptIntrinsicConvolve3x3
                        .create(renderScript, Element.U8_4(renderScript));
                convolution.setInput(input);
                convolution.setCoefficients(matrix_sharpen);
                convolution.forEach(output);

                output.copyTo(bitmap);
                renderScript.destroy();
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);

                ImgFilter.setImageBitmap(bitmap);
                if(mReturnFilteredBitmap != null)
                mReturnFilteredBitmap.returnFilteredBitmap(bitmap);
                //CustomDesignActivity.IV_WorkspaceImage.setImageBitmap(bitmap);
                //// CustomDesignActivity.mBitmap = bitmap;
            }
        }.execute();

    }

//    public void emboss() {
//
//
//        new AsyncTask() {
//            @Override
//            protected Object doInBackground(Object[] objects) {
//
//                convolutionMatrix convMatrix = new convolutionMatrix(3);
//                convMatrix.applyConfig(EmbossConfig);
//                convMatrix.Factor = 1;
//                convMatrix.Offset = 127;
//                bitmap =  convolutionMatrix.computeConvolution3x3(sourceBitmap, convMatrix);
//                return null;
//            }
//
//            @Override
//            protected void onPostExecute(Object o) {
//                super.onPostExecute(o);
//                ImgFilter.setImageBitmap(bitmap);
//            }
//        }.execute();
//
//
//    }

    private void blackNWhite() {

        new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {

                bitmap = Bitmap.createBitmap(
                        sourceBitmap.getWidth(), sourceBitmap.getHeight(), sourceBitmap.getConfig());

                for (int x = 0; x < sourceBitmap.getWidth(); x++) {
                    for (int y = 0; y < sourceBitmap.getHeight(); y++) {
                        int pixelColor = sourceBitmap.getPixel(x, y);
                        int pixelAlpha = Color.alpha(pixelColor);
                        int pixelRed = Color.red(pixelColor);
                        int pixelGreen = Color.green(pixelColor);
                        int pixelBlue = Color.blue(pixelColor);

                        int pixelBW = (pixelRed + pixelGreen + pixelBlue) / 3;
                        int newPixel = Color.argb(
                                pixelAlpha, pixelBW, pixelBW, pixelBW);

                        bitmap.setPixel(x, y, newPixel);
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                ImgFilter.setImageBitmap(bitmap);
                if(mReturnFilteredBitmap != null)
                mReturnFilteredBitmap.returnFilteredBitmap(bitmap);
                //CustomDesignActivity.IV_WorkspaceImage.setImageBitmap(bitmap);
                //// CustomDesignActivity.mBitmap = bitmap;
            }
        }.execute();

    }


    private void createBinaryBitmap() {

        new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {

//                ColorMatrix colorMatrix_Inverted =
//                        new ColorMatrix(new float[]{
//                                -1, 0, 0, 0, 255,
//                                0, -1, 0, 0, 255,
//                                0, 0, -1, 0, 255,
//                                0, 0, 0, 1, 0});
//
//                ColorFilter ColorFilter_Sepia = new ColorMatrixColorFilter(
//                        colorMatrix_Inverted);

                ColorMatrix colorMatrix = new ColorMatrix();
                colorMatrix.setSaturation(0);

                float m = 255f;
                float t = -255 * 128f;
                ColorMatrix threshold = new ColorMatrix(new float[]{
                        m, 0, 0, 1, t,
                        0, m, 0, 1, t,
                        0, 0, m, 1, t,
                        0, 0, 0, 1, 0
                });

                // Convert to grayscale, then scale and clamp
                colorMatrix.postConcat(threshold);
                ColorFilter ColorFilterBinary = new ColorMatrixColorFilter(
                        colorMatrix);

                bitmap = Bitmap.createBitmap(sourceBitmap.getWidth(), sourceBitmap.getHeight(),
                        Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);

                Paint paint = new Paint();

                paint.setColorFilter(ColorFilterBinary);
                canvas.drawBitmap(sourceBitmap, 0, 0, paint);
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                ImgFilter.setImageBitmap(bitmap);
                if(mReturnFilteredBitmap != null)
                mReturnFilteredBitmap.returnFilteredBitmap(bitmap);
                //CustomDesignActivity.IV_WorkspaceImage.setImageBitmap(bitmap);
                //// CustomDesignActivity.mBitmap = bitmap;

            }
        }.execute();

    }


    private void createAlphaBlue() {

        new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {

                ColorMatrix colorMatrix_Alphablue =
                        new ColorMatrix(new float[]{
                                0, 0, 0, 0, 0,
                                0.3f, 0, 0, 0, 50,
                                0, 0, 0, 0, 255,
                                0.2f, 0.4f, 0.4f, 0, -30
                        });


                ColorFilter ColorFilter_Alphablue = new ColorMatrixColorFilter(
                        colorMatrix_Alphablue);

                bitmap = Bitmap.createBitmap(sourceBitmap.getWidth(), sourceBitmap.getHeight(),
                        Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);

                Paint paint = new Paint();

                paint.setColorFilter(ColorFilter_Alphablue);
                canvas.drawBitmap(sourceBitmap, 0, 0, paint);
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                ImgFilter.setImageBitmap(bitmap);
                if(mReturnFilteredBitmap != null)
                mReturnFilteredBitmap.returnFilteredBitmap(bitmap);
                //CustomDesignActivity.IV_WorkspaceImage.setImageBitmap(bitmap);
                //// CustomDesignActivity.mBitmap = bitmap;

            }
        }.execute();

    }

    private void createAlphaPink() {

        new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {

                ColorMatrix colorMatrix_AlphaPink =
                        new ColorMatrix(new float[]{
                                0, 0, 0, 0, 255,
                                0, 0, 0, 0, 0,
                                0.2f, 0, 0, 0, 50,
                                0.2f, 0.2f, 0.2f, 0, -20
                        });


                ColorFilter ColorFilter_AlphaPink = new ColorMatrixColorFilter(
                        colorMatrix_AlphaPink);

                bitmap = Bitmap.createBitmap(sourceBitmap.getWidth(), sourceBitmap.getHeight(),
                        Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);

                Paint paint = new Paint();

                paint.setColorFilter(ColorFilter_AlphaPink);
                canvas.drawBitmap(sourceBitmap, 0, 0, paint);
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                ImgFilter.setImageBitmap(bitmap);
                if(mReturnFilteredBitmap != null)
                mReturnFilteredBitmap.returnFilteredBitmap(bitmap);
                //CustomDesignActivity.IV_WorkspaceImage.setImageBitmap(bitmap);
                //// CustomDesignActivity.mBitmap = bitmap;

            }
        }.execute();

    }


    public void linearGradient() {

        new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {


                final int GRADIENT_HEIGHT = 32;
                int w = sourceBitmap.getWidth();
                int h = sourceBitmap.getHeight();
                bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);

                canvas.drawBitmap(sourceBitmap, 0, 0, null);

                Paint paint = new Paint();
                LinearGradient shader = new LinearGradient(0, 0, 0, GRADIENT_HEIGHT, 0xFFFFFFFF, 0x00FFFFFF, Shader.TileMode.REPEAT);
                paint.setShader(shader);
                paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
                canvas.drawRect(0, h - GRADIENT_HEIGHT, w, h, paint);

                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                ImgFilter.setImageBitmap(bitmap);
                if(mReturnFilteredBitmap != null)
                mReturnFilteredBitmap.returnFilteredBitmap(bitmap);
                //CustomDesignActivity.IV_WorkspaceImage.setImageBitmap(bitmap);
                //// CustomDesignActivity.mBitmap = bitmap;

            }
        }.execute();

    }


    public void vignette() {

        new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {

                final int width = sourceBitmap.getWidth();
                final int height = sourceBitmap.getHeight();
                bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                float radius = (float) (width / 1.2);
                int[] colors = new int[]{0, 0x55000000, 0xff000000};
                float[] positions = new float[]{0.0f, 0.5f, 1.0f};

                RadialGradient gradient = new RadialGradient(width / 2, height / 2, radius, colors, positions, Shader.TileMode.CLAMP);
                Canvas canvas = new Canvas(bitmap);
                canvas.drawARGB(1, 0, 0, 0);

                final Paint paint = new Paint();
                paint.setAntiAlias(true);
                paint.setColor(Color.BLACK);
                paint.setShader(gradient);

                final Rect rect = new Rect(0, 0, sourceBitmap.getWidth(), sourceBitmap.getHeight());
                final RectF rectf = new RectF(rect);

                canvas.drawRect(rectf, paint);

                paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
                canvas.drawBitmap(sourceBitmap, rect, rect, paint);


                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                ImgFilter.setImageBitmap(bitmap);
                if(mReturnFilteredBitmap != null)
                mReturnFilteredBitmap.returnFilteredBitmap(bitmap);
                //CustomDesignActivity.IV_WorkspaceImage.setImageBitmap(bitmap);
                //// CustomDesignActivity.mBitmap = bitmap;

            }
        }.execute();

    }

    public void saturation() {


        new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {


                float f_value = (float) (200 / 100.0);

                int w = sourceBitmap.getWidth();
                int h = sourceBitmap.getHeight();

                bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
                Canvas canvasResult = new Canvas(bitmap);
                Paint paint = new Paint();
                ColorMatrix colorMatrix = new ColorMatrix();
                colorMatrix.setSaturation(f_value);
                ColorMatrixColorFilter filter = new ColorMatrixColorFilter(colorMatrix);
                paint.setColorFilter(filter);
                canvasResult.drawBitmap(sourceBitmap, 0, 0, paint);


                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                ImgFilter.setImageBitmap(bitmap);
                if(mReturnFilteredBitmap != null)
                mReturnFilteredBitmap.returnFilteredBitmap(bitmap);
                //CustomDesignActivity.IV_WorkspaceImage.setImageBitmap(bitmap);
                ////// CustomDesignActivity.mBitmap = bitmap;

            }
        }.execute();
    }

    public void tint() {

        new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {


                // image size
                int width = sourceBitmap.getWidth();
                int height = sourceBitmap.getHeight();
                // create output bitmap

                // create a mutable empty bitmap
                bitmap = Bitmap.createBitmap(width, height, sourceBitmap.getConfig());

                Paint p = new Paint(Color.RED);
                ColorFilter filter = new LightingColorFilter(0xFF1E8D24, 1);
                p.setColorFilter(filter);

                Canvas c = new Canvas();
                c.setBitmap(bitmap);
                c.drawBitmap(sourceBitmap, 0, 0, p);


                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                ImgFilter.setImageBitmap(bitmap);
                if(mReturnFilteredBitmap != null)
                mReturnFilteredBitmap.returnFilteredBitmap(bitmap);
                //CustomDesignActivity.IV_WorkspaceImage.setImageBitmap(bitmap);
                //// CustomDesignActivity.mBitmap = bitmap;

            }
        }.execute();

    }

    public void sketch() {

        new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {


                int type = 6;
                int threshold = 130;

                int width = sourceBitmap.getWidth();
                int height = sourceBitmap.getHeight();
                bitmap = Bitmap.createBitmap(width, height, sourceBitmap.getConfig());

                int A, R, G, B;
                int sumR, sumG, sumB;
                int[][] pixels = new int[3][3];
                for (int y = 0; y < height - 2; ++y) {
                    for (int x = 0; x < width - 2; ++x) {
                        //      get pixel matrix
                        for (int i = 0; i < 3; ++i) {
                            for (int j = 0; j < 3; ++j) {
                                pixels[i][j] = sourceBitmap.getPixel(x + i, y + j);
                            }
                        }
                        // get alpha of center pixel
                        A = Color.alpha(pixels[1][1]);
                        // init color sum
                        sumR = sumG = sumB = 0;
                        sumR = (type * Color.red(pixels[1][1])) - Color.red(pixels[0][0]) - Color.red(pixels[0][2]) - Color.red(pixels[2][0]) - Color.red(pixels[2][2]);
                        sumG = (type * Color.green(pixels[1][1])) - Color.green(pixels[0][0]) - Color.green(pixels[0][2]) - Color.green(pixels[2][0]) - Color.green(pixels[2][2]);
                        sumB = (type * Color.blue(pixels[1][1])) - Color.blue(pixels[0][0]) - Color.blue(pixels[0][2]) - Color.blue(pixels[2][0]) - Color.blue(pixels[2][2]);
                        // get final Red
                        R = (int) (sumR + threshold);
                        if (R < 0) {
                            R = 0;
                        } else if (R > 255) {
                            R = 255;
                        }
                        // get final Green
                        G = (int) (sumG + threshold);
                        if (G < 0) {
                            G = 0;
                        } else if (G > 255) {
                            G = 255;
                        }
                        // get final Blue
                        B = (int) (sumB + threshold);
                        if (B < 0) {
                            B = 0;
                        } else if (B > 255) {
                            B = 255;
                        }

                        bitmap.setPixel(x + 1, y + 1, Color.argb(A, R, G, B));
                    }
                }

                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                ImgFilter.setImageBitmap(bitmap);
                if(mReturnFilteredBitmap != null)
                mReturnFilteredBitmap.returnFilteredBitmap(bitmap);
                //CustomDesignActivity.IV_WorkspaceImage.setImageBitmap(bitmap);


            }
        }.execute();


    }
}
