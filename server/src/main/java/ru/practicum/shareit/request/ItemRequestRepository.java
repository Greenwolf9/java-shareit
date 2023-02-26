package ru.practicum.shareit.request;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.request.dto.ItemRequestWithListOfItems;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    @Query(value = "SELECT ir.id as id, " +
            "ir.description as description, " +
            "ir.created as created, " +
            "i.id as iid, " +
            "i.name as iname, " +
            "i.description as idescription, " +
            "i.available as iavailable, " +
            "i.request_id as irequestId " +
            "from item_request as ir left join items i on i.request_id = ir.id where ir.requestor_id = :requestorId ", nativeQuery = true)
    List<ItemRequestWithListOfItems> findAllByRequestorIdOrderByCreatedDesc(@Param("requestorId") Long requestorId);

    @Query(value = "SELECT ir.id as id, " +
            "ir.description as description, " +
            "ir.created as created, " +
            "i.id as iid, " +
            "i.name as iname, " +
            "i.description as idescription, " +
            "i.available as iavailable, " +
            "i.request_id as irequestId " +
            "from item_request as ir left join items i on i.request_id = ir.id where ir.requestor_id != :requestorId ", nativeQuery = true)
    Page<ItemRequestWithListOfItems> findAllRequestsByRequestorIdIsNot(@Param("requestorId") Long requestorId, Pageable pageable);

}
