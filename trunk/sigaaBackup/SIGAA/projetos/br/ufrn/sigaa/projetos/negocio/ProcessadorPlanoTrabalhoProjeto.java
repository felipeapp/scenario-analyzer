package br.ufrn.sigaa.projetos.negocio;

import java.rmi.RemoteException;
import java.util.Date;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.projetos.dominio.CalendarioProjeto;
import br.ufrn.sigaa.projetos.dominio.CronogramaProjeto;
import br.ufrn.sigaa.projetos.dominio.DiscenteProjeto;
import br.ufrn.sigaa.projetos.dominio.PlanoTrabalhoProjeto;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoDiscenteProjeto;

/** Processador respons�vel por opera��es envolvendo planos de trabalho de a��es associadas */
public class ProcessadorPlanoTrabalhoProjeto extends AbstractProcessador {

	@Override
	public Object execute(Movimento movimento) throws NegocioException, ArqException,
			RemoteException {
		
		MovimentoCadastro mov = (MovimentoCadastro) movimento;
		validate(mov);
		
		PlanoTrabalhoProjeto planoPro = (PlanoTrabalhoProjeto) mov.getObjMovimentado();

		if (mov.getCodMovimento().equals(SigaaListaComando.CADASTRAR_PLANO_TRABALHO_PROJETO)) {
			cadastrarPlanoTrabalho(planoPro, mov);
		}
		if (mov.getCodMovimento().equals(SigaaListaComando.REMOVER_PLANO_TRABALHO_PROJETO)) {
			removerPlanoTrabalho(planoPro, mov);
		}
		
		return null;
	}

	/** Cadastra plano de trabalho de a��es associadas */
	private void cadastrarPlanoTrabalho(PlanoTrabalhoProjeto planoPro, MovimentoCadastro mov) throws DAOException{
		GenericDAO dao = getGenericDAO(mov);
		try{
			// Evitar erro de objeto transient...
			if ((planoPro.getDiscenteProjeto().getBanco() != null) && (planoPro.getDiscenteProjeto().getBanco().getId() <= 0)) {
				planoPro.getDiscenteProjeto().setBanco(null);
			}

			dao.createOrUpdate(planoPro.getDiscenteProjeto());

			//Salvando plano de trabalho do projeto
			if (planoPro.getId() == 0) {
				dao.create(planoPro);
			}else {
				removerCronogramaAntigo(mov);
				dao.update(planoPro);
			}
			
			planoPro.getDiscenteProjeto().setPlanoTrabalhoProjeto(planoPro);
			planoPro.getDiscenteProjeto().setProjeto(planoPro.getProjeto());
			planoPro.getDiscenteProjeto().setDataFim(planoPro.getDataFim()); //definindo uma data fim padr�o para o discente (data fim do plano de trabalho)
			planoPro.getDiscenteProjeto().setTipoVinculo(planoPro.getTipoVinculo());
			dao.update(planoPro.getDiscenteProjeto());

			// gravando hist�rico da situa��o do discente	    
			DiscenteProjetoHelper.gravarHistoricoSituacao(dao, planoPro.getDiscenteProjeto(), mov.getUsuarioLogado().getRegistroEntrada());
		}
		finally{
			dao.close();
		}

	}
	
