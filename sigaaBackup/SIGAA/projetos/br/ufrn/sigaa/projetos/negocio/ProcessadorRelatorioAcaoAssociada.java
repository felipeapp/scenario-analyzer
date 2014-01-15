/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 31/01/2011
 *
 */
package br.ufrn.sigaa.projetos.negocio;

import java.rmi.RemoteException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.dominio.Responsavel;
import br.ufrn.rh.dominio.NivelResponsabilidade;
import br.ufrn.sigaa.arq.dao.UnidadeDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.arq.util.EnvioMensagemHelper;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.extensao.dominio.OrcamentoConsolidado;
import br.ufrn.sigaa.projetos.dominio.ArquivoProjeto;
import br.ufrn.sigaa.projetos.dominio.DetalhamentoRecursosProjeto;
import br.ufrn.sigaa.projetos.dominio.MembroProjeto;
import br.ufrn.sigaa.projetos.dominio.Projeto;
import br.ufrn.sigaa.projetos.dominio.RelatorioAcaoAssociada;
import br.ufrn.sigaa.projetos.dominio.TipoParecerAvaliacaoProjeto;
import br.ufrn.sigaa.projetos.dominio.TipoRelatorioProjeto;

/**
 * Processador para realizar cadastro de relat�rios finais e parciais de
 * a��es associadas
 * 
 * 
 * @author geyson
 * 
 */
public class ProcessadorRelatorioAcaoAssociada extends AbstractProcessador {

	/** Opera��es relacionadas aos relat�rios de a��es associadas */
	@Override
	public Object execute(Movimento movimento) throws NegocioException, ArqException,
	RemoteException {
		MovimentoCadastro mov = (MovimentoCadastro) movimento;
		GenericDAO dao = getGenericDAO(mov);
		validate(mov);

		RelatorioAcaoAssociada relatorio = mov.getObjMovimentado();
		try {
			
			if (mov.getCodMovimento().equals(SigaaListaComando.REMOVER_RELATORIO_ACAO_ASSOCIADA)) {
				removerRelatorioAssociado(relatorio, dao);
			}
			
			if (mov.getCodMovimento().equals(SigaaListaComando.ENVIAR_RELATORIO_ACAO_ASSOCIADA)
					|| mov.getCodMovimento().equals(SigaaListaComando.SALVAR_RELATORIO_ACAO_ASSOCIADA)) {
				salvarEnviarRelatorioAssociada(relatorio, dao, mov);
			}

			if (mov.getCodMovimento().equals(SigaaListaComando.VALIDAR_RELATORIO_ACAO_ASSOCIADA_DPTO)) {
				relatorio = validarRelatorioPorDepartamento(mov);
			}
			
			if (mov.getCodMovimento().equals(SigaaListaComando.VALIDAR_RELATORIO_ACAO_ASSOCIADA_COMITE)) {
				relatorio = validarRelatorioPorComite(mov);
			}			

			if (mov.getCodMovimento().equals(SigaaListaComando.DEVOLVER_RELATORIO_ACAO_ASSOCIADA_COORD)) {
				relatorio = devolverRelatorioParaCoordenacao(mov);
			}			

			
		}finally{
			dao.close();
		}

		return relatorio;
	}
	
	
	
