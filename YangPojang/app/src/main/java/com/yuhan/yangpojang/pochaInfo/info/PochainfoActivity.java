package com.yuhan.yangpojang.pochaInfo.info;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.yuhan.yangpojang.R;
import com.yuhan.yangpojang.model.Shop;
import com.yuhan.yangpojang.model.Store;
import com.yuhan.yangpojang.pochaInfo.meeting.PochameetingFragment;
import com.yuhan.yangpojang.pochaInfo.review.PochareviewFragment;

// pch: pojangmacha
// frg: fragment
// tv: TextView
public class PochainfoActivity extends AppCompatActivity {
    PochadetailFragment pchDetailFrg;      // 포차 상세정보 Fragment
    PochareviewFragment pchReviewFrg;      // 포차 리뷰 Fragment
    PochameetingFragment pchMeetingFrg;        // 포차 번개 Fragment
    Button pchDetailBtn;                   // 포차 상세정보 Button
    Button pchReviewBtn;                   // 포차 리뷰 Button
    Button pchMeetingBtn;                   // 포차 번개 Button
    FragmentManager frgManager;       // Fragment 관리자
    FragmentTransaction frgTransaction;        // Fragment 트랜잭션 : Fragment 작업을 처리
    Bundle bundle;              // Fragment에 포차 이름, 회원ID를 담아 전달할 객체
    private Shop shop;          // 포차 정보를 담을 객체
//    FirebaseDatabase ref = FirebaseDatabase.getInstance();
//    DatabaseReference shops = ref.getReference("shops");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pochainfo);

        String pchName = null;     // 포차 이름(값) 생성 및 초기화
        TextView pchNameTv = findViewById(R.id.tv_pochainfo_pochaname);     // 포차 이름(위젯)

        // ▼ HomeFragment에서 전달 받은 포차 객체 받아 처리
        Intent intent = getIntent();
        if(intent != null){
            shop = (Shop) intent.getSerializableExtra("shopInfo");  // 직렬화된 객체 수신
            pchName = shop.getShopName();
            // 포차 이름 변경
            pchNameTv.setText(pchName);
        }
        if(pchName == null){
            Toast.makeText(this, "해당 가게를 찾을 수 없습니다.", Toast.LENGTH_LONG);
        }
        // String pchName = intent.getStringExtra("pchName");      // 포차 이름
        // String uid = intent.getStringExtra("uid");              // 회원 ID
        // 임의 값 넣어 테스트
        String uid = "롤로";

//        shops.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                num = snapshot.getChildrenCount();
//                strNum = String.valueOf(num);
//                Log.e("test", "onDataChange: "+strNum);
//                pchNameTv.setText(strNum);
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });

        // 객체 생성 및 초기화
        pchDetailBtn = findViewById(R.id.btn_pochainfo_detailTab);
        pchReviewBtn = findViewById(R.id.btn_pochainfo_reviewTab);
        pchMeetingBtn = findViewById(R.id.btn_pochainfo_meetingTab);
        pchDetailFrg = new PochadetailFragment();
        pchReviewFrg = new PochareviewFragment();
        pchMeetingFrg = new PochameetingFragment();
        frgManager = getSupportFragmentManager();

        // ▼ fragment에 데이터 전달 코드
        bundle = new Bundle();              // 전달하기 위해 포차 이름과 회원ID 담을 객체
        bundle.putString("pchName", pchName);
        // bundle.putString("uid", uid);
        // 프래그먼트에 데이터(포차 이름, 회원id) 넘기기
        pchReviewFrg.setArguments(bundle);
        pchMeetingFrg.setArguments(bundle);

        // 탭 버튼 클릭 시, 화면 전환 - 포차 상세 정보, 리뷰 리스트, 번개 리스트
        pchDetailBtn.setOnClickListener(onClickListener);       // 포차 상세 정보
        pchReviewBtn.setOnClickListener(onClickListener);       // 포차 리뷰 리스트
        pchMeetingBtn.setOnClickListener(onClickListener);      // 포차 번개 리스트

    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // v.getResources().getResourceEntryName(v.getId()) : id의 이름(문자)을 반환
            switch (v.getResources().getResourceEntryName(v.getId())) {
                case "btn_pochainfo_detailTab":
                    // 객체 생성 및 초기화
                    frgTransaction = frgManager.beginTransaction();
                    // 포차 상세정보 Fragment로 화면 전환
                    frgTransaction.replace(R.id.frg_pochainfo_mainFragment, pchDetailFrg).commitNow();
                    // 버튼 탭의 색 변경
                    pchDetailBtn.setTextColor(Color.WHITE);
                    pchDetailBtn.setBackgroundResource(R.drawable.pochainfo_allbutton_selected);
                    pchReviewBtn.setTextColor(Color.BLACK);
                    pchReviewBtn.setBackgroundResource(R.drawable.pochainfo_allbutton_unselected);
                    pchMeetingBtn.setTextColor(Color.BLACK);
                    pchMeetingBtn.setBackgroundResource(R.drawable.pochainfo_allbutton_unselected);
                    break;
                case "btn_pochainfo_reviewTab":
                    // 객체 생성 및 초기화
                    frgTransaction = frgManager.beginTransaction();
                    // 포차 리뷰 Fragment로 화면 전환
                    frgTransaction.replace(R.id.frg_pochainfo_mainFragment, pchReviewFrg).commitNow();
                    // 버튼 탭의 색 변경
                    pchDetailBtn.setTextColor(Color.BLACK);
                    pchDetailBtn.setBackgroundResource(R.drawable.pochainfo_allbutton_unselected);
                    pchReviewBtn.setTextColor(Color.WHITE);
                    pchReviewBtn.setBackgroundResource(R.drawable.pochainfo_allbutton_selected);
                    pchMeetingBtn.setTextColor(Color.BLACK);
                    pchMeetingBtn.setBackgroundResource(R.drawable.pochainfo_allbutton_unselected);
                    break;
                case "btn_pochainfo_meetingTab":
                    // 객체 생성 및 초기화
                    frgTransaction = frgManager.beginTransaction();
                    // 포차 번개 Fragment로 화면 전환
                    frgTransaction.replace(R.id.frg_pochainfo_mainFragment, pchMeetingFrg).commitNow();
                    // 버튼 탭의 색 변경
                    pchDetailBtn.setTextColor(Color.BLACK);
                    pchDetailBtn.setBackgroundResource(R.drawable.pochainfo_allbutton_unselected);
                    pchReviewBtn.setTextColor(Color.BLACK);
                    pchReviewBtn.setBackgroundResource(R.drawable.pochainfo_allbutton_unselected);
                    pchMeetingBtn.setTextColor(Color.WHITE);
                    pchMeetingBtn.setBackgroundResource(R.drawable.pochainfo_allbutton_selected);
                    break;
            }
        }
    };
}