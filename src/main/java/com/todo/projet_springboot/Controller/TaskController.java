package com.todo.projet_springboot.Controller;

import com.todo.projet_springboot.Config.JwtTokenUtil;
import com.todo.projet_springboot.DTO.TaskRequest;
import com.todo.projet_springboot.Entity.Task;
import com.todo.projet_springboot.Enum.TaskStatus;
import com.todo.projet_springboot.Repository.TaskRepository;
import com.todo.projet_springboot.Service.TaskService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Controller
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

//
//    @GetMapping("/create")
//    public String showCreateTaskForm(Model model) {
//        model.addAttribute("taskRequest", new TaskRequest()); // Bind empty TaskRequest object
//        return "ajoutetask"; // Thymeleaf template name
//    }

    // Handle form submission and create the task
    @RequestMapping(value="/create", method ={RequestMethod.GET,RequestMethod.POST})
    public String createTask(@ModelAttribute TaskRequest taskRequest, Model model ,HttpSession session) {
        if(taskRequest== null){
            return "ajoutetask";
        }else {
            try {
                String token = (String) session.getAttribute("token");
                if(token==null){
                    model.addAttribute("error","you need to log in first.");
                    return "login";
                }
                Long userId = jwtTokenUtil.getIdFromToken(token);
                Task task = taskService.createTask(taskRequest, userId);
                String email= jwtTokenUtil.getUserEmailFromToken(token);
                String username = jwtTokenUtil.getUsernameFromToken(token);
                model.addAttribute("username",username);
                model.addAttribute("email",email);
                model.addAttribute("message", "Task created successfully!");
                model.addAttribute("task", task);

                return "redirect:/tasks/alltasks"; // Page to display the created task
            } catch (Exception e) {
                model.addAttribute("error", "Error: " + e.getMessage());
                return "ajoutetask"; // Redirect back to the form with error
            }
        }
    }



    @RequestMapping(value = "/alltasks", method = {RequestMethod.GET, RequestMethod.POST})
    public String getAllTasks(@RequestParam(value = "taskType", defaultValue = "all") String taskType, Model model, HttpSession session) {
        try {
            String token = (String) session.getAttribute("token");
            if (token == null) {
                model.addAttribute("error", "You need to log in first.");
                return "login";
            }
            LocalDate today = LocalDate.now();
            long doneTasksCount = taskService.getDoneTasksCount();
            long todoTasksCount = taskService.getInProgressTasksCount();
            long QATasksCount = taskService.getInQATasksCount();
            long allTasksCount = taskService.getCountTasks();
            String role = jwtTokenUtil.getRoleFromToken(token);
            Long userId = jwtTokenUtil.getIdFromToken(token);
            List<Task> tasks = new ArrayList<>();

            List<Task> tasksDueToday = taskService.getTasksDueOnDate(today);
            // Filter tasks based on the taskType
            switch (taskType) {
                case "done":
                    tasks = taskService.getTasksByStatus(TaskStatus.TERMINE);
                    break;
                case "todo":
                    tasks = taskService.getTasksByStatus(TaskStatus.EN_COURS);
                    break;
                case "QA":
                    tasks = taskService.getTasksByStatus(TaskStatus.QA);
                    break;
                case "all":
                default:
                    tasks = taskService.getAllTasksUsers();
                    break;
            }

            model.addAttribute("tasks", tasks);
            String email = jwtTokenUtil.getUserEmailFromToken(token);
            String username = jwtTokenUtil.getUsernameFromToken(token);
            Long iduser = jwtTokenUtil.getIdFromToken(token);
            model.addAttribute("username", username);
            model.addAttribute("email", email);
            model.addAttribute("doneTasksCount", doneTasksCount);
            model.addAttribute("todoTasksCount", todoTasksCount);
            model.addAttribute("QATasksCount", QATasksCount);
            model.addAttribute("allTasksCount", allTasksCount);
            model.addAttribute("tasksDueToday", tasksDueToday);
            model.addAttribute("iduser",iduser);
            model.addAttribute("role",role);

            return "taskList";
        } catch (Exception e) {
            model.addAttribute("error", "Error: " + e.getMessage());
            return "taskList";
        }
    }




    @RequestMapping(value = "/allMytasks", method = {RequestMethod.GET, RequestMethod.POST})
    public String getAllMYTasks(@RequestParam(value = "taskType", defaultValue = "all") String taskType, Model model, HttpSession session) {
        try {
            String token = (String) session.getAttribute("token");
            if (token == null) {
                model.addAttribute("error", "You need to log in first.");
                return "login";
            }
            LocalDate today = LocalDate.now();
            long doneTasksCount = taskService.getDoneTasksCount();
            long todoTasksCount = taskService.getInProgressTasksCount();
            long QATasksCount = taskService.getInQATasksCount();
            long allTasksCount = taskService.getCountTasks();
            String role = jwtTokenUtil.getRoleFromToken(token);
            Long userId = jwtTokenUtil.getIdFromToken(token);
            List<Task> tasks = new ArrayList<>();

            List<Task> tasksDueToday = taskService.getTasksDueOnDate(today);

            // Filter tasks based on the taskType
            switch (taskType) {
                case "done":
                    tasks = taskService.getTasksByStatus(TaskStatus.TERMINE);
                    break;
                case "todo":
                    tasks = taskService.getTasksByStatus(TaskStatus.EN_COURS);
                    break;
                case "QA":
                    tasks = taskService.getTasksByStatus(TaskStatus.QA);
                    break;
                case "all":
                default:
                    tasks = taskService.getAllTasks(userId);
                    break;
            }

            model.addAttribute("tasks", tasks);
            String email = jwtTokenUtil.getUserEmailFromToken(token);
            String username = jwtTokenUtil.getUsernameFromToken(token);
            Long iduser = jwtTokenUtil.getIdFromToken(token);
            model.addAttribute("username", username);
            model.addAttribute("email", email);
            model.addAttribute("doneTasksCount", doneTasksCount);
            model.addAttribute("todoTasksCount", todoTasksCount);
            model.addAttribute("QATasksCount", QATasksCount);
            model.addAttribute("allTasksCount", allTasksCount);
            model.addAttribute("tasksDueToday", tasksDueToday);
            model.addAttribute("iduser",iduser);
            model.addAttribute("role",role);
            return "taskList";
        } catch (Exception e) {
            model.addAttribute("error", "Error: " + e.getMessage());
            return "taskList";
        }
    }


    // Show details of a specific task
    @GetMapping("/markDone/{taskId}")
    public String markDone(@PathVariable Long taskId, Model model) {
        Task task = taskService.getTaskById(taskId);
        if (task != null) {
            taskService.markTaskAsDone(taskId);
            return "redirect:/tasks/alltasks"; // Thymeleaf template to display task details
        } else {
            model.addAttribute("error", "Task not found");
            return "taskList"; // Redirect back to task list page
        }
    }
    @GetMapping("/markQA/{taskId}")
    public String markQA(@PathVariable Long taskId, Model model) {
        Task task = taskService.getTaskById(taskId);
        if (task != null) {
            task.setDevid(null);
            taskService.markTaskAsQA(taskId);
            return "redirect:/tasks/alltasks"; // Thymeleaf template to display task details
        } else {
            model.addAttribute("error", "Task not found");
            return "taskList"; // Redirect back to task list page
        }
    }




    @RequestMapping(value = "/update/{taskId}" ,method = RequestMethod.GET)
    public String showUpdateForm(@PathVariable Long taskId, Model model, HttpSession session) {
        String token = (String) session.getAttribute("token");
        if (token == null) {
            model.addAttribute("error", "You need to log in first.");
            return "login";
        }

        try {
            // Verify the user's identity from the token
            Long userId = jwtTokenUtil.getIdFromToken(token);

            // Get the task
            Task task = taskService.getTaskById(taskId);

            // Check if task exists and belongs to the current user
            if (task != null ) {
                System.out.println("Task found: " + task);
                model.addAttribute("task", task);
                return "update";
            } else {
                System.out.println("No task found with ID or unauthorized access: " + taskId);
                model.addAttribute("error", "Task not found or you are not authorized to update this task");
                return "taskList";
            }
        } catch (Exception e) {
            model.addAttribute("error", "Authentication error: " + e.getMessage());
            return "login";
        }
    }

    @PostMapping("/update/{taskId}")
    public String updateTask(@PathVariable Long taskId,
                             @ModelAttribute TaskRequest taskRequest,
                             Model model,
                             HttpSession session) {
        try {
            String token = (String) session.getAttribute("token");
            if (token == null) {

                model.addAttribute("error", "Authentication required");
                return "login";
            }

            Long currentUserId = jwtTokenUtil.getIdFromToken(token);


            Task existingTask = taskService.getTaskById(taskId);
            if (existingTask == null || !existingTask.getUser().getId().equals(currentUserId)) {

                model.addAttribute("error", "You are not authorized to update this task or it does not exist");
                return "error";
            }

            // Perform the update
            Task updatedTask = taskService.updateTask(taskId, taskRequest);


            model.addAttribute("message", "Task updated successfully");
            model.addAttribute("tasks", updatedTask);
            return "redirect:/tasks/alltasks"; // Redirect or render task details page

        } catch (Exception e) {

            model.addAttribute("error", "Error updating task: " + e.getMessage());
            return "update";
        }
    }




    @GetMapping("/affected/{taskId}")
    public String affected(@PathVariable Long taskId,@ModelAttribute TaskRequest taskRequest,
                             Model model,
                             HttpSession session) {
        try {
            String token = (String) session.getAttribute("token");
            if (token == null) {

                model.addAttribute("error", "Authentication required");
                return "login";
            }

            Long currentUserId = jwtTokenUtil.getIdFromToken(token);
            String currentUserName= jwtTokenUtil.getUsernameFromToken(token);


            Task existingTask = taskService.getTaskById(taskId);
            if (existingTask == null ) {

                model.addAttribute("error", "You are not authorized to update this task or it does not exist");
                return "error";
            }

            // Perform the update
            Task updatedTask = taskService.affectedTask(taskId, taskRequest,currentUserId,currentUserName);


            model.addAttribute("message", "Task updated successfully");
            model.addAttribute("tasks", updatedTask);
            return "redirect:/tasks/alltasks"; // Redirect or render task details page

        } catch (Exception e) {

            model.addAttribute("error", "Error updating task: " + e.getMessage());
            return "update";
        }
    }

    // Handle task deletion
    @RequestMapping(value = "/delete/{taskId}",method = RequestMethod.GET)
    public String deleteTask(@PathVariable Long taskId, Model model) {
        if (taskService.deleteTask(taskId)) {
            model.addAttribute("message", "Task deleted successfully!");
            return "redirect:/tasks/alltasks"; // Redirect to task list page after successful deletion
        } else {
            model.addAttribute("error", "Task not found");
            return "taskList"; // Redirect to task list if task not found
        }
    }

}
