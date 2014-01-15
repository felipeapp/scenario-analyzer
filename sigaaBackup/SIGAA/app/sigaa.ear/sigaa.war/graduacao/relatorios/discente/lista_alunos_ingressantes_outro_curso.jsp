<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<style>
	tr.curso td {padding: 20px 0 0; border-bottom: 1px solid #555;}
	tr.header td {padding: 0px ; background-color: #eee; border-bottom: 1px solid #555; font-weight: bold; border: 1px solid #000;}
	tr.componentes td {padding: 5px 2px 2px; border-bottom: 1px solid #000; border: 1px solid #000;}
</style>

<f:view>
	<h2><b>Lista dos Alunos Ingressantes em um Novo Curso</b></h2>

	<div id="parametrosRelatorio">
		<table >
			<tr>
				<th>Ano-Período:</th>
				<td> ${relatorioDiscente.ano}.${relatorioDiscente.periodo}</td>
			</tr>
			<c:if test="${ relatorioDiscente.processoSeletivo != null }">
			<tr>
				<th>Processo Seletivo:</th>
				<td> ${ relatorioDiscente.descricaoProcessoSeletivo } </td>
			</tr>
			</c:if>
			<tr>
				<th>Chamada:</th>
	 			<c:choose>
						<c:when test="${relatorioDiscente.chamada == 0}">
							<td>Todas</td>
						</c:when>
						<c:otherwise>
							<td>${ relatorioDiscente.descricaoChamada }</td>
						</c:otherwise>
				</c:choose> 
		    </tr>
	    </table>
	</div>
	<br/>
	<div align="center"><b></>Total de Alunos Ingressantes em um Novo Curso: ${ fn:length(relatorioDiscente.listaDiscente) }</b></div>
    <c:set var="curso_old" />
	<table cellspacing="1" width="100%" style="font-size: 10px;" class="relatorio">
		<tbody>
			<c:forEach items="${relatorioDiscente.listaDiscente}" var="linha">
				<c:set var="curso_new_old" value="${linha.nome_curso_antigo}"/>
				  <c:if test="${curso_old != curso_new_old}">
					<tr class="curso">
						<td colspan="10" style="text-align: left;"><b>Antigo Curso: ${linha.nome_curso_antigo}</b></td>
					</tr>
					<tr class="header">
						<td>Nome</td>
						<td style="text-align: center;" width="100px;">CPF</td>
						<td style="text-align: center;">Matrícula</td>
						<td>Novo Curso</td>
					</tr>
					<c:set var="curso_old" value="${curso_new_old}"/>
				</c:if>
				<tr>
					<td> 
						${linha.nome}
						<c:if test="${not empty linha.municipio_polo}"> - PÓLO ${linha.municipio_polo}</c:if>
					</td>
					<td style="text-align: center;"><ufrn:format type="cpf_cnpj" valor="${linha.cpf_cnpj}"/></td>
					<td style="text-align: center;"> ${linha.matricula }</td>
					<td> ${linha.nome_curso_novo} </td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>