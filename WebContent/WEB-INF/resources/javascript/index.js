var match_data,home_team_id,away_team_id;
function processWaitingButtonSpinner(whatToProcess) 
{
	switch (whatToProcess) {
	case 'START_WAIT_TIMER': 
		$('.spinner-border').show();
		$(':button').prop('disabled', true);
		break;	
	case 'END_WAIT_TIMER': 
		$('.spinner-border').hide();
		$(':button').prop('disabled', false);
		break;
	}
}
function afterPageLoad(whichPageHasLoaded)
{
	switch (whichPageHasLoaded) {
	case 'SETUP':
		$('#homeFirstPlayerId').select2();
		$('#awayFirstPlayerId').select2();
		$('#homeTeamJerseyColor').select2();
		$('#awayTeamJerseyColor').select2();
		break;
	case 'MATCH':
		if(window.location.href.toLowerCase().includes('/stat_to_match')) {
			processBoxingProcedures('LOAD_MATCH_AFTER_STAT_LOG',null);
		}
		break;
	}
}
function millisToMinutesAndSeconds(millis) {
  var m = Math.floor(millis / 60000);
  var s = ((millis % 60000) / 1000).toFixed(0);
  return (m < 10 ? '0' + m : m) + ":" + (s < 10 ? '0' + s : s);
}
function displayMatchTime() {
	processBoxingProcedures('READ_CLOCK',null);
}
function initialiseForm(whatToProcess, dataToProcess)
{
	switch (whatToProcess) {
	case 'TIME':
		if(match_data) {
			if(document.getElementById('match_time_hdr')) {
				document.getElementById('match_time_hdr').innerHTML = 'MATCH TIME : ' + 
					millisToMinutesAndSeconds(match_data.clock.matchTotalMilliSeconds);
			}
		}
		
		break;
	
	case 'SETUP':
		
		if(dataToProcess) {
			document.getElementById('matchFileName').value = dataToProcess.matchFileName;
			document.getElementById('tournament').value = dataToProcess.tournament;
			document.getElementById('matchIdent').value = dataToProcess.matchIdent;
			document.getElementById('venue').value = dataToProcess.venue;
			document.getElementById('homeTeamJerseyColor').value = dataToProcess.homeTeamJerseyColor;
			document.getElementById('awayTeamJerseyColor').value = dataToProcess.awayTeamJerseyColor;
			document.getElementById('categoryType').value = dataToProcess.categoryType;
			document.getElementById('boxingType').value = dataToProcess.boxingType;
			document.getElementById('matchType').value = dataToProcess.matchType;
			document.getElementById('homeFirstPlayerId').value = dataToProcess.homeFirstPlayerId;
			document.getElementById('awayFirstPlayerId').value = dataToProcess.awayFirstPlayerId;
		} else {
			document.getElementById('matchFileName').value = '';
			document.getElementById('tournament').value = '';
			document.getElementById('matchIdent').value = '';
			document.getElementById('venue').value = '';
			document.getElementById('homeTeamJerseyColor').selectedIndex = 0;
			document.getElementById('awayTeamJerseyColor').selectedIndex = 1;
			document.getElementById('categoryType').selectedIndex = 0;
			document.getElementById('boxingType').selectedIndex = 0;
			document.getElementById('matchType').selectedIndex = 0;
			document.getElementById('homeFirstPlayerId').selectedIndex = 0;
			document.getElementById('awayFirstPlayerId').selectedIndex = 0;
		}
		$('#homeTeamJerseyColor').prop('selectedIndex', document.getElementById('homeTeamJerseyColor').options.selectedIndex).change();
		$('#awayTeamJerseyColor').prop('selectedIndex', document.getElementById('awayTeamJerseyColor').options.selectedIndex).change();
		
		//afterPageLoad('SETUP');
		break;
	}
}
function uploadFormDataToSessionObjects(whatToProcess)
{
	var formData = new FormData();
	var url_path;

	$('input, select, textarea').each(
		function(index){  
			if($(this).is("select")) {
				formData.append($(this).attr('id'),$('#' + $(this).attr('id') + ' option:selected').val());  
			} else {
				formData.append($(this).attr('id'),$(this).val());  
			}	
		}
	);
	
	switch(whatToProcess.toUpperCase()) {
	case 'RESET_MATCH':
		url_path = 'reset_and_upload_match_setup_data';
		break;
	case 'SAVE_MATCH':
		url_path = 'upload_match_setup_data';
		break;
	}
	
	$.ajax({    
		headers: {'X-CSRF-TOKEN': $('meta[name="_csrf"]').attr('content')},
        url : url_path,     
        data : formData,
        cache: false,
        contentType: false,
        processData: false,
        type: 'POST',     
        success : function(data) {

        	switch(whatToProcess.toUpperCase()) {
        	case 'RESET_MATCH':
        		alert('Match has been reset');
        		processWaitingButtonSpinner('END_WAIT_TIMER');
        		break;
        	case 'SAVE_MATCH':
        		document.setup_form.method = 'post';
        		document.setup_form.action = 'setup_to_match';
        	   	document.setup_form.submit();
        		break;
        	}
        	
        },    
        error : function(e) {    
       	 	console.log('Error occured in uploadFormDataToSessionObjects with error description = ' + e);     
        }    
    });		
	
}
function processUserSelectionData(whatToProcess,dataToProcess){
	//alert(whatToProcess);
	switch (whatToProcess) {
	case 'LOGGER_FORM_KEYPRESS':
		if($('#log_game_undo_btn').val()) { // Ignore keypress when user is working with UNDO
			return false;
		}
		switch (dataToProcess) {
		case 32:
			processBoxingProcedures('CLEAR-ALL');
			break;
		case 189:
			if(confirm('It will Also Delete Your Preview from Directory...\r\n\r\n Are You Sure To Animate Out?') == true){
				processBoxingProcedures('ANIMATE-OUT');
			}
			break;
		case 87:
			processBoxingProcedures('POPULATE-FF-SCORELINE');
			break;		
		case 187:
			processBoxingProcedures('ANIMATE-OUT-SCOREBUG');
			break;
		case 112:
			processBoxingProcedures('POPULATE-SCOREBUG');
			break;
		case 113:
			$("#select_event_div").hide();
			$("#match_configuration").hide();
			$("#boxing_div").hide();
			processBoxingProcedures('NAMESUPER_GRAPHICS-OPTIONS');
			break;
		case 73:
			$("#select_event_div").hide();
			$("#match_configuration").hide();
			$("#boxing_div").hide();
			processBoxingProcedures('NAMESUPER-CATEGORY_GRAPHICS-OPTIONS');
			break;	
		case 114:
			$("#select_event_div").hide();
			$("#match_configuration").hide();
			$("#boxing_div").hide();
			home_team_id = match_data.homeFirstPlayerId;
			away_team_id = match_data.awayFirstPlayerId;
			processBoxingProcedures('NAMESUPER-SP_GRAPHICS-OPTIONS');
			break;
		case 115:
			processBoxingProcedures('POPULATE-FF-MATCHID');
			break;
		case 117:
			$("#select_event_div").hide();
			$("#match_configuration").hide();
			$("#boxing_div").hide();
			processBoxingProcedures('BUG_GRAPHICS-OPTIONS');
			break;
		case 119:
			$("#select_event_div").hide();
			$("#match_configuration").hide();
			$("#boxing_div").hide();
			processBoxingProcedures('PROMO_GRAPHICS-OPTIONS');
			break;	
		case 120:
			$("#select_event_div").hide();
			$("#match_configuration").hide();
			$("#boxing_div").hide();
			processBoxingProcedures('NAMESUPER-MEDAL_MANUAL_GRAPHICS-OPTIONS');
			break;			
				
		}
		
		break;
	}
}
function processUserSelection(whichInput)
{	
	var error_msg = '';

	switch ($(whichInput).attr('name')) {
	case 'start_set_btn': 
		match_data.sets.forEach(function(set,set_index,set_arr){
			if(set.set_status.toLowerCase() == 'start') {
				error_msg = 'Game number ' + set.set_number + ' already in play. End this set first before starting a new one';
			}
		});
		if(error_msg) {
			alert(error_msg);
			return false;			
		} else {
			processWaitingButtonSpinner('START_WAIT_TIMER');
			processBoxingProcedures('LOG_SET','START');
		}
		break;
	case 'end_set_btn': case 'reset_set_btn':
		error_msg = 'Cannot find any started game. Please start a game first';
		match_data.sets.forEach(function(set,set_index,set_arr){
			if(set.set_status.toLowerCase() == 'start') {
				error_msg = '';
			}
		});
		if(error_msg) {
			alert(error_msg);
			return false;			
		} else {
			switch ($(whichInput).attr('name')) {
			case 'end_set_btn': 
				processVariousStats('CHECK-SET-WINNER',match_data);
				if(confirm('Confirm ' + $('#select_set_winner option:selected').text() + ' wins this set?')) {
					processWaitingButtonSpinner('START_WAIT_TIMER');
					processBoxingProcedures('LOG_SET','END');
				}
				break;
			case 'reset_set_btn':
				if(confirm('Do you really wish to RESET this set?')) {
					processWaitingButtonSpinner('START_WAIT_TIMER');
					processBoxingProcedures('LOG_SET','RESET');
				}
				break;
			}
		}
		break;
	case 'load_scene_btn':
		/*if(checkEmpty($('#vizIPAddress'),'IP Address Blank') == false
			|| checkEmpty($('#vizPortNumber'),'Port Number Blank') == false) {
			return false;
		}*/
	  	document.initialise_form.submit();
		break;
	case 'cancel_match_setup_btn':
		document.setup_form.method = 'post';
		document.setup_form.action = 'setup_to_match';
	   	document.setup_form.submit();
		break;
	case 'matchFileName':
		if(document.getElementById('matchFileName').value) {
			document.getElementById('matchFileName').value = 
				document.getElementById('matchFileName').value.replace('.xml','') + '.json';
		}
		break;
	case 'save_match_btn': case 'reset_match_btn':
		switch ($(whichInput).attr('name')) {
		case 'reset_match_btn':
	    	if (confirm('The setup selections of this match will be retained ' +
	    			'but the match data will be deleted permanently. Are you sure, you want to RESET this match?') == false) {
	    		return false;
	    	}
			break;
		}
		if (!checkEmpty(document.getElementById('matchFileName'),'Match Name')) {
			return false;
		} 
		if($('#homeFirstPlayerId option:selected').val() == $('#awayFirstPlayerId option:selected').val()) {
			alert('Player dupication found. Please choose different players');
			return false;
		}
		
		switch ($(whichInput).attr('name')) {
		case 'save_match_btn': 
			uploadFormDataToSessionObjects('SAVE_MATCH');
			break;
		case 'reset_match_btn':
			processWaitingButtonSpinner('START_WAIT_TIMER');
			uploadFormDataToSessionObjects('RESET_MATCH');
			break;
		}
		break;
	case 'setup_match_btn':
		document.boxing_form.method = 'post';
		document.boxing_form.action = 'setup';
	   	document.boxing_form.submit();
		break;
	case 'load_match_btn':
		processWaitingButtonSpinner('START_WAIT_TIMER');
		processBoxingProcedures('LOAD_MATCH',$('#select_boxing_matches option:selected'));
		break;
	case 'log_saves_btn':
		processBoxingProcedures('SAVES',match_data);
		break;
	case 'populate_namesupersp_btn':
		processBoxingProcedures('POPULATE-NAMESUPER-SP');
		break;
	case 'populate_namesupercategory_btn':
		processBoxingProcedures('POPULATE-NAMESUPER-CATEGORY');
		break;
	case 'populate_match_promo_btn':
		processBoxingProcedures('POPULATE-MATCH_PROMO');
		break;
	case 'populate_namesupermedal_manual_all_btn':
		processBoxingProcedures('POPULATE-NAMESUPER-MEDAL_MANUAL');
		break;	
	case 'populate_namesuper_btn': 
		processBoxingProcedures('POPULATE-L3-NAMESUPER');
		break;
	case 'populate_bug_btn':
		processBoxingProcedures('POPULATE-L3-BUG');
		break;
	case 'cancel_graphics_btn':
		$('#select_graphic_options_div').empty();
		document.getElementById('select_graphic_options_div').style.display = 'none';
		$("#select_event_div").show();
		$("#match_configuration").show();
		$("#boxing_div").show();
		break;
	case 'select_existing_boxing_matches':
		if(whichInput.value.toLowerCase().includes('new_match')) {
			initialiseForm('SETUP',null);
		} else {
			processWaitingButtonSpinner('START_WAIT_TIMER');
			processBoxingProcedures('LOAD_SETUP',$('#select_existing_boxing_matches option:selected'));
		}
		break;
	default:
		if(whichInput) {
			//alert(whichInput.id);
			if(whichInput.id.includes('_points_btn')) { 
				
				error_msg = 'Cannot find any started game. Please start a game first before logging an event';
				match_data.sets.forEach(function(set,set_index,set_arr){
					if(set.set_status.toLowerCase() == 'start') {
						error_msg = '';
					}
				});
				if(error_msg) {
					alert(error_msg);
					return false;			
				} else {
					processWaitingButtonSpinner('START_WAIT_TIMER');
					processBoxingProcedures('LOG_SCORE',whichInput);
				}
			} 
		}
		break;
	}
}
function processBoxingProcedures(whatToProcess, whichInput)
{
	var value_to_process; 
	
	switch(whatToProcess) {
	case 'LOG_SET':
		if(whichInput == 'START' || whichInput == 'RESET') {
			value_to_process = whichInput;
		}else if(whichInput == 'END') {
			switch(whatToProcess) {
			case 'LOG_SET': 
				value_to_process = whichInput + ',' + $('#select_set_winner option:selected').val();
				break;
			}
		}
		break;
	case 'LOAD_MATCH': case 'LOAD_SETUP':
		value_to_process = whichInput.val();
		break;
	case 'LOG_SCORE': 
		value_to_process = whichInput.id;
		break;
	case 'POPULATE-SCOREBUG':
		switch ($('#selectedBroadcaster').val()) {
		case 'DOAD_BOXING':
			value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_Khelo_India_2023/Scenes/Boxing_Scorebug.sum';
			break;
		case 'DOAD_NATIONALS':
			value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_Khelo_India_2023/Scenes/Boxing_Scorebug.sum';
			break;
		}
		break;
	case 'POPULATE-FF-SCORELINE':
		switch ($('#selectedBroadcaster').val()) {
			case 'DOAD_BOXING':
				value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_Khelo_India_2023/Scenes/FF_Breakup.sum' ;
			break;
			case 'DOAD_NATIONALS':
				value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_Khelo_India_2023/Scenes/FF_Breakup.sum' ;
			break;
		}
		break;	
	case 'POPULATE-NAMESUPER-SP':
		switch ($('#selectedBroadcaster').val()) {
		case 'DOAD_BOXING':
			value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_Khelo_India_2023/Scenes/LT.sum' + ',' + 
				$('#selectNameSupersp option:selected').val();
			break;
		case 'DOAD_NATIONALS':
			value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_Khelo_India_2023/Scenes/LT.sum' + ',' + 
				$('#selectNameSupersp option:selected').val();
			break;
		}
		break;
	case 'POPULATE-NAMESUPER-CATEGORY':
		switch ($('#selectedBroadcaster').val()) {
		case 'DOAD_BOXING':
			value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_Khelo_India_2023/Scenes/LT_Generic.sum' + ',' + 
				$('#selectNameSuperwinner option:selected').val() + ',' + $('#selectPlayers option:selected').val();
			break;
		case 'DOAD_NATIONALS':
			value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_Khelo_India_2023/Scenes/LT_Generic.sum' + ',' + 
				$('#selectNameSuperwinner option:selected').val() + ',' + $('#selectPlayers option:selected').val();
			break;
		}
		break;
	case 'POPULATE-MATCH_PROMO':
		switch ($('#selectedBroadcaster').val()) {
			case 'DOAD_BOXING':
			value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_Khelo_India_2023/Scenes/FF_MatchId.sum' + ',' + 
				$('#selectMatchPromo option:selected').val();
			break;
		case 'DOAD_NATIONALS':
			value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_Khelo_India_2023/Scenes/FF_MatchId.sum' + ',' + 
				$('#selectMatchPromo option:selected').val();
			break;
		}
		break;
	case 'POPULATE-L3-NAMESUPER':
		switch ($('#selectedBroadcaster').val()) {
			case 'DOAD_BOXING':
			value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_Khelo_India_2023/Scenes/LT.sum' + ',' + 
				$('#selectNameSuper option:selected').val();
			break;
		case 'DOAD_NATIONALS':
			value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_Khelo_India_2023/Scenes/LT.sum' + ',' + 
				$('#selectNameSuper option:selected').val();
			break;
		}
		break;
	case 'POPULATE-NAMESUPER-MEDAL_MANUAL':
		switch ($('#selectedBroadcaster').val()) {
		case 'DOAD_BOXING':
			value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_Khelo_India_2023/Scenes/LT_Medal_All_2Bro.sum' + ',' + 
			$('#selectNameSuperwinner1 option:selected').val() + ',' + $('#selectNameSuperwinner2 option:selected').val() + ',' + $('#selectNameSuperwinner3 option:selected').val() + ',' + $('#selectNameSuperwinner4 option:selected').val() +
			',' + $('#selectPlayers option:selected').val();
			break;
		case 'DOAD_NATIONALS':
			value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_Khelo_India_2023/Scenes/LT_Medal_All_2Bro.sum' + ',' + 
			$('#selectNameSuperwinner1 option:selected').val() + ',' + $('#selectNameSuperwinner2 option:selected').val() + ',' + $('#selectNameSuperwinner3 option:selected').val() + ',' + $('#selectNameSuperwinner4 option:selected').val() +
			',' + $('#selectPlayers option:selected').val();
			break;
		}
		break;	
	case 'POPULATE-L3-BUG':
		switch ($('#selectedBroadcaster').val()) {
			case 'DOAD_BOXING':
				value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_Khelo_India_2023/Scenes/Bug_OneLine.sum' + ',' + $('#selectBugdb option:selected').val();
			break;
		case 'DOAD_NATIONALS':
				value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_Khelo_India_2023/Scenes/Bug_OneLine.sum' + ',' + $('#selectBugdb option:selected').val();
			break;
		}
		break;
	case 'POPULATE-FF-MATCHID':
		switch ($('#selectedBroadcaster').val()) {
			case 'DOAD_BOXING':
				value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_Khelo_India_2023/Scenes/FF_MatchId.sum' ;
			break;
		case 'DOAD_NATIONALS':
				value_to_process = 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_Khelo_India_2023/Scenes/FF_MatchId.sum' ;
			break;
		}
		break;
	}

	$.ajax({    
        type : 'Get',     
        url : 'processBoxingProcedures.html',     
        data : 'whatToProcess=' + whatToProcess + '&valueToProcess=' + value_to_process, 
        dataType : 'json',
        success : function(data) {
			match_data = data;
        	switch(whatToProcess) {
			case 'READ_CLOCK':
				if(data.clock) {
					if(document.getElementById('match_time_hdr')) {
						document.getElementById('match_time_hdr').innerHTML = 'MATCH TIME : ' + 
							millisToMinutesAndSeconds(data.clock.matchTotalMilliSeconds);
					}
				}
				break;
			case 'LOG_SCORE': case 'LOAD_MATCH': case 'LOG_SET':
        		addItemsToList('LOAD_MATCH',data);
        		addItemsToList('LOAD_EVENTS',data);
				switch(whatToProcess) {
				case 'LOAD_MATCH': case 'LOAD_MATCH_AFTER_STAT_LOG':
					document.getElementById('boxing_div').style.display = '';
					document.getElementById('select_event_div').style.display = '';
					setInterval(displayMatchTime, 500);
					break;
				}
        		break;
        	case 'LOAD_SETUP':
        		initialiseForm('SETUP',data);
        		break;
        	case 'NAMESUPER_GRAPHICS-OPTIONS':
        		addItemsToList('NAMESUPER-OPTIONS',data);
        		break;
        	case 'BUG_GRAPHICS-OPTIONS':
        		addItemsToList('BUG-OPTIONS',data);
        		break;
        	case 'NAMESUPER-SP_GRAPHICS-OPTIONS':
				addItemsToList('NAMESUPER-SP-OPTIONS',data);
				break;
			case 'NAMESUPER-CATEGORY_GRAPHICS-OPTIONS':
				addItemsToList('NAMESUPER-CATEGORY-OPTIONS',data);
				processBoxingProcedures('CATEGORY-OPTIONS');
				break;
			case 'CATEGORY-OPTIONS':
				addItemsToList('POPULATE-CATEGORY',data);
				break;
			case 'PROMO_GRAPHICS-OPTIONS':
				addItemsToList('MATCH-PROMO-OPTIONS',data);
				break;
			case 'NAMESUPER-MEDAL_MANUAL_GRAPHICS-OPTIONS':
				addItemsToList('NAMESUPER-MEDAL_MANUAL-OPTIONS',data);
				processBoxingProcedures('NAMESUPER_CATEGORY-OPTIONS');
				break;
			case 'NAMESUPER_CATEGORY-OPTIONS':
				addItemsToList('POPULATE-CATEGORY',data);
				break;				
        	case 'POPULATE-SCOREBUG': case 'POPULATE-L3-NAMESUPER': case 'POPULATE-FF-MATCHID': case 'POPULATE-NAMESUPER-SP':
        	case 'POPULATE-FF-SCORELINE': case 'POPULATE-NAMESUPER-CATEGORY': case 'POPULATE-MATCH_PROMO': case "POPULATE-L3-BUG":
        	case 'POPULATE-NAMESUPER-MEDAL_MANUAL':
        		if(confirm('Animate In?') == true){
					switch(whatToProcess){
					case 'POPULATE-NAMESUPER-MEDAL_MANUAL':
						processBoxingProcedures('ANIMATE-IN-NAMESUPER-MEDAL_MANUAL');	
						break;
					case "POPULATE-L3-BUG":
						processBoxingProcedures('ANIMATE-IN-BUG');		
						break;
					case 'POPULATE-MATCH_PROMO':
						processBoxingProcedures('ANIMATE-IN-MATCH_PROMO');		
						break;
					case 'POPULATE-NAMESUPER-CATEGORY':
						processBoxingProcedures('ANIMATE-IN-CATEGORY');		
						break;
					case 'POPULATE-SCOREBUG':
						processBoxingProcedures('ANIMATE-IN-SCOREBUG');		
						break;
					case 'POPULATE-FF-SCORELINE':
						processBoxingProcedures('ANIMATE-IN-FFSCORELINE');		
						break;	
					case 'POPULATE-L3-NAMESUPER':
						processBoxingProcedures('ANIMATE-IN-NAMESUPERDB');
						break;
					case 'POPULATE-NAMESUPER-SP':
						processBoxingProcedures('ANIMATE-IN-NAMESUPER_SP');				
						break;
					case 'POPULATE-FF-MATCHID':
						processBoxingProcedures('ANIMATE-IN-MATCHID');
						break;
					}
				}
				break;
        	}
    		processWaitingButtonSpinner('END_WAIT_TIMER');
	    },    
	    error : function(e) {    
	  	 	console.log('Error occured in ' + whatToProcess + ' with error description = ' + e);     
	    }    
	});
}
function processVariousStats(whatToProcess, whichInput)
{
	switch(whatToProcess){
	case 'CHECK-SET-WINNER':
		if(match_data.sets[match_data.sets.length-1].homeScore > match_data.sets[match_data.sets.length-1].awayScore){
			$('#select_set_winner').val('home');
			//processUserSelection($('#end_set_btn'));
		}else if(match_data.sets[match_data.sets.length-1].homeScore < match_data.sets[match_data.sets.length-1].awayScore){
			$('#select_set_winner').val('away');
			//processUserSelection($('#end_set_btn'));
		}
		break;
	}
}
function addItemsToList(whatToProcess, dataToProcess)
{
	var div,row,header_text,select,option,tr,th,thead,text,table,tbody;
	var cellCount=0;
	//var cellCount=0;
	switch (whatToProcess) {
	case 'POPULATE-BUG-SCENE':
		
		$('#bugdbScene').empty();
		
		dataToProcess.forEach(function(bug,index,arr1){
			if(bug.bugId == $('#selectBugdb option:selected').val()){
				if($('#selectedBroadcaster').val().toUpperCase() == 'DOAD_BOXING'){
					if(bug.text2 == ''){
						document.getElementById('bugdbScene').value= 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_Khelo_India_2023/Scenes/Bug_OneLine.sum';
					}else{
						document.getElementById('bugdbScene').value= 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_Khelo_India_2023/Scenes/Bug_TwoLine.sum';
					}
				}
				if($('#selectedBroadcaster').val().toUpperCase() == 'DOAD_NATIONALS'){
					if(bug.text2 == ''){
						document.getElementById('bugdbScene').value= 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_Khelo_India_2023/Scenes/Bug_OneLine.sum';
					}else{
						document.getElementById('bugdbScene').value= 'D:/DOAD_In_House_Everest/Everest_Sports/Everest_Khelo_India_2023/Scenes/Bug_TwoLine.sum';
					}
				}
			}
		});
		break;
	case 'POPULATE-CATEGORY':
		$('#selectPlayers').empty();
		dataToProcess.forEach(function(inn,index,arr){
			
            $('#selectPlayers').append(
				$(document.createElement('option')).prop({
                value: inn.matchTypeID,
                text: inn.matchTypeName
            }))
		});
		break;		
	
	case 'NAMESUPER-OPTIONS': case 'NAMESUPER-SP-OPTIONS': case 'BUG-OPTIONS': case "NAMESUPER-CATEGORY-OPTIONS": case 'MATCH-PROMO-OPTIONS':
	case 'NAMESUPER-MEDAL_MANUAL-OPTIONS':
		switch ($('#selectedBroadcaster').val().toUpperCase()){
			case 'DOAD_BOXING':case 'DOAD_NATIONALS':
				
				$('#select_graphic_options_div').empty();
				
				header_text = document.createElement('h6');
				header_text.innerHTML = 'Select Graphic Options';
				document.getElementById('select_graphic_options_div').appendChild(header_text);
				
				table = document.createElement('table');
				table.setAttribute('class', 'table table-bordered');
						
				tbody = document.createElement('tbody');
		
				table.appendChild(tbody);
				document.getElementById('select_graphic_options_div').appendChild(table);
				
				row = tbody.insertRow(tbody.rows.length);
				
				switch(whatToProcess){
					case 'NAMESUPER-MEDAL_MANUAL-OPTIONS':
						select = document.createElement('select');
						select.style = 'width:130px';
						select.id = 'selectNameSuperwinner1';
						select.name = select.id;
						
						dataToProcess.forEach(function(nsp,index,arr1){
							option = document.createElement('option');
							option.value = nsp.playerId;
							option.text = nsp.full_name ;
							select.appendChild(option);
						});
						row.insertCell(cellCount).appendChild(select);
						cellCount = cellCount + 1;
						
						select = document.createElement('select');
						select.style = 'width:130px';
						select.id = 'selectNameSuperwinner2';
						select.name = select.id;
						
						dataToProcess.forEach(function(nsp,index,arr1){
							
							option = document.createElement('option');
							option.value = nsp.playerId;
							option.text = nsp.full_name;
							select.appendChild(option);
						});
						row.insertCell(cellCount).appendChild(select);
						cellCount = cellCount + 1;
						
						select = document.createElement('select');
						select.style = 'width:130px';
						select.id = 'selectNameSuperwinner3';
						select.name = select.id;
						
						dataToProcess.forEach(function(nsp,index,arr1){
							
							option = document.createElement('option');
							option.value = nsp.playerId;
							option.text = nsp.full_name;
							select.appendChild(option);
						});
						row.insertCell(cellCount).appendChild(select);
						cellCount = cellCount + 1;
						
						select = document.createElement('select');
						select.style = 'width:130px';
						select.id = 'selectNameSuperwinner4';
						select.name = select.id;
						
						dataToProcess.forEach(function(nsp,index,arr1){
							
							option = document.createElement('option');
							option.value = nsp.playerId;
							option.text = nsp.full_name;
							select.appendChild(option);
						});
						row.insertCell(cellCount).appendChild(select);
						cellCount = cellCount + 1;
						
						select = document.createElement('select');
						select.id = 'selectPlayers';
						select.name = select.id;
				
						row.insertCell(cellCount).appendChild(select);
						cellCount = cellCount + 1;
						
						break;
					case 'MATCH-PROMO-OPTIONS':
						select = document.createElement('select');
						select.id = 'selectMatchPromo';
						select.name = select.id;
						
						dataToProcess.forEach(function(oop,index,arr1){	
							option = document.createElement('option');
		                    option.value = oop.matchnumber;
		                    option.text = oop.bout + ' - ' + oop.home_Player.full_name + ' Vs ' + oop.away_Player.full_name ;
		                    select.appendChild(option);
								
		                });
						
						row.insertCell(cellCount).appendChild(select);
						cellCount = cellCount + 1;
						
						break;
						
					case "NAMESUPER-CATEGORY-OPTIONS":
						select = document.createElement('select');
						select.style = 'width:130px';
						select.id = 'selectNameSuperwinner';
						select.name = select.id;
						dataToProcess.forEach(function(nsp,index,arr1){
							
							option = document.createElement('option');
							option.value = nsp.playerId;
							option.text = nsp.full_name;
							select.appendChild(option);
						});
						row.insertCell(cellCount).appendChild(select);
						cellCount = cellCount + 1;
						
						select = document.createElement('select');
						select.id = 'selectPlayers';
						select.name = select.id;
				
						row.insertCell(cellCount).appendChild(select);
						cellCount = cellCount + 1;
						
						break;
					case 'NAMESUPER-SP-OPTIONS':
						select = document.createElement('select');
						select.style = 'width:130px';
						select.id = 'selectNameSupersp';
						select.name = select.id;
						
						dataToProcess.forEach(function(nsp,index,arr1){
							if(home_team_id == nsp.playerId){
								option = document.createElement('option');
								option.value = nsp.playerId;
								option.text = nsp.full_name ;
								select.appendChild(option);
							}
							if(away_team_id == nsp.playerId){
								option = document.createElement('option');
								option.value = nsp.playerId;
								option.text = nsp.full_name ;
								select.appendChild(option);
							}
						});
						row.insertCell(cellCount).appendChild(select);
						cellCount = cellCount + 1;
						break;
					case 'BUG-OPTIONS':
						select = document.createElement('select');
						select.style = 'width:130px';
						select.id = 'selectBugdb';
						select.name = select.id;
						
						dataToProcess.forEach(function(bug,index,arr1){
							option = document.createElement('option');
							option.value = bug.bugId;
							option.text = bug.prompt;
							select.appendChild(option);
						});
						select.setAttribute('onchange',"processUserSelection(this)");
						
						row.insertCell(cellCount).appendChild(select);
						cellCount = cellCount + 1;
						break;
					case 'NAMESUPER-OPTIONS':
						select = document.createElement('select');
						select.style = 'width:130px';
						select.id = 'selectNameSuper';
						select.name = select.id;
						
						dataToProcess.forEach(function(ns,index,arr1){
							option = document.createElement('option');
							option.value = ns.namesuperId;
							option.text = ns.subHeader ;
							select.appendChild(option);
						});
						
						row.insertCell(cellCount).appendChild(select);
						cellCount = cellCount + 1;
						break;
				}
				switch(whatToProcess){
					case 'NAMESUPER-MEDAL_MANUAL-OPTIONS':
						option = document.createElement('input');
				   	 	option.type = 'button';
					    option.name = 'populate_namesupermedal_manual_all_btn';
				    	option.value = 'Populate MEDAL ALL';
					    option.id = option.name;
					    option.setAttribute('onclick',"processUserSelection(this)");
					    
					    div = document.createElement('div');
					    div.append(option);
			
						option = document.createElement('input');
						option.type = 'button';
						option.name = 'cancel_graphics_btn';
						option.id = option.name;
						option.value = 'Cancel';
						option.setAttribute('onclick','processUserSelection(this)');
				
					    div.append(option);
					    
					    row.insertCell(cellCount).appendChild(div);
					    cellCount = cellCount + 1;
					    
						document.getElementById('select_graphic_options_div').style.display = '';
						break;
					case 'MATCH-PROMO-OPTIONS':
						option = document.createElement('input');
				   	 	option.type = 'button';
						option.name = 'populate_match_promo_btn';
						option.value = 'Populate Match Promo';
					    option.id = option.name;
					    
					    option.setAttribute('onclick',"processUserSelection(this)");
					    
					    div = document.createElement('div');
					    div.append(option);
						
						option = document.createElement('input');
						option.type = 'button';
						option.name = 'cancel_graphics_btn';
						option.id = option.name;
						option.value = 'Cancel';
						option.setAttribute('onclick','processUserSelection(this)');
				
					    div.append(option);
					    
					    row.insertCell(cellCount).appendChild(div);
					    cellCount = cellCount + 1;
					    
						document.getElementById('select_graphic_options_div').style.display = '';
						break;
					case "NAMESUPER-CATEGORY-OPTIONS":
						option = document.createElement('input');
				   	 	option.type = 'button';
					    option.name = 'populate_namesupercategory_btn';
				    	option.value = 'Populate Namesuper';
					    option.id = option.name;
					    option.setAttribute('onclick',"processUserSelection(this)");
					    
					    div = document.createElement('div');
					    div.append(option);
			
						option = document.createElement('input');
						option.type = 'button';
						option.name = 'cancel_graphics_btn';
						option.id = option.name;
						option.value = 'Cancel';
						option.setAttribute('onclick','processUserSelection(this)');
				
					    div.append(option);
					    
					    row.insertCell(cellCount).appendChild(div);
					    cellCount = cellCount + 1;
					    
						document.getElementById('select_graphic_options_div').style.display = '';
						break;
					case 'NAMESUPER-SP-OPTIONS':
						option = document.createElement('input');
				   	 	option.type = 'button';
					    option.name = 'populate_namesupersp_btn';
				    	option.value = 'Populate Namesuper';
					    option.id = option.name;
					    option.setAttribute('onclick',"processUserSelection(this)");
					    
					    div = document.createElement('div');
					    div.append(option);
			
						option = document.createElement('input');
						option.type = 'button';
						option.name = 'cancel_graphics_btn';
						option.id = option.name;
						option.value = 'Cancel';
						option.setAttribute('onclick','processUserSelection(this)');
				
					    div.append(option);
					    
					    row.insertCell(cellCount).appendChild(div);
					    cellCount = cellCount + 1;
					    
						document.getElementById('select_graphic_options_div').style.display = '';
						break;
					case 'BUG-OPTIONS':
						option = document.createElement('input');
				   	 	option.type = 'button';
					    option.name = 'populate_bug_btn';
					    option.value = 'Populate Bug';
					    option.id = option.name;
					    option.setAttribute('onclick',"processUserSelection(this)");
					    
					    div = document.createElement('div');
					    div.append(option);
			
						option = document.createElement('input');
						option.type = 'button';
						option.name = 'cancel_graphics_btn';
						option.id = option.name;
						option.value = 'Cancel';
						option.setAttribute('onclick','processUserSelection(this)');
				
					    div.append(option);
					    
					    row.insertCell(cellCount).appendChild(div);
					    cellCount = cellCount + 1;
					    
						document.getElementById('select_graphic_options_div').style.display = '';
						break;
					case 'NAMESUPER-OPTIONS':
						option = document.createElement('input');
				   	 	option.type = 'button';
					    option.name = 'populate_namesuper_btn';
					    option.value = 'Populate Namesuper';
					    option.id = option.name;
					    option.setAttribute('onclick',"processUserSelection(this)");
					    
					    div = document.createElement('div');
					    div.append(option);
			
						option = document.createElement('input');
						option.type = 'button';
						option.name = 'cancel_graphics_btn';
						option.id = option.name;
						option.value = 'Cancel';
						option.setAttribute('onclick','processUserSelection(this)');
				
					    div.append(option);
					    
					    row.insertCell(cellCount).appendChild(div);
					    cellCount = cellCount + 1;
					    
						document.getElementById('select_graphic_options_div').style.display = '';
						break;
				}
				
				break;
		}
		break;	
	case 'LOAD_EVENTS':
		
		$('#select_event_div').empty();
	
        for(var i=0; i<1; i++) {

			table = document.createElement('table');
			table.setAttribute('class', 'table table-bordered');
			tbody = document.createElement('tbody');
            row = tbody.insertRow(tbody.rows.length);
			
			switch (i) {
			case 0:
		   		header_text = document.createElement('h6');
				header_text.id = 'match_time_hdr';
				header_text.innerHTML = 'Match Time: 00:00';
				row.insertCell(0).appendChild(header_text);
		   		
		   		div = document.createElement('div');
				div.style = 'text-align:center;';
				
			    option = document.createElement('input');
			    option.type = 'button';
			    option.name = 'start_set_btn';
			    option.value = 'Start Round';
			    option.style = 'width:100px;';
			    option.style.backgroundColor = 'Pink';
			    option.id = option.name;
			    option.setAttribute('onclick','processUserSelection(this);');
				div.appendChild(option);
				row.insertCell(1).appendChild(option);
				
				select = document.createElement('select');
				select.id = 'select_set_winner';
				select.style = 'width:175px;';
				option = document.createElement('option');
				option.value = 'home';	
				option.text = match_data.homeFirstPlayer.full_name;
				select.appendChild(option);
				
				option = document.createElement('option');
				option.value = 'away';	
				option.text = match_data.awayFirstPlayer.full_name;
				select.appendChild(option);
				
				text = document.createElement('label');
				text.innerHTML = 'Set Winner: '
				text.htmlFor = select.id;
				text.style = 'display:none;';
				document.getElementById('select_event_div').appendChild(text).appendChild(select);
		
			    option = document.createElement('input');
			    option.type = 'button';
			    option.name = 'end_set_btn';
			    option.value = 'End Round';
			    option.style = 'width:100px;';
			    option.style.backgroundColor = 'Pink';
			    option.id = option.name;
			    option.setAttribute('onclick','processUserSelection(this);');
				div.appendChild(option);
				
				row.insertCell(2).appendChild(option);
		
			    option = document.createElement('input');
			    option.type = 'button';
			    option.name = 'reset_set_btn';
			    option.value = 'RESET Round';
			    option.style = 'width:110px;';
			    option.style.backgroundColor = 'Pink';
			    option.id = option.name;
			    option.setAttribute('onclick','processUserSelection(this);');
				div.appendChild(option);

				row.insertCell(3).appendChild(option);
				break;
			}
			table.appendChild(tbody);
			document.getElementById('select_event_div').appendChild(table);
	    }
	    
	    table = document.createElement('table');
		table.setAttribute('class', 'table table-bordered');
		tbody = document.createElement('tbody');
		for(var j=0; j<=2; j++){
			row = tbody.insertRow(tbody.rows.length);
			switch (j){
				case 0:
					for(var iCol=0; iCol<=2; iCol++){
						header_text = document.createElement('h6');
						header_text.style = 'text-align:center;font-size:15px; font-weight: bold;'; 
						switch(iCol){
							case 0:
								header_text.innerHTML = match_data.homeFirstPlayer.full_name;
								break;
							case 1:
								header_text.innerHTML = 'SCORES';
								break;
							case 2:
								header_text.innerHTML = match_data.awayFirstPlayer.full_name;
								break;
						}
						row.insertCell(a).appendChild(header_text);
					}
					break;
				case 1:
					for(var a=0; a<=2; a++){
						div = document.createElement('div');
	   					div.style = 'text-align:center;';
	   					if(a == 0){
							text = 'home';
						}else if(a == 2){
							text = 'away';
						}
	   					
	   					switch(a){
							case 0: case 2:
								option = document.createElement('input');
			        			option.type = 'button';
				    			option.name = text + '_increment_1_points_btn';
							    option.value = '1';
							    option.style = 'width:80px;';
							    option.id = option.name;
							    option.setAttribute('onclick','processUserSelection(this);');
				    			option.style = 'text-align:center;';
								div.appendChild(option);
								
								option = document.createElement('input');
			        			option.type = 'button';
				    			option.name = text + '_increment_2_points_btn';
							    option.value = '2';
							    option.style = 'width:80px;';
							    option.id = option.name;
							    option.setAttribute('onclick','processUserSelection(this);');
				    			option.style = 'text-align:center;';
								div.appendChild(option);
								
								option = document.createElement('input');
			        			option.type = 'button';
				    			option.name = text + '_increment_3_points_btn';
							    option.value = '3';
							    option.style = 'width:80px;';
							    option.id = option.name;
							    option.setAttribute('onclick','processUserSelection(this);');
				    			option.style = 'text-align:center;';
								div.appendChild(option);
								
								option = document.createElement('input');
			        			option.type = 'button';
				    			option.name = text + '_increment_4_points_btn';
							    option.value = '4';
							    option.style = 'width:80px;';
							    option.id = option.name;
							    option.setAttribute('onclick','processUserSelection(this);');
				    			option.style = 'text-align:center;';
								div.appendChild(option);
								
								option = document.createElement('input');
			        			option.type = 'button';
				    			option.name = text + '_increment_5_points_btn';
							    option.value = '5';
							    option.style = 'width:80px;';
							    option.id = option.name;
							    option.setAttribute('onclick','processUserSelection(this);');
				    			option.style = 'text-align:center;';
								div.appendChild(option);
								break;
						}
						row.insertCell(a).appendChild(div);
					}
					break;
				case 2:
					for(var a=0; a<=2; a++){
						div = document.createElement('div');
	   					div.style = 'text-align:center;';
	   					if(a == 0){
							text = 'home';
						}else if(a == 2){
							text = 'away';
						}
	   					
	   					switch(a){
							case 0: case 2:
								option = document.createElement('input');
			        			option.type = 'button';
				    			option.name = text + '_decrement_1_points_btn';
							    option.value = '-1';
							    option.style = 'width:80px;';
							    option.id = option.name;
							    option.setAttribute('onclick','processUserSelection(this);');
				    			option.style = 'text-align:center;';
								div.appendChild(option);
								
								option = document.createElement('input');
			        			option.type = 'button';
				    			option.name = text + '_decrement_2_points_btn';
							    option.value = '-2';
							    option.style = 'width:80px;';
							    option.id = option.name;
							    option.setAttribute('onclick','processUserSelection(this);');
				    			option.style = 'text-align:center;';
								div.appendChild(option);
								
								option = document.createElement('input');
			        			option.type = 'button';
				    			option.name = text + '_decrement_3_points_btn';
							    option.value = '-3';
							    option.style = 'width:80px;';
							    option.id = option.name;
							    option.setAttribute('onclick','processUserSelection(this);');
				    			option.style = 'text-align:center;';
								div.appendChild(option);
								
								option = document.createElement('input');
			        			option.type = 'button';
				    			option.name = text + '_decrement_4_points_btn';
							    option.value = '-4';
							    option.style = 'width:80px;';
							    option.id = option.name;
							    option.setAttribute('onclick','processUserSelection(this);');
				    			option.style = 'text-align:center;';
								div.appendChild(option);
								
								option = document.createElement('input');
			        			option.type = 'button';
				    			option.name = text + '_decrement_5_points_btn';
							    option.value = '-5';
							    option.style = 'width:80px;';
							    option.id = option.name;
							    option.setAttribute('onclick','processUserSelection(this);');
				    			option.style = 'text-align:center;';
								div.appendChild(option);
								break;
						}
						row.insertCell(a).appendChild(div);
					}
					break;
			}
		}
		table.appendChild(tbody);
		document.getElementById('select_event_div').appendChild(table);
	    		
		break;
				
	case 'LOAD_MATCH':
		
		$('#boxing_div').empty();
		
		if (dataToProcess)
		{
			table = document.createElement('table');
			table.setAttribute('class', 'table table-bordered');
			tbody = document.createElement('tbody');
			row = tbody.insertRow(tbody.rows.length);
			for (var j = 1; j <= (match_data.sets.length + 1); j++) {
				cell = row.insertCell(row.cells.length);
			    th = document.createElement('th'); //column
			    switch (j) {
				case 1:
				    cell.innerHTML = match_data.categoryType.toUpperCase() + " " + match_data.boxingType.toUpperCase() + " " + 
				    	match_data.matchType.toUpperCase(); 
					break;
				default:
				    cell.innerHTML = 'Round : ' + match_data.sets[j-2].set_number; 
					break;
				}
			}
						
			for (var j = 0; j <= 1; j++) {
				row = tbody.insertRow(tbody.rows.length);
				cell = row.insertCell(row.cells.length);
			    switch (j) {
				case 0:
				    cell.innerHTML = match_data.homeFirstPlayer.full_name;
					
					for (var k = 1; k <= match_data.sets.length ; k++) {
						cell = row.insertCell(row.cells.length);
					    th = document.createElement('th'); //column
					    cell.innerHTML = match_data.sets[k-1].homeScore;
					    //cell.innerHTML = 'P : ' + match_data.sets[k-1].homeScore + '   F : ' + match_data.sets[k-1].homeFoul;  
					}
					break;
				case 1:
				    cell.innerHTML = match_data.awayFirstPlayer.full_name;
					
					for (var l = 1; l <= match_data.sets.length ; l++) {
						cell = row.insertCell(row.cells.length);
					    th = document.createElement('th'); //column
					    cell.innerHTML = match_data.sets[l-1].awayScore; 
					    //cell.innerHTML = 'P : ' + match_data.sets[l-1].awayScore + '   F : ' + match_data.sets[l-1].awayFoul; 
					}
					break;
				}
			}
			table.appendChild(tbody);
			document.getElementById('boxing_div').appendChild(table);
		}
	break;
	}
}
function removeSelectDuplicates(select_id)
{
	var this_list = {};
	$("select[id='" + select_id + "'] > option").each(function () {
	    if(this_list[this.text]) {
	        $(this).remove();
	    } else {
	        this_list[this.text] = this.value;
	    }
	});
}
function checkEmpty(inputBox,textToShow) {

	var name = $(inputBox).attr('id');
	
	document.getElementById(name + '-validation').innerHTML = '';
	document.getElementById(name + '-validation').style.display = 'none';
	$(inputBox).css('border','');
	if(document.getElementById(name).value.trim() == '') {
		$(inputBox).css('border','#E11E26 2px solid');
		document.getElementById(name + '-validation').innerHTML = textToShow + ' required';
		document.getElementById(name + '-validation').style.display = '';
		document.getElementById(name).focus({preventScroll:false});
		return false;
	}
	return true;	
}	
