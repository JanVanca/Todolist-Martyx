package sk.janvanca.todolist.services.api;

import org.springframework.stereotype.Component;
import sk.janvanca.todolist.domain.Task;

import java.util.List;


@Component
public interface TaskService {
    List<Task> getTasks();
    Task get (Integer id);
    List<Task> getTaskByUserId(Integer id);
    Integer add(Task task);
    void delete (Integer id);
    void update(Integer id, Task task);
}
