<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<h2 class="tituloPagina"> <ufrn:subSistema /> &gt; Cadastro de Orientações </h2>

<html:form action="/ensino/cadastroOrientacaoDiscente" method="post" styleId="form">

	<html:hidden property="obj.id" />

	<table class="formulario" width="90%">
    <caption>Atribuir Orientação</caption>

    <tbody>
	<tr>
    	<th class="obrigatorio">Orientador: </th>
        <td>
			<c:set var="idAjax" value="obj.orientador.id" />
			<c:set var="nomeAjax" value="obj.orientador.pessoa.nome" />
			<%@include file="/WEB-INF/jsp/include/ajax/docente.jsp"%>
		</td>
	</tr>

	<tr>
    	<th class="obrigatorio">Aluno: </th>
        <td>
				<c:set var="idAjax" value="obj.orientado.id" /> <c:set
					var="nomeAjax" value="obj.orientado.pessoa.nome" /> <%@include
					file="/WEB-INF/jsp/include/ajax/discente.jsp"%>
		</td>
	</tr>
	<tr>
    	<th valign="top" class="obrigatorio">Tipo de Orientação:</th>
        <td><html:select property="obj.tipoOrientacaoDiscente.id">
	                <html:option value=""> Opções </html:option>
	                <html:options collection="tiposOrientacaoDiscente" property="id" labelProperty="descricao" />
                </html:select>
         </td>
    </tr>
    <tr>
    	<th class="obrigatorio">Ano - Período:</th>
        <td><html:text property="obj.ano" maxlength="4" size="4" onkeyup="formatarInteiro(this)"/> -
        <html:text property="obj.periodo" size="1" maxlength="1" onkeyup="formatarInteiro(this)"/>
        </td>
	</tr>

	<tr>
    	<th class="obrigatorio">Orientador Principal?</th>
        <td>
			<html:radio property="obj.orientadorPrincipal" value="true" styleId="simAtivo" styleClass="noborder"/> <label for="simAtivo">Sim</label>
           	<html:radio property="obj.orientadorPrincipal" value="false" styleId="naoAtivo" styleClass="noborder"/> <label for="naoAtivo">Não</label>
        </td>
	</tr>

	</tbody>

	<tfoot>
		<tr><td colspan="2">
				<html:button value="Confirmar" dispatch="persist" />
				<input value="Cancelar" type="button" onclick="javascript:cancelar('form');"/>
   			</td></tr>
	</tfoot>

	</table>

</html:form>
<br>
<div class="obrigatorio">Campo de preenchimento obrigatório.</div>
<c:if test="${ param['dispatch'] == 'remove' }">
<script type="text/javascript">
	new Remocao('form');
</script>
</c:if>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
