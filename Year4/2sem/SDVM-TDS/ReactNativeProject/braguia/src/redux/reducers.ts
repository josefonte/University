import { combineReducers } from 'redux';
import trailsReducer from './trailsReducer';


const rootReducer = combineReducers({
  trails: trailsReducer,
});

export default rootReducer;
