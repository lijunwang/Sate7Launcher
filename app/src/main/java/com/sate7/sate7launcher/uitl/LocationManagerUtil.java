package com.sate7.sate7launcher.uitl;

import java.text.DecimalFormat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import android.text.format.DateFormat;

import com.sate7.sate7launcher.App;
import com.sate7.sate7launcher.R;

/**
 * @author Vangelis.Wang
 * @date 2017/12/28
 * Email:Vangelis.wang@make1.cn
 * Company:Make1
 * Auther:Vangelis.wang in Make1
 * Conment: 定位工具类
 */

public class LocationManagerUtil {

    private static final String TAG = "SOS";
    private static final boolean isLog = false;
    private static LocationManager mLocaitonManager;
    private Context mContext;


    /**
     * 初始化
     *
     * @param context Context
     */
    public void init(Context context) {
        mLocaitonManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        mContext=context;
    }

    /**
     * 获取经纬度
     */
    @SuppressLint("MissingPermission")
    public Location getLatAndLng() {
        return mLocaitonManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    }

    /**
     * 开始监听位置的变化
     */
    @SuppressLint("MissingPermission")
    public void registerLocationUpdate(LocationListener  locationListener) {
        mLocaitonManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locationListener);

    }

    /**
     * 移除对位置变化的监听
     */
    public void removeLocationUpdate(LocationListener  locationListener) {
        mLocaitonManager.removeUpdates(locationListener);
    }

    /**
     * GPS位置变化监听
     */
    /*private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onProviderDisabled(String arg0) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onProviderEnabled(String arg0) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
            // TODO Auto-generated method stub

        }

    };*/

    private void printfLog(String msg) {
        if (isLog) {
            Log.d(TAG, "printfLog: " + msg);
        }
    }
    
    /**
     * 返回位置信息
     *
     * @return 位置信息
     */
    public String getLocaitonInfo() {
        Location location = getLatAndLng();
        DecimalFormat df = new DecimalFormat("#.00000");

        if (location != null) {
			/*
            String lat = df.format(location.getLatitude());
            String lot = df.format(location.getLongitude());
            printfLog("getLocaitonInfo E：" + lot + ",N:" + lat);
            if (TextUtils.isEmpty(lat)) {
                lat = App.getAppContext().getResources().getString(R.string.unknown);
            }
            if (TextUtils.isEmpty(lot)) {
                lot = App.getAppContext().getResources().getString(R.string.unknown);
            }
			
			return (App.getAppContext().getResources().getString(R.string.myposition)+","+App.getAppContext().getResources().getString(R.string.latitude)+":"+lat+","+App.getAppContext().getResources().getString(R.string.longitude)+":"+lot);
			*/
			double fLat = location.getLatitude();
			double fLot = location.getLongitude();
			int lat_du, lat_min, lat_sc;
			int lot_du, lot_min, lot_sc;
			double tmp;
			String lat_SN = "N", lot_EW = "E";
			
			if (fLat >= 0.0) {
				lat_SN = "N";
			} else {
				lat_SN = "S";
			}
			if (fLot >= 0.0) {
				lot_EW = "E";
			} else {
				lot_EW = "W";
			}
			
			lat_du = (int)Math.abs(fLat);
			tmp = (fLat - (double)lat_du) * 60;
			lat_min = (int)tmp;
			tmp = (tmp - (double)lat_min) * 60;
			lat_sc = (int)tmp;
			
			lot_du = (int)Math.abs(fLot);
			tmp = (fLot - (double)lot_du) * 60;
			lot_min = (int)tmp;
			tmp = (tmp - (double)lot_min) * 60;
			lot_sc = (int)tmp;
			
			String timeStr = DateFormat.format("yyyy/MM/dd HH:mm:ss", location.getTime()).toString();
			/*
			 +App.getAppContext().getResources().getString(R.string.longitude)+":"
			 +App.getAppContext().getResources().getString(R.string.latitude)+":"
			 */
            //return lot_EW+":"+lot_du+" "+lat_SN+":"+lat_du;
             //(App.getAppContext().getResources().getString(R.string.myposition)+"
            return	(lot_EW+":"+lot_du+"°"+lot_min+"′"+lot_sc+"″\n"+lat_SN+":"+lat_du+"°"+lat_min+"′"+lat_sc+"″");
        } else {
        	String unkonw = mContext.getResources().getString(R.string.unknow)+"\n";
        	return unkonw;//(App.getAppContext().getResources().getString(R.string.myposition)+":"+unkonw);
        }
    }
}
