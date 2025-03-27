package DIO.Projetos.ListaTarefa.persistence.config;

import java.sql.DriverManager;
import java.sql.SQLException;

import java.sql.Connection;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ConnectionConfig {
    
    public static Connection getConnection() throws SQLException {
        var url = "jdbc:mysql://localhost:3306/board"; // porta padrão
        var user = ""; //coloque o usuario do banco de dados criado
        var password = ""; // coloque a senha do banco de dados
        var connection = DriverManager.getConnection(url, user, password);
        connection.setAutoCommit(false);
        return connection;
    }
    
}
