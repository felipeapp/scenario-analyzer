<%@include file="/mobile/touch/include/cabecalho.jsp"%>

<a4j:keepAlive beanName="autenticacaoDocumentoTouch" />

<script src="/sigaa/mobile/touch/css/jquery.mobile.datebox-1.0.1.min.js"></script>
<link rel="stylesheet" type="text/css" href="/sigaa/mobile/touch/css/jquery.mobile.datebox-1.0.1.min.css"/>

<f:view>
	<div data-role="page" id="page-public-autenticar-documento" data-theme="b">
		<h:form id="form-autenticar-documento">
			<div data-role="header" data-position="inline" data-theme="b">
				<h1 style="white-space: normal;">${ configSistema['siglaInstituicao']} - ${configSistema['siglaSigaa'] } Mobile</h1>
			</div>
			
			<div data-role="navbar" data-theme="b" data-iconpos="left">
				<ul>
					<li><h:commandLink action="#{ autenticacaoDocumentoTouch.iniciarAutenticacao }" id="lnkVoltarTipo">Voltar</h:commandLink></li>
					<li><h:commandLink action="#{ portalPublicoTouch.forwardPortalPublico }" id="lnkInicio">Início</h:commandLink></li>
					<li><h:commandLink id="acessar" action="#{ loginMobileTouch.forwardPaginaLogin }" value="Entrar" /></li>
				</ul>
			</div>
		
			<div data-role="content">
				<p align="center"><strong>Validar Documento</strong></p>
				<%@include file="/mobile/touch/include/mensagens.jsp"%>
				<c:if test="${autenticacaoDocumentoTouch.tipoDocumentoComIdentificador }">
					<label class="obrigatorio">Identificador:</label> 
					<h:inputText value="#{autenticacaoDocumentoTouch.emissao.identificador }" />
				</c:if>
				<c:if test="${autenticacaoDocumentoTouch.tipoDocumentoComNumero }">
					<label class="obrigatorio">Número do Documento:</label> 
					<h:inputText value="#{autenticacaoDocumentoTouch.emissao.numeroDocumento }" />
				</c:if>
				<label class="obrigatorio">Data de Emissão:</label><br />
				<h:inputText value="#{autenticacaoDocumentoTouch.emissao.dataEmissao }" id="inputData">
					<f:converter converterId="convertData"/>
				</h:inputText><br />
				<label class="obrigatorio">Código de Verificação:</label>
				<h:inputText value="#{autenticacaoDocumentoTouch.emissao.codigoSeguranca }" />
				<label class="obrigatorio">Digite o conteúdo da imagem:</label>
				<h:graphicImage url="/captcha.jpg" /> <h:inputText value="#{autenticacaoDocumentoTouch.captcha}" size="8" style="font-size: 14px" id="captcha"/>
				<h:commandButton onclick="desativaAjax();" value="Validar Documento" action="#{autenticacaoDocumentoTouch.validar }" />
				<br/>
				<%@include file="/mobile/touch/include/mensagem_obrigatoriedade.jsp"%>
			</div>
			
			<%@include file="/mobile/touch/include/rodape_copyright.jsp"%>
			
			<script>
				jQuery.extend(jQuery.mobile.datebox.prototype.options.lang, {
				    'en': {
				        setDateButtonLabel: "Selecionar Data",
				        titleDateDialogLabel: "Selecionar Data",
				        daysOfWeek: ['Sunday', 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday'],
				        daysOfWeekShort: ['Su', 'Mo', 'Tu', 'We', 'Th', 'Fr', 'Sa'],
				        monthsOfYear: ['Janeiro', 'Fevereiro', 'Março', 'Abril', 'Maio', 'Junho', 'Julho', 'Agosto', 'Setembro', 'Outubro', 'Novembro', 'Dezembro'],
				        monthsOfYearShort: ['Jan', 'Fev', 'Mar', 'Abr', 'Mai', 'Jun', 'Jul', 'Ago', 'Set', 'Out', 'Nov', 'Dez'],
				        tooltip: "Abrir Calendário",
				        nextMonth: "Próximo Mês",
				        prevMonth: "Mês Anterior",
				        dateFieldOrder: ['d', 'm', 'y']
				    }
				});
			
				jQuery.extend(jQuery.mobile.datebox.prototype.options, {
				    'dateFormat': '%0d/%0m/%Y',
				    'headerFormat': '%0d/%0m/%Y'
				});
				
				function desativaAjax(){
					$("#form-autenticar-documento").attr("data-ajax", "false");
				}

				$("#form-autenticar-documento\\:lnkVoltarTipo").attr("data-icon", "back");
				$("#form-autenticar-documento\\:lnkInicio").attr("data-icon", "home");
				$("#form-autenticar-documento\\:acessar").attr("data-icon", "forward");
				$("#form-autenticar-documento\\:inputData").attr("data-role", "datebox");
				$("#form-autenticar-documento\\:inputData").attr("sdata-options", "{\"mode\": \"calbox\"}");
			</script>
		</h:form>
		<%@include file="/mobile/touch/include/modo_classico.jsp"%>
	</div>
</f:view>

<%@include file="/mobile/touch/include/rodape.jsp"%>
