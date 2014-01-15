<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<c:set var="confirmDevolucao" value="if (!confirm('Confirma devolução do material ?')) return false" scope="request" />

<f:view>

	<a4j:keepAlive beanName="moduloCirculacaoMBean" />

	<h:form id="formDevolveEmpretimos">
	
	<h2><ufrn:subSistema /> &gt; Módulo de Circulação &gt; Devolução</h2>
	
	<div class="descricaoOperacao" style="width:80%;">
		<p>Para devolver um empréstimo, selecione o material emprestado digitando seu código de barras e clicando em "Devolver Empréstimo".</p>
	</div>
	
	
	<%--  Parte onde o usuário visualiza o comprovante da devolução   --%>
	<c:if test="${moduloCirculacaoMBean.habilitaComprovante}">
		
			<table  class="subFormulario" align="center">
				<caption style="text-align: center;">Impressão Comprovante</caption>
				<tr>
					<td width="8%" valign="middle" align="center">
						<html:img page="/img/warning.gif"/>
					</td>
					<td valign="middle" style="text-align: justify">
						Por favor, para uma maior segurança imprima o comprovante da devolução clicando no link ao lado.
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
							 		<h:commandLink title="Imprimir Comprovante"  target="_blank" value="COMPROVANTE" action="#{moduloCirculacaoMBean.geraComprovanteDevolucao}"  />
							 	</td>
							 </tr>
						</table>
					</td>
				</tr>
			</table>
		<br/>
		
	</c:if>
	
	
	<br/>
	
	
	<table class="formulario" style="width:90%;">
	
		<caption>Devolver Empréstimo</caption>
		<tbody>
			<tr>
				<td style="width: 30%;"/>
				<th style="width:20%;">Código de Barras:</th>
				<td style="width:10%">
					<h:inputText size="12" maxlength="20" id="codigoBarras" value="#{moduloCirculacaoMBean.codigoBarras}" onkeypress="return executaClickBotao(event, 'formDevolveEmpretimos:botaoBuscarMaterial' )" />
				</td>
				<td style="width:10%">
					<h:commandButton id="botaoBuscarMaterial" value="Buscar Material" action="#{moduloCirculacaoMBean.buscarMaterial}" />
				</td>
				<td style="width: 30%;"/>
			</tr>
		</tbody>
		
	</table>
	
	<t:div rendered="#{moduloCirculacaoMBean.material != null}" >
	
		<table class="formulario" style="width:90%;">		
			<tbody>
				
				<tr>
					<th style="vertical-align:top;">Material: &nbsp;</th>
					<td>${moduloCirculacaoMBean.material.informacao}</td>
				</tr>
				
				<tr>	
					<td colspan="2" style="text-align: center; height: 40px;">
						<h:outputText value="#{moduloCirculacaoMBean.descricaoPoliticaASerUtilizada}" />
					</td>
				</tr>	
				
				<tr>
					<td colspan="2">
						<c:set var="_infoUsuarioCirculacao" value="${moduloCirculacaoMBean.infoUsuario}" scope="request" />
						<c:set var="_mostrarFoto" value="${false}" scope="request" />
						<%@ include file="/biblioteca/circulacao/dadosUsuarioBiblioteca.jsp"%>
					</td>
				</tr>
				<tr>
					<th>Prazo:</th>
					<td><ufrn:format type="dataHora" valor="${moduloCirculacaoMBean.emprestimoDoMaterial.prazo}" /></td>
				</tr>
				
	
				<tr>
					<th>Tipo de Empréstimo:</th>
					<td>
						<h:selectOneMenu id="tipoEmprestimo" value="#{moduloCirculacaoMBean.idTipoEmprestimo}" disabled="true">
							<f:selectItems value="#{moduloCirculacaoMBean.tiposEmprestimos}" />
						</h:selectOneMenu>
					</td>
				</tr>
				
			</tbody>
			
		</table>
	
	</t:div>
	
	<table class="formulario" style="width:90%;">
		<tfoot>
			<tr>
				<td colspan="2">
					<h:commandButton id="cmdButtonDevolverMaterial" value="Devolver Empréstimo"  disabled="#{moduloCirculacaoMBean.material == null || moduloCirculacaoMBean.usuarioBiblioteca == null}" action="#{moduloCirculacaoMBean.realizarOperacao}" onclick="#{confirmDevolucao}"/>
					<h:commandButton id="cmdButtonCancelarDevolucao" value="Cancelar" action="#{moduloCirculacaoMBean.cancelar}" immediate="true" onclick="#{confirm}" />
				</td>
			</tr>
		</tfoot>
	</table>
	
	</h:form>
	
</f:view>

<script type="text/javascript" src="/shared/loadScript?src=javascript/biblioteca/functions.js"></script>

<script type="text/javascript">
	document.getElementById("formDevolveEmpretimos:codigoBarras").focus();
</script>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>