package com.map.loadguied_v2.apiPackage;

import android.util.Log;


import com.kakao.vectormap.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class findRoad_Json {//경로검색 반환값 정리
    //routes 최상위 키

    public findRoad_Json(String jsonString){
        try {
            JSONObject jObject = new JSONObject(jsonString);
            getJsonData(jObject);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public findRoad_Json(JSONObject jsonObj){
        try {
            getJsonData(jsonObj);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    ArrayList<double[][]> pointList = new ArrayList<>();

    public void getJsonData(JSONObject obj) throws JSONException, Exception {
        JSONObject jsonData = obj.getJSONArray("routes").getJSONObject(0);
        JSONArray sections = jsonData.getJSONArray("sections");

        for (int i = 0; i < sections.length(); i++) {
            JSONObject sectionObj = sections.getJSONObject(i);
            JSONArray roads = sectionObj.getJSONArray("roads");

            for (int j = 0; j < roads.length(); j++) {
                JSONObject roadObj = roads.getJSONObject(j);
                JSONArray list = roadObj.getJSONArray("vertexes");

                for (int k = 0; k < list.length(); k += 2) {
                    double[][] point = {{list.getDouble(k), list.getDouble(k + 1)}};
                    pointList.add(point);
                }
            }
        }
    }
}
