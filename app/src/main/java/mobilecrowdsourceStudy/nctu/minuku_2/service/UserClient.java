package mobilecrowdsourceStudy.nctu.minuku_2.service;

import labelingStudy.nctu.minukucore.user.User;
import mobilecrowdsourceStudy.nctu.minuku_2.controller.ResponseResult;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by chiaenchiang on 28/11/2018.
 */

public interface UserClient {
    @POST("User")
    Call<User> UserInfo(@Body User user);

    @Multipart
    @POST("upload")
     Call<ResponseResult> uploadVideo(
             @Part("description") RequestBody description,
                   @Part MultipartBody.Part video);
}
