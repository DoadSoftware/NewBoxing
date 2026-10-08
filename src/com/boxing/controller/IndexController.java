package com.boxing.controller;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import com.boxing.model.Clock;
import com.boxing.model.Configurations;
import com.boxing.model.Event;
import com.boxing.model.EventFile;
import com.boxing.model.Match;
import com.boxing.model.Set;
import com.boxing.service.BoxingService;
import com.boxing.util.BoxingFunctions;
import com.boxing.util.BoxingUtil;
import com.boxing.broadcaster.DOAD_BOXING;
import com.boxing.broadcaster.DOAD_NATIONALS;
import com.boxing.containers.Scene;
import com.boxing.containers.ScoreBug;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class IndexController {

    @Autowired
    BoxingService boxingService;

    public static String expiry_date = "2025-12-31";
    public static String current_date = "";
    public static String error_message = "";
    public static DOAD_BOXING this_boxing;
    public static DOAD_NATIONALS this_boxing_nationals;
    public static Match session_match = new Match();
    public static EventFile session_event = new EventFile();
    public static Clock session_clock = new Clock();
    public static Configurations session_Configurations = new Configurations();
	public ObjectMapper objectMapper = new ObjectMapper(); 
    List<Scene> session_selected_scenes = new ArrayList<Scene>();
    public static String session_selected_broadcaster;
    public static Socket session_socket;
    public static PrintWriter print_writer;

    @RequestMapping(value = {"/", "/initialise"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String initialisePage(ModelMap model) throws JAXBException, IOException, ParseException {
        if (current_date == null || current_date.isEmpty()) {
            current_date = BoxingFunctions.getOnlineCurrentDate();
        }

        model.addAttribute("session_viz_scenes", new File(BoxingUtil.BOXING_DIRECTORY + BoxingUtil.SCENES_DIRECTORY).listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                String name = pathname.getName().toLowerCase();
                return name.endsWith(".via") && pathname.isFile();
            }
        }));

        model.addAttribute("match_files", new File(BoxingUtil.BOXING_DIRECTORY + BoxingUtil.MATCHES_DIRECTORY).listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                String name = pathname.getName().toLowerCase();
                return name.endsWith(".json") && pathname.isFile();
            }
        }));

        if (new File(BoxingUtil.BOXING_DIRECTORY + BoxingUtil.CONFIGURATIONS_DIRECTORY + BoxingUtil.OUTPUT_XML).exists()) {
            session_Configurations = (Configurations) JAXBContext.newInstance(Configurations.class).createUnmarshaller().unmarshal(new File(BoxingUtil.BOXING_DIRECTORY + BoxingUtil.CONFIGURATIONS_DIRECTORY + BoxingUtil.OUTPUT_XML));
        } else {
            session_Configurations = new Configurations();
            JAXBContext.newInstance(Configurations.class).createMarshaller().marshal(session_Configurations, new File(BoxingUtil.BOXING_DIRECTORY + BoxingUtil.CONFIGURATIONS_DIRECTORY + BoxingUtil.OUTPUT_XML));
        }

        model.addAttribute("session_Configurations", session_Configurations);
        return "initialise";
    }

    @RequestMapping(value = {"/setup"}, method = RequestMethod.POST)
    public String setupPage(ModelMap model) throws JAXBException, IllegalAccessException, InvocationTargetException, IOException, ParseException {
        model.addAttribute("match_files", new File(BoxingUtil.BOXING_DIRECTORY + BoxingUtil.MATCHES_DIRECTORY).listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                String name = pathname.getName().toLowerCase();
                return name.endsWith(".json") && pathname.isFile();
            }
        }));

        model.addAttribute("session_match", session_match);
        model.addAttribute("players", boxingService.getAllPlayer());
        model.addAttribute("teamcolor", boxingService.getTeamColors());
        model.addAttribute("matchTypes", boxingService.getMatchType());
        model.addAttribute("licence_expiry_message", "Software licence expires on " + new SimpleDateFormat("E, dd MMM yyyy").format(new SimpleDateFormat("yyyy-MM-dd").parse(expiry_date)));
        return "setup";
    }

    @RequestMapping(value = {"/stats_selection"}, method = RequestMethod.POST)
    public String statsSelectionPage(ModelMap model) {
        model.addAttribute("session_match", session_match);
        return "stat";
    }

    @RequestMapping(value = {"/match"}, method = {RequestMethod.POST, RequestMethod.GET})
    public String boxingMatchPage(ModelMap model, @RequestParam(value = "selectedBroadcaster", required = false, defaultValue = "") String selectedBroadcaster, @RequestParam(value = "vizIPAddress", required = false, defaultValue = "") String vizIPAddresss, @RequestParam(value = "vizPortNumber", required = false, defaultValue = "") String vizPortNumber, @RequestParam(value = "vizScene", required = false, defaultValue = "") String vizScene) throws IOException, ParseException, JAXBException, InterruptedException {
        if (current_date == null || current_date.isEmpty()) {
            model.addAttribute("error_message", "You must be connected to the internet online");
            return "error";
        } else if (new SimpleDateFormat("yyyy-MM-dd").parse(expiry_date).before(new SimpleDateFormat("yyyy-MM-dd").parse(current_date))) {
            model.addAttribute("error_message", "This software has expired");
            return "error";
        } else {
            model.addAttribute("match_files", new File(BoxingUtil.BOXING_DIRECTORY + BoxingUtil.MATCHES_DIRECTORY).listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    String name = pathname.getName().toLowerCase();
                    return name.endsWith(".json") && pathname.isFile();
                }
            }));

            model.addAttribute("licence_expiry_message", "Software licence expires on " + new SimpleDateFormat("E, dd MMM yyyy").format(new SimpleDateFormat("yyyy-MM-dd").parse(expiry_date)));

            session_match = new Match();
            session_event = new EventFile();

            if (session_event.getEvents() == null || session_event.getEvents().size() <= 0) {
                session_event.setEvents(new ArrayList<Event>());
            }

            if (session_match.getSets() == null || session_match.getSets().size() <= 0) {
                session_match.setSets(new ArrayList<Set>());
            }

            session_Configurations.setBroadcaster(selectedBroadcaster);
            session_Configurations.setPrimaryIpAddress(vizIPAddresss);

            if (!vizPortNumber.trim().isEmpty()) {
                session_Configurations.setPrimaryPortNumber(Integer.valueOf(vizPortNumber));
                session_socket = new Socket(vizIPAddresss, Integer.valueOf(vizPortNumber));
                print_writer = new PrintWriter(session_socket.getOutputStream(), true);
            }

            session_selected_broadcaster = selectedBroadcaster;

            switch (session_selected_broadcaster.toUpperCase()) {
                case BoxingUtil.DOAD_BOXING:
                    session_selected_scenes.add(new Scene("D:/DOAD_In_House_Everest/Everest_Sports/Everest_Khelo_India_2023/Scenes/Boxing_Scorebug.sum", BoxingUtil.ONE));
                    session_selected_scenes.add(new Scene("", BoxingUtil.TWO));
                    session_selected_scenes.get(0).scene_load(print_writer, session_selected_broadcaster);
                    this_boxing = new DOAD_BOXING();
                    this_boxing.scorebug = new ScoreBug();
                    break;
                case BoxingUtil.DOAD_NATIONALS:
                    session_selected_scenes.add(new Scene("D:/DOAD_In_House_Everest/Everest_Sports/Everest_Khelo_India_2023/Scenes/Boxing_Scorebug.sum", BoxingUtil.ONE));
                    session_selected_scenes.add(new Scene("", BoxingUtil.TWO));
                    session_selected_scenes.get(0).scene_load(print_writer, session_selected_broadcaster);
                    this_boxing_nationals = new DOAD_NATIONALS();
                    this_boxing_nationals.scorebug = new ScoreBug();
                    break;
            }

            JAXBContext.newInstance(Configurations.class).createMarshaller().marshal(session_Configurations, new File(BoxingUtil.BOXING_DIRECTORY + BoxingUtil.CONFIGURATIONS_DIRECTORY + BoxingUtil.OUTPUT_XML));
            model.addAttribute("session_selected_broadcaster", session_selected_broadcaster);
            return "match";
        }
    }

    @RequestMapping(value = {"/setup_to_match", "/stat_to_match"}, method = RequestMethod.POST)
    public String backToMatchPage(ModelMap model) throws ParseException {
        if (current_date == null || current_date.isEmpty()) {
            model.addAttribute("error_message", "You must be connected to the internet online");
            return "error";
        } else if (new SimpleDateFormat("yyyy-MM-dd").parse(expiry_date).before(new SimpleDateFormat("yyyy-MM-dd").parse(current_date))) {
            model.addAttribute("error_message", "This software has expired");
            return "error";
        } else {
            model.addAttribute("match_files", new File(BoxingUtil.BOXING_DIRECTORY + BoxingUtil.MATCHES_DIRECTORY).listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    String name = pathname.getName().toLowerCase();
                    return name.endsWith(".json") && pathname.isFile();
                }
            }));

            model.addAttribute("licence_expiry_message", "Software licence expires on " + new SimpleDateFormat("E, dd MMM yyyy").format(new SimpleDateFormat("yyyy-MM-dd").parse(expiry_date)));
            model.addAttribute("session_selected_broadcaster", session_selected_broadcaster);
            return "match";
        }
    }

    @RequestMapping(value = {"/upload_match_setup_data", "/reset_and_upload_match_setup_data"}, method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String uploadFormDataToSessionObjects(MultipartHttpServletRequest request) throws IllegalAccessException, InvocationTargetException, JAXBException, IOException {
        if (request.getRequestURI().contains("upload_match_setup_data") || request.getRequestURI().contains("reset_and_upload_match_setup_data")) {
            boolean reset_all_variables = false;

            if (request.getRequestURI().contains("reset_and_upload_match_setup_data")) {
                reset_all_variables = true;
            } else if (request.getRequestURI().contains("upload_match_setup_data")) {
                for (Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
                    if (entry.getKey().equalsIgnoreCase("select_existing_boxing_matches") && entry.getValue()[0].equalsIgnoreCase("new_match")) {
                        reset_all_variables = true;
                        break;
                    }
                }
            }

            if (reset_all_variables) {
                session_match = new Match();
                session_event = new EventFile();
                session_event.setEvents(new ArrayList<Event>());
            }

            for (Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
            	objectMapper.updateValue(session_match, Collections.singletonMap(entry.getKey(), entry.getValue()[0]));
            }

            if (new File(BoxingUtil.BOXING_DIRECTORY + BoxingUtil.MATCHES_DIRECTORY + session_match.getMatchFileName()).exists()) {
                session_match = new ObjectMapper().readValue(new File(BoxingUtil.BOXING_DIRECTORY + BoxingUtil.MATCHES_DIRECTORY + session_match.getMatchFileName()), Match.class);
                session_event = new ObjectMapper().readValue(new File(BoxingUtil.BOXING_DIRECTORY + BoxingUtil.EVENT_DIRECTORY + session_match.getMatchFileName()), EventFile.class);
                session_match = BoxingFunctions.populateMatchVariables(boxingService, session_match);
            } else {
                session_match = BoxingFunctions.populateMatchVariables(boxingService, session_match);
                new ObjectMapper().writeValue(new File(BoxingUtil.BOXING_DIRECTORY + BoxingUtil.MATCHES_DIRECTORY + session_match.getMatchFileName()), session_match);
                new ObjectMapper().writeValue(new File(BoxingUtil.BOXING_DIRECTORY + BoxingUtil.MATCHES_DIRECTORY + session_match.getMatchFileName()), session_match);
            }
        }

        session_match.setEvents(session_event.getEvents());
        return objectMapper.writeValueAsString(session_match).toString();
    }

    @RequestMapping(value = {"/processBoxingProcedures"}, method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String processBoxingProcedures(
    		@RequestParam(value = "whatToProcess", required = false, defaultValue = "") String whatToProcess, 
    		@RequestParam(value = "valueToProcess", required = false, defaultValue = "") String valueToProcess) 
    		throws JAXBException, IllegalAccessException, InvocationTargetException, IOException, NumberFormatException, InterruptedException {
        switch (whatToProcess.toUpperCase()) {
            case "NAMESUPER_GRAPHICS-OPTIONS":
            case "NAMESUPER-SP_GRAPHICS-OPTIONS":
            case "BUG_GRAPHICS-OPTIONS":
            case "NAMESUPER_CATEGORY-OPTIONS":
            case "NAMESUPER-CATEGORY_GRAPHICS-OPTIONS":
            case "PROMO_GRAPHICS-OPTIONS":
            case "NAMESUPER-MEDAL_MANUAL_GRAPHICS-OPTIONS":
            case "CATEGORY-OPTIONS":
                switch (session_selected_broadcaster) {
                    case "DOAD_NATIONALS":
                        return this_boxing_nationals.ProcessGraphicOption(whatToProcess, session_match, boxingService, print_writer, session_selected_scenes, valueToProcess).toString();
                    case "DOAD_BOXING":
                        return this_boxing.ProcessGraphicOption(whatToProcess, session_match, boxingService, print_writer, session_selected_scenes, valueToProcess).toString();
                }
                break;

            case BoxingUtil.READ_CLOCK:
                if (new File(BoxingUtil.BOXING_DIRECTORY + BoxingUtil.CLOCK_JSON).exists()) {
                    session_clock = new ObjectMapper().readValue(new File(BoxingUtil.BOXING_DIRECTORY + BoxingUtil.CLOCK_JSON), Clock.class);
                    session_match.setClock(session_clock);

                    switch (session_selected_broadcaster) {
                        case BoxingUtil.DOAD_BOXING:
                            this_boxing.updateScoreBug(session_selected_scenes, session_match, print_writer, boxingService);
                            break;
                        case BoxingUtil.DOAD_NATIONALS:
                            this_boxing_nationals.updateScoreBug(session_selected_scenes, session_match, print_writer, boxingService);
                            break;
                    }
                }

                return objectMapper.writeValueAsString(session_match).toString();

            case BoxingUtil.LOAD_MATCH:
            case BoxingUtil.LOAD_SETUP:
                session_match = new ObjectMapper().readValue(new File(BoxingUtil.BOXING_DIRECTORY + BoxingUtil.MATCHES_DIRECTORY + valueToProcess), Match.class);
                session_match = BoxingFunctions.populateMatchVariables(boxingService, session_match);
                return objectMapper.writeValueAsString(session_match).toString();

            case BoxingUtil.LOG_SET:
                if (session_match.getSets() == null) {
                    session_match.setSets(new ArrayList<Set>());
                }

                if (valueToProcess.toUpperCase().contains(BoxingUtil.START)) {
                    session_match.getSets().add(new Set(session_match.getSets().size() + 1, BoxingUtil.START));
                } else if (valueToProcess.toUpperCase().contains(BoxingUtil.RESET)) {
                    session_match.getSets().remove(session_match.getSets().size() - 1);
                } else if (valueToProcess.toUpperCase().contains(BoxingUtil.END)) {
                    session_match.getSets().get(session_match.getSets().size() - 1).setSet_status(BoxingUtil.END);
                    session_match.getSets().get(session_match.getSets().size() - 1).setSet_winner(valueToProcess.split(",")[1]);

                    if (valueToProcess.split(",")[1].equalsIgnoreCase(BoxingUtil.HOME)) {
                        session_match.setHomeTeamSetScore(session_match.getHomeTeamSetScore() + 1);
                    } else if (valueToProcess.split(",")[1].equalsIgnoreCase(BoxingUtil.AWAY)) {
                        session_match.setAwayTeamSetScore(session_match.getAwayTeamSetScore() + 1);
                    }
                }

                new ObjectMapper().writeValue(new File(BoxingUtil.BOXING_DIRECTORY + BoxingUtil.MATCHES_DIRECTORY + session_match.getMatchFileName()), session_match);
                return objectMapper.writeValueAsString(session_match).toString();

            case BoxingUtil.LOG_SCORE:
                if (valueToProcess.toUpperCase().contains(BoxingUtil.POINTS)) {
                    if (valueToProcess.toUpperCase().contains(BoxingUtil.HOME)) {
                        if (valueToProcess.toUpperCase().contains(BoxingUtil.INCREMENT)) {
                            switch (valueToProcess.split("_")[2]) {
                                case BoxingUtil.ONE:
                                case BoxingUtil.TWO:
                                case BoxingUtil.THREE:
                                case BoxingUtil.FOUR:
                                case BoxingUtil.FIVE:
                                    session_match.getSets().get(session_match.getSets().size() - 1).setHomeScore(session_match.getSets().get(session_match.getSets().size() - 1).getHomeScore() + Integer.valueOf(valueToProcess.split("_")[2]));
                                    break;
                            }
                        } else if (valueToProcess.toUpperCase().contains(BoxingUtil.DECREMENT)) {
                            switch (valueToProcess.split("_")[2]) {
                                case BoxingUtil.ONE:
                                case BoxingUtil.TWO:
                                case BoxingUtil.THREE:
                                case BoxingUtil.FOUR:
                                case BoxingUtil.FIVE:
                                    session_match.getSets().get(session_match.getSets().size() - 1).setHomeScore(session_match.getSets().get(session_match.getSets().size() - 1).getHomeScore() - Integer.valueOf(valueToProcess.split("_")[2]));
                                    break;
                            }
                        }
                    } else if (valueToProcess.toUpperCase().contains(BoxingUtil.AWAY)) {
                        if (valueToProcess.toUpperCase().contains(BoxingUtil.INCREMENT)) {
                            switch (valueToProcess.split("_")[2]) {
                                case BoxingUtil.ONE:
                                case BoxingUtil.TWO:
                                case BoxingUtil.THREE:
                                case BoxingUtil.FOUR:
                                case BoxingUtil.FIVE:
                                    session_match.getSets().get(session_match.getSets().size() - 1).setAwayScore(session_match.getSets().get(session_match.getSets().size() - 1).getAwayScore() + Integer.valueOf(valueToProcess.split("_")[2]));
                                    break;
                            }
                        } else if (valueToProcess.toUpperCase().contains(BoxingUtil.DECREMENT)) {
                            switch (valueToProcess.split("_")[2]) {
                                case BoxingUtil.ONE:
                                case BoxingUtil.TWO:
                                case BoxingUtil.THREE:
                                case BoxingUtil.FOUR:
                                case BoxingUtil.FIVE:
                                    session_match.getSets().get(session_match.getSets().size() - 1).setAwayScore(session_match.getSets().get(session_match.getSets().size() - 1).getAwayScore() - Integer.valueOf(valueToProcess.split("_")[2]));
                                    break;
                            }
                        }
                    }
                }

                new ObjectMapper().writeValue(new File(BoxingUtil.BOXING_DIRECTORY + BoxingUtil.MATCHES_DIRECTORY + session_match.getMatchFileName()), session_match);
                return objectMapper.writeValueAsString(session_match).toString();

            default:
                switch (session_selected_broadcaster) {
                    case BoxingUtil.DOAD_BOXING:
                        this_boxing.ProcessGraphicOption(whatToProcess, session_match, boxingService, print_writer, session_selected_scenes, valueToProcess);
                        break;
                    case BoxingUtil.DOAD_NATIONALS:
                        this_boxing_nationals.ProcessGraphicOption(whatToProcess, session_match, boxingService, print_writer, session_selected_scenes, valueToProcess);
                        break;
                }

                return objectMapper.writeValueAsString(session_match).toString();
        }
		return null;
    }
}