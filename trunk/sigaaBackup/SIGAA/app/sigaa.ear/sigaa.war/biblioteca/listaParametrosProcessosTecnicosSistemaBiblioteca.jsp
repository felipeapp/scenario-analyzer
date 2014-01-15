<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>


<f:view>

	<h2>  <ufrn:subSistema /> &gt; Visualizar / Alterar Parâmetros de Processos Técnicos do Módulo de Biblioteca </h2>

	<div class="descricaoOperacao"> 
     	<p> Nesta página é possível visualizar e alterar os parâmetros do setor de Processos Técnicos do módulo de biblioteca.</p>
	</div>

	<a4j:keepAlive beanName="configuraParametrosProcessosTecnicosMBean" />
	
	<h:form id="formAlterarParametrosBiblioteca">	
	
		

		<table class="formulario" style="width: 100%;">		
		
			<caption> Parâmetros do setor de Processos Técnicos (  ${fn:length(configuraParametrosProcessosTecnicosMBean.parametrosAlteracao)}  ) </caption>
		
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
							
							<%--  código biblioteca catalogo coletipo FGV  --%>
							<c:when test="${status.index == 0}"> 
								<h:inputText value="#{parametro.valor}" readonly="#{configuraParametrosProcessosTecnicosMBean.notAdministradorGeral}" size="40"/>
							</c:when>
							
							<%--  versão do programa que alterou o arquivo  --%>
							<c:when test="${status.index == 1}"> 
								<h:inputText value="#{parametro.valor}" readonly="#{configuraParametrosProcessosTecnicosMBean.notAdministradorGeral}" size="40" />
							</c:when>
							
							<%--  código da instituição da catalogação  --%>
							<c:when test="${status.index == 2}"> 
								<h:inputText value="#{parametro.valor}" readonly="#{configuraParametrosProcessosTecnicosMBean.notAdministradorGeral}" size="40"/>
							</c:when>
							
							<%--  idioma da catalogação  --%>
							<c:when test="${status.index == 3}"> 
								<h:inputText value="#{parametro.valor}" readonly="#{configuraParametrosProcessosTecnicosMBean.notAdministradorGeral}" size="40"/>
							</c:when>
							
							<%--   siglas trabalhos acadêmcios  --%>
							<c:when test="${status.index == 4}"> 
								<h:inputText value="#{parametro.valor}" readonly="#{configuraParametrosProcessosTecnicosMBean.notAdministradorGeral}" size="60"/>
							</c:when>
							
							<%--   código número controle bibliográfico  --%>
							<c:when test="${status.index == 5}"> 
								<h:inputText value="#{parametro.valor}" readonly="#{configuraParametrosProcessosTecnicosMBean.notAdministradorGeral}" size="40"/>
							</c:when>
							
							<%--   código número controle autoridade  --%>
							<c:when test="${status.index == 6}"> 
								<h:inputText value="#{parametro.valor}" readonly="#{configuraParametrosProcessosTecnicosMBean.notAdministradorGeral}" size="40"/>
							</c:when>
							
							<%--  dias de retardo para o material está disponível no acervo --%>
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
						<h:commandButton id="botaoAltualizarParametros" value="Atualizar Parâmetros" action="#{configuraParametrosProcessosTecnicosMBean.alterarParametros}" 
							onclick="return confirm('Confirma a atualização dos parâmetros do sistema ? ');" rendered="#{configuraParametrosProcessosTecnicosMBean.administradorGeral}" />
						<h:commandButton id="cmdCancelar" value="Cancelar" action="#{configuraParametrosCirculacaoMBean.cancelar}"  immediate="true" onclick="#{confirm}"  />
					</td>
				</tr>
			</tfoot>
		
		</table>

	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>