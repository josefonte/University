import React from 'react';
import { NavigationContainer } from '@react-navigation/native';
import NoAuthStack from './NoAuthStack';
import AuthStack from './AuthStack';
import { AuthContext } from '../navigation/AuthContext';
import { ActivityIndicator, View } from 'react-native';

export default function AppNav() {
  const { cookies, isLoading } = React.useContext(AuthContext);

  return (
    <NavigationContainer>
      {isLoading ? (
        <View style={{ flex: 1, justifyContent: 'center', alignItems: 'center' }}>
          <ActivityIndicator size={'large'} />
        </View>
      ) : cookies ? (
        <AuthStack />
      ) : (
        <NoAuthStack />
      )}
    </NavigationContainer>
  );
}
