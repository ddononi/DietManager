package kr.co.diet.map;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import kr.co.diet.ConstantActivity;
import kr.co.diet.DbHelper;
import kr.co.diet.R;
import kr.co.diet.dao.RunningData;
import kr.co.diet.utils.DietUtils;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.nhn.android.mapviewer.overlay.NMapPathDataOverlay;

/**
 * Naver open api �� �̿��Ͽ� ������ �����ش�. ��ġ �α� ������ �������� ���������� ��Ÿ����.
 */
public class MapActivity extends NMapActivity {
	// naver open api key
	private static final String API_KEY = "fae49f1c854f7d48285204deaa8dfd9d";
	private static final int MAP_LEVEL = 10;
	private static final String LOG_TAG = "NMapViewer";
	// mapview ����
	private NMapView mMapView = null;
	private NMapController mMapController;
	private NMapMyLocationOverlay mMyLocationOverlay;
	private NMapOverlayManager mOverlayManager;
	private NMapLocationManager mMapLocationManager;
	private NMapCompassManager mMapCompassManager;
	private NMapViewerResourceProvider mMapViewerResourceProvider;
	private final ArrayList<String> addressList = new ArrayList<String>(); // �ּҸ� ������ ����Ʈ
	
	// ui
	private TextView startTimeTv;		// ��� �ð�
	private TextView endTimeTv;			// ���� �ð�
	private TextView startPlaceTv;		// ��� ��ġ
	private TextView endPlaceTv;		// ���� ��ġ
	private TextView distanceTv;		// ��Ÿ�
	private NMapPOIdataOverlay myPlaceOveray;	// ����ġ ��������	
	private NMapPathDataOverlay pathOveray;		// ��� ��������
	// location
	private Location mLocation;
	private LocationManager mLocationManager;
	// trace 
	private boolean isStarted = false;
	private double beforeLat = -1;	// ���� �����Ÿ�
	private double beforeLon = -1;	// ���� �浵 �Ÿ�	
	private int totalDistance = 0;	// �� �̵��Ÿ�
	private int mWeight = 0;		// �� ������
	// ���� ��ǥ �÷���
	private ArrayList<NGeoPoint> traceList = new ArrayList<NGeoPoint>();
	// �����Dao
	private RunningData runningData = new RunningData();
	// ����ȯ�漳��
	private SharedPreferences pref;
	private final Handler mHandler = new Handler();

