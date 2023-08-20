package com.rnmedialibrary;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;

import com.facebook.react.bridge.ReactApplicationContext;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Objects;


class FileManager {
  private final ReactApplicationContext reactContext;
  private final Gson gson = new Gson();

  FileManager(ReactApplicationContext context) {
    reactContext = context;
  }

  public String getAllRootDirectories() {
    class Root {
      final String path;
      final String parent;
      final String name;
      final String freeSpace;
      final String usableSpace;
      final String totalSpace;

      Root(String path, String parent, String name, String freeSpace, String usableSpace, String totalSpace) {
        this.path = path;
        this.parent = parent;
        this.name = name;
        this.freeSpace = freeSpace;
        this.usableSpace = usableSpace;
        this.totalSpace = totalSpace;
      }
    }

    StorageManager storageManager = (StorageManager) reactContext.getSystemService(Context.STORAGE_SERVICE);
    StorageVolume[] storageVolumes = storageManager.getStorageVolumes().toArray(new StorageVolume[0]);
    ArrayList<Root> tempDirectoryList = new ArrayList<>();

    for (StorageVolume storageVolume : storageVolumes) {
      File rootDirectory = null;
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        rootDirectory = storageVolume.getDirectory();
      }
      if (rootDirectory != null) {

        String path = rootDirectory.getAbsolutePath();
        String parent = rootDirectory.getParent();
        String name = rootDirectory.getName();
        String freeSpace = Utils.formatSize(rootDirectory.getFreeSpace(), reactContext);
        String usableSpace = Utils.formatSize(rootDirectory.getUsableSpace(), reactContext);
        String totalSpace = Utils.formatSize(rootDirectory.getTotalSpace(), reactContext);

        Root rootDirectoryInfo = new Root(path, parent, name, freeSpace, usableSpace, totalSpace);
        tempDirectoryList.add(rootDirectoryInfo);
      }
    }
    return gson.toJson(tempDirectoryList);
  }

  public void requestAllFileAccess() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
      if (!Environment.isExternalStorageManager()) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
        Objects.requireNonNull(reactContext.getCurrentActivity()).startActivity(shareIntent);
      }
    }
  }

  public String getFoldersFilesInsideDirectory(String path) {
    File directory = new File(path);
    File[] files = directory.listFiles();
    ArrayList<FileInfo> tempFiles = fetchFilesFromFolder(directory.getName() + "/");
    ArrayList<Folder> tempFolderList = new ArrayList<>();

    for (File file : files) {
      if (file.isDirectory()) {
        Path filePath = Paths.get(path);
        BasicFileAttributes attributes = null;
        try {
          attributes = Files.readAttributes(filePath, BasicFileAttributes.class);
        } catch (IOException e) {
          throw new RuntimeException(e);
        }

        // Extract and format metadata
        long createdAt = attributes.creationTime().toMillis();
        long accessedAt = attributes.lastAccessTime().toMillis();
        long modifiedAt = attributes.lastModifiedTime().toMillis();
        int count = Utils.getItemCount(file.getAbsolutePath());
        String name = file.getName();
        String parent = file.getParent();
        Folder folder = new Folder(name, file.getAbsolutePath(), parent, count, createdAt, modifiedAt, accessedAt);
        tempFolderList.add(folder);
      }
    }

    ArrayList<Asset> combinedList = new ArrayList<>();
    combinedList.addAll(tempFiles);
    combinedList.addAll(tempFolderList);

    return gson.toJson(combinedList);
  }

  private ArrayList<FileInfo> fetchFilesFromFolder(String relPath) {
    ArrayList<FileInfo> tempFilesList = new ArrayList<>();
    try {
      String[] projection = {MediaStore.Files.FileColumns._ID, MediaStore.Files.FileColumns.MEDIA_TYPE, MediaStore.Files.FileColumns.DISPLAY_NAME, MediaStore.Files.FileColumns.MIME_TYPE, MediaStore.Files.FileColumns.DATE_ADDED, MediaStore.Files.FileColumns.DATE_MODIFIED, MediaStore.Files.FileColumns.SIZE,};
      String selection = MediaStore.Files.FileColumns.RELATIVE_PATH + "=? AND " + MediaStore.Files.FileColumns.MEDIA_TYPE + "!=" + MediaStore.Files.FileColumns.MEDIA_TYPE_NONE;

      String[] selectionArgs = new String[]{relPath};

      Uri filesUri = MediaStore.Files.getContentUri("external");

      Cursor cursor = reactContext.getContentResolver().query(filesUri, projection, selection, selectionArgs, null);

      if (cursor != null) {
        while (cursor.moveToNext()) {
          int idColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID);
          int mediaTypeColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MEDIA_TYPE);
          int displayNameColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME);
          int mimeTypeColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MIME_TYPE);
          int dateAddedColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_ADDED);
          int dateModifiedColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_MODIFIED);
          int sizeColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE);

          String _id = cursor.getString(idColumnIndex);
          String mediaType = cursor.getString(mediaTypeColumnIndex);
          String displayName = cursor.getString(displayNameColumnIndex);
          String mimeType = cursor.getString(mimeTypeColumnIndex);
          long dateAdded = cursor.getLong(dateAddedColumnIndex);
          long dateModified = cursor.getLong(dateModifiedColumnIndex);
          long size = cursor.getLong(sizeColumnIndex);

          String formattedSize = Utils.formatSize(size, reactContext);
          Uri uri = Utils.getContentUriById(_id);

          FileInfo file = new FileInfo(_id, displayName, mimeType, dateAdded, dateModified, formattedSize, uri.toString());
          tempFilesList.add(file);
        }
        cursor.close();
      }
    } catch (Exception e) {
      Log.e("files", e.toString());
    }
    return tempFilesList;
  }

  class Asset {
    private final String title;
    private final long createdAt;
    private final long modifiedAt;
    private final boolean isFolder;

    Asset(String title, long createdAt, long modifiedAt, boolean isFolder) {
      this.title = title;
      this.createdAt = createdAt;
      this.modifiedAt = modifiedAt;
      this.isFolder = isFolder;
    }
  }

  class FileInfo extends Asset {
    private final String _id;
    private final String mimeType;

    private final String uri;
    private final String size;


    FileInfo(String id, String title, String mimeType, long dateAdded, long dateModified, String uri, String size) {
      super(title, dateAdded, dateModified, false);
      _id = id;
      this.mimeType = mimeType;
      this.uri = uri;
      this.size = size;
    }
  }

  class Folder extends Asset {
    private final String path;
    private final String parent;
    private final int count;
    private final long accessedAt;

    private final String relativePath;

    Folder(String title, String path, String parent, int count, long createdAt, long modifiedAt, long accessedAt) {
      super(title, createdAt, modifiedAt, true);
      this.path = path;
      this.parent = parent;
      this.count = count;
      this.accessedAt = accessedAt;
      this.relativePath = title + "/";
    }
  }
}
