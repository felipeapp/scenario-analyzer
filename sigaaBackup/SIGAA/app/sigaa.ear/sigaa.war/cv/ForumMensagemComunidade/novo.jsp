<%@include file="/cv/include/cabecalho.jsp"%>
<f:view>

	<%@include file="/cv/include/_menu_comunidade.jsp" %>
	<%@include file="/cv/include/_info_comunidade.jsp" %>
	<div class="secaoComunidade">
	
	<rich:panel header="Novo Tópico">
	<h:form>
		<table class="formulario" width="80%">
		<caption>Novo Tópico</caption>
			<%@include file="/cv/ForumMensagemComunidade/_form.jsp"%>
			<tfoot>
				<tr> 
					<td colspan="2"> 
						<h:commandButton action="#{forumMensagemComunidadeMBean.cadastrarTopico}" value="Cadastrar" />
						<h:commandButton action="#{noticiaComunidadeMBean.listar}" value="Cancelar" onclick="#{confirm}" immediate="true"/>			
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>
	</rich:panel>
	</div>
</f:view>

<%@include file="/WEB-INF/jsp/include/_campos_obrigatorios.jsp"%>	
<%@include file="/cv/include/rodape.jsp" %>

<%--
<%@include file="/cv/include/cabecalho.jsp" %>

<f:view>
	<h:form>
		
			
<div id="wrapper">
	
	<h:messages showDetail="true" />
	
	<div id="menuSuperior">
		<%@include file="/cv/include/_menu_comunidade.jsp" %>
	</div>
	
	<div class="boxout">
		<h2>Comunidade Virtual <h:outputText value="#{comunidadeVirtualMBean.comunidade.descricao}" /></h2>
		<%@include file="/cv/include/_info_comunidade.jsp" %>
	</div>
	
	<div class="secaoComunidade">
		<h2> Dados do Tópico </h2>
	
		<%@include file="/cv/ForumMensagemComunidade/_form.jsp" %>
	
		<div class="botoes">
			<div class="form-actions">
				<h:commandButton action="#{forumMensagemComunidadeMBean.cadastrarMensagem}" value="Cadastrar" /> 
			</div>
			<div class="other-actions">
				<h:commandLink action="#{ forumMensagemComunidadeMBean.listar }" value="Voltar" onclick="return(confirm('Deseja realmente sair dessa página? Os dados informados serão perdidos.'));"/> 
			</div>
			<div class="required-items">
				<span class="required"/>
				Itens de Preenchimento Obrigatório
			</div>
		</div>
		
	</div>

</div>

</h:form>
<%@include file="/cv/include/rodape.jsp" %>

</f:view>

<script src="/shared/javascript/tiny_mce/tiny_mce.js"
	type="text/javascript"></script>
<script type="text/javascript">
tinyMCE.init({
	mode : "textareas", theme : "advanced", width : "520", height : "250", language : "pt",
	theme_advanced_buttons1 : "cut,copy,paste,separator,search,replace,separator,bold,italic,underline,separator,strikethrough,justifyleft,justifycenter,justifyright,justifyfull,separator,bullist,numlist,image",
	theme_advanced_buttons2 : "fontselect,fontsizeselect,separator,undo,redo,separator,forecolor,backcolor,link,separator,sub,sup,charmap",
	theme_advanced_buttons3 : "",
	plugins : "searchreplace,contextmenu,advimage",
	theme_advanced_toolbar_location : "top",
	theme_advanced_toolbar_align : "left"
});
</script>
 --%>
