<style>
#info-sistema span.sair-sistema,
	#info-sistema #tempoSessao{
	display: inline;
	right: inherit;
	position: relative;
	top: 0;
	padding-right: inherit;
}

#info-sistema div.dir{
	top:0;
	right:0;
	text-align: right;
	width: auto;
	position: absolute;
	margin:0;
	padding: 0;
	height:16px;
	display: inline;
	padding-right:5px;
	color: #FFF;
}
#info-sistema span.acessibilidade{
	padding: 0 5px;
	border: solid #D99C44;
	border-width: 0 1px 0 0;
	margin: 0 5px;
	padding: 0 5px;
	float: left;
}
#info-sistema span.acessibilidade a{
	font-size: 1.1em;
	font-weight: bolder;
	color: #FFF;
	margin-left: 2px;
	letter-spacing: 0.1px;
	word-spacing: 0.1px;
}
#info-sistema span.acessibilidade a.fonteMenor strong{
	font-size: 0.9em;
}	
</style>

<script>
	var status = getCookie("mysheet");
	
	
	
	if ( status == 'mono' ) {
		document.write("<span class='acessibilidade'>");
		document.write(" <form id='switchform'>");
		document.write("   <input type='image' src='${ctx}/img/greyscale.png' name='choice' value='none' onclick='chooseStyle(this.value, 1)' width='16px;' style='padding-top: 2px;' alt='Padrão' title='Padrão'>");
		document.write("   <input type='image' src='${ctx}/img/contraste.png' name='choice' value='contraste' onclick='chooseStyle(this.value, 1)' width='16px;' style='padding-top: 2px;' alt='Contraste' title='Contraste'>");
		document.write(" </form>");
		document.write("</span>");

	} else if ( status == 'contraste' ) {
		document.write("<span class='acessibilidade'>");
		document.write(" <form id='switchform'>");
		document.write("   <input type='image' src='${ctx}/img/greyscale2.png' name='choice' value='mono' onclick='chooseStyle(this.value, 1)' width='16px;' style='padding-top: 2px;' alt='Monocromático' title='Monocromático'>");
		document.write("   <input type='image' src='${ctx}/img/contraste.png' name='choice' value='none' onclick='chooseStyle(this.value, 1)' width='16px;' style='padding-top: 2px;' alt='Padrão' title='Padrão'>");
		document.write(" </form>");
		document.write("</span>");
	
	} else {
		document.write("<span class='acessibilidade'>");
		document.write(" <form id='switchform'>");
		document.write("   <input type='image' src='${ctx}/img/greyscale.png' name='choice' value='mono' onclick='chooseStyle(this.value, 1)' width='16px;' style='padding-top: 2px;' alt='Monocromático' title='Monocromático'>");
		document.write("   <input type='image' src='${ctx}/img/contraste.png' name='choice' value='contraste' onclick='chooseStyle(this.value, 1)' width='16px;' style='padding-top: 2px;' alt='Contraste' title='Contraste'>");
		document.write(" </form>");
		document.write("</span>");
	}

	
</script>