import 'dart:convert';
import 'dart:io';
import 'package:flutter/material.dart';
import 'package:image_picker/image_picker.dart';
import 'package:local_auth/local_auth.dart';
import 'package:on_audio_query/on_audio_query.dart';
import 'package:permission_handler/permission_handler.dart';
import 'package:just_audio/just_audio.dart';
import 'package:audio_service/audio_service.dart';
import 'package:just_audio_background/just_audio_background.dart';
import 'package:rxdart/rxdart.dart';
import 'package:path_provider/path_provider.dart';
import 'package:dio/dio.dart';
import 'package:flutter/services.dart';
import 'dart:async';



import 'dart:io';
import 'package:flutter/material.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:cloud_firestore/cloud_firestore.dart';
import 'package:cloud_firestore/cloud_firestore.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'dart:convert';
import 'package:shared_preferences/shared_preferences.dart';
List<SongModel> allSongs = [];
void main() async {
  WidgetsFlutterBinding.ensureInitialized();

  // گرفتن userId ذخیره شده
  final prefs = await SharedPreferences.getInstance();
  final userId = prefs.getString('userId');

  // بارگذاری تم ذخیره‌شده
  await ThemeManager.init();

  runApp(MyApp(userId: userId));
}

class MyApp extends StatelessWidget {
  final String? userId;

  const MyApp({super.key, required this.userId});

  @override
  Widget build(BuildContext context) {
    return ValueListenableBuilder<Color>(
      valueListenable: ThemeManager.backgroundColor,
      builder: (context, bgColor, _) {
        // تغییر رنگ نوار وضعیت بر اساس پس‌زمینه
        SystemChrome.setSystemUIOverlayStyle(SystemUiOverlayStyle(
          statusBarColor: bgColor,
          statusBarIconBrightness:
          bgColor.computeLuminance() > 0.5 ? Brightness.dark : Brightness.light,
        ));

        return MaterialApp(
          debugShowCheckedModeBanner: false,
          theme: ThemeData(
            scaffoldBackgroundColor: bgColor,
            appBarTheme: AppBarTheme(
              backgroundColor: bgColor,
              foregroundColor:
              bgColor.computeLuminance() > 0.5 ? Colors.black : Colors.white,
            ),
          ),
          home: Directionality(
            textDirection: TextDirection.ltr,
            child: userId != null
                ? SecondPage(userId: userId!) // وارد صفحه اصلی
                : const LoginPage(),           // یا بره لاگین کنه
          ),
        );
      },
    );
  }
}













class LoginPage extends StatefulWidget {
  const LoginPage({Key? key}) : super(key: key);

  @override
  State<LoginPage> createState() => _LoginPageState();
}

class _LoginPageState extends State<LoginPage> {
  final usernameController = TextEditingController();
  final passwordController = TextEditingController();
  bool passVisible = true;
  String? passError;
  bool loading = false;

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

    if (!validateUsername(username)) {
      _showMessage("Username must be a valid email or phone number");
      return;
    }

    if (!validatePassword(password)) {
      setState(() {
        passError = "Password must be at least 8 chars, include upper/lower case and numbers.";
      });
      return;
    }

    setState(() {
      passError = null;
      loading = true;
    });

    try {
      final socket = await Socket.connect('192.168.1.5', 1080)
          .timeout(const Duration(seconds: 5));

      final jsonMap = {
        "action": "logIn",
        "payload": {
          "username": username,
          "password": password,
        }
      };

      print("🚀 LOGIN_REQUEST: $jsonMap");

      socket.write(jsonEncode(jsonMap) + '\n');

      String buffer = '';
      socket.listen((data) async {
        buffer += utf8.decode(data);

        if (buffer.trim().endsWith('}')) {
          try {
            final response = jsonDecode(buffer);
            final status = (response['status'] ?? '').toString().toLowerCase();
            final message = response['message'] ?? 'Unknown error';
            final userId = response['payload']?['userId'];
            globaluserId = response['payload']?['userId'];

            print("🚀 LOGIN_RESPONSE: $response");

            if (status == 'success' && userId != null) {
              print("🚀 LOGIN_SUCCESS: userId=$userId");

              final prefs = await SharedPreferences.getInstance();
              await prefs.setString('userId', userId);

              if (!mounted) return;
              Navigator.pushReplacement(
                context,
                MaterialPageRoute(builder: (_) => SecondPage(userId: userId)),
              );
            } else {
              print("🚀 LOGIN_ERROR: $message");
              _showMessage(message);
            }
          } catch (e) {
            print("🚀 LOGIN_PARSE_ERROR: $e");
            _showMessage("Invalid response from server");
          } finally {
            if (mounted) setState(() => loading = false);
            socket.destroy();
          }
        }
      }, onError: (_) {
        socket.destroy();
        print("🚀 SOCKET_ERROR");
        _showMessage("Socket error");
        if (mounted) setState(() => loading = false);
      });
    } catch (_) {
      print("🚀 CONNECTION_FAILED");
      _showMessage("Server connection failed");
      if (mounted) setState(() => loading = false);
    }
  }


  Future<void> fingerprintLogin() async {
    final auth = LocalAuthentication();

    try {
      bool canAuthenticate = await auth.canCheckBiometrics || await auth.isDeviceSupported();
      if (!canAuthenticate) {
        _showMessage("Biometric not supported");
        return;
      }

      final authenticated = await auth.authenticate(
        localizedReason: 'Authenticate to enter the app',
        options: const AuthenticationOptions(biometricOnly: true, stickyAuth: true),
      );

      if (authenticated) {
        const fakeUserId = "test-user";
        final prefs = await SharedPreferences.getInstance();
        await prefs.setString('userId', fakeUserId);

        if (!mounted) return;
        Navigator.pushReplacement(context, MaterialPageRoute(builder: (_) => SecondPage(userId: fakeUserId)));
      }
    } catch (e) {
      print('Biometric error: $e');
      _showMessage("Biometric error: $e");
    }
  }

  void _showMessage(String msg) {
    if (!mounted) return;
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(content: Text(msg), backgroundColor: Colors.red),
    );
  }

  @override
  Widget build(BuildContext context) {
    final screenWidth = MediaQuery.of(context).size.width;
    final screenHeight = MediaQuery.of(context).size.height;
    final isMobile = screenWidth < 600;

    return Scaffold(
      body: SafeArea(
        child: Stack(
          children: [
            Container(
              decoration: const BoxDecoration(
                gradient: RadialGradient(
                  colors: [Colors.black, Color.fromRGBO(51, 51, 255, 1), Colors.black],
                  stops: [0.1, 0.4, 1.0],
                ),
              ),
              child: const Align(
                alignment: Alignment.center,
                child: Image(image: AssetImage("image/navaklogo.png")),
              ),
            ),
            Center(
              child: Container(
                width: screenWidth * 0.95,
                height: isMobile ? null : screenHeight * 0.8,
                padding: const EdgeInsets.all(16),
                decoration: BoxDecoration(
                  image: const DecorationImage(
                    image: AssetImage("image/back4.jpg"),
                    fit: BoxFit.cover,
                  ),
                  borderRadius: BorderRadius.circular(20),
                ),
                child: isMobile
                    ? SingleChildScrollView(child: buildRightSide(context))
                    : Row(children: [Expanded(child: buildRightSide(context))]),
              ),
            ),
            if (loading)
              const Center(child: CircularProgressIndicator(color: Colors.white)),
          ],
        ),
      ),
    );
  }

  Widget buildRightSide(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.all(30),
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          Image.asset("image/navaklogo.png", height: MediaQuery.of(context).size.height * 0.15),
          const SizedBox(height: 24),
          const Text("Navak", style: TextStyle(fontSize: 40, color: Colors.white, fontWeight: FontWeight.bold)),
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
            obscureText: passVisible,
            style: const TextStyle(color: Colors.white),
            decoration: InputDecoration(
              labelText: "Password",
              labelStyle: const TextStyle(color: Colors.white),
              prefixIcon: const Icon(Icons.lock, color: Colors.white),
              suffixIcon: IconButton(
                icon: Icon(passVisible ? Icons.visibility_off : Icons.visibility, color: Colors.white),
                onPressed: () => setState(() => passVisible = !passVisible),
              ),
              errorText: passError,
            ),
          ),
          const SizedBox(height: 24),
          ElevatedButton(
            onPressed: loading ? null : sendLoginRequest,
            child: const Text("Log in"),
          ),
          const SizedBox(height: 16),
          ElevatedButton.icon(
            onPressed: fingerprintLogin,
            icon: const Icon(Icons.fingerprint),
            label: const Text("Login with Fingerprint"),
          ),
          const SizedBox(height: 10),
          const Text("Don't have an account?", style: TextStyle(color: Colors.white)),
          const SizedBox(height: 12),
          ElevatedButton(
            onPressed: () {
              Navigator.push(context, MaterialPageRoute(builder: (_) => const Signup()));
            },
            child: const Text("Sign up"),
          ),
        ],
      ),
    );
  }
}








