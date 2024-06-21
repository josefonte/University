import React from 'react';
import EncryptedStorage from 'react-native-encrypted-storage';
import axios from 'axios';
import { fetchUser } from '../redux/actions';
import { API_URL } from '../utils/constants';
import { deleteCookies } from '../utils/cookieManager';
import { useDispatch, useSelector } from 'react-redux';

export const AuthContext = React.createContext(); // Add this line to import the 'AuthContext' namespace

export const AuthProvider = ({ children }) => {
  const [isLoading, setIsLoading] = React.useState(true);
  const [cookies, setCookies] = React.useState(null);
  const [errorLogin, setErrorLogin] = React.useState(false);
  const [username, setUsername] = React.useState('');

  async function login(username, password) {
    console.log('login', username, password);

    try {
      setErrorLogin(false);

      deleteCookies();

      const response = await axios.post(API_URL + 'login', {
        username,
        password,
      });

      if (response.status !== 200) {
        throw new Error('Network response was not ok');
      }

      const cookiesHeader = response.headers['set-cookie'];
      console.log('[Login Request] - Cookies : ', cookiesHeader[0]);

      setIsLoading(true);

      if (cookiesHeader) {
        setCookies(cookiesHeader[0]);
        await EncryptedStorage.setItem('cookies', cookiesHeader[0]);
        setUsername(username);
        await EncryptedStorage.setItem('username', username);

        fetchUser(cookiesHeader[0]);
      }

      setTimeout(() => {
        setIsLoading(false);
      }, 500);
    } catch (error) {
      setErrorLogin(true);
      console.log('ERROR', error);
      setIsLoading(false);
    }
  }

  async function logout() {
    setIsLoading(true);

    setCookies(null);
    await EncryptedStorage.removeItem('cookies');

    setUsername(null);
    await EncryptedStorage.removeItem('username');

    await EncryptedStorage.setItem('userType', null);

    setTimeout(() => {
      setIsLoading(false);
    }, 500);
  }

  const isLoggedIn = async () => {
    setIsLoading(true);
    try {
      const cookiesStored = await EncryptedStorage.getItem('cookies');
      const usernameStored = await EncryptedStorage.getItem('username');

      console.log('cookiesStored', cookiesStored);
      console.log('usernameStored', usernameStored);

      if (cookiesStored && usernameStored) {
        setCookies(cookiesStored);
        setUsername(usernameStored);

        fetchUser(cookiesStored);
      }
    } catch (error) {
      console.log(error);
    }

    setIsLoading(false);
  };

  React.useEffect(() => {
    isLoggedIn();
  }, []);

  return (
    <AuthContext.Provider
      value={{ login, logout, isLoading, cookies, errorLogin, username }}>
      {children}
    </AuthContext.Provider>
  );
};
