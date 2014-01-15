<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
<f:subview id="menu">
<%@include file="/portais/tutor/menu_tutor.jsp" %>
</f:subview>

<h2>Trancamento de Matrículas</h2>
<br/>

	<center>
	<div class="infoAltRem"><h:graphicImage value="/img/seta.gif" style="overflow: visible;" />:
	Selecione um discente para realizar o trancamento<br>
	</div>
	<h:outputText value="#{ fichaAvaliacaoEad.create }"/>

	<c:set value="#{fichaAvaliacaoEad.discentesTutoria}" var="tutorias" scope="page"/>
	<c:if test="${not empty tutorias }">

	<h:form>
	<table class="listagem" id="lista-turmas" width="80%">
		<caption>Discentes do Tutor</caption>

		<tbody>
			<c:forEach items="#{tutorias}" var="t" varStatus="status">
				<tr class="${ (status.index % 2 == 0) ? 'linhaPar' : 'linhaImpar' }">
					<td width="90">
						${t.matricula}
					</td>
					<td>
						${t.pessoa.nome}
					</td>
					<td width="6%">
						<h:commandLink action="#{trancamentoMatriculaTutor.selecionaDiscente}" title="Avaliar Discente">
							<h:graphicImage value="/img/seta.gif"/>
							<f:param name="id" value="#{t.id }"/>
						</h:commandLink>
					</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	</h:form>

	</c:if>
	<br>
	<h:form>
	<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{fichaAvaliacaoEad.cancelar}" />
	</h:form>
	</center>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>