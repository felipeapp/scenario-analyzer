<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<a4j:keepAlive beanName="notificacaoAcademica"></a4j:keepAlive>
	<h2><ufrn:subSistema /> > Notifica��o Acad�mica</h2>

	<c:if test="${acesso.administradorSistema && !notificacaoAcademica.individual}">
		<div class="descricaoOperacao">
			<b>Caro usu�rio,</b> 
			<br/><br/>
			Nesta tela � poss�vel cadastrar/alterar uma notifica��o acad�mica. 
			<br/><br/>
			Para que o sql do filtro dos discentes seja v�lido � necess�rio que a busca seja feita apenas pelo id do discente. 
			<br/><br/>
			Tamb�m � poss�vel utilizar os tokens <b>":anoReferencia"</b> e <b>":periodoReferencia"</b>
			para parametrizar o ano e per�odo da consulta. Deste modo, o ano e per�odo ser�o requisitados no momento do envio.
			<br/><br/>
		</div>
	</c:if>
	<center>
			<h:messages showDetail="true"/>
			<h:form>
			<c:if test="${!notificacaoAcademica.individual}">
				<div class="infoAltRem" style="text-align: center; width: 100%">
					<h:graphicImage value="/img/listar.gif" style="overflow: visible;"/>
		  			<h:commandLink value="Listar Notifica��es" action="#{notificacaoAcademica.iniciar}"/>
				</div>
			</c:if>
			</h:form>
	</center>

	<div id="cadastrar">

	<table class="formulario" width="95%" cellpadding="8">
		<h:form>
			<caption class="listagem">Cadastro de Notifica��o Acad�mica</caption>
			<c:if test="${acesso.administradorSistema || notificacaoAcademica.individual}">
				<tr>
					<th>Descri��o:<span class="required">&nbsp;</span></th>
					<td><h:inputText size="100" id="txtDescricao" value="#{notificacaoAcademica.obj.descricao}"/>
					</td>
				</tr>
			</c:if>
			<tr>
				<th>Mensagem de E-Mail:<span class="required">&nbsp;</span></th>
				<td><h:inputTextarea id="txtMensagemEmail" styleClass="mcEditor" cols="60" rows="5" value="#{notificacaoAcademica.obj.mensagemEmail}" />
				</td>
			</tr>
			<tr>
				<th>Mensagem de Notifica��o:<span class="required">&nbsp;</span></th>
				<td>
				<h:inputTextarea id="txtMensagemConfirmacao" styleClass="mcEditor" cols="60" rows="5" value="#{notificacaoAcademica.obj.mensagemNotificacao}" />
				</td>
			</tr>
			<c:if test="${acesso.administradorSistema && !notificacaoAcademica.individual}">
				<tr>
					<th>Filtro de Discentes:<span class="required">&nbsp;</span></th>
					<td>
					<h:inputTextarea id="filtroDiscentes" cols="96" style="width:99%" rows="14" value="#{notificacaoAcademica.obj.sqlFiltrosDiscentes}" />
					</td>
					<td  width="8%">	
						<ufrn:help>
							Para o SQL digitado seja validado � necess�rio que a busca seja feita apenas pelo id do discente.<br/>
							Ex: SELECT d.id_discente FROM discente d ...
							<p>Tamb�m � poss�vel utilizar os tokens <b>":anoReferencia"</b> e <b>":periodoReferencia"</b> no sql do filtro, 
							assim, o sistema ir� substituir automaticamente tais tokens pelo ano e per�odo passados como par�metros no envio da notifica��o.</p>
						</ufrn:help>
					</td>
				</tr>
			</c:if>	
			<tr>
				<th><h:selectBooleanCheckbox id="exigeConfirmacao" value="#{ notificacaoAcademica.obj.exigeConfirmacao }"/></th>
				<td>
				<b>Esta notifica��o necessita confirma��o pelos discentes.</b> 
				</td>
			</tr>	
			<c:if test="${acesso.administradorSistema && !notificacaoAcademica.individual}">
				<tr>
					<th><h:selectBooleanCheckbox id="anoPeriodoReferencia" value="#{ notificacaoAcademica.obj.anoPeriodoReferencia }"/></th>
					<td>
					<b>Suporta ano e per�odo de refer�ncia.</b> 	
					<ufrn:help>
						<p>Ao utilizar os tokens <b>":anoReferencia"</b> e <b>":periodoReferencia"</b> no sql do filtro de discentes, 
						o sistema ir� substituir automaticamente tais tokens pelo ano e per�odo passados como par�metros no envio da notifica��o.</p>
					</ufrn:help>
					</td>
				</tr>	
			</c:if>	
			<tfoot>
				<tr>
					<td colspan=3>
					<c:if test="${!notificacaoAcademica.individual}">
						<h:commandButton value="#{notificacaoAcademica.confirmButton}" action="#{notificacaoAcademica.cadastrar}" /> 
						<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{notificacaoAcademica.voltar}" />
					</c:if>
					<c:if test="${notificacaoAcademica.individual}">
						<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{notificacaoAcademica.cancelarIndividual}" /> 
						<h:commandButton value="<< Voltar" action="#{notificacaoAcademica.redirectBuscarDiscente}" /> 
						<h:commandButton value="Notificar" action="#{notificacaoAcademica.confirmarIndividual}" /> 
					</c:if>
					</td>
				</tr>
			</tfoot>
		</h:form>
	</table>
	<div align="center">
		<span class="required">&nbsp;</span>
		Campos de Preenchimento Obrigat�rio
	</div>
	</div>
</f:view>

<script src="/shared/javascript/tiny_mce/tiny_mce.js" type="text/javascript"></script>
<script type="text/javascript">
tinyMCE.init({
	mode : "textareas", editor_selector : "mcEditor" , theme : "advanced", width : "420", height : "250", language : "pt",
	theme_advanced_buttons1 : "cut,copy,paste,separator,search,replace,separator,bold,italic,underline,separator,strikethrough,justifyleft,justifycenter,justifyright,justifyfull,separator,bullist,numlist,image",
	theme_advanced_buttons2 : "fontselect,fontsizeselect,separator,undo,redo,separator,forecolor,backcolor,link,separator,sub,sup,charmap",
	theme_advanced_buttons3 : "",
	plugins : "searchreplace,contextmenu,advimage",
	theme_advanced_toolbar_location : "top",
	theme_advanced_toolbar_align : "left"
});
</script>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>