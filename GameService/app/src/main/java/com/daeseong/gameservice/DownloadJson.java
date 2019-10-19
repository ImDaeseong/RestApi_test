package com.daeseong.gameservice;

import android.os.AsyncTask;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONObject;

public class DownloadJson  extends AsyncTask<Void, Void, String> {

    private static final String TAG = DownloadJson.class.getSimpleName();

    private String sUrl = "http://110.9.68.64:8080/api/AllList";

    @Override
    protected String doInBackground(Void... voids) {

        String sResult = HttpUtil.GetGameDataResult(sUrl);
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
