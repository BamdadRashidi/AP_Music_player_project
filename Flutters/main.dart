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
  final accountNameController =TextEditingController();
  final usernameController =TextEditingController();
  final passwordController =TextEditingController();

  bool passcheck=true;
  String? passError;

  bool validatePassword(String password) {
    final regex = RegExp(r'^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)[a-zA-Z\d]{8,}$');
    return regex.hasMatch(password);
  }

  Future<void> sendLoginRequest() async {
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
        passError=null;
      });
    }

    try {
      final socket=await Socket.connect('10.0.2.2', 12345);
      final jsonMap={
        "type":"login",
        "accountname":accountName,
        "username":username,
        "password":password,
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
    final screenWidth =MediaQuery.of(context).size.width;
    final screenHeight =MediaQuery.of(context).size.height;
    final isMobile = screenWidth < 600;

    return Scaffold(



         appBar:   AppBar(backgroundColor: Colors.black,

            centerTitle: true,
            title: Padding(padding: EdgeInsets.only(top: 30),
              child:  Image.asset("image/logo2 (1).png",width: 80,height: 80,),),
          ),
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
          ),
          Center(
            child: Container(
              width: screenWidth *0.95,
              height: isMobile ? null : screenHeight* 0.8,
              padding: const EdgeInsets.all(16),
              decoration: BoxDecoration(
                color: const Color.fromRGBO(0, 0,255, 0.5),
                borderRadius: BorderRadius.circular(20),
              ),
              child: isMobile
                  ? SingleChildScrollView(
                child: Column(
                  children: [
                    buildLeftSide(context),
                    const SizedBox(height:16),
                    buildRightSide(context),
                  ],
                ),
              )
                  : Row(
                children: [
                  Expanded(child:buildLeftSide(context)),
                  Expanded(child:buildRightSide(context)),
                ],
              ),
            ),
          ),
        ],
      ),
    );
  }

  Widget buildLeftSide(BuildContext context) {
    return Container(
      width: MediaQuery.of(context).size.width,
      padding: const EdgeInsets.all(24),
      decoration: BoxDecoration(
        image: const DecorationImage(
          image: AssetImage('image/back2.png'),
          fit: BoxFit.cover,
        ),
        borderRadius: BorderRadius.circular(20),
        border: Border.all(color: Colors.black, width: 1.0),
      ),
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          LayoutBuilder(
            builder: (context, constraints) {
              return Text(
                "Navak",
                textAlign: TextAlign.center,
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
              );
            },
          ),
        ],
      ),
    );
  }

  Widget buildRightSide(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.all(50),
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
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
            style: const TextStyle(color: Colors.white),
            decoration: const InputDecoration(
              labelText: "Username",
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
              if (accountNameController.text.trim().isEmpty) {
                ScaffoldMessenger.of(context).showSnackBar(
                  SnackBar(content: Text("Account name cannot be empty!")),
                );
                return;
              }
              if (usernameController.text.trim().isEmpty) {
                ScaffoldMessenger.of(context).showSnackBar(
                  SnackBar(content: Text("Username cannot be empty!")),
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
            child: const Text("Sign in"),
          ),
          const SizedBox(height:10),
          const Text("Don't have an account?", style: TextStyle(color:Colors.white)),
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
            child: const Text("Sign up"),
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
      final socket =await Socket.connect('10.0.2.2', 12345);
      final jsonMap ={
        "type": "signup",
        "accountname": accountName,
        "username": username,
        "password": password,
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
                    buildLeftSide(context),
                    const SizedBox(height: 16),
                    buildRightSide(context),
                  ],
                ),
              )
                  : Row(
                children: [
                  Expanded(child: buildLeftSide(context)),
                  Expanded(child: buildRightSide(context)),
                ],
              ),
            ),
          ),
        ],
      ),
    );
  }

  Widget buildLeftSide(BuildContext context) {
    return Container(
      width: MediaQuery.of(context).size.width,
      padding: const EdgeInsets.all(24),
      decoration: BoxDecoration(
        image: const DecorationImage(
          image: AssetImage('image/back2.png'),
          fit: BoxFit.cover,
        ),
        borderRadius: BorderRadius.circular(20),
        border: Border.all(color: Colors.black, width: 1.0),
      ),
      child: Column(
        mainAxisAlignment:MainAxisAlignment.center,
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
          ),
        ],
      ),
    );
  }

  Widget buildRightSide(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.all(50),
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
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
              labelText: "Username",
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





