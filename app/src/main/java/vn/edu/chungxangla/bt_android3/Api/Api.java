package vn.edu.chungxangla.bt_android3.Api;

import io.reactivex.rxjava3.core.Observable;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import vn.edu.chungxangla.bt_android3.Model.LastIDModel;
import vn.edu.chungxangla.bt_android3.Model.Message;

public interface Api {
    @GET("android/api.aspx")
    Call<LastIDModel> getLastIdData(@Query("action") String action);

    @GET("android/api.aspx")
    Observable<Message> getBody(@Query("id") int id);

}
