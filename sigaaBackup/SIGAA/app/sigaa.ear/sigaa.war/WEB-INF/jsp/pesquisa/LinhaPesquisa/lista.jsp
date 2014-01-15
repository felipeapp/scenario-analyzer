<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@page import="br.ufrn.sigaa.pesquisa.form.LinhaPesquisaForm"%>

<h2 class="tituloPagina">
	<html:link action="/verMenuPesquisa">Pesquisa</html:link> &gt;
	Linhas de Pesquisa
</h2>

<html:form action="/pesquisa/cadastroLinhaPesquisa?dispatch=list" method="post" focus="obj.nome" styleId="form">
<table class="formulario" width="70%">
<caption>Busca por Linhas de Pesquisa</caption>
<tbody>
	<tr>
		<td> <html:radio property="tipoBusca" value="<%=String.valueOf(LinhaPesquisaForm.LINHA_PESQUISA_NOME)%>" styleClass="noborder" /> </td>
    	<td> <label for="linhaPesquisa"> Nome </label> </td>
    	<td> <html:text property="obj.nome" size="50" onfocus="javascript:forms[0].tipoBusca[0].checked = true;" styleId="linhaPesquisa"/></td>
    </tr>
	<tr>
		<td> <html:radio property="tipoBusca" value="<%=String.valueOf(LinhaPesquisaForm.PROJETO_PESQUISA_NOME)%>" styleClass="noborder" /> </td>
    	<td> <label for="projetoPesquisa"> Projeto de Pesquisa </label> </td>
    	<td> <html:text property="projetoPesquisa.titulo" size="50" onfocus="javascript:forms[0].tipoBusca[1].checked = true;" styleId="projetoPesquisa"/></td>
    </tr>
	<tr>
		<td> <html:radio property="tipoBusca" value="<%=String.valueOf(LinhaPesquisaForm.GRUPO_PESQUISA_NOME)%>" styleClass="noborder"/> </td>
    	<td> <label for="grupoPesquisa"> Grupo de Pesquisa </label> </td>
    	<td> <html:text property="obj.grupoPesquisa.nome" size="50" onfocus="javascript:forms[0].tipoBusca[2].checked = true;" styleId="grupoPesquisa" /></td>
    </tr>        
    <tr>
    	<td> <html:radio property="tipoBusca" value="<%=String.valueOf(LinhaPesquisaForm.TODOS)%>" styleClass="noborder" styleId="todos"/> </td>
    	<td> <label for="todos">Todos</label> </td>
	</tr>
</tbody>
<tfoot>
	<tr>
		<td colspan="3">
			<html:hidden property="buscar" value="true"/>
			<html:submit><fmt:message key="botao.buscar" /></html:submit>
			<input value="Cancelar" type="button" onclick="javascript:cancelar('form');"/>
    	</td>
    </tr>
</tfoot>
</table>
</html:form>

<br/> <br />
	<ufrn:table 
		collection="${lista}" 
		properties="nome"
		headers="Nome" 
		title="Linhas de Pesquisa Cadastradas" 
		crud="true"> 
	</ufrn:table>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>