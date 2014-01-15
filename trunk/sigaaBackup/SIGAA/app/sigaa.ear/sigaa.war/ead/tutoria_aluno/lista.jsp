<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<c:set var="confirmDelete" value="if (!confirm('Deseja realmente remover esta orientação acadêmica?')) return false" scope="request"/>

<f:view>
	<%@include file="/graduacao/menu_coordenador.jsp" %>
	<h2><ufrn:subSistema /> > Orientação Acadêmica > Lista</h2>
	<h:outputText value="#{tutoriaAluno.create}" />

	<table class="formulario" width="50%">
	<h:form id="formBusca">
	<caption>Busca por Orientação Acadêmica</caption>
	<tbody>
		<tr>
			<td><input type="radio" id="checkOrientador" name="paramBusca" value="orientador" class="noborder"></td>
			<td><label for="checkOrientador">Orientador</label></td>
			<td>
				<h:inputText value="#{tutoriaAluno.orientador.pessoa.nome}" id="nome" size="60" onchange="javascript:$('formBusca:checkOrientador').checked = checked;"/>
				 <h:inputHidden value="#{tutoriaAluno.orientador.id}" id="idServidor" />
	
				 <ajax:autocomplete source="formBusca:nome" target="formBusca:idServidor"
					baseUrl="/sigaa/ajaxServidor" className="autocomplete"
					indicator="indicator" minimumCharacters="3" parameters="tipo=todos"
					parser="new ResponseXmlToHtmlListParser()" />
				<span id="indicator" style="display:none; "> <img src="/sigaa/img/indicator.gif" /> </span>

			</td>
		</tr>
		<tr>
			<td><input type="radio" id="checkDiscente" name="paramBusca" value="discente" class="noborder"></td>
			<td><label for="checkDiscente">Discente</label></td>
			<td>
				 <h:inputHidden id="idDiscente" value="#{ tutoriaAluno.discente.id }"/>
				 <h:inputText id="nomeDiscente" value="#{ tutoriaAluno.discente.pessoa.nome }" size="90" onchange="javascript:$('formBusca:checkDiscente').checked = checked;"/>
			
				<ajax:autocomplete source="formBusca:nomeDiscente" target="formBusca:idDiscente"
					baseUrl="/sigaa/ajaxDiscente" className="autocomplete"
					indicator="indicatorDiscente" minimumCharacters="3" parameters="nivel=G"
					parser="new ResponseXmlToHtmlListParser()" />
			
				<span id="indicatorDiscente" style="display:none; "> <img src="/sigaa/img/indicator.gif"  alt="Carregando..." title="Carregando..."/> </span>
			</td>
		</tr>
		<tr>
			<td><input type="radio" name="paramBusca" value="todos" id="checkTodos" class="noborder"></td>
			<td><label for="checkTodos">Todos</label></td>
		</tr>
	</tbody>
	<tfoot>
		<tr>
			<td colspan="3">
				<h:commandButton value="Buscar" action="#{orientacaoAcademica.buscar}" /> 
				<h:commandButton value="Cancelar" action="#{orientacaoAcademica.cancelar}" />
			</td>
		</tr>
	</tfoot>
	</h:form>
	</table>

	<center>
	<div class="infoAltRem">
		<h:graphicImage value="/img/delete.gif" style="overflow: visible;" />:Remover Orientação Acadêmica
		<br />
	</div>
	</center>


	<c:if test="${empty orientacaoAcademica.orientacoes}">
		<br><div style="font-style: italic; text-align:center">Nenhuma orientação acadêmica encontrada para alunos do seu curso.</div>
	</c:if>
	
	<c:if test="${not empty orientacaoAcademica.orientacoes}">

		<table class=listagem>
			<caption class="listagem">Lista de Orientações Acadêmicas</caption>
			<thead>
				<tr>
					<td>Discente</td>
					<td>Orientador Acadêmico</td>
					<td>Data de Início</td>
					<td></td>
				</tr>
			</thead>
			<c:forEach items="${orientacaoAcademica.orientacoes}" var="item">
				<tr>
					<td>${item.discente}</td>
					<td>${item.servidor}</td>
					<td>${item.inicio}</td>
					<h:form>
						<td width=25>
							<input type="hidden" value="${item.id}" name="id" /> 
							<h:commandButton image="/img/delete.gif" styleClass="noborder" alt="Remover"
							action="#{orientacaoAcademica.removerOrientacao}" onclick="#{confirmDelete}"/>
						</td>
					</h:form>
				</tr>
			</c:forEach>
		</table>
	</c:if>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
