package com.rnmedialibrary;

public class RootDirectories {
    final String path;
    final String parent;
    final String name;
    final String freeSpace;
    final String usableSpace;
    final String totalSpace;

  RootDirectories(String path, String parent, String name, String freeSpace, String usableSpace, String totalSpace) {
      this.path = path;
      this.parent = parent;
      this.name = name;
      this.freeSpace = freeSpace;
      this.usableSpace = usableSpace;
      this.totalSpace = totalSpace;
  }
}
