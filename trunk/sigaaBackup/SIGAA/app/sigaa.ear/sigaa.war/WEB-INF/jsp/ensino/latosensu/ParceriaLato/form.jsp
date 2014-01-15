<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<h2 class="tituloPagina">
	<html:link action="/ensino/latosensu/criarParceria.do?dispatch=cancelar">
		<ufrn:subSistema semLink="true"/>
	</html:link> &gt;
	Cadastrar Parceria
</h2>

<html:form action="/ensino/latosensu/criarParceria?dispatch=persist" styleId="form">
	 <table class="formulario" width="70%">
		<caption>Dados da Parceria do Curso Lato Sensu</caption>
		<html:hidden property="obj.id" />

		<tbody>
			<tr>
 				<th nowrap="nowrap">Curso:</th>
		   	    <td>
		   	    <c:choose>
		   	    <c:when test="${usuario.coordenadorLato}">
		   	    	${usuario.cursoLato.nome}
		   	    </c:when>
		   	    <c:otherwise>
		   	    	<c:set var="idAjax" value="obj.cursoLato.id"/>
					<c:set var="nomeAjax" value="obj.cursoLato.nome"/>
					<c:set var="tipoCurso" value="lato"/>
					<%@include file="/WEB-INF/jsp/include/ajax/curso_tecnico.jsp" %>
					<span class="required">&nbsp;</span>
		   	    </c:otherwise>
		   	    </c:choose>
				</td>
			</tr>
			<tr>
				<th nowrap="nowrap">Pessoa Jurídica:</th>
				<td>
					<c:set var="idAjax" value="obj.pessoaJuridica.id"/>
					<c:set var="nomeAjax" value="obj.pessoaJuridica.pessoa.nome"/>
					<c:set var="tipo" value="J"/>
					<%@include file="/WEB-INF/jsp/include/ajax/pessoa.jsp" %>
		        	<span class="required">&nbsp;</span>
		        </td>
			</tr>
			<tr>
				<th nowrap="nowrap">Descrição:</th>
				<td><html:textarea property="obj.descricao" rows="6" cols="57" /> <span class="required">&nbsp;</span></td>
			</tr>
		</tbody>
		<tfoot>
			<tr>
				<td colspan="2" align="center">
					<html:submit>Confirmar</html:submit>
                	<input value="Cancelar" type="button" onclick="javascript:cancelar('form');"/>
                </td>
			</tr>
		</tfoot>
</table>
</html:form>

<br>
<center>
<html:img page="/img/required.gif" style="vertical-align: top;"/> <span class="fontePequena"> Campos de preenchimento obrigatório. </span>
</center>
<br/>

<c:if test="${ param['dispatch'] == 'remove' }">
<script type="text/javascript">
	new Remocao('form');
</script>
</c:if>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>