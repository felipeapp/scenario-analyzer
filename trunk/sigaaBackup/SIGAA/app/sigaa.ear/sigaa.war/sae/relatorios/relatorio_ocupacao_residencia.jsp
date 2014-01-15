<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<style>
.alinharCentro{ 
	text-align:center !important;
}
</style>
<f:view>
	<h2>Relatório de Ocupação de Residências</h2>
	
	<div id="parametrosRelatorio">
		<table >
			<tr>
				<th>Ano.Período:</th>
				<td> 
					${ relatoriosSaeMBean.ano } . ${ relatoriosSaeMBean.periodo }
				</td>
			</tr>
			<tr>
				<th>Residência:</th>
				<td> 
					${ relatoriosSaeMBean.residencia.localizacao }
				</td>
			</tr>
		</table>
	</div>
	<br/>
	
	<c:set var="localLoop" />
	<c:if test="${ not empty relatoriosSaeMBean.listaResidenciasGraduacao}">
		<table class="tabelaRelatorioBorda" width="100%">	
			<caption>Residência de Graduação</caption>
			<thead>
			<tr>
				<th width="10%" class="alinharCentro"> Matrícula </th>
				<th width="40%" align="left"> Discente </th>
				<th width="45%" align="left"> Curso </th>
			</tr>
			</thead>
			<tbody>
			<c:forEach var="item" items="#{relatoriosSaeMBean.listaResidenciasGraduacao}">
				<c:set var="localAtual" value="${item.residencia.localizacao}"/>
				<c:if test="${localLoop != localAtual}">
					<c:if test="${not empty item.residencia.localizacao}">
						<td colspan="3"><b>${fn:toUpperCase(item.residencia.localizacao)}</b></td>
					</c:if>
					<c:if test="${empty item.residencia.localizacao}">
						<td colspan="3"><b>NÃO INFORMADO</b></td>
					</c:if>
					    <c:set var="localLoop" value="${localAtual}"/>	
				</c:if>
				<tr>
					<td align="center"> ${item.discente.matricula} </td>
					<td> ${item.discente.pessoa.nome} </td>										
					<td> ${item.discente.curso.descricao} </td>
				</tr>
			</c:forEach>
			</tbody>
		</table>
	</c:if>
	<br>
	<br>
	<c:set var="localLoop" />
	<c:if test="${ not empty relatoriosSaeMBean.listaResidenciasPos}">
		<table class="tabelaRelatorioBorda" width="100%">	
			<caption>Residência de Pós-Graduação</caption>
			<thead>
				<tr>	
					<th width="10%" align="right"> Matrícula </th>
					<th width="40%" align="left"> Discente </th>
					<th width="45%" align="left"> Programa </th>
				</tr>
		
			</thead>
			<tbody>			
				<c:forEach var="item" items="#{relatoriosSaeMBean.listaResidenciasPos}">
					<c:set var="localAtual" value="${item.residencia.localizacao}"/>
					<c:if test="${localLoop != localAtual}">
						<c:if test="${not empty item.residencia.localizacao}">
							<td colspan="3"><b>${fn:toUpperCase(item.residencia.localizacao)}</b></td>
						</c:if>
						<c:if test="${empty item.residencia.localizacao}">
							<td colspan="3"><b>NÃO INFORMADO</b></td>
						</c:if>
						<c:set var="localLoop" value="${localAtual}"/>	
					</c:if>
					<tr>
						<td> ${item.discente.matricula} </td>
						<td> ${item.discente.pessoa.nome} </td>
						<td> ${item.discente.curso.descricao} </td>										
					</tr>				
				</c:forEach>
			</tbody>	
		</table>
	</c:if>
	<br>
	<table width="100%" class="tabelaRelatorioBorda">
		<c:if test="${ not empty relatoriosSaeMBean.listaResidenciasGraduacao}">
		<tr>
			<td align="center">
				<b>Total de Residentes de Graduação:</b>
				<h:outputText value="#{relatoriosSaeMBean.totalOcupantesResidenciaGrad}"/>
			</td>
		</tr>
		</c:if>
		<c:if test="${ not empty relatoriosSaeMBean.listaResidenciasPos}">
		<tr>
			<td align="center">
				<b>Total de Residentes de Pós-Graduação:</b>
				<h:outputText value="#{relatoriosSaeMBean.totalOcupantesResidenciaPos}"/>
			</td>
		</tr>
		</c:if>
		<c:if test="${ not empty relatoriosSaeMBean.listaResidenciasPos && not empty relatoriosSaeMBean.listaResidenciasGraduacao}">
		<tr>
			<td align="center">
				<b>Total de Residentes de Graduação e de Pós-Graduação:</b>
				<h:outputText value="#{relatoriosSaeMBean.totalOcupantesResidencia}"/>
			</td>
		</tr>
		</c:if>
	</table>
	<br><br>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>