package com.idmission.sdk2.sample;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Parcelable;
import androidx.annotation.Nullable;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.idmission.sdk2.R;
import com.idmission.sdk2.capture.IdMissionCaptureLauncher;
import com.idmission.sdk2.client.model.CommonApiResponse;
import com.idmission.sdk2.client.model.ExtractedIdData;
import com.idmission.sdk2.client.model.ExtractedPersonalData;
import com.idmission.sdk2.client.model.HostDataResponse;
import com.idmission.sdk2.client.model.InitializeResponse;
import com.idmission.sdk2.client.model.Response;
import com.idmission.sdk2.client.model.ResponseCustomerData;
import com.idmission.sdk2.identityproofing.IdentityProofingSDK;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

public class MainActivity extends Activity {
    String InitializeApiBaseUrl = "https://kyc.idmission.com/";
    String ApiBaseUrl = "https://api.idmission.com/";
    String LoginID = "";
    String Password = "";
    long MerchantID = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        ((Button) findViewById(R.id.button_init)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                initializeSDK("", "", ((EditText) findViewById(R.id.edit_text_login_id)).getText().toString(),
                        ((EditText) findViewById(R.id.edit_text_password)).getText().toString(),
                        ((EditText) findViewById(R.id.edit_text_merchant_id)).getText().toString());
            }
        });

        ((Button) findViewById(R.id.service_id_20)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                serviceID20();
            }
        });

        ((Button) findViewById(R.id.service_id_10)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                serviceID10();
            }
        });

        ((Button) findViewById(R.id.service_id_185)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                serviceID185();
            }
        });

        ((Button) findViewById(R.id.service_id_660)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                serviceID660();
            }
        });

        ((Button) findViewById(R.id.service_id_50)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                serviceID50(((EditText) findViewById(R.id.edit_text_customer_number)).getText().toString());
            }
        });

        ((Button) findViewById(R.id.service_id_175)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                serviceID175(((EditText) findViewById(R.id.edit_text_customer_number)).getText().toString());
            }
        });

        ((Button) findViewById(R.id.service_id_105)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                serviceID105(((EditText) findViewById(R.id.edit_text_customer_number)).getText().toString());
            }
        });

        ((Button) findViewById(R.id.submit)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                submitResult();
            }
        });
    }

    public void  initializeSDK(String initializeURL, String url, String loginID, String password, String merchantID) {
        if(!StringUtils.isEmpty(initializeURL)){
            InitializeApiBaseUrl = initializeURL;
        }
        if(!StringUtils.isEmpty(url)){
            ApiBaseUrl = url;
        }
        if(!StringUtils.isEmpty(loginID)){
            LoginID = loginID;
        }
        if(!StringUtils.isEmpty(password)){
            Password = password;
        }
        if(!StringUtils.isEmpty(merchantID)){
            MerchantID = Long.parseLong(merchantID);
        }
        new BackgroundTask().execute();
    }

    public void  serviceID20() {
        IdentityProofingSDK.INSTANCE.idValidation(this);
    }

    public void  serviceID10() {
        IdentityProofingSDK.INSTANCE.idValidationAndMatchFace(this);
    }

    public void  serviceID185() {
        IdentityProofingSDK.INSTANCE.identifyCustomer(this, null, null);
    }

    public void  serviceID660() {
        IdentityProofingSDK.INSTANCE.liveFaceCheck(this);
    }

    public void  serviceID50(String uniqueCustomerNumber) {
        if(!StringUtils.isEmpty(uniqueCustomerNumber)){
            IdentityProofingSDK.INSTANCE.idValidationAndcustomerEnroll(this, uniqueCustomerNumber);
        }
    }

    public void  serviceID175(String uniqueCustomerNumber) {
        if(!StringUtils.isEmpty(uniqueCustomerNumber)){
            IdentityProofingSDK.INSTANCE.customerEnrollBiometrics(this, uniqueCustomerNumber);
        }
    }

    public void  serviceID105(String uniqueCustomerNumber) {
        if(!StringUtils.isEmpty(uniqueCustomerNumber)){
            IdentityProofingSDK.INSTANCE.customerVerification(this, uniqueCustomerNumber);
        }
    }

    public void submitResult(){
        new FinalSubmitTask().execute();
    }

    public void popupMessage(String message){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setTitle("Response");
        alertDialogBuilder.setNegativeButton("OK", new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    class BackgroundTask extends AsyncTask<Void, Void, Response<InitializeResponse>> {

        @Override
        protected void onPostExecute(Response<InitializeResponse> initializeResponseResponse) {
            super.onPostExecute(initializeResponseResponse);
            popupMessage(initializeResponseResponse.getResult().toString());
        }

        @Override
        protected Response<InitializeResponse> doInBackground(Void... voids) {
            Response<InitializeResponse> response =
                    IdentityProofingSDK.INSTANCE.initialize(MainActivity.this,
                            InitializeApiBaseUrl,
                            ApiBaseUrl,
                            LoginID,
                            Password,
                            MerchantID,
                            false,
                            true);
            return response;
        }
    }

    class FinalSubmitTask extends AsyncTask<Void, Void, Response<CommonApiResponse>> {

        @Override
        protected void onPostExecute(Response<CommonApiResponse> apiResponse) {
            super.onPostExecute(apiResponse);
            if(apiResponse.getErrorStatus()!=null) {
                popupMessage(apiResponse.getErrorStatus().getStatusMessage());
            } else  {
                popupMessage(parseResponse(apiResponse));
            }
        }

        @Override
        protected Response<CommonApiResponse> doInBackground(Void... voids) {

            Response<CommonApiResponse> response =
                    IdentityProofingSDK.INSTANCE.finalSubmit(MainActivity.this);
            return response;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (requestCode == IdMissionCaptureLauncher.CAPTURE_REQUEST_CODE) {
                try{
                    Parcelable[] processedCaptures = data.getExtras().getParcelableArray(IdMissionCaptureLauncher.EXTRA_PROCESSED_CAPTURES);

                    JSONObject jo = new JSONObject();
                    jo.put("Image1", processedCaptures[0].toString());
                    if(processedCaptures.length>1){
                        jo.put("Image2", processedCaptures[1].toString());
                    }
                    if(processedCaptures.length>2){
                        jo.put("Image3", processedCaptures[2].toString());
                    }
                    if(processedCaptures.length>3){
                        jo.put("Image4", processedCaptures[3].toString());
                    }

                    popupMessage(jo.toString());
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onNewIntent(Intent intent) {}

    private String parseResponse(Response<CommonApiResponse> response){
        JSONObject jo = new JSONObject();
        try {
            jo.put("AdditionalData", response.getResult().getAdditionalData());
        }catch(Exception e){}
        try {
            JSONObject responseCustomerData = new JSONObject();
            JSONObject extractedIdData = new JSONObject();
            JSONObject extractedPersonalData = new JSONObject();

            ResponseCustomerData rcd = response.getResult().getResponseCustomerData();

            ExtractedIdData eid = rcd.getExtractedIdData();
            extractedIdData.put("BarcodeDataParsed",eid.getBarcodeDataParsed());
            extractedIdData.put("IdCountry",eid.getIdCountry());
            extractedIdData.put("IdDateOfBirth",eid.getIdDateOfBirth());
            extractedIdData.put("IdDateOfBirthFormatted",eid.getIdDateOfBirthFormatted());
            extractedIdData.put("IdDateOfBirthNonEng",eid.getIdDateOfBirthNonEng());
            extractedIdData.put("IdExpirationDate",eid.getIdExpirationDate());
            extractedIdData.put("IdExpirationDateFormatted",eid.getIdExpirationDateFormatted());
            extractedIdData.put("IdExpirationDateNonEng",eid.getIdExpirationDateNonEng());
            extractedIdData.put("IdIssueCountry",eid.getIdIssueCountry());
            extractedIdData.put("IdIssueDate",eid.getIdIssueDate());
            extractedIdData.put("IdIssueDateNonEng",eid.getIdIssueDateNonEng());
            extractedIdData.put("IdNumber",eid.getIdNumber());
            extractedIdData.put("IdNumberNonEng",eid.getIdNumberNonEng());
            extractedIdData.put("IdNumber1",eid.getIdNumber1());
            extractedIdData.put("IdNumber2",eid.getIdNumber2());
            extractedIdData.put("IdNumber2NonEng",eid.getIdNumber2NonEng());
            extractedIdData.put("IdNumber3",eid.getIdNumber3());
            extractedIdData.put("IdState",eid.getIdState());
            extractedIdData.put("IdType",eid.getIdType());
            extractedIdData.put("MrzData",eid.getMrzData());

            responseCustomerData.put("ExtractedIdData",extractedIdData);

            ExtractedPersonalData epd = rcd.getExtractedPersonalData();
            extractedPersonalData.put("AddressLine1",epd.getAddressLine1());
            extractedPersonalData.put("AddressLine1NonEng",epd.getAddressLine1NonEng());
            extractedPersonalData.put("AddressLine2",epd.getAddressLine2());
            extractedPersonalData.put("AddressLine2NonEng",epd.getAddressLine2NonEng());
            extractedPersonalData.put("City",epd.getCity());
            extractedPersonalData.put("AddressNonEng",epd.getAddressNonEng());
            extractedPersonalData.put("Country",epd.getCountry());
            extractedPersonalData.put("District",epd.getDistrict());
            extractedPersonalData.put("Dob",epd.getDob());
            extractedPersonalData.put("Email",epd.getEmail());
            extractedPersonalData.put("EnrolledDate",epd.getEnrolledDate());
            extractedPersonalData.put("FirstName",epd.getFirstName());
            extractedPersonalData.put("FirstNameNonEng",epd.getFirstNameNonEng());
            extractedPersonalData.put("Gender",epd.getGender());
            extractedPersonalData.put("LastName",epd.getLastName());
            extractedPersonalData.put("LastName2",epd.getLastName2());
            extractedPersonalData.put("LastNameNonEng",epd.getLastNameNonEng());
            extractedPersonalData.put("Name",epd.getName());
            extractedPersonalData.put("Phone",epd.getPhone());
            extractedPersonalData.put("UniqueNumber",epd.getUniqueNumber());
            extractedPersonalData.put("MiddleName",epd.getMiddleName());
            extractedPersonalData.put("MiddleNameNonEng",epd.getMiddleNameNonEng());

            responseCustomerData.put("ExtractedPersonalData",extractedPersonalData);

            HostDataResponse hdr = rcd.getHostData();
            responseCustomerData.put("HostDataResponse",hdr);

            jo.put("ResponseCustomerData", responseCustomerData);
        }catch(Exception e){}
        try {
            JSONObject responseCustomerVerifyData = new JSONObject();
            JSONObject extractedIdData = new JSONObject();
            JSONObject extractedPersonalData = new JSONObject();

            ResponseCustomerData rcvd = response.getResult().getResponseCustomerVerifyData();

            ExtractedIdData eid = rcvd.getExtractedIdData();
            extractedIdData.put("BarcodeDataParsed",eid.getBarcodeDataParsed());
            extractedIdData.put("IdCountry",eid.getIdCountry());
            extractedIdData.put("IdDateOfBirth",eid.getIdDateOfBirth());
            extractedIdData.put("IdDateOfBirthFormatted",eid.getIdDateOfBirthFormatted());
            extractedIdData.put("IdDateOfBirthNonEng",eid.getIdDateOfBirthNonEng());
            extractedIdData.put("IdExpirationDate",eid.getIdExpirationDate());
            extractedIdData.put("IdExpirationDateFormatted",eid.getIdExpirationDateFormatted());
            extractedIdData.put("IdExpirationDateNonEng",eid.getIdExpirationDateNonEng());
            extractedIdData.put("IdIssueCountry",eid.getIdIssueCountry());
            extractedIdData.put("IdIssueDate",eid.getIdIssueDate());
            extractedIdData.put("IdIssueDateNonEng",eid.getIdIssueDateNonEng());
            extractedIdData.put("IdNumber",eid.getIdNumber());
            extractedIdData.put("IdNumberNonEng",eid.getIdNumberNonEng());
            extractedIdData.put("IdNumber1",eid.getIdNumber1());
            extractedIdData.put("IdNumber2",eid.getIdNumber2());
            extractedIdData.put("IdNumber2NonEng",eid.getIdNumber2NonEng());
            extractedIdData.put("IdNumber3",eid.getIdNumber3());
            extractedIdData.put("IdState",eid.getIdState());
            extractedIdData.put("IdType",eid.getIdType());
            extractedIdData.put("MrzData",eid.getMrzData());

            responseCustomerVerifyData.put("ExtractedIdData",extractedIdData);

            ExtractedPersonalData epd = rcvd.getExtractedPersonalData();
            extractedPersonalData.put("AddressLine1",epd.getAddressLine1());
            extractedPersonalData.put("AddressLine1NonEng",epd.getAddressLine1NonEng());
            extractedPersonalData.put("AddressLine2",epd.getAddressLine2());
            extractedPersonalData.put("AddressLine2NonEng",epd.getAddressLine2NonEng());
            extractedPersonalData.put("City",epd.getCity());
            extractedPersonalData.put("AddressNonEng",epd.getAddressNonEng());
            extractedPersonalData.put("Country",epd.getCountry());
            extractedPersonalData.put("District",epd.getDistrict());
            extractedPersonalData.put("Dob",epd.getDob());
            extractedPersonalData.put("Email",epd.getEmail());
            extractedPersonalData.put("EnrolledDate",epd.getEnrolledDate());
            extractedPersonalData.put("FirstName",epd.getFirstName());
            extractedPersonalData.put("FirstNameNonEng",epd.getFirstNameNonEng());
            extractedPersonalData.put("Gender",epd.getGender());
            extractedPersonalData.put("LastName",epd.getLastName());
            extractedPersonalData.put("LastName2",epd.getLastName2());
            extractedPersonalData.put("LastNameNonEng",epd.getLastNameNonEng());
            extractedPersonalData.put("Name",epd.getName());
            extractedPersonalData.put("Phone",epd.getPhone());
            extractedPersonalData.put("UniqueNumber",epd.getUniqueNumber());
            extractedPersonalData.put("MiddleName",epd.getMiddleName());
            extractedPersonalData.put("MiddleNameNonEng",epd.getMiddleNameNonEng());

            responseCustomerVerifyData.put("ExtractedPersonalData",extractedPersonalData);

            HostDataResponse hdr = rcvd.getHostData();
            responseCustomerVerifyData.put("HostDataResponse",hdr);

            jo.put("ResponseCustomerVerifyData", responseCustomerVerifyData);
        }catch(Exception e){}
        try {
            jo.put("ResultData", response.getResult().getResultData());
        }catch(Exception e){}
        try {
            jo.put("Status", response.getResult().getStatus());
        }catch(Exception e){}

        return jo.toString();
    }
}
