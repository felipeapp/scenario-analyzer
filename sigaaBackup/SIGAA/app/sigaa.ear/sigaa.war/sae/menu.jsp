<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>

<f:view>

<h2>Assist�ncia ao Estudante</h2>



<h:form id="form">
	<input type="hidden" name="aba" id="aba"/>
		
	<div id="operacoes-subsistema"  class="reduzido">

		<div id="alimentacao" class="aba">
			<ul>
				<ufrn:checkRole
							papeis="<%= new int[] {SigaaPapeis.SAE_GESTOR_CARTAO_ALIMENTACAO} %>">
					<li>Cart�o Bolsa Alimenta��o
						<ul>
	
							<li><h:commandLink value="Bloquear Cart�o Benef�cio"
								action="#{cartaoBeneficio.iniciarBuscaBloqueio}" />
							</li>
							<li><h:commandLink
								value="Vincular Discente ao Cart�o Benef�cio"
								action="#{cartaoBeneficio.iniciarBusca}"/>
							</li>
							<li><h:commandLink
								value="Relat�rio de Discentes que Possuem Cart�o Benef�cio"
								action="#{relatorioCartaoBeneficio.iniciaRelatorio}"/>
							</li>
							<li><h:commandLink
								value="Relat�rio para Assinaturas de Discentes com Bolsa Alimenta��o"
								action="#{relatorioCartaoBeneficio.iniciarBuscaAssinaturas}"/></li>
						</ul>
					</li>
				</ufrn:checkRole>

				<li> Restaurante Universit�rio
					<ul>
						<li> <h:commandLink value="Alterar Hor�rio de Acesso ao R.U." action="#{tipoRefeicaoRUMBean.iniciarAlteracaoHorario}" onclick="setAba('alimentacao')"></h:commandLink> </li>
						<li> <h:commandLink value="Relat�rio Mapa de Acesso ao R.U." action="#{diasAlimentacaoMBean.iniciarRelatorioMapaAcessoRU}" onclick="setAba('alimentacao')"></h:commandLink> </li>
