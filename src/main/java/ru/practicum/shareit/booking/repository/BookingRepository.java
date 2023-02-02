package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByItemId(long itemId);

    List<Booking> findAllByBookerIdOrderByStartDesc(long userId);

    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(long userId, Status status);

    List<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(long userId, LocalDateTime date);

    List<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(long userId, LocalDateTime date);

    List<Booking> findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(long userId,
                                                                                 LocalDateTime start,
                                                                                 LocalDateTime end);

    @Query("select b from Booking b join b.item i where i.owner.id = :ownerId order by b.start desc ")
    List<Booking> findAllByOwnerIdAndItem(@Param("ownerId") Long ownerId);

    @Query("select b from Booking b join b.item i where i.owner.id = :ownerId and b.status = :status order by b.start desc ")
    List<Booking> findAllByOwnerIdAndItemWithStatus(@Param("ownerId") Long ownerId, @Param("status") Status status);

    @Query("select b from Booking b join b.item i where b.id = :bookingId and (i.owner.id = :userId or b.booker.id = :userId) ")
    Optional<Booking> findBookingByBookerIdOrOwnerId(@Param("bookingId") Long bookingId, @Param("userId") Long userId);

    @Query("select b from Booking b join b.item i where i.owner.id = :userId ")
    List<Booking> findAllByOwnerId(@Param("userId") Long userId);

}
