package com.example.sdk_demo_app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.axis.axismerchantsdk.AxisUpi;
import com.axis.axismerchantsdk.model.MerchantKeys;
import com.axis.axismerchantsdk.model.UPIRegistrationParams;
import com.axis.axismerchantsdk.model.UpiTransactionParams;
import com.axis.axismerchantsdk.util.RegistrationCallback;
import com.axis.axismerchantsdk.util.TransactionCallback;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class MainActivity extends AppCompatActivity {

    public  static final String MERCH_ID = "";
    public  static final String MERCH_CHAN_ID = "";
    public  final String MCC_CODE = "";
    public  static final String MERCHANT_KEY = "";
    public  String UNQ_TXN_ID="UNQ_TXN_ID";
    public  String UNQ_CUST_ID="UNQ_CUST_ID";
    public  final String MOBILENO="918123715658";
    public  final String EMAILID="parth.vora@juspay.in";
    public  String AMOUNT="2.00";
    public  final String TXN_DTL="TRAVEL";

    public  final String CURRENCY="INR";
    public  String ORDER_ID;
    public  String UDF_PARAMS = new JSONObject().toString();
    public  String MERCH_CHECKSUM="MERCH_CHECKSUM";

    Button btnRegPay;
    Button btnStrictPay;
    Button btnRegister;

    SharedPreferences sharedPreferences;
    HashMap<String, String> customParameters = new HashMap<String, String>();
    EditText editTextCustId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = this.getSharedPreferences("DEMO", Context.MODE_PRIVATE);
        btnRegPay = (Button) findViewById(R.id.btnRegPay);
        btnStrictPay = (Button) findViewById(R.id.btnStrictPay);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        editTextCustId = (EditText) findViewById(R.id.custId);

        btnRegPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getParams()){
                    onProcess();
                    callLibraryForPayment();
                } else{
                    Toast.makeText(MainActivity.this, "Please enter all the Fields", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getParams()){
                    onProcess();
                    callLibraryForRegistration();
                } else{
                    Toast.makeText(MainActivity.this, "Please enter all the Fields", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        customParameters = new HashMap<String, String>();
        setParams();
    }

    boolean getParams() {
        UNQ_CUST_ID = String.valueOf(editTextCustId.getText());
        if(UNQ_CUST_ID.equals(""))
            return false;
        return true;
    }

    public int getUniqTxnId() {
        return 5000 + (int)(Math.random() * ((1000000 - 5000) + 1));
    }

    void onProcess(){
        int txnId = getUniqTxnId();
        UNQ_TXN_ID = "TEST"+txnId;
        return;
    }

    void setParams() {
        String custId = sharedPreferences.getString("customerId",null);
        if(custId==null) {
            custId = "JUS"+getUniqTxnId();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("customerId",custId);
            editor.commit();
        }
        UNQ_CUST_ID = custId;
        ORDER_ID="PAY_1234";
        editTextCustId.setText(UNQ_CUST_ID);

        try{
            final String vpa = getSharedPreferences("UPI", Context.MODE_PRIVATE).getString(UNQ_CUST_ID, "");
            if(vpa.equals("")){
                btnStrictPay.setVisibility(View.GONE);
            } else {
                btnStrictPay.setVisibility(View.VISIBLE);
                btnStrictPay.setText("PAY ("+vpa+")");
            }
            btnStrictPay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(getParams()){
                        if(!vpa.equals("")) {
                            customParameters = new HashMap<String, String>();
                            customParameters.put(MerchantKeys.isStrictPaymentFlow, "true");
                            customParameters.put(MerchantKeys.customerVpa,vpa);
                        }
                        onProcess();
                        callLibraryForPayment();
                    } else{
                        Toast.makeText(MainActivity.this, "Please enter all the Fields", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        } catch (Exception e) {

        }

    }

    void callLibraryForPayment(){
        AxisUpi axisUpi = new AxisUpi(MainActivity.this);
        final UpiTransactionParams upiParams = new UpiTransactionParams();
        upiParams.setMerchantId(MERCH_ID);
        upiParams.setMerchantChannelId(MERCH_CHAN_ID);
        upiParams.setMcc(MCC_CODE);
        upiParams.setMerchantRequestId(UNQ_TXN_ID);
        upiParams.setMerchantCustomerId(UNQ_CUST_ID);
        upiParams.setCustomerMobileNumber(MOBILENO);
        upiParams.setCustomerEmail(EMAILID);
        upiParams.setAmount(AMOUNT);
        upiParams.setTransactionDescription(TXN_DTL);
        upiParams.setCurrency(CURRENCY);
        upiParams.setOrderId(ORDER_ID);
        upiParams.setUdfParameters(UDF_PARAMS);
        if(customParameters != null && customParameters.containsKey(MerchantKeys.customerVpa)) {
            upiParams.setCustomerVpa(customParameters.get(MerchantKeys.customerVpa));
        }
        if(customParameters != null && customParameters.containsKey("isStrictPaymentFlow")) {
            upiParams.setIsStrictPaymentFlow(customParameters.get("isStrictPaymentFlow"));
        }
        String payload = MERCH_ID+MERCH_CHAN_ID+UNQ_TXN_ID+UNQ_CUST_ID+MCC_CODE+AMOUNT+TXN_DTL+CURRENCY+MOBILENO+EMAILID+ORDER_ID+UDF_PARAMS;
        MERCH_CHECKSUM = hmacDigest(payload, MERCHANT_KEY, "HmacSHA256");
        upiParams.setMerchantChecksum(MERCH_CHECKSUM);

        upiParams.setAllowOtherVpa("false");
        upiParams.setShowOtherPaymentOptions("true");
        upiParams.setOtherPaymentOptionType("COLLECT");

        Log.d("SDK","TRANSACTION "+upiParams);

        axisUpi.startTransaction(upiParams, new TransactionCallback() {
            @Override
            public void onTransactionComplete(HashMap<String,String> upiResponse) {
                Intent intent = new Intent(MainActivity.this, SDKResponse.class);
                getSharedPreferences("UPI", Context.MODE_PRIVATE).edit().putString(upiResponse.get(MerchantKeys.merchantCustomerId),
                        upiResponse.get(MerchantKeys.customerVpa)).commit();
                for (String name: upiResponse.keySet()){
                    String key = name;
                    String value = upiResponse.get(name);
                    intent.putExtra(key,value);
                }
                startActivity(intent);
            }

        });
    }

    void callLibraryForRegistration(){
        AxisUpi axisUpi = new AxisUpi(MainActivity.this);
        UPIRegistrationParams upiParams = new UPIRegistrationParams();
        upiParams.setMerchantId(MERCH_ID);
        upiParams.setMerchantChannelId(MERCH_CHAN_ID);
        upiParams.setMcc(MCC_CODE);
        upiParams.setMerchantRequestId(UNQ_TXN_ID);
        upiParams.setMerchantCustomerId(UNQ_CUST_ID);
        upiParams.setCustomerMobileNumber(MOBILENO);
        upiParams.setCustomerEmail(EMAILID);
        upiParams.setUdfParameters(UDF_PARAMS);
        if(customParameters != null && customParameters.containsKey("bankCode")) {
            upiParams.setBankCode(customParameters.get("bankCode"));
        }
        String payload = MERCH_ID+MERCH_CHAN_ID+UNQ_TXN_ID+UNQ_CUST_ID+MCC_CODE+MOBILENO+EMAILID+UDF_PARAMS;
        MERCH_CHECKSUM = hmacDigest(payload, MERCHANT_KEY, "HmacSHA256");
        upiParams.setMerchantChecksum(MERCH_CHECKSUM);

        Log.d("SDK","REGISTRATION "+upiParams);

        axisUpi.startRegistration(upiParams, new RegistrationCallback() {
            @Override
            public void onRegistrationComplete(HashMap<String,String> upiResponse) {
                Intent intent = new Intent(MainActivity.this, SDKResponse.class);
                if(upiResponse.containsKey(MerchantKeys.customerVpa)){
                    getSharedPreferences("UPI", Context.MODE_PRIVATE).edit().putString(upiResponse.get(MerchantKeys.merchantCustomerId),
                            upiResponse.get(MerchantKeys.customerVpa)).commit();
                }
                for (String name: upiResponse.keySet()){
                    String key = name;
                    String value = upiResponse.get(name);
                    intent.putExtra(key,value);
                }
                startActivity(intent);
            }
        });
    }

    public static String hmacDigest(String msg, String keyString, String algo) {
        String digest = null;
        try {
            SecretKeySpec key = new SecretKeySpec((keyString).getBytes("UTF-8"), algo);
            Mac mac = Mac.getInstance(algo);
            mac.init(key);
            byte[] bytes = mac.doFinal(msg.getBytes("ASCII"));
            StringBuffer hash = new StringBuffer();
            for (int i = 0; i < bytes.length; i++) {
                String hex = Integer.toHexString(0xFF & bytes[i]);
                if (hex.length() == 1) {
                    hash.append('0');
                }
                hash.append(hex);
            }
            digest = hash.toString();
        } catch (UnsupportedEncodingException e) {
        } catch (InvalidKeyException e) {
        } catch (NoSuchAlgorithmException e) {
        }
        return digest;
    }

}
