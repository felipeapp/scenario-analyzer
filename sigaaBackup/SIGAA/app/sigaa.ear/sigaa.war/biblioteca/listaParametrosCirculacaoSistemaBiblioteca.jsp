<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>


<f:view>

	<h2>  <ufrn:subSistema /> &gt; Visualizar / Alterar Par�metros de Circula��o do M�dulo de Bibliotecas </h2>

	<div class="descricaoOperacao"> 
     	<p> Nesta p�gina � poss�vel visualizar e alterar os par�metros do setor de Circula��o do m�dulo de bibliotecas.</p>
	</div>

	<a4j:keepAlive beanName="configuraParametrosCirculacaoMBean" />
	
	<h:form id="formAlterarParametrosBiblioteca">	
	
		

		<table class="formulario" style="width: 100%;">		
		
			<caption> Par�metros do Setor de Circula��o (  ${fn:length(configuraParametrosCirculacaoMBean.parametrosAlteracao)}  ) </caption>
		
			<thead>
				<th style="text-align: left;">Nome</th>
				<th style="text-align: left;">Valor</th>
			</thead>
		
			<c:forEach var="parametro" items="#{configuraParametrosCirculacaoMBean.parametrosAlteracao}" varStatus="status">
				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
					<td style="width: 50%"> 
						<strong>${parametro.nome}</strong> <br/>
						<em> ${parametro.descricao} </em> 
					</td>
					<td style="width: 50%">
						<c:choose>							
							
						    <%--trabalha com suspensao --%>
							<c:when test="${status.index == 0}"> 
								<h:selectOneMenu value="#{parametro.valor}" disabled="#{configuraParametrosCirculacaoMBean.notAdministradorGeral}" >
									<f:selectItem itemLabel="SIM" itemValue="true" />
									<f:selectItem itemLabel="N�O" itemValue="false" />
								</h:selectOneMenu>
							</c:when>
							<%--trabalha com multa --%>
							<c:when test="${status.index == 1}"> 
								<h:selectOneMenu value="#{parametro.valor}" disabled="#{configuraParametrosCirculacaoMBean.notAdministradorGeral}" >
									<f:selectItem itemLabel="SIM" itemValue="true" />
									<f:selectItem itemLabel="N�O" itemValue="false" />
								</h:selectOneMenu>
							</c:when>
							<%--trabalha com reserva --%>
							<c:when test="${status.index == 2}"> 
								<h:selectOneMenu value="#{parametro.valor}" disabled="#{configuraParametrosCirculacaoMBean.notAdministradorGeral}" >
									<f:selectItem itemLabel="SIM" itemValue="true" />
									<f:selectItem itemLabel="N�O" itemValue="false" />
								</h:selectOneMenu>
							</c:when>
							
							<%--   sistema permite pol�ticas de empr�stimos diferentes por biblioteca  --%>
							<c:when test="${status.index == 3}"> 
								<h:selectOneMenu value="#{parametro.valor}" disabled="#{configuraParametrosCirculacaoMBean.notAdministradorGeral}" >
									<f:selectItem itemLabel="SIM" itemValue="true" />
									<f:selectItem itemLabel="N�O" itemValue="false" />
								</h:selectOneMenu>
							</c:when>
							
							<%--   dias suspenso por atraso dia  --%>
							<c:when test="${status.index == 4}"> 
								<h:inputText value="#{parametro.valor}" readonly="#{configuraParametrosCirculacaoMBean.notAdministradorGeral}" size="5" maxlength="2" onkeyup="return formatarInteiro(this);"/>
							</c:when>
							
							<%--    dias suspenso por atraso hora  --%>
							<c:when test="${status.index == 5}"> 
								<h:inputText value="#{parametro.valor}" readonly="#{configuraParametrosCirculacaoMBean.notAdministradorGeral}" size="5" maxlength="2" onkeyup="return formatarInteiro(this);"/>
							</c:when>
							
							<%--   valor multa atraso dia  --%>
							<c:when test="${status.index == 6}"> 
								<h:inputText value="#{parametro.valor}" readonly="#{configuraParametrosCirculacaoMBean.notAdministradorGeral}" size="10" maxlength="6" onkeydown="return(formataValor(this, event, 2))"/>
							</c:when>
							
							<%--  valor multa atraso hora  --%>
							<c:when test="${status.index == 7}"> 
								<h:inputText value="#{parametro.valor}" readonly="#{configuraParametrosCirculacaoMBean.notAdministradorGeral}" size="10" maxlength="6" onkeydown="return(formataValor(this, event, 2))"/>
							</c:when>
							
							<%--  prazo m�nimo entre empr�stimos --%>
							<c:when test="${status.index == 8}"> 
								<h:inputText value="#{parametro.valor}" readonly="#{configuraParametrosCirculacaoMBean.notAdministradorGeral}" size="10" maxlength="3" onkeyup="return formatarInteiro(this);"/>
							</c:when>
							
							<%--  prazo inrregularidade administrativa  --%>
							<c:when test="${status.index == 9}"> 
								<h:inputText value="#{parametro.valor}" readonly="#{configuraParametrosCirculacaoMBean.notAdministradorGeral}" size="10" maxlength="3" onkeyup="return formatarInteiro(this);"/>
							</c:when>
							
							<%--  mensagem alerta email empr�stimos em atraso  --%>
							<c:when test="${status.index == 10}"> 
								<h:inputTextarea value="#{parametro.valor}" readonly="#{configuraParametrosCirculacaoMBean.notAdministradorGeral}" rows="12" cols="60" />
							</c:when>
							
							<%--   qtd m�xima reservas   --%>
							<c:when test="${status.index == 11}"> 
								<h:inputText value="#{parametro.valor}" readonly="#{configuraParametrosCirculacaoMBean.notAdministradorGeral}" size="5" maxlength="2" onkeyup="return formatarInteiro(this);"/>
							</c:when>
							
							<%--    prazo em dias efetivar reserva  --%>
							<c:when test="${status.index == 12}"> 
								<h:inputText value="#{parametro.valor}" readonly="#{configuraParametrosCirculacaoMBean.notAdministradorGeral}" size="5" maxlength="3" onkeyup="return formatarInteiro(this);"/>
							</c:when>
							
							<%--   qtd m�nima solicitar reserva  --%>
							<c:when test="${status.index == 13}"> 
								<h:inputText value="#{parametro.valor}" readonly="#{configuraParametrosCirculacaoMBean.notAdministradorGeral}" size="5" maxlength="2" onkeyup="return formatarInteiro(this);"/>
							</c:when>
							
							<%--   prazo envio email emprestimo vencendo  --%>
							<c:when test="${status.index == 14}"> 
								<h:inputText value="#{parametro.valor}" readonly="#{configuraParametrosCirculacaoMBean.notAdministradorGeral}" size="5" maxlength="2" onkeyup="return formatarInteiro(this);"/>
							</c:when>
							
							<%--   link manual emprestimos  --%>
							<c:when test="${status.index == 15}"> 
								<h:inputTextarea value="#{parametro.valor}" rows="5" cols="60" readonly="#{configuraParametrosCirculacaoMBean.notAdministradorGeral}" />
							</c:when>
							
							<%--   link manual checkout  --%>
							<c:when test="${status.index == 16}"> 
								<h:inputTextarea value="#{parametro.valor}" rows="5" cols="60" readonly="#{configuraParametrosCirculacaoMBean.notAdministradorGeral}" />
							</c:when>
							
							<%--   mensagem sobre no desktop  --%>
							<c:when test="${status.index == 17}"> 
								<h:inputTextarea value="#{parametro.valor}" rows="8" cols="60" readonly="#{configuraParametrosCirculacaoMBean.notAdministradorGeral}" />
							</c:when>
							
							<%--   texto do termo de responsabilidade que o usuario ver� na ades�o ao sistema de bibliotecas  --%>
							<c:when test="${status.index == 18}"> 
								<h:inputTextarea value="#{parametro.valor}" rows="12" cols="60" readonly="#{configuraParametrosCirculacaoMBean.notAdministradorGeral}" />
							</c:when>
							
						</c:choose>
					</td>
				</tr>
			</c:forEach>	
		
			<tfoot>
				<tr>
					<td colspan="5" align="center">
						<h:commandButton id="botaoAltualizarParametros" value="Atualizar Par�metros" action="#{configuraParametrosCirculacaoMBean.alterarParametros}" 
							onclick="return confirm('Confirma a atualiza��o dos par�metros do sistema ? ');" rendered="#{configuraParametrosCirculacaoMBean.administradorGeral}" />
						<h:commandButton id="cmdCancelar" value="Cancelar" action="#{configuraParametrosCirculacaoMBean.cancelar}"  immediate="true" onclick="#{confirm}"  />
					</td>
				</tr>
			</tfoot>
		
		</table>

	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>