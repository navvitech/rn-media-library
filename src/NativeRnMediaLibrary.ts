import type { TurboModule } from 'react-native';
import { TurboModuleRegistry } from 'react-native';

export interface Spec extends TurboModule {
  getAssets(assetConfig: Object): Promise<String>;
  getConstants: () => {
    MEDIA_TYPE: Object;
  };
}

export default TurboModuleRegistry.getEnforcing<Spec>('RnMediaLibrary');
