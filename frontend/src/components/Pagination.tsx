import React from "react";
import {IconButton, makeStyles} from "@material-ui/core";
import Moment from "react-moment";
import KeyboardArrowLeftIcon from "@material-ui/icons/KeyboardArrowLeft";
import KeyboardArrowRightIcon from "@material-ui/icons/KeyboardArrowRight";
import {useDispatch, useSelector} from "react-redux";
import {
    fetchSummoners,
    getPagination,
    getLoading,
    getError,
} from "../state/summonersSlice";

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
}));

interface Props {
    showWhenLoading?: boolean;
}

const Pagination = ({showWhenLoading}: Props) => {
    const classes = useStyles();
    const dispatch = useDispatch();

    const error = useSelector(getError);
    const loading = useSelector(getLoading);
    const pagination = useSelector(getPagination);

    const goBackwards = () => {
        dispatch(fetchSummoners(pagination.backwards, true));
    };

    const goForwards = () => {
        dispatch(fetchSummoners(pagination.forwards, false));
    };

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
            <Moment
                date={new Date(pagination.backwards)}
                format="MM/DD/YYYY, hh:mm:ss A"
                className={classes.time}
            />
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
