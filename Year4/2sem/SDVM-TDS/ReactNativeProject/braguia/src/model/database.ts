import {Platform} from 'react-native';
import {Database} from '@nozbe/watermelondb';
import SQLiteAdapter from '@nozbe/watermelondb/adapters/sqlite';


import schema from './schema';
import {User, Trail, Edge, Pin, Media, RelatedTrail, RelatedPin,Socials, Contacts, Partners, App} from './model';

const adapter = new SQLiteAdapter({
  schema,
  jsi: true,
  onSetUpError: error => {
    // Handle setup error if needed
  },
});

// Then, make a Watermelon database from it!
const database = new Database({
  adapter,
  modelClasses: [
    User,
    Trail,
    Edge, 
    Pin,
    Media,
    RelatedTrail,
    RelatedPin,
    Socials,
    Contacts,
    Partners,
    App],
});

export default database;
