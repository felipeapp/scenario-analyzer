<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>


<h2>  <ufrn:subSistema /> &gt; Selecionar Fascículos para Transferência </h2>

<div class="descricaoOperacao"> 
   <p>Página para selecionar os fascículos que vão ser transferidos. </p>
   <p><strong> Observação: Os fascículos mostrados em <span style="color: red">vermelho</span> estão pendentes
   em outra transferência e não podem ser transferidos novamente até que a transferência anterior se complete.</strong></p>
</div>

<f:view>

	<a4j:outputPanel id="outPanelAjax" ajaxRendered="true" >

		<h:form id="formFasciculosTransferencia">

			
			<a4j:keepAlive beanName="solicitaTransferenciaFasciculosEntreAssinaturasMBean" />	
			<a4j:keepAlive beanName="assinaturaPeriodicoMBean" />
	
								
			<table id="tableHeadFasciculosAssinaturaSelecionada" class="listagem" width="100%">
			
				<caption> Fascículos da Assinatura Selecionada ( ${fn:length(solicitaTransferenciaFasciculosEntreAssinaturasMBean.fasciculosDaAssinaturaSelecionada)} ) </caption>	
				
				<thead>
					<tr>
						<th style="width: 30%; text-align: left; font-weight: bold;">Código de Barras</th>
						<th style="width: 10%; text-align: center; font-weight: bold;">Ano Cronológico</th>
						<th style="width: 10%; text-align: center; font-weight: bold;">Ano</th>
						<th style="width: 10%; text-align: center; font-weight: bold;">Dia/Mês</th>
						<th style="width: 10%; text-align: center; font-weight: bold;">Volume</th>
						<th style="width: 10%; text-align: center; font-weight: bold;">Número</th>
						<th style="width: 10%; text-align: center; font-weight: bold;">Edição</th>
						<th style="width: 10%; text-align: center; "> <h:selectBooleanCheckbox id="checkBoxGeral" value="false" onclick="selecionarTudo(this);"> </h:selectBooleanCheckbox> </th>
					</tr>
				</thead>
				
				<tr>
					<th colspan="3" style="text-align: right"> Escolha o Ano dos Fascículos:</th>
					<td colspan="4">
						<h:selectOneMenu id="comboBoxAnosCronologicosDosFasciculos" 
								value="#{solicitaTransferenciaFasciculosEntreAssinaturasMBean.anoPesquisaFasciculos}">
							<f:selectItems value="#{detalhesMateriaisDeUmTituloMBean.anosPesquisaFasciculos}"/>
							<a4j:support event="onchange"
								action="#{solicitaTransferenciaFasciculosEntreAssinaturasMBean.verificaAlteracaoFiltroAno}" 
							 	reRender="panelGeral"/>
						</h:selectOneMenu>
						
						<a4j:status startText=" Aguarde . . . " stopText=" " /> 
						
					</td>
				</tr>
			</table>
			
			<h:panelGrid id="panelGeral" style="width:100%;" columns="1">
				
				<t:div rendered="#{solicitaTransferenciaFasciculosEntreAssinaturasMBean.quantidadeFasciculosDaAssinaturaSelecionada > 0}">		
					
					<table id="tableFasciculosAssinaturaSelecionada" class="listagem" width="100%">
						
						<c:forEach items="#{solicitaTransferenciaFasciculosEntreAssinaturasMBean.fasciculosDaAssinaturaSelecionada}" var="fasciculo" varStatus="loop">
							<tr class="${loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
								<td style="width: 30%; text-align: left;   ${ fasciculo.desabilitado ? 'color: red' : ' '} ">  ${fasciculo.codigoBarras}    </td>
								<td style="width: 10%; text-align: center; ${ fasciculo.desabilitado ? 'color:red;' : ' '} ">  ${fasciculo.anoCronologico}  </td>
								<td style="width: 10%; text-align: center; ${ fasciculo.desabilitado ? 'color:red;' : ' '} ">  ${fasciculo.ano}             </td>
								<td style="width: 10%; text-align: center; ${ fasciculo.desabilitado ? 'color:red;' : ' '} ">  ${fasciculo.diaMes}             </td>
								<td style="width: 10%; text-align: center; ${ fasciculo.desabilitado ? 'color:red;' : ' '} ">  ${fasciculo.volume}          </td>
								<td style="width: 10%; text-align: center; ${ fasciculo.desabilitado ? 'color:red;' : ' '} ">  ${fasciculo.numero}          </td>
								<td style="width: 10%; text-align: center; ${ fasciculo.desabilitado ? 'color:red;' : ' '} ">  ${fasciculo.edicao}          </td>
								
								<td style="width: 10%; text-align: center;">	
									<h:selectBooleanCheckbox  value="#{fasciculo.selecionado}" rendered="#{! fasciculo.desabilitado}" />
								</td>
								
							</tr>
						</c:forEach>
					
					</table>
			
				</t:div>
						
			</h:panelGrid>	
			
			<table id="tableFooterFasciculosAssinaturaSelecionada" class="listagem" width="100%">
				<tfoot>
						<tr>
							<td colspan="8" style="text-align: center;">
								<h:commandButton id="botaoTransferirFaciculosSelecionados" value="Transferir Fascículos Selecionados" action="#{solicitaTransferenciaFasciculosEntreAssinaturasMBean.preTransferirFasciculos}" />
								<h:commandButton id="botaoVoltarTela" value="<< Voltar" action="#{assinaturaPeriodicoMBean.telaBuscaAssinaturas}" />
								<h:commandButton value="Cancelar" action="#{solicitaTransferenciaFasciculosEntreAssinaturasMBean.cancelar}" onclick="#{confirm}" immediate="true" id="cancelar" />
							</td>
						</tr>
					</tfoot>
			</table>
						
		</h:form>


	</a4j:outputPanel>

</f:view>



<script type="text/javascript">

	function selecionarTudo(chk){
	   for (i=0; i<document.formFasciculosTransferencia.elements.length; i++)
	      if(document.formFasciculosTransferencia.elements[i].type == "checkbox")
	         document.formFasciculosTransferencia.elements[i].checked= chk.checked;
	}

</script>




<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>