<!--						<li> <h:commandLink value="Relat�rio de Acessos ao R.U." action="#{buscaAcessoRUMBean.iniciarRelatorioAcessoRU}" onclick="setAba('alimentacao')"></h:commandLink> </li>-->
						<li> <h:commandLink value="Relat�rio de Discentes Contemplados Sem Digitais"  action="#{relatoriosDigitalMBean.relatorioDiscentesContempladosSemDigital}" onclick="setAba('alimentacao')"></h:commandLink> </li>
						<li> <h:commandLink value="Relat�rio de Discentes Contemplados Com Digitais" action="#{relatoriosDigitalMBean.relatorioDiscentesContempladosComDigital}" onclick="setAba('alimentacao')"></h:commandLink> </li>
					
						<ufrn:checkRole
							papeis="<%= new int[] { SigaaPapeis.SAE_VISUALIZA_ACESSO_RU }  %>">
								<li> <h:commandLink value="Relat�rio de Acessos das Catracas do RU" action="#{relatorioAcessoRu.iniciaRelatorio}" onclick="setAba('alimentacao')"></h:commandLink> </li>
						</ufrn:checkRole>
					</ul>
				</li>	
			</ul>
		</div>

		<div id="deae" class="aba">
			<ul>
				<li> Cadastro �nico
					<ul>
					<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.SAE_COORDENADOR }  %>">
						<li> <h:commandLink value="Gerenciar Cadastro �nico" action="#{cadastroUnicoBolsa.listar}"></h:commandLink> </li>
						<li> <h:commandLink value="Gerenciar Question�rio" action="#{cadastroUnicoBolsa.gerenciarQuestionario}"></h:commandLink> </li>
						<li> <h:commandLink value="Ranking de Pontua��o" action="#{rankingPontuacao.iniciar}"></h:commandLink> </li>
						<li> <h:commandLink value="Ades�es do Discente" action="#{adesaoCadastroUnico.iniciarAlterar}"></h:commandLink> </li>
					</ufrn:checkRole>
					
					<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.SAE_VISUALIZAR_RANKING }  %>">
						<li> <h:commandLink value="Ranking de Pontua��o" action="#{rankingPontuacao.iniciar}"></h:commandLink> </li>
					</ufrn:checkRole>
					
					<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.SAE_VISUALIZAR_CADASTRO_UNICO }  %>">
						<li> <h:commandLink value="Ades�es do Discente ao Cadastro �nico" action="#{adesaoCadastroUnico.iniciarAlterar}"></h:commandLink> </li>
					</ufrn:checkRole>
					</ul>
				</li>
				
				<li> Bolsa Aux�lio
					<ul>
						<li> <h:commandLink value="Solicita��o de Bolsa Aux�lio para Discente" action="#{buscarBolsaAuxilioMBean.iniciarNovaSolicitacao}"></h:commandLink> </li>
						<li> <h:commandLink value="Buscar Bolsas/Definir Dias de Alimenta��o" action="#{definirDiasAlimentacaoMBean.instanciar}"></h:commandLink> </li>
						<li> <h:commandLink value="Buscar Bolsa Aux�lio por Aluno" action="#{buscarBolsaAuxilioMBean.consultarBolsasAuxilio}"></h:commandLink> </li>
						<li> <h:commandLink value="Finalizar Bolsa Aux�lio" action="#{finalizarBolsaAuxilioMBean.iniciarFinalizacaoBolsistas}"></h:commandLink> </li>
						<li> <h:commandLink value="Envio Notifica��o Discentes" action="#{envioNotificacaoProaeMBean.iniciarNotificacao}"></h:commandLink> </li>
					</ul>
				</li>
				
				<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.SAE_COORDENADOR }  %>">
					<li> Cadastramento de Per�odos
						<ul>
							<li> <h:commandLink value="Cadastrar" action="#{calendarioBolsaAuxilioMBean.preCadastrar}"></h:commandLink> </li>
						</ul>
					</li>
				</ufrn:checkRole>
				
				<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.SAE_COORDENADOR }  %>">
					<li> Bloqueio Solicita��o Bolsa
						<ul>
							<li> <h:commandLink value="Cadastrar" action="#{restricaoSolicitacaoBolsaAuxilioMBean.preCadastrar}"></h:commandLink> </li>
						</ul>
					</li>
				</ufrn:checkRole>

				<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.SAE_COORDENADOR }  %>">
					<li> Crit�rios Renova��o Bolsa
						<ul>
							<li> <h:commandLink value="Cadastrar" action="#{criterioSolicitacaoRenovacaoMBean.preCadastrar}"></h:commandLink> </li>
							<li> <h:commandLink value="Listar/Remover" action="#{criterioSolicitacaoRenovacaoMBean.listar}"></h:commandLink> </li>
						</ul>
					</li>
				</ufrn:checkRole>

				<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.SAE_COORDENADOR }  %>">
					<li> Dados Acad�micos
						<ul>
							<li> <h:commandLink value="Consolidar �ndices Acad�micos dos Curso" action="#{dadosIndiceAcaMBean.iniciarImportacao}"></h:commandLink> </li>
							<li> <h:commandLink value="Listar/Remover" action="#{dadosIndiceAcaMBean.listar}"></h:commandLink> </li>
							<li> <h:commandLink value="Acompanhamento Acad�mico Discente" action="#{acompanhamentoAcademicoDiscenteMBean.iniciar}"></h:commandLink> </li>
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

				<li> Relat�rios
					<ul>
						<li><h:commandLink value="Relat�rio de Desempenho de Bolsistas" action="#{relatoriosSaeMBean.iniciarRelatorioDesempenho}" /></li>
						<li><h:commandLink value="Relat�rio de Desempenho Academico dos Bolsistas" action="#{relatoriosSaeMBean.iniciarRelatorioDesempenhoAcademico}" /></li>
						<li><h:commandLink value="Relat�rio de Discentes Priorit�rios que Solicitaram Bolsa Aux�lio" action="#{relatoriosSaeMBean.iniciarRelatorioDiscentesSituacaoCarencia}" onclick="setAba('deae')" /></li>
						<li><h:commandLink value="Relat�rio de Bolsistas (SIPAC) e Situa��o de Car�ncia" action="#{relatoriosSaeMBean.iniciarRelatorioDiscentesSituacaoCarenciaSIPAC}" onclick="setAba('deae')" /></li>
						<li><h:commandLink value="Relat�rio de Ocupa��o das Resid�ncias" action="#{relatoriosSaeMBean.iniciarRelatorioOcupacaoResidencia}"></h:commandLink> </li>
						<li><h:commandLink value="Relat�rio de Espectro de Renda com Dados da Matr�cula de Alunos de Gradua��o" action="#{rendaEspectro.iniciarMatricula}"></h:commandLink> </li>
						<li><h:commandLink value="Relat�rio da Quantidade de Alunos no Cadastro �nico por Curso/Centro" action="#{relatoriosSaeMBean.iniciarRelatorioAlunosCadastroUnico}"></h:commandLink> </li>
						<li><h:commandLink value="Relat�rio de Residentes no Cadastro �nico" action="#{relatoriosSaeMBean.iniciarRelatorioResidentesNoCadastroUnico}"></h:commandLink> </li>
						<li><h:commandLink value="Espectro das Resposta do Cadastro �nico" action="#{relQuestionarioCadUnico.iniciar}"></h:commandLink></li>
						<li><h:commandLink value="Relat�rio de Bolsistas com Mais de uma Bolsa" action="#{relatorioAcompanhamentoBolsas.iniciarRelatorioBolsistaDuploOuVinculo}" onclick="setAba('deae')" /></li>
						<li><h:commandLink value="Relat�rio de Acompanhamento de Desempenho Acad�mico de Bolsistas" action="#{relatorioAcompanhamentoBolsas.iniciarRelatorioBolsistaFrequencia}" onclick="setAba('deae')" /></li>
						<li><h:commandLink value="Relat�rio de Discentes Priorit�rios Por Faixa Et�ria" action="#{relatorioCadUnicoFaixaEtaria.iniciar}" /></li>
						<li> <h:commandLink value="Relat�rio de Bolsistas Contemplados" action="#{relatoriosSaeMBean.iniciarRelatorioContempladosDeferidos}"></h:commandLink> </li>
						<li> <h:commandLink value="Relat�rio de Movimenta��o de Discentes" action="#{relatoriosSaeMBean.iniciarRelatorioMovimentacaoDiscente}"></h:commandLink> </li>
						<li> <h:commandLink value="Relat�rio de Discentes de Gradua��o e P�s-Gradua��o" action="#{relatoriosSaeMBean.gerarRelatorioDiscentesVinculadosGraducaoPos}"></h:commandLink> </li>
						<li> <h:commandLink value="Relat�rio de Discentes Pagamentos no RU" action="#{relatoriosSaeMBean.discentesPagamentesRU}"></h:commandLink> </li>
						
						<!--
							RELATORIOS SOLICITADOS POR GLEYDSON PARA SEREM REMOVIDOS (#36732)
							<li><h:commandLink value="Bolsistas com CH Prevista para o Per�odo Atual do Discente" action="#{relatorioAcompanhamentoBolsas.iniciarRelatorioBolsistaCHMatriculada}" onclick="setAba('graduacao')" /></li>
							
							RELATORIOS SOLICITADOS PELO SAE PARA SEREM REMOVIDOS 
							<li> <h:commandLink value="Relat�rio de Alunos com Bolsas Alimenta��o" action="#{relatoriosSaeMBean.iniciarRelatorioTodosAlunosBolsaAlimentacao}"></h:commandLink> </li>
							<li> <h:commandLink value="Relat�rio Estat�stico de Bolsas" action="#{relatoriosSaeMBean.iniciarRelatorioAlimentacaoResidencia}"></h:commandLink> </li>
							<li> <h:commandLink value="Relat�rio de Bolsas por Bairro" action="#{relatoriosSaeMBean.iniciarRelatorioBairro}"></h:commandLink> </li>
							<li> <h:commandLink value="Relat�rio de Bolsas por Escolaridade do Pai do Aluno" action="#{relatoriosSaeMBean.iniciarRelatorioEscolaridadePai}"></h:commandLink> </li>
							<li> <h:commandLink value="Relat�rio de Bolsas por Profiss�o do Pai do Aluno" action="#{relatoriosSaeMBean.iniciarRelatorioProfissao}"></h:commandLink> </li>
							<li> <h:commandLink value="Relat�rio Espectro de Renda" action="#{rendaEspectro.gerarRendaEspectro}"></h:commandLink> </li>
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
        	abas.addTab('deae', "Assist�ncia ao Estudante");
        	abas.addTab('alimentacao', "Alimenta��o");
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