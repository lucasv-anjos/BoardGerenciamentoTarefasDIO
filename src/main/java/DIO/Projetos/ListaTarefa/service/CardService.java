package DIO.Projetos.ListaTarefa.service;

import lombok.AllArgsConstructor;

import static DIO.Projetos.ListaTarefa.persistence.entity.BoardColumnKindEnum.CANCEL;
import static DIO.Projetos.ListaTarefa.persistence.entity.BoardColumnKindEnum.FINAL;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import DIO.Projetos.ListaTarefa.dto.BoardColumnInfoDTO;
import DIO.Projetos.ListaTarefa.dto.CardDetailsDTO;
import DIO.Projetos.ListaTarefa.exception.CardBlockedException;
import DIO.Projetos.ListaTarefa.exception.CardFinishedException;
import DIO.Projetos.ListaTarefa.exception.EntityNotFoundException;
import DIO.Projetos.ListaTarefa.persistence.dao.BlockDAO;
import DIO.Projetos.ListaTarefa.persistence.dao.CardDAO;
import DIO.Projetos.ListaTarefa.persistence.entity.CardEntity;

@AllArgsConstructor
public class CardService {

    private final Connection connection;

    public CardEntity insert(final CardEntity entity) throws SQLException{

        try {
            var dao = new CardDAO(connection);
            dao.insert(entity);
            connection.commit();
            return entity;
        } catch (SQLException ex) {
            connection.rollback();
            throw ex;
        }
    }

    public void moveToNextColumn(final Long cardId, final List<BoardColumnInfoDTO> BoardColumnsInfo) throws SQLException {
        try {
            var dao = new CardDAO(connection);
            var optional = dao.findById(cardId);
            var dto = optional.orElseThrow( 
                () -> new EntityNotFoundException("O card com id %s nao foi encontrado".formatted(cardId))
            );
            if (dto.blocked()) {
                var message = "O card %s esta bloqueado, e necessario desbloquea-lo para move-lo".formatted(cardId);
                throw new CardBlockedException(message);
            }
            var currentColumn = BoardColumnsInfo.stream()
            .filter(bc -> bc.id()
                .equals(dto.columnId()))
                .findFirst()
                .orElseThrow( () -> new IllegalStateException("O card informado pertence a outro board"));

            if (currentColumn.kind().equals(FINAL)) {
                throw new CardFinishedException("O card ja foi finalizado");
            }
            var nextColumn = BoardColumnsInfo.stream()
                .filter(bc -> bc.order() == currentColumn.order() + 1)
                .findFirst().orElseThrow(() -> new IllegalStateException("O card esta cancelado"));
            dao.moveToColumn(nextColumn.id(), cardId);
            connection.commit();
        } catch (SQLException ex) {
            connection.rollback();
            throw ex;
        }

    }

    public void cancel(final Long cardId, final Long cancelColumnId ,final List<BoardColumnInfoDTO> BoardColumnsInfo) throws SQLException {

        try {
            var dao = new CardDAO(connection);
            var optional = dao.findById(cardId);
            var dto = optional.orElseThrow( 
                () -> new EntityNotFoundException("O card com id %s nao foi encontrado".formatted(cardId))
            );
            if (dto.blocked()) {
                var message = "O card %s esta bloqueado, e necessario desbloquea-lo para move-lo".formatted(cardId);
                throw new CardBlockedException(message);
            }
            var currentColumn = BoardColumnsInfo.stream()
            .filter(bc -> bc.id()
                .equals(dto.columnId()))
                .findFirst()
                .orElseThrow( () -> new IllegalStateException("O card informado pertence a outro board"));
            
            if (currentColumn.kind().equals(FINAL)) {
                throw new CardFinishedException("O card ja foi finalizado");
            }
            BoardColumnsInfo.stream()
                .filter(bc -> bc.order() == currentColumn.order() + 1)
                .findFirst().orElseThrow(() -> new IllegalStateException("O card esta cancelado"));

            dao.moveToColumn(cancelColumnId, cardId);
            connection.commit();

        } catch (SQLException ex) {
            connection.rollback();
            throw ex;
        }
    }

    public void block(final Long id, final String reason, final List<BoardColumnInfoDTO> BoardColumnsInfo) throws SQLException {
        
        try {

            var dao = new CardDAO(connection);
            var optional = dao.findById(id);
            var dto = optional.orElseThrow( 
                () -> new EntityNotFoundException("O card com id %s nao foi encontrado".formatted(id))
            );
            if (dto.blocked()) {
                var message = "O card %s já esta bloqueado".formatted(id);
                throw new CardBlockedException(message);
            }
            var currentColumn = BoardColumnsInfo.stream()
                .filter(bc -> bc.id().equals(dto.columnId()))
                .findFirst()
                .orElseThrow();

            if (currentColumn.kind().equals(FINAL) || currentColumn.kind().equals(CANCEL)) {
                var message = "O card está em uma coluna do tipo %s e não pode ser bloqueado"
                    .formatted(currentColumn.kind());
                throw new IllegalStateException(message);
            }
            var blockDAO = new BlockDAO(connection);
            blockDAO.block(id, reason);
            connection.commit();

        } catch (SQLException ex) {
            connection.rollback();
            throw ex;
        }
    
    }

    public void unblock(final Long id, final String reason) throws SQLException {
        
        try {
            var dao = new CardDAO(connection);
            var optional = dao.findById(id);
            var dto = optional.orElseThrow( 
                () -> new EntityNotFoundException("O card com id %s nao foi encontrado".formatted(id))
            );
            if (!dto.blocked()) {
                var message = "O card %s não esta bloqueado".formatted(id);
                throw new CardBlockedException(message);
            }
            var blockDAO = new BlockDAO(connection);
            blockDAO.unblock(id, reason);
            connection.commit();
        } catch (SQLException ex) {
            connection.rollback();
            throw ex;
        }
    
    }
}
