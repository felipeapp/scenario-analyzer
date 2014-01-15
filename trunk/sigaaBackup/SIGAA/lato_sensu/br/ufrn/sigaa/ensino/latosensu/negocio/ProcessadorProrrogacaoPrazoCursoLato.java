/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '26/09/2008'
 *
 */
package br.ufrn.sigaa.ensino.latosensu.negocio;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Date;

import br.ufrn.arq.caixa_postal.MensagemDAO;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.Formatador;
import br.ufrn.comum.dao.PermissaoDAO;
import br.ufrn.comum.dominio.Papel;
import br.ufrn.comum.dominio.UsuarioGeral;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.caixa_postal.dominio.Mensagem;
import br.ufrn.sigaa.caixa_postal.dominio.MensagensHelper;
import br.ufrn.sigaa.ensino.dominio.CoordenacaoCurso;
import br.ufrn.sigaa.ensino.latosensu.dominio.CursoLato;

/**
 * Processador responsável por efetuar a prorrogação do prazo final dos cursos
 * de especialização.
 * 
 * @author leonardo
 *
 */
public class ProcessadorProrrogacaoPrazoCursoLato extends AbstractProcessador {


	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {
		
		validate(mov);
		
		GenericDAO dao = getGenericDAO(mov);
		MovimentoCadastro movc = (MovimentoCadastro) mov;
		CursoLato curso = (CursoLato) movc.getObjMovimentado();
		
		if(mov.getCodMovimento().equals(SigaaListaComando.PRORROGAR_PRAZO_CURSO_LATO)){

			dao.updateField(CursoLato.class, curso.getId(), "dataFim", curso.getDataFim());			
			
			//Atualizando todas as coordenações do curso com a nova data final.
			Collection<CoordenacaoCurso> coordenacoes = dao.findByExactField(CoordenacaoCurso.class, "curso.id", curso.getId());			 
			for(CoordenacaoCurso cc : coordenacoes) {
				cc.setDataFimMandato(curso.getDataFim());				
				dao.update(cc);
			}		
			
		}else if(mov.getCodMovimento().equals(SigaaListaComando.SOLICITAR_PRORROGACAO_PRAZO_CURSO_LATO)){
			
			dao.updateField(CursoLato.class, curso.getId(), "justificativa", curso.getJustificativa());
			
			/*
			 * Enviar mensagens para gestores lato 
			 * */
			PermissaoDAO pdao = getDAO(PermissaoDAO.class, mov);
			Collection<UsuarioGeral> usuarios = pdao.findByPapel(new Papel(SigaaPapeis.GESTOR_LATO));
			if (usuarios != null) {
				for (UsuarioGeral user : usuarios) {

					Mensagem msg = new Mensagem();
					msg.setTitulo("Solicitação de Prorrogação de Prazo de Curso");
					msg.setMensagem("Solicitamos a prorrogação de prazo do curso " + curso.getDescricao() + " - " + CalendarUtils.getAno(curso.getDataInicio())
							+ " com novo prazo final para " + Formatador.getInstance().formatarData(curso.getDataFim()) + ".\n\n"
							+ " Justificativa:\n\n " + curso.getJustificativa());
					msg.setTipo(br.ufrn.arq.caixa_postal.Mensagem.MENSAGEM);
					msg.setDataCadastro(new Date());
					msg.setConfLeitura(false);
					msg.setUsuario(user);
					msg.setRemetente(mov.getUsuarioLogado());
					msg.setPapel(new Papel(SigaaPapeis.GESTOR_LATO));
					msg.setAutomatica(true);

					MensagemDAO msgDAO = new MensagemDAO();
					try{
						msgDAO.create(MensagensHelper.msgSigaaToMsgArq(msg));
					}finally{
						msgDAO.close();
					}
				}
			}
		}
		dao.close();
		return null;
	}

	
	public void validate(Movimento mov) throws NegocioException, ArqException {
		MovimentoCadastro movc = (MovimentoCadastro) mov;
		CursoLato curso = (CursoLato) movc.getObjMovimentado();

		CursoLato cursoOld = getGenericDAO(mov).findByPrimaryKey(curso.getId(), CursoLato.class);

		if(CalendarUtils.calculoDias(cursoOld.getDataFim(), curso.getDataFim()) < 0 || curso.getDataFim().equals(cursoOld.getDataFim()))
			throw new NegocioException("O Novo Prazo Final deve ser posterior ao atual.");
		
		// Não deve permitir que o período total do curso ultrapasse 18 meses (Cf. Resolução)
		Date prazoMaximo = CalendarUtils.somaMeses(curso.getDataInicio(), 18);
		if(CalendarUtils.calculoDias(prazoMaximo, cursoOld.getDataFim()) > 0)
			throw new NegocioException("O prazo máximo de duração de um curso de especialização é de 18 meses.");

	}

}
