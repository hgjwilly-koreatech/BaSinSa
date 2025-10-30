package Account;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class AccountManager {
    private static List<Member> members = new ArrayList<Member>();
    final static AccountManager instance = new AccountManager();

    private AccountManager() {
        //임시 계정들

        try {
            FileOutputStream fileOutputStream = new FileOutputStream("members.txt");
            FileInputStream fileInputStream = new FileInputStream("members.txt");
        } catch (FileNotFoundException e) {
            File file = new File("members.txt");
        }

    }

    public static Member login(String ID, String password) {
        for (Member user : members) {
            if (user.isEqualAccount(ID, password)) {
                return user;
            }
        }
        return null;
    }

    public static NormalMember registerNormalMember(String userID, String name, String phoneNumber, String password) {
        for (Member member  : members) {
            if (member.getID().equals(userID)) {
                return null;
            }
        }

        NormalMember newMember = new newMember(userID, name, phoneNumber, password);
        members.add(newMember);
        return newMember;
    }

    public static boolean removeAccount(String userID) {
        for (Person user : members) {
            if (user.getID().equals(userID)) {
                if(user.adminPermission) return false;
                members.remove(user);
                return true;
            }
        }
        return false;
    }
}
