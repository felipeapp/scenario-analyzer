/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 06/10/2006
 *
 */

package br.ufrn.sigaa.pesquisa.negocio;

import java.rmi.RemoteException;
import java.util.Collection;

import br.ufrn.arq.arquivos.EnvioArquivoHelper;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.MensagemAviso;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.pesquisa.EditalPesquisaDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.pesquisa.dominio.Cotas;
import br.ufrn.sigaa.pesquisa.dominio.EditalPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.ProjetoPesquisa;
import br.ufrn.sigaa.projetos.dominio.Edital;

/**
 * Processador para manipular publicar/editar editais de pesquisa
 *
 * @author ricardo
 *
 */
public class ProcessadorEditalPesquisa extends AbstractProcessador {

	/**
	 * Método responsável pela execução do processador do edital de pesquisa
	 */
	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {

		// Verificar papéis
		checkRole(SigaaPapeis.GESTOR_PESQUISA, mov);

		// Validar
		validate(mov);
		GenericDAO dao = getGenericDAO(mov);

		MovimentoEditalPesquisa movEdital = (MovimentoEditalPesquisa) mov;
		EditalPesquisa editalPesquisa = movEdital.getObjMovimentado();
		editalPesquisa.getEdital().getRestricaoCoordenacao().setMaxCoordenacoesAtivas(0);

		try {
			if (movEdital.getCodMovimento() == SigaaListaComando.PUBLICAR_EDITAL_PESQUISA ||
					movEdital.getCodMovimento() == SigaaListaComando.ENVIAR_ARQUIVO_EDITAL_PESQUISA) {
				
				dao.createOrUpdate(editalPesquisa.getEdital());
				dao.createOrUpdate(editalPesquisa);
				
			} else if (movEdital.getCodMovimento() == SigaaListaComando.REMOVER_EDITAL_PESQUISA) {
				// Remover Arquivo
				if(editalPesquisa.getIdArquivo() != null && editalPesquisa.getIdArquivo() != 0)
					EnvioArquivoHelper.removeArquivo(editalPesquisa.getIdArquivo());

				// Apenas inativa o registro
				dao.updateField(Edital.class, editalPesquisa.getEdital().getId(), "ativo", Boolean.FALSE);
			}
		}
		finally {
			dao.close();
		}

		return null;
	}

	/**
	 * Responsável pela validação do processamento do edital de pesquisa
	 */
	public void validate(Movimento mov) throws NegocioException, ArqException {
		MovimentoEditalPesquisa movEdital = (MovimentoEditalPesquisa) mov;
		EditalPesquisa editalPesquisa = movEdital.getObjMovimentado();
		ListaMensagens listaErros = new ListaMensagens();
		GenericDAO dao = getGenericDAO(mov);

		if (movEdital.getCodMovimento() == SigaaListaComando.PUBLICAR_EDITAL_PESQUISA){
			EditalPesquisaValidator.validaEdital(editalPesquisa, listaErros, getDAO(EditalPesquisaDao.class, mov));
			
			// Valida as cotas a serem cadastradas, se houver
			if(editalPesquisa.getCotas() != null && !editalPesquisa.getCotas().isEmpty()){
				for(Cotas c: editalPesquisa.getCotas()){
					if(c.getEditalPesquisa() == null)
						listaErros.addMensagem(new MensagemAviso("A cota deve possuir um edital associado.", TipoMensagemUFRN.ERROR));
					else if(c.getEditalPesquisa().getId() != editalPesquisa.getId())
						listaErros.addMensagem(new MensagemAviso("A cota está associada a um edital diferente do informado.", TipoMensagemUFRN.ERROR));
					
					if(c.getQuantidade() <= 0)
						listaErros.addMensagem(new MensagemAviso("A quantidade de bolsas deve ser maior que zero.", TipoMensagemUFRN.ERROR));
					
					if(c.getCotaDocente() != null)
						c.setCotaDocente(null);
				}
			}

		// Valida se o edital está associado a algum projeto de pesquisa
		} else if (movEdital.getCodMovimento() == SigaaListaComando.REMOVER_EDITAL_PESQUISA) {
			try {
				Collection<ProjetoPesquisa> projetos = dao.findByExactField(ProjetoPesquisa.class,
						"edital", editalPesquisa.getId());
				if (projetos != null && !projetos.isEmpty())
					listaErros.addMensagem(new MensagemAviso(
							"O Edital não pode ser removido pois existe(m) projeto(s) de pesquisa associado(s) a ele.",
							TipoMensagemUFRN.ERROR));
				if (editalPesquisa.isDistribuicaoCotas() && editalPesquisa.getCotas().size() < 1)
					listaErros.addMensagem(new MensagemAviso(
							"O Edital não pode ser removido pois existe(m) cotas distribuídas associada(s) a ele.",
							TipoMensagemUFRN.ERROR));
			}
			finally {
				dao.close();
			}
		} else if (movEdital.getCodMovimento() == SigaaListaComando.ENVIAR_ARQUIVO_EDITAL_PESQUISA){
			
		}
		
		checkValidation(listaErros);
	}

}
