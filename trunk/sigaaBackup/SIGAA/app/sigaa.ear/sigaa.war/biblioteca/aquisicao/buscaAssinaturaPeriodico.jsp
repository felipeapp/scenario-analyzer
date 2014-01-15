<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2>  <ufrn:subSistema /> &gt; Buscar Assinaturas de Periódicos </h2>

<div class="descricaoOperacao">
		<p> Página para busca das assinatura de periódicos. </p>
		<p> <strong> Observação: Assinaturas mostradas em <span style="color: red">vermelho</span> estão vencidas.</strong> </p>
</div>

<style type="text/css">

.textoCentralizado{
	text-align:center;
}

table.listagem tr.unidadeDestino td{
		background: #C8D5EC;
		font-weight: bold;
		padding-left: 20px;
}

</style>


<f:view>

	<a4j:keepAlive beanName="assinaturaPeriodicoMBean"></a4j:keepAlive>
	
	<%-- MBeans de outros casos de uso que utilizam  a busca de assinaturas precisam guardar os seus dados durante a realização do caso de uso.--%>
	<a4j:keepAlive beanName="renovaAssinaturaPeriodicoMBean"></a4j:keepAlive>
	<a4j:keepAlive beanName="removeAssinaturaPeriodicosMBean"></a4j:keepAlive>
	<a4j:keepAlive beanName="registraChegadaFasciculoMBean"></a4j:keepAlive>
	<a4j:keepAlive beanName="associaAssinaturaATituloMBean"></a4j:keepAlive>
	<a4j:keepAlive beanName="pesquisaTituloCatalograficoMBean"></a4j:keepAlive>
	<a4j:keepAlive beanName="solicitaTransferenciaFasciculosEntreAssinaturasMBean" />
	<a4j:keepAlive beanName="autorizaTransferenciaFasciculosEntreAssinaturasMBean" />
	
	<a4j:keepAlive beanName="gerenciarCodigosBarrasFasciculosMBean"></a4j:keepAlive>
	
	<h:form id="pesquisaAssinatura">
	
		<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_SETOR_AQUISICAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL} %>">
			<div class="infoAltRem" style="width: 80%">
				
				<c:if test="${assinaturaPeriodicoMBean.pesquisaNormal}">
					<h:graphicImage value="/img/adicionar.gif" />
					<h:commandLink id="cmdLinkCriarAssinatura" action="#{assinaturaPeriodicoMBean.preCriarAssinatura}" value="Criar Assinatura" />
					<h:graphicImage value="/img/alterar.gif" />: Alterar Assinatura
				</c:if>
				
				<c:if test="${assinaturaPeriodicoMBean.pesquisaSelecionaAssinaturaTransferenciaFasciculos}">
					<h:graphicImage value="/img/adicionar.gif" />
					<h:commandLink id="cmdLinkCriarAssinaturaParaTransferenciaFasciculos" action="#{assinaturaPeriodicoMBean.preCriacaoAssinaturaParaTransferenciaFasciculos}" value="Criar Assinatura para a Transferência" />
				</c:if>
				
				<c:if test="${assinaturaPeriodicoMBean.pesquisaSelecionaAssinatura || assinaturaPeriodicoMBean.pesquisaSelecionaAssinaturaSemTitulo
						|| assinaturaPeriodicoMBean.pesquisaSelecionaAssinaturaTransferenciaFasciculos }">
					<h:graphicImage value="/img/seta.gif" />: Selecionar Assinatura
				</c:if>
				
			</div>
		</ufrn:checkRole>
		
		<table class="formulario" width="80%" style="margin-bottom: 20px">
			<caption class="listagem">Filtrar Assinaturas</caption>
			
			<tr>
				<td width="2%">
					<h:selectBooleanCheckbox value="#{assinaturaPeriodicoMBean.buscarCodigo}" styleClass="noborder" id="checkCodigoAssinante"/>
				</td>
				<th colspan="1"  style="text-align: left">Código da Assinatura:</th>
				<td colspan="3" > 
					<h:inputText id="inputTextCodigoAssinaturaBusca" value="#{ assinaturaPeriodicoMBean.assinaturaModeloBuscas.codigo}" size="20" maxlength="40"  onfocus="getEl('pesquisaAssinatura:checkCodigoAssinante').dom.checked = true;"/> 
				</td>
			</tr>
			
			<tr>
				<td width="2%">
					<h:selectBooleanCheckbox id="checkISSN" value="#{assinaturaPeriodicoMBean.buscarIssn}" styleClass="noborder" />
				</td>
				<th style="text-align: left;">ISSN:</th>
				<td colspan="3" >
					<h:inputText
							id="inputTextISSN" value="#{ assinaturaPeriodicoMBean.assinaturaModeloBuscas.issn }"
							maxlength="40"  onfocus="getEl('pesquisaAssinatura:checkISSN').dom.checked = true;"/>
				</td>
			</tr>
			
			<tr>
				<td width="2%">
					<h:selectBooleanCheckbox value="#{assinaturaPeriodicoMBean.buscarTitulo }" styleClass="noborder" id="checkTituloAssinatura"/>
				</td>
				<th colspan="1" style="text-align: left">Título da Assinatura:</th>
				<td colspan="3" > 
					<h:inputText id="inputTextTituloAssinaturaBusca" value="#{ assinaturaPeriodicoMBean.assinaturaModeloBuscas.titulo}" size="50" maxlength="200" onfocus="getEl('pesquisaAssinatura:checkTituloAssinatura').dom.checked = true;" /> 
				</td>
			</tr>
			
			
			<tr>
				<td width="2%">
					<h:selectBooleanCheckbox value="#{assinaturaPeriodicoMBean.buscarUnidadeDestino}" styleClass="noborder" id="checkUnidadeDestino"/>
				</td>
				<th colspan="1" style="text-align: left">Unidade de Destino:</th>
				<td colspan="3" >
					<h:selectOneMenu id="comboboxBibliotecasAssinatura" value="#{assinaturaPeriodicoMBean.assinaturaModeloBuscas.unidadeDestino.id}" onfocus="getEl('pesquisaAssinatura:checkUnidadeDestino').dom.checked = true;" >
						<f:selectItem itemLabel="-- SELECIONE --" itemValue="-1" />
						<f:selectItems value="#{assinaturaPeriodicoMBean.bibliotecasInternas}"/>
					</h:selectOneMenu>
				</td>
			</tr>
			
			
			<tr>
				<td width="2%">
					<h:selectBooleanCheckbox value="#{assinaturaPeriodicoMBean.buscarModalidadeAquisicao}" styleClass="noborder" id="checkModalidadeAquisicao"/>
				</td>
				<th colspan="1"  style="text-align: left">Modalidade de Aquisição:</th>
				<td colspan="3" >			
					<h:selectOneMenu id="comboboxModalidadeAquisicaoBuscas" value="#{assinaturaPeriodicoMBean.assinaturaModeloBuscas.modalidadeAquisicao}" onfocus="getEl('pesquisaAssinatura:checkModalidadeAquisicao').dom.checked = true;">
						<f:selectItem itemLabel="-- SELECIONE --" itemValue="-1" />
						<f:selectItem itemLabel="Compra" itemValue="#{assinaturaPeriodicoMBean.assinaturaModeloBuscas.modalidadeCompra}" />
						<f:selectItem itemLabel="Doação" itemValue="#{assinaturaPeriodicoMBean.assinaturaModeloBuscas.modalidadeDoacao}" />
					</h:selectOneMenu>
				</td>
			</tr>
			
			
			<tr>
				<td width="2%">
					<h:selectBooleanCheckbox value="#{assinaturaPeriodicoMBean.buscarPeriodicidade}" styleClass="noborder" id="checkPeriodicidade"/>
				</td>
				<th colspan="1"  style="text-align: left">Periodicidade:</th>
				<td colspan="3" >			
					<h:selectOneMenu id="comboboxPeriodicidadeBuscas" value="#{assinaturaPeriodicoMBean.assinaturaModeloBuscas.frequenciaPeriodicos.id}" onfocus="getEl('pesquisaAssinatura:checkPeriodicidade').dom.checked = true;">
						<f:selectItem itemLabel="-- SELECIONE --" itemValue="-1" />
						<f:selectItems value="#{assinaturaPeriodicoMBean.allFrequenciasAtivasComboBox}"/>
					</h:selectOneMenu>
				</td>
			</tr>
			
			<tr>
				<td width="2%">
					<h:selectBooleanCheckbox value="#{assinaturaPeriodicoMBean.buscarInternacional}" styleClass="noborder" id="checkInternacional"/>
				</td>
				<th colspan="1"  style="text-align: left">Internacionalização </th> 
				<td colspan="3" >			
					<h:selectOneMenu id="comboboxInternacionalBuscas" value="#{assinaturaPeriodicoMBean.assinaturaModeloBuscas.internacional}" onfocus="getEl('pesquisaAssinatura:checkInternacional').dom.checked = true;">
						<f:selectItem itemLabel="-- SELECIONE --" itemValue="#{null}" />
						<f:selectItem itemLabel="Nacional" itemValue="false" />
						<f:selectItem itemLabel="Internacional" itemValue="true" />
					</h:selectOneMenu>
				</td>
			</tr>
			
			<tr>
				<td></td>
				<th style="text-align:left">Ordenação:</th>
				<td colspan="6">
					<h:selectOneMenu value="#{assinaturaPeriodicoMBean.valorCampoOrdenacao}">
						<f:selectItems value="#{assinaturaPeriodicoMBean.campoOrdenacaoResultadosComboBox}"/>
					</h:selectOneMenu>
				</td>
			</tr>
			
			<tr>
				<td></td>
				<th colspan="1" style="text-align: right">Data de Início da Assinatura:</th>
				<td colspan="1" >	
					<t:inputCalendar id="DataInicioAssinatura" value="#{assinaturaPeriodicoMBean.assinaturaModeloBuscas.dataInicioAssinatura}" 
						size="10" maxlength="10" renderAsPopup="true"  popupDateFormat="dd/MM/yyyy" 
						renderPopupButtonAsImage="true" title="Data de Início da Assinatura"
						onkeypress="return formataData(this, event)" />
				</td>
				
				<th colspan="1" >Data de Término da Assinatura:</th> 
				<td colspan="1" >
					<t:inputCalendar id="DataFinalAssinatura" value="#{assinaturaPeriodicoMBean.assinaturaModeloBuscas.dataTerminoAssinatura}" 
						size="10" maxlength="10" renderAsPopup="true"  popupDateFormat="dd/MM/yyyy" 
						renderPopupButtonAsImage="true" title="Data de Término da Assinatura"
						onkeypress="return formataData(this, event)" />
				</td>
			</tr>
			
			
			<tfoot>
				<tr>
					<td colspan="5">
						
						<h:commandButton id="cmdButtonBuscarAssinatura" value="Buscar" action="#{assinaturaPeriodicoMBean.buscarAssinaturas}" />
						
						<h:commandButton id="botaoVoltar" value="<< Voltar" action="#{assinaturaPeriodicoMBean.voltarBusca}" 
									rendered="#{assinaturaPeriodicoMBean.utilizandoBotaoVoltar}"/>
						
						<h:commandButton id="botaoLimpar" value="Limpar" action="#{assinaturaPeriodicoMBean.limparResultadosBusca}"></h:commandButton>
						<h:commandButton id="botaoCancelar" value="Cancelar" action="#{assinaturaPeriodicoMBean.cancelar}" immediate="true" onclick="#{confirm}"/>
						
					</td>
				</tr>
			</tfoot>
			
		</table>
		
		
		
		
		
		<%-- Resultados da busca --%>
		
		<c:if test="${ not empty assinaturaPeriodicoMBean.assinaturasBuscadas }">
			
			
			
			<table class="listagem" width="100%">
			
				<caption>Assinaturas ( ${fn:length( assinaturaPeriodicoMBean.assinaturasBuscadas ) } )</caption>
				
				<thead>
					<tr>
						<th>Código</th>
						<th>Título</th>
						<th style="text-align: center">Data de Início</th>
						<th style="text-align: center">Data de Término</th>
						<th width="10%">Periodicidade</th>
						<th width="10%">Modalidade Aquisição</th>
						<th style="text-align: center">Internacionalização </th>
						<th width="1%" colspan="1"></th>
					</tr>
				</thead>
				<tbody>
				
				
					<c:set var="idFiltroUnidadeDestino" value="-1" scope="request"/>
				
					<c:forEach var="assinatura" items="#{assinaturaPeriodicoMBean.assinaturasBuscadas}" varStatus="status">
						
						<c:if test="${ idFiltroUnidadeDestino != assinatura.unidadeDestino.id}">
							<c:set var="idFiltroUnidadeDestino" value="${assinatura.unidadeDestino.id}" scope="request" />
							<tr class="unidadeDestino">
								<td colspan="8">${assinatura.unidadeDestino.descricao}</td>
							</tr>
						</c:if>
						
						<tr class="${ status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }"  onMouseOver="javascript:this.style.backgroundColor='#C4D2EB'" onMouseOut="javascript:this.style.backgroundColor=''">
							
							
								<td style=" ${ assinatura.estaVencida ? 'color: red' : ' ' } ">${assinatura.codigo}</td>
								<td style="${ assinatura.estaVencida ? 'color: red' : ' ' }" width="30%">${assinatura.titulo}</td>
								<td style="${ assinatura.estaVencida ? 'color: red' : ' ' }; text-align: center"> <ufrn:format type="data" valor="${assinatura.dataInicioAssinatura}">  </ufrn:format> </td>
								<td style="${ assinatura.estaVencida ? 'color: red' : ' ' }; text-align: center"> <ufrn:format type="data" valor="${assinatura.dataTerminoAssinatura}">  </ufrn:format> </td>
								<td style="${ assinatura.estaVencida ? 'color: red' : ' ' }"width="10%">${assinatura.frequenciaPeriodicos.descricao}</td>
								
								<td style="${ assinatura.estaVencida ? 'color: red' : ' ' }"width="10%">
									<c:if test="${assinatura.assinaturaDeCompra}">
										COMPRA
									</c:if>
									<c:if test="${assinatura.assinaturaDeDoacao}">
										DOAÇÃO
									</c:if>
									<c:if test="${! assinatura.assinaturaDeDoacao && ! assinatura.assinaturaDeCompra}">
										INDEFINIDO
									</c:if>
								</td>
								
								<td style="${ assinatura.estaVencida ? 'color: red' : ' ' }; text-align: center">
									<c:if test="${assinatura.internacional}">
										Internacional
									</c:if>
									<c:if test="${ ! assinatura.internacional}">
										Nacional
									</c:if>
								</td>
								
								<td width="1%">
								
									<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_SETOR_AQUISICAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL} %>">
										
										<h:commandLink id="cmdLinkAlterarAssinatura"  title="Alterar Assinatura" 
												action="#{assinaturaPeriodicoMBean.preAlterarAssinatura}"
												rendered="#{assinaturaPeriodicoMBean.pesquisaNormal}">
											<f:param name="idAssinaturaAlteracao" value="#{assinatura.id}" />
											<h:graphicImage url="/img/alterar.gif" alt="Alterar Assinatura" />
										</h:commandLink>
										
									</ufrn:checkRole>
									
									<h:commandLink id="cmdLinkSelecionarAssinatura"  title="Selecionar Assinatura" 
										rendered="#{assinaturaPeriodicoMBean.pesquisaSelecionaAssinatura 
												|| assinaturaPeriodicoMBean.pesquisaSelecionaAssinaturaSemTitulo
										|| assinaturaPeriodicoMBean.pesquisaSelecionaAssinaturaTransferenciaFasciculos}"
										action="#{assinaturaPeriodicoMBean.selecionouAssinatura}">
										<f:param name="idAssinatura" value="#{assinatura.id}" />
										<h:graphicImage url="/img/seta.gif" alt="Selecionar Assinatura" />
									</h:commandLink>						
								
								</td>
							
						</tr>
					</c:forEach>
				</tbody>
				
				
			</table>	
		</c:if>
		
	</h:form>
	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>