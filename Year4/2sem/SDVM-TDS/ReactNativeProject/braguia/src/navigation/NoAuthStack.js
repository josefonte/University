import {createBottomTabNavigator} from '@react-navigation/bottom-tabs';
import {createStackNavigator} from '@react-navigation/stack';
import About from '../screens/About';
import Explore from '../screens/Explore';
import Map from '../screens/Map';
import Login from '../screens/Login';
import Support from '../screens/Support'; // Import the Support screen component
import TrailDetail from '../screens/TrailDetail';
import PontoDeInteresseDetail from '../screens/PontoDeInteresseDetail';
import {darkModeTheme, lightModeTheme} from '../utils/themes';
import {useColorScheme} from 'react-native';

import Ionicons from 'react-native-vector-icons/Ionicons';
import AntDesign from 'react-native-vector-icons/AntDesign';

const Tab = createBottomTabNavigator();
const Stack = createStackNavigator();

function TabGroup() {
  const isDarkMode = useColorScheme() === 'dark';
  const theme = useColorScheme() === 'dark' ? darkModeTheme : lightModeTheme;

  return (
    <Tab.Navigator
      screenOptions={({route}) => ({
        headerShown: false,
        tabBarActiveTintColor: isDarkMode ? '#FEFAE0' : 'black',
        tabBarInactiveTintColor: isDarkMode ? '#A3A3A3' : 'black',
        tabBarStyle: {
          height: 60,
          paddingBottom: 5,
          paddingTop: 5,
          backgroundColor: isDarkMode ? '#191A19' : 'white',
          borderTopColor: isDarkMode ? '#1B2107' : 'gray',
        },
        tabBarIcon: ({focused, color, size}) => {
          let iconName;
          if (route.name === 'About') {
            iconName = focused
              ? 'information-circle'
              : 'information-circle-outline';
            return <Ionicons name={iconName} size={28} color={color} />;
          } else if (route.name === 'Explore') {
            iconName = focused ? 'search' : 'search-outline';
            return <Ionicons name={iconName} size={26} color={color} />;
          }
          if (route.name === 'Map') {
            iconName = focused ? 'map-sharp' : 'map-outline';
            return <Ionicons name={iconName} size={26} color={color} />;
          }
          if (route.name === 'Favorites') {
            iconName = focused ? 'heart' : 'hearto';
            return <AntDesign name={iconName} size={26} color={color} />;
          }
          if (route.name === 'Profile') {
            iconName = focused ? 'person-circle' : 'person-circle-outline';
            return <Ionicons name={iconName} size={28} color={color} />;
          }
        },
      })}>
      <Tab.Screen name="About" component={About} />
      <Tab.Screen name="Explore" component={Explore} />
      <Tab.Screen name="Map" component={Map} />
      <Tab.Screen name="Favorites" component={Login} />
      <Tab.Screen name="Profile" component={Login} />
    </Tab.Navigator>
  );
}

export default function NoAuthStack() {
  return (
    <Stack.Navigator screenOptions={{headerShown: false}}>
      <Stack.Screen name="tabs" component={TabGroup} />
      <Stack.Screen name="Support" component={Support} />
      <Stack.Screen name="TrailDetail" component={TrailDetail} />
      <Stack.Screen
        name="PontoDeInteresseDetail"
        component={PontoDeInteresseDetail}
      />
    </Stack.Navigator>
  );
}
