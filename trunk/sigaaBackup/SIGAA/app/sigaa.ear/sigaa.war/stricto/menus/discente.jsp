<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html"%>

<ul>
	<li>Dados do Discente
	<ul>
		<li><h:commandLink action="#{discenteStricto.iniciarCadastroDiscenteNovo}" value="Cadastrar Discente" onclick="setAba('discente')" id="iniciarCadastroDiscenteNovo"/></li>
		<li><h:commandLink action="#{ alteracaoDadosDiscente.iniciar}" value="Atualizar Dados Pessoais" onclick="setAba('discente')" id="iniciarAlteracaoDadosDiscente"/></li>
		<li><h:commandLink action="#{ discenteStricto.atualizar}" value="Atualizar Discente" onclick="setAba('discente')" id="atualizarDisccente"/></li>
		<li><h:commandLink action="#{ observacaoDiscente.iniciar}" value="Editar Observa��es do Discente" onclick="setAba('discente')" id="linkEditarObservacao"/></li>
		<li> <h:commandLink action="#{discenteStricto.iniciarCadastroDiscenteAntigo}" value="Cadastrar Discente Antigo" onclick="setAba('discente')" id="linkCadastrarDiscenteAntigo"/> </li>
		<li> <h:commandLink action="#{excluirDiscente.iniciar}" value="Excluir Aluno"  onclick="setAba('discente')" id="linkExcluirDiscentes"/> </li>
		<li> <h:commandLink action="#{alteracaoStatusDiscente.iniciar}" value="Alterar Status do Discente"  onclick="setAba('discente')" id="linkAlterarStatusDiscentes"/> </li>
	</ul>
	</li>

	<li> Documentos
		<ul>
		<li> <h:commandLink action="#{ atestadoMatricula.buscarDiscente }" value="Emitir Atestado de Matr�cula" onclick="setAba('discente')" id="linkEmitirAtestadoMatricula"/> </li>
		<li> <h:commandLink	action="#{ historico.buscarDiscente }"	value="Emitir Hist�rico" onclick="setAba('discente')" id="linkEmissaoDeHistoricos"/> </li>
		<li> <h:commandLink	action="#{ termoPublicacaoTD.buscarDiscente }"	value="Emitir TEDE" onclick="setAba('discente')" id="linkEmissaoTEDE"/> </li>
		</ul>
	</li>

	<li>Matr�cula
	<ul>
		<li> <h:commandLink	action="#{alteracaoStatusMatricula.iniciar }" value="Alterar Status de Matr�culas" onclick="setAba('discente')" id="linkAlterarStatusMatricula"/> </li>
		<li><h:commandLink action="#{ matriculaGraduacao.iniciarMatriculasRegulares}" value="Matricular Discente" onclick="setAba('discente')" id="linkMatricularDiscentes"/></li>		
	</ul>
	</li>

	<li>Orienta��es
	<ul>
		<li> <h:commandLink	action="#{orientacaoAcademica.iniciar }" value="Gerenciar Orienta��es" onclick="setAba('discente')" id="linkGerenciarOrientacoes"/> </li>
	</ul>
	</li>

	<li> Aproveitamento de Estudos
		<ul>
		<li> <h:commandLink action="#{aproveitamento.iniciarAproveitamento}" value="Aproveitar Disciplina/Atividade" onclick="setAba('discente')" id="linkAproveitarEstudo"/> </li>
		<li> <h:commandLink action="#{aproveitamento.iniciarCancelamento}" value="Excluir Aproveitamento de Componente" onclick="setAba('discente')" id="linkExcluirAproveitamento"/> </li>
		<li> <h:commandLink action="#{aproveitamentoCredito.iniciar}" value="Cadastrar Aproveitamento de Cr�dito" onclick="setAba('discente')" id="linkCadastrarAproveitamentoDeCredito"/> </li>
		<li> <h:commandLink action="#{aproveitamentoCredito.iniciarRemover}" value="Excluir Aproveitamento de Cr�dito" onclick="setAba('discente')" id="linkExcluirAproveitamentoCredito"/> </li>
		<li> <h:commandLink	action="#{retificacaoMatricula.iniciar}" value="Retificar Aproveitamento e Consolida��o de Turma" onclick="setAba('discente')" id="linkRetificarAproveitamento"/> </li>		
		</ul>
	</li>
	
	<li> Outras Opera��es
		<ul>
		<li> <h:commandLink action="#{bancaPos.listar}" value="Alterar/Remover Banca de Defesa" onclick="setAba('discente')" id="linkListarBancaPos"/> </li>
		<li> <h:commandLink action="#{bancaPos.iniciarQualificacao}" value="Cadastrar Banca de Qualifica��o" onclick="setAba('discente')" id="linkCadastrarBancaQualificaca"/> </li>
		<li> <h:commandLink action="#{bancaPos.iniciarDefesa}" value="Cadastrar Banca de Defesa" onclick="setAba('discente')" id="linkCadastroBancaDefesa"/> </li>
		<li> <h:commandLink action="#{calculosDiscente.iniciarStricto}" value="C�lculos de Discente"  onclick="setAba('discente')" id="linkCalculosDiscente"/> </li>		
		<li> <h:commandLink action="#{jubilamentoMBean.iniciar}" value="Cancelamento de Discentes"  onclick="setAba('discente')" id="linkCancelamentoPorAbandonoRef"/> </li>
		<li> <h:commandLink action="#{prorrogacao.buscarDiscenteCancelarProrrogacao}" value="Cancelar Prorroga��o de Prazo de Conclus�o" onclick="setAba('discente')" id="linkCancelarProrrogacaoPrazoConclusao"/> </li>
		<li> <h:commandLink action="#{consolidacaoIndividual.iniciar}" value="Consolida��o Individual" onclick="setAba('discente')" id="linkConsolidacaoIndividual"/> </li>
		<li> <h:commandLink action="#{implantarHistorico.iniciar}" value="Implantar Hist�rico do Aluno" onclick="setAba('discente')" id="linkImplantarhistorico"/> </li>
		<li> <h:commandLink action="#{prorrogacao.buscarDiscente}" value="Prorrogar Prazo de Conclus�o" onclick="setAba('discente')" id="linkProrrogarProazoConclusao"/> </li>
		<li> <h:commandLink action="#{homologacaoTrabalhoFinal.iniciarHomologacao}" value="Solicitar Homologa��o de Diploma" onclick="setAba('discente')" id="linkSolicitarHomologDiploma"/> </li>
		</ul>
	</li>	
</ul>