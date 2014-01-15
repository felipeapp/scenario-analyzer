<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<script type="text/javascript">
<!--
function marcaJSFChkBox(elem) {
	elem.checked = true;
}
//-->
</script>
<f:view>
	<h:messages showDetail="true" />
	<h2><ufrn:subSistema /> > Relatório de Ocupação de Turma</h2>
	<br>

	<h:outputText value="#{componenteCurricular.create}" />
	<h:form id="formBusca">
		<table class="formulario" width="75%">
			<caption>Busca de Componentes Curriculares</caption>
			<tr>
<%-- 				<td></td> --%>
				<td>Ano-Semestre:</td>
				<td> 
					<h:inputText value="#{relatorioOcupacaoTurma.ano}" size="4"/>
					-
					<h:inputText value="#{relatorioOcupacaoTurma.periodo}" size="1"/> 
				<td>
			</tr>
<%-- 			<tr>
				<td><input type="radio" id="checkCodigo" name="paramBusca" value="codigo" class="noborder"></td>
				<td><label for="checkCodigo">Código</label></td>
				<td><h:inputText value="#{relatorioOcupacaoTurma.componente.detalhes.codigo}" size="10" onfocus="marcaCheckBox('checkCodigo')" />
				</td>
			</tr>
			<tr>
				<td><input type="radio" id="checkNome" name="paramBusca" value="nome" class="noborder"></td>
--%>				
			<tr>
				<td><label for="checkNome">Nome do Componente:</label></td>
				<td>
				
				<h:inputHidden value="#{relatorioOcupacaoTurma.componente.id}" id="idDisciplina"/>
				<h:inputText id="nomeDisciplina" value="#{relatorioOcupacaoTurma.componente.detalhes.nome}" size="60" onfocus="marcaCheckBox('checkNome')" />
				
				<ajax:autocomplete source="formBusca:nomeDisciplina" target="formBusca:idDisciplina"
					baseUrl="/sigaa/ajaxDisciplina" className="autocomplete"
					indicator="indicatorDisciplina" minimumCharacters="3" parameters="nivel=G"
					parser="new ResponseXmlToHtmlListParser()" />
					
				<span id="indicatorDisciplina" style="display:none; "> <img src="/sigaa/img/indicator.gif" /> </span>
				
				</td>
			</tr>
<%--  			<tr>
				<td><input type="radio" id="checkDepartamento" name="paramBusca" value="Departamento"
					class="noborder"></td>
				<td><label for="checkDepartamento">Departamento</label></td>
				<td><h:selectOneMenu id="unidades" style="width: 400px" onfocus="marcaCheckBox('checkDepartamento')"
					value="#{componenteCurricular.obj.unidade.id}">
					<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
					<f:selectItems value="#{unidade.allDepartamentoCombo}" />
				</h:selectOneMenu></td>
			</tr>
--%>			
			<tfoot>
				<tr>
					<td colspan="3"><h:commandButton action="#{relatorioOcupacaoTurma.gerarRelatorio}" value="Gerar Relatório" /> <h:commandButton
						value="Cancelar" onclick="#{confirm}" action="#{cursoGrad.cancelar}" /></td>
				</tr>
			</tfoot>
		</table>
	</h:form>


</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
