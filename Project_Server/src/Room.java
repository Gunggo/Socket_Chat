import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class Room {
    private List<User> userList;
    private User roomOwner;
    private String roomName;
    private String passWord = "";
    private int roomNumber;
    private int maxNumber;

    public Room(User user) {
        userList = new ArrayList<User>();
        userList.add(user);
        this.roomOwner = user;
    }

    public boolean joinRoom(User user) {
        if (passWord.equals("")) {
            if (userList.size() <= maxNumber) {
                userList.add(user);
                return true;
            }
        }
        return false;
    }

    public void exitRoom(User user) {
        userList.remove(user);
        // 아무도 없으면 방 제거
        if (userList.size() < 1) {
            RoomManager.deleteRoom(this);
            return;
        }
        // 마지막 남은새끼가 방장
        if (userList.size() > 2) {
            this.roomOwner = userList.get(0);
            return;
        }
    }

    // 방장 겟터
    public User getRoomOwner() {
        return roomOwner;
    }

    // 방장 셋터
    public void setRoomOwner(User user) {
        this.roomOwner = user;
    }

    // 방제 겟터
    public String getRoomName() {
        return roomName;
    }

    // 방제 셋터
    public void setRoomName(String name) {
        this.roomName = name;
    }

    // 리스트 겟터
    public int getUserList() {
        return userList.size();
    }

    // 리스트 셋터
    public void setUserList(List<User> userList) {
        this.userList = userList;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(int roomNumber) {
        this.roomNumber = roomNumber;
    }

    public int getMaxNumber() {
        return maxNumber;
    }

    public void setMaxNumber(int maxNumber) {
        this.maxNumber = maxNumber;
    }
}
