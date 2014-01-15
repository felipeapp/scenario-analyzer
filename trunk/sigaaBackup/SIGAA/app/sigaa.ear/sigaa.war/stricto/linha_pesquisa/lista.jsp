<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> > Consulta de Linhas de Pesquisa</h2>
	<h:outputText value="#{linhaPesquisa.create}" />
	<h:form id="busca">
		<table class="formulario" width="50%">
			<caption>Busca por Linhas de Pesquisa</caption>
			<tbody>
				<tr>
					<td>
						<input type="radio" id="checkPrograma" name="paramBusca" value="programa" 
							class="noborder" ${ linhaPesquisa.param == 'programa' ? "checked='checked'" : ""}>
					</td>
					<td><label for="checkPrograma">Programa:</label></td>
					<td>
						<h:selectOneMenu id="param1" value="#{linhaPesquisa.obj.programa.id}" onfocus="marcaCheckBox('checkPrograma')">
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
							<f:selectItems value="#{unidade.allProgramaPosCombo}" />
						</h:selectOneMenu>
					</td>
				</tr>
				<tr>
					<td>
						<input type="radio" id="checkNome" name="paramBusca" value="nome" 
							class="noborder" ${ linhaPesquisa.param == 'nome' ? "checked='checked'" : ""}>
					</td>
					<td><label for="checkNome">Nome:</label></td>
					<td><h:inputText value="#{linhaPesquisa.obj.denominacao}" size="40" id="param2"
						onfocus="marcaCheckBox('checkNome')" onkeyup="CAPS(this)" /></td>
				</tr>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="3"><h:commandButton value="Buscar" action="#{linhaPesquisa.buscar}" id="buscar"/> <h:commandButton
						value="Cancelar" action="#{linhaPesquisa.cancelar}" onclick="#{confirm}" id="cancelar" /></td>
				</tr>
			</tfoot>
		</table>
	</h:form>
	<c:if test="${empty linhaPesquisa.resultadosBusca}">
		<br><div style="font-style: italic; text-align:center">Nenhum registro a ser exibido</div>
	</c:if>
	<c:if test="${not empty linhaPesquisa.resultadosBusca}">
		<br>
		<center>
		<div class="infoAltRem"><h:graphicImage value="/img/alterar.gif" style="overflow: visible;" />:
		Alterar Dados da Linha de Pesquisa<h:graphicImage value="/img/delete.gif" style="overflow: visible;" />:
		Remover Linha de Pesquisa
		</div>
		</center>
		<br />
			<table class=listagem>
			<caption class="listagem">Lista de Linhas de Pesquisa Encontrados</caption>
			<thead>
				<tr>
					<td>Linha de Pesquisa</td>
					<td>Nível</td>
					<td>Área de Concentração</td>
					<td></td>
					<td></td>
				</tr>
			</thead>
			<c:forEach items="${linhaPesquisa.resultadosBusca}" var="item" varStatus="loop">
				<tr class="${loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
					<td>${item.denominacao}</td>
					<td>${item.area.nivelDesc}</td>
					<td>${item.area.denominacao}</td>
					<td width=20>
						<h:form id="alterarForm">
							<input type="hidden" value="${item.id}" name="id" /> <h:commandButton
							image="/img/alterar.gif" styleClass="noborder" value="Alterar" id="edit"
							action="#{linhaPesquisa.atualizar}" title="Alterar Dados da Linha de Pesquisa" />
						</h:form>
					</td>
					<td width=25>
						<h:form id="removerForm">
							<input type="hidden" value="${item.id}" name="id" /> <h:commandButton id="remove"
							image="/img/delete.gif" styleClass="noborder" alt="Remover" action="#{linhaPesquisa.preRemover}"
							title="Remover Linha de Pesquisa" />
						</h:form>
					</td>
				</tr>
			</c:forEach>
		</table>
	</c:if>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
