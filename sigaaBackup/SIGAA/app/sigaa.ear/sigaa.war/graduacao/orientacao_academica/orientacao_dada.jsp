<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@page import="br.ufrn.sigaa.ensino.dominio.MotivoTrancamento" %>
<f:view>

<h2><ufrn:subSistema /> > Orienta��es Dadas ao Discente</h2>

<h:form id="form">
	<c:set value="#{orientacaoAcademica.discenteBusca}" var="discente" />
	<%@ include file="/graduacao/info_discente.jsp"%>
	
 	<c:if test="${not empty orientacaoAcademica.orientacoesMatricula}">
 	<table class="listagem" width="100%">
 		<caption>Orienta��es de Matr�cula Dadas</caption>
 		<thead>
	 		<tr>
	 			<th>Ano/Per�odo</th>
	 			<th>Orienta��o</th>
	 			<th>Orientador</th>
	 		</tr>
 		</thead>
 		<tbody>
 			<c:forEach var="item" items="#{orientacaoAcademica.orientacoesMatricula}" varStatus="status">
	 			<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
		 			<td>${item.ano}.${item.periodo}</td>
		 			<td>${item.orientacao}</td>
		 			<td>
		 				<h:outputText value="#{item.orientador.nome}" rendered="#{not empty item.orientador.nome}" />
		 				<h:outputText value="#{item.orientadorExterno.nome}" rendered="#{not empty item.orientadorExterno.nome}" />
		 			</td>
		 		</tr>
	 		</c:forEach>
 		</tbody>
 	</table>
 	</c:if>
 	<c:if test="${empty orientacaoAcademica.orientacoesMatricula}">
 		<div align="center">N�o h� Orienta��es de Matr�culas para este discente.</div>
 	</c:if>
 	<br/>
 	<c:if test="${not empty orientacaoAcademica.solicitacoesTrancamento}">
 	<table class="listagem" width="100%">
 		<caption>Orienta��es de Trancamento Dadas</caption>
 		<thead>
	 		<tr>
	 			<th>Data</th>
	 			<th>Situa��o</th>
	 			<th>Motivo</th>
	 			<th>Orienta��o</th>
	 			<th>R�plica</th>
	 		</tr>
 		</thead>
 		<tbody>
 			<c:forEach var="item" items="#{orientacaoAcademica.solicitacoesTrancamento}" varStatus="status">
	 			<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
		 			<td><h:outputText value="#{item.dataCadastro}" /></td>
		 			<td>${item.situacaoString}</td>
		 			<td>
		 				<h:outputText value="#{item.motivo.descricao}" rendered="#{empty item.justificativa}" /> 
		 				<h:outputText value="#{item.justificativa}" rendered="#{not empty item.justificativa}" />
		 			</td>
		 			<td>
		 				${item.replica}
		 			</td>
		 		</tr>
	 		</c:forEach>
 		</tbody>
 	</table>
 	</c:if>
 	<c:if test="${empty orientacaoAcademica.solicitacoesTrancamento}">
 		<div align="center">N�o h� Orienta��es de Trancamento para este discente.</div>
 	</c:if>
	<br/>
	<div align="center">	
		<a href="javascript:history.go(-1);"> << Voltar</a>
	</div>
</h:form>
	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>