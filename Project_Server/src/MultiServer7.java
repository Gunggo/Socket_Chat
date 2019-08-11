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


    public void whisper(String to, String from, String msg) {
        PrintWriter toOut = (PrintWriter) clientMap.get(to);
        Iterator<String> it = clientMap.keySet().iterator();

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
        toOut.println("존재하지 않는 사용자입니다.");
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
                name = signMenu();
                name = URLDecoder.decode(name, "UTF-8");
                // 현재 객체가 가지고 있는 소켓을 제외하고 다른 소켓(클라이언트)들에게
                sendAllMsg("", name + "님이 입장하셨습니다.");
                // 접속을 알림.
                clientMap.put(name, out); // 해쉬멥에 키를 name으로 출력스트림 객체를 저장.
                System.out.println("현재 접속자 수는 " + clientMap.size() + "명 입니다.");

//                맵에 잘 put됬는지 key값 확인용
                Set<String> keys = clientMap.keySet();
                keys.forEach(key -> System.out.println(key));

                inMenu();

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
                        order(name, s);
                    } else
                        sendAllMsg(name, s);

                }

//                System.out.println("Bye....");
            } catch (Exception e) {
                System.out.println("예외: 쓰레드" + e);
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

        public void order(String user, String s) {

            String command = s.substring(s.indexOf("/") + 1, s.indexOf(" "));

            // 명령어 리스트
            if ("help".equals(command)) {
                out.println("======================명령어 목록======================");
                out.println("              /list : 접속중인 유저 목록");
                out.println("              /to 아이디 할말 : 고정귓말");
                out.println("              /w 아이디 할말 : 귓속말");
                out.println("              /exit : 메뉴로 돌아가기");
                out.println("              /quit : 프로그램 종료");
            }
            // 명령어 입력을 안했을때
            if (s.length() == 1) {
                out.println("명령어를 입력하세요");
                out.println("명령어 목록 : /help");
            }
            // 접속자 리스트
            if ("list".equals(command)) {
                list(out);
            }
            // 귓속말
            System.out.println("/w 아이디 할말");
            if ("w".equals(command)) {
                int begin = s.indexOf(" ") + 1;
                int end = s.indexOf(" ", begin);
                if (end != -1) {
                    String id = s.substring(begin, end);
                    String msg = s.substring(end + 1);
                    whisper(user, id, msg);
                }
            }

        }


        public void add() {

            // 회원가입
            // 공백검사는 나중에 ..
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
                signMenu();

            } catch (SQLIntegrityConstraintViolationException sqle) {
                if (sqle.getErrorCode() == 1400) {
                    out.println("비밀번호를 입력하세요\n");
                    add();
                } else if (sqle.getErrorCode() == 1) {
                    out.println("중복된 아이디 입니다.\n");
                    add();
                }
            } catch (SQLException e) {
                System.out.println("접속오류");
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public String logIn() {
            String id = "";
            while (true) {
                try {
                    out.println("===========로그인===========");
                    out.println("회원가입 = add");
                    out.println("ID : ");
                    id = in.readLine();
                    if (id.equals("add") || id.equals("ADD")) {
                        add();
                    }
                    out.println("PassWord : ");
                    String pass = in.readLine();

                    String sql = "select * from IdList where id = ? and pass = ?";
                    pstmt = con.prepareStatement(sql);
                    pstmt.setString(1, id);
                    pstmt.setString(2, pass);
                    ResultSet rs = pstmt.executeQuery();

                    if (!rs.next()) {
                        out.println("아이디 혹은 패스워드를 잘못 입력하셨습니다.\n");
                        continue;
                    } else if (clientMap.containsKey(id)) {
                        out.println("이미 접속중인 아이디입니다.");
                        continue;
                    }
                } catch (SQLException | IOException e) {
                    System.out.println("로그인에러");
                    break;
                }
                out.println("로그인 완료");
                break;
            }
            return id;
        }

        public String signMenu() {
            String name = "";
            try {
                while (true) {
                    out.println("\t<Menu>\n1.LogIn\n2.Join US\n선택");
                    String choice = in.readLine();
                    if (("1").equals(choice)) {
                        name = logIn();
                    } else if (("2").equals(choice)) {
                        add();
                    } else {
                        out.println("잘못입력하셨습니다.");
                        continue;
                    }
                    break;
                }
            } catch (SocketException ex) {
                System.out.println("접속종료");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            return name;
        }

        public void inMenu() {

            try {
                while (true) {
                    out.println("\t<Menu>\n1.대기방 참여\n2.채팅방 목록보기");
                    out.println("3.채팅방 개설\n4.채팅방 참여");
                    String inSelect = in.readLine();
                    switch (inSelect) {
                        case "1":
                            out.println("대기방으로 입장합니다.");
                            return;
                        case "2":

                            break;
                        case "3":

                            break;
                        case "4":

                            break;
                        default:
                            out.println("잘못된 명령입니다. 다시 입력하세요.");
                            continue;
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("인메뉴 오류");
            }
        }


    }

}

