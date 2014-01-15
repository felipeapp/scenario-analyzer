<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h:outputText value="#{trancamentoMatricula.create}"/>
	<h2> Solicitação de Trancamento de Matrícula </h2>

	<c:if test="${ not trancamentoMatricula.solicitado and not trancamentoMatricula.tutorEad }">
	<div class="descricaoOperacao">
		<p>
		Caro Aluno,
		</p> <br>
		<p>
		Confirme as disciplinas que deseja trancar e observe se não foram adicionados nenhum co-requisito para o trancamento.
		</p>
	</div>
	</c:if>

	<c:set var="discente" value="#{trancamentoMatricula.discente}"/>
	<%@include file="/graduacao/info_discente.jsp"%>

<h:form>

	<c:if test="${ trancamentoMatricula.solicitado }">
		<br>
		<div align="center">
			<h:commandButton image="/img/printer_ok.png"
			title="Gerar Comprovante da Solicitação" action="#{trancamentoMatricula.telaComprovante}"/> <br>
			<h:commandLink action="#{trancamentoMatricula.telaComprovante}" value="Imprimir Comprovante da Solicitação"/>
		</div>
		<br>
	</c:if>


	<table class="listagem" style="width: 90%;">
		<caption>Disciplinas com Trancamento Solicitado</caption>
		<thead>
		<tr>
			<th> Componente </th>
			<th> Turma</th>
		</tr>
		</thead>
		<tbody>
			<c:if test="${not empty trancamentoMatricula.matriculasEscolhidas}">
				<c:forEach var="matricula" items="#{trancamentoMatricula.matriculasEscolhidas}" varStatus="status">
				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}" style=" font-weight: bold">
					<td> ${matricula.matriculaComponente.componenteDescricao} </td>
					<td align="center">${matricula.matriculaComponente.turma.codigo}</td>
				</tr>
	
				<tr  class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
					<td colspan="2" style="font-style: italic; font-size: x-small;">
						Motivo:
						<c:if test="${not empty matricula.justificativa}">
							${matricula.justificativa}
						</c:if>
						
						<c:if test="${empty matricula.justificativa}">
							<c:forEach var="mot" items="#{motivoTrancamento.allExibir}">					
								<c:if test="${mot.id == matricula.motivo.id}">
									${mot.descricao}
								</c:if>
							 </c:forEach>
						</c:if>
						</b>
					</td>
				</tr>
				</c:forEach>
			</c:if>
		</tbody>
	</table>

	<c:if test="${ not trancamentoMatricula.solicitado }">
		<c:if test="${ not trancamentoMatricula.tutorEad }">
		<%@include file="/WEB-INF/jsp/include/confirma_senha.jsp"%>
		</c:if>
		<c:if test="${ trancamentoMatricula.tutorEad }">
		<br/>
		</c:if>
		<div align="center">
						<h:commandButton value="Confirmar Solicitação" action="#{trancamentoMatricula.cadastrarSolicitacao}"/>
						<input type="button" value="<< Voltar" onclick="javascript:history.go(-1)"/> 
						<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{trancamentoMatricula.cancelar}" />
		</div>
	</c:if>

	</h:form>
	<p align="center">
	<h:form>
	<center>
	<c:if test="${ trancamentoMatricula.solicitado }">
		<br>
		<h:commandButton value="Voltar ao Menu Principal" action="#{trancamentoMatricula.cancelar}" />
	</c:if>
	</center>
	</h:form>
	</p>


</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>