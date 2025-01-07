package vn.edu.chungxangla.bt_android3.Api;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import vn.edu.chungxangla.bt_android3.Model.LastIDModel;
import vn.edu.chungxangla.bt_android3.Model.Message;
import vn.edu.chungxangla.bt_android3.Model.MessageModel;
import vn.edu.chungxangla.bt_android3.Model.MessagesModel;

public interface Api {
    @GET("api.aspx?action=list_all")
    Call<MessagesModel> listAll();
    @GET("api.aspx?action=list_id")
    Call<LastIDModel> lastId();
    @GET("api.aspx?action=get_id&id={id}")
    Call<Message> getBody(@Path("id") int id);

}
