/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 20/10/2010
 */
package br.ufrn.sigaa.estagio.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Date;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.email.Mail;
import br.ufrn.arq.email.MailBody;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.estagio.EstagiarioDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.estagio.dominio.Estagiario;
import br.ufrn.sigaa.estagio.dominio.StatusEstagio;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * Processador responsável pela a persistência dos dados relacionados 
 * ao cadastro de Estágios.
 * 
 * @author Arlindo Rodrigues
 *
 */
public class ProcessadorEstagio extends AbstractProcessador {

	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {
		
		Estagiario estagiario = ((MovimentoCadastro)mov).getObjMovimentado();
		GenericDAO dao = getGenericDAO(mov);
		try {
			if (mov.getCodMovimento().equals(SigaaListaComando.CADASTRAR_ESTAGIO_AVULSO) || 
					mov.getCodMovimento().equals(SigaaListaComando.ANALISAR_ESTAGIO) || 
						mov.getCodMovimento().equals(SigaaListaComando.ALTERAR_ESTAGIO)){
				
				if (mov.getCodMovimento().equals(SigaaListaComando.CADASTRAR_ESTAGIO_AVULSO)){
					estagiario.setInteresseOferta(null);
					estagiario.setStatus(new StatusEstagio( StatusEstagio.APROVADO ));
				}
				
				// Valida os dados do estágio somente se o parecer for Aprovado
				if (estagiario.isAprovado())
					validate(mov);
				// caso o estágio tenha sido invalidado antes de aprovar, não possui orientador, tipo, etc.
				if (isEmpty(estagiario.getOrientador()))
					estagiario.setOrientador(null);
				if (isEmpty(estagiario.getTipoEstagio()))
					estagiario.setTipoEstagio(null);
				
				dao.createOrUpdate(estagiario);
				
				if (mov.getCodMovimento().equals(SigaaListaComando.CADASTRAR_ESTAGIO_AVULSO) || 
						mov.getCodMovimento().equals(SigaaListaComando.ANALISAR_ESTAGIO)){
					if (estagiario.isAprovado()){
						estagiario.setDataAprovacao(new Date());
						
						/* Notifica o discente */
						enviarMensagem(estagiario.getDiscente().getPessoa(), getMensagem(estagiario));	
						/* Notifica o Orientador */
						enviarMensagem(estagiario.getOrientador().getPessoa(), getMensagem(estagiario));	
						/* Notifica o Responsável pela Empresa */
						enviarMensagem(estagiario.getConcedente().getResponsavel().getPessoa(), getMensagem(estagiario));							
					}
				}
				
			} else if (mov.getCodMovimento().equals(SigaaListaComando.SOLICITAR_CANCELAMENTO_ESTAGIO)){
				estagiario.setStatus(new StatusEstagio( StatusEstagio.SOLICITADO_CANCELAMENTO ));
				estagiario.setRegistroSolicitacaoCancelamento(mov.getUsuarioLogado().getRegistroEntrada());
				dao.update(estagiario);
			} else if (mov.getCodMovimento().equals(SigaaListaComando.CANCELAR_ESTAGIO)){
				estagiario.setDataCancelamento(new Date());
				estagiario.setRegistroCancelamento(mov.getUsuarioLogado().getRegistroEntrada());
				if (!estagiario.isSolicitadoCancelamento())
					estagiario.setRegistroSolicitacaoCancelamento(mov.getUsuarioLogado().getRegistroEntrada());
				estagiario.setStatus(new StatusEstagio( StatusEstagio.CANCELADO ));
				dao.update(estagiario);
			}
		} finally {
			if (dao != null)
				dao.close();
		}
		return null;
	}
		
	/**
	 * Corpo da Mensagem enviada
	 * @param e
	 * @return
	 */
	private String getMensagem(Estagiario e){
		StringBuffer mensagem = new StringBuffer();
		
		mensagem.append("O Aluno "+e.getDiscente().getNome()+", matrícula "+e.getDiscente().getMatricula()+", do Curso de "+
				e.getDiscente().getCurso().getDescricao()+", foi cadastrado como Estagiário, na modalidade de "+
				e.getTipoEstagio().getDescricao()+", através do(a) "+e.getConcedente().getPessoa().getNome()+
				", CNPJ "+e.getConcedente().getPessoa().getCpfCnpjFormatado()+", Convênio nº "+e.getConcedente().getConvenioEstagio().getNumeroConvenio()+
				", com carga horária de "+e.getCargaHorariaSemanal()+" horas semanais e bolsa de R$ "+
				Formatador.getInstance().formatarMoeda(e.getValorBolsa())+" mensais, com vigência de "+
				Formatador.getInstance().formatarData(e.getDataInicio())+" a "+Formatador.getInstance().formatarData(e.getDataFim())+".");
		
		mensagem.append("<br/><br/>Mensagem Gerada pelo SIGAA - Sistema Gestão de Estágio");
		mensagem.append("<br/>Por favor, não responder este email.");		
		
		return mensagem.toString();
	}
	
	/**
	 * Método responsável pela construção e envio do email.
	 * @param p
	 * @param assunto
	 * @param conteudo
	 * @param complemento
	 */
	private void enviarMensagem(Pessoa p, String mensagem) {

		if (!ValidatorUtil.isEmpty(p.getEmail())){
			
			MailBody mail = new MailBody();
			mail.setContentType(MailBody.HTML);
			mail.setAssunto("SIGAA - Cadastro de Estagiário - MENSAGEM AUTOMÁTICA");
			mail.setMensagem(mensagem);
			mail.setEmail(p.getEmail());
			mail.setNome(p.getNome());
			Mail.send(mail);
			
		}
	}			

	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		Estagiario estagiario = ((MovimentoCadastro)mov).getObjMovimentado();
		checkValidation( estagiario.validate() );		
		
		EstagiarioDao dao = getDAO(EstagiarioDao.class, mov);
		try {
			Collection<Estagiario> estagios = dao.findEstagioAtivosByDiscente(estagiario.getDiscente().getMatricula().toString(), new Date(), new Date());
			
			for (Estagiario e : estagios){
				if (e.isVigente() && estagiario.getHoraInicio() != null && e.getHoraInicio() != null
						&& estagiario.getHoraInicio().getTime() >= e.getHoraInicio().getTime() && 
						estagiario.getHoraFim().getTime() <= e.getHoraFim().getTime()){
					throw new NegocioException("O Discente já possui um estágio Ativo no mesmo Horário.");
				}					
			}
		} finally {
			if (dao != null)
				dao.close();
		}
	}

}
