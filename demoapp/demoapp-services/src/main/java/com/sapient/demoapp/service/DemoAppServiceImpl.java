package com.sapient.demoapp.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.json.JSONArray;
import org.json.JSONObject;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sapient.demoapp.dto.TeamDetailDTO;

@Service
public class DemoAppServiceImpl implements DemoAppService {

	private static final Logger LOGGER = ESAPI.getLogger(DemoAppServiceImpl.class);

	@Value("${webservice.base.uri}")
	private String baseUri;

	@Value("${webservice.api.key}")
	private String apiKey;

	@Override
	public JSONObject getData(String country, String league, String team) {

		String countryId = "";
		String leagueId = "";
		String result = null;
		JSONObject jsonObj = new JSONObject();

		try {

			countryId = getCountryId(country);

			leagueId = getLeagueId(countryId);

			TeamDetailDTO teamDetail = getTeamDetail(leagueId, team);

			String overallLeaguePosition = getCurrentStanding(country, league, team, leagueId);

			jsonObj.put("Country ID & Name", countryId + " - " + country);
			jsonObj.put("League ID & Name", leagueId + " - " + league);
			jsonObj.put("Team ID & Name", teamDetail.getTeam_key() + " - " + team);
			jsonObj.put("Overall League Position", overallLeaguePosition);

		} catch (Exception e) {
			LOGGER.error(Logger.EVENT_FAILURE, "Error occurred");
		}

		return jsonObj;
	}

	public String getCurrentStanding(String country, String league, String team, String leagueId) {

		String resp = null;
		String overallLeaguePosition = "";
		StringBuilder finalUrl = new StringBuilder(baseUri);
		finalUrl.append("get_standings&league_id=").append(leagueId).append("&APIkey=").append(apiKey);

		GetMethod get = new GetMethod(finalUrl.toString());
		try {
			HttpClient httpclient = new HttpClient();

			int statusCode = httpclient.executeMethod(get);

			resp = get.getResponseBodyAsString();

			JSONArray jsonarray = new JSONArray(resp);
			for (int i = 0; i < jsonarray.length(); i++) {
				JSONObject jsonobject = jsonarray.getJSONObject(i);
				if (jsonobject.getString("country_name").equals(country)
						&& jsonobject.getString("league_name").equals(league)
						&& jsonobject.getString("league_name").equals(league)) {
					overallLeaguePosition = jsonobject.getString("overall_league_position");
				}
			}

		} catch (IOException e) {
			LOGGER.error(Logger.EVENT_FAILURE, "IOException in calling service ");
		} finally {
			get.releaseConnection();
		}
		return overallLeaguePosition;
	}

	public TeamDetailDTO getTeamDetail(String leagueId, String team) {

		List<TeamDetailDTO> teamList = new ArrayList<>();
		TeamDetailDTO teaamDto = new TeamDetailDTO();
		String resp = null;
		String countryId = "";
		StringBuilder finalUrl = new StringBuilder(baseUri);
		finalUrl.append("get_teams&league_id=").append(leagueId).append("&APIkey=").append(apiKey);

		GetMethod get = new GetMethod(finalUrl.toString());
		try {
			HttpClient httpclient = new HttpClient();

			int statusCode = httpclient.executeMethod(get);

			resp = get.getResponseBodyAsString();
			teamList = new Gson().fromJson(get.getResponseBodyAsString(), new TypeToken<List<TeamDetailDTO>>() {
			}.getType());

			for (TeamDetailDTO dto : teamList) {
				if (dto.getTeam_name().equals(team)) {
					teaamDto.setTeam_key(dto.getTeam_key());
					teaamDto.setTeam_name(dto.getTeam_name());
				}
			}

		} catch (IOException e) {
			LOGGER.error(Logger.EVENT_FAILURE, "IOException in calling service ");
		} finally {
			get.releaseConnection();
		}
		return teaamDto;

	}

	public String getCountryId(String country) {

		String resp = null;
		String countryId = "";
		StringBuilder finalUrl = new StringBuilder(baseUri);
		finalUrl.append("get_countries&APIkey=").append(apiKey);

		GetMethod get = new GetMethod(finalUrl.toString());
		try {
			HttpClient httpclient = new HttpClient();

			int statusCode = httpclient.executeMethod(get);

			resp = get.getResponseBodyAsString();

			JSONArray jsonarray = new JSONArray(resp);
			for (int i = 0; i < jsonarray.length(); i++) {
				JSONObject jsonobject = jsonarray.getJSONObject(i);
				String id = jsonobject.getString("country_id");
				String name = jsonobject.getString("country_name");
				if (name.equals(country)) {
					countryId = id;
				}
			}

		} catch (IOException e) {
			LOGGER.error(Logger.EVENT_FAILURE, "IOException in calling service ");
		} finally {
			get.releaseConnection();
		}
		return countryId;
	}

	public String getLeagueId(String countryId) {
		String resp = null;
		String leagueId = "";
		StringBuilder finalUrl = new StringBuilder(baseUri);
		finalUrl.append("get_leagues&country_id=").append(countryId).append("&APIkey=").append(apiKey);

		GetMethod get = new GetMethod(finalUrl.toString());
		try {
			HttpClient httpclient = new HttpClient();

			int statusCode = httpclient.executeMethod(get);

			resp = get.getResponseBodyAsString();

			JSONArray jsonarray = new JSONArray(resp);
			for (int i = 0; i < jsonarray.length(); i++) {
				JSONObject jsonobject = jsonarray.getJSONObject(i);
				String id = jsonobject.getString("league_id");
				String name = jsonobject.getString("country_id");
				if (name.equals(countryId)) {
					leagueId = id;
				}
			}

		} catch (IOException e) {
			LOGGER.error(Logger.EVENT_FAILURE, "IOException in calling service ");
		} finally {
			get.releaseConnection();
		}
		return leagueId;
	}
}
