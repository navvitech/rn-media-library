interface AssetConfig {
  limit?: number;
  offset?: number;
}

interface Asset {
  album: string;
  artist: string;
  artwork: string;
  artwork2: string;
  duration: string;
  genre: string;
  _id: string;
  mimeType: string;
  palette: Array<string>;
  path: string;
  size: string;
  title: string;
}

interface Assets {
  assets: Array<Asset>;
  totalCount: number;
  endCursor: number;
  hasNextPage: boolean;
}

export type { AssetConfig, Asset, Assets };
