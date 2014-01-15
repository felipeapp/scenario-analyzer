<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<style>
	table.formulario th{
		font-weight: bold;
	}
</style>
<f:view>
	<c:if test="${sessionScope.acesso.coordenadorCursoGrad}">
		<%@include file="/graduacao/menu_coordenador.jsp" %>
	</c:if>

	<h2><ufrn:subSistema /> > Histórico Completo do Discente </h2>

	<%-- Dados pessoais --%>
	<c:set var="discente" value="#{historicoDiscente.obj}"/>
	<%@include file="/geral/discente/dados_pessoais.jsp"%>

	<c:if test="${empty comprovante}">
	<br />
	<table width="100%">
	<caption> <h3 class="tituloTabela">Outros dados do Discente</h3> </caption>
	
	<%-- Outras informações --%>
	<tr><td>
	<div id="abas-outros-dados">
		<div id="afastamentos" class="aba">
			<%@include file="/graduacao/discente/include/afastamentos.jsp"%>
		</div>
			
		<div id="aproveitamentos" class="aba">
			<%@include file="/graduacao/discente/include/aproveitamentos.jsp"%>
		</div>
		
		<div id="prorrogacoes" class="aba">
			<%@include file="/graduacao/discente/include/prorrogacoes.jsp"%>
		</div>
		
		<div id="retificacoes" class="aba">
			<%@include file="/graduacao/discente/include/retificacoes_nota.jsp"%>
		</div>
	
		<div id="mudancas" class="aba">
			<%@include file="/graduacao/discente/include/mudancas_geral.jsp"%>
		</div>
	</div>
	</td></tr>

	<%-- Observações do discente --%>
	<c:if test="${not empty historicoDiscente.observacoes}">
	<tr>
		<td class="subFormulario"> Obervações do Discente </td>
	</tr>
	<tr>
		<td>
			<%@include file="/graduacao/discente/include/observacoes.jsp"%>
		</td>
	</tr>
	</c:if>

	<tfoot style="background: #C8D5EC;text-align: center;">
		<h:form prependId="false">
			<tr>
				<td>
				<c:if test="${not empty discente.idHistoricoDigital}">
						<a
							href="${ctx}/verProducao?idArquivo=${ discente.idHistoricoDigital}&key=${ sf:generateArquivoKey(discente.idHistoricoDigital) }"
							target="_blank">Clique aqui para ver o arquivo digitalizado do Histórico<h:graphicImage
							value="/img/icones/document_view.png" style="vertical-align: middle; overflow: visible;" />
						</a>
				</c:if> 
				<c:if test="${empty discente.idHistoricoDigital}">
					 <h:commandButton value="<< Voltar" action="#{historicoDiscente.voltar}" id="voltar" />
					 <h:commandButton value="Cancelar" action="#{historicoDiscente.cancelar}" onclick="#{confirm}" id="cancelar" />
					 <h:commandButton value="Consultar Histórico" action="#{historicoDiscente.historico}" id="btnHistorico"/>
					 <h:commandButton value="Consultar Atestado de Matrícula" action="#{historicoDiscente.atestadoMatricula}" id="btnAtestado"/>
				</c:if>
				</td>
			</tr>
		</h:form>
	</tfoot>
	</table>

	</c:if>
	<c:if test="${not empty comprovante}">
	<h:form prependId="false">
		<div align="center"><h:commandLink action="#{discenteGraduacao.cancelar}"  value="<< Voltar" id="btnVoltar" /></div>
	</h:form>
	</c:if>
<script>
var Abas = {
    init : function(){
        var abas = new YAHOO.ext.TabPanel('abas-outros-dados');
        abas.addTab('afastamentos', "Movimentações");
        <c:if test="${not acesso.consulta}">
        	abas.addTab('aproveitamentos', "Aproveitamentos");
        </c:if>
		abas.addTab('prorrogacoes', "Prorrogações");
		<c:if test="${not acesso.consulta}">
			abas.addTab('retificacoes', "Retificações");
		</c:if>

		abas.addTab('mudancas', "Mudanças");
        abas.activate('afastamentos');
    }
};

YAHOO.ext.EventManager.onDocumentReady(Abas.init, Abas, true);
</script>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>