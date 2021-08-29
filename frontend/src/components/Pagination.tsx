import React from "react";
import {IconButton, makeStyles} from "@material-ui/core";
import KeyboardArrowLeftIcon from "@material-ui/icons/KeyboardArrowLeft";
import KeyboardArrowRightIcon from "@material-ui/icons/KeyboardArrowRight";
import {useSelector} from "react-redux";
import MomentUtils from '@date-io/moment';
import {
    getPagination,
    getLoading,
    getError,
} from "../state/summonersSlice";
import {useHistory} from "react-router-dom";
import {navigate, useParams} from "../utils/api";
import {
    MuiPickersUtilsProvider,
    KeyboardDatePicker
} from '@material-ui/pickers';
import TodayIcon from '@material-ui/icons/Today';
import {MaterialUiPickersDate} from "@material-ui/pickers/typings/date";

const useStyles = makeStyles((theme) => ({
    pagination: {
        display: "flex",
        justifyContent: "center",
        alignItems: "center",
        marginBottom: theme.spacing(2),
    },
    time: {
        paddingLeft: theme.spacing(2),
        paddingRight: theme.spacing(2),
        paddingTop: theme.spacing(1),
        paddingBottom: theme.spacing(1),
        backgroundColor: "rgba(0, 0, 0, 0.2)",
        color: "rgb(3,169,244)",
        fontWeight: 500,
        fontSize: 14,
        borderRadius: 25,
    },
    button: {
        color: theme.palette.text.secondary,
        marginLeft: theme.spacing(1),
        marginRight: theme.spacing(1),
    },
    dateInput: {
        '&.MuiFormControl-root': {
            backgroundColor: 'rgba(0, 0, 0, 0.2)',
            paddingLeft: theme.spacing(2),
            paddingRight: theme.spacing(2),
            paddingTop: theme.spacing(0.5),
            paddingBottom: theme.spacing(0.5),
            borderRadius: 25,
            marginTop: 0,
            marginBottom: 0
        },
        '& > .MuiInput-root': {
            fontSize: 14,
            fontWeight: 500,
            color: 'rgb(3, 169, 244)',
            minWidth: '210px',
        },
        '& > .MuiInput-underline:before': {
            borderBottom: 'none!important'
        },
        '& .MuiButtonBase-root.MuiIconButton-root': {
            padding: theme.spacing(1)
        }
    },
    dateIcon: {
        '&.MuiSvgIcon-root': {
            width: '1.3rem',
            height: '1.3rem',
            color: 'rgba(255, 255, 255, 0.85)'
        }
    }
}));

interface Props {
    showWhenLoading?: boolean;
}

const Pagination = ({showWhenLoading}: Props) => {
    const classes = useStyles();
    const history = useHistory();
    const params = useParams();

    const error = useSelector(getError);
    const loading = useSelector(getLoading);
    const pagination = useSelector(getPagination);

    const goBackwards = () => {
        navigate(history, pagination.backwards, true, params.get('nameLength'))
    };

    const goForwards = () => {
        navigate(history, pagination.forwards, false, params.get('nameLength'))
    };

    const handleDateChange = (selectedDate: MaterialUiPickersDate) => {
        if (selectedDate == null) return;
        selectedDate.set('hour', 0).set('minute', 0).set('seconds', 0)
        navigate(history, selectedDate.toDate().valueOf(), false, params.get('nameLength'))
        // console.log(selectedDate.toDate().valueOf());
    }

    // test
    if (loading && !showWhenLoading) return null;
    if (error) return null;

    return (
        <div className={classes.pagination}>
            <IconButton
                size="small"
                className={classes.button}
                onClick={goBackwards}
                disabled={loading}
            >
                <KeyboardArrowLeftIcon/>
            </IconButton>
            <MuiPickersUtilsProvider utils={MomentUtils}>
                <KeyboardDatePicker
                    inputProps={{readOnly: true}}
                    className={classes.dateInput}
                    disableToolbar
                    variant="inline"
                    format="MM/DD/YYYY, hh:mm:ss A"
                    margin="normal"
                    value={new Date(pagination.backwards)}
                    onChange={(e) => handleDateChange(e)}
                    keyboardIcon={<TodayIcon className={classes.dateIcon} />}
                />
            </MuiPickersUtilsProvider>
            <IconButton
                size="small"
                className={classes.button}
                onClick={goForwards}
                disabled={loading}
            >
                <KeyboardArrowRightIcon/>
            </IconButton>
        </div>
    );
};

export default Pagination;
