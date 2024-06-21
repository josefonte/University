import { useEffect, useRef, useState } from 'react';
import BackgroundGeolocation from '@mauron85/react-native-background-geolocation';
import { Platform, Alert } from 'react-native';
import database from '../model/database';
import { Pin, Trail } from '../model/model';
import { Q } from '@nozbe/watermelondb';
import PushNotification from 'react-native-push-notification';


interface State {
    region: {
        latitude: number;
        longitude: number;
        latitudeDelta: number;
        longitudeDelta: number;
    } | null;
    latitude: number | null;
    longitude: number | null;
    isEnter: boolean;
}

interface GeoPoint {
    lat: number;
    lng: number;
    pin: Pin | null;
}


async function createGeoPoints(pins: Pin[]): Promise<GeoPoint[]> {
    const geoPoints: Promise<GeoPoint>[] = pins.map(async pin => {
        return {
            lat: pin.pinLat,
            lng: pin.pinLng,
            pin: pin
        };
    });
    const resolvedGeoPoints = await Promise.all(geoPoints);

    console.log("GEOFENCING DONE");

    return resolvedGeoPoints;
}


function calculateDistance(lat1: number, lon1: number, lat2: number, lon2: number): number {
    const R = 6371e3;
    const aa = lat1 * Math.PI / 180;
    const bb = lat2 * Math.PI / 180;
    const cc = (lat2 - lat1) * Math.PI / 180;
    const dd = (lon2 - lon1) * Math.PI / 180;

    const a = Math.sin(cc / 2) * Math.sin(cc / 2) +
        Math.cos(aa) * Math.cos(bb) *
        Math.sin(dd / 2) * Math.sin(dd / 2);
    const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

    return R * c; // Distance in meters
}


function triggeredPointOrNot(lat1: number, lon1: number, lat2: number, lon2: number): boolean {
    const distance = calculateDistance(lat1, lon1, lat2, lon2);
    return distance < 5000; // Geofencing radius
}

