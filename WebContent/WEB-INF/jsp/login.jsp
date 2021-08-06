<%--
    Mango - Open Source M2M - http://mango.serotoninsoftware.com
    Copyright (C) 2006-2011 Serotonin Software Technologies Inc.
    @author Matthew Lohbihler
    
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see http://www.gnu.org/licenses/.
--%>
<%@ include file="/WEB-INF/jsp/include/tech.jsp" %>
<tag:page onload="">
	
	<c:if test="${!empty sessionUser}">
		<!-- User already logged in. Go home URL. -->
		<script>goHomeUrl();</script>
	</c:if>
	
	<script type="text/javascript">
		function unsupportedWarning() {
			$("warnings").style.top = "150px";
			var span = document.querySelector("#warnings .content");
			span.innerHTML = $("unsupportedMsg").innerHTML;
			show("warningsBkg");
		}
		
		function showHelp() {
			MiscDwr.getDocumentationItem("welcomeToMango", function (response) {
				var span = document.querySelector("#warnings .content");
				span.innerHTML = response.content;
			});
			show("warningsBkg");
		}
		
		function usePng(img) {
			img.src = img.src.replace(".svg", ".png");
		}
		 
		function togglePassword() {
			if ($("password").type == "password") {
				$("password").type = "text";
				$("pswBtn").src = "images/hide-password.svg";
			} else {
				$("password").type = "password";
				$("pswBtn").src = "images/show-password.svg";
			}
		}
		
		var browserTested = false;
		
		// Test if browser is compatible. This calls newBrowserTest() in
		// resources/header.js
		function testBrowser() {
			if (!browserTested) {
				var testResult = newBrowserTest();
				if (testResult == "bad") {
					// Very old browser. Don't try to run.
					setInterval(function() {
						document.write("Unsupported browser!");
					}, 100);
				} else if (testResult == "regular") {
					// Old browser. Show a warning.
					unsupportedWarning();
				}
			}
			browserTested = true;
		}
		
		dojo.addOnLoad(testBrowser);
		
	</script>
	
	<!-- Audio Autoplay detection -->
	<script type="text/javascript" src="resources/can-autoplay.js"></script>
	<script type="text/javascript">		
		var autoplayRetries = 3;
		
		function detectAutoplay() {
			canAutoplay.audio().then(({result}) => {
				if (result === false) {
					// Autoplay disabled
					autoplayRetries -= 1;
					if (autoplayRetries)
						setTimeout(detectAutoplay, 500);
					else
						showAutoplayWarning();
				}
			});
		}
		
		function showAutoplayWarning() {
			$("autoplayDisabled").style.display = "";
			hideAutoplayWarning(false);
		}
		
		function hideAutoplayWarning(forceHide) {
			if (forceHide)
				$("autoplayDisabled").style.display = "none";
			else if (document.hidden)
				setTimeout(hideAutoplayWarning, 500);
			else
				setTimeout(function() {
					$("autoplayDisabled").style.display = "none";
				}, 10000);
		}
		
		dojo.addOnLoad(detectAutoplay);
	</script>
	
	<!--[if lt IE 10]>
		<script type="text/javascript">
			// Don't try to run in IE10 or earlier
			setInterval(function() {
				document.write("Unsupported browser!");
			}, 100);
		</script>
	<![endif]-->
		
	<style>
		html > body {
			/* A soft light gray background */
			background-color: #F7F7F7;
		}
		
		input::-ms-reveal, input::-ms-clear {
			/* IE/Edge workaround */
			display: none !important;
		}
		
		#warningsBkg {
			background-color: #D2D2D280;
			position: fixed;
			top: 0px;
			left: 0px;
			width: 100%;
			height: 100%;
			z-index: 10;
		}
		
		#warnings {
			max-width: 40%;
			position: fixed;
			top: 50%;
			left: 50%;
			transform: translate(-50%, -50%);
			ms-transform: translate(-50%, -50%);
			webkit-transform: translate(-50%, -50%);
			background-color: #FFFFFF;
			padding: 8px;
			color: #444444;
			font-size: 15px;
			text-align: justify;
			text-indent: 2em;
		}
		
		#warnings h1 {
			text-weight: bold;
			font-size: 16px;
		}
		
		#loginForm {
			width: 350px;
			margin: 50px auto;
			padding: 8px;
			border-radius: 6px;
			font-size: 15px;
			background-color: #FFFFFF;
			color: #444444;
			box-shadow: 0 0 1em #AAAAAA;
		}
		
		#loginForm label, #loginForm input, #loginForm span {
			display: block;
			padding: 6px 4px;
		}
		
		#loginForm a, #loginForm a:hover, #loginForm a:visited {
			font-size: 13px;
			padding: 4px;
		}
		
		#loginForm input {
			height: 40px;
			border-radius: 4px;
			box-sizing: border-box;
			font-size: 20px;
			border: 1px solid #888888;
		}
		
		#loginForm input[type="text"], #loginForm input[type="password"] {
			width: 100%;
		}
		
		#title {
			font-size: 30px;
			text-align: center;
		}
		
		#inputs {
			margin: 20px 0px;
		}
		
		#submit {
			width: 70%;
			margin: 15px auto;
		}
		
		#pswDiv {
			position: relative;
		}
		
		#pswBtn {
			position: absolute;
			top: 5px;
			right: 5px;
			width: 30px;
			height: 30px;
			border-radius: 4px;
			cursor: pointer;
		}
		
		#pswBtn:hover {
			background-color: #B7B7B750;
		}
		
		.errorMessage {
			font-size: 12px;
			color: #FF0000;
			text-align: center;
		}
		
		.link {
			display: block;
			text-align: right;
			margin: 6px 0;
		}

		/* Audio autoplay warning DIV */
		#autoplayDisabled {
			position: fixed;
			top: 15px;
			left: 20px;
			font-size: 14px;
			font-family: Arial, Helvetica, sans-serif;
			text-align: center;
			font-weight: bold;
		}

		#autoplayDisabled .arrow {
			margin: auto;
			width: 0;
			height: 0;
			border-left: 9px solid transparent;
			border-right: 9px solid transparent;
			border-bottom: 15px solid #FF6900FA;
		}
		
		#autoplayDisabled .warning {
			width: 250px;
			background-color: #FF6900FA;
			color: white;
			
			padding: 10px;
			border-radius: 10px;
		}
	</style>
	
	<div id="autoplayDisabled" style="display: none;" onclick="hideAutoplayWarning(true);">
		<div class="arrow"></div>
		<div class="warning"><fmt:message key="login.autoplayDisabled"/></div>
	</div>

	<span id="unsupportedMsg" style="display: none;"><fmt:message key="login.unsupportedBrowser"/></span>
	
	<div id="warningsBkg" style="display: none;">
		<div id="warnings" class="borderDiv">
			<img class="ptr" style="float: right;" src="images/cross.png" onclick="hide('warningsBkg');">
			<div style="clear: both; height: 10px;"></div>
			<span class="content"></span>
		</div>
	</div>
	
	<form action="login.htm" method="post">
		<div id="loginForm" class="borderDiv">
			<span id="title"><fmt:message key="header.login"/></span>
			
			<div id="inputs">
				<!-- Username field -->	
				<spring:bind path="login.username">
					<label for="username"><fmt:message key="login.userId"/></label>
					<input id="username" type="text" name="username" value="${fn:escapeXml(status.value)}" maxlength="40" autofocus>
					<c:if test="${status.error}">
						<span class="errorMessage">${status.errorMessage}</span>
					</c:if>
				</spring:bind>
				
				<!-- Password field -->
				<spring:bind path="login.password">
					<label for="password"><fmt:message key="login.password"/></label>
					<div id="pswDiv">
						<input id="password" type="password" name="password" value="${fn:escapeXml(status.value)}" maxlength="25"/>
						<img id="pswBtn" src="images/show-password.svg" onclick="togglePassword();" onerror="usePng(this);">
					</div>
					<c:if test="${status.error}">
						<span class="errorMessage">${status.errorMessage}</span>
					</c:if>
				</spring:bind>
				
				<!-- Generic error messages -->
				<spring:bind path="login">
					<c:if test="${status.error}">
						<span class="errorMessage">
							<c:forEach items="${status.errorMessages}" var="error">
								<c:out value="${error}"/><br>
							</c:forEach>
						</span>
					</c:if>
				</spring:bind>
				
				<!-- Help link -->
				<div class="link">
					<a class="ptr" onclick="showHelp();"><fmt:message key="common.help"/></a>
				</div>
			</div>
			
			<!-- Submit Button -->
			<input id="submit" class="coloredButton" type="submit" value="<fmt:message key="login.loginButton"/>">
		</div>
	</form>
</tag:page>
