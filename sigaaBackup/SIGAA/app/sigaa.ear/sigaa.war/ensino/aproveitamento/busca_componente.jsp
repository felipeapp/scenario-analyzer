<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<%@include file="/stricto/menu_coordenador.jsp" %>
	<h:outputText value="#{aproveitamento.create}" />
	<h2 class="title"><ufrn:subSistema /> > Aproveitamento de Estudos &gt; Busca de Componentes</h2>
	<h:form id="buscaCC">
		<table class="formulario" width="90%">
			<caption>Selecione um Componente Curricular</caption>
			<tbody>
				<tr>
					<td width="5%"><input type="checkbox" id="checkCodigo" name="buscaCodigo" value="true"
						class="noborder"></td>
					<th><label for="checkCodigo">Código</label></th>
					<td><h:inputText size="10" value="#{aproveitamento.obj.componente.codigo }"
						onfocus="marcaCheckBox('checkCodigo')" /></td>
				</tr>
				<tr>
					<td><input type="checkbox" id="checkNome" name="buscaNome" value="true" class="noborder"></td>
					<th><label for="checkNome">Nome</label></th>
					<td><h:inputText size="60" value="#{aproveitamento.obj.componente.nome }"
						onfocus="marcaCheckBox('checkNome')" /></td>
				</tr>
				<tr>
					<td><input type="checkbox" id="checkTipo" name="buscaTipo" value="true" class="noborder"></td>
					<th><label for="checkTipo">Tipo</label></th>
					<td><h:selectOneMenu id="tipos"
						value="#{aproveitamento.obj.componente.tipoComponente.id}"
						onfocus="marcaCheckBox('checkTipo')">
						<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
						<f:selectItems value="#{componenteCurricular.tiposComponentes}" />
					</h:selectOneMenu></td>
				</tr>
				<ufrn:subSistema teste="not infantil,medio,tecnico">
				<tr>
					<td><input type="checkbox" id="checkUnidade" name="buscaUnidade" value="true"
						class="noborder"></td>
					<th><label for="checkUnidade">Unidade Acadêmica</label></th>
					<td><h:selectOneMenu id="unidades" style="width: 400px"
						value="#{aproveitamento.obj.componente.unidade.id}"
						onfocus="marcaCheckBox('checkUnidade')">
						<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
						<f:selectItems value="#{unidade.allDepartamentoCombo}" />
					</h:selectOneMenu></td>
				</tr>
				</ufrn:subSistema>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="3"><h:commandButton value="Buscar" action="#{aproveitamento.buscarComponentes}" />
					<h:commandButton value="Cancelar" onclick="#{confirm}"
						action="#{aproveitamento.cancelar}" />
					<h:commandButton value="<< Escolher outro Discente" action="#{aproveitamento.iniciar}" /></td>
				</tr>
			</tfoot>
		</table>
	</h:form>

	<c:if test="${not empty aproveitamento.componentesEncontrados}">
		<br>
		<table class="listagem">
			<thead>
				<tr>
				<td colspan="2">${fn:length(aproveitamento.componentesEncontrados) } Componentes Econtrados </td>
				</tr>
			</thead>
			<tbody>
			<c:forEach items="${aproveitamento.componentesEncontrados }" var="cc" varStatus="status">
				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
					<td> ${cc.descricao} </td>
					<td width="2%">
					<h:form>
					<input type="hidden" name="id" value="${cc.id}" >
					<h:commandButton image="/img/seta.gif" title="Escolher Componente" action="#{aproveitamento.selecionarComponente}"  />
					</h:form>
					</td>
				</tr>
			</c:forEach>
			</tbody>
		</table>
	</c:if>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
