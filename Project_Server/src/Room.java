import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Room {
    private List<User> userList;
    private User roomOwner;
    private String roomName;
    private String passWord = "";
    private int roomNumber;
    private int maxNumber;
    Socket socket;
    BufferedReader in;
    PrintWriter out;
    // 깡통
    public Room(int roomNumber) {
        this.roomNumber = roomNumber;
        userList = new ArrayList<>();
    }

    public Room(User user) {
        userList = new ArrayList<>();
        user.joinRoom(this);
        userList.add(user);
        this.roomOwner = user;
    }

    public void joinUser(User user) {
        user.joinRoom(this);
        userList.add(user);
    }

    public void exitRoom(User user) {
        user.exitRoom(this);
        userList.remove(user);

        // 아무도 없으면 방 제거
        if (userList.size() < 1) {
            RoomManager.deleteRoom(this);
            return;
        }
        // 마지막 남은 사람이 방장
        if (userList.size() > 2) {
            this.roomOwner = userList.get(0);
            return;
        }
    }

    public void close() {
        for (User user : userList) {
            user.exitRoom(this);
        }
        this.userList.clear();
        this.userList = null;
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
    public int getUserListSize() {
        return userList.size();
    }

    public List<User> getUserList() {
        return userList;
    }

    public void getUserName(PrintWriter out) {
        for (User userName : userList) {
            out.println(userName.getName());
        }
    }

    public User getUser(User user) {
        int uNum = userList.indexOf(user);

        if (uNum > 0) {
            return userList.get(uNum);
        } else {
            return null;
        }
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
