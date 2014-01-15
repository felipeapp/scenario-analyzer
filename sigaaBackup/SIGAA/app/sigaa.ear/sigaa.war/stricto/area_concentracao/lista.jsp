<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<a4j:keepAlive beanName="areaConcentracao"></a4j:keepAlive>
	<h2><ufrn:subSistema /> > Consulta de Áreas de Concentração</h2>
	<h:outputText value="#{areaConcentracao.create}" />
	<h:form id="busca">
		<a4j:keepAlive beanName="areaConcentracao"/>
		<table class="formulario" width="50%">
			<caption>Busca por Áreas de Concentração</caption>
			<tbody>
				<tr>
					<td><input type="radio" id="checkPrograma" name="paramBusca" value="programa" class="noborder" ${ areaConcentracao.param == 'programa' ? "checked='checked'" : ""}></td>
					<td><label for="checkPrograma">Programa:</label></td>
					<td>
						<h:selectOneMenu id="param1" value="#{areaConcentracao.areaConcentracaoBusca.programa.id}" onfocus="marcaCheckBox('checkPrograma')">
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
							<f:selectItems value="#{unidade.allProgramaPosCombo}" />
						</h:selectOneMenu>
					</td>
				</tr>
				<tr>
					<td><input type="radio" id="checkNome" name="paramBusca" value="nome" class="noborder" ${ areaConcentracao.param == 'nome' ? "checked='checked'" : ""}></td>
					<td><label for="checkNome">Nome:</label></td>
					<td><h:inputText value="#{areaConcentracao.areaConcentracaoBusca.denominacao}" size="40" id="param2"
						onfocus="marcaCheckBox('checkNome')" onkeyup="CAPS(this)" /></td>
				</tr>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="3"><h:commandButton value="Buscar" action="#{areaConcentracao.buscar}" id="buscar" /> 
					<h:commandButton value="Cancelar" action="#{areaConcentracao.cancelar}" onclick="#{confirm}" id="cancelar"/></td>
				</tr>
			</tfoot>
		</table>
	</h:form>
	<c:if test="${empty areaConcentracao.resultadosBusca}">
		<br><div style="font-style: italic; text-align:center">Nenhum registro a ser exibido</div>
	</c:if>
	<c:if test="${not empty areaConcentracao.resultadosBusca}">
		<br>
		<center>
		<div class="infoAltRem"><h:graphicImage value="/img/alterar.gif" style="overflow: visible;" />:
		Alterar Dados da Área de Concentração<h:graphicImage value="/img/delete.gif" style="overflow: visible;" />:
		Remover Área de Concentração<br />
		</div>
		</center>
			<table class=listagem>
			<caption class="listagem">Lista de Áreas de Concentração Encontrados</caption>
			<thead>
				<tr>
					<td>Programa</td>
					<td>Nível</td>
					<td>Área de Concentração</td>
					<td width="2%"></td>
					<td width="2%"></td>
				</tr>
			</thead>
			<c:forEach items="${areaConcentracao.resultadosBusca}" var="item" varStatus="status">
				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
					<td >${item.programa.sigla }</td>
					<td >${item.nivelDesc }</td>
					<td>${item.denominacao}</td>
					<td>
						<h:form prependId="false">
						<input type="hidden" value="${item.id}" name="id" id="idItemListado"/> <h:commandButton 
							image="/img/alterar.gif" styleClass="noborder" value="Alterar"
							action="#{areaConcentracao.atualizar}" title="Alterar Dados da Área de Concentração"/>
						</h:form>
					</td>
					<td >
						<h:form prependId="false">
						<input type="hidden" value="${item.id}" name="id"  id="idDoItemListado"/> <h:commandButton 
							image="/img/delete.gif" styleClass="noborder" alt="Remover" action="#{areaConcentracao.preRemover}" title="Remover Área de Concentração" />
						</h:form>
					</td>
				</tr>
			</c:forEach>
		</table>
	</c:if>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
