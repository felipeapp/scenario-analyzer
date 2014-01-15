<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>
<%@page import="br.ufrn.sigaa.arq.util.CheckRoleUtil"%>

<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.GESTOR_AMBIENTES_VIRTUAIS } %>">

<f:view>

<h2>Ambientes Virtuais</h2>

<h:form>
		<input type="hidden" name="aba" id="aba"/>

		<div id="operacoes-subsistema"  class="reduzido">

			<div id="deae" class="aba">
				<ul>
					<li> Comunidades Virtuais
						<ul>
							<h:commandLink actionListener="#{ buscarComunidadeVirtualMBean.criar }" value="Buscar Comunidades Virtuais" />
						</ul>
						<ul>
							<h:commandLink actionListener="#{ comunidadeVirtualMBean.criar }" value="Criar Comunidade Virtual" />
						</ul>
						<ul>
							<h:commandLink action="#{buscarComunidadeVirtualMBean.exibirTodasComunidadesDocente}" value="Minhas Comunidades"/>
						</ul>
					</li>
					
					<li> Turmas Virtuais
						<ul>
							<h:commandLink action="#{ configuracaoPortaArquivos.iniciar }" value="Gerenciar Porta Arquivos" />
						</ul>
					</li>
				</ul>
			</div>
		</div>
		
</h:form>
<c:set var="hideSubsistema" value="true" />

</f:view>

<div class="linkRodape">
    <html:link action="/verMenuPrincipal">Menu Principal</html:link>
</div>
<script>
var Abas = {
    init : function(){
        var abas = new YAHOO.ext.TabPanel('operacoes-subsistema');
        	abas.addTab('deae', "AMBIENTES VIRTUAIS");
			abas.activate('deae');
    }
};

YAHOO.ext.EventManager.onDocumentReady(Abas.init, Abas, true);
function setAba(aba) {
	document.getElementById('aba').value = aba;
}
</script>

</ufrn:checkRole>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

<c:remove var="aba" scope="session"/>