	/** Remove plano de trabalho de a��es associadas */
	private void removerPlanoTrabalho(PlanoTrabalhoProjeto planoPro, MovimentoCadastro mov) throws DAOException{
		GenericDAO dao = getGenericDAO(mov);
		try{
			if (planoPro.getDiscenteProjeto() != null) {
				DiscenteProjeto discPro = planoPro.getDiscenteProjeto(); 
				discPro.setAtivo(false);
				discPro.setSituacaoDiscenteProjeto(new TipoSituacaoDiscenteProjeto(TipoSituacaoDiscenteProjeto.EXCLUIDO));		    
				dao.update(discPro);

				// gravando hist�rico da situa��o do discente	    
				DiscenteProjetoHelper.gravarHistoricoSituacao(dao, discPro, mov.getUsuarioLogado().getRegistroEntrada());
			}
			planoPro.setHistoricoDiscentesPlano(dao.findByExactField(DiscenteProjeto.class, "planoTrabalhoProjeto.id", planoPro.getId()));
			//Excluindo todos os discentes do hist�rico do plano
			for (DiscenteProjeto histoPlano : planoPro.getHistoricoDiscentesPlano()) {
				histoPlano.setAtivo(false);
				histoPlano.setSituacaoDiscenteProjeto(new TipoSituacaoDiscenteProjeto(TipoSituacaoDiscenteProjeto.EXCLUIDO));		    
				dao.update(histoPlano);

				// gravando hist�rico da situa��o de todos os discente que j� passaram por este plano de trabalho 	    
				DiscenteProjetoHelper.gravarHistoricoSituacao(dao, histoPlano, mov.getUsuarioLogado().getRegistroEntrada());
			}

			planoPro.setAtivo(false);
			planoPro.setRegistroEntrada(mov.getUsuarioLogado().getRegistroEntrada());
			dao.update(planoPro);
		}
		finally{
			dao.close();
		}
	}

	
    /**
     * Respons�vel pela remo��o de dados antigos do cronograma do plano.
     * Utilizado nas altera��es do plano.
     * 
     * @param mov
     * @throws DAOException
     */
    private void removerCronogramaAntigo(MovimentoCadastro mov) throws DAOException {
    	GenericDAO dao = getGenericDAO(mov);
    	try {
    		PlanoTrabalhoProjeto pt = mov.getObjMovimentado();	    
    		if( !ValidatorUtil.isEmpty(pt.getCronogramas())
    				&& pt.getCronogramas().get(0).getId() == 0 ) {

    			PlanoTrabalhoProjeto planoAntigo = dao.findByPrimaryKey(pt.getId(), PlanoTrabalhoProjeto.class);
    			if (planoAntigo.getCronogramas() != null) {
    				planoAntigo.getCronogramas().iterator();
    			}
    			dao.detach(planoAntigo);

    			// Remover cronograma do plano
    			for (CronogramaProjeto cronogramaAntigo : planoAntigo.getCronogramas() ) {
    				cronogramaAntigo.setPlanoTrabalhoExtensao(null);
    				dao.remove(cronogramaAntigo);
    			}
    			planoAntigo.setId(0);
    		}
    	} finally {
    		dao.close();
    	}
    }

	
	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		MovimentoCadastro aMov = (MovimentoCadastro) mov;
		ListaMensagens lista = new ListaMensagens();
		PlanoTrabalhoProjeto plano = aMov.getObjMovimentado();
		GenericDAO dao = getGenericDAO(aMov);

		// Se o usu�rio n�o for servidor ou docente externo e estiver tentando realizar esta opera��o.
		Usuario usuario = (Usuario) mov.getUsuarioLogado();
		if (!usuario.getVinculoAtivo().isVinculoServidor() && !usuario.getVinculoAtivo().isVinculoDocenteExterno()) {
			lista.addErro("Apenas Docentes ou T�cnicos Administrativos podem realizar esta opera��o.");
		}
		
		try {
			if (mov.getCodMovimento().equals(SigaaListaComando.CADASTRAR_PLANO_TRABALHO_PROJETO)) {			
	    		Date dataInicio = plano.getDiscenteProjeto().getDataInicio();
	        	CalendarioProjeto calAtualProjetos = dao.findByExactField(CalendarioProjeto.class, "anoReferencia", plano.getProjeto().getAno(), true);
	        	PlanoTrabalhoProjetoValidator.estaDentroPeriodoBolsa(dataInicio, calAtualProjetos, lista);			
				
				PlanoTrabalhoProjetoValidator.validaDadosGerais(plano, lista, null);
				PlanoTrabalhoProjetoValidator.validarCronogramaProjeto(plano, lista);
			}else if (mov.getCodMovimento().equals(SigaaListaComando.REMOVER_PLANO_TRABALHO_PROJETO)) {	
				
			}
		}finally {
			dao.close();
		}

		checkValidation(lista);
	}

}
