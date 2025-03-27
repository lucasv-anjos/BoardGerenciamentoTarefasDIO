package DIO.Projetos.ListaTarefa.ui;

import java.sql.SQLException;
import java.util.Scanner;

import DIO.Projetos.ListaTarefa.dto.BoardColumnInfoDTO;
import DIO.Projetos.ListaTarefa.persistence.entity.BoardColumnEntity;
import DIO.Projetos.ListaTarefa.persistence.entity.BoardEntity;
import DIO.Projetos.ListaTarefa.persistence.entity.CardEntity;
import DIO.Projetos.ListaTarefa.service.BoardColumnQueryService;
import DIO.Projetos.ListaTarefa.service.BoardQueryService;
import DIO.Projetos.ListaTarefa.service.CardQueryService;
import DIO.Projetos.ListaTarefa.service.CardService;
import lombok.AllArgsConstructor;
import static DIO.Projetos.ListaTarefa.persistence.config.ConnectionConfig.getConnection;
import static DIO.Projetos.ListaTarefa.persistence.entity.BoardColumnKindEnum.INITIAL;

@AllArgsConstructor
public class BoardMenu {
    private final BoardEntity entity;

    private final Scanner scanner = new Scanner(System.in).useDelimiter("/n");   

    public void execute() {
        try{
            System.out.printf("Board Menu %s, selecione a operação desejada\n", entity.getId());
            var option = -1;
            while (option != 9) {
                System.out.println("1 - Criar um novo card");
                System.out.println("2 - mover um card");
                System.out.println("3 - bloquear um card");
                System.out.println("4 - desbloquear um card");
                System.out.println("5 - cancelar um card");
                System.out.println("6 - visualizar board");
                System.out.println("7 - ver colunas com cards");
                System.out.println("8 - ver card");
                System.out.println("9 - voltar para o menu anterior");
                System.out.println("10 - sair");
                option = scanner.nextInt();
                switch (option) {

                    case 1 -> createCard();
                    case 2 -> moveCardToNextColumn();
                    case 3 -> blockCard();
                    case 4 -> unblockCard();
                    case 5 -> cancelCard();
                    case 6 -> showBoard();
                    case 7 -> showColumn();
                    case 8 -> showCard();
                    case 9 -> System.out.println("voltando para o menu anterior");
                    case 10 -> System.exit(0);
                    default -> System.out.println("Opção inválida");
                }
            }
        } catch (SQLException ex){
            ex.printStackTrace();
            System.exit(0);
        }
    }

    private void createCard() throws SQLException{
        var card = new CardEntity();
        System.out.println("informe o titulo do card");
        card.setTitle(scanner.next());
        System.out.println("informe a descrição do card");
        card.setDescription(scanner.next());
        card.setBoardColumn(entity.getInitialColumn());
        try(var connection = getConnection()){
            new CardService(connection).insert(card);
        }
    }

    private void moveCardToNextColumn() throws SQLException{

        System.out.println("informe o id do card que deseja mover para proxima coluna");
        var cardId = scanner.nextLong();

        var boardColumnsInfo = entity.getBoardColumns().stream()
            .map(bc -> new BoardColumnInfoDTO(bc.getId(), bc.getOrder(), bc.getKind()))
            .toList();

        try(var connection = getConnection()){
            new CardService(connection).moveToNextColumn(cardId, boardColumnsInfo);
        } catch (RuntimeException ex){
            System.out.println(ex.getMessage());
        }

    }

    private void blockCard() throws SQLException{
        
        System.out.println("informe o id do card");
        var cardId = scanner.nextLong();

        System.out.println("informe o motivo do bloqueio do card");
        var reason = scanner.next();

        var boardColumnsInfo = entity.getBoardColumns().stream()
        .map(bc -> new BoardColumnInfoDTO(bc.getId(), bc.getOrder(), bc.getKind()))
        .toList();

        try(var connection = getConnection()){
            new CardService(connection).block(cardId, reason, boardColumnsInfo);
        }catch (RuntimeException ex){
            System.out.println(ex.getMessage());
        }
    }

    private void unblockCard() throws SQLException{

        System.out.println("informe o id do card para desbloqueio");
        var cardId = scanner.nextLong();

        System.out.println("informe o motivo do desbloqueio do card");
        var reason = scanner.next();

        try(var connection = getConnection()){
            new CardService(connection).unblock(cardId, reason);
        }catch (RuntimeException ex){
            System.out.println(ex.getMessage());
        }
    }

    private void cancelCard() throws SQLException{

        System.out.println("informe o id do card que deseja mover para coluna de cancelamento");
        var cardId = scanner.nextLong();

        var cancelColumn = entity.getCancelColumn();


        var boardColumnsInfo = entity.getBoardColumns().stream()
            .map(bc -> new BoardColumnInfoDTO(bc.getId(), bc.getOrder(), bc.getKind()))
            .toList();

        try(var connection = getConnection()){
            new CardService(connection).cancel(cardId, cancelColumn.getId(), boardColumnsInfo);
        } catch (RuntimeException ex){
            System.out.println(ex.getMessage());
        }
    }

    private void showBoard() throws SQLException{

        try(var connection = getConnection()){
            var optional = new BoardQueryService(connection).showBoardDetails(entity.getId());
            optional.ifPresent(b -> {
                System.out.printf("Board [%s,%s] \n", b.id(), b.name());
                b.columns().forEach(c -> {
                    System.out.printf("coluna: %s\n tipo: %s \n possui %s cards \n" , c.name(), c.kind(), c.cardsAmount());
                });
            });
        }
    }

    private void showColumn() throws SQLException{
        var columnIds = entity.getBoardColumns().stream().map(BoardColumnEntity::getId).toList();
        var selectedColumn = -1L;
        while (!columnIds.contains(selectedColumn)){
            System.out.printf("escolha uma coluna do board %s\n", entity.getName());
            entity.getBoardColumns().forEach(c -> System.out.printf("%s - %s [%s]\n", c.getId(), c.getName(), c.getKind()));
            selectedColumn = scanner.nextLong();
        }
        try(var connection = getConnection()){
            var column = new BoardColumnQueryService(connection).findById(selectedColumn);
            column.ifPresent(co -> {
                System.out.printf("coluna %s tipo %s \n",co.getName(), co.getKind());
                co.getCards().forEach(ca -> System.out.printf("card %s - %s \n descrição:%s\n", ca.getId(), ca.getTitle(), ca.getDescription()));
            });
        }

    }

    private void showCard() throws SQLException{
        System.out.println("informe o id do card que deseja visualizar");
        var selectedCardId = scanner.nextLong();
        try(var connection = getConnection()){
            new CardQueryService(connection).findById(selectedCardId)
            .ifPresentOrElse(c -> {
                System.out.printf("Card %s - %s\n", c.id(), c.title());
                System.out.printf(" Descrição: %s\n", c.description());
                System.out.println(c.blocked() ? 
                    "está bloqueado, motivo:" + c.blockReason() : 
                    "não está bloqueado");
                System.out.printf(" ja foi bloqueado %s vezes \n", c.blocksAmount());
                System.out.printf(" está na coluna %s - %s\n", c.columnId(), c.columnName());
            },
            () -> System.out.printf("não existe um card com id %s \n", selectedCardId));
        }
    }
}
