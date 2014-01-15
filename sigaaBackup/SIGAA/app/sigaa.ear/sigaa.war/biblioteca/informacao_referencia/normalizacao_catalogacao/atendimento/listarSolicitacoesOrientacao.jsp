<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>
<%@page import="br.ufrn.sigaa.arq.util.CheckRoleUtil"%>

<f:view>

<h2><ufrn:subSistema/> &gt; Solicitações de orientação de normalização &gt; Solicitações Realizadas</h2>

<div class="descricaoOperacao">
	<p>Abaixo podem ser visualizadas as solicitações de orientação de normalização. Estas
		solicitações podem estar em 4 situações: </p>
	<ul>
		<li><strong>Solicitada</strong>: Indica que o usuário solicitou o agendamento de orientação de normalização mas ainda 
		não foi atendido por um bibliotecário.</li>
		<li><strong>Atendida</strong>: Indica que o bibliotecário atendeu a solicitação mas ainda não houve resposta do usuário 
		confirmando ou não o comparecimento.</li>
		<li><strong>Confirmada</strong>: Indica que o usuário aprovou o horário definido pelo bibliotecário e confirmou o comparecimento.</li>
		<li><strong>Cancelada</strong>: Indica que usuário ou bibliotecário cancelou a solicitação por algum motivo.</li>
	</ul>
</div>

<a4j:keepAlive beanName="solicitacaoOrientacaoMBean"></a4j:keepAlive>

