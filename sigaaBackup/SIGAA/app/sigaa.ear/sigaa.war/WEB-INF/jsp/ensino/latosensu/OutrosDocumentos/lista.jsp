<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<h2 class="tituloPagina">
<ufrn:subSistema></ufrn:subSistema> &gt;
Lista de Outros Documentos
</h2>

<html:form action="/ensino/latosensu/cadastroOutrosDocumentos.do?dispatch=list">
	<table class="formulario" width="75%">
		<caption>Busca por Outros Documentos de Lato Sensu</caption>

		<tbody>
			<tr>
				<td><html:radio property="tipoBusca" value="1" styleClass="noborder"
					styleId="buscaCodigo" /></td>
				<th nowrap="nowrap">Nome do Curso</th>
				<td><c:set var="idAjax" value="obj.curso.id"/>
				    	<c:set var="nomeAjax" value="obj.curso.nome"/>
				    	<c:set var="tipoCurso" value="lato"/>
						<%@include file="/WEB-INF/jsp/include/ajax/curso_tecnico.jsp" %></td>
			</tr>
			<tr>
				<td><html:radio property="tipoBusca" value="2" styleClass="noborder"
					styleId="buscaCodigo" /></td>
				<th nowrap="nowrap">Descrição:</th>
            	<td><html:text property="obj.descricao" size="40" maxlength="30" value="" onfocus="javascript:forms[0].tipoBusca[1].checked = true;"/>
            	</td>
			</tr>
			<tr>
				<td><html:radio property="tipoBusca" value="3" styleClass="noborder"
					styleId="buscaCodigo" /></td>
				<th nowrap="nowrap">Todos</th>
            </tr>
            </tbody>
            <tfoot>
            <tr>
				<td colspan="4"><html:submit><fmt:message key="botao.buscar" /></html:submit></td>
			</tr>
            </tfoot>
     </table>
</html:form>
<br/>
<c:if test="${not empty lista}">

<ufrn:table collection="${lista}"
	properties="data,curso.descricao,descricao"
	headers="Data, Curso Lato, Descrição"
	title="Outros Documentos" crud="true" />

</c:if>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
