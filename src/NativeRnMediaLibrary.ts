import type { TurboModule } from 'react-native';
import { TurboModuleRegistry } from 'react-native';

export interface Spec extends TurboModule {
  getAssets(assetConfig: Object): Promise<String>;
  getConstants: () => {
    MEDIA_TYPE: Object;
  };
  getAudioFileInfo(audioId: String): Promise<string>;
  getAlbums(albumConfig: Object): Promise<string>;
  getAlbumAssets(assetConfig: Object, id: String): Promise<string>;
  getArtists: Promise<string>;
  getArtistAudio(artistId: String): Promise<string>;
  getGenres: Promise<string>;
  getGenreAudio(genreId: String, assetConfig: Object): Promise<string>;
  getFoldersAndFiles(path: String): Promise<string>;
}

export default TurboModuleRegistry.getEnforcing<Spec>('RnMediaLibrary');
