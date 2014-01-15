<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<%@page import="br.ufrn.sigaa.extensao.dominio.TipoAtividadeExtensao"%>
<%@page import="br.ufrn.sigaa.extensao.dominio.AtividadeExtensao"%>

<f:view>

<h2><ufrn:subSistema /> > Remover A��o de Extens�o</h2>
<h:messages showDetail="true" showSummary="true"/>

<h:form id="form">

<table class=formulario width="100%">
	<caption class="listagem">Dados da A��o de Extens�o</caption>
	<%@include file="/extensao/Atividade/include/dados_atividade.jsp"%>
	
	<tfoot>
	<tr>
		<td colspan="2">
			<h:commandButton value="Confirmar Remo��o" action="#{atividadeExtensao.remover}" id="btConfirmar"/>
			<h:commandButton value="Cancelar" action="#{atividadeExtensao.cancelar}" onclick="#{confirm}" id="btCancelar"/>
		</td>
	</tr>
	</tfoot>
</table>

</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>