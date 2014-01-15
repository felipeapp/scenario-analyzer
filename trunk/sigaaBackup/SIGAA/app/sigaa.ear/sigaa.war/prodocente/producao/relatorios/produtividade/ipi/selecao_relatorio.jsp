<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> &gt; Cálculo do Índice de Produção dos Docentes </h2>
	<h:outputText value="#{classificacaoRelatorio.create}"/>
	<h:form id="form">
		<table class="formulario" width="90%">
			<caption>Defina os Critérios do Cálculo</caption>
			<tr>
				<th class="obrigatorio" style="width: 15%">Ano de Referência:</th>
				<td>
					<h:inputText value="#{classificacaoRelatorio.obj.anoVigencia}" size="4" onkeyup="formatarInteiro(this)" maxlength="4"/>
				</td>
			</tr>
			<tr>
				<th>Relatório:</th>
				<td>
					<h:selectOneMenu id="idRelatorio" value="#{classificacaoRelatorio.obj.relatorioProdutividade.id}">
						<f:selectItems value="#{relatorioAtividades.allCombo}" />
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<th class="obrigatorio">Filtro de Docentes:</th>
				<td>
					<h:selectOneMenu id="filtro" value="#{classificacaoRelatorio.tipoFiltro.id}" >
						<f:selectItem itemValue="0" itemLabel=" >> Selecione " />
						<f:selectItems value="#{tipoFiltroDocentesMBean.allAtivoCombo}" />
					</h:selectOneMenu>
				</td>
			</tr>
 			<tr>
				<th class="obrigatorio">Filtro de Editais:</th>
				<td>
					<h:selectManyCheckbox id="editaisChckBox" value="#{classificacaoRelatorio.editaisSelecionados}" layout="pageDirection">
						<f:selectItems value="#{classificacaoRelatorio.editaisCombo}" />
					</h:selectManyCheckbox>
				</td>
			</tr>
			<tr>
				<th>Observações:</th>
				<td>
					<h:inputTextarea value="#{classificacaoRelatorio.obj.finalidade}" rows="3" cols="80" id="observacoes" validatorMessage="O campo observação deve ter no máximo 500 caracteres.">
						<f:validateLength maximum="500"/>
					</h:inputTextarea>
				</td>
			</tr>
			<tfoot>
			<tr>
				<td colspan="2">
					<h:commandButton id="gerarRelatorio" value="Calcular Índice" action="#{classificacaoRelatorio.gerarClassificacao}" />
					<h:commandButton id="cancelar" value="Cancelar" action="#{classificacaoRelatorio.cancelar}" onclick="#{confirm}"/>
				</td>
			</tr>
			</tfoot>
		</table>
	</h:form>
	<div class="obrigatorio"> Campos de preenchimento obrigatório. </div>
	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
