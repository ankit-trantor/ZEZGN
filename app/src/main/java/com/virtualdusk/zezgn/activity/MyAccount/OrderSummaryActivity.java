package com.virtualdusk.zezgn.activity.MyAccount;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonElement;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import com.virtualdusk.zezgn.Model.OrderClass;
import com.virtualdusk.zezgn.Model.Order_Products;
import com.virtualdusk.zezgn.R;
import com.virtualdusk.zezgn.activity.AbstractActivity;
import com.virtualdusk.zezgn.activity.Adapter.MyOrdersListAdapter;
import com.virtualdusk.zezgn.activity.Adapter.OrderedListInfoAdapter;
import com.virtualdusk.zezgn.api.GetOrderApi;
import com.virtualdusk.zezgn.api.OrderSummaryApi;
import com.virtualdusk.zezgn.utils.ValidationMethod;

/**
 * Created by Amit Sharma on 10/18/2016.
 */
public class OrderSummaryActivity extends AbstractActivity {

    private  String TAG = OrderSummaryActivity.class.getSimpleName().toString();
    MyOrdersListAdapter myOrdersListAdapter;
    private ArrayList<OrderClass> arrayList;
    private ArrayList<OrderProducts> arrProducts;
    private Gallery galleryProduct;
    private ImageView ivOrderRecived,ivShppedBy,ivDelivered,ivBack;
    private TextView tvOrderRecivedDate,tvShippedByDate,tvDeliveryDate;
    private TextView tvSubTotal,tvShippingPrice,tvName,tvDeliveryAddress,tvEmail
            ,tvMobile,tvOrderTotalValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_summary_screen);
        arrProducts=new ArrayList<>();
        Intent intent=getIntent();

        mOrderId=intent.getStringExtra("KEY_ORDER_ID");
        findRes();
    }

    private void findRes() {

        galleryProduct=(Gallery)findViewById(R.id.galleryProduct);
        ivOrderRecived=(ImageView)findViewById(R.id.iv1);
        ivShppedBy=(ImageView)findViewById(R.id.iv2);
        ivDelivered=(ImageView)findViewById(R.id.iv3);
        ivBack=(ImageView)findViewById(R.id.ivBack);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               finish();
            }
        });

        tvOrderRecivedDate=(TextView)findViewById(R.id.tvOrderRecivedDate);
        tvShippedByDate=(TextView)findViewById(R.id.tvShippedByDate);
        tvDeliveryDate=(TextView)findViewById(R.id.tvDeliveryDate);
        tvSubTotal=(TextView)findViewById(R.id.tvSubTotal);
        tvShippingPrice=(TextView)findViewById(R.id.tvShipping);
        tvDeliveryAddress=(TextView)findViewById(R.id.tvDeliveryAddress);
        tvName=(TextView)findViewById(R.id.tvName);
        tvEmail=(TextView)findViewById(R.id.tvEmail);
        tvMobile=(TextView)findViewById(R.id.tvMobile);
        tvOrderTotalValue=(TextView)findViewById(R.id.tvOrderTotalValue);

             arrayList=new ArrayList<>();



        getDesignNotificationListViaRetrofit();
    }

    private void getDesignNotificationListViaRetrofit() {
        arrProducts=new ArrayList<>();
        //Creating an object of our api interface
        Retrofit restAdapter = OrderSummaryApi.retrofit;
        OrderSummaryApi registerApi = restAdapter.create(OrderSummaryApi.class);

        Call<JsonElement> call = registerApi.getInfoJSON(getParams());

        call.enqueue(new Callback<JsonElement>() {

            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                Log.e(TAG, "getDesignNotificationListViaRetrofit:onResponse: " + response.body());

                try {
                    JSONObject jsnon = new JSONObject(response.body().toString());
                    String response1 = jsnon.getString("response");
                    JSONObject jsonObject=new JSONObject(response1);
                    String statusCode = jsonObject.getString("code");
                    String data = jsonObject.getString("data");

                    if (statusCode.equals("200"))
                    {

                        JSONObject jsonObjectData=new JSONObject(data);

                        String id=jsonObjectData.getString("id");
                        String unique_id=jsonObjectData.getString("unique_id");
                        String user_id=jsonObjectData.getString("user_id");
                        String total_price=jsonObjectData.getString("total_price");
                        String coupon_code=jsonObjectData.getString("coupon_code");
                        String coupon_price=jsonObjectData.getString("coupon_price");
                        String shipping_address=jsonObjectData.getString("shipping_address");
                        JSONObject jsonObject_Shipping=new JSONObject(shipping_address);
                        String fname=jsonObject_Shipping.getString("fname");
                        String lname=jsonObject_Shipping.getString("lname");
                        String email=jsonObject_Shipping.getString("email");
                        String contact_no=jsonObject_Shipping.getString("contact_no");
                        String address=jsonObject_Shipping.getString("address");
                        String city=jsonObject_Shipping.getString("city");
                        String state=jsonObject_Shipping.getString("state");
                        String country_id=jsonObject_Shipping.getString("country_id");
                        String zipcode=jsonObject_Shipping.getString("zipcode");
                        String country_name=jsonObject_Shipping.getString("country_name");

                        String billing_address=jsonObjectData.getString("billing_address");
                        String payment_method=jsonObjectData.getString("payment_method");
                        String status=jsonObjectData.getString("status");
                        String est_delivery_date=jsonObjectData.getString("est_delivery_date");
                        String shipping_details=jsonObjectData.getString("shipping_details");
                        String shipping_price=jsonObjectData.getString("shipping_price");
                        String created=jsonObjectData.getString("created");
                        String modified=jsonObjectData.getString("modified");
                        String deleted=jsonObjectData.getString("deleted");
                        String order_products=jsonObjectData.getString("order_products");


                        tvEmail.setText("Email: "+email);
                        tvName.setText(fname+" "+lname);
                        tvMobile.setText("Mobile :"+contact_no);
                        tvDeliveryAddress.setText(address+","+city+" "+state+" "+country_name+"-"+zipcode);
                        tvShippingPrice.setText("AED "+shipping_price);
                        tvSubTotal.setText("AED "+total_price);

                        int pricetotal=Integer.valueOf(total_price)+Integer.valueOf(shipping_price);

                        tvOrderTotalValue.setText("AED "+pricetotal);

                        String orderRcvdDate=ValidationMethod.parseDateToSimpleFormat(created);
                        String orderShippedDate=ValidationMethod.parseDateToSimpleFormat(modified);
                        tvOrderRecivedDate.setText(orderRcvdDate);
                        tvShippedByDate.setText(orderShippedDate);

                        tvDeliveryDate.setText(orderShippedDate);

                        Log.e(TAG, "onResponse: "+status );
                        if (status.equals("1"))
                        {
                            //Recived
                            tvShippedByDate.setVisibility(View.GONE);
                            tvDeliveryDate.setVisibility(View.GONE);
                        }
                        else if (status.equals("3"))
                        {
                            ivShppedBy.setImageResource(R.mipmap.tick);

                            //Ship
                            tvShippedByDate.setVisibility(View.VISIBLE);
                            tvDeliveryDate.setVisibility(View.GONE);

                        }
                        else if (status.equals("4"))
                        {
                            ivShppedBy.setImageResource(R.mipmap.tick);
                            ivDelivered.setImageResource(R.mipmap.tick);
                            //deliver
                            tvShippedByDate.setVisibility(View.VISIBLE);
                            tvDeliveryDate.setVisibility(View.VISIBLE);

                        }
                        else
                        {

                        }

                        JSONArray jsonArray=new JSONArray(order_products);

                        for (int i=0;i<jsonArray.length();i++)
                        {

//
//                            "price":129,
//                                "shipping_price":20,
//                                "quantity":1,
//                                "design_id":2,
//                                "product_style_id":1,
//                                "product_id":1,
//                                "created":"2016-09-26T15:54:56+0530",
//                                "modified":"2016-09-26T15:54:56+0530",

                            JSONObject jsonObject_orderPro=jsonArray.getJSONObject(i);
                            OrderProducts orderClass=new OrderProducts();
                            orderClass.setId(jsonObject_orderPro.getString("id"));
                            orderClass.setOrder_id(jsonObject_orderPro.getString("order_id"));
                            orderClass.setTitle(jsonObject_orderPro.getString("title"));
                            orderClass.setProduct_thumb(jsonObject_orderPro.getString("product_thumb"));
                            orderClass.setProduct_color(jsonObject_orderPro.getString("product_color"));
                            orderClass.setPrice(jsonObject_orderPro.getString("price"));
                            orderClass.setShipping_price(jsonObject_orderPro.getString("shipping_price"));
                            orderClass.setQuantity(jsonObject_orderPro.getString("quantity"));
                            orderClass.setDesign_id(jsonObject_orderPro.getString("design_id"));
                            orderClass.setProduct_style_id(jsonObject_orderPro.getString("product_style_id"));
                            orderClass.setProduct_id(jsonObject_orderPro.getString("product_id"));
                            orderClass.setCreated(jsonObject_orderPro.getString("created"));
                            orderClass.setModified(jsonObject_orderPro.getString("modified"));
                            orderClass.setProduct(jsonObject_orderPro.getString("product"));
                           JSONObject jsonProduct=new JSONObject(jsonObject_orderPro.getString("product"));
                           JSONObject jsonDesign=new JSONObject(jsonObject_orderPro.getString("design"));

                            orderClass.setProduct_name(jsonProduct.getString("product_name"));
                            orderClass.setDesign_title(jsonDesign.getString("title"));


                            arrProducts.add(orderClass);

                        }
//                        est_delivery_date.
//
//                                if stq=1 //Recived order
//
//                        3 Shipp
//                            4=deliver


                        Log.e(TAG, "onResponse: "+arrProducts.size());



                    }

setAdapter();


                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }


            }

            @Override
            public void onFailure(Call<JsonElement> result, Throwable t) {
                Log.e(TAG, "onFailure: " + result.toString());
            }
        });



    }

    private String mOrderId;
    private Order_Products getParams()
    {
        Log.e(TAG, "Order Id:... "+mOrderId );

        Order_Products categoryList=new Order_Products();

        categoryList.setOrder_id(mOrderId);


        return categoryList;
    }

    private void setAdapter()
    {
        orderAdapter=new OrderAdapter(OrderSummaryActivity.this,arrProducts);
        galleryProduct.setAdapter(orderAdapter);
    }

    private OrderAdapter orderAdapter;
    public class OrderAdapter extends BaseAdapter {

        Context c;

        private LayoutInflater inflater = null;

        private ArrayList<OrderProducts> orderArr;
        private DisplayImageOptions options;
        private ImageLoader imageLoader = null;// ImageLoader.getInstance();

        public OrderAdapter(Context context, ArrayList<OrderProducts> arrayList) {
            c = context;
            inflater = (LayoutInflater) c
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.orderArr = arrayList;
            imageLoader = ImageLoader.getInstance();
            options = new DisplayImageOptions.Builder()
                    .showStubImage(R.mipmap.logo)
                    .showImageForEmptyUri(R.mipmap.logo)
                    .showImageOnFail(R.mipmap.logo)
                    .resetViewBeforeLoading()
                    .delayBeforeLoading(1000)
                    .cacheInMemory()
                    .cacheOnDisc()
                    .build();

        }

        @Override
        public int getCount() {
            return orderArr.size();
        }

        @Override
        public Object getItem(int i) {
            return i;
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {
            View vi = view;
            if (view == null)

                vi = inflater.inflate(R.layout.order_list_summary, null);

            ImageView ivImageProduct=(ImageView) vi.findViewById(R.id.ivImage);
            TextView tvProductName=(TextView)vi.findViewById(R.id.tvProductName);
            TextView tvProductCategory=(TextView)vi.findViewById(R.id.tvProductCategory);
            TextView tvProductQtyValue=(TextView)vi.findViewById(R.id.tvProductQtyValue);
            TextView tvProductPrice=(TextView)vi.findViewById(R.id.tvProductPrice);
            TextView tvProductShippingPrice=(TextView)vi.findViewById(R.id.tvProductShippingPrice);
            TextView tvProductDate=(TextView)vi.findViewById(R.id.tvProductDate);

            Log.e(TAG, "getView: "+ orderArr.get(i).getDesign_title()+","+orderArr.get(i).getProduct_name());
            tvProductName.setText(orderArr.get(i).getDesign_title());
            tvProductCategory.setText(orderArr.get(i).getProduct_name());
            tvProductQtyValue.setText(orderArr.get(i).getQuantity());
            tvProductPrice.setText("AED "+orderArr.get(i).getPrice());
            tvProductShippingPrice.setText("AED "+orderArr.get(i).getShipping_price());

            final String encodedString = orderArr.get(i).getProduct_thumb();//"data:image/png;base64, ....";
            final String pureBase64Encoded = encodedString.substring(encodedString.indexOf(",")  + 1);
            final byte[] decodedBytes = Base64.decode(pureBase64Encoded, Base64.DEFAULT);
            Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
            ivImageProduct.setImageBitmap(decodedBitmap);

            String date= ValidationMethod.parseDateToSimpleFormat(orderArr.get(i).getCreated());
            tvProductDate.setText(date);

            return vi;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }


    }
}
