package com.idt.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class testController {
	
	@RequestMapping(value={"get.do"},method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public Object get() {
		System.out.println("========");
		return "helloword";
	}
}
