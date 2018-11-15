package io.taptalk.TapTalk.API.Service;

import io.taptalk.TapTalk.Model.RequestModel.TAPAuthTicketRequest;
import io.taptalk.TapTalk.Model.RequestModel.TAPCommonRequest;
import io.taptalk.TapTalk.Model.RequestModel.TAPGetMessageListbyRoomAfterRequest;
import io.taptalk.TapTalk.Model.RequestModel.TAPGetMessageListbyRoomBeforeRequest;
import io.taptalk.TapTalk.Model.RequestModel.TAPGetUserByIdRequest;
import io.taptalk.TapTalk.Model.RequestModel.TAPGetUserByUsernameRequest;
import io.taptalk.TapTalk.Model.RequestModel.TAPGetUserByXcUserIdRequest;
import io.taptalk.TapTalk.Model.RequestModel.TAPPushNotificationRequest;
import io.taptalk.TapTalk.Model.RequestModel.TAPUserIdRequest;
import io.taptalk.TapTalk.Model.ResponseModel.TAPAuthTicketResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TAPBaseResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TAPCommonResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TAPContactResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TAPGetAccessTokenResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TAPGetMessageListbyRoomResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TAPGetRoomListResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TAPGetUserResponse;
import io.taptalk.Taptalk.BuildConfig;
import retrofit2.http.Body;
import retrofit2.http.POST;
import rx.Observable;

public interface TAPTalkApiService {
    String BASE_URL = BuildConfig.BASE_URL_API;
    //String BASE_URL = "dev.taptalk.io:8080/api/v1/";

    @POST("server/auth_ticket/request")
    Observable<TAPBaseResponse<TAPAuthTicketResponse>> getAuthTicket(@Body TAPAuthTicketRequest request);

    @POST("auth/access_token/request")
    Observable<TAPBaseResponse<TAPGetAccessTokenResponse>> getAccessToken();

    @POST("chat/message/room_list_and_unread")
    Observable<TAPBaseResponse<TAPGetRoomListResponse>> getRoomList(@Body TAPCommonRequest request);

    @POST("chat/message/new_and_updated")
    Observable<TAPBaseResponse<TAPGetRoomListResponse>> getPendingAndUpdatedMessage();

    @POST("chat/message/list_by_room/before")
    Observable<TAPBaseResponse<TAPGetMessageListbyRoomResponse>> getMessageListByRoomBefore(@Body TAPGetMessageListbyRoomBeforeRequest request);

    @POST("client/contact/list")
    Observable<TAPBaseResponse<TAPContactResponse>> getMyContactListFromAPI();

    @POST("client/push_notification/update")
    Observable<TAPBaseResponse<TAPCommonResponse>> registerFcmTokenToServer(@Body TAPPushNotificationRequest request);

    @POST("chat/message/list_by_room/after")
    Observable<TAPBaseResponse<TAPGetMessageListbyRoomResponse>> getMessageListByRoomAfter(@Body TAPGetMessageListbyRoomAfterRequest request);

    @POST("client/contact/add")
    Observable<TAPBaseResponse<TAPCommonResponse>> addContact(@Body TAPUserIdRequest request);

    @POST("client/contact/remove")
    Observable<TAPBaseResponse<TAPCommonResponse>> removeContact(@Body TAPUserIdRequest request);

    @POST("client/user/get_by_id")
    Observable<TAPBaseResponse<TAPGetUserResponse>> getUserByID(@Body TAPGetUserByIdRequest request);

    @POST("client/user/get_by_xcuserid")
    Observable<TAPBaseResponse<TAPGetUserResponse>> getUserByXcUserID(@Body TAPGetUserByXcUserIdRequest request);

    @POST("client/user/get_by_username")
    Observable<TAPBaseResponse<TAPGetUserResponse>> getUserByUsername(@Body TAPGetUserByUsernameRequest request);
}