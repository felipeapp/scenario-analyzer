<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%> 
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>

<c:set var="hideSubsistema" value="true" />

<f:view>
	<h:form>
	<h2>Menu da Ouvidoria</h2>

	<input type="hidden" name="aba" id="aba" />
		<div id="operacoes-subsistema" class="reduzido">
			<div id="comunidade_universitaria" class="aba">
				<ul>
					<li>
						<h:commandLink action="#{manifestacaoGeral.preCadastrar }" value="Entrar em Contato" onclick="setAba('comunidade_universitaria')" />
					</li>
					<li>
						<h:commandLink action="#{manifestacaoGeral.acompanharManifestacoes }" value="Acompanhar Manifestações" onclick="setAba('comunidade_universitaria')" />
					</li>
				</ul>
			</div>
			<ufrn:checkRole	papeis="<%= new int[] { SigaaPapeis.OUVIDOR, SigaaPapeis.SECRETARIO_OUVIDOR } %>">
				<div id="ouvidoria" class="aba">
					<ul>
						<li>Manifestações
						<ul>
							<li><h:commandLink action="#{manifestacaoOuvidoria.iniciarCadastro }" value="Cadastrar Manifestação" onclick="setAba('ouvidoria')" /></li>
							<li><h:commandLink action="#{analiseManifestacaoOuvidoria.listarPendentes }" value="Analisar/Encaminhar Manifestações Pendentes" onclick="setAba('ouvidoria')" /> <span class="tremeluzir">(${ fn:length(analiseManifestacaoOuvidoria.manifestacoesPendentes) })</span></li>
							<li><h:commandLink action="#{analiseManifestacaoOuvidoria.listarEncaminhadas }" value="Analisar Manifestações Encaminhadas" onclick="setAba('ouvidoria')" /> <span class="tremeluzir">(${ fn:length(analiseManifestacaoOuvidoria.manifestacoesEncaminhadas) })</span></li>
							<li><h:commandLink action="#{analiseManifestacaoOuvidoria.listarRespondidas }" value="Finalizar Manifestações Respondidas" onclick="setAba('ouvidoria')" /> <span class="tremeluzir">(${ fn:length(analiseManifestacaoOuvidoria.manifestacoesRespondidas) })</span></li>
							<li><h:commandLink action="#{analiseManifestacaoOuvidoria.listarManifestacoes }" value="Acompanhar Manifestação" onclick="setAba('ouvidoria')" /></li>
						</ul>
						</li>
						
						<li>Categoria do Assunto
						<ul>
							<li><h:commandLink action="#{categoriaAssuntoManifestacao.iniciarCadastro }" value="Cadastrar" onclick="setAba('ouvidoria')" /></li>
							<li><h:commandLink action="#{categoriaAssuntoManifestacao.listar }" value="Listar/Alterar" onclick="setAba('ouvidoria')" /></li>
						</ul>
						</li>
		
						<li>Assunto
						<ul>
							<li><h:commandLink action="#{assuntoManifestacao.iniciarCadastro }" value="Cadastrar" onclick="setAba('ouvidoria')" /></li>
							<li><h:commandLink action="#{assuntoManifestacao.listar }" value="Listar/Alterar" onclick="setAba('ouvidoria')" /></li>
						</ul>
						</li>
					</ul>
				</div>
				<div id="relatorios" class="aba">
					<ul>
						<li><h:commandLink action="#{relatoriosOuvidoria.iniciarQuadroGeral }" value="Quadro Geral de Manifestações" onclick="setAba('relatorios')" />
						</li>
						
						<li>Manifestações
						<ul>
							<li><h:commandLink action="#{relatoriosOuvidoria.iniciarRelatorioGeralManifestacoes }" value="Relatório Geral de Manifestações" onclick="setAba('relatorios')" /></li>
							<li><h:commandLink action="#{relatoriosOuvidoria.iniciarRelatorioManifestacoesNaoRespondidas }" value="Manifestações Não Respondidas" onclick="setAba('relatorios')" /></li>
							<li><h:commandLink action="#{relatoriosOuvidoria.iniciarRelatorioManifestacoesPorCategoriaSolicitante }" value="Manifestações por Categoria do Solicitante" onclick="setAba('relatorios')" /></li>
							<li><h:commandLink action="#{relatoriosOuvidoria.iniciarRelatorioManifestacoesPorStatus }" value="Manifestações por Status" onclick="setAba('relatorios')" /></li>
							<li><h:commandLink action="#{relatoriosOuvidoria.iniciarRelatorioManifestacoesPorAssunto }" value="Manifestações por Assunto" onclick="setAba('relatorios')" /></li>
							<li><h:commandLink action="#{relatoriosOuvidoria.iniciarRelatorioManifestacoesPorUnidadeResponsavel }" value="Manifestações por Unidade Responsável" onclick="setAba('relatorios')" /></li>
						</ul>
						</li>
					</ul>
				</div>
			</ufrn:checkRole>
			
			<c:if test="${analiseManifestacaoResponsavel.acessaAbaResponsabilidade }">
				<div id="responsavel" class="aba">
					<ul>
						<li>Manifestações
						<ul>
							<li><h:commandLink action="#{analiseManifestacaoResponsavel.listarPendentes }" value="Analisar/Designar Manifestações Pendentes" onclick="setAba('responsavel')" /> <span class="tremeluzir">(${ fn:length(analiseManifestacaoResponsavel.manifestacoesPendentes) })</span></li>
							<li><h:commandLink action="#{analiseManifestacaoResponsavel.listarManifestacoes }"value="Acompanhar Manifestação" onclick="setAba('responsavel')" /></li>
						</ul>
						</li>
					</ul>
				</div>
			</c:if>
			
			<ufrn:checkRole	papeis="<%= new int[] { SigaaPapeis.DESIGNADO_OUVIDORIA } %>">
				<div id="designado" class="aba">
					<ul>
						<li>Manifestações
						<ul>
							<li><h:commandLink action="#{analiseManifestacaoDesignado.listarPendentes }"value="Analisar Manifestações Pendentes" onclick="setAba('designado')" /> <span class="tremeluzir">(${ fn:length(analiseManifestacaoDesignado.manifestacoesPendentes) })</span></li>
							<li><h:commandLink action="#{analiseManifestacaoDesignado.listarManifestacoes }"value="Acompanhar Manifestação" onclick="setAba('designado')" /></li>
						</ul>
						</li>
					</ul>
				</div>
			</ufrn:checkRole>
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
	        var hasAbaAtiva = false;
	        
			<ufrn:checkRole papeis="<%=new int[] { SigaaPapeis.DESIGNADO_OUVIDORIA }%>">
				abas.addTab('designado', "Designado para Resposta");
				abas.activate('designado');
				hasAbaAtiva = true;
			</ufrn:checkRole>

			<c:if test="${analiseManifestacaoResponsavel.acessaAbaResponsabilidade }">
				abas.addTab('responsavel', "Reponsável por Unidade");
				abas.activate('responsavel');
				hasAbaAtiva = true;
			</c:if>
			
	        <ufrn:checkRole papeis="<%=new int[] { SigaaPapeis.OUVIDOR, SigaaPapeis.SECRETARIO_OUVIDOR }%>">
	        	abas.addTab('ouvidoria', "Ouvidoria");
	        	abas.addTab('relatorios', "Relatórios");
	        	abas.activate('ouvidoria');
	        	hasAbaAtiva = true;
	        </ufrn:checkRole>
	        
	        abas.addTab('comunidade_universitaria', "Comunidade Universitária");
	        if(!hasAbaAtiva) {
	        	abas.activate('comunidade_universitaria');
	        }
	        
	        <c:if test="${sessionScope.aba != null}">
		    	abas.activate('${sessionScope.aba}');
		    </c:if>

	    }
	};

	YAHOO.ext.EventManager.onDocumentReady(Abas.init, Abas, true);
	function setAba(aba) {
		document.getElementById('aba').value = aba;
	}
</script>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>