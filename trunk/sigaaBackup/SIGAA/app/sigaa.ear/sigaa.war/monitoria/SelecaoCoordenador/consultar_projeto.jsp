<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>

<f:view>
    
	<h2><ufrn:subSistema /> > Alterar Coordenador de Projetos</h2>

 	<h:messages />
	<%@include file="/monitoria/form_busca_projeto.jsp"%>


	<c:set var="projetos" value="#{projetoMonitoria.projetosLocalizados}"/>

	<c:if test="${empty projetos}">
		<center><i> Nenhum Projeto localizado </i></center>
	</c:if>

	<c:if test="${not empty projetos}">

		<div class="infoAltRem">
		    <h:graphicImage value="/img/view.gif"style="overflow: visible;"/>: Visualizar Projeto de Ensino
		    <h:graphicImage value="/img/coordenador.png" style="overflow: visible;" rendered="#{acesso.monitoria}" /><h:outputText value=": Alterar Coordenador de Projeto" rendered="#{acesso.monitoria}" />
		    <br/>
		</div>
	
		<h:form id="form">
		 <table class="listagem">
		    <caption>Projetos de Monitoria Encontrados (${ fn:length(projetos) })</caption>
	
		      <thead>
		      	<tr>
		      		<th>Ano</th>
		        	<th>Título</th>
		        	<th>Situação</th>
		        	<th>Dimensão Acadêmica</th>
		        	<th>&nbsp;</th>
		        </tr>
		 	</thead>
		 	<tbody>
		 	
		 	<c:set var="unidadeProjeto" value=""/>
	       	<c:forEach items="#{projetos}" var="projeto" varStatus="status">

						<c:if test="${ unidadeProjeto != projeto.unidade.id }">
							<c:set var="unidadeProjeto" value="${ projeto.unidade.id }"/>
							<tr>
								<td colspan="5" style="background: #C8D5EC; font-weight: bold; padding: 2px 0 2px 5px;">
								${ projeto.unidade.nome } / ${ projeto.unidade.sigla }
								</td>
							</tr>
						</c:if>

		               <tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">

	
						<td> ${projeto.ano}</td>
	                    <td width="60%"> ${projeto.titulo}
		                     <br/><i><h:outputText value="Coordenador(a): #{projeto.coordenacao.pessoa.nome}" rendered="#{not empty projeto.coordenacao}"/></i> 
	                    </td>
						<td> ${projeto.situacaoProjeto.descricao} </td>
						<td>${projeto.projetoAssociado ? 'ASSOCIADO' : 'MONITORIA'}</td>
						<td  width="5%">					
								<h:commandLink title="Visualizar Projeto de Monitoria" action="#{ projetoMonitoria.view }" style="border: 0;">
								   	<f:param name="id" value="#{projeto.id}"/>				    	
									<h:graphicImage url="/img/view.gif"  />
								</h:commandLink>

								<h:commandLink  action="#{projetoMonitoria.listarCoordenadores}"  title="Alterar coordenador de projeto" rendered="#{acesso.monitoria}" style="border: 0;">
								   	<f:param name="id" value="#{projeto.id}"/>				    	
									<h:graphicImage url="/img/coordenador.png"  />
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