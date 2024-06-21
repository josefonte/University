import React, {useEffect, useRef, useState} from 'react';
import {RouteProp, useNavigation} from '@react-navigation/native';
import {
  SafeAreaView,
  StyleSheet,
  TextInput,
  Text,
  View,
  useColorScheme,
  Button,
  TouchableOpacity,
  Image,
  ScrollView,
  PermissionsAndroid,
  Platform,
  Linking,
  Dimensions,
  Alert,
} from 'react-native';
import {Media, Pin, Trail} from '../model/model';
import Feather from 'react-native-vector-icons/Feather';
import Ionicons from 'react-native-vector-icons/Ionicons';
import Octicons from 'react-native-vector-icons/Octicons';
import MaterialIcons from 'react-native-vector-icons/MaterialCommunityIcons';
import {fetchTrails} from '../redux/actions';
import {AppDispatch, RootState} from '../redux/store';
import {useDispatch, useSelector} from 'react-redux';
import {useAppSelector, useAppDispatch} from '../redux/hooks';
import Sound from 'react-native-sound';
import Video, {VideoRef} from 'react-native-video';
import {Q} from '@nozbe/watermelondb';
import database from '../model/database';
import {aViajar, acabeiViajar} from './../redux/actions';
import BackgroundGeolocation from '@mauron85/react-native-background-geolocation';
// SVG

