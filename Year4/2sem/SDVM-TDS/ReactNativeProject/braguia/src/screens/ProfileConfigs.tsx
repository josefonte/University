import React, {useState,useEffect} from 'react';
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
  Linking,
  Alert,
} from 'react-native';
import Geolocation from '@react-native-community/geolocation';
import {check, request, PERMISSIONS, RESULTS} from 'react-native-permissions';
import RNAndroidLocationEnabler from 'react-native-android-location-enabler';
import {useIsFocused, useNavigation} from '@react-navigation/native';

import Ionicons from 'react-native-vector-icons/Ionicons';
import Octicons from 'react-native-vector-icons/Octicons';
import MaterialIcons from 'react-native-vector-icons/MaterialCommunityIcons';
import Feather from 'react-native-vector-icons/Feather';

import {AuthContext} from '../navigation/AuthContext';
import {darkModeTheme, lightModeTheme} from '../utils/themes';
import {color} from '@rneui/themed/dist/config';
import {cleanFavoritesUser, cleanHistoricoUser} from '../redux/actions';

export default function ProfileConfigs() {
  const navigation = useNavigation();
  const isFocused = useIsFocused(); // Hook para verificar se a tela está focada
  const theme = useColorScheme() === 'dark' ? darkModeTheme : lightModeTheme;
  const [isEnabled, setIsEnabled] = useState(false);

  useEffect(() => {
    // Verifica o estado da localização ao montar o componente
    checkLocationStatus();
  }, [isFocused]); // Executa sempre que a tela estiver focada

  const checkLocationStatus = async () => {
    try {
      const status = await check(
        Platform.OS === 'ios' ? PERMISSIONS.IOS.LOCATION_WHEN_IN_USE : PERMISSIONS.ANDROID.ACCESS_FINE_LOCATION
      );
      if (status === RESULTS.GRANTED) {
        setIsEnabled(true);
      } else {
        setIsEnabled(false);
      }
    } catch (error) {
      console.log('Error checking location status:', error);
    }
  };

  const requestLocationPermission = async () => {
    try {
      const status = await request(
        Platform.OS === 'ios' ? PERMISSIONS.IOS.LOCATION_WHEN_IN_USE : PERMISSIONS.ANDROID.ACCESS_FINE_LOCATION
      );
      if (status === RESULTS.GRANTED) {
        enableLocation();
      } else {
        setIsEnabled(false);
      }
    } catch (error) {
      console.log('Error requesting location permission:', error);
    }
  };

  const enableLocation = async () => {
    if (Platform.OS === 'android') {
      try {
        const result = await RNAndroidLocationEnabler.promptForEnableLocationIfNeeded({
          interval: 10000,
          fastInterval: 5000,
        });
        if (result === 'enabled' || result === 'already-enabled') {
          setIsEnabled(true);
        }
      } catch (err) {
        console.log('Error enabling location:', err);
        Alert.alert(
          'Erro',
          'Não foi possível ativar a localização. Por favor, ative manualmente nas configurações do dispositivo.',
          [
            { text: 'Cancelar', style: 'cancel' },
            {
              text: 'OK',
              onPress: () => {
                Linking.openSettings();
              },
            },
          ]
        );
        setIsEnabled(false);
      }
    } else {
      Geolocation.getCurrentPosition(
        position => {
          setIsEnabled(true);
        },
        error => {
          console.log('Error getting current position:', error);
          setIsEnabled(false);
        }
      );
    }
  };

  const toggleSwitch = async () => {
    if (!isEnabled) {
      await requestLocationPermission();
    } else {
      if (Platform.OS === 'android') {
        Alert.alert(
          'Desativar Localização',
          'Por favor, desative o GPS manualmente nas configurações do seu dispositivo.',
          [
            { text: 'Cancelar', style: 'cancel' },
            {
              text: 'OK',
              onPress: () => {
                Linking.openSettings();
              },
            },
          ]
        );
      } 
    }
    setIsEnabled(!isEnabled);
  };

  // Função para verificar e atualizar o estado do slider
  const updateLocationStatus = () => {
    checkLocationStatus(); // Verifica o status atual
  };

  useEffect(() => {
    if (isFocused) {
      updateLocationStatus(); // Atualiza o status ao focar na tela
    }
  }, [isFocused]);


  const backgroundColor = theme.background_color;
  const titleColor = theme.text;
  const textColor = theme.text2;
  const colorDiviver = theme.color8;
  const redButtonText = theme.redButtontitle;
  const redButtonPressed = theme.redButtonPressed;
  const redButton = theme.redButton;

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
              Configurações
            </Text>
          </View>
        </View>

        <ScrollView
          contentContainerStyle={styles.options}
          showsVerticalScrollIndicator={false}>
          <View style={styles.Section}>
            <Text style={[styles.titleSection, {color: textColor}]}>Conta</Text>
            <Pressable
              style={({pressed}) => [
                {
                  backgroundColor: pressed ? colorDiviver : backgroundColor,
                },
              ]}>
              <View style={[styles.button, {borderBottomColor: colorDiviver}]}>
                <Text style={{fontSize: 16, color: textColor, marginLeft: 10}}>
                  Alterar Nome, Username e Email
                </Text>

                <Octicons
                  name={'chevron-right'}
                  size={20}
                  color={textColor}
                  style={{end: 15, position: 'absolute'}}
                />
              </View>
            </Pressable>
            <Pressable
              style={({pressed}) => [
                {
                  backgroundColor: pressed ? colorDiviver : backgroundColor,
                },
              ]}>
              <View style={[styles.button, {borderBottomColor: colorDiviver}]}>
                <Text style={{fontSize: 16, color: textColor, marginLeft: 10}}>
                  Alterar Password
                </Text>

                <Octicons
                  name={'chevron-right'}
                  size={20}
                  color={textColor}
                  style={{end: 15, position: 'absolute'}}
                />
              </View>
            </Pressable>
            <Pressable
              style={({pressed}) => [
                {
                  backgroundColor: pressed ? redButtonPressed : redButton,
                },
              ]}>
              <View style={[styles.button, {borderBottomColor: colorDiviver}]}>
                <Text
                  style={{fontSize: 16, color: redButtonText, marginLeft: 10}}>
                  Eliminar Conta
                </Text>

                <Octicons
                  name={'trash'}
                  size={18}
                  color={textColor}
                  style={{end: 13, position: 'absolute', color: redButtonText}}
                />
              </View>
            </Pressable>
          </View>

          <View style={styles.Section}>
            <Text style={[styles.titleSection, {color: textColor}]}>
              Preferências
            </Text>
            <Pressable
              style={({pressed}) => [
                {
                  backgroundColor: pressed ? colorDiviver : backgroundColor,
                },
              ]}>
              <View style={[styles.button, {borderBottomColor: colorDiviver}]}>
                <Text style={{fontSize: 16, color: textColor, marginLeft: 10}}>
                  Unidades de Medida
                </Text>

                <Octicons
                  name={'chevron-right'}
                  size={20}
                  color={textColor}
                  style={{end: 15, position: 'absolute'}}
                />
              </View>
            </Pressable>
            <Pressable
              style={({pressed}) => [
                {
                  backgroundColor: pressed ? colorDiviver : backgroundColor,
                },
              ]}>
              <View style={[styles.button, {borderBottomColor: colorDiviver}]}>
                <Text style={{fontSize: 16, color: textColor, marginLeft: 10}}>
                  Linguaguem
                </Text>

                <Octicons
                  name={'chevron-right'}
                  size={20}
                  color={textColor}
                  style={{end: 15, position: 'absolute'}}
                />
              </View>
            </Pressable>
            <Pressable
              style={({pressed}) => [
                {
                  backgroundColor: pressed ? colorDiviver : backgroundColor,
                },
              ]}>
              <View style={[styles.button, {borderBottomColor: colorDiviver}]}>
                <Text style={{fontSize: 16, color: textColor, marginLeft: 10}}>
                  Notificações
                </Text>

                <Octicons
                  name={'chevron-right'}
                  size={20}
                  color={textColor}
                  style={{end: 15, position: 'absolute'}}
                />
              </View>
            </Pressable>
          </View>

          <View style={styles.Section}>
            <Text style={[styles.titleSection, {color: textColor}]}>
              Dados e Segurança
            </Text>
            <Pressable
              style={({pressed}) => [
                {
                  backgroundColor: pressed ? colorDiviver : backgroundColor,
                },
              ]}
              onPress={toggleSwitch}>
              <View style={[styles.button, {borderBottomColor: colorDiviver}]}>
                <Text style={{fontSize: 16, color: textColor, marginLeft: 10}}>
                  Serviços de Localização
                </Text>

                <Feather
                  name={'external-link'}
                  size={20}
                  color={textColor}
                  style={{end: 10, position: 'absolute'}}
                />
              </View>
            </Pressable>
            <Pressable
              onPress={() => {
                cleanHistoricoUser();
                cleanFavoritesUser();
              }}
              style={({pressed}) => [
                {
                  backgroundColor: pressed ? redButton : backgroundColor,
                },
              ]}>
              <View style={[styles.button, {borderBottomColor: colorDiviver}]}>
                <Text style={{fontSize: 16, color: textColor, marginLeft: 10}}>
                  Limpar dados armazenados
                </Text>

                <Octicons
                  name={'trash'}
                  size={18}
                  color={textColor}
                  style={{end: 13, position: 'absolute'}}
                />
              </View>
            </Pressable>
          </View>
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
