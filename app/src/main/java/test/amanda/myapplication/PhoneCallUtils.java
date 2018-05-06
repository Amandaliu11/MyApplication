package test.amanda.myapplication;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 工具类：拨打电话
 *
 * todo: 拨打电话（使用反射），尝试使用slot的SIM卡拨打电话
 *
 * 拨打电话（使用intent方法）intentCall(Context context,String tel)
 * 拨打电话（使用intent方法）,尝试使用slot的SIM卡拨打电话  intentCall(Context context,String tel, int slot)
 * 拨打电话（使用反射）refectCall(Context context,String tel)
 * 挂断电话（使用反射）endCall(Context context)
 */
public class PhoneCallUtils {

    /**
     * 拨打电话（使用intent方法）
     * @param context
     * @param tel
     * @return
     */
    public static boolean intentCall(Context context,String tel) {
        //=== 检查参数 ===
        if(null == context){
            return false;
        }

        if(null == tel || tel.isEmpty()){
            return false;
        }

        //=== 检查权限 ===
        if (!PermissionUtils.hasPermission(context, Manifest.permission.CALL_PHONE)){
            return false;
        }

        //=== 开始直接拨打电话 ===
        Intent intent2 = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + tel));
        intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent2);
        return true;
    }

    /**
     * 拨打电话（使用intent方法）,尝试使用slot的SIM卡拨打电话
     * slot： 0为SIM1，1为SIM2
     * @param context
     * @param tel
     * @return
     */
    @TargetApi(Build.VERSION_CODES.M)
    public static boolean intentCall(Context context,String tel, int slot) {
        //=== 检查参数 ===
        if(null == context){
            return false;
        }

        if(null == tel || tel.isEmpty()){
            return false;
        }

        //=== 检查权限 ===
        if (!PermissionUtils.hasPermission(context, Manifest.permission.CALL_PHONE)){
            return false;
        }

        //=== 开始直接拨打电话 ===
        Intent intent2 = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + tel));
        intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PhoneAccountHandle phoneAccountHandle = SIMUtils.getPhoneAccountHandle(context,slot);
        if (phoneAccountHandle != null){
            intent2.putExtra(TelecomManager.EXTRA_PHONE_ACCOUNT_HANDLE,phoneAccountHandle);
        }
        context.startActivity(intent2);
        return true;
    }

    /**
     * 拨打电话（使用反射）
     * todo：增加方法，使用默认的sim卡拨打电话
     * @param context
     * @param tel
     * @return
     */
    public static boolean refectCall(Context context,String tel) {
        //=== 检查参数 ===
        if(null == context){
            return false;
        }

        if(null == tel || tel.isEmpty()){
            return false;
        }

        //=== 开始直接拨打电话 ===
        try {
            // 首先拿到TelephonyManager
            TelephonyManager telMag = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            Class<TelephonyManager> c = TelephonyManager.class;

            // 再去反射TelephonyManager里面的私有方法 getITelephony 得到 ITelephony对象
            Method mthEndCall = c.getDeclaredMethod("getITelephony", (Class[]) null);
            //允许访问私有方法
            mthEndCall.setAccessible(true);
            final Object obj = mthEndCall.invoke(telMag, (Object[]) null);

            // 再通过ITelephony对象去反射里面的call方法，并传入包名和需要拨打的电话号码
            Method mt = obj.getClass().getMethod("call", new Class[] { String.class, String.class });
            //允许访问私有方法
            mt.setAccessible(true);
            mt.invoke(obj, new Object[] { context.getPackageName() + "", tel });

            return true;
        } catch (InvocationTargetException e) {
            e.printStackTrace();

        } catch (IllegalAccessException e) {
            e.printStackTrace();

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 挂断电话（使用反射）
     * @param context
     * @return
     */
    public static boolean endCall(Context context){
        //=== 检查参数 ===
        if(null == context){
            return false;
        }

        //=== 开始直接拨打电话 ===
        try {
            // 首先拿到TelephonyManager
            TelephonyManager telMag = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            Class<TelephonyManager> c = TelephonyManager.class;

            // 再去反射TelephonyManager里面的私有方法 getITelephony 得到 ITelephony对象
            Method mthEndCall = c.getDeclaredMethod("getITelephony", (Class[]) null);
            //允许访问私有方法
            mthEndCall.setAccessible(true);
            final Object obj = mthEndCall.invoke(telMag, (Object[]) null);

            // 再通过ITelephony对象去反射里面的endCall方法，挂断电话
            Method mt = obj.getClass().getMethod("endCall");
            //允许访问私有方法
            mt.setAccessible(true);
            mt.invoke(obj);

            return true;
        } catch (InvocationTargetException e) {
            e.printStackTrace();

        } catch (IllegalAccessException e) {
            e.printStackTrace();

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return false;
    }
}
