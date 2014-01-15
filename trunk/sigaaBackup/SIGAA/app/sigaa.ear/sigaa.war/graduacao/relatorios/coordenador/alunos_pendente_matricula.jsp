<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<style>
	tr.curso td {padding: 15px 0 0; border-bottom: 1px solid #555}
	tr.header td {padding: 3px ; border-bottom: 1px solid #555; background-color: #eee;}
	tr.discente td {border-bottom: 1px solid #888; font-weight: bold; padding-top: 7px;}
	tr.componentes td {padding: 4px 2px 2px ; border-bottom: 1px dashed #888;}
	tr.componentes td.assinatura { padding:2px; border-bottom: 1px solid #888;  width: 40%;}
</style>
<f:view>
	<h:outputText value="#{ relatoriosCoordenador.create }" />
	<c:set var="relatorio_" value="${ relatoriosCoordenador.relatorio }"/>

	<hr>
	<table width="100%">
		<caption><b>RELAÇÃO DE ALUNOS PENDENTES DE MATRÍCULA</b></caption>
		<tr>
			<td width="85">Curso:</td>
			<td><b>${relatoriosCoordenador.cursoAtualCoordenacao.nomeCompleto}</b></td>
		</tr>
		<tr>
			<td>Ano-Período:</td>
			<td><b><h:outputText value="#{relatoriosCoordenador.ano}"/>.<h:outputText value="#{relatoriosCoordenador.periodo}"/></b></td>
		</tr>
	</table>
	<hr>
	<table cellspacing="1" width="100%" style="font-size: 10px;">
		<caption><b>Total de Registros: ${ fn:length(relatorio_) }</b></caption>
		<tr class="header">
			<td>Ingresso</td>
			<td>Matrícula</td>
			<td>Nome</td>
			<td>Email<td>
		</tr>
		<c:forEach var="linha" items="${ relatorio_ }" varStatus="status">
			<tr class="componentes">
				<td> ${ linha.value.ano }.${ linha.value.periodo } </td>
				<td> ${ linha.value.matricula } </td>
				<td> ${ linha.key } </td>
				<td> ${ linha.value.email } </td>
			</tr>
		</c:forEach>
	</table>
	
	
	<br/>
	<div align="center" class="naoImprimir" style="color: blue;font-weight:bold;"><a onclick="mostrarEmails()" >E-mails dos alunos</a></div>
	<br/> 
	<div id="divEmails" align="center" style="display: none;border: 1px solid;" class="naoImprimir">
		<h:outputText>
		<c:forEach var="linha" items="${ relatorio_ }" varStatus="status">
			${ linha.value.email } <c:if test="${not status.last}">;</c:if>
		</c:forEach>
		</h:outputText>
	</div> 
</f:view>

<script type="text/javascript">

function mostrarEmails() {
	$("divEmails").toggle();
}
</script>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
