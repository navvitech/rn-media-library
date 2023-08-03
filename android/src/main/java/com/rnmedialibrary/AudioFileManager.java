package com.rnmedialibrary;

import static com.rnmedialibrary.Utils.formatSize;
import static com.rnmedialibrary.Utils.getColorFromUri;
import static com.rnmedialibrary.Utils.getContentUri;
import static com.rnmedialibrary.Utils.getGenre;

import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;

import java.util.ArrayList;

public class AudioFileManager {
  private ReactApplicationContext reactContext;

  AudioFileManager(ReactApplicationContext context) {
    reactContext = context;
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
    String[] projection = { MediaStore.Audio.Media._ID, MediaStore.Audio.Media.ALBUM_ID, MediaStore.Audio.Media.ALBUM,
        MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.DURATION, MediaStore.Audio.Media.ARTIST,
        MediaStore.Audio.Media.SIZE, MediaStore.Audio.Media.MIME_TYPE, };

    Cursor cursor = reactContext.getContentResolver().query(collection, projection, null, null,
        MediaStore.Audio.Media.TITLE + " ASC ");

    if (cursor != null) {
      int albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM);
      int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID);
      int albumIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID);
      int titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE);
      int durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION);
      int artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST);
      int sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE);
      int mimeTypeColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.MIME_TYPE);
      if (!cursor.moveToPosition((int) offset)) {
        return null;
      }
      int i = 0;
      while (i < limit && !cursor.isAfterLast()) {
        String _id = cursor.getString(idColumn);
        String album = cursor.getString(albumColumn);
        String title = cursor.getString(titleColumn);
        String duration = cursor.getString(durationColumn);
        String artist = cursor.getString(artistColumn);
        String size = cursor.getString(sizeColumn);
        String mimeType = cursor.getString(mimeTypeColumn);

        Uri contentUri = getContentUri(_id);
        String album_art2 = Uri.parse("content://media/external/audio/media/" + _id + "/albumart").toString();
        String formattedSize = formatSize(size, reactContext);

        String album_art1 = ContentUris.withAppendedId(albumArtUri, cursor.getLong(albumIdColumn)).toString();
        String genre = getGenre(contentUri, reactContext);
        ArrayList<String> palette = getColorFromUri(Uri.parse(album_art1), reactContext);
        AudioFile musicFile = new AudioFile(album_art1, album_art2, contentUri, title, artist, duration, _id,
            formattedSize, album, genre, mimeType, palette);
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
}
