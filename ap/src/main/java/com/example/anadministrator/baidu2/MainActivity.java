package com.example.anadministrator.baidu2;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, SensorEventListener {

    private MapView mMapView;
    /**
     * 缩小
     */
    private Button mButSmaller;
    /**
     * 放大
     */
    private Button mButBigger;
    /**
     * 旋转
     */
    private Button mButRotate;
    /**
     * 俯
     */
    private Button mButFuyang;
    /**
     * 仰
     */
    private Button mButFuyang2;
    /**
     * 移动
     */
    private Button mButMove;
    /**
     * 隐藏
     */
    private Button mButHide;
    /**
     * 重置
     */
    private Button mButLocation;
    private Button mButReset;
    private BaiduMap mBaiduMap;
    /**
     * 定位
     */
    // 定位相关
    LocationClient mLocClient;
    public MyLocationListenner myListener = new MyLocationListenner();
    private MyLocationConfiguration.LocationMode mCurrentMode;
    BitmapDescriptor mCurrentMarker;
    private static final int accuracyCircleFillColor = 0xAAFFFF88;
    private static final int accuracyCircleStrokeColor = 0xAA00FF00;
    private SensorManager mSensorManager;
    private Double lastX = 0.0;
    private int mCurrentDirection = 0;
    private double mCurrentLat = 0.0;
    private double mCurrentLon = 0.0;
    private float mCurrentAccracy;
    private MyLocationData locData;
    boolean isFirstLoc = true; // 是否首次定位
    /**
     * 重置
     */
    private Button mButResetTrue;
    private LocationClient client;
    /**
     * 导航
     */
    private Button mButDaoHang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        mBaiduMap = mMapView.getMap();
        mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);//获取传感器管理服务
    }

    private void initView() {
        mMapView = (MapView) findViewById(R.id.bmapView);
        mButSmaller = (Button) findViewById(R.id.butSmaller);
        mButSmaller.setOnClickListener(this);
        mButBigger = (Button) findViewById(R.id.butBigger);
        mButBigger.setOnClickListener(this);
        mButRotate = (Button) findViewById(R.id.butRotate);
        mButRotate.setOnClickListener(this);
        mButFuyang = (Button) findViewById(R.id.butFuyang);
        mButFuyang.setOnClickListener(this);
        mButFuyang2 = (Button) findViewById(R.id.butFuyang2);
        mButFuyang2.setOnClickListener(this);
        mButMove = (Button) findViewById(R.id.butMove);
        mButMove.setOnClickListener(this);
        mButHide = (Button) findViewById(R.id.butHide);
        mButHide.setOnClickListener(this);
        mButReset = (Button) findViewById(R.id.butReset);
        mButReset.setOnClickListener(this);
        mButLocation = (Button) findViewById(R.id.butLocation);
        mButLocation.setOnClickListener(this);
        mButResetTrue = (Button) findViewById(R.id.butResetTrue);
        mButResetTrue.setOnClickListener(this);
        mButDaoHang = (Button) findViewById(R.id.butDaoHang);
        mButDaoHang.setOnClickListener(this);
    }

    private int intSmaller = 10;
    private int Angle = 30;
    private int Angle2 = 0;
    private boolean flag = false;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.butSmaller://缩小
                intSmaller--;
                MapStatusUpdate zoomTo = MapStatusUpdateFactory.zoomTo(intSmaller);
                mBaiduMap.setMapStatus(zoomTo);
                break;
            case R.id.butBigger://放大
                intSmaller++;
                MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.zoomTo(intSmaller);
                mBaiduMap.setMapStatus(mapStatusUpdate);
                break;
            case R.id.butRotate://旋转
                Angle += 10;
                MapStatus ms = new MapStatus.Builder(mBaiduMap.getMapStatus()).rotate(Angle).build();
                MapStatusUpdate rotate = MapStatusUpdateFactory.newMapStatus(ms);
                mBaiduMap.setMapStatus(rotate);
                break;
            case R.id.butFuyang://俯仰
                Angle2 -= 10;
                MapStatus ms2 = new MapStatus.Builder(mBaiduMap.getMapStatus()).overlook(Angle2).build();
                MapStatusUpdate fu = MapStatusUpdateFactory.newMapStatus(ms2);
                mBaiduMap.setMapStatus(fu);
                break;
            case R.id.butMove://移动
                MapStatusUpdate latLng = MapStatusUpdateFactory.newLatLng(this.latLng);
                mBaiduMap.animateMapStatus(latLng, 100000);//第二个时间
                break;
            case R.id.butHide://隐藏
                if (flag == false) {
                    mMapView.setVisibility(View.GONE);
                    flag = true;
                } else {
                    mMapView.setVisibility(View.VISIBLE);
                    flag = false;
                }

                break;
            case R.id.butFuyang2://俯仰2
                Angle2 += 10;
                MapStatus ms3 = new MapStatus.Builder(mBaiduMap.getMapStatus()).overlook(Angle2).build();
                MapStatusUpdate yang = MapStatusUpdateFactory.newMapStatus(ms3);
                mBaiduMap.setMapStatus(yang);
                break;
            case R.id.butReset://卫星+交通
                mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
                mBaiduMap.setTrafficEnabled(true);
                break;
            case R.id.butLocation://定位
                /**
                 * 1.GPS定位
                 * 2.WIFI定位
                 * 3.基站定位
                 * 清单注册Service
                 */
