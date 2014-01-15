<%@page import="br.ufrn.sigaa.ouvidoria.dominio.ConfiguracaoOuvidoria"%>
<%@ include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

<f:view>
	<h2>Comprovante de Manifestação</h2>

	<table class="subFormulario" width="100%" style="background-color: #FFFFFF;">
		<tr>
			<td valign="middle" style="text-align: justify; font-size: 1.1em;">
				<p>Obrigado por entrar em contato conosco.</p><br />
				<p>Sua manifestação foi protocolada sob o número/ano <b><h:outputText value="#{manifestacaoOuvidoria.obj.numeroAno }" /></b> em <ufrn:format type="datahora" valor="${manifestacaoOuvidoria.obj.dataCadastro }"></ufrn:format>.</p><br />
				<p>Para acompanhar o andamento de sua manifestação, entre em contato com a Ouvidoria através do telefone <%=ConfiguracaoOuvidoria.TELEFONE%> e 
				informe o número e ano de seu protocolo, dirija-se pessoalmente até a Ouvidoria ou acesse um dos links oferecidos pelo sistema.</p><br />
			</td>
		</tr>
	</table>
	
	<br />
	
	<c:set var="manifestacao" value="#{manifestacaoOuvidoria.obj }" />
	<c:set var="interessado" value="#{manifestacao.interessadoManifestacao.dadosInteressadoManifestacao.pessoa}" />
	<c:set var="discenteInteressado" value="#{manifestacaoOuvidoria.discente}" />
	<c:set var="servidorInteressado" value="#{manifestacaoOuvidoria.servidor}" />
	<c:set var="interessadoNaoAutenticado" value="#{manifestacao.interessadoManifestacao.dadosInteressadoManifestacao.interessadoNaoAutenticado}" />
	<%@ include file="../include/dados_comprovante_impressao.jsp" %>

	<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
</f:view>