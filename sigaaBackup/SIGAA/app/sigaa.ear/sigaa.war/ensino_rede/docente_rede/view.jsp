<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<style>
	table.formulario th{
		font-weight: bold;
	}
</style>
<f:view>

	<h2><ufrn:subSistema /> > Histórico Completo do Docente </h2>

	<%-- Dados pessoais --%>
	<c:set var="nomeOperacao_" value="Dados do Docente"/>
	<c:set var="pessoa_" value="#{historicoDocenteRede.obj.pessoa}"/>
	<c:set var="docenteRede_" value="true"/>
	<%@include file="/geral/pessoa/view_generico.jsp"%>

	<c:if test="${empty comprovante}">
	<br />
	<table width="100%">
	<caption> <h3 class="tituloTabela">Outros dados do Docente</h3> </caption>
	
	<%-- Outras informações --%>
	<tr><td>
	<div id="abas-outros-dados">
		<div id="alteracoes" class="aba">
			<%@include file="/ensino_rede/docente_rede/include/alteracoes.jsp"%>
		</div>
			
	</div>
	</td></tr>

	<tfoot style="background: #C8D5EC;text-align: center;">
		<h:form prependId="false">
			<tr>
				<td>
					 <h:commandButton value="<< Voltar" action="#{selecionaDocenteRedeMBean.redirectTelaLista}" id="voltar" />
					 <h:commandButton value="Cancelar" action="#{selecionaDocenteRedeMBean.cancelar}" onclick="#{confirm}" id="cancelar" />
				</td>
			</tr>
		</h:form>
	</tfoot>
	</table>

	</c:if>

<script>
var Abas = {
    init : function(){
        var abas = new YAHOO.ext.TabPanel('abas-outros-dados');
        abas.addTab('alteracoes', "Movimentações");
        abas.activate('"alteracoes"');
    }
};

YAHOO.ext.EventManager.onDocumentReady(Abas.init, Abas, true);
</script>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>