<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>

<f:view>

<h2>Assistência ao Estudante</h2>



<h:form id="form">
	<input type="hidden" name="aba" id="aba"/>
		
	<div id="operacoes-subsistema"  class="reduzido">

		<div id="alimentacao" class="aba">
			<ul>
				<ufrn:checkRole
							papeis="<%= new int[] {SigaaPapeis.SAE_GESTOR_CARTAO_ALIMENTACAO} %>">
					<li>Cartão Bolsa Alimentação
						<ul>
	
							<li><h:commandLink value="Bloquear Cartão Benefício"
								action="#{cartaoBeneficio.iniciarBuscaBloqueio}" />
							</li>
							<li><h:commandLink
								value="Vincular Discente ao Cartão Benefício"
								action="#{cartaoBeneficio.iniciarBusca}"/>
							</li>
							<li><h:commandLink
								value="Relatório de Discentes que Possuem Cartão Benefício"
								action="#{relatorioCartaoBeneficio.iniciaRelatorio}"/>
							</li>
							<li><h:commandLink
								value="Relatório para Assinaturas de Discentes com Bolsa Alimentação"
								action="#{relatorioCartaoBeneficio.iniciarBuscaAssinaturas}"/></li>
						</ul>
					</li>
				</ufrn:checkRole>

				<li> Restaurante Universitário
					<ul>
						<li> <h:commandLink value="Alterar Horário de Acesso ao R.U." action="#{tipoRefeicaoRUMBean.iniciarAlteracaoHorario}" onclick="setAba('alimentacao')"></h:commandLink> </li>
						<li> <h:commandLink value="Relatório Mapa de Acesso ao R.U." action="#{diasAlimentacaoMBean.iniciarRelatorioMapaAcessoRU}" onclick="setAba('alimentacao')"></h:commandLink> </li>
