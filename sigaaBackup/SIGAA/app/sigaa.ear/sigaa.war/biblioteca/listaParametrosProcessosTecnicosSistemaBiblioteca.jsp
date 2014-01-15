<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>


<f:view>

	<h2>  <ufrn:subSistema /> &gt; Visualizar / Alterar Par�metros de Processos T�cnicos do M�dulo de Biblioteca </h2>

	<div class="descricaoOperacao"> 
     	<p> Nesta p�gina � poss�vel visualizar e alterar os par�metros do setor de Processos T�cnicos do m�dulo de biblioteca.</p>
	</div>

	<a4j:keepAlive beanName="configuraParametrosProcessosTecnicosMBean" />
	
	<h:form id="formAlterarParametrosBiblioteca">	
	
		

		<table class="formulario" style="width: 100%;">		
		
			<caption> Par�metros do setor de Processos T�cnicos (  ${fn:length(configuraParametrosProcessosTecnicosMBean.parametrosAlteracao)}  ) </caption>
		
			<thead>
				<th style="text-align: left;">Nome</th>
				<th style="text-align: left;">Valor</th>
			</thead>
		
			<c:forEach var="parametro" items="#{configuraParametrosProcessosTecnicosMBean.parametrosAlteracao}" varStatus="status">
				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
					<td style="width: 50%"> 
						<strong>${parametro.nome}</strong> <br/>
						<em> ${parametro.descricao} </em> 
					</td>
					<td style="width: 50%">
						<c:choose>							
							
							<%--  c�digo biblioteca catalogo coletipo FGV  --%>
							<c:when test="${status.index == 0}"> 
								<h:inputText value="#{parametro.valor}" readonly="#{configuraParametrosProcessosTecnicosMBean.notAdministradorGeral}" size="40"/>
							</c:when>
							
							<%--  vers�o do programa que alterou o arquivo  --%>
							<c:when test="${status.index == 1}"> 
								<h:inputText value="#{parametro.valor}" readonly="#{configuraParametrosProcessosTecnicosMBean.notAdministradorGeral}" size="40" />
							</c:when>
							
							<%--  c�digo da institui��o da cataloga��o  --%>
							<c:when test="${status.index == 2}"> 
								<h:inputText value="#{parametro.valor}" readonly="#{configuraParametrosProcessosTecnicosMBean.notAdministradorGeral}" size="40"/>
							</c:when>
							
							<%--  idioma da cataloga��o  --%>
							<c:when test="${status.index == 3}"> 
								<h:inputText value="#{parametro.valor}" readonly="#{configuraParametrosProcessosTecnicosMBean.notAdministradorGeral}" size="40"/>
							</c:when>
							
							<%--   siglas trabalhos acad�mcios  --%>
							<c:when test="${status.index == 4}"> 
								<h:inputText value="#{parametro.valor}" readonly="#{configuraParametrosProcessosTecnicosMBean.notAdministradorGeral}" size="60"/>
							</c:when>
							
							<%--   c�digo n�mero controle bibliogr�fico  --%>
							<c:when test="${status.index == 5}"> 
								<h:inputText value="#{parametro.valor}" readonly="#{configuraParametrosProcessosTecnicosMBean.notAdministradorGeral}" size="40"/>
							</c:when>
							
							<%--   c�digo n�mero controle autoridade  --%>
							<c:when test="${status.index == 6}"> 
								<h:inputText value="#{parametro.valor}" readonly="#{configuraParametrosProcessosTecnicosMBean.notAdministradorGeral}" size="40"/>
							</c:when>
							
							<%--  dias de retardo para o material est� dispon�vel no acervo --%>
							<c:when test="${status.index == 7}"> 
								<h:inputText value="#{parametro.valor}" readonly="#{configuraParametrosProcessosTecnicosMBean.notAdministradorGeral}" size="5" maxlength="2" onkeyup="return formatarInteiro(this);" />
							</c:when>
							
						</c:choose>
					</td>
				</tr>
			</c:forEach>	
		
			<tfoot>
				<tr>
					<td colspan="5" align="center">
						<h:commandButton id="botaoAltualizarParametros" value="Atualizar Par�metros" action="#{configuraParametrosProcessosTecnicosMBean.alterarParametros}" 
							onclick="return confirm('Confirma a atualiza��o dos par�metros do sistema ? ');" rendered="#{configuraParametrosProcessosTecnicosMBean.administradorGeral}" />
						<h:commandButton id="cmdCancelar" value="Cancelar" action="#{configuraParametrosCirculacaoMBean.cancelar}"  immediate="true" onclick="#{confirm}"  />
					</td>
				</tr>
			</tfoot>
		
		</table>

	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>