package kr.co.diet.map;

import java.util.ArrayList;
import kr.co.diet.R;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SubMenu;
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
import com.nhn.android.maps.overlay.NMapPOIitem;
import com.nhn.android.mapviewer.overlay.NMapCalloutOverlay;
import com.nhn.android.mapviewer.overlay.NMapMyLocationOverlay;
import com.nhn.android.mapviewer.overlay.NMapOverlayManager;
import com.nhn.android.mapviewer.overlay.NMapOverlayManager.OnCalloutOverlayListener;
import com.nhn.android.mapviewer.overlay.NMapPOIdataOverlay;

/**
 *	Naver open api �� �̿��Ͽ� ������ �����ش�.
 *	��ġ �α� ������ �������� ���������� ��Ÿ����.
 */
public class MapActivity extends NMapActivity {
	// naver open api key
	private static final String API_KEY = "7b8d41389c90191b5a7dbfa0e8d71aac";
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

	private NGeoPoint mMyLocation;	// ��ǥ
	/** Called when the activity is first created. */
	@Override
	public void onCreate(final Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);

		setContentView(R.layout.map_layout);

		/* mapView  ����  */
		mMapView = (NMapView)findViewById(R.id.mapView);
		mMapView.setApiKey(API_KEY);	// Ű ����
		mMapView.setClickable(true);	// Ŭ�� ����

		// register listener for map state changes
		mMapView.setOnMapStateChangeListener(onMapViewStateChangeListener);
		mMapView.setOnMapViewTouchEventListener(onMapViewTouchEventListener);

		// use map controller to zoom in/out, pan and set map center, zoom level etc.
		mMapController = mMapView.getMapController();	// ��Ʈ�� ������
		mMapController.setMapViewBicycleMode(!mMapController.getMapViewBicycleMode());
		// use built in zoom controls
		NMapView.LayoutParams lp = new NMapView.LayoutParams(NMapView.LayoutParams.WRAP_CONTENT,
			NMapView.LayoutParams.WRAP_CONTENT, NMapView.LayoutParams.BOTTOM_RIGHT);
		mMapView.setBuiltInZoomControls(true, lp);

		// create resource provider
		mMapViewerResourceProvider = new NMapViewerResourceProvider(this);
		// create overlay manager
		mOverlayManager = new NMapOverlayManager(this, mMapView, mMapViewerResourceProvider);
		// register callout overlay listener to customize it.
		mOverlayManager.setOnCalloutOverlayListener(new OnCalloutOverlayListener(){

			@Override
			public NMapCalloutOverlay onCreateCalloutOverlay(final NMapOverlay itemOverlay,final NMapOverlayItem overlayItem, final Rect itemBounds) {
				// set your callout overlay

				return new NMapCalloutBasicOverlay(itemOverlay, overlayItem, itemBounds);
			}

		});

		// location manager
		mMapLocationManager = new NMapLocationManager(this);
		mMapLocationManager.setOnLocationChangeListener(onMyLocationChangeListener);

		// compass manager
		mMapCompassManager = new NMapCompassManager(this);

		// create my location overlay
		mMyLocationOverlay = mOverlayManager.createMyLocationOverlay(mMapLocationManager, mMapCompassManager);

