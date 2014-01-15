<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>
<f:view>

<script type="text/javascript">
// seta UF escolhida no Hidden pro servlet poder receber o parametro
function preNaturUF() {
	$('naturUF').value = $F('polo:obj_unidadeFederativa');
}

// seleciona o Municipio ao carregar o form pra alteração
function posNaturUF() {
	<c:set var="naturMunicipio" value="#{poloBean.obj.cidade.id}"/>
	<c:if test="${not empty naturMunicipio}">
	var items = $A($('polo:obj_municipio').options);
	items.each(
		function(item) {
			if (item.value == ${naturMunicipio}) {
				item.selected = true;
			}
		}
	);
	</c:if>
}
</script>


<h2><ufrn:subSistema /> &gt; Cadastro de Pólos</h2>

<h:form id="polo">	
<h:messages showDetail="true"/>
<h:inputHidden value="#{poloBean.confirmButton}"/>
<h:inputHidden value="#{poloBean.obj.id}"/>

		<table class="formulario" width="80%">
			<caption>Dados do Pólo</caption>
			
			<tr>
				<th class="obrigatorio">Unidade Federativa:</th>
				<td>
					<h:selectOneMenu value="#{poloBean.idUf}">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --"  />
						<f:selectItems value="#{unidadeFederativa.allCombo}" />
						<a4j:support event="onchange" reRender="municipio" onsubmit="true" />
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<th class="obrigatorio">Município:</th>
				<td>
					<h:selectOneMenu value="#{poloBean.idCidade}" id="municipio">
						<f:selectItem itemLabel="-- SELECIONE --" itemValue="0" />
						<f:selectItems value="#{poloBean.allMunicipiosUf}" />
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<th class="required">Endereço:</th>
				<td><h:inputText value="#{poloBean.obj.endereco}" id="endereco"
						size="50" readonly="#{poloBean.readOnly}" /></td>
			</tr>
			<tr>
				<th class="required">Telefone:</th>
				<td><h:inputText value="#{poloBean.obj.telefone}"
						onkeyup="return formatarInteiro(this);" id="telefone" size="12"
						maxlength="10" readonly="#{poloBean.readOnly}" /></td>
			</tr>
			<tr>
				<th class="required">CEP:</th>
				<td><h:inputText value="#{poloBean.obj.cep}"
						onkeyup="return formataCEP(this, event, null);" id="cep" size="10"
						maxlength="10" readonly="#{poloBean.readOnly}" /></td>
			</tr>
			<tr>
				<th valign="top" style="padding-top: 3px" class="required">
					Horário de Funcionamento:</th>
				<td><h:inputTextarea
						value="#{poloBean.obj.horarioFuncionamento}" id="horario" rows="3"
						cols="50" readonly="#{poloBean.readOnly}" /></td>
			</tr>
			<tr>
				<th class="required">Código das Turmas:</th>
				<td><h:inputText value="#{poloBean.obj.codigo}" id="codigo"
						size="10" maxlength="10" readonly="#{poloBean.readOnly}" /></td>
			</tr>
			<tr>
				<th>Observação:</th>
				<td><h:inputText value="#{poloBean.obj.observacao}"
						id="observacao" size="20" maxlength="20"
						readonly="#{poloBean.readOnly}" /></td>
			</tr>
			<tr>
				<th valign="top" style="padding-top: 3px" class="required">
					Cursos:</th>
				<td><t:dataTable var="cursoLoop" value="#{ poloBean.cursos }"
						width="100%" style="font-size: 0.9em;">
						<t:column width="4%">
							<h:selectBooleanCheckbox id="selecionaCurso"
								value="#{ cursoLoop.selecionado }" />
						</t:column>

						<t:column width="96%">
							<h:outputLabel for="selecionaCurso"
								value="#{ cursoLoop.descricaoNivelEnsino }" />
						</t:column>
					</t:dataTable></td>
			</tr>

			<tfoot>
				<tr>
					<td colspan="3"><h:commandButton
							value="#{poloBean.confirmButton}" action="#{poloBean.cadastrar}" />
						<h:commandButton value="Cancelar" action="#{poloBean.cancelar}"
							onclick="return confirm('Deseja cancelar a operação? Todos os dados digitados não salvos serão perdidos!');">
						</h:commandButton></td>
				</tr>
			</tfoot>
			</h:form>
		</table>
		<br>
<div align="center">
	<html:img page="/img/required.gif" style="vertical-align: top;" /> <span class="fontePequena"> Campos de preenchimento obrigatório. </span> 
</div>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
