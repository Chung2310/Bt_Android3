package vn.edu.chungxangla.bt_android3.Api;

import java.util.List;
import io.reactivex.rxjava3.core.Observable;

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
    @GET("android/api.aspx?action=list_all")
    Observable<MessagesModel> listAll(@Query("action") String action);

    @GET("last_id")
    Observable<LastIDModel> lastId(@Query("action") String action);

    @GET("get_body/{id}")
    Observable<Message> getBody(@Path("id") int id);

}
