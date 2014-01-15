<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@page import="br.ufrn.sigaa.pesquisa.form.GrupoPesquisaForm"%>
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>

<h2 class="tituloPagina">
	<html:link action="/verMenuPesquisa">Pesquisa</html:link> &gt;
	Grupos de Pesquisa (Total: ${ count })
</h2>

<html:form action="/pesquisa/cadastroGrupoPesquisa?dispatch=list" method="post" focus="obj.nome" styleId="form">
<html:hidden property="buscar" value="true"/>

<table class="formulario" width="80%">
<caption>Busca por Grupos de Pesquisa</caption>
<tbody>
	<tr>
		<td> <html:radio property="tipoBusca" value="<%=String.valueOf(GrupoPesquisaForm.GRUPO_PESQUISA_NOME)%>" styleClass="noborder" /> </td>
    	<td> <label for="grupoPesquisa"> Nome do Grupo </label> </td>
    	<td> <html:text property="obj.nome" size="50" onfocus="javascript:document.formGrupoPesquisa.tipoBusca[0].checked = true;" styleId="grupoPesquisa"/></td>
    </tr>
	<tr>
		<td> <html:radio property="tipoBusca" value="<%=String.valueOf(GrupoPesquisaForm.AREA_DE_CONHECIMENTO)%>" styleClass="noborder" /> </td>
    	<td> <label for="areaConhecimento"> Área de Conhecimento </label> </td>
    	<td> <html:text property="obj.areaConhecimentoCnpq.nome" size="50" onfocus="javascript:document.formGrupoPesquisa.tipoBusca[1].checked = true;" styleId="areaConhecimento"/></td>
    </tr>
		<tr>
			<td> <html:radio property="tipoBusca" value="<%=String.valueOf(GrupoPesquisaForm.COORDENADOR)%>" styleClass="noborder" /> </td>
			<td>Coordenador:</td>
			<td>
				<c:set var="idAjax" value="obj.coordenador.id"/>
				<c:set var="nomeAjax" value="obj.coordenador.nome"/>
				<c:set var="obrigatorio" value="true"/>
				<c:set var="somenteInternos" value="true"/>
				<%@include file="/WEB-INF/jsp/include/ajax/docente.jsp" %>
			</td>
		</tr>
	<tr>
		<td> <html:radio property="tipoBusca" value="<%=String.valueOf(GrupoPesquisaForm.LINHA_PESQUISA_NOME)%>" styleClass="noborder"/> </td>
    	<td> <label for="linhaPesquisa"> Linha de Pesquisa </label> </td>
    	<td> <html:text property="linhaPesquisa.nome" size="50" onfocus="javascript:document.formGrupoPesquisa.tipoBusca[2].checked = true;" styleId="linhaPesquisa" /></td>
    </tr>
    <tr>
    	<td> <html:radio property="tipoBusca" value="<%=String.valueOf(GrupoPesquisaForm.TODOS)%>" styleClass="noborder" styleId="todos"/> </td>
    	<td> <label for="todos">Todos</label> </td>
	</tr>
</tbody>
<tfoot>
	<tr>
		<td colspan="3">
			<html:submit><fmt:message key="botao.buscar" /></html:submit>
			<input value="Cancelar" type="button" onclick="javascript:cancelar('form');"/>
    	</td>
    </tr>
</tfoot>
</table>
</html:form>

<br />
<c:if test="${not empty lista}">
	<ufrn:table collection="${lista}"
		title="Grupos de Pesquisa"
		properties="codigo, nome, coordenador.pessoa.nome, areaConhecimentoCnpq.nome"
		headers="Código, Nome, Coordenador, Área de Conhecimento" crud="false"
		links="src='${ctx}/img/alterar.gif',?id={id}&dispatch=edit, Alterar Grupo de Pesquisa;
			   src='${ctx}/img/delete.gif',?id={id}&dispatch=remove&desativar=true, Remover Grupo de Pesquisa;
			   src='${ctx}/img/pesquisa/view.gif',?id={id}&dispatch=view, Visualizar Grupo de Pesquisa"
		linksRoles="<%=new int[][] {
		   new int[] {SigaaPapeis.GESTOR_PESQUISA},
		   new int[] {SigaaPapeis.GESTOR_PESQUISA},
		   new int[] {SigaaPapeis.GESTOR_PESQUISA}}%>">
	</ufrn:table>
</c:if>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>