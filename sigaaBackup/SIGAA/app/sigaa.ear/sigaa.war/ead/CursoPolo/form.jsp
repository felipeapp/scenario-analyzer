<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>

<script type="text/javascript">
// seta UF escolhida no Hidden pro servlet poder receber o parametro
function preNaturUF() {
	$('naturUF').value = $F('polo:obj_unidadeFederativa');
}

// seleciona o Municipio ao carregar o form pra alteração
function posNaturUF() {
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

<f:view>
	<h2><ufrn:subSistema /> > Pólos de um Curso</h2>
	<br>
	<h:form id="polo">
	<h:messages showDetail="true"/>
	<h:inputHidden value="#{poloBean.confirmButton}"/>
	<h:inputHidden value="#{poloBean.obj.id}"/>

	<table class="formulario" width="100%">
	<caption class="listagem"> Cadastrar Pólos </caption>

	<tr>
		<th> Endereço: </th>
		<td> <h:inputText value="#{poloBean.obj.endereco}" id="endereco" size="50" readonly="#{poloBean.readOnly}"/>
		</td>
	</tr>
	<tr>
		<th> Telefone: </th>
		<td> <h:inputText value="#{poloBean.obj.telefone}" id="telefone" size="10" readonly="#{poloBean.readOnly}"/>
		</td>
	</tr>
	<tr>
		<th> CEP: </th>
		<td> <h:inputText value="#{poloBean.obj.cep}" id="cep" size="10" readonly="#{poloBean.readOnly}"/>
		</td>
	</tr>
	<tr>
		<th> Horário de Funcionamento: </th>
		<td> <h:inputTextarea value="#{poloBean.obj.horarioFuncionamento}" id="horario" rows="3" cols="50" readonly="#{poloBean.readOnly}"/>
		</td>
	</tr>
	<tr>
		<th>UF:</th>
		<td>
			<h:selectOneMenu id="obj_unidadeFederativa" value="#{poloBean.obj.unidadeFederativa.id}">
				<f:selectItems value="#{unidadeFederativa.allCombo}" />
			</h:selectOneMenu> <input type="hidden" id="naturUF" />
		</td>
	</tr>
	<tr>
		<th>Município:</th>
		<td>
			<select id="polo:obj_municipio" name="obj.cidade.id" />
			<ajax:select baseUrl="${applicationScope.contexto}/ajaxMunicipios"
					parameters="ufId={naturUF}" executeOnLoad="true"
					source="polo:obj_unidadeFederativa" target="polo:obj_municipio"
					preFunction="preNaturUF" postFunction="posNaturUF"/>
		</td>
	</tr>
	
	<tfoot>
		<tr>
			<td colspan="2">
				<h:commandButton value="#{poloBean.confirmButton}" action="#{poloBean.cadastrar}"/>
				<h:commandButton value="Cancelar" action="#{poloBean.cancelar}"/>
			</td>
		</tr>
	</tfoot>
	</h:form>

	</table>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