<h:form id="formBuscaSolicitacoes">

	<table class="formulario" style="width: 80%">
		
		<caption>Filtrar Solicitações</caption>
		
		<tr>
		
			<td width="2%">
				<h:selectBooleanCheckbox value="#{solicitacaoOrientacaoMBean.buscarNumeroSolicitacao}" styleClass="noborder" id="checkNumeroSolicitacao"/>
			</td>	
		
			<th style="text-align: left;">Número da Solicitação:</th>
			
			<td colspan="3">
				<h:inputText id="inputTextNumeroSolicitacao" label="Número da Solicitação" value="#{solicitacaoOrientacaoMBean.numeroSolicitacao}" size="10"  maxlength="9" 
						onfocus="getEl('formBuscaSolicitacoes:checkNumeroSolicitacao').dom.checked = true;" 
						onkeyup="return formatarInteiro(this);"/>
						<%-- Aqui o  marcarCheckBox não pode ficar no onchange porque no IE a função formatarInteiro anula a execução --%> 
			</td>
		</tr>
		
		<tr>
		
			<td width="2%"> </td>
			
			<th style="text-align: left;">Biblioteca:  <span class="obrigatorio"></span> </th>
			
			<td colspan="3" style="width: 65%;">
				<h:selectOneMenu
						id="comboBiblioteca" value="#{solicitacaoOrientacaoMBean.biblioteca.id}"
						onfocus="getEl('formBuscaSolicitacoes:checkBiblioteca').dom.checked = true;">
					<ufrn:checkRole papel="<%= SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL %>">
						<f:selectItem
								itemValue="#{ solicitacaoOrientacaoMBean.todasBibliotecas }"
								itemLabel="Todas" />
					</ufrn:checkRole>
					<f:selectItems value="#{solicitacaoOrientacaoMBean.bibliotecasCombo}"/>
				</h:selectOneMenu>
			</td>
		</tr>
		
		<tr>
			<td width="2%"> 
				<h:selectBooleanCheckbox value="#{solicitacaoOrientacaoMBean.buscarData}" styleClass="noborder" id="checkData"/>
			</td>
		
			<th style="text-align: left;">Data da Solicitação:</th>
	
			<td colspan="3">
				<t:inputCalendar value="#{solicitacaoOrientacaoMBean.dataInicial}" id="DataDeCriacaoInicial" size="10" maxlength="10"
						onkeypress="return(formatarMascara(this,event,'##/##/####'))" popupDateFormat="dd/MM/yyyy"
						renderAsPopup="true" renderPopupButtonAsImage="true" 
						popupTodayString="Hoje:" popupWeekString="Semana" onchange="javascript:$('formBuscaSolicitacoes:checkData').checked = true;">
				</t:inputCalendar>	
				a
				<t:inputCalendar value="#{solicitacaoOrientacaoMBean.dataFinal}" id="DataDeCriacaoFinal" size="10" maxlength="10"
						onkeypress="return(formatarMascara(this,event,'##/##/####'))" popupDateFormat="dd/MM/yyyy"
						renderAsPopup="true" renderPopupButtonAsImage="true" 
						popupTodayString="Hoje:" popupWeekString="Semana" onchange="javascript:$('formBuscaSolicitacoes:checkData').checked = true;">	
				</t:inputCalendar>
			</td>
		</tr>
		
		<tr>
		
			<td width="2%">
				<h:selectBooleanCheckbox value="#{solicitacaoOrientacaoMBean.buscarNomeUsuarioSolicitante}" styleClass="noborder" id="checkNomeSolicitante"/>
			</td>	
		
			<th style="text-align: left;">Nome do Solicitante:</th>
			
			<td colspan="3">
				<h:inputText id="inputTextNomeSolicitante" value="#{solicitacaoOrientacaoMBean.nomeUsuarioSolicitante}" size="60"  maxlength="100" 
						onfocus="getEl('formBuscaSolicitacoes:checkNomeSolicitante').dom.checked = true;" />
			</td>
		</tr>
		
		<tr>
			<th>
				<h:selectBooleanCheckbox value="#{solicitacaoOrientacaoMBean.buscarAtendidas}" id="buscarAtendidas"/>
			</th>
			
			<td colspan="4"><span style="white-space: nowrap;">Buscar Solicitações</span> Atendidas</td>
		</tr>
		
		<tr>
			<th>
				<h:selectBooleanCheckbox value="#{solicitacaoOrientacaoMBean.buscarConfirmadas}" id="buscarConfirmadas"/>
			</th>
			
			<td colspan="4"><span style="white-space: nowrap;">Buscar Solicitações</span> Confirmadas</td>
		</tr>
		
		<tr>
			<th>
				<h:selectBooleanCheckbox value="#{solicitacaoOrientacaoMBean.buscarCanceladas}" id="buscarCanceladas" />
			</th>
			
			<td colspan="4"><span style="white-space: nowrap;">Buscar Solicitações</span> Canceladas</td>
		</tr>
		
		<tr>
			<th>
				<h:selectBooleanCheckbox value="#{solicitacaoOrientacaoMBean.buscarRemovidasPeloUsuario}" id="buscarRemovidas" />
			</th>
			
			<td colspan="4"><span style="white-space: nowrap;">Buscar Solicitações</span> Removidas pelo Usuário</td>
		</tr>
		
		<tfoot>
			<tr>
				<td colspan="5">
					<h:commandButton action="#{solicitacaoOrientacaoMBean.buscarSolicitacoesSolicitadas}" value="Buscar Solicitações" id="buscarSolicitacoes"/>
					<h:commandButton action="#{solicitacaoOrientacaoMBean.limparResultadosBusca}" value="Limpar" id="limpar" />
					<h:commandButton action="#{solicitacaoOrientacaoMBean.cancelar}" onclick="#{confirm}" value="Cancelar" immediate="true" id="cancelar"/>
				</td>
			</tr>
		</tfoot>
	</table>


	<%@include file="/WEB-INF/jsp/include/_campos_obrigatorios.jsp"%>
