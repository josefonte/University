import React, {useEffect, useState} from 'react';
import {SearchBar} from '@rneui/themed';
import {
  View,
  Text,
  StyleSheet,
  ViewStyle,
  Image,
  useColorScheme,
} from 'react-native';
import {Media, Pin, Trail} from '../model/model';
import LinearGradient from 'react-native-linear-gradient';
import {useDispatch, useSelector} from 'react-redux';
import {RootState} from '../redux/store';
import database from '../model/database';
import {Q} from '@nozbe/watermelondb';

import Feather from 'react-native-vector-icons/Feather';
import Ionicons from 'react-native-vector-icons/Ionicons';
import Octicons from 'react-native-vector-icons/Octicons';
import MaterialIcons from 'react-native-vector-icons/MaterialCommunityIcons';
import {darkModeTheme, lightModeTheme} from '../utils/themes';

interface PontoDeInteresseProps {
  pin: Pin;
}

const PontoDeInteresse: React.FunctionComponent<PontoDeInteresseProps> = ({
  pin,
}) => {
  console.log('Carreguei ponto de interesse popular');
  const trailsState = useSelector((state: RootState) => state.trails);

  const [media, setMedia] = useState<Media[]>([]);

  const theme = useColorScheme() === 'dark' ? darkModeTheme : lightModeTheme;

  const backgroundColor = theme.background_color;
  const titleColor = theme.text;
  const textColor = theme.text2;
  const colorDiviver = theme.color8;

  useEffect(() => {
    const fetchData = async () => {
      const mediaData = await getMediaFromPin(pin.pinId);
      setMedia(mediaData);
    };

    if (pin.pinId !== null) {
      fetchData();
    }
  }, [pin.pinId]);

  async function getMediaFromPin(pinId: number): Promise<Media[]> {
    try {
      const mediaCollection = database.collections.get<Media>('media');
      const media = await mediaCollection
        .query(Q.where('media_pin', pinId))
        .fetch();
      return media;
    } catch (error) {
      console.error('Error fetching media from database:', error);
      return [];
    }
  }

  function checkImagemMedia(listaMedia: Media[]): string {
    if (listaMedia === []) return '';
    for (const media of listaMedia) {
      if (media.mediaType == 'I') {
        return media.mediaFile;
      }
    }
    return '';
  }

  const temMediaImagem = checkImagemMedia(media);
  if (temMediaImagem === '')
    return (
      <View style={[styles.view]}>
        <View>
          <View
            style={[
              styles.popular,
              {
                backgroundColor: 'gray',
                paddingTop: 30,
                alignItems: 'center',
              },
            ]}>
            <MaterialIcons
              name="file-image-marker"
              size={50}
              color={colorDiviver}
            />
          </View>
          <LinearGradient
            colors={['#00000000', '#000000']}
            style={styles.gradient}
          />
          <View style={[styles.viewTextoPop]}>
            <Text style={[styles.textoPop]}>{pin.pinName}</Text>
          </View>
        </View>
      </View>
    );
  else {
    return (
      <View style={[styles.view]}>
        <View>
          <Image source={{uri: temMediaImagem}} style={styles.popular} />
          <LinearGradient
            colors={['#00000000', '#000000']}
            style={styles.gradient}
          />
          <View style={[styles.viewTextoPop]}>
            <Text style={[styles.textoPop]}>{pin.pinName}</Text>
          </View>
        </View>
      </View>
    );
  }
};

const styles = StyleSheet.create({
  viewTextoPop: {
    position: 'absolute',
    top: 0,
    left: 0,
    right: 0,
    bottom: 0,
    justifyContent: 'flex-end',
    alignItems: 'flex-start',
    padding: 10,
    marginBottom: 4,
  },
  textoPop: {
    fontSize: 13,
    fontFamily: 'Roboto',
    fontWeight: 'bold',
    color: '#FEFAE0',
  },
  trailInfoContainer: {
    flexDirection: 'row',
  },
  popular: {
    width: 120,
    height: 120,
    borderRadius: 8,
  },
  view: {
    marginRight: 10,
    marginBottom: 20,
  },
  gradient: {
    position: 'absolute',
    height: 120,
    width: 120,
    borderRadius: 8,
  },
});

export default PontoDeInteresse;