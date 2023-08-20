package com.rnmedialibrary;

import java.util.ArrayList;

class AudioFile {
  private final String _id;
  private final String title;
  private final String displayName;
  private final String artist;
  private final String duration;
  private final String album;
  private final String path;
  private final String uri;
  private final String artwork;
  private final String artwork2;
  private final ArrayList<String> palette;

   AudioFile(String _id, String title, String displayName, String artist, String duration, String album,
      String path, String uri, String artwork, String artwork2, ArrayList<String> palette) {
    this._id = _id;
    this.title = title;
    this.displayName = displayName;
    this.artist = artist;
    this.duration = duration;
    this.album = album;
    this.path = path;
    this.uri = uri;
    this.artwork = artwork;
    this.artwork2 = artwork2;
    this.palette = palette;
  }
}
