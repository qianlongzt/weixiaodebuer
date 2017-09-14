package com.z_72.weixiaodebuger;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.uuzuche.lib_zxing.activity.CaptureActivity;
import com.uuzuche.lib_zxing.activity.CodeUtils;
import com.uuzuche.lib_zxing.activity.ZXingLibrary;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    TextView output;
    EditText text;
    private static final int REQUEST_CODE = 1;
    private static final String WeixiaoUrl = "http://weixiao.qq.com/apps/campuscard/gate/";

    @TargetApi(Build.VERSION_CODES.KITKAT)

    public  void log(String log) {
        output.setText(output.getText()+"\n"+log);
    }


    public void get(String result) {
        String url = WeixiaoUrl +text.getText().toString()+ "?auth_code=" + result;
        this.log("得到："+url);

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    // Display the first 500 characters of the response string.
                    try {
                        JSONObject jsonObject =  new JSONObject(response);
                        int code = jsonObject.getInt("code");
                        String message ;
                        if (code != 0) {
                            message = jsonObject.getString("message");
                        } else {
                            message = jsonObject.getString("card_number");
                        }
                        output.setText("code: "+code + "\n message:"+message);
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                output.setText(error.toString());
                error.printStackTrace();
            }
        });
// Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
    @Override
    public void onActivityResult (int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_CODE) {
            //处理扫描结果（在界面上显示）
            if (null != data) {
                Bundle bundle = data.getExtras();
                if (bundle == null) {
                    return;
                }
                if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                    String result = bundle.getString(CodeUtils.RESULT_STRING);
                    this.get(result);
                } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                    this.log("解析二维码失败");
                }
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.output = (TextView)findViewById(R.id.output);
        this.text = (EditText)findViewById(R.id.text);
        this.output.setText("");
        ZXingLibrary.initDisplayOpinion(this);

        Button button1 = (Button) findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CaptureActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });
        Button button2 = (Button) findViewById(R.id.clear);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                output.setText("");
            }
        });
    }
}