class SongManager {
  static final SongManager _instance = SongManager._internal();
  factory SongManager() => _instance;
  SongManager._internal();

  final List<Song> _songs = [];
  List<Song> get songs => _songs;

  Future<void> init() async {
    await loadSongs();
  }

  Future<void> saveSongs(List<Song> songsToSave) async {
    _songs.clear();
    _songs.addAll(songsToSave);

    final prefs = await SharedPreferences.getInstance();
    final encoded = jsonEncode(_songs.map((s) => s.toJson()).toList());
    await prefs.setString('saved_songs', encoded);
  }

  Future<void> loadSongs() async {
    final prefs = await SharedPreferences.getInstance();
    final jsonString = prefs.getString('saved_songs');
    if (jsonString != null) {
      final List decoded = jsonDecode(jsonString);
      _songs.clear();
      _songs.addAll(decoded.map((e) => Song.fromJson(e)));
    }
  }
}






class Signup extends StatefulWidget {
  const Signup({Key? key}) : super(key: key);

  @override
  State<Signup> createState() => SignupState();
}

class SignupState extends State<Signup> {
  final accountNameController = TextEditingController();
  final usernameController = TextEditingController();
  final passwordController = TextEditingController();

  bool passcheck = true;
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
    final accountName = accountNameController.text.trim();
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
      final socket = await Socket.connect('192.168.1.5', 1080);
      final jsonMap = {
        "action": "signIn",
        "payload": {
          "accountName": accountName,
          "username": username,
          "password": password,
        }
      };

      socket.write(jsonEncode(jsonMap) + '\n');

      socket.listen(
            (data) {
          final responseString = utf8.decode(data);
          try {
            final response = jsonDecode(responseString);
            final status = response['status']?.toLowerCase();
            final message = response['message'] ?? 'No message from server.';

            if (status == 'success') {
              Navigator.pushReplacement(context, MaterialPageRoute(builder: (_) => const LoginPage()));
            } else {
              ScaffoldMessenger.of(context).showSnackBar(
                SnackBar(content: Text(message), backgroundColor: Colors.red),
              );
            }
          } catch (_) {
            ScaffoldMessenger.of(context).showSnackBar(
              const SnackBar(content: Text("Invalid response from server.")),
            );
          } finally {
            socket.destroy();
          }
        },
        onError: (_) {
          socket.destroy();
          ScaffoldMessenger.of(context).showSnackBar(
            const SnackBar(content: Text("Socket error.")),
          );
        },
        onDone: () {},
      );
    } catch (_) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text("Server connection error.")),
      );
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
                colors: [Colors.black, Color.fromRGBO(51, 51, 255, 1), Colors.black],
                stops: [0.1, 0.4, 1.0],
              ),
            ),
          ),
          Center(
            child: Container(
              width: screenWidth * 0.95,
              height: isMobile ? null : screenHeight * 0.8,
              padding: const EdgeInsets.all(16),
              decoration: BoxDecoration(
                color: const Color.fromRGBO(0, 0, 255, 0.5),
                borderRadius: BorderRadius.circular(20),
              ),
              child: isMobile
                  ? SingleChildScrollView(child: buildForm(context))
                  : Row(children: [Expanded(child: buildForm(context))]),
            ),
          ),
        ],
      ),
    );
  }

  Widget buildForm(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.all(80),
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
                    ).createShader(Rect.fromLTWH(50, 60, constraints.maxWidth - 30, 70)),
                ),
              );
            },
          ),
          const SizedBox(height: 24),
          TextField(
            controller: accountNameController,
            style: const TextStyle(color: Colors.white),
            decoration: const InputDecoration(
              labelText: "Account name",
              labelStyle: TextStyle(color: Colors.white),
              prefixIcon: Icon(Icons.account_circle, color: Colors.white),
            ),
          ),
          const SizedBox(height: 16),
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
                icon: Icon(passcheck ? Icons.visibility_off : Icons.visibility, color: Colors.white),
                onPressed: () => setState(() => passcheck = !passcheck),
              ),
              errorText: passError,
            ),
          ),
          const SizedBox(height: 24),
          ElevatedButton(
            onPressed: () async {
              final accountName = accountNameController.text.trim();
              final username = usernameController.text.trim();
              final password = passwordController.text.trim();

              if (accountName.isEmpty) {
                ScaffoldMessenger.of(context).showSnackBar(
                  const SnackBar(content: Text("Account name cannot be empty!")),
                );
                return;
              }

              if (username.isEmpty) {
                ScaffoldMessenger.of(context).showSnackBar(
                  const SnackBar(content: Text("Username cannot be empty!")),
                );
                return;
              }

              if (!validateUsername(username)) {
                ScaffoldMessenger.of(context).showSnackBar(
                  const SnackBar(content: Text("Username must be a valid email or a phone number starting with 0 and 11 digits long.")),
                );
                return;
              }

              if (!validatePassword(password)) {
                setState(() {
                  passError = "Password must be at least 8 characters, include upper and lower case letters and numbers.";
                });
                return;
              }

              setState(() {
                passError = null;
              });

              await sendSignupRequest();

              Navigator.pop(context);
            },
            style: ElevatedButton.styleFrom(
              backgroundColor: Colors.black,
              foregroundColor: Colors.white,
              padding: const EdgeInsets.symmetric(horizontal: 40, vertical: 12),
              shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
            ),
            child: const Text("Sign up"),
          ),
        ],
      ),
    );
  }
}




class setting extends StatelessWidget {
  const setting({Key? key}) : super(key: key);
  @override
  Widget build(BuildContext context) {
    return Center(child: Text('Settings'));
  }
}

class SecondPage extends StatefulWidget
{
  final String userId;
  const SecondPage({Key? key,required this.userId}) : super(key: key);

  @override
  State<SecondPage> createState() => HomeScreen();
}

class HomeScreen extends State<SecondPage> {
  final OnAudioQuery audioQuery = OnAudioQuery();

  bool loading = true;




  @override
  void initState() {
    super.initState();
    _initLibraryAndDownload();
  }

  Future<void> _initLibraryAndDownload() async {
    await UserLibraryManager().init(widget.userId);
    UserLibraryManager().downloadAllSongs();
    await fetchSongs();
  }



