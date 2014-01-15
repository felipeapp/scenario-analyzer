<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>

<%@page import="br.ufrn.sigaa.monitoria.dominio.SituacaoDiscenteMonitoria"%>
<f:view>
	<%@include file="/portais/discente/menu_discente.jsp"%>

	<h:outputText value="#{discenteMonitoria.create}"/>	
	<h:outputText value="#{projetoMonitoria.create}"/>		
	
	<h2><ufrn:subSistema /> > Lista de Projetos do Discente</h2>

	<div class="infoAltRem">
    	<h:graphicImage value="/img/view.gif" style="overflow: visible;"/>: Visualizar Projeto	    
	    <h:graphicImage value="/img/monitoria/user1_view.png" style="overflow: visible;"/>: Visualizar Monitoria
	    <br/>	    
	    <h:graphicImage value="/img/seta.gif" style="overflow: visible;"/>: Atualizar Dados Bancários
	    <c:if test="${ monitoria.frequenciaMonitoria }">
	  	  <h:graphicImage value="/img/monitoria/document_new.png" style="overflow: visible;"/>: Cadastrar Nova Frequência<br/>
	    </c:if>
	    <h:graphicImage value="/img/monitoria/businessman_preferences.png" style="overflow: visible;"/>: Aceitar ou Recusar Monitoria
	    
	</div>

	<br/>

	<c:set var="discenteNosProjetos" value="#{discenteMonitoria.discentesMonitoriaUsuarioLogado}"/>

	<c:if test="${empty discenteNosProjetos}">
	<center><i> Você não participa de projetos de monitoria. </i></center>
	</c:if>


	<c:if test="${not empty discenteNosProjetos}">

	<h:form>
	 <table class="listagem">
	    <caption>Projetos de Monitoria Encontrados (${ fn:length(discenteNosProjetos) })</caption>

	      <thead>
	      	<tr>
	        	<th width="60%">Título</th>
	        	<th>Vínculo</th>
	        	<th>Situação</th>
	        	<th>&nbsp;</th>
	        	<th>&nbsp;</th>
	        	<th>&nbsp;</th>
	        	<th>&nbsp;</th>
	        	<th>&nbsp;</th>
	        </tr>
	 	</thead>
	 	<tbody>
	 	
       	<c:forEach items="#{discenteNosProjetos}" var="dm" varStatus="status">
               <tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">

                    <td> ${dm.projetoEnsino.anoTitulo}</td>
                    <td> ${dm.tipoVinculo.descricao}</td>
                    <td> ${dm.situacaoDiscenteMonitoria.descricao} </td>

					<td width="2%">	
							<h:commandLink  title="Ver"  action="#{projetoMonitoria.view}" style="border: 0;" id="visualizar_projetos_">
								      <f:param name="id" value="#{dm.projetoEnsino.id}"/>
								      <h:graphicImage url="/img/view.gif" />
							</h:commandLink>							
					</td>
										
					<td width="2%">
							<h:commandLink title="Visualizar monitoria" action="#{ consultarMonitor.view }" id="visualizar_monitoria_">
							      <f:param name="id" value="#{dm.id}"/>
							      <h:graphicImage url="/img/monitoria/user1_view.png" />
							</h:commandLink>
					</td>
					<td width="2%">
							<c:if test="${dm.assumiuMonitoria or dm.convocado}">							
								<h:commandLink  title="Cadastrar Dados Bancários" action="#{ discenteMonitoria.cadastrarDadosBancarios }" id="cadastrar_dados_bancarios_">							
								      <f:param name="idDiscenteMonitoria" value="#{dm.id}"/>
								      <h:graphicImage url="/img/seta.gif" />
								</h:commandLink>
							</c:if>						
					</td>							
					
					<td width="2%">
							<c:if test="${dm.convocado}">														
								<h:commandLink  title="Aceitar ou recusar monitoria" action="#{ discenteMonitoria.iniciarAceitarOuRecusarMonitoria }" id="Aceitar_ou_recusar_monitoria_">							
								      <f:param name="idDiscenteMonitoria" value="#{dm.id}"/>
								      <h:graphicImage url="/img/monitoria/businessman_preferences.png" />
								</h:commandLink>
							</c:if>						
					</td>							
					

					<td width="2%">
							<c:if test="${dm.assumiuMonitoria && monitoria.frequenciaMonitoria}">															
								<h:commandLink  title="Cadastrar Nova Frequência" action="#{ atividadeMonitor.iniciarCadastroAtividade }" id="cadastrar_frequencia_">							
								      <f:param name="idDiscente" value="#{dm.id}"/>
								      <h:graphicImage url="/img/monitoria/document_new.png" />
								</h:commandLink>
							</c:if>						
					</td>
					
              </tr>
          </c:forEach>
	 	</tbody>
	 </table>
	 </h:form>

	</c:if>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>