		// set data provider listener
		// ���� ���̺귯������ �����ϴ� ���� API ȣ�� �� ���信 ���� �ݹ� �������̽�
	    super.setMapDataProviderListener(new NMapActivity.OnDataProviderListener(){
			@Override
			public void onReverseGeocoderResponse(final NMapPlacemark arg0, final NMapError arg1) {
				// TODO Auto-generated method stub
			}
		});

	}

	/* MyLocation Listener */
	private final NMapLocationManager.OnLocationChangeListener onMyLocationChangeListener = new NMapLocationManager.OnLocationChangeListener() {

		@Override
		public boolean onLocationChanged(final NMapLocationManager locationManager, final NGeoPoint myLocation) {

			if (mMapController != null) {
				mMapController.animateTo(myLocation);
			}
			mMyLocation = myLocation;
			
			return true;
		}

		@Override
		public void onLocationUpdateTimeout(final NMapLocationManager locationManager) {
			Toast.makeText(MapActivity.this, "���� ��ġ�� ���ü� �����ϴ�.", Toast.LENGTH_LONG).show();
		}

		@Override
		public void onLocationUnavailableArea(final NMapLocationManager arg0,
				final NGeoPoint arg1) {
			// TODO Auto-generated method stub

		}

	};


	private final NMapView.OnMapStateChangeListener onMapViewStateChangeListener = new NMapView.OnMapStateChangeListener() {

		@Override
		public void onAnimationStateChange(final NMapView arg0, final int arg1, final int arg2) {
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
		public void onMapInitHandler(final NMapView arg0, final NMapError arg1) {
			Intent intent = getIntent();
			NGeoPoint point;

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
		public void onScroll(final NMapView arg0, final MotionEvent arg1, final MotionEvent arg2) {
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



	/* POI data State Change Listener*/
	private final NMapPOIdataOverlay.OnStateChangeListener onPOIdataStateChangeListener = new NMapPOIdataOverlay.OnStateChangeListener() {
		/**
		 * �������� �������� Ŭ���� �α�޽����� ����.
		 */
		@Override
		public void onCalloutClick(final NMapPOIdataOverlay poiDataOverlay, final NMapPOIitem item) {
			Log.i(LOG_TAG, "onCalloutClick: title=" + addressList.get( item.getId() )  );
			// �α� �޽��� �� �����ش�.
			Toast.makeText(MapActivity.this, addressList.get( item.getId() ) , Toast.LENGTH_LONG).show();
		}

		@Override
		public void onFocusChanged(final NMapPOIdataOverlay poiDataOverlay, final NMapPOIitem item) {
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
	 * @param menu the Menu to which entries may be added
	 * @return true
	 */
	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		super.onCreateOptionsMenu(menu);

		MenuItem menuItem = null;
		SubMenu subMenu = null;

		menuItem = menu.add(Menu.NONE, MENU_ITEM_CLEAR_MAP, Menu.CATEGORY_SECONDARY, "�� �ʱ�ȭ");
		menuItem.setAlphabeticShortcut('c');
		//menuItem.setIcon(android.R.drawable.ic_menu_revert);

		subMenu = menu.addSubMenu(Menu.NONE, MENU_ITEM_MAP_MODE, Menu.CATEGORY_SECONDARY, "���� ���");
		//subMenu.setIcon(android.R.drawable.ic_menu_mapmode);

		menuItem = subMenu.add(0, MENU_ITEM_MAP_MODE_SUB_VECTOR, Menu.NONE, "�Ϲݸ��");
		menuItem.setAlphabeticShortcut('m');
		menuItem.setCheckable(true);
		menuItem.setChecked(false);

		menuItem = subMenu.add(0, MENU_ITEM_MAP_MODE_SUB_SATELLITE, Menu.NONE, "�������");
		menuItem.setAlphabeticShortcut('s');
		menuItem.setCheckable(true);
		menuItem.setChecked(false);

		menuItem = subMenu.add(0, MENU_ITEM_MAP_MODE_SUB_HYBRID, Menu.NONE, "ȥ�ո��");
		menuItem.setAlphabeticShortcut('h');
		menuItem.setCheckable(true);
		menuItem.setChecked(false);

		menuItem = subMenu.add(0, MENU_ITEM_MAP_MODE_SUB_TRAFFIC, Menu.NONE, "����");
		menuItem.setAlphabeticShortcut('t');
		menuItem.setCheckable(true);
		menuItem.setChecked(false);

		menuItem = subMenu.add(0, MENU_ITEM_MAP_MODE_SUB_BICYCLE, Menu.NONE, "������");
		menuItem.setAlphabeticShortcut('b');
		menuItem.setCheckable(true);
		menuItem.setChecked(false);

		menuItem = menu.add(0, MENU_ITEM_ZOOM_CONTROLS, Menu.CATEGORY_SECONDARY, "�� ��Ʈ��");
		menuItem.setAlphabeticShortcut('z');
		//menuItem.setIcon(android.R.drawable.ic_menu_zoom);

		menuItem = menu.add(0, MENU_ITEM_MY_LOCATION, Menu.CATEGORY_SECONDARY, "����ġ ã��");
		menuItem.setAlphabeticShortcut('l');
		//menuItem.setIcon(android.R.drawable.ic_menu_mylocation);


		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(final Menu pMenu) {
		super.onPrepareOptionsMenu(pMenu);

		int viewMode = mMapController.getMapViewMode();

		pMenu.findItem(MENU_ITEM_CLEAR_MAP).setEnabled(
			(viewMode != NMapView.VIEW_MODE_VECTOR) || mOverlayManager.sizeofOverlays() > 0);
		pMenu.findItem(MENU_ITEM_MAP_MODE_SUB_VECTOR).setChecked(viewMode == NMapView.VIEW_MODE_VECTOR);
		pMenu.findItem(MENU_ITEM_MAP_MODE_SUB_SATELLITE).setChecked(viewMode == NMapView.VIEW_MODE_SATELLITE);
		pMenu.findItem(MENU_ITEM_MAP_MODE_SUB_HYBRID).setChecked(viewMode == NMapView.VIEW_MODE_HYBRID);


		if (mMyLocationOverlay == null) {
			pMenu.findItem(MENU_ITEM_MY_LOCATION).setEnabled(false);
		}

		return true;
	}

	/**
	 * Invoked when the user selects an item from the Menu.
	 *
	 * @param item the Menu entry which was selected
	 * @return true if the Menu item was legit (and we consumed it), false
	 *         otherwise
	 */
	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {

		switch (item.getItemId()) {
			case MENU_ITEM_CLEAR_MAP:	// �� �ʱ�ȭ
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
				mMapController.setMapViewTrafficMode(!mMapController.getMapViewTrafficMode());
				return true;

			case MENU_ITEM_MAP_MODE_SUB_BICYCLE:
				mMapController.setMapViewBicycleMode(!mMapController.getMapViewBicycleMode());
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
				boolean isMyLocationEnabled = mMapLocationManager.enableMyLocation(false);
				if (!isMyLocationEnabled) {
					Toast.makeText(MapActivity.this, "Please enable a My Location source in system settings",
						Toast.LENGTH_LONG).show();

					Intent goToSettings = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
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


}
