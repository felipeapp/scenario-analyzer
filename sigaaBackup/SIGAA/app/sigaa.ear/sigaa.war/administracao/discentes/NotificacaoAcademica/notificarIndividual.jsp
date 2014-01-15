<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<a4j:keepAlive beanName="notificacaoAcademica"></a4j:keepAlive>

<f:view>
	<h2 class="title"><ufrn:subSistema /> > Detalhes da Notificação</h2>

	<div class="descricaoOperacao">
	<b>Caro usuário,</b> 
	<br/><br/>
	Para enviar a notificação basta clicar em "Notificar" e confirmar a mensagem de segurança. 
	Uma vez que a notificação seja enviada, a operação não poderá ser desfeita. 
	</div>

	<h:form id="resumo">
		<table class="visualizacao">
			<caption class="listagem">Informações Referentes a Notificação Acadêmica</caption>
			<tbody>
			<tr>
				<td style="background-color:#ECF4FE;font-weight:bold" align="center" colspan=2>DISCENTE</td>
			</tr>
			<tr>
				<th width="30%" style="font-weight:normal">Matrícula:</th>
				<td><h:outputText value="#{notificacaoAcademica.discente.matricula}" /></td>
			</tr>
			<tr>
				<th style="font-weight:normal">Nome:</th>
				<td><h:outputText value="#{notificacaoAcademica.discente.pessoa.nome}" /></td>
			</tr>
			<tr>
				<th style="font-weight:normal">Curso:</th>
				<td>
					<h:outputText value="#{notificacaoAcademica.discente.curso.nome}" /> / 
					<h:outputText value="#{notificacaoAcademica.discente.curso.municipio.nome}" />
				</td>
			</tr>
			<tr>
				<td style="background-color:#ECF4FE;font-weight:bold" align="center" colspan=2>NOTIFICAÇÃO ACADÊMICA</td>
			</tr>
			<tr>
				<th style="font-weight:normal">Descrição:</th>
				<td><h:outputText value="#{notificacaoAcademica.obj.descricao}" /></td>
			</tr>
			<tr>
				<th style="font-weight:normal">Mensagem de Confirmação:</th>
				<td><h:outputText value="#{notificacaoAcademica.obj.mensagemNotificacao}" escape="false"/></td>
			</tr>
			<tr>
				<th style="font-weight:normal">Mensagem de E-mail:</th>
				<td><h:outputText value="#{notificacaoAcademica.obj.mensagemEmail}" escape="false"/></td>
			</tr>
			<tr>
				<th style="font-weight:normal">Necessita Confimirmação:</th>
				<td>
					<h:outputText value="Sim" rendered="#{notificacaoAcademica.obj.exigeConfirmacao}" />
					<h:outputText value="Não" rendered="#{!notificacaoAcademica.obj.exigeConfirmacao}" />
				</td>
			</tr>
			</tbody>
		</table>
		<br>
		<center>
			<h:commandButton 
				value="Cancelar" onclick="#{confirm}"
				action="#{notificacaoAcademica.cancelarIndividual}" /> 
			<h:commandButton 
				value="<< Voltar" 
				action="#{notificacaoAcademica.voltarForm}" /> 
			<h:commandButton
				value="Notificar" 
				action="#{notificacaoAcademica.notificarIndividual}" onclick="return(confirm('Deseja realmente notificar este discente?'));"/>
		</center>
	</h:form>
	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
