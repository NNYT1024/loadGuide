package com.map.loadguied_v2.apiPackage;

import android.location.GnssAntennaInfo;
import android.os.AsyncTask;
import android.util.Log;

import com.kakao.vectormap.LatLng;
import com.map.loadguied_v2.createGuide.createGuideMainActivity;

import org.json.*;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;

public class getPoints extends AsyncTask<Void, Void, ArrayList<LatLng>> {

    private static final String REST_API_KEY = "83d8db42662bbd2af86846795bcfb600";
    private static final String API_URL = "https://apis-navi.kakaomobility.com/v1/waypoints/directions";
    private createGuideMainActivity listener;
    String str;

    public getPoints(createGuideMainActivity listener, String jsonStr) {
        this.str = jsonStr;
        this.listener = listener;
    }

    @Override
    protected ArrayList<LatLng> doInBackground(Void... voids) {
        Log.d("비동기 실행","실행중");
        try {
            URL url = new URL(API_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", "KakaoAK " + REST_API_KEY);
            connection.setDoOutput(true);

            // JSON 데이터 전송 및 응답 받기
            String jsonData = str;
            try (DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {
                wr.writeBytes(jsonData);
                wr.flush();
            }

            // 응답 읽기
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                // 결과를 파싱하여 LatLng 리스트로 변환
                ArrayList<LatLng> points = getLatLngList(response.toString());
                Log.d("points 출력",points.toString());

                connection.disconnect();
                return points;
            } else {
                // 오류 처리
                Log.e("비동기 에러", "에러");
                return null;
            }
        } catch (Exception e) {
            Log.e("비동기 에러", e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    public void pringLog(String tag,String jsonText){
        String str = jsonText;
        int count = 1;
        while(true){
            if(str.length() > 1000){
                Log.d(tag + " 전체중[" + count++ + "]", str.substring(0, 1000));
                str = str.substring(1000);
            } else{
                Log.d(tag + " 마지막", str);
                break;
            }
        }
    }
    public ArrayList<LatLng> getLatLngList(String jsonText){
        pringLog("Json 전체 데이터",jsonText);

        ArrayList<LatLng> result = new ArrayList<LatLng>();
        try {
            JSONObject jsonObject = new JSONObject(jsonText);
            JSONArray routesArray = jsonObject.getJSONArray("routes");
            pringLog("json routesArray 데이터",routesArray.toString());
            if (routesArray.length() > 0) {
                JSONObject routeObject = routesArray.getJSONObject(0); // 첫 번째 route 가져옴

                JSONArray sectionsArray = routeObject.getJSONArray("sections");

                pringLog("json sectionsArray 데이터",sectionsArray.toString());
                if (sectionsArray.length() > 0) {
                    for (int k = 0; k < sectionsArray.length(); k++) {
                        JSONObject sectionObject = sectionsArray.getJSONObject(k);
                        JSONArray roadsArray = sectionObject.getJSONArray("roads");
                        pringLog("json roadsArray 데이터",roadsArray.toString());
                        if (roadsArray.length() > 0) {
                            for (int j = 0; j < roadsArray.length(); j++) {
                                JSONObject roadObject = roadsArray.getJSONObject(j);
                                JSONArray vertexesArray = roadObject.getJSONArray("vertexes");
                                pringLog("json vertexesArray 데이터",vertexesArray.toString());
                                for (int i = 0; i < vertexesArray.length(); i+=2) {
                                    double latitude = vertexesArray.getDouble(i);
                                    double longitude = vertexesArray.getDouble(i+1);
                                    LatLng latLng = LatLng.from(longitude,latitude);
                                    result.add(latLng);
                                }
                            }
                        }
                    }

                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        pringLog("points return",result.toString());
        return result;

    }

    @Override
    protected void onPostExecute(ArrayList<LatLng> points) {
        super.onPostExecute(points);

        if (points != null) {
            listener.onPointsResult(points);
        } else {
            Log.e("onPostExecute", "에러 발생");
        }
    }
}
