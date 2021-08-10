import React from 'react'
import {Container, Divider, makeStyles, Typography} from "@material-ui/core";
import {getLoading} from "../state/summonersSlice";
import {useSelector} from "react-redux";

const useStyles = makeStyles((theme) => ({
    footer: {
        paddingTop: theme.spacing(1),
    },
    container: {
        paddingTop: theme.spacing(2),
        paddingBottom: theme.spacing(2)
    },
    disclaimer: {
        color: theme.palette.text.secondary,
        fontSize: 12,
        maxWidth: 800,
        textAlign: 'center',
        margin: 'auto'
    },
    copyright: {
        display: 'block',
        marginBottom: theme.spacing(1)
    }
}))

const Footer = () => {
    const classes = useStyles();
    const loading = useSelector(getLoading)

    if (loading) return null;

    return (
        <div className={classes.footer}>
            <Divider/>
            <Container className={classes.container}>
                <Typography className={classes.disclaimer}>
                   <span className={classes.copyright}>2021 © NamesLoL</span>
                    NamesLoL isn't endorsed by Riot Games and doesn't reflect the views or opinions of{' '}
                    Riot Games or anyone officially involved in producing or managing Riot Games properties.{' '}
                    Riot Games, and all associated properties are trademarks or registered trademarks of Riot Games,
                    Inc.
                </Typography>
            </Container>
        </div>
    )
}

export default Footer;