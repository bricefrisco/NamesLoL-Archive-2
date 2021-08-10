import React from "react";
import ReplayIcon from "@material-ui/icons/Replay";
import {CircularProgress, IconButton, makeStyles} from "@material-ui/core";
import { parseResponse } from "../utils/api";
import { getLimit, getRegion, toggleLimit } from "../state/settingsSlice";
import { updateSummoner } from "../state/summonersSlice";
import { useDispatch, useSelector } from "react-redux";
import CheckIcon from "@material-ui/icons/Check";
import ErrorIcon from "@material-ui/icons/Error";

const useStyles = makeStyles((theme) => ({
  updated: {
    marginTop: theme.spacing(0.4),
    marginRight: theme.spacing(0.5)
  },
  loading: {
    color: theme.palette.text.secondary,
    marginRight: theme.spacing(0.5),
    marginTop: theme.spacing(0.4)
  }
}))

interface Props {
  summonerName: string;
}

const UpdateButton = ({ summonerName }: Props) => {
  const dispatch = useDispatch();
  const classes = useStyles();
  const limit = useSelector(getLimit);
  const region = useSelector(getRegion);

  const [loading, setLoading] = React.useState(false);
  const [success, setSuccess] = React.useState(false);
  const [error, setError] = React.useState<undefined | string>();

  const click = () => {
    if (loading || limit) return;

    dispatch(toggleLimit())

    setError(undefined);
    setLoading(true);
    setSuccess(false);

    fetch(`${process.env.REACT_APP_BACKEND_URI}/${region}/summoners/${summonerName}`)
      .then(parseResponse)
      .then((summoner: any) => {
        dispatch(updateSummoner(summoner));
        setSuccess(true);
        setLoading(false);
        setError(undefined);
      })
      .catch((err) => {
        setLoading(false)
        setError(err)
      });
  };

  if (loading) return <CircularProgress size={24} className={classes.loading} />;
  if (success) return <CheckIcon className={classes.updated}/>;
  if (error) return <ErrorIcon />;

  return (
    <IconButton size="small" onClick={click} disabled={limit} color='inherit'>
      <ReplayIcon />
    </IconButton>
  );
};

export default UpdateButton;
