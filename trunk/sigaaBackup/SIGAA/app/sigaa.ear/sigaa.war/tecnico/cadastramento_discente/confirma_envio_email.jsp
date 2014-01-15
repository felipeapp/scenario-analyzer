<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<a4j:keepAlive beanName="cadastramentoDiscenteTecnico"></a4j:keepAlive>
<h2><ufrn:subSistema /> &gt; Enviar Email aos Candidatos</h2>

<h:form>

<table class="visualizacao">
	<caption>Enviar email a candidatos</caption>
	<tr>
		<th style="width:203px;">Processo Seletivo Vestibular:</th>
		<td><h:outputText id="psVest" value="#{cadastramentoDiscenteTecnico.processoSeletivo.nome}"/></td>
	</tr>
	<tr>
	<td colspan="2">
		<table style="width:100%;">
			<tr><th style="width:200px;">Assunto:</th><td><h:inputText value="#{ cadastramentoDiscenteTecnico.tituloEmail }" style="width:500px;" /></td></tr>
			<tr><th>Texto:</th><td><h:inputTextarea value="#{ cadastramentoDiscenteTecnico.textoEmail }"/></td></tr>
			<tfoot>
				<tr>
					<td colspan="2" style="text-align: center;">
						<h:commandButton value="Confirmar" action="#{ cadastramentoDiscenteTecnico.confirmarEnvioEmail }" id="btnConfirmar" />
						<h:commandButton value="<< Voltar" action="#{ cadastramentoDiscenteTecnico.telaFormulario }" id="btnVoltar"/>
					</td>
				</tr>
			</tfoot>
		</table>
	</td>
	</tr>
	<tr>
		<td colspan="2">
			<table class="subFormulario" width="100%">
				<caption>Destinatários (${fn:length(cadastramentoDiscenteTecnico.cadastrados)})</caption>
				<thead>
					<tr>
						<th style="text-align: left;">Nome</th>
						<th style="text-align: left;">Email</th>
					</tr>
				</thead>
				<c:choose>
					<c:when test="${not empty cadastramentoDiscenteTecnico.cadastrados}">
						<c:forEach items="#{cadastramentoDiscenteTecnico.cadastrados}" var="discente" varStatus="loop">
							<tr class="${loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
								<td><h:outputText value="#{discente.pessoa.nome}"/></td>
								<td><h:outputText value="#{discente.pessoa.email}"/></td>
							</tr>
						</c:forEach>
					</c:when>
					<c:otherwise>
						<td colspan="4" style="text-align: center; color: red;">Nenhum discente nesta categoria.</td>
					</c:otherwise>
				</c:choose>
			</table>
		</td>
	</tr>
</table>


</h:form>

</f:view>


<script src="/shared/javascript/tiny_mce/tiny_mce.js" type="text/javascript"></script>
<script type="text/javascript">
tinyMCE.init({
	mode : "textareas", theme : "advanced", width : "500", height : "250", language : "pt",
	theme_advanced_buttons1 : "cut,copy,paste,separator,search,replace,separator,bold,italic,underline,separator,strikethrough,justifyleft,justifycenter,justifyright,justifyfull,separator,bullist,numlist,image",
	theme_advanced_buttons2 : "fontselect,fontsizeselect,separator,undo,redo,separator,forecolor,backcolor,link,separator,sub,sup,charmap",
	theme_advanced_buttons3 : "",
	plugins : "searchreplace,contextmenu,advimage",
	theme_advanced_toolbar_location : "top",
	theme_advanced_toolbar_align : "left"
});
</script>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>