	/** Descreve procedimento de valida��o do relat�rio de a��es associadas pelo chefe do departamento. */
	private RelatorioAcaoAssociada validarRelatorioPorDepartamento(MovimentoCadastro mov) throws NegocioException, ArqException,RemoteException {
		RelatorioAcaoAssociada relatorio = mov.getObjMovimentado();
		GenericDAO dao = getGenericDAO(mov);
		try {
			relatorio.setRegistroEntradaDepartamento(mov.getUsuarioLogado().getRegistroEntrada().getId());
			relatorio.setDataValidacaoDepartamento(new Date());
			
			/* @negocio: Se � relat�rio final e todas as inst�ncias o aprovaram, ent�o a a��o dever� ser conclu�da. */
	    	if (relatorio.isAprovadoComite() && relatorio.isAprovadoDepartamento()
	    			&& relatorio.getTipoRelatorio().getId() == TipoRelatorioProjeto.RELATORIO_FINAL) {

	    		//Evitar erro de lazy
	    		Projeto proj = relatorio.getProjeto();
	    		proj = dao.findByPrimaryKey(proj.getId(), Projeto.class);
	    		proj.getEquipe().iterator();
	    		//proj.getPlanosTrabalho().iterator(); TODO: criar Dao
	    		proj.getDiscentesProjeto().iterator();

	    		//concluindo a a��o associada, finalizando os membros da equipe, etc.
	    		ProcessadorProjetoBase procProjeto = new ProcessadorProjetoBase();
	    		MovimentoCadastro movPro = new MovimentoCadastro();				
	    		movPro.setCodMovimento(SigaaListaComando.CONCLUIR_PROJETO_BASE);
	    		movPro.setUsuarioLogado(movPro.getUsuarioLogado());
	    		movPro.setObjMovimentado(proj);
	    		movPro.setSistema( movPro.getSistema() );
	    		procProjeto.execute(movPro);

	    		// ao gravar projeto o relat�rio � carregado na sess�o
	    		// evitar erro de 2 objetos com mesmo id na sess�o.
	    		dao.clearSession();
	    	}

			//Libera relat�rio para edi��o do coordenador.
			if (relatorio.isReprovadoDepartamento() || relatorio.isAprovadoComRecomendacoesDepartamento()) {
				relatorio.setDataEnvio(null);
			}
			dao.update(relatorio);
			return relatorio;
		}finally {
			dao.close();
		}
	}
	

	/** Descreve procedimento de valida��o do relat�rio de a��es associadas pelo membro do comit� integrado. */
	private RelatorioAcaoAssociada validarRelatorioPorComite(MovimentoCadastro mov) throws NegocioException, ArqException,RemoteException { 
		RelatorioAcaoAssociada relatorio = mov.getObjMovimentado();
		GenericDAO dao = getGenericDAO(mov);
		try {
			relatorio.setRegistroEntradaDepartamento(mov.getUsuarioLogado().getRegistroEntrada().getId());
			relatorio.setDataValidacaoDepartamento(new Date());

			// @negocio: Libera relat�rio para edi��o do coordenador e reenvio para nova aprova��o.
			if (relatorio.isReprovadoComite() || relatorio.isAprovadoComRecomendacoesComite()) {
				relatorio.setDataEnvio(null);
			}
			
			dao.update(relatorio);
			
			
			/* @negocio: Se � relat�rio final e todas as inst�ncias o aprovaram, ent�o a a��o dever� ser conclu�da. */
	    	if (relatorio.isAprovadoComite() && relatorio.getTipoRelatorio().getId() == TipoRelatorioProjeto.RELATORIO_FINAL) {

	    		//Evitar erro de lazy
	    		Projeto proj = relatorio.getProjeto();
	    		proj = dao.findByPrimaryKey(proj.getId(), Projeto.class);
	    		proj.getEquipe().iterator();
	    		//proj.getPlanosTrabalho().iterator(); TODO: criar Dao
	    		proj.getDiscentesProjeto().iterator();

	    		//concluindo a a��o associada, finalizando os membros da equipe, etc.
	    		ProcessadorProjetoBase procProjeto = new ProcessadorProjetoBase();
	    		MovimentoCadastro movPro = new MovimentoCadastro();				
	    		movPro.setCodMovimento(SigaaListaComando.CONCLUIR_PROJETO_BASE);
	    		movPro.setUsuarioLogado(mov.getUsuarioLogado());
	    		movPro.setObjMovimentado(proj);
	    		movPro.setSistema( mov.getSistema() );
	    		procProjeto.execute(movPro);

	    		// ao gravar projeto o relat�rio � carregado na sess�o
	    		// evitar erro de 2 objetos com mesmo id na sess�o.
	    		dao.clearSession();
	    	}

	    	// @negocio: A��es associadas n�o realizadas devem passar para situa��o de 'cancelada' para que n�o aja cobran�a de relat�rios ou penalidades sejam aplicadas.
	    	if (relatorio.getTipoParecerComite() != null
	    			&& relatorio.getTipoParecerComite().getId() == TipoParecerAvaliacaoProjeto.PROJETO_NAO_REALIZADO
	    			&& relatorio.isRelatorioFinal()) {
	    		
	    		//Cancelando a a��o associada, todas as a��es vinculadas, membros da equipe, etc.
	    		Projeto projeto = dao.findByPrimaryKey(relatorio.getProjeto().getId(), Projeto.class);
	    		ProcessadorProjetoBase processador = new ProcessadorProjetoBase();
	    		MovimentoCadastro mov2 = new MovimentoCadastro();				
	    		mov2.setUsuarioLogado(mov.getUsuarioLogado());
	    		mov2.setSistema(mov.getSistema());
	    		mov2.setObjMovimentado(projeto);
	    		mov2.setCodMovimento(SigaaListaComando.NAO_EXECUTAR_PROJETO_BASE);
	    		processador.execute(mov2);

	    	}

			return relatorio;
		}finally {
			dao.close();
		}
	}

	

