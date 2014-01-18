<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<%@page import="br.ufrn.sigaa.monitoria.dominio.StatusRelatorio"%>
<f:view>
	

	<%@include file="/portais/docente/menu_docente.jsp" %>
	
	
	<h:outputText  value="#{projetoMonitoria.create}"/>
	<h:outputText  value="#{autorizacaoReconsideracao.create}"/>
	
	<h2><ufrn:subSistema /> > Resumos Enviados</h2>

	<div class="descricaoOperacao">
		<font color="red">Atenção:</font><br/>
		Os resumos só poderão ser alterados ou excluídos enquanto estiverem 
		'AGUARDANDO DISTRIBUIÇÃO', 'AGUARDANDO AVALIAÇÃO' ou 'AVALIADO COM RESSALVAS'.<br/>
		Cada resumo poderá ter até 3 bolsistas vinculados.
		Para este projeto é permitido submeter no máximo  
		<h:outputText value="#{resumoSid.qntMaximaResumoSid}" /> resumo(s).
	</div>

	<br/>

	<div class="infoAltRem">

    	<h:graphicImage value="/img/monitoria/document_view.png" style="overflow: visible;"/>: Visualizar Resumo

		<c:if test="${acesso.coordenadorMonitoria}">			
			    <h:graphicImage value="/img/view.gif" 		style="overflow: visible;" />: Visualizar Avaliações			    
			    <h:graphicImage value="/img/alterar.gif"	style="overflow: visible;" />: Alterar Resumo
			    <h:graphicImage value="/img/delete.gif" 	style="overflow: visible;" />: Remover Resumo
	    </c:if>
	    
	</div>

	<h:form>

	<table class="listagem">
		<caption class="listagem">
		Lista de Resumos Enviados para o projeto selecionado
		</caption>
		<thead>
			<tr>
				<th>Enviado em</th>				
				<th><span title="Ano de realização do Seminário de iniciação à Docência">Ano SID</span></th>
				<th>Situação do resumo</th>
				<th></th>
				<th></th>				
			</tr>
		</thead>
		

	<c:set var="AGUARDANDO_DISTRIBUICAO" value="<%= String.valueOf(StatusRelatorio.AGUARDANDO_DISTRIBUICAO) %>" scope="application"/>	 	
	<c:set var="AVALIADO_COM_RESSALVAS" value="<%= String.valueOf(StatusRelatorio.AVALIADO_COM_RESSALVAS) %>" scope="application"/>	 		
	<c:set var="AVALIADO_SEM_RESSALVAS" value="<%= String.valueOf(StatusRelatorio.AVALIADO) %>" scope="application"/>	 			
	<c:set var="AGUARDANDO_AVALIACAO" value="<%= String.valueOf(StatusRelatorio.AGUARDANDO_AVALIACAO) %>" scope="application"/>	 			

		
	<c:set var="resumos"  value="#{resumoSid.resumos}"/>
		
			<tr>
				<td colspan="5" style="background: #C8D5EC; font-weight: bold; padding: 2px 0 2px 5px;">
					${ resumoSid.buscaProjetoEnsino.anoTitulo }
				</td>
			</tr>
			
	<c:if test="${not empty resumos}">

		<c:forEach items="#{resumos}" var="resumo" varStatus="status">
            <tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
				<td><fmt:formatDate value="${resumo.dataEnvio}" pattern="dd/MM/yyyy HH:mm:ss"/></td>
				<td> ${resumo.anoSid} </td>				
				<td> ${resumo.status.descricao} </td>

				<td width="2%">
					<h:commandLink  title="Ver" action="#{resumoSid.view}" styleClass="noborder">
					   	<f:param name="id" value="#{resumo.id}"/>				    	
						<h:graphicImage url="/img/monitoria/document_view.png" />
					</h:commandLink>
				</td>			
				<td width="6%">
							<c:if test="${acesso.coordenadorMonitoria || acesso.monitoria}">
								
									<c:if test="${(resumo.status.id == AGUARDANDO_DISTRIBUICAO) or (resumo.status.id == AVALIADO_COM_RESSALVAS) or (resumo.status.id == AVALIADO_SEM_RESSALVAS) or (resumo.status.id == AGUARDANDO_AVALIACAO) }">				
										<h:commandLink  action="#{resumoSid.viewAvaliacoesProjeto}"  title="Visualizar Avaliações" styleClass="noborder" >
										   	<f:param name="idProjeto" value="#{resumo.projetoEnsino.id}"/>
											<h:graphicImage url="/img/view.gif" />
										</h:commandLink>
									</c:if>
									
									<c:if test="${(resumo.status.id == AGUARDANDO_DISTRIBUICAO) or (resumo.status.id == AVALIADO_COM_RESSALVAS) or (resumo.status.id == AGUARDANDO_AVALIACAO) }">
										<h:commandLink title="Alterar" action="#{resumoSid.atualizar}" styleClass="noborder" >
										   	<f:param name="id" value="#{resumo.id}"/>
											<h:graphicImage url="/img/alterar.gif"  />
										</h:commandLink>
									</c:if>
									
									<c:if test="${(resumo.status.id == AGUARDANDO_DISTRIBUICAO)}" >
										<h:commandLink title="Remover" action="#{resumoSid.preRemover}" styleClass="noborder" >
										   	<f:param name="id" value="#{resumo.id}"/>
											<h:graphicImage url="/img/delete.gif"   />
										</h:commandLink>
									</c:if>

							</c:if>
				</td>
			</tr>
		 </c:forEach>
		</c:if>

		<c:if test="${empty resumos}">
			<tr>
				<td colspan="5"><center><font color="red">Não há resumos enviados para o projeto de ensino selecionado<br/></font> </center></td>
			</tr>
		</c:if>

		<tfoot>
			<tr>
				<td colspan="5" align="center">
						<h:inputHidden value="#{resumoSid.buscaProjetoEnsino.id}"/>
						<h:commandButton value="Cadastrar Novo Resumo SID..." action="#{resumoSid.cadastrarResumoSid}"/>
				</td>
			</tr>
		</tfoot>

	</table>
</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>