<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<f:view>

<h2><ufrn:subSistema /> > Enviar Mensagem aos Orientandos</h2>

<div id="ajuda" class="descricaoOperacao">
	<p>Caro Orientador, </p>
	<p>
	Esta opera��o tem o intuito de facilitar a comunica��o junto aos seus orientandos. <br/>
	Atrav�s desta p�gina voc� poder� enviar uma mensagem para todos os seus orientandos. Os alunos notificados receber�o uma mensagem via Email 
	e poder�o receber uma mensagem na caixa postal do SIGAA a sua escolha.
	</p>
</div>

<h:form enctype="multipart/form-data" id="form">
	<table class="formulario" width="100%" cellpadding="2">
		<caption>Informe o conte�do da mensagem</caption>
		<tbody>
			<tr>
				<th class="required" width="20%">Conte�do da Mensagem:</th>
				<td>
					<h:inputTextarea id="mensagem" style="width: 95%" rows="9" value="#{notificarOrientandos.obj.mensagem}" />
				</td>
			</tr>
			<tr>
				<th><h:selectBooleanCheckbox id="receberCopia" value="#{ notificarOrientandos.receberCopia }"/></th>
				<td>
					Receber uma c�pia da notifica��o.
				</td>
			</tr>
			<tr>
				<th><h:selectBooleanCheckbox id="enviarMensagem" value="#{ notificarOrientandos.obj.enviarMensagem}"/></th>
				<td>
					Enviar para caixa postal do SIGAA.
				</td>
			</tr>
			<tr>
				<th>
					Anexo:
				</th>			
				<td>
					<t:inputFileUpload value="#{notificarOrientandos.obj.anexo}" styleClass="file" id="arquivo" />
				</td>
			</tr>
		</tbody>
		<tfoot>
			<tr>
				<td colspan="2">
					<h:commandButton value="Cancelar" action="#{ notificarOrientandos.cancelar }" onclick="#{confirm}" id="cancelarr"/>
					<h:commandButton title="Enviar Mensagem"  value="Enviar Mensagem" action="#{notificarOrientandos.enviar}" id="ennviar"/>
				</td>
			</tr>
		</tfoot>	
	</table>
	<br/>
	<center>
	  <h:graphicImage  url="/img/required.gif" style="vertical-align: top;"/> <span class="fontePequena"> Campos de preenchimento obrigat�rio. </span>
	 </center>
	
</h:form>
	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>