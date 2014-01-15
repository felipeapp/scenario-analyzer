<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>
<%@page import="br.ufrn.sigaa.arq.util.CheckRoleUtil"%>

<c:set var="hideSubsistema" value="true" />

<f:view>

	<h:form>
		<input type="hidden" name="aba" id="aba" />
		<h2>Vestibular</h2>

		<div id="operacoes-subsistema" class="reduzido">
		<div id="cadastros" class="aba">
		<ul>
			<li>Validação de Candidatos
				<ul>
					<li>
						<h:commandLink action="#{validacaoFotoBean.listar}" value="Validar as Fotos 3x4 dos Candidatos" onclick="setAba('candidatos')" id="validacaoFotoBean_listar"/>
						<c:set var="qtdPendente" value="#{validacaoFotoBean.qtdPendenteValidacao}" />
						<h:outputText value=" (#{qtdPendente} pedentes de validação)" rendered="#{qtdPendente > 0}" style="color: red;"/>
					</li>
				</ul>
			</li>
		</ul>
		</div>
		</div>
	</h:form>
</f:view>

<script>
var Abas = {
	init : function() {
		var abas = new YAHOO.ext.TabPanel('operacoes-subsistema');
		abas.addTab('cadastros', "Candidatos");
		abas.activate('cadastros');
	}
};

YAHOO.ext.EventManager.onDocumentReady(Abas.init, Abas, true);
</script>

<div class="linkRodape"><html:link action="/verMenuPrincipal">Menu Principal</html:link></div>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
		