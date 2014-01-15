<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

<style>
	tr.ingresso td {padding: 20px 0 0; border-bottom: 1px solid #555}
	tr.header td {padding: 0px ; background-color: #eee; border-bottom: 1px solid #555; font-weight: bold; border: 1px solid #000;}
	tr.componentes td {padding: 5px 2px 2px; border-bottom: 1px solid #000; border: 1px solid #000;}
</style>
<f:view>

	<h2>Relatório dos Discentes sem solicitação de Matrícula</h2>

<div id="parametrosRelatorio">
	<table >
		<tr>
			<th>Ano-Período: </th>
			<td> ${relatorioDiscenteMatricula.ano}.${relatorioDiscenteMatricula.periodo}</td>
		</tr>
	</table>
</div>

   <c:set var="ingresso" />
    
	<table cellspacing="1" width="100%" style="font-size: 10px;" align="center">
		<c:forEach items="#{relatorioDiscenteMatricula.listaDiscente}" var="linha" varStatus="indice">
			
			<c:set var="ingressoAtual" value="${linha.forma_ingresso}"/>
			  <c:if test="${ingresso != ingressoAtual}">
					<tr class="ingresso">
						<td colspan="10"><b>Forma de Ingresso: ${linha.forma_ingresso}</b></td>
					</tr>
					<tr class="header">
						<td align="center">Matrícula</td>
						<td align="center">Ingresso</td>
						<td>Nome</td>
						<td>Situação</td>
						<td>Curso</td>
						<td>Município</td>
						<td>Turno</td>
						<td>Habilitação</td>
						<td>Modalidade</td>
					</tr>
						<c:set var="ingresso" value="${ingressoAtual}"/>
				</c:if>
					<tr class="componentes">
						<td align="center">${linha.matricula}</td>
						<td align="center">${linha.ingresso}</td>
						<td>
							${linha.nome}
							<c:if test="${not empty linha.municipio_polo}"> - PÓLO ${linha.municipio_polo}</c:if>
						</td>
						<td>${linha.situacao}</td>
						<td>${linha.curso}</td>
						<td>${linha.municipio}</td>
						<td>${linha.turno}</td>
						<td align="${linha.habilitacao != null ? 'left' : 'center'}">${linha.habilitacao != null ? linha.habilitacao : '-'}</td>
						<td>${linha.modalidade}</td>
					</tr>
		</c:forEach>
	</table>
	<br/>
	<div align="center"><b>Total Discentes sem solicitação de Matrícula: ${ fn:length(relatorioDiscenteMatricula.listaDiscente) }</b></div>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>