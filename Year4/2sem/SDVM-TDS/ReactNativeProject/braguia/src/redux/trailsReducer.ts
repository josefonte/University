const initialState = {
  loading: false,
  error: null,
  viajar: false,
  premium: false,
};

const trailsReducer = (state = initialState, action: any) => {
  console.log(initialState.premium);
  switch (action.type) {
    case 'FETCH_TRAILS_REQUEST':
      return {
        ...state,
        loading: true,
        error: null,
      };
    case 'FETCH_TRAILS_SUCCESS':
      return {
        ...state,
        loading: false,
      };
    case 'FETCH_TRAILS_FAILURE':
      return {
        ...state,
        loading: false,
        error: action.payload,
      };
    case 'COMECEI_A_VIAJAR':
      return {
        ...state,
        viajar: true,
      };
    case 'ACABEI_DE_VIAJAR':
      return {
        ...state,
        viajar: false,
      };
    case 'COMECEI_A_VIAJAR':
      return {
        ...state,
        viajar: true,
      };
    case 'EU_SOU_PREMIUM':
      return {
        ...state,
        premium: true,
      };
    default:
      return state;
  }
};

export default trailsReducer;
