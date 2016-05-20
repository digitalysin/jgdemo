import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;
import java.io.*;

class Receiver extends ReceiverAdapter {
  @Override
  public void viewAccepted(View view) {
    System.out.println("got view : " + view);
  }

  @Override
  public void receive(Message message) {
    System.out.println("Got message from : " + message.getSrc() +  " - " + message.getObject());
  }
}

public class App {
  JChannel channel;

  public void start() {
    try {
      channel = new JChannel();
      channel.connect("Cluster");
      channel.setReceiver(new Receiver());
      eventLoop();
      channel.close();
    }
    catch(Exception e) {
      System.out.println("Failed to connect to cluster : " + e.getMessage());
    }
  }

  public void eventLoop() throws Exception {
    BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

    String userName = System.getProperty("user.name", "n/a");

    while(true) {
      System.out.print(">");
      System.out.flush();
      String line = in.readLine().toLowerCase();

      if(line.startsWith("quit") || line.startsWith("exit")) {
        break;
      }

      line = "[" + userName + "]" + " " + line;

      channel.send(new Message(null, null, line));
    }
  }

  public static void main(String [] args) {
    App app = new App();
    app.start();
  }
}
