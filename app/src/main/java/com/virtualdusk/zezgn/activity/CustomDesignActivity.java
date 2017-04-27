package com.virtualdusk.zezgn.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonElement;
import com.squareup.picasso.Picasso;
import com.virtualdusk.zezgn.InterfaceClasses.OnColorListClickListner;
import com.virtualdusk.zezgn.InterfaceClasses.OnEffectListClickListner;
import com.virtualdusk.zezgn.InterfaceClasses.OnFontsClickListner;
import com.virtualdusk.zezgn.InterfaceClasses.OnProductColorChangeListner;
import com.virtualdusk.zezgn.InterfaceClasses.ParseStyleInterface;
import com.virtualdusk.zezgn.InterfaceClasses.ReturnFilteredBitmap;
import com.virtualdusk.zezgn.Model.EdittextModalClass;
import com.virtualdusk.zezgn.Model.LastTextInProduct;
import com.virtualdusk.zezgn.Model.ProductStyleColor;
import com.virtualdusk.zezgn.Model.Style;
import com.virtualdusk.zezgn.MyApplication;
import com.virtualdusk.zezgn.R;
import com.virtualdusk.zezgn.Utilities.Constant;
import com.virtualdusk.zezgn.Utilities.FilterEffects;
import com.virtualdusk.zezgn.Utilities.MarshmallowPermissions;
import com.virtualdusk.zezgn.Utilities.SaveRecords;
import com.virtualdusk.zezgn.Utilities.Utilities;
import com.virtualdusk.zezgn.activity.Adapter.FontsAdapter;
import com.virtualdusk.zezgn.activity.Adapter.ImageFiltersAdapter;
import com.virtualdusk.zezgn.activity.Adapter.ProductColorsAdapter;
import com.virtualdusk.zezgn.activity.Adapter.ProductStyleAdapter;
import com.virtualdusk.zezgn.activity.Adapter.TshirtColorsAdapter;
import com.virtualdusk.zezgn.activity.Products.ProductFragment;
import com.virtualdusk.zezgn.api.AddCustomDesignApi;
import com.virtualdusk.zezgn.api.AddToCartApi;
import com.virtualdusk.zezgn.api.SaveProductApi;
import com.virtualdusk.zezgn.api.StyleApi;
import com.virtualdusk.zezgn.api.parseStyleApiResponse;
import com.virtualdusk.zezgn.utils.ConnectionDetector;
import com.virtualdusk.zezgn.utils.helper.AppUtils;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class CustomDesignActivity extends AbstractActivity implements View.OnClickListener,
        OnColorListClickListner, OnFontsClickListner,
        OnEffectListClickListner, ParseStyleInterface, OnProductColorChangeListner,  com.virtualdusk.zezgn.InterfaceClasses.DialogInterface, ReturnFilteredBitmap {

    private String TAG = CustomDesignActivity.class.getSimpleName().toString();

    // TEXT controls widget

    private ImageView fontImageview, textcolorImageview, filcolorImageview;
    // Area in which text nad image can do customize
    private RelativeLayout RL_Workspace1, RL_CaptureBitmap,LL_AddText;

    // create dynamically on add text button click
    private EditText et_dynamic = null;

    // default size of above edittext
    private Float textsize = 16f;

    // Adapters for colors list
    private TshirtColorsAdapter mTshirtColorsAdapter;

    private int _xDelta, _yDelta, tag = 1;
    private RecyclerView RV_Colors;
    //private int[] colorsList;
    private String[] colorsList = {"#000000","#00FFFF", "#7FFF00", "#DC143C", "#00008B",
            "#B8860B", "#8B008B", "#E9967A", "#FF1493", "#228B22", "#DAA520", "#FF69B4", "#FF00FF", "#FF4500", "#FF0000", "#2E8B57", "#9ACD32", "#FFFFFF"};
    private String[] filterEffectList;
    private String user_id;
    // Adapters for fonts list
    private FontsAdapter mFontsAdapter;
    private RecyclerView RV_Fonts;
    private ArrayList<String> fontList = new ArrayList<>();
    private OnFontsClickListner mOnFontsClickListner;

    private boolean isBackgroundColorSelected = false, isTextColorSelected = true,isFontChanger = false;

    private ImageView Imagedeleteicon, IV_BackgroundColor, IV_WorkspaceImage, IV_TextColor,IV_Tshirt1, IvSaveDesign, IvBack, IvReset;
    //public static ImageView ;
    public static  ArrayList<Style> productStyleList1 = new ArrayList<>();
    private OnColorListClickListner mOnColorListClickListner;
    private ArrayList<EdittextModalClass> edittextArraylist = new ArrayList<>();
    private Animation animZoomIn, animZoomOut;

    // these matrices will be used to move and zoom image
    private Matrix matrix = new Matrix();
    private Matrix savedMatrix = new Matrix();
    // we can be in one of these 3 states
    private static final int NONE = 0, DRAG = 1, ZOOM = 2;
    private int mode = NONE;
    // remember some things for zooming
    private PointF start = new PointF();
    private PointF mid = new PointF();
    private float oldDist = 1f, d = 0f, newRot = 0f, oldR = 0f, saveR = 0f;
    private float[] lastEvent = null;

    // variable for capturing images from camera and gallery
    private String userChoosenTask;
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;

    // widgets for text, filters, edit


    private TextView  TvTitle;


    float mRatio = 1.0f;
    int mBaseDist;
    float mBaseRatio;
    float fontsize = 13;
    final static float STEP = 200;
    // animate to hide

    private LinearLayout LL_Animate;
    private RelativeLayout  RL_Middle;
    private Animation animShow, animHide;

    private LinearLayout LL_BackgroundColor, LL_TextColor, LLFontchanger;
    // Edit Contrls

    private ImageView  IvCart;

    private OnEffectListClickListner mOnEffectListClickListner;

    private RelativeLayout laysave,layreset;

    private Bitmap mBitmap, backupBitmap;
    private ImageFiltersAdapter mImageFiltersAdapter;
    private String strMessage = "";

    private ArrayList<Style> productStyleList = new ArrayList<>();
    private ParseStyleInterface mParseStyleInterface;

    private Spinner mSpinnerProduct;

    private RecyclerView RV_productColor;
    private OnProductColorChangeListner mOnProductColorChangeListner;
    private LinearLayoutManager productColorLayoutManagaer;
    private Bitmap mProductBitmap, bitmapFromUrl;

    private LinearLayout LLTextControls;
    private String strDesignImageUrl = "";

    private ImageView IvDesignPic, IvDialogBack;
    private TextView TvImageQuality, TvDialogTitle, TvCart;
    private View ViewPoor, ViewFair, ViewGood;
    private EditText EtDesignName;
    private Button BtnClose, BtnContiue, BtnAddCart;
    private String strQuality = "", mImageName = "";
    private Bitmap capturedBitmap;

    private String strFinalProductId = "", strFinalDisplayOrder = "",
            strFinalProductHeight = "", strFinalProductPrice = "", strFinalFilterEffectName = "",
            strFinalProductStatus = "", strFinalProductImage = "", strFinalProductName = "", strFinalProductWidth = "", strFinalProductColor = "",
            strFinalProductBrightness = "", strFinalProductContrast = "", strFinalProductTransparent = "",
            strFinalProductX1 = "", strFinalProductY1 = "", strFinalProductX2 = "", strFinalProductY2 = "", strFinalProductW = "", strFinalProductH = "";

    private int dynamicEdittextListPosition;

    private SharedPreferences sharedPreferences;
    private String strFinalproductImagePath = "";
    private String strproductStyleId = "'";
    private String mUserId = "";
    private ProgressDialog progressDialog;
    private RelativeLayout Rlcart,Rldeleterl,RltvSpinneTextsp;
    private boolean is_Edit;

    private String strDesignImage = "", strProductImage = "", strId = "", strTitle = "", strProductStyleId = "", strModifiedDate = "",
            strProductView = "", strProductId = "", strDesignText = "", strDesignId = "", strproductIdX = "", strUserId = "", strDesignCategoryId = "", strDesignTitle = "";

    private String strLastProductColor = "", strLastProductName = "", strLastproductImage = "", strLastImageScale = "", strLastImageMidX = "",
            strLastImageMidY = "", strLastProductFilter = "", strLastProductBrightness = "", strLastProductContrast = "", strLastProductTransparency = "",
            strLastImageDragX = "", strLastImageDragY = "", strLastImageRotateR = "", strLastImageRotateTx = "", strLastImageRotateXC = "",
            strLastImageRotateTY = "", strLastImageRotateYC = "", strLastImageString = "",
            strLastProductX1 = "", strLastProductY1 = "", strLastProductX2 = "", strLastProductY2 = "", strLastProductW = "", strLastProductH = "";

    private ArrayList<LastTextInProduct> ListLastTextInProduct = new ArrayList<>();

    private String lastProductText = "", lastProductTextFontColor = null, lastProductTextFontBgColor = "", lastProductTextFontSize = "",
            lastProductTextFontName = "", lastLeft = "", lastBottom = "", lastTop = "", lastRight = "", lastTvH = "", lastTvW = "", lastTvX = "", lastTvY = "";
    private String strLastProductImageMatrix = "";
    private String strImageScale = "", strMidX = "", strMidY = "", strDragX = "", strDragY = "", strRotateR = "", strRotateTX = "", strRotateXC = "",
            strRotateTY = "", strRotateYC = "";
    private float spacingF, spacingX, spacingY, oldScale = 0f;

    private String strScaleX = "", strScaley = "", strSkewX = "", strSkewY = "", strTransalteX = "", strTransalteY = "", strPersp0 = "", strPersp1 = "", strPersp2 = "";


    //variable for counting two successive up-down events
    int clickCount = 0, imageclickcount = 0;
    //variable for storing the time of first click
    long startTime;
    //variable for calculating the total time
    long duration;
    //constant for defining the time duration between the click that can be considered as double-tap
    static final int MAX_DURATION = 500;
    private Utilities mUtilities;
    private  com.virtualdusk.zezgn.InterfaceClasses.DialogInterface mZezignDialogInterface;
    private boolean touchOne = false;
    private boolean isBitmapAvailable = false;


    static final int REQUEST_TAKE_PHOTO = 1;
    private String mCurrentPhotoPath;
    private Uri photoURI;

    private TextView tvSpinneText;
    private ImageView Img_ColorSymbol;
    private ProductColorsAdapter mProductColorsAdapter;
    private boolean isProductColorsListVisible = false;
    private ArrayList<ProductStyleColor> productColor = new ArrayList<>();
    private int calWidth, calHeight;
    private boolean imageFilterSet = false;

    float leftOffset, topOffset;
    private boolean isBitmapSetInCenter = false;
    private boolean isBitmapDownloaded = false;

    private ReturnFilteredBitmap mReturnFilteredBitmap;
    private boolean isOnlyTextModeApplicable = true;

    private boolean is_Edit_Cart = false;
    private String strIsDesignUploaded = "0";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        user_id = Home.sharedPreferences.getString(getString(R.string.key_user_id), null);
        mUtilities = new Utilities();

        Bundle extras = getIntent().getExtras();
        sharedPreferences = this.getAppSharedPreferences();

        if (extras != null) {
            String strEdit = sharedPreferences.getString("is_edit", "");
            is_Edit = extras.getBoolean(Constant.IS_EDIT, false);
            is_Edit_Cart = extras.getBoolean(Constant.IS_EDIT_CART, false);
            if (strEdit.equalsIgnoreCase("1")) {
                is_Edit = false;

                SaveRecords.saveToPreference("is_edit", "", CustomDesignActivity.this);

                strProductId = sharedPreferences.getString("str_product_id", "");
                strDesignImageUrl = sharedPreferences.getString("str_design_image", "");
                strDesignId = sharedPreferences.getString("str_design_id", "");
                strDesignText = sharedPreferences.getString("str_design_text", "");
                String strIsbitmapAvail = sharedPreferences.getString("get_bitmap", "");
                if (strIsbitmapAvail.equalsIgnoreCase("1")) {
                    isBitmapAvailable = true;
                } else if (strIsbitmapAvail.equalsIgnoreCase("0")) {
                    isBitmapAvailable = false;
                } else {
                    isBitmapAvailable = false;
                }
            }

            if (!is_Edit) {
                strProductId = extras.getString("product_id", strProductId);
                strDesignImageUrl = extras.getString("design_image", strDesignImageUrl);
                strDesignId = extras.getString("design_id", strDesignId);
                strDesignText = extras.getString("design_text", strDesignText);
                isBitmapAvailable = extras.getBoolean("get_bitmap", isBitmapAvailable);

            } else {
                strId = extras.getString(Constant.STR_ID, "");
                strTitle = extras.getString(Constant.STR_TITLE, "");
                strProductView = extras.getString(Constant.STR_PRODUCT_VIEW, "");

                strProductImage = extras.getString(Constant.STR_PRODUCT_IMAGE, "");
                strProductId = extras.getString(Constant.STR_PRODUCT_ID, "");
                strDesignId = extras.getString(Constant.STR_DESIGN_ID, "");
                strproductIdX = extras.getString(Constant.STR_PRODUCT_ID_X, "");
                strProductStyleId = extras.getString(Constant.STR_PRODUCT_STYLE_ID, "");
                strUserId = extras.getString(Constant.STR_USER_ID, "");
                strDesignCategoryId = extras.getString(Constant.STR_DESIGN_CATEGORY_ID, "");
                strDesignTitle = extras.getString(Constant.STR_DESIGN_TITLE, "");
                strDesignImage = extras.getString(Constant.STR_DESIGN_IMAGE, "");
                strModifiedDate = extras.getString(Constant.STR_MODIFIED_DATE, "");
                strIsDesignUploaded = extras.getString(Constant.STR_IS_DESIGN_UPLOADED, "");

                strDesignImageUrl = Constant.IMAGE_BASE_URL_Designs + strDesignImage;

                setEditingimage();
            }


        }
        mUtilities = new Utilities();

        if (strProductId.equalsIgnoreCase("1")) {
            setContentView(R.layout.activity_custom_design);
        } else if (strProductId.equalsIgnoreCase("2")) {
            setContentView(R.layout.activity_custom_design_phonecover);
        } else if (strProductId.equalsIgnoreCase("3")) {
            setContentView(R.layout.activity_custom_design_cup);
        }


        mUserId = sharedPreferences.getString(getString(R.string.key_user_id), null);

        initializeViews();
        createFontList();
        mReturnFilteredBitmap = this;
        mOnColorListClickListner = this;
        mOnFontsClickListner = this;
        mOnEffectListClickListner = this;
        mParseStyleInterface = this;
        mOnProductColorChangeListner = this;
        mZezignDialogInterface = this;

        filterEffectList = this.getResources().getStringArray(R.array.filters_list);

        mTshirtColorsAdapter = new TshirtColorsAdapter(colorsList, this, mOnColorListClickListner);

        mFontsAdapter = new FontsAdapter(fontList, this, mOnFontsClickListner);

//        LinearLayoutManager horizontalLayoutManagaer
//                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
//        RV_Colors.setLayoutManager(horizontalLayoutManagaer);
//        RV_Colors.setAdapter(mTshirtColorsAdapter);

        productColorLayoutManagaer = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

//        LinearLayoutManager FontsLayoutManagaer
//                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
//        RV_Fonts.setLayoutManager(FontsLayoutManagaer);
//        RV_Fonts.setAdapter(mFontsAdapter);

        LinearLayoutManager FontsLayoutManagaer
                = new LinearLayoutManager(CustomDesignActivity.this, LinearLayoutManager.HORIZONTAL, false);
        RV_Fonts.setLayoutManager(FontsLayoutManagaer);
        RV_Fonts.setAdapter(mFontsAdapter);
        RV_Fonts.setVisibility(View.GONE);
        LinearLayoutManager horizontalLayoutManagaer
                = new LinearLayoutManager(CustomDesignActivity.this, LinearLayoutManager.HORIZONTAL, false);
        RV_Colors.setLayoutManager(horizontalLayoutManagaer);
        RV_Colors.setAdapter(mTshirtColorsAdapter);
        RV_Colors.setVisibility(View.GONE);



        LLFontchanger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isBackgroundColorSelected = false;
                filcolorImageview.setImageDrawable(getResources().getDrawable(R.mipmap.fil_icon));
                isTextColorSelected = false;
                textcolorImageview.setImageDrawable(getResources().getDrawable(R.mipmap.text_iconc));
                RV_Fonts.setVisibility(View.VISIBLE);
                RV_Colors.setVisibility(View.GONE);

                if(!isFontChanger){
                    isFontChanger=true;
                    fontImageview.setImageDrawable(getResources().getDrawable(R.mipmap.font_change));
                }



            }
        });

        LL_TextColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isFontChanger=false;
                fontImageview.setImageDrawable(getResources().getDrawable(R.mipmap.font));
                isBackgroundColorSelected = false;
                filcolorImageview.setImageDrawable(getResources().getDrawable(R.mipmap.fil_icon));
                RV_Colors.setVisibility(View.VISIBLE);
                RV_Fonts.setVisibility(View.GONE);
                if (!isTextColorSelected) {
                    isTextColorSelected = true;

                    textcolorImageview.setImageDrawable(getResources().getDrawable(R.mipmap.textchange));
                }

            }
        });

        LL_BackgroundColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                isFontChanger=false;
                fontImageview.setImageDrawable(getResources().getDrawable(R.mipmap.font));
                isTextColorSelected = false;
                textcolorImageview.setImageDrawable(getResources().getDrawable(R.mipmap.text_iconc));
                RV_Colors.setVisibility(View.VISIBLE);
                RV_Fonts.setVisibility(View.GONE);
                if (!isBackgroundColorSelected) {
                    isBackgroundColorSelected = true;

                    filcolorImageview.setImageDrawable(getResources().getDrawable(R.mipmap.fillchange));
                }


            }
        });



        setZoomRotateOperationOnImage();
        initAnimation();
        onFilterClick();
        setSpinner();


        if (new ConnectionDetector(CustomDesignActivity.this).isConnectingToInternet()) {
            getStylesViaRetrofit();
        } else {
            showToast(getString(R.string.no_internet));
        }


    }

    private void setEditingimage() {

        if (strProductView != null) {
            try {
                Log.d(TAG, "setEditingimage: strProductView " + strProductView);
                JSONObject mProductJson = new JSONObject(strProductView);

                if (mProductJson != null) {
                    if (mProductJson.has("product_name") && mProductJson.getString("product_name") != null) {

                        strLastProductName = mProductJson.getString("product_name").toString();
                    }
                    if (mProductJson.has("product_image") && mProductJson.getString("product_image") != null) {

                        strLastproductImage = mProductJson.getString("product_image").toString();
                    }
                    if (mProductJson.has("product_color") && mProductJson.getString("product_color") != null) {

                        strLastProductColor = mProductJson.getString("product_color").toString();
                    }
                    if (mProductJson.has("product_filter") && mProductJson.getString("product_filter") != null) {

                        strLastProductFilter = mProductJson.getString("product_filter").toString();
                    }
                    if (mProductJson.has("product_brightness") && mProductJson.getString("product_brightness") != null) {

                        strLastProductBrightness = mProductJson.getString("product_brightness").toString();
                    }
                    if (mProductJson.has("product_Contrast") && mProductJson.getString("product_Contrast") != null) {

                        strLastProductContrast = mProductJson.getString("product_Contrast").toString();
                    }
                    if (mProductJson.has("product_transparency") && mProductJson.getString("product_transparency") != null) {

                        strLastProductTransparency = mProductJson.getString("product_transparency").toString();
                    }
                    if (mProductJson.has("scale") && mProductJson.getString("scale") != null) {

                        strLastImageScale = mProductJson.getString("scale").toString();
                        strImageScale = strLastImageScale;

                    }

                    if (mProductJson.has("mid_x") && mProductJson.getString("mid_x") != null) {

                        strLastImageMidX = mProductJson.getString("mid_x").toString();
                        strMidX = strLastImageMidX;
                    }
                    if (mProductJson.has("mid_y") && mProductJson.getString("mid_y") != null) {

                        strLastImageMidY = mProductJson.getString("mid_y").toString();
                        strMidY = strLastImageMidY;
                    }
                    if (mProductJson.has(Constant.IMAGE_DRAG_X) && mProductJson.getString(Constant.IMAGE_DRAG_X) != null) {

                        strLastImageDragX = mProductJson.getString(Constant.IMAGE_DRAG_X).toString();
                        strDragX = strLastImageDragX;
                    }
                    if (mProductJson.has(Constant.IMAGE_DRAG_Y) && mProductJson.getString(Constant.IMAGE_DRAG_Y) != null) {

                        strLastImageDragY = mProductJson.getString(Constant.IMAGE_DRAG_Y).toString();
                        strDragY = strLastImageDragY;
                    }
                    if (mProductJson.has(Constant.IMAGE_ROTATE_R) && mProductJson.getString(Constant.IMAGE_ROTATE_R) != null) {

                        strLastImageRotateR = mProductJson.getString(Constant.IMAGE_ROTATE_R).toString();
                        strRotateR = strLastImageRotateR;
                    }
                    if (mProductJson.has(Constant.IMAGE_ROTATE_TX) && mProductJson.getString(Constant.IMAGE_ROTATE_TX) != null) {

                        strLastImageRotateTx = mProductJson.getString(Constant.IMAGE_ROTATE_TX).toString();
                        strRotateTX = strLastImageRotateTx;
                    }
                    if (mProductJson.has(Constant.IMAGE_ROTATE_XC) && mProductJson.getString(Constant.IMAGE_ROTATE_XC) != null) {

                        strLastImageRotateXC = mProductJson.getString(Constant.IMAGE_ROTATE_XC).toString();
                        strRotateXC = strLastImageRotateXC;
                    }
                    if (mProductJson.has(Constant.IMAGE_ROTATE_TY) && mProductJson.getString(Constant.IMAGE_ROTATE_TY) != null) {

                        strLastImageRotateTY = mProductJson.getString(Constant.IMAGE_ROTATE_TY).toString();
                        strRotateTY = strLastImageRotateTY;
                    }
                    if (mProductJson.has(Constant.IMAGE_ROTATE_YC) && mProductJson.getString(Constant.IMAGE_ROTATE_YC) != null) {

                        strLastImageRotateYC = mProductJson.getString(Constant.IMAGE_ROTATE_YC).toString();
                        strRotateYC = strLastImageRotateYC;
                    }

                    if (mProductJson.has(Constant.M_PRESP_0) && mProductJson.getString(Constant.M_PRESP_0) != null) {

                        strPersp0 = mProductJson.getString(Constant.M_PRESP_0).toString();
                    }
                    if (mProductJson.has(Constant.M_PRESP_1) && mProductJson.getString(Constant.M_PRESP_1) != null) {

                        strPersp1 = mProductJson.getString(Constant.M_PRESP_1).toString();
                    }
                    if (mProductJson.has(Constant.M_PRESP_2) && mProductJson.getString(Constant.M_PRESP_2) != null) {

                        strPersp2 = mProductJson.getString(Constant.M_PRESP_2).toString();
                    }
                    if (mProductJson.has(Constant.M_SCALE_X) && mProductJson.getString(Constant.M_SCALE_X) != null) {

                        strScaleX = mProductJson.getString(Constant.M_SCALE_X).toString();
                    }
                    if (mProductJson.has(Constant.M_SCALE_Y) && mProductJson.getString(Constant.M_SCALE_Y) != null) {

                        strScaley = mProductJson.getString(Constant.M_SCALE_Y).toString();
                    }
                    if (mProductJson.has(Constant.M_TRANS_X) && mProductJson.getString(Constant.M_TRANS_X) != null) {

                        strTransalteX = mProductJson.getString(Constant.M_TRANS_X).toString();
                    }
                    if (mProductJson.has(Constant.M_TRANS_Y) && mProductJson.getString(Constant.M_TRANS_Y) != null) {

                        strTransalteY = mProductJson.getString(Constant.M_TRANS_Y).toString();
                    }
                    if (mProductJson.has(Constant.M_SKEW_X) && mProductJson.getString(Constant.M_SKEW_X) != null) {

                        strSkewX = mProductJson.getString(Constant.M_SKEW_X).toString();
                    }
                    if (mProductJson.has(Constant.M_SKEW_Y) && mProductJson.getString(Constant.M_SKEW_Y) != null) {

                        strSkewY = mProductJson.getString(Constant.M_SKEW_Y).toString();
                    }
                    if (mProductJson.has(Constant.AREA_X1) && mProductJson.getString(Constant.AREA_X1) != null) {

                        strLastProductX1 = mProductJson.getString(Constant.AREA_X1).toString();
                    }
                    if (mProductJson.has(Constant.AREA_X2) && mProductJson.getString(Constant.AREA_X2) != null) {

                        strLastProductX2 = mProductJson.getString(Constant.AREA_X2).toString();
                    }
                    if (mProductJson.has(Constant.AREA_Y1) && mProductJson.getString(Constant.AREA_Y1) != null) {

                        strLastProductY1 = mProductJson.getString(Constant.AREA_Y1).toString();
                    }
                    if (mProductJson.has(Constant.AREA_Y2) && mProductJson.getString(Constant.AREA_Y2) != null) {

                        strLastProductY2 = mProductJson.getString(Constant.AREA_Y2).toString();
                    }
                    if (mProductJson.has(Constant.AREA_W) && mProductJson.getString(Constant.AREA_W) != null) {

                        strLastProductW = mProductJson.getString(Constant.AREA_W).toString();
                    }
                    if (mProductJson.has(Constant.AREA_H) && mProductJson.getString(Constant.AREA_H) != null) {

                        strLastProductH = mProductJson.getString(Constant.AREA_H).toString();
                    }

                    if (mProductJson.has("product_text_array")) {

                        JSONArray mTextArray = mProductJson.getJSONArray("product_text_array");

                        if (mTextArray.length() > 0) {
                            for (int i = 0; i < mTextArray.length(); i++) {

                                LastTextInProduct mText = new LastTextInProduct();
                                JSONObject mTextObj = mTextArray.getJSONObject(i);
                                if (mTextObj != null) {

                                    if (mTextObj.has("product_text") && mTextObj.getString("product_text") != null) {
                                        mText.setProduct_text(mTextObj.getString("product_text").toString());
                                    }
                                    if (mTextObj.has("product_text_font_color") && mTextObj.getString("product_text_font_color") != null) {
                                        mText.setProduct_text_font_color(mTextObj.getString("product_text_font_color").toString());
                                    }
                                    if (mTextObj.has("product_text_font_bg_color") && mTextObj.getString("product_text_font_bg_color") != null) {
                                        mText.setProduct_text_font_bg_color(mTextObj.getString("product_text_font_bg_color").toString());
                                    }
                                    if (mTextObj.has("product_text_font_size") && mTextObj.getString("product_text_font_size") != null) {
                                        mText.setProduct_text_font_size(mTextObj.getString("product_text_font_size").toString());

                                    }
                                    if (mTextObj.has("product_text_font_name") && mTextObj.getString("product_text_font_name") != null) {
                                        mText.setProduct_text_font_name(mTextObj.getString("product_text_font_name").toString());
                                    }
                                    if (mTextObj.has("left") && mTextObj.getString("left") != null) {
                                        mText.setLeft(mTextObj.getString("left").toString());
                                    }
                                    if (mTextObj.has("bottom") && mTextObj.getString("bottom") != null) {
                                        mText.setBottom(mTextObj.getString("bottom").toString());
                                    }
                                    if (mTextObj.has("top") && mTextObj.getString("top") != null) {
                                        mText.setTop(mTextObj.getString("top").toString());
                                    }
                                    if (mTextObj.has("right") && mTextObj.getString("right") != null) {
                                        mText.setRight(mTextObj.getString("right").toString());
                                    }
                                    if (mTextObj.has("tv_h") && mTextObj.getString("tv_h") != null) {
                                        mText.setTextH(mTextObj.getString("tv_h").toString());
                                    }
                                    if (mTextObj.has("tv_w") && mTextObj.getString("tv_w") != null) {
                                        mText.setTextW(mTextObj.getString("tv_w").toString());
                                    }

                                    if (mTextObj.has("tv_x") && mTextObj.getString("tv_x") != null) {
                                        mText.setTextX(mTextObj.getString("tv_x").toString());
                                    }

                                    if (mTextObj.has("tv_y") && mTextObj.getString("tv_y") != null) {
                                        mText.setTextY(mTextObj.getString("tv_y").toString());
                                    }

                                    ListLastTextInProduct.add(mText);
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private void setImageFilterAdapter() {

        imageFilterSet = true;
        if (mBitmap != null) {
            mImageFiltersAdapter = new ImageFiltersAdapter(filterEffectList, this, mOnEffectListClickListner, mBitmap);
            mImageFiltersAdapter.notifyDataSetChanged();
        }

        if (is_Edit) {
            if (strLastProductFilter != null && !strLastProductFilter.equalsIgnoreCase("")) {
                strFinalFilterEffectName = strLastProductFilter;
                FilterEffects mFilterEffects = new FilterEffects(this);
                mFilterEffects.applyfilter(strLastProductFilter, mBitmap, IV_WorkspaceImage, mReturnFilteredBitmap);
                strFinalFilterEffectName = "" + strLastProductFilter;
            }
        }


    }

    private void initializeViews() {
        LL_BackgroundColor = (LinearLayout) findViewById(R.id.ll_bg_color);
        LL_TextColor = (LinearLayout) findViewById(R.id.ll_textcolor);

        LLFontchanger = (LinearLayout) findViewById(R.id.fontid);

        Rldeleterl = (RelativeLayout) findViewById(R.id.deleterl);

        RL_Workspace1 = (RelativeLayout) findViewById(R.id.ll_workspace1);
        Img_ColorSymbol = (ImageView) findViewById(R.id.img_colorsymbol);
        Log.e(TAG, "initializeViews: isBitmapAvailable" + isBitmapAvailable);

        if (isBitmapAvailable && ProductFragment.mBitmap != null) {
            mBitmap = ProductFragment.mBitmap;
            strIsDesignUploaded = "1";

            isOnlyTextModeApplicable = false;

        } else {
            Log.e(TAG, "initializeViews: bitmap null");
        }

        tvSpinneText = (TextView) findViewById(R.id.tvSpinneText);
        TvCart = (TextView) findViewById(R.id.tvCartCount);
        TvTitle = (TextView) findViewById(R.id.tvTitle);

        if (strDesignText != null && !strDesignText.isEmpty()) {
            TvTitle.setText(strDesignText + "");
        } else {
            TvTitle.setText("DESIGN");
        }

        Rlcart = (RelativeLayout) findViewById(R.id.rlCart);


        BtnAddCart = (Button) findViewById(R.id.btnAddCart);

        if (is_Edit_Cart) {

            BtnAddCart.setText("Update");
        }

        RltvSpinneTextsp=(RelativeLayout)findViewById(R.id.tvSpinneTextsp);
        LL_AddText = (RelativeLayout) findViewById(R.id.textrl);
        RL_CaptureBitmap = (RelativeLayout) findViewById(R.id.rlCaptureBitmap);
        LL_BackgroundColor = (LinearLayout) findViewById(R.id.ll_bg_color);
        LL_TextColor = (LinearLayout) findViewById(R.id.ll_textcolor);
        LL_Animate = (LinearLayout) findViewById(R.id.ll_animate);
        LLTextControls = (LinearLayout) findViewById(R.id.llTextControls);
        RL_Middle = (RelativeLayout) findViewById(R.id.rl_middle);

        IvReset = (ImageView) findViewById(R.id.imgReset);
        IV_BackgroundColor = (ImageView) findViewById(R.id.img_bg_color);
        IV_TextColor = (ImageView) findViewById(R.id.img_textcolor);
        IV_WorkspaceImage = (ImageView) findViewById(R.id.imageView);
        IV_Tshirt1 = (ImageView) findViewById(R.id.img_tshirt2);
        IvSaveDesign = (ImageView) findViewById(R.id.imgSaveDesign);
        laysave = (RelativeLayout) findViewById(R.id.save);
        layreset = (RelativeLayout) findViewById(R.id.reset);
        IvBack = (ImageView) findViewById(R.id.ivBack);
        IvCart = (ImageView) findViewById(R.id.ivCart);

        fontImageview = (ImageView) findViewById(R.id.img_add_text);
        textcolorImageview = (ImageView) findViewById(R.id.img_textcolor);
        filcolorImageview = (ImageView) findViewById(R.id.img_bg_color);



        RV_Colors = (RecyclerView) findViewById(R.id.rv_color);
        RV_Fonts = (RecyclerView) findViewById(R.id.rv_fonts);
        RV_productColor = (RecyclerView) findViewById(R.id.rv_productcolor);


        Imagedeleteicon = (ImageView) findViewById(R.id.deleteicon);

        mSpinnerProduct = (Spinner) findViewById(R.id.spinnerProduct);

        BtnAddCart.setOnClickListener(this);
        IvBack.setOnClickListener(this);
        IvReset.setOnClickListener(this);
        Imagedeleteicon.setOnClickListener(this);


        IvCart.setOnClickListener(this);
        IvSaveDesign.setOnClickListener(this);
        Rlcart.setOnClickListener(this);
        LL_AddText.setOnClickListener(this);
        Rldeleterl.setOnClickListener(this);

        LL_BackgroundColor.setOnClickListener(this);
        LL_TextColor.setOnClickListener(this);


        Img_ColorSymbol.setOnClickListener(this);




        if (mUserId.equalsIgnoreCase("-1")) {
            mUserId = mUtilities.getDeviceId(CustomDesignActivity.this);
           // IvSaveDesign.setVisibility(View.VISIBLE);
        }

        IV_WorkspaceImage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                IV_WorkspaceImage.setScaleType(ImageView.ScaleType.MATRIX);
                return false;
            }
        });

//        if (!strDesignImageUrl.isEmpty()) {
////            Picasso.with(CustomDesignActivity.this)
////                    .load(strDesignImageUrl)
////                    .into(IV_WorkspaceImage);
//
//            //  getSclaedBitmapFromURL(strDesignImageUrl, IV_WorkspaceImage);
//
//        }

        RltvSpinneTextsp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //mSpinnerProduct.performClick();
//                if(!is_Edit)
//                {
                    Intent i = new Intent(CustomDesignActivity.this, DesignStyleList.class);
                    startActivityForResult(i, 1);
//                }

            }
        });


    }


    private void setProductDetails(Style product) {

        strFinalProductId = product.getId();
        strFinalDisplayOrder = product.getDisplayorder();
        strFinalProductHeight = product.getH();
        strFinalProductPrice = product.getPrice();
        strFinalProductStatus = product.getStatus();
        strFinalProductImage = product.getStyleImage();
        strFinalProductName = product.getStyleName();
        strFinalProductWidth = product.getW();
        strFinalProductX1 = product.getX1();
        strFinalProductX2 = product.getX2();
        strFinalProductY1 = product.getY1();
        strFinalProductY2 = product.getY2();
        strFinalProductW = product.getW();
        strFinalProductH = product.getH();
    }

    private void createFontList() {

        fontList.add("arial.ttf");
        fontList.add("fearless.ttf");
        fontList.add("gorditasbold.ttf");
        fontList.add("gorditasregular.ttf");
        fontList.add("helvetica.ttf");
        fontList.add("timesnewroman.ttf");
        fontList.add("verdana.ttf");
        fontList.add("androidnation.ttf");
        fontList.add("androidnation_b.ttf");
        fontList.add("androidnation_i.ttf");


    }

    private void initAnimation() {
        animShow = AnimationUtils.loadAnimation(this, R.anim.view_show);
        animHide = AnimationUtils.loadAnimation(this, R.anim.view_hide);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.imgReset:

                if (new ConnectionDetector(this).isConnectingToInternet()) {

                    mUtilities.showCancelableDialog(CustomDesignActivity.this, "Do you want to reset this design ?", "Yes", mZezignDialogInterface, "", "reset_design");

                } else {
                    Toast.makeText(this, getString(R.string.no_internet), Toast.LENGTH_LONG).show();
                }
                break;

            case R.id.img_colorsymbol:

                if (!isProductColorsListVisible) {
                    isProductColorsListVisible = true;
                    if (mProductColorsAdapter != null) {
                        mProductColorsAdapter = null;
                        mProductColorsAdapter = new ProductColorsAdapter(productColor, CustomDesignActivity.this, mOnProductColorChangeListner);
                        RV_productColor.setLayoutManager(productColorLayoutManagaer);
                        RV_productColor.setAdapter(mProductColorsAdapter);
                    }

                } else {
                    isProductColorsListVisible = false;
                    RV_productColor.setAdapter(null);
                    //productColor.clear();
//                   int size = productColor.size();
//                   if (size > 0) {
//                       for (int i = 0; i < size; i++) {
//                        //   productColor.remove(i);
//                           mProductColorsAdapter.notifyItemRemoved(i);
//                           mProductColorsAdapter.notifyItemRangeRemoved(i, size);
//                           mProductColorsAdapter.notifyDataSetChanged();
//                       }
//
//                   }

                }


                break;
            case R.id.deleteicon:
//
                deleteAllFunction();

                break;

            case R.id.ivBack:
                onBackPressed();
                break;

            case R.id.btnAddCart:

                if (new ConnectionDetector(this).isConnectingToInternet()) {

                    saveDesign("cart");
                } else {
                    Toast.makeText(this, getString(R.string.no_internet), Toast.LENGTH_LONG).show();
                }

                break;
            case R.id.rlCart:
                Home.recreateActivity = false;
                Intent mIntent = new Intent(CustomDesignActivity.this, Home.class);
                mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//| Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK
                mIntent.putExtra("move", "cart");
                startActivity(mIntent);
                CustomDesignActivity.this.finish();
                //  startActivity(new Intent(CustomDesignActivity.this, ShoppingCartActivity.class));
                break;

            case R.id.ivCart:
                Home.recreateActivity = false;
                Intent i = new Intent(CustomDesignActivity.this, Home.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//| Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK
                i.putExtra("move", "cart");
                startActivity(i);
                //  startActivity(new Intent(CustomDesignActivity.this, ShoppingCartActivity.class));
                break;

            case R.id.imgSaveDesign:
                if(user_id.equalsIgnoreCase("-1")){
                    mUtilities.showLoginDialog(this,this.getString(R.string.alert_login_save));
                }else {


                    if (new ConnectionDetector(this).isConnectingToInternet()) {

                        saveDesign("save");
                    } else {
                        Toast.makeText(this,getString(R.string.no_internet),Toast.LENGTH_LONG).show();
                    }




                }
                break;


            case R.id.textrl:

                if (et_dynamic == null) {
                    et_dynamic = new EditText(this);
                    et_dynamic.setImeOptions(EditorInfo.IME_ACTION_DONE);
                    et_dynamic.setTag(tag);
                } else {
                    et_dynamic = new EditText(this);

                    tag++;
                    et_dynamic.setTag(tag);
                }
                dynamicEdittextListPosition = tag - 1;

                EdittextModalClass mEdittextModalClass = new EdittextModalClass();
                mEdittextModalClass.setEdittextNumber(tag);
                mEdittextModalClass.setEt(et_dynamic);
                mEdittextModalClass.setBackgroundColor("default");
                mEdittextModalClass.setTextColor("default");
                mEdittextModalClass.setFont("default");
                mEdittextModalClass.setTextsize(textsize);
                edittextArraylist.add(mEdittextModalClass);
                Typeface tf = Typeface.createFromAsset(this.getAssets(),
                        // "fonts/geneva.ttf");
                        "fonts/"+fontList.get(0));
                et_dynamic.setTypeface(tf);
                et_dynamic.setSingleLine(true);
                et_dynamic.setTextSize(textsize);
                et_dynamic.setLongClickable(false);
                et_dynamic.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.cursor));

                MyApplication.openKeyboard(this, LL_AddText);
                //

                RL_Workspace1.addView(et_dynamic);
                et_dynamic.requestFocus();

                et_dynamic.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent event) {
                        final int X = (int) event.getRawX();
                        final int Y = (int) event.getRawY();
                        int action = event.getAction();
                        int pureaction = action & MotionEvent.ACTION_MASK;
                        if (event.getPointerCount() == 2) {


                            if (pureaction == MotionEvent.ACTION_POINTER_DOWN) {
                                mBaseDist = getDistance(event);
                                mBaseRatio = mRatio;
                            } else {
                                float delta = (getDistance(event) - mBaseDist) / STEP;
                                float multi = (float) Math.pow(2, delta);
                                mRatio = Math.min(1024.0f, Math.max(0.1f, mBaseRatio * multi));
                                et_dynamic.setTextSize(mRatio + 13);
                            }
                        }
                        else
                        {
                            if(pureaction == MotionEvent.ACTION_DOWN)
                            {
                                RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
                                _xDelta = X - lParams.leftMargin;
                                _yDelta = Y - lParams.topMargin;
                                startTime = System.currentTimeMillis();
                                clickCount++;
                            }
                            else if(pureaction == MotionEvent.ACTION_MOVE)
                            {


                                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
                                layoutParams.leftMargin = X - _xDelta;
                                layoutParams.topMargin = Y - _yDelta;
                                layoutParams.rightMargin = -550;
                                layoutParams.bottomMargin = -550;
                                view.setLayoutParams(layoutParams);

                            }
                            else if(pureaction == MotionEvent.ACTION_UP)
                            {
                                long time = System.currentTimeMillis() - startTime;
                                duration = duration + time;
                                if (clickCount == 2) {
                                    if (duration <= MAX_DURATION) {

                                    } else {
                                    }
                                    et_dynamic.requestFocus();
                                    et_dynamic.setFocusable(true);
                                    et_dynamic.setFocusableInTouchMode(true);
                                    clickCount = 0;
                                    duration = 0;
                                } else if (clickCount == 1) {
                                    Animation animFadein = AnimationUtils.loadAnimation(getApplicationContext(),
                                            R.anim.fade_in);
                                    et_dynamic.setAnimation(animFadein);
                                    et_dynamic.startAnimation(animFadein);
                                    et_dynamic.clearFocus();
                                    et_dynamic.setFocusable(false);
                                    et_dynamic.setFocusableInTouchMode(false);
                                    et_dynamic.setCursorVisible(true);
                                } else {
                                    et_dynamic.clearFocus();
                                    et_dynamic.setFocusable(false);
                                    et_dynamic.setFocusableInTouchMode(false);

                                }
                            }

                            RL_Workspace1.invalidate();

                        }


                     /*  final int X = (int) event.getRawX();
                        final int Y = (int) event.getRawY();
                       // int view_tag = (Integer) view.getTag();
                        switch (event.getAction() & MotionEvent.ACTION_MASK) {
                            case MotionEvent.ACTION_DOWN:
                                RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
                                _xDelta = X - lParams.leftMargin;
                                _yDelta = Y - lParams.topMargin;

                                Log.d(TAG, "onTouch: here");
                                clickCount++;
                                break;
                            case MotionEvent.ACTION_UP:
                                long time = System.currentTimeMillis() - startTime;
                                duration = duration + time;
                                if (clickCount == 2) {
                                    if (duration <= MAX_DURATION) {

                                    } else {
                                    }
                                    et_dynamic.requestFocus();
                                    et_dynamic.setFocusable(true);
                                    et_dynamic.setFocusableInTouchMode(true);
                                    clickCount = 0;
                                    duration = 0;
                                    break;
                                } else if (clickCount == 1) {
                                    Animation animFadein = AnimationUtils.loadAnimation(getApplicationContext(),
                                            R.anim.fade_in);
                                    et_dynamic.setAnimation(animFadein);
                                    et_dynamic.startAnimation(animFadein);
                                    et_dynamic.clearFocus();
                                    et_dynamic.setFocusable(false);
                                    et_dynamic.setFocusableInTouchMode(false);
                                    et_dynamic.setCursorVisible(true);
                                } else {
                                    et_dynamic.clearFocus();
                                    et_dynamic.setFocusable(false);
                                    et_dynamic.setFocusableInTouchMode(false);

                                }
                                Log.d(TAG, "onTouch: here");
                                break;
                            case MotionEvent.ACTION_POINTER_DOWN:

                                Log.d(TAG, "onTouch: here");
                                break;
                            case MotionEvent.ACTION_POINTER_UP:

                                Log.d(TAG, "onTouch: here");
                                break;
                            case MotionEvent.ACTION_MOVE:

                                Log.d(TAG, "onTouch: here");
                                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
                                layoutParams.leftMargin = X - _xDelta;
                                layoutParams.topMargin = Y - _yDelta;
                                view.setLayoutParams(layoutParams);
                                break;
                        }*/


                        return false;
                    }
                });

