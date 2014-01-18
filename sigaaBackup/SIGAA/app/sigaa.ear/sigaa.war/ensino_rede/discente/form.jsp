<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>

<script type="text/javascript">
function limitText(limitField, limitCount, limitNum) {
	count = limitField.value.replace(/\n/g, "\n\r").length; 
	if (count > limitNum) {
		$(limitCount).innerHTML = 'Você excedeu ' + (count - limitNum) + ' caracter(es) além do limite permitido.';
	} else  {
		$(limitCount).innerHTML = 'Você pode digitar '+ (limitNum - count) +' caracter(es).';
	}
}
</script>

<f:view>
	<h2><ufrn:subSistema /> >  Cadastro de Discente Associado </h2>

	<h:form id="formcadastroDiscenteRedeMBean" enctype="multipart/form-data">
	<h:inputHidden value="#{cadastroDiscenteRedeMBean.obj.id}"/>
	<table class="formulario" style="width: 100%;">
		<caption>Informe os Dados do Discente</caption>
		<tbody>
			<tr>
				<th width="18%" class="rotulo"> CPF: </th>
				<td> <ufrn:format type="cpf" valor="${cadastroDiscenteRedeMBean.obj.pessoa.cpf_cnpj}" /></td>
			</tr>
			<tr>
				<th width="18%" class="rotulo"> Nome: </th>
				<td> ${cadastroDiscenteRedeMBean.obj.pessoa.nome}</td>
			</tr>
			<tr>
				<th class="required"> Status:</th>
				<td>
					<h:selectOneMenu value="#{cadastroDiscenteRedeMBean.obj.status.id}" id="status" >
						<f:selectItem itemLabel="-- SELECIONE --" itemValue="0"  />
						<f:selectItems value="#{cadastroDiscenteRedeMBean.statusCombo}" />
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<th class="required"> Ano-Período Inicial: </th>
				<td>
					<h:inputText id="ano" size="4" maxlength="4" value="#{ cadastroDiscenteRedeMBean.obj.anoIngresso }" /> -
					<h:inputText id="periodo" size="1" maxlength="1" value="#{ cadastroDiscenteRedeMBean.obj.periodoIngresso }"  />
				</td>
			</tr>
			<tr>
				<td colspan="2" class="subFormulario"><span class="required">Dados do Curso</span></td>
			</tr>
			<tr>
				<th class="required"> Programa:</th>
				<td>
					<h:selectOneMenu value="#{cadastroDiscenteRedeMBean.dadosCursoRede.programaRede.id}" id="programa">
						<f:selectItem itemLabel="-- SELECIONE --" itemValue="0"  />
						<f:selectItems value="#{programaRedeMBean.allCombo}" />
						<a4j:support event="onchange" reRender="curso, campus"
							action="#{cadastroDiscenteRedeMBean.programaListener}" />
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<th class="required"> Curso:</th>
				
				<td>
					<h:selectOneMenu value="#{cadastroDiscenteRedeMBean.dadosCursoRede.curso.id}" id="curso">
						<f:selectItem itemLabel="-- SELECIONE --" itemValue="0"  />
						<f:selectItems value="#{cadastroDiscenteRedeMBean.cursosFromProgramaRedeCombo}" />
						<a4j:support event="onchange" reRender="campus, nivelEnsino"
							action="#{cadastroDiscenteRedeMBean.cursoListener}" />
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<th class="rotulo"> Nível de Ensino:</th>
				<td>
					<h:outputText value="#{ cadastroDiscenteRedeMBean.obj.nivelDesc }" id="nivelEnsino"/>
				</td>
			</tr>
			<tr>
				<th class="required"> Campus:</th>
				<td>
					<h:selectOneMenu value="#{cadastroDiscenteRedeMBean.dadosCursoRede.campus.id}" id="campus">
						<f:selectItem itemLabel="-- SELECIONE --" itemValue="0"  />
						<f:selectItems value="#{cadastroDiscenteRedeMBean.campusFromCursoProgramaRedeCombo}" />
						<a4j:support event="onchange" action="#{cadastroDiscenteRedeMBean.campusListener}" />
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<td colspan="2" class="subFormulario">Observações</td>
			</tr>
			<tr>
				<th valign="top"> Observações: </th>
				<td>
					<h:inputTextarea value="#{cadastroDiscenteRedeMBean.obj.observacao}" rows="5" cols="100" id="observacao" 
						onkeydown="limitText(this, observacaoCount, 1000);" onkeyup="limitText(this, observacaoCount, 1000);"/>
					<br/>
					<span id="observacaoCount">${1000 - fn:length(cadastroDiscenteRedeMBean.obj.observacao)}</span>
				</td>
			</tr>
		</tbody>
		<tfoot>
			<tr><td colspan="2">
				<h:commandButton value="#{ cadastroDiscenteRedeMBean.confirmButton }" action="#{ cadastroDiscenteRedeMBean.cadastrar }" id="btnCadastrar"/>
				<h:commandButton value="<< Voltar" action="#{ dadosPessoais.voltarDadosPessoais }" id="btnVoltar"/>
				<h:commandButton value="Cancelar" action="#{ cadastroDiscenteRedeMBean.cancelar }" onclick="#{confirm}" id="btnCancelar"/>
			</td></tr>
		</tfoot>
	</table>

	</h:form>
</f:view>

<script>
limitText($('formcadastroDiscenteRedeMBean:observacao'), 'observacaoCount', 1000);
</script>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>