  Future<void> fetchSongs() async {

    bool permissionStatus =await audioQuery.permissionsStatus();
    if (!permissionStatus) {
      await audioQuery.permissionsRequest();
    }

    final songs = await audioQuery.querySongs(
      sortType: SongSortType.TITLE,
      orderType: OrderType.ASC_OR_SMALLER,
      uriType: UriType.EXTERNAL,
    );

    setState(() {
      allSongs = songs;
      loading = false;
    });
  }
  @override
  Widget build(BuildContext context) {
    String selectSort = "";
    String sortOption = "";
    final List<Map<String,dynamic>> Items=[
      { 'icon':Icons.settings,
        'title':'Setting',
        'page':setting(),},

      { 'icon':Icons.account_circle_outlined,
        'title':'Account',
        'page':setting(),},
      { 'icon':Icons.exit_to_app,
        'title':'Logout',
        'page':LoginPage(),}
    ];
    return Scaffold(
      appBar: PreferredSize(
        preferredSize: const Size.fromHeight(50),
        child: AppBar(
          backgroundColor: Colors.black,
          foregroundColor: Colors.white,
          leading:Builder(
            builder:(context)=>IconButton(onPressed: ()=>Scaffold.of(context).openDrawer(),
                icon: Icon(Icons.menu)
            ),
          )
          ,
        ),
      ),
      drawer: Drawer(
        child: ListView(
          padding: EdgeInsets.zero,
          children: [
            ...Items.map((item)=>  ListTile(
                leading:
                Icon(item['icon']),
                title:Text(item['title']),
                onTap:(){
                  Navigator.of(context).pop();
                  Navigator.of(context).push(
                    MaterialPageRoute(builder:(context)=>item['page']),
                  );
                }

            ) )  ],
        ),


      ),
      body: DefaultTabController(
        length: 2,
        child: Column(
          children: [
            Container(
              color: Colors.black,
              child: Padding(
                padding: const EdgeInsets.only(left: 0),
                child: Row(
                  children: [
                    Expanded(
                      child: TabBar(
                        isScrollable: true,
                        indicatorColor: Colors.white,
                        labelColor: Colors.white,
                        unselectedLabelColor: Colors.white70,
                        labelStyle: const TextStyle(
                          fontSize: 14,
                          fontWeight: FontWeight.bold,
                          shadows: [
                            Shadow(
                              blurRadius: 10,
                              color: Colors.grey,
                              offset: Offset(0, 0),
                            ),
                          ],
                        ),
                        labelPadding: const EdgeInsets.symmetric(horizontal: 16),
                        tabs: const [
                          Tab(text: 'Songs'),
                          Tab(text: 'Playlists'),
                        ],
                      ),
                    ),


                    DropdownButton<String>(
                      dropdownColor: Colors.black,
                      iconEnabledColor: Colors.white,
                      value: selectSort.isEmpty ? null : selectSort,
                      hint: const Text("Sort", style: TextStyle(color: Colors.white)),
                      underline: const SizedBox(),
                      items: const [
                        DropdownMenuItem(
                          value: 'alphabetically',
                          child: Text("Sort Alphabetically"),
                        ),
                        DropdownMenuItem(
                          value: 'artist',
                          child: Text("Sort by Artist"),
                        ),
                      ],
                      onChanged: (value) {
                        setState(() {
                          selectSort = value!;
                        });
                        UserLibraryManager._instance.sortTracks(value!);
                      },
                    ),
                  ],
                ),
              ),
            ),

            Expanded(
              child: TabBarView(
                children: [
                  loading
                      ? const Center(child: CircularProgressIndicator())
                      : MyLibraryScreen(
                    userId: widget.userId,
                    sortOption: selectSort,
                  ),
                  const PlaylistTab(),
                  const setting(),
                  const setting(),
                  const setting(),
                ],
              ),
            ),
          ],
        ),
      ),

    );
  }
}





class Song {
  String id;
  final String title;
  final String artist;
  final String album;
  String songUrl;
  final Duration duration;
  final String genre;
  final bool isExplicit;
  int playCount;
  DateTime? lastPlayed;

  Song({
    required this.id,
    required this.title,
    required this.artist,
    required this.album,
    required this.songUrl,
    required this.duration,
    required this.genre,
    this.isExplicit = false,
    this.playCount = 0,
    this.lastPlayed,
  });

  factory Song.fromSongModel(SongModel model) {
    return Song(
      id: model.id.toString(),
      title: model.title,
      artist: model.artist ?? 'Unknown Artist',
      album: model.album ?? 'Unknown Album',
      songUrl: model.uri ?? '',
      duration: Duration(milliseconds: model.duration ?? 0),
      genre: model.genre ?? 'Unknown',
      isExplicit:  false,
    );
  }

  factory Song.fromJson(Map<String, dynamic> json) {
    return Song(
      id: json['trackId'],
      title: json['trackName'],
      artist: json['artistName'] ?? 'Unknown Artist',
      album: json['album'] ?? '',
      songUrl: json['songUrl'] ?? '',
      duration: Duration(seconds: json['duration'] ?? 0),
      genre: json['genre'] ?? 'Unknown',
      isExplicit: json['isExplicit'] == true,
      playCount: json['likes'] ?? 0,
      lastPlayed: json['lastPlayed'] != null
          ? DateTime.tryParse(json['lastPlayed'])
          : null,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'trackId': id,
      'trackName': title,
      'artistName': artist,
      'album': album,
      'songUrl': songUrl,
      'duration': duration.inSeconds,
      'genre': genre,
      'isExplicit': isExplicit,
      'likes': playCount,
      'lastPlayed': lastPlayed?.toIso8601String(),
    };
  }
}




class SongStatsStorage {
  static const key ='song_stats';
  static Future<void> saveSongStats(Song song) async {
    final prefs = await SharedPreferences.getInstance();
    final Map<String, dynamic> allStats =
    json.decode(prefs.getString(key) ?? '{}');

    allStats[song.id] = song.toJson();
    await prefs.setString(key, json.encode(allStats));
  }

  static Future<Map<String, Song>> loadAllSongStats() async {
    final prefs = await SharedPreferences.getInstance();
    final jsonStr =prefs.getString(key);
    if (jsonStr ==null) return {};

    final Map<String, dynamic> data =json.decode(jsonStr);
    return data.map((key,value) =>
        MapEntry(key, Song.fromJson(value as Map<String, dynamic>)));
  }
}



Song convertFromSongModel(SongModel model) {
  return Song(
    id: model.id.toString(),
    title: model.title,
    artist: model.artist ??"Unknown",
    album: model.album ??"Unknown",
    songUrl: model.uri ?? '',
    duration: Duration(milliseconds: model.duration ??0),
    genre: model.genre ??"Unknown",

  );
}



class SongDownloader {
  final Dio dio = Dio();


  Future<Song> downloadAndCreateSong({
    required String url,
    required String title,
    required String artist,
    String album ="Unknown",
    String genre ="Unknown",
    Duration duration =Duration.zero,
  }) async {
    final dir =await getApplicationDocumentsDirectory();
    final sanitizedTitle =title.replaceAll(RegExp(r'[^\w\s]+'), '');
    final filePath = '${dir.path}/$sanitizedTitle.mp3';


    await dio.download(url, filePath);

    return Song(
      id: DateTime.now().millisecondsSinceEpoch.toString(),
      title:title,
      artist:artist,
      album:album,
      songUrl:filePath,
      duration:duration,
      genre:genre,

    );
  }
}








///TODO: fix adding/removing tracks to playlist and removing playlist with database

class PlaylistManager {
  static final PlaylistManager instance = PlaylistManager.internal();
  factory PlaylistManager() => instance;
  PlaylistManager.internal();

  final List<Playlist> playlistArray = [];

  List<Playlist> get playlists => playlistArray;

  Future<void> init() async {
    await loadPlaylist();
  }

  Future<void> deletePlaylist(String name) async {
    print("🟡 removePlaylist called for: $name");

    try {

      final socket = await Socket.connect('192.168.1.5', 1080)
          .timeout(const Duration(seconds: 10));
      print("🔌 Connected to backend for ID lookup");

      final lookupRequest = {
        "action": "getPlaylistName",
        "payload": {
          "userId": globaluserId,
          "playlistName": name,
        }
      };

      socket.add(utf8.encode(jsonEncode(lookupRequest) + '\n'));
      await socket.flush();
      print("📤 Sent getPlaylistName request for '$name'");

      final completer = Completer<void>();

      socket.listen((data) async {
        final responseString = utf8.decode(data);
        print("📥 Received backend response: $responseString");

        try {
          final response = jsonDecode(responseString);

          if (response["status"] == "success" &&
              response["payload"]?["playlistId"] != null) {
            final playlistId = response["payload"]["playlistId"];
            print("✅ Found playlistId for '$name': $playlistId");


            await _sendRemovePlaylist(name, playlistId);


            playlistArray.removeWhere((p) => p.name == name);
            await savePlaylist();
            print("🗑️ Removed '$name' locally and saved playlists.");
          } else {
            print("❌ Backend could not find playlistId for '$name'");
          }
        } catch (e) {
          print("❌ Error parsing backend response: $e");
        } finally {
          await socket.close();
          completer.complete();
        }
      });

      await completer.future;
    } catch (e) {
      print("❌ Exception in deletePlaylist: $e");
    }
  }

