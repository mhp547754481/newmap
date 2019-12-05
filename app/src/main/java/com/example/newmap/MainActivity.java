package com.example.newmap;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.RadioGroup;

import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.CustomMapStyleOptions;
import com.amap.api.maps.model.MyLocationStyle;

public class MainActivity extends AppCompatActivity {

    private MapView mapView;
    private AMap aMap;
    private UiSettings mUiSettings;
    private RadioGroup zoomRadioGroup;
    private CustomMapStyleOptions mapStyleOptions = new CustomMapStyleOptions();
    private MyLocationStyle myLocationStyle;

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

            styles();

//            //地图样式
//            byte[] buffer1 = null;
//            byte[] buffer2 = null;
//            InputStream is1 = null;
//            InputStream is2 = null;
//            try {
//                is1 = getAssets().open("style.data");
//                int lenght1 = is1.available();
//                buffer1 = new byte[lenght1];
//                is1.read(buffer1);
//                is2 = getAssets().open("style_extra.data");
//                int lenght2 = is2.available();
//                buffer2 = new byte[lenght2];
//                is2.read(buffer2);
//            } catch (IOException e) {
//                e.printStackTrace();
//            } finally {
//                try {
//                    if (is1 != null)
//                        is1.close();
//                    if (is2 != null)
//                        is2.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//            CustomMapStyleOptions customMapStyleOptions = new CustomMapStyleOptions();
//            customMapStyleOptions.setStyleData(buffer1);
//            customMapStyleOptions.setStyleExtraData(buffer2);
//            aMap.setCustomMapStyle(customMapStyleOptions);
        }
    }
    private void styles(){
        myLocationStyle = new MyLocationStyle();
        aMap.setMyLocationStyle(myLocationStyle);
        mUiSettings = aMap.getUiSettings();
        mUiSettings.setCompassEnabled(true);//显示指南针图标及功能
        mUiSettings.setMyLocationButtonEnabled(true);//是否显示默认的定位按钮
        aMap.setMyLocationEnabled(true);//设置为true表示显示定位层并可触发定位
        //aMap.setMyLocationStyle(myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE));//定位一次，且将视角移动到地图中心点。
        aMap.setMyLocationStyle(myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER));
        // 定位、但不会移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。
        //MyLocationStyle myLocationIcon(BitmapDescriptor myLocationIcon);//设置定位蓝点的icon图标方法，需要用到BitmapDescriptor类对象作为参数。
//            double a = amapLocation.getLatitude();//获取纬度
//            double b = amapLocation.getLongitude();//获取纬度
//            LatLng latLng = new LatLng(a,b);//构造一个位置
//            aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,11));
        aMap.setOnMapLoadedListener(new AMap.OnMapLoadedListener() {
            @Override
            public void onMapLoaded() {
                // 室内地图默认不显示，这里把它设置成显示
                aMap.showIndoorMap(true);
                // 关闭SDK自带的室内地图控件
                aMap.getUiSettings().setIndoorSwitchEnabled(true);
                //移动到有室内地图的地方,放大级别才可以看见
                //aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(39.91095, 116.37296), 19));
            }
        });
        //地图样式
        aMap.setCustomMapStyle(
                new com.amap.api.maps.model.CustomMapStyleOptions()
                        .setEnable(true)
                        .setStyleDataPath("style.data")
                        .setStyleExtraPath("style_extra.data")
        );
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mapView.onDestroy();
    }
    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mapView.onPause();
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mapView.onSaveInstanceState(outState);
    }

}
