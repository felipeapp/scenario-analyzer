<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<h2 class="tituloPagina"> <ufrn:subSistema /> &gt; Lista de Orientações </h2>


<html:form
	action="/ensino/cadastroOrientacaoDiscente" styleId="form">
	<table class="formulario" width="75%">
		<caption>Busca por Orientações</caption>
		<tbody>
			<tr>
				<th>Orientador:</th>
				<td><html:radio property="tipoBusca" value="1" styleClass="noborder"
					styleId="buscaCodigo" /></td>
				<td><c:set var="idAjax" value="obj.orientador.id" /> <c:set
					var="nomeAjax" value="obj.orientador.nome" /> <%@include
					file="/WEB-INF/jsp/include/ajax/docente.jsp"%></td>
			</tr>
			<tr>
				<th>Discente:</th>
				<td><html:radio property="tipoBusca" value="2" styleClass="noborder"
					styleId="buscaCodigo" /></td>
				<td><c:set var="idAjax" value="obj.orientado.id" /> <c:set
					var="nomeAjax" value="obj.orientado.pessoa.nome" /> <%@include
					file="/WEB-INF/jsp/include/ajax/discente.jsp"%></td>
			</tr>
			<tr>
				<th valign="top">Tipo de Orientacao:</th>
				<td><html:radio property="tipoBusca" value="3" styleClass="noborder"
					styleId="buscaCodigo" /></td>
				<td><html:select property="obj.tipoOrientacaoDiscente.id" onchange="javascript:forms[0].tipoBusca[2].checked = true;">
					<html:option value=""> Opções </html:option>
					<html:options collection="tiposOrientacaoDiscente" property="id"
						labelProperty="descricao" />
				</html:select></td>
			</tr>
		</tbody>
		<html:hidden property="buscar" value="true" />
		<tfoot>
			<tr>
				<td colspan="4" align="center">
				<html:button value="Buscar" dispatch="list" />
				<html:button value="Cancelar" dispatch="cancelar" />
				</td>
			</tr>
		</tfoot>
	</table>
</html:form>

<br>
	<ufrn:table collection="${lista}" properties="tipoOrientacaoDiscente.descricao,orientado.pessoa.nome,orientador.nome,ano,periodo"
		headers="Tipo de Orientação, Discente, Orientador, Ano, Periodo" title="Orientações"
		crud="true" />

<br />
<br />

<script type="text/javascript">
		// Quem quiser usar, deve re-escrever no final da sua jsp
		function docenteOnFocus() {
			javascript:document.forms[0].tipoBusca[0].checked = true;
		}
		function discenteOnFocus() {
			javascript:document.forms[0].tipoBusca[1].checked = true;
		}
	</script>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
