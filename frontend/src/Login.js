import * as React from 'react';
import Avatar from '@mui/material/Avatar';
import CssBaseline from '@mui/material/CssBaseline';
import TextField from '@mui/material/TextField';
import Box from '@mui/material/Box';
import LockOutlinedIcon from '@mui/icons-material/LockOutlined';
import Typography from '@mui/material/Typography';
import Container from '@mui/material/Container';
import {createTheme, ThemeProvider} from '@mui/material/styles';
import LoadingButton from "@mui/lab/LoadingButton";
import Alert from "@mui/material/Alert";


const theme = createTheme();

export default function Login(props) {

    const [loading, setLoading] = React.useState(false);
    const [alertType, setAlertType] = React.useState("error");
    const [alertMessage, setAlertMessage] = React.useState("");

    const handleSubmit = (event) => {
        event.preventDefault();
        const data = new FormData(event.currentTarget);
        // eslint-disable-next-line no-console
        const username = data.get('username')
        const password = data.get('password')
        setLoading(true)
        fetch('https://diyhgu3wwg.execute-api.eu-west-1.amazonaws.com/default/login/', {
            method: 'POST',
            headers: {
                'Authorization': `Basic ${btoa(`${username}:${password}`)}`
            }
        }).then(response => {
                setAlertType(response.ok ? "success" : "error")
                response.json().then(data => {
                    setAlertMessage(data.message);
                    props.cookies.set('kid', data.jwt, {path: '/'});
                })
                    .then(() => setLoading(false))
                if (response.ok) {
                    props.setLoggedIn("true");
                    props.setLoggedInUserName(username);
                    localStorage.setItem("loggedIn", "true")
                    localStorage.setItem("loggedInUserName", username)

                }

            }
        )

    };

    return (
        <ThemeProvider theme={theme}>
            <Container component="main" maxWidth="xs">
                <CssBaseline/>
                <Box
                    sx={{
                        marginTop: 8,
                        display: 'flex',
                        flexDirection: 'column',
                        alignItems: 'center',
                    }}
                >
                    <Avatar sx={{m: 1, bgcolor: 'secondary.main'}}>
                        <LockOutlinedIcon/>
                    </Avatar>
                    <Typography component="h1" variant="h5">
                        Sign in
                    </Typography>
                    <Box component="form" onSubmit={handleSubmit} noValidate sx={{mt: 1}}>
                        <TextField
                            margin="normal"
                            required
                            fullWidth
                            id="username"
                            label="Username"
                            name="username"
                            autoFocus
                        />
                        <TextField
                            margin="normal"
                            required
                            fullWidth
                            name="password"
                            label="Password"
                            type="password"
                            id="password"
                        />
                        <Alert severity={alertType}
                               sx={{visibility: alertMessage ? 'visible' : 'hidden'}}>{alertMessage}</Alert>
                        <LoadingButton
                            type="submit"
                            loading={loading}
                            fullWidth
                            variant="contained"
                            sx={{mt: 3, mb: 2}}
                        >
                            Sign In
                        </LoadingButton>
                    </Box>
                </Box>
            </Container>
        </ThemeProvider>
    );
}