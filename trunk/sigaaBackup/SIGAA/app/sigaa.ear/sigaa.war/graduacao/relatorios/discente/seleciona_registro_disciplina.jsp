<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<f:view>
<h:messages showDetail="true"></h:messages>
<h:outputText value="#{realtorioDiscente.create}"></h:outputText>
<h2><ufrn:subSistema /> > Relatório de Alunos com registro em um determinado Componente Curricular</h2>

<div class="descricaoOperacao">
	<p>Caro usuário,</p>
	<p>Para emissão do relatório, pelo menos um campo deve ser preenchido na busca.</p>
</div>

<h:form id="form">
<table align="center" class="formulario" width="100%">
	<caption class="listagem">Dados do Relatório</caption>
	<tr>
		<th>Componente Curricular: </th>
		<td><h:inputHidden id="idDisciplina" value="#{relatorioDiscente.disciplina.id}"></h:inputHidden>
				<h:inputText id="nomeDisciplina"
					value="#{relatorioDiscente.disciplina.detalhes.nome}" size="80" /> <ajax:autocomplete
					source="form:nomeDisciplina" target="form:idDisciplina"
					baseUrl="/sigaa/ajaxDisciplina" className="autocomplete"
					indicator="indicatorDisciplina" minimumCharacters="3" parameters="nivel=G"
					parser="new ResponseXmlToHtmlListParser()" /> <span id="indicatorDisciplina"
					style="display:none; "> <img src="/sigaa/img/indicator.gif" /> </span></td>

	</tr>
	<tr>
		<th>Discente: </th>
		<td><h:inputHidden id="idDiscente" value="#{relatorioDiscente.discente.id}"></h:inputHidden>
				<h:inputText id="nomeDiscente"
					value="#{relatorioDiscente.discente.pessoa.nome}" size="80" /> <ajax:autocomplete
					source="form:nomeDiscente" target="form:idDiscente"
					baseUrl="/sigaa/ajaxDiscente" className="autocomplete"
					indicator="indicatorDiscente" minimumCharacters="3" parameters="nivel=G"
					parser="new ResponseXmlToHtmlListParser()" /> <span id="indicatorDiscente"
					style="display:none; "> <img src="/sigaa/img/indicator.gif" /> </span></td>

	</tr>
	<tfoot>
		<tr>
			<td colspan="2" align="center">
							<h:commandButton id="gerarRelatorio" value="Gerar Relatório"
							action="#{relatorioDiscente.gerarRelatorioListaAlunoRegistroDisciplina}"/> <h:commandButton
							value="Cancelar" action="#{relatorioDiscente.cancelar}" id="cancelar" onclick="#{confirm}" immediate="true"/></td>
		</tr>
	</tfoot>

</table>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>