<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>

<%@ include file="/graduacao/matricula/cabecalho_matricula.jsp"%>

	<div class="infoAltRem"><h:graphicImage value="/img/seta.gif" style="overflow: visible;" />:
		Selecionar discente<br>
	</div>

	<c:set value="#{matriculaGraduacao.tutorias}" var="tutorias" />
	<c:if test="${not empty tutorias }">
		<h:form>
		<table class="listagem" id="lista-turmas" style="width:70%">
			<caption>Selecione um dos discentes sob sua tutoria</caption>
			<tbody>
				<c:forEach items="#{tutorias}" var="t" varStatus="status">
					<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
						<td width="15%"> ${t.aluno.matricula}</td>
						<td> ${t.aluno.nome}</td>
						<td width="3%">
							<c:choose>
								<c:when test="${requestScope.matriculaAtividade}">
									<h:commandLink action="#{registroAtividade.selecionaDiscenteTutoria}" title="Selecionar Discente" id="selecionaDiscenteTutoria">
										<h:graphicImage url="/img/seta.gif" />
										<f:param name="id" value="#{t.aluno.id}" />
									</h:commandLink>
								</c:when>
								<c:otherwise>
									<h:commandLink action="#{matriculaGraduacao.selecionarTutoriaAluno}" title="Selecionar Discente" id="btnTutoriaAluno">
										<h:graphicImage url="/img/seta.gif" />
										<f:param name="id" value="#{t.id}" />
									</h:commandLink>
								</c:otherwise>
							</c:choose>	
						</td>
					</tr>
				</c:forEach>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="3" align="center">
					<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{matriculaGraduacao.cancelarMatricula}" id="cancelarOperacaoo"/>
					</td>
				</tr>
			</tfoot>
		</table>
		</h:form>
	</c:if>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
