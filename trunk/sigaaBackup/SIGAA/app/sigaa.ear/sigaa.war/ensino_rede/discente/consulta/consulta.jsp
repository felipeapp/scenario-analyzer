<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<style>
	table.formulario th{
		font-weight: bold;
	}
</style>
<f:view>

	<a4j:keepAlive beanName = "selecionaDiscenteMBean" />

	<h2><ufrn:subSistema /> > Consulta Discente </h2>

	<%-- Dados pessoais --%>
	<c:set var="pessoa_" value="#{consultarDiscenteMBean.obj.discente.pessoa}"/>
	<%@include file="/geral/pessoa/view_generico.jsp"%>

	<br />
	<table width="100%">
	<caption> <h3 class="tituloTabela">Outros dados do Docente</h3> </caption>
	
	<%-- Outras informações --%>
	<tr><td>
	<div id="abas-outros-dados">
		<div id="movimentacoes" class="aba">
			<%@include file="/ensino_rede/discente/consulta/movimentacoes.jsp"%>
		</div>
			
	</div>
	</td></tr>

	<tfoot style="background: #C8D5EC;text-align: center;">
		<h:form prependId="false">
			<tr>
				<td>
					 <h:commandButton value="<< Voltar" action="#{selecionaDiscenteMBean.voltar}" id="voltar" />
					 <h:commandButton value="Cancelar" action="#{consultarDiscenteMBean.cancelar}" onclick="#{confirm}" id="cancelar" />
				</td>
			</tr>
		</h:form>
	</tfoot>
	</table>


<script>
var Abas = {
    init : function(){
        var abas = new YAHOO.ext.TabPanel('abas-outros-dados');
        abas.addTab('movimentacoes', "Movimentações");
        abas.activate('movimentacoes');
    }
};

YAHOO.ext.EventManager.onDocumentReady(Abas.init, Abas, true);
</script>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>