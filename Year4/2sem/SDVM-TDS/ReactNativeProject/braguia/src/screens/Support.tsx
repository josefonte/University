import React, { useEffect, useState } from 'react';
import {
  StyleSheet,
  Text,
  ScrollView,
  View,
  Pressable,
  useColorScheme,
  Switch,
  Linking,
} from 'react-native';

import {useNavigation} from '@react-navigation/native';

import Ionicons from 'react-native-vector-icons/Ionicons';
import Octicons from 'react-native-vector-icons/Octicons';
import MaterialIcons from 'react-native-vector-icons/MaterialCommunityIcons';
import Feather from 'react-native-vector-icons/Feather';

import {AuthContext} from '../navigation/AuthContext';
import {darkModeTheme, lightModeTheme} from '../utils/themes';
import {color} from '@rneui/themed/dist/config';
import { Contacts } from '../model/model';
import database from '../model/database';
;

export default function Support() {
  const navigation = useNavigation();
  const theme = useColorScheme() === 'dark' ? darkModeTheme : lightModeTheme;
  const [isEnabled, setIsEnabled] = React.useState(false);
  const toggleSwitch = () => setIsEnabled(previousState => !previousState);

  const backgroundColor = theme.background_color;
  const titleColor = theme.text;
  const textColor = theme.text2;
  const colorDiviver = theme.color8;
  const redButtonText = theme.redButtontitle;
  const redButtonPressed = theme.redButtonPressed;
  const redButton = theme.redButton;


  const [contactsData, setContactsData] = useState<Contacts[]>([]);

  useEffect(() => {
    const fetchData = async () => {
    try{
      const contactsCollection = database.collections.get<Contacts>('contacts');
      const contacts = await contactsCollection.query().fetch();
      setContactsData(contacts);
      console.log('Contacts Data:', contacts);
    }catch(error){
      console.log(error);
    }
    };

    fetchData();
  },[]);

  const handleUrlPress = (url: string) => {
    Linking.openURL(url).catch(err =>
      console.error('Failed to open URL:', err),
    );
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
            <Ionicons
              name={'settings-outline'}
              size={17}
              color={titleColor}
              style={{paddingRight: 5}}
            />

            <Text style={[styles.pageTitle, {color: titleColor}]}>
              Centro de Apoio da Braguia
            </Text>
          </View>
        </View>
        
        {contactsData.length > 0 ? (
          <>
            <View style={styles.Section}>
              <Text style={[styles.titleSection, { color: textColor }]}>Apoio ao Cliente</Text>
              <Pressable
                onPress={() => Linking.openURL(contactsData[0].contactUrl)} // Open the URL when pressed
                style={({ pressed }) => [
                  { backgroundColor: pressed ? colorDiviver : backgroundColor },
                ]}
              >
                <View style={[styles.button, { borderBottomColor: colorDiviver }]}>
                  <MaterialIcons
                    name={'web'}
                    size={20}
                    color={textColor}
                    style={{ paddingHorizontal: 10 }}
                  />
                  <Text style={{ fontSize: 16, color: textColor }}>
                    {contactsData[0].contactUrl}
                  </Text>
                  <Feather
                    name={'external-link'}
                    size={20}
                    color={colorDiviver}
                    style={{ end: 15, position: 'absolute' }}
                  />
                </View>
              </Pressable>

              <Pressable
                onPress={() => Linking.openURL(`mailto:${contactsData[0].contactMail}`)} // Open mail client when pressed
                style={({ pressed }) => [
                  { backgroundColor: pressed ? colorDiviver : backgroundColor },
                ]}
              >
                <View style={[styles.button, { borderBottomColor: colorDiviver }]}>
                  <MaterialIcons
                    name={'email-outline'}
                    size={20}
                    color={textColor}
                    style={{ paddingHorizontal: 10 }}
                  />
                  <Text style={{ fontSize: 16, color: textColor }}>
                    {contactsData[0].contactMail}
                  </Text>
                  <Feather
                    name={'external-link'}
                    size={20}
                    color={colorDiviver}
                    style={{ end: 15, position: 'absolute' }}
                  />
                </View>
              </Pressable>

              <Pressable
                onPress={() => Linking.openURL(`tel:${contactsData[0].contactPhone}`)} // Open phone dialer when pressed
                style={({ pressed }) => [
                  { backgroundColor: pressed ? colorDiviver : backgroundColor },
                ]}
              >
                <View style={[styles.button, { borderBottomColor: colorDiviver }]}>
                  <Feather
                    name={'phone'}
                    size={18}
                    color={textColor}
                    style={{ paddingHorizontal: 10 }}
                  />
                  <Text style={{ fontSize: 16, color: textColor }}>
                    {contactsData[0].contactPhone}
                  </Text>
                  <Feather
                    name={'external-link'}
                    size={20}
                    color={colorDiviver}
                    style={{ end: 15, position: 'absolute' }}
                  />
                </View>
              </Pressable>
            </View>

            <View style={styles.Section}>
              <Text style={[styles.titleSection, { color: textColor }]}>
                Serviços de Emergência Médica
              </Text>
              <Pressable
                onPress={() => Linking.openURL(contactsData[1].contactUrl)} // Open the URL when pressed
                style={({ pressed }) => [
                  { backgroundColor: pressed ? colorDiviver : backgroundColor },
                ]}
              >
                <View style={[styles.button, { borderBottomColor: colorDiviver }]}>
                  <MaterialIcons
                    name={'web'}
                    size={20}
                    color={textColor}
                    style={{ paddingHorizontal: 10 }}
                  />
                  <Text style={{ fontSize: 16, color: textColor }}>
                    {contactsData[1].contactUrl}
                  </Text>
                  <Feather
                    name={'external-link'}
                    size={20}
                    color={colorDiviver}
                    style={{ end: 15, position: 'absolute' }}
                  />
                </View>
              </Pressable>

              <Pressable
                onPress={() => Linking.openURL(`mailto:${contactsData[1].contactMail}`)} // Open mail client when pressed
                style={({ pressed }) => [
                  { backgroundColor: pressed ? colorDiviver : backgroundColor },
                ]}
              >
                <View style={[styles.button, { borderBottomColor: colorDiviver }]}>
                  <MaterialIcons
                    name={'email-outline'}
                    size={20}
                    color={textColor}
                    style={{ paddingHorizontal: 10 }}
                  />
                  <Text style={{ fontSize: 16, color: textColor }}>
                    {contactsData[1].contactMail}
                  </Text>
                  <Feather
                    name={'external-link'}
                    size={20}
                    color={colorDiviver}
                    style={{ end: 15, position: 'absolute' }}
                  />
                </View>
              </Pressable>

              <Pressable
                onPress={() => Linking.openURL(`tel:${contactsData[1].contactPhone}`)} // Open phone dialer when pressed
                style={({ pressed }) => [
                  { backgroundColor: pressed ? colorDiviver : backgroundColor },
                ]}
              >
                <View style={[styles.button, { borderBottomColor: colorDiviver }]}>
                  <Feather
                    name={'phone'}
                    size={18}
                    color={textColor}
                    style={{ paddingHorizontal: 10 }}
                  />
                  <Text style={{ fontSize: 16, color: textColor }}>
                    {contactsData[1].contactPhone}
                  </Text>
                  <Feather
                    name={'external-link'}
                    size={20}
                    color={colorDiviver}
                    style={{ end: 15, position: 'absolute' }}
                  />
                </View>
              </Pressable>
            </View>
          </>
        ) : (
          <Text style={{ color: textColor }}>Loading contacts...</Text>
        )}
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
