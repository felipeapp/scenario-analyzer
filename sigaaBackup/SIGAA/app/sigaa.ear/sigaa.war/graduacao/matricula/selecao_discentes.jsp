<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<%@ include file="/graduacao/matricula/cabecalho_matricula.jsp"%>
	
	<div class="descricaoOperacao">
		<p>
			Selecione um discente abaixo para realizar sua matrícula.
		</p>
	</div>
	
	<div class="infoAltRem" style="width: 80%">
	<h:graphicImage value="/img/seta.gif" style="overflow: visible;" />:
	Selecionar Discente
	</div>
	
	<h:form>
	<c:if test="${not empty matriculaGraduacao.discentesCurso }">
	<table class="listagem" id="lista-turmas-alunoscadastrados" style="width: 80%">
		<caption>${matriculaGraduacao.tituloSelecaoDiscente} (${matriculaGraduacao.calendarioParaMatricula.anoPeriodo }) </caption>

		<tbody>
			<c:forEach items="#{matriculaGraduacao.discentesCurso}" var="aluno" varStatus="status">
				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
					<td style="">
						<h:commandLink style="text-decoration: none; font-weight: normal; color: black" id="btaoDeSelecionarDiscente"
							 action="#{matriculaGraduacao.selecionaDiscente}" value="#{aluno.matriculaNome}">
								<f:param value="#{aluno.id}" name="idDiscente"/>
						</h:commandLink>
					</td>
					<td width="3%">
						<h:commandLink action="#{matriculaGraduacao.selecionaDiscente}" title="Selecionar Discente" id="botaoselecionaDiscenta">
							<f:param value="#{aluno.id}" name="idDiscente"/>
							<h:graphicImage url="/img/seta.gif" />
						</h:commandLink>
					</td>
				</tr>
			</c:forEach>
		</tbody>
		<tfoot>
			<tr>	
				<td colspan="2" align="center"> 
					<b>${fn:length(matriculaGraduacao.discentesCurso)} discente(s) ingressante(s)</b> 
				</td>
			</tr>
		</tfoot>
	</table>
	</c:if>
	<br>
	<div align="center">
		<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{matriculaGraduacao.cancelarMatricula}" id="botaoDeCancelar"/>
	</div>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
