package com.example.ovidiupodina.emotion.tasks.configApp;

import android.os.AsyncTask;
import android.util.Log;

import com.example.ovidiupodina.emotion.data.Storage;
import com.example.ovidiupodina.emotion.geofacing.Punct;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ConfigAppTask extends AsyncTask<String, Void, String> {

    private HttpURLConnection connection;
    private IConfigApp iConfigApp;

    public ConfigAppTask(IConfigApp iConfigApp) {
        this.iConfigApp = iConfigApp;
    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            connection = (HttpURLConnection) new URL(strings[0]).openConnection();
            connection.setReadTimeout(15000);
            connection.setConnectTimeout(15000);
            connection.setRequestMethod("GET");

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    sb.append(line);
                }
                in.close();

                return sb.toString();
            }
            Log.e(ConfigAppTask.class.getSimpleName(), connection.getResponseMessage());
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(ConfigAppTask.class.getSimpleName(), e.getMessage());
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

        return null;
    }

    @Override
    protected void onPostExecute(String res) {
        super.onPostExecute(res);

        if (res != null) {
            try {
                JSONObject object = new JSONObject(res);
                JSONObject pulseObject = (JSONObject) object.get("bloodPressurePulseRate");

                Storage.getInstance()
                        .setConfig(
                                object.getInt("dataSendInterval"),
                                object.getInt("idClient"),
                                pulseObject.getInt("interval"),
                                object.getInt("locationSendInterval"),
                                pulseObject.getInt("maxValue"),
                                pulseObject.getInt("minValue"),
                                object.getInt("idPersoana"),
                                object.getDouble("safeLatitude"),
                                object.getDouble("safeLongitude")
                        );


                String safeArea = object.getString("geolocationSafeArea");

                if (safeArea != null) {

                    Storage.getInstance().setGeofacing(true);
                    Storage.getInstance().setDistance(false);

                    String[] points = safeArea.split(",");

                    for (String p : points) {
                        String[] point = p.split(" ");
                        if (point.length > 1) {
                            Storage.getInstance().getGeofancing().addNewPoint(new Punct(Double.parseDouble(point[1]), Double.parseDouble(point[0])));
                        }
                    }
                }

                JSONObject safeDistance = object.getJSONObject("geolocationSafeDistance");

                if (safeDistance != null) {
                    Storage.getInstance().setGeofacing(false);
                    Storage.getInstance().setDistance(true);
                    Storage.getInstance().setDistancePoint(new Punct(Storage.getInstance().getConfig().safeLatitude, Storage.getInstance().getConfig().safeLongitude));
                    Storage.getInstance().setRaza(safeDistance.getInt("maxValue"));
                }

            } catch (JSONException e) {
                Log.e(ConfigAppTask.class.getSimpleName(), e.getMessage());
                e.printStackTrace();
            }
        }

        iConfigApp.saveSafeZone("" + Storage.getInstance().getConfig().safeLatitude, "" + Storage.getInstance().getConfig().safeLongitude);
        iConfigApp.onGetLoc();
        iConfigApp.onGetPulse();
        iConfigApp.onSaveData();
        iConfigApp.onSendData();
    }
}
