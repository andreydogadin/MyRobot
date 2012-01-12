package robot.media;

import java.util.Date;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: andrey.dogadin
 * Date: 8/16/11
 * Time: 1:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class Memory {
    private HashMap longTermMemory;
    private HashMap<String, MemoryItem> currentMemory;

    public Memory() {
        longTermMemory = new HashMap();
        currentMemory = new HashMap<String, MemoryItem>();
    }

    public void putCurrent(String key, Object value, int secsToForget) {
        this.currentMemory.put(key, new MemoryItem(value, new Date(), secsToForget));
        System.out.println(key + " " + value.toString());
    }

    public MemoryItem getCurrent(String key) {
        if (this.currentMemory.containsKey(key))
            return (MemoryItem) this.currentMemory.get(key);
        else return null;
    }

    public void forgetCurrent(String key) {
        if (this.currentMemory.containsKey(key))
            this.currentMemory.remove(key);
        System.out.println(key + " was forgotten ");
    }

    public void forgetOldItems()
    {
        for (String key: currentMemory.keySet())
            if (currentMemory.get(key).timeToForget.before(new Date()))
                forgetCurrent(key);

    }

    public HashMap<String, MemoryItem> getCurrentMemory() {
        return currentMemory;
    }

    public class MemoryItem {
        private Object value;
        private Date date;
        private Date timeToForget;

        MemoryItem(Object value, Date date, int secsToForget) {
            this.value = value;
            this.date = date;
            this.timeToForget = new Date(this.date.getTime()+ 1000*secsToForget);
        }

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }

        public Date getTimeToForget() {
            return timeToForget;
        }
    }
}