  Future<void> _sendRemovePlaylist(String name, String playlistId) async {
    try {
      final socket = await Socket.connect('192.168.1.5', 1080)
          .timeout(const Duration(seconds: 10));
      print("🔌 Connected to backend for removePlaylist");

      final request = {
        "action": "removePlaylist",
        "payload": {
          "userId": globaluserId,
          "playlistId": playlistId,
        }
      };

      final requestStr = jsonEncode(request) + '\n';
      socket.add(utf8.encode(requestStr));
      await socket.flush();
      print("📤 Sent removePlaylist request for '$name' with id $playlistId");

      socket.listen((data) {
        final responseString = utf8.decode(data);
        print("📥 Backend response (removePlaylist): $responseString");
        socket.close();
      });
    } catch (e) {
      print("❌ Error while sending removePlaylist: $e");
    }
  }






  void setPlaylists(List<Playlist> newPlaylists) {
    playlistArray.clear();
    playlistArray.addAll(newPlaylists);
  }



  Future<void> createPlaylist(String name) async {
    print("🟡 createPlaylist called with name: $name");

    if (playlistArray.any((p) => p.name == name)) {
      print("⚠️ Playlist with name '$name' already exists. Aborting creation.");
      return;
    }

    playlistArray.add(Playlist(name: name, songs: []));
    Playlist p = playlistArray.last;
    print("📂 Added new playlist locally: ${p.name}, playListId=${p.playListId}");

    try {
      final socket = await Socket.connect('192.168.1.5', 1080)
          .timeout(const Duration(seconds: 10));
      print("🔌 Connected to server at 192.168.1.5:1080");

      final request = {
        "action": "addPlaylist",
        "payload": {
          "userId": globaluserId,
          "playlistName": p.name,
        }
      };
      final requestStr = jsonEncode(request) + '\n';
      print("📤 Sending addPlaylist request: $requestStr");

      socket.add(utf8.encode(requestStr));
      await socket.flush();
      print("✅ Request sent and flushed to backend");

      socket.listen((data) {
        final responseString = utf8.decode(data);
        print("📥 Raw server response: $responseString");

        try {
          final response = jsonDecode(responseString);
          print("🔎 Decoded response: $response");

          if (response["payload"] != null &&
              response["payload"]["playlistId"] != null) {
            p.playListId = response["payload"]["playlistId"];
            print("🎉 Assigned playlistId=${p.playListId} to playlist '${p.name}'");
          } else {
            print("⚠️ No playlistId found in server response");
          }
        } catch (e) {
          print("❌ Failed to parse server response: $e");
        } finally {
          socket.close();
          print("🔒 Socket closed after handling response");
        }
      });
    } catch (e) {
      print("❌ Error while creating playlist on backend: $e");
    }

    savePlaylist();
    print("💾 Saved playlists locally (current count=${playlistArray.length})");
  }




  Future<void> addSongToPlaylist(String playlistName, Song song) async {
    final index = playlistArray.indexWhere((p) => p.name == playlistName);
    if (index == -1) return;

    final playlist = playlistArray[index];
    if (playlist.songs.any((s) => s.id == song.id)) return;
    playlist.songs.add(song);
    savePlaylist();
    final playlistId = playlist.playListId;
    if (playlistId == null) {
      // print("⚠️ Playlist ID is null, cannot sync with backend yet.");
      return;
    }

    try {
      final socket = await Socket.connect('192.168.1.5', 1080)
          .timeout(const Duration(seconds: 10));
      print("🔌 Connected to server at 192.168.1.5:1080");

      final request = {
        "action": "addTrackToPlayList",
        "payload": {
          "userId": globaluserId,
          "playlistId": playlistId,
          "trackId": song.id
        }
      };

      final requestStr = jsonEncode(request) + '\n';
      socket.add(utf8.encode(requestStr));
      await socket.flush();
      // print("📤 Sent addTrackToPlaylist request");

      socket.listen((data) {
        final responseString = utf8.decode(data);
        // print("📥 Server response: $responseString");
        socket.close();
      });
    } catch (e) {
      // print("❌ Error while adding song to backend: $e");
    }

  }


  Future<void> removeSongFromPlaylist(String playlistName, Song song) async{
    try {
      final playlist = playlistArray.firstWhere((p) => p.name == playlistName);
      playlist.songs.removeWhere((s) => s.id == song.id);

      final socket = await Socket.connect('192.168.1.5', 1080)
          .timeout(const Duration(seconds: 10));
      print("🔌 Connected to server at 192.168.1.5:1080");


      final request = {
        "action": "removeTrackFromPlayList",
        "payload": {
          "userId": globaluserId,
          "playlistId": playlist.playListId,
          "trackId": song.id
        }
      };
      final requestStr = jsonEncode(request) + '\n';
      socket.add(utf8.encode(requestStr));
      print('sent the request for adding playlist to database');
      await socket.flush();
      savePlaylist();
      await socket.close();
    } catch (e) {
    }
  }

  Future<void> savePlaylist() async {
    final prefs = await SharedPreferences.getInstance();
    final jsonList = playlistArray.map((p) => p.toJson()).toList();
    final jsonString = jsonEncode(jsonList);

    print("💾 Saving playlists: $jsonString");

    await prefs.setString('playlists', jsonString);
  }

  Future<void> loadPlaylist() async {
    final prefs = await SharedPreferences.getInstance();
    final jsonString = prefs.getString('playlists');

    if (jsonString != null) {
      print("📂 Loading playlists from storage: $jsonString");

      final List decoded = jsonDecode(jsonString);
      playlistArray.clear();
      playlistArray.addAll(decoded.map((e) => Playlist.fromJson(e)));

      for (var p in playlistArray) {
        print("✅ Loaded playlist: ${p.name}, ID=${p.playListId}, songs=${p.songs}");
      }
    } else {
      print("⚠️ No playlists found in storage.");
    }
  }

}





class Playlist {
  String? playListId;
  String name;
  List<Song> songs;

  Playlist({required this.name, required this.songs,this.playListId});


  Map<String, dynamic> toJson() => {
    'playlistId': playListId,
    'name': name,
    'songs': songs.map((s) => s.toJson()).toList(),
    "playlistId": playListId
  };

  factory Playlist.fromJson(Map<String, dynamic> json) => Playlist(
    name: json['name'],
    songs: (json['songs'] as List).map((s) => Song.fromJson(s)).toList(),
    playListId: json["playlistId"],);


}













class PlaylistTab extends StatefulWidget {
  const PlaylistTab({Key? key}) : super(key: key);

  @override
  PlaylistTabState createState() => PlaylistTabState();
}

class PlaylistTabState extends State<PlaylistTab> {
  @override
  void initState() {
    super.initState();
    PlaylistManager().init().then((_) {
      setState(() {});
    });
  }

  void _showPlaylistOptions(Playlist playlist) {
    showModalBottomSheet(
      context: context,
      backgroundColor: Colors.grey[900],
      shape: const RoundedRectangleBorder(
        borderRadius: BorderRadius.vertical(top: Radius.circular(16)),
      ),
      builder: (_) {
        return Wrap(
          children: [
            ListTile(
              leading: const Icon(Icons.delete, color: Colors.red),
              title: const Text("Delete Playlist", style: TextStyle(color: Colors.white)),
              onTap: () async {
                PlaylistManager().deletePlaylist(playlist.name);
                setState(() {});
                Navigator.pop(context);
              },
            ),
            ListTile(
              leading: const Icon(Icons.share, color: Colors.white),
              title: const Text("Share Playlist", style: TextStyle(color: Colors.white)),
              onTap: () {
                final songTitles = playlist.songs.map((s) => s.title).join(", ");
                Navigator.pop(context);
              },
            ),
          ],
        );
      },
    );
  }

