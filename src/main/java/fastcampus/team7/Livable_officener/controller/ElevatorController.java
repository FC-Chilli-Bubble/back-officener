package fastcampus.team7.Livable_officener.controller;

import fastcampus.team7.Livable_officener.dto.elevator.ElevatorDTO;
import fastcampus.team7.Livable_officener.global.util.APIDataResponse;
import fastcampus.team7.Livable_officener.repository.ElevatorRepository;
import fastcampus.team7.Livable_officener.service.ElevatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class ElevatorController {

    private final ElevatorService elevatorService;

    @GetMapping("/api/elevator")
    public ResponseEntity<APIDataResponse<List<ElevatorDTO>>> getElevators(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization){
        String token = authorization.split(" ")[1];
        return elevatorService.getElevators(token);
    }

    @GetMapping("/api/elevator/all")
    public ResponseEntity<APIDataResponse<List<ElevatorDTO>>> getAllElevators(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization){
        String token = authorization.split(" ")[1];
        return elevatorService.getAllElevators(token);
    }

    @PostMapping("/api/elevator")
    public ResponseEntity<APIDataResponse<String>> processSelectedIds(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
            @RequestBody ElevatorDTO.UserElevatorDTO userElevatorDTO) {
        String token = authorization.split(" ")[1];
        List<Long> selectedIds = userElevatorDTO.getSelectedIds();
        return elevatorService.setElevator(selectedIds,token);
    }

}
