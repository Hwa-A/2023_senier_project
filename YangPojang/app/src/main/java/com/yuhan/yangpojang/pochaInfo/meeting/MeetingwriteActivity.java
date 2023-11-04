package com.yuhan.yangpojang.pochaInfo.meeting;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.yuhan.yangpojang.R;
import com.yuhan.yangpojang.pochaInfo.model.MeetingDTO;

import org.checkerframework.checker.units.qual.C;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import io.grpc.Server;

// meet: meeting
// pch: pojangmacha
// txtLay: textInputLayout
public class MeetingwriteActivity extends AppCompatActivity {
    private MeetingDTO meeting;              // 번개 객체
    private ConnectivityManager.NetworkCallback networkCallback;    // 인터넷 연결 여부 확인 콜백 메서드
    EditText titleEdt;  // 번개 소개글 EditText
    TextInputLayout titleTxtLay;    // 번개 소개글 컨테이너
    private TimePickerDialog.OnTimeSetListener timeCallBack;    // 번개 시간 선택할 timePickerDialog 콜백 메서드
    private AutoCompleteTextView maxMemberTv;  // 번개 정원 autoCompleteTextView
    private  String[] maxMembers;       // 번개 정원 item 값들을 담는 배열
    private ArrayAdapter<String> arrayAdapter;  // autoCompleteTextView에서 maxMembers 관리할 Adapter
    TextInputLayout minAgeTxtLay;  // 번개 최소 연령 컨테이너
    TextInputLayout maxAgeTxtLay;  // 번개 최대 연령 컨테이너

    TextInputEditText minAgeEdt;     // 번개 최소 연령
    TextInputEditText maxAgeEdt;     // 번개 최대 연령
    private DatabaseReference ref;      // DB 참조 객체
    @Override     // onResume(): Activity가 재활성 될 때마다 호출 => 데이터 업데이트 + 초기화에 사용
    protected void onResume() {
        super.onResume();
        maxMemberTv.setAdapter(arrayAdapter);

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meetingwrite);

        /*
        *  // 네트워크 연결 상태 확인
        if (NetworkUtils.isNetworkAvailable(this)) {
            // 인터넷 연결 가능
            // 이곳에서 필요한 작업을 수행
        } else {
            // 인터넷 연결 끊김
            // 이곳에서 필요한 작업을 수행
        }
        * */

        // 객체 생성 및 초기화
        meeting = new MeetingDTO();         // 번개 객체
        TextView pchNameTv = findViewById(R.id.tv_meetingwrite_pochaName);  // 포차 이름 TextView
        Button registerBtn = findViewById(R.id.btn_meetingwrite_register);  // 번개 등록 Button
        Button cancelBtn = findViewById(R.id.btn_meetingwrite_cancel);  // 번개 취소 Button
        titleEdt = findViewById(R.id.edt_meetingwrite_title);  // 번개 소개글 EditText
        titleTxtLay = findViewById(R.id.txtLay_meetingwrite_titleContainer);    // 번개 소개글 컨테이너
        TextView dateTv = findViewById(R.id.tv_meetingwrite_date);      // 번개 날짜(당일) TextView
        TextView timeTv = findViewById(R.id.tv_meetingwrite_time);      // 번개 시간 TextView
        maxMemberTv = findViewById(R.id.autoTxt_meetingwrite_member);  // 번개 정원 autoCompleteTextView
        maxMembers = getResources().getStringArray(R.array.maxMembers);     // 정원 item
        // new ArrayAdapter<>(눈에 나타낼 xml, drop down되는 xml, 표시할 배열)
        arrayAdapter = new ArrayAdapter<>(this, R.layout.dropitem_pchmeeting_member, maxMembers);
        minAgeTxtLay = findViewById(R.id.txtLay_meetingwrite_minAgeContainer);  // 번개 최소 연령 컨테이너
        minAgeEdt = findViewById(R.id.edt_meetingwrite_minAge);    // 번개 최소 연령
        maxAgeTxtLay = findViewById(R.id.txtLay_meetingwrite_maxAgeContainer);  // 번개 최대 연령 컨테이너
        maxAgeEdt = findViewById(R.id.edt_meetingwrite_maxAge);    // 번개 최대 연령
        ref = FirebaseDatabase.getInstance().getReference();    // DB 참조 객체

