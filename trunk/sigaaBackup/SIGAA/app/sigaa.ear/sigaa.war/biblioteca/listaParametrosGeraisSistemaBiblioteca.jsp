<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>


<f:view>

	<h2>  <ufrn:subSistema /> &gt; Visualizar / Alterar Parâmetros Gerais do Módulo de Bibliotecas </h2>

	<div class="descricaoOperacao"> 
     	<p> Nesta página é possível visualizar e alterar os parâmetros gerais do módulo de bibliotecas.</p>
	</div>

	<a4j:keepAlive beanName="configuraParametrosGeraisBibliotecaMBean" />
	
	<h:form id="formAlterarParametrosBiblioteca">	
	
		

		<table class="formulario" style="width: 100%;">		
		
			<caption> Parâmetros Gerais (  ${fn:length(configuraParametrosGeraisBibliotecaMBean.parametrosAlteracao)}  ) </caption>
		
			<thead>
				<th style="text-align: left;">Nome</th>
				<th style="text-align: left;">Valor</th>
			</thead>
		
			<c:forEach var="parametro" items="#{configuraParametrosGeraisBibliotecaMBean.parametrosAlteracao}" varStatus="status">
				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
					<td style="width: 50%"> 
						<strong>${parametro.nome}</strong> <br/>
						<em> ${parametro.descricao} </em> 
					</td>
					<td style="width: 50%">
						<c:choose>							
							<%--   código library of congress  --%>
							<c:when test="${status.index == 0}"> 
								<h:inputText value="#{parametro.valor}" readonly="#{configuraParametrosGeraisBibliotecaMBean.notAdministradorGeral}" size="40"/>
							</c:when>
							
							<%--  nome do sistemas de biblioteca  --%>
							<c:when test="${status.index == 1}"> 
								<h:inputText value="#{parametro.valor}" readonly="#{configuraParametrosGeraisBibliotecaMBean.notAdministradorGeral}" size="60" onkeyup="CAPS(this);" />
							</c:when>
							
						</c:choose>
					</td>
				</tr>
			</c:forEach>	
		
			<tfoot>
				<tr>
					<td colspan="5" align="center">
						<h:commandButton id="botaoAltualizarParametros" value="Atualizar Parâmetros" action="#{configuraParametrosGeraisBibliotecaMBean.alterarParametros}" 
							onclick="return confirm('Confirma a atualização dos parâmetros do sistema ? ');" rendered="#{configuraParametrosGeraisBibliotecaMBean.administradorGeral}" />
						<h:commandButton id="cmdCancelar" value="Cancelar" action="#{configuraParametrosCirculacaoMBean.cancelar}"  immediate="true" onclick="#{confirm}"  />
					</td>
				</tr>
			</tfoot>
		
		</table>

	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>