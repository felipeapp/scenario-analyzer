<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
 
<%@ taglib uri="/tags/struts-html" prefix="html"  %> 
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"  %> 

<br/>
<h2>
	<html:link action="/verMenuEnsino?tipoEnsino=${tipoEnsino}">
		Ensino <fmt:message key="label.ensino.${tipoEnsino}" />
	</html:link>
	<fmt:message key="titulo.listar">
		<fmt:param value="Unidade Federativa"/>
	</fmt:message>
</h2>
<hr>
<%@include file="/WEB-INF/jsp/include/mensagem.jsp"%>

<html:form action="/geral/listarUnidadesFederativas" method="post" focus="nome">
	<div class="areaDeDados">
		<h2>Busca por Unidades Federativas</h2>
		<div class="dados">
			<div class="head"><input type="radio" name="tipoBusca" value="1" class="noborder">Nome</input></div>
			<div class="texto"><html:text property="nome" size="30" value="" onfocus="javascript:forms[0].tipoBusca[0].checked = true;"></html:text></div>
			<br/>
			
			<div class="head"><input type="radio" name="tipoBusca" value="2" class="noborder">Sigla</input></div>
			<div class="texto"><html:text property="sigla" size="5" value="" onfocus="javascript:forms[0].tipoBusca[1].checked = true;"></html:text></div>
			<br/>
			
			<div class="head"><input type="radio" name="tipoBusca" value="3" class="noborder">Todos</input></div>
			<br/>
			
			<div class="botoes">
				<html:submit><fmt:message key="botao.buscar" /></html:submit>
			</div>
		</div>	<!-- fim do div dados -->
	</div>	<!-- fim do div areaDeDados -->
</html:form>

<br>
<div class="infoAltRem">
	<html:img page="/img/alterar.gif" style="overflow: visible;"/>
	: Alterar dados da Unidade Federativa
	<html:img page="/img/delete.gif" style="overflow: visible;"/>
	: Remover Unidade Federativa
</div>
<br>

<div class="areaDeDados lista">
	<h2>Unidades Federativas Cadastradas</h2>
	<table>
	<thead>
	<th> Nome </th>
	<th> Sigla </th>

	<th colspan="2">&nbsp;</th>

	<tbody>

	<c:forEach items="${unidadesFederativas}" var="unidadeFederativa">
			<tr>
				<html:form action="/geral/detalharUnidadeFederativa" method="post">
					<html:hidden name="unidadeFederativa" property="id"/>
					<html:hidden property="acao" value="alterar"/>
					<td nowrap="nowrap"> ${unidadeFederativa.nome} </td>
					<td > ${unidadeFederativa.sigla} </td>
					<td width="15">
						<html:image style="border: none" page="/img/alterar.gif" alt="Alterar dados da Unidade Federativa" title="Alterar dados da Unidade Federativa" value="Alterar"></html:image>
					</td>
				</html:form>
				<html:form action="/geral/detalharUnidadeFederativa" method="post">
					<html:hidden name="unidadeFederativa" property="id"/>
					<html:hidden property="acao" value="remover"/>
					<td width="15">
						<html:image style="border: none" page="/img/delete.gif" alt="Remover Unidade Federativa" title="Remover Unidade Federativa" value="Remover"></html:image>
					</td>
				</html:form>
			</tr>
		</c:forEach>  
	</table>
</div> <!-- fim do div areaDeDados lista -->

<br><br>
<center>
	<html:link action="/verMenuEnsino?tipoEnsino=${tipoEnsino}">
		Menu do Ensino <fmt:message key="label.ensino.${tipoEnsino}" />
	</html:link>
</center>
