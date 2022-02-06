package gr.hua.dit.android.assignmentprovider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class MyContentProvider extends ContentProvider {

    private static UriMatcher uriMatcher;
    private DbHelper dbHelper;
    static final String AUTHORITY = "gr.hua.dit.android.assignmentprovider";
    static final String CONTENT_URI = "content://" + AUTHORITY;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, "coordinates", 1);
        uriMatcher.addURI(AUTHORITY, "coordinates/#", 2);
    }

    public MyContentProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        Uri result = null;
        switch (uriMatcher.match(uri)) {
            case 1:
                GeofenceData geofenceData = new GeofenceData(values.getAsString("lat")
                        , values.getAsString("lon"), values.getAsString("actions"), values.getAsString("timestamp") );
                Long id = dbHelper.insertCoordinates(geofenceData);
                result = Uri.parse(AUTHORITY+"/coordinates"+ id);
        }
        return result;
    }

    @Override
    public boolean onCreate() {
        dbHelper = new DbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection
            , @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor result = null;

        switch (uriMatcher.match(uri)) {
            case 1:
                result = dbHelper.selectAll();
                break;
            case 2:
                result = dbHelper.selectCoordinatesById(Integer.parseInt(uri.getLastPathSegment()));
                break;
        }

        return result;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}