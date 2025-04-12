package com.server;

import com.kurs.dto.*;
import com.server.Entities.Tour;
import com.server.Entities.User;
import com.server.Service.LoginService;
import com.server.Service.ProfileService;
import com.server.Service.RegistrationService;
import com.server.Service.TourService;
import com.server.search.TourSearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

@Component
public class TcpServer {
    private static final int PORT = 11000;

    private final LoginService loginService;
    private final RegistrationService registrationService;
    private final ProfileService profileService;
    private final TourService tourService;

    @Autowired
    public TcpServer(LoginService loginService, RegistrationService registrationService, ProfileService profileService, TourService tourService) {
        this.loginService = loginService;
        this.registrationService = registrationService;
        this.profileService = profileService;
        this.tourService = tourService;
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
