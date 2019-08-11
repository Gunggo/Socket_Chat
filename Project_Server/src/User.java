public class User {
    // 유저가 가져야할 정보들은 ?
    // 유저 이름, 유저 상태 이정도 ?
    // 유저 상태는 뭘로하지 ?????

    Room room;
    String name;

    public User(String name) {
        this.name = name;
    }

    public void JoinRoom(Room room) {
        room.JoinRoom(this);
        this.room = room;
    }
}
