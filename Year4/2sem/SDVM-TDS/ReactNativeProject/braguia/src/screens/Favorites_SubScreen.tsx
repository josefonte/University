import React, {useCallback, useEffect, useState} from 'react';
import {
  SafeAreaView,
  StyleSheet,
  TextInput,
  Text,
  View,
  useColorScheme,
  ScrollView,
  TouchableOpacity,
} from 'react-native';
import SugestedTrail from '../components/SugestedTrail';
import database from '../model/database';
import {Q} from '@nozbe/watermelondb';
import {Trail, User} from '../model/model';
import {useFocusEffect, useNavigation} from '@react-navigation/native';
import {darkModeTheme, lightModeTheme} from '../utils/themes';
import {AuthContext} from '../navigation/AuthContext';
import MaterialIcons from 'react-native-vector-icons/MaterialCommunityIcons';

export default function FavoritesTab() {
  const navigation = useNavigation();
  const theme = useColorScheme() === 'dark' ? darkModeTheme : lightModeTheme;
  const {username} = React.useContext(AuthContext);

  const [trailList, setTrailList] = React.useState<Trail[]>([]);

  const backgroundColor = theme.background_color;
  const titleColor = theme.text;
  const textColor = theme.text2;
  const colorDiviver = theme.color8;


  const fetchData = async () => {
    try {
      const usersCollection = database.collections.get<User>('users');
      console.log('username:', username);
      const userQuery = await usersCollection
        .query(Q.where('username', username))
        .fetch();

      if (userQuery.length > 0 && userQuery[0].favorites !== '') {
        const user = userQuery[0];
        const trailIds =
          user?.favorites?.split(';').map(i => parseInt(i)) ?? [];
        console.log('trailIds:', trailIds);
        if (trailIds.length > 0) {
          const trailCollection = database.collections.get<Trail>('trails');

          const trails = await trailCollection
            .query(Q.where('trail_id', Q.oneOf(trailIds)))
            .fetch();

          setTrailList(trails);
        }
      }
    } catch (error) {
      console.error('Error fetching trails:', error);
    }
  };
  useFocusEffect(
    useCallback(() => {
      console.log('fetchData');
      fetchData();
    }, [username])
  );

  return (
    <View style={[styles.container, {backgroundColor: backgroundColor}]}>
      <ScrollView
        contentContainerStyle={styles.options}
        showsVerticalScrollIndicator={false}>
        {trailList.length === 0 ? (
          <View
            style={{
              marginTop: '50%',
              width: '100%',
              flexDirection: 'column',
              alignItems: 'center',
            }}>
            <MaterialIcons
              name={'routes'}
              size={150}
              color={colorDiviver}
              style={{paddingRight: 3}}
            />
            <Text style={[styles.noTrailsText, {color: colorDiviver}]}>
              Lista de Favoritos vazia
            </Text>
          </View>
        ) : (
          trailList.map((trail: Trail, index: number) => (
            <View key={index}>
              <TouchableOpacity
                onPress={() =>
                  navigation.navigate('TrailDetail', {trail: trail})
                }>
                <SugestedTrail trail={trail}></SugestedTrail>
              </TouchableOpacity>
            </View>
          ))
        )}
      </ScrollView>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
  noTrailsText: {
    fontSize: 18,
    fontWeight: '500',
    textAlign: 'center',
    marginTop: 20,
    marginBottom: 20,
  },
  options: {
    bottom: 0,
    flexDirection: 'column',
    justifyContent: 'space-between',
    paddingTop: 15,
    marginHorizontal: 15,
  },
  textTitulo: {
    marginLeft: 10,
    fontSize: 26,
    fontFamily: 'Roboto',
    lineHeight: 32,
    fontWeight: 'bold',
    letterSpacing: 0.25,
    marginBottom: 20,
  },
});
