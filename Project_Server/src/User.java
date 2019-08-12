import java.io.PrintWriter;

public class User {
    // 유저가 가져야할 정보들은 ?
    // 유저 이름, 유저 상태 이정도 ?
    // 유저 상태는 뭘로하지 ?????

    private Room room;
    private String name;


    public User(String name) {
        this.name = name;
    }


    public void joinRoom(Room room) {
        room.joinRoom(this);
        this.room = room;
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
}
