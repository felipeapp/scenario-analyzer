<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>
<%@page import="br.ufrn.sigaa.arq.util.CheckRoleUtil"%>

<f:view>

<h2><ufrn:subSistema/> &gt; Solicitação de normalização e catalogação &gt; Solicitações Realizadas</h2>

<div class="descricaoOperacao">
	<p>Abaixo podem ser visualizadas as solicitações de normalização e catalogação na fonte realizadas. Estas
		solicitações podem estar nas seguintes situações: </p>
	<ul>
		<li><strong>Solicitada</strong>: Indica que o usuário solicitou a normalização ou a catalogação na fonte mas ainda
		nenhuma ação foi realizada.</li>
		<li><strong>Atendida</strong>: Indica que o bibliotecário atendeu a solicitação, e portanto a mesma está finalizada.</li>
		<li><strong>Cancelada</strong>: Indica que o bibliotecário cancelou a solicitação por algum motivo.</li>
		<%-- <li><b>Validada:</b> As solicitações neste estado já foram validadas por algum bibliotecário do setor de
		Informação e Referência e podem ser atendidas.</li> --%>
	</ul>
</div>

<a4j:keepAlive beanName="solicitacaoServicoDocumentoMBean"></a4j:keepAlive>

<h:form id="formBuscaSolicitacoes">

	<table class="formulario" style="width: 80%">
		<caption>Filtrar Solicitações</caption>
		
		<tr>
		
			<td width="2%">
				<h:selectBooleanCheckbox value="#{solicitacaoServicoDocumentoMBean.buscarNumeroSolicitacao}" styleClass="noborder" id="checkNumeroSolicitacao"/>
			</td>	
		
			<th style="text-align: left;">Número da Solicitação:</th>
			
			<td colspan="3">
				<h:inputText id="inputTextNumeroSolicitacao" title="Número da Solicitação" value="#{solicitacaoServicoDocumentoMBean.numeroSolicitacao}" size="10"  maxlength="9" 
						onfocus="getEl('formBuscaSolicitacoes:checkNumeroSolicitacao').dom.checked = true;" 
						onkeyup="return formatarInteiro(this);"/>
						<%-- Aqui o  marcarCheckBox não pode ficar no onchange porque no IE a função formatarInteiro anula a execução --%> 
			</td>
		</tr>
		
		<tr>
			<td width="2%"> </td>
			
			<th  style="text-align: left;">Biblioteca: <span class="obrigatorio"></span> </th>
			
			<td colspan="3">
				<h:selectOneMenu id="comboBiblioteca" value="#{solicitacaoServicoDocumentoMBean.biblioteca.id}">
					<ufrn:checkRole papel="<%= SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL %>">
						<f:selectItem
								itemValue="#{ solicitacaoServicoDocumentoMBean.todasBibliotecas }"
								itemLabel="Todas" />
					</ufrn:checkRole>
					<f:selectItems value="#{solicitacaoServicoDocumentoMBean.bibliotecasCombo}"/>
				</h:selectOneMenu>
			</td>
		</tr>
		
		<tr>
			<td width="2%">
				<h:selectBooleanCheckbox
						value="#{solicitacaoServicoDocumentoMBean.buscarTipoServico}"
						styleClass="noborder" id="checkTipoDocumento"/>
			</td>	
			
			<th style="text-align: left;">Tipo de Serviço:</th>
			
			<td colspan="3">
				<h:selectOneMenu id="comboTipoServico" value="#{solicitacaoServicoDocumentoMBean.tipoServico}"
					onfocus="getEl('formBuscaSolicitacoes:checkTipoDocumento').dom.checked = true;">
					<f:selectItem itemValue="-1" itemLabel=" -- SELECIONE --"  />
					<f:selectItems value="#{solicitacaoServicoDocumentoMBean.tiposServico}"/>
				</h:selectOneMenu>
			</td>
		</tr>
			
		<tr>
			<td width="2%">
				<h:selectBooleanCheckbox
						value="#{solicitacaoServicoDocumentoMBean.buscarTipoDocumento}"
						styleClass="noborder" id="checkTipoServico"/>
			</td>	
			
			<th style="text-align: left;">Tipo de Documento:</th>
			
			<td colspan="3">
				<h:selectOneMenu id="comboTipoObra" value="#{solicitacaoServicoDocumentoMBean.tipoDocumento}"
					onfocus="getEl('formBuscaSolicitacoes:checkTipoServico').dom.checked = true;">
					<f:selectItem itemValue="-1" itemLabel=" -- SELECIONE --"  />
					<f:selectItems value="#{tipoDocumentoNormalizacaoCatalogacaoMBean.allCombo}"/>
				</h:selectOneMenu>
			</td>
		</tr>
		
		<tr>
			<td width="2%"> 
				<h:selectBooleanCheckbox value="#{solicitacaoServicoDocumentoMBean.buscarData}" styleClass="noborder" id="checkData"/>
			</td>
			
			<th style="text-align: left;">Data da Solicitação:</th>
	
			<td>
				<t:inputCalendar value="#{solicitacaoServicoDocumentoMBean.dataInicial}" id="DataDeCriacaoInicial" size="10" maxlength="10"
						onkeypress="return(formatarMascara(this,event,'##/##/####'))" popupDateFormat="dd/MM/yyyy"
						renderAsPopup="true" renderPopupButtonAsImage="true" 
						popupTodayString="Hoje:" popupWeekString="Semana"  onchange="javascript:$('formBuscaSolicitacoes:checkData').checked = true;">
				</t:inputCalendar>		
				a
				<t:inputCalendar value="#{solicitacaoServicoDocumentoMBean.dataFinal}" id="DataDeCriacaoFinal" size="10" maxlength="10"
						onkeypress="return(formatarMascara(this,event,'##/##/####'))" popupDateFormat="dd/MM/yyyy"
						renderAsPopup="true" renderPopupButtonAsImage="true" 
						popupTodayString="Hoje:" popupWeekString="Semana"   onchange="javascript:$('formBuscaSolicitacoes:checkData').checked = true;">	
				</t:inputCalendar>
			</td>
		</tr>
		
		<tr>
		
			<td width="2%">
				<h:selectBooleanCheckbox value="#{solicitacaoServicoDocumentoMBean.buscarNomeUsuarioSolicitante}" styleClass="noborder" id="checkNomeSolicitante"/>
			</td>	
		
			<th style="text-align: left;">Nome do Solicitante:</th>
			
			<td colspan="3">
				<h:inputText id="inputTextNomeSolicitante" value="#{solicitacaoServicoDocumentoMBean.nomeUsuarioSolicitante}" size="60"  maxlength="100" 
						onfocus="getEl('formBuscaSolicitacoes:checkNomeSolicitante').dom.checked = true;" />
			</td>
		</tr>
		
		
		<tr>
			<th width="2%">
				<h:selectBooleanCheckbox value="#{solicitacaoServicoDocumentoMBean.buscarAtendidas}" id="buscarAtendidas" />
			</th>
			
			<td colspan="4">Buscar Solicitações Atendidas</td>
		</tr>
		
		<tr>
			<th width="2%">
				<h:selectBooleanCheckbox value="#{solicitacaoServicoDocumentoMBean.buscarCanceladas}" id="buscarCanceladas" />
			</th>
			
			<td colspan="4">Buscar Solicitações Canceladas</td>
		</tr>
		
		<tr>
			<th width="2%">
				<h:selectBooleanCheckbox value="#{solicitacaoServicoDocumentoMBean.buscarRemovidasPeloUsuario}" id="buscarRemovidas" />
			</th>
			
			<td colspan="4">Buscar Solicitações Removidas pelo Usuário</td>
		</tr>
		
		<tfoot>
			<tr>
				<td colspan="5">
					<h:commandButton action="#{solicitacaoServicoDocumentoMBean.buscarSolicitacoesSolicitadas}" value="Buscar Solicitações" id="buscarSolicitacoes"/>
					<h:commandButton action="#{solicitacaoServicoDocumentoMBean.limparResultadosBusca}" value="Limpar" id="limpar" />
					<h:commandButton action="#{solicitacaoServicoDocumentoMBean.cancelar}" onclick="#{confirm}" value="Cancelar" immediate="true" id="cancelar"/>
				</td>
			</tr>
		</tfoot>
	</table>


	<%@include file="/WEB-INF/jsp/include/_campos_obrigatorios.jsp"%>

