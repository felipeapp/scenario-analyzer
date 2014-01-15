<%@include file="/public/include/cabecalho.jsp"%>
<style>
	table.visualizacao th {font-weight: bold;}
</style>
<f:view>
	<f:loadBundle basename="br.ufrn.sigaa.sites.jsf.Mensagens" var="idioma"/>
	
	<h2 class="title"><h:outputText value="#{idioma.estagios}" /></h2>

	<c:set var="oferta" value="#{consultaPublicaCursos.ofertaEstagio}"/>
	<%@include file="/estagio/oferta_estagio/include/_oferta.jsp"%>
	
	<br/>
	<center>
		<a href="javascript: history.go(-1);"> << <h:outputText value="#{idioma.voltar}"/> </a>
	</center>
</f:view>

<%@include file="/public/include/rodape.jsp"%>
