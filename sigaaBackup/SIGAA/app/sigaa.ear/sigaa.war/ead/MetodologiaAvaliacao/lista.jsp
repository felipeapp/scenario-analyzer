<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@page import="br.ufrn.sigaa.arq.util.CheckRoleUtil"%>
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>

<% CheckRoleUtil.checkRole(request, response, new int[] { SigaaPapeis.SEDIS, SigaaPapeis.COORDENADOR_PEDAGOGICO_EAD }); %>

<f:view>
<h:form>
	<h:messages/>
	<h2><ufrn:subSistema /> > Metodologias de Avaliação</h2>

	<h:outputText value="#{metodologiaAvaliacaoEad.create}"/>

	<div class="infoAltRem">
	    <h:graphicImage value="/img/buscar.gif" style="overflow: visible;"/>: Visualizar Metodologia de Avaliação
	    <h:graphicImage value="/img/alterar.gif" style="overflow: visible;"/>: Alterar Metodologia de Avaliação <br />
	    <h:graphicImage value="/img/check_cinza.png" style="overflow: visible;"/>: Inativar Metodologia de Avaliação
	    <h:graphicImage value="/img/check.png" style="overflow: visible;"/>: Ativar Metodologia de Avaliação
	</div>

	<table class="listagem">
	<caption class="listagem">Lista de Metodologias de Avaliação </caption>
	<thead>
		<tr>
		<th>Curso</th>
		<th style="text-align: center">Ativa</th>
		<th style="text-align: center">Data de Cadastro</th>
		<th width="3%"></th>
		<th width="3%"></th>
		<th width="3%"></th>
		</tr>
	</thead>

	<c:forEach items="#{metodologiaAvaliacaoEad.all}" var="item" varStatus="status">
		<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
			<td> ${item.curso.descricao}</td>
			<td align="center"> ${item.ativa ? 'Sim' : 'Não' } </td>
			<td align="center"> <fmt:formatDate pattern="dd/MM/yyyy" value="${item.dataCadastro}"/> </td>
				<td>
					<h:commandLink action="#{metodologiaAvaliacaoEad.visualizar}" >
						<h:graphicImage value="/img/buscar.gif" style="overflow: visible;" title="Visualizar Metodologia de Avaliação" />
						<f:param name="id" value="#{item.id}"/>
					</h:commandLink>
				</td>
				<td>
					<h:commandLink action="#{metodologiaAvaliacaoEad.atualizar}" >
						<h:graphicImage value="/img/alterar.gif" style="overflow: visible;" title="Alterar Metodologia de Avaliação" rendered="#{item.ativa }" />
						<f:param name="id" value="#{item.id}"/>
					</h:commandLink>
				</td>
				<td  width="25">
					<h:commandLink action="#{metodologiaAvaliacaoEad.inativar}" onclick="return confirm('Deseja realmente inativar essa metodologia?');" rendered="#{item.ativa }">
						<h:graphicImage value="/img/check_cinza.png" alt="Inativar" title="Inativar Metodologia de Avaliação" />
						<f:param name="id" value="#{item.id}"/>
					</h:commandLink>
					<h:commandLink action="#{metodologiaAvaliacaoEad.ativar}" onclick="return confirm('Deseja realmente ativar essa metodologia?');" rendered="#{!item.ativa }">
						<h:graphicImage value="/img/check.png" alt="Ativar" title="Ativar Metodologia de Avaliação" />
						<f:param name="id" value="#{item.id}"/>
					</h:commandLink>
				</td>
	</c:forEach>
	</table>

</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>