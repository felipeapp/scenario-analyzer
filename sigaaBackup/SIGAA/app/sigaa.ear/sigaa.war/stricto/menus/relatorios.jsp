<%@page import="br.ufrn.comum.dominio.Sistema"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html"%>

<ul>

	<li>Alunos
	<ul>
		<li><h:commandLink action="#{relatoriosStricto.iniciarRelatorioMatriculadosAtividades}" value="Alunos Matriculados em Atividades" onclick="setAba('relatorios')" id="iniciarRelatorioMatriculadosAtividades"/></li>
		<li><a href="${ ctx }/arquivoSttu?nivel=S&log=true" id="linkAlunoProblemaCarteira">Alunos com problema na Carteira de Estudante</a></li>
		<li><h:commandLink value="Consulta Avan�ada" action="#{ buscaAvancadaDiscenteMBean.iniciarCoordStricto }" id="iniciarCoordStricto"/></li>
		<li><h:commandLink action="#{relatorioDiscente.iniciaRelatorioAlunosAtivosNaoMatriculadosBolsa}" value="Lista de Alunos Ativos n�o Matriculados" onclick="setAba('relatorios')" id="relAlunosAtivosNaoMatriculadosBolsa"/></li>
		<li><h:commandLink action="#{relatorioDiscente.carregarListaContatoAlunos}" value="Lista de Contatos de Alunos" onclick="setAba('relatorios')" id="listContatosAlunos"/></li>
		<li><h:commandLink action="#{relatorioDiscente.iniciaRelatorioAlunosReprovados}" value="Lista de Alunos Reprovados" onclick="setAba('relatorios')" id="listAlunosReprov"/></li>
		<li>
			<h:commandLink action="#{relatorioDiscente.carregarSelecaoRelatorio}"  onclick="setAba('relatorios')" value="Lista para elei��o" id="carregarSelecaoRelatorio">
				<f:param value="seleciona_eleicao.jsf" name="relatorio"/>
			</h:commandLink>
		</li>
		<li><h:commandLink action="#{relatorioQuantitativoAlunosPrograma.iniciarQuantitativoMatriculado}" value="Quantitativo de Alunos Ativos / Matriculados" onclick="setAba('relatorios')" id="iniciarQuantitativoMatriculado"/></li>
		<li><h:commandLink action="#{relatoriosStricto.iniciarRelatorioQuantitativoAlunosMatriculadosMes}" value="Quantitativo Geral de Alunos Matriculados por M�s" onclick="setAba('relatorios')"  id="quantGeralAlunosMatriculadosMes"/></li>
		<li><h:commandLink action="#{relatorioQuantitativoAlunosPrograma.iniciarQuantitativoAtivo}" value="Quantitativo de Alunos Ativos" onclick="setAba('relatorios')" id="iniciarQuantAlunoAtivos"/></li>
		<li><h:commandLink action="#{relatorioConcluintesPosBean.iniciar}" value="Quantitativo de Alunos Concluintes" onclick="setAba('relatorios')" id="quantAlunosConcluintes"/></li>
		<li><h:commandLink action="#{relatorioPrazoMaximoPosBean.iniciar}" value="Relat�rio de Prazo M�ximo" onclick="setAba('relatorios')" id="relatorioPrazomaximo"/></li>
		<li><h:commandLink action="#{relatorioAlunosMatriculasPosBean.iniciar}" value="Relat�rio de Alunos e Matr�culas" onclick="setAba('relatorios')" id="relAlunosMatriculas"/></li>
		
		<% if (Sistema.isSipacAtivo()) { %>
			<li><h:commandLink action="#{relatorioBolsasStrictoBean.iniciar}" value="Relat�rio de Bolsistas" onclick="setAba('relatorios')" id="relDeBolsistas"/></li>
			<li><h:commandLink action="#{relatorioDiscente.iniciaRelatorioPrazoMaximoBolsaAlunos}" value="Relat�rio de Bolsistas por Per�odo" onclick="setAba('relatorios')" id="relatorioDeBolsistaPorPeriodo"/></li>
		<% } %>
		
		<li><h:commandLink action="#{ relatorioDiscente.iniciarRelatorioDiscentesEspeciais }" value="Relat�rio de Alunos Especiais e Disciplinas"  onclick="setAba('relatorios')" id="relatorioDiscentesEspeciais"/></li>
		<li><h:commandLink action="#{ relatorioTempoMedioTitulacaoMBean.iniciarTempoMedioTitulacaoPorDiscente }" value="Relat�rio de Tempo M�dio de Titula��o por Discente"  onclick="setAba('relatorios')" id="relTempoMedioTitulacao"/></li>
		<li><h:commandLink action="#{ relatorioTempoMedioTitulacaoMBean.iniciarTempoMedioTitulacaoPorOrientador }" value="Relat�rio de Tempo M�dio de Titula��o por Orientador"  onclick="setAba('relatorios')" id="relTempoMedioTitulacaoPorOrientador"/></li>
		<li><h:commandLink action="#{relatorioTaxaSucessoStricto.iniciarTaxaSucesso}" value="Relat�rio de Taxa de Sucesso" id="iniciarTaxaSucesso"/></li>
		<li><h:commandLink action="#{relatorioCreditosIntegralizadosMBean.iniciar}" value="Relat�rio de Cr�ditos Integralizados" id="iniciarCreditosIntegralizados"/></li>
	</ul>
	</li>
	
	<li>Cursos
	<ul>
		<li><h:commandLink action="#{relatorioCursosPos.iniciar}" value="Conceitos dos Cursos" onclick="setAba('relatorios')" id="conceitosDosCursos"/></li>
		<li><h:commandLink action="#{cursoGrad.listar}" value="Consulta de Cursos" onclick="setAba('relatorios')" id="consultaDeCursos"/></li>

	</ul>
	</li>

	<li>Defesas
		<ul>
			<li><h:commandLink action="#{consultarDefesaMBean.iniciar}" value="Consultar Defesas de P�s-gradua��o" id="linkConsultarDefesaPos" onclick="setAba('relatorios')"/></li>
			<li><h:commandLink action="#{certificadoBancaPos.iniciar}" value="Emitir Declara��o de Participa��o em Banca" id="linkEmissaoDeclaracaoParticipacaoemBanca" onclick="setAba('relatorios')"/></li>
			<li><h:commandLink action="#{participantesDasBancas.iniciar}" value="Participantes das Bancas" id="linkParticipacaoBanca" onclick="setAba('relatorios')"/></li>
			<li><h:commandLink action="#{relatoriosStricto.iniciarRelatorioQuantativoDefesasAnosDetalhado}" value="Quantitativo Detalhado de Defesas por Ano" id="quantDetalhadoDefesaAno" onclick="setAba('relatorios')"/></li> 			
			<li><h:commandLink action="#{relatoriosStricto.iniciarRelatorioQuantativoDefesasAnos}" value="Quantitativo Geral de Defesas por Ano" id="quantGeralDefesaAno" onclick="setAba('relatorios')"/></li>
		</ul>
	</li>

	<li>Docentes
	<ul>
		<li><h:commandLink action="#{relatorioDocentesOrientacoes.iniciar}" value="Docentes X Orienta��es Conclu�das" onclick="setAba('relatorios')" id="docentesOrientacoesConcluidas"/></li>
		<li><h:commandLink action="#{certificadoBancaPos.iniciar}" value="Emitir Declara��o de Participa��o em Banca" id="emitirDecParticEmbanca"/></li>
		<li><h:commandLink action="#{relatorioEquipePrograma.iniciar}" value="Membros dos Programas" onclick="setAba('relatorios')" id="membDosProg"/></li>
		<li><h:commandLink action="#{relatorioOrientacoes.iniciar}" value="Orienta��es por programa" onclick="setAba('relatorios')" id="orientacoesPorProgram"/></li>
		<li><html:link action="/pesquisa/projetoPesquisa/buscarProjetos.do?dispatch=consulta&popular=true">Projetos de Pesquisa</html:link></li>
		<li><h:commandLink action="#{relatorioDocentesTurmaBean.iniciar}" value="Relat�rio de Docentes por Turma" onclick="setAba('relatorios')" id="relDeDocentesPORturma"/></li>
		<li><h:commandLink action="#{relatorioAtividadesDocente.iniciarRelatorioAtividades}" value="Relat�rio de Atividades do Docente" onclick="setAba('relatorios')" id="relAtivDoDocente"/></li>
		<li><h:commandLink action="#{relatorioAtividadesDocente.iniciarRelatorioSemAtividades}" value="Relat�rio de Docente Sem Atividades" onclick="setAba('relatorios')" id="RelDeDocenteSemAtiv"/></li>
	</ul>
	</li>

	<li>Coordenadores de Programa
	<ul>
		<li><h:commandLink action="#{coordenacaoCurso.listar}" value="Listar Coordenadores" onclick="setAba('relatorios')" id="listarCoordenacoesAbaRelatorios"/></li>
		<li> <a href="${ctx}/ensino/secretaria_unidade/secretarios_programa.jsf?aba=relatorios" id="listSecret"> Lista de Secret�rios </a></li>
	</ul>
	</li>

	<li>Programas
	<ul>
		<li><h:commandLink action="#{relatorioPrograma.iniciarProgramaNMatriculaOnline}" value="Relat�rio dos Programas que n�o fizeram Matr�cula on-line" id="iniciarProgramaNMatriculaOnline"/></li>
		<li><h:commandLink action="#{relatorioPrograma.iniciarProgramaNProcessoSeletivoOnline}" value="Relat�rio dos Programas que n�o usaram processos seletivos on-line" id="iniciarProgramaNProcessoSeletivoOnline"/></li>
	</ul>
	</li>
	
	<li>Biblioteca
		<ul>
			<li>
				<h:commandLink value="Verificar Situa��o Usu�rio / Emitir Declara��o de Quita��o" action="#{verificaSituacaoUsuarioBibliotecaMBean.iniciarVerificacao}" onclick="setAba('relatorios')" id="verifSituacaoEmitirQuitacao"/>
			</li>
		</ul>
	</li>
	
	<li>Processo Seletivo
		<ul>
			<li>
				<h:commandLink value="Relat�rio de Processos Seletivos (Demandas x Vagas)" action="#{processoSeletivo.iniciaRelatorioDemandaVagas}" onclick="setAba('relatorios')" id="relProcessosSeletivos"/>
			</li>
		</ul>
	</li>	

</ul>