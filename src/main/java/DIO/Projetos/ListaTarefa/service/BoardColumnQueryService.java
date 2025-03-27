package DIO.Projetos.ListaTarefa.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

import DIO.Projetos.ListaTarefa.persistence.dao.BoardColumnDAO;
import DIO.Projetos.ListaTarefa.persistence.entity.BoardColumnEntity;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class BoardColumnQueryService {

        private final Connection connection;

        public Optional<BoardColumnEntity> findById(final Long id) throws SQLException {
            var dao = new BoardColumnDAO(connection);
            return dao.findById(id);
        }
}
