<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>

<h2 class="tituloPagina">
	<ufrn:subSistema />
&gt; Pessoas Jurídicas </h2>

<html:form action="/pessoa/wizard.do?dispatch=list">
<html:hidden property="buscar" value="true"/>
	<table class="formulario" width="80%">
		<caption>Busca de Pessoas Jurídicas</caption>
		<tbody>
       	<tr>
       		<td>
       			<html:radio property="tipoBusca" styleId="checkCNPJ" value="cnpj" styleClass="noborder" />
       		</td>
       		<th><label for="checkCNPJ">CNPJ:</label></th>
       		<td>
       		<html:text property="cpf_cnpj" maxlength="25" size="25" onkeypress="formataCpfCnpj(this, event, null)"
       			onfocus="marcaCheckBox('checkCNPJ')"/>
			</td>
       	</tr>
       	<tr>
       		<td>
       			<html:radio property="tipoBusca" styleId="checkNome" value="nome" styleClass="noborder" />
       		</td>
       		<th><label for="checkNome"> Nome Fantasia: </label></th>
       		<td>
			<html:text property="pessoa.nome" size="60" onkeyup="CAPS(this)"
       			onfocus="marcaCheckBox('checkNome')"/>
            </td>
       	</tr>
       	<tr>
       		<td>
       			<html:radio property="tipoBusca" styleId="checkTodos" value="todos" styleClass="noborder" />
       		</td>
       		<th><label for="checkTodos">Todos</label></th>
       		<td></td>
       	</tr>
       	<tfoot>
       	<tr>
       		<td colspan="3" align="center">
       			<html:submit>Buscar</html:submit>
       		</td>
       	</tr>
        </tfoot>
    </table>
</html:form>
<br>
<ufrn:table collection="${lista}"
properties="pessoa.cpfCnpjFormatado, pessoa.nome"
headers="CNPJ, Nome Fantasia"
title="Pessoas Jurídicas Cadastradas" crud="false"
links="src='${ctx}/img/alterar.gif',?pessoaId={id}&dispatch=edit&tipoPessoa=J;
		src='${ctx}/img/delete.gif',?id={id}&dispatch=remove;"
crudRoles="<%=new int[] {SigaaPapeis.GESTOR_LATO}%>" />

<br><br>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>