package com.rnmedialibrary;

import static com.rnmedialibrary.Utils.formatSize;
import static com.rnmedialibrary.Utils.getColorFromUri;
import static com.rnmedialibrary.Utils.getContentUriById;
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
  private void deleteAudioAPI29(String audioId) {
    Activity currentActivity = reactContext.getCurrentActivity();
    ContentResolver resolver = reactContext.getContentResolver();

    Uri contentUri = getContentUriById(audioId);
    String selection = MediaStore.Audio.Media._ID + " = ?";
    String[] selectionArgs = new String[] { audioId };
    String[] projection = { MediaStore.Audio.Media.DATA, MediaStore.Audio.Media._ID };
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

  public void deleteAudio(String audioId, Promise promise) {
    Activity currentActivity = reactContext.getCurrentActivity();
    ContentResolver resolver = reactContext.getContentResolver();
    deletionPromise = promise;

    if (currentActivity == null) {
      Log.e(null, "Activity doesn't exist");
      return;
    }

    Uri contentUri = getContentUriById(audioId);
    String selection = MediaStore.Audio.Media._ID + " = ?";
    String[] selectionArgs = new String[] { audioId };

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
      deleteAudioAPI29(audioId);
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

  private Data<AudioFile> getAudioFiles(int limit, int offset, String selection, String[] selectionArgs) {
    Uri collection;
    Data<AudioFile> data = null;
    ArrayList<AudioFile> tempAudioList = new ArrayList<>();
    final Uri albumArtUri = Uri.parse("content://media/external/audio/albumart");

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
      collection = MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
    } else {
      collection = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
    }

    String[] projection = { MediaStore.Audio.Media._ID, MediaStore.Audio.Media.TITLE,
        MediaStore.Audio.Media.DISPLAY_NAME, MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.DURATION,
        MediaStore.Audio.Media.ALBUM, MediaStore.Audio.Media.ALBUM_ID, MediaStore.Audio.Media.DATA };
    Cursor cursor = reactContext.getContentResolver().query(collection, projection, selection, selectionArgs,
        MediaStore.Audio.Media.TITLE + " ASC ");

    if (limit == 0) {
      limit = cursor.getCount();
    }

    if (cursor != null) {
      int idColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID);
      int titleColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE);
      int displayNameColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME);
      int artistColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST);
      int durationColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION);
      int albumColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM);
      int albumIdColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID);
      int pathColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);

      if (!cursor.moveToPosition(offset)) {
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

        Uri contentUri = getContentUriById(_id);
        String album_art1 = ContentUris.withAppendedId(albumArtUri, albumId).toString();
        String album_art2 = Uri.parse("content://media/external/audio/media/" + _id + "/albumart").toString();
        ArrayList<String> palette = getColorFromUri(Uri.parse(album_art1), reactContext);

        AudioFile musicFile = new AudioFile(_id, title, displayName, artist, duration, album, path,
            contentUri.toString(), album_art1, album_art2, palette);
        tempAudioList.add(musicFile);
        cursor.moveToNext();
        i++;
      }

      data = new Data<>(tempAudioList, !cursor.isAfterLast(), cursor.getPosition(), cursor.getCount());
      cursor.close();
    }
    return data;
  }

  public Data<AudioFile> getData(int limit, int offset) {
    return getAudioFiles(limit, offset, null, null);
  }

  public void shareSong(String audioId, Promise promise) {
    Uri uri = getContentUriById(audioId);
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

  public AudioFileInfo getAudioFileInfo(String audioId) {
    Uri collection;
    AudioFileInfo audioFile = null;
    final Uri albumArtUri = Uri.parse("content://media/external/audio/albumart");

    String selection = MediaStore.Audio.Media._ID + " = ?";
    String[] selectionArgs = new String[] { audioId };

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
      collection = MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
    } else {
      collection = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
    }

    String[] projection = { MediaStore.Audio.Media._ID, MediaStore.Audio.Media.TITLE,
        MediaStore.Audio.Media.DISPLAY_NAME, MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.SIZE,
        MediaStore.Audio.Media.DURATION, MediaStore.Audio.Media.ALBUM, MediaStore.Audio.Media.ALBUM_ID,
        MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.MIME_TYPE };
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
        long size = cursor.getLong(sizeColumnIndex);
        String duration = cursor.getString(durationColumnIndex);
        String album = cursor.getString(albumColumnIndex);
        long albumId = cursor.getLong(albumIdColumnIndex);
        String mimeType = cursor.getString(mimeTypeColumnIndex);
        String path = cursor.getString(pathColumnIndex);

        Uri contentUri = getContentUriById(_id);
        String album_art1 = ContentUris.withAppendedId(albumArtUri, albumId).toString();
        String album_art2 = Uri.parse("content://media/external/audio/media/" + _id + "/albumart").toString();
        String formattedSize = formatSize(size, reactContext);
        String genre = getGenre(contentUri, reactContext);

        audioFile = new AudioFileInfo(_id, title, displayName, artist, formattedSize, duration, album, mimeType, path,
            contentUri.toString(), album_art1, album_art2, genre);

        cursor.moveToNext();
      }

      cursor.close();
    }

    return audioFile;
  }

  public ArrayList<AudioAlbum> getAudioAlbums() {
    Uri uri;
    ArrayList<AudioAlbum> tempAlbumList = new ArrayList<>();
    final Uri albumArtUri = Uri.parse("content://media/external/audio/albumart");

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
      uri = MediaStore.Audio.Albums.getContentUri(MediaStore.VOLUME_EXTERNAL);
    } else {
      uri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
    }

    String[] projection = { MediaStore.Audio.Albums._ID, MediaStore.Audio.Albums.ALBUM, MediaStore.Audio.Albums.ARTIST,
        MediaStore.Audio.Albums.NUMBER_OF_SONGS, };

    Cursor cursor = reactContext.getContentResolver().query(uri, projection, null, null,
        MediaStore.Audio.Media.ALBUM + " ASC ");
    if (cursor != null) {
      int idColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums._ID);
      int titleColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM);
      int artistColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ARTIST);
      int numSongsColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.NUMBER_OF_SONGS);

      while (cursor.moveToNext()) {
        String _id = cursor.getString(idColumnIndex);
        String title = cursor.getString(titleColumnIndex);
        String artist = cursor.getString(artistColumnIndex);
        int numSongs = cursor.getInt(numSongsColumnIndex);

        String album_art = ContentUris.withAppendedId(albumArtUri, cursor.getLong(idColumnIndex)).toString();
        AudioAlbum albums = new AudioAlbum(_id, title, album_art, artist, numSongs);
        tempAlbumList.add(albums);
      }
      cursor.close();
    }
    return tempAlbumList;
  }

  public Data<AudioFile> getAlbumAudio(String albumId) {
    String selection = MediaStore.Audio.Media.ALBUM_ID + " = ?";
    String[] selectionArgs = new String[] { albumId };
    return getAudioFiles(0, 0, selection, selectionArgs);
  }

  public ArrayList<Artist> getArtists() {
    Uri uri;
    ArrayList<Artist> tempArtistList = new ArrayList<>();

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
      uri = MediaStore.Audio.Artists.getContentUri(MediaStore.VOLUME_EXTERNAL);
    } else {
      uri = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI;
    }

    String[] projection = { MediaStore.Audio.Artists._ID, MediaStore.Audio.Artists.ARTIST,
        MediaStore.Audio.Artists.NUMBER_OF_TRACKS, MediaStore.Audio.Artists.NUMBER_OF_ALBUMS, };

    Cursor cursor = reactContext.getContentResolver().query(uri, projection, null, null,
        MediaStore.Audio.Artists.ARTIST + " ASC ");

    if (cursor != null) {
      int idColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Artists._ID);
      int nameColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Artists.ARTIST);
      int numTracksColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Artists.NUMBER_OF_TRACKS);
      int numAlbumsColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Artists.NUMBER_OF_ALBUMS);

      while (cursor.moveToNext()) {
        String _id = cursor.getString(idColumnIndex);
        String name = cursor.getString(nameColumnIndex);
        int numTracks = cursor.getInt(numTracksColumnIndex);
        int numAlbums = cursor.getInt(numAlbumsColumnIndex);

        Artist artist = new Artist(_id, name, numTracks, numAlbums);
        tempArtistList.add(artist);
      }
      cursor.close();
    }
    return tempArtistList;
  }

  public Data<AudioFile> getArtistAudio(String artistId) {
    String selection = MediaStore.Audio.Media.ARTIST_ID + " = ?";
    String[] selectionArgs = new String[] { artistId };
    return getAudioFiles(0, 0, selection, selectionArgs);
  }

  public ArrayList<Genre> getGenres() {
    Uri uri, genreSongsUri;
    ArrayList<Genre> tempArtistList = new ArrayList<>();
    final Uri albumArtUri = Uri.parse("content://media/external/audio/albumart");

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
      uri = MediaStore.Audio.Genres.getContentUri(MediaStore.VOLUME_EXTERNAL);
    } else {
      uri = MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI;
    }

    String[] projection = { MediaStore.Audio.Genres._ID, MediaStore.Audio.Genres.NAME };
    Cursor cursor = reactContext.getContentResolver().query(uri, projection, null, null,
        MediaStore.Audio.Genres.NAME + " ASC ");
    if (cursor != null) {
      int idColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Genres._ID);
      int nameColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Genres.NAME);

      while (cursor.moveToNext()) {
        String _id = cursor.getString(idColumnIndex);
        String name = cursor.getString(nameColumnIndex);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
          genreSongsUri = MediaStore.Audio.Genres.Members.getContentUri(MediaStore.VOLUME_EXTERNAL,
              Long.parseLong(_id));
        } else {
          genreSongsUri = MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI;
        }

        String[] songProjection = { MediaStore.Audio.Media.ALBUM_ID };
        Cursor songCursor = reactContext.getContentResolver().query(genreSongsUri, songProjection, null, null, null);

        int songCount = 0;
        String artwork = "";

        if (songCursor != null && songCursor.moveToFirst()) {
          int albumIdColumnIndex = songCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID);
          long albumId = songCursor.getLong(albumIdColumnIndex);

          artwork = ContentUris.withAppendedId(albumArtUri, albumId).toString();
          songCount = songCursor.getCount();
          songCursor.close();
        }

        Genre genre = new Genre(_id, name, songCount, artwork);
        tempArtistList.add(genre);
      }
      cursor.close();
    }
    return tempArtistList;
  }

  public Data<AudioFile> getGenreAudio(String genreId, int limit, int offset) {
    Data<AudioFile> data = null;
    ArrayList<AudioFile> tempAudioList = new ArrayList<>();
    final Uri albumArtUri = Uri.parse("content://media/external/audio/albumart");
    Uri uri = MediaStore.Audio.Genres.Members.getContentUri("external", Long.parseLong(genreId));

    String[] projection = { MediaStore.Audio.Media._ID, MediaStore.Audio.Media.TITLE,
        MediaStore.Audio.Media.DISPLAY_NAME, MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.DURATION,
        MediaStore.Audio.Media.ALBUM, MediaStore.Audio.Media.ALBUM_ID, MediaStore.Audio.Media.DATA, };
    Cursor cursor = reactContext.getContentResolver().query(uri, projection, null, null, null);

    if (cursor != null) {
      int idColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID);
      int titleColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE);
      int displayNameColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME);
      int artistColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST);
      int durationColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION);
      int albumColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM);
      int albumIdColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID);
      int pathColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);

      if (!cursor.moveToPosition(offset)) {
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

        Uri contentUri = getContentUriById(_id);
        String album_art1 = ContentUris.withAppendedId(albumArtUri, albumId).toString();
        String album_art2 = Uri.parse("content://media/external/audio/media/" + _id + "/albumart").toString();
        ArrayList<String> palette = getColorFromUri(Uri.parse(album_art1), reactContext);

        AudioFile musicFile = new AudioFile(_id, title, displayName, artist, duration, album, path,
            contentUri.toString(), album_art1, album_art2, palette);
        tempAudioList.add(musicFile);
        cursor.moveToNext();
        i++;
      }

      data = new Data<>(tempAudioList, !cursor.isAfterLast(), cursor.getPosition(), cursor.getCount());
      cursor.close();
    }
    return data;
  }
}
