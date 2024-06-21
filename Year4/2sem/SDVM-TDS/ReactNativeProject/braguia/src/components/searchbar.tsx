import React, {useState} from 'react';
import {SearchBar} from '@rneui/themed';
import {View, Text, StyleSheet, ViewStyle} from 'react-native';

interface SearchComponentProps {
  isDarkMode: boolean;
}

const SearchComponent: React.FunctionComponent<SearchComponentProps> = ({
  isDarkMode,
}) => {
  const [search, setSearch] = useState('');

  const updateSearch = (search: string) => {
    setSearch(search);
  };

  return (
    <View style={[styles.view]}>
      <SearchBar
        placeholder="Type Here..."
        onChangeText={updateSearch}
        value={search}
        lightTheme={isDarkMode ? false : true}
        containerStyle={styles.searchBarContainer}
        inputContainerStyle={styles.searchBarInputContainer}
      />
    </View>
  );
};

const styles = StyleSheet.create({
  searchBarInputContainer: {
    borderRadius: 50,
  },
  searchBarContainer: {
    borderRadius: 50,
    width: 280,
  },
  view: {
    margin: 10,
  },
});

export default SearchComponent;
