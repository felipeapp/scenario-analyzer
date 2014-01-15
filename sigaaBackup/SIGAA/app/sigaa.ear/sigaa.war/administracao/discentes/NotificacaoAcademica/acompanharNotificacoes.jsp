<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<a4j:keepAlive beanName="notificacaoAcademica"></a4j:keepAlive>

	<h:form prependId="false">
	<h2 class="title"><ufrn:subSistema /> > Notificações Acadêmicas Enviadas</h2>
	
		<div class="descricaoOperacao">
	<b>Caro usuário,</b> 
	<br/><br/>
	Nesta tela será possível acompanhar as notificações acadêmicas enviadas para os discentes.
	</div>
	
		<table class="formulario"  width="40%">
		<caption>Informe os Critérios de Busca</caption>
		<tr >	
			<td align="right"><label for="busca:chkNome">Ano-Período: </label></td>
			<td>			
				<h:inputText value="#{ notificacaoAcademica.ano }" size="4" onkeypress="return ApenasNumeros(event);" maxlength="4" id="Ano" />-
				<h:inputText value="#{ notificacaoAcademica.periodo }" size="1" onkeypress="return ApenasNumeros(event);" maxlength="1" id="Período" />
			</td>
		</tr>
		<tfoot><tr><td colspan="3" style="text-align:center;">
			<h:commandButton action="#{notificacaoAcademica.filtrarEnviadas}"  value=" Filtrar "/>
			</td></tr></tfoot>
	</table>
	<br/>
	<c:if test="${ empty notificacaoAcademica.notificacoesEnviadas }">
	<p style="font-weight:bold;text-align:center;color:red;margin:20px;">Nenhum item foi encontrado</p>
	</c:if>
	
	<c:if test="${ not empty notificacaoAcademica.notificacoesEnviadas }">

			<center>
				<div class="infoAltRem" style="text-align: center; width: 100%">
					<h:graphicImage value="/img/buscar.gif"style="overflow: visible;"/>:
		  			<h:outputText value="Visualizar Discentes Notificados"/>
				</div>
			</center>

			<table class="formulario" >
				<caption class="listagem">Lista de Notificações Enviadas</caption>
				<thead>
					<tr>
						<th width="60%">Descrição</th>
						<th width="12%"><p align="center">Data de Envio</p></th>
						<th><p align="center">Exige Confirmação</p></th>
						<th><p align="right">Total de Enviadas</p></th>
						<th><p align="right">Total de Confirmadas</p></th>
						<th width="4%"></th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="#{ notificacaoAcademica.notificacoesEnviadas }" var="notificacao" varStatus="loop">
		
					
						<tr class="${ loop.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
						
							<td align="left">
								<h:outputText value="#{notificacao.descricao}"/>
							</td>
							<td width="12%" align="center">
								<ufrn:format type="datahora" valor="${notificacao.dataEnvio}"/>					
							</td>
							<td align="center">
								<h:outputText value="Sim" rendered="#{notificacao.exigeConfirmacao}"/>
								<h:outputText value="Não" rendered="#{!notificacao.exigeConfirmacao}"/>						
							</td>
							<td align="right">
								<h:outputText value="#{notificacao.qtdEnviadas}"/>				
							</td>
							<td align="right">
								<h:outputText value="#{notificacao.qtdConfirmadas}"/>					
							</td>
							<td align="right">
								<h:commandLink action="#{notificacaoAcademicaDiscente.visualizarDiscentes}" title="Visualizar Discentes Notificados" >
									<f:param name="id" value="#{notificacao.id}" />
									<f:param name="idRegistro" value="#{notificacao.idRegistroNotificacao}" />
									<f:param name="qtdEnviadas" value="#{notificacao.qtdEnviadas}" />
									<h:graphicImage value="/img/buscar.gif"style="overflow: visible;"/>
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
