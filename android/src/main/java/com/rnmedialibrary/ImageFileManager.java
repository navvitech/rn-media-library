package com.rnmedialibrary;

import static com.rnmedialibrary.Utils.formatSize;

import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;

import com.facebook.react.bridge.ReactApplicationContext;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;

public class ImageFileManager {
  private final ReactApplicationContext reactContext;
  private final Gson gson = new Gson();

  ImageFileManager(ReactApplicationContext context) {
    reactContext = context;
  }

  private Data<ImageFile> getImages(double limit, double offset, String selection, String[] selectionArgs) {
    ArrayList<ImageFile> tempImagesList = new ArrayList<>();
    Uri collection;
    Data<ImageFile> data = null;
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
      collection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
    } else {
      collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    }
    String[] projection = {MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA, MediaStore.Images.Media.DISPLAY_NAME, MediaStore.Images.Media.MIME_TYPE, MediaStore.Images.Media.DATE_ADDED, MediaStore.Images.Media.DATE_MODIFIED, MediaStore.Images.Media.SIZE, MediaStore.Images.Media.WIDTH, MediaStore.Images.Media.HEIGHT,};

    Cursor cursor = reactContext.getContentResolver().query(collection, projection, selection, selectionArgs, null);

    if (limit == 0) {
      limit = cursor.getCount();
    }

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

        Uri contentUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, Long.parseLong(_id));

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

  public String getData(double limit, double offset) {
    return gson.toJson(getImages(limit, offset, null, null));
  }

  String getImageAlbums() {
    HashSet<ImageAlbum> tempImageAlbumsList = new HashSet<>();
    Uri collection;
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
      collection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
    } else {
      collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    }
    String[] projection = {MediaStore.Images.Media.BUCKET_ID, MediaStore.Images.Media.BUCKET_DISPLAY_NAME};

    Cursor cursor = reactContext.getContentResolver().query(collection, projection, null, null, null);

    if (cursor != null) {
      int idColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID);
      int bucketDisplayNameColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);

      while (cursor.moveToNext()) {
        String _id = cursor.getString(idColumnIndex);
        String displayName = cursor.getString(bucketDisplayNameColumnIndex);
        ImageAlbum imageAlbum = new ImageAlbum(_id, displayName);
        tempImageAlbumsList.add(imageAlbum);
      }

      cursor.close();
    }
    return gson.toJson(tempImageAlbumsList);
  }

  String getAlbumImages(String bucketId) {
    String selection = MediaStore.Images.Media.BUCKET_ID + " = ?";
    String[] selectionArgs = new String[]{String.valueOf(bucketId)};
    Data<ImageFile> albumImages = getImages(0, 0, selection, selectionArgs);
    Log.d("dfg", gson.toJson(albumImages) + "" + bucketId);
    return gson.toJson(albumImages);
  }

  class ImageAlbum {
    final String _id;
    final String displayName;

    ImageAlbum(String _id, String displayName) {
      this._id = _id;
      this.displayName = displayName;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      ImageAlbum imgAlbum = (ImageAlbum) o;
      return _id.equals(imgAlbum._id);
    }

    @Override
    public int hashCode() {
      return Objects.hash(_id);
    }
  }
}
