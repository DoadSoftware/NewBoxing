package com.boxing.broadcaster;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import com.boxing.containers.Scene;
import com.boxing.containers.ScoreBug;
import com.boxing.model.Bugs;
import com.boxing.model.Fixture;
import com.boxing.model.Match;
import com.boxing.model.MatchType;
import com.boxing.model.NameSuper;
import com.boxing.model.Player;
import com.boxing.model.Team;
import com.boxing.model.TeamColor;
import com.boxing.service.BoxingService;
import com.boxing.util.BoxingUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.xml.bind.JAXBException;

public class DOAD_NATIONALS extends Scene {

	public String session_selected_broadcaster = "DOAD_NATIONALS";
	public ObjectMapper objectMapper = new ObjectMapper();
	public ScoreBug scorebug = new ScoreBug(); 
	public String which_graphics_onscreen = "";
	public boolean is_infobar = false;
	private String status;
	private String icon_path = "D:\\DOAD_In_House_Everest\\Everest_Sports\\Everest_Khelo_India_2023\\Icons\\";
	public ScoreBug updateScoreBug(List<Scene> scenes, Match match, PrintWriter print_writer,BoxingService boxingService)
			throws InterruptedException, MalformedURLException, IOException {
		if (scorebug.isScorebug_on_screen() == true) {
			scorebug = populateScoreBug(true, scorebug, print_writer, scenes.get(0).getScene_path(), match,session_selected_broadcaster,boxingService.getTeam(),boxingService.getTeamColors(),boxingService.getAllPlayer());
			
		}
		return scorebug;
	}
	public Object ProcessGraphicOption(String whatToProcess, Match match, BoxingService boxingService,PrintWriter print_writer, 
			List<Scene> scenes, String valueToProcess)throws InterruptedException, NumberFormatException, MalformedURLException, IOException, JAXBException{
		
		switch (whatToProcess.toUpperCase()) {
		case "POPULATE-SCOREBUG":
		case "POPULATE-L3-NAMESUPER": case "POPULATE-FF-MATCHID": case "POPULATE-NAMESUPER_PLAYER": case "POPULATE-NAMESUPER-SP": 
		case "POPULATE-CROSS": case "POPULATE-FF-SCORELINE": case "POPULATE-L3-BUG": case "POPULATE-NAMESUPER-CATEGORY":
		case "POPULATE-MATCH_PROMO": case "POPULATE-NAMESUPER-MEDAL_MANUAL":
			
			switch(whatToProcess.toUpperCase()) {
			case "POPULATE-SCOREBUG":
				scenes.get(0).scene_load(print_writer, session_selected_broadcaster);
				break;
			default:
				scenes.get(1).setScene_path(valueToProcess.split(",")[0]);
				scenes.get(1).scene_load(print_writer,session_selected_broadcaster);
				break;
			}
			switch (whatToProcess.toUpperCase()) {
			case "POPULATE-SCOREBUG":
				populateScoreBug(false,scorebug,print_writer, valueToProcess.split(",")[0],match, session_selected_broadcaster,boxingService.getTeam(),boxingService.getTeamColors(),boxingService.getAllPlayer());
				break;
			case "POPULATE-L3-NAMESUPER":
				for(NameSuper ns : boxingService.getNameSupers()) {
					  if(ns.getNamesuperId() == Integer.valueOf(valueToProcess.split(",")[1])) {
						  populateNameSuper(print_writer, valueToProcess.split(",")[0], ns, match, session_selected_broadcaster);
					  }
					}
				break;
			case "POPULATE-NAMESUPER-MEDAL_MANUAL":
				populateNameSuperMedalManual(print_writer, valueToProcess.split(",")[0],Integer.valueOf(valueToProcess.split(",")[1]),
						Integer.valueOf(valueToProcess.split(",")[2]),Integer.valueOf(valueToProcess.split(",")[3]),Integer.valueOf(valueToProcess.split(",")[4]),valueToProcess.split(",")[5],
						match, session_selected_broadcaster,boxingService.getAllPlayer(),boxingService.getTeam(),boxingService.getMatchType());
				break;	
			case "POPULATE-NAMESUPER-CATEGORY":
				populateCategory(print_writer, valueToProcess.split(",")[0],Integer.valueOf(valueToProcess.split(",")[1]),valueToProcess.split(",")[2], 
							match, session_selected_broadcaster, boxingService.getTeam(), boxingService.getAllPlayer(),boxingService.getMatchType());
				break;
			case "POPULATE-FF-SCORELINE":
				populateFFScoreline(print_writer, valueToProcess.split(",")[0], match, session_selected_broadcaster, boxingService.getTeam(), boxingService.getAllPlayer());
				break;	
			case "POPULATE-NAMESUPER-SP":
				populateNameSuperSP(print_writer, valueToProcess.split(",")[0],Integer.valueOf(valueToProcess.split(",")[1]),
						match, session_selected_broadcaster,boxingService.getTeam(),boxingService.getAllPlayer());
				break;
			case "POPULATE-L3-BUG":
				for(Bugs bug : boxingService.getBug()) {
					  if(bug.getBugId() == Integer.valueOf(valueToProcess.split(",")[1])) {
						  populateBugsDB(print_writer, valueToProcess.split(",")[0], bug, match, session_selected_broadcaster);
					  }
					}
				break;
			case "POPULATE-MATCH_PROMO":
				populateMatchPromo(print_writer,valueToProcess.split(",")[0] , Integer.valueOf(valueToProcess.split(",")[1]), match, session_selected_broadcaster,boxingService.getFixture(),boxingService.getMatchType(),boxingService.getTeam(),boxingService.getAllPlayer());
				break;	
			case "POPULATE-FF-MATCHID":
				populateMatchId(print_writer,valueToProcess.split(",")[0], match, session_selected_broadcaster,boxingService.getFixture(),boxingService.getMatchType(),boxingService.getTeam());
				break;
			}
			
			case "PROMO_GRAPHICS-OPTIONS":
//				System.out.println(processAllFixtures(boxingService).get(0).getAway_Player().getFull_name());//
				return objectMapper.writeValueAsString(processAllFixtures(boxingService)).toString();
			case "NAMESUPER_CATEGORY-OPTIONS":
				return objectMapper.writeValueAsString(boxingService.getMatchType()).toString();
			case "BUG_GRAPHICS-OPTIONS":
				return objectMapper.writeValueAsString(boxingService.getBug()).toString();
			case "NAMESUPER_GRAPHICS-OPTIONS":
				return objectMapper.writeValueAsString(boxingService.getNameSupers()).toString();
			case "CATEGORY-OPTIONS":
				return objectMapper.writeValueAsString(boxingService.getMatchType()).toString();
			case "NAMESUPER-SP_GRAPHICS-OPTIONS": case "NAMESUPER-CATEGORY_GRAPHICS-OPTIONS": case "NAMESUPER-MEDAL_MANUAL_GRAPHICS-OPTIONS":
				return objectMapper.writeValueAsString(boxingService.getAllPlayer()).toString();
			
			case "ANIMATE-IN-SCOREBUG": case "ANIMATE-OUT-SCOREBUG": case "ANIMATE-IN-FFSCORELINE":
			case "ANIMATE-IN-NAMESUPERDB": case "ANIMATE-IN-MATCHID": case "ANIMATE-IN-NAMESUPER_SP":
			case "ANIMATE-IN-CATEGORY":	case "ANIMATE-IN-MATCH_PROMO": case "ANIMATE-IN-BUG":
			case "ANIMATE-IN-NAMESUPER-MEDAL_MANUAL":	
			case "CLEAR-ALL": case "ANIMATE-OUT":
				switch (whatToProcess.toUpperCase()) {
				case "ANIMATE-IN-SCOREBUG":
					processAnimation(print_writer, "In", "START", session_selected_broadcaster,1);
					TimeUnit.MILLISECONDS.sleep(200);
					print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*ScoreB_In START" + ";");
					which_graphics_onscreen = "SCOREBUG";
					is_infobar = true;
					scorebug.setScorebug_on_screen(true);
					break;
				case "ANIMATE-IN-FFSCORELINE":
					processAnimation(print_writer, "In", "START", session_selected_broadcaster,2);
					TimeUnit.MILLISECONDS.sleep(200);
					if(match.getSets() != null) {
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vSet01 " + (match.getSets().size()-1) + ";");
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vSet02 " + (match.getSets().size()-1) + ";");
					}
					which_graphics_onscreen = "FFSCORELINE";
					break;
				case "ANIMATE-IN-NAMESUPER-MEDAL_MANUAL":
					processAnimation(print_writer, "In", "START", session_selected_broadcaster,2);
					which_graphics_onscreen = "NAMESUPER-MEDAL_MANUAL";
					break;	
				case "ANIMATE-IN-BUG":
					processAnimation(print_writer, "In", "START", session_selected_broadcaster,2);
					which_graphics_onscreen = "BUG";
					break;
				case "ANIMATE-IN-CATEGORY":
					processAnimation(print_writer, "In", "START", session_selected_broadcaster,2);
					which_graphics_onscreen = "CATEGORY";
					break;
				case "ANIMATE-IN-MATCH_PROMO":
					processAnimation(print_writer, "In", "START", session_selected_broadcaster,2);
					which_graphics_onscreen = "MATCH_PROMO";
					break;
				case "ANIMATE-IN-NAMESUPERDB":
					processAnimation(print_writer, "In", "START", session_selected_broadcaster,2);
					which_graphics_onscreen = "NAMESUPERDB";
					break;
				case "ANIMATE-IN-NAMESUPER_SP":
					processAnimation(print_writer, "In", "START", session_selected_broadcaster,2);
					which_graphics_onscreen = "NAMESUPER_SP";
					break;
				case "ANIMATE-IN-MATCHID":
					processAnimation(print_writer, "In", "START", session_selected_broadcaster,2);
					which_graphics_onscreen = "MATCHID";
					break;
				case "CLEAR-ALL":
					print_writer.println("LAYER1*EVEREST*SINGLE_SCENE CLEAR;");
					print_writer.println("LAYER2*EVEREST*SINGLE_SCENE CLEAR;");
					which_graphics_onscreen = "";
					is_infobar = false;
					scorebug.setScorebug_on_screen(false);
					break;
					
				case "ANIMATE-OUT-SCOREBUG":
					if(is_infobar == true) {
						print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*ScoreB_Out START" + ";");
						TimeUnit.MILLISECONDS.sleep(200);
						processAnimation(print_writer, "Out", "START", session_selected_broadcaster,1);
						is_infobar = false;
						scorebug.setScorebug_on_screen(false);
					}
					break;
				case "ANIMATE-OUT":
					switch (which_graphics_onscreen) {
					case "MATCHID": case "FFSCORELINE": case "MATCH_PROMO":
						processAnimation(print_writer, "In", "CONTINUE", session_selected_broadcaster,2);
						which_graphics_onscreen = "";
						break;
					case "NAMESUPERDB": case "NAMESUPER_SP": case "CATEGORY": case "BUG": case "NAMESUPER-MEDAL_MANUAL":
						processAnimation(print_writer, "Out", "START", session_selected_broadcaster,2);
						which_graphics_onscreen = "";
						break;
					}
					break;
				}
			
			break;
		}
		return null;
	}
	
