package com.daeseong.gameservice;

import android.os.AsyncTask;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONObject;

public class DownloadJson  extends AsyncTask<Void, Void, String> {

    private static final String TAG = DownloadJson.class.getSimpleName();

    //사용시에는 리얼 IP 필요
    private String sUrl = "http://127.0.0.1:8080/api/AllList";

    @Override
    protected String doInBackground(Void... voids) {
        String sResult = "";
        try {
            sResult = HttpUtil.GetGameDataResult(sUrl);
        }catch (Exception ex){
            Log.e(TAG, ex.getMessage().toString());
        }
        return sResult;
    }

    @Override
    protected void onPostExecute(String sResult) {
        super.onPostExecute(sResult);

        try {
            JSONArray jsonArray = new JSONArray(sResult);
            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject jsonObject = jsonArray.getJSONObject(i);
                //Log.e( String.valueOf(i) + " - " , jsonObject.toString());

                String id = jsonObject.getString("id");
                String packagename = jsonObject.getString("packagename");
                String gametitle = jsonObject.getString("gametitle");
                String gamedesc = jsonObject.getString("gamedesc");
                iteminfo.getInstance().setGameItem(packagename);
            }
        }catch (Exception ex){
            Log.e(TAG, ex.getMessage().toString());
        }
    }

}
