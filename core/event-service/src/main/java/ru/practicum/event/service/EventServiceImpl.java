package ru.practicum.event.service;

import com.querydsl.core.BooleanBuilder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.category.service.CategoryService;
import ru.practicum.client.RecommendationsClient;
import ru.practicum.client.UserActionClient;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.mapper.LocationMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.Location;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.event.repository.LocationRepository;
import ru.practicum.ewm.stats.proto.ActionTypeProto;
import ru.practicum.ewm.stats.proto.RecommendedEventProto;
import ru.practicum.interactionapi.event.event.dto.*;
import ru.practicum.interactionapi.event.event.status.SortForParamPublicEvent;
import ru.practicum.interactionapi.event.event.status.StateEvent;
import ru.practicum.interactionapi.event.event.status.StateForUpdateEvent;
import ru.practicum.interactionapi.exception.ConflictException;
import ru.practicum.interactionapi.exception.NotFoundException;
import ru.practicum.interactionapi.request.client.PrivateRequestClient;
import ru.practicum.interactionapi.request.model.RequestStatus;
import ru.practicum.interactionapi.user.client.UserClient;
import ru.practicum.interactionapi.user.dto.UserDto;
import ru.practicum.interactionapi.util.PageRequestUtil;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final CategoryService categoryService;
    private final CategoryMapper categoryMapper;
    private final LocationRepository locationRepository;
    private final LocationMapper locationMapper;
    private final UserClient userClient;
    private final PrivateRequestClient privateRequestClient;
    private final UserActionClient userActionClient;
    private final RecommendationsClient recommendationsClient;

    @Transactional
    @Override
    public EventFullDto createEvent(Long userId, NewEventDto newEventDto) {
        UserDto user = userClient.getUserById(userId);
        Category category = categoryMapper.toCategory(categoryService.getCategory(newEventDto.getCategory()));
        Location location = locationRepository.save(locationMapper.toLocation(newEventDto.getLocation()));
        Event event = eventRepository.save(eventMapper.toEvent(newEventDto, user.getId(), category, location));
        return eventMapper.toEventFullDto(event);
    }

    @Override
    public List<EventShortDto> findEventByUserId(Long userId, int from, int size) {
        userClient.getUserById(userId);
        Pageable pageable = PageRequestUtil.of(from, size, Sort.by("id").ascending());
        return eventRepository.findEventByUserId(userId, pageable).getContent().stream()
                .map(eventMapper::toEventShortDto)
                .toList();
    }

    @Override
    public EventFullDto findEventByIdAndEventId(Long userId, Long eventId) {
        userClient.getUserById(userId);
        Event event = findEventWithOutDto(userId, eventId);

        return eventMapper.toEventFullDto(event);
    }

    @Transactional
    @Override
    public EventFullDto updateEvent(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest) {
        userClient.getUserById(userId);
        Event event = findEventWithOutDto(userId, eventId);
        //проверка статуса
        if (event.getState().equals(StateEvent.PUBLISHED)) {
            throw new ConflictException("Данный Event невозможно изменить, поскольку он уже опубликован");
        } else if (updateEventUserRequest.getStateAction() != null &&
                (event.getState().equals(StateEvent.CANCELED) &&
                        updateEventUserRequest.getStateAction().equals(StateForUpdateEvent.SEND_TO_REVIEW))) {
            event.setState(StateEvent.PENDING);
        } else if (updateEventUserRequest.getStateAction() != null &&
                (event.getState().equals(StateEvent.PENDING) &&
                        updateEventUserRequest.getStateAction().equals(StateForUpdateEvent.CANCEL_REVIEW))) {
            event.setState(StateEvent.CANCELED);
        }
        //проверка даты
        if (updateEventUserRequest.getEventDate() != null && updateEventUserRequest.getEventDate()
                .isAfter(LocalDateTime.now().plusHours(2))) {
            event.setEventDate(updateEventUserRequest.getEventDate());
        }
        //проверка категории и локации
        Event updateEventWithCategoryAndLocation = updateCategoryAndLocation(updateEventUserRequest, event);
        eventMapper.toUpdateEvent(updateEventUserRequest, updateEventWithCategoryAndLocation);

        return eventMapper.toEventFullDto(eventRepository.save(updateEventWithCategoryAndLocation));
    }

    @Override
    public EventFullDto getEventById(Long eventId) {
        return eventMapper.toEventFullDto(findEventById(eventId));
    }

    private Event findEventById(Long eventId) {
        return eventRepository.findEventById(eventId).orElseThrow(() ->
                new NotFoundException("Event c id - " + eventId + " не найден"));
    }

    @Transactional
    @Override
    public EventFullDto updateEventAdmin(Long eventId, UpdateEventAdminRequestDto updateEventAdminRequestDto) {
        Event event = findEventById(eventId);
        if (updateEventAdminRequestDto.getEventDate() != null) {
            if (!event.getEventDate().isAfter(LocalDateTime.now().plusHours(1))) {
                throw new ConflictException("Дата начала изменяемого события должна быть " +
                        "не ранее чем за час от текущего времени. Текущая дата события: " + event.getEventDate());
            }
        }
        if (!event.getState().equals(StateEvent.PENDING)) {
            throw new ConflictException("Статус у события, которое планируется опубликовать/отклонить, " +
                    "должен быть PENDING. Текущий статус: " + event.getState());
        }
        if (updateEventAdminRequestDto.getStateAction() != null) {
            if (updateEventAdminRequestDto.getStateAction().equals(StateForUpdateEvent.PUBLISH_EVENT)) {
                event.setState(StateEvent.PUBLISHED);
                event.setPublishedOn(LocalDateTime.now());
            }
            if (updateEventAdminRequestDto.getStateAction().equals(StateForUpdateEvent.REJECT_EVENT)) {
                event.setState(StateEvent.CANCELED);
            }
        }
        Event updateEventWithCategoryAndLocation = updateCategoryAndLocation(updateEventAdminRequestDto, event);
        eventMapper.toUpdateEvent(updateEventAdminRequestDto, updateEventWithCategoryAndLocation);

        return eventMapper.toEventFullDto(eventRepository.save(updateEventWithCategoryAndLocation));
    }

    @Transactional
    @Override
    public void updateConfirmedRequest(Long eventId, EventFullDto eventFullDto) {
        Event event = findEventById(eventId);
        if (eventFullDto.getConfirmedRequests() == null || eventFullDto.getConfirmedRequests() < 0) {
            throw new IllegalArgumentException(String.format("Некорректное поле - \"confirmedRequests\", значение: %d",
                    eventFullDto.getConfirmedRequests()));
        }

        event.setConfirmedRequests(eventFullDto.getConfirmedRequests());
        eventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    public List<EventFullDto> findEventByParamsAdmin(EventAdminParamDto eventParamDto) {
        BooleanBuilder booleanBuilder = EventRepository.PredicatesForParamAdmin.build(eventParamDto);

        Pageable pageable = PageRequestUtil.of(eventParamDto.getFrom(),
                eventParamDto.getSize(), Sort.by("id").ascending());

        List<Event> event = eventRepository.findAll(booleanBuilder, pageable).getContent();

        return event.stream().map(eventMapper::toEventFullDto).toList();
    }

    @Override
    public List<EventShortDto> findEventByParamsPublic(EventPublicParamsDto eventPublicParamsDto, HttpServletRequest request) {
        if (eventPublicParamsDto.getRangeEnd() != null && eventPublicParamsDto.getRangeStart() != null) {
            if (eventPublicParamsDto.getRangeEnd().isBefore(eventPublicParamsDto.getRangeStart())) {
                throw new IllegalStateException("Дата RangeEnd не должна быть раньше даты RangeStart. RangeStart:" +
                        eventPublicParamsDto.getRangeStart() + ". RangeEnd:" + eventPublicParamsDto.getRangeEnd());
            }
        }
        BooleanBuilder booleanBuilder = EventRepository.PredicatesForParamPublic.build(eventPublicParamsDto);

        String sort = (eventPublicParamsDto.getSort() != null &&
                eventPublicParamsDto.getSort().equals(SortForParamPublicEvent.EVENT_DATE))
                ? "eventDate"
                : (eventPublicParamsDto.getSort() != null && eventPublicParamsDto.getSort()
                .equals(SortForParamPublicEvent.VIEWS))
                ? "views"
                : "id";

        Pageable pageable = PageRequestUtil.of(eventPublicParamsDto.getFrom(),
                eventPublicParamsDto.getSize(), Sort.by(sort).descending());

        List<Event> event = eventRepository.findAll(booleanBuilder, pageable).getContent();

        return event.stream().map(eventMapper::toEventShortDto).toList();
    }

    @Transactional
    private Event updateCategoryAndLocation(UpdateEventUserRequest updateEventUserRequest, Event event) {
        if (updateEventUserRequest.getCategory() != null) {
            Category category = categoryMapper.toCategory(
                    categoryService.getCategory(updateEventUserRequest.getCategory()));
            event.setCategory(category);
        }
        if (updateEventUserRequest.getLocation() != null) {
            Location location = locationRepository.save(locationMapper.toLocation(updateEventUserRequest.getLocation()));
            event.setLocation(location);
        }
        return event;
    }

    @Override
    public EventFullDto findPublicEventById(long userId, Long eventId) {
        log.info("Найти публичное мероприятие по идентификатору {}, пользователь с id - {}", eventId, userId);
        Event event = findEventById(eventId);
        if (!event.getState().equals(StateEvent.PUBLISHED)) {
            throw new NotFoundException("Событие не доступно. Статус события: " + event.getState());
        }

        userActionClient.collectUserAction(eventId, userId, ActionTypeProto.ACTION_VIEW, Instant.now());

        return eventMapper.toEventFullDto(event);
    }

    @Override
    public List<EventFullDto> getEventsRecommendations(long userId, int maxResults) {
        userClient.getUserById(userId);

        Stream<RecommendedEventProto> recommendationsForUser = recommendationsClient.getRecommendationsForUser(userId, maxResults);
        Map<Long, Double> recommendations = recommendationsForUser.collect(
                Collectors.toMap(RecommendedEventProto::getEventId, RecommendedEventProto::getScore));

        List<Event> events = eventRepository.findAllById(recommendations.keySet());

        return events.stream()
                .map(eventMapper::toEventFullDto)
                .toList();
    }

    @Override
    public void addLike(long userId, Long eventId) {
        if (!privateRequestClient.isUserAttendedEvent(eventId, userId, RequestStatus.CONFIRMED)) {
            throw new ValidationException(String.format("Пользователь с id - %d не участвовал в событии под id - %d.", userId, eventId));
        }

        userActionClient.collectUserAction(eventId, userId, ActionTypeProto.ACTION_LIKE, Instant.now());
    }

    @Override
    public Event findEventWithOutDto(Long userId, Long eventId) {
        return eventRepository.findEventByUserIdAndEventId(eventId, userId).orElseThrow(() ->
                new NotFoundException("Event c id - " + eventId + " не найден у пользователя с id - " + userId));
    }

    @Override
    public List<Event> findEventsByids(List<Long> eventsIds) {
        List<Event> events = eventRepository.findEventsByIds(eventsIds);
        if (events.isEmpty()) {
            throw new NotFoundException("Events c ids - " + eventsIds + " не найдены");
        }

        return events;
    }

    @Transactional
    @Override
    public void saveEventWithRequest(Event event) {
        eventRepository.save(event);
    }
}