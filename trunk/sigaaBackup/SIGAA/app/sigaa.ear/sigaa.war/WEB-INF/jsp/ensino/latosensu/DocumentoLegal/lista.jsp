
<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2 class="tituloPagina">
	<html:link action="/verMenuLato">Ensino - Lato Sensu</html:link> &gt;
	Documentos Legais
</h2>

<html:form action="/ensino/latosensu/cadastroDocumentoLegal?dispatch=list" method="post">
<html:hidden property="buscar" value="true"/>
<table class="formulario" width="50%">
<caption>Busca por Documentos Legais</caption>
	<tbody>
	<tr>
	<td><html:radio property="tipoBusca" value="1" styleId="buscaNome" styleClass="noborder"/></td>
	<td><label for="buscaNome">Nome do Documento</label></td>
	<td><html:text property="obj.nomeDocumento" value="" size="30" onkeyup="CAPS(this)" onfocus="javascript:forms[0].tipoBusca[0].checked = true;"/></td>
	</tr>

	<tr>
	<td><html:radio property="tipoBusca" value="2" styleId="buscaNumero" styleClass="noborder"/></td>
	<td><label for="buscaNumero">Nº do Documento</label></td>
    <td><html:text property="obj.nroDocumento" value="" size="30" maxlength="20" onkeyup="CAPS(this)" onfocus="javascript:forms[0].tipoBusca[1].checked = true;"/></td>
	</tr>

    <tr>
    <td><html:radio property="tipoBusca" value="4" styleId="buscaTodos" styleClass="noborder"/></td>
    <td><label for="buscaTodos">Todos</label></td>
    </tr>
    </tbody>

    <tfoot>
	<tr>
	<td colspan="3"><html:submit><fmt:message key="botao.buscar" /></html:submit></td>
    </tr>
    </tfoot>
</table>
</html:form>
<br>
<ufrn:table collection="${lista}" properties="nomeDocumento,nroDocumento,curso.descricao"
headers="Nome, Número, Curso"
title="Documentos Legais Cadastrados" crud="true"/>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
