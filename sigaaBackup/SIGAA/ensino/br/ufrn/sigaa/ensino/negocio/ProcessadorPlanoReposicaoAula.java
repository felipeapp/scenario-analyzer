/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on 26/05/2010
 *
 */
package br.ufrn.sigaa.ensino.negocio;

import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.ufrn.arq.caixa_postal.ASyncMsgDelegate;
import br.ufrn.arq.caixa_postal.Mensagem;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.comum.dao.ResponsavelUnidadeDAO;
import br.ufrn.comum.dominio.PessoaGeral;
import br.ufrn.comum.dominio.Responsavel;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.comum.dominio.UsuarioGeral;
import br.ufrn.rh.dominio.NivelResponsabilidade;
import br.ufrn.sigaa.arq.dao.UsuarioDao;
import br.ufrn.sigaa.arq.dao.ensino.PlanoReposicaoAulaDao;
import br.ufrn.sigaa.arq.dao.rh.ServidorDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.AvisoFaltaDocenteHomologada;
import br.ufrn.sigaa.ensino.dominio.PlanoReposicaoAula;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.pessoa.dominio.DocenteExterno;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * Processador para criação dos {@link PlanoReposicaoAula}, 
 * assim como enviar as notificações associadas.
 * 
 * @author Henrique André
 *
 */
public class ProcessadorPlanoReposicaoAula extends AbstractProcessador {

	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {
		
		validate(mov);
		
		MovimentoCadastro movPlano = (MovimentoCadastro) mov;		
		PlanoReposicaoAula plano = ((MovimentoCadastro) mov).getObjMovimentado();
		
		if(movPlano.getCodMovimento().equals(SigaaListaComando.CADASTRAR_PLANO_RESPOSICAO_AULA)){
			criarPlano(mov, plano);
		} 
		
		notificarChefes(movPlano);
		
		return plano;
	}

