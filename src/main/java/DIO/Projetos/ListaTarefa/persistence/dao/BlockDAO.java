package DIO.Projetos.ListaTarefa.persistence.dao;

import static DIO.Projetos.ListaTarefa.persistence.converter.OffsetDateTimeConverter.toTimestamp;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.OffsetDateTime;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class BlockDAO {
    
    private final Connection connection;

    public void block(final Long cardId, final String reason) throws SQLException{
        var sql = "INSERT INTO BLOCKS (blocked_at, blocked_reason, card_id) VALUES (?, ?, ?);";
        try(var statement = connection.prepareStatement(sql)) {
            var i = 1;
            statement.setTimestamp(i ++ , toTimestamp(OffsetDateTime.now()));
            statement.setString(i ++, reason);
            statement.setLong(i , cardId);
            statement.executeUpdate();

        } 
    }

    public void unblock(final Long cardId, final String reason) throws SQLException{
        var sql = "UPDATE BLOCKS SET unblocked_at = ?, unblocked_reason = ? WHERE card_id = ? AND unblocked_reason IS NULL;";
        try(var statement = connection.prepareStatement(sql)) {
            var i = 1;
            statement.setTimestamp(i ++ , toTimestamp(OffsetDateTime.now()));
            statement.setString(i ++, reason);
            statement.setLong(i , cardId);
            statement.executeUpdate();

        } 
    }
}
