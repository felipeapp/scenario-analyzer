<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp" %>
<h2 class="tituloPagina">
	<html:link action="/ensino/cadastroCoordenacaoCurso?dispatch=cancelar">
		<ufrn:subSistema semLink="true"/>
	</html:link>
	&gt; Coordenações de Cursos
</h2>

<html:form action="/ensino/cadastroCoordenacaoCurso?dispatch=list" method="post">

<table class="formulario" width="500">
	<c:if test="${ param.page != 'null' }">
		<input type="hidden" name="page" value="${param.page + 0}"/>
	</c:if>
<caption>Busca por Membros de Coordenações</caption>
<tbody>
	<tr>
		<td> <html:radio property="tipoBusca" value="1" styleClass="noborder" styleId="buscaServidor" /> </td>
    	<td> <label for="buscaServidor"> Servidor </label></td>
    	<td>
    		<c:set var="idAjax" value="obj.servidor.id"/>
			<c:set var="nomeAjax" value="obj.servidor.pessoa.nome"/>
			<%@include file="/WEB-INF/jsp/include/ajax/servidor.jsp" %>
        </td>
    </tr>
 	<tr>
		<td> <html:radio property="tipoBusca" value="2" styleClass="noborder" styleId="buscaCurso" /> </td>
    	<td> <label for="buscaCurso"> Curso </label></td>
    	<td>
    		<html:select property="obj.curso.id" onfocus="marcaCheckBox('buscaCurso')">
                <html:option value="">-- SELECIONE --</html:option>
                <html:options collection="cursos" property="id" labelProperty="nome"/>
            </html:select>
        </td>
    </tr>
    <tr>
    	<td> <html:radio property="tipoBusca" value="2" styleClass="noborder" styleId="buscaTodos"/> </td>
    	<td> <label for="buscaTodos">Todos</label></td>
	</tr>
</tbody>
<tfoot>
	<tr>
		<td colspan="3">
			<html:hidden property="buscar" value="true"/>
			<html:submit value="Buscar"/>
    	</td>
    </tr>
</tfoot>
</table>
</html:form>
<br>
<ufrn:table collection="${lista}" properties="curso.descricao,servidor.pessoa.nome,cargoAcademico.descricao,dataInicioMandato"
headers="Curso,Servidor,Cargo,Início do Mandato"
title="Coordenações" crud="true"/>

<script type="text/javascript">
	function servidorOnFocus() {
		marcaCheckBox('buscaServidor');
	}
</script>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
