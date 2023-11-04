package com.yuhan.yangpojang;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yuhan.yangpojang.login.LoginActivity;
import com.yuhan.yangpojang.login.LogindetailAct;
import com.yuhan.yangpojang.login.User;

public class SplashImage extends AppCompatActivity {

    private static final int SPLASH_TIMEOUT = 2000; // 스플래시 화면을 보여줄 시간 (밀리초)
    private DatabaseReference mDatabase;
    private String user_info_uid = null;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            user_info_uid = user.getUid();
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // 스플래시 화면이 표시된 시간이 SPLASH_TIMEOUT 이후에 실행할 액티비티로 이동
                mAuth = FirebaseAuth.getInstance();
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if (currentUser != null) {
                    mDatabase.child("user-info").child(user_info_uid).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            User user_check = snapshot.getValue(User.class);

                            if(user_check == null)
                            {
                                Intent intent_main = new Intent(getApplication(), LogindetailAct.class);
                                startActivity(intent_main);
                                finish();
                            }
                            else
                            {
                                Intent intent_main = new Intent(getApplication(), MainActivity.class);
                                startActivity(intent_main);
                                finish();
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                else
                {
                    Intent intent_main = new Intent(getApplication(), LoginActivity.class);
                    startActivity(intent_main);
                    finish();
                }

            }
        }, SPLASH_TIMEOUT);
    }
}