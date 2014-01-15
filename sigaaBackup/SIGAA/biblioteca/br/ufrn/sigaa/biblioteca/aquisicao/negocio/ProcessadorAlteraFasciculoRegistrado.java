/*
 * ProcessadorAlteraFasciculoRegistrado.java
 *
 * UNIVERSIDADE FEDERAL DO RIO GRANDE DO NORTE
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * Campos Universit�rio Lagoa Nova
 * Natal - RN - Brasil
 *
 */
package br.ufrn.sigaa.biblioteca.aquisicao.negocio;

import java.rmi.RemoteException;
import java.util.List;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.biblioteca.FasciculoDao;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Assinatura;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Fasciculo;
import br.ufrn.sigaa.biblioteca.util.BibliotecaUtil;

/**
 * 
 * Processador que implementa as regras de altera��o ou remo��o de fasc�culos
 * apenas registrados, que n�o est�o no acervo ainda.
 * 
 * @author jadson
 * @since 20/11/2009
 * @version 1.0 criacao da classe
 * 
 */
public class ProcessadorAlteraFasciculoRegistrado extends AbstractProcessador {

	/**
	 * 
	 * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {

		MovimentoAlteraFasciculoRegistrado m = (MovimentoAlteraFasciculoRegistrado) mov;

		validate(mov);

		GenericDAO dao = null;

		try {

			dao = getGenericDAO(m);

			if (m.isRemovendo()) {
				
				dao.remove(m.getFasciculo());
				
				/*
				 * Verifica se o fasc�culo � o �ltimo daquela assinatura, se for volta 1 no contador que gerar os c�digos de barras
				 * 
				 * Isso para evitar que a sequ�ncia dos c�digos de barras fique "quebrada"
				 */
				if( ! m.getFasciculo().isSuplemento() ){
					
					try{
						
						int codigoGeradorFasciculoSelecionado = Integer.parseInt(m.getFasciculo().getCodigoBarras().split("-")[1]);
						Assinatura assinaturaSelecionada = dao.findByPrimaryKey(m.getAssinaturaDoFasciculo().getId(), Assinatura.class,"numeroGeradorFasciculo");
						if (assinaturaSelecionada.getNumeroGeradorFasciculo() == codigoGeradorFasciculoSelecionado) {
							dao.updateField(Assinatura.class, m.getAssinaturaDoFasciculo().getId(),"numeroGeradorFasciculo", (assinaturaSelecionada.getNumeroGeradorFasciculo() - 1));
						}
					}catch(NumberFormatException nfe){
						// se der erro na obten��o do n�mero do fasc�culo, simplemente n�o precisa voltar!
					}
					
				}else{ // se � suplemento, o n�mero que tem que voltar est� localizado no fasc�culo, n�o na assinatura.
					
					try{
						
						int posicaoLetra = m.getFasciculo().getCodigoBarras().length()-1;
						
						int codigoGeradorSuplementoSelecionado = BibliotecaUtil.geraValorCorespondente(  m.getFasciculo().getCodigoBarras().charAt(posicaoLetra) );
						
						// O valor atual no fasc�culo pai j� vai t� no pr�ximo valor //
						int codigoGeradorSuplementoAtual = codigoGeradorSuplementoSelecionado+1;
						
						Fasciculo fasciculoPai = dao.findByPrimaryKey(m.getFasciculo().getFasciculoDeQuemSouSuplemento().getId(), Fasciculo.class, "id", "numeroGeradorCodigoBarrasAnexos");
						if (fasciculoPai.getNumeroGeradorCodigoBarrasAnexos() == codigoGeradorSuplementoAtual) {
							dao.updateField(Fasciculo.class, fasciculoPai.getId(), "numeroGeradorCodigoBarrasAnexos", (codigoGeradorSuplementoAtual - 1));
						}
						
					}catch(NumberFormatException nfe){
						// se der erro na obten��o do n�mero do fasc�culo, simplemente n�o precisa voltar!
					}
				}
				
				
			} else
				dao.update(m.getFasciculo());

		} finally {
			if (dao != null)
				dao.close();
		}

		return null;
	}

	/**
	 * 
	 * @see br.ufrn.arq.negocio.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {

		ListaMensagens lista = new ListaMensagens();

		MovimentoAlteraFasciculoRegistrado m = (MovimentoAlteraFasciculoRegistrado) mov;

		// MaterialInformacionalDao dao = getDAO(MaterialInformacionalDao.class,
		// m);
		FasciculoDao daoFasciculo = getDAO(FasciculoDao.class, m);

		try {

			Fasciculo f = m.getFasciculo();
			Assinatura a = m.getAssinaturaDoFasciculo();

			a = daoFasciculo.refresh(a);

			if (a.isAssinaturaDeCompra())
				if (!m.getUsuarioLogado().isUserInRole(
						SigaaPapeis.BIBLIOTECA_SETOR_AQUISICAO_BIBLIOTECARIO,
						SigaaPapeis.BIBLIOTECA_SETOR_AQUISICAO,
						SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL)) {
					lista.addErro(" Usu�rio n�o tem permiss�o para alterar os dados de um fasc�culo de compra ");
				}

			try {
				// Sen�o � administrador geral, checa a unidade de registro da
				// chegado do fasc�culo.
				if (!mov.getUsuarioLogado().isUserInRole(
						SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL)) {
					checkRole(
							a.getUnidadeDestino().getUnidade(),
							m,
							SigaaPapeis.BIBLIOTECA_SETOR_AQUISICAO_BIBLIOTECARIO,
							SigaaPapeis.BIBLIOTECA_SETOR_AQUISICAO,
							SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO);
				}
			} catch (SegurancaException se) {
				lista.addErro("Usu�rio: "
						+ m.getUsuarioLogado().getNome()
						+ " n�o tem permiss�o para alterar os dados fasc�culos para a biblioteca: "
						+ a.getUnidadeDestino().getDescricao());
			}

			if (!m.isRemovendo()) {

				Long qtdFasciculo = 0L;

				if (!f.isSuplemento()) {

					qtdFasciculo = daoFasciculo
							.countFasciculoIguaisAssinaturaParaAlteracao(
									a.getId(), f.getAnoCronologico(),
									f.getAno(), f.getVolume(), f.getNumero(),
									f.getEdicao(), f.getDiaMes(), f.getId());

				} else {

					Fasciculo fTemp = daoFasciculo.refresh(f);

					qtdFasciculo = daoFasciculo
							.countFasciculoIguaisAssinaturaParaAlteracaoSuplemento(
									a.getId(), f.getAnoCronologico(), f
											.getAno(), f.getVolume(), f
											.getNumero(), f.getEdicao(), f
											.getDiaMes(), f.getId(), fTemp
											.getFasciculoDeQuemSouSuplemento()
											.getId());
				}

				if (qtdFasciculo > 0) {
					lista.addErro(" J� existe um fasc�culo com os mesmos ano cronol�gico, ano, volume, n�mero e edi��o para essa assinatura.");
				}

			} else {
				List<Fasciculo> temp = daoFasciculo
						.findSuplementosDoFasciculo(f.getId());

				if (temp.size() > 0)
					lista.addErro(" O fasc�culo n�o pode ser removido pois ele possui suplementos, remova primeiro os suplementos dele ");
			}

		} finally {
			daoFasciculo.close();

			checkValidation(lista);
		}

	}

}
