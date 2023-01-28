package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByItemId(long itemId);

    List<Booking> findAllByBookerIdOrderByStartDesc(long userId);

    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(long userId, Status status);

    List<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(long userId, LocalDateTime date);

    List<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(long userId, LocalDateTime date);

    List<Booking> findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(long userId,
                                                                                 LocalDateTime start,
                                                                                 LocalDateTime end);

    @Query("select b from Booking b join b.item i where i.owner.id =?1 order by b.start desc ")
    List<Booking> findAllByOwnerIdAndItem(Long ownerId);

    @Query("select b from Booking b join b.item i where i.owner.id =?1 and b.status =?2 order by b.start desc ")
    List<Booking> findAllByOwnerIdAndItemWithStatus(Long ownerId, Status status);

}
