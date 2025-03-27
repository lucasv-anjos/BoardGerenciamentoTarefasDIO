package DIO.Projetos.ListaTarefa.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

import DIO.Projetos.ListaTarefa.dto.BoardDetailDTO;
import DIO.Projetos.ListaTarefa.persistence.dao.BoardColumnDAO;
import DIO.Projetos.ListaTarefa.persistence.dao.BoardDAO;
import DIO.Projetos.ListaTarefa.persistence.entity.BoardEntity;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class BoardQueryService {

    private final Connection connection;

    public Optional<BoardEntity> findById(final Long id) throws SQLException{
        var dao = new BoardDAO(connection);
        var boardColumnDAO = new BoardColumnDAO(connection);
        var optional = dao.findById(id);
        if(optional.isPresent()){
            var entity = optional.get();
            entity.setBoardColumns(boardColumnDAO.findByBoardId(entity.getId()));
            return Optional.of(entity);
        }
        return Optional.empty();
    }
    
    public Optional<BoardDetailDTO> showBoardDetails(final long id) throws SQLException{
        var dao = new BoardDAO(connection);
        var boardColumnDAO = new BoardColumnDAO(connection);
        var optional = dao.findById(id);
        if(optional.isPresent()){
            var entity = optional.get();
            var columns = boardColumnDAO.findByBoardIdWithDetails(entity.getId());
            var dto = new BoardDetailDTO(entity.getId(), entity.getName(), columns);
            return Optional.of(dto);
        }
        return Optional.empty();
    }
}
