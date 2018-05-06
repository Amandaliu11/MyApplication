package test.amanda.myapplication;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * 拨打电话、挂断电话
 */
public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback{
    private final static int REQUEST_CODE = 666;
    private final static int INDEX_BTN1 = 0;
    private final static int INDEX_BTN2 = 1;
    private final static int INDEX_BTN3 = 2;

    EditText editText;
    Button btn_phone1;
    Button btn_phone2;
    Button btn_phone3;

    int index = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText = (EditText) findViewById(R.id.edittext);
        btn_phone1 = (Button) findViewById(R.id.btn_phone1);
        btn_phone2 = (Button) findViewById(R.id.btn_phone2);
        btn_phone3 = (Button) findViewById(R.id.btn_phone3);

        String tel = "15814003293";
        editText.setText(tel);
        editText.requestFocus();
        editText.setSelection(tel.length());

        //intent 拨打电话
        btn_phone1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //检查权限
                if (invalidPermissionAndRequest()){
                    index = INDEX_BTN1;
                    return;
                }

                //拨打电话
                intentCall();
            }
        });

        //反射 拨打电话
        btn_phone2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //检查权限
                if (invalidPermissionAndRequest()){
                    index = INDEX_BTN2;
                    return;
                }

                //拨打电话
                reflectCall();
            }
        });

        //挂断电话
        btn_phone3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //检查权限
                if (invalidPermissionAndRequest()){
                    index = INDEX_BTN3;
                    return;
                }

                //挂断电话
                endCall();
            }
        });

    }

    private void endCall() {
        switch (SIMUtils.getAvailableSimCardCount(MainActivity.this)){
            case 0:
                Toast.makeText(MainActivity.this, "没有sim卡，无需挂断电话！", Toast.LENGTH_SHORT).show();
                break;
            default:
                PhoneCallUtils.endCall(MainActivity.this);
                break;
        }
    }

    private void reflectCall() {
        String tel = getTelNum();
        if (tel == null) return;

        switch (SIMUtils.getAvailableSimCardCount(MainActivity.this)){
            case 0:
                Toast.makeText(MainActivity.this, "没有sim卡，无法拨打电话！", Toast.LENGTH_SHORT).show();
                break;
            default:
                boolean result = PhoneCallUtils.refectCall(MainActivity.this,tel);
                if (!result){
                    Toast.makeText(MainActivity.this, "拨打电话失败！", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void intentCall() {
        String tel = getTelNum();
        if (tel == null) return;

        switch (SIMUtils.getAvailableSimCardCount(MainActivity.this)){
            case 0:
                Toast.makeText(MainActivity.this, "没有sim卡，无法拨打电话！", Toast.LENGTH_SHORT).show();
                break;
            case 1:
                boolean result = PhoneCallUtils.intentCall(MainActivity.this,tel);
                if (!result){
                    Toast.makeText(MainActivity.this, "拨打电话失败！", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
                    result = PhoneCallUtils.intentCall(MainActivity.this,tel);
                }else {
                    result = PhoneCallUtils.intentCall(MainActivity.this, tel, 0);
                }

                if (!result){
                    Toast.makeText(MainActivity.this, "拨打电话失败！", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults){
        if (requestCode != REQUEST_CODE){
            return;
        }

        boolean requestResult = true;
        for(int i:grantResults){
            if (i <0){
                requestResult = false;
                break;
            }
        }


        if (requestResult){
            switch (index){
                case INDEX_BTN1:
                    intentCall();
                    break;
                case INDEX_BTN2:
                    reflectCall();
                    break;
                case INDEX_BTN3:
                    endCall();
                    break;
                default:
                    break;
            }
        }else{
            switch (index){
                case INDEX_BTN1:
                case INDEX_BTN2:
                    Toast.makeText(MainActivity.this, "申请权限失败，无法拨打电话", Toast.LENGTH_LONG).show();
                    break;
                case INDEX_BTN3:
                    Toast.makeText(MainActivity.this, "申请权限失败，无法挂断电话", Toast.LENGTH_LONG).show();
                    break;
                default:
                    break;
            }
        }

        index = -1;
    }

    /**
     * 获取要拨打的号码
     * @return
     */
    @Nullable
    private String getTelNum() {
        String tel = editText.getText().toString();
        if (tel == null || tel.isEmpty()){
            Toast.makeText(MainActivity.this, "电话号码为空", Toast.LENGTH_SHORT).show();
            return null;
        }
        return tel;
    }

    /**
     * 检查权限，如果没有则弹框向用户申请权限
     * @return
     */
    private boolean invalidPermissionAndRequest() {
        return !PermissionUtils.hasPermissionAndRequest(MainActivity.this,
                new String[]{Manifest.permission.CALL_PHONE,Manifest.permission.READ_PHONE_STATE,Manifest.permission.READ_CALL_LOG},
                REQUEST_CODE);
    }
}
