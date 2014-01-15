<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h2 class="title"><ufrn:subSistema /> > Estrutura Curricular de Matrizes Curriculares &gt; Consulta de Componente Curricular</h2>
	<h:form id="buscaCC">
		<table class="formulario" width="90%">
			<caption>Busca por Componentes Curriculares</caption>
			<tbody>
				<tr>
					<td align="center"><h:selectBooleanCheckbox value="#{curriculo.filtroCodigoComponente}" styleClass="noborder" id="checkCodigo" /></td>
					<th style="text-align: left"><label for="buscaCC:checkCodigo" >Código:</label></th>
					<td><h:inputText size="10" value="#{curriculo.curriculoComponente.componente.codigo}" onfocus="$('buscaCC:checkCodigo').checked = true;" onkeyup="CAPS(this)"/></td>
				</tr>
				<tr>
					<td align="center"><h:selectBooleanCheckbox value="#{curriculo.filtroNome}" styleClass="noborder" id="checkNome" /></td>
					<th style="text-align: left"><label for="buscaCC:checkNome">Nome:</label></th>
					<td><h:inputText size="60" value="#{curriculo.curriculoComponente.componente.nome }" onfocus="$('buscaCC:checkNome').checked = true;" /></td>
				</tr>
				<tr>
					<td align="center"><h:selectBooleanCheckbox value="#{curriculo.filtroTipo}" styleClass="noborder" id="checkTipo" /></td>
					<th style="text-align: left"><label for="buscaCC:checkTipo">Tipo:</label></th>
					<td><h:selectOneMenu id="tipos"
						value="#{curriculo.curriculoComponente.componente.tipoComponente.id}"
						onfocus="$('buscaCC:checkTipo').checked = true;">
						<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
						<f:selectItems value="#{componenteCurricular.tiposComponentes}" />
					</h:selectOneMenu></td>
				</tr>
				<tr>
					<td align="center"><h:selectBooleanCheckbox value="#{curriculo.filtroUnidade}" styleClass="noborder" id="checkUnidade" /></td>
					<th style="text-align: left"><label for="buscaCC:checkUnidade">Unidade Acadêmica:</label></th>
					<td><h:selectOneMenu id="unidades" style="width: 400px"
						value="#{curriculo.curriculoComponente.componente.unidade.id}"
						onfocus="$('buscaCC:checkUnidade').checked = true;">
						<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
						<f:selectItems value="#{unidade.allDetentorasComponentesGraduacaoCombo}" />
					</h:selectOneMenu></td>
				</tr>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="3"><h:commandButton value="Buscar" action="#{curriculo.buscarComponentes}" id="btaoBuscaComponent"/>
					<h:commandButton value="<< Voltar" action="#{curriculo.telaComponentes}" id="btnVoltar"/></td>
				</tr>
			</tfoot>
		</table>

	<c:if test="${not empty curriculo.componentesEncontrados}">
		<br>
		<table class="listagem">
			<thead>
				<tr>
				<td style="font-size: xx-small; font-style: italic;">
				<input type="checkbox" onclick="marcarTodos(this)" value="obr" id="todosObr">
				<label for="todosObr">Todos Obr.</label>
				</td>
				<td style="font-size: xx-small; font-style: italic;">
				<input type="checkbox" onclick="marcarTodos(this)" value="opt" id="todosOpt">
				<label for="todosOpt">Todos Opt.</label>
				</td>
				<td>${fn:length(curriculo.componentesEncontrados) } Componente(s) Econtrado(s) </td>
				</tr>
			</thead>
			<tbody>
			<c:forEach items="${curriculo.componentesEncontrados }" var="cc" varStatus="status">
				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
					<td style="font-size: xx-small; font-style: italic;" width="12%">
					<input type="checkbox" name="obrigatorias" value="${cc.id }" onfocus="$('opt_'+this.value).checked=false" id="obr_${cc.id }">
					<label for="obr_${cc.id }">Obrigatória</label>
					</td>
					<td style="font-size: xx-small; font-style: italic;" width="12%">
					<input type="checkbox" name="optativas" value="${cc.id }" onfocus="$('obr_'+this.value).checked=false" id="opt_${cc.id }" >
					<label for="opt_${cc.id }">Optativa</label>
					</td>
					<td>
					${cc.descricao}
					</td>
				</tr>
			</c:forEach>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="3" align="center"><h:commandButton value="Selecionar Componentes Curriculares" style="width: 400px"
					action="#{curriculo.adicionarComponente}" id="selecionarCompCurric"/></td>
				</tr>
			</tfoot>
		</table>
	</c:if>
	</h:form>
</f:view>
<script type="text/javascript">

function marcarTodos(chk) {
	var tipo;
	var optativas = document.getElementsByName('optativas');
	var obrigatorias = document.getElementsByName('obrigatorias');
	for (i=0;i<optativas.length;i++) {
		if (chk.value == 'obr') {
			obrigatorias[i].checked = chk.checked;
			if (chk.checked) {
				optativas[i].checked = !chk.checked;
				$('todosOpt').checked = !chk.checked;
			}
		} else  {
			optativas[i].checked = chk.checked;
			if (chk.checked) {
				obrigatorias[i].checked = !chk.checked;
				$('todosObr').checked = !chk.checked;
			}
		}
	}
}

</script>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
