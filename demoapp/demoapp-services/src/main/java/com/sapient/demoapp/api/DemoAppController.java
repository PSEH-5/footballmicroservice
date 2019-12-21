package com.sapient.demoapp.api;

import org.json.JSONObject;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sapient.demoapp.service.DemoAppService;

@RestController
@Validated
@CrossOrigin
@RequestMapping("/demoapp")
public class DemoAppController {

	private static final Logger LOGGER = ESAPI.getLogger(DemoAppController.class);

	@Autowired
	DemoAppService serv;

	@GetMapping(value = "/standings")
	public String getData(@RequestParam(required = true) String country,
			@RequestParam(required = true) String league, @RequestParam(required = true) String team) {
		LOGGER.debug(Logger.EVENT_SUCCESS, "Entering Controller::getData()");
		JSONObject obj = serv.getData(country, league, team);
		String response = obj.toString();
		return response;
	}

}
