<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>

<f:view>
	
	<h2 class="tituloPagina"> <ufrn:subSistema/> &gt; Notificar de Solicitação de <h:outputText value="#{solicitacaoServicoDocumentoMBean.obj.tipoServico}"/>.</h2>
	
	<div class="descricaoOperacao"> 
	   <p>Utilize o formulário abaixo para notificar um usuário ou setor da biblioteca sobre o atendimento da solicitação abaixo. </p>
	</div>
	
	<a4j:keepAlive beanName="solicitacaoServicoDocumentoMBean"></a4j:keepAlive>

	<h:form id="formNotificarSolicitacao">
	
		<table class="formulario" width="80%">
			<caption>Notificação de Solicitação de <h:outputText value="#{solicitacaoServicoDocumentoMBean.obj.tipoServico}"/> número <h:outputText value="#{solicitacaoServicoDocumentoMBean.obj.numeroSolicitacao}"/> </caption>
			<tbody>
				<tr>
					<th>Usuário:</th>
					<td>
						${solicitacaoServicoDocumentoMBean.obj.pessoa.nome}
					</td>
				</tr>
			
				<tr>
					<th>Tipo do Documento:</th>
					<td>
						<h:outputText value="#{solicitacaoCatalogacaoMBean.obj.tipoDocumento.denominacao}"/>
						<c:if test="${solicitacaoCatalogacaoMBean.obj.tipoDocumento.tipoDocumentoOutro}">
									(  ${solicitacaoCatalogacaoMBean.obj.outroTipoDocumento}  )
						</c:if>	
					</td>
				</tr>
			
				<tr>
					<th>Biblioteca da Solicitação:</th>
					<td>
						${solicitacaoServicoDocumentoMBean.obj.biblioteca.descricao}
					</td>
				</tr>
				<tr>
					<th class="obrigatorio">Email:</th>
					<td>
						<h:inputText id="textEmail" size="50" maxlength="100"  value="#{solicitacaoServicoDocumentoMBean.emailNotificacao}" />
					</td>
				</tr>
				<tr>
					<th class="obrigatorio">Mensagem:</th>
					<td>
						<h:inputTextarea id="textAreasTextoNotificacao" value="#{solicitacaoServicoDocumentoMBean.textoNotificacao}"></h:inputTextarea>
					</td>
				</tr>
			</tbody>
			
			<tfoot>
				<tr>
					<td colspan="2" align="center">
						<h:commandButton id="cmdButtonNotificar" value="Notificar Solicitação" action="#{solicitacaoServicoDocumentoMBean.notificarSolicitacao}" onclick="return confirm('Confirma o envio da notificação ? ');"/>
						<h:commandButton id="cmdCancelarNotificacao" value="Cancelar" action="#{solicitacaoServicoDocumentoMBean.telaListaSolicitacoes}" onclick="#{confirm}" immediate="true" />
					</td>
				</tr>
			</tfoot>
			
		</table>
	</h:form>
	
	<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp" %>	
	
</f:view>


<script src="/shared/javascript/tiny_mce/tiny_mce.js" type="text/javascript"></script>
<script type="text/javascript">
tinyMCE.init({
	mode : "textareas", theme : "advanced", width : "100%", height : "250", language : "pt",
	theme_advanced_buttons1 : "cut,copy,paste,separator,search,replace,separator,bold,italic,underline,separator,strikethrough,justifyleft,justifycenter,justifyright,justifyfull,separator,bullist,numlist,image",
	theme_advanced_buttons2 : "fontselect,fontsizeselect,separator,undo,redo,separator,forecolor,backcolor,link,separator,sub,sup,charmap",
	theme_advanced_buttons3 : "",
	plugins : "searchreplace,contextmenu,advimage",
	theme_advanced_toolbar_location : "top",
	theme_advanced_toolbar_align : "left"
});
</script>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>