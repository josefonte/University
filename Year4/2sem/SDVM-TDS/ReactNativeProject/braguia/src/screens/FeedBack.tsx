import React from 'react';
import {
  StyleSheet,
  Text,
  TextInput,
  View,
  Pressable,
  useColorScheme,
  Alert,
} from 'react-native';

import {useNavigation} from '@react-navigation/native';

import Ionicons from 'react-native-vector-icons/Ionicons';
import Octicons from 'react-native-vector-icons/Octicons';
import MaterialIcons from 'react-native-vector-icons/MaterialCommunityIcons';
import Feather from 'react-native-vector-icons/Feather';

import Mailer from 'react-native-mail';

import {AuthContext} from '../navigation/AuthContext';
import {darkModeTheme, lightModeTheme} from '../utils/themes';
import {color} from '@rneui/themed/dist/config';

export default function Feedback() {
  const navigation = useNavigation();
  const theme = useColorScheme() === 'dark' ? darkModeTheme : lightModeTheme;
  const [text, onChangeText] = React.useState('');

  const backgroundColor = theme.background_color;
  const titleColor = theme.text;
  const textColor = theme.text2;
  const colorDiviver = theme.color8;
  const redButtonText = theme.redButtontitle;
  const redButtonPressed = theme.redButtonPressed;
  const redButton = theme.redButton;


  const handleEmail = () => {
    Mailer.mail({
      subject: 'Feedback',
      recipients: ['pg53751@alunos.uminho.pt'], // Replace with your email address
      body: text,
      isHTML: false,
    }, (error, event) => {
      if (error) {
        Alert.alert('Error', 'Could not send email. Please try again.');
      } else {
        Alert.alert('Success', 'Email sent successfully.');
      }
    });
  };

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
            <MaterialIcons
              name={'script-text-outline'}
              size={17}
              color={textColor}
              style={{paddingRight: 8}}
            />

            <Text style={[styles.pageTitle, {color: textColor}]}>
              Enviar Feedback
            </Text>
          </View>
        </View>

        <TextInput
          style={[styles.input, {color: textColor, borderColor: colorDiviver}]}
          placeholder="Escreve-nos uma mensagem..."
          placeholderTextColor={textColor}
          onChangeText={onChangeText}
          value={text}
          multiline={true}
          textAlignVertical="top"
        />
        <Pressable
          style={[styles.buttonIniciar, {backgroundColor: titleColor}]}
          onPress={handleEmail}>
          <Text style={[styles.textIniciar, {color: backgroundColor}]}>
            Enviar Mensagem
          </Text>
        </Pressable>
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

  input: {
    height: '20%',
    borderRadius: 10,
    fontSize: 16,
    textAlign: 'left',
    textAlignVertical: 'top',
    marginTop: 20,
    marginBottom: 20,
    borderWidth: 2,
    padding: 15,
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
});
