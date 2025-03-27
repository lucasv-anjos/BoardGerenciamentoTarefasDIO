package DIO.Projetos.ListaTarefa.service;

import java.sql.Connection;
import java.sql.SQLDataException;
import java.sql.SQLException;

import DIO.Projetos.ListaTarefa.persistence.dao.BoardColumnDAO;
import DIO.Projetos.ListaTarefa.persistence.dao.BoardDAO;
import DIO.Projetos.ListaTarefa.persistence.entity.BoardEntity;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class BoardService {
    
    private final Connection connection;

    public BoardEntity insert(final BoardEntity entity) throws SQLException{
        var dao = new BoardDAO(connection);
        var BoardColumnDAO = new BoardColumnDAO(connection);
        try 
        {
            dao.insert(entity);
            var columns = entity.getBoardColumns().stream().map(c -> {
                c.setBoard(entity);
                return c;
            }).toList();
            for (var column : columns){
                BoardColumnDAO.insert(column);
            }
            connection.commit();
        }catch (SQLException e) {
            connection.rollback();
            throw e;
        }

        return entity;
    }

    public boolean delete(final Long id) throws SQLException{
        var dao = new BoardDAO(connection);
        try{
            if (!dao.exists(id)){  
                return false;
            }

            dao.delete(id);
            connection.commit();
            return true;

        } catch(SQLException e){
            connection.rollback();
            throw e;
        }
    }
}
