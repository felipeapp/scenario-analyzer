<%@page import="br.ufrn.sigaa.ouvidoria.dominio.ConfiguracaoOuvidoria"%>
<%@ include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<f:view>
	<h2>Comprovante de Manifesta��o</h2>

	<table class="subFormulario" width="100%" style="background-color: #FFFFFF;">
		<tr>
			<td valign="middle" style="text-align: justify; font-size: 1.1em;">
				<p>Obrigado por entrar em contato conosco.</p><br />
				<p>Sua manifesta��o foi protocolada sob o n�mero/ano <b><h:outputText value="#{manifestacaoComunidadeExterna.obj.numeroAno }" /></b> em <ufrn:format type="datahora" valor="${manifestacaoComunidadeExterna.obj.dataCadastro }"></ufrn:format>.</p><br />
				<p>Para acompanhar o andamento de sua manifesta��o, entre em contato com a Ouvidoria atrav�s do telefone <%=ConfiguracaoOuvidoria.TELEFONE%> e informe o n�mero e ano de seu protocolo ou dirija-se pessoalmente at� a Ouvidoria.</p><br />
			</td>
		</tr>
	</table>
	
	<br />
	
	<c:set var="manifestacao" value="#{manifestacaoComunidadeExterna.obj }" />
	<c:set var="interessadoNaoAutenticado" value="#{manifestacaoComunidadeExterna.interessadoNaoAutenticado}" />
	<%@ include file="/ouvidoria/Manifestacao/include/dados_comprovante_impressao.jsp" %>

	<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
</f:view>