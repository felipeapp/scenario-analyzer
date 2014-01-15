<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2>
	<html:link action="/verMenuPesquisa">Pesquisa</html:link> > Alterar Situação de Projeto de Pesquisa
</h2>

<html:form action="/pesquisa/alterarSituacaoProjetoPesquisa" method="post">
	<html:hidden property="projeto.id"/>
	<c:set var="projeto" value="${ formAlterarSituacaoProjetoPesquisa.projeto }"></c:set>

	<table class="formulario" style="width: 90%;">
	<caption> Dados do Projeto </caption>
	<tr>
		<th> Código: </th>
		<td> ${ projeto.codigo } </td>
	</tr>
	<tr>
		<th> Titulo do Projeto: </th>
		<td> ${ projeto.titulo } </td>
	</tr>
	<tr>
		<th> Coordenador: </th>
		<td> ${ projeto.coordenador.nome } </td>
	<tr>
	<tr>
		<th width="25%"> Tipo do Projeto: </th>
		<td> ${ projeto.interno ? "INTERNO" : "EXTERNO" } </td>
	</tr>
	<c:choose>
		<c:when test="${projeto.interno}">
			<tr>
				<th> Edital:	</th>
				<td> ${projeto.edital.descricao} </td>
			</tr>
			<tr>
				<th> Cota:	</th>
				<td> ${projeto.edital.cota} </td>
			</tr>
		</c:when>
		<c:otherwise>
			<tr>
				<th> Período do Projeto: </th>
				<td>
					<fmt:formatDate value="${projeto.dataInicio}"pattern="dd/MM/yyyy" />
					a <fmt:formatDate value="${projeto.dataFim}"pattern="dd/MM/yyyy" />
				</td>
			</tr>
		</c:otherwise>
	</c:choose>
	<tr>
		<th> Situação atual: </th>
		<td> ${projeto.situacaoProjeto.descricao}</td>
	</tr>
	<tr>
		<td colspan="2" class="subFormulario"> Alterar Tipo e/ou Situação do Projeto </td>
	</tr>
	<tr>
		<th> Situação do Projeto: </th>
		<td>
			<html:select property="projeto.situacaoProjeto.id" style="width:90%">
				<html:options collection="situacoesProjeto" property="id" labelProperty="descricao" />
			</html:select>
		</td>
	</tr>
	<tr>
		<th>Tipo:</th>
		<td>
			<html:radio property="projeto.interno" styleClass="noborder" value="true" onclick="showInterno()" />Interno
   			<html:radio property="projeto.interno" styleClass="noborder" value="false" onclick="showExterno()" />Externo
    	</td>
    </tr>
	<tr id="interno">
		<th>Edital de Pesquisa:</th>
		<td>
			<c:set var="editaisAbertos" value="${projetoPesquisaForm.referenceData.editaisAbertos }"/>
			<html:select property="projeto.edital.id" style="width:90%">
		        <html:options collection="editais" property="id" labelProperty="descricao" />
	        </html:select>
			<span class="required"></span>
		</td>
	</tr>
	<tr id="externo">
		<th>Período do Projeto:</th>
		<td>
			<ufrn:calendar property="dataInicio"/> a <ufrn:calendar property="dataFim"/>
			<span class="required"></span>
		</td>
	</tr>
	<tfoot>
		<tr>
			<td colspan="2">
				<html:button value="Confirmar Alteração" dispatch="confirmar"/>
				<html:button value="Cancelar" onclick="javascript: history.go(-1);" cancelar="true"/>
			</td>
		</tr>
	</tfoot>
	</table>
</html:form>

<script type="text/javascript">
<!--
function showInterno() {
	$('interno').show();
	$('externo').hide();
};

function showExterno() {
	$('externo').show();
	$('interno').hide();
};

if (${projeto.interno}) {
	showInterno();
} else {
	showExterno();
}
//-->
</script>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>