<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2><ufrn:subSistema /> > Contato com Discente</h2>

<f:view>
	<h:form id="form">
		<table class="formulario" width="100%">
			<tr>
				<td>
					<table class="subFormulario" width="100%">
						<caption>Formulario de contato (${ planoTrabalhoExtensao.inscricao.discente.pessoa.nome })</caption>
						  <tbody>
							<tr>
								<td width="15%" class="obrigatorio" style="text-align: right; padding-right: 10px;">Assunto:</td>
								<td><h:inputText value="#{ planoTrabalhoExtensao.inscricao.notificacao.titulo }" style="width: 90%" /></td>
							</tr>
							<tr>
								<td class="obrigatorio" style="text-align: right; padding-right: 10px;">Mensagem:</td>
								<td><h:inputTextarea value="#{ planoTrabalhoExtensao.inscricao.notificacao.mensagem}" style="width: 90%" rows="5" /></td>
							<tr>
							<tr>
								<td style="text-align: right; padding-right: 10px;">Enviar Email?</td>
								<td><h:selectBooleanCheckbox value="#{ planoTrabalhoExtensao.inscricao.notificacao.enviarEmail }"  /></td>
							<tr>
							<tr>
								<td style="text-align: right; padding-right: 10px;">Enviar Mensagem?</td>
								<td><h:selectBooleanCheckbox value="#{ planoTrabalhoExtensao.inscricao.notificacao.enviarMensagem }"  /></td>
							<tr>
						  </tbody>			
						  <tfoot>
							<tr>
								<td colspan=2>
									<h:commandButton value="Enviar" action="#{planoTrabalhoExtensao.enviarEmail}" />
									<input type="button" value="<< Voltar" onclick="history.go(-1)"/>
									<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{planoTrabalhoExtensao.cancelar}" immediate="true" /></td>
							</tr>
						  </tfoot>
					</table>
				</td>
			</tr>
		</table>
		<br/>
		<center>	
			<h:graphicImage  url="/img/required.gif" style="vertical-align: top;"/> 
			<span class="fontePequena"> Campos de preenchimento obrigatório. </span> 
		</center>
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>