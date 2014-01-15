<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<style>
table.listagem th {
	font-weight: bold;
	vertical-align: top
}

table.listagem td {
	vertical-align: top
}
</style>

<f:view>

<h2>Comprovante de Solicitação de Homologação de
<c:choose>
	<c:when test="${relatorioHomologacaoStricto.obj.banca.dadosDefesa.discente.stricto}">Diploma</c:when>
	<c:otherwise>Trabalho Final</c:otherwise>
</c:choose></h2>

<table class="listagem">
	<tbody>
	<tr>
		<th width="20%">Matrícula:</th>
		<td>${relatorioHomologacaoStricto.obj.banca.dadosDefesa.discente.matricula }</td>
		<th>Nome:</th>
		<td>${relatorioHomologacaoStricto.obj.banca.dadosDefesa.discente.nome }</td>
	</tr>
	<tr>
		<th>Curso:</th>
		<td colspan="3">${relatorioHomologacaoStricto.obj.banca.dadosDefesa.discente.curso.descricao }</td>
	</tr>
	<tr>
		<th colspan="2" valign="top">Banca examinadora:</th>
		<td colspan="2" valign="top">
			<c:forEach items="#{relatorioHomologacaoStricto.obj.banca.membrosBanca}" var="membro">
				${membro.nome} - ${membro.tipoDescricao}<br/>
			</c:forEach>
		</td>
	</tr>
	<tr>
		<th valign="top">Título:</th>
		<td colspan="3">${relatorioHomologacaoStricto.obj.banca.dadosDefesa.titulo}</td>
	</tr>
	<tr>
		<th>Data da defesa:</th>
		<td colspan="3"><ufrn:format type="data" valor="${relatorioHomologacaoStricto.obj.banca.data}"></ufrn:format></td>
	</tr>
	<tr>
		<th>Grand-Área:</th>
		<td colspan="3">${relatorioHomologacaoStricto.obj.banca.grandeArea.nome}</td>
	</tr>
	<tr>
		<th>Área:</th>
		<td colspan="3">${relatorioHomologacaoStricto.obj.banca.area.nome}</td>
	</tr>
	<c:if test="${not empty relatorioHomologacaoStricto.obj.banca.subArea.nome}">
		<tr>
			<th>Sub-Área:</th>
			<td colspan="3">${relatorioHomologacaoStricto.obj.banca.subArea.nome}</td>
		</tr>
	</c:if>
	<c:if test="${not empty relatorioHomologacaoStricto.obj.banca.especialidade.nome}">
		<tr>
			<th>Sub-Área:</th>
			<td colspan="3">${relatorioHomologacaoStricto.obj.banca.especialidade.nome}</td>
		</tr>
	</c:if>
	<tr>
		<th valign="top">Resumo:</th>
		<td colspan="3">${relatorioHomologacaoStricto.obj.banca.dadosDefesa.resumo}</td>
	</tr>
	<tr>
		<th valign="top">Palavras-Chave:</th>
		<td colspan="3">${relatorioHomologacaoStricto.obj.banca.dadosDefesa.palavrasChave}</td>
	</tr>
	</tbody>
</table>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>