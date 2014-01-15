
<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<link href="/shared/javascript/ext-2.0.a.1/resources/css/box.css" rel="stylesheet" type="text/css" />
<link href="/shared/javascript/ext-2.0.a.1/resources/css/button.css" rel="stylesheet" type="text/css" />
<link href="/shared/javascript/ext-2.0.a.1/resources/css/core.css" rel="stylesheet" type="text/css" />
<link href="/shared/javascript/ext-2.0.a.1/resources/css/dialog.css" rel="stylesheet" type="text/css" />
<link href="/shared/javascript/ext-2.0.a.1/resources/css/panel.css" rel="stylesheet" type="text/css" />
<link href="/shared/javascript/ext-2.0.a.1/resources/css/progress.css" rel="stylesheet" type="text/css" />
<link href="/shared/javascript/ext-2.0.a.1/resources/css/window.css" rel="stylesheet" type="text/css" />
<link href="/sigaa/javascript/processamento.css" rel="stylesheet" type="text/css" />

<h2>Geração dos relatórios de processamento</h2>

<f:view>

<h:outputText value="#{ processamentoMatricula.criarArquivosResultado }"/>

	<div id="progresso" style="display: none">
		<h1>Turmas Processadas </h1>
	
		<div id="pgb"></div>
		<span class="status"><strong>Status: </strong> <span id="pgbtext"></span></span>
		<input type="hidden" id="tipoProgresso" value="resultado"/>
	</div>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

<script type="text/javascript" src="/shared/loadScript?src=javascript/ext-2.0.a.1/ext-base.js"> </script>
<script type="text/javascript" src="/shared/loadScript?src=javascript/ext-2.0.a.1/ext-all.js"> </script>
<script type="text/javascript" src="/sigaa/javascript/processamento.js"></script>