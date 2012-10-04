package kr.co.diet.map;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import kr.co.diet.DbHelper;
import kr.co.diet.R;
import kr.co.diet.dao.PathPoint;
import kr.co.diet.dao.RunningData;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SubMenu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.nhn.android.maps.NMapActivity;
import com.nhn.android.maps.NMapCompassManager;
import com.nhn.android.maps.NMapController;
import com.nhn.android.maps.NMapLocationManager;
import com.nhn.android.maps.NMapOverlay;
import com.nhn.android.maps.NMapOverlayItem;
import com.nhn.android.maps.NMapView;
import com.nhn.android.maps.maplib.NGeoPoint;
import com.nhn.android.maps.nmapmodel.NMapError;
import com.nhn.android.maps.nmapmodel.NMapPlacemark;
import com.nhn.android.maps.overlay.NMapPOIdata;
import com.nhn.android.maps.overlay.NMapPOIitem;
import com.nhn.android.maps.overlay.NMapPathData;
import com.nhn.android.maps.overlay.NMapPathLineStyle;
import com.nhn.android.mapviewer.overlay.NMapCalloutOverlay;
import com.nhn.android.mapviewer.overlay.NMapMyLocationOverlay;
import com.nhn.android.mapviewer.overlay.NMapOverlayManager;
import com.nhn.android.mapviewer.overlay.NMapOverlayManager.OnCalloutOverlayListener;
import com.nhn.android.mapviewer.overlay.NMapPOIdataOverlay;

/**
 * Naver open api 를 이용하여 지도를 보여준다. 위치 로깅 내역을 오버레이 아이콘으로 나타낸다.
 */
public class ExerciseResultMapActivity extends NMapActivity {
	// naver open api key
	private static final String API_KEY = "fae49f1c854f7d48285204deaa8dfd9d";
	private static final String LOG_TAG = "NMapViewer";
	// mapview 설정
	private NMapView mMapView = null;
	private NMapController mMapController;
	private NMapMyLocationOverlay mMyLocationOverlay;
	private NMapOverlayManager mOverlayManager;
	private NMapLocationManager mMapLocationManager;
	private NMapCompassManager mMapCompassManager;
	private NMapViewerResourceProvider mMapViewerResourceProvider;
	private final ArrayList<String> addressList = new ArrayList<String>(); // 주소를 저장할 리스트
	
	// ui
	private TextView startTimeTv;		// 출발 시각
	private TextView endTimeTv;			// 도착 시각
	private TextView startPlaceTv;		// 출발 위치
	private TextView endPlaceTv;		// 도착 위치
	private TextView distanceTv;		// 운동거리

	// 운동정보Dao
	private RunningData runningData = new RunningData();


