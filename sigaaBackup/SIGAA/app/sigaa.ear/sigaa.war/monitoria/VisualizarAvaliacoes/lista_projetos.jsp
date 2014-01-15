<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>


<%@page import="br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto"%>

<f:view>
	<%@include file="/portais/docente/menu_docente.jsp" %>

	<h:outputText  value="#{projetoMonitoria.create}"/>

	<h2><ufrn:subSistema /> > Projetos de Ensino</h2>
	<br>	

	<div class="infoAltRem">
		<c:if test="${acesso.coordenadorMonitoria}">
	     	<h:graphicImage value="/img/monitoria/document_chart.png" style="overflow: visible;" />: Visualizar Avaliações
	    </c:if>
	</div>


<h:form id="form">
	<table class="listagem">
		<caption class="listagem">Projetos de Ensino Coordenados ou Criados pelo usuário atual </caption>
		<thead>
			<tr>
				<th>Título do Projeto</th>
				<th>Situação</th>
				<th></th>
			</tr>
		</thead>

		<c:forEach items="#{projetoMonitoria.projetosAtivosCoordenadosUsuarioLogado}" var="projeto" varStatus="status">
            <tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">

				<td> ${projeto.anoTitulo} </td>
				<td> ${projeto.situacaoProjeto.descricao} </td>
				<td>
					<h:commandLink  title="Visualizar Avaliações" action="#{projetoMonitoria.viewAvaliacoes}" 
						style="border: 0;" rendered="#{projeto.ativo}">
						   	<f:param name="id" value="#{projeto.id}"/>				    	
							<h:graphicImage url="/img/monitoria/document_chart.png" />
					</h:commandLink>
				</td>
			</tr>
		</c:forEach>

		<c:if test="${empty projetoMonitoria.projetosAtivosCoordenadosUsuarioLogado}">
			<tr>
				<td colspan="4"><center><font color="red">O usuário não é Coordenador de Projetos de Ensino Ativos<br/></font> </center></td>
			</tr>
		</c:if>

	</table>
	</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>