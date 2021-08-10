import React, { useEffect } from "react";
import { useDispatch, useSelector } from "react-redux";
import {
  fetchSummoners,
  getSummoners,
  getLoaded,
  getLoading, getError, getErrorMessage,
} from "../state/summonersSlice";
import { getNameLength, getRegion } from "../state/settingsSlice";
import {
  LinearProgress,
  makeStyles,
  Paper,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
} from "@material-ui/core";
import Moment from "react-moment";
import moment from "moment";
import UpdateButton from "./UpdateButton";

interface Summoner {
  region: string;
  accountId: string;
  revisionDate: number;
  level: number;
  availabilityDate: number;
  name: string;
  lastUpdated: number;
}

const useStyles = makeStyles((theme) => ({
  table: {
    cursor: 'default',
    width: "100%",
    maxWidth: 950,
    marginBottom: theme.spacing(2),
    backgroundColor: theme.palette.primary.main,
    border: '1px solid rgba(145, 158, 171, 0.24)'
  },
  tableCellHeader: {
    color: theme.palette.text.primary,
    borderColor: 'rgb(46,50,54)',
    height: 60,
    fontWeight: 600
  },
  tableCell: {
    color: theme.palette.text.secondary,
    borderColor: 'rgb(46,50,54)',
    height: 46,
  },
  tableRow: {
    '&:hover': {
      backgroundColor: 'rgba(255, 255, 255, 0.08)',
      transition: 'background-color 0.35s ease'
    }
  },
  name: {
    fontWeight: 500
  },
  link: {
    color: '#0d6efd',
    textDecoration: 'none',
    '&:hover': {
      textDecoration: 'underline',
      cursor: 'pointer'
    }
  },
  alert: {
    padding: '15px',
    borderRadius: 5,
    backgroundColor: 'rgba(255,0,0,0.35)',
    color: 'white',
    marginTop: theme.spacing(2)
  },
  loading: {
    backgroundColor: '#2e609c',
  }
}));

const SummonersTable = () => {
  const classes = useStyles();
  const dispatch = useDispatch();
  const loading = useSelector(getLoading);
  const loaded = useSelector(getLoaded);
  const error = useSelector(getError);
  const errorMessage = useSelector(getErrorMessage);
  const summoners = useSelector(getSummoners);
  const nameLength = useSelector(getNameLength);
  const region = useSelector(getRegion);

  useEffect(() => {
    dispatch(fetchSummoners(new Date().valueOf(), false));
  }, [nameLength, region, dispatch]);

  if (loading)
    return (
      <LinearProgress className={classes.loading}/>
    );

  if (error) {
    return (
        <div className={`${classes.alert}`}>
            Oh no! An error occurred: '{errorMessage}'<br />
            Please <span className={classes.link} onClick={() => dispatch(fetchSummoners(new Date().valueOf(), false))}>try again.</span>{' '}
            If the issue persists, please let us know{' '}
            <a className={classes.link} target='_blank' rel='noreferrer noopener' href='https://github.com/bricefrisco/LoLNames/issues/new'>here.</a>
        </div>
    )
  }

  if (!loaded) return null;


  return (
    <TableContainer component={Paper} className={classes.table}>
      <Table size="small">
        <TableHead>
          <TableRow>
            <TableCell align="left" className={classes.tableCellHeader}>Name</TableCell>
            <TableCell align="left" className={classes.tableCellHeader}>Name Available</TableCell>
            <TableCell align="center" className={classes.tableCellHeader}>Availability Date</TableCell>
            <TableCell align="right" className={classes.tableCellHeader}>Level</TableCell>
            <TableCell align="left" className={classes.tableCellHeader}>Last Updated</TableCell>
            <TableCell align="right" className={classes.tableCellHeader}>Update</TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {summoners.map((summoner: Summoner, idx: number) => (
            <TableRow key={idx} className={classes.tableRow}>
              <TableCell align="left" className={`${classes.tableCell} ${classes.name}`} width={100}>{summoner.name}</TableCell>
              <TableCell align="left" className={classes.tableCell} width={150}>
                {moment(summoner.availabilityDate).fromNow()}
              </TableCell>
              <TableCell align="center" className={`${classes.tableCell}`} width={200}>
                <Moment
                  date={new Date(summoner.availabilityDate)}
                  format="MM/DD/YYYY hh:mm A"
                />
              </TableCell>
              <TableCell align="right" className={`${classes.tableCell}`} width={100}>{summoner.level}</TableCell>
              <TableCell align="left" className={classes.tableCell} width={150}>
                {summoner.lastUpdated
                  ? moment(summoner.lastUpdated - 5000).fromNow() // Account for slight server delay
                  : "Never"}
              </TableCell>
              <TableCell align="right" className={classes.tableCell} width={100}>
                <UpdateButton summonerName={summoner.name} />
              </TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </TableContainer>
  );
};

export default SummonersTable;
