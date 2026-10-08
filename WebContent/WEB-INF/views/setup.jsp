<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Setup</title>
    
	<script type="text/javascript" src="<c:url value='/webjars/jquery/3.7.1/jquery.min.js'/>"></script>
	<script type="text/javascript" src="<c:url value='/webjars/bootstrap/5.3.8/js/bootstrap.bundle.min.js'/>"></script>
	<script type="text/javascript" src="<c:url value='/webjars/select2/4.1.0-rc.0/js/select2.min.js'/>"></script>
	
	<link rel="stylesheet" href="<c:url value='/webjars/bootstrap/5.3.8/css/bootstrap.min.css'/>">
	<link rel="stylesheet" href="<c:url value='/webjars/select2/4.1.0-rc.0/css/select2.min.css'/>">

    <script type="text/javascript" src="<c:url value='/resources/javascript/index.js'/>"></script>

</head>
<body onload="afterPageLoad('SETUP');">
<form:form name="setup_form" method="POST" action="match" enctype="multipart/form-data">
<div class="content py-5" style="background-color: #EAE8FF; color: #2E008B">
  <div class="container">
	<div class="row">
	 <div class="col-md-8 offset-md-2">
       <span class="anchor"></span>
         <div class="card card-outline-secondary">
           <div class="card-header">
             <h3 class="mb-0">Setup</h3>
             <h6>${licence_expiry_message}</h6>
           </div>
          <div class="card-body">
	         <div class="form-group row row-bottom-margin ml-2" style="margin-bottom:5px;">
			    <button style="background-color:#2E008B;color:#FEFEFE;" class="btn btn-sm" type="button"
			  		name="cancel_match_setup_btn" id="cancel_match_setup_btn" onclick="processUserSelection(this)">
		  		<i class="fas fa-window-close"></i> Back</button>
	         </div>
			  <div class="form-group row row-bottom-margin ml-2" style="margin-bottom:5px;">
			    <label for="select_existing_boxing_matches" class="col-sm-4 col-form-label text-left">Select BOXING Match 
			    	<i class="fas fa-asterisk fa-sm text-danger" style="font-size: 7px;"></i></label>
			    <div class="col-sm-6 col-md-6">
			      <select id="select_existing_boxing_matches" name="select_existing_boxing_matches" 
			      		class="browser-default custom-select custom-select-sm" onchange="processUserSelection(this)">
				        <option value="new_match">New Match</option>
						<c:forEach items = "${match_files}" var = "match">
				          <option value="${match.name}">${match.name}</option>
						</c:forEach>
			      </select>
			    </div>
			  </div>
			  <div id="matchFileName_div" class="form-group row row-bottom-margin ml-2" style="margin-bottom:5px;">
			    <label for="matchFileName" class="col-sm-4 col-form-label text-left">Match Filename 
			    <i class="fas fa-asterisk fa-sm text-danger" style="font-size: 7px;"></i></label>
			    <div class="col-sm-6 col-md-6">
		             <input type="text" id="matchFileName" name="matchFileName" class="form-control form-control-sm floatlabel" onblur="processUserSelection(this);"></input>
		              <label id="matchFileName-validation" style="color:red; display: none;"></label> 
			    </div>
			  </div>
			  <div id="tournament_div" class="form-group row row-bottom-margin ml-2" style="margin-bottom:5px;">
			    <label for="tournament" class="col-sm-4 col-form-label text-left">Tournament/Series Name <i class="fas fa-asterisk fa-sm text-danger" style="font-size: 7px;"></i></label>
			    <div class="col-sm-6 col-md-6">
		             <input type="text" id="tournament" name="tournament" class="form-control form-control-sm floatlabel" 
		             	onblur="processUserSelection(this);"></input>
			    </div>
			  </div>
			  <div id="matchIdent_div" class="form-group row row-bottom-margin ml-2" style="margin-bottom:5px;">
			    <label for="matchIdent" class="col-sm-4 col-form-label text-left">Match Ident <i class="fas fa-asterisk fa-sm text-danger" style="font-size: 7px;"></i></label>
			    <div class="col-sm-6 col-md-6">
		             <input type="text" id="matchIdent" name="matchIdent" 
		             	class="form-control form-control-sm floatlabel" onblur="processUserSelection(this);"></input>
			    </div>
			  </div>
			  <div id="venue_div" class="form-group row row-bottom-margin ml-2" style="margin-bottom:5px;">
			    <label for="venue" class="col-sm-4 col-form-label text-left">Venue <i class="fas fa-asterisk fa-sm text-danger" style="font-size: 7px;"></i></label>
			    <div class="col-sm-6 col-md-6">
		             <input type="text" id="venue" name="venue" 
		             	class="form-control form-control-sm floatlabel" onblur="processUserSelection(this);"></input>
			    </div>
			  </div>
			  <div class="form-group row row-bottom-margin ml-2" style="margin-bottom:5px;">
			    <label for="categoryType" class="col-sm-4 col-form-label text-left">Select Category Type
			    	<i class="fas fa-asterisk fa-sm text-danger" style="font-size: 7px;"></i></label>
			    <div class="col-sm-6 col-md-6">
			      <select id="categoryType" name="categoryType" onchange="processUserSelection(this)"
			      		class="browser-default custom-select custom-select-sm">
				        <option value="mens">Mens</option>
				        <option value="womens">Womens</option>
			      </select>
			    </div>
			  </div>
			  <!-- <div class="form-group row row-bottom-margin ml-2" style="margin-bottom:5px;">
			    <label for="boxingType" class="col-sm-4 col-form-label text-left">Select Boxing Type
			    	<i class="fas fa-asterisk fa-sm text-danger" style="font-size: 7px;"></i></label>
			    <div class="col-sm-6 col-md-6">
			      <select id="boxingType" name="boxingType" onchange="processUserSelection(this)"
			      		class="browser-default custom-select custom-select-sm">
				        <option value="folkstyle">Folkstyle</option>
				        <option value="freestyle">Freestyle</option>
				        <option value="Greco-Roman">Greco-Roman</option>
			      </select>
			    </div>
			  </div> -->
			  <div class="form-group row row-bottom-margin ml-2" style="margin-bottom:5px;">
			    <label for="matchTypeId" class="col-sm-4 col-form-label text-left">Select Category Type
			    	<i class="fas fa-asterisk fa-sm text-danger" style="font-size: 7px;"></i></label>
			    <div class="col-sm-6 col-md-6">
			      <select id="matchType" name="matchType" class="browser-default custom-select custom-select-sm"
			      	onchange="processUserSelection(this)">
				        <c:forEach items = "${matchTypes}" var = "matchtype">
				          <option value="${matchtype.matchTypeName}">${matchtype.matchTypeName}</option>
						</c:forEach> 
			      </select>
			    </div>
			  </div>
			  	<table class="table table-striped table-bordered"> 
				  <thead>
			        <tr>
			        	<th>Select HOME Team Jersey Color: 
					      <select id="homeTeamJerseyColor" name="homeTeamJerseyColor" class="browser-default custom-select custom-select-sm">
							<c:forEach items = "${teamcolor}" var = "teamcolor">
					          <option value="${teamcolor.colorType}">${teamcolor.colorType}</option>
							</c:forEach>
					      </select>
			        	</th>
			        	<th>Select AWAY Team Jersey Color: 
					      <select id="awayTeamJerseyColor" name="awayTeamJerseyColor" class="browser-default custom-select custom-select-sm">
							<c:forEach items = "${teamcolor}" var = "teamcolor" varStatus="status">
							<c:choose>
								<c:when test="${status.last}">
									<option value="${teamcolor.colorType}" selected="selected">${teamcolor.colorType}</option>
								</c:when>
								<c:otherwise>
						          <option value="${teamcolor.colorType}">${teamcolor.colorType}</option>
								</c:otherwise>
							</c:choose>
							</c:forEach>
					      </select>
			        	</th>
				    </tr>
				  </thead>
				</table>
			  	<table class="table table-striped table-bordered"> 
				  <thead>
			        <tr>
			        	<th>Select HOME First Player: 
					      <select id="homeFirstPlayerId" name="homeFirstPlayerId" 
					      		class="browser-default custom-select custom-select-sm">
							<c:forEach items = "${players}" var = "player">
					          <option value="${player.playerId}">${player.full_name}</option>
							</c:forEach>
					      </select>
			        	</th>
			        	<th>Select AWAY First Player: 
					      <select id="awayFirstPlayerId" name="awayFirstPlayerId" 
					      		class="browser-default custom-select custom-select-sm">
							<c:forEach items = "${players}" var = "player">
					          <option value="${player.playerId}">${player.full_name}</option>
							</c:forEach>
					      </select>
			        	</th>
				    </tr>
				  </thead>
				</table>
	         <div id="save_match_div" class="form-group row row-bottom-margin ml-2" style="margin-bottom:5px;">
			    <button style="background-color:#2E008B;color:#FEFEFE;" class="btn btn-sm" type="button"
			  		name="save_match_btn" id="save_match_btn" onclick="processUserSelection(this)">
		  		<i class="fas fa-download"></i> Save Match</button>
			    <button style="background-color:#2E008B;color:#FEFEFE;" class="btn btn-sm" type="button"
			  		name="reset_match_btn" id="reset_match_btn" onclick="processUserSelection(this)">
		  		<i class="fas fa-window-close"></i> Reset Match</button>
	         </div>
          </div>
         </div>
       </div>
    </div>
  </div>
</div>
</form:form>
</body>
</html>