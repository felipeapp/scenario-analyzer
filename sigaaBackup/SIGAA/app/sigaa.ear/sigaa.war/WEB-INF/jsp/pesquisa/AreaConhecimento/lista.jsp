<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<%@page import="br.ufrn.sigaa.dominio.AreaConhecimentoCnpq"%>
<%@page import="br.ufrn.sigaa.pesquisa.form.AreaConhecimentoForm"%>
<h2>
	<ufrn:subSistema /> &gt; �reas de Pesquisa
</h2>

<div class="descricaoOperacao">
	<p style="text-align: center">
		Para maiores informa��es referentes �s �reas de Conhecimento cadastradas, consulte a p�gina disponibilizada
		pelo CNPq: <a href="http://www.cnpq.br/areasconhecimento/" target="_blank"> http://www.cnpq.br/areasconhecimento/ </a>
	</p>
</div>

<html:form action="/pesquisa/cadastroAreaConhecimento?dispatch=list" method="post">
<table class="formulario" width="70%">
<caption>Buscar �reas de Conhecimento</caption>
<tbody>
	<tr>
		<td> <html:radio property="tipoBusca" value="<%=String.valueOf(AreaConhecimentoForm.GRANDE_AREA)%>" styleClass="noborder" /> </td>
		<td> <label for="grandeArea"> Grande �rea </label> </td>
		<td>
			<html:select property="obj.grandeArea.id" styleId="grandeArea" style="width: 95%" onchange="javascript:document.formAreaConhecimento.tipoBusca[0].checked = true;">
	        <html:option value=""> --- SELECIONE UMA �REA GRANDE �REA DE CONHECIMENTO --- </html:option>
	        <html:options collection="grandesAreas" property="id" labelProperty="nome" />
	        </html:select>
		</td>
	</tr>

	<tr>
		<td> <html:radio property="tipoBusca" value="<%=String.valueOf(AreaConhecimentoForm.NOME)%>" styleClass="noborder" /> </td>
    	<td> <label for="nomeArea"> Nome </label> </td>
    	<td> <html:text property="obj.nome" size="50" onfocus="javascript:document.formAreaConhecimento.tipoBusca[1].checked = true;" styleId="nomeArea"/></td>
	</tr>
    <tr>
    	<td> <html:radio property="tipoBusca" value="<%=String.valueOf(AreaConhecimentoForm.TODOS)%>" styleClass="noborder" styleId="todos"/> </td>
    	<td> <label for="todos">Todos</label> </td>
	</tr>
</tbody>
<tfoot>
	<tr>
		<td colspan="3">
			<html:hidden property="buscar" value="true"/>
			<html:submit><fmt:message key="botao.buscar" /></html:submit>
    	</td>
    </tr>
</tfoot>
</table>
</html:form>

<c:if test="${not empty lista}">
<br/>
	<ufrn:table collection="${lista}" properties="id, grandeArea.nome, area.nome, subarea.nome, especialidade.nome, nome " headers="C�digo, Grande �rea, �rea, Sub-�rea, Especialidade, Nome "
	title="�reas de Conhecimento"/>
</c:if>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>