package cau.handson.business.controller;

import cau.handson.business.dto.DataResponseDto;
import cau.handson.business.dto.RoomDto;
import cau.handson.business.service.RoomService;
import cau.handson.business.service.RoomUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RequiredArgsConstructor
@RestController
@RequestMapping("/room")
@Slf4j
public class RoomController {

    private final RoomService roomService;
    private final RoomUserService roomUserService;

    @PostMapping
    public DataResponseDto<Object> insert(
        @RequestBody RoomDto roomDto
    ) {
        return DataResponseDto.of(roomService.insert(roomDto));
    }

    @GetMapping
    public DataResponseDto<Object> getAll() {
        return DataResponseDto.of(roomService.getAll());
    }

    @GetMapping(path = "/{id}")
    public DataResponseDto<Object> get(
        @PathVariable String id
    ) {
        return DataResponseDto.of(roomService.get(id));
    }

    @PatchMapping(path = "/{id}")
    public DataResponseDto<Object> update(
        @PathVariable String id,
        @RequestBody RoomDto roomDto
    ) {
        return DataResponseDto.of(roomService.update(id, roomDto));
    }

    @DeleteMapping(path = "/{id}")
    public DataResponseDto<Object> delete(
        @PathVariable String id
    ) {
        return DataResponseDto.of(roomService.delete(id));
    }


    @PostMapping(path = "/{id}/me")
    public DataResponseDto<Object> getIn(
        @PathVariable String id
    ) {
        return DataResponseDto.of(roomUserService.insertMe(id));
    }

    @DeleteMapping(path = "/{id}/me")
    public DataResponseDto<Object> getOut(
    ) {
        return DataResponseDto.of(roomUserService.deleteMe());
    }
}