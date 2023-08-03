package com.rnmedialibrary;

import android.net.Uri;

import java.util.ArrayList;

public class AudioFile {
  private final String artwork;
  private final String artwork2;
  private final String path;
  private final String title;
  private final String artist;
  private final String album;
  private final String duration;
  private final String _id;
  private final String size;
  private final String genre;

  private final String mimeType;

  private final ArrayList<String> palette;

  public AudioFile(String album_art1, String album_art2, Uri path, String title, String artist, String duration,
      String _id, String size, String album, String genre, String mimeType, ArrayList<String> palette) {
    this.artwork = album_art1;
    this.artwork2 = album_art2;
    this.path = String.valueOf(path);
    this.title = title;
    this.artist = artist;
    this.album = album;
    this.duration = duration;
    this._id = _id;
    this.size = size;
    this.genre = genre;
    this.mimeType = mimeType;
    this.palette = palette;
  }
}
