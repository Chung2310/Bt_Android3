package vn.edu.chungxangla.bt_android3.Activity;

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
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.NotificationCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import vn.edu.chungxangla.bt_android3.Adapter.MessageAdapter;
import vn.edu.chungxangla.bt_android3.Api.Api;
import vn.edu.chungxangla.bt_android3.Api.RetrofitClient;
import vn.edu.chungxangla.bt_android3.Model.LastIDModel;
import vn.edu.chungxangla.bt_android3.Model.Message;
import vn.edu.chungxangla.bt_android3.Model.MessageModel;
import vn.edu.chungxangla.bt_android3.Model.MessagesModel;
import vn.edu.chungxangla.bt_android3.R;

public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    TextView id_main,title_main,body_main,time_main;
    AppCompatButton button;
    Api api;
    List<Message> messageList = new ArrayList<>();
    MessageAdapter adapter;
    CompositeDisposable compositeDisposable;
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
        api = RetrofitClient.getInstance("https://57kmt.duckdns.org/android/").create(Api.class);
        anhXa();
        listAll();
        //lastId();
        SharedPreferences sharedPreferences = getSharedPreferences("mypref",MODE_PRIVATE);
        last_ID = sharedPreferences.getInt("lastid",0);
        Handler handler = new Handler(Looper.getMainLooper());

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listAll();
            }
        });

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
        button = findViewById(R.id.button);
        compositeDisposable = new CompositeDisposable();
   }
    private void listAll() {
        compositeDisposable.add(api.listAll("list_all")
                .subscribeOn(Schedulers.io()) // Xử lý trên luồng nền
                .observeOn(AndroidSchedulers.mainThread()) // Kết quả trên luồng chính
                .subscribe(
                        messagesModel -> {
                            // Xử lý thành công
                            if (adapter == null) {
                                messageList = messagesModel.getData();
                                adapter = new MessageAdapter(this, messageList);
                                recyclerView.setAdapter(adapter);
                            } else {
                                adapter.notifyDataSetChanged();
                            }
                        },
                        throwable -> {
                            // Xử lý lỗi
                            Log.d("loi",throwable.getMessage()+"");
                            Toast.makeText(MainActivity.this, "Error: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                ));

    }

    private void lastId() {
        compositeDisposable.add(api.lastId("last_id")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        lastIDModel -> {
                            if (last_ID < lastIDModel.getLast_id()) {
                                last_ID = lastIDModel.getLast_id();
                                getBody(last_ID);
                                SharedPreferences sharedPreferences = getSharedPreferences("mypref", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putInt("lastid", lastIDModel.getLast_id());
                                editor.apply();
                            }
                        },
                        throwable -> {
                            Toast.makeText(MainActivity.this, "Error: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                ));
    }

    private void getBody(int id) {
        compositeDisposable.add(api.getBody(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        message -> {
                            id_main.setText(message.getId());
                            title_main.setText(message.getTitle());
                            body_main.setText(message.getBody());
                            time_main.setText(message.getTime());
                            showNotification(message.getTitle(), message.getBody());
                        },
                        throwable -> {
                            Toast.makeText(MainActivity.this, "Error: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                ));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (compositeDisposable != null && !compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
        }
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