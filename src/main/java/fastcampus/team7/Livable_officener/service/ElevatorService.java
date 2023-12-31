package fastcampus.team7.Livable_officener.service;

import fastcampus.team7.Livable_officener.domain.Elevator;
import fastcampus.team7.Livable_officener.domain.User;
import fastcampus.team7.Livable_officener.domain.UserElevator;
import fastcampus.team7.Livable_officener.dto.elevator.ElevatorDTO;
import fastcampus.team7.Livable_officener.global.constant.ElevatorStatus;
import fastcampus.team7.Livable_officener.global.sercurity.JwtProvider;
import fastcampus.team7.Livable_officener.global.util.APIDataResponse;
import fastcampus.team7.Livable_officener.repository.ElevatorRepository;
import fastcampus.team7.Livable_officener.repository.UserElevatorRepository;
import fastcampus.team7.Livable_officener.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Service
@RequiredArgsConstructor
public class ElevatorService {

    private final ElevatorRepository elevatorRepository;

    private final JwtProvider jwtProvider;

    private final UserRepository userRepository;

    private final UserElevatorRepository userElevatorRepository;

    public ResponseEntity<APIDataResponse<List<ElevatorDTO>>> getElevators(String token) {
        User user = userRepository.findByEmail(jwtProvider.getEmail(token))
                .orElse(null);
        List<UserElevator> userElevators = userElevatorRepository.findByUserId(user.getId()).orElse(null);
        List<ElevatorDTO> elevatorDTOs = new ArrayList<>();

        if (userElevators.isEmpty()) {
            List<Elevator> elevators = Collections.emptyList();
            for (Elevator elevator : elevators) {
                elevatorDTOs.add(convertToDTO(elevator));
            }
        } else {
            for (UserElevator userElevator : userElevators) {
                Elevator elevator = elevatorRepository.findById(userElevator.getElevatorId())
                        .orElseThrow(() -> new RuntimeException("해당하는 엘리베이터가 없습니다"));
                elevatorDTOs.add(convertToDTO(elevator));
            }
        }

        return APIDataResponse.of(HttpStatus.OK, elevatorDTOs);
    }

    public ResponseEntity<APIDataResponse<List<ElevatorDTO>>> getAllElevators(String token) {
        List<ElevatorDTO> elevatorDTOs = new ArrayList<>();

        List<Elevator> elevators = elevatorRepository.findAll();
        for (Elevator elevator : elevators) {
             elevatorDTOs.add(convertToDTO(elevator));
         }

        return APIDataResponse.of(HttpStatus.OK, elevatorDTOs);
    }

    public ResponseEntity<APIDataResponse<String>> setElevator(List<Long> selectedIds, String token) {
        User user = userRepository.findByEmail(jwtProvider.getEmail(token))
                .orElseThrow(() -> new RuntimeException("토큰에 일치하는 유저가 없습니다"));
        userElevatorRepository.deleteByUserId(user.getId());
        for (Long id : selectedIds) {
            UserElevator userElevator = new UserElevator();
            userElevator.setUserId(user.getId());
            userElevator.setElevatorId(id);
            userElevatorRepository.save(userElevator);
        }
        return APIDataResponse.empty(HttpStatus.OK);
    }

    private ElevatorDTO convertToDTO(Elevator elevator) {
        ElevatorDTO elevatorDTO = new ElevatorDTO();
        if (elevator.getStatus().equals(ElevatorStatus.REPAIR)) {
            elevatorDTO.setId(elevator.getId());
            elevatorDTO.setStatus(elevator.getStatus());
            return elevatorDTO;
        } else {
            elevatorDTO.setId(elevator.getId());
            elevatorDTO.setFloor(elevator.getFloor());
            elevatorDTO.setDirection(elevator.getDirection());
            elevatorDTO.setStatus(elevator.getStatus());
            return elevatorDTO;
        }
    }
}
