package ru.practicum.shareit.request.dto;

import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;
import java.util.List;

public interface ItemRequestWithListOfItems {
    Long getId();

    String getDescription();

    LocalDateTime getCreated();

    @Value("#{new ru.practicum.shareit.request.dto.ItemDetailsForRequest(target.iid, target.iname, target.idescription, target.iavailable, target.irequestId)}")
    List<ItemDetailsForRequest> getItems();


}