        // ▼ PochameetingFragment에서 전달 받은 데이터 받아 처리
        Intent intent = getIntent();
        if (intent != null){
            String pchKey = intent.getStringExtra("pchKey");         // 포차 고유키
            String pchName = intent.getStringExtra("pchName");      // 포차 이름
            String hostUid = intent.getStringExtra("uid");          // 회원 id
            // 포차 이름 변경
            pchNameTv.setText(pchName);
            // 포차 고유키, 회원 id를 번개 객체(MeetingDTO)에 저장
            meeting.setPchKey(pchKey);      // 포차 고유키
            meeting.setHostUid(hostUid);    // 회원 id
        }

        // timePickerDialog에서 번개 시간이 선택되면 호출되는 메서드 구현
        timeCallBack = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                meeting.setTime(hourOfDay+"시 "+minute+"분");     // 번개 객체에 저장(번개 시간)
                timeTv.setText(hourOfDay+"시 "+minute+"분");      // 화면의 번개 시간 변경
            }
        };
        // 처음 화면의 번개 날짜에 보여줄 현재 날짜 구하기
        String yearDate = (getCurrentDateAndTime().get("yearDate"));
        // 화면의 번개 날짜를 당일로 변경
        dateTv.setText(yearDate);
        meeting.setYearDate(yearDate);      // 번개 객체에 저장

        // 번개 소개글 입력에 따른 에러 메시지 출력
        titleErrorMessage(titleEdt, titleTxtLay);
        // 번개 최소 연령대 입력에 따른 에러 메시지 출력
        minAgeErrorMessage(minAgeEdt, minAgeTxtLay);
        // 번개 최대 연령대 입력에 따른 에러 메시지 출력
        maxAgeErrorMessage(maxAgeEdt, maxAgeTxtLay);

        // 번개 시간을 선택할 리스너 연결
        timeTv.setOnClickListener(selectTime);
        // 번개 등록 리스너 연결
        registerBtn.setOnClickListener(registerMeeting);
        // 번개 연령대 editText에서 포커스 벗어나면 텍스트 가운데 졍렬
