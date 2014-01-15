/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 18/03/2010
 *
 */
package br.ufrn.sigaa.extensao.negocio;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.List;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.extensao.dominio.AtividadeExtensao;
import br.ufrn.sigaa.negocio.ProjetoHelper;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto;

/***
 * 
 * Processador responsável por encerrar atividades de extensão.
 * @author Geyson
 *
 */
public class ProcessadorEncerrarProjetosCadastroExpirado extends AbstractProcessador{

	/**
	 * Encerra atividades de extensão após um quantidade de dias estabelecidos pela proex.
	 */
	@SuppressWarnings("unchecked")
	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {
		
		if(mov.getCodMovimento().equals(SigaaListaComando.ENCERRRAR_PROJETOS_COM_TEMPO_CADASTRO_EXPIRADO)){
			validate(mov);
			Collection<AtividadeExtensao> lista = (Collection<AtividadeExtensao>) ((MovimentoCadastro) mov).getColObjMovimentado();
			GenericDAO dao = getGenericDAO(mov);

			for (AtividadeExtensao atividade : lista) {

				if ( mov.getCodMovimento().equals(SigaaListaComando.ENCERRRAR_PROJETOS_COM_TEMPO_CADASTRO_EXPIRADO)){

					dao.updateField(AtividadeExtensao.class, atividade.getId(), "situacaoProjeto.id", TipoSituacaoProjeto.PROJETO_BASE_TEMPO_DE_CADASTRO_EXPIRADO);
					atividade.setSituacaoProjeto(new TipoSituacaoProjeto(TipoSituacaoProjeto.PROJETO_BASE_TEMPO_DE_CADASTRO_EXPIRADO));
					ProjetoHelper.gravarHistoricoSituacaoProjeto(atividade.getSituacaoProjeto().getId(), atividade.getProjeto().getId(), mov
							.getUsuarioLogado().getRegistroEntrada());
					ProjetoHelper.sincronizarSituacaoComProjetoBase(dao, atividade);

				}


			}
			dao.close();
			return null;
		}
		
		if(mov.getCodMovimento().equals(SigaaListaComando.REATIVAR_PROJETO_COM_TEMPO_CADASTRO_EXPIRADO)){
			AtividadeExtensao atividade = ((MovimentoCadastro) mov).getObjMovimentado();
			GenericDAO dao = getGenericDAO(mov);
			dao.updateField(AtividadeExtensao.class, atividade.getId(), "situacaoProjeto.id", TipoSituacaoProjeto.EXTENSAO_CADASTRO_EM_ANDAMENTO);
			atividade.setSituacaoProjeto(new TipoSituacaoProjeto(TipoSituacaoProjeto.EXTENSAO_CADASTRO_EM_ANDAMENTO));
			ProjetoHelper.gravarHistoricoSituacaoProjeto(atividade.getSituacaoProjeto().getId(), atividade.getProjeto().getId(), mov
					.getUsuarioLogado().getRegistroEntrada());
			ProjetoHelper.sincronizarSituacaoComProjetoBase(dao, atividade);
			dao.close();
			return null;
		}
		
		return null;
	}
	
	/**
	 * Valida se atividades de extensão possuem situação de cadastro em andamento.
	 */
	@SuppressWarnings("unchecked")
	public void validate(Movimento mov) throws NegocioException, ArqException {
		checkRole(SigaaPapeis.GESTOR_EXTENSAO, mov);
		List<AtividadeExtensao> lista = (List<AtividadeExtensao>) ((MovimentoCadastro) mov).getColObjMovimentado();
		ListaMensagens erros = new ListaMensagens();
		
		for (AtividadeExtensao atividade : lista) {
			if(!(atividade.getSituacaoProjeto().getId() == TipoSituacaoProjeto.EXTENSAO_CADASTRO_EM_ANDAMENTO)){
				erros.addErro("Atividade" + atividade.getTitulo() + "não pode ser encerrada.");
				
			}
		}
		
		checkValidation(erros);
		
	}

}
