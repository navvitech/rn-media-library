package com.rnmedialibrary;

public class Genre {
  private String id;
  private String genre;
  private int numTracks;
  private String artwork;

  public Genre(String genre_id, String genre, int numTracks, String artwork) {
    this.genre = genre;
    this.id = genre_id;
    this.numTracks = numTracks;
    this.artwork = artwork;
  }
}
