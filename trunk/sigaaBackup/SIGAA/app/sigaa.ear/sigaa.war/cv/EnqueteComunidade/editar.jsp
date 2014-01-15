<%@include file="/cv/include/cabecalho.jsp"%>
<f:view>
	
	<%@include file="/cv/include/_menu_comunidade.jsp" %>
	<%@include file="/cv/include/_info_comunidade.jsp" %>
	<div class="secaoComunidade">

	<h2>Editar Enquete</h2>
	<h:form>
		<h:inputHidden value="#{ enqueteComunidadeMBean.object.id }"/>
		<h:inputHidden value="#{ enqueteComunidadeMBean.paginaOrigem }"/>
		<table class="formulario" width="80%">
		<caption>Dados da Notícia</caption>
			<%@include file="/cv/EnqueteComunidade/_form.jsp"%>
			<tfoot>
				<tr> 
					<td colspan="2"> 
						<h:commandButton action="#{enqueteComunidadeMBean.atualizar}" value="Confirmar alteração" />
						<h:commandButton action="#{enqueteComunidadeMBean.listar}" value="Cancelar" onclick="#{confirm}" immediate="true"/>			
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>
	</div>
</f:view>
	
<%@include file="/WEB-INF/jsp/include/_campos_obrigatorios.jsp"%>	
<%@include file="/cv/include/rodape.jsp" %>

<%-- 
<%@include file="/cv/include/cabecalho.jsp"%>

<f:view>
	<h:form>

		<div id="wrapper"><h:messages showDetail="true" />
		<h:messages showDetail="true" />

		<div id="menuSuperior"><%@include file="/cv/include/menu_comunidade.jsp"%>
		</div>

		<div class="boxout">
		<h2>Comunidade Virtual <h:outputText value="#{comunidadeVirtualMBean.comunidade.descricao}" /></h2>
		<%@include file="/cv/include/info_comunidade.jsp"%></div>

		<div class="secaoComunidade">
		<h2>Editar Enquete</h2>

	<%@include file="/cv/EnqueteComunidade/_form.jsp" %>
	
	<h:inputHidden value="#{ enqueteComunidadeMBean.object.id }"/>
	
	<div class="botoes">
		<div class="form-actions">
			<h:commandButton action="#{enqueteComunidadeMBean.atualizar}" value="Atualizar Dados" /> 
		</div>
		<div class="other-actions">
			<h:commandLink action="#{ enqueteComunidadeMBean.mostrar }" value="Mostrar">
			<f:param name="id" value="#{ enqueteComunidadeMBean.object.id }"/>
			</h:commandLink> | <h:commandLink action="#{ enqueteComunidadeMBean.listar }" value="Voltar" onclick="return(confirm('Deseja realmente sair dessa página? Os dados informados serão perdidos.'));"/> 
		</div>
		<div class="required-items">
			<span class="required"/>
			Itens de Preenchimento Obrigatório. Devem existir ao menos duas respostas.
		</div>
	</div>

		</div>
	</div>
</h:form>
<%@include file="/cv/include/rodape.jsp"%>

</f:view>

<script src="/shared/javascript/tiny_mce/tiny_mce.js"
	type="text/javascript"></script>
<script type="text/javascript">
tinyMCE.init({
	mode : "textareas", theme : "advanced", width : "420", height : "250", language : "pt",
	theme_advanced_buttons1 : "cut,copy,paste,separator,search,replace,separator,bold,italic,underline,separator,strikethrough,justifyleft,justifycenter,justifyright,justifyfull,separator,bullist,numlist,image",
	theme_advanced_buttons2 : "fontselect,fontsizeselect,separator,undo,redo,separator,forecolor,backcolor,link,separator,sub,sup,charmap",
	theme_advanced_buttons3 : "",
	plugins : "searchreplace,contextmenu,advimage",
	theme_advanced_toolbar_location : "top",
	theme_advanced_toolbar_align : "left"
});
</script>
 --%>