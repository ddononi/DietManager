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
	private ArrayList<MapPoint> pathList = new ArrayList<MapPoint>();	// ��θ� ������ collection list
	private Button traceBtn;
	protected static boolean isStarted = false;	// ��߿���
	protected static boolean isEnded = false;	// ��������

	private TextView startPlaceTv;		// ���� ��ġ�ּ�
	private TextView endPlaceTv;		// ���� ��ġ�ּ�
	private TextView startTimeTv;		// ��� �ð�
	private TextView endTimeTv;			// ���� �ð�
	
	private boolean running = false;
	
	private float runDistance = 0;	// �̵��Ÿ�üũ
	
	// ui ó���� ���� �ڵ鷯
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
        // ���ý�Ʈ ���
        mContext = this;	
        
        // ���̾ƿ� �ʱ�ȭ
        initLayout();        
        // �� �ʱ�ȭ
        initMap();
        // ��ġ �ʱ�ȭ
        initLocation(); 
        
        // ȭ���� �������°� ���� �ʵ��� �Ѵ�.
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
  
    }

	private void initLayout() {
		traceBtn = (Button)findViewById(R.id.trace_btn);
        traceBtn.setClickable(false);	// ���� ��ġ�� ã�� �������� Ŭ�� ���

        //	 ����ġ ã�� ��ư
        ImageButton myLocBtn = (ImageButton)findViewById(R.id.loc_btn);

        myLocBtn.setOnClickListener(this);

        // ��� �ּ�
		startPlaceTv = (TextView)findViewById(R.id.start_place);
		// ���� �ּ�
		endPlaceTv = (TextView)findViewById(R.id.end_place);

		startTimeTv  = (TextView)findViewById(R.id.startTime);
		endTimeTv  = (TextView)findViewById(R.id.endTime);
	}

	/**
	 * �� Ű ���� �� �ʱ�ȭ ����
	 */
	private void initMap() {
        mMapView = new MapView(this);
        mMapView.setDaumMapApiKey(MAP_KEY);
        mMapView.setOpenAPIKeyAuthenticationResultListener(this);
        mMapView.setMapViewEventListener(this);
        mMapView.setCurrentLocationEventListener(this);
        mMapView.setPOIItemEventListener(this);
        // ���� Ÿ�� ����
        mMapView.setMapType(MapView.MapType.Standard);
        //	����ġ�� ǥ���ϴ� ������(��Ŀ)�� ȭ�鿡 ǥ�� ����
        mMapView.setShowCurrentLocationMarker(false);
        // ��θ� �׷��� �� ����
        tracePath.setLineColor(Color.argb(128, 255, 51, 0));
        ViewGroup parent = (ViewGroup)findViewById(R.id.map_parent);
        parent.addView(mMapView);
	}

	/**
	 * list�� ����� ��ǥ�� �̿��Ͽ� ��θ� �׷��ش�.
	 */
	private void drawPath() {
		// ���� ��� ��θ� ������ �ٽ� �׷��ش�.
		mMapView.removeAllPolylines();
		for(MapPoint p: pathList){
			tracePath.addPoint(p);
		}
		// �ʺ信 �ٿ��ش�.
		mMapView.addPolyline(tracePath);
		// ��ΰ� �� ���̵��� �����Ѵ�.
		mMapView.fitMapViewAreaToShowAllPolylines();
	}

	/**
	 * ��ġ������ ���� ��Ȯ�� ������ ����
	 * ���� ��ġ���� ���Ѵ�.
	 * @param location
	 * @param currentBestLocation
	 * @return
	 */
	protected boolean isBetterLocation(final Location location, final Location currentBestLocation){
		if(currentBestLocation == null){
			// �߸��� ��ġ�� ���°ͺ��� ����.
			return true;
		}

		// ���� ������ ���ο� �������� �ð� ���̸� �����Ѵ�.
		// 2�� �̻� ����� ���ο� ��ҷ� �̵��Ͽ��� ���ɼ��� ũ�Ƿ�
		// �ش� ������ ä���ϰ� �ȴ�.
		long timeDelta = location.getTime() - currentBestLocation.getTime();
		boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;	// ���ο� ��ġ��������
		boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
		boolean isNewer = timeDelta > 0;
		// 2�� �̻� �氡�� ��ġ��� true otherwise false
		if(isSignificantlyNewer){
			return true;
		}else if(isSignificantlyOlder){
			return false;
		}

		// ��ġ ��Ȯ�� ����
		// ��ġ��������ڰ� �� ���� �Ÿ� ���� ������ ������ �����Ѵٸ�
		// �� ������ ��Ȯ�ϴٰ� �Ǵ��� �� �ִ� ���⼭�� 200���ͷ� �ξ���.
		int accuracyDelta = (int)(location.getAccuracy() - currentBestLocation.getAccuracy());
		boolean isLessAccurate = accuracyDelta > 0;
		boolean isLMoreAccurate = accuracyDelta < 0;
		boolean isSignificantlyLessAccurate = accuracyDelta > ACCURATE_VALUE;

		// �տ����Ͱ��� ���� ��ȭ�� ���� ��ġ��������ڰ� ������ �� �ִ�.
		// ���� ����ڶ� �Ѵٸ� ���� ��Ȯ�� ��ġ������ ������ �� ���ɼ��� ���ٰ� �Ǵ��� �� �ִ�.
		boolean isFormSameProvider = isSameProvider(location.getProvider(), currentBestLocation.getProvider());

		// �ð��� �Ÿ��� ���̸� ����Ͽ� ��ġ�� ��Ȯ���� �����Ѵ�.
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
	 * ���� ���������� ����
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
		moveToMyPlace();	// ����ġ ã��
        
	}

	@Override
	public void onClick(final View v) {
		switch(v.getId()){
		case R.id.trace_btn:	// ���� ��ư�� ������ ���
			if(running == false){
				if(mLocation == null){	// ��ġ�� ã���� ���� ���
					Toast.makeText(this, "���� ��ġ�� ã���� �����ϴ�.", Toast.LENGTH_SHORT).show();
					return;
				}
				
				isStarted = true;
				running = true;
				startTrace();

				((Button)v).setText("����");
			}else{
				if(mLocation == null){
					return;
				}
				running = false;				
				endTrace();
				// �����Ϸ��̸� Ŭ�� ��ư�� �����.
				v.setVisibility(View.GONE);
				// ��ġ ������ ����
				locationManager.removeUpdates(loclistener);
				// ��� ���̾�α� ����
				
				Toast.makeText(mContext, "runDistance : " + runDistance, Toast.LENGTH_LONG).show();
				
				/*
				intent = new Intent(this, ExersiceRunResultActivity.class);
				startActivity(intent);
				finish();
				*/
			}
			break;
		case R.id.loc_btn :	// ���� ��ġ ã��
			if(mLocation != null){	// ���� ��ġ ������ ��������
				MapPoint point =  MapPoint.mapPointWithGeoCoord(mLocation.getLatitude(), mLocation.getLongitude());
				mMapView.setMapCenterPoint(point, true);
			}else{
				// ��Ʈ��ũ�� ������ ������ �ٽ� ���� ��ġ �˻�
				locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000L, 0, loclistener);
				moveToMyPlace();
				Toast.makeText(this, "���� ��ġ�� ã���� �����ϴ�.", Toast.LENGTH_SHORT).show();
			}
			break;
		}
	}
	
	
    /**
     * �ܸ��� ����ġ�� �̵�
     */
    private void moveToMyPlace() {
		try{
			MapPoint point =  MapPoint.mapPointWithGeoCoord(mLocation.getLatitude(), mLocation.getLongitude());
			mMapView.setMapCenterPoint(point, true);
		}catch(NullPointerException npe){
			// ��Ʈ��ũ�� ������ ���� �� �ٽ� ���� ��ġ �˻� 
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000L, 0, loclistener);
			// ������ ��Ʈ��ũ ��ġ ���Ű� �޾ƿ� ��!
			Location tmpLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

			// ������ ��ġ���� ������
			if(tmpLocation == null){
				// ������ ��ġ���̸� ��ġ���� �޾� �ü� �ְ� 1�ʸ� ��ٸ��� �ٽ� ��ġ ���� ����!
				mHandler.postDelayed(new Runnable() {
					@Override
					public void run() {
						moveToMyPlace();	// �ٽ� ���� ��ġ�� ã�´�.
					}
				}, 1000);
				return;
			}

			// ���� �ð� ��
			long locationTime = System.currentTimeMillis() - tmpLocation.getTime();
			// ���� ��ġ���ð��� 1�� �̳��� ��ġ�� ���
			if(locationTime < (1000 * 60 * 1) ){
				mLocation = tmpLocation;
				moveToMyPlace();	// �ٽ� ���� ��ġ�� ã�´�.
			}else{
				// ������ ��ġ���̸� ��ġ���� �޾� �ü� �ְ� 1�ʸ� ��ٸ��� �ٽ� ��ġ ���� ����
				mHandler.postDelayed(new Runnable() {
					@Override
					public void run() {
						moveToMyPlace();	// �ٽ� ���� ��ġ�� ã�´�.
					}
				}, 1000);
			}
		}

	}	

	@Override
	protected void onDestroy() {
		running = false;
		// ��ġ ������ ����
		locationManager.removeUpdates(loclistener);		
		super.onDestroy();
	}



	/*
     * ���� ȯ�濡 ���� LocationListener �� �����Ѵ�.
     */
    private void initLocation(){
    	locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
    	//criteria �� �̿��Ͽ� ������ ��ġ �����ڸ� �̿��Ѵ�.
    	Criteria criteria = new Criteria();
    	criteria.setAccuracy(Criteria.ACCURACY_FINE);// ��Ȯ��
    	criteria.setPowerRequirement(Criteria.POWER_HIGH); // ���� �Һ�
    	criteria.setAltitudeRequired(false); // �� ��뿩��
    	criteria.setBearingRequired(false); //
    	criteria.setSpeedRequired(false); // �ӵ�
    	criteria.setCostAllowed(true); // ���������
    	String provider = LocationManager.GPS_PROVIDER;
    	//String provider = mLocationManager.getBestProvider(criteria, true);
		//location = mLocationManager.getLastKnownLocation(provider);
    	locationManager.requestLocationUpdates(provider, 0, 0, loclistener);//���������� ������Ʈ

		// gps Ȱ��ȭ ����üũ�� ��Ȱ��ȭ��  gps ȯ�漳������ ����
        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        		|| !locationManager.isProviderEnabled (LocationManager.NETWORK_PROVIDER) ) {
    		showLocationDialog();
        }
    }

	/**
	 * gps�� ��Ȱ��ȭ�� gps ȯ�漳�� ȭ������ �̵����� ���̾�α�
	 */
	private void showLocationDialog() {
		new AlertDialog.Builder(this).setMessage(" ��ġ ���� ��Ʈ��ũ ���  Ȥ�� GPS�� ��Ȱ��ȭ �Ǿ��ֽ��ϴ�. ����ȭ������ �̵� �Ͻðڽ��ϱ�?")
        .setCancelable(false).setTitle("�˸�")
        .setPositiveButton("�̵�",
                new DialogInterface.OnClickListener() {
                    @Override
					public void onClick(final DialogInterface dialog, final int id) {
                        Intent gpsOptionsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(gpsOptionsIntent);
                    }
            })
        .setNegativeButton("���",
                new DialogInterface.OnClickListener() {
                    @Override
					public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
            }).show();
	}

    /**
     * ��ġ ���� ������
     */
    private final LocationListener loclistener = new LocationListener(){
        @Override
		public void onLocationChanged(final Location location) {	// ��ġ �����
        	
            mLocation = location;
			// �ܸ��� ��ġ�� ���� �̵�ó��
			// ���� ��ư Ǯ��
			traceBtn.setClickable(true);
			// �̺�Ʈ ������ �޾��ֱ�
	        traceBtn.setOnClickListener(ExerciseRunActivity.this);
			MapPoint point =  MapPoint.mapPointWithGeoCoord(location.getLatitude(), location.getLongitude());
			mMapView.setMapCenterPoint(point, true);
			mLocation = location;	// ��ġ �޾ƿ���
			if(running){	// �����̸� ��� ����
				pathList.add(point);
				// ���� ��ο��� �Ÿ��� ����Ѵ�.
				runDistance += mLocation.distanceTo(location);
			}
        }

        /**
         * ��ġ�������� ���°� ����Ǵ� ��� ȣ��ȴ�!
         */
        @Override
		public void onProviderDisabled(final String provider) {
        	if(running){
				Toast.makeText(ExerciseRunActivity.this, "GPS ��ġ������ ��Ȱ���� �ʽ��ϴ�.", Toast.LENGTH_SHORT).show();
        	}
        }

        @Override
		public void onProviderEnabled(final String provider) {
        	if(running){
				Toast.makeText(ExerciseRunActivity.this, "GPS ��ġ������ ���������� �����մϴ�.", Toast.LENGTH_SHORT).show();
        	}
        }

        @Override
		public void onStatusChanged(final String provider, final int status, final Bundle extras) {
        }
    };

	/**
	 * ���� ���۽� ��� �������� �������� �����ϰ� �ʺ信 �s���ش�.
	 */
	private void startTrace() {
		MapPoint point =  MapPoint.mapPointWithGeoCoord(mLocation.getLatitude(), mLocation.getLongitude());
		reverseGeoCoder = new MapReverseGeoCoder(DAUM_LOCAL_KEY, point, this, this);
		reverseGeoCoder.startFindingAddress();
		pathList.add(point);
		MapPOIItem item = new MapPOIItem();
		// poi ������ ����
		item.setTag(START_TAG);
		item.setItemName("���");
		item.setMapPoint(point);
		item.setShowAnimationType(ShowAnimationType.SpringFromGround);
		item.setMarkerType(MarkerType.CustomImage);
		item.setCustomImageResourceId(R.drawable.custom_poi_marker_start);
		item.setCustomImageAnchorPointOffset(new MapPOIItem.ImageOffset(22,0));
		// �ʿ� �ٿ��ش�.
		mMapView.addPOIItem(item);
		startTimeTv.setText("��߽ð� : " + new SimpleDateFormat("hh�� mm�� ss��").format(new Date()));
	}

	/**
	 * ���� ����� ���� �������� �������� �����ϰ� �ʺ信 �s���ش�.
	 */
	private void endTrace() {
		// ���� ��ġ�� ���
		isEnded = true;
		MapPoint point =  MapPoint.mapPointWithGeoCoord(mLocation.getLatitude(), mLocation.getLongitude());
	
		pathList.add(point);

		reverseGeoCoder = new MapReverseGeoCoder(DAUM_LOCAL_KEY, point, this, this);
		reverseGeoCoder.startFindingAddress();

		MapPOIItem item = new MapPOIItem();
		// poi ������ ����
		item.setTag(END_TAG);
		item.setItemName("����");
		item.setMapPoint(point);
		item.setShowAnimationType(ShowAnimationType.SpringFromGround);
		item.setMarkerType(MarkerType.CustomImage);
		item.setCustomImageResourceId(R.drawable.custom_poi_marker_end);
		item.setCustomImageAnchorPointOffset(new MapPOIItem.ImageOffset(22,0));
		// �ʿ� �ٿ��ش�.
		mMapView.addPOIItem(item);
		// ��� �׷��ֱ�
		endTimeTv.setText("�����ð� : " + new SimpleDateFormat("hh�� mm�� ss��").format(new Date()));
		drawPath();
	}


	@Override
	public void onBackPressed() {	//  �ڷ� �����ư Ŭ���� ���� ����
			AlertDialog.Builder ad = new AlertDialog.Builder(this);
			ad.setTitle("").setMessage("���忡�� �����ðڽ��ϱ�?")
			.setPositiveButton("����", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(final DialogInterface dialog, final int which) {
					finish();
				}
			}).setNegativeButton("���",null).show();
	}

    /**
     * �ɼ� �޴� ó��
     */
    @Override
    public boolean onCreateOptionsMenu(final Menu menu){
    	super.onCreateOptionsMenu(menu);
    	menu.add(0, 1, 0, "��������").setIcon(android.R.drawable.ic_menu_gallery);
    	menu.add(0, 2, 0, "������").setIcon(android.R.drawable.ic_menu_close_clear_cancel);
    	return true;
    }


    /**
     * �ɼ� �޴� ���ÿ� ���� �ش� ó���� ����
     */
    @Override
    public boolean onOptionsItemSelected(final MenuItem item){
    	switch(item.getItemId()){
    		case 1:
    			String[] mapTypeMenuItems = { "�Ϲ�����", "��������", "���̺긮��"};

    			Builder dialog = new AlertDialog.Builder(this);
    			dialog.setTitle("���� ��������");
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
		// ���� poiitem �� ã�´�.
		if(isEnded == false){
			MapPOIItem item = mMapView.findPOIItemByTag(START_TAG);
			try{
				item.setItemName(addressString);
			}catch(NullPointerException npe){}
			mMapView.addPOIItem(item);
			startPlaceTv.setText("��� ��ġ : " + addressString);
		}else{
			MapPOIItem item = mMapView.findPOIItemByTag(END_TAG);
			try{
				item.setItemName(addressString);
			}catch(NullPointerException npe){}			
			mMapView.addPOIItem(item);
			endPlaceTv.setText("���� ��ġ : " + addressString);
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
	 * Ű ���� ó��
	 */
	@Override
	public void onDaumMapOpenAPIKeyAuthenticationResult(final MapView v, final int arg1,
			final String arg2) {
	}	

}
