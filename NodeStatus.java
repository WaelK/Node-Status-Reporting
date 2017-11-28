public enum NodeStatus {

    /**
     * List of the types of Node Status
     */
    ALIVE("ALIVE"), DEAD("DEAD"), UNKNOWN("UNKNOWN");

    /**
     * name: String equivalent of the NodeStatus
     */
    String name;

    /**
     * NodeStatus constructor
     * assigns a value to name
     * @param name
     */
    NodeStatus(String name) {
        this.name = name;
    }

    /**
     * Returns the string corresponding to the notification type
     * @return name
     */
    @Override
    public String toString() {
        return name;
    }
}
