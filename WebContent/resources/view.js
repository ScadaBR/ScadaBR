/*
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
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

mango.view = {};
mango.view.graphic = {};

mango.view.setEditing = false;
mango.view.setEditingContent = null;

mango.view.setData = function(stateArr) {
    var state;
    for (var i=0; i<stateArr.length; i++) {
        state = stateArr[i];
        
        // Check that the point exists. Ignore if it doesn't.
        if (!$("c"+ state.id))
            throw "Can't find point view c"+ state.id;
        
        mango.view.setContent(state);
        
        if ($("c"+ state.id +"Controls")) {
            if (state.info != null)
                $set("c"+ state.id +"Info", state.info);
            if (state.change != null) {
                if (state.change) 
                    show("c"+ state.id +"ChangeMin");
                
                if (mango.view.setEditing)
                    // If the set value is being edited, save the content
                    mango.view.setEditingContent = state.change;
                else
                    $set("c"+ state.id +"Change", state.change);
            }
            if (state.chart != null) {
            	 $set("c"+ state.id +"Chart", state.chart);
            }
               
        }
        
        mango.view.setMessages(state);
    }
};

mango.view.setMessages = function(state) {
    var warningNode = $("c"+ state.id +"Warning");
    if (warningNode && state.messages != null) {
		// Change icon according to alarm level
		var levels = ["flag_red", "flag_orange", "flag_yellow", "flag_blue", "warn",
					  "flag_red_off", "flag_orange_off", "flag_yellow_off", "flag_blue_off"];
		for (var i in levels) {
			if (state.messages.search(levels[i] + ".png") > 0) {
				document.querySelector("#c" + state.id + "Warning > img").src = "images/" + levels[i] + ".png";
				break;
			}
		}
		// Set alarm messages to view component
        $set("c"+ state.id +"Messages", state.messages);
        if (state.messages)
            show(warningNode);
        else
            hide(warningNode);
    }
};

mango.view.setContent = function(state) {
    if (state.content != null) {
    	var comp = $("c"+ state.id +"Content");
    	//lastImage = comp.childNodes[0].src;
    	
    	//state.content = <img src="chart/1297885565049_60000_3_w500_h300.png" alt="Gráfico"/>
    	//comp.innerHTML = state.content;	
    	if(state.graph && comp.childNodes[0]) {
        	newImageSrc = extractSrcAttribute(state.content);
        	comp.childNodes[0].src = newImageSrc;
    	} else {
    		comp.innerHTML = state.content;
    	}
    	
		// New dynamic graphics
        var dyn = $("dyn"+ state.id);
        if (dyn) {
			mango.view.graphic.configureComponent(dyn, state.id);
        }
        
        // Look for scripts in the content.
        mango.view.runScripts(comp);
    }
};

function extractSrcAttribute(string) {
	string = string.replace("<img ","");
	string = string.replace(string.match("alt=.*"),"");
	string = string.replace("src=\"","");
	return string.replace("\"","");
}

mango.view.runScripts = function(node) { 
    var arr = [];
    mango.view.findScripts(node, arr);
    for (var i=0; i<arr.length; i++)
        eval(arr[i]);
}

mango.view.findScripts = function(node, arr) {
    for (var i=0; i<node.childNodes.length; i++) {
        var child = node.childNodes[i];
        if (child.tagName == "SCRIPT")
            arr.push(child.innerHTML);
        mango.view.findScripts(child, arr);
    }
}


mango.view.showChange = function(divId, xoffset, yoffset) {
    mango.view.setEditing = true;
    var theDiv = $(divId);
    showMenu(theDiv, xoffset, yoffset);
    
    // Automatically select the text in text boxes
    var inputElems = theDiv.getElementsByTagName("input");
    for (var i=0; i<inputElems.length; i++) {
        if (inputElems[i].id.startsWith("txtChange")) {
            var temp = inputElems[i].value;
            inputElems[i].value += " ";
            inputElems[i].value = temp;
            inputElems[i].select();
        }
    }
};

mango.view.hideChange = function(divId) {
    if ($(divId))
        hideLayer($(divId));
    mango.view.setEditing = false;
    if (mango.view.setEditingContent != null) {
        $set(divId, mango.view.setEditingContent);
        mango.view.setEditingContent = null;
    }
};

mango.view.showChart = function(componentId, event, source) {
	if (isMouseLeaveOrEnter(event, source)) {
		// Take the data in the chart textarea and put it into the chart layer div
		$set('c'+ componentId +'ChartLayer', $get('c'+ componentId +'Chart'));
        showMenu('c'+ componentId +'ChartLayer', 16, 0);
	}
}

mango.view.hideChart = function(componentId, event, source) {
	if (isMouseLeaveOrEnter(event, source))
		hideLayer('c'+ componentId +'ChartLayer');
}

function vcOver(base, amt) {
    if (!amt)
        amt = 10;
    setZIndex(base, amt);
    showLayer(base + 'Controls');
};

function vcOut(base) {
    setZIndex(base, 0);
    hideLayer(base +'Controls');
};


//
// Anonymous views
mango.view.initAnonymousView = function(viewId) {
    mango.view.setPoint = mango.view.anon.setPoint;
    // Tell the long poll request that we're interested in anonymous view data, and not max alarm.
    mango.longPoll.pollRequest.maxAlarm = false;
    mango.longPoll.pollRequest.anonViewId = viewId;
    mango.view.anon.viewId = viewId;
};

mango.view.anon = {};
mango.view.anon.setPoint = function(pointId, viewComponentId, value) {
    show("c"+ viewComponentId +"Changing");
    mango.view.hideChange("c"+ viewComponentId +"Change");
    ViewDwr.setViewPointAnon(mango.view.anon.viewId, viewComponentId, value, function(viewComponentId) {
        hide("c"+ viewComponentId +"Changing");
        MiscDwr.notifyLongPoll(mango.longPoll.pollSessionId);
    });
};


//
// Normal views
mango.view.initNormalView = function() {
    mango.view.setPoint = mango.view.norm.setPoint;
    // Tell the long poll request that we're interested in view data.
	mango.longPoll.pollRequest.view = true;
	
	// Specify view Id
	mango.longPoll.pollRequest.viewId = 0;
	if (window.location.href.includes("viewId="))
		mango.longPoll.pollRequest.viewId = parseInt(window.location.href.match(/viewId=(\d*)[^\d]?/)[1]);
};

mango.view.norm = {};
mango.view.norm.setPoint = function(pointId, viewComponentId, value) {
    show("c"+ viewComponentId +"Changing");
    mango.view.hideChange("c"+ viewComponentId +"Change");
    ViewDwr.setViewPoint(viewComponentId, value, function(viewComponentId) {
        hide("c"+ viewComponentId +"Changing");
        MiscDwr.notifyLongPoll(mango.longPoll.pollSessionId);
    });
};


//
// View editing
mango.view.initEditView = function() {
    // Tell the long poll request that we're interested in view editing data.
    mango.longPoll.pollRequest.viewEdit = true;
    mango.view.setData = mango.view.edit.setData;
};

mango.view.edit = {};
mango.view.edit.iconize = false;
mango.view.edit.setData = function(stateArr) {
    var state, node;
    for (var i=0; i<stateArr.length; i++) {
        state = stateArr[i];
        
        // Check that the point exists. Ignore if it doesn't.
        if (!$("c"+ state.id))
            continue;
            //throw "Can't find point view c"+ state.id;
        
        if (state.content != null) {
            if (!state.content)
                state.content = "<img src='images/logo_icon.gif'/>";
            
            if (mango.view.edit.iconize)
                $("c"+ state.id).savedState = state;
            else
                mango.view.setContent(state);
        }
        
        if (state.info != null) {
            node = $("c"+ state.id +"Info");
            if (node)
                node.innerHTML = state.info;
        }
        mango.view.setMessages(state);
    }
};


//
// Watchlist
mango.view.initWatchlist = function() {
    mango.view.setPoint = mango.view.watchList.setPoint;
    // Tell the long poll request that we're interested in watchlist data.
    mango.longPoll.pollRequest.watchList = true;
};

mango.view.watchList = {};
mango.view.watchList.reset = function() {
    MiscDwr.resetWatchlistState(mango.longPoll.pollSessionId);
};

mango.view.watchList.setPoint = function(pointId, componentId, value) {
    startImageFader("p"+ pointId +"Changing");
    mango.view.hideChange("p"+ pointId +"Change");
    WatchListDwr.setPoint(pointId, componentId, value, function(pointId) {
        stopImageFader("p"+ pointId +"Changing");
        MiscDwr.notifyLongPoll(mango.longPoll.pollSessionId);
    });
};

mango.view.watchList.setData = function(stateArr) {
    for (var i=0; i<stateArr.length; i++)
        mango.view.watchList.setDataImpl(stateArr[i]);
};
    
mango.view.watchList.setDataImpl = function(state) {
    // Check that the point exists. Ignore if it doesn't.
    if (state && $("p"+ state.id)) {
        var node;
        if (state.value != null) {
            node = $("p"+ state.id +"Value");
            node.innerHTML = state.value;
            dojo.html.addClass(node, "viewChangeBkgd");
            setTimeout('mango.view.watchList.safeRemoveClass("'+ node.id +'", "viewChangeBkgd")', 2000);
        }
        
        if (state.time != null) {
            node = $("p"+ state.id +"Time");
            node.innerHTML = state.time;
            dojo.html.addClass(node, "viewChangeBkgd");
            setTimeout('mango.view.watchList.safeRemoveClass("'+ node.id +'", "viewChangeBkgd")', 2000);
        }
        
        if (state.change != null) {
            show($("p"+ state.id +"ChangeMin"));
            if (!mango.view.setEditing)
                $set("p"+ state.id +"Change", state.change);
        }
        
        if (state.chart != null) {
            show($("p"+ state.id +"ChartMin"));
            $set("p"+ state.id +"Chart", state.chart);
        }
        
        if (state.messages != null)
            $("p"+ state.id +"Messages").innerHTML = state.messages;
        //else
        //    $("p"+ state.id +"Messages").innerHTML = "";
    }
};

mango.view.watchList.safeRemoveClass = function(nodeId, className) {
    var node = $(nodeId);
    if (node)
        dojo.html.removeClass(node, className);
};


mango.view.executeScript = function(scriptId) {
	ViewDwr.executeScript(scriptId, function(success) {
		if(!success) {
		} 
    });
};

//
// Point details
mango.view.initPointDetails = function() {
    mango.view.setPoint = mango.view.pointDetails.setPoint;
    // Tell the long poll request that we're interested in point details data.
    mango.longPoll.pollRequest.pointDetails = true;
};

mango.view.pointDetails = {};
mango.view.pointDetails.setPoint = function(pointId, componentId, value) {
    startImageFader("pointChanging");
    DataPointDetailsDwr.setPoint(pointId, componentId, value, function(componentId) {
        stopImageFader("pointChanging");
        MiscDwr.notifyLongPoll(mango.longPoll.pollSessionId);
    });
};

mango.view.pointDetails.setData = function(state) {
    if (state.value != null)
        $("pointValue").innerHTML = state.value;
    
    if (state.time != null)
        $("pointValueTime").innerHTML = state.time;
    
    if (state.change != null) {
        show($("pointChangeNode"));
        $set("pointChange", state.change);
    }
    
    if (state.messages != null)
        $("pointMessages").innerHTML = state.messages;
};


//
// Custom views
mango.view.initCustomView = function() {
    mango.view.setData = mango.view.custom.setData;
    mango.view.setPoint = mango.view.custom.setPoint;
    // Tell the long poll request that we're interested in custom view data, and not max alarm.
    mango.longPoll.pollRequest.maxAlarm = false;
    mango.longPoll.pollRequest.customView = true;
};

mango.view.custom = {};
mango.view.custom.functions = {};
mango.view.custom.setData = function(stateArr) {
    var node;
    for (var i=0; i<stateArr.length; i++) {
        var func = mango.view.custom.functions["c"+ stateArr[i].id];
        if (func)
            func(stateArr[i].value, new Date(stateArr[i].time));
        else {
            node = $("c"+ stateArr[i].id);
            if (node)
                $set(node, stateArr[i].value);
        }
    }
}

mango.view.custom.setPoint = function(xid, value, callback) {
    CustomViewDwr.setCustomPoint(xid, value, function() {
        if (callback)
            callback();
        MiscDwr.notifyLongPoll(mango.longPoll.pollSessionId);
    });
};

mango.view.teste = function() {
	alert('oi, talvez isso seja um Easter Egg');
    
};

//
// New dynamic graphics engine
mango.view.graphic.dynamic = {};

mango.view.graphic.configureComponent = function (dyn, elementId) {
	var elementName = JSON.parse(dyn.value).graphic;
	
	if (elementName.length) {
		// Create a Static div
		if (!$("c" + elementId + "Static")) {
			var div = document.createElement("div");
			div.id = "c" + elementId + "Static";
			$("c" + elementId).appendChild(div);
		}
		
		// Clear an existing Static div
		if (!$("c" + elementId + "Static").classList.contains(elementName)) {
			$("c" + elementId + "Static").innerHTML = "";
			$("c" + elementId + "Static").className = elementName;
		}
		
		// Update the component
		var percentage = JSON.parse(dyn.value).value;
		var width = document.querySelector("#c" + elementId + "Content > img").width || $("c" + elementId).clientWidth;
		var height = document.querySelector("#c" + elementId + "Content > img").height || $("c" + elementId).clientHeight;
		mango.view.graphic.runDynamicFunction(elementName, elementId, percentage, width, height);
	}	
}

mango.view.graphic.runDynamicFunction = function (funcName, elementId, percentage, width, height) {
	// If function already exists, run it
	if(mango.view.graphic.dynamic[funcName])
		mango.view.graphic.dynamic[funcName](elementId, percentage, width, height);
	// Else, add function's custom script
	else {
		mango.view.graphic.addCustomScript("graphics/_dynamicScripts/" + funcName + "/custom.js");
		setTimeout(function() {
			if(mango.view.graphic.dynamic[funcName])
				mango.view.graphic.dynamic[funcName](elementId, percentage, width, height);
			else
				mango.view.graphic.runDynamicFunction(funcName, elementId, percentage, width, height);
		}, 50);
	}
}

mango.view.graphic.addCustomScript = function(source) {
	if(!document.querySelector("script[src*='" + source + "']")) {
		var newScript = document.createElement("script");
		newScript.src = source;
		document.querySelector("body > div#mainContent").appendChild(newScript);
		// console.log(source + " added.");
	}
}