	/** Remove relat�rio  */
	private void removerRelatorioAssociado(RelatorioAcaoAssociada relatorio, GenericDAO dao) throws DAOException{
		relatorio.setAtivo(false);
		dao.updateField(RelatorioAcaoAssociada.class, relatorio.getId(), "ativo", false);
		// Arquivos
		if (relatorio.getArquivos() != null) {
			for (ArquivoProjeto ar : relatorio.getArquivos()) {
				if (ar.getId() != 0) {
					dao.updateField(ArquivoProjeto.class, ar.getId(), "ativo", false);
				}
			}
		}
	}
	
	/** Salva ou envia relat�rio */
	private void salvarEnviarRelatorioAssociada(RelatorioAcaoAssociada relatorio, GenericDAO dao, MovimentoCadastro mov) throws DAOException{
		dao.clearSession();
		dao.createOrUpdate(relatorio);
		dao.detach(relatorio);

		// Detalhamento do recursos
		for (DetalhamentoRecursosProjeto det : relatorio.getDetalhamentoRecursosProjeto()) {
			dao.createOrUpdate(det);
		}

		// Arquivos
		for (ArquivoProjeto ar : relatorio.getArquivos()) {
			dao.createOrUpdate(ar);
		}

		// Quando envia o relat�rio atualiza os dados na a��o associada...
		if (mov.getCodMovimento().equals(SigaaListaComando.ENVIAR_RELATORIO_ACAO_ASSOCIADA)) {

			// Apaga a valida��o n�o aprovada realizada pelo chefe de departamento 
			if ((relatorio.isReprovadoDepartamento() || relatorio.isAprovadoComRecomendacoesDepartamento())) {
				relatorio.setDataValidacaoDepartamento(null);
				relatorio.setTipoParecerDepartamento(null);
				relatorio.setParecerDepartamento(null);
				
			}
			// Apaga a valida��o n�o aprovada realizada pelo comit�  
			if((relatorio.isReprovadoComite() || relatorio.isAprovadoComRecomendacoesComite())){
				relatorio.setDataValidacaoComite(null);
				relatorio.setTipoParecerComite(null);
				relatorio.setParecerComite(null);
			}

			//Envia o relat�rio
			relatorio.setDataEnvio(new Date());
			dao.update(relatorio);


			// Atualizando or�amento utilizado no projeto....
			Projeto projeto = dao.findByPrimaryKey(relatorio.getProjeto().getId(), Projeto.class);
			relatorio.setProjeto(projeto);
			for (OrcamentoConsolidado orc : projeto.getOrcamentoConsolidado()) {
				for (DetalhamentoRecursosProjeto drp : relatorio.getDetalhamentoRecursosProjeto()) {
					if (drp.getElemento().getId() == orc.getElementoDespesa().getId()) {
						orc.setFundacaoUtilizado(drp.getInterno());
						orc.setFundoUtilizado(drp.getExterno());
						orc.setOutrosUtilizado(drp.getOutros());
					}
					// gravando o total gasto no detalhamento da despesa...
					dao.createOrUpdate(drp);
				}
				dao.update(orc);
			}
			dao.update(projeto);
			
			notificarChefesDepartamentos(mov);
		}
		
	}
	
	
	/** Descreve procedimento de devolu��o do relat�rio de a��es associadas pelo membro do comit� integrado para ajustes no relat�rio por parte da coordena��o do projeto. */
	private RelatorioAcaoAssociada devolverRelatorioParaCoordenacao(MovimentoCadastro mov) throws NegocioException, ArqException,RemoteException { 
		RelatorioAcaoAssociada relatorio = mov.getObjMovimentado();
		GenericDAO dao = getGenericDAO(mov);
		try {
			// @negocio: Libera relat�rio para edi��o do coordenador e reenvio para nova aprova��o.
			relatorio.setRegistroEntradaDepartamento(null);
			relatorio.setDataValidacaoDepartamento(null);
			relatorio.setDataEnvio(null);
			relatorio.setDataValidacaoComite(null);
			relatorio.setRegistroEntradaComite(null);
			relatorio.setTipoParecerComite(null);
			relatorio.setTipoParecerDepartamento(null);
			relatorio.setParecerComite(null);
			relatorio.setParecerDepartamento(null);
			dao.update(relatorio);
			return relatorio;
		}finally {
			dao.close();
		}
	}

