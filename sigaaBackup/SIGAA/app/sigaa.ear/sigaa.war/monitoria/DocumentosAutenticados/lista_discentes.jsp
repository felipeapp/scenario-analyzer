<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<f:view>

<a4j:region rendered="#{ acesso.acessibilidade }">
	<script type="text/javascript" src="/shared/javascript/jquery/jquery-1.4.4.min.js"></script>
	<script src="${ctx}/javascript/jquery.acessibilidade.js" type="text/javascript" ></script>
</a4j:region>


<%@include file="/portais/docente/menu_docente.jsp"%>
<h2><ufrn:subSistema /> > Documentos autenticados de Monitoria</h2>

<a4j:keepAlive beanName="documentosAutenticadosMonitoria" />

<h:form>
	<div class="infoAltRem">
		<h:graphicImage value="/img/monitoria/businessman_view.png" style="overflow: visible;"/>: Visualizar
		<h:graphicImage value="/img/comprovante.png" height="19" width="19" style="overflow: visible;"/>: Emitir Declaração	
		<h:graphicImage value="/img/certificate.png" height="19" width="19" style="overflow: visible;"/>: Emitir Certificado			    		    
	</div>
	
	
		<table class=listagem>
				<caption class="listagem">Lista de participações do discente em Projetos de Monitoria</caption>
				<thead>
						<tr>
							<th width="60%">Projeto de Monitoria</th>
							<th>Vínculo</th>
							<th>Início</th>
							<th>Fim</th>
							<th></th>
							<th></th>							
							<th></th>							
						</tr>
				</thead>
				<tbody>
						
					<c:forEach items="#{documentosAutenticadosMonitoria.discentesMonitoria}" var="item" varStatus="status">						
			               <tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
									<td>${ item.projetoEnsino.anoTitulo }</td>
									<td>${item.tipoVinculo.descricao}</td>									
									<td><fmt:formatDate value="${item.dataInicio}" pattern="dd/MM/yyyy"/></td>
									<td><fmt:formatDate value="${item.dataFim}" pattern="dd/MM/yyyy"/></td>

									<td width="2%">								               
										<h:commandLink action="#{consultarMonitor.view}" style="border: 0;" id="visualizarMonitor">
										   <f:param name="id" value="#{item.id}"/>
								           <h:graphicImage url="/img/monitoria/businessman_view.png" alt="Visualizar" />
										</h:commandLink>
									</td>	
									
									
									<td width="2%">
										<h:commandLink title="Emitir Declaração" action="#{declaracaoDiscenteMonitoria.emitirDeclaracao}" immediate="true" id="btEmitirDeclaracao"	rendered="#{ item.vigente }">
											<f:param name="id" value="#{item.id}"/>
					                   		<h:graphicImage url="/img/comprovante.png" height="19" width="19" alt="Emitir Declaração"/>
										</h:commandLink>
									</td>			
									
									
									<td width="2%">
										<h:commandLink title="Emitir Certificado" action="#{certificadoMonitor.emitirCertificado}" immediate="true" id="btEmitirCertificado" 
											rendered="#{item.recebeCertificado}">
											<f:param name="id" value="#{item.id}"/>
					                   		<h:graphicImage url="/img/certificate.png" height="19" width="19" alt="Emitir Certificado"/>
										</h:commandLink>
									</td>									
							</tr>
			 		   </c:forEach>
			 		   
			 		   <c:if test="${empty documentosAutenticadosMonitoria.discentesMonitoria}" >
			 		   		<tr><td colspan="6" align="center"><font color="red">Usuário atual não possui certificados pendentes de impressão.</font></td></tr>
			 		   </c:if>
			 		   
					</tbody>	
		</table>
		
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>