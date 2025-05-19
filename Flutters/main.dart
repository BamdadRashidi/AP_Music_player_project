import 'package:flutter/material.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      debugShowCheckedModeBanner: false,

      home: Directionality(
        textDirection: TextDirection.ltr,
        child: LoginPage(),
      ),
    );
  }
}

class LoginPage extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Scaffold(


      body: Stack(
        children: [
          Container(
            decoration: BoxDecoration(
              gradient: RadialGradient(
                center: Alignment(-0.4,-0.3),
                radius: 1.2,
                colors: [
                  Color.fromRGBO(234, 181, 36, 1.0),
                  Color.fromRGBO(220, 38, 93, 1.0),
                  Color.fromRGBO(33, 33,33, 1),
                ],
                stops: [0.1,0.4,1.0],
              ),
            ),
          ),

          Center(
            child: Container(

              width: 800,
              height: 600,
              decoration: BoxDecoration(
                  color: Color.fromRGBO(255,153, 153, 0.5),
                borderRadius: BorderRadius.horizontal(left: Radius.circular(20)),


              ),
              child: Row(
                children: [
                  //
                  Expanded(
                    child: Stack(
                      children: [
                        Container(
                          padding: EdgeInsets.all(24),
                          decoration: BoxDecoration(
                              image: DecorationImage(image: AssetImage('image/back_clean.png'),fit: BoxFit.cover,),
                              borderRadius: BorderRadius.horizontal(left: Radius.circular(20)),

                              border: Border.all(
                                color: Colors.black,
                                width: 1.0,
                              )
                          ),
                          child: Column(
                            mainAxisAlignment: MainAxisAlignment.center,
                            crossAxisAlignment: CrossAxisAlignment.start,
                            children: [
                              Padding(
                                padding: const EdgeInsets.only(left:40,top: 10),
                                child: Text("Welcome to App",
                                    style: TextStyle(color: Colors.white,fontFamily: 'custom',
                                        fontSize: 32, fontWeight: FontWeight.bold)),
                              ),
                              SizedBox(height: 16),
                              Text(
                                "Please sign in to continue access",
                                style: TextStyle(fontSize: 17,color: Colors.white,fontFamily: 'custom'),
                              ),SizedBox(height: 15,),
                              Text("Music is the bond of mankind with the universe the key of understanding it \u00A0\ \u00A0\ \u00A0\ \u00A0\  being one's ear",
                                style:TextStyle(fontSize: 15,color: Colors.pink,fontFamily: 'custom'),)

                            ],
                          ),
                        ),
                        Positioned(
                            top :100,
                            left: 150,
                            child: Image.asset('image/logo.png',width:100,height: 100,))
                      ],
                    )
                  ),

                  Expanded(
                    child: Padding(
                      padding: EdgeInsets.all(24),
                      child: Column(
                        mainAxisAlignment: MainAxisAlignment.center,
                        children: [ TextField(
    decoration: InputDecoration(labelText: "Account name (name of your account)",
    prefixIcon: Icon(Icons.email),),
    ),
    SizedBox(height: 16),

                          TextField(
                            decoration: InputDecoration(labelText: "Username (email/phone number)",
                              prefixIcon: Icon(Icons.email),),
                          ),
                          SizedBox(height: 16),
                          TextField(

                            obscureText: true,
                            decoration: InputDecoration(labelText: "Password",
                            prefixIcon: Icon(Icons.lock),
                            ),
                          ),
                          SizedBox(height: 24),
                          ElevatedButton(
                            onPressed: () {
                              Navigator.push(
                                  context,
                                  MaterialPageRoute(builder: (context) => SecondPage()));
                            },
                            child: Text("Sign in"),
                            style: ElevatedButton.styleFrom(
                              backgroundColor: Colors.pink,
                              foregroundColor: Colors.white,
                              padding:
                              EdgeInsets.symmetric(horizontal: 40, vertical: 12),
                              shape: RoundedRectangleBorder(
                                  borderRadius: BorderRadius.circular(12)),
                            ),


                          ),
                          SizedBox(height: 10,),


                          Text("Don't have an account?"),SizedBox(height: 0,width: 3,)
                        ,
                          SizedBox(width: 5,),
                          GestureDetector(
                            child: Text("Sign up",style: TextStyle( decoration: TextDecoration.underline,fontSize: 18),),

                          )
                        ],
                      ),
                    ),
                  ),
                ],
              ),
            ),
          ),],),
    );
  }
}
class SecondPage extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Scaffold(

    );
  }
}
