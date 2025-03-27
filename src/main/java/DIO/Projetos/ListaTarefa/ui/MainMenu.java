package DIO.Projetos.ListaTarefa.ui;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import DIO.Projetos.ListaTarefa.persistence.entity.BoardColumnEntity;
import DIO.Projetos.ListaTarefa.persistence.entity.BoardColumnKindEnum;
import DIO.Projetos.ListaTarefa.persistence.entity.BoardEntity;
import DIO.Projetos.ListaTarefa.service.BoardQueryService;
import DIO.Projetos.ListaTarefa.service.BoardService;

import static DIO.Projetos.ListaTarefa.persistence.config.ConnectionConfig.getConnection;
import static DIO.Projetos.ListaTarefa.persistence.entity.BoardColumnKindEnum.CANCEL;
import static DIO.Projetos.ListaTarefa.persistence.entity.BoardColumnKindEnum.FINAL;
import static DIO.Projetos.ListaTarefa.persistence.entity.BoardColumnKindEnum.INITIAL;
import static DIO.Projetos.ListaTarefa.persistence.entity.BoardColumnKindEnum.PENDING;

public class MainMenu{
    
    private final Scanner scanner = new Scanner(System.in);

    public void execute() throws SQLException{
        System.out.println("Bem vindo ao sistema de gerenciamento de tarefas");
        var option = -1;
        while (true) {
            System.out.println("1 - Criar um novo board");
            System.out.println("2 - Selecionar um board existente");
            System.out.println("3 - Excluir um board");
            System.out.println("4 - Sair");
            option = scanner.nextInt();
            switch (option) {

                case 1 -> createBoard();
                case 2 -> selectBoard();
                case 3 -> deleteBoard();
                case 4 -> System.exit(0);
                default -> System.out.println("Opção inválida");
            }
        }
    }

    private void createBoard() throws SQLException{
        var entity = new BoardEntity();
        System.out.println("Digite o nome do board");
        entity.setName(scanner.next());
        
        System.out.println("seu board terá mais boards além das 3 padroes? se sim infome quantas, se não informe 0");
        var additinalColumns = scanner.nextInt();

        List<BoardColumnEntity> columns = new ArrayList<>();

        System.out.println("informe o nome da coluna inicial do board"); 
        var initialColumnName = scanner.next();
        var initialColumn = createColumn(initialColumnName, INITIAL, 0);
        columns.add(initialColumn);

        for(int i = 0 ; i < additinalColumns; i++){
            System.out.println("informe o nome da coluna pendente do board");
            var pendingColumnName = scanner.next();
            var pendingColumn = createColumn(pendingColumnName, PENDING, i + 1);
            columns.add(pendingColumn);
        }

        System.out.println("informe o nome da coluna final");
        var finalColumnName = scanner.next();
        var finalColumn = createColumn(finalColumnName, FINAL, additinalColumns + 1);
        columns.add(finalColumn);

        System.out.println("informe o nome da coluna de cancelamento do board");
        var cancelColumnName = scanner.next();
        var cancelColumn = createColumn(cancelColumnName, CANCEL, additinalColumns + 2);
        columns.add(cancelColumn);

        entity.setBoardColumns(columns);

        try(var connection = getConnection()){
            var service = new BoardService(connection);
            service.insert(entity);
        }
    }

    private void selectBoard() throws SQLException{
        System.out.println("informe o id do board que deseja selecionar");
        var id = scanner.nextLong();
        try(var connection = getConnection()){
            var queryService = new BoardQueryService(connection);
            var optional = queryService.findById(id);
            optional.ifPresentOrElse(
                b -> new BoardMenu(b).execute(), 
                () -> System.out.printf("O board %s não foi encontrado \n", id)
                );
        }
    }

    private void deleteBoard() throws SQLException{
        System.out.println("Digite o id do board que deseja excluir");
        var id = scanner.nextLong();
        try(var connection = getConnection()){
            var service = new BoardService(connection);
            if(service.delete(id)){
                System.out.printf("O board %s foi excluido \n", id);
            }else{
                System.out.printf("O board %s não foi encontrado \n", id);
            }
        }
    }

    private BoardColumnEntity createColumn(final String name, final BoardColumnKindEnum kind, final int order) {
        var boardColumn = new BoardColumnEntity();
        boardColumn.setName(name);
        boardColumn.setKind(kind);
        boardColumn.setOrder(order);
        return boardColumn;
    }
}
