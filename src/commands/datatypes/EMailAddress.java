package commands.datatypes;

import utils.RobotConsts;

/**
 * Created by IntelliJ IDEA.
 * User: Robot
 * Date: 07.01.12
 * Time: 19:10
 * To change this template use File | Settings | File Templates.
 */
public class EMailAddress {
    private String name;
    private String email;
    private String password;
    private String pop3Host = "pop.mail.ru";
    private Integer pop3Port = 110;

    public EMailAddress(String name, String email, String password) {
        this.name = name;
        this.password = password;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public Integer getPop3Port() {
        return pop3Port;
    }

    public String getPop3Host() {
        return pop3Host;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }
    
    public static EMailAddress getEMailAddressByName(String name){
        for(EMailAddress e : RobotConsts.eMailAddresses){
            if (e.getName().equalsIgnoreCase(name))
                return e;
        }
        return null;
    }
}
