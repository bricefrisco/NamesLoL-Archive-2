import {createSlice} from "@reduxjs/toolkit";
import {parseResponse} from "../utils/api";
import {SummonerData} from "../components/Summoner";

export const summonersSlice = createSlice({
    name: "summoners",
    initialState: {
        summoners: undefined,
        loaded: false,
        loading: false,
        error: false,
        errorMessage: undefined,
        pagination: {
            backwards: undefined,
            forwards: undefined,
        },
    },
    reducers: {
        pagination: (state, action) => {
            state.pagination.backwards = action.payload.backwards;
            state.pagination.forwards = action.payload.forwards;
        },
        loading: (state) => {
            state.loaded = false;
            state.loading = true;
            state.error = false;
            state.errorMessage = undefined;
            state.summoners = undefined;
        },
        loaded: (state, action) => {
            state.loaded = true;
            state.loading = false;
            state.error = false;
            state.errorMessage = undefined;
            state.summoners = action.payload;
        },
        errored: (state, action) => {
            state.loaded = false;
            state.loading = false;
            state.error = true;
            state.errorMessage = action.payload;
            state.summoners = undefined;
        },
    },
});

export const {pagination, loading, loaded, errored} = summonersSlice.actions;

export const getSummoners = (state: any) => state.summoners.summoners;
export const getLoading = (state: any) => state.summoners.loading;
export const getLoaded = (state: any) => state.summoners.loaded;
export const getError = (state: any) => state.summoners.error;
export const getErrorMessage = (state: any) => state.summoners.errorMessage;
export const getPagination = (state: any) => state.summoners.pagination;

export const fetchSummoners = (timestamp: number, backwards: boolean, nameLength: number | null) => (
    dispatch: any,
    getState: any
) => {
    dispatch(loading());
    const region = getState().settings.region;

    const url = new URL(`${process.env.REACT_APP_BACKEND_URI}/${region}/summoners`);
    url.searchParams.append("timestamp", String(timestamp));
    url.searchParams.append("backwards", String(backwards));
    if (nameLength) url.searchParams.append("nameLength", String(nameLength));

    fetch(url.toString())
        .then(parseResponse)
        .then((response: any) => {
            dispatch(loaded(response.summoners));
            dispatch(
                pagination({
                    forwards: response.forwards,
                    backwards: response.backwards,
                })
            )
        })
        .catch((err) => dispatch(errored(err.toString())));
};

export const updateSummoner = (summoner: SummonerData) => (
    dispatch: any,
    getState: any
) => {
    const summoners = getState().summoners.summoners;
    const updatedSummoners = summoners.map((s: SummonerData) => {
        if (s.name.replace(/ /g, '').toLowerCase() !== summoner.name.replace(/ /g, '').toLowerCase()) return s;
        return {...summoner, name: s.name.toLowerCase()};
    });
    dispatch(loaded(updatedSummoners));
};

export default summonersSlice.reducer;
