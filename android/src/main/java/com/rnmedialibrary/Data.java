package com.rnmedialibrary;

import java.util.ArrayList;

public class Data<T> {
  private final ArrayList<T> assets;
  private final boolean hasNextPage;
  private final int endCursor;
  private final int totalCount;

   Data(ArrayList<T> assets, boolean hasNextPage, int endCursor, int totalCount) {
    this.assets = assets;
    this.hasNextPage = hasNextPage;
    this.endCursor = endCursor;
    this.totalCount = totalCount;
  }
}
