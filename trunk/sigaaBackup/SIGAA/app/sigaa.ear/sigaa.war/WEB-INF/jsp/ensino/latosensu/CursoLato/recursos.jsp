<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2 class="tituloPagina">
	<ufrn:steps/>
</h2>

<input type="hidden" id="linkAjuda" value="/manuais/lato/Proposta/3_recursosfisicos_materiais.htm">

<html:form action="/ensino/latosensu/criarCurso" method="post" onsubmit="return validateLatoRecursosCursoForm(this);" >
<table class="formulario" width="100%">
<caption>Recursos Físicos e Materiais</caption>
	<tr>
		<th>Instalações:</th>
		<td align="left">
			<html:textarea property="proposta.recursoInstalacao" cols="100" rows="10"/>
		</td>
	</tr>
	<tr>
		<th>Biblioteca:</th>
		<td align="left">
		<html:textarea property="proposta.recursoBiblioteca" cols="100" rows="10"/>
		</td>
	</tr>
	<tr>
		<th>Recursos de Informática: </th>
		<td align="left">
		<html:textarea property="proposta.recursoInformatica" cols="100" rows="10"/>
		</td>
	</tr>
	<tr>
		<th>Reprografia:</th>
		<td align="left">
		<html:textarea property="proposta.recursoReprografia" cols="100" rows="10"/>
		</td>
	</tr>
	<tfoot>
	<tr>
		<td colspan="4">
			<html:button dispatch="gravar" value="Gravar"/>
			<html:button view="selecao" value="<< Voltar" />
			<html:button dispatch="cancelar" value="Cancelar" cancelar="true"/>
			<html:button dispatch="docentes" value="Avançar >>"/>
		</td>
	</tr>
	</tfoot>
</table>
</html:form>

<center>
<br>
<html:img page="/img/required.gif" style="vertical-align: top;"/> <span class="fontePequena"> Campos de preenchimento obrigatório. </span>
<br>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>