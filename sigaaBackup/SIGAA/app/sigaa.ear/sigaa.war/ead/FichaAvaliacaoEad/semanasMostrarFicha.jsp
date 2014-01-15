<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
<f:subview id="menu" rendered="#{ usuario.vinculoAtivo.vinculoTutorOrientador }">
<%@include file="/portais/tutor/menu_tutor.jsp" %>
</f:subview>

<h2><ufrn:subSistema /> > Avaliação Periódica</h2>
<br/>

	<center>
	<div class="infoAltRem"><h:graphicImage value="/img/seta.gif" style="overflow: visible;" />:
	Selecione uma semana/componente para visualizar a avaliação<br>
	</div>
	<c:set value="${fichaAvaliacaoEad.metodologia.semanasAvaliacaoAtivas}" var="semanas" />
	<c:if test="${not empty semanas }">
	<table class="listagem" id="lista-turmas" width="80%">
		<caption>Componentes Curriculares</caption>

		<tbody>
			<c:forEach items="${semanas}" var="s" varStatus="status">
				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
					<td>
						${s.descricao}
					</td>
					<td width="6%">
						<h:form>
						<input type="hidden" name="semana" value="${ s.semana }">
						<h:commandButton image="/img/seta.gif" action="#{fichaAvaliacaoEad.selecionaSemanaMostrarFicha}" title="Selecionar Componente"/>
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