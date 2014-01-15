/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 18/05/2010
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.negocio;

import java.rmi.RemoteException;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.integracao.interfaces.PatrimonioRemoteService;
import br.ufrn.sigaa.arq.dao.biblioteca.ExemplarDao;
import br.ufrn.sigaa.arq.dao.biblioteca.MaterialInformacionalDao;
import br.ufrn.sigaa.arq.dao.biblioteca.TituloCatalograficoDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Exemplar;
import br.ufrn.sigaa.biblioteca.util.BibliotecaUtil;

/**
 * Processador que desfaz a operação de dar baixa em um exemplar.
 *
 * @author Bráulio
 */
public class ProcessadorDesfazerBaixaExemplar extends AbstractProcessador{

	/**
	 *  
	 * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {
		
		validate(mov);
		
		MaterialInformacionalDao dao = null;
		
		try {
			
			dao = getDAO(MaterialInformacionalDao.class, mov);
			
			MovimentoDesfazerBaixaExemplar movimento = (MovimentoDesfazerBaixaExemplar) mov;
			
			Exemplar exemplar = movimento.getExemplar();
			
			// Seta a situação para a nova e limpa o campo de motivo da baixa
			exemplar.setSituacao(movimento.getNovaSituacao());
			exemplar.setMotivoBaixa(null);
			exemplar.setInformacoesTituloMaterialBaixado(null);
			
			// Desfaz a baixa no acervo da biblioteca
			ProcessadorAtualizaExemplar processadorAtualizaExemplar = new ProcessadorAtualizaExemplar();
			
			MovimentoAtualizaExemplar movAuxiliar = new MovimentoAtualizaExemplar(exemplar, "Retirada a baixa do exemplar ");
			movAuxiliar.setCodMovimento(SigaaListaComando.ATUALIZA_EXEMPLAR);
			movAuxiliar.setUsuarioLogado(movimento.getUsuarioLogado());
			movAuxiliar.setSistema(movimento.getSistema());
			movAuxiliar.setPermiteAtualizacaoDeBaixados(true);
			
			processadorAtualizaExemplar.execute(movAuxiliar);
			
			// Atualiza o cache de quantidade de materiais do título
			BibliotecaUtil.incrementaQuantidadesMateriaisTitulo(dao, exemplar.getTituloCatalografico().getId());
			
			// Desfaz a baixa do patrimônio no SIPAC
			// Observação: ainda não está fazendo isso, por enquanto.
			try {
				if ( exemplar.getNumeroPatrimonio() != null) {
					PatrimonioRemoteService remoto = getBean("patrimonioRemoteServiceInvoker", movimento);
					remoto.desfazerBaixaLivro( exemplar.getNumeroPatrimonio() );
				}
			} catch ( Exception ex ) {
				throw new NegocioException("Não foi possível desfazer a baixa do patrimônio: "+exemplar.getNumeroPatrimonio() +" no "+RepositorioDadosInstitucionais.get("siglaSipac") );
			}
			
		}finally {
			if(dao != null ) dao.close();
		}
		
		return null;
	}

	
	
	/**
	 *  Ver comentários da classe pai.<br/>
	 *
	 * @see br.ufrn.arq.negocio.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	public void validate(Movimento mov) throws NegocioException, ArqException {
		
		ListaMensagens lista = new ListaMensagens();
		
		MovimentoDesfazerBaixaExemplar movimento = (MovimentoDesfazerBaixaExemplar) mov;
		
		Exemplar exemplar = movimento.getExemplar();
		
		MaterialInformacionalDao dao = null;
		ExemplarDao exemplarDao = null;
		TituloCatalograficoDao tituloDao = null;
		
		try {
			
			dao = getDAO(MaterialInformacionalDao.class, movimento);
			exemplarDao = getDAO(ExemplarDao.class, mov);
			tituloDao = getDAO(TituloCatalograficoDao.class, mov);
			
			if ( movimento.getNovaSituacao() == null || movimento.getNovaSituacao().getId() <= 0 )
				lista.addErro("É preciso informar a nova situação do exemplar.");
			
			if ( movimento.getNovaSituacao().isSituacaoDeBaixa() || movimento.getNovaSituacao().isSituacaoEmprestado() )
				lista.addErro("Não é possível desfazer a baixa de um material mantendo a sua situação de baixa e nem colocando-o como emprestado.");
			
			boolean tituloRemovido = ! tituloDao.findSeTituloEstaAtivo( exemplar.getTituloCatalografico().getId() );
			
			if(tituloRemovido ){
				lista.addErro("A baixa do exemplar não pôde ser desfeita, pois a catalogação a que ele pertencia foi removido do sistema.");
			}
			
			
			if ( exemplar.getExemplarDeQuemSouAnexo() != null ) {
				
				Exemplar exemplarDoBanco = dao.findByPrimaryKey(exemplar.getId(), Exemplar.class);
				
				Exemplar deQuemSouAnexo = exemplarDoBanco.getExemplarDeQuemSouAnexo();
			
				if ( ! deQuemSouAnexo.isAtivo() || deQuemSouAnexo.getSituacao().isSituacaoDeBaixa() )
					lista.addErro("A baixa do exemplar não pôde ser desfeita, pois o material do " +
							"qual ele é anexo não está ativo. Desfaça primeiro a baixa do material principal.");
			}
			
		} finally {
			if (dao != null) dao.close();
			if (exemplarDao != null) exemplarDao.close();
			if (tituloDao != null) tituloDao.close();
			
			checkValidation(lista);
		}
		
	}

}
