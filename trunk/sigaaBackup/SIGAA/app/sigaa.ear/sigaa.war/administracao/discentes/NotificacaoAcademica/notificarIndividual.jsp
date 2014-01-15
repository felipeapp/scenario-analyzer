<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<a4j:keepAlive beanName="notificacaoAcademica"></a4j:keepAlive>

<f:view>
	<h2 class="title"><ufrn:subSistema /> > Detalhes da Notifica��o</h2>

	<div class="descricaoOperacao">
	<b>Caro usu�rio,</b> 
	<br/><br/>
	Para enviar a notifica��o basta clicar em "Notificar" e confirmar a mensagem de seguran�a. 
	Uma vez que a notifica��o seja enviada, a opera��o n�o poder� ser desfeita. 
	</div>

	<h:form id="resumo">
		<table class="visualizacao">
			<caption class="listagem">Informa��es Referentes a Notifica��o Acad�mica</caption>
			<tbody>
			<tr>
				<td style="background-color:#ECF4FE;font-weight:bold" align="center" colspan=2>DISCENTE</td>
			</tr>
			<tr>
				<th width="30%" style="font-weight:normal">Matr�cula:</th>
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
				<td style="background-color:#ECF4FE;font-weight:bold" align="center" colspan=2>NOTIFICA��O ACAD�MICA</td>
			</tr>
			<tr>
				<th style="font-weight:normal">Descri��o:</th>
				<td><h:outputText value="#{notificacaoAcademica.obj.descricao}" /></td>
			</tr>
			<tr>
				<th style="font-weight:normal">Mensagem de Confirma��o:</th>
				<td><h:outputText value="#{notificacaoAcademica.obj.mensagemNotificacao}" escape="false"/></td>
			</tr>
			<tr>
				<th style="font-weight:normal">Mensagem de E-mail:</th>
				<td><h:outputText value="#{notificacaoAcademica.obj.mensagemEmail}" escape="false"/></td>
			</tr>
			<tr>
				<th style="font-weight:normal">Necessita Confimirma��o:</th>
				<td>
					<h:outputText value="Sim" rendered="#{notificacaoAcademica.obj.exigeConfirmacao}" />
					<h:outputText value="N�o" rendered="#{!notificacaoAcademica.obj.exigeConfirmacao}" />
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
