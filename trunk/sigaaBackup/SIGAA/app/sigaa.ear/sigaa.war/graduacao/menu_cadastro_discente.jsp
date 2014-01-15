<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>
<%@page import="br.ufrn.sigaa.arq.util.CheckRoleUtil"%>

<% CheckRoleUtil.checkRole(request, response, new int[] {SigaaPapeis.CADASTRA_DISCENTE_GRADUACAO} ); %>

<c:set var="hideSubsistema" value="true" />
<f:view>

<h:form>

	<input type="hidden" name="aba" id="aba"/>
	<h2>Menu de Cadastramento de Discentes</h2>

	<div id="operacoes-subsistema" class="reduzido" >
			<div id="cadastro" class="aba">
			<ul>
				<li> Cadastrar Discente
					<ul>
					<li> <h:commandLink action="#{cadastramentoDiscenteConvocadoMBean.iniciarCadastramento}" value="Cadastramento de Discentes Aprovados em Processos Seletivos" onclick="setAba('cadastro')" id="convocacaoVestibular_iniciarCadastramento"/></li>
					</ul>
				</li>
				<li> Documentos
					<ul>
					<li> <h:commandLink action="#{ atestadoMatricula.buscarDiscente }" value="Emitir Atestado de Matr�cula" onclick="setAba('cadastro')"/> </li>
					<li> <h:commandLink	action="#{ historico.buscarDiscente }"	value="Emitir Hist�rico" onclick="setAba('cadastro')"/> </li>
					<li> <h:commandLink	action="#{ declaracaoVinculo.buscarDiscente }"	value="Emitir Declara��o de V�nculo/Cadastro" onclick="setAba('cadastro')"/> </li>
					</ul>
				</li>
			</ul>
			</div>
	</div>

<script>
var Abas = {
    init : function(){
        var abas = new YAHOO.ext.TabPanel('operacoes-subsistema');
        <%-- Configura��o das abas --%>
        abas.addTab('cadastro', "Cadastro");
    	abas.activate('cadastro');
    }
};

YAHOO.ext.EventManager.onDocumentReady(Abas.init, Abas, true);
function setAba(aba) {
	document.getElementById('aba').value = aba;
}
</script>

	</h:form>
</f:view>

<div class="linkRodape">
	<html:link action="/verMenuPrincipal">Menu Principal</html:link>
</div>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
			