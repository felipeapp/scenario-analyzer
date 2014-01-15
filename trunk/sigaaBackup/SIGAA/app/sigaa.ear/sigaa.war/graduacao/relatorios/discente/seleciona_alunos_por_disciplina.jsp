<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<f:view>
<h2><ufrn:subSistema/> > Lista de Alunos Por Disciplina</h2>

	<h:form id="form">
		<table align="center" class="formulario" width="80%">
			<caption class="listagem">Dados do Relatório</caption>
			
			<tr>
				<th class="required" width="10%">Ano-Período:</th>
				<td>
					<h:inputText value="#{relatorioAlunosEmDisciplinas.ano}" size="4" maxlength="4" onkeyup="return formatarInteiro(this)" /> - <h:inputText value="#{relatorioAlunosEmDisciplinas.periodo}" size="2" maxlength="2" onkeyup="return formatarInteiro(this)" />
				</td>
			</tr>
			
			<tr>
				<th class="required">Disciplina:</th>
				<td>
					<h:inputHidden id="idDisciplina" value="#{relatorioAlunosEmDisciplinas.disciplina.id}"></h:inputHidden>
					<h:inputText id="nomeDisciplina" value="#{relatorioAlunosEmDisciplinas.disciplina.nome}" style="width: 90%" />
						<ajax:autocomplete
						source="form:nomeDisciplina" target="form:idDisciplina"
						baseUrl="/sigaa/ajaxDisciplina" className="autocomplete"
						indicator="indicatorDisciplina" minimumCharacters="6"
						parameters="todosOsProgramas=true"
						parser="new ResponseXmlToHtmlListParser()" />
						<span id="indicatorDisciplina" style="display:none; ">
						<img src="/sigaa/img/indicator.gif" /> </span>		
				</td>
			</tr>
			<tr>
				<td colspan="2" align="center">
					<h:commandButton id="gerarRelatorio" value="Gerar Relatório"
					action="#{relatorioAlunosEmDisciplinas.gerarRelatorio}"/>
					<h:commandButton
					value="Cancelar" action="#{relatorioAlunosEmDisciplinas.cancelar}" id="cancelar" />
				</td>
			</tr>
		</table>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>