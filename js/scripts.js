var flipAnim = 0;
window.onload = function(){
	fixLayout();
	putLogoSpr();
}

window.onresize = function(e){
	fixLayout();
	fixLogoSpr();

}

window.onscroll = function(){
	fixLogoSpr();
}

function fixLayout(){
	var header = document.getElementById("header");
	var middle = document.getElementById("middle");
	var footer = document.getElementById("footer");
	var pageCont = document.getElementById("pageCont");
	var menu = document.getElementById("menu");

	var margins = 12;
	
	middle.style.minHeight = (document.body.offsetHeight-header.offsetHeight-footer.offsetHeight)+"px";

	//pageCont.style.width = (document.body.offsetWidth - menu.offsetWidth - margins*2)+"px";
	pageCont.style.width = "700px";
	pageCont.style.marginLeft = (menu.offsetWidth + margins)+"px";

	menu.style.marginTop = (-menu.offsetHeight/2)+"px";

}

function putLogoSpr(){
	var cont = document.getElementById("cont");

	cont.innerHTML = "<div class=\"logospr\" id=\"logospr\" onclick=\"window.location='#'; flip();\"></div>"+cont.innerHTML;
}

function flip(){
	window.flipAnim = 1 - window.flipAnim;
	fixLogoSpr();
}

function fixLogoSpr(){
	var frames = 37-1;
	var rows = 5;
	var cols = 8;
	
	var scrollTopMax = document.documentElement.scrollHeight - document.documentElement.clientHeight;
	var currentFrame = Math.round(Math.abs(flipAnim-document.documentElement.scrollTop/scrollTopMax)*frames);
	if (isNaN(currentFrame) || currentFrame >= frames)
		currentFrame = frames;
	var sprDiv = document.getElementById("logospr");

	var ofTop = Math.floor(currentFrame/cols)*sprDiv.offsetHeight;
	var ofLeft = (currentFrame%cols)*sprDiv.offsetWidth;

	sprDiv.style.backgroundPosition = "-"+ofLeft+"px -"+ofTop+"px";
	
}
