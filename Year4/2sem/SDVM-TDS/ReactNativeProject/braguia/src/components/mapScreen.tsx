import React, {useEffect, useState} from 'react';
import {StyleSheet, View} from 'react-native';
import MapView, {Marker, Polyline} from 'react-native-maps';
import database from '../model/database';
import { Pin } from '../model/model';
import { useNavigation } from '@react-navigation/native';

interface MapProps {
  localizacoes: [number, number][];
}

interface point {
  lat: number;
  lng: number;
  pin: Pin;
}

async function createGeoPoints(pins: Pin[]): Promise<point[]> {
  const geoPoints: Promise<point>[] = pins.map(async pin => {
      return {
          lat: pin.pinLat,
          lng: pin.pinLng,
          pin: pin,
      };
  });
  const resolvedGeoPoints = await Promise.all(geoPoints);

  console.log("GEOFENCING DONE");

  return resolvedGeoPoints;
}


const MapScreen: React.FunctionComponent<MapProps> = ({localizacoes}) => {
  const navigation = useNavigation();
  const [points, setPoints] = useState<point[]>([]);
  useEffect(() => {
    const fetchData = async () => {
        try {
            const fetchedPins = await database.collections
                .get<Pin>('pins')
                .query()
                .fetch();

              const result = await createGeoPoints(fetchedPins);
              setPoints(result);

        } catch (error) {
            console.error('Error fetching data:', error);
        }
    };

    fetchData();
}, []);

  if (!localizacoes) {
    return (
      <View style={styles.container}>
        <MapView
          style={styles.map}
          initialRegion={{
            latitude: 41.5595, // Latitude da Universidade do Minho
            longitude: -8.396, // Longitude da Universidade do Minho
            latitudeDelta: 0.91,
            longitudeDelta: 0.91,
          }}>
        {points.map((point, index) => (
          <Marker
            key={index}
            coordinate={{
              latitude: point.lat,
              longitude: point.lng,
            }}
            onPress={() => navigation.navigate('PontoDeInteresseDetail', {
              pin: point.pin,
            })}
          />
        ))}
          </MapView>
      </View>
    );
  } else if (localizacoes.length == 1) {
    return (
      <View style={styles.container2}>
        <MapView
          style={styles.map2}
          initialRegion={{
            latitude: localizacoes[0][0],
            longitude: localizacoes[0][1],
            latitudeDelta: 0.01,
            longitudeDelta: 0.01,
          }}>
          <Marker
            coordinate={{
              latitude: localizacoes[0][0],
              longitude: localizacoes[0][1],
            }}
          />
        </MapView>
      </View>
    );
  } else {
    return (
      <View style={styles.container2}>
        <MapView
          style={styles.map2}
          initialRegion={{
            latitude: localizacoes[0][0],
            longitude: localizacoes[0][1],
            latitudeDelta: 0.2,
            longitudeDelta: 0.2,
          }}>
          {localizacoes.map(([latitude, longitude], index) => (
            <Marker key={index} coordinate={{latitude, longitude}} />
          ))}
          <Polyline
            coordinates={localizacoes.map(([latitude, longitude]) => ({
              latitude,
              longitude,
            }))}
            strokeColor="#000" // Line color
            strokeWidth={2} // Line width
          />
        </MapView>
      </View>
    );
  }
};

const styles = StyleSheet.create({
  container: {
    ...StyleSheet.absoluteFillObject,
    height: '100%',
    width: '100%',
    justifyContent: 'flex-end',
    alignItems: 'center',
  },
  container2: {
    height: 300,
    width: '100%',
    justifyContent: 'center',
    borderRadius: 20,
  },
  map2: {
    flex: 1,
    borderRadius: 20,
  },
  map: {
    ...StyleSheet.absoluteFillObject,
  },
});

export default MapScreen;
