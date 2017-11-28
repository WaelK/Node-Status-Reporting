public class Node {

    /**
     * name: holds the name of the Node
     * latestNotif: holds the notification that indicates the status of the node
     * status: the status of the node
     */
    private String name;
    private Notification latestNotif;
    private NodeStatus status;

    /**
     * Node Constructor
     * Assigns the values to the instance variables
     * @param name
     * @param latestNotifIndex
     * @param status
     */
    public Node(String name, Notification latestNotifIndex, NodeStatus status) {
        this.name = name;
        this.latestNotif = latestNotifIndex;
        this.status = status;
    }

    /**
     * @return the last notification that indicates the status of the node
     */
    public Notification getLatestNotif() {
        return latestNotif;
    }

    /**
     * Assigns/updates the latest notification that indicates the status of the node
     * @param latestNotifIndex
     */
    public void setLatestNotif(Notification latestNotifIndex) {
        this.latestNotif = latestNotifIndex;
    }

    /**
     * Assigns/updates the status of the node
     * @param status
     */
    public void setStatus(NodeStatus status) {
        this.status = status;
    }

    /**
     * Makes a string containing all the information needed for the node
     * @return the nodes information
     */
    @Override
    public String toString() {
        String str = name + "  " + status + "  " + latestNotif.getNotifReceived() + "  " + latestNotif.getSender() + " " + latestNotif.getMessageType();

        if(latestNotif.getMessageType() != NotificationType.HELLO) {
            str = str + "  " + latestNotif.getSubject();
        }

        return str;
    }
}
