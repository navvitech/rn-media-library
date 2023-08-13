package com.rnmedialibrary;

public class AudioFileInfo {
  private final String _id;
  private final String title;
  private final String displayName;
  private final String artist;
  private final String size;
  private final String duration;
  private final String album;
  private final String mimeType;
  private final String path;
  private final String uri;
  private final String artwork;
  private final String artwork2;
  private final String genre;

  public AudioFileInfo(String _id, String title, String displayName, String artist, String size, String duration, String album, String mimeType, String path, String uri, String artwork, String artwork2, String genre) {
    this._id = _id;
    this.title = title;
    this.displayName = displayName;
    this.artist = artist;
    this.size = size;
    this.duration = duration;
    this.album = album;
    this.mimeType = mimeType;
    this.path = path;
    this.uri = uri;
    this.artwork = artwork;
    this.artwork2 = artwork2;
    this.genre = genre;
  }
}
