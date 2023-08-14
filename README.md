# rn-media-library

⚠️ This library is under development ⚠️

A React Native module that allows you to fetch and manage audios, images, vidoes.


## Installation

```sh
npm install rn-media-library
```

# Documentation
Check out my dedicated documentation page for all the information about this library, API's and more:

[Documentation](https://rn-media-library-docs.vercel.app/)

## Usage

```js
import { getAssets } from 'rn-media-library';
 
// ...
 
// call the function inside the useEffect on mount and set the state
getAssets({ mediaType: 'audio' })
  .then((res) => {
    //parse and set the value in the state
    setAssets(JSON.parse(res));
  })
  .catch((error) => {
    console.log(error);
  });
```

## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT


