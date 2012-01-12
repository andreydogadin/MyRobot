package commands.datatypes;

/**
 * Created by IntelliJ IDEA.
 * User: Robot
 * Date: 08.01.12
 * Time: 22:53
 * To change this template use File | Settings | File Templates.
 */
public class EMail {
    private String author;
    private String topic;

    public EMail(String author, String topic) {
        this.author = author;
        this.topic = topic;
    }

    public String getAuthor() {
        return author;
    }

    public String getTopic() {
        return topic;
    }
}