const useBackgroundGeolocationTracker = () => {

    const [pins, setPins] = useState<Pin[]>([]);
    const [geoPoints, setGeoPoints] = useState<GeoPoint[]>([]);
    const [retryCount, setRetryCount] = useState<number>(0);
    const [flag, setFlag] = useState<number>(0);

    useEffect(() => {
        const fetchData = async () => {
            try {
                const fetchedPins = await database.collections
                    .get<Pin>('pins')
                    .query()
                    .fetch();
                if (fetchedPins.length === 0) {
                    setRetryCount(retryCount + 1);
                    setTimeout(fetchData, 2000);
                } else {
                    setPins(fetchedPins);
                    setFlag(1); // Trigger creation of geoPoints
                }
            } catch (error) {
                console.error('Error fetching data:', error);
            }
        };

        fetchData();
    }, [retryCount]); // Only retry fetching pins when retryCount changes

    useEffect(() => {
        if (flag === 1 && pins.length > 0) { // Ensure flag is set and pins are fetched
            console.log("Creating GeoPoints...");
            async function fetchData2() {
                try {
                    const result = await createGeoPoints(pins);
                    const uniqueGeoPoints = result.filter((point, index, self) =>
                        index === self.findIndex((p) => (
                            p.lat === point.lat && p.lng === point.lng
                        ))
                    );

                    console.log("[GEOFENCES] Created:", uniqueGeoPoints);
                    setGeoPoints(uniqueGeoPoints);
                } catch (error) {
                    console.error('Error creating geoPoints:', error);
                }
            }

            fetchData2();
        }
    }, [pins, flag]); // Trigger creation of geoPoints when flag or pins change

    // Ensure geoPoints is not accessed until it's populated
    console.log("Current geoPoints:", geoPoints);
    const geoPointsRef = useRef<GeoPoint[]>([]);
    geoPointsRef.current = geoPoints;

    const [state, setState] = useState<State>({
        region: null,
        latitude: null,
        longitude: null,
        isEnter: false,
    });
    const longitudeDelta = 0.01;
    const latitudeDelta = 0.01;

    useEffect(() => {
        // Configs
        BackgroundGeolocation.configure({
            desiredAccuracy: BackgroundGeolocation.HIGH_ACCURACY,
            stationaryRadius: 50,
            distanceFilter: 50,
            notificationTitle: 'Background tracking',
            notificationText: 'enabled',
            debug: false,
            startOnBoot: false,
            stopOnTerminate: false,
            locationProvider:
                Platform.OS === 'android'
                    ? BackgroundGeolocation.ACTIVITY_PROVIDER
                    : BackgroundGeolocation.DISTANCE_FILTER_PROVIDER,
            interval: 20000,
            fastestInterval: 10000,
            activitiesInterval: 20000,
            stopOnStillActivity: false,
            url: null,
            syncUrl: 'http://192.168.0.97:3000/sync',
            httpHeaders: {
                'X-FOO': 'bar',
            },
            // customize post properties
            postTemplate: {
                lat: '@latitude',
                lon: '@longitude',
            },
        });

        // Onchange
        BackgroundGeolocation.on('location', (location) => {

            console.log('[DEBUG] BackgroundGeolocation location', location);
            console.log("GEOPOINTS:");
            console.log(geoPointsRef.current);
            for (const ponto of geoPointsRef.current) {
                const check = triggeredPointOrNot(ponto.lat, ponto.lng, location.latitude, location.longitude)
                if (check === true) {
                    console.log("VOU MANDAR NOTIFICAÇAO!");

                    PushNotification.localNotification({
                        channelId: "notificacaoPins",
                        title: 'Triggered Point Detected',
                        message: `Point (${ponto.lat}, ${ponto.lng}) is close ->${ponto.pin?.pinId}`,
                        actions: ["View"],
                    });
                }
                else {
                    console.log("LIMIT TESTING NOTIFICAÇOES");
                }
            }


            BackgroundGeolocation.startTask((taskKey) => {
                const region = Object.assign({}, location, {
                    latitudeDelta,
                    longitudeDelta,
                });

                setState((state) => ({
                    ...state,
                    latitude: location.latitude,
                    longitude: location.longitude,
                    region: region,
                }));

                BackgroundGeolocation.endTask(taskKey);
            });
        });

        // On Start
        BackgroundGeolocation.on('start', () => {
            console.log('[INFO] BackgroundGeolocation service has been started');

            PushNotification.createChannel(
                {
                    channelId: "notificacaoPins", // You can name this channel as per your requirement
                    channelName: "notificacaoPins",
                    channelDescription: "A default channel for notifications",
                    playSound: false,
                    soundName: "default",
                    importance: 3, // (optional) default: 4. Available options: 1-5.
                    vibrate: false,
                },
                (created: any) => console.log(`Notification channel created: ${created}`)
            );

            BackgroundGeolocation.getCurrentLocation(
                (location) => {
                    const region = Object.assign({}, location, {
                        latitudeDelta,
                        longitudeDelta,
                    });
                    setState((state) => ({
                        ...state,
                        latitude: location.latitude,
                        longitude: location.longitude,
                        region: region,
                    }));
                },
                (error) => {
                    setTimeout(() => {
                        Alert.alert(
                            'Error obtaining current location',
                            JSON.stringify(error),
                        );
                    }, 100);
                },
            );
        });


        BackgroundGeolocation.on('stop', () => {
            console.log('[INFO] BackgroundGeolocation service has been stopped');
        });


        BackgroundGeolocation.checkStatus((status) => {
            console.log(
                '[INFO] BackgroundGeolocation service is running',
                status.isRunning,
            );
            console.log(
                '[INFO] BackgroundGeolocation services enabled',
                status.locationServicesEnabled,
            );
            console.log(
                '[INFO] BackgroundGeolocation auth status: ' + status.authorization,
            );

            // you don't need to check status before start (this is just the example)
            // if (!status.isRunning) {
            // BackgroundGeolocation.start(); //triggers start on start event
            // }
        });

        BackgroundGeolocation.on('background', () => {
            console.log('[INFO] App is in background');
        });

        BackgroundGeolocation.on('foreground', () => {
            console.log('[INFO] App is in foreground');
        });

        BackgroundGeolocation.on('stationary', (location) => {
            console.log('[DEBUG] BackgroundGeolocation stationary', location);
        });



        return () => {
            BackgroundGeolocation.events.forEach((event) =>
                BackgroundGeolocation.removeAllListeners(event),
            );
        };
    }, []);

    return state;
};

export default useBackgroundGeolocationTracker;
