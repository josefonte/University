import React from 'react';
import {
  StyleSheet,
  Text,
  ScrollView,
  View,
  Pressable,
  useColorScheme,
  Switch,
  PermissionsAndroid,
  Platform,
  TouchableOpacity,
} from 'react-native';
import Geolocation from '@react-native-community/geolocation';
import {check, request, PERMISSIONS, RESULTS} from 'react-native-permissions';
import {useNavigation} from '@react-navigation/native';
import database from '../model/database';
import {Q} from '@nozbe/watermelondb';

import Ionicons from 'react-native-vector-icons/Ionicons';
import Octicons from 'react-native-vector-icons/Octicons';
import MaterialIcons from 'react-native-vector-icons/MaterialCommunityIcons';
import Feather from 'react-native-vector-icons/Feather';

import {AuthContext} from '../navigation/AuthContext';
import {darkModeTheme, lightModeTheme} from '../utils/themes';
import {color} from '@rneui/themed/dist/config';
import {Trail, User} from '../model/model';
import SugestedTrail from '../components/SugestedTrail';
import {column} from '@nozbe/watermelondb/QueryDescription';

export default function ProfileHistorico() {
  const navigation = useNavigation();
  const theme = useColorScheme() === 'dark' ? darkModeTheme : lightModeTheme;
  const {username} = React.useContext(AuthContext);

  const [trailList, setTrailList] = React.useState<Trail[]>([]);

  const backgroundColor = theme.background_color;
  const titleColor = theme.text;
  const textColor = theme.text2;
  const colorDiviver = theme.color8;

  React.useEffect(() => {
    const fetchData = async () => {
      try {
        const usersCollection = database.collections.get<User>('users');
        const userQuery = await usersCollection
          .query(Q.where('username', username))
          .fetch();

        if (userQuery.length > 0 && userQuery[0].historico !== '') {
          const user = userQuery[0];
          const trailIds =
            user?.historico?.split(';').map(i => parseInt(i)) ?? [];

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

    fetchData();
  }, []);

  return (
    <View style={[styles.container, {backgroundColor}]}>
      <View style={styles.content}>
        <View
          style={{
            flexDirection: 'row',
            justifyContent: 'flex-start',
            marginBottom: 10,
          }}>
          <Pressable onPress={() => navigation.goBack()}>
            <View
              style={{
                borderRadius: 100,
                backgroundColor: colorDiviver,
                width: 40,
                height: 40,
                justifyContent: 'center',
                alignItems: 'center',
              }}>
              <Octicons
                name={'chevron-left'}
                size={20}
                color={textColor}
                style={{paddingRight: 3}}
              />
            </View>
          </Pressable>
          <View style={[styles.pageTitleContainer, {borderColor: titleColor}]}>
            <Ionicons
              name={'settings-outline'}
              size={17}
              color={titleColor}
              style={{paddingRight: 5}}
            />

            <Text style={[styles.pageTitle, {color: titleColor}]}>
              Histórico
            </Text>
          </View>
        </View>

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
                Histórico de trilhos vazio
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
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
  content: {
    flex: 1,
    flexDirection: 'column',
    margin: '5%',
  },

  options: {
    bottom: 0,
    flexDirection: 'column',
    justifyContent: 'space-between',
    paddingTop: 10,
  },
  noTrailsText: {
    fontSize: 18,
    fontWeight: '500',
    textAlign: 'center',
    marginTop: 20,
    marginBottom: 20,
  },
  pageTitleContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    paddingBottom: 5,
    paddingLeft: 5,
    borderBottomWidth: 1,
    borderStyle: 'solid',
    width: '85%',
    marginLeft: 15,
  },
  pageTitle: {
    fontSize: 18,
  },

  button: {
    display: 'flex',
    flexDirection: 'row',
    alignItems: 'center',
    borderStyle: 'solid',
    borderBottomWidth: 1,
    paddingTop: 20,
    paddingBottom: 20,
  },

  titleSection: {
    fontSize: 23,
    fontWeight: 'bold',
    marginTop: 10,
    marginBottom: 10,
  },

  Section: {
    marginTop: 30,
    marginBottom: 30,
  },
});
