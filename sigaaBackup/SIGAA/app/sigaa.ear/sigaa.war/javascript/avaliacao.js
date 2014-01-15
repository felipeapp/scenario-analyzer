
function marcarResposta(source, resp) {
	var td = source.parentNode;
	var element = td.getElementsByTagName('input')
	element[0].value = resp;
	
	var imgs = td.getElementsByTagName('img')
	for (var i = 0; i < imgs.length; i++) {
		if (imgs[i] != source) {
			if (imgs[i].src.indexOf('disable') == -1)
				imgs[i].src = imgs[i].src.replace('.png', '_disable.png');
		} else {
			imgs[i].src = imgs[i].src.replace('_disable.png', '.png');
		}
	}
}
