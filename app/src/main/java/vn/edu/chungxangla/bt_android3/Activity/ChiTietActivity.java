package vn.edu.chungxangla.bt_android3.Activity;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import vn.edu.chungxangla.bt_android3.R;

public class ChiTietActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chi_tiet);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        TextView tvId = findViewById(R.id.tv_id_detail);
        TextView tvTime = findViewById(R.id.tv_time_detail);
        TextView tvTomtat = findViewById(R.id.tv_tomtat_detail);
        TextView tvFullbody = findViewById(R.id.tv_fullbody_detail);

        // Get data from intent
        int id = getIntent().getIntExtra("id",0);
        String time = getIntent().getStringExtra("time");
        String tomtat = getIntent().getStringExtra("tomtat");
        String fullbody = getIntent().getStringExtra("fullbody");

        // Set data
        tvId.setText("ID: " + id);
        tvTime.setText("User ID: " + time);
        tvTomtat.setText("Tóm tắt: " + tomtat);
        tvFullbody.setText("Nội dung đầy đủ: " + fullbody);
    }
}