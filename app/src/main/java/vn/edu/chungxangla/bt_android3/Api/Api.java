package vn.edu.chungxangla.bt_android3.Api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import vn.edu.chungxangla.bt_android3.Model.LastIDModel;

public interface Api {
    @GET("android/api.aspx")
    Call<LastIDModel> getLastIdData(@Query("action") String action);


}
