package com.laura.demowebapp.controller;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.laura.demowebapp.model.Todo;
import com.laura.demowebapp.service.TodoService;

@Controller
public class TodosController {
	
	@Autowired
	TodoService todoService;
	
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		//date dd/MM/yyyy
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		binder.registerCustomEditor(Date.class, new CustomDateEditor(
				dateFormat, false));
	}

	@RequestMapping(value="/list-todos", method = RequestMethod.GET)
	public String showListTodos( ModelMap model) {
		String name = getLoggedInUsername(model);
		model.put("todos", todoService.retrieveTodos(name));
		return "list-todos";
	}
	
	@RequestMapping(value="/add-todo", method = RequestMethod.GET)
	public String showTodoPage( ModelMap model) {
		String name = getLoggedInUsername(model);
		model.addAttribute("todo", new Todo(0, name, "", new Date(), false));
		return "todo";
	}

	private String getLoggedInUsername(ModelMap model) {
		//return (String) model.get("name");
		Object principal = SecurityContextHolder.getContext()
				.getAuthentication().getPrincipal();
		if(principal instanceof UserDetails) {
			return ((UserDetails) principal).getUsername();
		}
		return principal.toString();
	}
	
	@RequestMapping(value="/add-todo", method = RequestMethod.POST)
	public String addTodo( ModelMap model, @Valid Todo todo, BindingResult result) {
		if(result.hasErrors()) {
			return "todo";
		}
		String name = getLoggedInUsername(model);
		todoService.addTodo(name, todo.getDesc(), todo.getTargetDate(), false);
		return "redirect:/list-todos";
	}
	
	@RequestMapping(value="/delete-todo", method = RequestMethod.GET)
	public String deleteTodo( @RequestParam int id) {
		todoService.deleteTodo(id);
		return "redirect:/list-todos";
	}
	
	@RequestMapping(value="/update-todo", method = RequestMethod.GET)
	public String showUpdateTodoPage( @RequestParam int id, ModelMap model) {
		Todo todo = todoService.retrieveTodo(id);
		model.put("todo", todo);
		return "todo";
	}
	
	@RequestMapping(value="/update-todo", method = RequestMethod.POST)
	public String updateTodo(ModelMap model, @Valid Todo todo, BindingResult result) {
		String name = getLoggedInUsername(model);
		todo.setUser(name); 
		if(result.hasErrors()) {
			return "todo";
		}
		todoService.updateTodo(todo);
		return "redirect:/list-todos";
	}
}