/*
               et_dynamic.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent event) {

                        for (int i = 0; i < edittextArraylist.size(); i++) {
                            int view_tag = (Integer) view.getTag();
                            if (edittextArraylist.get(i).getEdittextNumber() == view_tag) {
                                dynamicEdittextListPosition = i;
                                et_dynamic = edittextArraylist.get(i).getEt();
                            }
                        }

                        final int X = (int) event.getRawX();
                        final int Y = (int) event.getRawY();
                        int view_tag = (Integer) view.getTag();


                        switch (event.getAction() & MotionEvent.ACTION_MASK) {
                            case MotionEvent.ACTION_DOWN:
                                RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
                                _xDelta = X - lParams.leftMargin;
                                _yDelta = Y - lParams.topMargin;

                                startTime = System.currentTimeMillis();
                                Log.d(TAG, "onTouch: et clicked");

                                clickCount++;

                                break;
                            case MotionEvent.ACTION_UP:
                                long time = System.currentTimeMillis() - startTime;
                                duration = duration + time;
                                if (clickCount == 2) {
                                    if (duration <= MAX_DURATION) {

                                    } else {
                                    }
                                    et_dynamic.requestFocus();
                                    et_dynamic.setFocusable(true);
                                    et_dynamic.setFocusableInTouchMode(true);
                                    clickCount = 0;
                                    duration = 0;
                                    break;
                                } else if (clickCount == 1) {
                                    Animation animFadein = AnimationUtils.loadAnimation(getApplicationContext(),
                                            R.anim.fade_in);
                                    et_dynamic.setAnimation(animFadein);
                                    et_dynamic.startAnimation(animFadein);

                                    et_dynamic.clearFocus();
                                    et_dynamic.setFocusable(false);
                                    et_dynamic.setFocusableInTouchMode(false);
                                    et_dynamic.setCursorVisible(true);
                                } else {
                                    et_dynamic.clearFocus();
                                    et_dynamic.setFocusable(false);
                                    et_dynamic.setFocusableInTouchMode(false);

                                }
                                break;
                            case MotionEvent.ACTION_POINTER_DOWN:
                                break;
                            case MotionEvent.ACTION_POINTER_UP:
                                break;
                            case MotionEvent.ACTION_MOVE:
                                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
                                layoutParams.leftMargin = X - _xDelta;
                                layoutParams.topMargin = Y - _yDelta;
                                view.setLayoutParams(layoutParams);
                                break;
                        }
                        RL_Workspace.invalidate();
                        return false;
                        // return gestureDetector.onTouchEvent(event);
                    }
                });
*/

                break;



        }

    }
    int getDistance(MotionEvent event) {
        int dx = (int) (event.getX(0) - event.getX(1));
        int dy = (int) (event.getY(0) - event.getY(1));
        return (int) (Math.sqrt(dx * dx + dy * dy));
    }
    private void saveDesign(String strButtonName) {
        try {

            RL_CaptureBitmap.setDrawingCacheEnabled(true);
            RL_CaptureBitmap.buildDrawingCache();
            capturedBitmap = RL_CaptureBitmap.getDrawingCache();
            MarshmallowPermissions marshmallowPermissions = new MarshmallowPermissions(CustomDesignActivity.this);
            if (marshmallowPermissions.checkPermissionForExternalStorage()) {
                saveCapturedbitmap(capturedBitmap, strButtonName);
            } else {
                marshmallowPermissions.requestPermissionForExternalStorage();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addToCartViaRetrofit(String strJson) {

        mUtilities.showProgressDialog(CustomDesignActivity.this, "adding product in cart...");

        Retrofit restAdapter = AddToCartApi.retrofit;
        AddToCartApi addToCartApi = restAdapter.create(AddToCartApi.class);

        MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
        File file_mine = new File(strFinalproductImagePath);

        if (mUserId.equalsIgnoreCase("-1")) {
            mUserId = mUtilities.getDeviceId(CustomDesignActivity.this);
        }


        RequestBody cart_product_id = RequestBody.create(MediaType.parse("text/plain"), strId);
        RequestBody title = RequestBody.create(MediaType.parse("text/plain"), strFinalProductName);
        RequestBody product_thumb = RequestBody.create(MEDIA_TYPE_PNG, file_mine);
        //RequestBody product_thumb = RequestBody.create(MediaType.parse("text/plain"), encodedImage);
        RequestBody product_data = RequestBody.create(MediaType.parse("text/plain"), strJson);
        RequestBody price = RequestBody.create(MediaType.parse("text/plain"), strFinalProductPrice);
        RequestBody quantity = RequestBody.create(MediaType.parse("text/plain"), "1");
        RequestBody user_id = RequestBody.create(MediaType.parse("text/plain"), mUserId);
        RequestBody design_id = RequestBody.create(MediaType.parse("text/plain"), strDesignId);
        RequestBody product_style_id = RequestBody.create(MediaType.parse("text/plain"), strFinalProductId);
        RequestBody product_id = RequestBody.create(MediaType.parse("text/plain"), strProductId);
        RequestBody device_type = RequestBody.create(MediaType.parse("text/plain"), "Android Device");
        RequestBody isUploadedDesign;
        if(strIsDesignUploaded.equalsIgnoreCase("0") || strIsDesignUploaded.equalsIgnoreCase("")){
            isUploadedDesign = RequestBody.create(MediaType.parse("text/plain"), "0");
            Log.d(TAG, "saveProductViaRetrofit: isUploadedDesign  " + "0");
        }else{
            isUploadedDesign = RequestBody.create(MediaType.parse("text/plain"), "1");
            Log.d(TAG, "saveProductViaRetrofit: isUploadedDesign  " + "1");
        }


        if (is_Edit_Cart) {

            Call<JsonElement> call = addToCartApi.updateCart(cart_product_id, title, product_thumb,
                    product_data, price, quantity, user_id, design_id, product_id, product_style_id,device_type);

            call.enqueue(new Callback<JsonElement>() {

                @Override
                public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                    Log.d(TAG, "onResponse: " + response.body());
                    if (response.body() != null) {
                        mUtilities.hideProgressDialog();
                        hideProgressDialog();
                        parseSaveProductResponse(response.body().toString(), "cart");
                    }
                }

                @Override
                public void onFailure(Call<JsonElement> result, Throwable t) {
                    Log.e(TAG, "onFailure: " + result.toString());
                    mUtilities.hideProgressDialog();
                    hideProgressDialog();
                }
            });

        } else {

            Call<JsonElement> call = addToCartApi.addToCart(title, product_thumb, product_data,
                    price, quantity, user_id, design_id, product_id, product_style_id,isUploadedDesign,device_type);

            call.enqueue(new Callback<JsonElement>() {

                @Override
                public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                    Log.d(TAG, "onResponse: " + response.body());
                    if (response.body() != null) {
                        mUtilities.hideProgressDialog();
                        hideProgressDialog();
                        parseSaveProductResponse(response.body().toString(), "cart");
                    }

                }

                @Override
                public void onFailure(Call<JsonElement> result, Throwable t) {
                    Log.e(TAG, "onFailure: " + result.toString());
                    mUtilities.hideProgressDialog();
                    hideProgressDialog();
                }
            });
        }


    }

    private void makeJsonToSave(String strBtnName) {

        try {

            float[] f = new float[9];
            matrix.getValues(f);

            float scaleX = f[0];
            float skewX = f[1];
            float transalteX = f[2];

            float scaleY = f[3];
            float skewY = f[4];
            float transalteY = f[5];

            float persp0 = f[6];
            float persp1 = f[7];
            float persp2 = f[8];

            strScaleX = scaleX + "";
            strSkewX = skewX + "";
            strTransalteX = transalteX + "";

            strScaley = scaleY + "";
            strSkewY = skewY + "";
            strTransalteY = transalteY + "";

            strPersp0 = persp0 + "";
            strPersp1 = persp1 + "";
            strPersp2 = persp2 + "";


            JSONObject mJsonObj = new JSONObject();
            mJsonObj.put(Constant.PRODUCT_NAME, strFinalProductName);
            mJsonObj.put(Constant.PRODUCT_IMAGE, strFinalProductImage);
            mJsonObj.put(Constant.PRODUCT_ID, strFinalProductId);
            mJsonObj.put(Constant.PRODUCT_PRICE, strFinalProductPrice);
            mJsonObj.put(Constant.PRODUCT_DISPLAY_ORDER, strFinalDisplayOrder);
            mJsonObj.put(Constant.PRODUCT_HEIGHT, strFinalProductHeight);
            mJsonObj.put(Constant.PRODUCT_WIDTH, strFinalProductWidth);
            mJsonObj.put(Constant.PRODUCT_STATUS, strFinalProductStatus);
            mJsonObj.put(Constant.PRODUCT_COLOR, strFinalProductColor);
            mJsonObj.put(Constant.PRODUCT_FILTER, strFinalFilterEffectName);
            mJsonObj.put(Constant.PRODUCT_BRIGHTNESS, strFinalProductBrightness);
            mJsonObj.put(Constant.PRODUCT_CONTRAST, strFinalProductContrast);
            mJsonObj.put(Constant.PRODUCT_TRANSPARENCY, strFinalProductTransparent);
            mJsonObj.put(Constant.IMAGE_SCALE, strImageScale);
            mJsonObj.put(Constant.IMAGE_MID_X, strMidX);
            mJsonObj.put(Constant.IMAGE_MID_Y, strMidY);
            mJsonObj.put(Constant.IMAGE_DRAG_X, strDragX);
            mJsonObj.put(Constant.IMAGE_DRAG_Y, strDragY);
            mJsonObj.put(Constant.IMAGE_ROTATE_R, strRotateR);
            mJsonObj.put(Constant.IMAGE_ROTATE_TX, strRotateTX);
            mJsonObj.put(Constant.IMAGE_ROTATE_TY, strRotateTY);
            mJsonObj.put(Constant.IMAGE_ROTATE_XC, strRotateXC);
            mJsonObj.put(Constant.IMAGE_ROTATE_YC, strRotateYC);

            mJsonObj.put(Constant.M_PRESP_0, strPersp0);
            mJsonObj.put(Constant.M_PRESP_1, strPersp1);
            mJsonObj.put(Constant.M_PRESP_2, strPersp2);

            mJsonObj.put(Constant.M_SCALE_X, strScaleX);
            mJsonObj.put(Constant.M_SKEW_X, strSkewX);
            mJsonObj.put(Constant.M_TRANS_X, strTransalteX);

            mJsonObj.put(Constant.M_SCALE_Y, strScaley);
            mJsonObj.put(Constant.M_SKEW_Y, strSkewY);
            mJsonObj.put(Constant.M_TRANS_Y, strTransalteY);

            mJsonObj.put(Constant.AREA_X1, strFinalProductX1);
            mJsonObj.put(Constant.AREA_X2, strFinalProductX2);
            mJsonObj.put(Constant.AREA_Y1, strFinalProductY1);
            mJsonObj.put(Constant.AREA_Y2, strFinalProductY2);
            mJsonObj.put(Constant.AREA_W, strFinalProductW);
            mJsonObj.put(Constant.AREA_H, strFinalProductH);

            JSONArray mEditTextArray = new JSONArray();

//            IV_WorkspaceImage.buildDrawingCache();
//            Bitmap bmap = IV_WorkspaceImage.getDrawingCache();

//            String encodedImage = "";
//            try {
//                ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                bmap.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
//                byte[] b = baos.toByteArray();
//                encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
//                Log.e(TAG, "encodedImage : " + encodedImage);
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            mJsonObj.put("image", encodedImage);
            for (int i = 0; i < edittextArraylist.size(); i++) {

                JSONObject mEditObj = new JSONObject();
                EditText et = edittextArraylist.get(i).getEt();
                String text = et.getText().toString().trim();

                mEditObj.put(Constant.PRODUCT_TEXT, text + "");
                mEditObj.put(Constant.PRODUCT_TEXT_FONT_COLOR, edittextArraylist.get(i).getTextColor() + "");
                mEditObj.put(Constant.PRODUCT_TEXT_FONT_BG_COLOR, edittextArraylist.get(i).getBackgroundColor() + "");
                mEditObj.put(Constant.PRODUCT_TEXT_FONT_SIZE, edittextArraylist.get(i).getTextsize() + "");
                mEditObj.put(Constant.PRODUCT_TEXT_FONT_NAME, edittextArraylist.get(i).getFont());

                mEditObj.put(Constant.PRODUCT_TEXT_LEFT, et.getLeft() + "");
                mEditObj.put(Constant.PRODUCT_TEXT_BOTTOM, et.getBottom() + "");
                mEditObj.put(Constant.PRODUCT_TEXT_TOP, et.getTop() + "");
                mEditObj.put(Constant.PRODUCT_TEXT_RIGHT, et.getRight() + "");
                mEditObj.put(Constant.PRODUCT_TEXT_H, et.getHeight() + "");
                mEditObj.put(Constant.PRODUCT_TEXT_W, et.getWidth() + "");
                mEditObj.put(Constant.PRODUCT_TEXT_X, et.getX() + "");
                mEditObj.put(Constant.PRODUCT_TEXT_Y, et.getY() + "");

                mEditTextArray.put(mEditObj);
            }

            mJsonObj.put(Constant.PRODUCT_TEXT_ARRAY, mEditTextArray);
            String strJson = mJsonObj.toString();
            Log.d(TAG, "JSON : " + strJson);

            if (strBtnName.equalsIgnoreCase("save")) {
                saveProductViaRetrofit(strJson);
            } else if (strBtnName.equalsIgnoreCase("cart")) {
                addToCartViaRetrofit(strJson);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void saveProductViaRetrofit(String strJson) {

        showProgressDialog("Please wait...");

        Retrofit restAdapter = SaveProductApi.retrofit;
        SaveProductApi saveProductApi = restAdapter.create(SaveProductApi.class);

        MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
        File file_mine = new File(strFinalproductImagePath);
        Log.e(TAG, "ImagePath: " + strFinalproductImagePath);
//        String encodedImage = "";
//        try {
//            Bitmap bm = BitmapFactory.decodeFile(strFinalproductImagePath);
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
//            byte[] b = baos.toByteArray();
//            encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
//            Log.e(TAG, "encodedImage : " + encodedImage);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        RequestBody title = RequestBody.create(MediaType.parse("text/plain"), strFinalProductName);
        RequestBody product_view = RequestBody.create(MediaType.parse("text/plain"), strJson);
        RequestBody product_img = RequestBody.create(MEDIA_TYPE_PNG, file_mine);
        //   RequestBody product_img = RequestBody.create(MediaType.parse("text/plain"), encodedImage);
        RequestBody device_type = RequestBody.create(MediaType.parse("text/plain"), "Android Device");
        RequestBody design_id = RequestBody.create(MediaType.parse("text/plain"), strDesignId);
        RequestBody product_id = RequestBody.create(MediaType.parse("text/plain"), strProductId);
        RequestBody product_style_id = RequestBody.create(MediaType.parse("text/plain"), strFinalProductId);
        RequestBody user_id = RequestBody.create(MediaType.parse("text/plain"), mUserId);

        RequestBody isUploadedDesign;
        Log.d(TAG, "saveProductViaRetrofit: isUploadedDesign  " + strIsDesignUploaded);

        if(strIsDesignUploaded.equalsIgnoreCase("0") || strIsDesignUploaded.equalsIgnoreCase("")){
            isUploadedDesign = RequestBody.create(MediaType.parse("text/plain"), "0");
            Log.d(TAG, "saveProductViaRetrofit: isUploadedDesign  " + "0");
        }else{
            isUploadedDesign = RequestBody.create(MediaType.parse("text/plain"), "1");
            Log.d(TAG, "saveProductViaRetrofit: isUploadedDesign  " + "1");
        }



        Call<JsonElement> call = saveProductApi.saveProduct(title, product_view, product_img, design_id, product_id,
                product_style_id, user_id, device_type,isUploadedDesign);

        call.enqueue(new Callback<JsonElement>() {

            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (response.body() != null) {
                    Log.d(TAG, "onResponse: " + response.body());
                    hideProgressDialog();
                    parseSaveProductResponse(response.body().toString(), "save");
                }

            }

            @Override
            public void onFailure(Call<JsonElement> result, Throwable t) {
                hideProgressDialog();
                Log.e(TAG, "onFailure: " + result.toString());
            }
        });
    }


    private void saveCapturedbitmap(Bitmap image, String strBtnName) {

        File pictureFile = getOutputMediaFile();
        if (pictureFile == null) {
            Log.d(TAG,"Error creating media file, check storage permissions: ");// e.getMessage());
            return;
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            image.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
            RL_CaptureBitmap.setDrawingCacheEnabled(false);
            makeJsonToSave(strBtnName);
        } catch (Exception e) {
            Log.d(TAG, "File not found: " + e.getMessage());
        }
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.
                INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        return true;
    }
    private File getOutputMediaFile() {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                + "/Android/data/"
                + getApplicationContext().getPackageName()
                + "/Files");

        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmm").format(new Date());
        File mediaFile;
        mImageName = "MI_" + timeStamp + ".png";
        strFinalproductImagePath = mediaStorageDir.getPath() + File.separator + mImageName;
        mediaFile = new File(strFinalproductImagePath);
        return mediaFile;
    }



    private void onFilterClick() {
        LL_Animate.setVisibility(View.INVISIBLE);





    }






    @Override
    public void onColorChange(String colorCode) {

        // callback methids from adapter to change color

        Log.d(TAG, "dynamicEdittextListPosition " + dynamicEdittextListPosition + " colorCode " + colorCode);
        if (et_dynamic != null) {
            if (isBackgroundColorSelected) {
                et_dynamic.setBackgroundColor(Color.parseColor(colorCode));
                edittextArraylist.get(dynamicEdittextListPosition).setBackgroundColor(colorCode);
            } else if (isTextColorSelected) {
                et_dynamic.setTextColor(Color.parseColor(colorCode));
                edittextArraylist.get(dynamicEdittextListPosition).setTextColor(colorCode);
            }
        }

    }

    private void selectImage() {

        // this will open dialog to add image from camera or gallery
        final CharSequence[] items = {"Camera", "Gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(CustomDesignActivity.this);
        builder.setTitle("Choose Image");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result = MyApplication.checkPermission(CustomDesignActivity.this);

                if (items[item].equals("Camera")) {
                    userChoosenTask = "Camera";
                    if (result)
                        cameraIntent();

                } else if (items[item].equals("Gallery")) {
                    userChoosenTask = "Gallery";
                    if (result)
                        galleryIntent();

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void cameraIntent() {

        MarshmallowPermissions mPermission = new MarshmallowPermissions(CustomDesignActivity.this);

        if (!mPermission.checkPermissionForCamera()) {
            mPermission.requestPermissionForCamera();
        } else {
            if (!mPermission.checkPermissionForExternalStorage()) {
                mPermission.requestPermissionForExternalStorage();
            } else {

//                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                startActivityForResult(intent, REQUEST_CAMERA);
                dispatchTakePictureIntent();
            }
        }


    }

    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1){
            if(resultCode == RESULT_OK){
                int i = data.getIntExtra("pos", 0);


                String productUrl = Constant.IMAGE_BASE_URL_PRODUCT_STYLES + productStyleList.get(i).getStyleImage();
                Log.d(TAG, "productUrl " + productUrl);


                //left
                Animation animationToLeft = new TranslateAnimation(230, -230, 0, 0);
                animationToLeft.setDuration(9000);
                animationToLeft.setRepeatCount(-1);
                animationToLeft.setRepeatMode(Animation.REVERSE);
                animationToLeft.setRepeatCount(Animation.INFINITE);
                tvSpinneText.setAnimation(animationToLeft);
                String textLeft = productStyleList.get(i).getStyleName();
                tvSpinneText.setText(textLeft);



                Picasso.with(CustomDesignActivity.this)
                        .load(productUrl)
                        .into(IV_Tshirt1);

                setWorkspaceLayout(productStyleList.get(i).getH(), productStyleList.get(i).getW(), productStyleList.get(i).getX1(),
                        productStyleList.get(i).getY1(), productStyleList.get(i).getX2(),
                        productStyleList.get(i).getY2(), true);


                productColor = productStyleList.get(i).getProductStyleColor();
                mProductColorsAdapter = new ProductColorsAdapter(productColor, CustomDesignActivity.this, mOnProductColorChangeListner);
                RV_productColor.setLayoutManager(productColorLayoutManagaer);

                setProductDetails(productStyleList.get(i));
            }
        }
        else
        {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_TAKE_PHOTO)
                onCaptureImageResult();
        }
    }

    private void onCaptureImageResult() {
        try {
//            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            Bitmap thumbnail = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoURI);
            Log.d(TAG, "onActivityResult: bitmap " + thumbnail.getHeight() + "  " + thumbnail.getWidth());
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

            File destination = new File(Environment.getExternalStorageDirectory(),
                    System.currentTimeMillis() + ".png");

            FileOutputStream fo;

            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();

            String path = MediaStore.Images.Media.insertImage(getContentResolver(), thumbnail, "Title", null);
            Uri selectedImageUri = Uri.parse(path);
            String strGalleryPath = AppUtils.getRealPathFromURI(CustomDesignActivity.this, selectedImageUri);
            mBitmap = thumbnail;
            uploadPicDialog(strGalleryPath, "camera");

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {

        Bitmap bm = null;
        String strGalleryPath = "";
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
                Uri selectedImageUri = data.getData();
                strGalleryPath = AppUtils.getRealPathFromURI(CustomDesignActivity.this, selectedImageUri);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        mBitmap = bm;

        uploadPicDialog(strGalleryPath, "gallery");
    }

    private void uploadPicDialog(final String strGalleryPath, String ImageType) {


        Log.e(TAG, "onSelectFromGalleryResult: strGalleryPath " + strGalleryPath);

        if (strGalleryPath == null) {

            Utilities mUtilities = new Utilities();
            mUtilities.showDialog(CustomDesignActivity.this, "Please select image from gallery", "OK");

        } else {
            final Dialog dialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar);
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
                        showToast("Please enter your design title");
                    } else {

                        addDesignViaRetrofit(strGalleryPath);
                        dialog.dismiss();

                    }

                }
            });
        }


    }

    private void addDesignViaRetrofit(String strGalleryPath) {

        showProgressDialog("Adding Design...");

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
                    hideProgressDialog();
                    parseAddDesignResponse(response.body().toString());
                }

            }

            @Override
            public void onFailure(Call<JsonElement> result, Throwable t) {
                hideProgressDialog();
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
                                    setImageFilterAdapter();
                                    IV_WorkspaceImage.setImageBitmap(mBitmap);

                                }
                            }
                        }
                    } else {
                        showToast(strMessage);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private void setZoomRotateOperationOnImage() {


        // image that can be zoom in-out, rotate
        RL_CaptureBitmap.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ImageView view = (ImageView) IV_WorkspaceImage;
                IV_WorkspaceImage.setFocusable(true);
                IV_WorkspaceImage.setFocusableInTouchMode(true);
                IV_WorkspaceImage.requestFocus();
                clickCount = 0;
                imageclickcount = 0;
//                if(et_dynamic != null){
//                    et_dynamic.setBackgroundDrawable(null);
//                }

                MyApplication.closeSoftKeyBoard(CustomDesignActivity.this, RL_CaptureBitmap);
                switch (event.getAction() & MotionEvent.ACTION_MASK) {

                    case MotionEvent.ACTION_DOWN:

                        savedMatrix.set(matrix);
                        start.set(event.getRawX(), event.getRawY());
                        mode = DRAG;
                        lastEvent = null;
                        imageclickcount++;
                        Animation animFa = AnimationUtils.loadAnimation(getApplicationContext(),
                                R.anim.fade_in);
                        IV_WorkspaceImage.setAnimation(animFa);
                        IV_WorkspaceImage.startAnimation(animFa);
                        break;

                    case MotionEvent.ACTION_POINTER_DOWN:

                        oldDist = spacing(event);

                        if (oldDist > 10f) {

                            savedMatrix.set(matrix);
                            midPoint(mid, event);
                            mode = ZOOM;
                        }

                        lastEvent = new float[4];
                        lastEvent[0] = event.getX(0);
                        lastEvent[1] = event.getX(1);
                        lastEvent[2] = event.getY(0);
                        lastEvent[3] = event.getY(1);

                        d = rotation(event);

                        break;

                    case MotionEvent.ACTION_UP:
                        long time = System.currentTimeMillis() - startTime;
                        duration = duration + time;
                        if (imageclickcount == 2) {
                            if (duration <= MAX_DURATION) {

                            } else {
                            }

                            imageclickcount = 0;
                            duration = 0;
                            break;
                        } else if (imageclickcount == 1) {


                        } else {


                        }
                        break;

                    case MotionEvent.ACTION_POINTER_UP:

                        mode = NONE;
                        lastEvent = null;

                        break;

                    case MotionEvent.ACTION_MOVE:

                        if (mode == DRAG) {

                            matrix.set(savedMatrix);
                            float dx = event.getRawX() - start.x;
                            float dy = event.getRawY() - start.y;
                            strDragX = dx + "";
                            strDragY = dy + "";
                            matrix.postTranslate(dx, dy);

                            // Log.e(TAG, "onTouch: " + "strDragX : " + strDragX + "strDragY " + strDragY);

                        } else if (mode == ZOOM) {

                            float newDist = spacing(event);

                            if (newDist > 10f) {

                                matrix.set(savedMatrix);
                                float scale = (newDist / oldDist);



                                float  centerX = RL_Workspace1.getWidth() / 2F;
                                float   centerY = RL_Workspace1.getHeight() / 2F;

                                // matrix.postScale(scale, scale, mid.x, mid.y);
                                matrix.postScale(scale, scale, centerX, centerY);


                                Log.e(TAG, "onTouch: " + "scale : " + scale);
                                //    Log.e(TAG, "onTouch: " + "scale : " + scale + "newDist " + newDist + "oldDist " + oldDist);

                            }

                            if (lastEvent != null && event.getPointerCount() == 2) {

                                newRot = rotation(event);
                                float r = newRot - d;
                                //   oldR = oldR + r;
//                                Log.e(TAG, "q: newRot " + newRot);
//                                Log.e(TAG, "q: d " + d);
//                                Log.e(TAG, "q: r " + r);
//                                Log.e(TAG, "q: oldR " + oldR);
                                float[] values = new float[9];
                                matrix.getValues(values);
                                float tx = values[2];
                                float ty = values[5];
                                float sx = values[0];
                                float xc = (view.getWidth() / 2) * sx;
                                float yc = (view.getHeight() / 2) * sx;
                                matrix.postRotate(r, tx + xc, ty + yc);
//
//                                strRotateR = newRot + "";
//                                strRotateTX = tx + "";
//                                strRotateXC = xc + "";
//                                strRotateTY = ty + "";
//                                strRotateYC = yc + "";

                                //  Log.e(TAG, "onTouch: " + "strRotateR : " + strRotateR + "strRotateTX " + strRotateTX
                                //       + "strRotateXC " + strRotateXC + "strRotateTY " + strRotateTY + "strRotateYC " + strRotateYC);
                            }
                        }
                        break;
                }

                view.setImageMatrix(matrix);


                return true;
            }
        });

