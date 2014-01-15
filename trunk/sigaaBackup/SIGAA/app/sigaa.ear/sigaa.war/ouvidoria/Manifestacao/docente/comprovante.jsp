<%@page import="br.ufrn.sigaa.ouvidoria.dominio.ConfiguracaoOuvidoria"%>
<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<a4j:keepAlive beanName="manifestacaoDocente" />

<f:view>
	<h:form>
		<h2>
			<ufrn:subSistema /> &gt; Comprovante da Manifestação
		</h2>
		<br />
		<table class="subFormulario" width="100%">
			<tr>
			<td width="8%" valign="middle" align="center">
				<html:img page="/img/warning.gif"/>
			</td>
			<td valign="middle" style="text-align: justify; font-size: 1.1em;">
				Sua manifestação foi cadastrada com sucesso sob o número de protocolo/ano <b><h:outputText value="#{manifestacaoDocente.obj.numeroAno }" /></b> em <ufrn:format type="datahora" valor="${manifestacaoDocente.obj.dataCadastro }"></ufrn:format>. <br /><br />
				É recomendado que anote o número de sua manifestação para acompanhamento, que pode ser feito via telefone (<%=ConfiguracaoOuvidoria.TELEFONE%>), pessoalmente na Ouvidoria 
				ou acessando a opção presente em SIGAA -> Portal do Docente -> Outros -> Ouvidoria -> Acompanhar Manifestações. <br /><br />
				Para imprimir este comprovante, basta clicar no ícone ao lado.
			</td>
			<td>
			<table>
				<tr>
					<td align="center">
						<h:commandLink title="Imprimir Comprovante"  target="_blank" action="#{manifestacaoDocente.imprimirComprovante }" id="printComprovante" >
				 			<h:graphicImage url="/img/printer_ok.png" />
				 		</h:commandLink>
				 	</td>
				 </tr>
				 <tr>
				 	<td style="font-size: medium;">
				 		<h:commandLink title="Imprimir Comprovante"  target="_blank" value="Imprimir" action="#{manifestacaoDocente.imprimirComprovante }"  id="imprimirComprovante"/>
				 	</td>
				 </tr>
			</table>
			</td>
			</tr>
		</table>
		
		<br />
		
		<c:set var="manifestacao" value="#{manifestacaoDocente.obj }" />
		<c:set var="interessado" value="#{manifestacao.interessadoManifestacao.dadosInteressadoManifestacao.pessoa}" />
		<c:set var="servidorInteressado" value="#{manifestacaoDocente.servidorUsuario}" />
		<c:set var="arquivo" value="#{manifestacaoDocente.arquivo }" />
		<%@ include file="../include/dados_comprovante_impressao.jsp" %>
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>