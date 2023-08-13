package com.rnmedialibrary;

import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.format.Formatter;
import android.util.Log;

import androidx.palette.graphics.Palette;

import com.facebook.react.bridge.ReactApplicationContext;

import java.util.ArrayList;

public class Utils {
  public static Uri getContentUri(String _id) {
    Uri contentUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, Long.parseLong(_id));
    return contentUri;
  }

  public static String formatSize(String size, ReactApplicationContext context) {
    String formattedSize = Formatter.formatFileSize(context, Long.parseLong(size));
    return formattedSize;
  }

  public static String getGenre(Uri audioUri, ReactApplicationContext context) {
    MediaMetadataRetriever retriever = new MediaMetadataRetriever();
    retriever.setDataSource(context, audioUri);
    String Genre = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE);
    return Genre;
  }

  private static Palette createPaletteSync(Bitmap bitmap) {
    return Palette.from(bitmap).generate();
  }

  private static String extractRGBA(int p) {
    int green = Color.green(p);
    int red = Color.red(p);
    int blue = Color.blue(p);
    int alpha = Color.alpha(p);
    final String format = String.format("rgba(%d, %d, %d, %d)", red, green, blue, alpha);
    return format;
  }

  public static ArrayList<String> getColorFromUri(Uri uri, Context mContext) {
    try {
      Bitmap bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), uri);
      ArrayList<String> colors = new ArrayList<>();
      int lightVibrantColor = createPaletteSync(bitmap).getLightVibrantColor(0);
      int darkVibrantColor = createPaletteSync(bitmap).getDarkVibrantColor(0);

      colors.add(extractRGBA(lightVibrantColor));
      colors.add(extractRGBA(darkVibrantColor));

      return colors;
    } catch (Exception e) {
      Log.e("bitmap failure", e.toString());
      return null;
    }
  }
}
