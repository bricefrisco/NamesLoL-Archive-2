import {AppBar, Container, FormControl, makeStyles, MenuItem, Select, Toolbar, Typography} from "@material-ui/core";
import React from "react";
import {changeRegion, getRegion, Region} from "../state/settingsSlice";
import {useDispatch, useSelector} from "react-redux";
import LanguageIcon from '@material-ui/icons/Language';

const useStyles = makeStyles((theme) => ({
    appbar: {
        maxHeight: 48,
        minHeight: 48,
    },
    container: {
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'center',
    },
    select: {
        color: '#fff',
        fontWeight: 500,
        marginLeft: theme.spacing(1),
        backgroundColor: 'rgba(0, 0, 0, 0)',
        '& > svg': {
            color: '#fff'
        },
        "& > .MuiSelect-root.MuiSelect-select.MuiSelect-selectMenu.MuiSelect-filled": {
            paddingTop: 10,
            paddingBottom: 10,
            "&:focus,&:hover": {
                backgroundColor: "rgba(0, 0, 0, 0)",
            },
        },
    },
    region: {
        display: 'flex',
        alignItems: 'center'
    }
}));

const Navigation = () => {
    const dispatch = useDispatch();
    const classes = useStyles();
    const region = useSelector(getRegion)

    return (
        <AppBar position="static" className={classes.appbar}>
            <Toolbar className={classes.appbar}>
                <Container className={classes.container}>
                    <Typography variant="h6">
                        NamesLoL
                    </Typography>
                    <div className={classes.region}>
                        <LanguageIcon/>
                        <FormControl>
                            <Select
                                autoWidth
                                value={region}
                                variant="filled"
                                disableUnderline
                                className={classes.select}
                                onChange={(e) => dispatch(changeRegion(e.target.value))}
                            >
                                {Object.keys(Region).map((region) => (
                                    <MenuItem
                                        key={region.toString().toUpperCase()}
                                        value={region}
                                    >
                                        {region.toString().toUpperCase()}
                                    </MenuItem>
                                ))}
                            </Select>
                        </FormControl>
                    </div>
                </Container>
            </Toolbar>
        </AppBar>
    );
};

export default Navigation;
