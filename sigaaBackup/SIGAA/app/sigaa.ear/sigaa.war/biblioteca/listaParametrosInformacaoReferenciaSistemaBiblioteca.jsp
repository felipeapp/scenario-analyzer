<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>


<f:view>

	<h2>  <ufrn:subSistema /> &gt; Visualizar / Alterar Par�metros de Informa��o e Refer�ncia do M�dulo de Bibliotecas </h2>

	<div class="descricaoOperacao"> 
     	<p> Nesta p�gina � poss�vel visualizar e alterar os par�metros  do setor de Informa��o e Refer�ncia do m�dulo de bibliotecas.</p>
	</div>

	<a4j:keepAlive beanName="configuraParametrosInformacaoReferenciaMBean" />
	
	<h:form id="formAlterarParametrosBiblioteca">	
	
		

		<table class="formulario" style="width: 100%;">		
		
			<caption> Par�metros do setor de Informa��o e Refer�ncia (  ${fn:length(configuraParametrosInformacaoReferenciaMBean.parametrosAlteracao)}  ) </caption>
		
			<thead>
				<th style="text-align: left;">Nome</th>
				<th style="text-align: left;">Valor</th>
			</thead>
		
			<c:forEach var="parametro" items="#{configuraParametrosInformacaoReferenciaMBean.parametrosAlteracao}" varStatus="status">
				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
					<td style="width: 50%"> 
						<strong>${parametro.nome}</strong> <br/>
						<em> ${parametro.descricao} </em> 
					</td>
					<td style="width: 50%">
						<c:choose>							
						
							<%--   texto ficha catalografica  --%>
							<c:when test="${status.index == 0}"> 
								<h:inputTextarea value="#{parametro.valor}" readonly="#{configuraParametrosInformacaoReferenciaMBean.notAdministradorGeral}" rows="2" cols="60" />
							</c:when>
							
							<%--    sigla da unidade federativa  impressa da ficha catalografica  --%>
							<c:when test="${status.index == 1}"> 
								<h:inputText value="#{parametro.valor}" readonly="#{configuraParametrosInformacaoReferenciaMBean.notAdministradorGeral}" size="40" />
							</c:when>
							
						</c:choose>
					</td>
				</tr>
			</c:forEach>	
		
			<tfoot>
				<tr>
					<td colspan="5" align="center">
						<h:commandButton id="botaoAltualizarParametros" value="Atualizar Par�metros" action="#{configuraParametrosInformacaoReferenciaMBean.alterarParametros}" 
							onclick="return confirm('Confirma a atualiza��o dos par�metros do sistema ? ');" rendered="#{configuraParametrosInformacaoReferenciaMBean.administradorGeral}" />
						<h:commandButton id="cmdCancelar" value="Cancelar" action="#{configuraParametrosCirculacaoMBean.cancelar}"  immediate="true" onclick="#{confirm}"  />
					</td>
				</tr>
			</tfoot>
		
		</table>

	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>