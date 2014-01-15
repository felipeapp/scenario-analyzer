<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<a4j:keepAlive beanName="municipio" />

<f:view>
<h:outputText value="#{municipio.create }"></h:outputText>
	<h2><ufrn:subSistema /> > Município</h2>
	<h:outputText value="#{municipio.create}" />

<ufrn:subSistema teste="not graduacao, consulta">
	<center>
			<h:messages/>
			<div class="infoAltRem">
				<h:form>
					<h:graphicImage value="/img/adicionar.gif"style="overflow: visible;"/>
					<h:commandLink action="#{municipio.preCadastrar}" value="Cadastrar"/>
				</h:form>
			</div>
	</center>
</ufrn:subSistema>
<h:form id="form">
	<table class="formulario" width="70%">
			<caption>Informe os Parâmetros da Busca</caption>
			<tr>
				<td><h:selectBooleanCheckbox value="#{municipio.filtroCodigo}" styleClass="noborder" id="checkCodigo"/></td>
				<td>Código:</td>
				<td>
					<h:inputText value="#{municipio.obj.codigo}" id="codigo" 
					maxlength="12" size="12" onfocus="$('form:checkCodigo').checked = true;"/>
				</td>
			</tr>
			<tr>
				<td><h:selectBooleanCheckbox value="#{municipio.filtroNome}" styleClass="noborder" id="checkNome"/></td>
				<td>Nome:</td>
				<td>
					<h:inputText value="#{municipio.obj.nome}" id="nomeMunicipio" onkeyup="CAPS(this);"
					maxlength="80" size="60" onfocus="$('form:checkNome').checked = true;"/>
				</td>
			</tr>
			<tr>
				<td><h:selectBooleanCheckbox value="#{municipio.filtroUF}" styleClass="noborder" id="checkComponente"/></td>
				<td>Unidade Federativa:</td>
				<td>
					<h:selectOneMenu value="#{municipio.obj.unidadeFederativa.id}" disabled="#{municipio.readOnly}" onchange="$('form:checkComponente').checked = true;">
						<f:selectItem itemLabel="-- SELECIONE --" itemValue="0"/>
						<f:selectItems value="#{unidadeFederativa.allCombo}" />
					</h:selectOneMenu>
				</td>
			</tr>
			<tfoot>
				<tr>
					<td colspan="3">
						<input type="hidden" name="regAtividadesEspecificas" id="regAtividadesEspecificas" value="${orientacaoAtividade.buscaRegistroAtivEspecificas}" />
						<h:commandButton value="Buscar" action="#{municipio.buscar}" id="busca"/>
						<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{municipio.cancelar}" id="cancelar"/>
					</td>
				</tr>
			</tfoot>
		</table>
</h:form>
	<br />
	<c:if test="${not empty municipio.lista }">
		<table class=listagem>
			<caption class="listagem">Lista de Municípios</caption>
			<thead>
			<tr>
				<td>Código</td>
				<td>Nome</td>
				<td>UF</td>
			</tr>
			<tbody>
			<c:forEach items="${municipio.lista}" var="item" varStatus="status">
				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
					<td>${item.codigo}</td>
					<td>${item.nome}</td>
					<td>${item.unidadeFederativa.descricao}</td>
				</tr>
			</c:forEach>
		</table>

	<center>
	<h:form>
	<div style="text-align: center;"> 
	    <h:commandButton image="/img/voltar.gif" actionListener="#{paginacao.previousPage}" rendered="#{paginacao.paginaAtual > 0 }"/>
	 
	    <h:selectOneMenu value="#{paginacao.paginaAtual}" valueChangeListener="#{paginacao.changePage}" onchange="submit()" immediate="true" rendered="#{ paginacao.totalPaginas > 1 }">
		<f:selectItems id="paramPagina" value="#{paginacao.listaPaginas}"/>
	    </h:selectOneMenu>
	 
	    <h:commandButton image="/img/avancar.gif" actionListener="#{paginacao.nextPage}"  rendered="#{paginacao.paginaAtual < (paginacao.totalPaginas - 1)}"/>
	    <br/><br/>
	 
	    <em><h:outputText value="#{paginacao.totalRegistros }"/> Registro(s) Encontrado(s)</em>
	</div>
	</h:form>
	</center>
	</c:if>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>