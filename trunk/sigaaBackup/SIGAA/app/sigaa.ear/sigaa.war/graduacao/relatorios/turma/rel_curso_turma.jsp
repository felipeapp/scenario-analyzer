<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<style>
	tr.header td {padding: 0px ; background-color: #fffff;  font-weight: bold; }
	tr.componente td {padding: 0px 0px 0px; font-weight: bold; color: red; }
	tr.componentes td {padding: 0px 0px 0px; background-color: #eee; }
</style>
<f:view>
	<table width="100%">
  	  <tr>		
		<td class="listagem">
			<td align="center">
				<h2><b>Relatório dos Cursos que não Solicitaram Turmas</b></h2>
			</td>
	</table>
<br />
	<div id="parametrosRelatorio">
		<table>
			<tr>
				<th>Ano-Período:</th>
				<td><h:outputText value="#{relatorioTurma.ano}"/>.<h:outputText value="#{relatorioTurma.periodo}"/></td>
			</tr>
		</table>
	</div>
	<div align="center">
		<b>T.A</b> - Turmas Abertas &nbsp;
		<b>S.A</b> - Solicitações Atendidas &nbsp;
		<b>S.N</b> - Solicitações Negadas <br />
		<b>S.R</b> - Solicitações Removida <br />
		<b>S.Alt</b> - Solicitações Alteradas &nbsp;
		<b>S.A.A</b> - Solicitações Atendidas Alteração <br />
		<b>S.A.P</b> - Solicitações Atendidas Parcialmente
	</div>
	<br />
	
    <c:set var="_centro" />
    
	<table cellspacing="1" width="100%" style="font-size: 11px;" align="center" border="1">
		
		<c:forEach items="#{relatorioTurma.listaTurma}" var="linha" varStatus="indice">
			<c:set var="centroAtual" value="${linha.centro}"/>
			  <c:if test="${_centro != centroAtual}">
					<tr class="header">
						<td colspan="10" style="font-size: 12px;"><b>Centro: ${linha.centro}</b></td>
					</tr>
					<tr class="componentes">
						<td colspan="3">Curso</td>
						<td align="right">T. A.</td>
						<td align="right">S. A.</td>
						<td align="right">S. N.</td>
						<td align="right">S. R.</td>
						<td align="right">S. Alt.</td>
						<td align="right">S. A. A.</td>
						<td align="right">S. A. P.</td>
					</tr>
						<c:set var="_centro" value="${centroAtual}"/>
			  </c:if>
			  		<c:set var="totalLInha" value="0" />
			  		<c:set var="totalLinha" value="${linha.aberta + linha.atendida + linha.negada + 
			  				linha.removida + linha.solicitacao_alteracao + linha.atendida_alteracao + 
			  				linha.atendida_parcialmente}"/>
			  		<c:choose>
			  		<c:when test="${totalLinha == 0}">
			  			<tr class="componente">
			  			<td style="background-color: #FF6347; color: black; font-weight: normal;" 
			  					 colspan="3">
					</c:when>
					<c:otherwise>
						<tr>
						<td colspan="3">
					</c:otherwise>
					</c:choose>
							${linha.curso}</td>
							<td align="right">${linha.aberta}</td>
							<td align="right">${linha.atendida}</td>
							<td align="right">${linha.negada}</td>
							<td align="right">${linha.removida}</td>
							<td align="right">${linha.solicitacao_alteracao}</td>
							<td align="right">${linha.atendida_alteracao}</td>
							<td align="right">${linha.atendida_parcialmente}</td>
					</tr>
		</c:forEach>
	</table>
	<br />
	<div align="center">
		<b>T.A</b> - Turmas Abertas &nbsp;
		<b>S.A</b> - Solicitações Atendidas &nbsp;
		<b>S.N</b> - Solicitações Negadas <br />
		<b>S.R</b> - Solicitações Removida <br />
		<b>S.Alt</b> - Solicitações Alteradas &nbsp;
		<b>S.A.A</b> - Solicitações Atendidas Alteração <br />
		<b>S.A.P</b> - Solicitações Atendidas Parcialmente
	</div>
	<br />
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>