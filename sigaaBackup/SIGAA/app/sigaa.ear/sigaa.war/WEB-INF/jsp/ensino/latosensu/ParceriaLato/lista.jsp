<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<h2 class="tituloPagina">
	<ufrn:subSistema></ufrn:subSistema> &gt;
	Parcerias
</h2>

<html:form action="/ensino/latosensu/criarParceria.do?dispatch=list">
	<table class="formulario" width="70%">
		<caption>Busca por Parcerias</caption>

		<tbody>
			<tr>
				<td><html:radio property="tipoBusca" value="1" styleClass="noborder"
					styleId="buscaCurso" /></td>
				<td> Curso: </td>
		   	    <td>
		   	    	<c:choose>
		   	    	<c:when test="${usuario.coordenadorLato}">
		   	    		<html:select property="obj.cursoLato.id" onfocus="javascript:forms[0].tipoBusca[0].checked = true;">
		   	    			<html:option value="">-- SELECIONE --</html:option>
		   	    			<html:options collection="cursosLato" property="id" labelProperty="nome"/>
		   	    		</html:select>
		   	    	</c:when>
		   	    	<c:otherwise>
		   	    		<c:set var="idAjax" value="obj.cursoLato.id"/>
						<c:set var="nomeAjax" value="obj.cursoLato.nome"/>
						<c:set var="tipoCurso" value="lato"/>
						<%@include file="/WEB-INF/jsp/include/ajax/curso_tecnico.jsp" %>
		   	    	</c:otherwise>
		   	    	</c:choose>
				 </td>
			</tr>
			<tr>
				<td><html:radio property="tipoBusca" value="2" styleClass="noborder" 
					styleId="buscaPessoa" /></td>
				<td> Pessoa Jurídica: </td>
		   	    	<td>
		   	    		<c:set var="idAjax" value="obj.pessoaJuridica.id"/>
						<c:set var="nomeAjax" value="obj.pessoaJuridica.pessoa.nome"/>
						<c:set var="tipo" value="J"/>
						<%@include file="/WEB-INF/jsp/include/ajax/pessoa.jsp" %>
		        	</td>
			</tr>
			<tr>
				<td><html:radio property="tipoBusca" value="3" styleClass="noborder"
					styleId="buscaCodigo" /></td>
				<td> Todos</td>
            </tr>
            </tbody>
            <tfoot>
            	<tr>
					<html:hidden property="buscar" value="true"/>
					<td colspan="4"><html:submit><fmt:message key="botao.buscar" /></html:submit></td>
				</tr>
			</tfoot>
     </table>
</html:form>
<br>

<c:if test="${not empty lista}">

<ufrn:table collection="${lista}"
	properties="cursoLato.nome, pessoaJuridica.pessoa.nome"
	headers="Curso, Pessoa Jurídica"
	title="Parcerias" crud="true" />

</c:if>

<script type="text/javascript">
		// Quem quiser usar, deve re-escrever no final da sua jsp
		function cursoOnFocus(e) {
			$('buscaCurso').checked = true;
		}
</script>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>