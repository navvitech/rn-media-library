import { NativeModules, Platform } from 'react-native';
import type { AssetConfig } from './types';

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

export function deleteAsset(_id: string): Promise<string> {
  return RnMediaLibrary.deleteAsset(_id);
}

export function shareAsset(_id: string): Promise<string> {
  return RnMediaLibrary.shareAsset(_id);
}

export function getAudioFileInfo(_id: string): Promise<string> {
  return RnMediaLibrary.getAudioFileInfo(_id);
}

export function deleteManyAudio(uriList: Array<String>): Promise<string> {
  return RnMediaLibrary.deleteManyAudio(uriList);
}
