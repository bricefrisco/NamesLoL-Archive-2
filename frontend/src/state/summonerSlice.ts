import { createSlice } from "@reduxjs/toolkit";
import { parseResponse } from "../utils/api";

export const summonerSlice = createSlice({
  name: "summoner",
  initialState: {
    summoner: undefined,
    loading: false,
    error: false,
    errorMessage: undefined,
    open: false,
  },
  reducers: {
    loading: (state) => {
      state.loading = true;
      state.error = false;
      state.errorMessage = undefined;
      state.summoner = undefined;
      state.open = true;
    },
    loaded: (state, action) => {
      state.loading = false;
      state.error = false;
      state.errorMessage = undefined;
      state.summoner = action.payload;
      state.open = true;
    },
    errored: (state, action) => {
      state.loading = false;
      state.error = true;
      state.errorMessage = action.payload;
      state.summoner = undefined;
      state.open = true;
    },
    close: (state) => {
      state.open = false;
    }
  },
});

export const { loading, loaded, errored, close } = summonerSlice.actions;

export const getLoading = (state: any) => state.summoner.loading;
export const getError = (state: any) => state.summoner.error;
export const getErrorMessage = (state: any) => state.summoner.errorMessage;
export const getSummoner = (state: any) => state.summoner.summoner;
export const getOpen = (state: any) => state.summoner.open;

export const fetchSummoner = () => (dispatch: any, getState: any) => {
  const name = getState().settings.nameInput;
  const region = getState().settings.region;
  const hideSearch = getState().settings.hideSearch;

  dispatch(loading());

  const url = new URL(process.env.REACT_APP_BACKEND_URI + "/" + region.toLowerCase() + "/summoners/" + name);

  if (hideSearch) {
    url.searchParams.append('hideSearch', 'true')
  }

  fetch(url.toString())
    .then(parseResponse)
    .then((summoner) => dispatch(loaded(summoner)))
    .catch((err) => {
      if (err.toString().includes('summoner not found')) {
        dispatch(loaded({
          name,
          region
        }))
      } else {
        dispatch(errored(err.toString()))
      }
    });
};

export default summonerSlice.reducer;
