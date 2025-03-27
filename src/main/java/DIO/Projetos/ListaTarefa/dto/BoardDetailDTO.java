package DIO.Projetos.ListaTarefa.dto;

import java.util.List;

public record BoardDetailDTO(long id, String name, List<BoardColumnDTO> columns) {
    
}
