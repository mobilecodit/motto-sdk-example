package kr.motto.mottoexample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import kr.motto.mottoexample.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity
{
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnLaunch.setOnClickListener(v -> {
            String uid = binding.etUid.getText().toString();
            if(uid.isEmpty())
            {
                Toast.makeText(this, "식별자 아이디를 입력하세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(MainActivity.this, MottoExampleActivity.class);
            intent.putExtra("uid", uid);
            startActivity(intent);
        });
    }
}