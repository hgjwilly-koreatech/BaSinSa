package Account;

public class Member {
    public String id;
    public String pw;
    public String name;
    public String phoneNumber;
    public Member(String id, String pw, String name) {
        this.id = id;
        this.pw = pw;
    }

    public  String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public boolean isEqualAccount(String id, String pw) {
        return this.id.equals(id);
    }
}
