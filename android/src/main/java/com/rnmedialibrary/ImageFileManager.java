package com.rnmedialibrary;

import static com.rnmedialibrary.Utils.formatSize;
import static com.rnmedialibrary.Utils.getContentUri;

import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import com.facebook.react.bridge.ReactApplicationContext;

import java.util.ArrayList;

public class ImageFileManager {
  private ReactApplicationContext reactContext;

  ImageFileManager(ReactApplicationContext context) {
    reactContext = context;
  }

  private Data<ImageFile> getImages(double limit, double offset) {
    ArrayList<ImageFile> tempImagesList = new ArrayList<>();
    Uri collection;
    Data<ImageFile> data = null;
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
      collection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
    } else {
      collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    }
    String[] projection = { MediaStore.Images.Media._ID, MediaStore.Images.Media.DATE_ADDED,
        MediaStore.Images.Media.DATE_MODIFIED, MediaStore.Images.Media.DISPLAY_NAME, MediaStore.Images.Media.MIME_TYPE,
        MediaStore.Images.Media.RESOLUTION, MediaStore.Images.Media.SIZE };

    Cursor cursor = reactContext.getContentResolver().query(collection, projection, null, null,
        MediaStore.Images.Media.TITLE + " ASC ");

    if (cursor != null) {
      int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
      int dateAddedColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED);
      int dateModifiedColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_MODIFIED);
      int displayNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
      int mimeTypeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.MIME_TYPE);
      int resolutionColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.RESOLUTION);
      int sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE);
      if (!cursor.moveToPosition((int) offset)) {
        return null;
      }
      int i = 0;
      while (i < limit && !cursor.isAfterLast()) {
        String _id = cursor.getString(idColumn);
        String dateAdded = cursor.getString(dateAddedColumn);
        String dateModified = cursor.getString(dateModifiedColumn);
        String displayName = cursor.getString(displayNameColumn);
        String mimeType = cursor.getString(mimeTypeColumn);
        String resolution = cursor.getString(resolutionColumn);
        String size = cursor.getString(sizeColumn);

        Uri contentUri = getContentUri(_id);

        String formattedSize = formatSize(size, reactContext);

        ImageFile imageFile = new ImageFile(_id, displayName, contentUri, formattedSize, mimeType, resolution,
            dateAdded, dateModified);
        tempImagesList.add(imageFile);
        cursor.moveToNext();
        i++;
      }

      data = new Data<>(tempImagesList, !cursor.isAfterLast(), cursor.getPosition(), cursor.getCount());
      cursor.close();
    }
    return data;
  }

  public Data<ImageFile> getData(double limit, double offset) {
    return getImages(limit, offset);
  }
}
