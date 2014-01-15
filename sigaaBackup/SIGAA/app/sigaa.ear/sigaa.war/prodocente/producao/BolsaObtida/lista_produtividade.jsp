<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<f:view>
	<c:set var="dirBase" value="/prodocente/producao/" scope="session"/>

	<h2>
		Pesquisa > Bolsistas de Produtividade
	</h2>

	<h:outputText value="#{bolsaObtida.create}" />
	

	<h:form id="formConsulta">
		<h:inputHidden id="produtividade" value="#{bolsaObtida.produtividade}" />

		<table class="formulario" align="center" width="85%">
		<caption class="listagem">Critérios de Busca</caption>

			<tr>
				<td width="3%"><h:selectBooleanCheckbox value="#{bolsaObtida.filtroDepartamento}" id="checkDepartamento" styleClass="noborder"/> </td>
				<td width="22%">Departamento:</td>
				<td>
					<h:selectOneMenu id="departamento" value="#{bolsaObtida.departamento.id}" style="width:90%" onfocus="$('formConsulta:checkDepartamento').checked = true;">
						<f:selectItem itemLabel=" -- SELECIONE UM DEPARTAMENTO -- " itemValue="0"/>
						<f:selectItems value="#{unidade.allDepartamentoCombo}"/>
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<td><h:selectBooleanCheckbox value="#{bolsaObtida.filtroServidor}" id="checkDocente" styleClass="noborder"/> </td>
				<td> Docente: </td>
				<td>
					<h:inputHidden id="idDocente" value="#{bolsaObtida.servidor.id}"></h:inputHidden>
					<h:inputText id="nomeDocente" value="#{bolsaObtida.servidor.pessoa.nome}" style="width: 90%;" onchange="$('formConsulta:checkDocente').checked = true;"/>
					<ajax:autocomplete
						source="formConsulta:nomeDocente" target="formConsulta:idDocente"
						baseUrl="/sigaa/ajaxDocente" className="autocomplete"
						indicator="indicator" minimumCharacters="3" parameters="tipo=ufrn,situacao=ativo"
						parser="new ResponseXmlToHtmlListParser()" />
					<span id="indicator" style="display:none; ">
						<img src="/sigaa/img/indicator.gif" />
				</td>
			</tr>
			<tr>
				<td><h:selectBooleanCheckbox value="#{bolsaObtida.filtroAno}" id="checkAno" styleClass="noborder"/> </td>
				<td> Ano: </td>
				<td>
					<h:inputText id="ano" value="#{bolsaObtida.ano}" size="5" maxlength="4" onkeyup="formatarInteiro(this)" onchange="$('formConsulta:checkAno').checked = true;"/>
				</td>
			</tr>
		<tfoot>
			<tr>
				<td colspan="3">
					<h:commandButton id="buscar" action="#{bolsaObtida.filtrarProdutividade}" value="Buscar"/>
					<h:commandButton id="cancelar" action="#{bolsaObtida.cancelar}" value="Cancelar"/>
				</td>
			</tr>
		</tfoot>
		</table>
	</h:form>

	<br />
	<h:form>
		<div class="infoAltRem">
			 <h:graphicImage value="/img/adicionar.gif"style="overflow: visible;"/>: <h:commandLink action="#{bolsaObtida.iniciarBolsaProdutividade}" value="Cadastrar Bolsista de Produtividade" />
		    <h:graphicImage value="/img/alterar.gif"style="overflow: visible;"/>: Alterar Bolsista
		    <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover Bolsista
		</div>
	</h:form>

	<c:if test="${empty lista}">
		<br />
		<center>
			<span style="color:red;">Nenhum bolsista de produtividade cadastrado.</span>
		</center>
	</c:if>

	<c:if test="${not empty lista}">
	<table class="listagem">
		<caption class="listagem">Bolsistas de Produtividade</caption>
		<thead>
			<tr>
			<td>Docente</td>
			<td>Período</td>
			<td>Tipo de Bolsa</td>
			<td></td>
			<td></td>
			<td></td>
		</thead>
		<c:forEach items="${lista}" var="item" varStatus="status">
			<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
				<td>${item.servidor.nome}</td>
				<td nowrap="nowrap">
					<fmt:formatDate  value="${item.periodoInicio }" pattern="MM/yyyy"/>
					a <fmt:formatDate  value="${item.periodoFim }" pattern="MM/yyyy"/>
				</td>
				<td nowrap="nowrap">
					${item.tipoBolsaProdocente.descricao}
				</td>

				<td width="10">
				  <h:form>
					  <input type="hidden" value="${item.id}" name="id" />
					  <h:commandButton
						image="/img/alterar.gif" value="Alterar"
						action="#{bolsaObtida.atualizar}" />
				  </h:form>
				</td>

				<td width="10">
				<h:form>
				  	<input type="hidden" value="${item.id}" name="id" />
				  	<h:commandButton
						image="/img/delete.gif" alt="Remover"
						action="#{bolsaObtida.remover}" onclick="#{confirmDelete}"/>
				 </h:form>
				</td>
			</tr>
		</c:forEach>
	</table>
	</c:if>
<h:outputText value="#{bolsaObtida.dropList}" />
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>