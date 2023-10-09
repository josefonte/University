import java.util.Objects;

public class User{
    String user;
    String pass;

    public User(String user, String pass){
        this.user=user;
        this.pass=pass;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user1 = (User) o;
        return user.equals(user1.user) && pass.equals(user1.pass);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, pass);
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }
}
