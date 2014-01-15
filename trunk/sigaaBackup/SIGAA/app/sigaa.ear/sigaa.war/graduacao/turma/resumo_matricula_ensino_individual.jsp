<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>

	<c:if test="${acesso.chefeDepartamento}"><%@include file="/portais/docente/menu_docente.jsp" %></c:if>
	<c:if test="${acesso.coordenadorCursoStricto}"><%@include file="/stricto/menu_coordenador.jsp" %></c:if>
	<c:if test="${acesso.coordenadorCursoGrad or acesso.coordenacaoProbasica}"><%@include file="/graduacao/menu_coordenador.jsp" %></c:if>
	
	<h2><ufrn:subSistema /> > Atendimento da solicitação para Turma de Ensino Individual</h2>
	
	<div class="descricaoOperacao">
		<p>Caro Usuário(a),</p>
		<p>Não é possível criar mais de uma turma de ensino individual para o componente ${turmaGraduacaoBean.obj.disciplina.codigo},
		mas ainda há vagas para matricular os discentes dessa solicitação em outra turma.</p>
		<p>Abaixo é listada a turma de ensino individual localizada e os discentes interessados no ensino individualizado.</p>
		<br/>
	</div>	
	
	<h:form id="form">

		<h3 class="tituloTabela" style="width: 99%">Dados da Turma</h3>
		<c:set var="turma" value="${turmaGraduacaoBean.obj}"/>
		<%@include file="/ensino/turma/info_turma.jsp"%>
		
		<br/><br/>
		
		<table class="formulario" width="90%">
			<tbody>
				<caption>Discentes Interessados na Turma de 
					<h:outputText value="Férias" rendered="#{ turmaGraduacaoBean.obj.turmaFerias }" />
					<h:outputText value="Ensino Individual" rendered="#{ turmaGraduacaoBean.obj.turmaEnsinoIndividual }" /> 
				</caption>
				<c:choose>
					<c:when test="${ not empty turmaGraduacaoBean.solicitacao.discentes  }">
						<c:forEach items="${turmaGraduacaoBean.solicitacao.discentes}" var="discenteLoop" varStatus="status">
							<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
								<td></td>
								<td>${discenteLoop.discenteGraduacao}</td>
							</tr>
						</c:forEach>
					</c:when>
					<c:otherwise>
						<tr>
							<td style="color: red;"> Não há nenhum Discente Interessado na Turma de 
								<h:outputText value="Férias" rendered="#{ turmaGraduacaoBean.obj.turmaFerias }" />
								<h:outputText value="Ensino Individual" rendered="#{ turmaGraduacaoBean.obj.turmaEnsinoIndividual }" />
							 </td>
						</tr>
					</c:otherwise>
				</c:choose>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="3">
						<h:commandButton value="Confirmar Matrícula" action="#{ turmaGraduacaoBean.cadastrarMatriculasEnsinoIndividual }" id="btCadastrar"/>
						<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{ turmaGraduacaoBean.cancelar }" id="btCancelar"/>
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>