	/** Called when the activity is first created. */
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.map_layout);

		/* mapView ���� */
		mMapView = (NMapView) findViewById(R.id.mapView);
		mMapView.setApiKey(API_KEY); // Ű ����
		mMapView.setClickable(true); // Ŭ�� ����

		// register listener for map state changes
		mMapView.setOnMapStateChangeListener(onMapViewStateChangeListener);
		mMapView.setOnMapViewTouchEventListener(onMapViewTouchEventListener);

		// use map controller to zoom in/out, pan and set map center, zoom level
		// etc.
		mMapController = mMapView.getMapController(); // ��Ʈ�� ������
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
		// ���� ���̺귯������ �����ϴ� ���� API ȣ�� �� ���信 ���� �ݹ� �������̽�
		super.setMapDataProviderListener(new NMapActivity.OnDataProviderListener() {
			@Override
			public void onReverseGeocoderResponse(final NMapPlacemark arg0,
					final NMapError arg1) {
				// TODO Auto-generated method stub
			}
		});

		initLayout();				// ���̾ƿ� �ʱ�ȭ
		initLocationListener();	// ��ġ ���� ����
		
		moveToMyPlace();		// ���� ��ġ�� �̵�
		
		SharedPreferences pref = getSharedPreferences(ConstantActivity.PREFERENCE, MODE_PRIVATE);
		mWeight = Integer.valueOf(pref.getString("weight", "50"));
	}

	/**
	 * ���̾ƿ� �ʱ�ȭ
	 */
	private void initLayout() {
		startPlaceTv = (TextView)findViewById(R.id.start_place);
		endPlaceTv = (TextView)findViewById(R.id.end_place);
		distanceTv = (TextView)findViewById(R.id.distance);
		startTimeTv = (TextView)findViewById(R.id.startTime);
		endTimeTv  = (TextView)findViewById(R.id.endTime);
		// �� ��ġ�� �̵� ��ư ó��
		ImageButton locBtn = (ImageButton) findViewById(R.id.loc_btn);
		locBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
					// ������ ���� ��ġ�� �̵�
					moveToMyPlace();
			}
		});
		// ��� or ���� ��ư ó��
		final Button  traceBtn = (Button) findViewById(R.id.trace_btn);
		// ����ġ�� ã�������� ��߹�ư�� ��ٴ�.
		traceBtn.setEnabled(false);
		traceBtn.setClickable(false);		
		traceBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				doTrace(traceBtn);				
			}
		});
		
		// �� �ξƿ� ó��
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
	
	/*
	 * ���� ȯ�濡 ���� LocationListener �� �����Ѵ�.
	 */
	private void initLocationListener() {
		mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		// criteria �� �̿��Ͽ� ������ ��ġ �����ڸ� �̿��Ѵ�.
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);// ��Ȯ��
		criteria.setPowerRequirement(Criteria.POWER_HIGH); // ���� �Һ�
		criteria.setAltitudeRequired(false); // �� ��뿩��
		criteria.setBearingRequired(false); //
		criteria.setSpeedRequired(false); // �ӵ�
		criteria.setCostAllowed(true); // ���������
		//String provider = LocationManager.GPS_PROVIDER;
		 String provider = mLocationManager.getBestProvider(criteria, true);
		// location = mLocationManager.getLastKnownLocation(provider);
		mLocationManager.requestLocationUpdates(provider, 60000L, 0, loclistener);// ���������� ������Ʈ
		mLocationManager.getLastKnownLocation(provider);		

		// gps Ȱ��ȭ ����üũ�� ��Ȱ��ȭ�� gps ȯ�漳������ ����
		if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
				|| !mLocationManager
						.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
			showLocationDialog();
		}
	}	
	

	/**
	 * ���, ������������ ó�� �� �̵���� ó��
	 * @param traceBtn
	 */
	private void doTrace(final Button traceBtn) {
		// set POI data
		NMapPOIdata poiData = new NMapPOIdata(1, mMapViewerResourceProvider);
		poiData.beginPOIdata(1);				
		// ��� Ȥ�� ������ �������� �������� �����ش�.
		if(TextUtils.equals(traceBtn.getText(), "���")  ){
			// ���� �Ÿ� ����
			beforeLat = mLocation.getLatitude();
			beforeLon = mLocation.getLongitude();	
			
			poiData.addPOIitem(mLocation.getLongitude(), mLocation.getLatitude(), "", NMapPOIflagType.FROM, null);
			traceBtn.setText("����");
			isStarted = true;
			String startPlace = getAddressStr(mLocation.getLatitude(), mLocation.getLongitude());
			runningData.setStartPlace(startPlace);			
			startPlaceTv.setText("��� : " + startPlace);
			startTimeTv.setText("��߽ð� : " +new SimpleDateFormat("k�� m�� s��").format(new Date()));
	
		}else{		// ������ ��ư ����
			isStarted = false;
			poiData.addPOIitem(mLocation.getLongitude(), mLocation.getLatitude(), "", NMapPOIflagType.TO, null);
			traceBtn.setVisibility(View.GONE);
			String endPlace = getAddressStr(mLocation.getLatitude(), mLocation.getLongitude());
			runningData.setEndPlace(endPlace);
			endPlaceTv.setText("���� : " +endPlace);		
			endTimeTv.setText("�����ð� : " + new SimpleDateFormat("k�� m�� s��").format(new Date()));			
			 
			// �̵��Ÿ� ��� ó��
			saveTrace();
		}
		poiData.endPOIdata();
		mOverlayManager.createPOIdataOverlay(poiData, null);
		
		traceList.add(new NGeoPoint(mLocation.getLongitude(), mLocation.getLatitude()));
		// set path data points
		NMapPathData pathData = new NMapPathData(traceList.size());
		pathData.initPathData();
		pathData.initPathData();				
		for(NGeoPoint p : traceList){
			pathData.addPathPoint(p.getLongitude(), p.getLatitude(), NMapPathLineStyle.TYPE_SOLID);
		}
		pathData.endPathData();
		mOverlayManager.createPathDataOverlay(pathData);
	}	
	
	/**
	 * �̵� ���� ó��
	 */
	private void saveTrace(){
		DbHelper db = new DbHelper(this);
		// � ���� ���
		runningData.setDistance(String.valueOf(totalDistance));
		runningData.setCal(DietUtils.calcurateCal(totalDistance, mWeight));		
		db.insertRunning(runningData);
		// ���� �ε��� ���
		int index = db.getLastRunningIndex();
		// �� �̵� ��ǥ ����
		int totalSize = traceList.size();
		String flag;
		for(int i=0;  i<totalSize; i++){
			flag = "";
			NGeoPoint p = traceList.get(i);
			// ���, ���� ����
			if(i == 0){
				flag = "start";
			}else if(i == totalSize-1){
				flag = "end";
			}
			db.insertTraceCoord(index, p.getLatitude(), p.getLongitude(), flag);			
		}

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
			Toast.makeText(MapActivity.this, "���� ��ġ�� ���ü� �����ϴ�.",
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
	    	pref = getSharedPreferences(ConstantActivity.DIET_PREF, Context.MODE_PRIVATE);

	    	// ����� ��ǥ�� ������ �����ϰ� ������ ����Ʈ�� ������ ��ǥ�� �����´�.
			String lastLat = pref.getString(ConstantActivity.LAST_LAT, ConstantActivity.DEFAULT_LAT);
			String lastLng = pref.getString(ConstantActivity.LAST_LNG, ConstantActivity.DEFAULT_LNG);			
			NGeoPoint point = new NGeoPoint(Double.valueOf(lastLng), Double.valueOf(lastLat));
			mMapController.setMapCenter(point, 10);
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
		 * �������� �������� Ŭ���� �α�޽����� ����.
		 */
		@Override
		public void onCalloutClick(final NMapPOIdataOverlay poiDataOverlay,
				final NMapPOIitem item) {
			Log.i(LOG_TAG,
					"onCalloutClick: title=" + addressList.get(item.getId()));
			// �α� �޽��� �� �����ش�.
			Toast.makeText(MapActivity.this, addressList.get(item.getId()),
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
				Menu.CATEGORY_SECONDARY, "�� �ʱ�ȭ");
		menuItem.setAlphabeticShortcut('c');
		// menuItem.setIcon(android.R.drawable.ic_menu_revert);

		subMenu = menu.addSubMenu(Menu.NONE, MENU_ITEM_MAP_MODE,
				Menu.CATEGORY_SECONDARY, "���� ���");
		// subMenu.setIcon(android.R.drawable.ic_menu_mapmode);

		menuItem = subMenu.add(0, MENU_ITEM_MAP_MODE_SUB_VECTOR, Menu.NONE,
				"�Ϲݸ��");
		menuItem.setAlphabeticShortcut('m');
		menuItem.setCheckable(true);
		menuItem.setChecked(false);

		menuItem = subMenu.add(0, MENU_ITEM_MAP_MODE_SUB_SATELLITE, Menu.NONE,
				"�������");
		menuItem.setAlphabeticShortcut('s');
		menuItem.setCheckable(true);
		menuItem.setChecked(false);

		menuItem = subMenu.add(0, MENU_ITEM_MAP_MODE_SUB_HYBRID, Menu.NONE,
				"ȥ�ո��");
		menuItem.setAlphabeticShortcut('h');
		menuItem.setCheckable(true);
		menuItem.setChecked(false);

		menuItem = subMenu.add(0, MENU_ITEM_MAP_MODE_SUB_TRAFFIC, Menu.NONE,
				"����");
		menuItem.setAlphabeticShortcut('t');
		menuItem.setCheckable(true);
		menuItem.setChecked(false);

		menuItem = subMenu.add(0, MENU_ITEM_MAP_MODE_SUB_BICYCLE, Menu.NONE,
				"������");
		menuItem.setAlphabeticShortcut('b');
		menuItem.setCheckable(true);
		menuItem.setChecked(false);

		menuItem = menu.add(0, MENU_ITEM_ZOOM_CONTROLS,
				Menu.CATEGORY_SECONDARY, "�� ��Ʈ��");
		menuItem.setAlphabeticShortcut('z');
		// menuItem.setIcon(android.R.drawable.ic_menu_zoom);

		menuItem = menu.add(0, MENU_ITEM_MY_LOCATION, Menu.CATEGORY_SECONDARY,
				"����ġ ã��");
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
		case MENU_ITEM_CLEAR_MAP: // �� �ʱ�ȭ
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
	 * ����ġ ã��
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
							MapActivity.this,
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
	 * �� ��ġ ã�� �ߴ�
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
	 * �ܸ��� ����ġ�� �̵�
	 */
	private void moveToMyPlace() {
		try {
			//String addressStr = getAddressStr();

			mMapController.animateTo(new NGeoPoint(mLocation.getLongitude(), mLocation.getLatitude()));
			// ��� or ���� ��ư ó��
			final Button  traceBtn = (Button) findViewById(R.id.trace_btn);
			// ����ġ�� ã�������� ��߹�ư�� ��ٴ�.
			traceBtn.setEnabled(true);
			traceBtn.setClickable(true);		
			
		} catch (NullPointerException npe) {
			// ������ ��Ʈ��ũ ��ġ ���Ű� �޾ƿ� ��!
			Location tmpLocation = mLocationManager
					.getLastKnownLocation(LocationManager.GPS_PROVIDER);

			// ������ ��ġ���� ������
			if (tmpLocation == null) {
				// ������ ��ġ���̸� ��ġ���� �޾� �ü� �ְ� 1�ʸ� ��ٸ��� �ٽ� ��ġ ���� ����!
				mHandler.postDelayed(new Runnable() {
					@Override
					public void run() {
						moveToMyPlace(); // �ٽ� ���� ��ġ�� ã�´�.
					}
				}, 1000);
				return;
			}

			// ���� �ð� ��
			long locationTime = System.currentTimeMillis()
					- tmpLocation.getTime();
			// ���� ��ġ���ð��� 1�� �̳��� ��ġ�� ���
			if (locationTime < (1000 * 60 * 1)) {
				mLocation = tmpLocation;
				moveToMyPlace(); // �ٽ� ���� ��ġ�� ã�´�.
			} else {
				// ������ ��ġ���̸� ��ġ���� �޾� �ü� �ְ� 1�ʸ� ��ٸ��� �ٽ� ��ġ ���� ����
				mHandler.postDelayed(new Runnable() {
					@Override
					public void run() {
						moveToMyPlace(); // �ٽ� ���� ��ġ�� ã�´�.
					}
				}, 1000);
			}
		}
	}

	/**
	 * ��ǥ�� �̿��Ͽ� �ּҰ�������
	 * 
	 * @return �ּҸ�
	 * @throws IOException
	 */
	private String getAddressStr() {
		Geocoder gc = new Geocoder(this, Locale.getDefault());
		List<Address> addresses;
		String addressStr = "����ġ";
		try {
			addresses = gc.getFromLocation(mLocation.getLatitude(),
					mLocation.getLongitude(), 1);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return addressStr;
		}
		if (addresses.size() > 0) { // �ּҰ� ������
			// ù��° �ּ� �÷����� ������
			Address address = addresses.get(0);
			// ���� �ּҸ� �����´�.
			addressStr = address.getAddressLine(0).replace("���ѹα�", "").trim();
			Toast.makeText(MapActivity.this, addressStr, Toast.LENGTH_LONG)
					.show();
		}
		return addressStr;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// ��ġ ���� ����
		mLocationManager.removeUpdates(loclistener);
		// ������ ��ġ ���� ó��
		SharedPreferences.Editor editor = pref.edit();
		try{
			editor.putString(ConstantActivity.LAST_LAT, String.valueOf(mLocation.getLatitude()));
			editor.putString(ConstantActivity.LAST_LNG, String.valueOf(mLocation.getLongitude()));
			editor.commit();	// ������ �ݵ�� commit
		}catch(NullPointerException npe){

		}
	}

	/**
	 * gps�� ��Ȱ��ȭ�� gps ȯ�漳�� ȭ������ �̵����� ���̾�α�
	 */
	private void showLocationDialog() {
		new AlertDialog.Builder(this)
				.setMessage(
						" ��ġ ���� ��Ʈ��ũ ���  Ȥ�� GPS�� ��Ȱ��ȭ �Ǿ��ֽ��ϴ�. ����ȭ������ �̵� �Ͻðڽ��ϱ�?")
				.setCancelable(false)
				.setTitle("�˸�")
				.setPositiveButton("�̵�", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(final DialogInterface dialog,
							final int id) {
						Intent gpsOptionsIntent = new Intent(
								android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
						startActivity(gpsOptionsIntent);
					}
				})
				.setNegativeButton("���", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(final DialogInterface dialog,
							final int id) {
						dialog.cancel();
					}
				}).show();
	}

	/**
	 * ��ġ ���� ������
	 */
	private final LocationListener loclistener = new LocationListener() {
		@Override
		public void onLocationChanged(final Location location) { // ��ġ �����
			mLocation = location;
			if(isStarted){		// ��� ���������� ��ġ�� �����Ѵ�.
				traceList.add(new NGeoPoint(mLocation.getLongitude(), mLocation.getLatitude()));
				if(pathOveray != null){
					mOverlayManager.removeOverlay(pathOveray);
				}
				// set path data points
				NMapPathData pathData = new NMapPathData(traceList.size());
				pathData.initPathData();
				for(NGeoPoint p : traceList){
					pathData.addPathPoint(p.getLongitude(), p.getLatitude(), NMapPathLineStyle.TYPE_SOLID);
				}
				pathData.endPathData();
				pathOveray = mOverlayManager.createPathDataOverlay(pathData);	
				
				// ���� ����ġ�� ������ �����Ѵ�.
				if(myPlaceOveray != null){
					mOverlayManager.removeOverlay(myPlaceOveray);
				}
				// ����ġ ��Ŀ
				NMapPOIdata poiData = new NMapPOIdata(1, mMapViewerResourceProvider);
				poiData.beginPOIdata(1);				
				poiData.addPOIitem(mLocation.getLongitude(), mLocation.getLatitude(), "", NMapPOIflagType.SPOT, null);
				poiData.endPOIdata();
				myPlaceOveray= mOverlayManager.createPOIdataOverlay(poiData, null);
			
				// �̵��Ÿ� ����ϱ�
				float[] results = new float[5];
				if(beforeLat !=-1){	// ���� �Ÿ��� ������츸
					Location.distanceBetween(beforeLat, beforeLon, location.getLatitude(), 	location.getLongitude(), results);
					totalDistance += (int)results[0];
					int cal = DietUtils.calcurateCal(totalDistance, mWeight);
					distanceTv.setText("�̵� �Ÿ� : " + totalDistance + "m (" + cal + "Kcal)");
					
				}
				// ���� �Ÿ� ����
				beforeLat = location.getLatitude();
				beforeLon = location.getLongitude();
			}
		}

		/**
		 * ��ġ�������� ���°� ����Ǵ� ��� ȣ��ȴ�!
		 */
		@Override
		public void onProviderDisabled(final String provider) {
		}

		@Override
		public void onProviderEnabled(final String provider) {

		}

		@Override
		public void onStatusChanged(final String provider, final int status,
				final Bundle extras) {
		}
	};
	
	/**
	 * 	��ǥ�� �̿��Ͽ� �ּҰ�������
	 * @return
	 * 	�ּҸ�
	 * @throws IOException
	 */
	private String getAddressStr(final double lat, final double lng) {
		Geocoder gc = new Geocoder(this,Locale.getDefault());
		List<Address> addresses;
		String addressStr = "����ġ";
		try {
			addresses = gc.getFromLocation(lat, lng, 1);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return addressStr;
		}
		if(addresses.size()>0) {	// �ּҰ� ������
			// ù��° �ּ� �÷����� ������
			Address address = addresses.get(0);
			// ���� �ּҸ� �����´�.
			addressStr = address.getAddressLine(0).replace("���ѹα�", "").trim();
		}
		return addressStr;
	}
	
}
