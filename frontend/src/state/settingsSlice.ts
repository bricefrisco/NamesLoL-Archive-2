import {createSlice} from "@reduxjs/toolkit";
import {close} from './summonerSlice';

export enum Region {
    NA = "na",
    EUNE = "eune",
    EUW = "euw",
    OCE = "oce",
    LAS = "las"
}

const parseRegion = (cookie: string) => {
    if (!cookie) return 'NA';
    cookie = cookie.replace('region=', '')
    return cookie;
}

export const settingsSlice = createSlice({
    name: "settings",
    initialState: {
        nameInput: "",
        region: parseRegion(document.cookie),
        nameLength: undefined,
        limit: false,
        hideSearch: false
    },
    reducers: {
        setName: (state, action) => {
            state.nameInput = action.payload;
        },
        setNameLength: (state, action) => {
            state.nameLength = action.payload;
        },
        setRegion: (state, action) => {
            state.region = action.payload;
            document.cookie = 'region=' + action.payload;
        },
        setLimit: (state, action) => {
            state.limit = action.payload;
        },
        setHideSearch: (state, action) => {
            state.hideSearch = action.payload;
        }
    },
});

export const {
    setName,
    setNameLength,
    setRegion,
    setLimit,
    setHideSearch
} = settingsSlice.actions;

export const getNameInput = (state: any) => state.settings.nameInput;
export const getRegion = (state: any) => state.settings.region;
export const getNameLength = (state: any) => state.settings.nameLength;
export const getLimit = (state: any) => state.settings.limit;
export const getHideSearch = (state: any) => state.settings.hideSearch;

export const toggleLimit = () => (dispatch: any) => {
    dispatch(setLimit(true))
    setTimeout(() => {
        dispatch(setLimit(false))
    }, 1300)
}

export const changeRegion = (region: any) => (dispatch: any) => {
    dispatch(setRegion(region))
    dispatch(close())
}

export default settingsSlice.reducer;
