<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2><ufrn:subSistema /> > Contato com Discentes</h2>

<f:view>
	<div class="descricaoOperacao">
			<p>Caro Usuário,</p>
		<p>
			Digite o assunto e o texto da comunicação a ser enviada. Marque <b>Enviar
				E-mail</b> caso deseje que o candidato receba o comunicado por e-mail.
			Marque <b>Enviar Mensagem</b> se desejar que o candidato receba
			apenas uma mensagem via SIGAA.
		</p>
	</div>
	<h:form id="form">
			<table class="formulario" width="100%">
				<caption>Formulário de Contato</caption>
				  	<tr>
				  		<th width="20%">Discentes que Serão Notificados:</th>
			  			<td>
				  			<c:forEach items="#{ discenteMonitoria.grupoEmail }" var="item" varStatus="status">
				  				<c:if test="${ status.index > 0}">, </c:if>
				  				<h:outputText value="#{ item.discente.matriculaNome }"/>
			  				</c:forEach>
			  			</td>
				  	</tr>
					<tr>
						<th class="obrigatorio" style="text-align: right; padding-right: 10px;">Assunto:</th>
						<td><h:inputText value="#{ discenteMonitoria.inscricaoSelMonitoria.notificacao.titulo }" style="width: 90%" /></td>
					</tr>
					<tr>
						<th class="obrigatorio"  style="text-align: right; padding-right: 10px;">Mensagem:</th>
						<td><h:inputTextarea value="#{ discenteMonitoria.inscricaoSelMonitoria.notificacao.mensagem}" style="width: 90%" rows="5" /></td>
					<tr>
					<tr>
						<th style="text-align: right; padding-right: 10px;">Enviar Email?</th>
						<td><h:selectBooleanCheckbox value="#{ discenteMonitoria.inscricaoSelMonitoria.notificacao.enviarEmail }"  /></td>
					<tr>
					<tr>
						<th style="text-align: right; padding-right: 10px;">Enviar Mensagem?</th>
						<td><h:selectBooleanCheckbox value="#{ discenteMonitoria.inscricaoSelMonitoria.notificacao.enviarMensagem }"  /></td>
					<tr>
				  <tfoot>
					<tr>
						<td colspan=2>
							<h:commandButton value="Enviar" action="#{discenteMonitoria.enviarEmail}" />
							<h:commandButton value="<< Voltar" action="#{discenteMonitoria.inicioEnvioEmail}" immediate="true" />
							<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{provaSelecao.iniciarProcessoSeletivo}" immediate="true" /></td>
					</tr>
				  </tfoot>
			</table>
		<br/>
		<center>	
			<h:graphicImage  url="/img/required.gif" style="vertical-align: top;"/> 
			<span class="fontePequena"> Campos de preenchimento obrigatório. </span> 
		</center>
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>