<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>

<f:view>
	<h:outputText value="#{cadMonitor.create}"/>	
		
	<h2><ufrn:subSistema /> > Selecionar Projeto de Ensino</h2>


	<%@include file="/monitoria/form_busca_projeto.jsp"%>

	<c:set var="projetos" value="#{projetoMonitoria.projetosLocalizados}"/>

	<c:if test="${empty projetos}">
		<center><i> Nenhum Projeto localizado </i></center>
	</c:if>


	<c:if test="${not empty projetos}">

		<div class="infoAltRem">
		    <h:graphicImage value="/img/alterar.gif" style="overflow: visible;" rendered="#{acesso.monitoria}"/><h:outputText rendered="#{acesso.monitoria}" value=": Alterar Proposta do Projeto"/>
		    <h:graphicImage value="/img/alterar_old.gif" style="overflow: visible;" rendered="#{acesso.monitoria}"/><h:outputText rendered="#{acesso.monitoria}" value=": Alterar Situação do Projeto" escape="false"/>
		    <h:graphicImage value="/img/view.gif" style="overflow: visible;"/>: Visualizar Projeto de Ensino<br/>
		    <h:graphicImage value="/img/alterar2.gif" style="overflow: visible;"/>: Devolver para Coordenador(a)
		    <h:graphicImage width="18px;" height="18px;" value="/img/extensao/financiamento_faex.png" style="overflow: visible;"/>: Definir valor Financiado
		    <h:graphicImage width="18px;" height="18px;" value="/img/monitoria/document_new.png" style="overflow: visible;"/>: Resumo SID
		    <br/>
		</div>

	<h:form>
		 <table class="listagem">
		    <caption>Projetos de Monitoria Encontrados (${ fn:length(projetos) })</caption>
		      <thead>
		      	<tr>
		        	<th>Ano</th>
		        	<th>Título</th>
		        	<th width="10%">Tipo</th>
		        	<th width="15%">Dimensão</th>		        	
		        	<th>Situação</th>
		        	<th>&nbsp;</th>
		        	<th>&nbsp;</th>
		        	<th>&nbsp;</th>
		        	<th>&nbsp;</th>
		        	<th>&nbsp;</th>
		        	<th>&nbsp;</th>
		        </tr>
		 	</thead>
		 			 	
		 	<tbody>
			<c:set var="unidadeProjeto" value=""/>	 		 
	       	<c:forEach items="#{projetos}" var="projeto" varStatus="status">
	
						<c:if test="${ unidadeProjeto != projeto.unidade.id }">
							<c:set var="unidadeProjeto" value="${ projeto.unidade.id }"/>
							<tr>
								<td colspan="11" style="background: #C8D5EC; font-weight: bold; padding: 2px 0 2px 5px;">
								${ projeto.unidade.nome } / ${ projeto.unidade.sigla }
								</td>
							</tr>
						</c:if>

		               <tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
						
						<td> ${projeto.ano}</td>
	                    <td>
	                    	<h:outputText value="#{projeto.titulo}">
	                    		<f:attribute name="lineWrap" value="90"/>
								<f:converter converterId="convertTexto"/>
	                    	</h:outputText>  
		                     <br/><i><h:outputText value="Coordenador(a): #{projeto.coordenacao.pessoa.nome}" rendered="#{not empty projeto.coordenacao}"/></i>
	                    </td>
	                    <td> ${projeto.tipoProjetoEnsino.sigla} </td> 
	                    <td> ${projeto.projetoAssociado ? 'PROJETO ASSOCIADO' : 'PROJETO ISOLADO'}</td>	                    
						<td> ${projeto.situacaoProjeto.descricao} </td>
						<td width="2%">		
								<h:commandLink title="Alterar Proposta do Projeto" action="#{ projetoMonitoria.atualizar }" immediate="true" rendered="#{acesso.monitoria}">
							       <f:param name="id" value="#{projeto.id}"/>
				                   <h:graphicImage url="/img/alterar.gif" />
								</h:commandLink>
						</td>
								
						<td width="2%">		
								<h:commandLink title="Alterar Situação do Projeto" action="#{ projetoMonitoria.iniciarAlterarSituacaoProjeto }" immediate="true" rendered="#{acesso.monitoria}">
							       <f:param name="id" value="#{projeto.id}"/>
				                   <h:graphicImage url="/img/alterar_old.gif" />
								</h:commandLink>
						</td>
						<td width="2%">
								<h:commandLink title="Visualizar Projeto de Monitoria"  action="#{ projetoMonitoria.view }" immediate="true">
							       <f:param name="id" value="#{projeto.id}"/>
				                   <h:graphicImage url="/img/view.gif" />
								</h:commandLink>
						</td>
							
						<td width="2%">
							<h:commandLink title="Definir valor financiado" action="#{valorFinanciadoProjetoMBean.atualizar}">
								<f:param name="id" value="#{projeto.id}"/>
					        	<h:graphicImage url="/img/extensao/financiamento_faex.png" width="19px;" height="19px;"/>
							</h:commandLink>
						</td>
						
						<td width="2%">
							<h:commandLink rendered="#{projeto.situacaoProjeto.id == 1 || projeto.situacaoProjeto.id == 2 }" title="Devolver Proposta para Coordenador(a)" action="#{projetoMonitoria.reeditarProposta}"
								onclick="return confirm('Tem certeza que deseja Devolver esta proposta para Coordenador(a)?');" id="devolver_coord_dpto_">
								<f:param name="id" value="#{projeto.id}"/>
					        	<h:graphicImage url="/img/alterar2.gif"/>
							</h:commandLink>
						</td>
						
						<td>
							<h:commandLink  title="Resumo(s) SID" action="#{resumoSid.listarResumosProjeto}" style="border: 0;">
							   	<f:param name="id" value="#{projeto.id}"/>				    	
								<h:graphicImage url="/img/monitoria/document_new.png" />
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