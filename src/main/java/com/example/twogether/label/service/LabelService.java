package com.example.twogether.label.service;


import com.example.twogether.board.entity.Board;
import com.example.twogether.board.repository.BoardRepository;
import com.example.twogether.common.error.CustomErrorCode;
import com.example.twogether.common.exception.CustomException;
import com.example.twogether.label.dto.LabelRequestDto;
import com.example.twogether.label.dto.LabelResponseDto;
import com.example.twogether.label.entity.Label;
import com.example.twogether.label.repository.LabelRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LabelService {
    private final LabelRepository labelRepository;
    private final BoardRepository boardRepository;

    @Transactional(readOnly = true)
    public List<LabelResponseDto> getLabels(Long boardId) {
        findBoard(boardId);

        List<Label> labels = labelRepository.findAllByBoard_Id(boardId);
        return labels.stream().map(LabelResponseDto::of).toList();
    }

    @Transactional
    public LabelResponseDto createLabel(LabelRequestDto requestDto, Long boardId) {
        Board board = findBoard(boardId);
        findDuplicateLabel(requestDto.getTitle(), boardId);

        Label label = requestDto.toEntity(board);
        labelRepository.save(label);

        return LabelResponseDto.of(label);
    }

    @Transactional
    public LabelResponseDto editLabel(Long labelId, LabelRequestDto requestDto) {
        Label label = findLabel(labelId);
        findDuplicateLabel(requestDto.getTitle(), label.getBoard().getId());

        if(requestDto.getTitle() != null) label.editTitle(requestDto.getTitle());
        if(requestDto.getColor() != null) label.editColor(requestDto.getColor());

        return LabelResponseDto.of(label);
    }

    @Transactional
    public void deleteLabel(Long labelId) {
        Label label = findLabel(labelId);
        labelRepository.delete(label);
    }

    private Board findBoard(Long boardId) {
        return boardRepository.findById(boardId).orElseThrow(() ->
            new CustomException(CustomErrorCode.BOARD_NOT_FOUND));
    }

    private void findDuplicateLabel(String title, Long boardId) {
        long cnt = labelRepository.findAllByTitle(title).stream().filter(label ->
            label.getBoard().getId().equals(boardId)
        ).count();
        if (cnt > 0) throw new CustomException(CustomErrorCode.LABEL_ALREADY_EXISTS);
    }

    private Label findLabel(Long labelId) {
        return labelRepository.findById(labelId).orElseThrow(() ->
            new CustomException(CustomErrorCode.LABEL_NOT_FOUND));
    }
}