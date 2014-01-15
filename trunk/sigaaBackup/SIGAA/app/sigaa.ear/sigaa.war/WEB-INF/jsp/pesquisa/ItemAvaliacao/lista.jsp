<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@page import="br.ufrn.sigaa.pesquisa.form.ItemAvaliacaoForm"%>

<h2 class="tituloPagina">
	<html:link action="/verMenuPesquisa">Pesquisa</html:link> &gt;
	Itens de Avaliação
</h2>

<html:form action="/pesquisa/cadastroItemAvaliacao?dispatch=list" method="post" focus="obj.descricao">
<table class="formulario" width="70%">
<caption>Busca por Itens de Avaliação</caption>
<tbody>
	<tr>
		<td> <html:radio property="tipoBusca" value="<%=String.valueOf(ItemAvaliacaoForm.DESCRICAO)%>" styleClass="noborder" /> </td>
    	<td> <label for="descricao"> Descrição </label> </td>
    	<td> <html:text property="obj.descricao" size="50" onfocus="javascript:forms[0].tipoBusca[0].checked = true;" styleId="descricao"/></td>
    </tr>
    <tr>
    	<td> <html:radio property="tipoBusca" value="<%=String.valueOf(ItemAvaliacaoForm.TODOS)%>" styleClass="noborder" styleId="todos"/> </td>
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
	<div class="infoAltRem">
	    <html:img page="/img/alterar.gif" style="overflow: visible;"/>
	    : Alterar dados do Item de Avaliação
	    <html:img page="/img/delete.gif" style="overflow: visible;"/>
	    : Remover Item de Avaliação
	</div>
	<br>
	
	<table class="listagem">
		<caption>Itens de Avaliação Cadastrados</caption>
		<thead>
			<tr> 
	        	<th width="75%">Descrição</th>
	        	<th> Tipo </th>
	        	<th> Peso </th>
	        	<th width="20%">Data de Criação</th>	        	
	        	<th colspan="2">&nbsp;</th>
			</tr>
		</thead>
		<tbody>
	    <c:forEach items="${lista}" var="itemAvaliacao" varStatus="status">
			<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
				<td> ${itemAvaliacao.descricao} </td>
				<td> ${itemAvaliacao.tipo == 1 ? 'Projeto' : 'Resumo'} </td>
				<td align="right"> ${itemAvaliacao.peso} </td>
				<td align="center"> <ufrn:format name="itemAvaliacao" property="dataCriacao" type="data" /> </td>				
				<td width="15">
					<html:link action="/pesquisa/cadastroItemAvaliacao?dispatch=edit&id=${itemAvaliacao.id}">
						<img src="<%= request.getContextPath() %>/img/alterar.gif" alt="Alterar dados do Item de Avaliação" title="Alterar dados do Item de Avaliação" border="0"/>
					</html:link>
				</td>
				<td width="15">
                   	<html:link action="/pesquisa/cadastroItemAvaliacao?dispatch=remove&id=${itemAvaliacao.id}">
                       <img src="<%= request.getContextPath() %>/img/delete.gif" alt="Remover Item de Avaliação" title="Remover Item de Avaliação" border="0"/>
					</html:link>
				</td>				
			</tr>
		</c:forEach>
		</tbody>
		<tfoot>
			<tr>
				<td colspan="6" align="center"> <strong> ${fn:length(lista)} itens cadastrados </strong></td>
			</tr>
		</tfoot>
		  
	</table>
</c:if>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>