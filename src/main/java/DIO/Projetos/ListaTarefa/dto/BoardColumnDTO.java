package DIO.Projetos.ListaTarefa.dto;

import DIO.Projetos.ListaTarefa.persistence.entity.BoardColumnKindEnum;

public record BoardColumnDTO(Long id, String name, BoardColumnKindEnum kind, int cardsAmount) {
    
    
}
