import React from "react";
import {Collapse, IconButton, LinearProgress, makeStyles, Typography} from "@material-ui/core";
import Moment from "react-moment";
import {
  getLoading,
  getOpen,
  close,
  getSummoner,
  getError,
  getErrorMessage,
  fetchSummoner
} from "../state/summonerSlice";
import {useDispatch, useSelector} from "react-redux";
import {Close} from "@material-ui/icons";
import {Alert} from "@material-ui/lab";

export interface SummonerData {
  id: string;
  accountId: string;
  puuid: string;
  name: string;
  profileIconId: number;
  revisionDate: number;
  summonerLevel: number;
  region: string;
  level: number;
  availabilityDate: number;
  lastUpdated: number;
}

const useStyles = makeStyles((theme) => ({
  card: {
    marginTop: theme.spacing(2),
    borderRadius: 5,
    minHeight: 100,
    display: "flex",
    justifyContent: "space-around",
    alignItems: "center",
    '@media (max-width: 800px)': {
      padding: theme.spacing(3),
      flexDirection: 'column',
      '& > div, & > button': {
        marginTop: theme.spacing(2)
      }
    }
  },
  available: {
    backgroundColor: "rgb(61,177,128)",
    color: "#fff"
  },
  unavailable: {
    backgroundColor: "rgb(255,152,0)",
    '& > button': {
      color: 'black'
    }
  },
  name: {
    textAlign: "center",
    fontWeight: 400,
    '@media (max-width: 800px)': {
      fontWeight: 500,
      fontSize: 24
    }
  },
  status: {
    textAlign: "center",
  },
  label: {
    marginBottom: theme.spacing(0.3),
    fontWeight: 500,
  },
  disclosure: {
    display: "flex",
    justifyContent: "flex-end",
    fontSize: 12,
    color: "lightgray",
  },
  loading: {
    marginTop: theme.spacing(2),
    backgroundColor: '#2e609c'
  },
  close: {
    '&::before': {

    },
    '&::after': {
      content: '',
      flex: '1 0 auto'
    }
  },
  alert: {
    marginTop: theme.spacing(2)
  },
  link: {
    color: '#0d6efd',
    textDecoration: 'none',
    '&:hover': {
      textDecoration: 'underline',
      cursor: 'pointer'
    }
  },
}));

const Summoner = () => {
  const classes = useStyles();
  const dispatch = useDispatch();
  const loading = useSelector(getLoading);
  const open = useSelector(getOpen);
  const summoner = useSelector(getSummoner);
  const error = useSelector(getError);
  const errorMessage = useSelector(getErrorMessage);

  if (loading) {
    return <LinearProgress className={classes.loading} />;
  }

  if (error) {
    return (
        <Alert severity='error' className={classes.alert}>
          Oh no! An error occurred: '{errorMessage}'<br />
          Please <span className={classes.link} onClick={() => dispatch(fetchSummoner())}>try again.</span>{' '}
          If the issue persists, please let us know{' '}
          <a className={classes.link} target='_blank' rel='noreferrer noopener' href='https://github.com/bricefrisco/LoLNames/issues/new'>here.</a>
        </Alert>
    )
  }

  if (!summoner) return null;

  const infoAvailable = summoner.availabilityDate !== undefined;
  const nameAvailable =
    !infoAvailable || new Date(summoner.availabilityDate) <= new Date();

  const getClassName = () => {
    if (nameAvailable) {
      return `${classes.card} ${classes.available}`;
    } else {
      return `${classes.card} ${classes.unavailable}`;
    }
  };

  return (
    <Collapse in={open}>
      <div className={getClassName()}>
        <Typography variant="h3" className={classes.name}>
          {summoner.name}
        </Typography>
        <div className={classes.status}>
          <div className={classes.label}>Status</div>
          <div>{nameAvailable ? "Available*" : "Unavailable"}</div>
        </div>
        {infoAvailable && (
          <>
            <div className={classes.status}>
              <div className={classes.label}>Availability Date</div>
              <Moment
                date={new Date(summoner.availabilityDate)}
                format="MM/DD/YYYY hh:mm A"
              />
            </div>
            <div className={classes.status}>
              <div className={classes.label}>Last Activity</div>
              <Moment
                date={new Date(summoner.revisionDate)}
                format="MM/DD/YYYY"
              />
            </div>
            <div className={classes.status}>
              <div className={classes.label}>Level</div>
              <div>{summoner.level}</div>
            </div>
          </>
        )}
        <IconButton size='small' onClick={() => dispatch(close())}>
          <Close />
        </IconButton>
      </div>
      {nameAvailable && (
        <Typography className={classes.disclosure}>
          *If the name is not blocked by Riot Games
        </Typography>
      )}
    </Collapse>
  );
};

export default Summoner;
