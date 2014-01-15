<style type="text/css">
body {
	margin:0;
	padding:0;
	font-weight: none;
}
a:link {
	font-weight: inherit;
}
</style>

<link rel="stylesheet" type="text/css" href="/sigaa/css/tabview.css" />
<script type="text/javascript" src="/sigaa/javascript/yahoo-dom-event.js"></script>
<script type="text/javascript" src="/sigaa/javascript/element-min.js"></script>
<script type="text/javascript" src="/sigaa/javascript/tabview-min.js"></script>
</head>

<h:form>
<body class="yui-skin-sam">

<div id="demo" class="yui-navset">
    <ul class="yui-nav">
        <li class="selected"><a href="#tab1" id="ensino" title="Ensino" style="font-weight: none;"><em>Ensino</em></a></li>
        <li><a href="#tab2" id="Pesquisa" title="Pesquisa"><em>Pesquisa</em></a></li>
        <c:if test="${(usuario.discenteAtivo.graduacao || usuario.discenteAtivo.tecnico || usuario.discenteAtivo.stricto)}">
        	<li><a href="#tab3" id="Extensão"><em>Extensão</em></a></li>
        </c:if>
        <c:if test="${(usuario.discenteAtivo.graduacao)}">
        	<li><a href="#tab4" id="Monitoria" title="Monitoria"><em>Monitoria</em></a></li>
        </c:if>
        <c:if test="${(usuario.discenteAtivo.graduacao || usuario.discenteAtivo.tecnico || usuario.discenteAtivo.stricto || usuario.discenteAtivo.lato)}">
        	<li><a href="#tab5" id="associadas" title="associadas"><em>Ações Associadas</em></a></li>
        </c:if>
        <li><a href="#tab5" id="Biblioteca" title="Biblioteca"><em>Biblioteca</em></a></li>
        <c:if test="${(usuario.discenteAtivo.graduacao || usuario.discenteAtivo.tecnico || usuario.discenteAtivo.stricto 
			|| usuario.discenteAtivo.lato ) && usuario.discente.regular }">
        	<li><a href="#tab6" id="Bolsas" title="Bolsas"><em>Bolsas</em></a></li>
        </c:if>
        <li><a href="#tab7" id="Virtual" title="Virtual"><em>Virtual</em></a></li>
        <li><a href="#tab7" id="Outros" title="Outros"><em>Outros</em></a></li>

    </ul>            
	    <div class="yui-content">
	        <div class="corpo"><%@include file="/portais/discente/include/ensino.jsp"%></div>
	        <div class="corpo"><%@include file="/portais/discente/include/pesquisa.jsp"%></div>
	        <c:if test="${(usuario.discenteAtivo.graduacao || usuario.discenteAtivo.tecnico || usuario.discenteAtivo.stricto)}">
		        <div class="corpo"><%@include file="/portais/discente/include/extensao.jsp"%></div>
	        </c:if>
	        <c:if test="${(usuario.discenteAtivo.graduacao)}">
		        <div class="corpo"><%@include file="/portais/discente/include/monitoria.jsp"%></div>
		    </c:if>
	        <c:if test="${(usuario.discenteAtivo.graduacao || usuario.discenteAtivo.tecnico || usuario.discenteAtivo.stricto || usuario.discenteAtivo.lato)}">
    	    	<div class="corpo"><%@include file="/portais/discente/include/associada.jsp"%></div>
        	</c:if>
	        <div class="corpo"><%@include file="/portais/discente/include/biblioteca.jsp"%></div>
	        <c:if test="${(usuario.discenteAtivo.graduacao || usuario.discenteAtivo.tecnico || usuario.discenteAtivo.stricto 
				|| usuario.discenteAtivo.lato ) && usuario.discente.regular }">
	        	<div class="corpo"><%@include file="/portais/discente/include/bolsas.jsp"%></div>
	        </c:if>
	        <div class="corpo"><%@include file="/portais/discente/include/virtuais.jsp"%></div>
	        <div class="corpo"><%@include file="/portais/discente/include/outros.jsp"%></div>
	    </div>
</div>

<script>
(function() {
    var tabView = new YAHOO.widget.TabView('demo');

    YAHOO.log("The example has finished loading; as you interact with it, you'll see log messages appearing here.", "info", "example");

})();

</script>

</body>
</h:form>