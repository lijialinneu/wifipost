package lijialin.wifipost.activity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import lijialin.wifipost.R;
import lijialin.wifipost.utils.DataBaseHelper;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "asdf MainActivity";

    @BindView(R.id.textView)
    TextView textView;
    @BindView(R.id.textView2)
    TextView textView2;
    private String username;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        queryUsernamePassword(); //每次启动都到数据库中查询用户名和密码
//        username = "1470874";
//        password = "16331";
//        username = "1570973";
//        password = "lijialin19930715";

    }


    /**
     * 查询数据
     */
    public void queryUsernamePassword() {
        //打开或创建数据库
        SQLiteDatabase db = this.openOrCreateDatabase(DataBaseHelper.DB_NAME,MODE_PRIVATE,null);
        //检查是否有user表
        String sql = "select count(*) from sqlite_master where type ='table' and name ='user'" ;
        Cursor cursor = db.rawQuery(sql, null);
        if(cursor.moveToNext()){
            int num = cursor.getInt(0);

            if(num == 1) { //有表
                String[] columns = {"username", "password"};
                cursor = db.query ("user", columns, null, null, null, null, null);

                if(cursor.moveToFirst()) {
                    Log.d(TAG, "能查到数据");
                    username = cursor.getString(0);//获得用户名
                    password = cursor.getString(1);//获得密码
                    Log.d(TAG, username);
                    Log.d(TAG, password);
                    textView2.setText("当前用户: "+username);
                } else {
                    Log.d(TAG, "查不到数据");
                    gotoLoginActivity();
                }
            }else { //无表
                Log.d(TAG, "没有表");
                gotoLoginActivity();
            }
        }

        db.close();
        cursor.close();
    }

    /**
     * 跳转到LoginActivity
     */
    public void gotoLoginActivity() {
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, LoginActivity.class);
        MainActivity.this.startActivity(intent);
        finish();
    }

    /**
     * used to show the result
     */
    Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            String str = msg.getData().getString("return_string");//接受msg传递过来的参数
            Log.d(TAG, str);
            textView.setText(str);
            super.handleMessage(msg);
        }
    };

    /**
     * post to logout
     */
    @OnClick(R.id.login_btn)
    public void login() {
        new Thread(){
            @Override
            public void run(){
                try {
                    OkHttpClient client = new OkHttpClient();
                    MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");

                    StringBuffer stringBuffer = new StringBuffer();
                    stringBuffer.append("username=");
                    stringBuffer.append(username);
                    stringBuffer.append("&ac_id=1&action=login&password=");
                    stringBuffer.append(password);
                    stringBuffer.append("\n");

                    RequestBody body = RequestBody.create(mediaType, stringBuffer.toString());

                    Request request = new Request.Builder()
                            .url("https://ipgw.neu.edu.cn/srun_portal_pc.php?url=&ac_id=1")
                            .post(body)
                            .addHeader("content-type", "application/x-www-form-urlencoded")
                            .build();

                    Response response = client.newCall(request).execute();

                    String str = response.body().string();
                    String str2,str3;

                    if(str.contains("<p>E2553: Password is error.(密码错误)</p>")) {
                        str3 = "密码错误";
                    }else {
                        int a = str.indexOf("<td height=\"40\" style=\"font-weight:bold;color:orange;\">");
                        str2 = str.substring(a, a+121);
                        str3 = str2.substring(65, 110);
                    }
                    Message msg = new Message();
                    Bundle bundle = new Bundle();
                    bundle.putString("return_string",str3);  //往Bundle中put数据
                    msg.setData(bundle);
                    myHandler.sendMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }

    /**
     * post to logout
     */
    @OnClick(R.id.logout_btn)
    public void logout() {
        new Thread(){
            @Override
            public void run(){
                try {
                    OkHttpClient client = new OkHttpClient();
                    MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");

                    StringBuffer stringBuffer = new StringBuffer();
                    stringBuffer.append("username=");
                    stringBuffer.append(username);
                    stringBuffer.append("&ajax=1&action=logout&password=");
                    stringBuffer.append(password);
                    stringBuffer.append("\n");

                    RequestBody body = RequestBody.create(mediaType, stringBuffer.toString());

                    Request request = new Request.Builder()
//                            .url("https://ipgw.neu.edu.cn:802/include/auth_action.php")
                            .url("https://ipgw.neu.edu.cn/srun_portal_pc.php?url=&ac_id=1")
                            .post(body)
                            .addHeader("content-type", "application/x-www-form-urlencoded")
                            .build();
                    Response response = client.newCall(request).execute();

                    String str = response.body().string();

                    Log.d(TAG, str);

                    Message msg = new Message();
                    Bundle bundle = new Bundle();
                    bundle.putString("return_string",str);  //往Bundle中put数据
                    msg.setData(bundle);
                    myHandler.sendMessage(msg);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }


    /**
     * update username and password
     */
    @OnClick(R.id.update)
    public void updateInfo() {
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, UpdateActivity.class);
        MainActivity.this.startActivity(intent);
        finish();
    }
}