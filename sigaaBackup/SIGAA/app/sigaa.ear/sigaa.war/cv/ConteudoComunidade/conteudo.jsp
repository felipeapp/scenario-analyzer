<%@include file="/cv/include/cabecalho.jsp"%>

<f:view>
	<h:form>

		<div id="wrapper"><h:messages showDetail="true" />
		<h:messages showDetail="true" />

		<div id="menuSuperior">
			<%@include file="/cv/include/menu_comunidade.jsp"%>
		</div>

		<div class="boxout">
		<h2>Comunidade Virtual <h:outputText value="#{comunidadeVirtualMBean.comunidade.descricao}" /></h2>
		<%@include file="/cv/include/info_comunidade.jsp"%></div>

		<div class="secaoComunidade">
		<h2>Visualização de Conteúdo</h2>
	
<h:messages showDetail="true" />

	
<ul class="show">
	<li>
		<label>Título: </label>
		<div class="campo">		 
		${conteudoComunidadeMBean.object.titulo}
		</div>
	</li>
	
	<li>
		<label>Conteúdo: </label>
		<div class="campo">		 
		${conteudoComunidadeMBean.object.conteudo}
		</div>
	</li>
	
	<li>		
		<label>Data cadastro: </label>
		<div class="campo">		
			<fmt:formatDate value="${conteudoComunidadeMBean.object.dataCadastro}" pattern="dd/MM/yyyy"/>
		</div>
	</li>

</ul>




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
