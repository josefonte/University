import React from 'react';
import {
  SafeAreaView,
  StyleSheet,
  TextInput,
  Text,
  View,
  Pressable,
  useColorScheme,
  Image,
} from 'react-native';
import {useNavigation} from '@react-navigation/native';
import {fetchUser} from '../redux/actions';

import EncryptedStorage from 'react-native-encrypted-storage';
import {Q} from '@nozbe/watermelondb';

import database from '../model/database';

import Ionicons from 'react-native-vector-icons/Ionicons';
import Octicons from 'react-native-vector-icons/Octicons';
import MaterialIcons from 'react-native-vector-icons/MaterialCommunityIcons';
import Feather from 'react-native-vector-icons/Feather';

import {AuthContext} from '../navigation/AuthContext';
import {darkModeTheme, lightModeTheme} from '../utils/themes';
import {User} from '../model/model';

export default function Profile() {
  const [userData, setUserData] = React.useState<User>();
  const {username} = React.useContext(AuthContext);

  React.useEffect(() => {
    const fetchData = async () => {
      try {
        console.log('UserNAME:', username);

        const usersCollection = database.collections.get<User>('users');
        const user = await usersCollection
          .query(Q.where('username', username))
          .fetch();

        if (user.length > 0) {
          setUserData(user[0]);
          console.log('User found:', user[0].username);
        } else {
          console.log('No User found in DB');
          const cookiesStored = await EncryptedStorage.getItem('cookies');
          if (cookiesStored) {
            fetchUser(cookiesStored);
          }
        }
      } catch (error) {
        console.error('Error fetching data:', error);
      }
    };
    fetchData();
  }, []);

  const {logout} = React.useContext(AuthContext);

  const theme = useColorScheme() === 'dark' ? darkModeTheme : lightModeTheme;
  const navigation = useNavigation();

  const backgroundColor = theme.background_color;
  const titleColor = theme.text;
  const textColor = theme.text2;
  const colorDiviver = theme.color8;
  const redButtonPressed = theme.redButton;
  const redtitle = theme.redButtontitle;

  const onPressLeave = () => {
    logout();
  };

  return (
    <View style={[styles.container, {backgroundColor}]}>
      <View style={styles.content}>
        <View>
          <Text style={[styles.username, {color: titleColor}]}>
            {userData ? userData.username : 'Placeholder'}
          </Text>
          <View style={[styles.stats, {borderColor: colorDiviver}]}>
            <View style={styles.stats_cont}>
              <Text style={[styles.stats_num, {color: textColor}]}>
                {userData?.historico !== ''
                  ? userData?.historico?.split(';').length
                  : 0}
              </Text>
              <Text style={[styles.stats_desc, {color: textColor}]}>
                Feitos
              </Text>
            </View>

            <View
              style={{
                height: '100%',
                borderStyle: 'solid',
                borderColor: colorDiviver,
                borderRightWidth: 1,
              }}
            />
            <View style={styles.stats_cont}>
              <Text style={[styles.stats_num, {color: textColor}]}>0</Text>
              <Text style={[styles.stats_desc, {color: textColor}]}>
                Criados
              </Text>
            </View>
          </View>
        </View>

        <View style={styles.options}>
          <Pressable
            onPress={() => navigation.navigate('ProfileHistorico')}
            style={({pressed}) => [
              {
                backgroundColor: pressed ? colorDiviver : backgroundColor,
              },
            ]}>
            <View style={[styles.button, {borderBottomColor: colorDiviver}]}>
              <MaterialIcons
                name={'history'}
                size={20}
                color={textColor}
                style={{paddingHorizontal: 10}}
              />

              <Text style={{fontSize: 18, color: textColor}}>Histórico</Text>

              <Octicons
                name={'chevron-right'}
                size={22}
                color={textColor}
                style={{end: 15, position: 'absolute'}}
              />
            </View>
          </Pressable>

          <Pressable
            onPress={() => navigation.navigate('Configs')}
            style={({pressed}) => [
              {
                backgroundColor: pressed ? colorDiviver : backgroundColor,
              },
            ]}>
            <View style={[styles.button, {borderBottomColor: colorDiviver}]}>
              <Ionicons
                name={'settings-outline'}
                size={20}
                color={textColor}
                style={{paddingHorizontal: 10}}
              />

              <Text style={{fontSize: 18, color: textColor}}>
                Configurações
              </Text>

              <Octicons
                name={'chevron-right'}
                size={22}
                color={textColor}
                style={{end: 15, position: 'absolute'}}
              />
            </View>
          </Pressable>

          <Pressable
            onPress={() => navigation.navigate('Support')}
            style={({pressed}) => [
              {
                backgroundColor: pressed ? colorDiviver : backgroundColor,
              },
            ]}>
            <View style={[styles.button, {borderBottomColor: colorDiviver}]}>
              <Ionicons
                name={'help-buoy-outline'}
                size={20}
                color={textColor}
                style={{paddingHorizontal: 10}}
              />

              <Text style={{fontSize: 18, color: textColor}}>Apoio</Text>

              <Octicons
                name={'chevron-right'}
                size={22}
                color={textColor}
                style={{end: 15, position: 'absolute'}}
              />
            </View>
          </Pressable>

          <Pressable
            onPress={() => navigation.navigate('About')}
            style={({pressed}) => [
              {
                backgroundColor: pressed ? colorDiviver : backgroundColor,
              },
            ]}>
            <View style={[styles.button, {borderBottomColor: colorDiviver}]}>
              <Ionicons
                name={'information-circle-outline'}
                size={20}
                color={textColor}
                style={{paddingHorizontal: 10}}
              />

              <Text style={{fontSize: 18, color: textColor}}>Sobre a app</Text>

              <Octicons
                name={'chevron-right'}
                size={22}
                color={textColor}
                style={{end: 15, position: 'absolute'}}
              />
            </View>
          </Pressable>

          <Pressable
            onPress={() => navigation.navigate('FeedBack')}
            style={({pressed}) => [
              {
                backgroundColor: pressed ? colorDiviver : backgroundColor,
              },
            ]}>
            <View style={[styles.button, {borderBottomColor: colorDiviver}]}>
              <MaterialIcons
                name={'script-text-outline'}
                size={20}
                color={textColor}
                style={{paddingHorizontal: 10}}
              />

              <Text style={{fontSize: 18, color: textColor}}>
                Enviar Feedback
              </Text>

              <Octicons
                name={'chevron-right'}
                size={22}
                color={textColor}
                style={{end: 15, position: 'absolute'}}
              />
            </View>
          </Pressable>

          <Pressable
            onPress={onPressLeave}
            style={({pressed}) => [
              {
                backgroundColor: pressed ? redButtonPressed : backgroundColor,
              },
            ]}>
            <View style={[styles.button, {borderBottomWidth: 0}]}>
              <Text style={{paddingLeft: 12, fontSize: 18, color: redtitle}}>
                Terminar Sessão
              </Text>

              <Feather
                name={'log-out'}
                size={20}
                color={redtitle}
                style={{end: 8, position: 'absolute'}}
              />
            </View>
          </Pressable>
        </View>
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
    justifyContent: 'space-between',
    margin: '5%',
  },

  options: {
    bottom: 0,
    flexDirection: 'column',
    justifyContent: 'space-between',
  },

  username: {
    marginTop: '10%',
    fontSize: 30,
    fontWeight: '600',
    textAlign: 'center',
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

  stats: {
    flexDirection: 'row',
    alignContent: 'center',
    justifyContent: 'center',
    alignItems: 'center',
    marginTop: '10%',
    marginHorizontal: '10%',
    paddingBottom: 10,
    borderStyle: 'solid',
    borderBottomWidth: 1,
  },

  stats_cont: {
    flexDirection: 'column',
    alignItems: 'center',
    justifyContent: 'center',
    paddingHorizontal: 15,
  },

  stats_num: {
    fontSize: 30,
    fontWeight: '600',
  },

  stats_desc: {
    fontSize: 12,
    fontWeight: '400',
  },
});