  @override
  Widget build(BuildContext context) {
    final playlists = PlaylistManager().playlists;

    if (playlists.isEmpty) {
      return const Center(child: Text("No playlists", style: TextStyle(color: Colors.white70)));
    }

    return ListView.builder(
      itemCount: playlists.length,
      itemBuilder: (context, index) {
        final playlist = playlists[index];
        final hasSongs = playlist.songs.isNotEmpty;
        final firstSongArtworkId = hasSongs ? int.tryParse(playlist.songs.first.id) ?? 0 : 0;

        return GestureDetector(
          onLongPress: () => _showPlaylistOptions(playlist),
          child: Card(
            margin: const EdgeInsets.symmetric(horizontal: 12, vertical: 8),
            color: Colors.black87,
            child: ExpansionTile(
              leading: hasSongs
                  ? QueryArtworkWidget(
                id: firstSongArtworkId,
                type: ArtworkType.AUDIO,
                artworkBorder: BorderRadius.circular(8),
                nullArtworkWidget: const Icon(Icons.queue_music, color: Colors.white),
              )
                  : const Icon(Icons.queue_music, color: Colors.white),
              title: Text(
                playlist.name,
                style: const TextStyle(fontWeight: FontWeight.bold, color: Colors.white),
              ),
              collapsedBackgroundColor: Colors.black,
              backgroundColor: Colors.grey[850],
              children: playlist.songs.asMap().entries.map((entry) {
                final i = entry.key;
                final song = entry.value;

                return ListTile(
                  leading: QueryArtworkWidget(
                    id: int.tryParse(song.id) ?? 0,
                    type: ArtworkType.AUDIO,
                    artworkBorder: BorderRadius.circular(8),
                    nullArtworkWidget: const Icon(Icons.music_note, color: Colors.white),
                  ),
                  title: Text(song.title, style: const TextStyle(color: Colors.white)),
                  subtitle: Text(song.artist, style: const TextStyle(color: Colors.white70)),
                  trailing: IconButton(
                    icon: const Icon(Icons.remove_circle, color: Colors.redAccent),
                    onPressed: () async {
                      PlaylistManager().removeSongFromPlaylist(playlist.name, song);
                      setState(() {});
                    },
                  ),
                  onTap: () {
                    Navigator.push(
                      context,
                      MaterialPageRoute(
                        builder: (_) => PlayerScreen(
                          songs: playlist.songs,
                          initialIndex: i,
                        ),
                      ),
                    );
                  },
                );
              }).toList(),
            ),
          ),
        );
      },
    );
  }
}







class SongsTabView extends StatefulWidget {
  final List<SongModel> songs;
  const SongsTabView({Key? key, required this.songs}) : super(key: key);

  @override
  State<SongsTabView> createState() => SongsTabViewState();
}

class SongsTabViewState extends State<SongsTabView> {
  List<SongModel> filteredSongs =[];
  final TextEditingController searchController = TextEditingController();

  @override
  void initState() {
    super.initState();
    filteredSongs = List.from(widget.songs);
    searchController.addListener(SearchMusic);
  }

  @override
  void dispose() {
    searchController.dispose();
    super.dispose();
  }

  void SearchMusic() {
    final query = searchController.text.toLowerCase();
    setState(() {
      filteredSongs = widget.songs.where((song) {
        final titleMatch =song.title.toLowerCase().contains(query);
        final artistMatch =(song.artist ?? '').toLowerCase().contains(query);
        final genreMatch =(song.genre ?? '').toLowerCase().contains(query);
        return titleMatch ||artistMatch|| genreMatch;
      }).toList();
    });
  }

  @override
  Widget build(BuildContext context) {
    return Container(
      color: Colors.black,
      child: Column(
        children: [
          Padding(
            padding: const EdgeInsets.all(12),
            child: searchField(),
          ),
          Expanded(
            child: filteredSongs.isEmpty
                ? const Center(
              child: Text('No songs found',
                  style: TextStyle(color: Colors.white)),
            )
                : ListView.builder(
              itemCount:filteredSongs.length,
              itemBuilder:(context, index) {
                final songModel =filteredSongs[index];
                final actualIndex =widget.songs.indexWhere(
                        (original) =>original.id ==songModel.id);
                final song = Song.fromSongModel(songModel);

                return ListTile(
                  leading:QueryArtworkWidget(
                    id:songModel.id,
                    type: ArtworkType.AUDIO,
                    artworkBorder: BorderRadius.circular(8),
                    nullArtworkWidget:
                    const Icon(Icons.music_note, color: Colors.white),
                  ),
                  title: Text(song.title,
                      style: const TextStyle(color: Colors.white)),
                  subtitle: Text(song.artist,
                      style: const TextStyle(color: Colors.white70)),
                  onTap: () {
                    Navigator.push(
                      context,
                      MaterialPageRoute(
                        builder: (_) => PlayerScreen(
                          songs: widget.songs
                              .map((sm) => Song.fromSongModel(sm))
                              .toList(),
                          initialIndex: actualIndex,
                        ),
                      ),
                    );
                  },
                  trailing: PopupMenuButton<String>(
                    icon: const Icon(Icons.more_vert, color: Colors.white),
                    onSelected: (value) {
                      if (value == 'add_library') {
                        UserLibraryManager().addSong(song);
                        ScaffoldMessenger.of(context).showSnackBar(
                          SnackBar(
                            content: Text(
                                '"${song.title}" added to your library'),
                          ),
                        );
                      }
                    },
                    itemBuilder: (context) => [
                      const PopupMenuItem<String>(
                        value: 'add_library',
                        child: Text('Add to My Library'),
                      ),
                    ],
                  ),
                );
              },
            ),
          ),
        ],
      ),
    );
  }

  Widget searchField() {
    return TextField(
      controller: searchController,
      style: const TextStyle(color: Colors.white),
      decoration: InputDecoration(
        hintText: 'Search by title, artist, genre',
        hintStyle: const TextStyle(color: Colors.white60),
        prefixIcon: const Icon(Icons.search, color: Colors.white),
        filled: true,
        fillColor: Colors.grey[900],
        border: OutlineInputBorder(
          borderRadius: BorderRadius.circular(12),
          borderSide: BorderSide.none,
        ),
      ),
    );
  }
}







class MyLibraryScreen extends StatefulWidget {
  final String userId;
  final String sortOption;
  const MyLibraryScreen({super.key, required this.userId, required this.sortOption});


  @override
  State<MyLibraryScreen> createState() => _MyLibraryState();
}

class _MyLibraryState extends State<MyLibraryScreen> {
  List<Song> userSongs = [];
  bool isLoading = true;

  @override
  void initState() {
    super.initState();
    loadUserSongs();
  }


  Future<void> loadUserSongs() async {
    setState(() => isLoading = true);
    await UserLibraryManager().init(widget.userId);
    setState(() {
      userSongs = UserLibraryManager().userSongs;
      isLoading = false;
    });


    UserLibraryManager()._fetchFromServer(widget.userId).then((_) {
      setState(() {
        userSongs = UserLibraryManager().userSongs;
      });
    });
  }

  Future<void> refreshLibrary() async {
    setState(() => isLoading = true);
    await UserLibraryManager()._fetchFromServer(widget.userId);
    setState(() {
      userSongs = UserLibraryManager().userSongs;
      isLoading = false;
    });
  }

