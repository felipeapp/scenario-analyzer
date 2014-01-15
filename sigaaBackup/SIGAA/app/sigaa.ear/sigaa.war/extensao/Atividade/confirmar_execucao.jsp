<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<%@page import="br.ufrn.sigaa.projetos.dominio.FuncaoMembro"%>
<%@page import="br.ufrn.sigaa.extensao.dominio.TipoAtividadeExtensao"%>
<f:view>
	<h2><ufrn:subSistema /> > Resumo da A��o de Extens�o</h2>

<h:form id="formConfirma">

<table class="formulario" width="99%">
	<caption class="listagem"> RESUMO DA A��O </caption>

	<%@include file="/extensao/Atividade/include/dados_atividade.jsp"%>
	
	<!-- BOTOES -->
	<tfoot>
	<tr><td colspan="2">
                <h:commandButton value="Executar A��o de Extens�o" action="#{atividadeExtensao.executarAcaoExtensao }" id="btConfirmarExecucao"/>
                <h:commandButton value="N�o Executar A��o de Extens�o" 
                onclick="if (!confirm(\"Tem certeza que deseja N�O executar esta A��o de Extens�o?\")) return false;"
                action="#{atividadeExtensao.naoExecutarAcaoExtensao}" id="btConfirmarNaoExecucao"/>
                <h:commandButton value="<< Voltar" action="#{atividadeExtensao.listarMinhasAtividades}" immediate="true" id="btVoltar"/>
                <h:commandButton value="Cancelar" action="#{atividadeExtensao.cancelar}" onclick="#{confirm}" id="btCancelar"/>
	</td></tr>
	</tfoot>
</table>


</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>