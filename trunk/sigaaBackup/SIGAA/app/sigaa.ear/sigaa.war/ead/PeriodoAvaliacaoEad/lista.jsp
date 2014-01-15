<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>
<%@page import="br.ufrn.sigaa.arq.util.CheckRoleUtil"%>
<% CheckRoleUtil.checkRole(request, response, new int[] { SigaaPapeis.SEDIS, SigaaPapeis.COORDENADOR_TUTORIA_EAD, SigaaPapeis.COORDENADOR_PEDAGOGICO_EAD }); %>

<f:view>
	<h:messages/>
	<h2><ufrn:subSistema /> > Avaliações Periódicas</h2>
	<br>

	<h:outputText value="#{avaliacaoPeriodica.create}"/>

	<div class="infoAltRem">
	    <h:graphicImage value="/img/alterar.gif"style="overflow: visible;"/>: Alterar Avaliação
	    <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover Avaliação<br/>
	</div>


	<table class="listagem">
	<caption class="listagem">Lista de Avaliações </caption>
	<thead>
		<tr>
		<th>Pólo</th>
		<th>Curso</th>
		<th>Unidade</th>
		<th>Início</th>
		<th>Fim</th>
		<th></th>
		<th></th>
		</tr>
	</thead>

	<c:forEach items="${avaliacaoPeriodica.all}" var="avaliacao">
		<tr>
			<td>${avaliacao.poloCurso.polo.cidade.nomeUF}</td>
			<td>${avaliacao.poloCurso.curso.descricao}</td>
			<td> ${avaliacao.unidade} </td>
			<td> <fmt:formatDate value="${avaliacao.dataInicio}" pattern="dd/MM/yyyy"/> </td>
			<td> <fmt:formatDate value="${avaliacao.dataFim}" pattern="dd/MM/yyyy"/> </td>
			<h:form>
				<td>
					<input type="hidden" value="${avaliacao.id}" name="id"/>
					<h:commandButton image="/img/alterar.gif" value="Alterar" action="#{avaliacaoPeriodica.atualizar}" style="border: 0;"/>

				</td>
			</h:form>
			<h:form>
				<td  width="25">
					<input type="hidden" value="${avaliacao.id}" name="id"/>
					<h:commandButton image="/img/delete.gif" alt="Remover" action="#{avaliacaoPeriodica.preRemover}" style="border: 0;"/>
				</td>
			</h:form>
	</c:forEach>
	</table>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
