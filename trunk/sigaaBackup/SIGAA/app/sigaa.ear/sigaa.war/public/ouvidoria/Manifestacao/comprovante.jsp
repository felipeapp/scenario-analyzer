<%@ include file="/public/include/cabecalho.jsp" %>
<%@page import="br.ufrn.sigaa.ouvidoria.dominio.ConfiguracaoOuvidoria"%>
<a4j:keepAlive beanName="manifestacaoComunidadeExterna" />

<f:view>
	<h:form>
		<br />
		<table class="subFormulario" width="100%">
			<tr>
			<td width="8%" valign="middle" align="center">
				<html:img page="/img/warning.gif"/>
			</td>
			<td valign="middle" style="text-align: justify; font-size: 1.1em;">
				Sua manifestação foi cadastrada com sucesso sob o número de protocolo/ano <b><h:outputText value="#{manifestacaoComunidadeExterna.obj.numeroAno }" /></b> em <ufrn:format type="datahora" valor="${manifestacaoComunidadeExterna.obj.dataCadastro }"></ufrn:format>. <br /><br />
				É recomendado que anote o número de sua manifestação para acompanhamento, que pode ser feito via telefone (<%=ConfiguracaoOuvidoria.TELEFONE%>) ou pessoalmente na Ouvidoria. <br /><br />
				Para imprimir este comprovante, basta clicar no ícone ao lado.
			</td>
			<td>
			<table>
				<tr>
					<td align="center">
						<h:commandLink title="Imprimir Comprovante"  target="_blank" action="#{manifestacaoComunidadeExterna.imprimirComprovante }" id="printComprovante" >
				 			<h:graphicImage url="/img/printer_ok.png" />
				 		</h:commandLink>
				 	</td>
				 </tr>
				 <tr>
				 	<td style="font-size: medium;">
				 		<h:commandLink title="Imprimir Comprovante"  target="_blank" value="Imprimir" action="#{manifestacaoComunidadeExterna.imprimirComprovante }"  id="imprimirComprovante"/>
				 	</td>
				 </tr>
			</table>
			</td>
			</tr>
		</table>
		
		<br />
		
		<c:set var="manifestacao" value="#{manifestacaoComunidadeExterna.obj }" />
		<c:set var="interessadoNaoAutenticado" value="#{manifestacaoComunidadeExterna.interessadoNaoAutenticado}" />
		<c:set var="arquivo" value="#{manifestacaoComunidadeExterna.arquivo }" />
		<%@ include file="/ouvidoria/Manifestacao/include/dados_comprovante.jsp" %>
		
		<br />
		
		<div style="text-align: center; margin: 0 auto;">
			<a href="/sigaa/public/home.jsf" style="color: #404E82;">&lt;&lt; Voltar ao menu principal</a>
		</div>
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
