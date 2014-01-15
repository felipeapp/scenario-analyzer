<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> > Consulta de Habilitações de Graduação</h2>
	<h:outputText value="#{habilitacaoGrad.create}" />
	<h:form id="busca">
		<table class="formulario" width="50%">
			<caption>Busca por Habilitações</caption>
			<tbody>
				<tr>
					<td><input type="radio" id="checkNome" name="paramBusca" value="nome" class="noborder"></td>
					<td><label for="checkNome">Nome:</label></td>
					<td><h:inputText value="#{habilitacaoGrad.obj.nome}" size="40" id="param1"
						onfocus="marcaCheckBox('checkNome')" onkeyup="CAPS(this)" /></td>
				</tr>
				<tr>
					<td><input type="radio" id="checkCod" name="paramBusca" value="codigo" class="noborder"></td>
					<td><label for="checkCod">Código:</label></td>
					<td><h:inputText value="#{habilitacaoGrad.obj.codigoIes}" size="10" maxlength="10" id="param2"
						onfocus="marcaCheckBox('checkCod')" onkeyup="CAPS(this)" /></td>
				</tr>
				<tr>
					<td><input type="radio" name="paramBusca" value="todos" id="checkTodos" class="noborder"></td>
					<td><label for="checkTodos">Todos</label></td>
				</tr>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="3">
						<h:commandButton value="Buscar" action="#{habilitacaoGrad.buscar}" id="btnBuscar"/> 
						<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{habilitacaoGrad.cancelar}" id="btnCancelar" />
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>
	
	<c:if test="${not empty habilitacaoGrad.resultadosBusca}">
		<br>
		<c:if test="${sessionScope.acesso.cdp}">
			<div align="center" class="infoAltRem"><h:graphicImage value="/img/alterar.gif" style="overflow: visible;" />:
			Alterar dados da Habilitação<h:graphicImage value="/img/delete.gif" style="overflow: visible;" />:
			Remover Habilitação<br>
			</div>
		</c:if>
		<table class="listagem" style="width: 100%">
			<caption class="listagem">Lista de Habilitações Encontradas</caption>
			<thead>
				<tr>
					<td>Nome</td>
					<td>Curso</td>
					<td>Língua Obrigatória no Vestibular</td>
					<c:if test="${sessionScope.acesso.cdp}">
						<td></td>
						<td></td>
					</c:if>
				</tr>
			</thead>
			<c:forEach items="${habilitacaoGrad.resultadosBusca}" var="item" varStatus="loop">
				<tr class="${loop.index % 2 == 0 ? "linhaPar" : "linhaImpar"}" >
					<td>${item.nome}</td>
					<td>${item.curso.descricao}</td>
					<td>${item.linguaObrigatoriaVestibular.denominacao}</td>
					<c:if test="${sessionScope.acesso.cdp}">
							<td width=20>
							<h:form>
								<input type="hidden" value="${item.id}" name="id" /> <h:commandButton
								image="/img/alterar.gif" styleClass="noborder" value="Alterar" id="alterar"
								action="#{habilitacaoGrad.atualizar}" />
							</h:form></td>
							<td width=25>
							<h:form>
								<input type="hidden" value="${item.id}" name="id" /> <h:commandButton id="delete"
								image="/img/delete.gif" styleClass="noborder" alt="Remover" action="#{habilitacaoGrad.remover}" onclick="#{confirmDelete}"/>
							</h:form></td>
					</c:if>
				</tr>
			</c:forEach>
		</table>
	</c:if>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