	/** Valida��es */
	@Override
	public void validate(Movimento movimento) throws NegocioException, ArqException {
		MovimentoCadastro aMov = (MovimentoCadastro) movimento;
		ListaMensagens lista = new ListaMensagens();
		RelatorioAcaoAssociada relatorio = aMov.getObjMovimentado();
		
		// Se o usu�rio n�o for servidor ou docente externo e estiver tentando realizar esta opera��o.
		Usuario usuario = (Usuario) movimento.getUsuarioLogado();
		if (!usuario.getVinculoAtivo().isVinculoServidor() && !usuario.getVinculoAtivo().isVinculoDocenteExterno()) {
			lista.addErro("Apenas Docentes ou T�cnicos Administrativos podem realizar esta opera��o.");
		}
		
		if (aMov.getCodMovimento().equals(SigaaListaComando.ENVIAR_RELATORIO_ACAO_ASSOCIADA)) {
			RelatorioProjetoValidator.validaDadosGeraisRelatorioProjeto(relatorio, lista);
		}
		
		if (aMov.getCodMovimento().equals(SigaaListaComando.VALIDAR_RELATORIO_ACAO_ASSOCIADA_DPTO)) {
			lista = relatorio.validate();
		}
		
		if (aMov.getCodMovimento().equals(SigaaListaComando.VALIDAR_RELATORIO_ACAO_ASSOCIADA_COMITE)) {
			lista = relatorio.validate();
		}
		
		checkValidation(lista);
	}
	
	 /**
     * M�todo utilizado para notificar por e-mail os Chefes dos departamentos envolvidos o envio de relat�rio.
     * 
     * @param mov
     * @throws DAOException
     */
    public void notificarChefesDepartamentos(MovimentoCadastro mov) throws DAOException {
	RelatorioAcaoAssociada relatorio = mov.getObjMovimentado();
	Projeto projeto = relatorio.getProjeto();
	if (!ValidatorUtil.isEmpty(projeto.getEquipe())) {
        	UnidadeDao dao = getDAO(UnidadeDao.class, mov);
        	try {
        	    /** @negocio: Um email com os dados do projeto deve ser enviado para os deptos. de todos os servidores envolvidos. 
        	     * Sem duplica��o no envio, caso tenha mais de um servidor do mesmo departamento. */
        	    Set<Integer> unidadesParticipantes = new HashSet<Integer>();        	    
        	    for (MembroProjeto mp : projeto.getEquipe()) {
        		if (!ValidatorUtil.isEmpty(mp.getServidor()) && !ValidatorUtil.isEmpty(mp.getServidor().getUnidade())) {
        		    unidadesParticipantes.add(mp.getServidor().getUnidade().getId());
        		}
        	    }
        	    
        	    List<Responsavel> responsaveis = dao.findResponsaveisByUnidades(unidadesParticipantes, new char[] {NivelResponsabilidade.CHEFE} );
        	    EnvioMensagemHelper.comunicarRelatorioResponsaveisUnidade(projeto, responsaveis);
        	    
        	}finally {
        	    dao.close();
        	}
	}
    }

}
