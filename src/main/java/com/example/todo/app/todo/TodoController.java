package com.example.todo.app.todo;

import java.util.Collection;

import javax.inject.Inject;
import javax.validation.groups.Default;
import javax.validation.Valid;

import com.github.dozermapper.core.Mapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.terasoluna.gfw.common.exception.BusinessException;
import org.terasoluna.gfw.common.message.ResultMessage;
import org.terasoluna.gfw.common.message.ResultMessages;

import com.example.todo.app.todo.TodoForm.TodoCreate;
import com.example.todo.app.todo.TodoForm.TodoFinish;
import com.example.todo.app.todo.TodoForm.TodoDelete;
import com.example.todo.domain.model.Todo;
import com.example.todo.domain.service.todo.TodoService;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;


@Controller
@RequestMapping("todo")
public class TodoController {
	@Inject
	TodoService todoService;
	
	@Inject
	Mapper beanMapper;

	@ModelAttribute
	public TodoForm setUpForm() {
		TodoForm form = new TodoForm();
		//System.out.println(1 + ToStringBuilder.reflectionToString(form, ToStringStyle.MULTI_LINE_STYLE));
		return form;
	}
	
	@GetMapping("list")
	public String list(Model model) {
		Collection<Todo> todos = todoService.findAll();
		System.out.println("findAll_todos" + ToStringBuilder.reflectionToString(todos, ToStringStyle.MULTI_LINE_STYLE));
		model.addAttribute("todos",todos);
		model.asMap().entrySet().stream().forEach(s -> System.out.println("findAll_model" + s));
		return "todo/list";
	}
	
	@PostMapping("create")
	public String create(@Validated({Default.class, TodoCreate.class}) TodoForm todoForm, BindingResult bindingResult, 
				Model model, RedirectAttributes attributes) {
		//System.out.println(2 + ToStringBuilder.reflectionToString(todoForm, ToStringStyle.MULTI_LINE_STYLE));
		//System.out.println(ToStringBuilder.reflectionToString(model, ToStringStyle.MULTI_LINE_STYLE));
		if (bindingResult.hasErrors()) {
			return list(model);
		}
		
		Todo todo = beanMapper.map(todoForm, Todo.class);
		
		try {
			todoService.create(todo);
		}catch(BusinessException e) {
			model.addAttribute(e.getResultMessages());
			return list(model);
		}
		
		attributes.addFlashAttribute(ResultMessages.success()
				.add(ResultMessage.fromText("Created successfully!")));
		
		return "redirect:/todo/list";
	}
	
	@PostMapping("finish")
	public String finish(@Validated({Default.class,TodoFinish.class}) TodoForm form,
			BindingResult bindingResult, Model model, RedirectAttributes attributes) {
		if (bindingResult.hasErrors()) {
			return list(model);
		}
		
		try {
			todoService.finish(form.getTodoId());
		}catch(BusinessException e) {
			model.addAttribute(e.getResultMessages());
		}
		
		System.out.println("finish_form " + ToStringBuilder.reflectionToString(form, ToStringStyle.MULTI_LINE_STYLE));
		attributes.addFlashAttribute(ResultMessages.success().add(ResultMessage.fromText(
				"Finished successfully!")));
		
		return "redirect:/todo/list";
	}
	
	@PostMapping("delete")
	public String delete(@Validated({Default.class,TodoDelete.class}) TodoForm form,
			BindingResult bindingResult, Model model, RedirectAttributes attributes) {
		if (bindingResult.hasErrors()) {
			return list(model);
		}
		
		try {
			todoService.delete(form.getTodoId());
		}catch(BusinessException e) {
			model.addAttribute(e.getResultMessages());
		}
		
		attributes.addFlashAttribute(ResultMessages.success().add(
				ResultMessage.fromText("Deleted Successfully")));
		
		return "redirect:/todo/list";
	}
}
