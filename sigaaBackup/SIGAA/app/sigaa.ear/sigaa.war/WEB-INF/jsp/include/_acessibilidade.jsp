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
	.rich-tabpanel-content {
		font-size: inherit;
	}	
</style>
<span class="acessibilidade">
	<a class="fonteMaior" title="Aumentar a texto" alt="Aumentar a texto" href="#">
		<strong>A</strong>+
	</a>
	<a class="fonteMenor" title="Diminuir o texto" alt="Diminuir o texto" href="#">
		<strong>A</strong>-
	</a>
</span>
<script type="text/javascript" src="/shared/javascript/jquery/jquery-1.4.4.min.js"></script>
<script src="${ctx}/javascript/jquery.fontsize.js" type="text/javascript" ></script>
<script type="text/javascript" >
	redimensionarFonte('.acessibilidade', '#conteudo', 11, 11, 14);
</script>