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
 * <p>Processador que cont�m as regras de neg�cio para registrar uma comunica��o de material perdido. </p>
 * <p>� registrado um prorroga��o por perda de material e calculado uma suspes�o para o usu�rio caso a 
 *   comunica��o seja feita depois que o empr�stimo venceu ou depois que o prazo da �ltima prorroga��o por perda de material venceu.
 * </p>
 * <p>Podem ser cadastradas v�rias comunica��es por perda de material para um mesmo empr�stimo.</p>
 * 
 * @author Fred_Castro
 *
 */

public class ProcessadorComunicarMaterialPerdido extends AbstractProcessador {

	
	/**
	 * Ver coment�rio na classe pai.
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
			 * Suspende o usu�rio pelos dias que ele ficou sem comunicar a perda do material.
			 * 
			 * Observa��o: Como quando o usu�rio comunica a perda e � gerado uma prorroga��o a data 
			 * do empr�stimo � atualizada, se o empr�stimo est� atrasado tamb�m est� levando em conta
			 * a situa��o que o usu�rio comunica a perda, o prazo para reposi��o � finalizado e novamente 
			 * atrasado o usu�rio cominica que precisa de um novo prazo para reposi��o.
			 * 
			 * Por esses dias que o usu�rio novamente comunicou em atraso ele vai levar nova suspens�o.
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
				
			// Cria a nova prorroga��o.
			ProrrogacaoEmprestimo novaProrrogacao = new ProrrogacaoEmprestimo();
			novaProrrogacao.setDataAnterior(e.getPrazo());
			novaProrrogacao.setDataAtual(CalendarUtils.configuraTempoDaData(personalMov.getNovoPrazo(), 23, 59, 59, 999));
			novaProrrogacao.setMotivo(personalMov.getMotivo());
			novaProrrogacao.setTipo(TipoProrrogacaoEmprestimo.PERDA_DE_MATERIAL);
			novaProrrogacao.setEmprestimo(e);
			
			e.setPrazo(CalendarUtils.configuraTempoDaData(personalMov.getNovoPrazo(), 23, 59, 59, 999));
			
			List <ProrrogacaoEmprestimo> ps = new ArrayList <ProrrogacaoEmprestimo> ();
			ps.add(novaProrrogacao);
			
			// Livra feriados, finais de semana e interrup��es da biblioteca.
			ps.addAll(CirculacaoUtil.geraProrrogacoesEmprestimo(e, e.getMaterial().getBiblioteca(), null));
			
			// Atualiza o prazo do empr�stimo no banco.
			dao.updateField(Emprestimo.class, e.getId(), "prazo", e.getPrazo());
			
			dao.detach(e); // para n�o atualizar o empr�stimos por completo quando salvar a prorroga��o, s� o prazo deve ser atualizado.
			
			// Salva as novas prorroga��o do empr�stimo.
			for (ProrrogacaoEmprestimo p : ps)
				dao.create(p);
			
			Object[] retorno = new Object[2];
			
			retorno[0] = punicoesSofridas;
			retorno[1] = e; // o empr�timo com prazo prorrocado
			
			return retorno; // retorna a supens�o que o usu�rio levou.
			
		} finally {
			if (dao != null)  dao.close();
		}
		
	}
	
	

	/**
	 *  Ver coment�rio na classe pai.
	 *  
	 * @see br.ufrn.arq.negocio.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	public void validate(Movimento mov) throws NegocioException, ArqException {
		
		
		
		MovimentoComunicarMaterialPerdido personalMov = (MovimentoComunicarMaterialPerdido) mov;
		
		EmprestimoDao dao = null;
		
		Emprestimo emprestimoPerdido = personalMov.getEmprestimo();
		
		if (emprestimoPerdido == null)
			throw new NegocioException ("Erro ao enviar o empr�stimo. Contate o suporte.");
		
		if (personalMov.getMotivo() == null)
			throw new NegocioException ("Um motivo da comunica��o da perda deve ser informado.");
		
		
		try{
			
			dao = getDAO(EmprestimoDao.class, personalMov);
		
			if(! mov.getUsuarioLogado().isUserInRole( SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL)){
				
				try{
					Unidade unidade  = dao.findUnidadeMaterialDoEmprestimo(personalMov.getEmprestimo().getId());
					
					checkRole(unidade, personalMov, 
							SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO,
							SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF);
				
				}catch (SegurancaException se) {
					
					Emprestimo emprestimoBanco = dao.findByPrimaryKey(emprestimoPerdido.getId(), Emprestimo.class); // se n�o d� LazyExcepton
					
					throw new NegocioException ("O usu�rio(a): "+ mov.getUsuarioLogado().getNome()
								+ " n�o tem permiss�o para comunicar perda de material para a biblioteca. "
								+emprestimoBanco.getMaterial().getBiblioteca().getDescricao());
				}
			
			}
		
		}finally{
			if(dao != null) dao.close();
		}
		
		if (personalMov.getNovoPrazo() == null
				|| CalendarUtils.estorouPrazo(personalMov.getNovoPrazo(), personalMov.getEmprestimo().getPrazo())
				|| CalendarUtils.estorouPrazo(personalMov.getNovoPrazo(), new Date())){
			throw new NegocioException ("Um prazo deve ser informado. Este deve ser posterior � data atual e ao prazo do empr�stimo.");
		}
		
		
	}
}
