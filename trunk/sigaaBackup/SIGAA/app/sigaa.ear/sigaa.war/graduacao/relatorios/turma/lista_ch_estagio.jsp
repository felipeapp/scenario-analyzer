<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<style>
	tr.curso td {padding: 15px 0 0; border-bottom: 1px solid #555}
	tr.header td {padding: 3px ; border-bottom: 1px solid #555; background-color: #eee;}
	tr.discente td {border-bottom: 1px solid #888; font-weight: bold; padding-top: 7px;}
	tr.componentes td {padding: 4px 2px 2px ; border-bottom: 1px dashed #888;}
	tr.componentes td.assinatura { padding:2px; border-bottom: 1px solid #888;  width: 40%;}
</style>
<f:view>
	<hr>
	<table width="100%">
		<caption><b>Relatório de Disciplinas de Estágio</b></caption>
			<tr>
				<th>Departamento:</th>
				<td><b><h:outputText
					value="#{relatorioTurma.filtroDepartamento ? relatorioTurma.departamento.nome : 'TODOS'}" /></b></td>
				<th>Ano-Período:</th>
				<td><b><h:outputText
					value="#{relatorioTurma.ano}" />.<h:outputText
					value="#{relatorioTurma.periodo}" /></b></td>
			</tr>
	</table>
	<hr>
	<table cellspacing="1" width="100%" style="font-size: 10px;">
		<caption><b>Total de Registros: <h:outputText value="#{relatorioTurma.numeroRegistosEncontrados}"/></b></caption>
	<c:set var="departamento_loop"/>
		<c:forEach items="${relatorioTurma.listaTurma}" var="linha">
			<c:set var="departamento_atual" value="${linha.depto_nome}"/>
			
			<c:if test="${departamento_loop != departamento_atual}">
				<c:set var="departamento_loop" value="${departamento_atual}"/>
				<tr class="curso">
					<td colspan="3">
						<br>
						<b>${linha.depto_nome}</b>
					</td>
				</tr>
				<tr class="header">
					<td>Ano-Período</td>
					<td>Componente Curricular</td>
					<td>Número de Docentes</td>
				<tr>
			</c:if>
			
			<tr class="componentes">
				<td>
					${linha.ano}.${linha.periodo}
				</td>
				<td>
					${linha.componente_nome}
				</td>
				<td>
					${linha.qtd_docente}
				</td>
			</tr>
		</c:forEach>
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
