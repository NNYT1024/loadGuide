package com.map.loadguied_v2.apiPackage;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class findAddress_Json {//주소검색 반환값 정리

    public findAddress_Json(String jsonString){
        try {
            JSONObject jObject = new JSONObject(jsonString);

            getJsonData(jObject); //여기서 멈춤
            Log.d("getJsonData후",jsonString);
        } catch (JSONException e) {
            Log.e("문자열 입력 에러",e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public HashMap<String,String> getAddressIndexData(int index){
        HashMap<String,String> result = new HashMap<String,String>();
        document doc = documents.get(index);
        result.put("x",doc.x);
        result.put("y",doc.y);

        result.put("address_name",doc.address_name);
        result.put("address_type",doc.address_type);

        return result;
    }

    public findAddress_Json(JSONObject obj){
        getJsonData(obj);
    }

    public void getJsonData(JSONObject obj){
        try {
            JSONObject metaData = obj.getJSONObject("meta");
            JSONArray documentData = obj.getJSONArray("documents");
//            Log.d("metaData 데이터",metaData.toString());
//            Log.d("documentData 데이터","" + documentData.get(0).toString());
//            JSONObject documentData0 = (JSONObject)documentData.get(0);
//            Iterator i = documentData0.keys(); // key값들을 모두 얻어옴.
//
//            while(i.hasNext())
//            {
//                String b = i.next().toString();
//                Log.d("키 목록", b+ ":" + documentData0.getString(b));
//            }
            setMetaData(metaData);
            Log.d("실행 테스트","1");
            setDocuments(documentData);
            Log.d("실행 테스트","2");
        } catch (JSONException e) {
            Log.e("반환데이터 분류 에러",e.getMessage());
            setMetaData(new meta());
            setDocuments(new document());
            
            throw new RuntimeException(e);
        } catch(Exception e){

            Log.e("반환데이터 Exception 에러",e.getMessage());
        }
        //여기 실행 안됨

        for(document doc : documents){
            Log.d("documents 변환",doc.toString());
        }

    }

    meta metaData;
    ArrayList<document> documents = new ArrayList<document>();

    public meta getMetaData() {
        return metaData;
    }

    public void setMetaData(JSONObject obj) {
        this.metaData = new meta(obj);
    }
    public void setMetaData(meta metaData) {
        this.metaData = metaData;
    }
    public ArrayList<document> getDocuments() {
        return documents;
    }

    public void setDocuments(JSONArray arr) {
        for(int i = 0;i<arr.length();i++){

            Log.d("setDocuments 실행",i + "");
            try {
                JSONObject obj = (JSONObject)arr.get(i);
                document doc = new document(obj);
                documents.add(doc);
            } catch (JSONException e) {
                setDocuments(new document());

                Log.d("setDocuments 에러","JSONException");
                //throw new RuntimeException(e);
                break;
                //throw new RuntimeException(e);
            }catch (Exception e) {
                setDocuments(new document());

                Log.d("setDocuments 에러","Exception");
                //throw new RuntimeException(e);
                break;
            }
        }
    }
    public void setDocuments(document doc) {
        documents.clear();
        documents.add(new document());
    }
    public class meta{
        @Override
        public String toString() {
            return "meta{" +
                    "total_count=" + total_count +
                    ", pageable_count=" + pageable_count +
                    ", is_end=" + is_end +
                    '}';
        }

        int total_count; //[필수]검색어에 검색된 문서 수
        int pageable_count; //[필수]total_count 중 노출 가능 문서 수, 최대 45
        boolean is_end; //[필수]현재 페이지가 마지막 페이지인지 여부, 값이 false면 page를 증가시켜 다음 페이지를 요청할 수 있음
        public meta(JSONObject obj) {
            try{
                setTotal_count(obj.getInt("total_count"));
                setPageable_count(obj.getInt("pageable_count"));
                setIs_end(obj.getBoolean("is_end"));
            } catch (JSONException e) {
                setTotal_count(-1);
                setPageable_count(-1);
                setIs_end(true);
                //throw new RuntimeException(e);
            }
        }
        public meta() {
            setTotal_count(-1);
            setPageable_count(-1);
            setIs_end(true);
        }
        public boolean isIs_end() {
            return is_end;
        }

        public void setIs_end(boolean is_end) {
            this.is_end = is_end;
        }

        public int getPageable_count() {
            return pageable_count;
        }

        public void setPageable_count(int pageable_count) {
            this.pageable_count = pageable_count;
        }

        public int getTotal_count() {
            return total_count;
        }

        public void setTotal_count(int total_count) {
            this.total_count = total_count;
        }

    }

    public class document{
        String address_name; //[필수]전체 지번 주소 또는 전체 도로명 주소. 입력에 따라 결정됨
        String address_type; //[필수]address_name의 값의 타입(Type). REGION(지명), ROAD(도로명), REGION_ADDR(지번 주소), ROAD_ADDR (도로명 주소) 중 하나
        String x; //[필수]X 좌표값, 경위도인 경우 longitude (경도)
        String y; //[필수]Y 좌표값, 경위도인 경우 latitude (위도)

        public document(JSONObject obj) {

            try{
                setAddress_name(obj.getString("address_name"));
                setAddress_type(obj.getString("address_type"));
                setX(obj.getString("x"));
                setY(obj.getString("y"));
            } catch (JSONException e) {
                setAddress_name("none");
                setAddress_type("none");
                setX("-1.0");
                setY("-1.0");
                //throw new RuntimeException(e);
            }
            Log.d("document 생성자",toString());
        }
        public document() {
            setAddress_name("none");
            setAddress_type("none");
            setX("-1.0");
            setY("-1.0");
        }
        @Override
        public String toString() {
            return "document{" +
                    "address_name='" + address_name + '\'' +
                    ", address_type='" + address_type + '\'' +
                    ", x='" + x + '\'' +
                    ", y='" + y + '\'' +
                    '}';
        }

        public String getAddress_name() {
            return address_name;
        }

        public void setAddress_name(String address_name) {
            this.address_name = address_name;
        }

        public String getAddress_type() {
            return address_type;
        }

        public void setAddress_type(String address_type) {
            this.address_type = address_type;
        }

        public String getX() {
            return x;
        }

        public void setX(String x) {
            this.x = x;
        }

        public String getY() {
            return y;
        }

        public void setY(String y) {
            this.y = y;
        }
    }
}
