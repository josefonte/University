import React, {useState, useRef, useEffect} from 'react';
import {
  View,
  TouchableOpacity,
  Animated,
  StyleSheet,
  useColorScheme,
  Text,
} from 'react-native';
import {darkModeTheme, lightModeTheme} from '../utils/themes';
import Ionicons from 'react-native-vector-icons/Ionicons';
import Octicons from 'react-native-vector-icons/Octicons';
import MaterialIcons from 'react-native-vector-icons/MaterialCommunityIcons';
import Feather from 'react-native-vector-icons/Feather';

const FiltroIcon: React.FC<{
  packageNAme: string;
  iconName: string;
  filterName: string;
  isActive: boolean;
  toggleFilter: () => void;
}> = ({packageNAme, iconName, filterName, isActive, toggleFilter}) => {
  const theme = useColorScheme() === 'dark' ? darkModeTheme : lightModeTheme;
  console.log('ACTIVE: ', isActive);
  const animation = useRef(new Animated.Value(1)).current;

  const buttonColor = isActive
    ? theme.filterButtonBackground
    : theme.filterButtonBackgroundPressed;
  const borderColor = isActive
    ? theme.filterButtonBackground
    : theme.filterButtonIconPressed;
  const iconColor = isActive
    ? theme.filterButtonIcon
    : theme.filterButtonIconPressed;

  const handlePress = () => {
    Animated.sequence([
      Animated.timing(animation, {
        toValue: 1.1,
        duration: 100,
        useNativeDriver: true,
      }),
      Animated.timing(animation, {
        toValue: 1,
        duration: 100,
        useNativeDriver: true,
      }),
    ]).start();

    toggleFilter();
  };

  return (
    <View style={styles.container}>
      <TouchableOpacity onPress={handlePress}>
        <Animated.View>
          <View
            style={[
              styles.button,
              {
                backgroundColor: buttonColor,
                borderColor: borderColor,
                borderStyle: 'solid',
                borderWidth: 2,
              },
            ]}>
            {packageNAme === 'Ionicons' && (
              <Ionicons name={iconName} size={35} color={iconColor} />
            )}
            {packageNAme === 'Feather' && (
              <Feather name={iconName} size={35} color={iconColor} />
            )}
            {packageNAme === 'Octicons' && (
              <Octicons name={iconName} size={35} color={iconColor} />
            )}
            {packageNAme === 'MaterialIcons' && (
              <MaterialIcons name={iconName} size={35} color={iconColor} />
            )}
            <Text style={{color: iconColor, fontSize: 11, fontWeight: '700'}}>
              {filterName}
            </Text>
          </View>
        </Animated.View>
      </TouchableOpacity>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {marginRight: 10},
  image: {
    width: 80,
    height: 80,
  },
  button: {
    height: 75,
    width: 75,
    borderRadius: 15,
    justifyContent: 'center',
    alignItems: 'center',
  },
});

export default FiltroIcon;