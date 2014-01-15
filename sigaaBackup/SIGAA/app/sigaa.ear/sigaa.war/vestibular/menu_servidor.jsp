<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>
<%@page import="br.ufrn.sigaa.arq.util.CheckRoleUtil"%>

<c:set var="hideSubsistema" value="true" />

<f:view>
	
	<h:form>
		<input type="hidden" name="aba" id="aba"/>
		
		<h2>Vestibular</h2>

		<div id="operacoes-subsistema" class="reduzido">
		<c:if test="${usuario.vinculoAtivo.vinculoServidor}">
			<div id="fiscais" class="aba">
				<ul>
					<li><h:commandLink
						action="#{inscricaoSelecaoFiscalVestibular.iniciarInscricaoFiscal}"
						value="Inscrição para Fiscal do Vestibular" onclick="setAba('fiscais')"></h:commandLink></li>
					<li><h:commandLink
						action="#{relatoriosVestibular.iniciarListaLocaisProva}"
						value="Lista de locais de aplicação de Prova" onclick="setAba('fiscais')"></h:commandLink></li>
					<li><h:commandLink
						action="#{inscricaoSelecaoFiscalVestibular.exibeComprovanteInscricao}"
						value="Comprovante de inscrição" onclick="setAba('fiscais')"></h:commandLink></li>
					<li><h:commandLink value="Resultado do Processamento"
						action="#{inscricaoSelecaoFiscalVestibular.exibeResultadoDoProcessamento}" onclick="setAba('fiscais')"/>
					</li>
					<li>
						<h:commandLink value="Justificativa de Ausência" 
						action="#{justificativaAusencia.preCadastrar}" onclick="setAba('fiscais')"/>
					</li>
					
				</ul>
			</div>
		</c:if>
		<ufrn:checkRole papel="<%=SigaaPapeis.GESTOR_CONVOCACOES_PROCESSOS_SELETIVOS%>">
			<div id="importacao" class="aba">
				<ul>
					<li>Vestibular Interno
						<ul>
							<li>
								<h:commandLink value="Importação do Resultado do Vestibular" action="#{processoImportacaDadosProcessoSeletivo.importacaoCandidatosVestibular}" onclick="setAba('importacao')" id="processoImportacaDadosProcessoSeletivo_importacaoCandidatosVestibular"/>
							</li>
						</ul>
					</li>
					<li>Vestibulares/Concursos Externos
						<ul>
							<li><h:commandLink action="#{importaAprovadosOutrosVestibularesMBean.iniciarDefinicaoLeiaute}" value="Definir Leiaute do Arquivo de Importação" onclick="setAba('importacao')" id="importaInscricaoVestibularMBean_iniciarDefinicaoLeiaute"/></li>
							<li><h:commandLink action="#{importaAprovadosOutrosVestibularesMBean.listarLeiautes}" value="Listar Leiautes do Arquivo de Importação" onclick="setAba('importacao')" id="importaInscricaoVestibularMBean_listarLeiautes"/></li>
							<li><h:commandLink action="#{importaAprovadosOutrosVestibularesMBean.iniciarImportacao}" value="Importar Aprovados" onclick="setAba('importacao')" id="importaInscricaoVestibularMBean_iniciarImportacao"/></li>
						</ul>
					</li>
					<li>Convocação
						<ul>
							<li>
								<h:commandLink value="Primeira Convocação de Candidatos do Vestibular" action="#{convocacaoVestibular.iniciarConvocacaoImportacaoVestibular}" onclick="setAba('importacao')" id="convocacaoVestibular_iniciarConvocacaoImportacaoVestibular"/>
							</li>
						</ul>
					</li>
				</ul>
		
				</div>
		</ufrn:checkRole>

		
	</div>	
	</h:form>
	<div class="linkRodape"><html:link action="/verMenuPrincipal">Menu Principal</html:link></div>
</f:view>

<script>
	var Abas = {
		init : function() {
			var abas = new YAHOO.ext.TabPanel('operacoes-subsistema');
			var primeiraAba ='';
			<c:if test="${usuario.vinculoAtivo.vinculoServidor}">
				abas.addTab('fiscais', "Fiscais");
				primeiraAba = 'fiscais';
			</c:if>
			<ufrn:checkRole papel="<%=SigaaPapeis.GESTOR_CONVOCACOES_PROCESSOS_SELETIVOS%>">
				abas.addTab('importacao', "Importação - Cadastro");
				primeiraAba = 'importacao';
			</ufrn:checkRole>
			<c:if test="${sessionScope.aba != null && sessionScope.aba != ''}">
	    		abas.activate('${sessionScope.aba}');
		    </c:if>
		    <c:if test="${sessionScope.aba == null}">
				abas.activate(primeiraAba);
			</c:if>
		}
	};

	YAHOO.ext.EventManager.onDocumentReady(Abas.init, Abas, true);
	function setAba(aba) {
		document.getElementById('aba').value = aba;
	}
</script>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
<c:remove var="aba" scope="session"/>