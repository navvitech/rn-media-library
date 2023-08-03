import { NativeModules, Platform } from 'react-native';

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

export function multiply(a: number, b: number): Promise<number> {
  return RnMediaLibrary.multiply(a, b);
}
