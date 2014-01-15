<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2 class="tituloPagina">
	<html:link action="/verMenuPesquisa">Pesquisa</html:link> &gt;
	Cadastro de Itens de Avaliação
</h2>

<html:form action="/pesquisa/cadastroItemAvaliacao?dispatch=persist" method="post" focus="obj.descricao"  styleId="form">
	<html:hidden property="obj.id" />
	<html:hidden property="dataCadastro" />
	<table class="formulario" width="75%">
        <caption>Dados do Item de Avaliação</caption>
        <tbody>
        <tr>
            <th class="required">Descrição:</th>
            <td>
                <html:text property="obj.descricao" size="60" maxlength="300" />
            </td>
        </tr>
        <tr>
            <th class="required">Tipo:</th>
            <td>
                <html:select property="obj.tipo">
                	<html:option value="1">Avaliação de Projeto</html:option>
                	<html:option value="2">Avaliação de Resumo</html:option>
                </html:select>
            </td>
        </tr>
        <tr>
            <th class="required">Peso:</th>
            <td>
                <html:text property="obj.peso" size="2" maxlength="3" />
            </td>
        </tr>
		<tfoot>
			<tr><td colspan="2">
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
<br>
<center>
<html:img page="/img/required.gif" style="vertical-align: top;"/> <span class="fontePequena"> Campos de preenchimento obrigatório. </span>
</center>
<br>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>