  void ShowOption(Song song) {
    showModalBottomSheet(
      context: context,
      backgroundColor: Colors.grey[900],
      shape: const RoundedRectangleBorder(
        borderRadius: BorderRadius.vertical(top: Radius.circular(16)),
      ),
      isScrollControlled: true,
      builder: (context) {
        return Wrap(
          children: [
            ListTile(
              leading: const Icon(Icons.playlist_add, color: Colors.white),
              title: const Text("Add to Playlist", style: TextStyle(color: Colors.white)),
              onTap: () {
                Navigator.pop(context);
                showPlaylist(song);
              },
            ),
            ListTile(
              leading: const Icon(Icons.delete, color: Colors.redAccent),
              title: const Text("Delete from Library", style: TextStyle(color: Colors.white)),
              onTap: () async {
                await UserLibraryManager().removeSong(song.id);
                setState(() {
                  userSongs = UserLibraryManager().userSongs;
                });
                Navigator.pop(context);
              },
            ),
            ListTile(
              leading: const Icon(Icons.share, color: Colors.white),
              title: const Text("Share", style: TextStyle(color: Colors.white)),
              onTap: () {
                Navigator.pop(context);
                showDialog(
                  context: context,
                  builder: (context) {
                    final TextEditingController controller = TextEditingController();
                    return AlertDialog(
                      backgroundColor: Colors.grey[850],
                      title: const Text("Share Track", style: TextStyle(color: Colors.white)),
                      content: TextField(
                        controller: controller,
                        style: const TextStyle(color: Colors.white),
                        decoration: const InputDecoration(
                          hintText: "Enter account name",
                          hintStyle: TextStyle(color: Colors.white70),
                          enabledBorder: UnderlineInputBorder(
                            borderSide: BorderSide(color: Colors.white54),
                          ),
                          focusedBorder: UnderlineInputBorder(
                            borderSide: BorderSide(color: Colors.white),
                          ),
                        ),
                      ),
                      actions: [
                        TextButton(
                          onPressed: () => Navigator.pop(context),
                          child: const Text("Cancel", style: TextStyle(color: Colors.white70)),
                        ),
                        TextButton(
                          onPressed: () async {
                            final accountName = controller.text.trim();
                            Navigator.pop(context);
                            if (accountName.isNotEmpty) {
                              await UserLibraryManager().shareSong(song.id, accountName);
                              ScaffoldMessenger.of(context).showSnackBar(
                                SnackBar(
                                  content: Text("Shared '${song.title}' with $accountName"),
                                  backgroundColor: Colors.green,
                                ),
                              );
                            }
                          },
                          child: const Text("Share", style: TextStyle(color: Colors.lightBlue)),
                        ),
                      ],
                    );
                  },
                );
              },
            ),
          ],
        );
      },
    );
  }

  void showPlaylist(Song song) {
    final playlists = PlaylistManager().playlists;

    showDialog(
      context: context,
      builder: (context) {
        TextEditingController newPlaylistcontroller = TextEditingController();
        return AlertDialog(
          backgroundColor: Colors.grey[900],
          title: const Text("Select Playlist", style: TextStyle(color: Colors.white)),
          content: SizedBox(
            height: 300,
            width: double.maxFinite,
            child: Column(
              children: [
                Expanded(
                  child: ListView.builder(
                    itemCount: playlists.length,
                    itemBuilder: (context, index) {
                      final playlist = playlists[index];
                      return ListTile(
                        title: Text(playlist.name, style: const TextStyle(color: Colors.white)),
                        onTap: () {
                          PlaylistManager().addSongToPlaylist(playlist.name, song);
                          Navigator.pop(context);
                          ScaffoldMessenger.of(context).showSnackBar(
                            SnackBar(content: Text('Added to "${playlist.name}"')),
                          );
                        },
                      );
                    },
                  ),
                ),
                TextField(
                  controller: newPlaylistcontroller,
                  style: const TextStyle(color: Colors.white),
                  decoration: InputDecoration(
                    hintText: 'New Playlist Name',
                    hintStyle: const TextStyle(color: Colors.white54),
                    filled: true,
                    fillColor: Colors.black12,
                    border: OutlineInputBorder(
                      borderRadius: BorderRadius.circular(12),
                      borderSide: BorderSide.none,
                    ),
                  ),
                ),
                const SizedBox(height: 8),
                ElevatedButton(
                  onPressed: () {
                    final name = newPlaylistcontroller.text.trim();
                    if (name.isNotEmpty) {
                      PlaylistManager().createPlaylist(name);
                      PlaylistManager().addSongToPlaylist(name, song);
                      Navigator.pop(context);
                      ScaffoldMessenger.of(context).showSnackBar(
                        SnackBar(content: Text('Added to "$name"')),
                      );
                      setState(() {});
                    }
                  },
                  child: const Text('Create and Add'),
                )
              ],
            ),
          ),
        );
      },
    );
  }

  void TransferToAddsong() {
    Navigator.push(
      context,
      MaterialPageRoute(
        builder: (_) => Scaffold(
          appBar: AppBar(
            title: const Text('All Songs'),
            centerTitle: true,
          ),
          body: SongsTabView(songs: allSongs),
        ),
      ),
    ).then((_) {
      setState(() {
        userSongs = UserLibraryManager().userSongs;
      });
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text("My Library"),
        backgroundColor: Colors.black,
        actions: [
          IconButton(
            icon: const Icon(Icons.add),
            onPressed: TransferToAddsong,
          ),
        ],
      ),
      backgroundColor: Colors.black,
      body: isLoading
          ? const Center(child: CircularProgressIndicator())
          : RefreshIndicator(
        onRefresh: refreshLibrary,
        child: userSongs.isEmpty
            ? ListView(
          children: const [
            SizedBox(height: 200),
            Center(
              child: Text(
                "No songs in your library",
                style: TextStyle(color: Colors.white70),
              ),
            ),
          ],
        )
            : ListView.builder(
          itemCount: userSongs.length,
          itemBuilder: (context, index) {
            final song = userSongs[index];
            return ListTile(
              onLongPress: () => ShowOption(song),
              leading: QueryArtworkWidget(
                id: int.tryParse(song.id) ?? 0,
                type: ArtworkType.AUDIO,
                artworkBorder: BorderRadius.circular(8),
                nullArtworkWidget: const Icon(Icons.music_note, color: Colors.white),
              ),
              title: Text(song.title, style: const TextStyle(color: Colors.white)),
              subtitle: Text(song.artist, style: const TextStyle(color: Colors.white70)),
              onTap: () {
                Navigator.push(
                  context,
                  MaterialPageRoute(
                    builder: (_) => PlayerScreen(
                      songs: userSongs,
                      initialIndex: index,
                    ),
                  ),
                );
              },
            );
          },
        ),
      ),
    );
  }
}









class UserLibraryManager {
  static final UserLibraryManager _instance = UserLibraryManager._internal();
  factory UserLibraryManager() => _instance;
  UserLibraryManager._internal();

  List<Song> userSongs = [];
  String? userIdentifier;

  Future<void> init(String identifier) async {
    userIdentifier = identifier;
    try {
      final prefs = await SharedPreferences.getInstance();
      final savedSongs = prefs.getString('songs_$userIdentifier');
      if (savedSongs != null) {
        final songList = jsonDecode(savedSongs) as List<dynamic>;
        userSongs = songList
            .map((s) => Song.fromJson(Map<String, dynamic>.from(s)))
            .toList();
      } else {
        await _fetchFromServer(identifier);
      }
    } catch (e) {
      print("init error: $e");
    }
  }

  void sortTracks(String sortType){
    print('initiating sorting: ' + sortType);
    switch(sortType){
      case('alphabetically'):
        userSongs.sort((a, b) => a.title.compareTo(b.title));
        break;
      case('artist'):
        userSongs.sort((a,b) => a.artist.compareTo(b.artist));
        break;
    }
  }

  void printAllSongIds() {
    print("📌 List of all song IDs:");
    for (var i = 0; i < userSongs.length; i++) {
      final song = userSongs[i];
      print("[$i] ${song.title} → id: ${song.id}");
    }
  }

  Future<void> downloadAllSongs() async {
    printAllSongIds();
    print("////download started ${userSongs.length} music.////");

    int index = 1;
    for (var song in userSongs) {
      print("($index/${userSongs.length}) 🎶 downloading: ${song.title} ////");
      await downloadSongById(song.id, song.title);
      index++;
    }

    print("////✅ all downloaded////");
  }

