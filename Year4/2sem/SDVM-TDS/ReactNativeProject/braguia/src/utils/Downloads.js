import RNFetchBlob from 'rn-fetch-blob';
import {Platform, PermissionsAndroid} from 'react-native';

/// grant permission in android
export const requestStoragePermission = async () => {
  try {
    const granted = await PermissionsAndroid.request(
      PermissionsAndroid.PERMISSIONS.WRITE_EXTERNAL_STORAGE,
    );
    if (granted === PermissionsAndroid.RESULTS.GRANTED) {
      console.log('Storage permission granted');
      return true;
    } else {
      console.log('Storage permission denied');
      return false;
    }
  } catch (err) {
    console.warn(err);
    return false;
  }
};

export const downloadFile = async fileUrl => {
  let granted = await requestStoragePermission();
  
  if (!granted) {
    // Permissão negada inicialmente, exibir pop-up
    Alert.alert(
      'Permissão necessária',
      'Para baixar arquivos, é necessário permitir o acesso ao armazenamento.',
      [
        { text: 'Cancelar', onPress: () => console.log('Cancel Pressed'), style: 'cancel' },
        { text: 'Configurações', onPress: () => openAppSettings() },
      ],
      { cancelable: false }
    );
  } else {
    // Permissão concedida, iniciar o download
    console.log(`A iniciar download do arquivo: ${fileUrl}`);
    // Continuar com o código para download aqui...
  }

  const openAppSettings = () => {
    if (Platform.OS === 'ios') {
      Linking.openURL('app-settings:');
    } else {
      Linking.openSettings();
    }
  };

  console.log(`Iniciando download do arquivo: ${fileUrl}`);
  const {dirs} = RNFetchBlob.fs;
  const dirToSave =
    Platform.OS === 'ios' ? dirs.DocumentDir : dirs.DownloadDir;
  const fileName = fileUrl.split('/').pop();

  const configfb = {
    fileCache: true,
    addAndroidDownloads: {
      useDownloadManager: true,
      notification: true,
      mediaScannable: true,
      title: fileName,
      path: `${dirToSave}/${fileName}`,
      mime: 'application/octet-stream', // Fallback MIME type
      description: 'Downloading file.',
    },
    path: `${dirToSave}/${fileName}`,
    mime: 'application/octet-stream',
  };

  const configOptions = Platform.select({
    ios: configfb,
    android: configfb,
  });

  try {
    const res = await RNFetchBlob.config(configOptions || {}).fetch('GET', fileUrl, {});

    console.log('Download concluído');

    let downloadedFilePath;

    if (Platform.OS === 'ios') {
      await RNFetchBlob.fs.writeFile(configfb.path, res.data, 'base64');
      downloadedFilePath = configfb.path;
      RNFetchBlob.ios.previewDocument(configfb.path);
    } else if (Platform.OS === 'android') {
      console.log('Arquivo baixado');
      downloadedFilePath = res.path(); // Obter o caminho do arquivo baixado no Android
    }

    console.log('Caminho do arquivo baixado:', downloadedFilePath);

  } catch (e) {
    console.log('Falha no download', e);
  }

}