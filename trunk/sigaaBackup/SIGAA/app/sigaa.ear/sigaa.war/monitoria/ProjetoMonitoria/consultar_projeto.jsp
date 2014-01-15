<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>

<%@page import="br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto"%>

<f:view>

    <c:if test="${!acesso.monitoria}">
		<%@include file="/portais/docente/menu_docente.jsp" %>	
	</c:if>
	
	<h2><ufrn:subSistema /> &gt; Consultar Situação dos Projetos de Ensino</h2>


	<%@include file="/monitoria/form_busca_projeto.jsp"%>


	<c:set var="projetos" value="#{projetoMonitoria.projetosLocalizados}"/>



	<c:if test="${empty projetos}">
		<center><i> Nenhum Projeto localizado </i></center>
	</c:if>


	<c:if test="${not empty projetos}">

		<div class="infoAltRem">
		    <h:graphicImage value="/img/view.gif"style="overflow: visible;"/>: Visualizar Projeto de Ensino	    
		    <h:graphicImage value="/img/monitoria/document_attachment.png"style="overflow: visible;"/>: Visualizar Arquivo	    	    
		    <br/>
		</div>
	
		<h:form id="form">
		 <table class="listagem" id="lista">
		    <caption>Projetos de Monitoria Encontrados (${ fn:length(projetos) })</caption>
	
		      <thead>
		      	<tr>
		      		<th>Ano</th>
		        	<th>Título</th>
		        	<th style="text-align:center;">Tipo do Projeto</th>
		        	<th>Situação</th>
		        	<th style="text-align: center;">Interno</th>
		        	<th>&nbsp;</th>
		        </tr>
		 	</thead>
		 	<tbody>
		 	
		 			<c:set var="RECOMENDADO" value="<%= String.valueOf(TipoSituacaoProjeto.MON_RECOMENDADO) %>" scope="application"/>
					<c:set var="NAO_RECOMENDADO" value="<%= String.valueOf(TipoSituacaoProjeto.MON_NAO_RECOMENDADO) %>" scope="application"/>				
					<c:set var="AVALIADO_COM_DISCREPANCIA" value="<%= String.valueOf(TipoSituacaoProjeto.MON_AVALIADO_COM_DISCREPANCIA_DE_NOTAS) %>" scope="application"/>								
		 	
			<c:set var="unidadeProjeto" value=""/>	 		 	
	       	<c:forEach items="#{projetos}" var="projeto" varStatus="status">
	
						<c:if test="${ unidadeProjeto != projeto.unidade.id }">
							<c:set var="unidadeProjeto" value="${ projeto.unidade.id }"/>
							<tr>
								<td colspan="6" style="background: #C8D5EC; font-weight: bold; padding: 2px 0 2px 5px;">
								${ projeto.unidade.nome } / ${ projeto.unidade.sigla }
								</td>
							</tr>
						</c:if>

		               <tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">

						<td> ${projeto.ano}</td>
	                    <td width="50%"> ${projeto.titulo}
		                     <br/><i><h:outputText value="Coordenador(a): #{projeto.coordenacao.pessoa.nome}" rendered="#{not empty projeto.coordenacao}"/></i>
	                    </td>
	                    <td align="center"> ${projeto.tipoProjetoEnsino.descricao} </td> 
						<td> ${projeto.situacaoProjeto.descricao} </td>
						<td style="text-align: center;"> <ufrn:format valor="${projeto.projeto.interno}" type="simnao" /> </td>
						<td width="6%">					
						
								<%-- comandos liberados inclusive para ALUNOS --%>
								
								<h:commandLink title="Visualizar Projeto de Monitoria" action="#{ projetoMonitoria.view }" id="btViewProjeto" rendered="#{projeto.id > 0 }">
								   	<f:param name="id" value="#{projeto.id}"/>				    	
									<h:graphicImage url="/img/view.gif"/>
								</h:commandLink>
											
									
								<h:commandLink  title="Visualizar Arquivo Anexo" action="#{projetoMonitoria.viewArquivo}" id="btViewArquivo" rendered="#{projeto.idArquivo != null}">
								   	<f:param name="idArquivo" value="#{projeto.idArquivo}"/>				    	
									<h:graphicImage url="/img/monitoria/document_attachment.png"/>
								</h:commandLink>
											
						</td>
	              </tr>
	          </c:forEach>
		 	</tbody>
		 </table>
	</h:form>

	</c:if>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>