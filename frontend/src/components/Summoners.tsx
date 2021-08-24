import React from 'react'
import {makeStyles, Typography} from "@material-ui/core";
import SummonersTable from "./SummonersTable";
import Filters from "./Filters";
import Pagination from "./Pagination";
import {useHistory, useLocation} from "react-router-dom";
import {navigate, useParams} from "../utils/api";

const useStyles = makeStyles((theme) => ({
    title: {
        color: theme.palette.text.secondary,
        fontSize: 24,
        fontWeight: 400,
        marginTop: theme.spacing(4),
        marginBottom: theme.spacing(3),
        '@media (max-width: 600px)': {
            fontSize: 24
        }
    },
    box: {
        display: 'flex',
        marginTop: theme.spacing(2),
        justifyContent: 'space-between',
        alignItems: 'flex-start',
        '@media (max-width: 850px)': {
            flexWrap: 'wrap',
            flexDirection: 'column-reverse'
        }
    },
    tableArea: {
        width: '100%'
    },
}))

const timeIsValid = (time: string | number | null) => {
    if (time === undefined || time === null) return false;
    if (isNaN(Number(time))) return false;
    return Number(time) >= 1;
}

const backwardsIsValid = (backwards: string | null) => {
    if (backwards === undefined || backwards === null) return false;
    return !(backwards !== 'true' && backwards !== 'false');
}

const nameLengthIsValid = (nameLength: string | null) => {
    if (isNaN(Number(nameLength))) return false;
    return Number(nameLength) >= 3 && Number(nameLength) <= 16;
}

const Summoners = () => {
    const classes = useStyles()
    const params = useParams();
    const history = useHistory();

    if (!timeIsValid(params.get('time')) || !backwardsIsValid(params.get('backwards'))) {
        navigate(history, new Date().valueOf(), false, null)
        return null;
    }

    const time = parseInt(params.get('time') as string)
    const backwards = params.get('backwards') === 'true'

    let nameLength = params.get('nameLength')
    if (params.get('nameLength') && !nameLengthIsValid(params.get('nameLength'))) {
        navigate(history, time, backwards, null);
        return null;
    }

    return (
        <>
            <Typography variant='h1' className={classes.title}>
                Find upcoming and expired summoner names
            </Typography>
            <div className={classes.box}>
                <div className={classes.tableArea}>
                    <Pagination showWhenLoading={false}/>
                    <SummonersTable timestamp={time} backwards={backwards} nameLength={nameLength ? parseInt(nameLength as string) : null} />
                    <Pagination />
                </div>
                <Filters />
            </div>
        </>
    )
}

export default Summoners;