package com.rnmedialibrary;

import static com.rnmedialibrary.Utils.formatSize;
import static com.rnmedialibrary.Utils.getColorFromUri;
import static com.rnmedialibrary.Utils.getContentUri;
import static com.rnmedialibrary.Utils.getGenre;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.content.IntentSender;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.BaseActivityEventListener;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReadableArray;

import java.io.File;
import java.util.ArrayList;

public class AudioFileManager {

  private final ReactApplicationContext reactContext;
  private Promise deletionPromise;

  AudioFileManager(ReactApplicationContext context) {
    reactContext = context;
    ActivityEventListener mActivityEventListener = new BaseActivityEventListener() {
      @Override
      public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent intent) {
        if (requestCode == Constants.DELETE_AUDIO_REQUEST_CODE) {
          if (deletionPromise != null) {
            if (resultCode == Activity.RESULT_CANCELED) {
              deletionPromise.resolve(false);
              Toast.makeText(activity, "fail", Toast.LENGTH_SHORT).show();
            } else if (resultCode == Activity.RESULT_OK) {
              showSuccessToaster(activity);
              deletionPromise.resolve(true);
            }
            deletionPromise = null;
          }
        }
      }
    };
    context.addActivityEventListener(mActivityEventListener);
  }

  public void showSuccessToaster(Activity activity) {
    Toast.makeText(activity, "deleted", Toast.LENGTH_SHORT).show();
  }

  // Function to delete an audio file in Android version 10 and below
  private void deleteAudioAPI29(String _id) {
    Activity currentActivity = reactContext.getCurrentActivity();
    ContentResolver resolver = reactContext.getContentResolver();

    Uri contentUri = getContentUri(_id);
    String selection = MediaStore.Audio.Media._ID + " = ?";
    String[] selectionArgs = new String[]{_id};
    String[] projection = {MediaStore.Audio.Media.DATA, MediaStore.Audio.Media._ID};
    Uri collection = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

    Cursor cursor = reactContext.getContentResolver().query(collection, projection, selection, selectionArgs, null);
    if (cursor != null) {
      int pathColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
      while (cursor.moveToNext()) {
        String path = cursor.getString(pathColumn);
        File file = new File(path);
        if (file.exists() && file.delete()) {
          int numDeleted = resolver.delete(contentUri, selection, selectionArgs);
          if (numDeleted > 0) {
            showSuccessToaster(currentActivity);
            cursor.close();
            deletionPromise.resolve(true);
            return;
          }
        }
      }
    }
  }

  public void deleteAudio(String _id, Promise promise) {
    Activity currentActivity = reactContext.getCurrentActivity();
    ContentResolver resolver = reactContext.getContentResolver();
    deletionPromise = promise;

    if (currentActivity == null) {
      Log.e(null, "Activity doesn't exist");
      return;
    }

    Uri contentUri = getContentUri(_id);
    String selection = MediaStore.Audio.Media._ID + " = ?";
    String[] selectionArgs = new String[]{_id};

    ArrayList<Uri> uri = new ArrayList<>();
    uri.add(contentUri);

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
      try {
        int numDeleted = resolver.delete(contentUri, selection, selectionArgs);
        if (numDeleted > 0) {
          deletionPromise.resolve(true);
          showSuccessToaster(currentActivity);
          return;
        }
      } catch (SecurityException securityException) {
        IntentSender intentSender = MediaStore.createDeleteRequest(resolver, uri).getIntentSender();
        try {
          currentActivity.startIntentSenderForResult(intentSender, 7170, null, 0, 0, 0, null);
        } catch (IntentSender.SendIntentException e) {
          throw new RuntimeException(e);
        }
      }
    } else {
      deleteAudioAPI29(_id);
    }
  }

  public void deleteManyAudio(ReadableArray uriList, Promise promise) {
    Activity currentActivity = reactContext.getCurrentActivity();
    ContentResolver resolver = reactContext.getContentResolver();
    deletionPromise = promise;

    if (currentActivity == null) {
      Log.e(null, "Activity doesn't exist");
      return;
    }

    ArrayList<Uri> uriToDelete = new ArrayList<>();
    uriList.toArrayList().forEach(element -> {
      uriToDelete.add(Uri.parse(element.toString()));
    });

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
      IntentSender intentSender = MediaStore.createDeleteRequest(resolver, uriToDelete).getIntentSender();
      try {
        currentActivity.startIntentSenderForResult(intentSender, 7170, null, 0, 0, 0, null);
      } catch (IntentSender.SendIntentException e) {
        throw new RuntimeException(e);
      }
      promise.resolve(true);
    } else {
      promise.reject("ERROR", "METHOD_NOT_ALLOWED");
    }
  }

  private Data<AudioFile> getAudioFiles(double limit, double offset) {
    ArrayList<AudioFile> tempAudioList = new ArrayList<>();
    Data<AudioFile> data = null;
    final Uri albumArtUri = Uri.parse("content://media/external/audio/albumart");
    Uri collection;
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
      collection = MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
    } else {
      collection = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
    }

    String[] projection = {MediaStore.Audio.Media._ID, MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.DISPLAY_NAME, MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.DURATION, MediaStore.Audio.Media.ALBUM, MediaStore.Audio.Media.ALBUM_ID, MediaStore.Audio.Media.DATA,};

    Cursor cursor = reactContext.getContentResolver().query(collection, projection, null, null, MediaStore.Audio.Media.TITLE + " ASC ");
    if (cursor != null) {
      int idColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID);
      int titleColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE);
      int displayNameColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME);
      int artistColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST);
      int durationColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION);
      int albumColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM);
      int albumIdColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID);
      int pathColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);

      if (!cursor.moveToPosition((int) offset)) {
        return null;
      }
      int i = 0;
      while (i < limit && !cursor.isAfterLast()) {
        String _id = cursor.getString(idColumnIndex);
        String title = cursor.getString(titleColumnIndex);
        String displayName = cursor.getString(displayNameColumnIndex);
        String artist = cursor.getString(artistColumnIndex);
        String duration = cursor.getString(durationColumnIndex);
        String album = cursor.getString(albumColumnIndex);
        long albumId = cursor.getLong(albumIdColumnIndex);
        String path = cursor.getString(pathColumnIndex);

        Uri contentUri = getContentUri(_id);
        String album_art1 = ContentUris.withAppendedId(albumArtUri, albumId).toString();
        String album_art2 = Uri.parse("content://media/external/audio/media/" + _id + "/albumart").toString();
        ArrayList<String> palette = getColorFromUri(Uri.parse(album_art1), reactContext);

        AudioFile musicFile = new AudioFile(_id, title, displayName, artist, duration, album, path, contentUri.toString(), album_art1, album_art2, palette);
        tempAudioList.add(musicFile);
        cursor.moveToNext();
        i++;
      }

      data = new Data<>(tempAudioList, !cursor.isAfterLast(), cursor.getPosition(), cursor.getCount());
      cursor.close();
    }
    return data;
  }

  public Data<AudioFile> getData(double limit, double offset) {
    return getAudioFiles(limit, offset);
  }

  public void shareSong(String _id, Promise promise) {
    Uri uri = getContentUri(_id);
    Activity currentActivity = reactContext.getCurrentActivity();
    if (currentActivity == null) {
      Log.e(null, "Activity doesn't exist");
      return;
    }
    Intent shareIntent = new Intent();
    shareIntent.setAction(Intent.ACTION_SEND);
    shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
    shareIntent.setType("audio/*");
    currentActivity.startActivity(Intent.createChooser(shareIntent, null));
  }

  public AudioFileInfo getAudioFileInfo(String id) {
    Uri collection;
    AudioFileInfo audioFile = null;
    final Uri albumArtUri = Uri.parse("content://media/external/audio/albumart");

    String selection = MediaStore.Audio.Media._ID + " = ?";
    String[] selectionArgs = new String[]{id};

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
      collection = MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
    } else {
      collection = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
    }

    String[] projection = {MediaStore.Audio.Media._ID, MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.DISPLAY_NAME, MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.SIZE, MediaStore.Audio.Media.DURATION, MediaStore.Audio.Media.ALBUM, MediaStore.Audio.Media.ALBUM_ID, MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.MIME_TYPE,};

    Cursor cursor = reactContext.getContentResolver().query(collection, projection, selection, selectionArgs, null);
    if (cursor != null) {
      int idColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID);
      int titleColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE);
      int displayNameColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME);
      int artistColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST);
      int sizeColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE);
      int durationColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION);
      int albumColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM);
      int albumIdColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID);
      int mimeTypeColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.MIME_TYPE);
      int pathColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);

      while (cursor.moveToNext()) {
        String _id = cursor.getString(idColumnIndex);
        String title = cursor.getString(titleColumnIndex);
        String displayName = cursor.getString(displayNameColumnIndex);
        String artist = cursor.getString(artistColumnIndex);
        String size = cursor.getString(sizeColumnIndex);
        String duration = cursor.getString(durationColumnIndex);
        String album = cursor.getString(albumColumnIndex);
        long albumId = cursor.getLong(albumIdColumnIndex);
        String mimeType = cursor.getString(mimeTypeColumnIndex);
        String path = cursor.getString(pathColumnIndex);

        Uri contentUri = getContentUri(_id);
        String album_art1 = ContentUris.withAppendedId(albumArtUri, albumId).toString();
        String album_art2 = Uri.parse("content://media/external/audio/media/" + _id + "/albumart").toString();
        String formattedSize = formatSize(size, reactContext);
        String genre = getGenre(contentUri, reactContext);

        audioFile = new AudioFileInfo(_id, title, displayName, artist, formattedSize, duration, album, mimeType, path, contentUri.toString(), album_art1, album_art2, genre);

        cursor.moveToNext();
      }

      cursor.close();
    }

    return audioFile;
  }
}
