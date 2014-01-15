<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<c:set var="confirmRenovacao" value="if (!confirm('Confirma renova��o do material ?')) return false" scope="request" />



<f:view>

	<a4j:keepAlive beanName="moduloCirculacaoMBean" />

	<h:form id="form">
	
	<h2><ufrn:subSistema /> &gt; M�dulo de Circula��o &gt; Renova��o</h2>
	
	<div class="descricaoOperacao" style="width:80%;">
		<p>Para renovar um empr�stimo, informe o material emprestado digitando seu c�digo de barras e clicando em "Renovar Empr�stimo".</p>
	</div>
	
	<%--  Parte onde o usu�rio visualiza o comprovante da devolu��o   --%>
	<t:div rendered="#{moduloCirculacaoMBean.habilitaComprovanteRenovacao}">
		
			<table  class="subFormulario" align="center">
				<caption style="text-align: center;">Impress�o Comprovante</caption>
				<tr>
					<td width="8%" valign="middle" align="center">
						<html:img page="/img/warning.gif"/>
					</td>
					<td valign="middle" style="text-align: justify">
						Por favor, para uma maior seguran�a imprima o comprovante da renova��o clicando no link ao lado.
					</td>
					<td>
						<table>
							<tr>
								<td align="center">
							 		<h:graphicImage url="/img/printer_ok.png" />
							 	</td>
							 </tr>
							 <tr>
							 	<td style="font-size: medium;">
							 		<h:commandLink title="Imprimir Comprovante"  target="_blank" value="COMPROVANTE" action="#{moduloCirculacaoMBean.geraComprovanteRenovacao}"  />
							 	</td>
							 </tr>
						</table>
					</td>
				</tr>
			</table>
		<br/>
		
	</t:div>
	
	
	<br>
	<table class="formulario" style="width:90%;">
	
		<caption>Renovar Empr�stimo</caption>
		<tbody>
			<tr>
				<th style="width:150px;">C�digo de Barras:</th>
				<td>
					<h:inputText id="codigoBarras" size="12" maxlength="20" value="#{moduloCirculacaoMBean.codigoBarras}" onkeypress="return executaClickBotao(event, 'form:botaoBuscarMaterial' )" />
					<h:commandButton id="botaoBuscarMaterial" value="Buscar" action="#{moduloCirculacaoMBean.buscarMaterial}" />
				</td>
			</tr>
		<tbody>	
	</table>
	
	<t:div rendered="#{moduloCirculacaoMBean.material == null}">
		<table class="formulario" style="width:90%;">
			<tr>
				<th>Material:</th>
				<td style="font-weight:bold; color: red;">Informe um Material</td>
			</tr>
		</table>
	</t:div>
	
	<t:div rendered="#{moduloCirculacaoMBean.material != null}">
	
		<table class="formulario" style="width:90%;">		
			<tr>
				<th style="vertical-align:top;">Material: &nbsp; </th>
				<td>${moduloCirculacaoMBean.material.informacao}</td>
			</tr>
			<tr>
				<td colspan="2" style="text-align: center;" >
					<strong>
						<c:if test="${moduloCirculacaoMBean.material.emprestado}">
							<p style='color:#00AA00;'>Material Emprestado.</p>
						</c:if>
						
						<c:if test="${not moduloCirculacaoMBean.material.emprestado}">
							
							<p style='color:#FF0000;'>Material n�o est� Emprestado.</p>
							
						</c:if>
					</strong>
				</td>
			</tr>
			<tr>	
				<td colspan="2" style="text-align: center; height: 40px;">
					<h:outputText value="#{moduloCirculacaoMBean.descricaoPoliticaASerUtilizada}" />
				</td>
			</tr>	
			
		</table>
	</t:div>
	
	<t:div rendered="#{moduloCirculacaoMBean.material != null && moduloCirculacaoMBean.material.emprestado}">			
		<table class="formulario" style="width:90%;">		
			<tr>
				<td colspan="2">
					<c:set var="_infoUsuarioCirculacao" value="${moduloCirculacaoMBean.infoUsuario}" scope="request" />
					<c:set var="_mostrarFoto" value="${false}" scope="request" />
					<%@ include file="/biblioteca/circulacao/dadosUsuarioBiblioteca.jsp"%>
				</td>
			</tr>
			<tr>
				<th>Data do empr�stimo:</th>
				<td><ufrn:format type="dataHora" valor="${moduloCirculacaoMBean.emprestimoDoMaterial.dataEmprestimo}" /></td>
			</tr>
			<tr>
				<th>Prazo:</th>
				<td><ufrn:format type="dataHora" valor="${moduloCirculacaoMBean.emprestimoDoMaterial.prazo}" /></td>
			</tr>
			
			<tr>
				<td colspan="2">
					<table style="width: 30%; margin-left: auto; margin-right: auto;">
						<th style="font-weight: bold;">Senha da Biblioteca:</th>
						<td> 
							<h:inputSecret id="inputSecretSenhaBiblioteca"  value="#{moduloCirculacaoMBean.senhaBiblioteca}"  
								onkeypress="return executaClickBotao(event, 'form:botaoRenovarEmprestimo' )" onkeyup="return formatarInteiro(this);" size="12" maxlength="8" />
						</td>
					</table>
				</td>
			</tr>
			
			<tr id="linhaTipoEmprestimo">
				<th>Tipo de Empr�stimo:</th>
				<td>
					<h:selectOneMenu id="tipoEmprestimo" value="#{moduloCirculacaoMBean.idTipoEmprestimo}" disabled="true">
						<f:selectItems value="#{moduloCirculacaoMBean.tiposEmprestimos}" />
					</h:selectOneMenu>
				</td>
			</tr>
			
		</table>
	</t:div>				
				
			
	<table class="formulario" style="width:90%;">				
		<tfoot>
			<tr>
				<td colspan="2">
					<h:commandButton id="botaoRenovarEmprestimo" value="Renovar Empr�stimo" action="#{moduloCirculacaoMBean.realizarOperacao}" onclick="#{confirmRenovacao}"
						disabled="#{moduloCirculacaoMBean.material == null || ! moduloCirculacaoMBean.material.emprestado || moduloCirculacaoMBean.usuarioBiblioteca == null }" />
					<h:commandButton value="Cancelar" action="#{moduloCirculacaoMBean.cancelar}" immediate="true" onclick="#{confirm}" />
				</td>
			</tr>
		</tfoot>
	</table>
	
	</h:form>
	
</f:view>

<script>
	<c:if test="${moduloCirculacaoMBean.material == null}">
		document.getElementById("form:codigoBarras").focus();
	</c:if>
	
	<c:if test="${moduloCirculacaoMBean.material != null}">
		document.getElementById("form:inputSecretSenhaBiblioteca").focus();
	</c:if>
	
	//fun��o que executa o click no botao passado quando o usu�rio pressiona o enter
	function executaClickBotao(evento, idBotao) {
		
		var tecla = "";
		if (isIe())
			tecla = evento.keyCode;
		else
			tecla = evento.which;

		if (tecla == 13){
			document.getElementById(idBotao).click();
			return false;
		}
		
		return true;
		
	}	

	// testa se � o IE ou n�o
	function isIe() {
		return (typeof window.ActiveXObject != 'undefined');
	}

	
</script>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>