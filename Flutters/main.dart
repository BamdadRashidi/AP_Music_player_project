import 'dart:convert';
import 'dart:io';
import 'package:flutter/material.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      home:Directionality(
        textDirection:TextDirection.ltr,
        child: LoginPage(),
      ),
    );
  }
}

class LoginPage extends StatefulWidget {
  const LoginPage({Key? key}) : super(key: key);

  @override
  State<LoginPage> createState() =>Loginstate();
}

class Loginstate extends State<LoginPage> {
  final usernameController =TextEditingController();
  final passwordController =TextEditingController();

  bool passcheck=true;
  String? passError;

  bool validatePassword(String password) {
    final regex = RegExp(r'^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)[a-zA-Z\d]{8,}$');
    return regex.hasMatch(password);
  }

  bool validateUsername(String username) {
    final emailRegex = RegExp(r'^[\w-\.]+@([\w-]+\.)+[\w-]{2,4}$');
    final phoneRegex = RegExp(r'^0\d{10}$');
    return emailRegex.hasMatch(username) || phoneRegex.hasMatch(username);
  }


  Future<void> sendLoginRequest() async {
    final username = usernameController.text.trim();
    final password = passwordController.text.trim();

    if (!validatePassword(password)) {
      setState(() {
        passError = "Password must be at least 8 characters, include upper and lower case letters and numbers.";
      });
      return;
    } else {
      setState(() {
        passError = null;
      });
    }

    try {
      final socket = await Socket.connect('10.0.2.2', 1080);
      final jsonMap = {
        "action": "logIn",
        "payload": {
          "username": username,
          "password": password
        }
      };

      final jsonString = jsonEncode(jsonMap);
      socket.write(jsonString + '\n');

      // Read response
      socket.listen((List<int> event) {
        final responseString = utf8.decode(event);
        final response = jsonDecode(responseString);

        socket.destroy();

        if (response['status'] == 'success') {
          Navigator.push(
            context,
            MaterialPageRoute(builder: (context) => SecondPage()),
          );
        } else {
          // Show error as SnackBar
          ScaffoldMessenger.of(context).showSnackBar(
            SnackBar(content: Text(response['message'] ?? 'Login failed. Please sign in first.')),
          );
        }
      });

    } catch (e) {
      print("Connection error: $e");
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text("Server connection error.")),
      );
    }
  }


  @override
  Widget build(BuildContext context) {
    final screenWidth =MediaQuery.of(context).size.width;
    final screenHeight =MediaQuery.of(context).size.height;
    final isMobile = screenWidth < 600;

    return Scaffold(



      body: Stack(
        children: [

          Container(
            decoration: const BoxDecoration(
              gradient: RadialGradient(
                colors: [
                  Color.fromRGBO(0,0,0,1),
                  Color.fromRGBO(51,51,255,1),
                  Color.fromRGBO(0, 0,0,1),
                ],
                stops: [0.1, 0.4,1.0],
              ),
            ),
            child: Stack(
              children: [
                Align(
                  alignment:Alignment.center,
                  child: Image.asset("image/navakLogo.png"),

                )
              ],
            ),
          ),
          Center(
            child: Container(
              width: screenWidth *0.95,
              height: isMobile ? null : screenHeight* 0.8,
              padding: const EdgeInsets.all(16),
              decoration: BoxDecoration(
                image: DecorationImage(image: AssetImage("image/back4.jpg"),fit: BoxFit.cover
                ),
                borderRadius: BorderRadius.circular(20),
              ),
              child: isMobile
                  ? SingleChildScrollView(
                child: Column(
                  children: [

                    buildRightSide(context),
                  ],
                ),
              )
                  : Row(
                children: [

                  Expanded(child:buildRightSide(context)),
                ],
              ),
            ),
          ),
        ],
      ),
    );
  }



  Widget buildRightSide(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.all(30),
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
         Align(
           alignment: Alignment.topCenter,
           child: Image( image: AssetImage('image/navakLogo.png'),height: MediaQuery.of(context).size.height*0.15,),
         )
          ,
          LayoutBuilder(
            builder: (context, constraints) {
              return Padding(
                padding: const EdgeInsets.only(bottom: 40),
                child: Text(
                  "Navak",

                  style: TextStyle(
                    fontFamily: 'custom',
                    fontSize: 42,
                    fontWeight: FontWeight.bold,
                    foreground: Paint()
                      ..shader = const LinearGradient(
                        colors: [Color.fromRGBO(0, 0, 204, 1), Colors.white],
                      ).createShader(
                        Rect.fromLTWH(50, 60, constraints.maxWidth - 30, 70),
                      ),
                  ),
                ),
              );
            },
          ),
          TextField(
            controller: usernameController,
            style: const TextStyle(color: Colors.white),
            decoration: const InputDecoration(
              labelText: "Username (email/phone)",
              labelStyle: TextStyle(color: Colors.white),
              prefixIcon: Icon(Icons.person, color: Colors.white),
            ),
          ),
          const SizedBox(height: 16),
          TextField(
            controller: passwordController,
            obscureText: passcheck,
            style: const TextStyle(color: Colors.white),
            decoration: InputDecoration(
              labelText: "Password",
              labelStyle: const TextStyle(color: Colors.white),
              prefixIcon: const Icon(Icons.lock, color: Colors.white),
              suffixIcon: IconButton(
                icon: Icon(
                  passcheck ?Icons.visibility_off :Icons.visibility,
                  color: Colors.white,
                ),
                onPressed: () {
                  setState(() {
                    passcheck =!passcheck;
                  });
                },
              ),
              errorText: passError,
            ),
          ),
          const SizedBox(height: 24),
          ElevatedButton(
            onPressed: () {
              final password = passwordController.text.trim();
              if (usernameController.text.trim().isEmpty) {
                ScaffoldMessenger.of(context).showSnackBar(
                  SnackBar(content: Text("Username cannot be empty!")),
                );
                return;
              }
              if (!validateUsername(usernameController.text.trim())) {
                ScaffoldMessenger.of(context).showSnackBar(
                  SnackBar(content: Text("Username must be a valid email or a phone number starting with 0 and 11 digits long.")),
                );
                return;
              }

              if (!validatePassword(password)) {

                setState(() {
                  passError='Password must be at least 8 characters and include upper, lower case letters and numbers.';
                });
                return;
              }

              setState(() {
                passError=null;
              });

              sendLoginRequest();

              Navigator.push(
                context,
                MaterialPageRoute(builder: (context) => SecondPage()),
              );
            },

            style: ElevatedButton.styleFrom(
              backgroundColor:Colors.black,
              foregroundColor:Colors.white,
              padding: const EdgeInsets.symmetric(horizontal: 40, vertical: 12),
              shape: RoundedRectangleBorder(
                borderRadius: BorderRadius.circular(12),

              ),


            ),
            child: const Text("Log in"),
          ),
          const SizedBox(height:10),
          const Text("Don't have an account?", style: TextStyle(color:Colors.white)),
          SizedBox(height: 12,),
          ElevatedButton(
            onPressed: () {
              Navigator.push(context, MaterialPageRoute(builder: (context)=>Signup()));
            },
            style: ElevatedButton.styleFrom(
              backgroundColor:Colors.black,
              foregroundColor:Colors.white,
              padding: const EdgeInsets.symmetric(horizontal: 40, vertical: 12),
              shape: RoundedRectangleBorder(
                borderRadius: BorderRadius.circular(12),
              ),
            ),
            child: const Text("Sign in"),
          ),
        ],
      ),
    );
  }
}


