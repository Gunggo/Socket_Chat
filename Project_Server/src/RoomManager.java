import java.util.ArrayList;
import java.util.List;

public class RoomManager {

    static List<Room> roomList;

    public RoomManager() {
        roomList = new ArrayList<Room>();
    }

    // 방만들기
    public Room CreateRoom(User user) {
        Room room = new Room(user);
        roomList.add(room);
        System.out.println("채팅방 개설 완료");
        return room;
    }
    // 전달받은 방을 삭제하기
    public static void DeleteRoom(Room room) {
        roomList.remove(room);
        System.out.println("채팅방 삭제 완료");
    }
    // 개설된 방의 갯수
    public static int RoomCount() {
        return roomList.size();
    }
}
