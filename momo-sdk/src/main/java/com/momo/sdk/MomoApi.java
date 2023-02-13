package com.momo.sdk;

import android.annotation.SuppressLint;

import com.google.gson.Gson;
import com.momo.sdk.callbacks.APIRequestCallback;
import com.momo.sdk.config.UserConfiguration;
import com.momo.sdk.model.AccountBalance;
import com.momo.sdk.model.DeliveryNotification;
import com.momo.sdk.model.collection.AccountIdentifier;
import com.momo.sdk.model.collection.RequestPay;
import com.momo.sdk.model.collection.RequestPayStatus;
import com.momo.sdk.model.collection.Result;
import com.momo.sdk.model.collection.Withdraw;
import com.momo.sdk.model.collection.WithdrawStatus;
import com.momo.sdk.model.user.ApiKey;
import com.momo.sdk.model.user.ApiUser;
import com.momo.sdk.model.StatusResponse;
import com.momo.sdk.model.user.BasicUserInfo;
import com.momo.sdk.model.user.CallBackHost;
import com.momo.sdk.network.APIService;
import com.momo.sdk.network.RetrofitHelper;
import com.momo.sdk.util.APIConstants;
import com.momo.sdk.util.AppConstants;
import com.momo.sdk.util.SubscriptionType;
import com.momo.sdk.util.Utils;

import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.RequestBody;


public class MomoApi {
    private final APIService apiHelper;
    private final RequestManager requestManager;
    private final HashMap<String, String> headers;
    private final MediaType mediaType = MediaType.parse("application/json");

