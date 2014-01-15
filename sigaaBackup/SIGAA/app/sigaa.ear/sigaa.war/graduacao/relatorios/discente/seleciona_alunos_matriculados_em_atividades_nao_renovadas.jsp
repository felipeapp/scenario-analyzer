<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<f:view>
<h:messages showDetail="true"></h:messages>
<h:outputText value="#{realtorioDiscente.create}"></h:outputText>
<h2><ufrn:subSistema /> > Alunos Matriculados em Atividades Não Renovadas</h2>

<h:form id="form">
<table align="center" class="formulario" width="75%">
	<caption>Dados do Relatório</caption>
	<tr>
		<th>Atividade: </th>
		<td>
			<input type="hidden" id="buscaAjaxDisciplina" >
			<table class="buscaAjax">
				<tr class="titulo">
				</tr>
				<tr>
					<td colspan="4">
						<h:inputText id="nomeDisciplina" value="#{relatorioDiscente.disciplina.detalhes.nome}" size="80" />
						<span id="indicatorDisciplina" style="display:none; ">
						<img src="/sigaa/img/indicator.gif" /></span>
					</td>
				</tr>
			</table>
			
			<ajax:autocomplete source="form:nomeDisciplina" target="form:idDisciplina"
				baseUrl="/sigaa/ajaxDisciplina" className="autocomplete" parameters="tipo={buscaAjaxDisciplina}&tipoComponente=A"
				indicator="indicatorDisciplina" minimumCharacters="5"
				parser="new ResponseXmlToHtmlListParser()" />
			
			<h:inputHidden id="idDisciplina" value="#{relatorioDiscente.disciplina.id}"></h:inputHidden>
			
			<script type="text/javascript">
				// Quem quiser usar, deve re-escrever no final da sua jsp
				function disciplinaOnFocus() {
				}
			
				function buscarDisciplinaPor(radio) {
					$('buscaAjaxDisciplina').value = $(radio).value;
					marcaCheckBox(radio);
					$('form:nomeDisciplina').focus();
				}
				buscarDisciplinaPor('buscaAjaxNomeDisciplina');
			</script>

		</td>
	</tr>
	
	<tr>
		<td align="right">Ano-Período Renovação:</td>
		<td>
			<h:selectOneMenu value="#{relatorioDiscente.ano}" id="ano">
				<f:selectItems value="#{relatorioDiscente.anos}" />
			</h:selectOneMenu>
			-
			<h:selectOneMenu value="#{relatorioDiscente.periodo}" id="periodo">
				<f:selectItems value="#{relatorioDiscente.periodos}" />
			</h:selectOneMenu>
		</td>
	</tr>
	<tfoot> 
	<tr>
		<td colspan="2" align="center">
			<h:commandButton id="gerarRelatorio" value="Gerar Relatório"
				action="#{relatorioDiscente.gerarRelatorioAlunosMatriculadosEmAtividadesNaoRenovadas}"/>
			<h:commandButton value="Cancelar" action="#{relatorioDiscente.cancelar}" id="cancelar" 
				onclick="return confirm('Deseja cancelar a operação? Todos os dados digitados não salvos serão perdidos!');"/>
		</td>
	</tr>
	</tfoot>  
</table>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
