<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>

<%@include file="/stricto/menu_coordenador.jsp" %>
<%@include file="/graduacao/menu_coordenador.jsp" %>
<h2> <ufrn:subSistema/> &gt; ${registroAtividade.descricaoOperacao} &gt; Seleção de atividade </h2>

<c:set var="discente" value="#{registroAtividade.obj.discente}"/>
<%@include file="/graduacao/info_discente.jsp"%>

<div class="infoAltRem" style="width: 100%;">
	<img src="${ctx}/img/seta.gif">: Selecionar Atividade
</div>

<h:form id="form">
<table class="formulario">
	<caption>Esse discente possui ${fn:length(registroAtividade.atividadesMatriculadas) } matrícula(s) em atividades acadêmicas específicas</caption>
	<thead>
		<tr>
			<td>Atividades</td>
			<td width="8%">Período</td>
			<td width="2%"> </td>
		</tr>
	</thead>
	
	<tbody>
		<c:forEach items="#{registroAtividade.atividadesMatriculadas}" var="mat" varStatus="status">
			<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
				<td>${mat.componenteDescricao }</td>
				<td>${mat.anoPeriodo }</td>
				<td>
					<h:commandLink title="Selecionar Atividade" action="#{registroAtividade.selecionarMatriculaAtividade}" id="selecionar">
						<f:param value="#{mat.id}" name="id"/>
						<h:graphicImage value="/img/seta.gif"/>
					</h:commandLink>
				</td>
			</tr>
		</c:forEach>
	</tbody>
	
	<tfoot>
		<tr>
			<td colspan="3">
				<h:commandButton value="<< Selecionar Outro Discente" action="#{registroAtividade.selecionarDiscente}" id="btnOutroDiscente"/>
				<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{registroAtividade.cancelar}" id="btnCancelar"/>
			</td>
		</tr>
	</tfoot>
	
</table>
</h:form>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>