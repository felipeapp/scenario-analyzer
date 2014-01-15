<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>

<f:view>
	<%@page import="br.ufrn.sigaa.monitoria.dominio.SituacaoDiscenteMonitoria"%>
	<a4j:keepAlive beanName="certificadoMonitor"></a4j:keepAlive>
	<h2><ufrn:subSistema /> > Consultar Monitor / Emitir Certificado</h2>

	<h:messages />
	<div class="descricaoOperacao">
		<font color="red">Atenção:</font> 
		Somente discentes que enviaram o Relatório Final ou de Desligamento e tiveram os relatórios<br/> 
		validados pela Coordenação do projeto e pela PROGRAD tem o certificado de participação disponível para impressão.<br>
	</div>	

    <%@include file="/monitoria/ConsultarMonitor/include/buscar.jsp"%>	

	<c:if test="${not empty monitores}">
		<div class="infoAltRem">
		    <h:graphicImage value="/img/monitoria/user1_view.png" style="overflow: visible;"/>: Visualizar Dados do Monitor
	   	    <c:if test="${acesso.monitoria}">
			    <h:graphicImage url="/img/certificate.png" height="19" width="19" style="overflow: visible;"/>: Emitir Certificado
			    <h:graphicImage value="/img/comprovante.png" height="19" width="19" style="overflow: visible;"/>: Emitir Declaração
	   	    </c:if>
		</div>
	
		<br/>
	</c:if>

	<c:if test="${empty monitores}">
    	<center><i> Nenhum monitor localizado </i></center>
	</c:if>

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
		        	<th>&nbsp;</th>
		        	<th>&nbsp;</th>
		        	
		        </tr>
		 	</thead>
		 	<tbody>
		 	<c:set var="ASSUMIU_MONITORIA" value="<%= String.valueOf(SituacaoDiscenteMonitoria.ASSUMIU_MONITORIA) %>" scope="application"/>	 		 	
		 	
		 	
		 	<c:set var="projeto" value=""/>	 	
		 	
	       	<c:forEach items="#{monitores}" var="monitor" varStatus="status">
	
		       			<c:if test="${ projeto != monitor.projetoEnsino.id }">
							<c:set var="projeto" value="${ monitor.projetoEnsino.id }"/>
							<tr>
									<td colspan="8" style="background: #C8D5EC; font-weight: bold; padding: 2px 0 2px 5px;">
										${monitor.projetoEnsino.anoTitulo}
									</td>
							</tr>						
						</c:if>
	
	               <tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
	                    
	                    <td> ${monitor.discente.matriculaNome} </td>
	                    <td> ${monitor.tipoVinculo.descricao} </td>
						<td> 
							<c:set var="cor" value="${((monitor.situacaoDiscenteMonitoria.id == ASSUMIU_MONITORIA) and (monitor.ativo)) ? 'blue':'red'}"/>
							<font color="${cor}">${monitor.situacaoDiscenteMonitoria.descricao}</font> 
						</td>										
						<td width="2%"> <fmt:formatDate value="${monitor.dataInicio}" pattern="dd/MM/yyyy" /> </td>
						<td width="2%"> <fmt:formatDate value="${monitor.dataFim}" pattern="dd/MM/yyyy" /> </td>
						
						<td width="2%">
							<h:commandLink title="Visualizar Monitor" action="#{ consultarMonitor.view }">
							      <f:param name="id" value="#{monitor.id}"/>
							      <h:graphicImage url="/img/monitoria/user1_view.png" />
							</h:commandLink>
						</td>
						
						<td width="2%">
								<h:commandLink title="Emitir certificado" action="#{certificadoMonitor.emitirCertificado}"  id="botaoEmitirCertificado" 
								rendered="#{(monitor.recebeCertificado) && ((monitor.projetoEnsino.coordenacao.id == consultarMonitor.servidorUsuario.id) || (acesso.monitoria))}">
									<f:param name="id" value="#{monitor.id}"/>
					         		<h:graphicImage url="/img/certificate.png" height="16" width="16" id="img1"/>
								</h:commandLink>					
						</td>
						
						<td width="2%">		
								<h:commandLink title="Emitir Declaração" action="#{declaracaoDiscenteMonitoria.emitirDeclaracao}"  id="botaoEmitirDeclaracao" 
								rendered="#{(monitor.vigente) && ((monitor.projetoEnsino.coordenacao.id == consultarMonitor.servidorUsuario.id) || (acesso.monitoria))}">
									<f:param name="id" value="#{monitor.id}"/>
					        		<h:graphicImage url="/img/comprovante.png" height="16" width="16" id="img2"/>
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