<br/>

		<%-- Resultados da Pesquisa--%>

		<c:if test="${not empty solicitacaoServicoDocumentoMBean.solicitacoes}">
		
		<div class="infoAltRem">
				<h:graphicImage value="/img/view.gif" style="overflow: visible;"/>: Visualizar Solicitação
		        <h:graphicImage value="/img/refresh.png" style="overflow: visible;"/>: Transferir Solicitação
		        <h:graphicImage value="/img/notificar.png" style="overflow: visible;"/>: Notificar sobre Solicitação
		        <h:graphicImage value="/img/seta.gif" style="overflow: visible;"/>: Atender Solicitação
		        <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Cancelar Solicitação
		</div>
		
		<table class="listagem">
			<caption>Lista de solicitações de Normalização e Catalogação na Fonte  ( ${fn:length(solicitacaoServicoDocumentoMBean.solicitacoes)} ) </caption>
			
				<thead>
					<tr>
						<th>Número</th>
						<th>Tipo de serviço</th>
						<th>Solicitante</th>
						<th>Tipo de obra</th>
						<th>Biblioteca</th>
						<th>Data Solicitação</th>
						<th>Situação</th>
						<th style="max-width:5px;"></th>
						<th style="max-width:5px;"></th>
						<th style="max-width:5px;"></th>
						<th style="max-width:5px;"></th>
						<th style="max-width:5px;"></th>
					</tr>
				</thead>
				
				<c:forEach items="#{solicitacaoServicoDocumentoMBean.solicitacoes}" var="solicitacao" varStatus="status">
					<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
						<td>${solicitacao.numeroSolicitacao}</td>
						<td>${solicitacao.tipoServico}</td>
						<td>${solicitacao.solicitante}</td>
						<td>${solicitacao.tipoDocumento.denominacao}</td>
						<td>${solicitacao.biblioteca.descricao}</td>
						<td><ufrn:format type="data" valor="${solicitacao.dataCadastro}"/></td>
						<td style="${! solicitacao.ativa ? 'text-decoration: line-through;': ''}">${solicitacao.situacao.descricao} </td>
						
						
						<c:if test="${! solicitacao.ativa}">
							<td colspan="5" style="font-style: italic;"> Removida pelo Usuário </td>
						</c:if>
						
						<c:if test="${solicitacao.ativa}">
							
							<td>
								
								<h:commandLink action="#{solicitacaoServicoDocumentoMBean.visualizarDadosSolicitacaoAtendimento}" rendered="#{solicitacao.ativa}">
									<h:graphicImage id="gphicimgVisualizarSolicitacao" url="/img/view.gif" style="border:none" title="Visualizar Solicitação"/>
									<f:param name="idSolicitacao" value="#{solicitacao.id}"/>		
								</h:commandLink>
								
			
							</td>
							
							<td>
								<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL, SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO} %>">
									<c:if test="${(solicitacao.solicitado || solicitacao.reenviado) && solicitacao.ativa }">
										<h:commandLink action="#{solicitacaoServicoDocumentoMBean.preTransferirSolicitacao}">
											<h:graphicImage id="gphicimgTransferirSolicitacao" url="/img/refresh.png" style="border:none"
												title="Transferir Solicitação" />
											<f:param name="idSolicitacao" value="#{solicitacao.id}"/>		
										</h:commandLink>
									</c:if>
								</ufrn:checkRole>
							</td>
							
							<td>
								<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL, SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO} %>">
									<c:if test="${(solicitacao.solicitado || solicitacao.reenviado) && solicitacao.ativa}">
										<h:commandLink action="#{solicitacaoServicoDocumentoMBean.preNotificarSolicitacao}">
											<h:graphicImage id="gphicimgNotificarSolicitacao" url="/img/notificar.png" style="border:none"
												title="Notificar sobre Solicitação" />
											<f:param name="idSolicitacao" value="#{solicitacao.id}"/>		
										</h:commandLink>
									</c:if>
								</ufrn:checkRole>
							</td>
							
							<td>
							
								<%-- O bibliotecário de setor de catalogação pode atender as solicitações porque é ele quem gera a ficha catalográfica --%>
								
								<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL, 
										SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO
										, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO} %>">
							
									<c:if test="${(solicitacao.solicitado || solicitacao.reenviado) && solicitacao.ativa}">
									
										<h:commandLink action="#{solicitacaoServicoDocumentoMBean.atenderSolicitacao}">
											<h:graphicImage id="gphicimgAtenderSolicitacao" url="/img/seta.gif" style="border:none"
												title="Atender Solicitação" />
											<f:param name="idSolicitacao" value="#{solicitacao.id}"/>		
										</h:commandLink>
									</c:if>
								</ufrn:checkRole>
								
							</td>
							<td>
								<c:if test="${!solicitacao.atendido && !solicitacao.cancelado}">
								<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL, 
										SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO} %>">
									<h:commandLink action="#{solicitacaoServicoDocumentoMBean.cancelarSolicitacao}" rendered="#{solicitacao.ativa}">
										<h:graphicImage id="gphicimgCancelarSolicitacao" url="/img/delete.gif" style="border:none"
												title="Cancelar Solicitação" />
											<f:param name="idSolicitacao" value="#{solicitacao.id}"/>		
									</h:commandLink>
								</ufrn:checkRole>
								</c:if>
							</td>
							
						</c:if>
						
					</tr>
				</c:forEach>
			
			<c:if test="${empty solicitacaoServicoDocumentoMBean.solicitacoes}">
				<tr><td style="text-align:center;color:#FF0000;font-weight:bold">Não há solicitações a serem exibidas.</td></tr>
			</c:if>
			
		</table>
			
		</c:if>
			
	
	</h:form>
	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>