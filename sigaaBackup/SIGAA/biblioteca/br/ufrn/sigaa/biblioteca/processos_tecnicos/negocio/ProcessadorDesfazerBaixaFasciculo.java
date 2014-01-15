/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
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
import br.ufrn.sigaa.arq.dao.biblioteca.FasciculoDao;
import br.ufrn.sigaa.arq.dao.biblioteca.MaterialInformacionalDao;
import br.ufrn.sigaa.arq.dao.biblioteca.TituloCatalograficoDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Fasciculo;
import br.ufrn.sigaa.biblioteca.util.BibliotecaUtil;

/**
 * Processador que desfaz a opera��o de dar baixa em um fasc�culo.
 *
 * @author Br�ulio
 */
public class ProcessadorDesfazerBaixaFasciculo extends AbstractProcessador {

	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {
		
		validate(mov);
		
		MaterialInformacionalDao dao = null;
		
		try {
			
			dao = getDAO(MaterialInformacionalDao.class, mov);
		
			MovimentoDesfazerBaixaFasciculo movimento = (MovimentoDesfazerBaixaFasciculo) mov;
			
			Fasciculo fasciculo = movimento.getFasciculo();
			
			// Seta a situa��o para para a nova e limpa o campo de motivo da baixa
			fasciculo.setSituacao(movimento.getNovaSituacao());
			fasciculo.setMotivoBaixa(null);
			fasciculo.setInformacoesTituloMaterialBaixado(null);
			
			// Desfaz a baixa no acervo da biblioteca
			ProcessadorAtualizaFasciculo processadorAtualizaFasciculo = new ProcessadorAtualizaFasciculo();
			
			MovimentoAtualizaFasciculo movAuxiliar = new MovimentoAtualizaFasciculo(fasciculo, "Retirada a baixa do fasc�culo " );
			movAuxiliar.setCodMovimento(SigaaListaComando.ATUALIZA_FASCICULO);
			movAuxiliar.setUsuarioLogado(movimento.getUsuarioLogado());
			movAuxiliar.setSistema(movimento.getSistema());
			movAuxiliar.setPermiteAtualizacaoDeBaixados(true);
			
			processadorAtualizaFasciculo.execute(movAuxiliar);
			
			// Atualiza o cache de quantidade de materiais do t�tulo
			BibliotecaUtil.incrementaQuantidadesMateriaisTitulo(dao, fasciculo.getAssinatura().getTituloCatalografico().getId());

		} finally {
			if (dao != null ) dao.close();
		}
		
		return null;
	}

	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		
		ListaMensagens lista = new ListaMensagens();
		
		MovimentoDesfazerBaixaFasciculo movimento = (MovimentoDesfazerBaixaFasciculo) mov;
		
		Fasciculo fasciculo = movimento.getFasciculo();
		
		MaterialInformacionalDao dao = null;
		FasciculoDao fasciculoDao = null;
		TituloCatalograficoDao tituloDao = null;
		
		try {
		
			dao = getDAO(MaterialInformacionalDao.class, movimento);
			fasciculoDao = getDAO(FasciculoDao.class, mov);
			tituloDao = getDAO(TituloCatalograficoDao.class, mov);
			
			if ( movimento.getNovaSituacao() == null || movimento.getNovaSituacao().getId() <= 0 )
				lista.addErro("� preciso informar a nova situa��o do exemplar.");
			
			if ( movimento.getNovaSituacao().isSituacaoDeBaixa() ||
					movimento.getNovaSituacao().isSituacaoEmprestado() )
				lista.addErro("A nova situa��o n�o pode ser nem de baixa e nem de empr�stimo.");
			
			boolean tituloRemovido = ! tituloDao.findSeTituloEstaAtivo( fasciculo.getAssinatura().getTituloCatalografico().getId() );
			
			if(tituloRemovido ){
				lista.addErro("A baixa do fasc�culo n�o p�de ser desfeita, pois a cataloga��o a que ele pertencia foi removido do sistema.");
			}
			
			if ( fasciculo.getFasciculoDeQuemSouSuplemento() != null ) {
				
				Fasciculo fasciculoDoDB = fasciculoDao.findByPrimaryKey(fasciculo.getId(), Fasciculo.class);
				
				Fasciculo deQuemSouSuplm = fasciculoDoDB.getFasciculoDeQuemSouSuplemento();
				
				if ( ! deQuemSouSuplm.isAtivo() || deQuemSouSuplm.getSituacao().isSituacaoDeBaixa() )
					lista.addErro("A baixa do fasc�culo n�o p�de ser desfeita, pois o material do " +
							"qual ele � suplemento n�o est� ativo. Desfa�a primeiro a baixa do material principal.");
			}
			
		} finally {
			if (dao != null) dao.close();
			if (fasciculoDao != null) fasciculoDao.close();
			if (tituloDao != null) tituloDao.close();
			
			checkValidation(lista);
		}
		
	}

}
