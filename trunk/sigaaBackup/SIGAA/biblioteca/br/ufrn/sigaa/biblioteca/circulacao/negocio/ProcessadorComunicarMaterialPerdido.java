package br.ufrn.sigaa.biblioteca.circulacao.negocio;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.sigaa.arq.dao.biblioteca.EmprestimoDao;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.Emprestimo;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.ProrrogacaoEmprestimo;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.PunicaoAtrasoEmprestimoBiblioteca;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.TipoProrrogacaoEmprestimo;
import br.ufrn.sigaa.biblioteca.util.CirculacaoUtil;
import br.ufrn.sigaa.dominio.Unidade;

/**
 * <p>Processador que contém as regras de negócio para registrar uma comunicação de material perdido. </p>
 * <p>É registrado um prorrogação por perda de material e calculado uma suspesão para o usuário caso a 
 *   comunicação seja feita depois que o empréstimo venceu ou depois que o prazo da última prorrogação por perda de material venceu.
 * </p>
 * <p>Podem ser cadastradas várias comunicações por perda de material para um mesmo empréstimo.</p>
 * 
 * @author Fred_Castro
 *
 */

public class ProcessadorComunicarMaterialPerdido extends AbstractProcessador {

	
	/**
	 * Ver comentário na classe pai.
	 * 
	 * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {

		MovimentoComunicarMaterialPerdido personalMov = (MovimentoComunicarMaterialPerdido) mov;

		GenericDAO dao = null;
		
		try {
			
			validate(personalMov);
			
			dao = getGenericDAO(personalMov);
			
			Emprestimo e = personalMov.getEmprestimo();
			
			e = dao.refresh(e);
			
			/* *************************************************************************************
			 * Suspende o usuário pelos dias que ele ficou sem comunicar a perda do material.
			 * 
			 * Observação: Como quando o usuário comunica a perda e é gerado uma prorrogação a data 
			 * do empréstimo é atualizada, se o empréstimo está atrasado também está levando em conta
			 * a situação que o usuário comunica a perda, o prazo para reposição é finalizado e novamente 
			 * atrasado o usuário cominica que precisa de um novo prazo para reposição.
			 * 
			 * Por esses dias que o usuário novamente comunicou em atraso ele vai levar nova suspensão.
			 * 
			 * *************************************************************************************/
			
			List<PunicaoAtrasoEmprestimoBiblioteca> punicoesSofridas = new ArrayList<PunicaoAtrasoEmprestimoBiblioteca>();
			
			if( e.isAtrasado()){   
				
				List<PunicaoAtrasoEmprestimoStrategy> punicoesStrategy = new PunicaoAtrasoEmprestimoStrategyFactory().getEstrategiasPunicao();
				
				for (PunicaoAtrasoEmprestimoStrategy punicaoStrategy : punicoesStrategy) {
					PunicaoAtrasoEmprestimoBiblioteca punicao = punicaoStrategy.criarPunicaoAutomatica(e, e.getPrazo(), new Date());
					dao.create(punicao);
					punicoesSofridas.add(punicao);
				}
			}
				
			// Cria a nova prorrogação.
			ProrrogacaoEmprestimo novaProrrogacao = new ProrrogacaoEmprestimo();
			novaProrrogacao.setDataAnterior(e.getPrazo());
			novaProrrogacao.setDataAtual(CalendarUtils.configuraTempoDaData(personalMov.getNovoPrazo(), 23, 59, 59, 999));
			novaProrrogacao.setMotivo(personalMov.getMotivo());
			novaProrrogacao.setTipo(TipoProrrogacaoEmprestimo.PERDA_DE_MATERIAL);
			novaProrrogacao.setEmprestimo(e);
			
			e.setPrazo(CalendarUtils.configuraTempoDaData(personalMov.getNovoPrazo(), 23, 59, 59, 999));
			
			List <ProrrogacaoEmprestimo> ps = new ArrayList <ProrrogacaoEmprestimo> ();
			ps.add(novaProrrogacao);
			
			// Livra feriados, finais de semana e interrupções da biblioteca.
			ps.addAll(CirculacaoUtil.geraProrrogacoesEmprestimo(e, e.getMaterial().getBiblioteca(), null));
			
			// Atualiza o prazo do empréstimo no banco.
			dao.updateField(Emprestimo.class, e.getId(), "prazo", e.getPrazo());
			
			dao.detach(e); // para não atualizar o empréstimos por completo quando salvar a prorrogação, só o prazo deve ser atualizado.
			
			// Salva as novas prorrogação do empréstimo.
			for (ProrrogacaoEmprestimo p : ps)
				dao.create(p);
			
			Object[] retorno = new Object[2];
			
			retorno[0] = punicoesSofridas;
			retorno[1] = e; // o emprétimo com prazo prorrocado
			
			return retorno; // retorna a supensão que o usuário levou.
			
		} finally {
			if (dao != null)  dao.close();
		}
		
	}
	
	

	/**
	 *  Ver comentário na classe pai.
	 *  
	 * @see br.ufrn.arq.negocio.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	public void validate(Movimento mov) throws NegocioException, ArqException {
		
		
		
		MovimentoComunicarMaterialPerdido personalMov = (MovimentoComunicarMaterialPerdido) mov;
		
		EmprestimoDao dao = null;
		
		Emprestimo emprestimoPerdido = personalMov.getEmprestimo();
		
		if (emprestimoPerdido == null)
			throw new NegocioException ("Erro ao enviar o empréstimo. Contate o suporte.");
		
		if (personalMov.getMotivo() == null)
			throw new NegocioException ("Um motivo da comunicação da perda deve ser informado.");
		
		
		try{
			
			dao = getDAO(EmprestimoDao.class, personalMov);
		
			if(! mov.getUsuarioLogado().isUserInRole( SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL)){
				
				try{
					Unidade unidade  = dao.findUnidadeMaterialDoEmprestimo(personalMov.getEmprestimo().getId());
					
					checkRole(unidade, personalMov, 
							SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO,
							SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF);
				
				}catch (SegurancaException se) {
					
					Emprestimo emprestimoBanco = dao.findByPrimaryKey(emprestimoPerdido.getId(), Emprestimo.class); // se não dá LazyExcepton
					
					throw new NegocioException ("O usuário(a): "+ mov.getUsuarioLogado().getNome()
								+ " não tem permissão para comunicar perda de material para a biblioteca. "
								+emprestimoBanco.getMaterial().getBiblioteca().getDescricao());
				}
			
			}
		
		}finally{
			if(dao != null) dao.close();
		}
		
		if (personalMov.getNovoPrazo() == null
				|| CalendarUtils.estorouPrazo(personalMov.getNovoPrazo(), personalMov.getEmprestimo().getPrazo())
				|| CalendarUtils.estorouPrazo(personalMov.getNovoPrazo(), new Date())){
			throw new NegocioException ("Um prazo deve ser informado. Este deve ser posterior à data atual e ao prazo do empréstimo.");
		}
		
		
	}
}
