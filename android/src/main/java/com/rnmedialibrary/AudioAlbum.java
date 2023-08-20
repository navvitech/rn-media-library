package com.rnmedialibrary;

class AudioAlbum {
  private String _id;
  private String title;
  private String artwork;
  private String artist;
  private int numSongs;

   AudioAlbum(String _id, String title, String artwork, String artist, int numSongs) {
    this._id = _id;
    this.title = title;
    this.artwork = artwork;
    this.artist = artist;
    this.numSongs = numSongs;
  }
}
