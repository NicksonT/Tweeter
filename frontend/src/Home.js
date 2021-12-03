import * as React from 'react';
import {useEffect} from 'react';
import AppBar from '@mui/material/AppBar';
import Button from '@mui/material/Button';
import CssBaseline from '@mui/material/CssBaseline';
import Toolbar from '@mui/material/Toolbar';
import Typography from '@mui/material/Typography';
import GlobalStyles from '@mui/material/GlobalStyles';
import Container from '@mui/material/Container';
import Divider from "@mui/material/Divider";
import TextField from "@mui/material/TextField";
import LoadingButton from "@mui/lab/LoadingButton";
import CircularProgress from "@mui/material/CircularProgress";

export default function Home(props) {
    const [tweets, setTweets] = React.useState(localStorage.getItem("tweets") == null ? [] : JSON.parse(localStorage.getItem("tweets")));
    const [draftTweetLength, setDraftTweetLength] = React.useState(0);
    const [draftTweet, setDraftTweet] = React.useState("");
    const [loading, setLoading] = React.useState(false);

    const handleSubmit = (event) => {
        event.preventDefault();
        setLoading(true);
        fetch('https://diyhgu3wwg.execute-api.eu-west-1.amazonaws.com/default/tweet/', {
            method: 'POST',
            body: draftTweet,
            headers: {"Authorization": `Bearer ${props.cookies.get("kid")}`}
        }).then(() => {
                setLoading(false);
                setDraftTweetLength(0);
                setDraftTweet('');
                getTweets();
            }
        )
    }

    const onChangeFn = (event) => {
        event.preventDefault();
        setDraftTweet(event.target.value)
        setDraftTweetLength(event.target.value.length);
    }
    const getTweets = async () => {
        fetch('https://diyhgu3wwg.execute-api.eu-west-1.amazonaws.com/default/tweet/', {
            method: 'GET',
            headers: {"Authorization": `Bearer ${props.cookies.get("kid")}`}
        }).then(response => response.json())
            .then(data => {
                localStorage.setItem("tweets", JSON.stringify(data))
                setTweets(data);
                }
            )
    }
    useEffect(() => {
        const interval = setInterval(() => {
            getTweets()
        }, 60000);
        getTweets()

        return () => clearInterval(interval);
    }, []);
    return (
        <React.Fragment>
            <GlobalStyles styles={{ul: {margin: 0, padding: 0, listStyle: 'none'}}}/>
            <CssBaseline/>
            <AppBar
                position="static"
                color="default"
                elevation={0}
                sx={{borderBottom: (theme) => `1px solid ${theme.palette.divider}`}}
            >
                <Toolbar sx={{flexWrap: 'wrap'}}>
                    <Typography variant="h6" color="inherit" noWrap sx={{flexGrow: 1}}>
                        Tweeter
                    </Typography>
                    Welcome, {props.loggedInUserName}!
                    <Button variant="outlined" sx={{my: 1, mx: 1.5}}
                            onClick={() => {
                                props.setLoggedIn("false");
                                props.cookies.remove("kid")
                                localStorage.setItem("loggedIn", "false")
                            }}
                    >
                        logout
                    </Button>
                </Toolbar>
            </AppBar>
            {/* Hero unit */}
            <Container disableGutters maxWidth="sm" component="main" sx={{pt: 8, pb: 6}}>
                <TextField
                    id="outlined-multiline-flexible"
                    label="Post Tweet"
                    helperText={draftTweetLength + "/180"}
                    onChange={onChangeFn}
                    value={draftTweet}
                    multiline
                    fullWidth={true}
                    maxRows={4}
                    inputProps={{maxLength: 180}}

                />
                <LoadingButton variant="outlined"
                               loading={loading}
                               onClick={handleSubmit}
                               disabled={draftTweetLength === 0}>Post</LoadingButton>
                <Container>
                    <CircularProgress sx={{visibility: !tweets.length ? 'visible' : 'hidden'}}/>
                </Container>
                {tweets.map(tweet => <Container><Divider>{tweet.posted_date.s}</Divider><h1>{tweet.username.s}</h1>
                    <p>{tweet.tweet_body.s}</p></Container>)}
            </Container>
        </React.Fragment>
    );
}

