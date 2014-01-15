/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 12/04/2011
 * 
 */
package br.ufrn.sigaa.biblioteca.circulacao.negocio;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.rh.dominio.Ativo;
import br.ufrn.rh.dominio.Categoria;
import br.ufrn.sigaa.arq.dao.biblioteca.ConsultasEmprestimoDao;
import br.ufrn.sigaa.arq.dao.biblioteca.UsuarioBibliotecaDao;
import br.ufrn.sigaa.arq.dao.pesquisa.MembroProjetoDiscenteDao;
import br.ufrn.sigaa.biblioteca.circulacao.dao.VinculoUsuariosBibliotecaDao;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.InfoVinculoUsuarioBiblioteca;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.UsuarioBiblioteca;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.UsuarioExternoBiblioteca;
import br.ufrn.sigaa.biblioteca.dominio.Biblioteca;
import br.ufrn.sigaa.biblioteca.dominio.InformacoesUsuarioBiblioteca;
import br.ufrn.sigaa.biblioteca.dominio.VinculoUsuarioBiblioteca;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.parametros.dominio.ParametrosGerais;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.DocenteExterno;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 *
 * <p>Implementa as regras padrão para obter o vínculo preferencial para os usuários da biblioteca. </p>
 *
 * 
 * @author jadson
 *
 */
public class ObtemVinculoUsuarioBibliotecaDefaultStrategy extends ObtemVinculoUsuarioBibliotecaStrategy {

	
	/**
	 * Ver comentários da classe pai.<br/>
	 * 
	 * @see br.ufrn.sigaa.biblioteca.circulacao.negocio.ObtemVinculoUsuarioBibliotecaStrategy#getVinculosAtivos(java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public  List<InfoVinculoUsuarioBiblioteca> getVinculos(int idPessoa) throws DAOException, NegocioException {
		
		List<InfoVinculoUsuarioBiblioteca> vinculosUsuario = new ArrayList<InfoVinculoUsuarioBiblioteca>();

		vinculosUsuario.addAll(montaInformacoesVinculoDiscenteBiblioteca(idPessoa));
		vinculosUsuario.addAll(montaInformacoesVinculoServidorBiblioteca(idPessoa));
		vinculosUsuario.addAll(montaInformacoesVinculoDocenteExternoBiblioteca(idPessoa));
		vinculosUsuario.addAll(montaInformacoesVinculoUsuarioExternoBiblioteca(idPessoa));
		
		return vinculosUsuario;
	}
	
	
	/**
	 * Ver comentários da classe pai.<br/>
	 * 
	 * @see br.ufrn.sigaa.biblioteca.circulacao.negocio.ObtemVinculoUsuarioBibliotecaStrategy#getVinculosAtivos(java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public  List<InfoVinculoUsuarioBiblioteca> getVinculosAtivos(int idPessoa) throws DAOException, NegocioException {
		
		List<InfoVinculoUsuarioBiblioteca> vinculosAtivosUsuario = new ArrayList<InfoVinculoUsuarioBiblioteca>();

		ConsultasEmprestimoDao dao = null;
		
		try {
			
			dao = DAOFactory.getInstance().getDAO(ConsultasEmprestimoDao.class);
			
			
			
			
			List<Servidor> servidores = dao.findServidoresBibliotecaByPessoa(idPessoa);

		
					
			//////  1º NA PRECEDÊNCIA:  DOCENTE INTERNO ////
			
			List<Servidor> docentes = encontraServidoresDocente(servidores);
			
				
			for (Servidor docente : docentes) {
				vinculosAtivosUsuario.add( new InfoVinculoUsuarioBiblioteca(VinculoUsuarioBiblioteca.DOCENTE, docente.getId()));
			}
				
			
				
					
			//  2º NA PRECEDÊNCIA: DOCENTE EXTERNO
			
			
			List <Integer> idsDes = dao.findIdDocenteExternoAtivosByPessoa( new Pessoa(idPessoa) );
			
			for (Integer ids : idsDes){ // Se tiver mais de um, pega o primeiro
				vinculosAtivosUsuario.add( new InfoVinculoUsuarioBiblioteca(VinculoUsuarioBiblioteca.DOCENTE_EXTERNO, ids));
			}
				
			
					
					
					
			
			
			List<Discente> discentes = dao.findDiscentesBibliotecaByPessoa(idPessoa);
				
					
			if (discentes.size() > 0){  // USUÁRIO POSSUI UM DISCENTE
				
				
				
				//  3º NA PRECEDÊNCIA: DISCENTE DE PÓS-GRADUAÇÃO
				
				List<Discente> discentesPos =  encontraDiscentesPos(discentes);
			
				for (Discente discentePos : discentesPos) {
					vinculosAtivosUsuario.add( new InfoVinculoUsuarioBiblioteca(VinculoUsuarioBiblioteca.ALUNO_POS_GRADUCACAO, discentePos.getId()));
				}
				
			
				//  4 º NA PRECEDÊNCIA: SERVIDOR TÉCNICO-ADMINISTRATIVO
				
				List<Servidor> servidoresTecnico = encontraServidoresTecnico(servidores);
				
				for (Servidor servidorTecnico : servidoresTecnico) {
					vinculosAtivosUsuario.add( new InfoVinculoUsuarioBiblioteca(VinculoUsuarioBiblioteca.SERVIDOR_TECNO_ADMINISTRATIVO, servidorTecnico.getId()));
				}
				
				//  5 º NA PRECEDÊNCIA: DISCENTES DE GRADUAÇÃO
				
				List<Discente> discentesGraduacao =  encontraDiscentesGraduacao(discentes);
				
				for (Discente discenteGraduacao : discentesGraduacao) {
					vinculosAtivosUsuario.add( new InfoVinculoUsuarioBiblioteca(VinculoUsuarioBiblioteca.ALUNO_GRADUACAO, discenteGraduacao.getId()));
				}
			
				//  6 º NA PRECEDÊNCIA: DISCENTES DE MÉDIO / TÉCNICO
				
				List<Discente> discentesMedioTecnico =  encontraDiscentesMedioTecnico(discentes);
				
				for (Discente discenteMedioTecnico : discentesMedioTecnico) {
					vinculosAtivosUsuario.add( new InfoVinculoUsuarioBiblioteca(VinculoUsuarioBiblioteca.ALUNO_TECNICO_MEDIO, discenteMedioTecnico.getId()));
				}
				
				
				//  7 º NA PRECEDÊNCIA: DISCENTES INFANTIL
				
				List<Discente> discentesInfantil =  encontraDiscentesInfantil(discentes);
				
				for (Discente discenteInfantil : discentesInfantil) {
					vinculosAtivosUsuario.add( new InfoVinculoUsuarioBiblioteca(VinculoUsuarioBiblioteca.ALUNO_INFANTIL, discenteInfantil.getId()));
				}
		
			}else{ // Se não possui discente
				
				//  4 º NA PRECEDÊNCIA: SERVIDOR TÉCNICO-ADMINISTRATIVO
				
				List<Servidor> servidoresTecnico = encontraServidoresTecnico(servidores);
				
				for (Servidor servidorTecnico : servidoresTecnico) {
					vinculosAtivosUsuario.add( new InfoVinculoUsuarioBiblioteca(VinculoUsuarioBiblioteca.SERVIDOR_TECNO_ADMINISTRATIVO, servidorTecnico.getId()));
				}
			}
	
	
				
				
			/* 	
		 	 *	 8º NA PRECEDÊNCIA: USUÁRIO EXTERNO
			 */
			