class SecondPage extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
        appBar:  PreferredSize(preferredSize: Size.fromHeight(90), child:AppBar(

          actions: [

            IconButton(onPressed: (){
              Navigator.push(context,MaterialPageRoute(builder: (context)=>const setting()));
            }, icon: Icon(Icons.settings ,size: 40,))
            ,SizedBox(width: 10,),
            IconButton(onPressed: (){
              Navigator.push(context,MaterialPageRoute(builder: (context)=>const setting()));
            }, icon: Icon(Icons.notifications, size:40,)),
            SizedBox(width:10,),
            IconButton(onPressed: (){
              Navigator.push(context,MaterialPageRoute(builder: (context)=>const setting()));
            }, icon: Icon(Icons.account_circle_rounded,size: 40,))
          ],

          backgroundColor: Colors.black,foregroundColor: Colors.white,
          leading: Padding(padding: EdgeInsets.all(8.0),

            child: Image.asset('image/logo.png',width: 60,height: 60,),),
        ),)

    );
  }
}
class setting extends StatelessWidget {
  const setting({super.key});

  @override
  Widget build(BuildContext context) {
    return const Placeholder();
  }
}







class Signup extends StatefulWidget {
  const Signup({Key? key}) : super(key: key);

  @override
  State<Signup> createState() => _SignupState();
}

class _SignupState extends State<Signup> {
  final accountNameController =TextEditingController();
  final usernameController= TextEditingController();
  final passwordController= TextEditingController();

  bool passcheck =true;
  String? passError;

  bool validatePassword(String password) {
    final regex = RegExp(r'^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)[a-zA-Z\d]{8,}$');
    return regex.hasMatch(password);
  }
  bool validateUsername(String username) {
    final emailRegex = RegExp(r'^[\w-\.]+@([\w-]+\.)+[\w-]{2,4}$');
    final phoneRegex = RegExp(r'^0\d{10}$');
    return emailRegex.hasMatch(username) || phoneRegex.hasMatch(username);
  }


  Future<void> sendSignupRequest() async {
    final accountName =accountNameController.text.trim();
    final username =usernameController.text.trim();
    final password =passwordController.text.trim();
    if (!validatePassword(password)) {
      setState(() {
        passError ="Password must be at least 8 characters, include upper and lower case letters and numbers.";
      });
      return;
    } else {
      setState(() {
        passError =null;
      });
    }

    try {
      final socket =await Socket.connect('10.0.2.2', 1080);
      final jsonMap ={
        "action": "signIn",
        "payload":{
          "username": username,
          "password": password,
          "accountName": accountName
        }
      };

      final jsonString =jsonEncode(jsonMap);
      socket.write(jsonString + '\n');
      socket.destroy();
    } catch (e) {
      print("Connection error: $e");
    }
  }

