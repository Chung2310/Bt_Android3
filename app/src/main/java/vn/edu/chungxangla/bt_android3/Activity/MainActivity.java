package vn.edu.chungxangla.bt_android3.Activity;

import static vn.edu.chungxangla.bt_android3.utils.Utils.BASE_URL;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import vn.edu.chungxangla.bt_android3.Adapter.MessageAdapter;
import vn.edu.chungxangla.bt_android3.Api.Api;
import vn.edu.chungxangla.bt_android3.Model.LastIDModel;
import vn.edu.chungxangla.bt_android3.Model.Message;
import vn.edu.chungxangla.bt_android3.Model.MessageModel;
import vn.edu.chungxangla.bt_android3.Model.MessagesModel;
import vn.edu.chungxangla.bt_android3.R;
import vn.edu.chungxangla.bt_android3.utils.Utils;

public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    TextView id_main,title_main,body_main,time_main;
    Api api;
    List<Message> messageList = new ArrayList<>();
    MessageAdapter adapter;
    int last_ID;
    private static final String CHANNEL_ID = "my_channel_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        anhXa();
        listAll();
        SharedPreferences sharedPreferences = getSharedPreferences("mypref",MODE_PRIVATE);
        last_ID = sharedPreferences.getInt("lastid",0);
        Handler handler = new Handler(Looper.getMainLooper());

        Runnable periodicTask = new Runnable() {
            @Override
            public void run() {
                listAll();
                lastId();
                handler.postDelayed(this, 30000); // Lặp lại sau 30 giây
            }
        };
        handler.post(periodicTask);

    }
   private void anhXa() {
        recyclerView = findViewById(R.id.recyclerView);
        id_main = findViewById(R.id.id_main);
        title_main = findViewById(R.id.title_main);
        body_main = findViewById(R.id.body_main);
        time_main = findViewById(R.id.time_main);

   }
    private void listAll(){
        // Tạo Retrofit instance
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL) // URL chính xác của API
                .addConverterFactory(GsonConverterFactory.create()) // Dùng Gson để parse JSON
                .build();
        Api apiService = retrofit.create(Api.class);
        Call<MessagesModel> call = apiService.listAll("list_all");

        call.enqueue(new Callback<MessagesModel>() {
            @Override
            public void onResponse(Call<MessagesModel> call, Response<MessagesModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    MessagesModel tt = response.body();
                    if (tt.getOk() == 1) {
                        messageList = tt.getData();
                        adapter = new MessageAdapter(getApplicationContext(), messageList);
                        recyclerView.setAdapter(adapter);

                    }
                } else {
                    System.err.println("Response is empty or unsuccessful");
                }
            }
            @Override
            public void onFailure(Call<MessagesModel> call, Throwable t) {
                System.err.println("Error: " + t.getMessage());
            }
        });
    }
    private void lastId(){
        // Tạo Retrofit instance
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL) // URL chính xác của API
                .addConverterFactory(GsonConverterFactory.create()) // Dùng Gson để parse JSON
                .build();

        Api apiService = retrofit.create(Api.class);
        Call<LastIDModel> call = apiService.lastId("last_id");

        call.enqueue(new Callback<LastIDModel>() {
            @Override
            public void onResponse(Call<LastIDModel> call, Response<LastIDModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LastIDModel tt = response.body();
                    if (tt.getOk() == 1) {
                        if(last_ID < tt.getLast_id()){
                            last_ID = tt.getLast_id();
                            getBody(last_ID);
                            SharedPreferences sharedPreferences = getSharedPreferences("mypref",MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putInt("lastid",tt.getLast_id());
                            editor.commit();
                        }
                        else{
                            return;
                        }

                    }
                }

            }
            @Override
            public void onFailure(Call<LastIDModel> call, Throwable t) {
                System.err.println("Error: " + t.getMessage());
            }
        });
    }
    private void getBody(int id) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Api apiService = retrofit.create(Api.class);
        Call<Message> call = apiService.getBody("get_id",id);

        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, Response<Message> response) {
                if (response.isSuccessful() && response.body() != null) {

                    Message newBody = response.body();
                    id_main.setText(newBody.getId());
                    title_main.setText(newBody.getTitle());
                    body_main.setText(newBody.getBody());
                    time_main.setText(newBody.getTime());
                    showNotification(newBody.getTitle(), newBody.getBody());
                }
            }

            @Override
            public void onFailure(Call<Message> call, Throwable t) {
                System.err.println("Error: " + t.getMessage());
            }
        });
    }

    private void showNotification(String title, String body) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Tên kênh thông báo",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("Mô tả kênh thông báo");
            notificationManager.createNotificationChannel(channel);
        }

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle(title)
                .setContentText(body)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        notificationManager.notify(1, builder.build());
    }

}