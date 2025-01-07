package vn.edu.chungxangla.bt_android3.Api;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import vn.edu.chungxangla.bt_android3.Model.LastIDModel;
import vn.edu.chungxangla.bt_android3.Model.Message;
import vn.edu.chungxangla.bt_android3.Model.MessageModel;
import vn.edu.chungxangla.bt_android3.Model.MessagesModel;

public interface Api {
    @POST("android/api.aspx")
    Call<MessagesModel> listAll(@Query("action") String action);
    @GET("android/api.aspx")
    Call<LastIDModel> lastId(@Query("action") String action);
    @GET("android/api.aspx")
    Call<Message> getBody(@Query("action") String action, @Query("id") int id);

}
