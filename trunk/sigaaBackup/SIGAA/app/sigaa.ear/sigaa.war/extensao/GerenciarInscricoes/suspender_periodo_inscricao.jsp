<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	
	<a4j:keepAlive beanName="gerenciarInscricoesCursosEventosExtensaoMBean" />
	
	<h2><ufrn:subSistema /> &gt; Suspender Inscrição</h2>
	
	<div class="descricaoOperacao">
		<p> Caro Coordenador(a), </p>
		<p> Essa opção permite suspender um período de inscrição antes do seu término.</p>
		<br/>
		<p> Ao suspender o período de inscrição novas inscrições não poderão ser realizadas e <strong>todas</strong> as inscrições não aprovadas serão canceladas.</p>
		<br/>
		<br/>
		<p>
		</p>
	</div>	
	
	<h:form id="formSuspenderPeriodoInscricao">
	
	<c:set var="participantes" value="#{gerenciarInscricoesCursosEventosExtensaoMBean.obj.participantesInscritos}" />
		<table class="visualizacao" width="90%">
			<caption>Informações sobre o Período de Inscrição</caption>
			<tbody>
				<tr>
					<td colspan="2" class="subFormulario">Dados da Inscrição</td>
				</tr>
				
				<tr>
					<th width="16%">Período:</th>
					<td>
						<h:outputText value="#{gerenciarInscricoesCursosEventosExtensaoMBean.obj.periodoInicio}">
							<f:convertDateTime pattern="dd/MM/yyyy" />
						</h:outputText><i> até </i>
						<h:outputText value="#{gerenciarInscricoesCursosEventosExtensaoMBean.obj.periodoFim}">
							<f:convertDateTime pattern="dd/MM/yyyy" />
						</h:outputText>
					</td>
				</tr>
				
				<tr>
					<th>Quantidade de Vagas:</th>
					<td>${gerenciarInscricoesCursosEventosExtensaoMBean.obj.quantidadeVagas}</td>
				</tr>
				
				<tr>
					<td colspan="2" class="subFormulario">Informe o motivo de suspensão do período do inscrição</td>
				</tr>

				<tr>
					<th>Motivo:
						<ufrn:help>O motivo da suspensão será informado aos participantes.<br /><br /></ufrn:help>
					</th>
					<td><h:inputTextarea value="#{gerenciarInscricoesCursosEventosExtensaoMBean.obj.motivoCancelamento}" cols="100" id="motivo"
							rows="2" disabled="#{gerenciarInscricoesCursosEventosExtensaoMBean.qtdInscricoesParticipante == 0}" /></td>
				</tr>
				
				
			</tbody>
			<tfoot>
				<tr>
					<td colspan="2" style="text-align: center;">						
							<h:commandButton value="Suspender Inscrição" action="#{gerenciarInscricoesCursosEventosExtensaoMBean.suspenderPeriodoInscricao}" id="suspender" />
							<h:commandButton value="Cancelar" action="#{gerenciarInscricoesCursosEventosExtensaoMBean.telaListaInscricoesAtividade}" id="cancelar"  onclick="#{confirm}" immediate="true" />
					</td>
				</tr>
			</tfoot>
			
		</table>
		
		
		<table class="formulario" style="margin-top: 20px;">
				<tr>
					<td colspan="2" class="subFormulario">Pessoas inscritas ( ${gerenciarInscricoesCursosEventosExtensaoMBean.qtdInscricoesParticipante} ) ainda não aprovadas nesse Período de Inscrição </td>
				</tr>
				<tr>
					<td colspan="2">
					<c:choose>
						<c:when test="${gerenciarInscricoesCursosEventosExtensaoMBean.qtdInscricoesParticipante > 0 }">
							<table class="listagem" style="width: 100%;">
								<thead>
									<tr>
										<th style="text-align: left">Nome</th>
										<th style="text-align: left">Email</th>									
									</tr>
								</thead>
								<tbody>
									<c:forEach var="participante" items="#{gerenciarInscricoesCursosEventosExtensaoMBean.inscricoesParticipante}" varStatus="count">
									<tr class="${ count.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
										<td>${participante.cadastroParticipante.nome}</td>
										<td>${participante.cadastroParticipante.email}</td>
									</tr>
									</c:forEach>
								</tbody>
							</table>
						</c:when>
						<c:otherwise>
							<div style="color:red; text-align: center; width: 100%;">Não existem participantes inscritos</div>
						</c:otherwise>
					</c:choose>
					</td>
				</tr>
		</table>
		
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>