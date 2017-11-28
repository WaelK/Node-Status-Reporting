public enum NotificationType {

    /**
     * Notification Types
     */
    HELLO("HELLO"), LOST("LOST"), FOUND("FOUND");

    /**
     * name: String equivalent of the NotificationType
     */
    String name;

    /**
     * NotificationType constructor
     * assigns a value to name
     * @param name
     */
    NotificationType(String name) {
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

    /**
     * Searches through the notification types, returning the type corresponding to str
     * @param str name of the NotificationType
     * @return NotificationType
     */
    public static NotificationType fromString(String str) {
        for (NotificationType type : NotificationType.values()) {
            if(str.equals(type.name)) {
                return type;
            }
        }
        return null;
    }
}
