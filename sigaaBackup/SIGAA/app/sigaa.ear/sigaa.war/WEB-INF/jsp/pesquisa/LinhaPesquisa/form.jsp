<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2 class="tituloPagina">
	<html:link action="/verMenuPesquisa">Pesquisa</html:link> &gt;
	Cadastro de Linhas de Pesquisa
</h2>

<html:form action="/pesquisa/cadastroLinhaPesquisa?dispatch=persist" method="post" focus="obj.codigo" styleId="form">
	<html:hidden property="obj.id" />

    <table class="formulario" width="60%">
	    <caption>Dados da Linha Pesquisa</caption>
     	<tbody>
        <c:if test="${not empty obj.grupoPesquisa}">
    	<tr>
    		<th class="required">Grupo de Pesquisa:</th>
    		<td colspan="2">
    			<html:hidden property="obj.grupoPesquisa.id" />
				 ${obj.grupoPesquisa.nome}
    		</td>
    	</tr>
    	</c:if>

        <tr>
            <th class="required">Nome:</th>
            <td colspan="2"> <html:text property="obj.nome" size="50" maxlength="120" /> </td>
		</tr>
		<tr>
            <th class="required">Ativa?</th>
            <td> <html:radio property="obj.inativa" value="false"/> Sim </td>
            <td> <html:radio property="obj.inativa" value="true"/> Não </td>
		</tr>
		</tbody>
		<tfoot>
			<tr><td colspan="3">
				<html:submit>Confirmar</html:submit>
		    	<input value="Cancelar" type="button" onclick="javascript:cancelar('form');"/>
	    	</td></tr>
		</tfoot>
	</table>
</html:form>

<c:if test="${ param['dispatch'] == 'remove' }">
<script type="text/javascript">
	new Remocao('form');
</script>
</c:if>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>