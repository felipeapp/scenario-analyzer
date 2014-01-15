<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	
	<a4j:keepAlive beanName="gerenciarInscricoesCursosEventosExtensaoMBean" />
	
	<h2><ufrn:subSistema /> &gt; Suspender Inscri��o</h2>
	
	<div class="descricaoOperacao">
		<p> Caro Coordenador(a), </p>
		<p> Essa op��o permite suspender um per�odo de inscri��o antes do seu t�rmino.</p>
		<br/>
		<p> Ao suspender o per�odo de inscri��o novas inscri��es n�o poder�o ser realizadas e <strong>todas</strong> as inscri��es n�o aprovadas ser�o canceladas.</p>
		<br/>
		<br/>
		<p>
		</p>
	</div>	
	
	<h:form id="formSuspenderPeriodoInscricao">
	
	<c:set var="participantes" value="#{gerenciarInscricoesCursosEventosExtensaoMBean.obj.participantesInscritos}" />
		<table class="visualizacao" width="90%">
			<caption>Informa��es sobre o Per�odo de Inscri��o</caption>
			<tbody>
				<tr>
					<td colspan="2" class="subFormulario">Dados da Inscri��o</td>
				</tr>
				
				<tr>
					<th width="16%">Per�odo:</th>
					<td>
						<h:outputText value="#{gerenciarInscricoesCursosEventosExtensaoMBean.obj.periodoInicio}">
							<f:convertDateTime pattern="dd/MM/yyyy" />
						</h:outputText><i> at� </i>
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
					<td colspan="2" class="subFormulario">Informe o motivo de suspens�o do per�odo do inscri��o</td>
				</tr>

				<tr>
					<th>Motivo:
						<ufrn:help>O motivo da suspens�o ser� informado aos participantes.<br /><br /></ufrn:help>
					</th>
					<td><h:inputTextarea value="#{gerenciarInscricoesCursosEventosExtensaoMBean.obj.motivoCancelamento}" cols="100" id="motivo"
							rows="2" disabled="#{gerenciarInscricoesCursosEventosExtensaoMBean.qtdInscricoesParticipante == 0}" /></td>
				</tr>
				
				
			</tbody>
			<tfoot>
				<tr>
					<td colspan="2" style="text-align: center;">						
							<h:commandButton value="Suspender Inscri��o" action="#{gerenciarInscricoesCursosEventosExtensaoMBean.suspenderPeriodoInscricao}" id="suspender" />
							<h:commandButton value="Cancelar" action="#{gerenciarInscricoesCursosEventosExtensaoMBean.telaListaInscricoesAtividade}" id="cancelar"  onclick="#{confirm}" immediate="true" />
					</td>
				</tr>
			</tfoot>
			
		</table>
		
		
		<table class="formulario" style="margin-top: 20px;">
				<tr>
					<td colspan="2" class="subFormulario">Pessoas inscritas ( ${gerenciarInscricoesCursosEventosExtensaoMBean.qtdInscricoesParticipante} ) ainda n�o aprovadas nesse Per�odo de Inscri��o </td>
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
							<div style="color:red; text-align: center; width: 100%;">N�o existem participantes inscritos</div>
						</c:otherwise>
					</c:choose>
					</td>
				</tr>
		</table>
		
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>