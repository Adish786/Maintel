<html>
<head>
<script type='text/javascript'>
	function load(){
		var ifr = document.getElementById("ifr");
		var ld = document.getElementById("ld");
		ld.style.visibility="hidden";
		ifr.src="/debug/flamegraph.svg";
		ifr.style.visibility="visible";
		ifr.style.height="100%";
		ifr.style.width="100%";
	}	

	function updateTime(){
		var e = document.getElementById("sec");
		var s = parseInt(e.innerText);
		if (s === 1){
			load();
		} else {
			setTimeout(updateTime,1000);
		}
		e.innerText = (s-1);
	}
</script>
</head>
<body onload="setTimeout(updateTime,1000)">
<div id='ld'>
Loading in <span id="sec">${model.towait}</span>
</div>
<iframe id='ifr' style='visiblity:hidden' frameborder='0'></iframe>
</body>
</html>