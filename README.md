# Guide to Installing the Motto SDK on Android
*  This document contains some initial settings and sources for installing Motto SDK.
*  The latest version of Motto SDK is 1.0.10. Please always use the latest version.
*  Please refer to the sample project for the full source.

## AndroidManifest setting
* You need to add permission to allow internet access.
```xml
<uses-permission android:name="android.permission.INTERNET">
```

* You must write the registered app key.
```xml
<meta-data 
    android:name="kr.motto.pub_key" 
    android:value="{app-key}" />
```

## Gradle setting
* Add the below in the dependencies block in build.gradle file.
```java
implementation 'kr.motto:motto-sdk:1.0.10'
```
* Add the belows to repositories settings
```java
repositories {
    ...
    mavenCentral()
    maven { url 'https://artifact.bytedance.com/repository/pangle'}
    ...
}
```

## Proguard setting
* If you use Proguard, add the code below to the proguard-rules.pro file.
```java
-keep class kr.motto.mottolib.* { public *; }
-keep class com.google.gson.reflect.TypeToken
-keep class * extends com.google.gson.reflect.TypeToken
-keep public class * implements java.lang.reflect.Type
```

## Motto-sdk initialize
* To use Motto SDK, initialization is required first.
* A unique ID must be set to identify the user so that the user can receive rewards upon completing Motto's mission. (If there is no replacement ID or code, you must set the ID of an actual member.)
* Motto SDK currently only supports Android Fragment. You can create it with the code below.
* If you have already collected the Google Advertising ID in the app, please set it. It is optional.
  
```java
Motto.setUid(uid);   // user id (or unique value of user)
//Motto.setAdId(googleAdId);  // google adid
mottoFragment = Motto.create(this); 
getSupportFragmentManager().
              beginTransaction().
              replace(R.id.motto_frame, mottoFragment) 
              .commit();
```

### Process Back-key event
* Since back-key envent must be handled separately within the Motto Fragment, the fragment must be notified when a back-key event occurs.
* Handle it in accordance with the app's policy only when the goBack() function does not return true. 
```java
@Override
public void onBackPressed(){
  if(mottoFragment.goBack())
    return;
  finish();
}
```

### Layout XML setting
* In the layout where you will insert the Motto fragment, the fragment's layout_height should NOT be set to 0dp. <br> You must set it to match_parent or wrap_content.
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

### Android share setting
* We are using the Android sharing feature to enter campaign answers. Please add an activity and set it in AndroidManifest as shown in the example below.
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
            //todo: process app when Motto SDK returns false.
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

### Theme setting
* Choose dark mode, light mode  (default: light mode)
```java
Motto.setIsDarkMode(true); 
```
* Setting background-color, main button color (optional)
```java
Motto.setBackgroundColor(Color.parseColor("#C9C9C9"));
Motto.setMainColor(Color.parseColor("#FF4356"));
```


### Postback setting
* The user's campaign participation record is sent to the registered postback URL. (Registration is possible on the company registration website)
* Called using HTTP POST method
```xml
    pub_key => registered app key 
    user_id => user id
    user_reward => reward value
    transaction_id => transaction id of campaign complete
    campaign_title => title of campaign
```

* Response: success
```xml
    {
        "result": 1,
        "message": "success"
    }
```
* Response: fail
```xml
    {
        "result": 0,
        "message": "{error message}"
    }
```


### Help: minkyu.joo.mjt@gmail.com


---


# 안드로이드용 Motto SDK 연동 가이드
* 이 문서는 Motto SDK를 연동하기 위한 초기 설정과 소스 일부를 포함하고 있습니다.
* 현재 Motto SDK의 최신버전은 1.0.10 입니다. 항상 최신 버전을 사용해주시길 바랍니다.
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
implementation 'kr.motto:motto-sdk:1.0.10'
```
* repositories settings에 아래 내용을 추가합니다.
```java
repositories {
    ...
    mavenCentral()
    maven { url 'https://artifact.bytedance.com/repository/pangle'}
    ...
}
```

## Proguard 설정
* Proguard를 사용하신다면 proguard-rules.pro 파일에 아래의 코드를 추가합니다.
```java
-keep class kr.motto.mottolib.* { public *; }
-keep class com.google.gson.reflect.TypeToken
-keep class * extends com.google.gson.reflect.TypeToken
-keep public class * implements java.lang.reflect.Type
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
    transaction_id => 캠페인 완료 처리 transaction id 
    campaign_title => 캠페인 제목
```
* Response: 성공시
```xml
    {
        "result": 1,
        "message": "success"
    }
```
* Response: 실패
```xml
    {
        "result": 0,
        "message": "{error message}"
    }
```
