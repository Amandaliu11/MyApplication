package test.amanda.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * 工具类：权限
 *
 * 检查是否有权限 hasPermission(Context context, String permission)
 * 检查是否有一组权限 hasPermission(Context context,String[] permissions)
 * 申请权限：弹框提示用户是否允许权限 requestPermission(Activity activity, String[] persions, int requestCode)
 * 检查是否有权限,如果没有则申请权限 hasPermissionAndRequest(Activity activity, String permission, int requestCode)
 * 检查是否有一组权限,如果没有则申请权限 hasPermissionAndRequest(Activity activity, String[] permissions, int requestCode)
 */
public class PermissionUtils {
    /**
     * 检查是否有权限
     * @param context
     * @param permission
     * @return
     */
    public static boolean hasPermission(Context context, String permission){
        if (null == permission || permission.isEmpty()){
            return true;
        }

        if (ContextCompat.checkSelfPermission(context, permission)
                != PackageManager.PERMISSION_GRANTED) {
            // 没有权限，申请权限。
            // todo:自动申请权限

            return false;
        }else{
            //有权限
            return true;
        }
    }

    /**
     * 检查是否有一组权限
     * @param context
     * @param permissions
     * @return
     */
    public static boolean hasPermission(Context context,String[] permissions){
        if (null == permissions || permissions.length<=0){
            return true;
        }

        boolean result = true;
        for(String str:permissions){
            if (ContextCompat.checkSelfPermission(context, str) != PackageManager.PERMISSION_GRANTED) {
                // 没有权限
                result = false;
                break;
            }
        }

        return result;
    }


    /**
     * 申请权限：弹框提示用户是否允许权限
     * @param activity
     * @param persions
     * @param requestCode
     */
    public static void requestPermission(Activity activity, String[] persions, int requestCode){
        ActivityCompat.requestPermissions(activity,persions,requestCode);
    }


    /**
     * 检查是否有权限,如果没有则申请权限
     * @param activity
     * @param permission
     * @return
     */
    public static boolean hasPermissionAndRequest(Activity activity, String permission, int requestCode){
        if (hasPermission(activity,permission)){
            return true;
        }

        requestPermission(activity,new String[]{permission},requestCode);
        return false;
    }

    /**
     * 检查是否有一组权限,如果没有则申请权限
     * @param activity
     * @param permissions
     * @return
     */
    public static boolean hasPermissionAndRequest(Activity activity, String[] permissions, int requestCode){
        if (hasPermission(activity,permissions)){
            return true;
        }

        requestPermission(activity,permissions,requestCode);
        return false;
    }
}
