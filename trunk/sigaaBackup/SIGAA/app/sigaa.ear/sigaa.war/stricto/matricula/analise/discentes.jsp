<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<c:choose>
		<c:when test="${acesso.coordenadorCursoGrad}">
			<%@include file="/graduacao/menu_coordenador.jsp" %>
		</c:when>
		<c:when test="${acesso.orientadorAcademico}">
			<%@include file="/portais/docente/menu_docente.jsp" %>
		</c:when>
		<c:when test="${acesso.coordenadorCursoStricto || acesso.secretariaPosGraduacao }">
			<%@include file="/stricto/menu_coordenador.jsp" %>
		</c:when>
	</c:choose>
	
	<h2><ufrn:subSistema /> > An�lise de Solicita��es de Matr�cula</h2>

	<div class="descricaoOperacao">
		<p>Caro ${analiseSolicitacaoMatricula.portalCoordenadorGraduacao or analiseSolicitacaoMatricula.portalCoordenadorStricto ? 'Coordenador' : 'Orientador'},</p>
		
		<c:if test="${analiseSolicitacaoMatricula.discente.graduacao}">
		<p> Esta opera��o tem a finalidade refor�ar a orienta��o acad�mica do aluno.
		Os alunos realizam suas matr�culas pela Internet e o coordenador os orienta. Caso queira dividir
		a carga de trabalho � poss�vel cadastrar orientadores acad�micos (docentes) que tamb�m podem realizar
		esta opera��o para os alunos a eles designados. </p>
		</c:if>
		
		<c:if test="${!analiseSolicitacaoMatricula.discente.graduacao}">
			<c:if test="${!analiseSolicitacaoMatricula.analiseOutroPrograma}">
				<p> Selecione um discente para iniciar a an�lise da matr�cula. 
				</p>
			</c:if>
			
			<c:if test="${analiseSolicitacaoMatricula.analiseOutroPrograma}">
				<p> Estes s�o os discentes de um programa diferente do seu que solicitaram matr�cula em disciplinas do seu programa.
				Para um discente realizar a matr�cula em disciplinas de outro programa � necess�rio que a matr�cula seja aprovada pelo
				seu orientador ou coordena��o de programa e pela coordena��o do programa dono da disciplina que ele deseja se matr�cular. 
				</p>
			</c:if>
		</c:if>
	</div>

	<c:if test="${not empty analiseSolicitacaoMatricula.discentes or not empty analiseSolicitacaoMatricula.discentesPendentes }">
	<center>
	<div class="infoAltRem"><h:graphicImage value="/img/seta.gif" style="overflow: visible;" />:
	Selecione um discente para analisar as matr�culas
	</div>
	<br>
	<h:form>
	
	<table class="listagem" id="lista-turmas" width="100%">
		<caption>Selecione um dos discentes abaixo para analisar suas matr�culas (${fn:length(analiseSolicitacaoMatricula.discentesPendentes) + fn:length(analiseSolicitacaoMatricula.discentes)})</caption>
		<tbody>
		
			<%-- Pendentes --%>
			<c:if test="${not empty analiseSolicitacaoMatricula.discentesPendentes}">
			<tr>
				<td colspan="2" class="subFormulario"> Solicita��es pendentes de an�lise (${fn:length(analiseSolicitacaoMatricula.discentesPendentes)}) </td> 
			</tr>
			<c:forEach items="#{analiseSolicitacaoMatricula.discentesPendentes}" var="d" varStatus="status">
				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
					<td> ${d.matriculaNome} </td>
					<td width="3%">
						<h:commandLink action="#{analiseSolicitacaoMatricula.selecionaDiscente}" title="Selecionar Discente" id="linkSelecionarDiscente">
							<f:param name="id" value="#{d.id}" />
							<f:param name="outrosProgramas" value="#{analiseSolicitacaoMatricula.analiseOutroPrograma}"/>
						 	<h:graphicImage url="/img/seta.gif"/>
						</h:commandLink>
					</td>
				</tr>
			</c:forEach>
			</c:if>		

			<%-- Outras --%>
			<c:if test="${not empty analiseSolicitacaoMatricula.discentes}">
			<tr>
				<td colspan="2" class="subFormulario"> Solicita��es analisadas (${fn:length(analiseSolicitacaoMatricula.discentes)})</td> 
			</tr>
			<c:forEach items="#{analiseSolicitacaoMatricula.discentes}" var="d" varStatus="status">
				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
					<td> ${d.matriculaNome} </td>
					<td width="3%">
						<h:commandLink action="#{analiseSolicitacaoMatricula.selecionaDiscente}" title="Selecionar Discente" id="linkSelecionarDiscente2">
							<f:param name="id" value="#{d.id}" />
							<f:param name="outrosProgramas" value="#{analiseSolicitacaoMatricula.analiseOutroPrograma}"/>
						 	<h:graphicImage url="/img/seta.gif"/>
						</h:commandLink>
					</td>
				</tr>
			</c:forEach>
			</c:if>
		</tbody>
		<tfoot>
			<tr>
				<td colspan="2" align="center">
					<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{analiseSolicitacaoMatricula.cancelar}" id="botaoParaCancelarOp"/>	
				</td>
			</tr>
		</tfoot>
	</table>
	</h:form>
	</c:if>

	<c:if test="${empty analiseSolicitacaoMatricula.discentes and empty analiseSolicitacaoMatricula.discentesPendentes }">
		<center>
		<i>
		N�o h� nenhuma solicita��o de matr�cula pendente de an�lise.
		</i>
		<br><br>
		<h:form>
			<h:commandButton value="Voltar"  action="#{analiseSolicitacaoMatricula.cancelar}" id="botaoDeVoltar"/>
		</h:form>
		</center>
	</c:if>

	</center>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