	public void processAnimation(PrintWriter print_writer, String animationName,String animationCommand, String which_broadcaster,int which_layer) throws IOException
	{
		switch(which_broadcaster.toUpperCase()) {
		case "DOAD_NATIONALS":
			switch(which_layer) {
			case 1:
				print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*" + animationName + " " + animationCommand + ";");
				break;
			case 2:
				print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*" + animationName + " " + animationCommand + ";");
				break;
			}
			break;
		}
	}
	
	public ScoreBug populateScoreBug(boolean is_this_updating,ScoreBug scorebug, PrintWriter print_writer,String viz_sence_path,Match match, String selectedbroadcaster,List<Team> tm,List<TeamColor> tc,List<Player> plyr) throws IOException
	{
		if (match == null) {
			this.status = "ERROR: Match is null";
		} else {
			if(is_this_updating == false) {
				
				if(match.getClock().getMatchHalves().equalsIgnoreCase("first")) {
					print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tRound " + "1st ROUND" + ";");

				}else if(match.getClock().getMatchHalves().equalsIgnoreCase("second")) {
					print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tRound " + "2nd ROUND" + ";");
				}else if(match.getClock().getMatchHalves().equalsIgnoreCase("third")) {
					print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tRound " + "3rd ROUND" + ";");
				}
				
				for(TeamColor teamcolor : tc) {
					if(match.getHomeTeamJerseyColor().equalsIgnoreCase(teamcolor.getColorType())) {
						print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tColor01 " + teamcolor.getRgb() + ";");
					}
					
					if(match.getAwayTeamJerseyColor().equalsIgnoreCase(teamcolor.getColorType())) {
						print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tColor02 " + teamcolor.getRgb() + ";");
					}
				}
				print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tMatchInfo " + match.getTournament() +";");
				
				print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerName01 " + match.getHomeFirstPlayer().getFull_name() +";");
				print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerName02 " + match.getAwayFirstPlayer().getFull_name() +";");
					
				print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tFrom01 " + tm.get(plyr.get(match.getHomeFirstPlayerId()).getTeamId() - 1).getTeamName4() +";");
				print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tFrom02r " + tm.get(plyr.get(match.getAwayFirstPlayerId()).getTeamId() - 1).getTeamName4() +";");
			}
			
			int seconds =0;
			long millis = match.getClock().getMatchTotalMilliSeconds(); 
			long minutes = (millis / 1000)  / 60;
			seconds = (int)((millis / 1000) % 60);
			
			if(seconds >=10 && seconds <= 99 || seconds>=-99 && seconds<=-10) {
				print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tTimer " + "0" + minutes + ":" + seconds +";");
			}else {
				print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tTimer " + "0" + minutes + ":" + "0" + seconds +";");
			}
//			print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSetPoint01A " + match.getHomeTeamSetScore() +";");
//			print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSetPoint02A " + match.getAwayTeamSetScore() +";");
		}	
			
		return scorebug;
	}
	public void populateFFScoreline(PrintWriter print_writer,String viz_sence_path, Match match,String selectedbroadcaster,List<Team> team,List<Player> player) throws IOException {
		if (match == null) {
			this.status = "ERROR: Match is null";
		} else {
			
			if(match.getSets() != null) {
				for(int i = 0; i<= match.getSets().size()-1; i++) {
					if(i==0) {
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSetPoint01A " + match.getSets().get(0).getHomeScore() + ";");
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSetPoint02A " + match.getSets().get(0).getAwayScore() + ";");
					}else if(i==1) {
//						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vSet01 1;");
//						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vSet02 1;");
						
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSetPoint01A " + match.getSets().get(0).getHomeScore() + ";");
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSetPoint02A " + match.getSets().get(0).getAwayScore() + ";");
						
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSetPoint01B " + match.getSets().get(1).getHomeScore() + ";");
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSetPoint02B " + match.getSets().get(1).getAwayScore() + ";");
					}else if(i==2) {
//						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vSet01 2;");
//						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vSet02 2;");
						
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSetPoint01A " + match.getSets().get(0).getHomeScore() + ";");
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSetPoint02A " + match.getSets().get(0).getAwayScore() + ";");
						
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSetPoint01B " + match.getSets().get(1).getHomeScore() + ";");
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSetPoint02B " + match.getSets().get(1).getAwayScore() + ";");
						
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSetPoint01C " + match.getSets().get(2).getHomeScore() + ";");
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSetPoint02C " + match.getSets().get(2).getAwayScore() + ";");
					}
					else if(i==3) {
//						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vSet01 2;");
//						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vSet02 2;");
						
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSetPoint01A " + match.getSets().get(0).getHomeScore() + ";");
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSetPoint02A " + match.getSets().get(0).getAwayScore() + ";");
						
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSetPoint01B " + match.getSets().get(1).getHomeScore() + ";");
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSetPoint02B " + match.getSets().get(1).getAwayScore() + ";");
						
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSetPoint01C " + match.getSets().get(2).getHomeScore() + ";");
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSetPoint02C " + match.getSets().get(2).getAwayScore() + ";");
						
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSetPoint01D " + match.getSets().get(3).getHomeScore() + ";");
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSetPoint02D " + match.getSets().get(3).getAwayScore() + ";");
						
//						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSetPoint01E " + match.getSets().get(4).getHomeScore() + ";");
//						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSetPoint02E " + match.getSets().get(4).getAwayScore() + ";");
					
					}
					else if(i==4) {
//						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vSet01 2;");
//						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vSet02 2;");
						
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSetPoint01A " + match.getSets().get(0).getHomeScore() + ";");
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSetPoint02A " + match.getSets().get(0).getAwayScore() + ";");
						
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSetPoint01B " + match.getSets().get(1).getHomeScore() + ";");
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSetPoint02B " + match.getSets().get(1).getAwayScore() + ";");
						
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSetPoint01C " + match.getSets().get(2).getHomeScore() + ";");
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSetPoint02C " + match.getSets().get(2).getAwayScore() + ";");
						
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSetPoint01D " + match.getSets().get(3).getHomeScore() + ";");
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSetPoint02D " + match.getSets().get(3).getAwayScore() + ";");
					
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSetPoint01E " + match.getSets().get(4).getHomeScore() + ";");
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSetPoint02E " + match.getSets().get(4).getAwayScore() + ";");
					
					}
					else if(i==5) {
//						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vSet01 2;");
//						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vSet02 2;");
						
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSetPoint01A " + match.getSets().get(0).getHomeScore() + ";");
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSetPoint02A " + match.getSets().get(0).getAwayScore() + ";");
						
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSetPoint01B " + match.getSets().get(1).getHomeScore() + ";");
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSetPoint02B " + match.getSets().get(1).getAwayScore() + ";");
						
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSetPoint01C " + match.getSets().get(2).getHomeScore() + ";");
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSetPoint02C " + match.getSets().get(2).getAwayScore() + ";");
						
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSetPoint01D " + match.getSets().get(3).getHomeScore() + ";");
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSetPoint02D " + match.getSets().get(3).getAwayScore() + ";");
					
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSetPoint01E " + match.getSets().get(4).getHomeScore() + ";");
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSetPoint02E " + match.getSets().get(4).getAwayScore() + ";");
					
					}
					
				}
			}
			
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgGameIcon " + icon_path + "BOXING" + 
					BoxingUtil.PNG_EXTENSION + ";");
			
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHeader " + "BOXING" + ";");
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tBottomInfo " + match.getVenue() + ";");

			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSubText01 " + match.getTournament() + ";");
			
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vVSOptions 0;");
			
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tTeam01 " + match.getHomeFirstPlayer().getFull_name() + ";");
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tTeam02 " + match.getAwayFirstPlayer().getFull_name() + ";");
			
			
			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW ON;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In STOP;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out STOP;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In SHOW 110.0;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT_PATH C:/Temp/Preview.png;");
			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT 1920 1080;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW OFF;");
		}
	}
	
	public void populateBugsDB(PrintWriter print_writer,String viz_scene, Bugs bug ,Match match, String session_selected_broadcaster) throws InterruptedException
	{
		if (match == null) {
			this.status = "ERROR: Match is null";
		} else {
			
			if(bug.getText1() != null && bug.getText2() != null) {
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHeader " + bug.getText1() +";");
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tDetails " + bug.getText2() +";");
			}else if(bug.getText1() != null) {
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHeader " + bug.getText1() +";");
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tDetails " + "" +";");
			}else {
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHeader " + bug.getText2() +";");
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tDetails " + "" +";");
			}
			
			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW ON;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In STOP;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out STOP;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In SHOW 32.0;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT_PATH C:/Temp/Preview.png;");
			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT 1920 1080;");
			TimeUnit.SECONDS.sleep(1);
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW OFF;");
				
		}
		
	}
	public void populateNameSuper(PrintWriter print_writer,String viz_scene, NameSuper ns ,Match match, String selectedbroadcaster) throws InterruptedException, IOException
	{
		if (match == null) {
			this.status = "ERROR: Match is null";
		} else {
			int l=500;
			if(ns.getFirstname() == null) {
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tName " + ns.getSurname().toUpperCase() +";");
				TimeUnit.MILLISECONDS.sleep(l);
			}else if(ns.getSurname() == null) {
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tName " + ns.getFirstname().toUpperCase() +";");
				TimeUnit.MILLISECONDS.sleep(l);
			}else {
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tName " + ns.getFirstname().toUpperCase() + " " + ns.getSurname().toUpperCase() +";");
				TimeUnit.MILLISECONDS.sleep(l);
			}
			
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgIcon " + icon_path + "BOXING" + 
					BoxingUtil.PNG_EXTENSION + ";");
			
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tOtherInfo " + " " +";");
			TimeUnit.MILLISECONDS.sleep(l);
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tDetails " + ns.getSubLine().toUpperCase() + ";");
			TimeUnit.MILLISECONDS.sleep(l);
			
			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW ON;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In STOP;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out STOP;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In SHOW 110.0;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT_PATH C:/Temp/Preview.png;");
			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT 1920 1080;");
			TimeUnit.SECONDS.sleep(1);
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW OFF;");
		}
	}
	public void populateNameSuperSP(PrintWriter print_writer, String viz_sence_path,int playerid,Match match,String selectedbroadcaster,List<Team> tm,List<Player> plyr) throws InterruptedException {
		
		if (match == null) {
			this.status = "ERROR: Match is null";
		} else {
			
			int l=500;
			
			if(playerid == match.getHomeFirstPlayerId()) {
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tName " + match.getHomeFirstPlayer().getFull_name() +";");
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tDetails " + tm.get(plyr.get(match.getHomeFirstPlayerId() - 1).getTeamId() -1).getTeamName1() + ";");
			}else if(playerid == match.getAwayFirstPlayerId()) {
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tName " + match.getAwayFirstPlayer().getFull_name() +";");
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tDetails " + tm.get(plyr.get(match.getAwayFirstPlayerId() - 1).getTeamId() -1).getTeamName1() + ";");
			}
			
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgIcon " + icon_path + "BOXING" + 
					BoxingUtil.PNG_EXTENSION + ";");
			
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tOtherInfo " + " " +";");
			TimeUnit.MILLISECONDS.sleep(l);
			
			TimeUnit.MILLISECONDS.sleep(l);
			
			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW ON;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In STOP;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out STOP;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In SHOW 110.0;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT_PATH C:/Temp/Preview.png;");
			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT 1920 1080;");
			TimeUnit.SECONDS.sleep(1);
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW OFF;");
		}
	}
	public void populateMatchId(PrintWriter print_writer,String viz_scene, Match match, String session_selected_broadcaster,List<Fixture> fix,List<MatchType> mt,List<Team> tm) throws InterruptedException, IOException
	{
		if (match == null) {
			this.status = "ERROR: Match is null";
		}else {
			int l = 4;
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHeader " + "BOXING" + ";");
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vSubHeader 2;");
			
			for(Fixture fx : fix) {
				if(fx.getHomeplayerid() == match.getHomeFirstPlayerId() && fx.getAwayplayerid() == match.getAwayFirstPlayerId()) {
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSubText01 " + mt.get(fx.getWeightCategory() - 1).getMatchTypeName() + ";");
				}
			}
			
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgGameIcon " + icon_path + "BOXING" + 
					BoxingUtil.PNG_EXTENSION + ";");
			
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vVSOptions 1;");

			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerInfo01 " + "" + ";");
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerInfo02 " + "" + ";");
			
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerName01A " + match.getHomeFirstPlayer().getFull_name() + ";");
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerName02A " + match.getAwayFirstPlayer().getFull_name() + ";");
			
			
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerName01B " + tm.get(match.getHomeFirstPlayer().getTeamId() -1).getTeamName4() + ";");
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerName02B " + tm.get(match.getAwayFirstPlayer().getTeamId() -1).getTeamName4() + ";");
			
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tBottomInfo " + match.getMatchIdent() + ", " + match.getVenue() + ";");
			
			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW ON;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In STOP;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out STOP;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In SHOW 110.0;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT_PATH C:/Temp/Preview.png;");
			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT 1920 1080;");
			TimeUnit.SECONDS.sleep(1);
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW OFF;");
		}
	}
	public void populateCategory(PrintWriter print_writer,String viz_sence_path,int playerId,String category, Match match,String selectedbroadcaster,List<Team> team,List<Player> player,List<MatchType> mt) throws IOException {
		if (match == null) {
			this.status = "ERROR: Match is null";
		}else {
			int l=500;
			
			for(Player plyr : player ) {
				if(plyr.getPlayerId() == playerId) {
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tName " + plyr.getFull_name().toUpperCase() +";");
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tDetails " + team.get(plyr.getTeamId() - 1).getTeamName1() +";");
				}
			}
			
			for(MatchType mtchType : mt) {
				if(mtchType.getMatchTypeName().equalsIgnoreCase(category)) {
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tOtherInfo " + mtchType.getMatchTypeName() +";");
				}
			}
			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW ON;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In STOP;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out STOP;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In SHOW 110.0;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT_PATH C:/Temp/Preview.png;");
			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT 1920 1080;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW OFF;");
		}
	}
	public void populateNameSuperMedalManual(PrintWriter print_writer, String viz_sence_path,int playerid,int playerid2,int playerid3,int playerid4,String category3,Match match,String selectedbroadcaster,List<Player> plyr,List<Team> tm,List<MatchType> cat) throws InterruptedException {
		
		if (match == null) {
			this.status = "ERROR: Match is null";
		} else {
			
			int l=500;
			
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tName " + "BOXING" +";");

			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tOtherInfo " + category3.toUpperCase() +";");

//			for(MatchType category : cat) {
//				if(category.getMatchTypeId() == category3) {
//					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tOtherInfo " + category3.toUpperCase() +";");
//				}
//			}
			
			for(Player plyrs : plyr) {
				if(plyrs.getPlayerId() == playerid4) {
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayer04 " + plyrs.getFull_name().toUpperCase() +";");
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tTeam04 " + tm.get(plyr.get(plyrs.getPlayerId() -1).getTeamId() -1).getTeamName4().toUpperCase() +";");
				}
				
				if(plyrs.getPlayerId() == playerid3) {
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayer03 " + plyrs.getFull_name().toUpperCase() +";");
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tTeam03 " + tm.get(plyr.get(plyrs.getPlayerId() -1).getTeamId() -1).getTeamName4().toUpperCase() +";");
				}
				
				if(plyrs.getPlayerId() == playerid2) {
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayer02 " + plyrs.getFull_name().toUpperCase() +";");
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tTeam02 " + tm.get(plyr.get(plyrs.getPlayerId() -1).getTeamId() -1).getTeamName4().toUpperCase() +";");
				}
				
				if(plyrs.getPlayerId() == playerid) {
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayer01 " + plyrs.getFull_name().toUpperCase() +";");
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tTeam01 " + tm.get(plyr.get(plyrs.getPlayerId() -1).getTeamId() -1).getTeamName4().toUpperCase() +";");
				}
			}
			
//			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tDetails " + match.getCategoryType().toUpperCase() + " " + 
//					match.getWrestlingType().toUpperCase() + " " + match.getMatchType().toUpperCase() + ";");
			TimeUnit.MILLISECONDS.sleep(l);
			
			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW ON;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In STOP;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out STOP;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In SHOW 90;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT_PATH C:/Temp/Preview.png;");
			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT 1920 1080;");
			TimeUnit.SECONDS.sleep(1);
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW OFF;");
		}
	}
	public void populateMatchPromo(PrintWriter print_writer,String viz_scene, int match_number,Match match, String session_selected_broadcaster,List<Fixture> fix,List<MatchType> mt,List<Team> tm,List<Player> plyr) throws InterruptedException, IOException
	{
		if (match == null) {
			this.status = "ERROR: Match is null";
		}else {
			int l = 4;
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHeader " + "BOXING" + ";");
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vSubHeader 2;");
			
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vVSOptions 1;");
			
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerInfo01 " + "" + ";");
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerInfo02 " + "" + ";");
			
			for(Fixture fx : fix) {
				if(fx.getMatchnumber() == match_number) {
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSubText01 " + mt.get(fx.getWeightCategory() - 1).getMatchTypeName() + ";");
					
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerName01A " + plyr.get(fx.getHomeplayerid() - 1).getFull_name() + ";");
					print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerName02A " + plyr.get(fx.getAwayplayerid() - 1).getFull_name() + ";");
					
					
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerName01B " + tm.get(plyr.get(fx.getHomeplayerid() - 1).getTeamId() - 1).getTeamName4()  + ";");
						print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerName02B " + tm.get(plyr.get(fx.getAwayplayerid() - 1).getTeamId() - 1).getTeamName4() + ";");
					
					
				}
			}
			
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgGameIcon " + icon_path + "BOXING" + 
					BoxingUtil.PNG_EXTENSION + ";");
			
			
			
			print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tBottomInfo " + "COMING UP NEXT" + ";");
			
			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW ON;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In STOP;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out STOP;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In SHOW 110.0;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT_PATH C:/Temp/Preview.png;");
			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT 1920 1080;");
			TimeUnit.SECONDS.sleep(1);
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW OFF;");
		}
	}
	public static List<Fixture> processAllFixtures(BoxingService boxingService) {
		List<Fixture> fixtures = boxingService.getFixture();
		for(Player tm : boxingService.getAllPlayer()) {
			for(Fixture fx : fixtures) {
				if(fx.getHomeplayerid() == tm.getPlayerId()) {
					fx.setHome_Player(tm);
				}
				if(fx.getAwayplayerid() == tm.getPlayerId()) {
					fx.setAway_Player(tm);
				}
			}
		}
		return fixtures;
	}
}
