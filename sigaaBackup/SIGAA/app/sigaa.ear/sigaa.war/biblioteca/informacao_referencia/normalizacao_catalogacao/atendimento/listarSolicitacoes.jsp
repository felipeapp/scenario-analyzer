<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>
<%@page import="br.ufrn.sigaa.arq.util.CheckRoleUtil"%>

<f:view>

<h2><ufrn:subSistema/> &gt; Solicita��o de normaliza��o e cataloga��o &gt; Solicita��es Realizadas</h2>

<div class="descricaoOperacao">
	<p>Abaixo podem ser visualizadas as solicita��es de normaliza��o e cataloga��o na fonte realizadas. Estas
		solicita��es podem estar nas seguintes situa��es: </p>
	<ul>
		<li><strong>Solicitada</strong>: Indica que o usu�rio solicitou a normaliza��o ou a cataloga��o na fonte mas ainda
		nenhuma a��o foi realizada.</li>
		<li><strong>Atendida</strong>: Indica que o bibliotec�rio atendeu a solicita��o, e portanto a mesma est� finalizada.</li>
		<li><strong>Cancelada</strong>: Indica que o bibliotec�rio cancelou a solicita��o por algum motivo.</li>
		<%-- <li><b>Validada:</b> As solicita��es neste estado j� foram validadas por algum bibliotec�rio do setor de
		Informa��o e Refer�ncia e podem ser atendidas.</li> --%>
	</ul>
</div>

<a4j:keepAlive beanName="solicitacaoServicoDocumentoMBean"></a4j:keepAlive>

<h:form id="formBuscaSolicitacoes">

	<table class="formulario" style="width: 80%">
		<caption>Filtrar Solicita��es</caption>
		
		<tr>
		
			<td width="2%">
				<h:selectBooleanCheckbox value="#{solicitacaoServicoDocumentoMBean.buscarNumeroSolicitacao}" styleClass="noborder" id="checkNumeroSolicitacao"/>
			</td>	
		
			<th style="text-align: left;">N�mero da Solicita��o:</th>
			
			<td colspan="3">
				<h:inputText id="inputTextNumeroSolicitacao" title="N�mero da Solicita��o" value="#{solicitacaoServicoDocumentoMBean.numeroSolicitacao}" size="10"  maxlength="9" 
						onfocus="getEl('formBuscaSolicitacoes:checkNumeroSolicitacao').dom.checked = true;" 
						onkeyup="return formatarInteiro(this);"/>
						<%-- Aqui o  marcarCheckBox n�o pode ficar no onchange porque no IE a fun��o formatarInteiro anula a execu��o --%> 
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
			
			<th style="text-align: left;">Tipo de Servi�o:</th>
			
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
			
			<th style="text-align: left;">Data da Solicita��o:</th>
	
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
			
			<td colspan="4">Buscar Solicita��es Atendidas</td>
		</tr>
		
		<tr>
			<th width="2%">
				<h:selectBooleanCheckbox value="#{solicitacaoServicoDocumentoMBean.buscarCanceladas}" id="buscarCanceladas" />
			</th>
			
			<td colspan="4">Buscar Solicita��es Canceladas</td>
		</tr>
		
		<tr>
			<th width="2%">
				<h:selectBooleanCheckbox value="#{solicitacaoServicoDocumentoMBean.buscarRemovidasPeloUsuario}" id="buscarRemovidas" />
			</th>
			
			<td colspan="4">Buscar Solicita��es Removidas pelo Usu�rio</td>
		</tr>
		
		<tfoot>
			<tr>
				<td colspan="5">
					<h:commandButton action="#{solicitacaoServicoDocumentoMBean.buscarSolicitacoesSolicitadas}" value="Buscar Solicita��es" id="buscarSolicitacoes"/>
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
				<h:graphicImage value="/img/view.gif" style="overflow: visible;"/>: Visualizar Solicita��o
		        <h:graphicImage value="/img/refresh.png" style="overflow: visible;"/>: Transferir Solicita��o
		        <h:graphicImage value="/img/notificar.png" style="overflow: visible;"/>: Notificar sobre Solicita��o
		        <h:graphicImage value="/img/seta.gif" style="overflow: visible;"/>: Atender Solicita��o
		        <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Cancelar Solicita��o
		</div>
		
		<table class="listagem">
			<caption>Lista de solicita��es de Normaliza��o e Cataloga��o na Fonte  ( ${fn:length(solicitacaoServicoDocumentoMBean.solicitacoes)} ) </caption>
			
				<thead>
					<tr>
						<th>N�mero</th>
						<th>Tipo de servi�o</th>
						<th>Solicitante</th>
						<th>Tipo de obra</th>
						<th>Biblioteca</th>
						<th>Data Solicita��o</th>
						<th>Situa��o</th>
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
							<td colspan="5" style="font-style: italic;"> Removida pelo Usu�rio </td>
						</c:if>
						
						<c:if test="${solicitacao.ativa}">
							
							<td>
								
								<h:commandLink action="#{solicitacaoServicoDocumentoMBean.visualizarDadosSolicitacaoAtendimento}" rendered="#{solicitacao.ativa}">
									<h:graphicImage id="gphicimgVisualizarSolicitacao" url="/img/view.gif" style="border:none" title="Visualizar Solicita��o"/>
									<f:param name="idSolicitacao" value="#{solicitacao.id}"/>		
								</h:commandLink>
								
			
							</td>
							
							<td>
								<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL, SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO} %>">
									<c:if test="${(solicitacao.solicitado || solicitacao.reenviado) && solicitacao.ativa }">
										<h:commandLink action="#{solicitacaoServicoDocumentoMBean.preTransferirSolicitacao}">
											<h:graphicImage id="gphicimgTransferirSolicitacao" url="/img/refresh.png" style="border:none"
												title="Transferir Solicita��o" />
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
												title="Notificar sobre Solicita��o" />
											<f:param name="idSolicitacao" value="#{solicitacao.id}"/>		
										</h:commandLink>
									</c:if>
								</ufrn:checkRole>
							</td>
							
							<td>
							
								<%-- O bibliotec�rio de setor de cataloga��o pode atender as solicita��es porque � ele quem gera a ficha catalogr�fica --%>
								
								<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL, 
										SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO
										, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO} %>">
							
									<c:if test="${(solicitacao.solicitado || solicitacao.reenviado) && solicitacao.ativa}">
									
										<h:commandLink action="#{solicitacaoServicoDocumentoMBean.atenderSolicitacao}">
											<h:graphicImage id="gphicimgAtenderSolicitacao" url="/img/seta.gif" style="border:none"
												title="Atender Solicita��o" />
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
												title="Cancelar Solicita��o" />
											<f:param name="idSolicitacao" value="#{solicitacao.id}"/>		
									</h:commandLink>
								</ufrn:checkRole>
								</c:if>
							</td>
							
						</c:if>
						
					</tr>
				</c:forEach>
			
			<c:if test="${empty solicitacaoServicoDocumentoMBean.solicitacoes}">
				<tr><td style="text-align:center;color:#FF0000;font-weight:bold">N�o h� solicita��es a serem exibidas.</td></tr>
			</c:if>
			
		</table>
			
		</c:if>
			
	
	</h:form>
	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>