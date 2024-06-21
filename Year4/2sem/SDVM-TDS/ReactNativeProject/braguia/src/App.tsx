import React, {useEffect, useState} from 'react';

import 'react-native-gesture-handler';

import {NavigationContainer} from '@react-navigation/native';
import {createBottomTabNavigator} from '@react-navigation/bottom-tabs';

import {AppState, AppStateStatus, SafeAreaView, useColorScheme} from 'react-native';

import About from './screens/About';
import Explore from './screens/Explore';
import Map from './screens/Map';
import Favorites from './screens/Favorites';
import Login from './screens/Login';
import Support from './screens/Support'; // Import the Support screen component
import TrailDetail from './screens/TrailDetail';
import PontoDeInteresseDetail from './screens/PontoDeInteresseDetail';
import {Provider, useDispatch, useSelector} from 'react-redux';
import store from './redux/store';

import {createStackNavigator} from '@react-navigation/stack';
import {AppDispatch, RootState} from './redux/store';
import {fetchApp, fetchTrails} from './redux/actions';
import {useAppDispatch} from './redux/hooks';
import AppNav from './navigation/AppNav';
import {AuthProvider} from './navigation/AuthContext';

import { PermissionsAndroid, Platform } from 'react-native';
import Geolocation from 'react-native-geolocation-service';
import { request, PERMISSIONS, RESULTS } from 'react-native-permissions';
import { requestBackgroundLocation, requestFineLocation} from './utils/location';
import BackgroundGeolocation from '@mauron85/react-native-background-geolocation';
import { requestStoragePermission } from './utils/Downloads';
import useBackgroundGeolocationTracker from './utils/BgTracking';

const Tab = createBottomTabNavigator();
const Stack = createStackNavigator();

export default function App(): React.JSX.Element {
  const isDarkMode = useColorScheme() === 'dark';
  console.log('Redux store created na App:', store);
  const backgroundStyle = {
    backgroundColor: isDarkMode ? '#191A19' : 'white',
  };
  const [flag, setFlag] = useState<number>(0);

  if (flag === 0) {
    fetchTrails();
    fetchApp();
    setFlag(1);
  }
  const perm1 = requestStoragePermission() ;
  //const perm2 = requestFineLocation();
  const geolocationState = useBackgroundGeolocationTracker();

  //useEffect(() => {
  //  console.log('Geolocation state updated:', geolocationState);
  //}, [geolocationState]);



  return (
    <Provider store={store}>
      <SafeAreaView
        style={{flex: 1, backgroundColor: backgroundStyle.backgroundColor}}>
        <AuthProvider>
          <AppNav />
        </AuthProvider>
      </SafeAreaView>
    </Provider>
  );
}
