import React, {useEffect, useState} from 'react';
import {SearchBar} from '@rneui/themed';
import {View, Text, StyleSheet, ViewStyle, Image} from 'react-native';
import {Pin, Trail} from '../model/model';
import LinearGradient from 'react-native-linear-gradient';

import TrailInfo from './../assets/trailInfo';
import database from '../model/database';
import {Q} from '@nozbe/watermelondb';
interface PopularTrailProps {
  trail: Trail;
}
import Ionicons from 'react-native-vector-icons/Ionicons';
import Octicons from 'react-native-vector-icons/Octicons';
import MaterialIcons from 'react-native-vector-icons/MaterialCommunityIcons';
import Feather from 'react-native-vector-icons/Feather';

const PopularTrail: React.FunctionComponent<PopularTrailProps> = ({trail}) => {
  console.log('Carreguei imagem popular');

  const changeDifficulty = (dificuldade: string) => {
    if (dificuldade === 'E') {
      return 'Fácil';
    }
    if (dificuldade === 'D') {
      return 'Médio -';
    }
    if (dificuldade === 'C') {
      return 'Médio +';
    }
    if (dificuldade === 'B') {
      return 'Difícil';
    }
    if (dificuldade === 'A') {
      return 'Mt. Difícil';
    }
  };

  const [dataLoaded, setDataLoaded] = useState(false);
  const [numerodePins, setNumeroPins] = useState(0);
  const [distancia, setDistancia] = useState(0);

  const getPinsFromTrail = async (): Promise<void> => {
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

      setDataLoaded(true);
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
        await getPinsFromTrail();
        await getDistanceFromTrail();
      } catch (error) {
        console.error('Error fetching pins:', error);
        // Handle error if needed
      }
    };

    fetchData(); // Call fetchData when the component mounts
  }, []);

  return (
    <View style={[styles.view]}>
      <View>
        <Image source={{uri: trail.trailImg}} style={styles.popular} />
        <LinearGradient
          colors={['#00000000', '#000000']}
          style={styles.gradient}
        />
        <View style={[styles.viewTextoPop]}>
          <Text style={[styles.textoPop]}>{trail.trailName}</Text>
          <View style={styles.trailInfoContainer}>
            <View style={[styles.itemBar]}>
              <Ionicons name="time-outline" size={13} color="black" />
              <View></View>
              <Text style={styles.textoPop2}> {trail.trailDuration}</Text>
              <Text
                style={{
                  fontSize: 8,
                  color: 'black',
                  fontWeight: 'normal',
                  alignSelf: 'flex-end',
                  paddingBottom: 1,
                }}>
                min
              </Text>
            </View>
            <View
              style={{height: '100%', width: 1, backgroundColor: 'black'}}
            />
            <View style={[styles.itemBar]}>
              <Ionicons name="footsteps-outline" size={13} color="black" />

              <Text style={styles.textoPop2}> {distancia}</Text>
              <Text
                style={{
                  fontSize: 8,
                  color: 'black',
                  fontWeight: 'normal',
                  alignSelf: 'flex-end',
                  paddingBottom: 1,
                }}>
                km
              </Text>
            </View>
            <View
              style={{height: '100%', width: 1, backgroundColor: 'black'}}
            />
            <View style={[styles.itemBar]}>
              <Ionicons name="location-outline" size={13} color="black" />

              <Text style={styles.textoPop2}> {numerodePins}</Text>
            </View>
            <View
              style={{height: '100%', width: 1, backgroundColor: 'black'}}
            />
            <View style={[styles.itemBar]}>
              <Octicons
                name="flame"
                size={12}
                color="black"
                style={{marginRight: 3}}
              />

              <Text style={styles.textoPop2}>
                {changeDifficulty(trail.trailDifficulty)}
              </Text>
            </View>
          </View>
        </View>
      </View>
    </View>
  );
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
    marginBottom: 10,
  },
  textoPop: {
    fontSize: 22,
    marginLeft: 10,
    fontFamily: 'Roboto',
    fontWeight: 'bold',
    color: '#FEFAE0',
  },
  trailInfoContainer: {
    flexDirection: 'row',
    justifyContent: 'space-around',
    alignItems: 'center',
    marginTop: 10,
    marginLeft: 10,
    paddingHorizontal: 5,
    height: 20,
    width: 245,
    borderRadius: 5,
    backgroundColor: 'white',
  },
  itemBar: {
    flexDirection: 'row',
    justifyContent: 'center',
    alignItems: 'center',

    paddingHorizontal: 3,
  },
  textoPop2: {
    fontSize: 12,
    color: 'black',
    fontWeight: 'bold',
  },
  popular: {
    width: 300,
    height: 300,
    borderRadius: 8,
  },
  view: {
    marginRight: 10,
    marginBottom: 20,
  },
  gradient: {
    position: 'absolute',
    height: 300,
    width: 300,
    borderRadius: 8,
  },
});

export default PopularTrail;