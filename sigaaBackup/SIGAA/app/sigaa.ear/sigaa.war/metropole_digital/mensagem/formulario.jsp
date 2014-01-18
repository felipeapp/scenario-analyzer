<h:form enctype="multipart/form-data" id="form">
	<table class="formulario" width="100%">
		<caption>Formulário de Envio de Mensagens</caption>
		<tbody>
			<tr>
				<td width="11%" style="text-align: right;" class="required">Assunto: &nbsp;</td>
				<td><h:inputText id="assunto" value="#{notificacoesIMD.obj.titulo}" style="width: 74%"/></td>
			</tr>
			<tr>
				<td width="11%" style="text-align: right;" class="required">Mensagem: &nbsp;</td>					
				<td width="89%"><h:inputTextarea id="mensagem" rows="9"  value="#{notificacoesIMD.obj.mensagem}" /></td>
			</tr>
			<tr>
				<td>
					Anexo:
				</td>			
				<td>
					<t:inputFileUpload value="#{notificacoesIMD.obj.anexo}" styleClass="file" id="arquivo" />
				</td>
			</tr>			
			<tr>
				<td colspan="2">
					<h:selectBooleanCheckbox value="#{notificacoesIMD.obj.enviarEmail}" id="checkEmail"/>
					<h:outputLabel for="checkEmail">
						Enviar notificação por e-mail
					</h:outputLabel>
				</td>
			</tr>
			<tr>
				<td colspan="2">
				    <h:selectBooleanCheckbox value="#{notificacoesIMD.obj.enviarMensagem}" id="checkMensagem"/>
					<h:outputLabel for="checkMensagem">
						Enviar notificação através de mensagem para a caixa postal dos sistemas
					</h:outputLabel>
				</td>
			</tr>		
		</tbody>
		<tfoot>
			<tr>
				<td colspan="2">
					<h:commandButton title="Enviar Mensagem"  value="Enviar Mensagem" action="#{notificacoesIMD.enviar}" />
					<h:commandButton value="<< Voltar" action="#{relatoriosCoordenadorTutoresIMD.listagemExecucaoFrequenciaNotas}" />
					<h:commandButton value="Cancelar" action="#{ notificacoesIMD.cancelar }" onclick="#{confirm}"/>
				</td>
			</tr>
		</tfoot>	
	</table>
	<br/>
	<center>
	  <h:graphicImage  url="/img/required.gif" style="vertical-align: top;"/> <span class="fontePequena"> Campos de preenchimento obrigatório. </span>
	 </center>
</h:form>