package com.rnmedialibrary;

class Artist {
  private String _id;
  private String name;
  private int numTracks;
  private int numAlbums;

  Artist(String _id, String name, int numTracks, int numAlbums) {
    this._id = _id;
    this.name = name;
    this.numTracks = numTracks;
    this.numAlbums = numAlbums;
  }
}
