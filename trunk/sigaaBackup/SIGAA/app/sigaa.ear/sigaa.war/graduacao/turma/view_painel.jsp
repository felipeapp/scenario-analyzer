<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<style>
	table tbody tr td {text-align: left;}
	table tbody tr th {text-align: right;}
	table.subFormulario  tr.turmas td{
		background: #C4D2EB;
		padding-left: 10px;
		font-weight: bold;
	}
</style>

<f:view>
	${buscaTurmaBean.create}
	${buscaTurmaBean.carregarTurma}
	<c:set var="matriculados" value="${buscaTurmaBean.alunosMatriculados }" />

	<h:messages showDetail="true"></h:messages>
	<h:form id="resumo">
	<table class="visualizacao" width="100%">
	<tr><td>
		<table class="subFormulario" width="100%">
		<caption>Dados da Turma</caption>
			<tr>
				<th width="23%"> Ano/Período: </th>
				<td colspan="3">${buscaTurmaBean.obj.anoPeriodo}</td>
			</tr>
			<tr>
				<th>Período Letivo:</th>
				<td colspan="3">de <ufrn:format type="data" valor="${buscaTurmaBean.obj.dataInicio}" /> até <ufrn:format type="data" valor="${buscaTurmaBean.obj.dataFim}"/></td>
			</tr>
			<tr>
				<th>Componente e Turma:</th>
				<td colspan="3">${buscaTurmaBean.obj.disciplina.descricaoResumida} - Turma ${buscaTurmaBean.obj.codigo}</td>
			</tr>
			<c:if test="${not empty buscaTurmaBean.obj.observacao}">
			<tr>
				<th>Observação:</th>
				<td><h:outputText value="#{ buscaTurmaBean.obj.observacao}"/></td>
			</tr>
			</c:if>

			<tr>
				<th> Tipo do Componente: </th>
				<td> ${buscaTurmaBean.obj.disciplina.tipoComponente.descricao } </td>
			</tr>			

			<c:if test="${buscaTurmaBean.obj.curso.id > 0}">
			<tr>
				<th>Curso</th>
				<td colspan="3">${buscaTurmaBean.obj.curso.descricao}</td>
			</tr>
			</c:if>

			<tr>
				<th>Créditos / Carga Horária:</th>
				<td colspan="3">
					${buscaTurmaBean.obj.disciplina.crTotal} cr / ${buscaTurmaBean.obj.disciplina.chTotal} horas
				</td>
			</tr>

			<tr>
				<th> Tipo da Turma: </th>
				<td colspan="3"> ${buscaTurmaBean.obj.tipoString} </td>
			</tr>

			<c:if test="${!buscaTurmaBean.obj.disciplina.bloco}">
			<c:if test="${ !buscaTurmaBean.obj.distancia }">
			<tr>
				<th>Local e Horário:</th>
				<td colspan="3">
				<h:outputText value="#{ buscaTurmaBean.obj.local }"/> - <h:outputText value="#{ buscaTurmaBean.obj.descricaoHorario }"/>
				</td>
			</tr>
			</c:if>

			<c:if test="${buscaTurmaBean.obj.especializacao.id > 0}">
			<tr>
				<th>Especialidade da Turma de Entrada: </th>
				<td>${buscaTurmaBean.obj.especializacao.descricao }</td>
			</tr>
			</c:if>

			</c:if>
			<c:if test="${ !buscaTurmaBean.obj.distancia }">
			<tr>
				<th>Capacidade:</th>
				<td><h:outputText value="#{ buscaTurmaBean.obj.capacidadeAluno }"/> alunos</td>
			</tr>
			</c:if>
			<tr>
			<c:set var="totalSolicitacoes" value="${buscaTurmaBean.obj.totalSolicitacoes}" />
			<c:if test="${ matriculados > 0 or totalSolicitacoes > 0}">
				<th>Totais:</th>
				<td colspan="3">
				<c:if test="${totalSolicitacoes > 0}">
					<c:set var="solicitacoesRematricula" value="${buscaTurmaBean.solicitacoesRematricula}" />
					${buscaTurmaBean.obj.totalSolicitacoes - solicitacoesRematricula} solicitações realizadas na matrícula <br />
					${solicitacoesRematricula } solicitações realizadas na rematrícula <br />
				</c:if>
				<c:if test="${matriculados > 0}">
				 	${matriculados } alunos matriculados
				</c:if>
				</td>
			</c:if>
			</tr>
			<c:if test="${!buscaTurmaBean.obj.disciplina.bloco}">
			<tr>
				<td colspan="2" valign="top" width="50%">
				<table class="subFormulario" width="100%">
					<% boolean temFoto = false; %>
					<caption> Professores (${fn:length(buscaTurmaBean.obj.docentesTurmas)}) </caption>
					<tr>
						<td valign="top">
							<table style="listagem">
							<c:forEach items="${buscaTurmaBean.obj.docentesTurmas}" var="dt"> 
							<tr>
								<td valign="top" style="font-size: 9px">
									${dt.docenteNome} (${dt.chDedicadaPeriodo}h)
								</td>
							</tr>				
							</c:forEach>
							</table>
						</td>
					</tr>
				</table>
				</td>
				<td colspan="2" valign="top" width="50%">
					
					<c:if test="${buscaTurmaBean.obj.graduacao}">
						<table class="subFormulario" width="100%">
							<caption>Vagas Reservadas (Ingressantes/Demais)</caption>
							<c:if test="${ empty buscaTurmaBean.obj.reservas }">
								<tr><td style="font-size: 9px">
									<center><font color="red" ><i><strong>Não há reservas para esta turma</strong></i></font></center>
								</td></tr>
							</c:if>
	
							<c:if test="${ not empty buscaTurmaBean.obj.reservas }">
								<c:forEach  items="${buscaTurmaBean.obj.reservas }" var="reserva" varStatus="status">
								<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}" style="font-size: 9px">
									<td style="text-align: left;">${ reserva.descricao }</td>
									<td style="text-align: right;" width="5%">
										(${reserva.vagasReservadasIngressantes }/${reserva.vagasReservadas })
									</td>
								</tr>
								</c:forEach>
							</c:if>
						</table>
					</c:if>
				</td>
			</tr>
			</c:if>
		</table>
	</td></tr>

	</table>
	</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
