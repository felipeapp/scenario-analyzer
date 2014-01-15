<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<style>
<!--
tr.componentes td {padding: 2px; border-bottom: 1px dashed #888}
tr.header td {padding: 2px; border-bottom: 1px solid #888; font-weight: bold;}
-->
</style>

<f:view>
	<h2>Lista de trancamentos por disciplina</h2>

	<div id="parametrosRelatorio">
	<table >
		<tr>
			<th>Unidade:</th>
			<td>${usuario.vinculoAtivo.unidade.nome}</td>
		</tr>
		<tr>
			<th>Ano-Período:</th>
				<td><h:outputText value="#{relatoriosTecnico.ano}"/>.<h:outputText value="#{relatoriosTecnico.periodo}"/>
			</td>
		</tr>
		</table>
	</div>
  <br /><br />
  
  <table cellspacing="1" width="100%" style="font-size: 10px;">
	<c:set var="disciplina"/>
	<c:set var="totalDisciplina"/>
	<c:forEach items="${relatoriosTecnico.lista}" var="linha">
		<c:set var="disciplinaAtual" value="${linha.id_disciplina}"/>
			
		<c:if test="${disciplina != disciplinaAtual}">
			<c:if test="${not empty disciplina}">
	    		<tr>
	    			<td colspan="2" align="right"><b>${totalDisciplina} trancamento(s)</b></td>
	    		</tr>
			</c:if>
			<c:set var="totalDisciplina" value="0"/>
			<c:set var="disciplina" value="${disciplinaAtual}"/>
			<tr>
				<td colspan="2">
					<br>
					<b>${linha.codigo} - ${linha.nome_disciplina}</b>
					<hr>
				</td>
			</tr>
			<tr class="header">
				<td style="text-align: center;">Matricula</td>
				<td>Discente</td>
			</tr>
		</c:if>
		<c:set var="totalDisciplina" value="${totalDisciplina + 1}"/>
		<tr class="componentes">
			<td style="text-align: center;">${linha.matricula}</td>
			<td>${linha.nome_discente}</td>
		</tr>
	</c:forEach>
		<tr>
   			<td colspan=2><hr></td>
   		</tr>
   		<tr>
   			<td colspan="2" align="right"><b>${totalDisciplina} trancamento(s)</b></td>
   		</tr>

		<tr align="center">
			<td colspan="2"><b>Total de Registros: <h:outputText value="#{relatoriosTecnico.numeroRegistrosEncontrados}"/></b></td>
		</tr>
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
