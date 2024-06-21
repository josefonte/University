import React, {useEffect, useState} from 'react';
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
import {useSelector} from 'react-redux';
import {RootState} from '../redux/store';
import SugestedTrail from '../components/SugestedTrail';
import database from '../model/database';
import {Q} from '@nozbe/watermelondb';
import {Trail, User} from '../model/model';
import {useNavigation} from '@react-navigation/native';
import {darkModeTheme, lightModeTheme} from '../utils/themes';
import {AuthContext} from '../navigation/AuthContext';
import FavoritesTab from './Favorites_SubScreen';
import CreatedTab from './Created_SubScreen';

import {NavigationContainer} from '@react-navigation/native';
import {createMaterialTopTabNavigator} from '@react-navigation/material-top-tabs';
import {color} from '@rneui/themed/dist/config';

const Tab = createMaterialTopTabNavigator();

export default function Favorites() {
  const navigation = useNavigation();
  const theme = useColorScheme() === 'dark' ? darkModeTheme : lightModeTheme;
  const {username} = React.useContext(AuthContext);

  const [trailList, setTrailList] = React.useState<Trail[]>([]);

  const backgroundColor = theme.background_color;
  const titleColor = theme.text;
  const colorDiviver = theme.color8;

  return (
    <View style={[styles.container, {backgroundColor: backgroundColor}]}>
      <Tab.Navigator
        screenOptions={{
          tabBarStyle: {backgroundColor: backgroundColor}, // Change this color to your desired tab bar background color
          tabBarActiveTintColor: titleColor, // Active tab text color
          tabBarInactiveTintColor: colorDiviver,
          tabBarIndicatorStyle: {
            backgroundColor: titleColor,
            width: '40%',
            marginLeft: '5%',
          },
          tabBarLabelStyle: {
            fontSize: 16,
            fontWeight: '600',
            textTransform: 'none',
          },
        }}>
        <Tab.Screen
          name="FavoritesTab"
          component={FavoritesTab}
          options={{tabBarLabel: 'Favorites'}}
        />
        <Tab.Screen name="Created" component={CreatedTab} />
      </Tab.Navigator>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
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
