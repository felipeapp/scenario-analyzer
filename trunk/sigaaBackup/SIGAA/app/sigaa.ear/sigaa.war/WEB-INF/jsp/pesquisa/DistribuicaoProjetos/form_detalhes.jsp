<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<%@page import="br.ufrn.sigaa.pesquisa.form.DistribuicaoProjetosForm"%>
<h2 class="tituloPagina">
	<html:link action="/verMenuPesquisa">Pesquisa</html:link> &gt;
	Consulta de Avaliações Pendentes
</h2>

<html:form action="/pesquisa/distribuirProjetoPesquisa" method="post" focus="codigo.numero">
<table class="formulario" width="95%">
	<caption> Critérios de Consulta </caption>
	<tr>
		<td> <html:checkbox property="filtros" styleId="consultorCheck" value="<%="" + DistribuicaoProjetosForm.CONSULTOR%>" styleClass="noborder"/> </td>
		<td><label for="consultorCheck"> Consultor:</label> </td>
		<td>
			<c:set var="idAjax" value="consultor.id"/>
			<c:set var="nomeAjax" value="consultor.nome"/>
			<%@include file="/WEB-INF/jsp/include/ajax/consultor.jsp" %>
		</td>
	</tr>
	<tr>
		<td> <html:checkbox property="filtros" styleId="centroCheck" value="<%="" + DistribuicaoProjetosForm.CENTRO%>" styleClass="noborder"/> </td>
		<td> <label for="centroCheck">Centro:</label> </td>
		<td>
			<html:select property="centro.id" style="width: 450px;" styleId="centro" onchange="$('centroCheck').checked = true;">
				<html:options collection="centros" property="unidade.id" labelProperty="unidade.codigoNome" />
			</html:select>
		</td>
	</tr>
	<tr>
		<td> <html:checkbox property="filtros" styleId="tipoCheck" value="<%="" + DistribuicaoProjetosForm.TIPO_DISTRIBUICAO%>" styleClass="noborder"/> </td>
		<td> <label for="tipoCheck">Tipo da Distribuição:</label> </td>
		<td>
			<html:select property="tipoDistribuicao" style="width: 200px;" styleId="tipoDistribuicao" onchange="$('tipoCheck').checked = true;">>
				<html:options collection="tipos" property="key" labelProperty="value" />
			</html:select>
		</td>
	</tr>
	<tr>
		<td> <html:checkbox property="filtros" styleId="editalCheck" value="<%="" + DistribuicaoProjetosForm.EDITAL%>" styleClass="noborder"/> </td>
		<td> <label for="editalCheck">Edital:</label> </td>
		<td>
			<html:select property="idEdital" style="width: 450px;" styleId="editalPesquisa" onchange="$('editalCheck').checked = true;">>
				<html:options collection="editaisPesquisa" property="id" labelProperty="descricao" />
			</html:select>
		</td>
	</tr>
	<tfoot>
		<tr>
			<td colspan="3">
				<html:button dispatch="resultadoDistribuicao" value="Consultar"/>
				<html:button dispatch="cancelar" value="Cancelar"/>
	    	</td>
	    </tr>
	</tfoot>
</table>
</html:form>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>