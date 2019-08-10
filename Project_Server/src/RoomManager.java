import java.util.ArrayList;

public class RoomManager {
    // 방 정보에는 뭐가있을까 ?
    // 방 번호, 방 이름, 방장, 비번, 정원, 현인원, 게스트정보,
    String user;
    String roomName;
    String passWord;
    ArrayList<String> guestList;

    // 공개방
    public RoomManager(String user, String roomName) {
        this.user = user;
        this.roomName = roomName;
    }

    // 비공개방
    public RoomManager(String user, String roomName, String passWord) {
        this.user = user;
        this.roomName = roomName;
        this.passWord = passWord;
    }
}
