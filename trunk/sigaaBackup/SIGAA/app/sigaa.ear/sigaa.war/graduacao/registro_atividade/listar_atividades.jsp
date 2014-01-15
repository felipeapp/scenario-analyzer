<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>


<f:view>
<a4j:keepAlive beanName="registroAtividade" />
<%@include file="/graduacao/menu_coordenador.jsp" %>
<%@include file="/WEB-INF/jsp/include/errosAjax.jsp"%>
<h2><ufrn:subSistema /> > Consolidar Matrículas</h2>

	<div class="descricaoOperacao">
			<b> Caro(a) usuário, </b>
			<br/><br/>
			Nesta tela são listados todos os discentes matriculados em atividades complementares.
			<br/><br/>
			Após selecionar o discente será possível consolidar a atividade do mesmo.
			<br/><br/>
			Alternativamente, é possível buscar um discente específico para a consolidação acessando o link <b>Buscar Discente</b>.
	</div>
	
	<h:form id="form">
	
		<div class="infoAltRem">
			<img src="/sigaa/img/user.png">	
			<h:commandLink action="#{registroAtividade.iniciarConsolidacao}" value="Buscar Discente"/>
			<h:graphicImage value="/img/seta.gif"/>: Selecionar Discente
		</div>

		<c:if test="${not empty registroAtividade.atividadesMatriculadas}">
			<table class="listagem" style="width:100%">
			<caption class="listagem">Lista de Matrículas para Consolidar (${fn:length(registroAtividade.atividadesMatriculadas)})</caption>
			
			<thead>
			<tr>
				<th width="10%">Matrícula</th>
				<th width="50%">Discente</th>
				<th style="text-align:center;width:10%;">Status do Discente</th>
				<th style="text-align:center; width:7%">Período</th>
				<th></th>
			</tr>
			</thead>
				<c:set var="idAtividade" value="0" />		
				<c:forEach items="#{registroAtividade.atividadesMatriculadas}" var="m" varStatus="status">

					<c:if test="${idAtividade != m.componente.id}">
					<tr><td colspan="5" style="background-color: #C8D5EC;font-weight:bold;"><h:outputText value="#{m.componenteNome}"/></td></tr>
					</c:if>
					<tr class="${ status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
						<td><h:outputText value="#{m.discente.matricula}"/></td>
						<td><h:outputText value="#{m.discente.nome}"/></td>
						<td align="center"><h:outputText value="#{m.discente.statusString}"/></td>
						<td align="center"><h:outputText value="#{m.anoPeriodo}"/></td>
						<td align="right" width="2%">
							<h:commandLink  title="Selecionar Discente" action="#{registroAtividade.selecionarMatriculaAtividade}"	id="selecionar">
								<f:param value="#{m.id}" name="id"/>
								<h:graphicImage value="/img/seta.gif"/>
							</h:commandLink>
						</td>
					</tr>
					<c:set var="idAtividade" value="${m.componente.id}" />		
				</c:forEach>
			</table>
		</c:if>
	</h:form>
</f:view>

<script>
function checkAll() {
	$A(document.getElementsByClassName('check')).each(function(e) {
		e.checked = !e.checked;
	});
}
if (document.getElementById('checkTodos') != null){
	document.getElementById('checkTodos').checked = true;
	checkAll();
}
</script>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
