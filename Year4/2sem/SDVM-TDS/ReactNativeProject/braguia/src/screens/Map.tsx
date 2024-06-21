import React from 'react';
import {
  SafeAreaView,
  StyleSheet,
  TextInput,
  Text,
  View,
  useColorScheme,
} from 'react-native';
import {NavigationContainer} from '@react-navigation/native';
import {createStackNavigator} from '@react-navigation/stack';
import MapScreen from '../components/mapScreen';

export default function Map() {
  const isDarkMode = useColorScheme() === 'dark';
  const Stack = createStackNavigator();

  return <MapScreen localizacoes={undefined}></MapScreen>;
}

const styles = StyleSheet.create({});
