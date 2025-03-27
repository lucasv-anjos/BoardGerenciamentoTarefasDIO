package DIO.Projetos.ListaTarefa.persistence.converter;

import lombok.NoArgsConstructor;
import static lombok.AccessLevel.PRIVATE;
import static java.time.ZoneOffset.UTC;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import static java.util.Objects.nonNull;

@NoArgsConstructor(access = PRIVATE)
public class OffsetDateTimeConverter {
    
    public static OffsetDateTime toOffsetDateTime(final Timestamp value) {
        return nonNull(value) ? OffsetDateTime.ofInstant(value.toInstant(), UTC) : null;
    }

    public static Timestamp toTimestamp(final OffsetDateTime value) {
        return nonNull(value) ? Timestamp.valueOf(value.atZoneSameInstant(UTC).toLocalDateTime()) : null;
        }
}
