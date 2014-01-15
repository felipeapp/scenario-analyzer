<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>

<f:view>
	<%@include file="/graduacao/menu_coordenador.jsp" %>
	<%@include file="/stricto/menu_coordenador.jsp" %>
	<h2> <ufrn:subSistema /> > Cadastrar Orientação Acadêmica > Selecionar Orientador </h2>
	<h:messages showDetail="true"></h:messages>

	<div class="descricaoOperacao">
		<p>Caro Usuário, <p>
		<p>
			Selecione o docente para cadastrar a orientação acadêmica.
		</p>
	</div>

	<h:form id="form">
	<table class="formulario" width="50%">
		<caption> Selecione o Orientador Acadêmico </caption>
		<tbody>
			<tr>
				<th class="required"> Orientador: </th>

				<c:if test="${not (acesso.secretariaPosGraduacao || acesso.coordenadorCursoStricto)}">
				<td>
					<h:inputHidden id="id" value="#{orientacaoAcademica.orientador.id}"></h:inputHidden>
					<h:inputText id="nome" value="#{orientacaoAcademica.orientador.pessoa.nome}" size="50" />

					<ajax:autocomplete
					source="form:nome" target="form:id"
					baseUrl="/sigaa/ajaxDocente" className="autocomplete"
					indicator="indicator" minimumCharacters="3" parameters="tipo=ufrn,situacao=ativo"
					parser="new ResponseXmlToHtmlListParser()" />
					<span id="indicator" style="display:none; "> <img src="/sigaa/img/indicator.gif" /> </span>
				</td>
				</c:if>

				<c:if test="${(acesso.secretariaPosGraduacao || acesso.coordenadorCursoStricto)}">
				<td>
					<h:selectOneMenu id="orientador" value="#{orientacaoAcademica.equipe.id}" >
							<f:selectItem itemValue="0" itemLabel=">> SELECIONE UM ORIENTADOR"  />
							<f:selectItems value="#{orientacaoAcademica.docentesPrograma}"/>
					</h:selectOneMenu>
					<span class="required">&nbsp;</span>
					</td>
				</c:if>
			</tr>

		</tbody>
		<tfoot>
			<tr><td colspan="2">
				<h:commandButton value="Cancelar" action="#{ orientacaoAcademica.cancelar }" id="cancelOperation" onclick="#{confirm}"/>
				<h:commandButton value="Avançar >> " action="#{ orientacaoAcademica.selecionarOrientador }" id="btaoAvancar"/>
			</td></tr>
		</tfoot>
	</table>
	<br>
	<center><html:img page="/img/required.gif" style="vertical-align: top;" /> <span
		class="fontePequena"> Campos de preenchimento obrigatório. </span> <br>
	<br>
	</center>

	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>