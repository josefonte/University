import React, {useContext} from 'react';
import {
  SafeAreaView,
  StyleSheet,
  TextInput,
  Text,
  View,
  Pressable,
  useColorScheme,
  Image,
  TouchableOpacity,
} from 'react-native';

import AppLogo from '../assets/logo.svg';
import {AuthContext} from '../navigation/AuthContext';

export default function Profile() {
  const isDarkMode = useColorScheme() === 'dark';

  const {login, errorLogin} = useContext(AuthContext);

  const [username, setUsername] = React.useState('');
  const [password, setPassword] = React.useState('');

  const backgroundColor = isDarkMode ? '#161716' : 'white';
  const textColor = isDarkMode ? '#FEFAE0' : 'black';
  const inputBorderColor = isDarkMode ? '#434343' : '#D3D3D3';
  const buttonIniciarBackground = isDarkMode ? '#FEFAE0' : 'black';
  const buttonCriarBackground = isDarkMode ? '#191A19' : 'white';
  const buttonIniciarTextColor = isDarkMode ? '#191A19' : 'white';
  const buttonCriarTextColor = isDarkMode ? '#FEFAE0' : 'black';

  const onPressIniciar = () => {
    console.log('Iniciar Sessão : ', username, ' | ', password);
    login(username, password);
  };

  return (
    <View style={[styles.container, {backgroundColor}]}>
      <View style={styles.content}>
        <AppLogo width={200} height={200} alignSelf={'center'} />
        <Text style={[styles.textTitulo, {color: textColor}]}>
          Inscreve-te ou inicia sessão para aceder ao teu perfil
        </Text>
        <Text style={{color: errorLogin ? 'red' : textColor}}>Username</Text>
        <TextInput
          style={[
            styles.input,
            {
              borderColor: errorLogin ? 'red' : inputBorderColor,
              color: textColor,
            },
          ]}
          placeholder="username"
          onChangeText={setUsername}
          value={username}
        />
        <Text style={{color: errorLogin ? 'red' : textColor}}>Password</Text>
        <TextInput
          style={[
            styles.input,
            {
              borderColor: errorLogin ? 'red' : inputBorderColor,
              color: textColor,
            },
          ]}
          placeholder="password"
          onChangeText={setPassword}
          value={password}
          secureTextEntry={true}
        />
        <Text style={[styles.forgotPassword, {color: textColor}]}>
          Esqueceu-se da password?
        </Text>
        <TouchableOpacity
          style={[
            styles.buttonIniciar,
            {backgroundColor: buttonIniciarBackground},
          ]}
          onPress={onPressIniciar}>
          <Text style={[styles.textIniciar, {color: buttonIniciarTextColor}]}>
            Iniciar Sessão
          </Text>
        </TouchableOpacity>
        <TouchableOpacity
          style={[
            styles.buttonCriar,
            {
              backgroundColor: buttonCriarBackground,
              borderColor: buttonCriarTextColor,
            },
          ]}>
          <Text style={[styles.textCriar, {color: buttonCriarTextColor}]}>
            Criar Conta
          </Text>
        </TouchableOpacity>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
  content: {
    flexDirection: 'column',
    justifyContent: 'space-between',
    margin: '5%',
  },

  input: {
    height: 40,
    marginTop: 12,
    marginBottom: 12,
    borderWidth: 2,
    borderRadius: 10,
    padding: 10,
  },
  buttonIniciar: {
    alignItems: 'center',
    justifyContent: 'center',
    paddingVertical: 12,
    borderRadius: 10,
    elevation: 3,
  },
  buttonCriar: {
    alignItems: 'center',
    justifyContent: 'center',
    marginTop: 12,
    marginBottom: 12,
    paddingVertical: 12,
    borderRadius: 10,
    elevation: 3,
    borderWidth: 2,
  },
  textIniciar: {
    fontSize: 16,
    lineHeight: 21,
    fontWeight: 'bold',
    letterSpacing: 0.25,
  },
  textCriar: {
    fontSize: 16,
    lineHeight: 21,
    fontWeight: 'bold',
    letterSpacing: 0.25,
  },
  textTitulo: {
    fontSize: 26,
    fontFamily: 'Roboto',
    textAlign: 'center',
    lineHeight: 32,
    fontWeight: 'bold',
    letterSpacing: 0.25,
    marginBottom: 20,
  },
  forgotPassword: {
    marginBottom: 12,
  },
});
