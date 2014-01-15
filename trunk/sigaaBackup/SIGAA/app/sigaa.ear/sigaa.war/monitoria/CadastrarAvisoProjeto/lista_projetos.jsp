<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>


<%@page import="br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto"%>

<f:view>
	<%@include file="/portais/docente/menu_docente.jsp" %>

	<h:outputText  value="#{projetoMonitoria.create}"/>
	<h:outputText  value="#{avisoProjeto.create}"/>
	<h2><ufrn:subSistema /> > Projetos de Ensino</h2>
	<br>
	<div class="infoAltRem">
		<c:if test="${acesso.coordenadorMonitoria}">
		    <h:graphicImage value="/img/seta.gif" style="overflow: visible;" />: Cadastrar Avisos para Membros do Projeto
	    </c:if>
	</div>

	<h:form>
	<table class="listagem">
		<caption class="listagem">Projetos de Ensino Coordenados ou Criados pelo usuário atual </caption>
		<thead>
			<tr>
				<th width="70%">Título do Projeto</th>
				<th>Situação</th>
				<th></th>
			</tr>
		</thead>

		<c:forEach items="#{projetoMonitoria.projetosAtivosCoordenadosUsuarioLogado}" var="proj" varStatus="status">
            <tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
				<td> ${proj.anoTitulo} </td>
				<td> ${proj.situacaoProjeto.descricao} </td>
				<td width="3%">
					<h:commandLink  title="Cadastrar Novo Aviso" action="#{avisoProjeto.verAvisosDoProjeto}" style="border: 0;">
					      <f:param name="id" value="#{proj.projeto.id}"/>
					      <h:graphicImage url="/img/seta.gif" />
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