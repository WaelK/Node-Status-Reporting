import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class NodeReport {

    /**
     * nodes: A HashMap used to store all the nodes. K will be the name (String) and V will be the node object (Node)
     * notification: An ArrayList used to store the notifications
     */
    private HashMap<String, Node> nodes = new HashMap<>();
    private ArrayList<Notification> notifications = new ArrayList<Notification>();

    /**
     * This method will be used to start the process by collecting the notifications,
     *      processing the notifications and printing the status of the nodes
     * @param filename the name of the file with the notifications
     */
    public void start(String filename) {
        getNotifications(filename);

        processNotifications();

        printStatusReport();
    }

    /**
     * This method will create a scanner to scan the file given by filename
     *      the method will then make a Notification object from each line
     *      storing the Notification object in notifications
     * @param filename name of the file with the notications
     */
    private void getNotifications(String filename) {

        try {
            Scanner scan = new Scanner(new File(filename));

            while (scan.hasNextLine()) {

                String[] line = scan.nextLine().split(" ");
                long timeRecieved = Long.parseLong(line[0].trim());
                long timeSent = Long.parseLong(line[1].trim());
                String sender = line[2].trim();
                String type = line[3].trim();
                NotificationType notifType = NotificationType.fromString(type);

                if(notifType == null) throw new RuntimeException("Notification type not recognised");

                Notification notif = new Notification(timeRecieved, timeSent, sender, notifType);

                if (notifType != NotificationType.HELLO) {
                    notif.setSubject(line[4].trim());
                }

                notifications.add(notif);
            }
        } catch (FileNotFoundException e) {
            System.out.print("File \"" + filename + "\" was not found.");
        }
    }

    /**
     * This method sorts the collection by the timestamp when the node generated the message
     *      and processes the notifications, handling different message type in different ways
     *      and cases where notifications may not be synchronised
     */
    private void processNotifications() {

        Collections.sort(notifications);

        for (int i = 0; i < notifications.size(); i++) {
            Notification notif = notifications.get(i);

            //checks if theres a clash, updates a node with status UNKNOWN
            if(checkIfClashes(i)) {
                updateNodes(notif.getSender(), notif, NodeStatus.UNKNOWN);
                continue;
            }

            //The sender will always be alive
            updateNodes(notif.getSender(), notif, NodeStatus.ALIVE);

            //Handles updating the status of the subject
            switch (notif.getMessageType()) {
                case FOUND:
                    updateNodes(notif.getSubject(), notif, NodeStatus.ALIVE);
                    break;
                case LOST:
                    updateNodes(notif.getSubject(), notif, NodeStatus.DEAD);
                    break;
            }
        }
    }

    /**
     * This method checks if the notifications neighbouring the notification being tested are within 50ms
     *      of the notification being tested and will handle the clash by calling checkClash(...)
     * @param index position of the current notification being tested
     * @return true if the notification leads to ambiguity
     */
    private boolean checkIfClashes(int index) {

        //check notifications before until range of 50ms reached or the start of the array reached
        int j= index-1;
        while(j >= 0 && Math.abs(notifications.get(index).getNotifGenerated()-notifications.get(j).getNotifGenerated())<=50) {
            if(checkClash(notifications.get(index), notifications.get(j))) return true;
            j--;
        }

        //check notifications after until range of 50ms reached or the end of the array reached
        j= index+1;
        while((j < notifications.size()) && Math.abs(notifications.get(index).getNotifGenerated()-notifications.get(j).getNotifGenerated())<=50) {
            if(checkClash(notifications.get(index), notifications.get(j))) return true;
            j++;
        }

        return false;
    }

    /**
     * This method checks whether the two notifications leads to ambiguity
     *      this occurs where one notification has a node as the subject of a LOST notification
     *      and the same node is the present in a HELLO or FOUND notificatin or the sender of a LOST notification
     * @param currentNotif the notification being tested
     * @param compareNotif the notification that might lead to ambiguity
     * @return true if the notifications leads to ambiguity, false otherwise
     */
    private boolean checkClash(Notification currentNotif, Notification compareNotif) {

        if((currentNotif.getMessageType() == NotificationType.HELLO)) {
            //check if the first message type HELLO is sent by the node implying the node is alive
            // and the second message is type LOST where the node is the subject, implying the node is dead
            return (currentNotif.getSender().equals(compareNotif.getSubject())) && (compareNotif.getMessageType() == NotificationType.LOST);
        }

        if((compareNotif.getMessageType() == NotificationType.HELLO)) {
            //check if the second message type HELLO is sent by the node implying the node is alive
            // and the first message is type LOST where the node is the subject, implying the node is dead
            return (currentNotif.getSubject().equals(compareNotif.getSender())) && (currentNotif.getMessageType() == NotificationType.LOST);
        }

        //Check if one message is type FOUND implying the node is alive
        // and the other is LOST where the node is the subject, implying if the node is dead
        boolean foundAndLostSubjects = (currentNotif.getSender().equals(compareNotif.getSubject())) && ((currentNotif.getMessageType() == NotificationType.FOUND) && (compareNotif.getMessageType() == NotificationType.LOST));
        foundAndLostSubjects = foundAndLostSubjects || ((currentNotif.getSubject().equals(compareNotif.getSender())) && ((currentNotif.getMessageType() == NotificationType.LOST) && (compareNotif.getMessageType() == NotificationType.FOUND)));

        if(foundAndLostSubjects) return true;

        //Check if one message is type LOST where the node is the sender implying the node is alive
        // and the other is LOST where the node is the subject, implying if the node is dead
        boolean lostSenderAndLostSubjects = (currentNotif.getSender().equals(compareNotif.getSubject())) && ((currentNotif.getMessageType() == NotificationType.LOST) && (compareNotif.getMessageType() == NotificationType.LOST));
        lostSenderAndLostSubjects = lostSenderAndLostSubjects || (currentNotif.getSubject().equals(compareNotif.getSender())) && ((currentNotif.getMessageType() == NotificationType.LOST) && (compareNotif.getMessageType() == NotificationType.LOST));

        if(lostSenderAndLostSubjects) return true;

        return false;
    }

    /**
     * This method checks if the HashMap nodes contains a node with the name targetName, updating the name in this case
     *      otherwise, creating a new Node and storing it into the HashMap
     * @param targetName name of the Node
     * @param notification most recent notification that indicates the status of the Node
     * @param status new status the Node
     */
    private void updateNodes(String targetName, Notification notification, NodeStatus status) {

        if(!nodes.containsKey(targetName)) {
            nodes.put(targetName, new Node(targetName, notification, status));
        } else {
            Node node = nodes.get(targetName);
            if(node.getLatestNotif().getNotifGenerated()<notification.getNotifGenerated()) {
                node.setStatus(status);
                node.setLatestNotif(notification);
            }
        }
    }

    /**
     * This method passes through the nodes, printing out the required information
     */
    private void printStatusReport() {

        for(Map.Entry<String, Node> node : nodes.entrySet()) {
            System.out.println(node.getValue());
        }
    }

    /**
     * This creates a new instance of NodeReport and starts the process
     * @param param contains the filename
     */
    public static void main(String[] param) {

        if(param.length == 0) {
            System.out.println("There must be at least one argument. \nThe target file name must be the first argument");
            System.exit(0);
        }

        String filename = param[0];
        (new NodeReport()).start(filename);

    }
}