<br/>

		<%-- Resultados da Pesquisa--%>

		<c:if test="${not empty solicitacaoOrientacaoMBean.solicitacoes}">
		
		<div class="infoAltRem">
				<h:graphicImage value="/img/view.gif" style="overflow: visible;"/>: Visualizar Solicitação
		        <h:graphicImage value="/img/refresh.png" style="overflow: visible;"/>: Transferir Solicitação
		        <h:graphicImage value="/img/notificar.png" style="overflow: visible;"/>: Notificar sobre Solicitação
		        <h:graphicImage value="/img/seta.gif" style="overflow: visible;"/>: Atender Solicitação
		        <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Cancelar Solicitação
		</div>
		
		<table class="listagem">
			<caption>Lista de solicitações de Orientação de Normalização ( ${fn:length(solicitacaoOrientacaoMBean.solicitacoes)} ) </caption>
			
				<thead>
					<tr>
						<th>Número</th>
						<th>Solicitante</th>
						<th>Biblioteca</th>
						<th>Horário agendado</th>
						<th>Data Solicitação</th>
						<th>Situação</th>
						<th style="max-width:20px;"></th>
						<th style="max-width:20px;"></th>
						<th style="max-width:20px;"></th>
						<th style="max-width:20px;"></th>
						<th style="max-width:20px;"></th>
					</tr>
				</thead>
				
				<c:forEach items="#{solicitacaoOrientacaoMBean.solicitacoes}" var="solicitacao" varStatus="status">
					<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
						<td>${solicitacao.numeroSolicitacao}</td>
						<td>${solicitacao.solicitante}</td>
						<td>${solicitacao.biblioteca.descricao}</td>
						<td>${solicitacao.confirmado ? solicitacao.descricaoHorarioAtendimento : 'N/A'}</td>
						<td><ufrn:format type="data" valor="${solicitacao.dataCadastro}"/></td>
						<td style="${! solicitacao.ativa ? 'text-decoration: line-through;': ''}">${solicitacao.situacao.descricao} </td>
						
						
						<c:if test="${! solicitacao.ativa}">
							<td colspan="5" style="font-style: italic;"> Removida pelo Usuário </td>
						</c:if>
						
						<c:if test="${solicitacao.ativa}">
						
							<td>
								<h:commandLink action="#{solicitacaoOrientacaoMBean.visualizarDadosSolicitacaoAtendimento}" rendered="#{solicitacao.ativa}">
									<h:graphicImage id="gphicimgVisualizarSolicitacao" url="/img/view.gif" style="border:none" title="Visualizar Solicitação" />
									<f:param name="idSolicitacao" value="#{solicitacao.id}"/>		
								</h:commandLink>
							</td>
							
							
							<td>
								<%-- O bibliotecário de setor de catalogação pode confirmar as solicitações porque é ele quem gera a ficha catalográfica --%>
								
								<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL, 
										SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO
										, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO} %>">
									<c:if test="${solicitacao.solicitado && solicitacao.ativa}">
										<h:commandLink action="#{solicitacaoOrientacaoMBean.preTransferirSolicitacao}">
											<h:graphicImage id="gphicimgTransferirSolicitacao" url="/img/refresh.png" style="border:none"
												title="Transferir Solicitação" />
											<f:param name="idSolicitacao" value="#{solicitacao.id}"/>		
										</h:commandLink>
									</c:if>
								</ufrn:checkRole>
							</td>
							
							<td>
								<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL, SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO} %>">
									<c:if test="${solicitacao.solicitado && solicitacao.ativa}">
										<h:commandLink action="#{solicitacaoOrientacaoMBean.preNotificarSolicitacao}">
											<h:graphicImage id="gphicimgNotificarSolicitacao" url="/img/notificar.png" style="border:none"
												title="Notificar sobre Solicitação" />
											<f:param name="idSolicitacao" value="#{solicitacao.id}"/>		
										</h:commandLink>
									</c:if>
								</ufrn:checkRole>
							</td>
							
							<td>
							
								<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL, 
										SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO} %>">
									<c:if test="${solicitacao.solicitado && solicitacao.ativa}">
										<h:commandLink action="#{solicitacaoOrientacaoMBean.atenderSolicitacao}">
											<h:graphicImage id="gphicimgAtenderSolicitacao" url="/img/seta.gif" style="border:none"
												title="Atender Solicitação" />
											<f:param name="idSolicitacao" value="#{solicitacao.id}"/>		
										</h:commandLink>
									</c:if>
								</ufrn:checkRole>
							</td>
							
							<td>
								<c:if test="${!solicitacao.confirmado && !solicitacao.cancelado}">
								<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL, 
										SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO} %>">
									<c:if test="${solicitacao.ativa}">
										<h:commandLink action="#{solicitacaoOrientacaoMBean.cancelarSolicitacaoAtendimento}">
											<h:graphicImage id="gphicimgCancelarSolicitacao" url="/img/delete.gif" style="border:none"
													title="Cancelar Solicitação" />
												<f:param name="idSolicitacao" value="#{solicitacao.id}"/>		
										</h:commandLink>
									</c:if>
								</ufrn:checkRole>
								</c:if>
							</td>
							
						</c:if>
						
					</tr>
				</c:forEach>
			
			<c:if test="${empty solicitacaoOrientacaoMBean.solicitacoes}">
				<tr><td style="text-align:center;color:#FF0000;font-weight:bold">Não há solicitações a serem exibidas.</td></tr>
			</c:if>
			
		</table>
			
		</c:if>
			
	
	</h:form>
	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>