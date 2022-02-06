package gr.hua.dit.android.assignmentprovider;

import android.content.BroadcastReceiver;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;

import java.util.Date;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

        Location location = geofencingEvent.getTriggeringLocation();
        Date ts = new Date( System.currentTimeMillis());

        ContentResolver resolver = context.getApplicationContext().getContentResolver();
        ContentValues values = new ContentValues(4);

        if(geofencingEvent.hasError()) {
            String errorMessage = GeofenceStatusCodes.getStatusCodeString(geofencingEvent.getErrorCode());
            Log.e("BroadcastReceiver" , errorMessage);
        }

//       -----------------------Enter-------------------------------------
        if(geofencingEvent.getGeofenceTransition() == Geofence.GEOFENCE_TRANSITION_ENTER) {

            values.put("lat", String.valueOf(location.getLatitude()) );
            values.put("lon", String.valueOf(location.getLongitude()));
            values.put("actions", "Enter ");
            values.put("timestamp", ts.toString());

            Uri result = resolver.insert(Uri.parse(MyContentProvider.CONTENT_URI+"/coordinates"), values);

            System.out.println("-------------- Enter");

        }

        //--------------Exit---------------------------
        if(geofencingEvent.getGeofenceTransition() == Geofence.GEOFENCE_TRANSITION_EXIT) {

            values.put("lat", String.valueOf(location.getLatitude()) );
            values.put("lon", String.valueOf(location.getLongitude()));
            values.put("actions", "Exit ");
            values.put("timestamp", ts.toString());

            Uri result = resolver.insert(Uri.parse(MyContentProvider.CONTENT_URI+"/coordinates"), values);

            System.out.println("-------------------------------Exit");
        }
    }
}