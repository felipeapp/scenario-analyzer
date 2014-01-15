<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<style>
	.colCH{text-align:right !important;width: 80px;}
	.colIcon{width: 18px;}
</style>
<f:view>
	<%@include file="/graduacao/menu_coordenador.jsp" %>
	<%@include file="/stricto/menu_coordenador.jsp" %>

	<h:outputText value="#{ registroAtividade.create }" />
	<h2><ufrn:subSistema /> &gt; ${registroAtividade.descricaoOperacao} &gt; Seleção da Atividade Acadêmica Específica</h2>

	<c:if test="${ registroAtividade.validacao }">
		<div class="descricaoOperacao" style="width: 90%">
			${ registroAtividade.resolucaoValidacaoAtividadeGraduacao }
		</div>
	</c:if>

	<c:set var="discente" value="#{registroAtividade.obj.discente}" />
	<%@include file="/graduacao/info_discente.jsp"%>

	<h:form id="form">
	<table class="formulario" style="width: 90%">
		<caption> Selecione os critérios de busca das atividades </caption>
		<tbody>
			<tr>
				<td><h:selectBooleanCheckbox value="#{registroAtividade.buscaDepartamento}" id="departamento" styleClass="noborder" /></td>
				<td><label for="departamento" onclick="$('form:departamento').checked = !$('form:departamento').checked;">
						<c:if test="${!acesso.algumUsuarioStricto}">Departamento:</c:if>
						<c:if test="${acesso.algumUsuarioStricto}">Programa:</c:if>
					</label> 
				</td>
				<td><h:selectOneMenu id="idUnidadeComponente" value="#{ registroAtividade.obj.componente.unidade.id }"
							onchange="getEl('form:departamento').dom.checked = true;" style="width: 500px;">
						<f:selectItem itemValue="-1" itemLabel=" -- SELECIONE --" />
						
						<c:if test="${acesso.gestorResidenciaMedica}">
							<f:selectItems value="#{ unidade.allProgramaResidenciaCombo }"/>
						</c:if>
						<c:if test="${!acesso.algumUsuarioStricto and !acesso.gestorResidenciaMedica}">
							<f:selectItems value="#{ unidade.allDeptosEscolasCoordCursosCombo }" />
						</c:if>
						<c:if test="${acesso.algumUsuarioStricto}">
							<f:selectItems value="#{unidade.allProgramaPosCombo}" />
						</c:if>
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<td><h:selectBooleanCheckbox value="#{registroAtividade.buscaNomeAtividade}" id="nomeAtividade" styleClass="noborder" /></td>
				<td><label for="nomeAtividade" onclick="$('form:nomeAtividade').checked = !$('form:nomeAtividade').checked;">
					Atividade:</label></td>
				<td><h:inputText id="nomeAtividadeInput" value="#{registroAtividade.obj.componente.detalhes.nome}" 
						size="70" maxlength="70" onfocus="$('form:nomeAtividade').checked = true;" /></td>
			</tr>
			<tr>
				<td><h:selectBooleanCheckbox value="#{registroAtividade.buscaTipoAtividade}" id="tipoAtividade" styleClass="noborder"/></td>

				<td><label for="tipoAtividade" onclick="$('form:tipoAtividade').checked = !$('form:tipoAtividade').checked;">
					Tipo de Atividade:</label></td>
				<td><h:selectOneMenu id="idTipoAtividade" value="#{ registroAtividade.obj.componente.tipoAtividade.id }"
							onchange="getEl('form:tipoAtividade').dom.checked = true;">
						<f:selectItem itemValue="-1" itemLabel=" -- SELECIONE -- " />
						<f:selectItems value="#{ tipoAtividade.allCombo }" />
					</h:selectOneMenu>
				</td>
			</tr>
		</tbody>
		<tfoot>
			<tr>
				<td colspan="3">
					<h:commandButton action="#{registroAtividade.buscarAtividades}" value="Buscar Atividades" id="atividades" />
					<h:commandButton action="#{registroAtividade.buscarDiscente}" value="<< Selecionar Outro Discente" id="voltar"/>
					<h:commandButton action="#{registroAtividade.cancelar}" value="Cancelar" onclick="#{confirm}" id="cancelar"/>
				</td>
			</tr>
		</tfoot>
	</table>
	</h:form>
	
	<c:set var="atividades" value="#{registroAtividade.atividades}" />
	<c:if test="${!empty atividades}">
		<br />
		<div class="infoAltRem">
			<h:graphicImage value="/img/seta.gif" 	style="overflow: visible;"/>: Selecionar Atividade
		</div>
		<table class="listagem">
			<caption> Selecione uma das atividades abaixo (${fn:length(atividades)}) </caption>
			<thead>
				<tr>
					<th> Atividade </th>
					<th> Tipo </th>
					<th class="colCH"> CH </th>
					<th class="colIcon"> </th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${atividades}" var="atividade" varStatus="status">
				<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
					<td>${atividade.codigoNome}</td>
					<td>${atividade.tipoAtividade}</td>
					<td class="colCH"> ${atividade.chTotal}h </td>
					<td class="colIcon">
						<h:form>
							<input type="hidden" value="${atividade.id}" name="idAtividade" />
							<h:commandButton image="/img/seta.gif" alt="Selecionar Atividade" title="Selecionar Atividade" 
									action="#{registroAtividade.selecionarAtividade}" />
						</h:form>
					</td>
				</tr>
				</c:forEach>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="5" align="center"> <b>${fn:length(atividades)} atividades encontradas</b> </td>
				</tr>
			</tfoot>
		</table>
	</c:if>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>