	/**
	 * Realiza a validação do objeto.
	 */
	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		
	}
	
	/**
	 * Efetua a criação do plano de reposição.
	 * 
	 * @param mov
	 * @param plano
	 * @throws DAOException
	 */
	private void criarPlano(Movimento mov, PlanoReposicaoAula plano) throws DAOException {
		PlanoReposicaoAulaDao dao = getDAO(PlanoReposicaoAulaDao.class, mov);
		
		try{
			dao.create(plano);
			plano.getFaltaHomologada().setPlanoAprovado(plano);
			dao.updateField(AvisoFaltaDocenteHomologada.class, plano.getFaltaHomologada().getId(), "planoAprovado", plano);
		} finally {
			dao.close();
		}
	}
	
	/**
	 * Notifica os chefes do departamento e do centro sobre o plano de reposição.
	 * @param movPlano
	 * @throws DAOException
	 */
	private void notificarChefes(MovimentoCadastro movPlano) throws DAOException{
		List<UsuarioGeral> destinatarios = new ArrayList<UsuarioGeral>();
		
		Usuario chefeDpto = findChefeDepartamento(movPlano);
		if (chefeDpto != null)
			destinatarios.add(chefeDpto);
		List<Usuario> chefeCentro = findDiretorCentro(movPlano);
		if(chefeCentro != null)
			destinatarios.addAll(chefeCentro);
		Mensagem msg = criarMensagem(movPlano);
		
		ASyncMsgDelegate.getInstance().enviaMensagemUsuarios(msg, chefeDpto, destinatarios);
	}
	
	/**
	 * Retorna o chefe de departamento da turma
	 * 
	 * @param mov
	 * @return
	 * @throws DAOException
	 */
	private Usuario findChefeDepartamento(MovimentoCadastro mov) throws DAOException {
		
		PlanoReposicaoAula plano = mov.getObjMovimentado();

		Turma turma = plano.getFaltaHomologada().getDadosAvisoFalta().getTurma();
		int idUnidade = turma.getDisciplina().getUnidade().getId();
		Usuario usuario = null;

		ServidorDao dao = getDAO(ServidorDao.class, mov);
		UsuarioDao uDao = null;
		
		try {
			Servidor chefe = dao.findChefeByDepartamento(idUnidade);
	
			if (chefe != null) {
				uDao = getDAO(UsuarioDao.class, mov);
				usuario = uDao.findByServidor(chefe);
			}
		} finally {
			dao.close();
			if(uDao != null)
				uDao.close();
		}

		return usuario;
	}
	
	/**
	 * Retorna o chefe do centro ao qual a turma pertence
	 * 
	 * @param mov
	 * @return
	 * @throws DAOException
	 */
	private List<Usuario> findDiretorCentro(MovimentoCadastro mov) throws DAOException {

		PlanoReposicaoAula plano = mov.getObjMovimentado();

		Turma turma = plano.getFaltaHomologada().getDadosAvisoFalta().getTurma();
		int idUnidade = turma.getDisciplina().getUnidade().getUnidadeResponsavel().getId();
		List<Usuario> usuarios = new ArrayList<Usuario>();

		ServidorDao dao = getDAO(ServidorDao.class, mov);
		ResponsavelUnidadeDAO responsavelUnidadeDAO = null;
		UsuarioDao uDao = null;
		
		try {
			Servidor diretor = dao.findDiretorByCentro(idUnidade);
	
			if (diretor != null) {
				uDao = getDAO(UsuarioDao.class, mov);
				Usuario usuario = uDao.findByServidor(diretor);
				usuarios.add(usuario);
			}
	
			responsavelUnidadeDAO = getDAO(ResponsavelUnidadeDAO.class, mov);
			Responsavel responsabilidades = responsavelUnidadeDAO.findResponsavelAtualByUnidade(idUnidade, NivelResponsabilidade.CHEFE);
			
			if (responsabilidades != null) {
				
				PessoaGeral pessoaGeral = responsabilidades.getServidor().getPessoa();
				
				uDao = getDAO(UsuarioDao.class, mov);
				List<Usuario> usuarioDaMesmaPessoa = uDao.findByPessoa(new Pessoa(pessoaGeral.getId()));
				usuarios.addAll(usuarioDaMesmaPessoa);
			}
		} finally {
			dao.close();
			if(responsavelUnidadeDAO != null)
				responsavelUnidadeDAO.close();
			if(uDao != null)
				uDao.close();
		}
		
		return usuarios;
	}
	
	/**
	 * Cria a mensagem que será enviada aos chefes.
	 * 
	 * @param mov
	 * @return
	 */
	private Mensagem criarMensagem(MovimentoCadastro mov) {
		PlanoReposicaoAula plano = mov.getObjMovimentado();

		Turma turma = plano.getFaltaHomologada().getDadosAvisoFalta().getTurma();
		Date dataAula = plano.getFaltaHomologada().getDadosAvisoFalta().getDataAula();
		Servidor docenteUFRN = plano.getFaltaHomologada().getDadosAvisoFalta().getDocente();
		DocenteExterno docenteExteno = plano.getFaltaHomologada().getDadosAvisoFalta().getDocenteExterno();
		
		String nomeDocente = ( docenteUFRN == null ? docenteExteno.getNome() : docenteUFRN.getNome() ); 

		SimpleDateFormat sdf = new SimpleDateFormat(
				"EEEE, dd 'de' MMMM 'de' yyyy");

		String titulo = "Apresentação do Plano de Reposição da turma " + turma.getDisciplina().getNome();
		String conteudo = "Caro chefe,\n" +
				"Comunicamos que  "
				+ nomeDocente
				+ ", docente da turma "
				+ turma.getDisciplina().getNome()
				+ ", tendo sua falta de "
				+ sdf.format(dataAula)
				+ " homologada anteriormente, apresentou o plano de reposição da aula perdida."
				+ "\n\nObs.: O Chefe de Departamento pode emitir um parecer sobre o plano de aula através da opção "
				+ "SIGAA -> Portal do Docente -> Chefia -> Docentes -> Gerenciar Avisos de Falta Homologados -> Analisar Plano de Aula.";

		Mensagem mensagem = new Mensagem();
		mensagem.setAutomatica(true);
		mensagem.setTitulo(titulo.toUpperCase());
		mensagem.setMensagem(StringUtils.stripHtmlTags(conteudo));
		mensagem.setSistema(Sistema.SIGAA);

		return mensagem;
	}
}
