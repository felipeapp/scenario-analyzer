<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
<f:subview id="menu">
<%@include file="/portais/tutor/menu_tutor.jsp" %>
</f:subview>

<h2><ufrn:subSistema /> > Avaliação Periódica</h2>
<br/>

	<center>
	<div class="infoAltRem"><h:graphicImage value="/img/seta.gif" style="overflow: visible;" />:
	Selecione um discente para visualizar a ficha de avaliação<br>
	</div>
	<c:set value="${fichaAvaliacaoEad.discentesTutoria}" var="tutorias" />
	<c:if test="${not empty tutorias }">
	<table class="listagem" id="lista-turmas" width="80%">
		<caption>Discentes do Tutor</caption>

		<tbody>
			<c:forEach items="${tutorias}" var="t" varStatus="status">
				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
					<td width="90">
						<a href="#" title="Ver detalhes">
						${t.matricula}
						</a>
					</td>
					<td>
						<a href="#" title="Ver detalhes">
						${t.pessoa.nome}
						</a>
					</td>
					<td width="6%">
						<h:form>
						<input type="hidden" name="discente" value="${t.id }">
						<h:commandButton image="/img/icones/page_white_magnify.png" action="#{fichaAvaliacaoEad.verFichaDiscente}" title="Ver Ficha de Avaliação"/>
						</h:form>
					</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	</c:if>
	<br>
	<h:form>
	<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{fichaAvaliacaoEad.cancelar}" />
	</h:form>
	</center>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>