	/** Called when the activity is first created. */
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.map_layout);

		/* mapView 설정 */
		mMapView = (NMapView) findViewById(R.id.mapView);
		mMapView.setApiKey(API_KEY); // 키 설정
		mMapView.setClickable(true); // 클릭 설정

		// register listener for map state changes
		mMapView.setOnMapStateChangeListener(onMapViewStateChangeListener);
		mMapView.setOnMapViewTouchEventListener(onMapViewTouchEventListener);

		// use map controller to zoom in/out, pan and set map center, zoom level
		// etc.
		mMapController = mMapView.getMapController(); // 컨트롤 얻어오기
		mMapController.setMapViewBicycleMode(!mMapController
				.getMapViewBicycleMode());
		// use built in zoom controls
		/*
		NMapView.LayoutParams lp = new NMapView.LayoutParams(
				NMapView.LayoutParams.WRAP_CONTENT,
				NMapView.LayoutParams.WRAP_CONTENT,
				NMapView.LayoutParams.TOP_LEFT);
		mMapView.setBuiltInZoomControls(true, lp);
		*/
		
		// create resource provider
		mMapViewerResourceProvider = new NMapViewerResourceProvider(this);
		// create overlay manager
		mOverlayManager = new NMapOverlayManager(this, mMapView,
				mMapViewerResourceProvider);
		// register callout overlay listener to customize it.
		mOverlayManager
				.setOnCalloutOverlayListener(new OnCalloutOverlayListener() {

					@Override
					public NMapCalloutOverlay onCreateCalloutOverlay(
							final NMapOverlay itemOverlay,
							final NMapOverlayItem overlayItem,
							final Rect itemBounds) {
						// set your callout overlay

						return new NMapCalloutBasicOverlay(itemOverlay,
								overlayItem, itemBounds);
					}

				});

		// location manager
		mMapLocationManager = new NMapLocationManager(this);
		mMapLocationManager
				.setOnLocationChangeListener(onMyLocationChangeListener);

		// compass manager
		mMapCompassManager = new NMapCompassManager(this);

		// create my location overlay
		mMyLocationOverlay = mOverlayManager.createMyLocationOverlay(
				mMapLocationManager, mMapCompassManager);

		// set data provider listener
		// 지도 라이브러리에서 제공하는 서버 API 호출 시 응답에 대한 콜백 인터페이스
		super.setMapDataProviderListener(new NMapActivity.OnDataProviderListener() {
			@Override
			public void onReverseGeocoderResponse(final NMapPlacemark arg0,
					final NMapError arg1) {
				// TODO Auto-generated method stub
			}
		});

		initLayout();				// 레이아웃 초기화
	}

	/**
	 * 레이아웃 초기화
	 */
	private void initLayout() {
		startPlaceTv = (TextView)findViewById(R.id.start_place);
		endPlaceTv = (TextView)findViewById(R.id.end_place);
		distanceTv = (TextView)findViewById(R.id.distance);
		startTimeTv = (TextView)findViewById(R.id.startTime);
		endTimeTv  = (TextView)findViewById(R.id.endTime);	
		final Button  traceBtn = (Button) findViewById(R.id.trace_btn);		
		traceBtn.setVisibility(View.GONE);
		
		// 내 위치로 이동 버튼 처리
		ImageButton locBtn = (ImageButton) findViewById(R.id.loc_btn);
		locBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				//  내 위치 찾기
				startMyLocation();
			}
		});

		
		// 줌 인아웃 처리
		Button zoomInBtn = (Button)findViewById(R.id.zoom_in);
		zoomInBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mMapController.zoomIn();
			}
		});
		Button zoomOutBtn = (Button)findViewById(R.id.zoom_out);
		zoomOutBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mMapController.zoomOut();
			}
		});		
	}

	
	/**
	 * 운동 경로 불러오기
	 */
	private void loadExercisePath(){
		Intent intent = getIntent();
		int index = intent.getIntExtra("index", -1);
		int cal = intent.getIntExtra("cal", 0);
		distanceTv.setText("이동거리 : " + intent.getStringExtra("distance") + "m (" + cal + "Kcal)");
		DbHelper db = new DbHelper(this);
		ArrayList<PathPoint> list = db.selectExercusePath(index);
		// 총 이동 좌표 갯수
		int totalSize = list.size();
		// set path data points
		NMapPathData pathData = new NMapPathData(totalSize);
		pathData.initPathData();	
		PathPoint p;
		NMapPOIdata poiData = new NMapPOIdata(1, mMapViewerResourceProvider);
		poiData.beginPOIdata(2);	
		
		for(int i=0;  i<totalSize; i++){
			p = list.get(i);
			pathData.addPathPoint(p.getLongitude(), p.getLatitude(), NMapPathLineStyle.TYPE_SOLID);
			// 출발위치
			if(i == 0){
				// 출발 위치로 지도 이동
				mMapController.animateTo(new NGeoPoint(p.getLongitude(), p.getLatitude()));	
				poiData.addPOIitem(p.getLongitude(), p.getLatitude(), "", NMapPOIflagType.FROM, null);
				String startPlace = getAddressStr(p.getLatitude(), p.getLongitude());				
				startPlaceTv.setText("출발 : " + startPlace);
				Date date = new Date(Long.valueOf(p.getDate()));
				startTimeTv.setText("출발시각 : " + new SimpleDateFormat("k시 m분 s초").format(date));						
			// 도착위치	
			}else if(i == totalSize -1){
				poiData.addPOIitem(p.getLongitude(), p.getLatitude(), "", NMapPOIflagType.TO, null);
				String endPlace = getAddressStr(p.getLatitude(), p.getLongitude());
				runningData.setEndPlace(endPlace);
				endPlaceTv.setText("도착 : " +endPlace);	
				Date date = new Date(Long.valueOf(p.getDate()));
				endTimeTv.setText("도착시각 : " + new SimpleDateFormat("k시 m분 s초").format(date));					
				poiData.endPOIdata();
				mOverlayManager.createPOIdataOverlay(poiData, null);				
			}			
		}


		pathData.endPathData();
		mOverlayManager.createPathDataOverlay(pathData);			

		db.close();
	}

	/* MyLocation Listener */
	private final NMapLocationManager.OnLocationChangeListener onMyLocationChangeListener = new NMapLocationManager.OnLocationChangeListener() {

		@Override
		public boolean onLocationChanged(
				final NMapLocationManager locationManager,
				final NGeoPoint myLocation) {

			if (mMapController != null) {
				mMapController.animateTo(myLocation);
			}

			return true;
		}

		@Override
		public void onLocationUpdateTimeout(
				final NMapLocationManager locationManager) {
			Toast.makeText(ExerciseResultMapActivity.this, "현재 위치를 얻어올수 없습니다.",
					Toast.LENGTH_LONG).show();
		}

		@Override
		public void onLocationUnavailableArea(final NMapLocationManager arg0,
				final NGeoPoint arg1) {
			// TODO Auto-generated method stub

		}

	};

	private final NMapView.OnMapStateChangeListener onMapViewStateChangeListener = new NMapView.OnMapStateChangeListener() {

		@Override
		public void onAnimationStateChange(final NMapView arg0, final int arg1,
				final int arg2) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onMapCenterChange(final NMapView arg0, final NGeoPoint arg1) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onMapCenterChangeFine(final NMapView arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onMapInitHandler(final NMapView nv, final NMapError ne) {
			loadExercisePath();			// 운동 경로 정보 가져오기
			
		}

		@Override
		public void onZoomLevelChange(final NMapView arg0, final int arg1) {
			// TODO Auto-generated method stub

		}
	};

	private final NMapView.OnMapViewTouchEventListener onMapViewTouchEventListener = new NMapView.OnMapViewTouchEventListener() {

		@Override
		public void onLongPress(final NMapView arg0, final MotionEvent arg1) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onLongPressCanceled(final NMapView arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onScroll(final NMapView arg0, final MotionEvent arg1,
				final MotionEvent arg2) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onSingleTapUp(final NMapView arg0, final MotionEvent arg1) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onTouchDown(final NMapView arg0, final MotionEvent arg1) {
			// TODO Auto-generated method stub

		}
	};

	/* POI data State Change Listener */
	private final NMapPOIdataOverlay.OnStateChangeListener onPOIdataStateChangeListener = new NMapPOIdataOverlay.OnStateChangeListener() {
		/**
		 * 오버레이 아이콘을 클릭시 로깅메시지를 띄운다.
		 */
		@Override
		public void onCalloutClick(final NMapPOIdataOverlay poiDataOverlay,
				final NMapPOIitem item) {
			Log.i(LOG_TAG,
					"onCalloutClick: title=" + addressList.get(item.getId()));
			// 로깅 메시지 를 보여준다.
			Toast.makeText(ExerciseResultMapActivity.this, addressList.get(item.getId()),
					Toast.LENGTH_LONG).show();
		}

		@Override
		public void onFocusChanged(final NMapPOIdataOverlay poiDataOverlay,
				final NMapPOIitem item) {
			if (item != null) {
				Log.i(LOG_TAG, "onFocusChanged: " + item.toString());
			} else {
				Log.i(LOG_TAG, "onFocusChanged: ");
			}
		}
	};

	/* Menus */
	private static final int MENU_ITEM_CLEAR_MAP = 10;
	private static final int MENU_ITEM_MAP_MODE = 20;
	private static final int MENU_ITEM_MAP_MODE_SUB_VECTOR = MENU_ITEM_MAP_MODE + 1;
	private static final int MENU_ITEM_MAP_MODE_SUB_SATELLITE = MENU_ITEM_MAP_MODE + 2;
	private static final int MENU_ITEM_MAP_MODE_SUB_HYBRID = MENU_ITEM_MAP_MODE + 3;
	private static final int MENU_ITEM_MAP_MODE_SUB_TRAFFIC = MENU_ITEM_MAP_MODE + 4;
	private static final int MENU_ITEM_MAP_MODE_SUB_BICYCLE = MENU_ITEM_MAP_MODE + 5;
	private static final int MENU_ITEM_ZOOM_CONTROLS = 30;
	private static final int MENU_ITEM_MY_LOCATION = 40;

	/**
	 * Invoked during init to give the Activity a chance to set up its Menu.
	 * 
	 * @param menu
	 *            the Menu to which entries may be added
	 * @return true
	 */
	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		super.onCreateOptionsMenu(menu);

		MenuItem menuItem = null;
		SubMenu subMenu = null;

		menuItem = menu.add(Menu.NONE, MENU_ITEM_CLEAR_MAP,
				Menu.CATEGORY_SECONDARY, "맵 초기화");
		menuItem.setAlphabeticShortcut('c');
		// menuItem.setIcon(android.R.drawable.ic_menu_revert);

		subMenu = menu.addSubMenu(Menu.NONE, MENU_ITEM_MAP_MODE,
				Menu.CATEGORY_SECONDARY, "지도 모드");
		// subMenu.setIcon(android.R.drawable.ic_menu_mapmode);

		menuItem = subMenu.add(0, MENU_ITEM_MAP_MODE_SUB_VECTOR, Menu.NONE,
				"일반모드");
		menuItem.setAlphabeticShortcut('m');
		menuItem.setCheckable(true);
		menuItem.setChecked(false);

		menuItem = subMenu.add(0, MENU_ITEM_MAP_MODE_SUB_SATELLITE, Menu.NONE,
				"위성모드");
		menuItem.setAlphabeticShortcut('s');
		menuItem.setCheckable(true);
		menuItem.setChecked(false);

		menuItem = subMenu.add(0, MENU_ITEM_MAP_MODE_SUB_HYBRID, Menu.NONE,
				"혼합모드");
		menuItem.setAlphabeticShortcut('h');
		menuItem.setCheckable(true);
		menuItem.setChecked(false);

		menuItem = subMenu.add(0, MENU_ITEM_MAP_MODE_SUB_TRAFFIC, Menu.NONE,
				"교통");
		menuItem.setAlphabeticShortcut('t');
		menuItem.setCheckable(true);
		menuItem.setChecked(false);

		menuItem = subMenu.add(0, MENU_ITEM_MAP_MODE_SUB_BICYCLE, Menu.NONE,
				"자전거");
		menuItem.setAlphabeticShortcut('b');
		menuItem.setCheckable(true);
		menuItem.setChecked(false);

		menuItem = menu.add(0, MENU_ITEM_ZOOM_CONTROLS,
				Menu.CATEGORY_SECONDARY, "줌 컨트롤");
		menuItem.setAlphabeticShortcut('z');
		// menuItem.setIcon(android.R.drawable.ic_menu_zoom);

		menuItem = menu.add(0, MENU_ITEM_MY_LOCATION, Menu.CATEGORY_SECONDARY,
				"내위치 찾기");
		menuItem.setAlphabeticShortcut('l');
		// menuItem.setIcon(android.R.drawable.ic_menu_mylocation);

		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(final Menu pMenu) {
		super.onPrepareOptionsMenu(pMenu);

		int viewMode = mMapController.getMapViewMode();

		pMenu.findItem(MENU_ITEM_CLEAR_MAP).setEnabled(
				(viewMode != NMapView.VIEW_MODE_VECTOR)
						|| mOverlayManager.sizeofOverlays() > 0);
		pMenu.findItem(MENU_ITEM_MAP_MODE_SUB_VECTOR).setChecked(
				viewMode == NMapView.VIEW_MODE_VECTOR);
		pMenu.findItem(MENU_ITEM_MAP_MODE_SUB_SATELLITE).setChecked(
				viewMode == NMapView.VIEW_MODE_SATELLITE);
		pMenu.findItem(MENU_ITEM_MAP_MODE_SUB_HYBRID).setChecked(
				viewMode == NMapView.VIEW_MODE_HYBRID);

		if (mMyLocationOverlay == null) {
			pMenu.findItem(MENU_ITEM_MY_LOCATION).setEnabled(false);
		}

		return true;
	}

	/**
	 * Invoked when the user selects an item from the Menu.
	 * 
	 * @param item
	 *            the Menu entry which was selected
	 * @return true if the Menu item was legit (and we consumed it), false
	 *         otherwise
	 */
	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {

		switch (item.getItemId()) {
		case MENU_ITEM_CLEAR_MAP: // 맵 초기화
			if (mMyLocationOverlay != null) {
				stopMyLocation();
				mOverlayManager.removeOverlay(mMyLocationOverlay);
			}

			mMapController.setMapViewMode(NMapView.VIEW_MODE_VECTOR);
			mMapController.setMapViewTrafficMode(false);
			mMapController.setMapViewBicycleMode(false);

			return true;

		case MENU_ITEM_MAP_MODE_SUB_VECTOR:
			mMapController.setMapViewMode(NMapView.VIEW_MODE_VECTOR);
			return true;

		case MENU_ITEM_MAP_MODE_SUB_SATELLITE:
			mMapController.setMapViewMode(NMapView.VIEW_MODE_SATELLITE);
			return true;

		case MENU_ITEM_MAP_MODE_SUB_HYBRID:
			mMapController.setMapViewMode(NMapView.VIEW_MODE_HYBRID);
			return true;

		case MENU_ITEM_MAP_MODE_SUB_TRAFFIC:
			mMapController.setMapViewTrafficMode(!mMapController
					.getMapViewTrafficMode());
			return true;

		case MENU_ITEM_MAP_MODE_SUB_BICYCLE:
			mMapController.setMapViewBicycleMode(!mMapController
					.getMapViewBicycleMode());
			return true;

		case MENU_ITEM_ZOOM_CONTROLS:
			mMapView.displayZoomControls(true);
			return true;

		case MENU_ITEM_MY_LOCATION:
			startMyLocation();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	/**
	 * 내위치 찾기
	 */
	private void startMyLocation() {

		if (mMyLocationOverlay != null) {
			if (!mOverlayManager.hasOverlay(mMyLocationOverlay)) {
				mOverlayManager.addOverlay(mMyLocationOverlay);
			}

			if (mMapLocationManager.isMyLocationEnabled()) {

				if (!mMapView.isAutoRotateEnabled()) {
					mMyLocationOverlay.setCompassHeadingVisible(true);

					mMapCompassManager.enableCompass();

					mMapView.setAutoRotateEnabled(true, false);

					mMapView.requestLayout();
				} else {
					stopMyLocation();
				}

				mMapView.postInvalidate();
			} else {
				boolean isMyLocationEnabled = mMapLocationManager
						.enableMyLocation(false);
				if (!isMyLocationEnabled) {
					Toast.makeText(
							ExerciseResultMapActivity.this,
							"Please enable a My Location source in system settings",
							Toast.LENGTH_LONG).show();

					Intent goToSettings = new Intent(
							Settings.ACTION_LOCATION_SOURCE_SETTINGS);
					startActivity(goToSettings);

					return;
				}
			}
		}
	}

	/**
	 * 내 위치 찾기 중단
	 */
	private void stopMyLocation() {
		if (mMyLocationOverlay != null) {
			mMapLocationManager.disableMyLocation();

			if (mMapView.isAutoRotateEnabled()) {
				mMyLocationOverlay.setCompassHeadingVisible(false);

				mMapCompassManager.disableCompass();

				mMapView.setAutoRotateEnabled(false, false);

				mMapView.requestLayout();
			}
		}
	}

	
	/**
	 * 	좌표를 이용하여 주소가져오기
	 * @return
	 * 	주소명
	 * @throws IOException
	 */
	private String getAddressStr(final double lat, final double lng) {
		Geocoder gc = new Geocoder(this,Locale.getDefault());
		List<Address> addresses;
		String addressStr = "현위치";
		try {
			addresses = gc.getFromLocation(lat, lng, 1);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return addressStr;
		}
		if(addresses.size()>0) {	// 주소가 있으면
			// 첫번째 주소 컬렉션을 얻은후
			Address address = addresses.get(0);
			// 실제 주소만 가져온다.
			addressStr = address.getAddressLine(0).replace("대한민국", "").trim();
		}
		return addressStr;
	}
	
}
