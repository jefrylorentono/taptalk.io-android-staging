package com.moselo.HomingPigeon.API.Api;

import com.moselo.HomingPigeon.API.BaseResponse;
import com.moselo.HomingPigeon.Model.RequestModel.CommonRequest;
import com.moselo.HomingPigeon.Model.ResponseModel.AuthTicketResponse;
import com.moselo.HomingPigeon.Model.ResponseModel.GetAccessTokenResponse;
import com.moselo.HomingPigeon.Model.RequestModel.AuthTicketRequest;
import com.moselo.HomingPigeon.Model.ResponseModel.GetRoomListResponse;

import retrofit2.http.Body;
import retrofit2.http.POST;
import rx.Observable;

public interface HomingPigeonApiService {
    // TODO: 13/09/18 Change base URL
    String BASE_URL = "https://hp-staging.moselo.com:8080/api/v1/";

    @POST("server/request_auth_ticket")
    Observable<BaseResponse<AuthTicketResponse>> getAuthTicket(@Body AuthTicketRequest request);

    @POST("client/request_access_token")
    Observable<BaseResponse<GetAccessTokenResponse>> getAccessToken();

    @POST("client/refresh_access_token")
    Observable<BaseResponse<GetAccessTokenResponse>> refreshAccessToken();

    @POST("chat/room/list")
    Observable<BaseResponse<GetRoomListResponse>> getRoomList(@Body CommonRequest request);
}