const { getDefaultConfig, mergeConfig } = require('@react-native/metro-config');

module.exports = mergeConfig(getDefaultConfig(__dirname), {
  transformer: {
    babelTransformerPath: require.resolve('react-native-svg-transformer'),
    getTransformOptions: async () => ({
      transform: {
        experimentalImportSupport: false,
        inlineRequires: true,
      },
    }),
  },
  resolver: {
    assetExts: ['bmp', 'gif', 'jpeg', 'jpg', 'png', 'webp'],
    sourceExts: ['js', 'json', 'ts', 'tsx', 'jsx', 'svg'],
  },
});
