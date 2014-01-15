<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h:messages showDetail="true"></h:messages>
	<h2 class="title"><ufrn:subSistema /> &gt; Envio de e-mail para uma turma</h2>
	<a4j:keepAlive beanName="envioEmailTurma" />

	<h:form id="form">

		<div class="descricaoOperacao">
			<strong>Caro Usuário</strong>,
			<ufrn:checkNotRole papeis="<%= new int[] { SigaaPapeis.COORDENADOR_CURSO , SigaaPapeis.SECRETARIA_COORDENACAO } %>">
				<p>Nesta tela é possível enviar e-mails para os integrantes da turma virtual.</p>
			</ufrn:checkNotRole>
			<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.COORDENADOR_CURSO , SigaaPapeis.SECRETARIA_COORDENACAO } %>">
				<p>Nesta tela é possível enviar e-mails para os discentes da turma que fazem parte do seu curso.</p>
			</ufrn:checkRole>
		</div>

		<table class="formulario" style="width: 95%;">
			<caption>Formulário de Envio de E-mail</caption>
			<tr> 
				<td width="1%"></td>
				<th width="10%" ><b>Turma:</b></th>
				<td><b>${envioEmailTurma.turma.nome}</b></td>
			</tr>
			<tr>
				<td></td>
				<th class="required">
					<label for="form:titulo">Título: </label>
				</th>
				<td>
					<h:inputText maxlength="200" value="#{envioEmailTurma.titulo}" id="titulo" style="width: 95%"/>
				</td>
			</tr>
			<tr>
				<td></td>
				<th class="required">
					<label for="form:texto">Texto: </label>
				</th>
				<td>
					<h:inputTextarea id="texto" value="#{envioEmailTurma.texto}" style="width: 98%" rows="10"/>
				</td>
			</tr>
			<tfoot>
				<tr>
					<td colspan="3">
						<h:commandButton value="Enviar Email" action="#{envioEmailTurma.enviarEmail}" id="btnCadastrar"/>
						<input type="button" value="<< Voltar" onclick="javascript:history.go(-1)"/> 
						<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{envioEmailTurma.cancelar}" id="btnCancelar" immediate="true"/>
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>
	

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

<script src="/shared/javascript/tiny_mce/tiny_mce.js" type="text/javascript"></script>
<script type="text/javascript">
tinyMCE.init({
	mode : "textareas", theme : "advanced", width : "680", height : "250", language : "pt",
	theme_advanced_buttons1 : "cut,copy,paste,separator,search,replace,separator,bold,italic,underline,separator,strikethrough,justifyleft,justifycenter,justifyright,justifyfull,separator,bullist,numlist,image",
	theme_advanced_buttons2 : "fontselect,fontsizeselect,separator,undo,redo,separator,forecolor,backcolor,link,separator,sub,sup,charmap",
	theme_advanced_buttons3 : "",
	plugins : "searchreplace,contextmenu,advimage",
	theme_advanced_toolbar_location : "top",
	theme_advanced_toolbar_align : "left"
});
</script>