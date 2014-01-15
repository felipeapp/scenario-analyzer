<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<%@page import="br.ufrn.sigaa.monitoria.dominio.StatusAvaliacao"%>

<f:view>
	<%@include file="/portais/docente/menu_docente.jsp" %>
	
	<h2><ufrn:subSistema /> > Projeto de Ensino não avaliados</h2>

	<c:set var="projetosEnsinoDistribuidosUsuarioAtual" value="${avalProjetoMonitoria.allProjetosAvaliacao}" scope="application"/>

	<div class="infoAltRem">
   	    <h:graphicImage value="/img/avaliar.gif" style="overflow: visible;"/>: Visualizar Avaliação
   	    <h:graphicImage value="/img/view.gif" style="overflow: visible;"/>: Visualizar Projeto
	    <h:graphicImage value="/img/seta.gif" style="overflow: visible;"/>: Avaliar Projeto
	</div>


	<table class="visualizacao">
	<caption>Lista de todos os Projeto de Ensino dependentes de sua avaliação (${ fn:length(avalProjetoMonitoria.avaliacoes) })</caption>
		<thead>
			<tr>
				<th style="text-align: left;">Ano</th>
				<th style="text-align: left;" width="50%">Título</th>
				<th style="text-align: left;">Tipo</th>
				<th style="text-align: right;">Avaliação</th>
				<th style="text-align: center;">Avaliado em</th>						
				<th></th>										
				<th></th>
				<th></th>				
			</tr>
		</thead>
		
		
	<tbody>
	
							
	<c:if test="${not empty avalProjetoMonitoria.avaliacoes}">
		<h:form>
			<c:forEach items="#{avalProjetoMonitoria.avaliacoes}" var="avaliacao" varStatus="status">
					<tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
					
						<td> ${avaliacao.projetoEnsino.ano} </td>
						<td> ${avaliacao.projetoEnsino.titulo} </td>
						<td> ${avaliacao.projetoEnsino.tipoProjetoEnsino.descricao}</td>
						<td style="text-align: right;" width="10%"> ${avaliacao.notaAvaliacao} </td>
						<td style="text-align: center;"> <fmt:formatDate value="${avaliacao.dataAvaliacao}" pattern="dd/MM/yyyy" /></td>
			
						<td width="2%">
								<h:commandLink action="#{avalProjetoMonitoria.view}" style="border: 0;" title="Visualizar Avaliação"
									rendered="#{! avaliacao.avaliacaoEmAberto}">
								      <f:param name="idAvaliacao" value="#{avaliacao.id}"/>								      
								      <h:graphicImage url="/img/avaliar.gif" />
								</h:commandLink>								
						</td>					
						<td width="2%">
								<h:commandLink action="#{projetoMonitoria.view}" style="border: 0;" title="Visualizar Projeto">
								      <f:param name="id" value="#{avaliacao.projetoEnsino.id}"/>								      
								      <h:graphicImage url="/img/view.gif" />
								</h:commandLink>								
						</td>
						<td width="2%">	
								<h:commandLink action="#{avalProjetoMonitoria.escolherAvaliacao}" style="border: 0;" title="Avaliar Projeto" 
									rendered="#{avaliacao.avaliacaoEmAberto}">
								      <f:param name="idAvaliacao" value="#{avaliacao.id}"/>								      
								      <h:graphicImage url="/img/seta.gif" />
								</h:commandLink>								
						</td>					
						
					</tr>
			</c:forEach>
		</h:form>
	</c:if>
	
	<c:if test="${empty avalProjetoMonitoria.avaliacoes}">            
			<tr>
				<td colspan="4"><center><font color="red">O usuário atual não possui Projetos pendentes de avaliação<br/> ou não é Membro de Comissão de Monitoria</font> </center></td>
			</tr>
	</c:if>
	</tbody>	
	</table>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>