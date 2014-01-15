<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>

<c:set var="hideSubsistema" value="true" />
<f:view>
	<h:form>
	<h2>Menu de Pesquisa para Servidores Técnicos-administrativos</h2>

	<input type="hidden" name="aba" id="aba"/>
		<div id="operacoes-subsistema" class="reduzido" >
			<div id="atividade" class="aba">
				<ul>
				    <li>Notificação de Invenção
			            <ul>
			            	<li><h:commandLink action="#{invencao.iniciar}"	value="Notificar Invenção"/></li>
						</ul>
					</li>
				    <li>Projetos de Pesquisa
			            <ul>
			            	<li> <html:link action="/pesquisa/projetoPesquisa/criarProjetoPesquisa.do?dispatch=listByMembro">Listar projetos que participo</html:link> </li>
						</ul>
					</li>
				</ul>
			 </div>
		</div>


	</h:form>
</f:view>

<div class="linkRodape">
	<html:link action="/verMenuPrincipal">Menu Principal</html:link>
</div>

				<script>
						var Abas = {
						    init : function(){
						        var abas = new YAHOO.ext.TabPanel('operacoes-subsistema');
						        abas.addTab('atividade', "Operações");
						        abas.activate('atividade');

						    }
						};

						YAHOO.ext.EventManager.onDocumentReady(Abas.init, Abas, true);
			
				</script>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>