import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class RoomManager {

    private static List<Room> roomList;
    private static int rNum = 1;

    static {
        roomList = new ArrayList<>();
    }

    public RoomManager() {
    }

    // 최초 공개방
    public Room createRoom() {
        Room room = new Room(0);
        room.setRoomName("WatingRoom");
        room.setRoomNumber(rNum);
        room.setMaxNumber(100);
        rNum++;
        roomList.add(room);
        return room;
    }

    // 공개방만들기 (방 만들때 비번을 안주면)

    public Room createRoom(User user, String roomName, PrintWriter out, int maxNumber) {
        Room room = new Room(user);
        room.setRoomName(roomName);
        room.setRoomNumber(rNum);
        room.setMaxNumber(maxNumber);
        rNum++;
        roomList.add(room);
        return room;
    }

    // 비공개방만들기 (방 만들때 비번 주면)
    public Room createRoom(User user, String roomName, String passWord, PrintWriter out, int maxNumber) {
        Room room = new Room(user);
        room.setRoomName(roomName);
        room.setPassWord(passWord);
        room.setRoomNumber(rNum);
        room.setMaxNumber(maxNumber);
        rNum++;
        roomList.add(room);
        return room;
    }

    // 전달받은 방을 삭제하기

    public static void deleteRoom(Room room) {
        room.close();
        roomList.remove(room);
        System.out.println("채팅방 삭제 완료");
    }
    // 개설된 방의 갯수

    public static int roomCount() {
        return roomList.size();
    }

    public void roomNameList(PrintWriter out) {
        for (Room rName : roomList) {
            out.println(rName.getRoomNumber() + ". " + rName.getRoomName());
        }
    }

    public int getrNum() {
        return rNum;
    }

    public void setrNum(int rNum) {
        this.rNum = rNum;
    }

    public List<Room> getRoomList() {
        return roomList;
    }

    public void setRoomList(List<Room> roomList) {
        RoomManager.roomList = roomList;
    }
}
