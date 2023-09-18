package kr.motto.mottoexample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import kr.motto.mottoexample.databinding.ActivityMottoExampleBinding;
import kr.motto.mottolib.Motto;
import kr.motto.mottolib.MottoFragment;

public class MottoExampleActivity extends AppCompatActivity
{
    private ActivityMottoExampleBinding binding;
    private MottoFragment mottoFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        binding = ActivityMottoExampleBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        String uid = intent.getStringExtra("uid");

        Motto.setUid(uid);   // 유저식별 값(아이디 혹은 유저를 판별할 수 있는 유니크한 값)
        mottoFragment = Motto.create(this);
        getSupportFragmentManager().
                beginTransaction().
                replace(R.id.motto_frame, mottoFragment)
                .commit();
    }

    @Override
    public void onBackPressed()
    {
        if(mottoFragment.goBack())
            return;

        finish();
    }
}