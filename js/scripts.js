window.onload = function(){
	fixLayout();
}

window.onresize = function(){
	fixLayout();
}

function fixLayout(){
	var header = document.getElementById("header");
	var middle = document.getElementById("middle");
	var footer = document.getElementById("footer");
	var pageCont = document.getElementById("pageCont");
	var menu = document.getElementById("menu");

	var margins = 12;
	
	middle.style.minHeight = (document.body.offsetHeight-header.offsetHeight-footer.offsetHeight+header.offsetHeight)+"px";

	pageCont.style.width = (document.body.offsetWidth - menu.offsetWidth - margins*2)+"px";
	pageCont.style.marginLeft = (menu.offsetWidth + margins)+"px";

	menu.style.marginTop = (-menu.offsetHeight/2)+"px";
}
