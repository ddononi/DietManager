package kr.co.diet;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import kr.co.diet.dao.PathPoint;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPOIItem.MarkerType;
import net.daum.mf.map.api.MapPOIItem.ShowAnimationType;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapPolyline;
import net.daum.mf.map.api.MapReverseGeoCoder;
import net.daum.mf.map.api.MapReverseGeoCoder.ReverseGeoCodingResultListener;
import net.daum.mf.map.api.MapView;
import net.daum.mf.map.api.MapView.CurrentLocationEventListener;
import net.daum.mf.map.api.MapView.MapViewEventListener;
import net.daum.mf.map.api.MapView.OpenAPIKeyAuthenticationResultListener;
import net.daum.mf.map.api.MapView.POIItemEventListener;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

/**
 */
public class ExerciseRunActivity extends BaseActivity implements OpenAPIKeyAuthenticationResultListener, MapViewEventListener,
CurrentLocationEventListener, POIItemEventListener, OnClickListener, ReverseGeoCodingResultListener{
	private Context mContext;	
	
	private MapView mMapView;
	private MapReverseGeoCoder reverseGeoCoder = null;
	private final MapPolyline tracePath = new MapPolyline();
	private LocationManager locationManager;
	private Location mLocation = null;
	private ArrayList<MapPoint> pathList = new ArrayList<MapPoint>();	// 경로를 저장할 collection list
	private Button traceBtn;
	protected static boolean isStarted = false;	// 출발여부
	protected static boolean isEnded = false;	// 도착여부

	private TextView startPlaceTv;		// 시작 위치주소
	private TextView endPlaceTv;		// 도착 위치주소
	private TextView startTimeTv;		// 출발 시간
	private TextView endTimeTv;			// 도착 시간
	
	private boolean running = false;
	
	private float runDistance = 0;	// 이동거리체크
	
	// ui 처리를 위한 핸들러
	private final Handler mHandler = new Handler() {
    	@Override
		public void handleMessage(final Message msg) {

    	}
	};	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exercise_run_layout);
        getSharedPreferences(PREFERENCE, MODE_PRIVATE);
        // 컨택스트 얻기
        mContext = this;	
        
        // 레이아웃 초기화
        initLayout();        
        // 맵 초기화
        initMap();
        // 위치 초기화
        initLocation(); 
        
        // 화면이 슬립상태가 되지 않도록 한다.
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
  
    }

	private void initLayout() {
		traceBtn = (Button)findViewById(R.id.trace_btn);
        traceBtn.setClickable(false);	// 현재 위치를 찾기 전까지는 클릭 잠금

        //	 내위치 찾기 버튼
        ImageButton myLocBtn = (ImageButton)findViewById(R.id.loc_btn);

        myLocBtn.setOnClickListener(this);

        // 출발 주소
		startPlaceTv = (TextView)findViewById(R.id.start_place);
		// 도착 주소
		endPlaceTv = (TextView)findViewById(R.id.end_place);

		startTimeTv  = (TextView)findViewById(R.id.startTime);
		endTimeTv  = (TextView)findViewById(R.id.endTime);
	}

	/**
	 * 맵 키 설정 및 초기화 설정
	 */
	private void initMap() {
        mMapView = new MapView(this);
        mMapView.setDaumMapApiKey(MAP_KEY);
        mMapView.setOpenAPIKeyAuthenticationResultListener(this);
        mMapView.setMapViewEventListener(this);
        mMapView.setCurrentLocationEventListener(this);
        mMapView.setPOIItemEventListener(this);
        // 지도 타입 설정
        mMapView.setMapType(MapView.MapType.Standard);
        //	현위치를 표시하는 아이콘(마커)를 화면에 표시 안함
        mMapView.setShowCurrentLocationMarker(false);
        // 경로를 그려줄 색 설정
        tracePath.setLineColor(Color.argb(128, 255, 51, 0));
        ViewGroup parent = (ViewGroup)findViewById(R.id.map_parent);
        parent.addView(mMapView);
	}

	/**
	 * list에 저장된 좌표를 이용하여 경로를 그려준다.
	 */
	private void drawPath() {
		// 오전 모든 경로를 지운후 다시 그려준다.
		mMapView.removeAllPolylines();
		for(MapPoint p: pathList){
			tracePath.addPoint(p);
		}
		// 맵뷰에 붙여준다.
		mMapView.addPolyline(tracePath);
		// 경로가 다 보이도록 조정한다.
		mMapView.fitMapViewAreaToShowAllPolylines();
	}

	/**
	 * 위치정보의 보다 정확한 수신을 위해
	 * 이전 위치값과 비교한다.
	 * @param location
	 * @param currentBestLocation
	 * @return
	 */
	protected boolean isBetterLocation(final Location location, final Location currentBestLocation){
		if(currentBestLocation == null){
			// 잘못된 위치라도 없는것보다 좋다.
			return true;
		}

		// 기존 정보와 새로운 정보간의 시간 차이를 츨정한다.
		// 2분 이상 경과시 새로운 장소로 이동하였을 가능성이 크므로
		// 해당 정보를 채택하게 된다.
		long timeDelta = location.getTime() - currentBestLocation.getTime();
		boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;	// 새로운 위치정보여부
		boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
		boolean isNewer = timeDelta > 0;
		// 2분 이상 경가된 위치라면 true otherwise false
		if(isSignificantlyNewer){
			return true;
		}else if(isSignificantlyOlder){
			return false;
		}

		// 위치 정확성 차이
		// 위치정보사업자가 수 미터 거리 내로 정밀한 정보를 제공한다면
		// 그 정보는 정확하다고 판단할 수 있다 여기서는 200미터로 두었다.
		int accuracyDelta = (int)(location.getAccuracy() - currentBestLocation.getAccuracy());
		boolean isLessAccurate = accuracyDelta > 0;
		boolean isLMoreAccurate = accuracyDelta < 0;
		boolean isSignificantlyLessAccurate = accuracyDelta > ACCURATE_VALUE;

		// 앞에서와같이 상태 변화에 따라 위치정보사업자가 변동될 수 있다.
		// 같은 사업자라 한다면 더욱 정확한 위치정보를 제공해 줄 가능성이 높다고 판단할 수 있다.
		boolean isFormSameProvider = isSameProvider(location.getProvider(), currentBestLocation.getProvider());

		// 시간과 거리의 차이를 계산하여 위치의 정확도를 결정한다.
		if(isLMoreAccurate){
			return true;
		}else if(isNewer && !isLessAccurate){
			return true;
		}else if(isNewer && !isSignificantlyLessAccurate && isFormSameProvider){
			return true;
		}

		return false;

	}

	/**
	 * 같은 공급자인지 측정
	 * @param provider1
	 * @param provider2
	 * @return
	 */
	private boolean isSameProvider(final String provider1, final String provider2){
		if(provider1 == null){
			return provider2 == null;
		}
		return provider1.equals(provider2);

	}
	
	

	@Override
	protected void onResume() {
		super.onResume();
		moveToMyPlace();	// 현위치 찾기
        
	}

	@Override
	public void onClick(final View v) {
		switch(v.getId()){
		case R.id.trace_btn:	// 추적 버튼을 눌렀을 경우
			if(running == false){
				if(mLocation == null){	// 위치를 찾을수 없을 경우
					Toast.makeText(this, "현재 위치를 찾을수 없습니다.", Toast.LENGTH_SHORT).show();
					return;
				}
				
				isStarted = true;
				running = true;
				startTrace();

				((Button)v).setText("도착");
			}else{
				if(mLocation == null){
					return;
				}
				running = false;				
				endTrace();
				// 도착완료이면 클릭 버튼을 숨긴다.
				v.setVisibility(View.GONE);
				// 위치 리스너 제거
				locationManager.removeUpdates(loclistener);
				// 결과 다이얼로그 띄우기
				
				Toast.makeText(mContext, "runDistance : " + runDistance, Toast.LENGTH_LONG).show();
				
				/*
				intent = new Intent(this, ExersiceRunResultActivity.class);
				startActivity(intent);
				finish();
				*/
			}
			break;
		case R.id.loc_btn :	// 현재 위치 찾기
			if(mLocation != null){	// 현재 위치 정보가 있을때만
				MapPoint point =  MapPoint.mapPointWithGeoCoord(mLocation.getLatitude(), mLocation.getLongitude());
				mMapView.setMapCenterPoint(point, true);
			}else{
				// 네트워크로 공급자 변경후 다시 현재 위치 검색
				locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000L, 0, loclistener);
				moveToMyPlace();
				Toast.makeText(this, "현재 위치를 찾을수 없습니다.", Toast.LENGTH_SHORT).show();
			}
			break;
		}
	}
	
	
    /**
     * 단말기 현위치로 이동
     */
    private void moveToMyPlace() {
		try{
			MapPoint point =  MapPoint.mapPointWithGeoCoord(mLocation.getLatitude(), mLocation.getLongitude());
			mMapView.setMapCenterPoint(point, true);
		}catch(NullPointerException npe){
			// 네트워크로 공급자 변경 후 다시 현재 위치 검색 
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000L, 0, loclistener);
			// 마지막 네트워크 위치 수신값 받아온 후!
			Location tmpLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

			// 마지막 위치값이 없으면
			if(tmpLocation == null){
				// 오래된 위치값이면 위치값을 받아 올수 있게 1초를 기다린후 다시 위치 수신 실행!
				mHandler.postDelayed(new Runnable() {
					@Override
					public void run() {
						moveToMyPlace();	// 다시 현재 위치를 찾는다.
					}
				}, 1000);
				return;
			}

			// 이전 시간 비교
			long locationTime = System.currentTimeMillis() - tmpLocation.getTime();
			// 이전 위치값시간이 1분 이내면 위치값 사용
			if(locationTime < (1000 * 60 * 1) ){
				mLocation = tmpLocation;
				moveToMyPlace();	// 다시 현재 위치를 찾는다.
			}else{
				// 오래된 위치값이면 위치값을 받아 올수 있게 1초를 기다린후 다시 위치 수신 실행
				mHandler.postDelayed(new Runnable() {
					@Override
					public void run() {
						moveToMyPlace();	// 다시 현재 위치를 찾는다.
					}
				}, 1000);
			}
		}

	}	

	@Override
	protected void onDestroy() {
		running = false;
		// 위치 리스너 제거
		locationManager.removeUpdates(loclistener);		
		super.onDestroy();
	}



	/*
     * 수신 환경에 따라 LocationListener 를 설정한다.
     */
    private void initLocation(){
    	locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
    	//criteria 를 이용하여 적절한 위치 공급자를 이용한다.
    	Criteria criteria = new Criteria();
    	criteria.setAccuracy(Criteria.ACCURACY_FINE);// 정확도
    	criteria.setPowerRequirement(Criteria.POWER_HIGH); // 전원 소비량
    	criteria.setAltitudeRequired(false); // 고도 사용여부
    	criteria.setBearingRequired(false); //
    	criteria.setSpeedRequired(false); // 속도
    	criteria.setCostAllowed(true); // 금전적비용
    	String provider = LocationManager.GPS_PROVIDER;
    	//String provider = mLocationManager.getBestProvider(criteria, true);
		//location = mLocationManager.getLastKnownLocation(provider);
    	locationManager.requestLocationUpdates(provider, 0, 0, loclistener);//현재정보를 업데이트

		// gps 활성화 유무체크후 비활성화시  gps 환경설정으로 보냄
        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        		|| !locationManager.isProviderEnabled (LocationManager.NETWORK_PROVIDER) ) {
    		showLocationDialog();
        }
    }

	/**
	 * gps가 비활성화시 gps 환경설정 화면으로 이동여부 다이얼로그
	 */
	private void showLocationDialog() {
		new AlertDialog.Builder(this).setMessage(" 위치 무선 네트워크 사용  혹은 GPS가 비활성화 되어있습니다. 설정화면으로 이동 하시겠습니까?")
        .setCancelable(false).setTitle("알림")
        .setPositiveButton("이동",
                new DialogInterface.OnClickListener() {
                    @Override
					public void onClick(final DialogInterface dialog, final int id) {
                        Intent gpsOptionsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(gpsOptionsIntent);
                    }
            })
        .setNegativeButton("취소",
                new DialogInterface.OnClickListener() {
                    @Override
					public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
            }).show();
	}

    /**
     * 위치 수신 리스너
     */
    private final LocationListener loclistener = new LocationListener(){
        @Override
		public void onLocationChanged(final Location location) {	// 위치 변경시
        	
            mLocation = location;
			// 단말기 위치로 지도 이동처리
			// 추적 버튼 풀기
			traceBtn.setClickable(true);
			// 이벤트 리스너 달아주기
	        traceBtn.setOnClickListener(ExerciseRunActivity.this);
			MapPoint point =  MapPoint.mapPointWithGeoCoord(location.getLatitude(), location.getLongitude());
			mMapView.setMapCenterPoint(point, true);
			mLocation = location;	// 위치 받아오기
			if(running){	// 운동모드이면 경로 저장
				pathList.add(point);
				// 이전 경로와의 거리르 계산한다.
				runDistance += mLocation.distanceTo(location);
			}
        }

        /**
         * 위치제공자의 상태가 변경되는 경우 호출된다!
         */
        @Override
		public void onProviderDisabled(final String provider) {
        	if(running){
				Toast.makeText(ExerciseRunActivity.this, "GPS 위치수신이 원활하지 않습니다.", Toast.LENGTH_SHORT).show();
        	}
        }

        @Override
		public void onProviderEnabled(final String provider) {
        	if(running){
				Toast.makeText(ExerciseRunActivity.this, "GPS 위치수신이 정상적으로 동작합니다.", Toast.LENGTH_SHORT).show();
        	}
        }

        @Override
		public void onStatusChanged(final String provider, final int status, final Bundle extras) {
        }
    };

	/**
	 * 추적 시작시 출발 오버레이 아이템을 생성하고 맵뷰에 븉여준다.
	 */
	private void startTrace() {
		MapPoint point =  MapPoint.mapPointWithGeoCoord(mLocation.getLatitude(), mLocation.getLongitude());
		reverseGeoCoder = new MapReverseGeoCoder(DAUM_LOCAL_KEY, point, this, this);
		reverseGeoCoder.startFindingAddress();
		pathList.add(point);
		MapPOIItem item = new MapPOIItem();
		// poi 아이템 설정
		item.setTag(START_TAG);
		item.setItemName("출발");
		item.setMapPoint(point);
		item.setShowAnimationType(ShowAnimationType.SpringFromGround);
		item.setMarkerType(MarkerType.CustomImage);
		item.setCustomImageResourceId(R.drawable.custom_poi_marker_start);
		item.setCustomImageAnchorPointOffset(new MapPOIItem.ImageOffset(22,0));
		// 맵에 붙여준다.
		mMapView.addPOIItem(item);
		startTimeTv.setText("출발시간 : " + new SimpleDateFormat("hh시 mm분 ss초").format(new Date()));
	}

	/**
	 * 추적 종료시 종료 오버레이 아이템을 생성하고 맵뷰에 븉여준다.
	 */
	private void endTrace() {
		// 현재 위치를 얻고
		isEnded = true;
		MapPoint point =  MapPoint.mapPointWithGeoCoord(mLocation.getLatitude(), mLocation.getLongitude());
	
		pathList.add(point);

		reverseGeoCoder = new MapReverseGeoCoder(DAUM_LOCAL_KEY, point, this, this);
		reverseGeoCoder.startFindingAddress();

		MapPOIItem item = new MapPOIItem();
		// poi 아이템 설정
		item.setTag(END_TAG);
		item.setItemName("도착");
		item.setMapPoint(point);
		item.setShowAnimationType(ShowAnimationType.SpringFromGround);
		item.setMarkerType(MarkerType.CustomImage);
		item.setCustomImageResourceId(R.drawable.custom_poi_marker_end);
		item.setCustomImageAnchorPointOffset(new MapPOIItem.ImageOffset(22,0));
		// 맵에 붙여준다.
		mMapView.addPOIItem(item);
		// 경로 그려주기
		endTimeTv.setText("도착시간 : " + new SimpleDateFormat("hh시 mm분 ss초").format(new Date()));
		drawPath();
	}


	@Override
	public void onBackPressed() {	//  뒤로 가기버튼 클릭시 종료 여부
			AlertDialog.Builder ad = new AlertDialog.Builder(this);
			ad.setTitle("").setMessage("운동모드에서 나가시겠습니까?")
			.setPositiveButton("종료", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(final DialogInterface dialog, final int which) {
					finish();
				}
			}).setNegativeButton("취소",null).show();
	}

    /**
     * 옵션 메뉴 처리
     */
    @Override
    public boolean onCreateOptionsMenu(final Menu menu){
    	super.onCreateOptionsMenu(menu);
    	menu.add(0, 1, 0, "지도종류").setIcon(android.R.drawable.ic_menu_gallery);
    	menu.add(0, 2, 0, "나가기").setIcon(android.R.drawable.ic_menu_close_clear_cancel);
    	return true;
    }


    /**
     * 옵션 메뉴 선택에 따라 해당 처리를 해줌
     */
    @Override
    public boolean onOptionsItemSelected(final MenuItem item){
    	switch(item.getItemId()){
    		case 1:
    			String[] mapTypeMenuItems = { "일반지도", "위성지도", "하이브리드"};

    			Builder dialog = new AlertDialog.Builder(this);
    			dialog.setTitle("지도 종류선택");
    			dialog.setItems(mapTypeMenuItems, new DialogInterface.OnClickListener() {
    				@Override
					public void onClick(final DialogInterface dialog, final int which) {
    					switch (which) {
    					case 0: // Standard
    						mMapView.setMapType(MapView.MapType.Standard);
    						break;
    					case 1: // Satellite
    						mMapView.setMapType(MapView.MapType.Satellite);
    						break;
    					case 2: // Hybrid
    						mMapView.setMapType(MapView.MapType.Hybrid);
    						break;
    					}
    				}

    			}).show();
    			return true;
    		case 2:
    			finishDialog(this);
    			return true;
    	}
    	return false;
    }


	@Override
	public void onReverseGeoCoderFoundAddress(final MapReverseGeoCoder rGeoCoder, final String addressString) {
		// 시작 poiitem 을 찾는다.
		if(isEnded == false){
			MapPOIItem item = mMapView.findPOIItemByTag(START_TAG);
			try{
				item.setItemName(addressString);
			}catch(NullPointerException npe){}
			mMapView.addPOIItem(item);
			startPlaceTv.setText("출발 위치 : " + addressString);
		}else{
			MapPOIItem item = mMapView.findPOIItemByTag(END_TAG);
			try{
				item.setItemName(addressString);
			}catch(NullPointerException npe){}			
			mMapView.addPOIItem(item);
			endPlaceTv.setText("도착 위치 : " + addressString);
		}

		reverseGeoCoder = null;
	}


	@Override
	public void onReverseGeoCoderFailedToFindAddress(final MapReverseGeoCoder rGeoCoder) {
		reverseGeoCoder = null;
	}

	@Override
	public void onCalloutBalloonOfPOIItemTouched(final MapView arg0, final MapPOIItem arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDraggablePOIItemMoved(final MapView arg0, final MapPOIItem arg1,
			final MapPoint arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPOIItemSelected(final MapView arg0, final MapPOIItem arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onCurrentLocationDeviceHeadingUpdate(final MapView arg0, final float arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onCurrentLocationUpdate(final MapView arg0, final MapPoint arg1, final float arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onCurrentLocationUpdateCancelled(final MapView arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onCurrentLocationUpdateFailed(final MapView arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMapViewCenterPointMoved(final MapView arg0, final MapPoint arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMapViewDoubleTapped(final MapView arg0, final MapPoint arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMapViewLongPressed(final MapView arg0, final MapPoint arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMapViewSingleTapped(final MapView arg0, final MapPoint arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMapViewZoomLevelChanged(final MapView arg0, final int arg1) {
		// TODO Auto-generated method stub

	}

	/**
	 * 키 인증 처리
	 */
	@Override
	public void onDaumMapOpenAPIKeyAuthenticationResult(final MapView v, final int arg1,
			final String arg2) {
	}	

}
