package pro.network.adminneyvelimart.user;

public class Users {
    String name;
    String phone;
    String id;
    String wallerAmt;
    String email;

    public Users(String name, String phone, String id, String wallerAmt, String email) {
        this.name = name;
        this.phone = phone;
        this.id = id;
        this.wallerAmt = wallerAmt;
        this.email = email;
    }

    public Users() {

    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getWallerAmt() {
        return wallerAmt;
    }

    public void setWallerAmt(String wallerAmt) {
        this.wallerAmt = wallerAmt;
    }
}
