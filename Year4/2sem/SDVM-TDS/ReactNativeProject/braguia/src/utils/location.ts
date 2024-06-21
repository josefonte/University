import React, { useEffect } from 'react';
import { PermissionsAndroid, Alert, AppState, AppStateStatus, Platform, Linking } from 'react-native';
import Geolocation from '@react-native-community/geolocation';
import BackgroundTimer from 'react-native-background-timer';
import { canOpenURL, openURL } from 'react-native-app-link';
const requestBackgroundLocation = async (): Promise<boolean> => {
    try {
        const granted = await PermissionsAndroid.request(
            PermissionsAndroid.PERMISSIONS.ACCESS_BACKGROUND_LOCATION,
            {
                title: 'Track Background Location Permission',
                message: 'We need to get background location',
                buttonNeutral: 'Ask Me Later',
                buttonNegative: 'Cancel',
                buttonPositive: 'OK',
            },
        );
        if (granted === PermissionsAndroid.RESULTS.GRANTED) {
            console.log('Tracking background location...');
            return true;
        } else {
            console.log('Background location permission denied');
            return false;
        }
    } catch (err) {
        console.warn(err);
        return false;
    }
};

const requestFineLocation = async (): Promise<boolean> => {
    try {
        const granted = await PermissionsAndroid.request(
            PermissionsAndroid.PERMISSIONS.ACCESS_FINE_LOCATION,
            {
                title: 'Track Fine Location Permission',
                message: 'We need to get fine location',
                buttonNeutral: 'Ask Me Later',
                buttonNegative: 'Cancel',
                buttonPositive: 'OK',
            },
        );
        if (granted === PermissionsAndroid.RESULTS.GRANTED) {
            console.log('Tracking fine location...');
            return true;
        } else {
            console.log('Fine location permission denied');
            return false;
        }
    } catch (err) {
        console.warn(err);
        return false;
    }
};



export { requestBackgroundLocation, requestFineLocation};


