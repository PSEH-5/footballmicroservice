package com.sapient.demoapp.service;

import org.json.JSONObject;

public interface DemoAppService {

	JSONObject getData(String country, String league, String team);
}
