package ch.bbw.m320.hanoi.mapper;

import ch.bbw.m320.hanoi.HanoiBoard;
import ch.bbw.m320.hanoi.api.model.HanoiBoardDto;
import org.springframework.stereotype.Component;

@Component
public class HanoiBoardMapper {

    public HanoiBoardDto toDto(final HanoiBoard board) {
        HanoiBoardDto dto = new HanoiBoardDto();
        dto.setPegA(board.pegA());
        dto.setPegB(board.pegB());
        dto.setPegC(board.pegC());
        return dto;
    }
}