  Future<void> downloadSongById(String trackId, String fileName) async {
    if (userIdentifier == null) {
      print("❌ User identifier is null. Aborting download.");
      return;
    }

    Socket? socket;

    try {
      print("🔌 Connecting to server...");
      socket = await Socket.connect('192.168.1.5', 1080)
          .timeout(const Duration(seconds: 10));
      print("✅ Connected to server.");

      print("🎵 Preparing request for trackId: $trackId, fileName: $fileName");

      final request = {
        "action": "downloadTrack",
        "payload": {"trackId": trackId}
      };
      final requestStr = jsonEncode(request) + '\n';
      socket.add(utf8.encode(requestStr));
      await socket.flush();
      print("📤 Request sent: ${requestStr.length} chars, trackId: $trackId");


      final dir = await getApplicationDocumentsDirectory();
      final file = File('${dir.path}/$trackId.mp3');
      print("📂 Saving file to: ${file.path}");


      final line = await socket
          .cast<List<int>>()
          .transform(utf8.decoder)
          .transform(const LineSplitter())
          .first;

      print("📩 Server response (length: ${line.length})");

      try {
        final response = jsonDecode(line);
        print("✅ JSON decoded successfully.");

        final status = response['status'];
        final message = response['message'];
        print("📜 Response status: $status, message: $message");

        if (status == "success") {
          final payload = response['payload'];
          final base64Data = payload['fileData'] as String;
          print("📦 Base64 length: ${base64Data.length}");

          final bytes = base64Decode(base64Data);
          print("📏 Decoded bytes length: ${bytes.length}");

          await file.writeAsBytes(bytes);
          print("✅ File written successfully: ${file.path}");
        } else {
          print("❌ Server returned failure: $message");
        }
      } catch (e, st) {
        print("⚠️ JSON parse/handle error: $e");
        print(st);
      }

      print("🔌 Finished receiving data for trackId: $trackId");

    } catch (e, st) {
      print("❌ Error in downloadSongById: $e");
      print(st);
    } finally {
      if (socket != null) {
        await socket.close();
        print("🔌 Socket closed.");
      }
    }
  }







  Future<void> addSong(Song song) async {
    try {
      print("🎵 Starting upload for: ${song.title} by ${song.artist}");

      final uri = Uri.parse(song.songUrl);
      final bytes = await _readBytesFromContentUri(uri);

      if (bytes == null || bytes.isEmpty) {
        print("❌ Failed to read file bytes for: ${song.title}");
        return;
      }

      final base64Str = base64Encode(bytes);
      print("🔄 File encoded to Base64, length: ${base64Str.length}");

      final socket = await Socket.connect('192.168.1.5', 1080)
          .timeout(const Duration(seconds: 10));
      print("🔌 Connected to server at 192.168.1.5:1080");

      final request = {
        "action": "uploadTrack",
        "payload": {
          "userId": userIdentifier,
          "trackName": song.title,
          "artistName": song.artist,
          "genre": song.genre ?? "Default",
          "isExplicit": song.isExplicit.toString(),
          "fileData": base64Str,
        }
      };

      final requestStr = jsonEncode(request) + '\n';
      socket.add(utf8.encode(requestStr));
      await socket.flush();
      print("📤 Upload request sent (length: ${requestStr.length})");
      final responseStr = await utf8.decoder.bind(socket).join();
      print("📩 Server response received: $responseStr");

      final responseJson = jsonDecode(responseStr);
      if (responseJson['status'] == 'success') {
        final payload = responseJson['payload'];
        song.id = payload['trackId'];
        song.songUrl = payload['songUrl'];
        print("✅ Track uploaded successfully: ID=${song.id}, URL=${song.songUrl}");

        userSongs.add(song);
        await save();
        print("💾 Track added to local userSongs and saved.");
      } else {
        print("❌ Upload failed: ${responseJson['message']}");
      }

      await socket.close();
      print("🔌 Socket closed.");
    } catch (e, st) {
      print("⚠️ Error in addSong: $e");
      print(st);
    }
  }





  Future<void> _fetchFromServer(String identifier) async {
    try {
      final socket = await Socket.connect('192.168.1.5', 1080)
          .timeout(const Duration(seconds: 5));

      final request = {
        "action": "getLibrary",
        "payload": {"userId": identifier},
      };

      socket.write(jsonEncode(request) + '\n');

      String buffer = '';
      Completer<void> completer = Completer();

      socket.listen(
            (data) {
          buffer += utf8.decode(data);
        },
        onDone: () async {
          try {
            final response = jsonDecode(buffer.trim());
            if (response['status'] == 'success') {
              final songList = (response['payload']['library'] ?? response['payload']['songs']) as List<dynamic>;
              userSongs = songList
                  .map((s) => Song.fromJson(Map<String, dynamic>.from(s)))
                  .toList();
              await save();
              print("Library fetched successfully: ${userSongs.length} songs");
            } else {
              print('getLibrary failed: ${response['message']}');
            }
          } catch (e) {
            print('getLibrary parse error: $e');
          } finally {
            if (!completer.isCompleted) completer.complete();
            socket.destroy();
          }
        },
        onError: (error) {
          print('Socket error in getLibrary: $error');
          if (!completer.isCompleted) completer.complete();
          socket.destroy();
        },
        cancelOnError: true,
      );

      await completer.future.timeout(const Duration(seconds: 15), onTimeout: () {
        print('getLibrary timeout');
        socket.destroy();
      });
    } catch (e) {
      print("fetchFromServer error: $e");
    }
  }



  Future<void> save() async {
    if (userIdentifier == null) return;
    final prefs = await SharedPreferences.getInstance();
    await prefs.setString(
      'songs_$userIdentifier',
      jsonEncode(userSongs.map((s) => s.toJson()).toList()),
    );
  }

  Future<Uint8List?> _readBytesFromContentUri(Uri uri) async {
    try {
      final byteData = await MethodChannel('content_resolver')
          .invokeMethod<Uint8List>('getBytes', {'uri': uri.toString()});
      return byteData;
    } catch (e) {
      print("Error reading Content URI: $e");
      return null;
    }
  }

  Future<void> removeSong(String id) async {
    if (userIdentifier == null) return;
    userSongs.removeWhere((s) => s.id == id);
    await save();
    try {
      final socket = await Socket.connect('192.168.1.5', 1080)
          .timeout(const Duration(seconds: 5));
      final request = {
        "action": "removeTrack",
        "payload": {
          "userId": userIdentifier,
          "trackId": id,
        },
      };
      socket.write(jsonEncode(request) + '\n');
      String buffer = '';
      await socket.listen((data) {
        buffer += utf8.decode(data);
      }, onDone: () async {
        print("Server response (removeSong): $buffer");
      }).asFuture();
      await socket.close();
    } catch (e) {
      print("removeSong error: $e");
    }
  }

  Future<void> shareSong(String trackId, String accountName) async {
    if (userIdentifier == null) {
      print("❌ User identifier is null.");
      return;
    }

    Socket? socket;
    try {
      print("🔌 Connecting to server for shareTrack...");
      socket = await Socket.connect('192.168.1.5', 1080)
          .timeout(const Duration(seconds: 10));

      final request = {
        "action": "shareTrack",
        "payload": {
          "fromUserId": userIdentifier,
          "toAccountName": accountName,
          "trackId": trackId,
        }
      };

      final requestStr = jsonEncode(request) + '\n';
      socket.add(utf8.encode(requestStr));
      await socket.flush();
      print("📤 Share request sent: $requestStr");

      // read response
      final responseStr = await utf8.decoder.bind(socket).join();
      print("📩 ShareTrack server response: $responseStr");

      final response = jsonDecode(responseStr);

      if (response['status'] == 'success') {
        print("✅ Track shared successfully with $accountName");
        final payload = response['payload'];
        print("➡️ Shared with userId: ${payload?['toUserId']}");
      } else {
        print("❌ Failed to share track: ${response['message']}");
      }
    } catch (e, st) {
      print("⚠️ Error while sharing track: $e");
      print(st);
    } finally {
      if (socket != null) {
        await socket.close();
        print("🔌 Socket closed (shareTrack).");
      }
    }
  }

}

class UserLibraryScreen extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    final songs = UserLibraryManager().userSongs;

    return Scaffold(
      appBar: AppBar(title: Text("My Library")),
      body: songs.isEmpty
          ? Center(child: Text("Your library is empty."))
          : ListView.builder(
        itemCount: songs.length,
        itemBuilder: (context, index) {
          final song = songs[index];
          return ListTile(
            title: Text(song.title),
            subtitle: Text(song.artist ?? 'Unknown'),
          );
        },
      ),
    );
  }
}






