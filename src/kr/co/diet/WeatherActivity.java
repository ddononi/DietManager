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

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.weather_layout);
		mContext = this;
		initLayout();

		new LoadWeatherTask().execute("����");
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
								String[] locals = mContext.getResources().getStringArray(R.array.local_list);
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

			} catch (Exception e) {
				// TODO: handle exception
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
	public WeatherData requestWeatherData(final String local) throws IOException, SAXException, ParserConfigurationException {
		// ���� ���� api ����
		String weatherUrl  =GOOGLE_WEATHER_URL + URLEncoder.encode(local, "UTF-8");
		URL url = new URL(weatherUrl);
		URLConnection conn = url.openConnection();
		// ����õ� �ð� ����
		conn.setConnectTimeout(CONNECTION_TIME_OUT);
		String line;
		StringBuilder sb = new StringBuilder();
		// ���� ������ �����ü� �ְ� bufferedReader �� �����´�.
		// ���ڵ� euc-kr�� ��ȯ
		BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream() , "EUC-KR"));
		while((line = reader.readLine()) != null){
			sb.append(line);
		}
		Log.i("weather", sb.toString());
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
		Log.i("diary",  xmlStr);
		WeatherData data =new WeatherData();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		InputStream is = new ByteArrayInputStream(xmlStr.getBytes());
		Document document = builder.parse(is);
		Element weather = document.getDocumentElement();

		NodeList nodeList;
		Node node;

		nodeList = weather.getElementsByTagName("forecast_information");
		node = nodeList.item(0).getFirstChild();
		data.setLocal(node.getAttributes().getNamedItem("data").getNodeValue());	// ���� ��������
		nodeList = weather.getElementsByTagName("current_conditions");
		NodeList currentCondiList = nodeList.item(0).getChildNodes();

		// ���� ��������
		data.setCurrTemp("���� ��� : " + currentCondiList.item(2).getAttributes().getNamedItem("data").getNodeValue() +"��" );
		// ���� ��������
		data.setCurrHumidify( currentCondiList.item(3).getAttributes().getNamedItem("data").getNodeValue() );
		// �̹��� ��������
		data.setCurrWeatherImgUrl(currentCondiList.item(4).getAttributes().getNamedItem("data").getNodeValue() );
		mCurrCondImage.setImasgeUrl(GOOGLE_URL + data.getCurrWeatherImgUrl());
		// ��������
		nodeList = weather.getElementsByTagName("forecast_conditions");
		ArrayList<ForecastData> list = new ArrayList<ForecastData>();
		for(int i =0; i<nodeList.getLength();  i++){
			NodeList forecastItems = nodeList.item(i).getChildNodes();
			ForecastData forecastData = new ForecastData();
			// ���� ��������
			forecastData.setDayOfWeek(forecastItems.item(0).getAttributes().getNamedItem("data").getNodeValue() );
			// ������� ��������
			forecastData.setLowTemp(forecastItems.item(1).getAttributes().getNamedItem("data").getNodeValue() );
			// �ְ��� ��������
			forecastData.setHighTemp(forecastItems.item(2).getAttributes().getNamedItem("data").getNodeValue() );
			// ���� �̹��� ��������
			forecastData.setWeatherImgUrl(forecastItems.item(3).getAttributes().getNamedItem("data").getNodeValue() );
			// ����  ���� ��������
			forecastData.setCondition(forecastItems.item(4).getAttributes().getNamedItem("data").getNodeValue() );
			// ����Ʈ�� ���� ���� ���
			list.add(forecastData);
		}
		data.setForecasts(list);

		return data;
	}

}
