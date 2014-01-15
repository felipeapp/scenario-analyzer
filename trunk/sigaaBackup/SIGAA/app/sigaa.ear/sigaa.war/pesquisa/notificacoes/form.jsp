<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<script src="/shared/javascript/tiny_mce/tiny_mce.js" type="text/javascript"></script>
<script type="text/javascript">
  tinyMCE.init({
        mode : "textareas",
        theme : "simple",
        width : "650",
        height : "200"
 });
</script>

<style>
	#tiposBolsa table { width: 100%; }
</style>

<f:view>
	<h:messages showDetail="true"></h:messages>
	<h2 class="title"><ufrn:subSistema /> &gt; Envio de mensagens a discentes de Iniciação Científica</h2>

	<h:form id="form">
		<table class="formulario" style="width: 95%;">
			<caption>Formulário de Envio de Mensagens</caption>
			<tr>
				<td colspan="3" class="subFormulario"> Selecione os discentes que receberão a mensagem (por tipo de bolsa) </td>
			</tr>
			<tr>
				<th> </th>
				<td colspan="2" id="tiposBolsa">
					<t:selectManyCheckbox value="#{notificacoesPesquisaBean.tiposBolsaSelecionados}" layoutWidth="4" layout="pageDirection">
						<f:selectItems value="#{notificacoesPesquisaBean.tiposBolsa}"/> 
					</t:selectManyCheckbox>
				</td>
			</tr>
			<tr>
				<td colspan="3" class="subFormulario"> Mensagem </td>
			</tr>
			<tr>
				<td width="1%"></td>
				<td width="10%" class="required">Assunto:</td>
				<td><h:inputText value="#{notificacoesPesquisaBean.obj.titulo}" style="width:95%;"/></td>
			</tr>
			<tr>
				<td></td>
				<td colspan="2">
					<h:inputTextarea value="#{notificacoesPesquisaBean.obj.mensagem}" style="width: 95%" rows="10"/>
				</td>
			</tr>
			<tr>
				<td><h:selectBooleanCheckbox value="#{notificacoesPesquisaBean.obj.enviarEmail}" id="checkEmail"/></td>
				<td colspan="2">
					<h:outputLabel for="checkEmail">
						<strong>Enviar notificação por e-mail</strong>
					</h:outputLabel>
				</td>
			</tr>
			<tr>
				<td><h:selectBooleanCheckbox value="#{notificacoesPesquisaBean.obj.enviarMensagem}" id="checkMensagem"/></td>
				<td colspan="2">
					<h:outputLabel for="checkMensagem">
						<strong>Enviar notificação através de mensagem para a caixa-postal dos sistemas</strong>
					</h:outputLabel>
				</td>
			</tr>
			<tfoot>
				<tr>
					<td colspan="3">
						<h:commandButton value="Enviar notificação" action="#{notificacoesPesquisaBean.enviar}" /> 
						<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{notificacoesPesquisaBean.cancelar}" />
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>
	<div class="obrigatorio"> Campos de preenchimento obrigatório. </div>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
