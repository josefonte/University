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
import Feather from 'react-native-vector-icons/Feather';
import Ionicons from 'react-native-vector-icons/Ionicons';
import Octicons from 'react-native-vector-icons/Octicons';
import MaterialIcons from 'react-native-vector-icons/MaterialCommunityIcons';

import RNFetchBlob from 'rn-fetch-blob';
import RNFS from 'react-native-fs';
import BackgroundGeolocation from '@mauron85/react-native-background-geolocation';
import {Media, Pin, Trail, User} from '../model/model';

import {
  acabeiViajar,
  addFavoriteUser,
  addHistoricoUser,
  comecarViajar,
  removeFavoriteUser,
} from '../redux/actions';
import {AppDispatch, RootState} from '../redux/store';
import {useDispatch, useSelector} from 'react-redux';
import {useAppSelector, useAppDispatch} from '../redux/hooks';
import Sound from 'react-native-sound';
import Video, {VideoRef} from 'react-native-video';
import {Q} from '@nozbe/watermelondb';
import database from '../model/database';
import { AuthContext } from '../navigation/AuthContext';


// COMPONENTES
import MapScreen from '../components/mapScreen';
import {darkModeTheme, lightModeTheme} from '../utils/themes';
import {ScreenWidth} from '@rneui/themed/dist/config';
import PontoDeInteresse from '../components/PontoDeInteresse';
import EncryptedStorage from 'react-native-encrypted-storage';
import { downloadFile } from '../utils/Downloads';

const changeDifficulty = (dificuldade: string) => {
  if (dificuldade === 'E') {
    return 'Fácil';
  }
  if (dificuldade === 'D') {
    return 'Fácil';
  }
  if (dificuldade === 'C') {
    return 'Médio';
  }
  if (dificuldade === 'B') {
    return 'Difícil';
  }
  if (dificuldade === 'A') {
    return 'Mt. Difícil';
  }
};

