import * as React from 'react';
import {
  StyleSheet,
  View,
  Text,
  FlatList,
  Image,
  useWindowDimensions,
  ActivityIndicator,
} from 'react-native';
import { getAssets } from 'rn-media-library';
import type { Assets, Asset } from 'src/types';

export default function App() {
  const [assets, setAssets] = React.useState<Assets>();
  const [loading, setLoading] = React.useState<boolean>(false);
  const { width } = useWindowDimensions();

  React.useEffect(() => {
    setLoading(true);
    getAssets({ mediaType: 'audio' }).then((res) => {
      setAssets(JSON.parse(res));
      setLoading(false);
    });
  }, []);

  const _renderItem = ({ item }: { item: Asset }) => {
    const { artwork, title, artist } = item;
    return (
      <View style={styles.container}>
        <View>
          <Image
            style={styles.artwork}
            source={{
              uri: artwork,
            }}
            resizeMode="contain"
          />
        </View>
        <View
          style={[
            styles.textContainer,
            {
              maxWidth: width * 0.7,
            },
          ]}
        >
          <Text
            numberOfLines={1}
            style={[
              styles.title,
              { color: item.palette ? item.palette[0] : 'white' },
            ]}
          >
            {title}
          </Text>
          <Text numberOfLines={1} style={styles.artist}>
            {artist}
          </Text>
        </View>
      </View>
    );
  };

  return (
    <View>
      {loading ? (
        <ActivityIndicator
          size="large"
          color="red"
          style={[styles.flexCenter, { backgroundColor: 'rgb(18,18,18)' }]}
        />
      ) : (
        <FlatList
          data={assets?.assets}
          keyExtractor={(item) => item._id}
          renderItem={_renderItem}
          contentContainerStyle={styles.flatListContainer}
        />
      )}
    </View>
  );
}

const styles = StyleSheet.create({
  flatListContainer: {
    padding: 10,
    backgroundColor: 'rgb(18,18,18)',
  },
  container: {
    paddingVertical: 10,
    flexDirection: 'row',
    alignItems: 'center',
  },
  artwork: {
    width: 70,
    height: 70,
    marginRight: 10,
    borderRadius: 10,
  },
  textContainer: {
    width: '100%',
  },
  artist: {
    fontSize: 16,
    color: 'gray',
  },
  title: {
    fontSize: 18,
    marginBottom: 5,
  },
  flexCenter: {
    flexDirection: 'row',
    justifyContent: 'center',
    alignItems: 'center',
    height: '100%',
  },
});
