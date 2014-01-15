<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> >  E-mails de Notificação das bibliotecas  </h2>
	
	<div class="descricaoOperacao"> 
    	<p> Entre com os e-mails a serem notificados na realização de serviço, separandos por <strong>";"</strong> </p>
    	<br/>
    	<br/>
	</div>
	
	<h:form id="formEmailsNotificacao">
	
		<a4j:keepAlive beanName="emailsNotificacaoServicosBibliotecaMBean" />
	
		<table class="formulario">
			<caption> E-mails de Notificação </caption>
			<tr>
				<th>Biblioteca:</th>
				<td><h:outputText value="#{emailsNotificacaoServicosBibliotecaMBean.obj.biblioteca.descricao}" /></td>
			</tr>
			<tr>
				<th>Tipo de Serviço:</th>
				<td><h:outputText value="#{emailsNotificacaoServicosBibliotecaMBean.obj.tipoServico.descricao}" /></td>
			</tr>
			<tr>
				<th class="obrigatorio">E-mails:</th>
				<td style="width: 70%;">
					<t:inputTextarea id="emailsNotificacao" value="#{emailsNotificacaoServicosBibliotecaMBean.obj.emailsNotificacao}" 
						disabled="#{! emailsNotificacaoServicosBibliotecaMBean.podeAlterarEmailsBiblioteca}"
						rows="5" cols="100" 
						onkeyup="textCounter(this, 'quantidadeCaracteresDigitados', 200);"/> 
				</td>
			</tr>
			<tr>
				<th style="font-weight: normal;">Caracteres Restantes:</th>
				<td>
					<span id="quantidadeCaracteresDigitados">200</span>/200
				</td>
			</tr>
			
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="Atualizar" action="#{emailsNotificacaoServicosBibliotecaMBean.atualizar}" rendered="#{emailsNotificacaoServicosBibliotecaMBean.podeAlterarEmailsBiblioteca}"/>
						<h:commandButton value="Cancelar" action="#{emailsNotificacaoServicosBibliotecaMBean.listar}" onclick="#{confirm}" immediate="true"/>
					</td>
				</tr>
			</tfoot>
		</table>
		<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp" %>
	</h:form>
</f:view>


<script language="javascript">
<!--
function textCounter(field, idMostraQuantidadeUsuario, maxlimit) {
	
	if (field.value.length > maxlimit){
		field.value = field.value.substring(0, maxlimit);
	}else{ 
		document.getElementById(idMostraQuantidadeUsuario).innerHTML = maxlimit - field.value.length ;
	} 
}
-->
</script>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
