import java.io.PrintWriter;
import java.net.Socket;

public class User {
    // 유저가 가져야할 정보들은 ?
    // 유저 이름, 유저 상태 이정도 ?
    // 유저 상태는 뭘로하지 ?????

    private Room room;
    private String name;
    Socket socket;
    private int rNumb;

    public User() {
    }

    public User(String name) {
        this.name = name;
    }


    public void joinRoom(Room room) {
        room.joinUser(this);
        this.rNumb = room.getRoomNumber();
        this.room = room;
    }

    public void exitRoom(Room room) {
        this.room = null;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getrNumb() {
        return rNumb;
    }

    public void setrNumb(int rNumb) {
        this.rNumb = rNumb;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }
}
