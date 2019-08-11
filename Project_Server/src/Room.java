import java.util.ArrayList;
import java.util.List;

public class Room {
    private List<User> userList;
    private User roomOwner;
    private String roomName;

    public Room(User user) {
        userList = new ArrayList<User>();
        userList.add(user);
        this.roomOwner = user;
    }

    public void JoinRoom(User user) {
        userList.add(user);
    }

    public void ExitRoom(User user) {
        userList.remove(user);
        // 아무도 없으면 방 제거
        if (userList.size() < 1) {
            RoomManager.DeleteRoom(this);
            return;
        }
        // 마지막 남은새끼가 방장
        if (userList.size() > 2) {
            this.roomOwner = userList.get(0);
            return;
        }
    }

    // 방장 겟터
    public User GetRoomOwner() {
        return roomOwner;
    }
    // 방장 셋터
    public void SetRoomOwner(User user) {
        this.roomOwner = user;
    }
    // 방제 겟터
    public String GetRoomName() {
        return roomName;
    }
    // 방제 셋터
    public void SetRoomName(String name) {
        this.roomName = name;
    }
    // 리스트 겟터
    public int GetUserList() {
        return userList.size();
    }
    // 리스트 셋터
    public void setUserList(List<User> userList) {
        this.userList = userList;
    }



}
