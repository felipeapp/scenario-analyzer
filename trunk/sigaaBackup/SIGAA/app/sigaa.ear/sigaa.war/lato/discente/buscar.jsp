<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<f:view>
	<h:form id="form">
	<h:inputHidden id="codigoOperacao" value="#{buscaPessoa.codigoOperacao}"/>

	<h2> <ufrn:subSistema /> &gt; Buscar Discente</h2>

	<table class="formulario" style="width:75%;">
		<caption> Informe o critério de busca desejado</caption>
		<tbody>
			<tr>
				<td><h:selectBooleanCheckbox value="#{discenteLato.buscaMatricula}" id="checkMatricula" styleClass="noborder" /></td>
				<th style="text-align: left;"> <label for="checkMatricula" onclick="$('form:checkMatricula').checked = !$('form:checkMatricula').checked;">Matrícula:</label></th>													   
				<td> <h:inputText value="#{discenteLato.obj.discente.matricula}" 
						id="matricula" size="10" maxlength="15" onkeyup="formatarInteiro(this);"
						onfocus="$('form:checkMatricula').checked = true;"/>
				</td>
			</tr>
			<tr>
				<td><h:selectBooleanCheckbox value="#{discenteLato.buscaNome}" id="checkNome" styleClass="noborder" /></td> 
				<th style="text-align: left;"> <label for="checkNome" onclick="$('form:checkNome').checked = !$('form:checkNome').checked;" >Nome:</label></th>
				<td> <h:inputText value="#{discenteLato.obj.discente.pessoa.nome}" size="60"  maxlength="200"
						 id="nomePessoa" onfocus="$('form:checkNome').checked = true;"/> 
				</td>
			</tr>
			<tr>
				<c:choose>
				<c:when test="${discenteLato.usuarioLogado.coordenadorLato}">
					<td><h:selectBooleanCheckbox value="#{discenteLato.buscaCurso}" id="checkCurso" styleClass="noborder" disabled="true" /></td>
					<td>Curso:</td>
					<td>					
						<h:inputHidden id="idCurso" value="#{discenteLato.cursoAtualCoordenacao.id}" />
						<h:outputText value="#{discenteLato.cursoAtualCoordenacao.nome}" />
				</c:when>
				<c:otherwise>
					<td><h:selectBooleanCheckbox value="#{discenteLato.buscaCurso}" id="checkCurso" styleClass="noborder" /></td>
					<th style="text-align: left;"><label for="checkCurso" onclick="$('form:checkCurso').checked = !$('form:checkCurso').checked;" >Curso:</label> </th>
					<td>
					 	<h:inputHidden id="idCurso" value="#{discenteLato.obj.discente.curso.id}" />
						<h:inputText id="nomeCurso"	value="#{discenteLato.obj.discente.curso.nome}" size="80" 
								onfocus="$('form:checkCurso').checked = true;"/> 
						<ajax:autocomplete source="form:nomeCurso" target="form:idCurso"
								baseUrl="/sigaa/ajaxCurso" className="autocomplete"
								indicator="indicatorCurso" minimumCharacters="3" parameters="nivel=L"
								parser="new ResponseXmlToHtmlListParser()" /> 
						<span id="indicatorCurso" style="display:none; "> <img src="/sigaa/img/indicator.gif" /> </span>
				</c:otherwise>
				</c:choose>
				</td>
			</tr>
		</tbody>
		<tfoot>
			<tr>
				<td colspan="3">
					<h:commandButton id="buscar" action="#{discenteLato.buscar}" value="Buscar"/>
					<h:commandButton id="cancelar" action="#{discenteLato.cancelar}" value="Cancelar" onclick="#{confirm}"/>
				</td>
			</tr>
		</tfoot>
	</table>
	</h:form>

	<c:if test="${empty discenteLato.lista}">
		<br><div style="font-style: italic; text-align:center">Nenhum registro encontrado de acordo com os critérios de busca informados.</div>
	</c:if>

	<c:if test="${not empty discenteLato.lista}">
		<br />
		<table class="listagem" width="75%">
			<caption class="listagem">Selecione um Discente na lista abaixo:</caption>
			<thead>
				<tr>
					<td width="10%" style="text-align: center;">Matrícula</td>
					<td>Nome</td>
					<td>Curso</td>
					<td style="text-align: center;">Ano</td>
					<td width="8%"></td>
				</tr>
			</thead>
			<h:form id="formulario">
			<div class="infoAltRem">
    			<h:graphicImage value="/img/alterar.gif" style="overflow: visible;"/>: Atualizar Dados Pessoais
    			<h:graphicImage value="/img/user.png" style="overflow: visible;"/>: Atualizar Dados Acadêmicos 
    			<h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover Discente 
			</div>
			<c:forEach items="#{discenteLato.lista}" var="item" varStatus="status">
				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
					<td style="text-align: center;">${item.matricula }</td>
					<td>${item.pessoa.nome}</td>
					<td>${item.curso.descricao }</td>
					<td style="text-align: center;">${item.anoIngresso}</td>
					<td width="3%">

						<h:commandLink id="atualizarDiscente" action="#{discenteLato.atualizarDadosPessoais}" title="Atualizar Dados Pessoais">
							<h:graphicImage url="/img/alterar.gif"/>
							<f:param name="idDiscente" value="#{item.id}"/>
						</h:commandLink>
						<h:commandLink id="selecionarDiscente" action="#{discenteLato.selecionaDiscente}" title="Atualizar Dados Acadêmicos">
							<h:graphicImage url="/img/user.png"/>
							<f:param name="idDiscente" value="#{item.id}"/>
						</h:commandLink>
						<h:commandLink id="removerDiscente" action="#{discenteLato.remover}" title="Remover Discente" onclick="#{confirmDelete}">
							<h:graphicImage url="/img/delete.gif"/>
							<f:param name="idDiscente" value="#{item.id}"/>
						</h:commandLink>
					</td>
				</tr>
			</c:forEach>
			</h:form>
		</table>
	</c:if>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>