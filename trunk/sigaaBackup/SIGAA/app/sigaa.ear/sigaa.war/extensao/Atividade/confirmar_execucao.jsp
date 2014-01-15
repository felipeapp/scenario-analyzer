<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<%@page import="br.ufrn.sigaa.projetos.dominio.FuncaoMembro"%>
<%@page import="br.ufrn.sigaa.extensao.dominio.TipoAtividadeExtensao"%>
<f:view>
	<h2><ufrn:subSistema /> > Resumo da Ação de Extensão</h2>

<h:form id="formConfirma">

<table class="formulario" width="99%">
	<caption class="listagem"> RESUMO DA AÇÃO </caption>

	<%@include file="/extensao/Atividade/include/dados_atividade.jsp"%>
	
	<!-- BOTOES -->
	<tfoot>
	<tr><td colspan="2">
                <h:commandButton value="Executar Ação de Extensão" action="#{atividadeExtensao.executarAcaoExtensao }" id="btConfirmarExecucao"/>
                <h:commandButton value="Não Executar Ação de Extensão" 
                onclick="if (!confirm(\"Tem certeza que deseja NÃO executar esta Ação de Extensão?\")) return false;"
                action="#{atividadeExtensao.naoExecutarAcaoExtensao}" id="btConfirmarNaoExecucao"/>
                <h:commandButton value="<< Voltar" action="#{atividadeExtensao.listarMinhasAtividades}" immediate="true" id="btVoltar"/>
                <h:commandButton value="Cancelar" action="#{atividadeExtensao.cancelar}" onclick="#{confirm}" id="btCancelar"/>
	</td></tr>
	</tfoot>
</table>


</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>