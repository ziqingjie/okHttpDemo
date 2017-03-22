package com.example.okhttpdemo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;

import java.io.IOException;
import java.util.concurrent.ForkJoinPool;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private ImageView imageView;
    private Button button;
    public final String path="http://pic250.quanjing.com/imageclk003/ic00412294.jpg";
    private final int SUCCESS=1;
    private final int FAIL=0;
    private final String TAG=this.getClass().getSimpleName();
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case SUCCESS:
                    Log.e(TAG,"请求成功");
                   byte[] b = (byte[]) msg.obj;
                    Bitmap bitmap=new CropSquareTrans().transform(BitmapFactory.decodeByteArray(b,0,b.length));
                   imageView.setImageBitmap(bitmap);
                    break;
                case FAIL:
                    Log.e(TAG,"请求失败");
                    break;
            }
        }
    };
    private OkHttpClient okHttpClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       imageView=(ImageView)findViewById(R.id.imageView);
       
        //创建网络请求对象
        okHttpClient = new OkHttpClient();
        //使用get请求获取请求对象
        final Request request = new Request.Builder().get().url(path).build();

        button=(Button)findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                okHttpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {

                        Message message = handler.obtainMessage();
                        if (response.isSuccessful()){
                            message.what=SUCCESS;
                            message.obj=response.body().bytes();
                            handler.sendMessage(message);
                        }else{
                            handler.sendEmptyMessage(FAIL);
                        }

                    }
                });
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
