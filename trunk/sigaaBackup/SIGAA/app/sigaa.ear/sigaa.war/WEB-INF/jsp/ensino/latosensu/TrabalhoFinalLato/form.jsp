<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<h2 class="tituloPagina">
	<html:link action="/ensino/latosensu/cadastroTrabalhoFinalLato.do?dispatch=cancelar">
		<ufrn:subSistema semLink="true"/>
	</html:link> &gt;
	Cadastro de Trabalho Final
</h2>

<html:form action="/ensino/latosensu/cadastroTrabalhoFinalLato?dispatch=persist" method="post" focus="obj.titulo" styleId="form">
    <table class="formulario" width="80%">
		<caption>Trabalho Final de Lato Sensu</caption>
		<html:hidden property="obj.id" />
		<tbody>
			<tr>
 				<th class="required">Título:</th>
				<td><html:text property="obj.titulo" size="60" onkeyup="CAPS(this)" /></td>
            </tr>
			<tr>
 				<th class="required">Aluno:</th>
				<td>
					<c:set var="idAjax" value="obj.discenteLato.id"/>
	                <c:set var="nomeAjax" value="obj.discenteLato.pessoa.nome"/>
	                <c:set var="nivel" value="L" />
	                <%@include file="/WEB-INF/jsp/include/ajax/discente.jsp" %>
                </td>
            </tr>
            <tr>
 				<th class="required">Orientador:</th>
				<td>
					<c:set var="todosDocentes" value="true" />
					<c:set var="idAjax" value="obj.servidor.id"/>
					<c:set var="nomeAjax" value="obj.servidor.pessoa.nome"/>
					<%@include file="/WEB-INF/jsp/include/ajax/docente.jsp" %>
                </td>
            </tr>
            <tr>
            	<th class="required"> Arquivo do Trabalho Final: </th>
            	<td> <html:file property="arquivoTrabalhoFinal" size="55"></html:file> </td>
            </tr>
        </tbody>
		<tfoot>
   			<tr>
				<td colspan="2">
				<html:submit value="Confirmar" />
                <input value="Cancelar" type="button" onclick="javascript:cancelar('form');"/>
                </td>
			</tr>
		</tfoot>
	</table>
</html:form>

<c:if test="${ param['dispatch'] == 'remove' }">
<script type="text/javascript">
	new Remocao('form');
</script>
</c:if>
<center>
<br>
<html:img page="/img/required.gif" style="vertical-align: top;"/> <span class="fontePequena"> Campos de preenchimento obrigatório. </span>
<br>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>