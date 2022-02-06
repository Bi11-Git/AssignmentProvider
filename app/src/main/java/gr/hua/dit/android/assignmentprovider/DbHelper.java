package gr.hua.dit.android.assignmentprovider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DbHelper extends SQLiteOpenHelper {

    public static String DB_NAME = "GeofenceDB";
    public static String TABLE_NAME = "coordinates";
    public static String FIELD_1 = "lat";
    public static String FIELD_2 = "lon";
    public static String FIELD_3 = "actions";
    public static String FIELD_4 = "timestamp";

    private String SQL_QUERY = "CREATE TABLE " + TABLE_NAME + " (" + FIELD_1 + " TEXT, " + FIELD_2 +
            " TEXT, "+ FIELD_3 + " TEXT, "+FIELD_4+" TEXT)";

    public DbHelper(@Nullable Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public long insertCoordinates(GeofenceData geofenceData){
        ContentValues values = new ContentValues();
        values.put(FIELD_1, geofenceData.getLat());
        values.put(FIELD_2, geofenceData.getLon());
        values.put(FIELD_3, geofenceData.getAction());
        values.put(FIELD_4, geofenceData.getTimestamp());
        long id = this.getWritableDatabase().insert(TABLE_NAME,null,values);
        return id;
    }

    public Cursor selectAll(){
        return this.getReadableDatabase().query(TABLE_NAME,null,null,null,null,null,null);
    }

    public Cursor selectCoordinatesById(long id){
        return this.getReadableDatabase().query(TABLE_NAME,null,"rowid=?",new String[]{id+""},null,null,null);
    }
}
