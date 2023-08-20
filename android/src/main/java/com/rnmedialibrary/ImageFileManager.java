package com.rnmedialibrary;

import static com.rnmedialibrary.Utils.formatSize;
import static com.rnmedialibrary.Utils.getContentUriById;

import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import com.facebook.react.bridge.ReactApplicationContext;

import java.util.ArrayList;

public class ImageFileManager {
  private final ReactApplicationContext reactContext;

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
    String[] projection = {MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA, MediaStore.Images.Media.DISPLAY_NAME, MediaStore.Images.Media.MIME_TYPE, MediaStore.Images.Media.DATE_ADDED, MediaStore.Images.Media.DATE_MODIFIED, MediaStore.Images.Media.SIZE, MediaStore.Images.Media.WIDTH, MediaStore.Images.Media.HEIGHT,};

    Cursor cursor = reactContext.getContentResolver().query(collection, projection, null, null, null);

    if (cursor != null) {
      int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
      int dateAddedColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED);
      int dateModifiedColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_MODIFIED);
      int displayNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
      int mimeTypeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.MIME_TYPE);
      int sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE);
      int widthColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.WIDTH);
      int heightColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.HEIGHT);
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
        long size = cursor.getLong(sizeColumn);
        String width = cursor.getString(widthColumn);
        String height = cursor.getString(heightColumn);

        Uri contentUri = getContentUriById(_id);

        String formattedSize = formatSize(size, reactContext);

        ImageFile imageFile = new ImageFile(_id, displayName, contentUri.toString(), formattedSize, mimeType, "resolution", dateAdded, dateModified, width, height);
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
