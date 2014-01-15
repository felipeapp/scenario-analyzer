<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%> 
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<script type="text/javascript" src="/shared/loadScript?src=javascript/jquery.tablesorter.min.js"></script>
<link rel="stylesheet" type="text/css" href="/shared/css/tablesorter/style.css" />

<f:view>
	<%@include file="/portais/docente/menu_docente.jsp"%>

	<h2><ufrn:subSistema /> > Relatórios de Atividade do Monitor</h2>

	<h:outputText value="#{atividadeMonitor.create}"/>
	<h:outputText  value="#{avisoProjeto.create}"/>

	<div class="infoAltRem">
	    <h:graphicImage value="/img/view.gif" 	style="overflow: visible;"/>: Visualizar Relatório			    	    
	    <html:img page="/img/email_go.png" style="overflow: visible;"/>: Enviar Mensagem		    
	    <h:graphicImage value="/img/seta.gif" 	style="overflow: visible;"/>: Validar Relatório	
	</div>

	<h:form id="form">
		<c:choose>
			<c:when test="${not empty atividadeMonitor.atividades}">
				<h:panelGrid columns="1" width="100%">
					<c:set var="idProjeto" value="0" />
					<c:forEach items="#{atividadeMonitor.atividades}" var="atividade">
						<c:if test="${idProjeto != atividade.discenteMonitoria.projetoEnsino.projeto.id}">
							<c:set var="idProjeto" value="${atividade.discenteMonitoria.projetoEnsino.projeto.id}" />
							<rich:simpleTogglePanel switchType="client" label="#{atividade.discenteMonitoria.projetoEnsino.anoTitulo}" height="150px" opened="true">
			                    <table width="100%" class="listagem">
		                    		<thead>
										<tr>
											<th>Matrícula</th>
											<th>Discente</th>
											<th>Mês/Ano</th>
											<th>Validado Orientador</th>
											<th>Analisado Em</th>
											<th></th>
											<th></th>
											<th></th>
										</tr>
									</thead>
				                    <c:forEach items="#{atividadeMonitor.atividades}" var="atv" varStatus="status">
				                    	<c:if test="${atv.discenteMonitoria.projetoEnsino.projeto.id == idProjeto}">
				                    		<tr>
												  <td> ${atv.discenteMonitoria.discente.matricula}</td>			
												  <td> ${atv.discenteMonitoria.discente.nome}</td>
												  <td> <fmt:formatNumber value="${atv.mes}" pattern="00" />/${ atv.ano } </td>
												  <td> ${ atv.validadoOrientador == true ? 'SIM': (atv.validadoOrientador == false ? 'NÃO' : '-') } </td>												  
												  <td> <fmt:formatDate pattern="dd/MM/yyyy HH:mm" value="${atv.dataValidacaoOrientador}"/> </td>
										
												  <td width="2%">
													  <h:commandLink  title="Visualizar Relatório de Atividades" action="#{atividadeMonitor.visualizarRelatorioMonitor}" style="border: 0;">
														  <f:param name="id" value="#{atv.id}"/>
													  <h:graphicImage url="/img/view.gif" />
													  </h:commandLink>								
												  </td>
												  <td width="2%">
													  <h:commandLink  title="Enviar Mensagem" action="#{avisoProjeto.enviarMensagem}" style="border: 0;">
														  <f:param name="idDiscente" value="#{atv.discenteMonitoria.discente.id}"/>
													  <h:graphicImage url="/img/email_go.png" />
													  </h:commandLink>								
												  </td>
										
												  <td width="2%">
													<c:if test="${empty atv.dataValidacaoOrientador}">   
														<h:commandLink id="btAnalisar"  title="Analisar" action="#{atividadeMonitor.avaliarRelatorioAtividade}" style="border: 0;">
														      <f:param name="id" value="#{atv.id}"/>
													      <h:graphicImage url="/img/seta.gif" />
														</h:commandLink>
													</c:if>
												  </td>
										    </tr>
				                    	</c:if>
				                    </c:forEach>
			                    </table>
	           		 		</rich:simpleTogglePanel>
           		 		</c:if>
					</c:forEach>
				</h:panelGrid>	
			</c:when>
			<c:otherwise>
				Não existem Atividades.
			</c:otherwise>
		</c:choose>
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>