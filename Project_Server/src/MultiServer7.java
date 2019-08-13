import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.*;

public class MultiServer7 {

    static {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
        } catch (ClassNotFoundException cnfe) {
            cnfe.printStackTrace();

        }
    }

    {
        try {
            con = DriverManager.getConnection(
                    "jdbc:oracle:thin:@localhost:1521:xe",
                    "scott",
                    "tiger");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    Scanner sc = new Scanner(System.in);
    Connection con;
    PreparedStatement pstmt;
    ServerSocket serverSocket = null;
    Socket socket = null;
    Map<String, PrintWriter> clientMap;
    RoomManager manager = new RoomManager();
    Room room;
//    Map<String, ArrayList<RoomManager>> roomInfo;


    // 생성자
    public MultiServer7() {
        // 클라이언트의 출력스트림을 저장할 해쉬맵 생성.
        clientMap = new HashMap<String, PrintWriter>();
        // 해쉬맵 동기화 설정.
        Collections.synchronizedMap(clientMap);
//        Collections.synchronizedMap(roomInfo);

    }

    public void init() {

        try {
            serverSocket = new ServerSocket(9999); // 9999포트로 서버소켓 객체생성.
            System.out.println("서버가 시작되었습니다.");


            while (true) {
                socket = serverSocket.accept();
                System.out.println(socket.getInetAddress() + ":" + socket.getPort());

                Thread msr = new MultiServerT(socket); // 쓰렛드 생성.
                msr.start(); // 쓰레드 시동.
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                serverSocket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // 접속자 리스트 보내기
    public void list(PrintWriter out) {
        //  출력스트림을 순차적으로 얻어와서 해당 메시지를 출력한다.
        Iterator<String> it = clientMap.keySet().iterator();
        String msg = "사용자 리스트 [";
        while (it.hasNext()) {
            msg += (String) it.next() + ",";
        }
        msg = msg.substring(0, msg.length() - 1) + "]";
        try {
            out.println(URLEncoder.encode(msg, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
        }
    }

    // 접속된 모든 클라이언트들에게 메시지를 전달.
    public void sendAllMsg(String user, String msg) {

        // 출력스트림을 순차적으로 얻어와서 해당 메시지를 출력한다.
        Iterator it = clientMap.keySet().iterator();

        while (it.hasNext()) {
            try {
                PrintWriter it_out = (PrintWriter) clientMap.get(it.next());
                if (("").equals(user))
                    it_out.println(URLEncoder.encode(msg, "UTF-8"));
                else
                    it_out.println("[" + URLEncoder.encode(user, "UTF-8") + "] "
                            + URLEncoder.encode(msg, "UTF-8"));
            } catch (Exception e) {
                System.out.println("예외:" + e);
            }
        }
    }

    public void sendGroupMsg(User user, Room room, String msg) {
        try {
            for (User uName : room.getUserList()) {
                PrintWriter out = (PrintWriter) clientMap.get(user.getName());
                out.println("[" + user.getName() + "] " + msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("그룹메시지 오류");
        }
    }

    public void whisper(String to, String from, String msg) {
        PrintWriter toOut = (PrintWriter) clientMap.get(to);
        Iterator<String> it = clientMap.keySet().iterator();
        if (to.equals(from)) {
            toOut.println("스스로에게 귓속말을 보낼 수 없습니다.\n");
            return;
        }

        while (it.hasNext()) {
            try {
                String name = it.next();
                if (from.trim().equals(name.trim())) {
                    PrintWriter fromOut = (PrintWriter) clientMap.get(name);
                    fromOut.println(to + "님으로부터의 귓속말: " + msg);
                    return;
                }
            } catch (Exception e) {
                System.out.println("귓속말 에러:" + e);
            }
        }
        toOut.println("접속중인 사용자가 아닙니다.");
    }

    public static void main(String[] args) {
        //서버객체 생성.
        MultiServer7 ms = new MultiServer7();
        ms.init();
    }


/////////////////////////////////////////////////////////////////
// 내부클래스
// 클라이언트로부터 읽어온 메시지를 다른 클라이언트(socket)에 보내는 역활을 하는 메서드

    class MultiServerT extends Thread {
        Socket socket;
        PrintWriter out = null;
        BufferedReader in = null;

        // 생성자
        public MultiServerT(Socket socket) {
            this.socket = socket;
            try {
                out = new PrintWriter(this.socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(
                        this.socket.getInputStream(), "UTF-8"));
            } catch (Exception e) {
                System.out.println("에외:" + e);
            }
        }

        //쓰레드를 사용하기 위해서 run()메서드 재정의
        @Override
        public void run() {

//            String s = "";
            String name = ""; //클라이언트로부터 받은이름을 저장할 변수.
            try {
                signMenu();
                out.println("사용하실 닉네임을 입력하세요");
                name = in.readLine();
                // 현재 객체가 가지고 있는 소켓을 제외하고 다른 소켓(클라이언트)들에게
                // 접속을 알림.
                User user = new User(name);
                user.setName(name);
                name = URLDecoder.decode(name, "UTF-8");
                clientMap.put(name, out); // 해쉬멥에 키를 name으로 출력스트림 객체를 저장.
                sendAllMsg("", user.getName() + "님이 입장하셨습니다.");
                System.out.println("현재 접속자 수는 " + clientMap.size() + "명 입니다.");

                if (manager.roomCount() == 0) {
                    room = new Room(0);
                    manager.createRoom();
                }
                user.joinRoom(room);
                out.println("대기방에 입장하셨습니다.");

                String s = "";
                while (in != null) {
                    s = in.readLine();
                    s = URLDecoder.decode(s, "UTF-8");


                    // 유저들이 하는말
//                    System.out.println(name + " " + s);

//                    if (s.equals("q") || s.equals("Q"))
//                        break;

                    // 명령어 리스트
                    if (s.charAt(0) == '/') {
                        System.out.println("명령어 리스트로 들어감");
                        order(name, s, user);
                    } else
                        sendGroupMsg(user, room, s);
                }

//                System.out.println("Bye....");
            } catch (Exception e) {
                System.out.println("예외: 쓰레드" + e);
                e.printStackTrace();
            } finally {
                // 예외가 발생할 때 퇴장. 해쉬맵에서 해당 데이터 제거.
                // 보통 종료하거나 나가면 java.net.SocketException: 예외발생
                clientMap.remove(name);
                sendAllMsg("", name + "님이 퇴장하셨습니다.");
                System.out.println("현재 접속자 수는 " + clientMap.size() + "명 입니다.");

                try {
                    in.close();
                    out.close();

                    socket.close();
                } catch (Exception e) {
                    System.out.println(name + "퇴장");
                    e.printStackTrace();
                }
            }
        }

        public void order(String name, String s, User user) {
            String command = "";

            // 명령어 미입력
            if (s.length() == 1) {
                out.println("\n명령어를 입력하세요");
                out.println("명령어 목록 : /help\n");
                return;
            }
            // 명령어만 짤라내기
            if (s.contains(" ")) {
                command = s.substring(s.indexOf("/") + 1, s.indexOf(" "));
            } else {
                command = s.substring(s.indexOf("/") + 1);
            }

            switch (command) {
                // 명령어 리스트
                case "help":
                    out.println("======================명령어 목록======================");
                    out.println("/list : 접속중인 유저 목록");
                    out.println("/to 아이디 할말 : 고정귓말");
                    out.println("/w 아이디 할말 : 귓속말");
                    out.println("/menu : 메뉴로 돌아가기");
                    out.println("/quit : 프로그램 종료");
                    break;
                // 접속자 리스트
                case "list":
                    list(out);
                    break;
                case "w":
                    // 귓속말
                    int begin = s.indexOf(" ") + 1;
                    int end = s.indexOf(" ", begin);
                    if (end != -1) {
                        String id = s.substring(begin, end);
                        String msg = s.substring(end + 1);
                        whisper(name, id, msg);
                    }
                    break;
                // 메뉴로 돌아가기
                case "menu":
                    inMenu(user);
            }
            // 메뉴로 돌아가기

        }


        public void add() {
            while (true) {
                try {
                    out.println("===========회원가입===========");
                    out.println("ID : ");
                    String id = in.readLine();
                    out.println("PassWord : ");
                    String pass = in.readLine();
                    String sql = "insert into IdList values(?, ?)";
                    pstmt = con.prepareStatement(sql);
                    pstmt.setString(1, id);
                    pstmt.setString(2, pass);
                    pstmt.executeUpdate();

                    out.println("가입완료");
                    return;

                } catch (SQLIntegrityConstraintViolationException sqle) {
                    if (sqle.getErrorCode() == 1400) {
                        out.println("비밀번호를 입력하세요\n");
                        continue;
                    } else if (sqle.getErrorCode() == 1) {
                        out.println("중복된 아이디 입니다.\n");
                        continue;
                    }
                } catch (SQLException e) {
                    System.out.println("접속오류");
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public boolean login() {
            while (true) {
                try {
                    out.println("===========로그인===========");
                    out.println("ID : ");
                    String id = in.readLine();
                    out.println("PassWord : ");
                    String pass = in.readLine();

                    String sql = "select * from IdList where id = ? and pass = ?";
                    pstmt = con.prepareStatement(sql);
                    pstmt.setString(1, id);
                    pstmt.setString(2, pass);
                    ResultSet rs = pstmt.executeQuery();

                    if (!rs.next()) {
                        out.println("아이디 혹은 패스워드를 잘못 입력하셨습니다.\n");
                        return false;
                    } else if (clientMap.containsKey(id)) {
                        out.println("이미 접속중인 아이디입니다.");
                        return false;
                    }
                } catch (SQLException | IOException e) {
                    System.out.println("로그인에러");
                    break;
                }
                out.println("로그인 완료");
                return true;
            }
            return false;
        }

        public void signMenu() {
            try {
                while (true) {
                    out.println("\t<Menu>\n1.로그인\n2.회원가입\n");
                    String choice = in.readLine();
                    if (("1").equals(choice)) {
                        if (login()) {
                            break;
                        }
                    } else if (("2").equals(choice)) {
                        add();
                    } else {
                        out.println("잘못입력하셨습니다.");
                    }
                }
            } catch (SocketException ex) {
                System.out.println("접속종료");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        public void inMenu(User user) {

            Room room = new Room(user);
            try {
                while (true) {
                    out.println("\n\t<Menu>\n1.대기방 참여\n2.채팅방 목록보기");
                    out.println("3.채팅방 개설\n4.채팅방 참여");
                    String inSelect = in.readLine();
                    switch (inSelect) {
                        case "1":
                            out.println("대기방으로 입장합니다.\n");
                            return;
                        case "2":
                            if (RoomManager.roomCount() == 0) {
                                out.println("현재 개설된 방이 없습니다.");
                            } else {
                                manager.roomNameList(out);
                            }
                            break;
                        case "3":
                            out.println("방 이름을 입력하세요");
                            String roomName = in.readLine();
                            out.println("최대 정원을 입력하세요.");
                            int maxNumber = in.read();
                            room.setMaxNumber(maxNumber);
                            in.skip(2);
                            System.out.println(maxNumber);
                            out.println("비밀번호를 설정하시겠습니까?<Y / N>");
                            String passWordSelect = in.readLine();
                            switch (passWordSelect) {
                                case "Y":
                                case "y":
                                    out.println("비밀번호를 입력하세요");
                                    String passWord = in.readLine();
                                    manager.createRoom(user, roomName, passWord, out, maxNumber);
                                    out.println(roomName + "에 입장합니다.");
                                    roomMenu(user, room);
                                    break;
                                case "N":
                                case "n":
                                    manager.createRoom(user, roomName, out, maxNumber);
                                    out.println(roomName + "에 입장합니다.");
                                    roomMenu(user, room);
                                    break;
                                default:
                                    out.println("잘못 입력하셨습니다.");
                            }
                            break;
                        case "4":
                            break;
                        default:
                            out.println("잘못된 명령입니다. 다시 입력하세요.\n");
                            continue;
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("인메뉴 오류");
            }
        }

        public void roomMenu(User user, Room room) {
            try {
                String s = "";
                while (in != null) {
                    s = in.readLine();
                    s = URLDecoder.decode(s, "UTF-8");

                    // 명령어 리스트
                    if (s.charAt(0) == '/') {
                        System.out.println("명령어 리스트로 들어감");
                        order(user.getName(), s, user);
                    } else
                        sendGroupMsg(user, room, s);
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("roomMenu 오류");
            }
        }

    }

}

