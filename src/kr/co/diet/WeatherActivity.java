package kr.co.diet;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import kr.co.diet.dao.ForecastData;
import kr.co.diet.dao.WeatherData;
import kr.co.diet.widget.WebImageView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class WeatherActivity extends ConstantActivity {
	private TextView mCurrLocal;
	private TextView mCurrTemp;
	private TextView mCurrHumdity;
	private WebImageView mCurrCondImage;

	private ListView mListView;
	private Context mContext;

	private String selectedLocal = "����";
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.weather_layout);
		mContext = this;
		initLayout();

		new LoadWeatherTask().execute("seoul");
	}

	/**
	 * ���̾ƿ� hooking �� �̺�Ʈ ����
	 */
	private void initLayout() {
		mCurrLocal = (TextView) findViewById(R.id.location);
		mCurrTemp = (TextView) findViewById(R.id.location_temp);
		mCurrHumdity = (TextView) findViewById(R.id.location_hum);
		mCurrCondImage = (WebImageView) findViewById(R.id.icon);

		mListView = (ListView) findViewById(R.id.list_view);
		Button localBtn = (Button) findViewById(R.id.local_btn);
		localBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				new AlertDialog.Builder(mContext)
				.setTitle("���� �����ϱ�")
				.setItems(R.array.local_list,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(final DialogInterface dialog,
									final int which) {
								// ���������� �ش� ���� ���� ���� ��������
								String[] locals = mContext.getResources().getStringArray(R.array.local_list_value);
								selectedLocal =  mContext.getResources().getStringArray(R.array.local_list)[which]; 
								new LoadWeatherTask().execute(locals[which]);
							}
						}).show();
			}
		});
	}

	private class LoadWeatherTask extends AsyncTask<String, WeatherData, WeatherData> {
		private ProgressDialog progress = null;

		@Override
		protected WeatherData doInBackground(final String... params) {
			WeatherData data = null;
			try {
				data = requestWeatherData(params[0]);
			} catch (IOException ioe){
				Log.i(DEBUG_TAG , "" +ioe.getMessage());
			} catch (Exception e) {
				Log.i(DEBUG_TAG , "" +  e.getMessage());
			}
			return data;
		}

		/*
		 * ������ �ε� �Ϸ�ó����
		 */
		@Override
		protected void onPostExecute(final WeatherData data) {
			if(data == null){	// ���� ������ ���� ���
				Toast.makeText(mContext, "���� ������ �������� ���߽��ϴ�.", Toast.LENGTH_SHORT).show();
			}else{
				// ������Ʈ�� �������� ������ ä���ش�.
				mCurrLocal.setText(data.getLocal());
				mCurrTemp.setText(data.getCurrTemp());
				mCurrHumdity.setText(data.getCurrHumidify());
				WeatherListAdapter adapter = new WeatherListAdapter(data.getForecasts(), mContext);
				mListView.setAdapter(adapter);
			}

			// �ε�â �ݱ�
			if(progress != null && progress.isShowing()){
				progress.dismiss();
			}
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			progress = ProgressDialog.show(mContext, "", "������ �ҷ����� ���Դϴ�.");
		}

	}

	/**
	 * ���� ���� ��û
	 * ���� api �� �̿��Ͽ� ���������� xml���·� �����ϰ�
	 * XML �����͸� �Ľ�
	 * @param string
	 * @return
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 */
	public WeatherData requestWeatherData(String local) throws IOException, SAXException, ParserConfigurationException {
		// ���� ���� api ����
		String weatherUrl  =MSN_WEATHER_URL + URLEncoder.encode(local, "UTF-8");
		URL url = new URL(weatherUrl);
		URLConnection conn = url.openConnection();
		// ����õ� �ð� ����
		conn.setConnectTimeout(CONNECTION_TIME_OUT);
		String line;
		StringBuilder sb = new StringBuilder();
		// ���� ������ �����ü� �ְ� bufferedReader �� �����´�.
		// ���ڵ� euc-kr�� ��ȯ
		BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream() , "UTF-8"));
		while((line = reader.readLine()) != null){
			sb.append(line);
		}
		return parseXml(sb.toString());
	}

	/**
	 * ������ XML�� SAX �Ľ�ó��
	 * @param string
	 * @return
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 */
	private WeatherData parseXml(final String xmlStr) throws SAXException, IOException, ParserConfigurationException {
		Log.i(DEBUG_TAG,  xmlStr);
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		InputStream is = new ByteArrayInputStream(xmlStr.getBytes());
		Document document = builder.parse(is);
		Element weather = document.getDocumentElement();

		NodeList nodeList;
		Node node;

		// ���� root
		nodeList = weather.getElementsByTagName("weather");
		// ù��° ������ �����´�.
		node = nodeList.item(0);
		WeatherData data =new WeatherData();		
		data.setLocal(selectedLocal);	// ���� 
		NodeList currentCondiList = nodeList.item(0).getChildNodes();

		// ���� ��������
		data.setCurrTemp("���� ��� : " + currentCondiList.item(0).getAttributes().getNamedItem("temperature").getNodeValue() +"��" );
		Log.i(DEBUG_TAG,  "������: " +  data.getCurrTemp());			
		// ���� ��������
		data.setCurrHumidify( currentCondiList.item(0).getAttributes().getNamedItem("humidity").getNodeValue() );
		Log.i(DEBUG_TAG,  "���� : " +  data.getCurrTemp());			
		// �̹��� ��������
		data.setCurrWeatherImgUrl(currentCondiList.item(0).getAttributes().getNamedItem("skycode").getNodeValue() );
		Log.i(DEBUG_TAG,  "�̹��� : " +  data.getCurrWeatherImgUrl());			
		// �̹��� ����
		mCurrCondImage.setImasgeUrl(MSN_WEATHER_IMAGE_URL + data.getCurrWeatherImgUrl() +  ".gif");
		// ��������

		ArrayList<ForecastData> list = new ArrayList<ForecastData>();
		// �������� toobar�̹Ƿ� ���� �̴�
		int size = currentCondiList.getLength()-1;
		for(int i =0; i< size;  i++){
			// ù��°�� ���� �����̹Ƿ� �Ѿ��.
			if(i == 0){
				continue;
			}
			Node forecastItems = currentCondiList.item(i);
			
			ForecastData forecastData = new ForecastData();
			//NamedNodeMap attrs = forecastItems.item(0).getAttributes();
			// ���� ��������
			forecastData.setDayOfWeek(forecastItems.getAttributes().getNamedItem("day").getNodeValue() );
			Log.i(DEBUG_TAG,  "��¥: " +  forecastData.getDayOfWeek());				
			// ������� ��������
			forecastData.setLowTemp(forecastItems.getAttributes().getNamedItem("low").getNodeValue() );
			// �ְ��� ��������
			forecastData.setHighTemp(forecastItems.getAttributes().getNamedItem("high").getNodeValue() );
			// ���� �̹��� ��������
			forecastData.setWeatherImgUrl(forecastItems.getAttributes().getNamedItem("skycodeday").getNodeValue() );
			// ����  ���� ��������
			forecastData.setCondition(forecastItems.getAttributes().getNamedItem("skytextday").getNodeValue() );
			// ����Ʈ�� ���� ���� ���
			list.add(forecastData);
		}
		data.setForecasts(list);

		return data;
	}

}
