# AP_Music_player_project
a project for AP subject about a music player called <b>"Navak"</b>

## Features:

### signing in and logging in
like any good program, you can create an account in order to sign in and log in
<b>Sign-in page</b>: you enter 3 parameters
1. Your username which is either your gmail or your phone number.
2. your password which is self explanatory.
3. your account name this is what other users see from your account.
the Json protocol structure for all almost everything your account is capable of doing is below (only request as response is almost the same for each):
```
// for signing in
{
  "action": "sign_up",
  "payload": {
    "username": "",
    "accountName": "",
    "password": ""
  }
}

// for logging in
{
  "action": "log_in",
  "payload": {
    "username": "",
    "password": ""
  }
}
```



### The home page:
this is the main hub of your program where you can upload, download and search for tracks
you can like or unlike them, set them to be explicit or not, see your top history, and navigate to other pages if you so wish.
all the Json protocols related to track is below:

### The albums page:
this is where you create albums and playlists in order to sort your tracks to your liking. you are able to share playlists, add or remove or change things to your liking here.
all the json protocols for playlist related tasks is below:

### The Now playing (track) page:
just a simple controller for playing your tracks and such
Json prototypes:


### The user settings page:
this is where you customize your account whether you want to add a profile picture, change your account related data, delete or log out from an account and other customizing actions.
more advanced account related protocols are down below:
```
// for changing password
{
  "action": "change_password",
  "payload": {
    "userId": "",
    "oldPassword": "",
    "newPassword": ""
  }
}

//for chaning username
{
  "action": "change_username",
  "payload": {
    "userId": "",
    "oldUsername": "",
    "newUsername": ""
  }
}

// for changning the accountname
{
  "action": "change_accountname",
  "payload": {
    "userId": "",
    "newaccountname": ""
  }
}

// for logging out
{
  "action": "log_out",
  "payload": {
    "userId": ""
  }
}

// for removing an account
{
  "action": "delete_account",
  "payload": {
    "userId": "",
    "password": ""
  }
}

// to change pfp
{
  "action": "change_profile_picture",
  "payload": {
    "userId": "",
    "imageBase64": "insert the base64 data of the image here"
  }
}




```


