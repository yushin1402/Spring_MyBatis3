package com.example.todo.domain.repository.todo;
import java.util.Collection;
import com.example.todo.domain.model.Todo;
import java.util.Optional;

public interface TodoRepository {
	Optional<Todo> findById(String todoId);
	Collection<Todo> findAll();
	void create(Todo todo);
	boolean updateById(Todo todo);
	void deleteById(Todo todo);
	long countByFinished(boolean finished);
}