//                startActivity(new Intent(MainActivity.this,LocalActivity.class));
                mMapView = (MapView) findViewById(R.id.bmapView);
                mBaiduMap = mMapView.getMap();

                //设置定位的位置

                isFirstLoc = false;
                LatLng ll = new LatLng(40.047862, 116.306586);
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(ll).zoom(13.0f);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));

                // 开启定位图层
                mBaiduMap.setMyLocationEnabled(true);
                // 定位初始化
                mLocClient = new LocationClient(this);
                mLocClient.registerLocationListener(myListener);
                LocationClientOption option = new LocationClientOption();
                option.setOpenGps(true); // 打开gps
                option.setCoorType("bd09ll"); // 设置坐标类型
                option.setScanSpan(1000);
                mLocClient.setLocOption(option);
                mLocClient.start();
                break;
            case R.id.butResetTrue:
                startActivity(new Intent(MainActivity.this, CloudSearchDemo.class));
//                if(client.isStarted()){
//                    client.unRegisterLocationListener(myListener);
//                    client.stop();
//                }
//                mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
                break;
            case R.id.butDaoHang:
                startActivity(new Intent(MainActivity.this, RoutePlanDemo.class));
                break;
        }
    }

    LatLng latLng = new LatLng(39.932216, 116.402926);

    /**
     * 定位SDK监听函数
     */

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        double x = sensorEvent.values[SensorManager.DATA_X];
        if (Math.abs(x - lastX) > 1.0) {
            mCurrentDirection = (int) x;
            locData = new MyLocationData.Builder()
                    .accuracy(mCurrentAccracy)
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(mCurrentDirection).latitude(mCurrentLat)
                    .longitude(mCurrentLon).build();
            mBaiduMap.setMyLocationData(locData);
        }
        lastX = x;

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }


    /**
     * 定位SDK监听函数
     */
    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null) {
                return;
            }
            mCurrentLat = location.getLatitude();
            mCurrentLon = location.getLongitude();
            mCurrentAccracy = location.getRadius();
            locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(mCurrentDirection).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);
            if (isFirstLoc) {
                isFirstLoc = false;
                LatLng ll = new LatLng(location.getLatitude(),
                        location.getLongitude());
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(ll).zoom(18.0f);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            }
        }


    }

    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mMapView.onResume();
        super.onResume();
        //为系统的方向传感器注册监听器
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onStop() {
        //取消注册传感器监听
        mSensorManager.unregisterListener(this);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        // 退出时销毁定位
        client.stop();
        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;
        super.onDestroy();
    }

}
