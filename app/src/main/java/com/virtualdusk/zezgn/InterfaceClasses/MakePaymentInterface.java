package com.virtualdusk.zezgn.InterfaceClasses;

/**
 * Created by bhart.gupta on 10-Nov-16.
 */

public interface MakePaymentInterface {

    public void onPaymentSuccess(String ClassName, String TxnId,String paymentData);
    public void onPaymentError(String ClassName, String errorMsg);
}
