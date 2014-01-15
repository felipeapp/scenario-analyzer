<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>

<f:view>
	
	<h:outputText value="#{cadMonitor.create}"/>		    
	<h2><ufrn:subSistema /> > Selecionar Projeto de Ensino</h2>
 	<h:messages />


	<%@include file="/monitoria/form_busca_projeto.jsp"%>

	<c:set var="projetos" value="#{projetoMonitoria.projetosLocalizados}"/>

	<c:if test="${empty projetos}">
		<center><i> Nenhum Projeto localizado </i></center>
	</c:if>


	<c:if test="${not empty projetos}">


		<div class="infoAltRem">
		    <h:graphicImage value="/img/view.gif" style="overflow: visible;"/>: Visualizar Projeto de Ensino
		    <h:graphicImage value="/img/seta.gif" style="overflow: visible;" rendered="#{acesso.monitoria}"/><h:outputText rendered="#{acesso.monitoria}" value=": Alterar Componentes Obrigatórios"/>
		    <br/>
		</div>

	<h:form>
		 <table class="listagem">
		    <caption>Projetos de Monitoria Encontrados (${ fn:length(projetos) })</caption>
		      <thead>
		      	<tr>
		        	<th>Título</th>
		        	<th>Ano</th>
		        	<th>Unidade</th>
		        	<th>Situação</th>
		        	<th>&nbsp;</th>
		        	<th>&nbsp;</th>
		        </tr>
		 	</thead>
		 			 	
		 	<tbody>
	       	<c:forEach items="#{projetos}" var="projeto" varStatus="status">
	               <tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
	
	                    <td> ${projeto.titulo} </td>
	                    <td> ${projeto.ano} </td>
	                    <td> ${projeto.unidade.sigla} </td>
						<td> ${projeto.situacaoProjeto.descricao} </td>
						<td width="2%">
								<h:commandLink title="Visualizar Projeto de Monitoria" action="#{projetoMonitoria.view}">
					                   <f:param name="id" value="#{projeto.id}"/>
					                   <h:graphicImage url="/img/view.gif" />
								</h:commandLink>
						</td>		
						<td width="2%">
								<h:commandLink title="Alterar Componentes Obrigatórios do Projeto"action="#{ projetoMonitoria.iniciarAlterarComponentesObrigatorios }" immediate="true" rendered="#{acesso.monitoria}">							
				                   <f:param name="id" value="#{projeto.id}"/>
				                   <h:graphicImage url="/img/seta.gif" />				                   
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