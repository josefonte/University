import CookieManager from '@react-native-cookies/cookies';
import {API_URL} from './constants';

export const hasCookies = async () => {
  return CookieManager.get(API_URL + 'login')
    .then(cookies => cookies !== null && Object.keys(cookies).length > 0)
    .catch(error => {
      throw error;
    });
};

export const deleteCookies = async () => {
  return CookieManager.clearAll().catch(error => {
    throw error;
  });
};
