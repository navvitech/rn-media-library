import { NativeModules, Platform } from 'react-native';
import type { AssetConfig, AlbumConfig } from './types';

const LINKING_ERROR =
  `The package 'rn-media-library' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo Go\n';

// @ts-expect-error
const isTurboModuleEnabled = global.__turboModuleProxy != null;

const RnMediaLibraryModule = isTurboModuleEnabled
  ? require('./NativeRnMediaLibrary').default
  : NativeModules.RnMediaLibrary;

const RnMediaLibrary = RnMediaLibraryModule
  ? RnMediaLibraryModule
  : new Proxy(
      {},
      {
        get() {
          throw new Error(LINKING_ERROR);
        },
      }
    );

export function getAssets(assetConfig: AssetConfig): Promise<string> {
  return RnMediaLibrary.getAssets(assetConfig);
}

export function deleteAsset(assetId: string): Promise<string> {
  return RnMediaLibrary.deleteAsset(assetId);
}

export function shareAsset(assetId: string): Promise<string> {
  return RnMediaLibrary.shareAsset(assetId);
}

export function getAudioFileInfo(audioId: string): Promise<string> {
  return RnMediaLibrary.getAudioFileInfo(audioId);
}

export function deleteManyAudio(uriList: Array<String>): Promise<string> {
  return RnMediaLibrary.deleteManyAudio(uriList);
}

export function getAlbums(albumConfig: AlbumConfig): Promise<string> {
  return RnMediaLibrary.getAlbums(albumConfig);
}

export function getAlbumAssets(
  assetConfig: AssetConfig,
  id: String
): Promise<string> {
  return RnMediaLibrary.getAlbumAssets(assetConfig, id);
}

export function getArtists(): Promise<string> {
  return RnMediaLibrary.getArtists();
}

export function getArtistAudio(artistId: String): Promise<string> {
  return RnMediaLibrary.getArtistAudio(artistId);
}

export function getGenres(): Promise<string> {
  return RnMediaLibrary.getGenres();
}

export function getGenreAudio(
  genreId: String,
  assetConfig: AssetConfig
): Promise<string> {
  return RnMediaLibrary.getGenreAudio(genreId, assetConfig);
}

export function getFoldersAndFiles(path: String): Promise<string> {
  return RnMediaLibrary.getFoldersAndFiles(path);
}

export function requestAllFileAccessPermission(): Promise<void> {
  return RnMediaLibrary.requestAllFileAccessPermission();
}

export function checkAllFileAccessPermission(): Promise<boolean> {
  return RnMediaLibrary.checkAllFileAccessPermission();
}
