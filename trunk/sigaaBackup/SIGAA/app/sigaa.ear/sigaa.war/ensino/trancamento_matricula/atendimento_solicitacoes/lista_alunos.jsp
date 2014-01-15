<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>

	<f:subview id="menu">
		<ufrn:subSistema teste="not portalCoordenadorGrad">
			<%@include file="/portais/docente/menu_docente.jsp" %>
		</ufrn:subSistema>
	</f:subview>
	<ufrn:subSistema teste="portalCoordenadorGrad">
		<%@include file="/graduacao/menu_coordenador.jsp" %>
	</ufrn:subSistema>

	<h:outputText value="#{atenderTrancamentoMatricula.create}"></h:outputText>

	<h2><ufrn:subSistema /> > Atendimento de Solicitação de Trancamento de Matrícula </h2>

	<div class="descricaoOperacao">
		<p>
		Caro ${acesso.coordenadorCursoGrad or acesso.coordenadorCursoStricto? 'Coordenador,' : 'Orientador'}
		</p><br>
		<p>
		Selecione abaixo o discente para analisar sua solicitação de trancamento de matrícula em componentes curriculares.
		</p>
	</div>

		<center>
		<div class="infoAltRem"><h:graphicImage value="/img/seta.gif" style="overflow: visible;" />:
		Visualizar Solicitações de Trancamento
		<br />
		</div>
		</center>


	<table class="listagem">
		<caption>Discentes com Solicitação de Trancamento Pendente</caption>
		<tbody>
			
			<c:if test="${empty atenderTrancamentoMatricula.discentes}">
				<tr>
					<td align="center"><i>Não há discentes com solicitação de trancamento pendentes.</i></td>
				</tr>
			</c:if>
		
			<c:set var="nivelDiscente" />
			<c:forEach items="#{atenderTrancamentoMatricula.discentes}" var="discente" varStatus="status">

				<c:if test="${nivelDiscente != discente.nivel}">
					<c:set var="nivelDiscente" value="${discente.nivel}" />
					<tr>
						<td colspan="2" class="subFormulario"> ${discente.nivelDesc} </td>
					</tr>
				</c:if>

				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}" style="font-size: xx-small">
					<td>${discente.matriculaNome }</td>
					<td width="2%">
					<h:form>
						<input type="hidden" name="idAluno" value="${discente.id }">
					<c:if test="${ !atenderTrancamentoMatricula.cursoAtualCoordenacao.ADistancia }">
					<h:commandButton action="#{atenderTrancamentoMatricula.selecionarAluno}" image="/img/seta.gif" title="Ver solicitações de trancamento deste aluno" />
					</c:if>
					<c:if test="${ atenderTrancamentoMatricula.cursoAtualCoordenacao.ADistancia }">
					<h:commandButton action="#{atenderTrancamentoMatricula.selecionarAlunoEad}" image="/img/seta.gif" title="Ver solicitações de trancamento deste aluno" />
					</c:if>
					</h:form>
					</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	
	<br/>
	
	<table class="listagem">
		<caption>Discentes com Solicitação Processadas</caption>
		<tbody>
		
			<c:if test="${empty atenderTrancamentoMatricula.todasSolicitacoes}">
				<tr>
					<td align="center"><i>Não há discentes com solicitação processadas.</i></td>
				</tr>
			</c:if>		
		
			<c:if test="${not empty atenderTrancamentoMatricula.todasSolicitacoes}">
			<c:set var="nomeDiscente" />
		
			<c:forEach items="#{atenderTrancamentoMatricula.todasSolicitacoes}" var="solicitacao" varStatus="status">
				<c:if test="${solicitacao.matriculaComponente.discente.matriculaNome != nomeDiscente}">
					<c:set var="nomeDiscente" value="${solicitacao.matriculaComponente.discente.matriculaNome}"/>
					<tr>
						<td class="subFormulario" colspan="4"> ${nomeDiscente} </td>
					</tr>
				</c:if>
			
			
				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}" style="font-size: xx-small">
					<td width="5%">${ solicitacao.matriculaComponente.turma.anoPeriodo }</td>
					<td width="35%">${ solicitacao.matriculaComponente.componente.codigoNome }</td> 
					<td width="10%">${fn:toUpperCase(solicitacao.situacaoString)}</td>		 	
					<td width="50%">
						<c:if test="${empty solicitacao.replica || solicitacao.replica == null}">
							Você não fez observações
						</c:if>
							${fn:toUpperCase(solicitacao.replica)}		
					</td>
				</tr>
				
				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}" style="font-size: xx-small">
					<td colspan="4">
						<i>
							<c:if test="${ solicitacao.motivo.id != 6 }">${fn:toUpperCase(solicitacao.motivo.descricao)}</c:if>		
							<c:if test="${ solicitacao.motivo.id == 6 }">${fn:toUpperCase(solicitacao.justificativa)}</c:if>
						</i> 
					</td>				
				</tr>
				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}" style="font-size: xx-small">
					<td colspan="4">
						<i>
						<center>Analisado por: ${ solicitacao.registroAtendendor.usuario.pessoa.nome }</center>
						</i> 
					</td>				
				</tr>				
			</c:forEach>
			</c:if>
		</tbody>
	</table>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>