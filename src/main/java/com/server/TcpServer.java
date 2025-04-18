package com.server;

import com.kurs.dto.*;
import com.kurs.dto.AdminDTOs.*;
import com.server.Entities.Booking;
import com.server.Entities.Tour;
import com.server.Entities.User;
import com.server.Service.*;
import com.server.Service.AdminService.AdminBookingService;
import com.server.Service.AdminService.AdminService;
import com.server.search.TourSearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

@Component
public class TcpServer {
    private static final int PORT = 11000;

    private final LoginService loginService;
    private final RegistrationService registrationService;
    private final ProfileService profileService;
    private final TourService tourService;
    private final BookingService bookingService;
    private final AdminService adminService;
    private final AdminBookingService adminBookingService;

    @Autowired
    public TcpServer(LoginService loginService, RegistrationService registrationService, ProfileService profileService, TourService tourService, BookingService bookingService, AdminService adminService, AdminBookingService adminBookingService) {
        this.loginService = loginService;
        this.registrationService = registrationService;
        this.profileService = profileService;
        this.tourService = tourService;
        this.bookingService = bookingService;
        this.adminService = adminService;
        this.adminBookingService = adminBookingService;
    }

    @PostConstruct
    public void init() {
        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(PORT)) {
                System.out.println("Сервер запущен на порту " + PORT);
                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("Принято подключение от " + clientSocket.getRemoteSocketAddress());
                    new Thread(() -> handleClient(clientSocket)).start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void handleClient(Socket socket) {
        try (
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())
        ) {
           Object input = in.readObject();
           if (input instanceof LoginRequest req) {
               LoginResponse resp = loginService.validateUser(req.getLogin(), req.getPassword(), req.getRole());
               if (resp.isSuccess()) {
                   User user = loginService.getUser(req.getLogin(), req.getPassword(), req.getRole());
                   String sessionId = SessionManager.createSession(user);
                   resp = new LoginResponse(true, sessionId);
               } else resp = new LoginResponse(false, "Неверные данные");
               out.writeObject(resp);
               out.flush();
           } else if (input instanceof RegistrationRequest req) {
               RegistrationResponse resp = registrationService.register(req.getName(), req.getLogin(), req.getPassword());
               if (resp.isSuccess()) {
                   User user = registrationService.getUser(req.getLogin());
                   String sessionId = SessionManager.createSession(user);
                   resp = new RegistrationResponse(true, sessionId);
               } else resp = new RegistrationResponse(false, "Неверные данные");
               out.writeObject(resp);
               out.flush();
           } else if (input instanceof ProfileRequest req) {
               User user = SessionManager.getUser(req.getSessionId());
               if (user == null) {
                   ProfileResponse resp = new ProfileResponse(false, "Сессия не найдена", null);
                   out.writeObject(resp);
                   out.flush();
                   return;
               }
               if (req.getUpdatedUser() == null) {
                   ProfileResponse resp = new ProfileResponse(true, "Профиль получен", profileService.getUserProfile(user));
                   out.writeObject(resp);
                   out.flush();
               } else {
                   boolean success = profileService.updateUserProfile(user, req.getUpdatedUser());
                   if (success) {
                       ProfileResponse resp = new ProfileResponse(true, "Профиль обновлен", profileService.getUserProfile(user));
                       out.writeObject(resp);
                       out.flush();
                   } else {
                       ProfileResponse resp = new ProfileResponse(false, "Ошибка обновления профиля", null);
                       out.writeObject(resp);
                       out.flush();
                   }
               }
           } else if (input instanceof TourRequest req) {
               TourSearchCriteria criteria = new TourSearchCriteria();
               criteria.setCountry(req.getCountry());
               criteria.setStartDate(req.getStartDate());
               criteria.setNights(req.getNights());
               criteria.setPersons(req.getPersons());
               criteria.setMaxPrice(req.getMaxPrice());
               criteria.setMinPrice(req.getMinPrice());
               criteria.setFood(req.getFood());

               List<Tour> tours = tourService.searchTours(criteria);

               List<TourDTO> toursDTO = tours.stream().map(tour -> {
                  TourDTO tourDTO = new TourDTO();
                  tourDTO.setId(tour.getId());
                  tourDTO.setTitle(tour.getTitle());
                  tourDTO.setDescription(tour.getDescription());
                  tourDTO.setCountry(tour.getCountry());
                  tourDTO.setStartDate(tour.getStartDate());
                  tourDTO.setNights(tour.getNights());
                  tourDTO.setPrice(tour.getPrice());
                  tourDTO.setFood(tour.getFood());
                  tourDTO.setCapacity(tour.getCapacity());
                  return tourDTO;
               }).toList();
               TourResponse resp = new TourResponse(true, "Туры получены", toursDTO);
               out.writeObject(resp);
               out.flush();
           } else if (input instanceof BookTourRequest req) {
               User user = SessionManager.getUser(req.getSessionId());
               if (user == null) {
                   BookTourResponse resp = new BookTourResponse(false, "Сессия не найдена");
                   out.writeObject(resp);
                   out.flush();
                   return;
               }

               boolean bookingSucces = bookingService.bookTour(req.getTourId(), user.getId());
               BookTourResponse resp = new BookTourResponse(bookingSucces,
                       bookingSucces ? "Тур успешно забронирован" : "Не удалось забронировать тур");
               out.writeObject(resp);
               out.flush();
           } else if (input instanceof BookingRequest req) {
               String sessionId = req.getSessionId();
               User user = SessionManager.getUser(sessionId);
               if (user == null) {
                   BookingResponse resp = new BookingResponse(false, "Сессия не найдена", null);
                   out.writeObject(resp);
                   out.flush();
                   return;
               }
               List<BookingDTO> bookings = bookingService.getBookingByUserId(user.getId());
               BookingResponse resp = new BookingResponse(true, "Бронирования получены", bookings);
               out.writeObject(resp);
               out.flush();
           } else if (input instanceof UsersRequest req) {
                List<UserDTO> users = adminService.getUsers();
                UsersResponse resp = new UsersResponse(true, "Пользователи получены", users);
                out.writeObject(resp);
                out.flush();
           } else if (input instanceof DeleteUserRequest req) {
               boolean success = adminService.deleteUser(req.getUserId());
               DeleteUserResponse resp = new DeleteUserResponse(success, success ? "Пользователь удалён" : "Ошибка удаления пользователя");
               out.writeObject(resp);
               out.flush();
           } else if (input instanceof PromoteUserRequest req) {
               boolean success = adminService.promoteToAdmin(req.getUserId());
               PromoteUserResponse resp = new PromoteUserResponse(success, success ? "Пользователь теперь администратор" : "Ошибка изменения роли");
               out.writeObject(resp);
               out.flush();
           } else if (input instanceof AddTourRequest req) {
               Tour newTour = new Tour();
               newTour.setTitle(req.getTitle());
               newTour.setDescription(req.getDescription());
               newTour.setCountry(req.getCountry());
               newTour.setStartDate(req.getStartDate());
               newTour.setNights(req.getNights());
               newTour.setPrice(req.getPrice());
               newTour.setFood(req.getFood());
               newTour.setCapacity(req.getCapacity());

               newTour = tourService.saveTour(newTour);

               TourDTO tourDTO = new TourDTO();
               tourDTO.setId(newTour.getId());
               tourDTO.setTitle(newTour.getTitle());
               tourDTO.setDescription(newTour.getDescription());
               tourDTO.setCountry(newTour.getCountry());
               tourDTO.setStartDate(newTour.getStartDate());
               tourDTO.setNights(newTour.getNights());
               tourDTO.setPrice(newTour.getPrice());
               tourDTO.setFood(newTour.getFood());
               tourDTO.setCapacity(newTour.getCapacity());

               AddTourResponse resp = new AddTourResponse(true, "Тур добавлен");
               out.writeObject(resp);
               out.flush();
           } else if (input instanceof UpdateTourRequest req) {
               Tour tour = tourService.findTourById(req.getId());
               if (tour == null) {
                   UpdateTourResponse resp = new UpdateTourResponse(false, "Тур не найден", null);
                   out.writeObject(resp);
                   out.flush();
                   return;
               }

               tour.setTitle(req.getTitle());
               tour.setDescription(req.getDescription());
               tour.setCountry(req.getCountry());
               tour.setStartDate(req.getStartDate());
               tour.setNights(req.getNights());
               tour.setPrice(req.getPrice());
               tour.setFood(req.getFood());
               tour.setCapacity(req.getCapacity());

               tour = tourService.saveTour(tour);

               TourDTO tourDTO = new TourDTO();
               tourDTO.setId(tour.getId());
               tourDTO.setTitle(tour.getTitle());
               tourDTO.setDescription(tour.getDescription());
               tourDTO.setCountry(tour.getCountry());
               tourDTO.setStartDate(tour.getStartDate());
               tourDTO.setNights(tour.getNights());
               tourDTO.setPrice(tour.getPrice());
               tourDTO.setFood(tour.getFood());
               tourDTO.setCapacity(tour.getCapacity());

               UpdateTourResponse resp = new UpdateTourResponse(true, "Тур обновлен", tourDTO);
               out.writeObject(resp);
               out.flush();
           } else if (input instanceof DeleteTourRequest req) {
               boolean success = tourService.deleteTourById(req.getTourId());
               DeleteTourResponse resp = new DeleteTourResponse(success, success ? "Тур удален" : "Ошибка удаления тура");
               out.writeObject(resp);
               out.flush();
           } else if (input instanceof AdminBookingRequest req) {
               List<Booking> list = adminBookingService.getAllBookings();
               List<AdminBookingDTO> dtos = list.stream().map(b -> {
                   AdminBookingDTO d = new AdminBookingDTO();
                   d.setId(b.getId());
                   d.setUserId(b.getUserId());
                   d.setTourId(b.getTourId());
                   d.setBookingDate(b.getBookingDate());
                   d.setStatus(b.getStatus());

                   User user = adminBookingService.findUserById(b.getUserId());
                   Tour tour = adminBookingService.findTourById(b.getTourId());

                   d.setUserName(user != null ? user.getName() : "неизвестно");
                   d.setTourName(tour != null ? tour.getTitle() : "неизвестно");
                   return d;
               }).toList();
               AdminBookingResponse resp = new AdminBookingResponse(true, "Успех", dtos);
               out.writeObject(resp);
           } else if (input instanceof ApproveBookingRequest req) {
               boolean ok = adminBookingService.approveBooking(req.getBookingId());
               out.writeObject(new ApproveBookingResponse(ok, ok ? "Подтверждено" : "Ошибка"));
           } else if (input instanceof RejectBookingRequest req) {
               boolean ok = adminBookingService.rejectBooking(req.getBookingId());
               out.writeObject(new RejectBookingResponse(ok, ok ? "Отклонено" : "Ошибка"));
           }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
