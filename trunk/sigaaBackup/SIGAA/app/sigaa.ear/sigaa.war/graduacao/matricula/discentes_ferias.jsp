<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<%@ include file="/graduacao/matricula/cabecalho_matricula.jsp"%>
	<center>
	<div class="infoAltRem">
		<h:form>
		<h:graphicImage value="/img/seta.gif" style="overflow: visible;" />: Selecione um discente para realizar a matrícula
		<h:graphicImage value="/img/cronograma/adicionar.gif" style="overflow: visible;" />: 
		<h:commandLink action="#{matriculaGraduacao.buscarDiscenteFerias}" value="Buscar Outro Discente" id="buscaDiscenteFerias"/>
		<br>
		</h:form>
	</div>
	<c:set value="${matriculaGraduacao.alunosFerias}" var="alunos" />
	<c:if test="${not empty alunos }">
	<table class="listagem" id="lista-turmas" width="80%">
		<caption>Discentes Que Solicitaram Turmas de Férias</caption>

		<thead>
			<td>Discentes</td>
			<td>Componente Solicitada</td>
			<td></td>
		</thead>

		<tbody>
			<c:forEach items="${alunos}" var="sol" varStatus="status">
				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
					<td>
						<a href="#" title="Ver detalhes">
						${sol.discenteGraduacao.matricula} - ${sol.discenteGraduacao.nome}
						</a>
					</td>
					<td>
						<a href="#" title="Ver detalhes">
						${sol.solicitacaoTurma.componenteCurricular}
						</a>
					</td>
					<td width="3%">
						<h:form>
						<input type="hidden" name="id" value="${sol.id }">
						<h:commandLink action="#{matriculaGraduacao.selecionaDiscenteFerias}" title="Selecionar Discente" id="selecionaDiscentefeerias">
							<h:graphicImage url="/img/seta.gif" />
						</h:commandLink>
						</h:form>
					</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	</c:if>
	<br>
	<h:form>
	<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{matriculaGraduacao.cancelarMatricula}" id="cancOperacao"/>
	</h:form>
	</center>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
