import type { TurboModule } from 'react-native';
import { TurboModuleRegistry } from 'react-native';

export interface Spec extends TurboModule {
  getAssets(assetConfig: Object): Promise<string>;
  getConstants: () => {
    MEDIA_TYPE: Object;
  };
  getAudioFileInfo(audioId: string): Promise<string>;
  getAlbums(albumConfig: Object): Promise<string>;
  getAlbumAssets(assetConfig: Object, id: string): Promise<string>;
  getArtists(): Promise<string>;
  getArtistAudio(artistId: string): Promise<string>;
  getGenres(): Promise<string>;
  getGenreAudio(genreId: string, assetConfig: Object): Promise<string>;
  getFoldersAndFiles(path: string): Promise<string>;
}

export default TurboModuleRegistry.getEnforcing<Spec>('RnMediaLibrary');