//        minAgeEdt.setOnFocusChangeListener(textAlignCenter);

        // ▼ 번개 취소 버튼 클릭한 경우, 현재 Activity 종료 코드
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();       // 현재 Activity 종료
            }
        });
    }

    // ▼ 번개 연령대 editText에 포커스가 벗어난 경우, 텍스트 가운데 정렬
    View.OnFocusChangeListener textAlignCenter = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if(!hasFocus){
                if(minAgeTxtLay.getError() == null) {
                    // 포커스가 없는 경우

                    minAgeTxtLay.setEndIconMode(TextInputLayout.END_ICON_NONE);     // clear text 아이콘 숨김
                    minAgeEdt.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);   // 가운데 정렬
                    maxAgeEdt.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);   // 가운데 정렬
                }
            }else {
                // 포커스가 있는 경우
                // clear text 아이콘 표시
                minAgeTxtLay.setEndIconMode(TextInputLayout.END_ICON_CLEAR_TEXT);
            }
        }
    };


    // ▼ 현재 날짜 구하는 코드
    public Map<String, String> getCurrentDateAndTime(){
        // ▼ 현재 시간 구하는 코드
        long currentTimeMillis = System.currentTimeMillis();    // 현재 시간(밀리초)로 가져오기
        Date now = new Date(currentTimeMillis);      // 현재 시간(밀리초)를 Date 객체로 변환
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd hh시 mm분");     // 변환할 형식 지정
        String currentDate = dateFormat.format(now);       // 날짜 형식 변환
        Map<String, String> dateMap = new HashMap<>();    // 필요한 유형의 날짜들을 HashMap 형태로 저장할 Map 객체

        String yearDate = currentDate.substring(0, currentDate.indexOf(" "));  // yyyy/MM/dd 문자열 분리
        String date = yearDate.substring(yearDate.indexOf("/")+1);    // MM/dd 문자열 분리
        String registerTime = date.substring(date.indexOf(" ")+1);   // hh:mm 문자열 분리

        // Map에 값 저장
        dateMap.put("yearDate", yearDate);      // 날짜(년도O)
        dateMap.put("date", date);      // 날짜(년도X)
        dateMap.put("registerTime", registerTime);      // 시간(시,분)

        return dateMap;
    }

    // ▼ 버튼 클릭한 경우, 번개 등록
    View.OnClickListener registerMeeting = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // 번개 객체에 데이터 저장
            meeting.setTitle(titleEdt.getText().toString());    // 번개 소개글 저장
            String maxMember = maxMemberTv.getText().toString();
            String maxMem = maxMember.substring(0, maxMember.indexOf("명"));  // 번개 정원에서 숫자만 분리("명" 제외)
            meeting.setMaxMember(Integer.parseInt(maxMem));     // 번개 정원 저장
            if (!TextUtils.isEmpty(minAgeEdt.getText().toString())) {
                meeting.setMinAge(Integer.parseInt(minAgeEdt.getText().toString()));     // 번개 최소 연령 저장
            }
            if (!TextUtils.isEmpty(maxAgeEdt.getText().toString())) {
                meeting.setMaxAge(Integer.parseInt(maxAgeEdt.getText().toString()));     // 번개 최대 연령 저장
            }
            meeting.setDate(getCurrentDateAndTime().get("date"));   // 날짜(년도X)
            meeting.setRegisterTime(getCurrentDateAndTime().get("registerTime"));   // 날짜

            // 번개 객체의 모든 필드에 값이 존재하는 경우
            if (isValid()) {
                // 번개 연령 범위가 적절한 경우
                if ((minAgeTxtLay.getError() == null) && (maxAgeTxtLay.getError() == null)) {
                    // 번개 id 생성
                    String meetingKey = ref.child("meeting").push().getKey();
                    // firebase에 저장
                    Map<String, Object> meetingInsert = new HashMap<>();
                    meetingInsert.put("/meeting/"+meetingKey, meeting);         // meeting 테이블에 저장
                    meetingInsert.put("/myMeeting/"+meeting.getHostUid()+"/"+meetingKey, meeting.getPchKey());      // myMeeting 테이블에 저장

                    // firebase에 업로드
                    ref.updateChildren(meetingInsert, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                            if (error == null){
                                // 업로드 성공한 경우
                            }else {
                                // 업로드 실패 경우
                            }
                        }
                    });
                }
            }
        }
    };

    // ▼ 버튼 클릭한 경우, TimePickerDialog로 번개 시간 선택
    View.OnClickListener selectTime = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // TimePickerDialog를 화면 출력할 때마다 해당 시점의 시간을 보여줄 수 있도록 onClick 안에 캘린더 객체 생성
            Calendar calendar = Calendar.getInstance();     // 캘린더(TimePickerDialog 구현에 사용할)
            // TimePickerDialog 생성 및 설정
            // is24Hour가 true: AM/PM의 12시간 모드, false:24시간 모드
            TimePickerDialog timePickerDialog = new TimePickerDialog(v.getContext(),
                    android.R.style.Theme_Holo_Light_Dialog_NoActionBar, timeCallBack
                    , calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);
            timePickerDialog.setTitle("번개 시간 선택");      // timePicker 제목 변경
            timePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);    // timePicker 배경 투명하게 변경
            timePickerDialog.show();    // 화면에 출력

        }
    };

    // ▼ 번개 소개글에 공백 입력한 경우, 에러 메시지 출력 코드
    public void titleErrorMessage(EditText edt, TextInputLayout txtLayout){
        edt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // text 변경 전에 호출되는 메서드
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // text 변경 될 때 호출되는 메서드
            }
            @Override
            public void afterTextChanged(Editable s) {
                // text 변경 후에 호출되는 메서드
                // TextUtils.isEmpty(): 라이브러리에서 제공하는 'null or 공백' 체크 함수
                if (TextUtils.isEmpty(s.toString())){
                    // text가 공백인 경우, 에러 메시지 출력
                    txtLayout.setError("문자 입력 필수");
                }else {
                    // text가 공백이 아닌 경우
                    txtLayout.setError(null);
                }
            }
        });
    }
    // ▼ 번개 최소 연령에 공백 입력 or 값이 최대 연령보다 높은 경우, 에러 메시지 출력
    public void minAgeErrorMessage(EditText edt, TextInputLayout txtLayout){
        edt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s.toString())){
                    // text가 공백인 경우, 에러 메시지 출력
                    txtLayout.setError("문자 입력 필수");
                    if(!TextUtils.isEmpty(maxAgeEdt.getText().toString())){
                        maxAgeTxtLay.setError(null);
                        maxAgeTxtLay.setEndIconMode(TextInputLayout.END_ICON_NONE);     // clear text 아이콘 숨김
                        maxAgeEdt.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);   // 가운데 정렬
                    }
                }else {
                    // text가 공백이 아닌 경우
                    txtLayout.setError(null);
                    if((s.toString().length() == 2) && (s.toString().indexOf("0") == 0)){
                        // 십의 자릿 수가 0인 두 자릿 수인 경우, 십의 자리의 0은 생략
                        edt.setText(s.toString().substring(1));
                        edt.setSelection(1);      // 커서를 맨 뒤로 이동
                    }
                    if(!TextUtils.isEmpty(maxAgeEdt.getText().toString())){
                        int min = Integer.parseInt(s.toString());   // 최소 연령
                        int max = Integer.parseInt(maxAgeEdt.getText().toString());  // 최대 연령
                        if(min > max){
                            // 범위가 틀린 경우
                            txtLayout.setError("잘못된 범위");
                        }else{
                            // 범위가 맞는 경우
                            maxAgeTxtLay.setError(null);
                            maxAgeTxtLay.setEndIconMode(TextInputLayout.END_ICON_NONE);     // clear text 아이콘 숨김
                            maxAgeEdt.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);   // 가운데 정렬
                        }
                    }
                }
            }

        });
    }
    // ▼ 번개 최대 연령에 공백 입력 or 값이 최소 연령보다 낮은 경우, 에러 메시지 출력
    public void maxAgeErrorMessage(EditText edt, TextInputLayout txtLayout){
        edt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s.toString())){
                    // text가 공백인 경우, 에러 메시지 출력
                    txtLayout.setError("문자 입력 필수");
                    if(!TextUtils.isEmpty(minAgeEdt.getText().toString())){
                        minAgeTxtLay.setError(null);
                        minAgeTxtLay.setEndIconMode(TextInputLayout.END_ICON_NONE);     // clear text 아이콘 숨김
                        minAgeEdt.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);   // 가운데 정렬
                    }
                }else {
                    // text가 공백이 아닌 경우
                    txtLayout.setError(null);
                    if((s.toString().length() == 2) && (s.toString().indexOf("0") == 0)){
                        // 십의 자릿 수가 0인 두 자릿 수인 경우, 십의 자리의 0은 생략
                        edt.setText(s.toString().substring(1));
                        edt.setSelection(1);      // 커서를 맨 뒤로 이동
                    }
                    if(!TextUtils.isEmpty(minAgeEdt.getText().toString())){
                        int min = Integer.parseInt(minAgeEdt.getText().toString());   // 최소 연령
                        int max = Integer.parseInt(s.toString());  // 최대 연령
                        if(max < min){
                            txtLayout.setError("잘못된 범위");
                        }else{
                            // 범위가 맞는 경우
                            minAgeTxtLay.setError(null);
                            minAgeTxtLay.setEndIconMode(TextInputLayout.END_ICON_NONE);     // clear text 아이콘 숨김
                            minAgeEdt.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);   // 가운데 정렬
                        }
                    }
                }
            }
        });
    }

    // ▼ 번개 객체의 모든 필드에 값이 존재하는지 확인
    private boolean isValid(){
        return !TextUtils.isEmpty(meeting.getHostUid()) && !TextUtils.isEmpty(meeting.getPchKey())
                && !TextUtils.isEmpty(meeting.getTitle()) && !TextUtils.isEmpty(meeting.getYearDate())
                && !TextUtils.isEmpty(meeting.getDate()) && !TextUtils.isEmpty(meeting.getTime())
                && (meeting.getMinAge() > 0) && (meeting.getMaxAge() > 0) && (meeting.getMaxMember() > 0)
                && !TextUtils.isEmpty(meeting.getRegisterTime());
    }

    // ▼ 화면 터치 시, 키보드를 숨기는 코드
    @Override //focusView의 화면에서 보이는 영역의 위치와 크기 정보
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View focusView = getCurrentFocus();     // 현재 포커스를 가진 뷰를 가져옴
        if(focusView != null){
            Rect rect = new Rect();     // 포커스 뷰의 전역적인 가시영역을 가져옴
            focusView.getGlobalVisibleRect(rect);   // 포커스 뷰의 화면에서 보이는 역영의 위치와 크기 등 정보를 rect에 포함
            int x = (int)ev.getX();     // 현재 위치(x)
            int y = (int)ev.getY();     // 현재 위치(y)
            if(!rect.contains(x,y)){    // rect 객체의 위치가 현재 클릭 위치와 다른 경우
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                if(imm != null)
                    // 키보드 숨김
                    imm.hideSoftInputFromWindow(focusView.getWindowToken(), 0);
                // 포커스 제거
                focusView.clearFocus();
            }
        }
        return super.dispatchTouchEvent(ev);
    }
}