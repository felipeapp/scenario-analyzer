<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<link rel="stylesheet" media="all" href="/sigaa/css/matricula.css" type="text/css" />
<script type="text/javascript" src="/shared/javascript/paineis/turma_componentes.js"></script>

<style>
	
	table.listagem tr.linhaImpar td{
		background: #F1F1F1;	
	}
	
	table.listagem td.anoPeriodo {
		background: #EFF3FA;
		
	}
	
	table.visualizacao tbody tr td, table.visualizacao tbody tr th {
		font-size: 10px;
	{

</style>

<f:view>
	<h:outputText value="#{analiseSolicitacaoMatricula.create }" />
	<h2> Análises de Solicitações de Matrícula</h2>

	<c:set var="discente" value="#{analiseSolicitacaoMatricula.discente}"/>
	<%@include file="/graduacao/info_discente.jsp"%>
	
	<c:if test="${not empty solicitacoes}">
	
	<h:form>
	<table class="listagem" style="width: 100%">
		<caption>Solicitações de Matrícula Analisadas</caption>
		<thead style="font-size: xx-small;">
			<tr>
				<td width="8%"></td>
				<td>Componente Curricular</td>
				<td width="12%">Situação</td>
				<td width="10%" nowrap="nowrap">Submetida em</td>
				<td width="8%" nowrap="nowrap">Analisada em</td>
				<td width="8%" ></td>
			</tr>
		</thead>
		<tbody>

			<c:set var="anoPeriodo" />
			<c:forEach items="${solicitacoes}" var="solicitacao" varStatus="status">
				<c:set var="analisePositiva" value="${solicitacao.atendida or solicitacao.vista}" />				
				<c:set var="analiseNegativa" value="${solicitacao.negada or solicitacao.negadaOutroPrograma}" />				

				<c:if test="${anoPeriodo != solicitacao.anoPeriodo}">
					<c:set var="anoPeriodo" value="${solicitacao.anoPeriodo}"/>
					<tr>
						<td colspan="6" class="subFormulario"> ${anoPeriodo}</td>
					</tr>

					<tr>
						<td colspan="6" style="background: #EEE;"> <b>Orientacao Geral:</b>  
							${orientacoes[anoPeriodo] != null ? orientacoes[anoPeriodo].orientacao : "<i>nenhuma orientação cadastrada</i>"} 
						</td>
					</tr>
				</c:if>

				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}" style="font-size: xx-small">
					<td align="center">
						<c:if test="${not empty solicitacao.turma}">
						<a href="javascript:void(0);" onclick="PainelTurma.show(${solicitacao.turma.id})" title="Ver Detalhes dessa turma">
							T${solicitacao.turma.codigo}
						</a>
						</c:if>
					</td>
					<td>	
						<b>
						<a href="javascript:void(0);" onclick="PainelComponente.show(${solicitacao.turma.disciplina.id})" title="Ver Detalhes do Componente Curricular">
						${solicitacao.componente.codigo}
						</a> - ${solicitacao.componente.nome}
						</b>
					</td>
					<td style="color: ${analisePositiva ? '#292' : (analiseNegativa ? '#922' : '') };"> 
						${solicitacao.statusDescricao}
					</td>
					<td><ufrn:format type="data" valor="${solicitacao.dataCadastro}" /></td>
					<td> <ufrn:format type="data" valor="${solicitacao.dataAlteracao}" /></td>
					<td align="center"> 
						<c:if test="${!solicitacao.discente.stricto or !solicitacao.negada}">
							${solicitacao.processamentoStatus} 
						</c:if>
					</td>
				</tr>
				<c:if test="${not empty solicitacao.observacao }">
					<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}" style="font-size: xx-small">
						<td> </td>
						<td colspan="5"><i>Observações:</i> ${solicitacao.observacao }</td>
					</tr>
					<c:if test="${solicitacao.discente.tecnico or solicitacao.discente.stricto}">
					<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}" style="font-size: xx-small">
						<td> </td>
						<td colspan="5"> <i>Analisada por ${solicitacao.registroAlteracao.usuario.nome}</i></td>
					</tr>
					</c:if>
				</c:if>
			</c:forEach>
		</tbody>
	</table>
	<br>
	</h:form>
	</c:if>

</f:view>

<%@ include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>