    private MomoApi() {
        apiHelper = RetrofitHelper.getApiHelper();
        requestManager = new RequestManager();
        headers = new HashMap<>();

    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static MomoApi getInstance() {

        return SingletonCreationAdmin.INSTANCE;

    }

    /**************************************Authentication*********************************************?

    /* Create token user
     *
     * @param callBackRequest    The request model containing callback url
     * @param apiRequestCallback Listener for api operation
     */
    public void createUser(CallBackHost callBackRequest, APIRequestCallback<StatusResponse> apiRequestCallback) {
        headers.clear();
        String uuid = Utils.generateUUID();
        AppConstants.CURRENT_X_REFERENCE_ID = uuid;
        headers.put(APIConstants.X_REFERENCE_ID, uuid);
        headers.put(APIConstants.OCP_APIM_SUBSCRIPTION_KEY, UserConfiguration.UserConfigurationBuilder.getSubscriptionKey());
        requestManager.request(new RequestManager.DelayedRequest<>(apiHelper.createUser(headers, RequestBody.create(new Gson().toJson(callBackRequest), mediaType)), apiRequestCallback));

    }

    /** get User details
     *
     * @param xReferenceId       Reference id of created user
     * @param apiRequestCallback Listener for api operation
     */

    public void getUserDetails(String xReferenceId, APIRequestCallback<ApiUser> apiRequestCallback) {
        headers.clear();
        AppConstants.CURRENT_X_REFERENCE_ID = xReferenceId;
        headers.put(APIConstants.OCP_APIM_SUBSCRIPTION_KEY, UserConfiguration.UserConfigurationBuilder.getSubscriptionKey());
        requestManager.request(new RequestManager.DelayedRequest<>(apiHelper.getUserDetails(xReferenceId, headers), apiRequestCallback));
    }


    /** get User details
     *
     * @param xReferenceId       Reference id of created user
     * @param apiRequestCallback Listener for api operation
     */
    public void getApiKey(String xReferenceId, APIRequestCallback<ApiKey> apiRequestCallback) {
        headers.clear();
        AppConstants.CURRENT_X_REFERENCE_ID = xReferenceId;
        headers.put(APIConstants.OCP_APIM_SUBSCRIPTION_KEY, UserConfiguration.UserConfigurationBuilder.getSubscriptionKey());
        requestManager.request(new RequestManager.DelayedRequest<>(apiHelper.createApiKey(xReferenceId, headers), apiRequestCallback));

    }

    /**************************************Common*********************************************?

     /** Account balance
     *
     * @param subscriptionType The SubscriptionType object
     * @param  getBalanceAPIRequestCallback Listener for api operation
     */

    public void getAccountBalance(SubscriptionType subscriptionType,
                                  APIRequestCallback<AccountBalance> getBalanceAPIRequestCallback){
        HashMap<String,String> headers;
        headers=Utils.getHeaders("",subscriptionType,"",false);
        requestManager.request(new RequestManager.DelayedRequest<>(apiHelper.getBalance(
                subscriptionType.name().toLowerCase(),headers),
                getBalanceAPIRequestCallback));

    }


    /** Request to validate Account holder Status
     *
     * @param accountIdentifier Account identifier
     * @param subscriptionType The SubscriptionType object
     * @param requestPayAPIRequestCallback Listener
     */

    public void validateAccountHolderStatus(AccountIdentifier accountIdentifier,
                                            SubscriptionType subscriptionType,
                                            APIRequestCallback<Result> requestPayAPIRequestCallback
    ){
        HashMap<String ,String > headers;
        headers=Utils.getHeaders("",subscriptionType,"",false);

        requestManager.request(new RequestManager.DelayedRequest<>(apiHelper.validateAccountHolderStatus(subscriptionType.name().toLowerCase(),accountIdentifier.getAccountHolderIdType(),
                accountIdentifier.getAccountHolderId(),headers),requestPayAPIRequestCallback));


    }

    /** Request to get Basic User Info
     *
     * @param accountMsisdn MSISDN string
     * @param subscriptionType SubscriptionType object
     * @param accountMsisdn Msisdn of account
     * @param requestPayAPIRequestCallback Listener
     */

    public void getBasicUserInfo(String accountMsisdn,SubscriptionType subscriptionType,
                                 APIRequestCallback<BasicUserInfo> requestPayAPIRequestCallback){


        HashMap<String,String> headers;
        headers=Utils.getHeaders("",subscriptionType,"",false);

        requestManager.request(new RequestManager.DelayedRequest<>(apiHelper.basicUserInfo(subscriptionType.name().toLowerCase(),
                accountMsisdn,headers),requestPayAPIRequestCallback));
    }

    /** Request to pay delivery Notification
     *
     * @param referenceId Reference Id of user
     * @param language language String
     * @param notificationMessage Notification message string
     * @param subscriptionType SubscriptionType object
     * @param deliveryNotification DeliveryNotification object
     * @param requestPayAPIRequestCallback Listener
     */

    public void requestPayDeliveryNotification(String referenceId,String notificationMessage,
                                               String language,
                                               SubscriptionType subscriptionType, DeliveryNotification deliveryNotification,
                                               APIRequestCallback<StatusResponse> requestPayAPIRequestCallback){


        HashMap<String,String> headers;
        headers=Utils.getHeaders("",subscriptionType,"",false);
        headers.put(APIConstants.NOTIFICATION_MESSAGE,notificationMessage);
        headers.put(APIConstants.LANGUAGE,language);

        requestManager.request(new RequestManager.DelayedRequest<>(apiHelper.requestPayDeliveryNotification
                (subscriptionType.name().toLowerCase(), referenceId,headers,
                        RequestBody.create(new Gson().toJson(deliveryNotification), mediaType)
                ),requestPayAPIRequestCallback));
    }


    /**************************************Collection*********************************************?

     /** Request pay
     *
     * @param requestPay The payment object
     * @param callBakUrl server url for callback
     * @param apiRequestCallback Listener for api operation
     */

    public void requestPay(RequestPay requestPay, String callBakUrl, APIRequestCallback<StatusResponse> apiRequestCallback) {
        headers.clear();
        HashMap<String ,String > headers;
        headers=Utils.getHeaders(Utils.generateUUID(),SubscriptionType.COLLECTION,Utils.setCallbackUrl(callBakUrl,SubscriptionType.COLLECTION),true);
        requestManager.request(new RequestManager.DelayedRequest<>(apiHelper.requestToPay(headers, RequestBody.create(new Gson().toJson(requestPay), mediaType)), apiRequestCallback));

    }

    /** Request transaction status
     *
     * @param referenceId Reference id
     * @param requestPayAPIRequestCallback Listener for api operation
     */

    public void requestToPayTransactionStatus(String referenceId, APIRequestCallback<RequestPayStatus> requestPayAPIRequestCallback){
        headers.clear();
        HashMap<String ,String > headers;
        headers=Utils.getHeaders(referenceId,SubscriptionType.COLLECTION,"",false);
        requestManager.request(new RequestManager.DelayedRequest<>(apiHelper.requestPayStatus(referenceId,headers),
                requestPayAPIRequestCallback));

    }


    /** Request withdraw V1
     *
     * @param withdraw The Withdraw object
     * @param callBakUrl server url for callback
     * @param apiRequestCallback Listener for api operation
     */

    public void requestToWithdrawV1(Withdraw withdraw, String callBakUrl,
                                    APIRequestCallback<StatusResponse> apiRequestCallback){
        HashMap<String,String> headers;
        headers=Utils.getHeaders(Utils.generateUUID(),SubscriptionType.COLLECTION,callBakUrl,true);
        requestManager.request(new RequestManager.DelayedRequest<>(apiHelper.requestToWithdrawV1(headers, RequestBody.create(new Gson().toJson(withdraw), mediaType)),apiRequestCallback ));

    }

    /** Request withdraw V2
     *
     * @param withdraw The payment object
     * @param callBakUrl server url for callback
     * @param apiRequestCallback Listener for api operation
     */

    public void requestToWithdrawV2(Withdraw withdraw, String callBakUrl, APIRequestCallback<StatusResponse> apiRequestCallback){
        HashMap<String,String> headers;
        headers=Utils.getHeaders(Utils.generateUUID(),SubscriptionType.COLLECTION,callBakUrl,true);
        requestManager.request(new RequestManager.DelayedRequest<>(apiHelper.requestToWithdrawV2(headers, RequestBody.create(new Gson().toJson(withdraw), mediaType)),apiRequestCallback ));
    }



    /** Request withdraw transaction status
     *
     * @param referenceId Reference id
     * @param requestPayAPIRequestCallback Listener for api operation
     */

    public void requestToWithdrawTransactionStatus(String referenceId,APIRequestCallback<WithdrawStatus> requestPayAPIRequestCallback){
        HashMap<String,String> headers;
        headers=Utils.getHeaders(referenceId,SubscriptionType.COLLECTION,"",false);
        requestManager.request(new RequestManager.DelayedRequest<>(apiHelper.requestToWithdrawTransactionStatus(referenceId,headers),
                requestPayAPIRequestCallback));

    }

    private static class SingletonCreationAdmin {
        @SuppressLint("StaticFieldLeak")
        private static final MomoApi INSTANCE = new MomoApi();
    }


}