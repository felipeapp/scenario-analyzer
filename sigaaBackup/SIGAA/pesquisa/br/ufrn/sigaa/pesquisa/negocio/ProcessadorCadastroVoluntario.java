/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 05/03/2007
 *
 */
package br.ufrn.sigaa.pesquisa.negocio;

import java.rmi.RemoteException;
import java.util.Date;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.sigaa.arq.dao.pesquisa.PlanoTrabalhoDao;
import br.ufrn.sigaa.arq.dao.prodocente.BolsaObtidaDao;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.pesquisa.dominio.MembroProjetoDiscente;
import br.ufrn.sigaa.pesquisa.dominio.PlanoTrabalho;
import br.ufrn.sigaa.pesquisa.dominio.ProjetoPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.TipoBolsaPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.TipoStatusPlanoTrabalho;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * Processador responsável pelo cadastro de planos de trabalho e voluntários
 * de projetos de pesquisa
 *
 * @author Ricardo Wendell
 *
 */
@Deprecated
public class ProcessadorCadastroVoluntario extends AbstractProcessador {

	/**
	 * Método responsável pela execução do processamento do cadastro de voluntário
	 */
	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {

		validate(mov);

		PlanoTrabalho plano = (PlanoTrabalho) mov;
		MembroProjetoDiscente membroDiscente = plano.getMembroProjetoDiscente();

		GenericDAO dao = getGenericDAO(mov);

		// Buscar projeto
		ProjetoPesquisa projeto = dao.findByPrimaryKey(plano.getProjetoPesquisa().getId(), ProjetoPesquisa.class);

		// Buscar Orientador
		Servidor orientador = ((Usuario) mov.getUsuarioLogado()).getServidor();

		plano.setStatus(TipoStatusPlanoTrabalho.EM_ANDAMENTO);
		plano.setMembroProjetoDiscente(null);
		plano.setOrientador(orientador);
		plano.setProjetoPesquisa(projeto);
		plano.setDataCadastro(new Date());
		plano.setRegistroEntrada(mov.getUsuarioLogado().getRegistroEntrada());
		plano.setCota( projeto.getEdital().getCota() );
		dao.create(plano);

		// Preparar dados do voluntário
		membroDiscente.setPlanoTrabalho(plano);
		membroDiscente.setDataInicio(new Date());
		membroDiscente.setDataIndicacao(new Date());
		membroDiscente.setRegistroEntrada(mov.getUsuarioLogado().getRegistroEntrada());
		membroDiscente.setTipoBolsa(plano.getTipoBolsa());
		dao.create(membroDiscente);

		// Definir associação do plano com o voluntário
		plano.setMembroProjetoDiscente(membroDiscente);
		dao.update(plano);

		return plano;
	}

	/**
	 * Método responsável pela validação do processamento do cadastro de um voluntário
	 */
	public void validate(Movimento mov) throws NegocioException, ArqException {
		ListaMensagens erros = new ListaMensagens();

		PlanoTrabalho plano = (PlanoTrabalho) mov;
		MembroProjetoDiscente membroDiscente = plano.getMembroProjetoDiscente();

		PlanoTrabalhoDao planoDao = getDAO(PlanoTrabalhoDao.class, mov);

		// Verificar se já existe um plano de trabalho para o bolsista selecionado
		if (planoDao.findByDiscenteAtivo(membroDiscente.getDiscente().getId()) != null) {
			erros.addErro("O voluntário informado já está associado a um outro plano de trabalho em andamento");
		}

		// Verificar tipos válidos de bolsa
		if (plano.getTipoBolsa().getId() != TipoBolsaPesquisa.VOLUNTARIO && plano.getTipoBolsa().getId() != TipoBolsaPesquisa.BALCAO) {
			erros.addErro("Planos com bolsa do tipo " + plano.getTipoBolsaString()  + " não podem ser cadastrados por esta operação.");
		}

		// Somente bolsistas de produtividade do CNPq podem cadastrar bolsistas BALCÃO
		BolsaObtidaDao bolsaObtidaDao = getDAO(BolsaObtidaDao.class, mov);
		if ( plano.getTipoBolsa().getId() == TipoBolsaPesquisa.BALCAO
				&& !bolsaObtidaDao.isBolsistaProdutividade( ((Usuario) mov.getUsuarioLogado()).getServidor() )) {
			erros.addErro("Somente bolsistas de produtividade do CNPq podem cadastrar bolsistas BALCÃO. Em caso de dúvidas, contate a " +
					"Pró-Reitoria de Pesquisa. ");
		}

		checkValidation(erros.getMensagens());
	}

}
