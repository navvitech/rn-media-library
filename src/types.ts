interface AssetConfig {
  limit?: number;
  offset?: number;
  mediaType?: string;
}

interface Asset {
  _id: string;
  title: string;
  displayName: string;
  artist: string;
  duration: string;
  album: string;
  path: string;
  uri: string;
  artwork: string;
  artwork2: string;
  mimeType: string;
  palette: Array<string>;
}

interface AssetInfo {
  _id: string;
  title: string;
  displayName: string;
  artist: string;
  size: string;
  duration: string;
  album: string;
  mimeType: string;
  path: string;
  uri: string;
  artwork: string;
  artwork2: string;
  genre: string;
}

interface Assets {
  assets: Array<Asset>;
  totalCount: number;
  endCursor: number;
  hasNextPage: boolean;
}

interface Error {
  nativeStackAndroid: string;
  userInfo: string;
  message: string;
  code: string;
}

export type { AssetConfig, Asset, Assets, Error, AssetInfo };
