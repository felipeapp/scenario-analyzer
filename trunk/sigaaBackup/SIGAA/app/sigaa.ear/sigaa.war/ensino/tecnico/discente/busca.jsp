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
				<td><h:selectBooleanCheckbox value="#{discenteTecnico.filtroMatricula}" id="checkMatricula" styleClass="noborder" /></td>
				<th style="text-align: left;"> <label for="checkMatricula" onclick="$('form:checkMatricula').checked = !$('form:checkMatricula').checked;">Matrícula:</label></th>													   
				<td> <h:inputText value="#{discenteTecnico.obj.discente.matricula}" 
						id="matricula" size="10" maxlength="15" onkeyup="formatarInteiro(this);"
						onfocus="$('form:checkMatricula').checked = true;"/>
				</td>
			</tr>
			<tr>
				<td><h:selectBooleanCheckbox value="#{discenteTecnico.filtroNome}" id="checkNome" styleClass="noborder" /></td> 
				<th style="text-align: left;"> <label for="checkNome" onclick="$('form:checkNome').checked = !$('form:checkNome').checked;" >Nome:</label></th>
				<td> <h:inputText value="#{discenteTecnico.obj.discente.pessoa.nome}" size="60"  maxlength="200"
						 id="nomePessoa" onfocus="$('form:checkNome').checked = true;"/> 
				</td>
			</tr>
			<tr>
				<td><h:selectBooleanCheckbox value="#{discenteTecnico.filtroCurso}" id="checkCurso" styleClass="noborder" /></td>
				<th style="text-align: left;"><label for="checkCurso" onclick="$('form:checkCurso').checked = !$('form:checkCurso').checked;" >Curso:</label> </th>
				<td>
				 	<h:inputHidden id="idCurso" value="#{discenteTecnico.obj.discente.curso.id}" />
					<h:inputText id="nomeCurso"	value="#{discenteTecnico.obj.discente.curso.nome}" size="80" 
							onfocus="$('form:checkCurso').checked = true;"/> 
					<ajax:autocomplete source="form:nomeCurso" target="form:idCurso"
							baseUrl="/sigaa/ajaxCurso" className="autocomplete"
							indicator="indicatorCurso" minimumCharacters="3" parameters="nivel=${implantarHistorico.nivelEnsino}"
							parser="new ResponseXmlToHtmlListParser()" /> 
					<span id="indicatorCurso" style="display:none; "> <img src="/sigaa/img/indicator.gif" /> </span>
				</td>
			</tr>
		</tbody>
		<tfoot>
			<tr>
				<td colspan="3">
					<h:commandButton id="buscar" action="#{discenteTecnico.buscar}" value="Buscar"/>
					<h:commandButton id="cancelar" action="#{discenteTecnico.cancelar}" value="Cancelar" onclick="#{confirm}"/>
				</td>
			</tr>
		</tfoot>
	</table>
	</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>