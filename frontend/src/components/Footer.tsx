import React from 'react'
import {Container, Divider, makeStyles, Typography} from "@material-ui/core";
import {getLoading} from "../state/summonersSlice";
import {useSelector} from "react-redux";
import GitHubIcon from '@material-ui/icons/GitHub';
import {Link} from 'react-router-dom'

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
    },
    flex: {
        display: 'flex',
        justifyContent: 'center',
        alignItems: 'center'
    },
    github: {
        marginRight: theme.spacing(1),
        color: 'rgb(66, 135, 245)',
        cursor: 'pointer'
    },
    link: {
        color: 'rgb(66, 135, 245)',
        textDecoration: 'none',
        '&:hover': {
            textDecoration: 'underline',
            cursor: 'pointer'
        }
    },
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
                    <div className={classes.flex}>
                        <a target='_blank' rel='noreferrer noopener' href='https://github.com/bricefrisco/NamesLoL'>
                            <GitHubIcon className={classes.github}/>
                        </a>
                        <span>
                            Open source. Like this project?{' '}
                            <a target='_blank' rel='noreferrer noopener' href='https://github.com/bricefrisco/NamesLoL'
                               className={classes.link}>View the code</a>
                            {' '}and give it a star!
                            To report a bug or request a feature,{' '}
                            <a target='_blank' rel='noreferrer noopener'
                               href='https://github.com/bricefrisco/NamesLoL/issues' className={classes.link}>open an issue.</a>
                        </span>
                    </div>

                    <p>NamesLoL isn't endorsed by Riot Games and doesn't reflect the views or opinions of{' '}
                        Riot Games or anyone officially involved in producing or managing Riot Games properties.{' '}
                        Riot Games, and all associated properties are trademarks or registered trademarks of Riot Games,
                        Inc.
                    </p>
                </Typography>
            </Container>
        </div>
    )
}

export default Footer;