<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<%@page import="br.ufrn.sigaa.extensao.dominio.TipoAtividadeExtensao"%>
<%@page import="br.ufrn.sigaa.extensao.dominio.AtividadeExtensao"%>

<f:view>

<h2><ufrn:subSistema /> > Remover Ação de Extensão</h2>
<h:messages showDetail="true" showSummary="true"/>

<h:form id="form">

<table class=formulario width="100%">
	<caption class="listagem">Dados da Ação de Extensão</caption>
	<%@include file="/extensao/Atividade/include/dados_atividade.jsp"%>
	
	<tfoot>
	<tr>
		<td colspan="2">
			<h:commandButton value="Confirmar Remoção" action="#{atividadeExtensao.remover}" id="btConfirmar"/>
			<h:commandButton value="Cancelar" action="#{atividadeExtensao.cancelar}" onclick="#{confirm}" id="btCancelar"/>
		</td>
	</tr>
	</tfoot>
</table>

</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>