package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentShort;
import ru.practicum.shareit.item.model.Comment;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class CommentMapper {
    public static CommentShort toCommentShort(Comment comment) {
        return new CommentShort(comment.getId(),
                comment.getText(),
                comment.getAuthor().getName(),
                comment.getCreated());
    }

    public static Comment toComment(CommentDto commentDto) {
        Comment comment = new Comment();
        comment.setText(commentDto.getText());
        comment.setCreated(LocalDateTime.now());
        return comment;
    }

    public static List<CommentShort> toCommentShortList(Collection<Comment> comments) {
        return comments.stream()
                .map(CommentMapper::toCommentShort)
                .collect(Collectors.toList());
    }
}