			List<UsuarioExternoBiblioteca> usuariosExterno = dao.findUsuariosExternosBibliotecaByPessoa(idPessoa);
			
			
			if(usuariosExterno.size() > 1) // Incosistencia no sistema;
				throw new NegocioException(" Usuário possui cadastro de usuário externo duplicado, entre em contado com o suporta para desativar um cadastro");
			
			if(usuariosExterno.size() == 1){ // possui cadastro como usuário externo
			
				UsuarioExternoBiblioteca usuarioExterno = usuariosExterno.get(0);
				vinculosAtivosUsuario.add( new InfoVinculoUsuarioBiblioteca(VinculoUsuarioBiblioteca.USUARIO_EXTERNO, usuarioExterno.getId()));
			}
		
			return vinculosAtivosUsuario;
				
		} finally {
			if (dao != null) dao.close();
		}
	}


	
	/**
	 * Retorna a descrião de docente externo caso o usuário tenha um vínculo de docente externo.
	 * 
	 * @param cpf
	 * @param matricula
	 * @param siape
	 * @param nome
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public String recuperaInformacoesCompletasVinculos(VinculoUsuarioBiblioteca vinculo,  Integer identificacaoVinculo) throws DAOException{
		
		String hql =" ";
		
		if(vinculo == null || identificacaoVinculo == null)
			return "";
		
		GenericDAO dao = null;
		
		try {
			
			dao = DAOFactory.getGeneric(Sistema.SIGAA);
		
			switch (vinculo) {
			
			case DOCENTE_EXTERNO:
				hql += " SELECT p.cpf_cnpj, docente.unidade.sigla, docente.unidade.nome ";
				hql += " FROM DocenteExterno docente  ";
				hql += " INNER JOIN docente.pessoa p  ";
				hql += " WHERE docente.id = "+identificacaoVinculo;
				break;
			case ALUNO_POS_GRADUCACAO:
			case ALUNO_GRADUACAO:
			case ALUNO_TECNICO_MEDIO:
			case ALUNO_INFANTIL:
				hql += " SELECT d.matricula, curso.nome as nomeCurso, gestora.nome as nomeCentro ";
				hql += " FROM Discente d ";
				hql +=" LEFT JOIN d.curso curso ";
				hql +=" LEFT JOIN curso.unidade unidade ";
				hql +=" LEFT JOIN unidade.gestora gestora  ";
				hql += " WHERE d.id = "+identificacaoVinculo;
				break;
			case SERVIDOR_TECNO_ADMINISTRATIVO:
			case DOCENTE:
				hql += " SELECT servidor.siape, servidor.unidade.nome, servidor.cargo.denominacao ";
				hql += " FROM Servidor servidor  ";
				hql += " WHERE servidor.id = "+identificacaoVinculo;
				break;
			case USUARIO_EXTERNO:
				hql += " SELECT p.cpf_cnpj, convenio.nome, unidade.nome ";
				hql += " FROM UsuarioExternoBiblioteca ube  ";
				hql += " INNER JOIN ube.usuarioBiblioteca ub  ";
				hql += " INNER JOIN ub.pessoa p  ";
				hql += " LEFT JOIN ube.convenio convenio ";
				hql += " LEFT JOIN  ube.unidade unidade ";
				hql += " WHERE ube.id = "+identificacaoVinculo;
				break;
			case BIBLIOTECA:
				hql += " SELECT b.identificador, b.descricao, unidade.nome ";
				hql += " FROM Biblioteca b  ";
				hql += " INNER JOIN  b.unidade unidade ";
				hql += " WHERE b.id = "+identificacaoVinculo;
				break;
			case BIBLIOTECA_EXTERNA:
				hql += " SELECT b.identificador, b.descricao, '' as externa ";
				hql += " FROM Biblioteca b  ";
				hql += " WHERE b.id = "+identificacaoVinculo;
				break;
			default:
				return  "";// não tem descrição completa
			}
		
			Query q = dao.getSession().createQuery(hql);
			
			Object[] lista = (Object[]) q.uniqueResult();
			
			if(lista == null || lista.length != 3)
				return "";
			
			return ( lista[0] != null ? lista[0].toString() : "" )  +" - "+  ( lista[1] != null ? lista[1].toString().toLowerCase()  : "" )
							+ ( lista[2] != null ? ", "+ lista[2].toString().toLowerCase()  : "" );
			
		} finally {
			if (dao != null) dao.close();
		}
	}
	
	
	
	/**
	 * Ver comentários da classe pai.<br/>
	 * 
	 * @throws DAOException 
	 * @throws NegocioException 
	 * @see br.ufrn.sigaa.biblioteca.circulacao.negocio.ObtemVinculoUsuarioBibliotecaStrategy#getVinculo()
	 */
	@Override
	public InformacoesUsuarioBiblioteca getInformacoesUsuario(UsuarioBiblioteca usuarioBiblioteca, Integer idPessoa, Integer idBiblioteca) throws DAOException, NegocioException {

		
		long time = System.currentTimeMillis();
		
		// Sempre o tipo do usuário é aquele que tem mais permissões na biblioteca           // 
		// Se o usuário for 'aluno de pós' e servidor normal, o tipo retornado é 'aluno pós' //
		// porque 'aluno pós' pode fazer mais empréstimos que servidor, se for professor e   //
		// qualquer coisa retorna professor e assim sucessivamente.                          //

		
		ConsultasEmprestimoDao dao = null;
		UsuarioBibliotecaDao daoUsuario = null;
		MembroProjetoDiscenteDao mpdDao = null;
		try {
			
			dao = DAOFactory.getInstance().getDAO(ConsultasEmprestimoDao.class);
			daoUsuario = DAOFactory.getInstance().getDAO(UsuarioBibliotecaDao.class);
			
			if( usuarioBiblioteca == null && idPessoa == null && idBiblioteca == null)
				throw new NegocioException("As informações do usuário não foram selecionadas corretamente, por favor reinicie o processo.");
			
			 /* ***********************************************************************************
			  * Utilizado quando o usuário numca fez cadastro na biblioteca, ou estão todos quitado
			  * ***********************************************************************************/
			if(usuarioBiblioteca == null){
				
				if(idPessoa != null){
					Pessoa pessoa = dao.findInfoPessoaSemVinculo(idPessoa);
				
					Usuario usuario = daoUsuario.findUsuarioMaisRecenteAtivoByPessoa(idPessoa);
					
					return new InformacoesUsuarioBiblioteca(
						pessoa.getNome(),
						pessoa.getCpf_cnpj() !=  null ? ""+pessoa.getCpfCnpjFormatado() : pessoa.getPassaporte(),
						pessoa.getCpf_cnpj() !=  null ? true : false,
						usuario.getIdFoto(),
						pessoa.getEmail(),
						pessoa.getTelefone(),
						pessoa.getEnderecoContato().getLogradouro(),
						pessoa.getEnderecoContato().getNumero(),
						pessoa.getEnderecoContato().getComplemento(),
						pessoa.getEnderecoContato().getCep(),
						pessoa.getEnderecoContato().getBairro());
				}else{
					Biblioteca biblioteca = dao.findInfoBibliotecaSemVinculo(idBiblioteca);
					
					return new InformacoesUsuarioBiblioteca(
							biblioteca.getDescricao(),
							biblioteca.getIdentificador(),
							biblioteca.getEmail(),
							biblioteca.getTelefone(),
							biblioteca.getEndereco().getLogradouro(),
							biblioteca.getEndereco().getNumero(),
							biblioteca.getEndereco().getComplemento(),
							biblioteca.getEndereco().getCep(),
							biblioteca.getEndereco().getBairro());
				}
			}
			
			// Informações do vínculo estarão mulas se o usuário já tinha cadastro anteriormente no sistema, mas nunca fez empréstimo //
			if( usuarioBiblioteca.getVinculo() == null || usuarioBiblioteca.getIdentificacaoVinculo() == null)
				throw new NegocioException("As informações do seu cadastro estão incompletas, por favor realize a atualização do seu cadastro no sistema de bibliotecas.");
			
			
			switch (usuarioBiblioteca.getVinculo()) {
			
			case ALUNO_POS_GRADUCACAO:
			case ALUNO_GRADUACAO:
			case ALUNO_TECNICO_MEDIO:
			case ALUNO_INFANTIL:
	
				mpdDao = DAOFactory.getInstance().getDAO(MembroProjetoDiscenteDao.class);
				
				Discente discente = dao.findInfoDiscenteBiblioteca(usuarioBiblioteca.getIdentificacaoVinculo());
				boolean iniciacaoCientifica = mpdDao.discentePossuiProjetoAtivo(discente.getId());
				
				Usuario usuarioDiscente = daoUsuario.findUsuarioMaisRecenteAtivoByPessoa(discente.getPessoa().getId());
				
				return new InformacoesUsuarioBiblioteca(
						usuarioBiblioteca.getId(),
						usuarioBiblioteca.getVinculo(),
						discente.getId(),
						pegaDescricaoCentro(discente),
						pegaDescricaoCurso(discente),
						discente.getTipo(),
						(usuarioDiscente != null ? usuarioDiscente.getEmail() : null),
						(usuarioDiscente != null ? usuarioDiscente.getIdFoto() : null),
						String.valueOf(discente.getMatricula()),
						discente.getPessoa().getNome(),
						(usuarioDiscente != null ? usuarioDiscente.getLogin() : null),
						discente.getPessoa().getTelefone(),
						usuarioBiblioteca.getSenha(),
						( discente.getFormaIngresso() != null ? discente.getFormaIngresso().isMobilidadeEstudantil() : false ),
						iniciacaoCientifica,
						discente.getPessoa().getEnderecoContato().getLogradouro(),
						discente.getPessoa().getEnderecoContato().getNumero(),
						discente.getPessoa().getEnderecoContato().getComplemento(),
						discente.getPessoa().getEnderecoContato().getCep(),
						discente.getPessoa().getEnderecoContato().getBairro());
				
			case SERVIDOR_TECNO_ADMINISTRATIVO:
			case DOCENTE:
				
				Servidor servidor = dao.findInfoServidorBiblioteca(usuarioBiblioteca.getIdentificacaoVinculo());
				
				Usuario usuarioServidor = daoUsuario.findUsuarioMaisRecenteAtivoByPessoa(servidor.getPessoa().getId());
				
				return new InformacoesUsuarioBiblioteca(
						usuarioBiblioteca.getId(),
						pegaCargoServidor(servidor),
						usuarioServidor != null ? usuarioServidor.getEmail() : null,
						usuarioServidor != null ? usuarioServidor.getIdFoto() : null,
						pegaLotacaoServidor(servidor),
						pegaSiapeServidor(servidor),
						servidor.getPessoa().getNome(),
						usuarioServidor != null ? usuarioServidor.getLogin() : "",
						servidor.getPessoa().getTelefone(), 
						usuarioBiblioteca.getVinculo(),
						usuarioBiblioteca.getIdentificacaoVinculo(),
						usuarioBiblioteca.getSenha(),
						servidor.getPessoa().getEnderecoContato().getLogradouro(),
						servidor.getPessoa().getEnderecoContato().getNumero(),
						servidor.getPessoa().getEnderecoContato().getComplemento(),
						servidor.getPessoa().getEnderecoContato().getCep(),
						servidor.getPessoa().getEnderecoContato().getBairro());
				
			case DOCENTE_EXTERNO:
				
				DocenteExterno docenteExterno = dao.findInfoDocenteExternoBiblioteca(usuarioBiblioteca.getIdentificacaoVinculo());
				
				Usuario usuarioDocenteExterno = daoUsuario.findUsuarioMaisRecenteAtivoByPessoa(docenteExterno.getPessoa().getId());
				
				return new InformacoesUsuarioBiblioteca(
						usuarioBiblioteca.getId(),
						usuarioBiblioteca.getVinculo(),
						usuarioBiblioteca.getIdentificacaoVinculo(),
						usuarioDocenteExterno != null ? usuarioDocenteExterno.getEmail() : null,
						docenteExterno.getPessoa().getTelefone(),
						usuarioDocenteExterno != null ? usuarioDocenteExterno.getIdFoto() : null,
						docenteExterno.getInstituicao().getNome(),
						docenteExterno.getPessoa().getNome(),
						( docenteExterno.getPessoa().getCpf_cnpj() != null && docenteExterno.getPessoa().getCpf_cnpj() > 0  ? docenteExterno.getPessoa().getCpfCnpjFormatado() : docenteExterno.getPessoa().getPassaporte()) ,
						( docenteExterno.getPessoa().getCpf_cnpj() != null && docenteExterno.getPessoa().getCpf_cnpj() > 0 ? true : false) ,
						usuarioDocenteExterno != null ? usuarioDocenteExterno.getLogin() : null,
						usuarioBiblioteca.getSenha(),
						docenteExterno.getPessoa().getEnderecoContato().getLogradouro(),
						docenteExterno.getPessoa().getEnderecoContato().getNumero(),
						docenteExterno.getPessoa().getEnderecoContato().getComplemento(),
						docenteExterno.getPessoa().getEnderecoContato().getCep(),
						docenteExterno.getPessoa().getEnderecoContato().getBairro());
				
			case USUARIO_EXTERNO:
				
				UsuarioExternoBiblioteca usuarioExterno = dao.findInfoUsuarioExternoBiblioteca(usuarioBiblioteca.getIdentificacaoVinculo());
				
				return new InformacoesUsuarioBiblioteca(
						usuarioBiblioteca.getId(),
						usuarioExterno.getId(),
						usuarioExterno.getUsuarioBiblioteca().getPessoa().getEmail(),
						usuarioExterno.getUsuarioBiblioteca().getPessoa().getNome(),
						( usuarioExterno.getUsuarioBiblioteca().getPessoa().getCpf_cnpj() != null && usuarioExterno.getUsuarioBiblioteca().getPessoa().getCpf_cnpj() > 0 ? usuarioExterno.getUsuarioBiblioteca().getPessoa().getCpfCnpjFormatado() : usuarioExterno.getUsuarioBiblioteca().getPessoa().getPassaporte()),
						( usuarioExterno.getUsuarioBiblioteca().getPessoa().getCpf_cnpj() != null && usuarioExterno.getUsuarioBiblioteca().getPessoa().getCpf_cnpj() > 0 ? true : false) ,
						usuarioExterno.getUsuarioBiblioteca().getPessoa().getTelefone(),
						usuarioExterno.getMotivoCancelamento(),
						usuarioBiblioteca.getSenha(),
						usuarioExterno.getUsuarioBiblioteca().getPessoa().getEnderecoContato().getLogradouro(),
						usuarioExterno.getUsuarioBiblioteca().getPessoa().getEnderecoContato().getNumero(),
						usuarioExterno.getUsuarioBiblioteca().getPessoa().getEnderecoContato().getComplemento(),
						usuarioExterno.getUsuarioBiblioteca().getPessoa().getEnderecoContato().getCep(),
						usuarioExterno.getUsuarioBiblioteca().getPessoa().getEnderecoContato().getBairro());
				
			case BIBLIOTECA:
			case BIBLIOTECA_EXTERNA:
				Biblioteca biblioteca = dao.findInfoBibliotecaBiblioteca(usuarioBiblioteca.getIdentificacaoVinculo());
				
				return new InformacoesUsuarioBiblioteca(
						usuarioBiblioteca.getId(),
						biblioteca.getIdentificador(),
						biblioteca.getDescricao(),
						biblioteca.getEmail(),
						biblioteca.getTelefone(),
						usuarioBiblioteca.getVinculo(),	
						biblioteca.getId(),
						biblioteca.getEndereco().getLogradouro(),
						biblioteca.getEndereco().getNumero(),
						biblioteca.getEndereco().getComplemento(),
						biblioteca.getEndereco().getCep(),
						biblioteca.getEndereco().getBairro());
			default:
				
				/* *******************************************************
				 *  Utilizado quando o usuário tem uma conta mais não foi possível obter o seu vínculo. ( Caso do usuário de migração )
				 * *******************************************************/
				
				if(usuarioBiblioteca.getPessoa() != null){
					Pessoa pessoa = dao.findInfoPessoaSemVinculo(usuarioBiblioteca.getPessoa().getId());
				
					Usuario usuario = daoUsuario.findUsuarioMaisRecenteAtivoByPessoa(pessoa.getId());
					
					return new InformacoesUsuarioBiblioteca(
						pessoa.getNome(),
						pessoa.getCpf_cnpj() !=  null && pessoa.getCpf_cnpj() > 0 ? ""+pessoa.getCpfCnpjFormatado() : pessoa.getPassaporte(),
						pessoa.getCpf_cnpj() !=  null && pessoa.getCpf_cnpj() > 0 ? true : false,
						usuario.getIdFoto(),
						pessoa.getEmail(),
						pessoa.getTelefone(),
						pessoa.getEnderecoContato().getLogradouro(),
						pessoa.getEnderecoContato().getNumero(),
						pessoa.getEnderecoContato().getComplemento(),
						pessoa.getEnderecoContato().getCep(),
						pessoa.getEnderecoContato().getBairro());
				}else{
					if(usuarioBiblioteca.getBiblioteca() != null){	
						
						Biblioteca biblioteca2 = dao.findInfoBibliotecaSemVinculo(usuarioBiblioteca.getBiblioteca().getId());
						
						return new InformacoesUsuarioBiblioteca(
								biblioteca2.getDescricao(),
								biblioteca2.getIdentificador(),
								biblioteca2.getEmail(),
								biblioteca2.getTelefone(),
								biblioteca2.getEndereco().getLogradouro(),
								biblioteca2.getEndereco().getNumero(),
								biblioteca2.getEndereco().getComplemento(),
								biblioteca2.getEndereco().getCep(),
								biblioteca2.getEndereco().getBairro());
					}
				}
			
			return null;
			}
		
		} finally {
			if (dao != null) dao.close();
			if (mpdDao != null) mpdDao.close();
			if (daoUsuario != null) daoUsuario.close();
			
			System.out.println("OBTER INFORMAÇÕES DO USUÁRIO : >>>>>>>>> "+ (  ( System.currentTimeMillis() - time) ) + "ms  <<<<<<<");
		}
		
	}
	
	/**
	 * Ver comentários da classe pai.<br/>
	 * 
	 * @see br.ufrn.sigaa.biblioteca.circulacao.negocio.ObtemVinculoUsuarioBibliotecaStrategy#isVinculoAtivo(int, br.ufrn.sigaa.biblioteca.dominio.VinculoUsuarioBiblioteca, java.lang.Integer)
	 */
	public boolean isVinculoAtivo(int idPessoa, VinculoUsuarioBiblioteca vinculo,  Integer idDentificacaoVinculo) throws DAOException, NegocioException{
		
		long time = System.currentTimeMillis();

		ConsultasEmprestimoDao dao = null;
		
		if(vinculo == null || idDentificacaoVinculo == null){
			throw new NegocioException(" Usuário não possui vínculo com a instituição para realizar empréstimos, realize um cadastro na biblioteca para obter o vínculo");
		}
		
		try {
			
			dao = DAOFactory.getInstance().getDAO(ConsultasEmprestimoDao.class);
			
			List<Servidor> servidores = new ArrayList<Servidor>();
			List<Discente> discentes = new ArrayList<Discente>();
			
			if(vinculo.isVinculoServidor()){
				servidores = dao.findServidoresBibliotecaByPessoa(idPessoa);
			}
			
			if(vinculo.isVinculoAluno()){
				discentes  = dao.findDiscentesBibliotecaByPessoa(idPessoa);
			}
		
			
			switch (vinculo) {
				case DOCENTE:	
					//////  1º NA PRECEDÊNCIA:  DOCENTE INTERNO ////
					
					List<Servidor> docentes = encontraServidoresDocente(servidores);
					
						
					for (Servidor docente : docentes) {
						if(idDentificacaoVinculo.equals( docente.getId()  ))
							return true;
					}
				return false;
				
				
				case DOCENTE_EXTERNO:
					///////  2º NA PRECEDÊNCIA: DOCENTE EXTERNO ///////
					
					List <Integer> idsDes = dao.findIdDocenteExternoAtivosByPessoa( new Pessoa(idPessoa) );
					
					for (Integer ids : idsDes){ // Se tiver mais de um, pega o primeiro
						if( idDentificacaoVinculo.equals(ids) )
							return true;
					}
						
				return false;
				
				case ALUNO_POS_GRADUCACAO:
					////  3º NA PRECEDÊNCIA: DISCENTE DE PÓS-GRADUAÇÃO  ////
					
					List<Discente> discentesPos =  encontraDiscentesPos(discentes);
				
					for (Discente discentePos : discentesPos) {
						if(idDentificacaoVinculo.equals(discentePos.getId())){
							return true;
						}
					}
					
					return false;
				
				case SERVIDOR_TECNO_ADMINISTRATIVO:
					
					//  4 º NA PRECEDÊNCIA: SERVIDOR TÉCNICO-ADMINISTRATIVO
					
					List<Servidor> servidoresTecnico = encontraServidoresTecnico(servidores);
					
					for (Servidor servidorTecnico : servidoresTecnico) {
						if(idDentificacaoVinculo.equals( servidorTecnico.getId()))
							return true;
					}
					
				return false;	
					
				case ALUNO_GRADUACAO:
					
					/////  5 º NA PRECEDÊNCIA: DISCENTES DE GRADUAÇÃO  /////
					List<Discente> discentesGraduacao =  encontraDiscentesGraduacao(discentes);
					
					for (Discente discenteGraduacao : discentesGraduacao) {
						if(idDentificacaoVinculo.equals(discenteGraduacao.getId()))
							return true;
					}
				return false;
				case ALUNO_TECNICO_MEDIO:
					//  6 º NA PRECEDÊNCIA: DISCENTES DE MÉDIO / TÉCNICO
					
					List<Discente> discentesMedioTecnico =  encontraDiscentesMedioTecnico(discentes);
					
					for (Discente discenteMedioTecnico : discentesMedioTecnico) {
						if( idDentificacaoVinculo.equals( discenteMedioTecnico.getId() ) )
							return true;
					}
				return false;
				case ALUNO_INFANTIL:
					//  7 º NA PRECEDÊNCIA: DISCENTES DE MÉDIO / TÉCNICO
					
					List<Discente> discentesInfantil =  encontraDiscentesInfantil(discentes);
					
					for (Discente discenteInfantil : discentesInfantil) {
						if( idDentificacaoVinculo.equals( discenteInfantil.getId() ) )
							return true;
					}
				return false;
				
				case USUARIO_EXTERNO:
				/* 	
			 	 *	 8º NA PRECEDÊNCIA: USUÁRIO EXTERNO
				 */
				
				List<UsuarioExternoBiblioteca> usuariosExterno = dao.findUsuariosExternosBibliotecaByPessoa(idPessoa);
				
				
				if(usuariosExterno.size() > 1) // Incosistencia no sistema;
					throw new NegocioException(" Usuário possui cadastro de usuário externo duplicado, entre em contado com o suporta para desativar um cadastro");
				
				if(usuariosExterno.size() == 1){ // possui cadastro como usuário externo
				
					UsuarioExternoBiblioteca usuarioExterno = usuariosExterno.get(0);
					if( idDentificacaoVinculo.equals( usuarioExterno.getId() )  )
						return true;
				}
				
				return false;
					
			} // fim switch
			
			
			return false;
				
		} finally {
			if (dao != null) dao.close();
			
			System.out.println("VERIFICAR VÍNCULO ATIVO DA PESSOA DOMOROU: >>>>>>>>> "+ (  ( System.currentTimeMillis() - time) ) + "ms  <<<<<<<");
		}
		
	}
	
	
	/**
	 *   Encontra entre os servidores do usuário biblioteca que podem fazer empréstimo, o que possui o vínculo de DOCENTE
	 *
	 * @param servidores
	 * @return
	 */
	private  List<Servidor> encontraServidoresDocente(List<Servidor> servidoresAtivos){
		
		List<Servidor> docentes = new ArrayList<Servidor>();
		
		for (Servidor servidor : servidoresAtivos) {
			if(servidor.getCategoria()!= null && Arrays.asList(getCategoriaDocenteBiblioteca()).contains(servidor.getCategoria().getId())){
				docentes.add(servidor);
			}
		}
		return docentes;
	}
	
	
	/**
	 *   Encontra entre os servidores do usuário biblioteca que podem fazer empréstimo, o que possui o vínculo de TÉCNICO-ADMINISTRATIVO
	 *
	 * @param servidores
	 * @return
	 */
	private  List<Servidor> encontraServidoresTecnico(List<Servidor> servidoresAtivos){
		
		List<Servidor> tecnicosAdministrativos = new ArrayList<Servidor>();

		for (Servidor servidor : servidoresAtivos) {
			if(servidor.getCategoria()!= null && Arrays.asList(getCategoriaServidorBiblioteca()).contains(servidor.getCategoria().getId()) ){  
				tecnicosAdministrativos.add(servidor);
			}
		}
		return tecnicosAdministrativos;
	}
	
	
	/**
	 *   Encontra entre os discentes do usuário biblioteca que podem fazer empréstimo, os que possuiem o vínculo de Pós-graduação
	 *
	 * @param servidores
	 * @return
	 */
	private  List<Discente> encontraDiscentesPos(List<Discente> discentesAtivos){
		
		List<Discente> discentesPos = new ArrayList<Discente>();
		
		for (Discente discente : discentesAtivos) {
			if( Arrays.asList(getNiveisAlunosPosGraduacaoBiblioteca()).contains(discente.getNivel()) ){
				discentesPos.add(discente);
			}
		}
		
		return discentesPos;
	}
	

	/**
	 *    Encontra entre os discentes do usuário biblioteca que podem fazer empréstimo, os que possuiem o vínculo de Gradução
	 *
	 * @param servidores
	 * @return
	 */
	private  List<Discente> encontraDiscentesGraduacao(List<Discente> discentesAtivos){
		
		List<Discente> discentesGraduacao = new ArrayList<Discente>();
		
		for (Discente discente : discentesAtivos) {
			if( Arrays.asList(getNiveisAlunosGraduacaoBiblioteca()).contains(discente.getNivel()) ){
				discentesGraduacao.add(discente);
			}
		}
		
		return discentesGraduacao;
	}
	
	/**
	 *    Encontra entre os discentes do usuário biblioteca que podem fazer empréstimo, os que possuiem o vínculo de Técnico/Médio
	 *
	 * @param servidores
	 * @return
	 */
	private  List<Discente> encontraDiscentesMedioTecnico(List<Discente> discentesAtivos){
		
		List<Discente> discentesMedio = new ArrayList<Discente>();
		
		for (Discente discente : discentesAtivos) {
			if(Arrays.asList(getNiveisAlunosMedioTecnicoBiblioteca()).contains(discente.getNivel()) ){
				discentesMedio.add(discente);
			}
		}
		
		return discentesMedio;
	}
	
	
	/**
	 *    Encontra entre os discentes do usuário biblioteca que podem fazer empréstimo, os que possuiem o vínculo de Técnico/Médio
	 *
	 * @param servidores
	 * @return
	 */
	private  List<Discente> encontraDiscentesInfantil(List<Discente> discentesAtivos){
		
		List<Discente> discentesInfantil = new ArrayList<Discente>();
		
		for (Discente discente : discentesAtivos) {
			if(Arrays.asList(getNiveisAlunosInfantilBiblioteca()).contains(discente.getNivel()) ){
				discentesInfantil.add(discente);
			}
		}
		
		return discentesInfantil;
	}
	
	
	/** 
	 * Método que testa se a lotação do servidor existe antes de pegá-la para não dar NullPointerExeception.
	 */
	private static String pegaLotacaoServidor(Servidor servidor){

		if (servidor != null && servidor.getUnidade() != null)
			return servidor.getUnidade().getNome();

		return "NÃO INFORMADA";
	}

	/** 
	 * Método que testa se o SIAPE do servidor existe antes de pegá-lo para não dar NullPointerExeception. 
	 */
	private static String pegaSiapeServidor(Servidor servidor){
		return servidor != null 
		? String.valueOf(servidor.getSiape()) 
				: " NÃO INFORMADO";
	} 

	/** 
	 *   Método que testa se a descrição do curso existe antes de pegá-la para não dar NullPointerExeception.
	 */
	private static String pegaDescricaoCurso(Discente d){
		return  d.getCurso() != null ? d.getCurso().getDescricao() : "NÃO INFORMADO";
	}


	/**
	 * Método que testa se a descrição do centro existe antes de pegá-la para não dar NullPointerExeception. 
	 */
	private static String pegaDescricaoCentro(Discente d){
		return d.getCurso() != null && d.getCurso().getUnidade() != null 
		&& d.getCurso().getUnidade().getUnidadeGestora() != null 
		? d.getCurso().getUnidade().getUnidadeGestora().getNome()
				: "NÃO INFORMADO";
	}

	/**
	 * Método que testa se o cargo de servidor existe antes de pegá-lo para não dar NullPointerExeception. 
	 */
	private static String pegaCargoServidor(Servidor servidor){
		return servidor != null && servidor.getCargo() != null 
		? servidor.getCargo().getDenominacao() 
				: "NÃO INFORMADO";
	}

	
	/**
	 * Monta as informações dos vinculos do usuário de acordo com as regras a ufrn. 
	 *
	 * @param infoVinculoTemp
	 * @return
	 * @throws DAOException 
	 */
	public List<InfoVinculoUsuarioBiblioteca> montaInformacoesVinculoDiscenteBiblioteca(int idPessoa) throws DAOException{
		
		List<InfoVinculoUsuarioBiblioteca> infoVinculosDiscenteUsuario = new ArrayList<InfoVinculoUsuarioBiblioteca>();
		
		VinculoUsuariosBibliotecaDao dao = null;
		
		try{
			dao = DAOFactory.getInstance().getDAO(VinculoUsuariosBibliotecaDao.class);
			List<Object[]> infoVinculosDiscenteUsuarioTemp = dao.findInformacoesVinculosDiscente(idPessoa);
		
			if(infoVinculosDiscenteUsuarioTemp != null)
			for (Object[] object : infoVinculosDiscenteUsuarioTemp) {
				
				boolean podeRealizarEmprestimos = false;
				
				int identificacaoVinculo = ((Integer) object[0]);
				Long matricula = object[1] != null ? ( (BigInteger) object[1] ).longValue() : 0l;
				char nivel = (Character)object[2];
				Integer status = ((Short)object[3]).intValue();
				int tipo = (Integer)object[4];
				Boolean formaIngressoPermiteEmprestimosBiblioteca = (Boolean) object[5];
				
				List<Integer> statusPermitidosBiblioteca = Arrays.asList(getStatusDiscenteUtilizarBiblioteca());
				
				List<Integer> lista = Arrays.asList( getTiposDiscenteBiblioteca() );
				
				// Regra válida da UFRN
				if( ( lista.contains(tipo) || (formaIngressoPermiteEmprestimosBiblioteca != null && formaIngressoPermiteEmprestimosBiblioteca) ) && statusPermitidosBiblioteca.contains(status)){
					podeRealizarEmprestimos = true;
				}
				
				Discente d = new Discente();
				d.setTipo(tipo);
				String descricaoTipo =   d.getTipoString();
				
				if(formaIngressoPermiteEmprestimosBiblioteca != null && formaIngressoPermiteEmprestimosBiblioteca)
					descricaoTipo += " ( Forma Ingresso Permite Empréstimos )";
			
				VinculoUsuarioBiblioteca vinculo = VinculoUsuarioBiblioteca.INATIVO;
				
				if (Arrays.asList(getNiveisAlunosPosGraduacaoBiblioteca()).contains(nivel)){
					vinculo = VinculoUsuarioBiblioteca.ALUNO_POS_GRADUCACAO;
				}else{
					if (Arrays.asList(getNiveisAlunosGraduacaoBiblioteca()).contains(nivel)){
						vinculo = VinculoUsuarioBiblioteca.ALUNO_GRADUACAO;
					}else{
						if (Arrays.asList(getNiveisAlunosMedioTecnicoBiblioteca()).contains(nivel)){
							vinculo = VinculoUsuarioBiblioteca.ALUNO_TECNICO_MEDIO;
						}else{
							if (Arrays.asList(getNiveisAlunosInfantilBiblioteca()).contains(nivel)){
								vinculo = VinculoUsuarioBiblioteca.ALUNO_INFANTIL;
							}else{
								
							}
						}
					}
				}
				
				InfoVinculoUsuarioBiblioteca infoVinculo = new InfoVinculoUsuarioBiblioteca(vinculo, identificacaoVinculo, String.valueOf(matricula), NivelEnsino.getDescricao( nivel)
						, descricaoTipo, null, StatusDiscente.getDescricao(status ), podeRealizarEmprestimos);
				
				infoVinculosDiscenteUsuario.add(infoVinculo);
			}
		
		}finally{
			if(dao != null) dao.close();
		}
		
		return infoVinculosDiscenteUsuario;
	}
	

	
	/**
	 * Monta as informações dos vinculos do usuário de acordo com as regras a ufrn. 
	 *
	 * @param infoVinculoTemp
	 * @return
	 * @throws DAOException 
	 */
	public List<InfoVinculoUsuarioBiblioteca> montaInformacoesVinculoServidorBiblioteca(int idPessoa) throws DAOException{
		
		List<InfoVinculoUsuarioBiblioteca> infoVinculosDiscenteUsuario = new ArrayList<InfoVinculoUsuarioBiblioteca>();
		
		VinculoUsuariosBibliotecaDao dao = null;
		
		try{
			dao = DAOFactory.getInstance().getDAO(VinculoUsuariosBibliotecaDao.class);
			List<Object[]> infoVinculosServdiorUsuarioTemp = dao.findInformacoesVinculosServidor(idPessoa);
		
			if(infoVinculosServdiorUsuarioTemp != null)
			for (Object[] object : infoVinculosServdiorUsuarioTemp) {
				
				boolean podeRealizarEmprestimos = false;
				
				int identificacaoVinculo = ((Integer) object[0]);
				Integer siape = object[1] != null ? (Integer) object[1] : 0;
				int idCategoria = (Integer) object[2];
				String descricaoCategoria  = ((String) object[3]).toUpperCase();
				int ativo = (Integer)object[4];
				String descricaoStatus  = ((String) object[5]).toUpperCase();
				
				List<Integer> statusPermitidosBiblioteca = Arrays.asList(getStatusServidorUtilizarBiblioteca());
				// Regras da UFRN
				if( statusPermitidosBiblioteca.contains(ativo)){
					podeRealizarEmprestimos = true;
				}
	
				VinculoUsuarioBiblioteca vinculo = VinculoUsuarioBiblioteca.INATIVO;
				
				if (Arrays.asList(getCategoriaDocenteBiblioteca()).contains(idCategoria)){
					vinculo = VinculoUsuarioBiblioteca.DOCENTE;
				}else{
					if (Arrays.asList(getCategoriaServidorBiblioteca()).contains(idCategoria)){
						vinculo = VinculoUsuarioBiblioteca.SERVIDOR_TECNO_ADMINISTRATIVO;
					}else{
						if(Categoria.MEDICO_RESIDENTE == idCategoria){ // obs.: para suportar usuário antigos, onde médicos residentes eram tratados como servidor
							vinculo = VinculoUsuarioBiblioteca.SERVIDOR_TECNO_ADMINISTRATIVO;
							podeRealizarEmprestimos = false;
						}
					}
				}
				
				InfoVinculoUsuarioBiblioteca infoVinculo = new InfoVinculoUsuarioBiblioteca(vinculo, identificacaoVinculo, String.valueOf(siape), descricaoCategoria
						, null, null, descricaoStatus, podeRealizarEmprestimos);
				
				infoVinculosDiscenteUsuario.add(infoVinculo);
			}
		
		}finally{
			if(dao != null) dao.close();
		}
		
		return infoVinculosDiscenteUsuario;
	}
	
	
	/**
	 * Monta as informações dos vinculos do usuário de acordo com as regras a ufrn. 
	 *
	 * @param infoVinculoTemp
	 * @return
	 * @throws DAOException 
	 */
	public List<InfoVinculoUsuarioBiblioteca> montaInformacoesVinculoUsuarioExternoBiblioteca(int idPessoa) throws DAOException{
		
		List<InfoVinculoUsuarioBiblioteca> infoVinculosDocenteExterno = new ArrayList<InfoVinculoUsuarioBiblioteca>();
		
		VinculoUsuariosBibliotecaDao dao = null;
		
		try{
			dao = DAOFactory.getInstance().getDAO(VinculoUsuariosBibliotecaDao.class);
			List<Object[]> infoVinculosUsuariosExternoTemp = dao.findInformacoesVinculosUsuarioExterno(idPessoa);
		
			if(infoVinculosUsuariosExternoTemp != null)
			for (Object[] object : infoVinculosUsuariosExternoTemp) {
				
				boolean podeRealizarEmprestimos = false;
				
				int idUsuarioExterno = (Integer) object[0];
				String cpf = object[1] != null ? Formatador.getInstance().formatarCPF_CNPJ( ((BigInteger) object[1]).longValue()) : "";
				Date prazoVinculo = (Date) object[2];
				Boolean cancelado = (Boolean) object[3];
				
				if( ! cancelado && prazoVinculo != null && CalendarUtils.calculoDias(new Date(), prazoVinculo) >= 0  ){
					podeRealizarEmprestimos = true;
				}
	
				// Regras da UFRN
				InfoVinculoUsuarioBiblioteca infoVinculo = new InfoVinculoUsuarioBiblioteca(VinculoUsuarioBiblioteca.USUARIO_EXTERNO, idUsuarioExterno, cpf, null
						, null, prazoVinculo != null ? new SimpleDateFormat("dd/MM/yyyy").format(prazoVinculo) : "", (cancelado ?  "CANCELADO" : "ATIVO" ), podeRealizarEmprestimos);
				
				infoVinculosDocenteExterno.add(infoVinculo);
			}
		
		}finally{
			if(dao != null) dao.close();
		}
		
		return infoVinculosDocenteExterno;
	}
	
	
	/**
	 * Monta as informações dos vinculos do usuário de acordo com as regras a ufrn. 
	 *
	 * @param infoVinculoTemp
	 * @return
	 * @throws DAOException 
	 */
	public List<InfoVinculoUsuarioBiblioteca> montaInformacoesVinculoDocenteExternoBiblioteca(int idPessoa) throws DAOException{
		
		List<InfoVinculoUsuarioBiblioteca> infoVinculosDocenteExterno = new ArrayList<InfoVinculoUsuarioBiblioteca>();
		
		VinculoUsuariosBibliotecaDao dao = null;
		
		try{
			dao = DAOFactory.getInstance().getDAO(VinculoUsuariosBibliotecaDao.class);
			List<Object[]> infoVinculosDocenteExternoTemp = dao.findInformacoesVinculosDocenteExterno(idPessoa);
		
			int qtdDiasAdicionais = ParametroHelper.getInstance().getParametroInt(ParametrosGerais.PERIODO_ACESSO_ADICIONAL_DOCENTE_EXTERNO );
			
			if(infoVinculosDocenteExternoTemp != null)
			for (Object[] object : infoVinculosDocenteExternoTemp) {
				
				boolean podeRealizarEmprestimos = false;
				
				int idDocenteExterno = (Integer) object[0];
				String cpf = object[1] != null ? Formatador.getInstance().formatarCPF_CNPJ( ((BigInteger) object[1]).longValue()) : "";
				String matricula = (String) object[2];
				Date prazoValidade = (Date) object[3];
				Boolean ativo = (Boolean) object[4];
				
				
				
				Date dataAtual = new Date();
				dataAtual = CalendarUtils.adicionaDias(dataAtual, -qtdDiasAdicionais-1);
				
				// Regras da UFRN
				if( ativo && StringUtils.notEmpty(matricula) && ( prazoValidade == null || CalendarUtils.calculoDias(dataAtual, prazoValidade) >= 0) ){
					podeRealizarEmprestimos = true;
				}
				
				String prazoValidadeStr = "";
	
				if(prazoValidade != null){
					Date prazoValidadeCorrigido = CalendarUtils.adicionaDias(prazoValidade, qtdDiasAdicionais+1);
					prazoValidadeStr = new SimpleDateFormat("dd/MM/yyyy").format(prazoValidadeCorrigido);
				}
				
				InfoVinculoUsuarioBiblioteca infoVinculo = new InfoVinculoUsuarioBiblioteca(VinculoUsuarioBiblioteca.DOCENTE_EXTERNO, idDocenteExterno, cpf, matricula
						, null, prazoValidadeStr, (ativo ? "ATIVO" : "CANCELADO"), podeRealizarEmprestimos);
				
				infoVinculosDocenteExterno.add(infoVinculo);
			}
		
		}finally{
			if(dao != null) dao.close();
		}
		
		return infoVinculosDocenteExterno;
	}
	
	
	/**
	 * Ver comentários da classe pai.<br/>
	 *
	 * @see br.ufrn.sigaa.biblioteca.circulacao.negocio.ObtemVinculoUsuarioBibliotecaStrategy#getStatusDiscenteUtilizarBiblioteca()
	 */
	
	@Override
	public Integer[] getStatusDiscenteUtilizarBiblioteca() {
		Collection<Integer> statusAtivosDiscenteBiblioteca = StatusDiscente.getAtivos();
		
		// Para a biblioteca esses dois são considerados ativos também, podem fazer empréstimos //
		statusAtivosDiscenteBiblioteca.add( StatusDiscente.GRADUANDO );
		statusAtivosDiscenteBiblioteca.add( StatusDiscente.DEFENDIDO );
		
		return statusAtivosDiscenteBiblioteca.toArray(new Integer[0]);
	}


	/**
	 * Ver comentários da classe pai.<br/>
	 *
	 * @see br.ufrn.sigaa.biblioteca.circulacao.negocio.ObtemVinculoUsuarioBibliotecaStrategy#getStatusServidorUtilizarBiblioteca()
	 */
	@Override
	public Integer[] getStatusServidorUtilizarBiblioteca() {
		return new Integer[]{Ativo.SERVIDOR_ATIVO, Ativo.APOSENTADO, Ativo.CEDIDO};
	}

	
	/**
	 * Ver comentários da classe pai.<br/>
	 *
	 * @see br.ufrn.sigaa.biblioteca.circulacao.negocio.ObtemVinculoUsuarioBibliotecaStrategy#getStatusServidorUtilizarBiblioteca()
	 */
	@Override
	public Character[] getNiveisAlunosPosGraduacaoBiblioteca() {
		 return new Character[]{ NivelEnsino.STRICTO, NivelEnsino.MESTRADO, NivelEnsino.DOUTORADO, NivelEnsino.LATO, NivelEnsino.RESIDENCIA }; // Residentes são considerados alunos de pós agora 13/06/2011
	}
	
	/**
	 * Ver comentários da classe pai.<br/>
	 *
	 * @see br.ufrn.sigaa.biblioteca.circulacao.negocio.ObtemVinculoUsuarioBibliotecaStrategy#getStatusServidorUtilizarBiblioteca()
	 */
	@Override
	public Character[] getNiveisAlunosGraduacaoBiblioteca() {
		 return new Character[]{ NivelEnsino.GRADUACAO };
	}
	
	/**
	 * Ver comentários da classe pai.<br/>
	 *
	 * @see br.ufrn.sigaa.biblioteca.circulacao.negocio.ObtemVinculoUsuarioBibliotecaStrategy#getStatusServidorUtilizarBiblioteca()
	 */
	@Override
	public Character[] getNiveisAlunosMedioTecnicoBiblioteca() {
		 return new Character[]{ NivelEnsino.MEDIO, NivelEnsino.TECNICO  };
	}
	
	/**
	 * Ver comentários da classe pai.<br/>
	 *
	 * @see br.ufrn.sigaa.biblioteca.circulacao.negocio.ObtemVinculoUsuarioBibliotecaStrategy#getStatusServidorUtilizarBiblioteca()
	 */
	@Override
	public Character[] getNiveisAlunosInfantilBiblioteca() {
		 return new Character[]{ NivelEnsino.INFANTIL };
	}
	
	/**
	 * Ver comentários da classe pai.<br/>
	 *
	 * @see br.ufrn.sigaa.biblioteca.circulacao.negocio.ObtemVinculoUsuarioBibliotecaStrategy#getStatusServidorUtilizarBiblioteca()
	 */
	@Override
	public Integer[] getCategoriaServidorBiblioteca() {
		 return new Integer[]{ Categoria.TECNICO_ADMINISTRATIVO  };
	}
	
	/**
	 * Ver comentários da classe pai.<br/>
	 *
	 * @see br.ufrn.sigaa.biblioteca.circulacao.negocio.ObtemVinculoUsuarioBibliotecaStrategy#getStatusServidorUtilizarBiblioteca()
	 */
	@Override
	public Integer[] getCategoriaDocenteBiblioteca() {
		 return new Integer[]{ Categoria.DOCENTE  };
	}
	
	/**
	 * Ver comentários da classe pai.<br/>
	 *
	 * @see br.ufrn.sigaa.biblioteca.circulacao.negocio.ObtemVinculoUsuarioBibliotecaStrategy#getTiposDiscenteBiblioteca()
	 */
	@Override
	public Integer[] getTiposDiscenteBiblioteca() {
		 return new Integer[]{ Discente.REGULAR, Discente.ESPECIAL  };
	}
	
}
