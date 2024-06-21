import React, {useEffect, useState} from 'react';
import {
  StyleSheet,
  Text,
  View,
  useColorScheme,
  TouchableOpacity,
  Linking,
  Pressable,
  AppStateStatus,
  AppState,
} from 'react-native';

import {lightModeTheme, darkModeTheme} from '../utils/themes';
import {fetchTrails, fetchApp} from '../redux/actions';
import database from '../model/database';
import {App, Partners, Pin, Socials} from '../model/model';
import {useAppDispatch} from '../redux/hooks';
import {useNavigation} from '@react-navigation/native';
const MAX_RETRY_COUNT = 5;
// Assets
import AppLogo from '../assets/logo.svg';
import FacebookLogo from '../assets/facebook.svg';
import UmLogo from '../assets/umlogo.svg';

// GEOFENCING
import {PermissionsAndroid, Platform} from 'react-native';
import Geolocation from '@react-native-community/geolocation';
import BackgroundFetch from 'react-native-background-fetch';
import PushNotification from 'react-native-push-notification';
import { Q } from '@nozbe/watermelondb';

export default function About() {
  const theme = useColorScheme() === 'dark' ? darkModeTheme : lightModeTheme;
  const navigation = useNavigation();
  const [appData, setAppData] = useState<App[]>([]);
  const [socialsData, setSocialsData] = useState<Socials[]>([]);
  const [partnersData, setPartnersData] = useState<Partners[]>([]);
  const [retryCount, setRetryCount] = useState<number>(0);


  const [showPopup, setShowPopup] = useState(false);
  const [googleMapsInstalled, setGoogleMapsInstalled] = useState(false);

  useEffect(() => {
    const checkGoogleMapsInstallation = async () => {
      // Verifica se o Google Maps está instalado
      const url = 'https://maps.google.com';
      const isInstalled = await Linking.canOpenURL(url);
      setGoogleMapsInstalled(isInstalled);
      
      // Mostra o pop-up se o Google Maps não estiver instalado
      if (!isInstalled) {
        setShowPopup(true);
      }
    };

    checkGoogleMapsInstallation();
  }, []);

  const handleInstallGoogleMaps = () => {
    const googleMapsUrl =
      Platform.OS === 'ios'
        ? 'https://apps.apple.com/us/app/google-maps-transit-food/id585027354'
        : 'https://play.google.com/store/apps/details?id=com.google.android.apps.maps';

    Linking.openURL(googleMapsUrl).then(() => {
      setShowPopup(false); // Fecha o pop-up após redirecionar para a loja
    }).catch(err => console.error('Failed to open Google Maps URL:', err));
  };






  var flag = false;
  useEffect(() => {
    const fetchData = async () => {
      if (retryCount >= MAX_RETRY_COUNT && flag === true) {
        console.log('Max retry count reached. Stopping retries.');
        return;
      }

      try {
        const appCollection = database.collections.get<App>('app');
        const socialsCollection = database.collections.get<Socials>('socials');
        const partnersCollection =
          database.collections.get<Partners>('partners');

        const app = await appCollection.query().fetch();
        const socials = await socialsCollection.query().fetch();
        const partners = await partnersCollection.query().fetch();

        setAppData(app);
        setSocialsData(socials);
        setPartnersData(partners);

        if (app.length !== 0 && socials.length !== 0 && partners.length !== 0) {
          flag = true;
        }

        console.log('App Data:', app);
        console.log('Socials Data:', socials);
        console.log('Partners Data:', partners);
      } catch (error) {
        console.error('Error fetching data:', error);
        console.log('Retrying - attempt2: ', retryCount);
        setRetryCount(prevCount => prevCount + 1);
      }

      if (!flag) {
        console.log('Retrying - attempt: ', retryCount);
        setRetryCount(prevCount => prevCount + 1);
      }
    };

    fetchData();
  }, [retryCount]);




  //PushNotification.configure({
  //  onAction: function (notification) {
  //      console.log('Notification action received: ', notification.action);
  //      console.log('mensagem lá dentro -> ' + notification.message)
  //  }
  //});
  PushNotification.configure({
    // Called when a remote or local notification is opened or received
    onNotification: async function (notification) {
      console.log('Local Notification:', notification);
  
      // Determine which action was triggered
      switch (notification.action) {
        case 'View':
          // Handle 'View' action
          console.log('User pressed "View" action');
          console.log(notification.message);
          const strr : string = notification.message.toString()
          const lastChar: string = strr.slice(-1); // Get the last character
          const lastCharAsInt: number = parseInt(lastChar, 10); // Convert to integer
          const fetchedTrails = await database.collections
          .get<Pin>('pins')
          .query(Q.where('pin_id', lastCharAsInt))
          .fetch();
          navigation.navigate('PontoDeInteresseDetail', {pin: fetchedTrails[0]})
          // Navigate to a specific screen or perform an action
          // Example: navigation.navigate('DetailsScreen', { itemId: notification.data.itemId });
          break;
        // Add additional cases for other actions if needed
  
        default:
          console.log(`Unknown action: ${notification.action}`);
          break;
      }
    },
  });

  const handlePress = (url: string) => {
    Linking.openURL(url).catch(err =>
      console.error('Failed to open URL:', err),
    );
  };

  const styles = StyleSheet.create({
    container: {
      flex: 1,
      justifyContent: 'flex-start',
      flexDirection: 'column',
      backgroundColor: theme.background_color,
      alignItems: 'center',
      padding: '5%',
    },
    appNameText: {
      fontSize: 40,
      fontWeight: 'bold',
      color: theme.text,
      marginBottom: 10,
    },
    appDescText: {
      fontSize: 20,
      color: theme.text,
      marginBottom: 25,
      textAlign: 'center',
    },
    appLandingText: {
      fontSize: 14,
      color: theme.text,
      marginBottom: 25,
      textAlign: 'justify',
    },
    rowContainer: {
      flexDirection: 'row',
      justifyContent: 'space-between',
      alignItems: 'center',
      width: '80%',
    },
    columnContainer: {
      flexDirection: 'column',
      justifyContent: 'space-between',
      alignItems: 'center',
    },
    infoText: {
      fontSize: 15,
      fontWeight: '600',
      color: theme.text,
      marginBottom: 10,
    },
    imageButton: {
      marginHorizontal: 5,
      alignItems: 'center',
      alignSelf: 'center',
    },
    image: {
      width: 100,
      height: 100,
      resizeMode: 'cover',
      alignSelf: 'center',
    },
    partnerText: {
      fontSize: 13,
      fontFamily: 'Roboto',
      color: theme.text,
      marginBottom: 5,
      alignSelf: 'center',
      alignItems: 'center',
      alignContent: 'center',
    },
    // Estilos para o pop-up de instalação do Google Maps
    popupContainer: {
      position: 'absolute',
      backgroundColor: theme.background_color,
      padding: 20,
      borderRadius: 10,
      borderWidth: 1,
      borderColor: theme.color8,
      width: '80%',
      alignSelf: 'center',
      top: '30%',
    },
    popupText: {
      fontSize: 16,
      marginBottom: 10,
      textAlign: 'center',
      fontFamily: 'Roboto',
      color: theme.text,
    },
    popupButton: {
      padding: 10,
      borderRadius: 5,
      marginTop: 10,
      alignItems: 'center',
    },
    popupButtonText: {
      fontSize: 16,
      color: theme.text,
    },
  });

  return (
    <View style={styles.container}>
      <AppLogo height={200} width={200} />

      {appData.length > 0 && (
        <>
          <Text style={styles.appNameText}>{appData[0].appName}</Text>
          <Text style={styles.appDescText}>{appData[0].appDesc}</Text>
          <Text style={styles.appLandingText}>{appData[0].appLanding}</Text>
        </>
      )}

      <View style={styles.rowContainer}>
        <View style={styles.columnContainer}>
          <Text style={styles.infoText}>Segue-nos em:</Text>
          {socialsData.map((social, index) => (
            <TouchableOpacity
              key={index}
              style={styles.imageButton}
              onPress={() => handlePress(social.socialUrl)}>
              <FacebookLogo height={100} width={100} />
            </TouchableOpacity>
          ))}
        </View>

        <View style={styles.columnContainer}>
          <Text style={styles.infoText}>Os nossos parceiros:</Text>
          {partnersData.map((partner, index) => {
            console.log('Rendering partner:', partner); // Debugging log
            return (
              <View key={index} style={styles.container}>
                <Text style={[styles.infoText, {marginBottom: 0}]}>
                  {partner.partnerName}
                </Text>
                <TouchableOpacity
                  style={styles.imageButton}
                  onPress={() => handlePress(partner.partnerUrl)}>
                  <UmLogo height={100} width={150} />
                </TouchableOpacity>

                <TouchableOpacity
                  onPress={() =>
                    Linking.openURL(`mailto:${partner.partnerMail}`)
                  }
                  style={styles.partnerText}>
                  <Text style={styles.partnerText}>{partner.partnerMail}</Text>
                </TouchableOpacity>

                <TouchableOpacity
                  onPress={() => Linking.openURL(`tel:${partner.partnerPhone}`)}
                  style={styles.partnerText}>
                  <Text style={styles.partnerText}>{partner.partnerPhone}</Text>
                </TouchableOpacity>
              </View>
            );
          })}
        </View>
      </View>
    {/* Pop-up para instalação do Google Maps */}
    {showPopup && !googleMapsInstalled && (
        <View style={styles.popupContainer}>
          <Text style={styles.popupText}>
            Parece que o Google Maps não está instalado. Instale para uma melhor experiência.
          </Text>
          <TouchableOpacity style={styles.popupButton} onPress={handleInstallGoogleMaps}>
            <Text style={styles.popupButtonText}>Instalar Google Maps</Text>
          </TouchableOpacity>
          <TouchableOpacity style={styles.popupButton} onPress={() => setShowPopup(false)}>
            <Text style={styles.popupButtonText}>Ignorar</Text>
          </TouchableOpacity>
        </View>
      )}
    </View>
  );
}