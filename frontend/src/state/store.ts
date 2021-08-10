import { configureStore } from "@reduxjs/toolkit";
import settingsReducer from "./settingsSlice";
import summonerReducer from "./summonerSlice";
import summonersReducer from "./summonersSlice";

export default configureStore({
  reducer: {
    settings: settingsReducer,
    summoner: summonerReducer,
    summoners: summonersReducer,
  },
});
