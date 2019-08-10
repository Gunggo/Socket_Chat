import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.URLEncoder;

public class ChatWin extends JFrame {

    private static final long serialVersionUID = 1L;
    JTextField tf;
    JPanel p;
    TextHandler handler = null;

    Socket socket;
    PrintWriter out = null;
    String name;

    ChatWin(Socket socket) {

        this.setTitle("Chat Window");
        this.setSize(600, 100);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        p = new JPanel();
        p.setLayout(new FlowLayout());

        tf = new JTextField(40);
        p.add(tf);

        this.setContentPane(p);
        this.setVisible(true);

        handler = new TextHandler();
        tf.addActionListener(handler);

        //-------------------------------------------------------------------

        this.socket = socket;
        try {
            out = new PrintWriter(this.socket.getOutputStream(), true);
            //서버에 결과값 전송
        } catch(Exception e) {
            System.out.println("예외S3:"+e);
        }

    }

    //Inner Class TextHandler
    class TextHandler implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            String msg = tf.getText();
            if ("".equals(msg)) return;

            if (msg.equals("q") || msg.equals("Q") ) {
                try {
                    out.close();
                    socket.close();
                } catch (IOException e1) {
                }
            } else {
                try {
                    out.println(URLEncoder.encode(msg,"UTF-8"));
                } catch (UnsupportedEncodingException ex) {}
            }

            tf.setText("");
        }
    }

}




