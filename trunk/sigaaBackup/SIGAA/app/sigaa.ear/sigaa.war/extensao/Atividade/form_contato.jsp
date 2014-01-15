<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2><ufrn:subSistema /> > Contato com Coordenador</h2>

<f:view>
	<h:form id="form">
		<table class="formulario" width="100%">
			<tr>
				<td>
					<table class="subFormulario" width="100%">
						<caption>Formulario de contato (${ agregadorBolsas.destinarioMensagem.pessoa.nome })</caption>
						<tbody>
							<tr>
								<td width="8%">Assunto:</td>
								<td><h:inputText value="#{agregadorBolsas.mensagem.titulo}" style="width: 90%" /></td>
							</tr>
							<tr>
								<td width="8%" class="obrigatorio">Mensagem:</td>
								<td><h:inputTextarea value="#{agregadorBolsas.mensagem.mensagem}" style="width: 89%" rows="5" /></td>
							<tr>
						</tbody>			
						<tfoot>
							<tr>
								<td colspan=2>
									<h:commandButton value="Enviar" action="#{agregadorBolsas.enviarMensagem}" />
									<h:commandButton value="<< Voltar" action="#{agregadorBolsas.voltarListagem}" immediate="true" />
									<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{agregadorBolsas.cancelar}" immediate="true" /></td>
							</tr>
						</tfoot>
					</table>
				</td>
			</tr>
		</table>
		<br/><center>	<h:graphicImage  url="/img/required.gif" style="vertical-align: top;"/> <span class="fontePequena"> Campos de preenchimento obrigatório. </span> </center>
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>