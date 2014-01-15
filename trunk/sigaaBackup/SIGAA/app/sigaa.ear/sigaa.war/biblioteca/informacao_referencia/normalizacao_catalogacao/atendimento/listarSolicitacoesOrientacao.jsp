<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>
<%@page import="br.ufrn.sigaa.arq.util.CheckRoleUtil"%>

<f:view>

<h2><ufrn:subSistema/> &gt; Solicita��es de orienta��o de normaliza��o &gt; Solicita��es Realizadas</h2>

<div class="descricaoOperacao">
	<p>Abaixo podem ser visualizadas as solicita��es de orienta��o de normaliza��o. Estas
		solicita��es podem estar em 4 situa��es: </p>
	<ul>
		<li><strong>Solicitada</strong>: Indica que o usu�rio solicitou o agendamento de orienta��o de normaliza��o mas ainda 
		n�o foi atendido por um bibliotec�rio.</li>
		<li><strong>Atendida</strong>: Indica que o bibliotec�rio atendeu a solicita��o mas ainda n�o houve resposta do usu�rio 
		confirmando ou n�o o comparecimento.</li>
		<li><strong>Confirmada</strong>: Indica que o usu�rio aprovou o hor�rio definido pelo bibliotec�rio e confirmou o comparecimento.</li>
		<li><strong>Cancelada</strong>: Indica que usu�rio ou bibliotec�rio cancelou a solicita��o por algum motivo.</li>
	</ul>
</div>

<a4j:keepAlive beanName="solicitacaoOrientacaoMBean"></a4j:keepAlive>

<h:form id="formBuscaSolicitacoes">

	<table class="formulario" style="width: 80%">
		
		<caption>Filtrar Solicita��es</caption>
		
		<tr>
		
			<td width="2%">
				<h:selectBooleanCheckbox value="#{solicitacaoOrientacaoMBean.buscarNumeroSolicitacao}" styleClass="noborder" id="checkNumeroSolicitacao"/>
			</td>	
		
			<th style="text-align: left;">N�mero da Solicita��o:</th>
			
			<td colspan="3">
				<h:inputText id="inputTextNumeroSolicitacao" label="N�mero da Solicita��o" value="#{solicitacaoOrientacaoMBean.numeroSolicitacao}" size="10"  maxlength="9" 
						onfocus="getEl('formBuscaSolicitacoes:checkNumeroSolicitacao').dom.checked = true;" 
						onkeyup="return formatarInteiro(this);"/>
						<%-- Aqui o  marcarCheckBox n�o pode ficar no onchange porque no IE a fun��o formatarInteiro anula a execu��o --%> 
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
		
			<th style="text-align: left;">Data da Solicita��o:</th>
	
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
			
			<td colspan="4"><span style="white-space: nowrap;">Buscar Solicita��es</span> Atendidas</td>
		</tr>
		
		<tr>
			<th>
				<h:selectBooleanCheckbox value="#{solicitacaoOrientacaoMBean.buscarConfirmadas}" id="buscarConfirmadas"/>
			</th>
			
			<td colspan="4"><span style="white-space: nowrap;">Buscar Solicita��es</span> Confirmadas</td>
		</tr>
		
		<tr>
			<th>
				<h:selectBooleanCheckbox value="#{solicitacaoOrientacaoMBean.buscarCanceladas}" id="buscarCanceladas" />
			</th>
			
			<td colspan="4"><span style="white-space: nowrap;">Buscar Solicita��es</span> Canceladas</td>
		</tr>
		
		<tr>
			<th>
				<h:selectBooleanCheckbox value="#{solicitacaoOrientacaoMBean.buscarRemovidasPeloUsuario}" id="buscarRemovidas" />
			</th>
			
			<td colspan="4"><span style="white-space: nowrap;">Buscar Solicita��es</span> Removidas pelo Usu�rio</td>
		</tr>
		
		<tfoot>
			<tr>
				<td colspan="5">
					<h:commandButton action="#{solicitacaoOrientacaoMBean.buscarSolicitacoesSolicitadas}" value="Buscar Solicita��es" id="buscarSolicitacoes"/>
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
				<h:graphicImage value="/img/view.gif" style="overflow: visible;"/>: Visualizar Solicita��o
		        <h:graphicImage value="/img/refresh.png" style="overflow: visible;"/>: Transferir Solicita��o
		        <h:graphicImage value="/img/notificar.png" style="overflow: visible;"/>: Notificar sobre Solicita��o
		        <h:graphicImage value="/img/seta.gif" style="overflow: visible;"/>: Atender Solicita��o
		        <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Cancelar Solicita��o
		</div>
		
		<table class="listagem">
			<caption>Lista de solicita��es de Orienta��o de Normaliza��o ( ${fn:length(solicitacaoOrientacaoMBean.solicitacoes)} ) </caption>
			
				<thead>
					<tr>
						<th>N�mero</th>
						<th>Solicitante</th>
						<th>Biblioteca</th>
						<th>Hor�rio agendado</th>
						<th>Data Solicita��o</th>
						<th>Situa��o</th>
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
							<td colspan="5" style="font-style: italic;"> Removida pelo Usu�rio </td>
						</c:if>
						
						<c:if test="${solicitacao.ativa}">
						
							<td>
								<h:commandLink action="#{solicitacaoOrientacaoMBean.visualizarDadosSolicitacaoAtendimento}" rendered="#{solicitacao.ativa}">
									<h:graphicImage id="gphicimgVisualizarSolicitacao" url="/img/view.gif" style="border:none" title="Visualizar Solicita��o" />
									<f:param name="idSolicitacao" value="#{solicitacao.id}"/>		
								</h:commandLink>
							</td>
							
							
							<td>
								<%-- O bibliotec�rio de setor de cataloga��o pode confirmar as solicita��es porque � ele quem gera a ficha catalogr�fica --%>
								
								<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL, 
										SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO
										, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO} %>">
									<c:if test="${solicitacao.solicitado && solicitacao.ativa}">
										<h:commandLink action="#{solicitacaoOrientacaoMBean.preTransferirSolicitacao}">
											<h:graphicImage id="gphicimgTransferirSolicitacao" url="/img/refresh.png" style="border:none"
												title="Transferir Solicita��o" />
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
												title="Notificar sobre Solicita��o" />
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
												title="Atender Solicita��o" />
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
													title="Cancelar Solicita��o" />
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
				<tr><td style="text-align:center;color:#FF0000;font-weight:bold">N�o h� solicita��es a serem exibidas.</td></tr>
			</c:if>
			
		</table>
			
		</c:if>
			
	
	</h:form>
	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>