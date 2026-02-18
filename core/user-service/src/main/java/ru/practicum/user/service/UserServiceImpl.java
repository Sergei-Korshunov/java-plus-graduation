package ru.practicum.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.interactionapi.exception.ConflictException;
import ru.practicum.interactionapi.exception.NotFoundException;
import ru.practicum.interactionapi.user.dto.NewUserRequest;
import ru.practicum.interactionapi.user.dto.UserDto;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;
import ru.practicum.interactionapi.util.PageRequestUtil;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserDto create(NewUserRequest req) {
        if (repository.existsByEmailIgnoreCase(req.getEmail()))
            throw new ConflictException("User with email already exists: " + req.getEmail());
        return userMapper.toDto(repository.save(userMapper.toEntity(req)));
    }

    @Override
    public List<UserDto> get(List<Long> ids, int from, int size) {
        Pageable pr = PageRequestUtil.of(from, size, Sort.by("id").ascending());
        var page = (ids == null || ids.isEmpty())
                ? repository.findAll(pr)
                : repository.findAllByIdIn(ids, pr);
        return page.map(userMapper::toDto).getContent();
    }

    @Override
    @Transactional
    public void delete(long userId) {
        if (!repository.existsById(userId)) throw new NotFoundException("User with id=" + userId + " was not found");
        repository.deleteById(userId);
    }

    @Override
    public UserDto getUserById(Long userId) {
        return userMapper.toDto(findUserById(userId));
    }

    private User findUserById(Long userId) {
        return repository.findById(userId).orElseThrow(() ->
                new NotFoundException("User with id=" + userId + " was not found"));
    }
}