package DIO.Projetos.ListaTarefa.dto;

import DIO.Projetos.ListaTarefa.persistence.entity.BoardColumnKindEnum;

public record BoardColumnInfoDTO(
    Long id,
    int order,
    BoardColumnKindEnum kind
    ) {
    
}
