<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@ taglib uri="/tags/a4j" prefix="a4j" %>

<f:view>	

	<a4j:keepAlive beanName="gerenciarParticipantesExtensaoMBean" />
	<a4j:keepAlive beanName="listaAtividadesParticipantesExtensaoMBean" />

	<h2><ufrn:subSistema /> > Notificar Participante</h2>
				
	<h:form id="form">
		
		<div class="descricaoOperacao">
			<p><strong>Atenção</strong> </p>
			<p>Enviar uma mensagem para o participante: ${ gerenciarParticipantesExtensaoMBean.cadastroSelecionadoCoordenador.nome}  <i>( ${ gerenciarParticipantesExtensaoMBean.cadastroSelecionadoCoordenador.email} ) </i>. </p>
		</div>	
		
		<table class="formulario" style="width: 100%;">
				<caption class="listagem">Enviar Mensagem para Participantes ( ${ gerenciarParticipantesExtensaoMBean.cadastroSelecionadoCoordenador.email} ) </caption>
									
					<tr>					
						<td> <h:inputTextarea value="#{ gerenciarParticipantesExtensaoMBean.mensagemEmailParticipantes }"  rows="20" cols="90" /> </td>
					</tr>			
					
					
					<tfoot>
						<tr>
							<td>
								<h:commandButton id="notificarParticipantes" value="Enviar" action="#{gerenciarParticipantesExtensaoMBean.notificarParticipantes}" />
								<h:commandButton value="Cancelar" action="#{gerenciarParticipantesExtensaoMBean.telaListaParticipantesParaGerenciar}" id="btCancelar" 
											immediate="true" onclick="#{confirm}"/> 			
							</td>
						</tr>
					</tfoot>
					
			</table>
	</h:form>
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