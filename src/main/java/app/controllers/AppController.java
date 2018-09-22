package app.controllers;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import app.entities.EntityExample;

@RestController
public class AppController {
	
	@RequestMapping("/")
	public String works(HttpServletResponse response) {
		EntityExample e = new EntityExample();
		if(e.someMethod()) {
			response.setStatus( HttpServletResponse.SC_OK  );
		}
		return "Funcionou! \n" + response;
	}
	
}
