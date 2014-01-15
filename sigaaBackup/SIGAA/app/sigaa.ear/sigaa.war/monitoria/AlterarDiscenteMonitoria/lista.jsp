<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>

<f:view>
	<h2><ufrn:subSistema /> > Alterar Monitoria</h2>
	<a4j:keepAlive beanName="consultarMonitor" />
    <c:if test="${acesso.monitoria}">
        <%@include file="/monitoria/ConsultarMonitor/include/buscar.jsp"%>
    </c:if>
	<c:set var="monitores" value="#{consultarMonitor.monitores}"/>
	
	<c:if test="${not empty monitores}">
		<div class="infoAltRem">
		    <h:graphicImage value="/img/monitoria/user1_view.png" style="overflow: visible;"/>: Visualizar Dados do Monitor
		    <h:graphicImage value="/img/monitoria/replace2.png" style="overflow: visible;" rendered="#{acesso.monitoria}"/><h:outputText value=": Alterar Vínculo do Monitor" rendered="#{acesso.monitoria}"/>
		    <h:graphicImage value="/img/alterar.gif" style="overflow: visible;"/><h:outputText value=": Alterar Nota e Classificação" />
		    <br/>
		    <h:graphicImage value="/img/monitoria/user1_refresh.png" style="overflow: visible;"/><h:outputText value=": Alterar Orientação"/>
		    <h:graphicImage value="/img/monitoria/user1_add.png" style="overflow: visible;" rendered="#{acesso.monitoria}"/><h:outputText value=": Reativar Monitoria" rendered="#{acesso.monitoria}"/>
		    <h:graphicImage value="/img/monitoria/user1_delete.png" style="overflow: visible;" rendered="#{acesso.monitoria}"/><h:outputText value=": Finalizar Monitoria" rendered="#{acesso.monitoria}"/>	    
		    <h:graphicImage value="/img/delete.gif" style="overflow: visible;" rendered="#{acesso.monitoria}"/><h:outputText value=": Excluir Monitoria"	rendered="#{acesso.monitoria}"/>
		</div>
	</c:if>

	<br/>

<!--	<c:if test="${empty monitores}">-->
<!--	   <center><i> Nenhum monitor localizado </i></center>-->
<!--	</c:if>-->

	<c:if test="${not empty monitores}">
	    <h:form id="form">
		   <table class="listagem">
		    <caption>Monitores Encontrados (${ fn:length(monitores) })</caption>
	
		      <thead>
		      	<tr>
		        	<th>Discente</th>
		        	<th>Vínculo</th>
		        	<th>Situação</th>
		        	<th>Início</th>
		        	<th>Fim</th>	        	
		        	<th>&nbsp;</th>
		        </tr>
		 	</thead>
		 	<tbody>
	
			<c:set var="projeto" value=""/>	 	
	       	<c:forEach items="#{monitores}" var="monitor" varStatus="status">
	
					<c:if test="${ projeto != monitor.projetoEnsino.id }">
						<c:set var="projeto" value="${ monitor.projetoEnsino.id }"/>
						<tr>
							<td colspan="6" style="background: #C8D5EC; font-weight: bold; padding: 2px 0 2px 5px;">
								${ monitor.projetoEnsino.anoTitulo }
							</td>
						</tr>					
					</c:if>
	
	               <tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
	                    <td width="50%"> ${monitor.discente.matriculaNome} </td>
	                    <td> ${monitor.tipoVinculo.descricao} </td>
						<td> 
							<c:set var="cor" value="${((monitor.assumiuMonitoria) and (monitor.ativo)) ? 'blue':'red'}"/>
							<font color="${cor}">${monitor.situacaoDiscenteMonitoria.descricao}</font> 
						</td>
						<td width="2%"> <fmt:formatDate value="${monitor.dataInicio}" pattern="dd/MM/yyyy" /> </td>
						<td width="2%"> <fmt:formatDate value="${monitor.dataFim}" pattern="dd/MM/yyyy" /> </td>
						<td width="13%">
		
								    <h:commandLink  title="Visualizar Dados do Monitor" action="#{ consultarMonitor.view }" 
								         styleClass="noborder" id="link_visualizar_dados_monitor_">
								       <f:param name="id" value="#{monitor.id}"/>
					                   <h:graphicImage url="/img/monitoria/user1_view.png" id="img_view_monitor_"/>
								    </h:commandLink>
								
									<h:commandLink title="Alterar Nota e Classificação" action="#{alterarDiscenteMonitoria.preAlterarNotas}" 
									    styleClass="noborder"  id="botao_alterar_notar_">
										<f:param name="id" value="#{monitor.id}"/>
										<h:graphicImage url="/img/alterar.gif" id="img_alterar_nota_"/>
									</h:commandLink>
								
									<h:commandLink  title="Alterar Vínculo do Monitor" action="#{alterarDiscenteMonitoria.preAlterarVinculoMonitor}"  
									    styleClass="noborder" id="botao_alterar_vinculo_" rendered="#{(acesso.monitoria) && ((monitor.assumiuMonitoria) || (monitor.aguardandoConvocacao && monitor.vinculoEmEspera))}" >
								       <f:param name="id" value="#{monitor.id}"/>
					                   <h:graphicImage url="/img/monitoria/replace2.png" id="img_alterar_vinculo_"/>
									</h:commandLink>
	
									<h:commandLink title="Alterar Orientação" action="#{alterarDiscenteMonitoria.preAlterarOrientadores}" 
									    styleClass="noborder"  id="botao_alterar_orientacao_" rendered="#{monitor.assumiuMonitoria || monitor.convocado}">
								       <f:param name="id" value="#{monitor.id}"/>
					                   <h:graphicImage url="/img/monitoria/user1_refresh.png" id="img_alterar_orientacao_"/>
									</h:commandLink>
									
									<h:commandLink title="Finalizar Monitoria" action="#{alterarDiscenteMonitoria.preFinalizarMonitoria}" 
									    styleClass="noborder" id="botao_finalizar_monitoria_" rendered="#{acesso.monitoria && monitor.assumiuMonitoria}" >				
								       <f:param name="id" value="#{monitor.id}"/>
					                   <h:graphicImage url="/img/monitoria/user1_delete.png" id="img_finalizar_monitoria_"/>
									</h:commandLink>
	
									<h:commandLink title="Reativar Monitoria" action="#{alterarDiscenteMonitoria.preReativarMonitoria}" 
									    styleClass="noborder"  id="botao_reativar_monitor_" rendered="#{acesso.monitoria && monitor.finalizado && !monitor.assumiuMonitoria}" >
								    	<f:param name="id" value="#{monitor.id}"/>
					               		<h:graphicImage url="/img/monitoria/user1_add.png" id="img_reativar_monitor_"/>
									</h:commandLink>
								
	    							<h:commandLink title="Excluir Monitor" action="#{alterarDiscenteMonitoria.preExcluirMonitoria}" 
	    							     styleClass="noborder" id="botao_excluir_monitor" rendered="#{acesso.monitoria}" >
								       <f:param name="id" value="#{monitor.id}"/>
					                   <h:graphicImage url="/img/delete.gif" id="img_excluir_monitor_"/>
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