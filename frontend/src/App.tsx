import React from "react";
import {
    BrowserRouter as Router,
    Switch,
    Route,
} from "react-router-dom";
import NamesLoL from "./components/NamesLoL";
import Navigation from "./components/Navigation";
import {createMuiTheme, MuiThemeProvider} from "@material-ui/core";

const theme = createMuiTheme({
    palette: {
        type: 'dark',
        primary: {
            main: 'rgb(34,43,54)'
        },
        secondary: {
            main: 'rgb(23,28,36)',
        },
        text: {
            secondary: 'rgba(255, 255, 255, 0.85)'
        }
    }
})

function App() {
    return (
        <MuiThemeProvider theme={theme}>
            <Router>
                <Switch>
                    <Route path='/'>
                        <>
                            <Navigation/>
                            <NamesLoL/>
                        </>
                    </Route>
                </Switch>
            </Router>
        </MuiThemeProvider>
    );
}

export default App;
