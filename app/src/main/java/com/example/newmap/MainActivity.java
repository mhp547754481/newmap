package com.example.newmap;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Point;
import android.location.Location;
import android.os.Bundle;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.CancelableCallback;
import com.amap.api.maps.AMap.OnMapClickListener;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.Projection;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.CustomMapStyleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;

public class MainActivity extends AppCompatActivity implements LocationSource,
        AMapLocationListener, AMap.OnMapTouchListener {

    private MapView mapView;
    private AMap aMap;
    private UiSettings mUiSettings;
    private RadioGroup zoomRadioGroup;
    private CustomMapStyleOptions mapStyleOptions = new CustomMapStyleOptions();
    private Location location;
    private AMapLocation amapLocation;

    private OnLocationChangedListener mListener;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;

    public static final String TAG = "AMAP_iii";

    boolean useMoveToLocationWithMapMode = true;

    //自定义定位小蓝点的Marker
    Marker locationMarker;

    //坐标和经纬度转换工具
    Projection projection;

    MyLocationStyle myLocationStyle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //获取地图控件引用
        mapView = (MapView) findViewById(R.id.map);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mapView.onCreate(savedInstanceState);
        init();
    }

    /**
     * 初始化AMap对象
     */
    private void init() {
        if (aMap == null) {
            aMap = mapView.getMap();

            aMap.setLocationSource(this);// 设置定位监听
            aMap.setOnMapTouchListener(this);

//            myLocationStyle = new MyLocationStyle();
//            aMap.setMyLocationStyle(myLocationStyle);
            mUiSettings = aMap.getUiSettings();
            mUiSettings.setCompassEnabled(true);//显示指南针图标及功能
            mUiSettings.setMyLocationButtonEnabled(true);//是否显示默认的定位按钮
            aMap.setMyLocationEnabled(true);//设置为true表示显示定位层并可触发定位
//            double a = amapLocation.getLatitude();//获取纬度
//            double b = location.getLongitude();//获取纬度
//            //LatLng latLng = new LatLng(a,b);//构造一个位置
//            aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(a,b),11));

            //aMap.setMyLocationStyle(myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE)) ;//定位一次，且将视角移动到地图中心点。
//            aMap.setMyLocationStyle(myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER));
//            // 定位、但不会移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。
            aMap.setOnMapLoadedListener(new AMap.OnMapLoadedListener() {
                @Override
                public void onMapLoaded() {
                    // 室内地图默认不显示，这里把它设置成显示
                    aMap.showIndoorMap(true);
                    // 关闭SDK自带的室内地图控件
                    aMap.getUiSettings().setIndoorSwitchEnabled(true);
                }
            });
            //地图样式
            byte[] buffer1 = null;
            byte[] buffer2 = null;
            InputStream is1 = null;
            InputStream is2 = null;
            try {
                is1 = getAssets().open("style.data");
                int lenght1 = is1.available();
                buffer1 = new byte[lenght1];
                is1.read(buffer1);
                is2 = getAssets().open("style_extra.data");
                int lenght2 = is2.available();
                buffer2 = new byte[lenght2];
                is2.read(buffer2);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (is1 != null)
                        is1.close();
                    if (is2 != null)
                        is2.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            CustomMapStyleOptions customMapStyleOptions = new CustomMapStyleOptions();
            customMapStyleOptions.setStyleData(buffer1);
            customMapStyleOptions.setStyleExtraData(buffer2);
            aMap.setCustomMapStyle(customMapStyleOptions);
//            aMap.setOnMyLocationChangeListener(new AMap.OnMyLocationChangeListener() {
//                @Override
//                public void onMyLocationChange(Location location) {
//                    //从location对象中获取经纬度信息，地址描述信息，建议拿到位置之后调用逆地理编码接口获取
//                    double a = location.getLatitude();
//                    double b = location.getLongitude();
//                    BigDecimal bd = new BigDecimal(a);
//                    String strD1 = Double.toString(a);
//                    aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 11));
//                    Toast.makeText(MainActivity.this, strD1, Toast.LENGTH_SHORT).show();
//                }
//            });

        }
    }

    //    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
//        mapView.onDestroy();
//    }
    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mapView.onResume();
        useMoveToLocationWithMapMode = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mapView.onPause();
        deactivate();

        useMoveToLocationWithMapMode = false;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mapView.onDestroy();
        if (null != mlocationClient) {
            mlocationClient.onDestroy();
        }
    }

    /**
     * 定位成功后回调函数
     */
    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (mListener != null && amapLocation != null) {
            if (amapLocation != null
                    && amapLocation.getErrorCode() == 0) {
                LatLng latLng = new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude());
                //展示自定义定位小蓝点
                if (locationMarker == null) {
                    //首次定位
                    locationMarker = aMap.addMarker(new MarkerOptions().position(latLng)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.location_marker))
                            .anchor(0.5f, 0.5f));

                    //首次定位,选择移动到地图中心点并修改级别到15级
                    aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                } else {

                    if (useMoveToLocationWithMapMode) {
                        //二次以后定位，使用sdk中没有的模式，让地图和小蓝点一起移动到中心点（类似导航锁车时的效果）
                        startMoveLocationAndMap(latLng);
                    } else {
                        startChangeLocation(latLng);
                    }

                }


            } else {
                String errText = "定位失败," + amapLocation.getErrorCode() + ": " + amapLocation.getErrorInfo();
                Log.e("AmapErr", errText);
            }
        }
    }

    /**
     * 修改自定义定位小蓝点的位置
     *
     * @param latLng
     */
    private void startChangeLocation(LatLng latLng) {

        if (locationMarker != null) {
            LatLng curLatlng = locationMarker.getPosition();
            if (curLatlng == null || !curLatlng.equals(latLng)) {
                locationMarker.setPosition(latLng);
            }
        }
    }

    /**
     * 同时修改自定义定位小蓝点和地图的位置
     *
     * @param latLng
     */
    private void startMoveLocationAndMap(LatLng latLng) {

        //将小蓝点提取到屏幕上
        if (projection == null) {
            projection = aMap.getProjection();
        }
        if (locationMarker != null && projection != null) {
            LatLng markerLocation = locationMarker.getPosition();
            Point screenPosition = aMap.getProjection().toScreenLocation(markerLocation);
            locationMarker.setPositionByPixels(screenPosition.x, screenPosition.y);

        }

        //移动地图，移动结束后，将小蓝点放到放到地图上
        myCancelCallback.setTargetLatlng(latLng);
        //动画移动的时间，最好不要比定位间隔长，如果定位间隔2000ms 动画移动时间最好小于2000ms，可以使用1000ms
        //如果超过了，需要在myCancelCallback中进行处理被打断的情况
        aMap.animateCamera(CameraUpdateFactory.changeLatLng(latLng), 1000, myCancelCallback);

    }


    MyCancelCallback myCancelCallback = new MyCancelCallback();

    @Override
    public void onTouch(MotionEvent motionEvent) {
        Log.i("amap", "onTouch 关闭地图和小蓝点一起移动的模式");
        useMoveToLocationWithMapMode = false;
    }

    /**
     * 监控地图动画移动情况，如果结束或者被打断，都需要执行响应的操作
     */
    class MyCancelCallback implements AMap.CancelableCallback {

        LatLng targetLatlng;

        public void setTargetLatlng(LatLng latlng) {
            this.targetLatlng = latlng;
        }

        @Override
        public void onFinish() {
            if (locationMarker != null && targetLatlng != null) {
                locationMarker.setPosition(targetLatlng);
            }
        }

        @Override
        public void onCancel() {
            if (locationMarker != null && targetLatlng != null) {
                locationMarker.setPosition(targetLatlng);
            }
        }
    }

    ;


    /**
     * 激活定位
     */
    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;
        if (mlocationClient == null) {
            mlocationClient = new AMapLocationClient(this);
            mLocationOption = new AMapLocationClientOption();
            //设置定位监听
            mlocationClient.setLocationListener(this);
            //设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //是指定位间隔
            mLocationOption.setInterval(2000);
            //设置定位参数
            mlocationClient.setLocationOption(mLocationOption);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mlocationClient.startLocation();
        }
    }

    /**
     * 停止定位
     */
    @Override
    public void deactivate() {
        mListener = null;
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
    }

}
