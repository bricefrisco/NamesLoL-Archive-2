import React from "react";
import {useDispatch, useSelector} from "react-redux";
import {fetchSummoner, getLoading} from "../state/summonerSlice";
import {
    getLimit,
    getNameInput,
    getHideSearch,
    setHideSearch,
    setName, toggleLimit,
} from "../state/settingsSlice";
import {
    IconButton,
    InputAdornment,
    makeStyles,
    TextField, Tooltip, Typography,
} from "@material-ui/core";
import SearchIcon from "@material-ui/icons/Search";
import Checkbox from '@material-ui/core/Checkbox';

const useStyles = makeStyles((theme) => ({
    input: {
        backgroundColor: 'rgba(255, 255, 255, 0.02)',
        marginTop: theme.spacing(2),
        fontFamily: "Ubuntu Mono",
        '& > div': {
            '&::before': {
                borderColor: 'rgb(46,50,54)!important',
            },
            color: theme.palette.text.secondary
        },
        '& input': {
            marginLeft: theme.spacing(1)
        }
    },
    hideSearch: {
        color: theme.palette.text.secondary,
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'flex-end'
    },
    hideSearchText: {
        fontSize: 11,
        fontWeight: 500,
        minWidth: 70,
        marginRight: theme.spacing(-1.6)
    },
    searchIcon: {
        color: theme.palette.text.secondary
    }
}));

const NameInput = () => {
    const dispatch = useDispatch();
    const classes = useStyles();
    const summonerName = useSelector(getNameInput);
    const loading = useSelector(getLoading);
    const limit = useSelector(getLimit);
    const hideSearch = useSelector(getHideSearch);

    const click = () => {
        dispatch(toggleLimit())
        dispatch(fetchSummoner());
    };

    const keypress = (e: any) => {
        if (e.keyCode === 13) {
            click();
        }
    };

    return (
        <TextField
            variant="standard"
            size="small"
            fullWidth
            className={classes.input}
            placeholder="Summoner name"
            value={summonerName}
            onKeyDown={keypress}
            inputProps={{
                style: {fontFamily: "Ubuntu Mono"},
            }}
            InputProps={{
                endAdornment: (
                    <>
                        <div className={classes.hideSearch}>
                            <Typography className={classes.hideSearchText}>Hide Search</Typography>
                            <Tooltip title="Hide search (won't add summoner name to table)">
                                <Checkbox size='small' color='default' checked={hideSearch}
                                          onChange={(e) => dispatch(setHideSearch(e.target.checked))}/>
                            </Tooltip>
                        </div>
                        <InputAdornment position="end">
                            <IconButton size="small" onClick={click}>
                                <SearchIcon className={classes.searchIcon}/>
                            </IconButton>
                        </InputAdornment>
                    </>
                ),
            }}
            onChange={(e) => dispatch(setName(e.target.value))}
            disabled={loading || limit}
        />
    );
};

export default NameInput;
