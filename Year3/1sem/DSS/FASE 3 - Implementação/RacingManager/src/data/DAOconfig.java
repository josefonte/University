package data;
public class DAOconfig {
    public static final String USERNAME = "admin";                       // Actualizar
    public static final String PASSWORD = "admin";                       // Actualizar
    private static final String DATABASE = "racing";          // Actualizar
    private static final String DRIVER = "jdbc:mariadb";        // Usar para MariaDB
    //private static final String DRIVER = "jdbc:mysql";        // Usar para MySQL
    public static final String URL = DRIVER+"://localhost:3306/"+DATABASE;
}
