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


<c:if test="${acesso.coordenadorPolo}">
	<%@include file="/portais/cpolo/menu_cpolo.jsp" %>
</c:if>

<h2><ufrn:subSistema /> > Editar dados do Pólos</h2>

<h:form id="polo">	
<h:messages showDetail="true"/>
<h:inputHidden value="#{poloBean.confirmButton}"/>
<h:inputHidden value="#{poloBean.obj.id}"/>

<table class="formulario" width="80%">


<!-- CAMPOS OBRIGATORIOS -->
<caption>Dados do Pólo</caption>
<tr>
	<th class="required">UF:</th>
	<td>
		<h:selectOneMenu id="obj_unidadeFederativa" value="#{poloBean.obj.cidade.unidadeFederativa.id}">
			<f:selectItems value="#{unidadeFederativa.allCombo}" />
		</h:selectOneMenu> <input type="hidden" id="naturUF" />
	</td>
</tr>
<tr>
	<th class="required">Município:</th>
	<td>
		<select id="polo:obj_municipio" name="obj.cidade.id" />
		<ajax:select baseUrl="${applicationScope.contexto}/ajaxMunicipios"
				parameters="ufId={naturUF}" executeOnLoad="true"
				source="polo:obj_unidadeFederativa" target="polo:obj_municipio"
				preFunction="preNaturUF" postFunction="posNaturUF"/>
	</td>
</tr>
<tr>
	<th class="required"> Endereço: </th>
	<td> <h:inputText value="#{poloBean.obj.endereco}" id="endereco" size="50" readonly="#{poloBean.readOnly}"/></td>
</tr>
<tr>
	<th class="required"> Telefone: </th>
	<td> <h:inputText value="#{poloBean.obj.telefone}" id="telefone" size="10" maxlength="10" onkeyup="formatarInteiro(this)" /></td>
</tr>
<tr>
	<th class="required"> CEP: </th>
	<td> <h:inputText value="#{poloBean.obj.cep}" id="cep" size="10" maxlength="10" onkeyup="formatarInteiro(this)" /></td>
</tr>
<tr>
	<th class="required" valign="top" style="padding-top: 3px"> Horário de Funcionamento: </th>
	<td> <h:inputTextarea value="#{poloBean.obj.horarioFuncionamento}" id="horario" rows="3" cols="50" readonly="#{poloBean.readOnly}"/>	</td>
</tr>

<c:if test="${acesso.coordenadorPolo == true}">
	<tr>
		<th class="required"> Código das Turmas: </th>
		<td> <h:inputText value="#{poloBean.obj.codigo}" id="codigo" size="10" maxlength="10" disabled="true" /></td>
	</tr>
</c:if>

<c:if test="${acesso.coordenadorPolo == false}">
	<tr>
		<th class="required"> Código das Turmas: </th>
		<td> <h:inputText value="#{poloBean.obj.codigo}" id="codigo" size="10" maxlength="10" readonly="#{poloBean.readOnly}"/></td>
	</tr>
</c:if>

<tr>
	<th> Observação: </th>
	<td> <h:inputText value="#{poloBean.obj.observacao}" id="observacao" size="20" maxlength="20" readonly="#{poloBean.readOnly}"/></td>
</tr>

<c:if test="${acesso.coordenadorPolo == true}">
<tr>
	<th class="required" valign="top" style="padding-top: 3px"> Cursos: </th>
	<td> 
		<t:dataTable var="cursoLoop" value="#{ poloBean.cursos }" width="100%" style="font-size: 0.9em;">				
			<t:column width="3%">
				<h:selectBooleanCheckbox disabled="true" id="selecionaCurso" value="#{ cursoLoop.selecionado }"/>
				<h:inputHidden id="selecionaCurso2" value="#{cursoLoop.selecionado}"></h:inputHidden>
			</t:column>
			
			<t:column width="97%">
				<h:outputLabel for="selecionaCurso" value="#{ cursoLoop.nome }"/>
			</t:column>
		</t:dataTable>
	</td>
</tr>
</c:if>

<c:if test="${acesso.coordenadorPolo == false}">
<tr>
	<th class="required" valign="top" style="padding-top: 3px"> Cursos: </th>
	<td> 
		<t:dataTable var="cursoLoop" value="#{ poloBean.cursos }" width="100%" style="font-size: 0.9em;">				
			<t:column width="3%" >
				<h:selectBooleanCheckbox id="selecionaCurso" value="#{ cursoLoop.selecionado }"/>
			</t:column>
			
			<t:column width="97%">
				<h:outputLabel for="selecionaCurso" value="#{ cursoLoop.descricaoNivelEnsino }"/>
			</t:column>
		</t:dataTable>
	</td>
</tr>
</c:if>
<tfoot>
	<tr>
		<td colspan="3">
			<h:commandButton value="#{poloBean.confirmButton}" action="#{poloBean.cadastrar}"/>
			<h:commandButton value="<< Voltar" action="#{poloBean.voltar}"/>
			 <h:commandButton value="Cancelar" action="#{poloBean.cancelar}"
			 	 onclick="return confirm('Deseja cancelar a operação? Todos os dados digitados não salvos serão perdidos!');"> 
			  </h:commandButton>
		</td>
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