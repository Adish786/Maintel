<!doctype html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <title>Logout</title>
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <script>
        let baseUrl = '/';
        // If the user was redirected here, send them to the authRedirectUrl
        const authRedirectUrl = localStorage.getItem('authRedirectUrl');

        if (authRedirectUrl) {
            localStorage.removeItem('authRedirectUrl');
            baseUrl = authRedirectUrl;
        }
        
        if (window.location.hash.includes('triggerLogin')) {
            window.location = baseUrl + '#triggerLogin';
        } else {
            window.location = baseUrl;
        }
    </script>
    <meta name="robots" content="noindex">
    <meta name="googlebot" content="noindex">
</head>
<body>
</body>
</html>