const TrailDetail = ({
  route,
}: {
  route: RouteProp<{TrailDetail: {trail: Trail}}, 'TrailDetail'>;
}) => {
  console.log('Entrei num trail específico');
  const {trail} = route.params;
  const theme = useColorScheme() === 'dark' ? darkModeTheme : lightModeTheme;

  const backgroundColor = theme.background_color;
  const titleColor = theme.text;
  const textColor = theme.text2;
  const colorDiviver = theme.color8;
  const {username} = React.useContext(AuthContext);
  const navigation = useNavigation();
  const trailsState = useSelector((state: RootState) => state.trails);
  const dispatch = useDispatch();

  const [favoriteButton, setFavoriteButton] = useState<boolean>(false);
  const [isPremium, setIsPremium] = useState<boolean>(false);

  const clickFavorite = () => {
    !favoriteButton ? addFavoriteUser(trail) : removeFavoriteUser(trail);
    setFavoriteButton(!favoriteButton);
  };

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

  // Dar audio
  useEffect(() => {
    Sound.setCategory('Playback');

    const fetchFavorite = async () => {
      if (username) {
        const usersCollection = database.collections.get<User>('users');
        const user = await usersCollection
          .query(Q.where('username', username))
          .fetch();

        if (user.length > 0) {
          const userFavorites = user[0].favorites;
          if (userFavorites.split(';').includes(trail.trailId.toString())) {
            setFavoriteButton(true);
          }
        }
      }
    };

    fetchFavorite();
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

  // Ir buscar toda a Media de um Trail
  async function getMediaFromTrail(trailId: number): Promise<Media[]> {
    try {
      // Fetch pins from the database that belong to the specified trail
      const pinCollection = database.collections.get<Pin>('pins');
      const pins = await pinCollection
        .query(Q.where('pin_trail', trailId))
        .fetch();

      const pinIds = Array.from(new Set(pins.map(pin => pin.pinId)));

      // Fetch media from the database that belong to the pins of the specified trail
      const mediaCollection = database.collections.get<Media>('media');
      const media = await mediaCollection
        .query(Q.where('media_pin', Q.oneOf(pinIds)))
        .fetch();

      const mediaIds = new Set(media.map(m => m.mediaId));

      // Filter out unique media items
      const uniqueMedia = media.filter((m: {mediaId: number}) => {
        if (mediaIds.has(m.mediaId)) {
          mediaIds.delete(m.mediaId);
          return true;
        } else {
          return false;
        }
      });

      console.log(uniqueMedia);
      return uniqueMedia;
    } catch (error) {
      console.error('Error fetching media from trail:', error);
      return [];
    }
  }

  async function getPinsFromTrail(trailId: number): Promise<Pin[]> {
    try {
      const pinCollection = database.collections.get<Pin>('pins');
      const pins = await pinCollection
        .query(Q.where('pin_trail', trailId))
        .fetch();

      const listaIds: number[] = [];
      const uniquePins = pins.filter(pin => {
        if (listaIds.includes(pin.pinId)) {
          return false;
        } else {
          listaIds.push(pin.pinId);
          return true;
        }
      });

      return uniquePins;
    } catch (error) {
      console.error('Error fetching pins from trail:', error);
      return [];
    }
  }

  const [media, setMedia] = useState<Media[]>([]);
  const [pins, setPins] = useState<Pin[]>([]);
  const [locs, setLocs] = useState<[number, number][]>([]);
  const [flag, setFlag] = useState<number>(0);
  const [flag2, setFlag2] = useState<number>(0);

  useEffect(() => {
    const fetchMedia = async () => {
      try {
        const mediaData = await getMediaFromTrail(trail.trailId);
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

        const pinsData = await getPinsFromTrail(trail.trailId);
        await setPins(pinsData);
        await setLocs(pinsData.map(pin => [pin.pinLat, pin.pinLng]));
        console.log(pinsData);
      } catch (error) {
        console.error('Error fetching media:', error);
      }
    };

    fetchMedia();
  }, [trail.id]);

  useEffect(() => {
    console.log(pins);
    if (pins.length > 0) {
      setFlag2(1);
    }
  }, [pins]);

  useEffect(() => {
    console.log(locs);
    if (locs.length > 0) {
      setFlag(1);
    }
  }, [locs]);

  // GOOGLE MAPS
  const openGoogleMapsDirections = (locations: [number, number][]) => {
    BackgroundGeolocation.start();
    const destinationString = locations
      .map(([latitude, longitude]) => `${latitude},${longitude}`)
      .join('/');
    const url = `https://www.google.com/maps/dir/${destinationString}`;

    addHistoricoUser(trail)
      .then(() => {
        console.log('Adicionado ao historico | trailId : ', trail.trailId);
      })
      .catch(error => {
        throw error;
      });
    dispatch(comecarViajar());
    console.log(url);
    Linking.openURL(url).catch(err =>
      console.error('Error opening Google Maps:', err),
    );
  };

  // End button

  const endViagem = () => {
    dispatch(acabeiViajar());
    BackgroundGeolocation.stop();
  }

  const [numerodePins, setNumeroPins] = useState(0);
  const [distancia, setDistancia] = useState(0);

  const getPinsFromTrail2 = async (): Promise<void> => {
    try {
      const pinCollection = database.collections.get<Pin>('pins');

      const pins = await pinCollection
        .query(Q.where('pin_trail', trail.trailId))
        .fetch();

      const listaIds: number[] = [];
      const uniquePins = pins.filter(pin => {
        if (listaIds.includes(pin.pinId)) {
          return false;
        } else {
          listaIds.push(pin.pinId);
          return true;
        }
      });

      setNumeroPins(uniquePins.length);
    } catch (error) {
      console.error('Error fetching pins from trail:', error);
    }
  };

  const getDistanceFromTrail = async (): Promise<void> => {
    try {
      const pinCollection = database.collections.get<Pin>('pins');
      const pins = await pinCollection
        .query(Q.where('pin_trail', trail.trailId))
        .fetch();
      const listaIds: number[] = [];
      const uniquePins = pins.filter(pin => {
        if (listaIds.includes(pin.pinId)) {
          return false;
        } else {
          listaIds.push(pin.pinId);
          return true;
        }
      });

      // Calculate total distance between pins
      let totalDistance = 0;
      for (let i = 0; i < uniquePins.length - 1; i++) {
        const pin1 = uniquePins[i];
        const pin2 = uniquePins[i + 1];
        const distance = calculateDistance(
          pin1.pinLat,
          pin1.pinLng,
          pin2.pinLat,
          pin2.pinLng,
        );
        totalDistance += distance;
      }

      setDistancia(parseFloat(totalDistance.toFixed(2)));
    } catch (error) {
      console.error('Error fetching pins from trail:', error);
    }
  };

  // Function to calculate distance between two points using Haversine formula
  const calculateDistance = (
    lat1: number,
    lon1: number,
    lat2: number,
    lon2: number,
  ): number => {
    const R = 6371; // Radius of the earth in km
    const dLat = deg2rad(lat2 - lat1);
    const dLon = deg2rad(lon2 - lon1);
    const a =
      Math.sin(dLat / 2) * Math.sin(dLat / 2) +
      Math.cos(deg2rad(lat1)) *
        Math.cos(deg2rad(lat2)) *
        Math.sin(dLon / 2) *
        Math.sin(dLon / 2);
    const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    const d = R * c; // Distance in km
    return d;
  };

  const deg2rad = (deg: number): number => {
    return deg * (Math.PI / 180);
  };

  useEffect(() => {
    const fetchData = async () => {
      try {
        await getPinsFromTrail2();
        await getDistanceFromTrail();
        const tipo = await EncryptedStorage.getItem('userType');
        if (tipo === "Premium"){
          console.log("[TIPO USER] SET PREMIUM");
          setIsPremium(true);
        }
      } catch (error) {
        console.error('Error fetching pins:', error);
        // Handle error if needed
      }
    };

    fetchData(); // Call fetchData when the component mounts
  }, []);

  
  return (
    <View style={[styles.container, {backgroundColor: backgroundColor}]}>
      <ScrollView>
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
            {media.length === 0 || isPremium === false ? (
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
          ) : (
            media.map((mediaItem, index) => (
              <View key={index} style={{ flexDirection: 'column', alignItems: 'center' }}>
                {mediaItem.mediaType === 'R' ? (
                  <TouchableOpacity onPress={() => playSound(mediaItem.DownloadedMediaFile || mediaItem.mediaFile)}>
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
                      source={{ uri: mediaItem.DownloadedMediaFile ? `file://${mediaItem.DownloadedMediaFile}` : mediaItem.mediaFile }}
                      style={styles.imagemRolo}
                    />
                    {mediaItem.DownloadedMediaFile && (
                      <Text style={styles.textSimple}>Arquivo baixado</Text>
                    )}
                  </View>
                ) : mediaItem.mediaType === 'V' ? (
                  <View>
                    <Video
                      source={{ uri: mediaItem.DownloadedMediaFile ? `file://${mediaItem.DownloadedMediaFile}` : mediaItem.mediaFile}}
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
                      <Text>
                        Tipo de mídia desconhecido
                      </Text>
                    </Text>
                  </View>
                )}
                <View style={styles.botaoDownload}>
                  <TouchableOpacity onPress={() => downloadFile(mediaItem.mediaFile)} >
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
                        style={{ paddingHorizontal: 10 }}
                        />
                    </View>
                  </TouchableOpacity>
                </View>
              </View>
            ))
          )}
            </ScrollView>

            <View style={styles.botaoComecar}>
              <TouchableOpacity onPress={clickFavorite}>
                <View
                  style={{
                    backgroundColor: favoriteButton
                      ? titleColor
                      : backgroundColor,
                    borderColor: favoriteButton ? titleColor : colorDiviver,
                    borderWidth: 2,
                    height: 50,
                    width: 50,
                    alignItems: 'center',
                    justifyContent: 'center',
                    borderRadius: 100,
                  }}>
                  <Feather
                    name={'heart'}
                    size={25}
                    color={favoriteButton ? backgroundColor : colorDiviver}
                    style={{paddingHorizontal: 10}}
                  />
                </View>
              </TouchableOpacity>
              {trailsState.viajar === false ? (
                flag === 0 ? (
                  <TouchableOpacity>{ButtonStartStop(true)}</TouchableOpacity>
                ) : (
                  <TouchableOpacity
                    onPress={() => openGoogleMapsDirections(locs)}>
                    {ButtonStartStop(true)}
                  </TouchableOpacity>
                )
              ) : (
                <TouchableOpacity onPress={() => endViagem()}>
                  {ButtonStartStop(false)}
                </TouchableOpacity>
              )}
            </View>
          </View>

          <Text style={[styles.textTitulo, {color: titleColor}]}>
            {trail.trailName}
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
            {trail.trailDesc}
          </Text>

          <View
            style={[styles.horizontalLine, {backgroundColor: colorDiviver}]}
          />

          <View
            style={{
              flexDirection: 'row',
              paddingVertical: 10,
              paddingHorizontal: 15,
              width: '100%',
              justifyContent: 'center',

              alignItems: 'center',
              gap: 5,
            }}>
            <View style={styles.gridCollumn}>
              <View style={styles.gridItem}>
                <Text style={[styles.gridText, {color: titleColor}]}>
                  {distancia} <Text style={{fontSize: 12}}>km</Text>
                </Text>
                <Text style={{color: titleColor, fontSize: 12}}>
                  Comprimento do Roteiro
                </Text>
              </View>
              <View style={styles.gridItem}>
                <Text style={[styles.gridText, {color: titleColor}]}>
                  {numerodePins}
                </Text>
                <Text style={{color: titleColor, fontSize: 12}}>
                  {' '}
                  Pontos de Interesse
                </Text>
              </View>
            </View>
            <View style={styles.gridCollumn}>
              <View style={styles.gridItem}>
                <Text style={[styles.gridText, {color: titleColor}]}>
                  {trail.trailDuration} <Text style={{fontSize: 12}}>km</Text>
                </Text>
                <Text style={{color: titleColor, fontSize: 12}}>
                  Tempo Médio
                </Text>
              </View>
              <View style={styles.gridItem}>
                <Text style={[styles.gridText, {color: titleColor}]}>
                  {changeDifficulty(trail.trailDifficulty)}
                </Text>
                <Text style={{color: titleColor, fontSize: 12}}>
                  Dificuldade
                </Text>
              </View>
            </View>
          </View>

          <View
            style={[styles.horizontalLine, {backgroundColor: colorDiviver}]}
          />

          <Text
            style={[
              {
                color: titleColor,
                fontSize: 22,
                marginBottom: 20,
                marginLeft: 15,
                fontWeight: '600',
              },
            ]}>
            Pontos do Roteiro
          </Text>

          <ScrollView
            showsHorizontalScrollIndicator={false}
            horizontal={true}
            style={styles.scrollViewPop}>
            {pins.map((pin: Pin, index: number) => (
              <View key={index}>
                <TouchableOpacity
                  onPress={() =>
                    navigation.navigate('PontoDeInteresseDetail', {
                      pin: pin,
                    })
                  }>
                  <PontoDeInteresse pin={pin}></PontoDeInteresse>
                </TouchableOpacity>
              </View>
            ))}
          </ScrollView>
          <Text
            style={[
              {
                color: titleColor,
                fontSize: 22,
                marginBottom: 20,
                marginLeft: 15,
                fontWeight: '600',
              },
            ]}>
            Mapa
          </Text>
        </View>

        {flag === 0 ? (
          <Text>Loading...</Text>
        ) : (
          <View style={[styles.containerMapa]}>
            <MapScreen localizacoes={locs} />
          </View>
        )}
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
  gridCollumn: {
    flexDirection: 'column',
    paddingHorizontal: 15,
    gap: 15,
    width: '50%',
  },
  gridItem: {
    height: 60,
    width: '100%',
    flexDirection: 'column',
    justifyContent: 'center',
    alignItems: 'center',
    gap: 5,
  },

  gridText: {
    fontSize: 25,
    fontWeight: 'bold',
  },

  itemText: {
    textAlign: 'center',
    fontSize: 16,
    fontWeight: 'bold',
    marginBottom: 5,
  },
  itemValue: {
    textAlign: 'center',
    fontSize: 16,
  },
  horizontalLine: {
    height: 1,
    width: '100%',
    marginVertical: 20,
  },
  linha: {
    width: 50,
  },
  itemPontoInteresse: {
    marginTop: 4,
  },

  containerMapa: {
    height: 350,
    marginHorizontal: 15,
  },
  textSimple: {
    marginLeft: 15,
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
  botaoComecar: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    gap: 15,
    position: 'absolute',
    bottom: -20,
    right: 20,
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
});

export default TrailDetail;