//        // image that can be zoom in-out, rotate
//        IV_WorkspaceImage.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                ImageView view = (ImageView) IV_WorkspaceImage;
//
//                switch (event.getAction() & MotionEvent.ACTION_MASK) {
//
//                    case MotionEvent.ACTION_DOWN:
//
//                        savedMatrix.set(matrix);
//                        start.set(event.getRawX(), event.getRawY());
//                        mode = DRAG;
//                        lastEvent = null;
//
//                        break;
//
//                    case MotionEvent.ACTION_POINTER_DOWN:
//
//                        oldDist = spacing(event);
//
//                        if (oldDist > 10f) {
//
//                            savedMatrix.set(matrix);
//                            midPoint(mid, event);
//                            mode = ZOOM;
//
//                        }
//
//                        lastEvent = new float[4];
//                        lastEvent[0] = event.getX(0);
//                        lastEvent[1] = event.getX(1);
//                        lastEvent[2] = event.getY(0);
//                        lastEvent[3] = event.getY(1);
//
//                        d = rotation(event);
//
//                        break;
//
//                    case MotionEvent.ACTION_UP:
//
//                    case MotionEvent.ACTION_POINTER_UP:
//
//                        mode = NONE;
//                        lastEvent = null;
//
//                        break;
//
//                    case MotionEvent.ACTION_MOVE:
//
//                        if (mode == DRAG) {
//
//                            matrix.set(savedMatrix);
//                            float dx = event.getRawX() - start.x;
//                            float dy = event.getRawY() - start.y;
//                            matrix.postTranslate(dx, dy);
//
//                        } else if (mode == ZOOM) {
//
//                            float newDist = spacing(event);
//
//                            if (newDist > 10f) {
//
//                                matrix.set(savedMatrix);
//                                float scale = (newDist / oldDist);
//                                matrix.postScale(scale, scale, mid.x, mid.y);
//
//                                strImageScale = scale + "";
//                                strMidX = mid.x + "";
//                                strMidY = mid.y + "";
//
//                                Log.e(TAG, "onTouch: " + "scale : " + scale + "mid.x " + mid.x + "mid.y " + mid.y);
//
//                            }
//
//                            if (lastEvent != null && event.getPointerCount() == 2) {
//
//                                newRot = rotation(event);
//                                float r = newRot - d;
//                                float[] values = new float[9];
//                                matrix.getValues(values);
//                                float tx = values[2];
//                                float ty = values[5];
//                                float sx = values[0];
//                                float xc = (view.getWidth() / 2) * sx;
//                                float yc = (view.getHeight() / 2) * sx;
//                                matrix.postRotate(r, tx + xc, ty + yc);
//
//                            }
//
//                        }
//
//                        break;
//
//                }
//
//                view.setImageMatrix(matrix);
//
//
//                return true;
//            }
//        });
    }

    /**
     * Determine the space between the first two fingers
     */
    private float spacing(MotionEvent event) {

        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
//        spacingX = event.getX(0) - event.getX(1);
//        spacingY = event.getY(0) - event.getY(1);
//        spacingF = (float) Math.sqrt((x * x + y * y));
//        Log.e(TAG, "spacing: spacingF " + spacingF );

        return (float) Math.sqrt((x * x + y * y));
    }

    /**
     * Calculate the mid point of the first two fingers
     */
    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    /**
     * Calculate the degree to be rotated by.
     *
     * @param event
     * @return Degrees
     */
    private float rotation(MotionEvent event) {
        double delta_x = (event.getX(0) - event.getX(1));
        double delta_y = (event.getY(0) - event.getY(1));
        double radians = Math.atan2(delta_y, delta_x);
        return (float) Math.toDegrees(radians);
    }

    @Override
    public void onFontSelected(String fontname, int position) {

        if (et_dynamic != null) {
            Typeface tf = Typeface.createFromAsset(getAssets(),
                    "fonts/" + fontname);
            et_dynamic.setTypeface(tf);

            edittextArraylist.get(dynamicEdittextListPosition).setFont(fontname);
        }

    }

