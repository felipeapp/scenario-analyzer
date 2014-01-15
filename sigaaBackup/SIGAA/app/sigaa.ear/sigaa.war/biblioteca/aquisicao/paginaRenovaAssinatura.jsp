<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>


<h2>  <ufrn:subSistema /> &gt; Renovar Assinatura de Periódicos </h2>


<div class="descricaoOperacao">

	<p> Página para realizar a renovação de uma assinatura de periódico.</p>	

</div>

<f:view>

<h:form id="fromRenovaAssinatura">
	
	
	<%-- Se o usuário quiser voltar para a página de pesquisa e escolher outra assinatura --%>
	<a4j:keepAlive beanName="assinaturaPeriodicoMBean"></a4j:keepAlive>
		
	<a4j:keepAlive beanName="renovaAssinaturaPeriodicoMBean"></a4j:keepAlive>



	<%-- Formulário para realizar uma nova renovação  --%>
	
	<table class="listagem" style="width:70%;">
		
		<caption> Escolha o próximo período de vigência da assinatura</caption>

		<tr>	
			<th style="width: 30%; font-weight: bold;" >Código:</th>
			<td colspan="3">	
				<h:outputText value="#{renovaAssinaturaPeriodicoMBean.assinaturaSelecionada.codigo}"></h:outputText>
			</td>
		</tr>

		<tr>	
			<th style="font-weight: bold;">Título:</th>
			<td colspan="3">	
				<h:outputText value="#{renovaAssinaturaPeriodicoMBean.assinaturaSelecionada.titulo}"></h:outputText>
			</td>
		</tr>

		<tr>	
			<th style="font-weight: bold;">ISSN:</th>
			<td colspan="3">	
				<h:outputText value="#{renovaAssinaturaPeriodicoMBean.assinaturaSelecionada.issn}"></h:outputText>
			</td>
		</tr>

		<tr>	
			<th style="font-weight: bold;">Unidade:</th>
			<td colspan="3">	
				<h:outputText value="#{renovaAssinaturaPeriodicoMBean.assinaturaSelecionada.unidadeDestino.descricao}"></h:outputText>
			</td>
		</tr>

		<tr>	
			<th style="font-weight: bold;">Modalidade Aquisição:</th>
			<td colspan="3">
				<c:if test="${renovaAssinaturaPeriodicoMBean.assinaturaSelecionada.assinaturaDeCompra}">
					COMPRA
				</c:if>
				<c:if test="${renovaAssinaturaPeriodicoMBean.assinaturaSelecionada.assinaturaDeDoacao}">
					DOAÇÃO
				</c:if>
				<c:if test="${! renovaAssinaturaPeriodicoMBean.assinaturaSelecionada.assinaturaDeDoacao && ! renovaAssinaturaPeriodicoMBean.assinaturaSelecionada.assinaturaDeCompra}">
					INDEFINIDO
				</c:if>
			</td>
		</tr>

		<tr>	
			<th style="font-weight: bold;">Vigência Atual: </th>
			
			<c:if test="${renovaAssinaturaPeriodicoMBean.assinaturaSelecionada.dataInicioAssinatura != null 
					&& renovaAssinaturaPeriodicoMBean.assinaturaSelecionada.dataTerminoAssinatura != null}">
				<td style="width: 15%; font-weight: bold;">	
					<h:outputText value="#{renovaAssinaturaPeriodicoMBean.assinaturaSelecionada.dataInicioAssinatura}"></h:outputText>
				</td>
				<th style="width: 2%" >a</th>
				<td style="font-weight: bold; ${ renovaAssinaturaPeriodicoMBean.assinaturaSelecionada.estaVencida ? "color: red" : " " } ">	
					<h:outputText value="#{renovaAssinaturaPeriodicoMBean.assinaturaSelecionada.dataTerminoAssinatura}"></h:outputText>
				</td>
			</c:if>
			
			<c:if test="${renovaAssinaturaPeriodicoMBean.assinaturaSelecionada.dataInicioAssinatura == null 
					&& renovaAssinaturaPeriodicoMBean.assinaturaSelecionada.dataTerminoAssinatura == null}">
				<td colspan="3" style="color: red; font-style: italic;">	
					<c:out value="Não há Vigência Atual"></c:out>
				</td>
			</c:if>
			
		</tr>

		<tr>
			<td colspan="4">
				<table class="subFormulario" style="width: 100%">
					<caption>Novo Período da Assinatura</caption>
					<tr>	
						<th class="required" style="width: 30%">Data de Início:</th>
						<td>	
							<t:inputCalendar id="DataInicioAssinatura" value="#{renovaAssinaturaPeriodicoMBean.obj.dataInicial}" 
								size="10" maxlength="10" renderAsPopup="true"  popupDateFormat="dd/MM/yyyy" 
								renderPopupButtonAsImage="true" title="Data de Início da Assinatura"
								onkeypress="return formataData(this, event)" />
						</td>
					</tr>
					
					<tr>
						<th class="required">Data de Término:</th> 
						<td>
							<t:inputCalendar id="DataTerminoAssinatura" value="#{renovaAssinaturaPeriodicoMBean.obj.dataFinal}" 
								size="10" maxlength="10" renderAsPopup="true"  popupDateFormat="dd/MM/yyyy" 
								renderPopupButtonAsImage="true" title="Data de Término da Assinatura"
								onkeypress="return formataData(this, event)" />
						</td>
					</tr>
					
					<tr>
						<th>Observação:</th> 
						<td>
							<t:inputTextarea id="textAreaObservacao" value="#{renovaAssinaturaPeriodicoMBean.obj.observacao}"  cols="70" rows="4" onkeyup="textCounter(this, 'quantidadeCaracteresDigitados', 200);"/>
						</td>
					</tr>	
					<tr>
						<th>Caracteres Restantes:</th>
						<td>
							<span id="quantidadeCaracteresDigitados">200</span>
						</td>
					</tr>
							
				</table>
			</td>
		</tr>
	

		<tfoot>
			<tr>
				<td colspan="5" align="center">
		
					<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL, SigaaPapeis.BIBLIOTECA_SETOR_AQUISICAO_BIBLIOTECARIO} %>">
						<h:commandButton value="Renovar Assinatura" action="#{renovaAssinaturaPeriodicoMBean.renovarAssinatura}" />
					</ufrn:checkRole>
	
					<h:commandButton value="Cancelar" action="#{renovaAssinaturaPeriodicoMBean.voltar}" immediate="true" onclick="#{confirm}" />
						
				</td>
			</tr>
				
		</tfoot>

	</table>


	<%@include file="/WEB-INF/jsp/include/_campos_obrigatorios.jsp"%>


	<%-- Mostra ao usuário as renovações que já foram feitas --%>
	
	<table class="listagem" style="width: 80%;">
		<caption> Renovações realizadas para esta assinatura ( ${fn:length(renovaAssinaturaPeriodicoMBean.renovacoes)} ) </caption>
		
		<c:if test="${fn:length(renovaAssinaturaPeriodicoMBean.renovacoes) == 0}">
			<tr>
				<th style="text-align: center; font-weight: bold;"> Assinatura ainda não foi renovada</th>
			</tr>
		</c:if>
		
		<c:if test="${fn:length(renovaAssinaturaPeriodicoMBean.renovacoes) > 0}">
		
			<thead>
				<th style="text-align: left; width: 20%" >Data Inicial</th>
				<th style="text-align: left; width: 20%">Data Final</th>
				<th style="text-align: left;">Renovada por</th>
			</thead>
		
			<c:forEach items="#{renovaAssinaturaPeriodicoMBean.renovacoes}" var="renovacao" varStatus="status">
				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
					<td> <ufrn:format type="data" valor="${renovacao.dataInicial}"></ufrn:format></td>
					<td> <ufrn:format type="data" valor="${renovacao.dataFinal}"></ufrn:format> </td>
					<td> ${renovacao.registroCriacao.usuario.pessoa.nome}  </td>
				</tr>
				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
					<td  style="font-style: italic;"  colspan="3"> ${renovacao.observacao}  </td>
				</tr>
			</c:forEach>
		</c:if>
		
	</table>	
	


</h:form>


</f:view>


<script type="text/javascript">
 
function textCounter(field, idMostraQuantidadeUsuario, maxlimit) {
	
	if (field.value.length > maxlimit){
		field.value = field.value.substring(0, maxlimit);
	}else{ 
		document.getElementById(idMostraQuantidadeUsuario).innerHTML = maxlimit - field.value.length ;
	} 
}

</script>



<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>