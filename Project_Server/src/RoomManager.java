import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class RoomManager {

    static List<Room> roomList;
    private int rNum = 1;

    public RoomManager() {
        roomList = new ArrayList<Room>();
    }

    // 공개방만들기 (방 만들때 비번을 안주면)

    public Room createRoom(User user, String roomName, PrintWriter out, int maxNumber) {
        Room room = new Room(user);
        room.setRoomName(roomName);
        room.setRoomNumber(rNum);
        room.setMaxNumber(maxNumber);
        rNum++;
        roomList.add(room);
        out.println("공개방 개설 완료");
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
        out.println("비공개방 개설 완료");
        return room;
    }

    // 전달받은 방을 삭제하기

    public static void deleteRoom(Room room) {
        roomList.remove(room);
        System.out.println("채팅방 삭제 완료");
    }
    // 개설된 방의 갯수

    public static int roomCount() {

        return roomList.size();
    }

    public void roomNameList(PrintWriter out) {
        for (Room rName : roomList) {
            out.print(rName.getRoomNumber()+ ". " + rName.getRoomName() + "\n");
        }
    }

    public int getrNum() {
        return rNum;
    }

    public void setrNum(int rNum) {
        this.rNum = rNum;
    }
}
