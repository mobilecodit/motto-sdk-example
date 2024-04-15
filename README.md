# 안드로이드용 Motto SDK 연동 가이드
* 이 문서는 Motto SDK를 연동하기 위한 초기 설정과 소스 일부를 포함하고 있습니다.
* 현재 Motto SDK의 최신버전은 1.0.1 입니다. 항상 최신 버전을 사용해주시길 바랍니다.
* 전체 소스는 샘플 프로젝트를 참조하시길 바랍니다.

## AndroidManifest 설정
* 인터넷 접속을 허용하는 권한을 추가해야합니다.
```xml
<uses-permission android:name="android.permission.INTERNET">
```

* 비즈핏에서 발급받은 앱키를 등록해야합니다.
```xml
<meta-data 
    android:name="kr.motto.pub_key" 
    android:value="앱키" />
```

## Gradle 설정
* 모듈 수준의 build.gradle에 dependencies 블럭내 아래의 모듈을 추가합니다.
```java
implementation 'kr.motto:motto-sdk:1.0.1'
```

## Proguard 설정
* Proguard를 사용하신다면 proguard-rules.pro 파일에 아래의 코드를 추가합니다.
```java
-keep class kr.motto.mottolib.* { public *; }
```

## Motto-sdk 소스 연동
* Motto SDK를 연동하기 위해서는 먼저 초기화가 필요합니다.
* 유저가 Motto의 미션을 완료시 보상을 받을 수 있도록 유저를 식별 할 수 있는 유니크한 ID를 설정해야 합니다.(대체할 ID나 코드가 없다면 실제 회원의 ID라도 설정해야합니다.)
* Motto SDK는 현재 프래그먼트 형식만 지원합니다. 아래의 코드로 생성할 수 있습니다.
* 구글광고 아이디는 앱에서 이미 수집하고 있으시면 설정해주시길 바랍니다.<br>
  더 정확한 사용자 참여를 확인하기 위한 용도로 사용되며 생략하셔도 무방합니다.
```java
Motto.setUid(uid);   // 유저식별 값(아이디 혹은 유저를 판별할 수 있는 유니크한 값)
//Motto.setAdId(googleAdId);  // 구글 광고아이디(생략가능)
mottoFragment = Motto.create(this); 
getSupportFragmentManager().
              beginTransaction().
              replace(R.id.motto_frame, mottoFragment) 
              .commit();
```

### 뒤로 가기 처리
* Motto Fragment 내에서 뒤로 가기에 대한 별도 처리를 해야 하므로 뒤로가기 이벤트 발생시 프래그먼트에 알려줘야 합니다.
* goBack() 함수 호출시 true를 반환하지 않을때만 앱의 정책에 맞게 처리해주시면 됩니다. 
```java
@Override
public void onBackPressed(){
  if(mottoFragment.goBack())
    return;
  finish();
}
```

### Layout XML 주의사항
* Motto fragment를 삽입할 레이아웃에서 fragment의 layout_height는 0dp 를 설정하시면 안됩니다. <br> match_parent 나 wrap_content로 설정해주셔야 합니다.
```xml
<FrameLayout
    android:id="@+id/motto_frame"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:layout_marginTop="5dp"
    app:layout_constraintBottom_toBottomOf="parent"
    app:layout_constraintLeft_toLeftOf="parent"
    app:layout_constraintRight_toRightOf="parent"
    app:layout_constraintTop_toTopOf="parent" />
```

### Android 공유기능 설정
* 캠페인 정답 입력을 위해 Android 공유 기능을 사용하고 있습니다. 아래 예제와 같이 activity 추가 및 AndroidManifest에 설정해 주세요.
```java
package kr.motto.mottoapp;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import kr.motto.mottolib.Motto;

public class SharedIntentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        processReceivedIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        processReceivedIntent(intent);
    }

    private void processReceivedIntent(Intent intent) {
        if (!Motto.processReceivedIntent(intent)) {
            //todo: 모또 SDK에서 처리하지 않은 경우 앱에 처리
        }
        finish();
    }
}
```
```xml
<activity
    android:name=".SharedIntentActivity"
    android:exported="true"
    android:screenOrientation="portrait"
    android:launchMode="singleTask"
    android:theme="@style/AppTheme.NoTitle">
    <intent-filter>
        <action android:name="android.intent.action.SEND" />
        <category android:name="android.intent.category.DEFAULT" />
        <data android:mimeType="*/*" />
    </intent-filter>
</activity>
```

### 테마 설정 기능
* 다크모드, 라이트모드 설정 (default: 라이트모드)
```java
Motto.setIsDarkMode(true); //다크모드 설정
```
* 백그라운드, 메인색상 지정 (선택사항)
```java
Motto.setBackgroundColor(Color.parseColor("#C9C9C9"));
Motto.setMainColor(Color.parseColor("#FF4356"));
```


### 포스트백 설정 기능
* 사용자의 캠페인 참여 기록을 등록된 포스트백 url로 전송합니다. (등록은 업체등록 홈페이지에서 가능)
* HTTP POST 방식으로 호출합니다.
```xml
    pub_key => 등록된 앱 key 값
    user_id => Motto SDK로 전달된 유저 id
    user_reward => 등록된 비율에 따라 계산된 유저포인트 값
```

