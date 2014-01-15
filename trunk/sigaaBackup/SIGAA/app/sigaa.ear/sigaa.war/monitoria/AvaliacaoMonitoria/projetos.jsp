<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<%@page import="br.ufrn.sigaa.monitoria.dominio.StatusAvaliacao"%>

<f:view>
	<%@include file="/portais/docente/menu_docente.jsp" %>
	
	<h2><ufrn:subSistema /> > Projeto de Ensino n�o avaliados</h2>

	<c:set var="projetosEnsinoDistribuidosUsuarioAtual" value="${avalProjetoMonitoria.allProjetosAvaliacao}" scope="application"/>

	<div class="infoAltRem">
   	    <h:graphicImage value="/img/avaliar.gif" style="overflow: visible;"/>: Visualizar Avalia��o
   	    <h:graphicImage value="/img/view.gif" style="overflow: visible;"/>: Visualizar Projeto
	    <h:graphicImage value="/img/seta.gif" style="overflow: visible;"/>: Avaliar Projeto
	</div>


	<table class="visualizacao">
	<caption>Lista de todos os Projeto de Ensino dependentes de sua avalia��o (${ fn:length(avalProjetoMonitoria.avaliacoes) })</caption>
		<thead>
			<tr>
				<th style="text-align: left;">Ano</th>
				<th style="text-align: left;" width="50%">T�tulo</th>
				<th style="text-align: left;">Tipo</th>
				<th style="text-align: right;">Avalia��o</th>
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
								<h:commandLink action="#{avalProjetoMonitoria.view}" style="border: 0;" title="Visualizar Avalia��o"
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
				<td colspan="4"><center><font color="red">O usu�rio atual n�o possui Projetos pendentes de avalia��o<br/> ou n�o � Membro de Comiss�o de Monitoria</font> </center></td>
			</tr>
	</c:if>
	</tbody>	
	</table>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>