package test.amanda.myapplication;

import android.Manifest;
import android.content.Context;
import android.os.Build;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * 工具类：SIM卡
 *
 * todo: 获取上次拨打电话的sim卡
 *
 * 检查手机上有几个可用的SIM卡 getAvailableSimCardCount(Context context)
 * 获取slotId对应的PhoneAccountHandle getPhoneAccountHandle(Context context,int slotId)
 *
 */
public class SIMUtils {
    /**
     * 检查手机上有几个可用的SIM卡
     * @param context
     * @return
     */
    public static int getAvailableSimCardCount(Context context){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1){
            int count = 0;
            SubscriptionManager mSubscriptionManager = SubscriptionManager.from(context);
            for(int i = 0; i < getSimCardCount(context); i++){
                SubscriptionInfo sir = mSubscriptionManager.getActiveSubscriptionInfoForSimSlotIndex(i);
                if(sir != null){
                    count++;
                }
            }


            return count;
        }else{
            return 1;
        }
    }

    /**
     * Returns the number of phones available.
     * Returns 1 for Single standby mode (Single SIM functionality)
     * Returns 2 for Dual standby mode.(Dual SIM functionality)
     */
    private static int getSimCardCount(Context context) {
        TelephonyManager mTelephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        Class cls = mTelephonyManager.getClass();
        try {
            Method mMethod = cls.getMethod("getSimCount");
            mMethod.setAccessible(true);
            return (int) mMethod.invoke(mTelephonyManager);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * 获取slotId对应的PhoneAccountHandle
     */
    public static PhoneAccountHandle getPhoneAccountHandle(Context context,int slotId) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            return null;
        }

        //=== 检查参数 ===
        if(null == context){
            return null;
        }

        //=== 检查权限 ===
        if (!PermissionUtils.hasPermission(context, Manifest.permission.READ_PHONE_STATE)){
            return null;
        }

        //=== 获取slotId对应的PhoneAccountHandle ===
        TelecomManager tm = (TelecomManager) context.getSystemService(Context.TELECOM_SERVICE);
        if (tm != null) {
            List<PhoneAccountHandle> phoneAccountHandleList = tm.getCallCapablePhoneAccounts();
            if (phoneAccountHandleList == null || phoneAccountHandleList.isEmpty()){
                return null;
            }

            if (slotId <0 || slotId >=phoneAccountHandleList.size()){
                return null;
            }

            return phoneAccountHandleList.get(slotId);

        }

        return null;
    }




    // todo:获取上次拨打telNum时使用的SIM卡
//    /**
//     * 获取上次拨打telNum时使用的SIM卡
//     * @param context
//     * @param telNum
//     * @return
//     */
//    @Nullable
//    public static String getLastestSim(Context context, String telNum){
//        //=== 检查参数 ===
//        if(null == context){
//            return null;
//        }
//
//        if(null == telNum || telNum.isEmpty()){
//            return null;
//        }
//
//        //=== 检查权限 ===
//        if (!PermissionUtils.checkPermission(context, Manifest.permission.READ_CALL_LOG)){
//            return null;
//        }
//
//        //=== 检查拨打电话的日志，获取上次拨打telNum时的sim卡 ===
//        String result = null;
//        Cursor cursor = null;
//        try {
//            cursor = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, new String[]{CallLog.Calls.NUMBER, CallLog.Calls.PHONE_ACCOUNT_ID},
//                    CallLog.Calls.NUMBER + " = ?", new String[]{telNum}, CallLog.Calls.DEFAULT_SORT_ORDER);
//            if (cursor != null && cursor.moveToFirst()) {
//                int subId = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.PHONE_ACCOUNT_ID));
//                Log.d("tag", "getLastestSim subId:" + subId);
//                int slotId = getSlotIdUsingSubId(subId, context);
//                Log.d("tag", "getLastestSim slotId:" + slotId);
//                if(0 == slotId){
//                    result = "SIM1";
//                }else if(1 == slotId){
//                    result = "SIM2";
//                }
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//        }finally {
//            if(cursor != null){
//                cursor.close();
//            }
//        }
//
//        Log.d("tag", "getLastestSim result:" + result);
//
//        return result;
//    }
//
//
//    public static int getSlotIdUsingSubId(int subId,Context context) throws InvocationTargetException {
//        int  result = 0;
//        try {
//            Class<?> clz = Class.forName(SUBSCRIPTION_MANAGER);
//            Object subSm;
//            Constructor<?> constructor = clz.getDeclaredConstructor(Context.class);
//            subSm  = constructor.newInstance(context);
//            Method mth = clz.getMethod("getSlotId", int.class);
//            result = (int)mth.invoke(subSm, subId);
//
//        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
//                | IllegalArgumentException | NoSuchMethodException | InvocationTargetException e) {
//            e.printStackTrace();
//        }
//        return result;
//    }
}
