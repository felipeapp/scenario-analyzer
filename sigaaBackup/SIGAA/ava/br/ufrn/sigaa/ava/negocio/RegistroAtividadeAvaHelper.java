/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 02/09/2011
 *
 */

package br.ufrn.sigaa.ava.negocio;

import java.util.Date;

import br.ufrn.arq.dominio.Comando;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.sigaa.ava.dominio.RegistroAtividadeTurma;
import br.ufrn.sigaa.ava.jsf.ControllerTurmaVirtual;
import br.ufrn.sigaa.ava.jsf.TurmaVirtualMBean;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.twitter.jsf.TwitterMBean;

/**
 * Helper que gerencia os registros de ações sobre entidades da Turma Virtual.
 * 
 * @author Fred_Castro
 *
 */

public class RegistroAtividadeAvaHelper extends ControllerTurmaVirtual {
	
	/** Instância do helper. */
	private static RegistroAtividadeAvaHelper instance;
	
	/**
	 * Retorna a instancia singleton do helper.
	 * 
	 * @return
	 */
	public static RegistroAtividadeAvaHelper getInstance(){
		if (instance == null)
			instance = new RegistroAtividadeAvaHelper();
		
		return instance;
	}
	
	/**
	 * Método que deve ser chamado nos MBeans da Turma Virtual para se registrarem as ações sobre as entidades da Turma Virtual.<br/><br/>
 	 * 
	 * Método não invocado por JSPs.
	 * 
	 * @param entidade o número referente a entidade utilizada @see br.ufrn.sigaa.ava.dominio.EntidadeRegistroAva
	 * @param acao o número referente a ação realizada sobre a entidade @see br.ufrn.sigaa.ava.dominio.AcaoAva
	 * @param id o id das entidades utilizadas
	 * @throws ArqException
	 */
	public void registrarAtividade (Turma t, String atividade) {
		
		Comando comandoAtivo = null;
		
		try {
			
			RegistroAtividadeTurma object = new RegistroAtividadeTurma ();
			object.setData(new Date());
			object.setDescricao(atividade);
			object.setTurma(t);
			object.setUsuario(getUsuarioLogado());
			
			MovimentoCadastro mov = new MovimentoCadastro(object);
			
			comandoAtivo = getUltimoComando();
			
			mov.setCodMovimento(ArqListaComando.CADASTRAR);
			prepareMovimento(ArqListaComando.CADASTRAR);
			
			// Cadastra a atividade
			executeWithoutClosingSession(mov);
			
			// Cadastra a mensagem no twitter.
			TwitterMBean twitterMBean = getMBean("twitterMBean");
			twitterMBean.novoTwitter(t.getId());
			
			String tag = "#" + t.getDisciplina().getCodigo() + "_T" + t.getCodigo() + "_" + t.getAnoPeriodo() + " - ";
			tag = tag.replace(".","_");
			String mensagem = tag + atividade;
			
			twitterMBean.setNovoStatus(mensagem);
			twitterMBean.enviarNovoStatus();
			
			TurmaVirtualMBean tBean = getMBean("turmaVirtual");
			tBean.setUltimasAtividades(null);
			tBean.getUltimasAtividades();
			
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
		} catch (ArqException e) {
			notifyError(e);
		} finally {
			try {
				if (comandoAtivo != null)
					prepareMovimento(comandoAtivo);
			} catch (ArqException e){
				notifyError (e);
			}
		}
	}
}