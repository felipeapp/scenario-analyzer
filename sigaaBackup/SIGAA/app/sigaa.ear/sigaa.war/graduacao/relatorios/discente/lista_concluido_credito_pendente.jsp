<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<f:view>
	<h:outputText value="#{relatorioDiscente.create}"/>
	<c:set var="resultado" value="${relatorioDiscente.listaDiscente}"/>
	
	<hr>
	<table width="100%">
		<caption><b>Lista de Alunos Concluídos com Créditos Pendentes</b></caption>
		<tr><td>&nbsp;</td></tr>
	</table>
	<hr>
	
	<table cellspacing="1" width="100%" style="font-size: 10px;">
		<caption><b>Total de Registros: ${ fn:length(resultado) }</b></caption>

		<tr>
			<td>Ingresso</td>
			<td>Matricula</td>
			<td>Nome</td>
		<tr>
	<c:forEach items="${resultado}" var="linha">
		<tr>
			<td>
				${linha.ano_ingresso}
				-
				${linha.periodo_ingresso}
			</td>
			<td>
				${linha.matricula}
			</td>
			<td>
				${linha.nome}
				<c:if test="${not empty linha.municipio_polo}"> - PÓLO ${linha.municipio_polo}</c:if>
			</td>
		</tr>
	</c:forEach>
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
