/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 10/06/09
 */

package br.ufrn.sigaa.biblioteca.circulacao.negocio;

import java.rmi.RemoteException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.biblioteca.SuspensaoUsuarioBibliotecaDao;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.Emprestimo;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.SuspensaoUsuarioBiblioteca;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.UsuarioBiblioteca;
import br.ufrn.sigaa.biblioteca.util.BibliotecaUtil;
import br.ufrn.sigaa.biblioteca.util.CirculacaoUtil;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.mensagens.MensagensBiblioteca;

/**
 * Processador que estorna suspensões de usuários da biblioteca, adiantando suspensões futuras a estas.
 * 
 * @author Fred_Castro
 *
 */
public class ProcessadorEstornarSuspensoes extends AbstractProcessador {

	/**
	 * De acordo com a suspensão a estornar, adianta as datas das suspensões futuras para o usuário desta
	 * suspensão, diminuindo efetivamente o tempo que o usuário ficará suspenso.
	 */
	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {

		MovimentoEstornarSuspensoes personalMov = (MovimentoEstornarSuspensoes) mov;
		
		SuspensaoUsuarioBibliotecaDao dao = null;
		
		validate(personalMov);
		
		try {
			dao = getDAO (SuspensaoUsuarioBibliotecaDao.class, personalMov);
			
			// A data de fim da suspensão que está sendo estornada passará
			// a ser o dia de ontem
			SuspensaoUsuarioBiblioteca auxS = new SuspensaoUsuarioBiblioteca();
			Calendar cal = Calendar.getInstance();
			cal.setTime(new Date());
			cal.add(Calendar.DAY_OF_MONTH, -1);
			auxS.setDataFim(cal.getTime());
			Emprestimo auxE = new Emprestimo ();
			
			SuspensaoUsuarioBiblioteca primeira = personalMov.getSuspensoesAEstornar().get(0);
			
			// Pega o usuário biblioteca, seja pelo empréstimo associado ou diretamente, se for uma
			// suspensão manual
			UsuarioBiblioteca usuarioBiblioteca = null;
			if (! primeira.isManual())
				usuarioBiblioteca = primeira.getEmprestimo().getUsuarioBiblioteca();
			else
				usuarioBiblioteca = primeira.getUsuarioBiblioteca();
			
			auxE.setUsuarioBiblioteca(usuarioBiblioteca);
			auxS.setEmprestimo(auxE);
			
			int diasAVoltar = 0;
			
			UsuarioBiblioteca usuarioBibliotecaCompleto = dao.findByPrimaryKey(usuarioBiblioteca.getId(), UsuarioBiblioteca.class, "pessoa.id", "biblioteca.id");
			
			// Retorna as supensões futuras ordenadas pela data de fim //
			List <SuspensaoUsuarioBiblioteca> sFuturas = dao.findSuspensoesFuturasDoUsuario(
					usuarioBibliotecaCompleto.getIdentificadorPessoa()
					, usuarioBibliotecaCompleto.getIdentificadorBiblioteca()
					, auxS.getDataFim());
			
			for (SuspensaoUsuarioBiblioteca suspensaoFutura : sFuturas){
				
				if (personalMov.getSuspensoesAEstornar().contains(suspensaoFutura)){
					
					// Se a suspensão vai ser estornada, estorna a suspensão e incrementa a quantidade de dias que vão ser retirados do período de suspensão do usuário
					
					suspensaoFutura.setAtivo(false);
					suspensaoFutura.setMotivoEstorno(personalMov.getMotivo());
					suspensaoFutura.setDataEstorno(new Date());
					suspensaoFutura.setUsuarioEstorno((Usuario) personalMov.getUsuarioLogado());
					
					diasAVoltar += CirculacaoUtil.calculaDiasEmAtrasoBiblioteca(suspensaoFutura.getDataInicio(), suspensaoFutura.getDataFim());
					
				} else {

					// Se a suspensão não vai ser estornada, apenas volta ela a quantidade de dias das supensões já estornadas
					
					cal.setTime(suspensaoFutura.getDataInicio());
					cal.add(Calendar.DAY_OF_MONTH, -1*diasAVoltar);
					suspensaoFutura.setDataInicio(cal.getTime());
					
					cal.setTime(suspensaoFutura.getDataFim());
					cal.add(Calendar.DAY_OF_MONTH, -1*diasAVoltar);
					suspensaoFutura.setDataFim(cal.getTime());
				}
				
				dao.update(suspensaoFutura);
			}
			
		} finally {
			if (dao != null) dao.close();
		}
		
		return null;
	}

	public void validate(Movimento mov) throws NegocioException, ArqException {
		
		MovimentoEstornarSuspensoes personalMov = (MovimentoEstornarSuspensoes) mov;
		
		if (personalMov.getSuspensoesAEstornar() == null || personalMov.getSuspensoesAEstornar().isEmpty()){
			NegocioException ne = new NegocioException ();
			ne.getListaMensagens().addMensagem(MensagensBiblioteca.USUARIO_SEM_SUSPENSOES_ATIVAS);
			throw ne;
		}
		
		boolean selecionada = false;
		for (SuspensaoUsuarioBiblioteca s : personalMov.getSuspensoesAEstornar())
			if (s.isSelecionado()){
				selecionada = true;
				break;
			}
		
		if (!selecionada){
			NegocioException ne = new NegocioException();
			ne.getListaMensagens().addMensagem(MensagensBiblioteca.SELECIONE_UMA_SUSPENSAO);
			throw ne;
		}
		
		// Um bibliotecário só pode estornar uma suspensão da sua própria biblioteca:
		// Obs.: suspensões manuais não são ligadas a uma biblioteca
		for ( SuspensaoUsuarioBiblioteca s : personalMov.getSuspensoesAEstornar() )
			if ( s.isSelecionado() ) {
				SuspensaoUsuarioBibliotecaDao dao = null;
				try {
					dao = getDAO(SuspensaoUsuarioBibliotecaDao.class, personalMov);
					
					// Retorna o id da unidade do material do emprestimo da suspensão para verificar a permissão do usuário
					Integer idUnidadeSuspensao = dao.findUnidadeDaSuspensao(s.getId() );
					
					if ( idUnidadeSuspensao == null ) // é multa manual
						continue;
					
					
					if(! personalMov.getUsuarioLogado().isUserInRole(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL)){
					
						
						List<Integer> unidades = BibliotecaUtil.encontraUnidadesPermissaoDoUsuario(personalMov.getUsuarioLogado(),
								SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO);
					
						if ( ! unidades.contains(idUnidadeSuspensao) ) {
							NegocioException ne = new NegocioException();
							ne.getListaMensagens().addErro("O senhor(a) não tem permissão para estornar suspensões de empréstimos " +
									"feitos em outras bibliotecas.");
							throw ne;
						}
					}
					
				} finally {
					if ( dao != null ) dao.close();
				}
			}
		
	}
}