  @override
  Widget build(BuildContext context) {
    final screenWidth = MediaQuery.of(context).size.width;
    final screenHeight = MediaQuery.of(context).size.height;
    final isMobile = screenWidth < 600;

    return Scaffold(
      body: Stack(
        children: [
          Container(
            decoration: const BoxDecoration(
              gradient: RadialGradient(
                colors: [
                  Color.fromRGBO(0, 0, 0,1),
                  Color.fromRGBO(51, 51,255,1),
                  Color.fromRGBO(0, 0, 0,1),
                ],
                stops: [0.1, 0.4,1.0],
              ),
            ),
          ),
          Center(
            child: Container(
              width: screenWidth *0.95,
              height: isMobile ? null : screenHeight *0.8,
              padding: const EdgeInsets.all(16),
              decoration: BoxDecoration(
                color: const Color.fromRGBO(0, 0, 255, 0.5),
                borderRadius: BorderRadius.circular(20),
              ),
              child:isMobile
                  ? SingleChildScrollView(
                child: Column(
                  children: [

                    buildRightSide(context),
                  ],
                ),
              )
                  : Row(
                children: [

                  Expanded(child: buildRightSide(context)),
                ],
              ),
            ),
          ),
        ],
      ),
    );
  }



  Widget buildRightSide(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.all(80),
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          LayoutBuilder(
            builder: (context, constraints) {
              return Text(
                "Navak",
                textAlign:TextAlign.center,
                style: TextStyle(
                  fontFamily:'custom',
                  fontSize:42,
                  fontWeight: FontWeight.bold,
                  foreground: Paint()
                    ..shader = const LinearGradient(
                      colors: [Color.fromRGBO(0, 0, 204, 1), Colors.white],
                    ).createShader(
                      Rect.fromLTWH(50, 60, constraints.maxWidth - 30, 70),
                    ),
                ),
              );
            },
          ),SizedBox(height: 10,),

          TextField(
            controller: accountNameController,
            style: const TextStyle(color: Colors.white),
            decoration: const InputDecoration(
              labelText: "Account name",
              labelStyle: TextStyle(color: Colors.white),
              prefixIcon: Icon(Icons.email, color: Colors.white),
            ),
          ),
          const SizedBox(height: 16),
          TextField(
            controller: usernameController,
            style: const TextStyle(color:Colors.white),
            decoration: const InputDecoration(
              labelText: "Username (email/phone)",
              labelStyle:TextStyle(color:Colors.white),
              prefixIcon:Icon(Icons.person,color:Colors.white),
            ),
          ),
          const SizedBox(height: 16),
          TextField(
            controller:passwordController,
            obscureText:passcheck,
            style: const TextStyle(color:Colors.white),
            decoration: InputDecoration(
              labelText:"Password",
              labelStyle: const TextStyle(color: Colors.white),
              prefixIcon: const Icon(Icons.lock, color: Colors.white),
              suffixIcon: IconButton(
                icon: Icon(
                  passcheck ? Icons.visibility_off : Icons.visibility,
                  color: Colors.white,
                ),
                onPressed: () {
                  setState((){
                    passcheck=!passcheck;
                  });
                },
              ),
              errorText:passError,
            ),
          ),
          const SizedBox(height: 24),
          ElevatedButton(
            onPressed: () async {
              final password =passwordController.text.trim();
              if (accountNameController.text.trim().isEmpty) {
                ScaffoldMessenger.of(context).showSnackBar(
                  SnackBar(content:Text("Account name cannot be empty!")),
                );
                return;
              }
              if (usernameController.text.trim().isEmpty) {
                ScaffoldMessenger.of(context).showSnackBar(
                  SnackBar(content:Text("Username cannot be empty!")),
                );
                return;
              }
              if (!validateUsername(usernameController.text.trim())) {
                ScaffoldMessenger.of(context).showSnackBar(
                  SnackBar(content: Text("Username must be a valid email or a phone number starting with 0 and 11 digits long.")),
                );
                return;
              }

              if (!validatePassword(password)) {
                setState((){
                  passError=
                  "Password must be at least 8 characters, include upper and lower case letters and numbers.";
                });
                return;
              }

              setState(() {
                passError =null;
              });

              await sendSignupRequest();


              Navigator.pop(context);
            },

            style: ElevatedButton.styleFrom(
              backgroundColor:Colors.black,
              foregroundColor:Colors.white,
              padding:const EdgeInsets.symmetric(horizontal: 40, vertical: 12),
              shape: RoundedRectangleBorder(
                borderRadius:BorderRadius.circular(12),
              ),
            ),
            child: const Text("Sign up"),
          ),
        ],
      ),
    );
  }
}