<!--						<li> <h:commandLink value="Relatório de Acessos ao R.U." action="#{buscaAcessoRUMBean.iniciarRelatorioAcessoRU}" onclick="setAba('alimentacao')"></h:commandLink> </li>-->
						<li> <h:commandLink value="Relatório de Discentes Contemplados Sem Digitais"  action="#{relatoriosDigitalMBean.relatorioDiscentesContempladosSemDigital}" onclick="setAba('alimentacao')"></h:commandLink> </li>
						<li> <h:commandLink value="Relatório de Discentes Contemplados Com Digitais" action="#{relatoriosDigitalMBean.relatorioDiscentesContempladosComDigital}" onclick="setAba('alimentacao')"></h:commandLink> </li>
					
						<ufrn:checkRole
							papeis="<%= new int[] { SigaaPapeis.SAE_VISUALIZA_ACESSO_RU }  %>">
								<li> <h:commandLink value="Relatório de Acessos das Catracas do RU" action="#{relatorioAcessoRu.iniciaRelatorio}" onclick="setAba('alimentacao')"></h:commandLink> </li>
						</ufrn:checkRole>
					</ul>
				</li>	
			</ul>
		</div>

		<div id="deae" class="aba">
			<ul>
				<li> Cadastro Único
					<ul>
					<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.SAE_COORDENADOR }  %>">
						<li> <h:commandLink value="Gerenciar Cadastro Único" action="#{cadastroUnicoBolsa.listar}"></h:commandLink> </li>
						<li> <h:commandLink value="Gerenciar Questionário" action="#{cadastroUnicoBolsa.gerenciarQuestionario}"></h:commandLink> </li>
						<li> <h:commandLink value="Ranking de Pontuação" action="#{rankingPontuacao.iniciar}"></h:commandLink> </li>
						<li> <h:commandLink value="Adesões do Discente" action="#{adesaoCadastroUnico.iniciarAlterar}"></h:commandLink> </li>
					</ufrn:checkRole>
					
					<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.SAE_VISUALIZAR_RANKING }  %>">
						<li> <h:commandLink value="Ranking de Pontuação" action="#{rankingPontuacao.iniciar}"></h:commandLink> </li>
					</ufrn:checkRole>
					
					<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.SAE_VISUALIZAR_CADASTRO_UNICO }  %>">
						<li> <h:commandLink value="Adesões do Discente ao Cadastro Único" action="#{adesaoCadastroUnico.iniciarAlterar}"></h:commandLink> </li>
					</ufrn:checkRole>
					</ul>
				</li>
				
				<li> Bolsa Auxílio
					<ul>
						<li> <h:commandLink value="Solicitação de Bolsa Auxílio para Discente" action="#{buscarBolsaAuxilioMBean.iniciarNovaSolicitacao}"></h:commandLink> </li>
						<li> <h:commandLink value="Buscar Bolsas/Definir Dias de Alimentação" action="#{definirDiasAlimentacaoMBean.instanciar}"></h:commandLink> </li>
						<li> <h:commandLink value="Buscar Bolsa Auxílio por Aluno" action="#{buscarBolsaAuxilioMBean.consultarBolsasAuxilio}"></h:commandLink> </li>
						<li> <h:commandLink value="Finalizar Bolsa Auxílio" action="#{finalizarBolsaAuxilioMBean.iniciarFinalizacaoBolsistas}"></h:commandLink> </li>
						<li> <h:commandLink value="Envio Notificação Discentes" action="#{envioNotificacaoProaeMBean.iniciarNotificacao}"></h:commandLink> </li>
					</ul>
				</li>
				
				<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.SAE_COORDENADOR }  %>">
					<li> Cadastramento de Períodos
						<ul>
							<li> <h:commandLink value="Cadastrar" action="#{calendarioBolsaAuxilioMBean.preCadastrar}"></h:commandLink> </li>
						</ul>
					</li>
				</ufrn:checkRole>
				
				<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.SAE_COORDENADOR }  %>">
					<li> Bloqueio Solicitação Bolsa
						<ul>
							<li> <h:commandLink value="Cadastrar" action="#{restricaoSolicitacaoBolsaAuxilioMBean.preCadastrar}"></h:commandLink> </li>
						</ul>
					</li>
				</ufrn:checkRole>

				<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.SAE_COORDENADOR }  %>">
					<li> Critérios Renovação Bolsa
						<ul>
							<li> <h:commandLink value="Cadastrar" action="#{criterioSolicitacaoRenovacaoMBean.preCadastrar}"></h:commandLink> </li>
							<li> <h:commandLink value="Listar/Remover" action="#{criterioSolicitacaoRenovacaoMBean.listar}"></h:commandLink> </li>
						</ul>
					</li>
				</ufrn:checkRole>

				<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.SAE_COORDENADOR }  %>">
					<li> Dados Acadêmicos
						<ul>
							<li> <h:commandLink value="Consolidar Índices Acadêmicos dos Curso" action="#{dadosIndiceAcaMBean.iniciarImportacao}"></h:commandLink> </li>
							<li> <h:commandLink value="Listar/Remover" action="#{dadosIndiceAcaMBean.listar}"></h:commandLink> </li>
							<li> <h:commandLink value="Acompanhamento Acadêmico Discente" action="#{acompanhamentoAcademicoDiscenteMBean.iniciar}"></h:commandLink> </li>
						</ul>
					</li>
				</ufrn:checkRole>
				<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.SAE_COORDENADOR }  %>">
					<li> Homologar Bolsista no SIPAC
						<ul>
							<li><h:commandLink id="homologarCadastro" value="Homologar Cadastro de Bolsistas no SIPAC" action="#{ homologacaoBolsistaMBean.popular }" onclick="setAba('deae')"/></li>
							<li><h:commandLink id="finalizarCadastro" value="Finalizar Cadastro de Bolsistas no SIPAC" action="#{ homologacaoBolsistaMBean.popularFinalizar }" onclick="setAba('deae')"/></li>
						</ul>
					</li>
				</ufrn:checkRole>

				<li> Relatórios
					<ul>
						<li><h:commandLink value="Relatório de Desempenho de Bolsistas" action="#{relatoriosSaeMBean.iniciarRelatorioDesempenho}" /></li>
						<li><h:commandLink value="Relatório de Desempenho Academico dos Bolsistas" action="#{relatoriosSaeMBean.iniciarRelatorioDesempenhoAcademico}" /></li>
						<li><h:commandLink value="Relatório de Discentes Prioritários que Solicitaram Bolsa Auxílio" action="#{relatoriosSaeMBean.iniciarRelatorioDiscentesSituacaoCarencia}" onclick="setAba('deae')" /></li>
						<li><h:commandLink value="Relatório de Bolsistas (SIPAC) e Situação de Carência" action="#{relatoriosSaeMBean.iniciarRelatorioDiscentesSituacaoCarenciaSIPAC}" onclick="setAba('deae')" /></li>
						<li><h:commandLink value="Relatório de Ocupação das Residências" action="#{relatoriosSaeMBean.iniciarRelatorioOcupacaoResidencia}"></h:commandLink> </li>
						<li><h:commandLink value="Relatório de Espectro de Renda com Dados da Matrícula de Alunos de Graduação" action="#{rendaEspectro.iniciarMatricula}"></h:commandLink> </li>
						<li><h:commandLink value="Relatório da Quantidade de Alunos no Cadastro Único por Curso/Centro" action="#{relatoriosSaeMBean.iniciarRelatorioAlunosCadastroUnico}"></h:commandLink> </li>
						<li><h:commandLink value="Relatório de Residentes no Cadastro Único" action="#{relatoriosSaeMBean.iniciarRelatorioResidentesNoCadastroUnico}"></h:commandLink> </li>
						<li><h:commandLink value="Espectro das Resposta do Cadastro Único" action="#{relQuestionarioCadUnico.iniciar}"></h:commandLink></li>
						<li><h:commandLink value="Relatório de Bolsistas com Mais de uma Bolsa" action="#{relatorioAcompanhamentoBolsas.iniciarRelatorioBolsistaDuploOuVinculo}" onclick="setAba('deae')" /></li>
						<li><h:commandLink value="Relatório de Acompanhamento de Desempenho Acadêmico de Bolsistas" action="#{relatorioAcompanhamentoBolsas.iniciarRelatorioBolsistaFrequencia}" onclick="setAba('deae')" /></li>
						<li><h:commandLink value="Relatório de Discentes Prioritários Por Faixa Etária" action="#{relatorioCadUnicoFaixaEtaria.iniciar}" /></li>
						<li> <h:commandLink value="Relatório de Bolsistas Contemplados" action="#{relatoriosSaeMBean.iniciarRelatorioContempladosDeferidos}"></h:commandLink> </li>
						<li> <h:commandLink value="Relatório de Movimentação de Discentes" action="#{relatoriosSaeMBean.iniciarRelatorioMovimentacaoDiscente}"></h:commandLink> </li>
						<li> <h:commandLink value="Relatório de Discentes de Graduação e Pós-Graduação" action="#{relatoriosSaeMBean.gerarRelatorioDiscentesVinculadosGraducaoPos}"></h:commandLink> </li>
						<li> <h:commandLink value="Relatório de Discentes Pagamentos no RU" action="#{relatoriosSaeMBean.discentesPagamentesRU}"></h:commandLink> </li>
						
						<!--
							RELATORIOS SOLICITADOS POR GLEYDSON PARA SEREM REMOVIDOS (#36732)
							<li><h:commandLink value="Bolsistas com CH Prevista para o Período Atual do Discente" action="#{relatorioAcompanhamentoBolsas.iniciarRelatorioBolsistaCHMatriculada}" onclick="setAba('graduacao')" /></li>
							
							RELATORIOS SOLICITADOS PELO SAE PARA SEREM REMOVIDOS 
							<li> <h:commandLink value="Relatório de Alunos com Bolsas Alimentação" action="#{relatoriosSaeMBean.iniciarRelatorioTodosAlunosBolsaAlimentacao}"></h:commandLink> </li>
							<li> <h:commandLink value="Relatório Estatístico de Bolsas" action="#{relatoriosSaeMBean.iniciarRelatorioAlimentacaoResidencia}"></h:commandLink> </li>
							<li> <h:commandLink value="Relatório de Bolsas por Bairro" action="#{relatoriosSaeMBean.iniciarRelatorioBairro}"></h:commandLink> </li>
							<li> <h:commandLink value="Relatório de Bolsas por Escolaridade do Pai do Aluno" action="#{relatoriosSaeMBean.iniciarRelatorioEscolaridadePai}"></h:commandLink> </li>
							<li> <h:commandLink value="Relatório de Bolsas por Profissão do Pai do Aluno" action="#{relatoriosSaeMBean.iniciarRelatorioProfissao}"></h:commandLink> </li>
							<li> <h:commandLink value="Relatório Espectro de Renda" action="#{rendaEspectro.gerarRendaEspectro}"></h:commandLink> </li>
						-->
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
        	abas.addTab('deae', "Assistência ao Estudante");
        	abas.addTab('alimentacao', "Alimentação");
			abas.activate('deae');
			
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

<c:remove var="aba" scope="session"/>