// COMPONENTES
import MapScreen from '../components/mapScreen';
import RNFS from 'react-native-fs';
import RNFetchBlob from 'rn-fetch-blob';
import {darkModeTheme, lightModeTheme} from '../utils/themes';
import {ScreenHeight, ScreenWidth} from '@rneui/themed/dist/config';
import EncryptedStorage from 'react-native-encrypted-storage';
import { downloadFile } from '../utils/Downloads';
const PontoDeInteresseDetail = ({
  route,
}: {
  route: RouteProp<
    {PontoDeInteresseDetail: {pin: Pin}},
    'PontoDeInteresseDetail'
  >;
}) => {
  console.log('Entrei num ponto específico');
  const {pin} = route.params;
  const isDarkMode = useColorScheme() === 'dark';

  const theme = useColorScheme() === 'dark' ? darkModeTheme : lightModeTheme;

  const backgroundColor = theme.background_color;
  const titleColor = theme.text;
  const textColor = theme.text2;
  const colorDiviver = theme.color8;
  const navigation = useNavigation();
  const trailsState = useSelector((state: RootState) => state.trails);
  const dispatch = useDispatch();

  const ButtonStartStop = (start: boolean) => {
    return (
      <View
        style={{
          width: start ? 125 : 115,
          height: 50,
          backgroundColor: start ? '#355228' : '#612828',
          borderRadius: 15,
          flexDirection: 'row',
          justifyContent: 'center',
          alignItems: 'center',
          gap: 5,
          paddingHorizontal: 10,
        }}>
        <Text
          style={{
            fontSize: 18,
            fontWeight: 600,
            color: "#FEFAE0",
            paddingLeft: 5,
          }}>
          {start ? 'Começar' : 'Acabar'}
        </Text>
        <MaterialIcons
          name={start ? 'play-outline' : 'stop'}
          size={22}
          color="#FEFAE0"
        />
      </View>
    );
  };

  // Abrir Maps
  const navigateToLocation = (latitude: number, longitude: number) => {
    BackgroundGeolocation.start();
    const url = `https://www.google.com/maps/dir/?api=1&destination=${latitude},${longitude}`;
    dispatch(aViajar());
    Linking.openURL(url);
  };

  const endViagem = () => {
    dispatch(acabeiViajar());
    BackgroundGeolocation.stop();
  }

  // Dar audio
  useEffect(() => {
    Sound.setCategory('Playback');
  }, []);

  const requestCameraPermission = async () => {
    try {
      const granted = await PermissionsAndroid.request(
        PermissionsAndroid.PERMISSIONS.RECORD_AUDIO,
        {
          title: 'Audio Permission',
          message: 'This app needs access to your microphone to play audio.',
          buttonNeutral: 'Ask Me Later',
          buttonNegative: 'Cancel',
          buttonPositive: 'OK',
        },
      );
      if (granted === PermissionsAndroid.RESULTS.GRANTED) {
        console.log('You can use the audio functionality');
      } else {
        console.log('Audio permission denied');
      }
    } catch (err) {
      console.warn(err);
    }
  };

  const playSound = async (ficheiro: string) => {
    requestCameraPermission();

    const sound = new Sound(ficheiro, undefined, error => {
      if (error) {
        console.log('Failed to load the sound', error);
        return;
      }
      sound.play(success => {
        if (success) {
          console.log('Tocar áudio');
        } else {
          console.log('Erro ao tocar o áudio...');
        }
      });
    });
  };

  async function getMediaFromPin(pinId: number): Promise<Media[]> {
    try {
      const mediaCollection = database.collections.get<Media>('media');
      const media = await mediaCollection
        .query(Q.where('media_pin', pinId))
        .fetch();

      const mediaIds = new Set(media.map(m => m.mediaId));

      const uniqueMedia = media.filter((m: {mediaId: number}) => {
        if (mediaIds.has(m.mediaId)) {
          mediaIds.delete(m.mediaId);
          return true;
        } else {
          return false;
        }
      });

      return uniqueMedia;
    } catch (error) {
      console.error('Error fetching media from database:', error);
      return [];
    }
  }

  const [media, setMedia] = useState<Media[]>([]);
  const [isPremium, setIsPremium] = useState<boolean>(false);
  const prevPinIdRef = useRef<number | null>(null);

  useEffect(() => {
    const fetchMedia = async () => {
      try {
        const mediaData = await getMediaFromPin(pin.pinId);
        
        const localMediaData = await Promise.all(
          mediaData.map(async (mediaItem) => {
            const downloadDir = RNFS.DownloadDirectoryPath;
            const filePath = `${downloadDir}/${mediaItem.mediaFile.split('/').pop()}`;
            console.log("O ficheiro se estiver downloaded está com este caminho:",filePath);
            const fileExists = await RNFS.exists(filePath);

            if (fileExists) {
              console.log("Entrei na condição se existe o ficheiro existe nos downloads");
              const updatedMediaItem = {...mediaItem, DownloadedMediaFile: `${filePath}`, mediaType: mediaItem.mediaType};
              return updatedMediaItem;
            }
            return mediaItem;
          })
        );

        console.log('Local media data:', localMediaData); // Log the local media data

        setMedia(localMediaData);
        
        const tipo = await EncryptedStorage.getItem('userType');
        if (tipo === "Premium"){
          console.log("[TIPO USER] SET PREMIUM");
          setIsPremium(true);
        }

      } catch (error) {
        console.error('Error fetching media:', error);
      }
    };

    if (pin.pinId !== prevPinIdRef.current) {
      fetchMedia();
      prevPinIdRef.current = pin.pinId;
    }
  }, [pin.pinId]);

  useEffect(() => {
    console.log("O estado media foi atualizado:", media);
    media.map((mediaItem) => {
      console.log("cccc", mediaItem.mediaType);
      console.log("aaaa", mediaItem.mediaFile);
      console.log("bbbb", mediaItem.DownloadedMediaFile);
    });

  }, [media]);


 




  return (
    <View style={[styles.container, {backgroundColor: backgroundColor}]}>
      <ScrollView showsVerticalScrollIndicator={false}>
        <View>
          <View style={{backgroundColor: backgroundColor}}>
            <View
              style={{
                position: 'absolute',
                top: 20,
                left: 15,
                zIndex: 2,
              }}>
              <TouchableOpacity onPress={() => navigation.goBack()}>
                <View
                  style={{
                    zIndex: 2,
                    borderRadius: 100,
                    backgroundColor: backgroundColor,
                    width: 48,
                    height: 48,
                    justifyContent: 'center',
                    alignItems: 'center',
                  }}>
                  <Octicons
                    name={'chevron-left'}
                    size={28}
                    color={colorDiviver}
                    style={{paddingRight: 3}}
                  />
                </View>
              </TouchableOpacity>
            </View>

            <ScrollView horizontal={true} style={styles.scrollViewPop}>
              {media.length === 0  ? (
                <View style={[styles.emptyImagens]}>
                  
                </View>
              ) : isPremium === false ? (
                <View style={[styles.emptyImagens]}>
                <MaterialIcons
                  name={'image-off-outline'}
                  size={100}
                  color={colorDiviver}
                  style={{paddingHorizontal: 10}}
                />
                <Text
                  style={[
                    {fontWeight: 500, color: colorDiviver, textAlign: 'center'},
                  ]}>
                  Media indisponível {'\n '} para utilizadores gratuitos
                </Text>
              </View>
              ): (
                media.map((mediaItem, index) => (
                  <View
                    key={index}
                    style={{flexDirection: 'column', alignItems: 'center' }}>
                    {mediaItem.mediaType === 'R' ? (
                      <TouchableOpacity
                        onPress={() => playSound(mediaItem.DownloadedMediaFile || mediaItem.mediaFile)}>
                        <View style={styles.audioRolo}>
                          <Text style={styles.audioText}>Áudio</Text>
                          <Text style={styles.audioText}>Apenas Premium</Text>
                        {mediaItem.DownloadedMediaFile && (
                          <Text style={styles.textSimple}>Arquivo baixado</Text>
                            )}
                      </View>
                      </TouchableOpacity>
                    ) : mediaItem.mediaType === 'I' ? (
                      <View>
                      <Image
                          source={{uri: mediaItem.DownloadedMediaFile ? `file://${mediaItem.DownloadedMediaFile}` : mediaItem.mediaFile}}
                          style={styles.imagemRolo}
                      />
                        {mediaItem.DownloadedMediaFile && (
                        <Text style={styles.textSimple}>Arquivo baixado</Text>
                      )}
                    </View>
                  ) : mediaItem.mediaType === 'V' ? (
                      <View>
                      <Video
                            source={{uri: mediaItem.DownloadedMediaFile || mediaItem.mediaFile}}
                          style={styles.videoRolo}
                          controls={true}
                        />
                        {mediaItem.DownloadedMediaFile && (
                        <Text style={styles.downloadedText}>Arquivo baixado</Text>
                      )}
                    </View>
                  ) : (
                      <View style={styles.imagemRolo}>
                        <Text onPress={() => playSound(mediaItem.mediaFile)}>
                          <Text style={styles.unknown}>
                          Tipo de mídia desconhecido
                        </Text>
                        </Text>
                      </View>
                    )}
                    <View style={styles.botaoDownload}>
                      <TouchableOpacity
                        onPress={() => downloadFile(mediaItem.mediaFile)}>
                        <View
                          style={{
                            backgroundColor: backgroundColor,
                            borderColor: colorDiviver,
                            borderWidth: 2,
                            height: 50,
                            width: 50,
                            alignItems: 'center',
                            borderRadius: 100,
                            justifyContent: 'center',
                          }}>
                          <Feather
                            name={'download'}
                            size={25}
                            color={colorDiviver}
                            style={{paddingHorizontal: 10}}
                          />
                        </View>
                      </TouchableOpacity>
                    </View>
                  </View>
                ))
              )}
            </ScrollView>

            {trailsState.viajar === false ? (
              <TouchableOpacity
                style={styles.botaoComecar}
                onPress={() => navigateToLocation(pin.pinLat, pin.pinLng)}>
                {ButtonStartStop(true)}
              </TouchableOpacity>
            ) : (
              <TouchableOpacity
                style={styles.botaoComecar}
                onPress={() => endViagem()}>
                {ButtonStartStop(false)}
              </TouchableOpacity>
            )}
          </View>

          <Text style={[styles.textTitulo, {color: titleColor}]}>
            {pin.pinName}
          </Text>
          <View
            style={{
              flexDirection: 'row',
              gap: 3,
              alignContent: 'center',
              alignItems: 'center',
              marginTop: 5,
              marginBottom: 20,
            }}>
            <Ionicons
              name={'location-outline'}
              size={16}
              color={titleColor}
              style={{marginLeft: 15}}
            />
            <Text style={[{color: titleColor, fontSize: 15}]}>Braga</Text>
          </View>

          <Text
            style={[
              {
                fontSize: 15,
                color: textColor,
                textAlign: 'justify',
                paddingHorizontal: 15,
                lineHeight: 20,
              },
            ]}>
            {pin.pinDesc}
          </Text>
          <Text
            style={[
              {
                color: titleColor,
                fontSize: 22,
                fontWeight: 'bold',
                marginLeft: 15,
                marginBottom: 20,
                marginTop: 20,
              },
            ]}>
            Mapa
          </Text>
        </View>

        <View style={[styles.containerMapa]}>
          <MapScreen localizacoes={[[pin.pinLat, pin.pinLng]]} />
        </View>
      </ScrollView>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
  emptyImagens: {
    alignItems: 'center',
    justifyContent: 'center',
    height: 225,
    width: ScreenWidth,
    backgroundColor: 'gray',
  },
  containerMapa: {
    marginHorizontal: 15,
    height: 350,
  },
  textSimple: {
    marginLeft: 10,
    fontFamily: 'Roboto',
    fontSize: 16,
  },
  textTitulo: {
    marginTop: 30,
    marginLeft: 15,
    fontSize: 30,
    fontFamily: 'Roboto',
    lineHeight: 32,
    fontWeight: 'bold',
    letterSpacing: 0.25,
  },
  mediaContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    position: 'relative', // Adicionado para permitir o posicionamento absoluto do botão de download
    marginVertical: 0,
  },
  botaoComecar: {
    position: 'absolute',
    bottom: -20,
    right: 20,
    alignItems: 'flex-end',
    zIndex: 2,
  },
  botaoTopo: {
    position: 'absolute',
    top: 10,
    left: 10,
    zIndex: 2,
  },
  botaoDownload: {
    position: 'absolute',
    top: 10,
    right: 10,
    zIndex: 2,
    padding: 10,
  },
  scrollViewPop: {
    flexDirection: 'row',
    zIndex: 1,
  },
  imagemRolo: {
    height: 225,
    width: ScreenWidth,
  },
  videoRolo: {
    height: 225,
    width: ScreenWidth,
  },
  audioRolo: {
    height: 225,
    width: ScreenWidth,
    backgroundColor: 'grey',
  },
  audioText: {
    fontSize: 20,
    fontFamily: 'Roboto',
    textAlign: 'center',
    textAlignVertical: 'center',
    marginTop: 30,
  },
  downloadButton: {
    position: 'absolute',
    top: 10,
    left: 10,
    zIndex: 2,
    backgroundColor: 'black',
    padding: 10,
    borderRadius: 10,
  },
  unknown:{
    fontFamily: 'Roboto',
    fontSize: 20,
    textAlign: 'center',
    textAlignVertical: 'center',
    marginTop: 50,
  },
});

export default PontoDeInteresseDetail;