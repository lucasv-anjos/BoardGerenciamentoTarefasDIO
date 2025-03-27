package DIO.Projetos.ListaTarefa;

import java.sql.SQLException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import DIO.Projetos.ListaTarefa.persistence.config.ConnectionConfig;
import DIO.Projetos.ListaTarefa.persistence.migration.MigrationStrategy;
import DIO.Projetos.ListaTarefa.ui.MainMenu;

@SpringBootApplication
public class ListaTarefaApplication {

	public static void main(String[] args) throws SQLException {
		try(var connection = ConnectionConfig.getConnection()){
			new MigrationStrategy(connection).executeMigration();
		}
		new MainMenu().execute();
		SpringApplication.run(ListaTarefaApplication.class, args);
	}

}
