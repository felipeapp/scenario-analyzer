<h:form id="form">
	<table class="formulario" width="100%">
		<caption>Formulário de Envio de Mensagens</caption>
		<tbody>
			<tr>
				<td width="11%" style="text-align: right;" class="required">Assunto: &nbsp;</td>
				<td><h:inputText id="assunto" value="#{notificacoes.obj.titulo}" style="width: 74%"/></td>
			</tr>
			<tr>
				<td width="11%" style="text-align: right;" class="required">Mensagem: &nbsp;</td>					
				<td><h:inputTextarea id="mensagem" rows="9" value="#{notificacoes.obj.mensagem}" /></td>
			</tr>			
			<tr>
				<td colspan="2">
					<h:selectBooleanCheckbox value="#{notificacoes.obj.enviarEmail}" id="checkEmail"/>
					<h:outputLabel for="checkEmail">
						Enviar notificação por e-mail
					</h:outputLabel>
				</td>
			</tr>
			<tr>
				<td colspan="2">
				    <h:selectBooleanCheckbox value="#{notificacoes.obj.enviarMensagem}" id="checkMensagem"/>
					<h:outputLabel for="checkMensagem">
						Enviar notificação através de mensagem para a caixa postal dos sistemas
					</h:outputLabel>
				</td>
			</tr>		
		</tbody>
		<tfoot>
			<tr>
				<td colspan="2">
					<h:commandButton title="Enviar Mensagem"  value="Enviar Mensagem" action="#{notificacoes.enviar}" />
					<h:commandButton value="Cancelar" action="#{ notificacoes.cancelar }" onclick="#{confirm}"/>
				</td>
			</tr>
		</tfoot>	
	</table>
	<br/>
	<center>
	  <h:graphicImage  url="/img/required.gif" style="vertical-align: top;"/> <span class="fontePequena"> Campos de preenchimento obrigatório. </span>
	 </center>
</h:form>