//    @Override
//    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
//
//        if (seekBar == SbBrightness) {
//
//            strFinalProductBrightness = i + "";
//
//            if(mBitmap != null){
//                Bitmap bm = changeBitmapContrastBrightness(mBitmap, (float) (SbContrast.getProgress() - 1), (float) (i - 255));
//                IV_WorkspaceImage.setImageBitmap(bm);
//            }
//
//
//        } else if (seekBar == SbContrast) {
//
//            strFinalProductContrast = i + "";
//            if(mBitmap != null){
//                Bitmap bm = changeBitmapContrastBrightness(mBitmap, (float) (i - 1), (float) SbBrightness.getProgress() - 255);
//                IV_WorkspaceImage.setImageBitmap(bm);
//            }
//
//
//        } else if (seekBar == SbTransparent) {
//
//            if(mBitmap != null){
//                strFinalProductTransparent = i + "";
//                IV_WorkspaceImage.setAlpha((float) ((double) i / (double) 10));
//            }
//
//
//        }
//
//    }
//
//
//    @Override
//    public void onStartTrackingTouch(SeekBar seekBar) {
//
//    }
//
//    @Override
//    public void onStopTrackingTouch(SeekBar seekBar) {
//
//    }


    public static Bitmap changeBitmapContrastBrightness(Bitmap bmp, float contrast, float brightness) {
        ColorMatrix cm = new ColorMatrix(new float[]
                {
                        contrast, 0, 0, 0, brightness,
                        0, contrast, 0, 0, brightness,
                        0, 0, contrast, 0, brightness,
                        0, 0, 0, 1, 0
                });

        Bitmap ret = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), bmp.getConfig());

        Canvas canvas = new Canvas(ret);

        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        canvas.drawBitmap(bmp, 0, 0, paint);

        return ret;
    }

    @Override
    public void onFilterEffectChange(String effectName, int pos) {

        Log.d("bharat", "effectName " + effectName + " pos " + pos);
        strFinalFilterEffectName = effectName;

        FilterEffects mFilterEffects = new FilterEffects(this);

        mFilterEffects.applyfilter(effectName, mBitmap, IV_WorkspaceImage, mReturnFilteredBitmap);
        strFinalFilterEffectName = "" + effectName;

        //IV_WorkspaceImage.setImageBitmap(b);

    }

    private void getStylesViaRetrofit() {
        //Creating an object of our api interface
        Retrofit restAdapter = StyleApi.retrofit;
        StyleApi styleApi = restAdapter.create(StyleApi.class);

        showProgressDialog("Please wait...");

        Call<JsonElement> call = styleApi.getProductStyleViaJSON(getStyleJSON());

        call.enqueue(new Callback<JsonElement>() {

            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (response.body() != null) {
                    hideProgressDialog();
                     Log.e("onResponsessss: ", response.body().toString());
                    parseStyleApiResponse mResponse = new parseStyleApiResponse(CustomDesignActivity.this, response.body().toString(), mParseStyleInterface);
                    // parseResponse();
                }
            }

            @Override
            public void onFailure(Call<JsonElement> result, Throwable t) {
                hideProgressDialog();
                Log.e(TAG, "onFailure: " + result.toString());
                Toast.makeText(CustomDesignActivity.this, "Something went wrong. Please try again", Toast.LENGTH_LONG).show();
            }
        });

    }

    private RequestBody getStyleJSON() {

        Map<String, String> paramsJsonObject = new ArrayMap<>();
        paramsJsonObject.put("product_id", strProductId);
        Log.d(TAG, "strProductId " + strProductId);

        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), (new JSONObject(paramsJsonObject)).toString());

        return body;
    }

    private void showToast(String message) {
        Toast.makeText(CustomDesignActivity.this, "" + message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStyleResponseError(String msg) {
        showToast(msg);
    }

    @Override
    public void onStyleResponseSuccess(ArrayList<Style> mStyleList, String msg) {

        this.productStyleList = mStyleList;
        this.productStyleList1 = mStyleList;

        ProductStyleAdapter mProductStyleAdapter = new ProductStyleAdapter(CustomDesignActivity.this, mStyleList);
        mSpinnerProduct.setAdapter(mProductStyleAdapter);

        if (mStyleList.size() > 0) {

            productColor = productStyleList.get(0).getProductStyleColor();

            mProductColorsAdapter = new ProductColorsAdapter(productColor, CustomDesignActivity.this, mOnProductColorChangeListner);
            RV_productColor.setLayoutManager(productColorLayoutManagaer);

            //left
            Animation animationToLeft = new TranslateAnimation(230, -230, 0, 0);
            animationToLeft.setDuration(9000);
            animationToLeft.setRepeatCount(-1);
            animationToLeft.setRepeatMode(Animation.REVERSE);
            animationToLeft.setRepeatCount(Animation.INFINITE);
            tvSpinneText.setAnimation(animationToLeft);
            String textLeft =productStyleList.get(0).getStyleName();
            tvSpinneText.setText(textLeft);


            if (!is_Edit) {

                setWorkspaceLayout(mStyleList.get(0).getH(), mStyleList.get(0).getW(),
                        mStyleList.get(0).getX1(), mStyleList.get(0).getY1(), mStyleList.get(0).getX2(), mStyleList.get(0).getY2(), false);

                setProductDetails(mStyleList.get(0));
            } else {
                for (int i = 0; i < mStyleList.size(); i++) {
                    Log.d(TAG, "mStyleList.get(i).getStyleImage() " + mStyleList.get(i).getStyleImage());
                    Log.d(TAG, "strLastproductImage " + strLastproductImage);
                    if (mStyleList.get(i).getStyleImage().equalsIgnoreCase(strLastproductImage)) {
                        mSpinnerProduct.setSelection(i);
                        break;
                    }
                }
            }


            if (is_Edit) {
                try {

                    setLastEditText();
                  //  IvSaveDesign.setVisibility(View.VISIBLE);
                    if (strLastProductColor != null && !strLastProductColor.equalsIgnoreCase("")) {
                        strFinalProductColor = strLastProductColor;
                        IV_Tshirt1.setBackgroundColor(Color.parseColor(strLastProductColor + ""));
                    }


                    Float scaleX, skewX, translateX, scaleY, skewY, translateY, perspective0, perspective1, perspective2;
                    try {
                        float[] f = new float[9];
                        scaleX = Float.parseFloat(strScaleX);
                        skewX = Float.parseFloat(strSkewX);
                        translateX = Float.parseFloat(strTransalteX);
                        scaleY = Float.parseFloat(strScaley);
                        skewY = Float.parseFloat(strSkewY);
                        translateY = Float.parseFloat(strTransalteY);
                        perspective0 = Float.parseFloat(strPersp0);
                        perspective1 = Float.parseFloat(strPersp1);
                        perspective2 = Float.parseFloat(strPersp2);

                        Log.d(TAG, "onStyleResponseSuccess: translateX " + translateX);
                        Log.d(TAG, "onStyleResponseSuccess: translateY " + translateY);

                        f[0] = scaleX;
                        f[1] = skewX;
                        f[2] = translateX;
                        f[3] = scaleY;
                        f[4] = skewY;
                        f[5] = translateY;
                        f[6] = perspective0;
                        f[7] = perspective1;
                        f[8] = perspective2;

                        matrix.setValues(f);
                        IV_WorkspaceImage.setImageMatrix(matrix);

                        setWorkspaceLayout(strLastProductH, strLastProductW, strLastProductX1, strLastProductY1, strLastProductX2, strLastProductY2, false);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }


    }

    private void setSpinner() {
        productColor = new ArrayList<ProductStyleColor>();
        mSpinnerProduct.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                String productUrl = Constant.IMAGE_BASE_URL_PRODUCT_STYLES + productStyleList.get(i).getStyleImage();
                Log.d(TAG, "productUrl " + productUrl);

                //left
                Animation animationToLeft = new TranslateAnimation(230, -230, 0, 0);
                animationToLeft.setDuration(9000);
                animationToLeft.setRepeatCount(-1);
                animationToLeft.setRepeatMode(Animation.REVERSE);
                animationToLeft.setRepeatCount(Animation.INFINITE);
                tvSpinneText.setAnimation(animationToLeft);
                String textLeft = productStyleList.get(i).getStyleName();
                tvSpinneText.setText(textLeft);

                Picasso.with(CustomDesignActivity.this)
                        .load(productUrl)
                        .into(IV_Tshirt1);

                setWorkspaceLayout(productStyleList.get(i).getH(), productStyleList.get(i).getW(), productStyleList.get(i).getX1(),
                        productStyleList.get(i).getY1(), productStyleList.get(i).getX2(),
                        productStyleList.get(i).getY2(), true);


                productColor = productStyleList.get(i).getProductStyleColor();
                mProductColorsAdapter = new ProductColorsAdapter(productColor, CustomDesignActivity.this, mOnProductColorChangeListner);
                RV_productColor.setLayoutManager(productColorLayoutManagaer);

                setProductDetails(productStyleList.get(i));
            }


            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


    }

    private void setLastEditText() {

        if (ListLastTextInProduct.size() > 0) {

            for (int i = 0; i < ListLastTextInProduct.size(); i++) {

                LastTextInProduct mLastText = ListLastTextInProduct.get(i);
                if (mLastText != null) {

                    lastProductText = mLastText.getProduct_text();
                    lastProductTextFontBgColor = mLastText.getProduct_text_font_bg_color();
                    lastProductTextFontColor = mLastText.getProduct_text_font_color();
                    lastProductTextFontName = mLastText.getProduct_text_font_name();
                    lastProductTextFontSize = mLastText.getProduct_text_font_size();
                    lastBottom = mLastText.getBottom();
                    lastLeft = mLastText.getLeft();
                    lastRight = mLastText.getRight();
                    lastTop = mLastText.getTop();
                    lastTvH = mLastText.getTextH();
                    lastTvW = mLastText.getTextW();
                    lastTvX = mLastText.getTextX();
                    lastTvY = mLastText.getTextY();

                    if (et_dynamic == null) {
                        et_dynamic = new EditText(this);
                        et_dynamic.setTag(tag);
                    } else {
                        et_dynamic = new EditText(this);
                        tag++;
                        et_dynamic.setTag(tag);
                    }

                    Log.d(TAG, "lastProductText " + lastProductText);
                    Log.d(TAG, "lastProductTextFontBgColor " + lastProductTextFontBgColor);
                    Log.d(TAG, "lastProductTextFontColor " + lastProductTextFontColor);
                    Log.d(TAG, "lastProductTextFontName " + lastProductTextFontName);
                    Log.d(TAG, "lastProductTextFontSize " + lastProductTextFontSize);
                    Log.d(TAG, "lastBottom " + lastBottom);
                    Log.d(TAG, "lastLeft " + lastLeft);
                    Log.d(TAG, "lastRight " + lastRight);
                    Log.d(TAG, "lastTop " + lastTop);

                    EdittextModalClass mEdittextModalClass = new EdittextModalClass();
                    mEdittextModalClass.setEdittextNumber(tag);
                    mEdittextModalClass.setEt(et_dynamic);


                    if (lastProductTextFontName == null || lastProductTextFontName.equalsIgnoreCase("")
                            || lastProductTextFontName.equalsIgnoreCase("default")) {

                        mEdittextModalClass.setFont("default");

                    } else {
                        mEdittextModalClass.setFont(lastProductTextFontName);
                        Typeface tf = Typeface.createFromAsset(getAssets(),
                                "fonts/" + lastProductTextFontName);
                        et_dynamic.setTypeface(tf);

                    }
                    if (lastProductTextFontSize != null && !lastProductTextFontSize.equalsIgnoreCase("")) {
                        et_dynamic.setTextSize(Float.parseFloat(lastProductTextFontSize));
                    }


                    mEdittextModalClass.setTextsize(textsize);

                    et_dynamic.setSingleLine(true);
                    et_dynamic.setText(lastProductText + "");
                    et_dynamic.requestFocus();

                    if (lastProductTextFontColor == null || lastProductTextFontColor.equalsIgnoreCase("")
                            || lastProductTextFontColor.equalsIgnoreCase("default")) {

                        mEdittextModalClass.setTextColor("default");

                    } else {
                        mEdittextModalClass.setTextColor(lastProductTextFontColor);
                        Log.d(TAG, "setting text color " + lastProductTextFontColor + "");
                        et_dynamic.setTextColor(Color.parseColor(lastProductTextFontColor + ""));
                    }

                    et_dynamic.setLongClickable(false);
                    et_dynamic.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.cursor));

                    MyApplication.openKeyboard(this, LL_AddText);
                    edittextArraylist.add(mEdittextModalClass);

                    if (lastTvX != null && !lastTvX.equalsIgnoreCase("") &&
                            lastTvY != null && !lastTvY.equalsIgnoreCase("")) {

                        Float x = Float.parseFloat(lastTvX);
                        Float y = Float.parseFloat(lastTvY);

                        et_dynamic.setX(x);
                        et_dynamic.setY(y);

                        RL_Workspace1.addView(et_dynamic);
                    } else {

                        RL_Workspace1.addView(et_dynamic);
                    }


                    if (lastProductTextFontBgColor == null || lastProductTextFontBgColor.equalsIgnoreCase("")
                            || lastProductTextFontBgColor.equalsIgnoreCase("default")) {

                        mEdittextModalClass.setBackgroundColor("default");

                    } else {
                        mEdittextModalClass.setBackgroundColor(lastProductTextFontBgColor);

                        et_dynamic.setBackgroundColor(Color.parseColor(lastProductTextFontBgColor + ""));
                        Log.d(TAG, "setting backgroud color #909090 " + lastProductTextFontBgColor + "");
                    }


                    et_dynamic.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View view, MotionEvent event) {


                            for (int i = 0; i < edittextArraylist.size(); i++) {
                                int view_tag = (Integer) view.getTag();
                                if (edittextArraylist.get(i).getEdittextNumber() == view_tag) {
                                    dynamicEdittextListPosition = i;
                                    et_dynamic = edittextArraylist.get(i).getEt();
                                }
                            }

                            final int X = (int) event.getRawX();
                            final int Y = (int) event.getRawY();
                            int view_tag = (Integer) view.getTag();


                            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                                case MotionEvent.ACTION_DOWN:
                                    RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
                                    _xDelta = X - lParams.leftMargin;
                                    _yDelta = Y - lParams.topMargin;

                                    Log.d(TAG, "onTouch: here");
                                    clickCount++;
                                    break;
                                case MotionEvent.ACTION_UP:
                                    long time = System.currentTimeMillis() - startTime;
                                    duration = duration + time;
                                    if (clickCount == 2) {
                                        if (duration <= MAX_DURATION) {

                                        } else {
                                        }
                                        et_dynamic.requestFocus();
                                        et_dynamic.setFocusable(true);
                                        et_dynamic.setFocusableInTouchMode(true);
                                        clickCount = 0;
                                        duration = 0;
                                        break;
                                    } else if (clickCount == 1) {
                                        Animation animFadein = AnimationUtils.loadAnimation(getApplicationContext(),
                                                R.anim.fade_in);
                                        et_dynamic.setAnimation(animFadein);
                                        et_dynamic.startAnimation(animFadein);
                                        et_dynamic.clearFocus();
                                        et_dynamic.setFocusable(false);
                                        et_dynamic.setFocusableInTouchMode(false);
                                        et_dynamic.setCursorVisible(true);
                                    } else {
                                        et_dynamic.clearFocus();
                                        et_dynamic.setFocusable(false);
                                        et_dynamic.setFocusableInTouchMode(false);

                                    }
                                    Log.d(TAG, "onTouch: here");
                                    break;
                                case MotionEvent.ACTION_POINTER_DOWN:

                                    Log.d(TAG, "onTouch: here");
                                    break;
                                case MotionEvent.ACTION_POINTER_UP:

                                    Log.d(TAG, "onTouch: here");
                                    break;
                                case MotionEvent.ACTION_MOVE:

                                    Log.d(TAG, "onTouch: here");
                                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
                                    layoutParams.leftMargin = X - _xDelta;
                                    layoutParams.topMargin = Y - _yDelta;
                                    view.setLayoutParams(layoutParams);
                                    break;
                            }

                            RL_Workspace1.invalidate();
                            return false;
                        }
                    });

                }
            }

        }
        IV_WorkspaceImage.setFocusable(true);
        IV_WorkspaceImage.setFocusableInTouchMode(true);
        IV_WorkspaceImage.requestFocus();
        MyApplication.closeSoftKeyBoard(CustomDesignActivity.this, RL_CaptureBitmap);

    }

    @Override
    public void onProductColorChange(int colorCode, String strColor, String productStyleId) {

        strFinalProductColor = strColor;
        strproductStyleId = productStyleId;

        IV_Tshirt1.setBackgroundColor(colorCode);

    }

    private void setWorkspaceLayout(final String strHeight,
                                    final String strWeight, final String X1, final String Y1, final String X2, final String Y2, boolean isSpinnerChange) {

        final int comingHeight = Integer.parseInt(strHeight);
        final int comingWidth = Integer.parseInt(strWeight);

        RL_CaptureBitmap.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT >= 16) {
                    RL_CaptureBitmap.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    RL_CaptureBitmap.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }

                float widthScale = (float) comingWidth / 430f;
                float heightScale = (float) comingHeight / 511f;

                float cal_W = widthScale * RL_CaptureBitmap.getWidth();// calulated width
                float cal_H = heightScale * RL_CaptureBitmap.getHeight();// calulated width

                calWidth = Math.round(cal_W);
                calHeight = Math.round(cal_H);

                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(calWidth, calHeight);

                float left = (float) Integer.parseInt(X1) / 430f;
                left = left * RL_CaptureBitmap.getWidth();
                float top = (float) Integer.parseInt(Y1) / 511f;
                top = top * RL_CaptureBitmap.getHeight();

                //  layoutParams.setMargins(Math.round(left), Math.round(top), Math.round(right), Math.round(bottom));


                RL_Workspace1.setLayoutParams(layoutParams);
                RL_Workspace1.setX(left);
                RL_Workspace1.setY(top);

                if (isBitmapAvailable && ProductFragment.mBitmap != null) {

                    // if image is coming from gallery or camera then resize image here and set im imageview

                    mBitmap = ProductFragment.mBitmap;
                    int O_H = mBitmap.getHeight();
                    int O_W = mBitmap.getWidth();

                    float xScale = ((float) calWidth) / O_W;
                    float yScale = ((float) calHeight) / O_H;
                    float scale = (xScale <= yScale) ? xScale : yScale;

                    // Create a matrix for the scaling and add the scaling data
                    Matrix matrix = new Matrix();
                    matrix.postScale(scale, scale);

                    // Create a new bitmap and convert it to a format understood by the ImageView
                    mBitmap = Bitmap.createBitmap(mBitmap, 0, 0, O_W, O_H, matrix, true);

//                    int h = mBitmap.getHeight();
//                    int w = mBitmap.getWidth();
//
//                    double newH_D = h * 0.1;
//                    h = h - (int) newH_D;
//
//                    double newW_D = w * 0.1;
//                    w = w - (int) newW_D;
//                    mBitmap = Bitmap.createScaledBitmap(mBitmap, w, h, false);
                    if (IV_WorkspaceImage != null) {
                        IV_WorkspaceImage.setImageBitmap(mBitmap);
                    }

                    setImageFilterAdapter();
                    setBitmapInCenter();



                    if (isOnlyTextModeApplicable || !strIsDesignUploaded.equalsIgnoreCase("1")) {


                        LL_Animate.setVisibility(View.VISIBLE);
                        LL_Animate.startAnimation(animShow);


                    }

                }else {
                    if (strIsDesignUploaded.equals("0")) {



                        LL_Animate.setVisibility(View.VISIBLE);
                        LL_Animate.startAnimation(animShow);


                        getSclaedBitmapFromURL(strDesignImageUrl, IV_WorkspaceImage);
                    }
                    else {
                        Log.e("PPPPPPPPPPPPPPP",strIsDesignUploaded);
                        //Log.e("isOnlyTextMod",""+isOnlyTextModeApplicable);
                        getSclaedBitmapFromURL(strDesignImageUrl, IV_WorkspaceImage);

                    }

                }


            }
        });
        Log.d(TAG, "onGlobalLayout: strIsDesignUploaded " + strIsDesignUploaded);
        Log.d(TAG, "onGlobalLayout: isOnlyTextModeApplicable " + isOnlyTextModeApplicable);
      /*  if (!strIsDesignUploaded.equalsIgnoreCase("1") || isOnlyTextModeApplicable ) {

            llTopBarParent = (LinearLayout) findViewById(R.id.llTopBarParent);
            llTopBarParent.setVisibility(View.GONE);
            seletText();

            LL_Animate.setVisibility(View.VISIBLE);
            LL_Animate.startAnimation(animShow);

            RL_AnimEdit.setVisibility(View.INVISIBLE);
            RL_Animfilter.setVisibility(View.INVISIBLE);
        }*/

    }


    private void getSclaedBitmapFromURL(final String strDesignImageUrl, final ImageView Img) {

        new AsyncTask<Void, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(Void... voids) {
                try {

                    if (!is_Edit) {
                        matrix = new Matrix();
                        isBitmapSetInCenter = false;
                    }


                    URL url = new URL(strDesignImageUrl);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    InputStream input = connection.getInputStream();
                    Bitmap myBitmap = BitmapFactory.decodeStream(input);

                    return myBitmap;


                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);

                mBitmap = bitmap;


                resizeBitmap();

                if (Img != null) {
                    //  Img.setImageBitmap(bitmap);
                }
                if (!imageFilterSet) {
                    setImageFilterAdapter();
                }

                if (strFinalFilterEffectName != null && !strFinalFilterEffectName.equalsIgnoreCase("")) {

                    FilterEffects mFilterEffects = new FilterEffects(CustomDesignActivity.this);

                    mFilterEffects.applyfilter(strFinalFilterEffectName, mBitmap, IV_WorkspaceImage, mReturnFilteredBitmap);
                    strFinalFilterEffectName = "" + strFinalFilterEffectName;
                } else {
                    IV_WorkspaceImage.setImageBitmap(mBitmap);
                }




            }
        }.execute();
    }


    private void resizeBitmap() {

        int newH = 0, newW;
        int O_H = mBitmap.getHeight();
        int O_W = mBitmap.getWidth();

        float xScale = ((float) calWidth) / O_W;
        float yScale = ((float) calHeight) / O_H;
        float scale = (xScale <= yScale) ? xScale : yScale;

        // Create a matrix for the scaling and add the scaling data
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);

        // Create a new bitmap and convert it to a format understood by the ImageView
        mBitmap = Bitmap.createBitmap(mBitmap, 0, 0, O_W, O_H, matrix, true);

        int h = mBitmap.getHeight();
        int w = mBitmap.getWidth();

        double newH_D = h * 0.1;
        h = h - (int) newH_D;

        double newW_D = w * 0.1;
        w = w - (int) newW_D;
        mBitmap = Bitmap.createScaledBitmap(mBitmap, w, h, false);

        if (!is_Edit) {
            setBitmapInCenter();
        }


    }

    private void parseSaveProductResponse(String response, String ButtonName) {

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

                        //Log.d(TAG,"ButtonName " + ButtonName);

                        if (ButtonName.equalsIgnoreCase("cart")) {
                            if( !is_Edit_Cart){
                                Home.CartCount++;
                                TvCart.setVisibility(View.VISIBLE);
                                Log.d(TAG, "Home.CartCount " + Home.CartCount);
                                TvCart.setText(Home.CartCount + "");
                                SaveRecords.saveIntegerToPreference(getResources().getString(R.string.key_cartcount), Home.CartCount, CustomDesignActivity.this);
                                if (Home.tvCart != null && Home.CartCount > 0 ) {

                                    Home.tvCart.setText(Home.CartCount + "");
                                    Home.tvCart.setVisibility(View.VISIBLE);
                                }
                            }

                        }


                        showToast(strMessage);
                    } else {
                        showToast(strMessage);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private void showProgressDialog(String strMessage) {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.setMessage(strMessage);
        } else {
            progressDialog = ProgressDialog.show(this, "", strMessage, false);
        }
    }

    private void hideProgressDialog() {
        try {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage(), ex);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (Home.CartCount != 0) {

            TvCart.setText(Home.CartCount + "");
            TvCart.setVisibility(View.VISIBLE);

        } else {
            TvCart.setVisibility(View.GONE);
        }
    }


    public void onWindowFocusChanged(boolean hasFocus) {
        // TODO Auto-generated method stub
        super.onWindowFocusChanged(hasFocus);

        Log.d(TAG, "onWindowFocusChanged: " + RL_CaptureBitmap.getWidth() + " , " + RL_CaptureBitmap.getHeight());



    }

    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        Log.d(TAG, "dpToPx: px " + px);
        return px;
    }

    @Override
    public void onCancelButtonClick(String DialogName, String Message) {


    }

    @Override
    public void onOkButtonClick(String DialogName, String Message) {

        if (Message.equalsIgnoreCase("reset_design")) {

            is_Edit = false;
            Log.d(TAG, "onOkButtonClick: strProductId " + strProductId);
            Log.d(TAG, "onOkButtonClick: strDesignImageUrl " + strDesignImageUrl);
            Log.d(TAG, "onOkButtonClick: strDesignId " + strDesignId);
            Log.d(TAG, "onOkButtonClick: strDesignText " + strDesignText);
            Log.d(TAG, "onOkButtonClick: isBitmapAvailable " + isBitmapAvailable);

            SaveRecords.saveToPreference("is_edit", "1", CustomDesignActivity.this);
            SaveRecords.saveToPreference("str_product_id", strProductId, CustomDesignActivity.this);
            SaveRecords.saveToPreference("str_design_image", strDesignImageUrl, CustomDesignActivity.this);
            SaveRecords.saveToPreference("str_design_id", strDesignId, CustomDesignActivity.this);
            SaveRecords.saveToPreference("str_design_text", strDesignText, CustomDesignActivity.this);
            if (isBitmapAvailable) {
                SaveRecords.saveToPreference("get_bitmap", "1", CustomDesignActivity.this);
            } else {
                SaveRecords.saveToPreference("get_bitmap", "0", CustomDesignActivity.this);
            }

            CustomDesignActivity.this.recreate();


        } else {
            Home.ivSetting.setVisibility(View.GONE);
            this.finish();
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();

        mUtilities.showCancelableDialog(CustomDesignActivity.this, "Do you want to go back ?", "Yes", mZezignDialogInterface, "", "back");


    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
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
                photoURI = FileProvider.getUriForFile(this,
                        "zezgn.com.zezgn.fileprovider",
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

        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void setBitmapInCenter() {

        IV_WorkspaceImage.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT >= 16) {
                    IV_WorkspaceImage.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    IV_WorkspaceImage.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }


                int h = mBitmap.getHeight();
                int w = mBitmap.getWidth();

                if (!isBitmapSetInCenter) {
                    isBitmapSetInCenter = true;
                    leftOffset = (calWidth - w) / 2f;
                    topOffset = (calHeight - h) / 2f;
                    Log.d(TAG, "onGlobalLayout: leftOffset " + leftOffset);
                    Log.d(TAG, "onGlobalLayout: topOffset " + topOffset);

                    matrix.preTranslate(leftOffset, topOffset);
                    IV_WorkspaceImage.setImageMatrix(matrix);
                }

            }
        });
    }

    @Override
    public void returnFilteredBitmap(Bitmap bitmap) {
        if (bitmap != null) {
            // mBitmap = bitmap;
        }

    }
    public void deleteAllFunction() {


        // this will open dialog to add image from camera or gallery
        final CharSequence[] items = {"Text", "Image", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Do you want to delete this");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {


                if (items[item].equals("Text")) {

                    for (int i = 0, count = RL_Workspace1.getChildCount(); i < count; ++i) {
                        View view = RL_Workspace1.getChildAt(i);
                        if (view instanceof EditText) {
                            ((EditText) view).setText("");
                        }
                    }

                    dialog.dismiss();

                } else if (items[item].equals("Image")) {
                    IV_WorkspaceImage.setImageResource(0);
                    dialog.dismiss();

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();

    }
}
