package com.todo.projet_springboot.DTO;

import com.todo.projet_springboot.Enum.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskRequest {


    @NotBlank
    private String title;

    private String description;

    @NotBlank
    private TaskStatus status;  // Task status like "Pending", "In Progress", or "Completed"

    @NotBlank
    private String priorite;

    @NotNull
    private LocalDate deadline;

    private  Long devid;

    private String devName ;

}