class PlayerScreen extends StatefulWidget {
  final List<Song> songs;
  final int initialIndex;

  const PlayerScreen({
    Key? key,
    required this.songs,
    required this.initialIndex,
  }) : super(key: key);

  @override
  _PlayerScreenState createState() => _PlayerScreenState();
}

class _PlayerScreenState extends State<PlayerScreen> with SingleTickerProviderStateMixin {
  late AudioPlayer _player;
  late ConcatenatingAudioSource _playlist;
  int currentIndex = 0;
  bool isPlaying = false;
  bool repeatone = false;
  bool isLiked = false;

  late AnimationController circleController;

  @override
  @override
  void initState() {
    super.initState();
    _player = AudioPlayer();
    currentIndex = widget.initialIndex;


    List<AudioSource> sources = [];
    for (var song in widget.songs) {

      String fullPath = '/data/user/0/com.example.musicplayer6.musicplayer6/app_flutter/${song.songUrl.replaceFirst("tracks/", "")}';

      if (File(fullPath).existsSync()) {
        sources.add(AudioSource.file(fullPath));
        debugPrint("✅ File exists and added: $fullPath");
      } else {
        debugPrint("❌ File not found: $fullPath");
      }
    }


    _playlist = ConcatenatingAudioSource(children: sources);

    initPlayer();

    _player.currentIndexStream.listen((index) {
      if (index != null) {
        setState(() {
          currentIndex = index;
          isLiked = false;
        });
      }
    });

    _player.playerStateStream.listen((state) {
      setState(() {
        isPlaying = state.playing;
      });
    });

    circleController = AnimationController(
      vsync: this,
      duration: const Duration(seconds: 10),
    )..repeat();
  }


  Future<void> initPlayer() async {
    try {
      await _player.setAudioSource(_playlist, initialIndex: currentIndex);
      _player.play();
      debugPrint("▶️ Player started successfully");
    } catch (e) {
      debugPrint("❌ Error setting audio source: $e");
    }
  }

  @override
  void dispose() {
    _player.dispose();
    circleController.dispose();
    super.dispose();
  }

  void togglePlayer() => isPlaying ? _player.pause() : _player.play();
  void _next() => _player.seekToNext();
  void _previous() => _player.seekToPrevious();

  void toggleRepeat() {
    setState(() {
      repeatone = !repeatone;
      _player.setLoopMode(repeatone ? LoopMode.one : LoopMode.off);
    });
  }

  void toggleLike() {
    setState(() {
      isLiked = !isLiked;
    });
  }

  Stream<PositionData> _positionDataStream() {
    return Rx.combineLatest3<Duration, Duration, Duration?, PositionData>(
      _player.positionStream,
      _player.bufferedPositionStream,
      _player.durationStream,
          (position, buffered, duration) => PositionData(
        position,
        buffered,
        duration ?? Duration.zero,
      ),
    );
  }

  String formatDuration(Duration duration) {
    String twoDigits(int n) => n.toString().padLeft(2, '0');
    final minutes = twoDigits(duration.inMinutes.remainder(60));
    final seconds = twoDigits(duration.inSeconds.remainder(60));
    return "$minutes:$seconds";
  }

  @override
  Widget build(BuildContext context) {
    final song = widget.songs[currentIndex];

    return Scaffold(
      backgroundColor: Colors.black,
      body: Stack(
        children: [
          AnimatedBuilder(
            animation: circleController,
            builder: (context, child) {
              return CustomPaint(
                painter: RotaryGradientPainter(circleController.value),
                size: MediaQuery.of(context).size,
              );
            },
          ),
          SafeArea(
            child: Padding(
              padding: const EdgeInsets.all(24),
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  QueryArtworkWidget(
                    id: int.tryParse(song.id) ?? 0,
                    type: ArtworkType.AUDIO,
                    artworkHeight: 250,
                    artworkWidth: 250,
                    nullArtworkWidget: const Icon(
                      Icons.music_note,
                      size: 150,
                      color: Colors.white,
                    ),
                  ),
                  const SizedBox(height: 32),
                  Text(song.title,
                      textAlign: TextAlign.center,
                      style: const TextStyle(
                          fontSize: 24,
                          fontWeight: FontWeight.bold,
                          color: Colors.white)),
                  Text(song.artist,
                      textAlign: TextAlign.center,
                      style: const TextStyle(fontSize: 16, color: Colors.grey)),
                  const SizedBox(height: 24),
                  StreamBuilder<PositionData>(
                    stream: _positionDataStream(),
                    builder: (context, snapshot) {
                      final positionData = snapshot.data;
                      final position = positionData?.position ?? Duration.zero;
                      final duration = positionData?.duration ?? Duration.zero;

                      return Column(
                        children: [
                          Slider(
                            min: 0.0,
                            max: duration.inMilliseconds.toDouble(),
                            value: position.inMilliseconds
                                .clamp(0, duration.inMilliseconds)
                                .toDouble(),
                            onChanged: (value) {
                              _player.seek(Duration(milliseconds: value.toInt()));
                            },
                            activeColor: Colors.white,
                            inactiveColor: Colors.grey.shade600,
                          ),
                          Padding(
                            padding: const EdgeInsets.symmetric(horizontal: 8.0),
                            child: Row(
                              mainAxisAlignment: MainAxisAlignment.spaceBetween,
                              children: [
                                Text(formatDuration(position),
                                    style: const TextStyle(color: Colors.white)),
                                Text(formatDuration(duration),
                                    style: const TextStyle(color: Colors.white)),
                              ],
                            ),
                          ),
                        ],
                      );
                    },
                  ),
                  const SizedBox(height: 30),
                  Row(
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: [
                      IconButton(
                          icon: const Icon(Icons.skip_previous,
                              size: 48, color: Colors.white),
                          onPressed: _previous),
                      const SizedBox(width: 10),
                      IconButton(
                        iconSize: 64,
                        icon: Icon(
                            isPlaying
                                ? Icons.pause_circle_filled
                                : Icons.play_circle_fill,
                            color: Colors.white),
                        onPressed: togglePlayer,
                      ),
                      const SizedBox(width: 10),
                      IconButton(
                          icon: const Icon(Icons.skip_next,
                              size: 48, color: Colors.white),
                          onPressed: _next),
                    ],
                  ),
                  const SizedBox(height: 20),
                  Row(
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: [
                      IconButton(
                          icon: Icon(
                              isLiked ? Icons.favorite : Icons.favorite_border,
                              color: Colors.red),
                          onPressed: toggleLike),
                      const SizedBox(width: 32),
                      IconButton(
                          icon: Icon(
                              repeatone ? Icons.repeat_one : Icons.repeat,
                              color: Colors.blue),
                          onPressed: toggleRepeat),
                    ],
                  )
                ],
              ),
            ),
          ),
        ],
      ),
    );
  }
}
















class PositionData {
  final Duration position;
  final Duration bufferedPosition;
  final Duration duration;

  PositionData(this.position, this.bufferedPosition, this.duration);
}


class RotaryGradientPainter extends CustomPainter {
  final double rotationValue;

  RotaryGradientPainter(this.rotationValue);

  @override
  void paint(Canvas canvas, Size size) {
    final center = Offset(size.width / 2, size.height / 2);
    final radius = size.longestSide * 1.2;

    final gradient = SweepGradient(
      startAngle: 0,
      endAngle: 2 * 3.1415,
      transform: GradientRotation(2 * 3.1415 * rotationValue),
      colors: [
        Colors.blue.shade900,
        Colors.black,
        Colors.blue.shade900,
      ],
    );

    final paint = Paint()
      ..shader = gradient.createShader(Rect.fromCircle(center: center, radius: radius))
      ..style = PaintingStyle.fill;

    canvas.drawRect(
      Rect.fromLTWH(0, 0, size.width, size.height),
      paint,
    );
  }

  @override
  bool shouldRepaint(covariant CustomPainter oldDelegate) => true;
}