package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.dto.ItemView;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByOwnerIdOrderByIdAsc(Long userId);

    @Query(value = "SELECT i.id as id, i.name as name, i.description as description, i.available as available, " +
            "last.id as lastBookingId, " +
            "last.user_id as lastBookingBookerId," +
            "next.id as nextBookingId, next.user_id as nextBookingBookerId " +
            "from items as i " +
            "left join booking as last on i.id = last.item_id " +
            "left join booking as next on i.id = next.item_id " +
            "where i.user_id = :userId and (last.id is null or next.id is null) " +
            "or (i.user_id = :userId and last.id != next.id and last.end_date < next.start_date and next.start_date > :time)  " +
            "order by i.id ",
            nativeQuery = true)
    List<ItemView> findByItemIdAndUserId(@Param("userId") Long userId, @Param("time") LocalDateTime time);
}
