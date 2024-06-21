import React from 'react';
import { TouchableOpacity, Text, StyleSheet, useColorScheme, Linking, View } from 'react-native';
import Site from './../assets/site.svg';
import Mail from './../assets/mail.svg';
import Phone from './../assets/phone2.svg';
import Redirect from './../assets/redirect.svg';

const ContactLink = ({ type, label, url }) => {
  const isDarkMode = useColorScheme() === 'dark';
  const textColor = isDarkMode ? '#FEFAE0' : 'black';

  const handlePress = () => {
    if (type === 'phone') {
      Linking.openURL(`tel:${url}`).catch((err) => console.error('Failed to open phone number:', err));
    } else if (type === 'mail') {
      Linking.openURL(`mailto:${url}`).catch((err) => console.error('Failed to open email:', err));
    } else if (type === 'site') {
      Linking.openURL(url).catch((err) => console.error('Failed to open URL:', err));
    }
  };

  const getIcon = () => {
    if (type === 'phone') {
      return <Phone height={20} width={20} />;
    } else if (type === 'mail') {
      return <Mail height={20} width={20} />;
    } else if (type === 'site') {
      return <Site height={20} width={20} />;
    }
  };

  return (
    <TouchableOpacity style={styles.imageButton} onPress={handlePress}>
      <View style={styles.rowContainer}>
        {getIcon()}
        <Text style={[styles.infoText, { color: textColor }]}>{label}</Text>
        <Redirect height={20} width={20} />
      </View>
    </TouchableOpacity>
  );
};

const styles = StyleSheet.create({
  rowContainer: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: 5,
    width: '80%',
  },
  infoText: {
    fontSize: 15,
    fontFamily: 'Roboto',
  },
  imageButton: {
    marginHorizontal: 10,
    alignItems: 'center',
    alignSelf: 'center',
  },
});

export default ContactLink;
