# rn-media-library

⚠️ This library is under development ⚠️

A React Native module that allows you to fetch and manage audios, images, vidoes.

## Installation

```sh
npm install rn-media-library
```

## Usage

```js
import { getAssets } from 'rn-media-library';

// ...

getAssets({ mediaType: 'audio' }).then((res) => {
    //parse and set the value in the state
    setAssets(JSON.parse(res));
});
```

## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT

---

Made with [create-react-native-library](https://github.com/callstack/react-native-builder-bob)
