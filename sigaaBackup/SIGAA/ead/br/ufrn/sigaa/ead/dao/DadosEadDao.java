/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 14/06/2011
 *
 */
package br.ufrn.sigaa.ead.dao;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.ModalidadeEducacao;
import br.ufrn.sigaa.ead.resources.ComponenteCurricularEadDTO;
import br.ufrn.sigaa.ead.resources.CoordenadorCursoEadDTO;
import br.ufrn.sigaa.ead.resources.CoordenadorPoloEadDTO;
import br.ufrn.sigaa.ead.resources.DiscenteEadDTO;
import br.ufrn.sigaa.ead.resources.DiscenteTurmaEadDTO;
import br.ufrn.sigaa.ead.resources.DocenteExternoEadDTO;
import br.ufrn.sigaa.ead.resources.DocenteTurmaEadDTO;
import br.ufrn.sigaa.ead.resources.MatriculaComponenteEadDTO;
import br.ufrn.sigaa.ead.resources.ParticipantesAcaoExtensaoDTO;
import br.ufrn.sigaa.ead.resources.PessoaEadDTO;
import br.ufrn.sigaa.ead.resources.ServidorEadDTO;
import br.ufrn.sigaa.ead.resources.TurmaEadDTO;
import br.ufrn.sigaa.ead.resources.TutorEadDTO;
import br.ufrn.sigaa.ead.resources.TutoriaAlunoEadDTO;
import br.ufrn.sigaa.ead.resources.UsuarioEadDTO;

/**
 * Dao responsável por recuperar os dados necessários para a exportação dos dados referentes à ead para a Sedis.
 * 
 * @author Fred_Castro
 * 
 */
public class DadosEadDao extends GenericSigaaDAO {
	
	/**
	 * Retorna os identificadores dos cursos de educação à distância, filtrados por nivel de ensino ou id do curso.
	 * 
	 * @param nivelEnsino
	 * @param idCurso
	 * @return
	 */
	private String getSqlCurso (char nivelEnsino, int idCurso){
		return "select c.id_curso " +
				"from curso c " +
				"join comum.unidade u using (id_unidade) " +
				"left join ead.polo_curso pc using(id_curso) " +
				"where (id_modalidade_educacao in (" + ModalidadeEducacao.A_DISTANCIA + ", " + ModalidadeEducacao.SEMI_PRESENCIAL + ") or pc.id_polo_curso is not null) " +
				(nivelEnsino != '0' ? "and c.nivel = '" + nivelEnsino + "' " : "") +
				(idCurso > 0 ? "and c.id_curso = '" + idCurso + "' " : "") +
				"group by c.id_curso";
	}
	
	public List <TutorEadDTO> findTutoresByNivelPeriodoCursoPolo (char nivelEnsino, long inicio, long fim, int idCurso, int idPolo, String login) throws DAOException {
		
		Calendar cal = Calendar.getInstance();
		
		String dataInicio = "", dataFim = "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		if (inicio > 0){
			cal.setTimeInMillis(inicio);
			dataInicio = sdf.format(cal.getTime());
		}
		
		if (fim > 0){
			cal.setTimeInMillis(fim);
			dataFim = sdf.format(cal.getTime());
		}
		
		String sql = "select " +
						"t.id_tutor_orientador, pc.id_polo, t.id_vinculo, pc.id_curso, " +
						"p.id_pessoa, p.nome, p.data_nascimento, p.sexo, p.cpf_cnpj, p.email, p.telefone_fixo as telefone, e.logradouro, e.numero, e.complemento, e.bairro, m.nome as cidade, uf.sigla as estado, e.cep, " +
						"u.id_usuario, u.login, u.senha, u.inativo, " +
						"ta.id_tutoria_aluno, ta.id_discente, ta.inicio, ta.fim, ta.ativo " +
						"from ead.tutor_orientador t " +
						"join comum.pessoa p using (id_pessoa) " +
						"left join comum.endereco e on e.id_endereco = p.id_endereco_contato " +
						"left join comum.municipio m using(id_municipio) " +
						"left join comum.unidade_federativa uf on uf.id_unidade_federativa = e.id_unidade_federativa " +
						"join comum.usuario u using (id_pessoa) " +
						"join ead.polo_curso pc using (id_polo_curso) " +
						"join curso c on c.id_curso = pc.id_curso and c.id_curso in (" + getSqlCurso(nivelEnsino, idCurso) + ") " +
						"left join ead.tutoria_aluno ta on ta.id_tutor = t.id_tutor_orientador " + ( inicio > 0 ? " and ta.inicio >= '" + dataInicio + "'" : "") + ( fim > 0 ? " and (ta.fim is null or ta.fim < '" + dataFim + "') " : "") +
						"where 1 = 1 " +
						(idPolo > 0 ? " and pc.id_polo = " + idPolo : "") +
						(!login.equals("0") ? " and u.login = '" + login.replace("'", "''") + "'" : "");
		
		@SuppressWarnings("unchecked")
		List <Object []> ls = getSession().createSQLQuery(sql).list();
		
		List <TutorEadDTO> rs = new ArrayList <TutorEadDTO> ();

		TutorEadDTO tutor = null;
		
		int indiceTutorias = 0;
		
		for (Object [] l : ls){
			int i = 0;
			
			if (tutor == null || tutor.getId() != ((Number) l[0]).intValue()){
				if (tutor != null)
					rs.add(tutor);
				
				tutor = new TutorEadDTO ();
				
				tutor.setId(((Number) l[i ++]).intValue());
				tutor.setIdPolo(((Number) l[i ++]).intValue());
				tutor.setIdVinculo(l[i] != null ? ((Number) l[i]).intValue() : null); i ++;
				tutor.setIdCurso(((Number) l[i ++]).intValue());
				
				PessoaEadDTO p = new PessoaEadDTO ();
				p.setId(((Number) l[i ++]).intValue());
				p.setNome((String) l[i ++]);
				p.setDataNascimento(l[i] != null ? ((Date) l[i]).getTime() : 0); i ++;
				p.setSexo((Character) l[i ++]);
				p.setCpf(l[i] != null ? ((Number) l[i]).longValue() : null);  i++;
				p.setEmail((String) l[i ++]);
				p.setTelefone((String) l[i ++]);
				p.setLogradouro(l[i] != null ? (String) l[i] : null); i++;
				p.setNumero(l[i] != null ? (String) l[i] : null);  i++;
				p.setComplemento(l[i] != null ? (String) l[i] : null);  i++;
				p.setBairro(l[i] != null ? (String) l[i] : null);  i++;
				p.setCidade(l[i] != null ? (String) l[i] : null);  i++;
				p.setEstado(l[i] != null ? (String) l[i] : null);  i++;
				p.setCep(l[i] != null ? (String) l[i] : null);  i++;
				
				tutor.setPessoa(p);
				
				UsuarioEadDTO u = new UsuarioEadDTO ();
				u.setId(((Number) l[i ++]).intValue());
				u.setLogin((String) l[i ++]);
				u.setSenha((String) l[i ++]);
				u.setInativo((Boolean) l[i ++]);
				
				tutor.setUsuario(u);
				
				indiceTutorias = i;
			}
			
			i = indiceTutorias;
			
			if (l[i] != null){
				TutoriaAlunoEadDTO ta = new TutoriaAlunoEadDTO ();
				ta.setId(((Number) l[i ++]).intValue());
				ta.setIdTutor(tutor.getId());
				ta.setIdDiscente(((Number) l[i ++]).intValue());
				ta.setInicio(((Date) l[i ++]).getTime());
				ta.setFim(l[i] != null ? ((Date) l[i]).getTime() : 0); i ++;
				ta.setAtivo((Boolean) l[i ++]);
				
				tutor.getTutorias().add(ta);
			}
		}
		
		if (tutor != null)
			rs.add(tutor);
		
		return rs;
	}
	
	public List <DiscenteEadDTO> findAlunosTutorByNivelPeriodoTutor (char nivelEnsino, long inicio, long fim, int idTutor) throws DAOException {
		
		Calendar cal = Calendar.getInstance();
		
		String dataInicio = "", dataFim = "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		if (inicio > 0){
			cal.setTimeInMillis(inicio);
			dataInicio = sdf.format(cal.getTime());
		}
		
		if (fim > 0){
			cal.setTimeInMillis(fim);
			dataFim = sdf.format(cal.getTime());
		}
		
		String sql = "select " +
						"d.id_discente, d.periodo_ingresso, d.ano_ingresso, d.matricula, d.id_curso, d.status as id_status, g.id_polo, " +
						"p.id_pessoa, p.nome, p.data_nascimento, p.sexo, p.cpf_cnpj, p.email, p.telefone_fixo as telefone, e.logradouro, e.numero, e.complemento, e.bairro, m.nome as cidade, uf.sigla as estado, e.cep, " +
						"u.id_usuario, u.login, u.senha, u.inativo " +
						"from discente d " +
						"left join graduacao.discente_graduacao g on d.id_discente = g.id_discente_graduacao and g.id_polo is not null " +
						"join curso c on c.id_curso = d.id_curso and c.id_curso in (" + getSqlCurso(nivelEnsino, 0) + ") " +
						"join comum.pessoa p using (id_pessoa) " +
						"left join comum.endereco e on e.id_endereco = p.id_endereco_contato " +
						"join comum.municipio m on m.id_municipio = e.id_municipio " +
						"join comum.unidade_federativa uf on uf.id_unidade_federativa = e.id_unidade_federativa " +
						"join comum.usuario u using (id_pessoa) " +
						"join ead.tutoria_aluno ta on ta.id_discente = d.id_discente " + ( inicio > 0 ? " and ta.inicio >= '" + dataInicio + "'" : "") + ( fim > 0 ? " and (ta.fim is null or ta.fim < '" + dataFim + "') " : "") +
						"where 1 = 1 " +
						(idTutor > 0 ? " and ta.id_tutor = " + idTutor : "");
		
		@SuppressWarnings("unchecked")
		List <Object []> ls = getSession().createSQLQuery(sql).list();
		
		List <DiscenteEadDTO> rs = new ArrayList <DiscenteEadDTO> ();

		for (Object [] l : ls){
			int i = 0;
			
			DiscenteEadDTO d = new DiscenteEadDTO ();
			
			d.setId(((Number) l[i ++]).intValue());
			d.setPeriodoIngresso(((Number) l[i ++]).intValue());
			d.setAnoIngresso(((Number) l[i ++]).intValue());
			d.setMatricula(l[i] != null ? ((Number) l[i]).longValue() : null); i++;
			d.setIdCurso(((Number) l[i ++]).intValue());
			d.setIdStatus(((Number) l[i ++]).intValue());
			d.setIdPolo(l[i] != null ? ((Number) l[i]).intValue() : null); i++;
			
			PessoaEadDTO p = new PessoaEadDTO ();
			p.setId(((Number) l[i ++]).intValue());
			p.setNome((String) l[i ++]);
			p.setDataNascimento(l[i] != null ? ((Date) l[i]).getTime() : 0); i ++;
			p.setSexo((Character) l[i ++]);
			p.setCpf(l[i] != null ? ((Number) l[i]).longValue() : null); i++;
			p.setEmail((String) l[i ++]);
			p.setTelefone((String) l[i ++]);
			p.setLogradouro((String) l[i ++]);
			p.setNumero((String) l[i ++]);
			p.setComplemento((String) l[i ++]);
			p.setBairro((String) l[i ++]);
			p.setCidade((String) l[i ++]);
			p.setEstado((String) l[i ++]);
			p.setCep((String) l[i ++]);
			
			d.setPessoa(p);
			
			UsuarioEadDTO u = new UsuarioEadDTO ();
			u.setId(((Number) l[i ++]).intValue());
			u.setLogin((String) l[i ++]);
			u.setSenha((String) l[i ++]);
			u.setInativo((Boolean) l[i ++]);
			
			d.setUsuario(u);
			
			rs.add(d);
		}
		
		return rs;
	}
	
	public List <DiscenteEadDTO> findDiscentesByNivelPeriodoCursoPolo (char nivelEnsino, long ano, long periodo, int idCurso, int idPolo, long matricula) throws DAOException {
		
		String sql = "select " +
						"d.id_discente, d.periodo_ingresso, d.ano_ingresso, d.matricula, d.id_curso, d.status as id_status, g.id_polo, ta.id_tutor, " +
						"p.id_pessoa, p.nome, p.data_nascimento, p.sexo, p.cpf_cnpj, p.email, p.telefone_fixo as telefone, e.logradouro, e.numero, e.complemento, e.bairro, m.nome as cidade, uf.sigla as estado, e.cep, " +
						"u.id_usuario, u.login, u.senha, u.inativo, " +
						"mc.id_matricula_componente, mc.media_final, mc.recuperacao, mc.id_turma, mc.id_componente_curricular, mc.id_situacao_matricula, cc.codigo as codigo_disciplina, t.codigo as codigo_turma, t.ano, t.periodo " +
						"from discente d " +
						"left join graduacao.discente_graduacao g on d.id_discente = g.id_discente_graduacao and g.id_polo is not null " +
						"join curso c on c.id_curso = d.id_curso and c.id_curso in (" + getSqlCurso(nivelEnsino, idCurso) + ") " +
						"join comum.pessoa p using (id_pessoa) " +
						"left join comum.endereco e on e.id_endereco = p.id_endereco_contato " +
						"left join comum.municipio m on m.id_municipio = e.id_municipio " +
						"left join comum.unidade_federativa uf on uf.id_unidade_federativa = e.id_unidade_federativa " +
						"left join comum.usuario u using (id_pessoa) " +
						"left join ead.tutoria_aluno ta on ta.id_discente = d.id_discente " +
						"join ensino.matricula_componente mc on d.id_discente = mc.id_discente " + (ano > 0 ? " and mc.ano = " + ano + " " : "") + (periodo > 0 ? " and mc.periodo = " + periodo + " " : "") +
						"join ensino.turma t on t.id_turma = mc.id_turma and t.distancia = true " +
						"join ensino.componente_curricular cc using (id_disciplina) " +
						"where 1 = 1 " +
						(idPolo > 0 ? " and pc.id_polo = " + idPolo : "") +
						(matricula > 0 ? " and d.matricula = " + matricula : "");
		
		@SuppressWarnings("unchecked")
		List <Object []> ls = getSession().createSQLQuery(sql).list();
		
		List <DiscenteEadDTO> rs = new ArrayList <DiscenteEadDTO> ();

		DiscenteEadDTO discente = null;
		
		int indiceDiscente = 0;
		
		for (Object [] l : ls){
			int i = 0;
			
			if (discente == null || discente.getId() != ((Number) l[0]).intValue()){
				if (discente != null)
					rs.add(discente);
				
				discente = new DiscenteEadDTO ();
				
				discente.setId(((Number) l[i ++]).intValue());
				discente.setPeriodoIngresso(((Number) l[i ++]).intValue());
				discente.setAnoIngresso(((Number) l[i ++]).intValue());
				discente.setMatricula(l[i] != null ? ((Number) l[i]).longValue() : null); i++;
				discente.setIdCurso(((Number) l[i ++]).intValue());
				discente.setIdStatus(((Number) l[i ++]).intValue());
				discente.setIdPolo(l[i] != null ? ((Number) l[i]).intValue() : null); i++;
				discente.setIdTutor(l[i] != null ? ((Number) l[i]).intValue() : null); i++;
				
				PessoaEadDTO p = new PessoaEadDTO ();
				p.setId(((Number) l[i ++]).intValue());
				p.setNome((String) l[i ++]);
				p.setDataNascimento(l[i] != null ? ((Date) l[i]).getTime() : 0); i ++;
				p.setSexo((Character) l[i ++]);
				p.setCpf(l[i] != null ? ((Number) l[i]).longValue() : null); i++;
				p.setEmail((String) l[i ++]);
				p.setTelefone((String) l[i ++]);
				
				p.setLogradouro(l[i] != null ? (String) l[i] : null); i ++;
				p.setNumero(l[i] != null ? (String) l[i] : null); i ++;
				p.setComplemento(l[i] != null ? (String) l[i] : null); i ++;
				p.setBairro(l[i] != null ? (String) l[i] : null); i ++;
				p.setCidade(l[i] != null ? (String) l[i] : null); i ++;
				p.setEstado(l[i] != null ? (String) l[i] : null); i ++;
				p.setCep(l[i] != null ? (String) l[i] : null); i ++;
			
				discente.setPessoa(p);
				
				UsuarioEadDTO u = new UsuarioEadDTO ();
				Integer idUsuario = (Integer) (l[i ++]);
				
				if (!isEmpty(idUsuario)) {
					u.setId(idUsuario);
					u.setLogin((String) l[i ++]);
					u.setSenha((String) l[i ++]);
					u.setInativo((Boolean) l[i ++]);
				} else {
					i=i+3;
					u = null;
				}
				
				
				discente.setUsuario(u);
				
				indiceDiscente = i;
			}
			
			i = indiceDiscente;
			
			if (l[i] != null){
				
				DiscenteTurmaEadDTO dt = new DiscenteTurmaEadDTO();
				dt.setId(((Number) l[i++]).intValue());
				dt.setMediaFinal(l[i] != null ? ((Number) l[i]).intValue() : null); i++;
				dt.setRecuperacao(l[i] != null ? ((Number) l[i]).intValue() : null); i++;
				dt.setIdTurma(l[i] != null ? ((Number) l[i]).intValue() : null); i++;
				dt.setIdComponenteCurricular(((Number) l[i++]).intValue());
				dt.setIdSituacaoMatricula(((Number) l[i++]).intValue());
				dt.setCodigoDisciplina((String) l[i++]);
				dt.setCodigoTurma(((String) l[i++]));
				dt.setAno(((Number) l[i++]).intValue());
				dt.setPeriodo(((Number) l[i++]).intValue());
				
				if (!discente.getDiscenteTurmas().contains(dt))
					discente.getDiscenteTurmas().add(dt);
			}
		}
		
		if (discente != null)
			rs.add(discente);
		
		return rs;
	}
	
	public List <CoordenadorCursoEadDTO> findCoordenadoresCursoByNivelNivelPeriodoCurso (long inicio, long fim, int idCurso) throws DAOException {
		
		Calendar cal = Calendar.getInstance();
		
		String dataInicio = "", dataFim = "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		if (inicio > 0){
			cal.setTimeInMillis(inicio);
			dataInicio = sdf.format(cal.getTime());
		}
		
		if (fim > 0){
			cal.setTimeInMillis(fim);
			dataFim = sdf.format(cal.getTime());
		}
		
		String sql = "select " +
						"cc.id_coordenacao_curso, cc.id_curso, cc.data_inicio_mandato, cc.data_fim_mandato, cc.id_cargo_academico, " +
						"p.id_pessoa, p.nome, p.data_nascimento, p.sexo, p.cpf_cnpj, p.email, p.telefone_fixo as telefone, e.logradouro, e.numero, e.complemento, e.bairro, m.nome as cidade, uf.sigla as estado, e.cep, " +
						"u.id_usuario, u.login, u.senha, u.inativo " +
						"from ensino.coordenacao_curso cc " +
						"join rh.servidor s using (id_servidor) " +
						"join comum.pessoa p using (id_pessoa) " +
						"join comum.usuario u using (id_pessoa) " +
						"left join comum.endereco e on e.id_endereco = p.id_endereco_contato " +
						"join comum.municipio m on m.id_municipio = e.id_municipio " +
						"join comum.unidade_federativa uf on uf.id_unidade_federativa = e.id_unidade_federativa " +
						"where cc.id_curso in (" + getSqlCurso('0', idCurso) + ") " +
						(inicio > 0 ? " and cc.data_inicio_mandato >= '" + dataInicio + "'" : "") + ( fim > 0 ? " and (cc.data_fim_mandato is null or cc.data_fim_mandato < '" + dataFim + "') " : "");
		
		@SuppressWarnings("unchecked")
		List <Object []> ls = getSession().createSQLQuery(sql).list();
		
		List <CoordenadorCursoEadDTO> rs = new ArrayList <CoordenadorCursoEadDTO> ();

		for (Object [] l : ls){
			int i = 0;
			
			CoordenadorCursoEadDTO c = new CoordenadorCursoEadDTO ();
			
			c.setId(((Number) l[i ++]).intValue());
			c.setIdCurso(((Number) l[i ++]).intValue());
			c.setInicio(l[i] != null ? ((Date) l[i]).getTime() : 0); i ++;
			c.setFim(l[i] != null ? ((Date) l[i]).getTime() : 0); i ++;
			c.setIdCargoAcademico(((Number) l[i ++]).intValue());
			
			PessoaEadDTO p = new PessoaEadDTO ();
			p.setId(((Number) l[i ++]).intValue());
			p.setNome((String) l[i ++]);
			p.setDataNascimento(l[i] != null ? ((Date) l[i]).getTime() : 0); i ++;
			p.setSexo((Character) l[i ++]);
			p.setCpf(l[i] != null ? ((Number) l[i]).longValue() : null); i++;
			p.setEmail((String) l[i ++]);
			p.setTelefone((String) l[i ++]);
			p.setLogradouro((String) l[i ++]);
			p.setNumero((String) l[i ++]);
			p.setComplemento((String) l[i ++]);
			p.setBairro((String) l[i ++]);
			p.setCidade((String) l[i ++]);
			p.setEstado((String) l[i ++]);
			p.setCep((String) l[i ++]);
			
			c.setPessoa(p);
			
			UsuarioEadDTO u = new UsuarioEadDTO ();
			u.setId(((Number) l[i ++]).intValue());
			u.setLogin((String) l[i ++]);
			u.setSenha((String) l[i ++]);
			u.setInativo((Boolean) l[i ++]);
			
			c.setUsuario(u);
			
			rs.add(c);
		}
		
		return rs;
	}
	
	public List <CoordenadorPoloEadDTO> findCoordenadoresPoloByNivelNivelPeriodoPolo (long inicio, long fim, int idPolo) throws DAOException {
		
		Calendar cal = Calendar.getInstance();
		
		String dataInicio = "", dataFim = "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		if (inicio > 0){
			cal.setTimeInMillis(inicio);
			dataInicio = sdf.format(cal.getTime());
		}
		
		if (fim > 0){
			cal.setTimeInMillis(fim);
			dataFim = sdf.format(cal.getTime());
		}
		
		String sql = "select " +
						"c.id_coordenacao_polo, c.id_polo, c.inicio, c.fim, " +
						"p.id_pessoa, p.nome, p.data_nascimento, p.sexo, p.cpf_cnpj, p.email, p.telefone_fixo as telefone, e.logradouro, e.numero, e.complemento, e.bairro, m.nome as cidade, uf.sigla as estado, e.cep, " +
						"u.id_usuario, u.login, u.senha, u.inativo " +
						"from ead.coordenacao_polo c " +
						"join comum.pessoa p using (id_pessoa) " +
						"join comum.usuario u using (id_pessoa) " +
						"left join comum.endereco e on e.id_endereco = p.id_endereco_contato " +
						"join comum.municipio m on m.id_municipio = e.id_municipio " +
						"join comum.unidade_federativa uf on uf.id_unidade_federativa = e.id_unidade_federativa " +
						"where 1 = 1 " +
						(inicio > 0 ? " and c.inicio >= '" + dataInicio + "'" : "") + ( fim > 0 ? " and (c.fim is null or c.fim < '" + dataFim + "') " : "") +
						(idPolo > 0 ? " and c.id_polo = " + idPolo : "");
		
		@SuppressWarnings("unchecked")
		List <Object []> ls = getSession().createSQLQuery(sql).list();
		
		List <CoordenadorPoloEadDTO> rs = new ArrayList <CoordenadorPoloEadDTO> ();

		for (Object [] l : ls){
			int i = 0;
			
			CoordenadorPoloEadDTO c = new CoordenadorPoloEadDTO ();
			
			c.setId(((Number) l[i ++]).intValue());
			c.setIdPolo(((Number) l[i ++]).intValue());
			c.setInicio(l[i] != null ? ((Date) l[i]).getTime() : 0); i ++;
			c.setFim(l[i] != null ? ((Date) l[i]).getTime() : 0); i ++;
			
			PessoaEadDTO p = new PessoaEadDTO ();
			p.setId(((Number) l[i ++]).intValue());
			p.setNome((String) l[i ++]);
			p.setDataNascimento(l[i] != null ? ((Date) l[i]).getTime() : 0); i ++;
			p.setSexo((Character) l[i ++]);
			p.setCpf(l[i] != null ? ((Number) l[i]).longValue() : null); i++;
			p.setEmail((String) l[i ++]);
			p.setTelefone((String) l[i ++]);
			p.setLogradouro((String) l[i ++]);
			p.setNumero((String) l[i ++]);
			p.setComplemento((String) l[i ++]);
			p.setBairro((String) l[i ++]);
			p.setCidade((String) l[i ++]);
			p.setEstado((String) l[i ++]);
			p.setCep((String) l[i ++]);
			
			c.setPessoa(p);
			
			UsuarioEadDTO u = new UsuarioEadDTO ();
			u.setId(((Number) l[i ++]).intValue());
			u.setLogin((String) l[i ++]);
			u.setSenha((String) l[i ++]);
			u.setInativo((Boolean) l[i ++]);
			
			c.setUsuario(u);
			
			rs.add(c);
		}
		
		return rs;
	}
	
	@SuppressWarnings("unchecked")
	public List <ComponenteCurricularEadDTO> findTurmasByNivelPeriodoCurso (char nivelEnsino, int ano, int periodo, int idCurso, int idComponenteCurricular, long inicio) throws DAOException {
		
		Calendar cal = Calendar.getInstance();
		
		String dataInicio = "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		if (inicio > 0){
			cal.setTimeInMillis(inicio);
			dataInicio = sdf.format(cal.getTime());
		}
		
		String sql = "select " +
				"cc.id_componente_curricular, cc.codigo, cc.nome, cc.ementa, cc.ch_aula, cc.ch_laboratorio, cc.ch_estagio, cc.unidade_responsavel, cc.id_tipo_componente_curricular, " +
				"t.id_turma, t.id_polo, ('' || t.ano || t.periodo || '_' || t.codigo || '_' || cc.codigo) as codigo_turma, t.ano, t.periodo, t.data_inicio, t.data_fim " +
				"from (select distinct cc.id_disciplina as id_componente_curricular, cc.codigo, d.nome, d.ementa, d.ch_aula, d.ch_laboratorio, d.ch_estagio, u.nome as unidade_responsavel, cc.id_tipo_componente as id_tipo_componente_curricular " +
				"from ensino.componente_curricular cc " +
				"join comum.unidade u on cc.id_unidade = u.id_unidade " +
				"join ensino.componente_curricular_detalhes d on cc.id_detalhe = d.id_componente_detalhes " +
				"join tecnico.modulo_disciplina md using (id_disciplina) " +
				"join tecnico.modulo m using (id_modulo) " +
				"join tecnico.modulo_curricular using (id_modulo) " +
				"join tecnico.estrutura_curricular_tecnica ect using (id_estrutura_curricular) " +
				"where ect.id_curso in (" + getSqlCurso(nivelEnsino, idCurso) + ") and ect.ativa = true " +
				"union " +
				"select distinct cc.id_disciplina as id_componente_curricular, cc.codigo, d.nome, d.ementa, d.ch_aula, d.ch_laboratorio, d.ch_estagio, u.nome as unidade_responsavel, cc.id_tipo_componente as id_tipo_componente_curricular " +
				"from ensino.componente_curricular cc " +
				"join comum.unidade u on cc.id_unidade = u.id_unidade " +
				"join ensino.componente_curricular_detalhes d on cc.id_detalhe = d.id_componente_detalhes " +
				"join graduacao.curriculo_componente ccc on ccc.id_componente_curricular = cc.id_disciplina " +
				"join graduacao.curriculo c on c.id_curriculo = ccc.id_curriculo and c.id_curso in (" + getSqlCurso(nivelEnsino, idCurso) + ")) as cc " +
				"join ensino.turma t on t.id_disciplina = cc.id_componente_curricular " +
				"where 1 = 1 " +
				(ano > 0 ? "and t.ano =" + ano : "") +
				(periodo > 0 ? "and t.periodo =" + periodo + " " : "") + 
				(idComponenteCurricular > 0 ? "and cc.id_componente_curricular =" + idComponenteCurricular + " " : "") + 
				(!dataInicio.equals("") ? "and t.data_cadastro >= '" + dataInicio + "' or t.data_alteracao >= '" + dataInicio + "' " : "") +
				"order by t.id_turma";
		
		List <Object []> ls = getSession().createSQLQuery(sql).list();
		
		List <ComponenteCurricularEadDTO> rs = new ArrayList <ComponenteCurricularEadDTO> ();

		ComponenteCurricularEadDTO d = null;
		
		int indiceTurmas = 0;
		
		String idsTurmas = "";
		
		for (Object [] l : ls){
			int i = 0;
			
			if (d == null || d.getId() != ((Number) l[0]).intValue()){
				if (d != null)
					rs.add(d);
				
				d = new ComponenteCurricularEadDTO ();
				
				d.setId(((Number) l[i ++]).intValue());
				d.setCodigo((String) l[i ++]);
				d.setNome((String) l[i ++]);
				d.setEmenta((String) l[i ++]);
				d.setChAula(((Number) l[i ++]).intValue());
				d.setChLaboratorio(((Number) l[i ++]).intValue());
				d.setChEstagio(((Number) l[i ++]).intValue());
				d.setIdUnidadeResponsavel(0); i++; // TODO CORRIGIR PARA STRING ((Number) l[i ++]).intValue());
				d.setIdTipoComponenteCurricular(((Number) l[i ++]).intValue());
				
				indiceTurmas = i;
			}
			
			i = indiceTurmas;
			
			if (l[i] != null){
				TurmaEadDTO t = new TurmaEadDTO ();

				idsTurmas += (idsTurmas.equals("") ? "" : ",") + l[i];
				
				t.setId(((Number) l[i ++]).intValue());
				t.setIdPolo(l[i] != null ? ((Number) l[i]).intValue() : null); i++;
				t.setCodigoTurma((String) l[i ++]);
				t.setAno(((Number) l[i ++]).intValue());
				t.setPeriodo(((Number) l[i ++]).intValue());
				t.setDataInicio(l[i] != null ? ((Date) l[i]).getTime() : 0); i++;
				t.setDataFim(l[i] != null ? ((Date) l[i]).getTime() : 0); i++;
				
				d.getTurmas().add(t);
			}
		}
		
		if (d != null)
			rs.add(d);
		
		// Busca os docentes das turmas
		/*ls = getSession().createSQLQuery("select dt.id_turma, p.id_pessoa, p.nome, u.id_usuario, u.login, u.email " +
				"from ensino.docente_turma dt " +
				"left join rh.servidor s on s.id_servidor = dt.id_docente " +
				"left join endino.docente_externo de using(id_docente_externo) " +
				"join comum.pessoa p on p.id_pessoa = coalesce(s.id_pessoa, de.id_pessoa) " +
				"where dt.id_turma in (" + idsTurmas + ") " +
				"order by dt.id_turma").list();
		
		int c = 0;
		for (ComponenteCurricularEadDTO r : rs){
	//		while (c < ls.size() && ls[c][0] == r.getTurmas().get(0).getId())
		}*/
		
		return rs;
	}
	
	public List <MatriculaComponenteEadDTO> findMatriculasByNivelPeriodoCodigoCurso (char nivelEnsino, int ano, int periodo, String codigoDisciplina, int idCurso, long inicio, Boolean rematricula) throws DAOException {
		
		Calendar cal = Calendar.getInstance();
		
		String dataInicio = "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		if (inicio > 0){
			cal.setTimeInMillis(inicio);
			dataInicio = sdf.format(cal.getTime());
		}
		
		String sql = "select " +
				"mc.id_matricula_componente, mc.media_final, mc.recuperacao, mc.id_discente, mc.id_turma, mc.id_situacao_matricula, mc.data_cadastro, d.matricula, " +
				"cc.codigo, mun.id_municipio, sem_acento(mun.nome), po.codigo as codpolo, " +
				"trim(to_char(pe.cpf_cnpj, '00000000000')) as cpfaluno, " +
				"trim(to_char(pr.cpf_cnpj, '00000000000')) as cpfdocente " +
				"from (select distinct cc.id_disciplina, cc.codigo " +
				"from ensino.componente_curricular cc " +
				"join comum.unidade u on cc.id_unidade = u.id_unidade " +
				"join ensino.componente_curricular_detalhes d on cc.id_detalhe = d.id_componente_detalhes " +
				"join tecnico.modulo_disciplina md using (id_disciplina) " +
				"join tecnico.modulo m using (id_modulo) " +
				"join tecnico.modulo_curricular using (id_modulo) " +
				"join tecnico.estrutura_curricular_tecnica ect using (id_estrutura_curricular) " +
				"where ect.id_curso in (" + getSqlCurso(nivelEnsino, idCurso) + ") and ect.ativa = true " +
				"union " +
				"select distinct cc.id_disciplina, cc.codigo " +
				"from ensino.componente_curricular cc " +
				"join comum.unidade u on cc.id_unidade = u.id_unidade " +
				"join ensino.componente_curricular_detalhes d on cc.id_detalhe = d.id_componente_detalhes " +
				"join graduacao.curriculo_componente ccc on ccc.id_componente_curricular = cc.id_disciplina " +
				"join graduacao.curriculo c on c.id_curriculo = ccc.id_curriculo and c.id_curso in (" + getSqlCurso(nivelEnsino, idCurso) + ")) as cc " +
				"join ensino.turma t on t.id_disciplina = cc.id_disciplina " +
				"left join ensino.docente_turma dt using (id_turma) " +
				"left join rh.servidor s on dt.id_docente = s.id_servidor " +
				"left join comum.pessoa pr on s.id_pessoa = pr.id_pessoa " +
				"join ensino.matricula_componente mc using (id_turma) " +
				"join discente d using (id_discente) " +
				"join comum.pessoa pe on d.id_pessoa = pe.id_pessoa " +
				"join graduacao.discente_graduacao dg on (d.id_discente=dg.id_discente_graduacao) " +
				"join ead.polo po on (dg.id_polo=po.id_polo) " +
				"join comum.municipio mun on (mun.id_municipio=po.id_cidade) " +
				"where 1 = 1 " +
				(ano > 0 ? "and t.ano =" + ano : "") +
				(periodo > 0 ? "and t.periodo =" + periodo + " " : "") + 
				(!codigoDisciplina.equals("0") ? "and cc.codigo = '" + codigoDisciplina + "' " : "") + 
				(!dataInicio.equals("") ? "and mc.data_cadastro >= '" + dataInicio + "'" : "") +
				(rematricula != null && rematricula ? "and mc.rematricula = trueValue()" : "");
		
		@SuppressWarnings("unchecked")
		List <Object []> ls = getSession().createSQLQuery(sql).list();
		
		List <MatriculaComponenteEadDTO> rs = new ArrayList <MatriculaComponenteEadDTO> ();

		MatriculaComponenteEadDTO m = null;
		
		int idAtual = 0;
		
		for (Object [] l : ls){
			int i = 0;
			
			
			int id = ((Number) l[i ++]).intValue();
			
			if(id != idAtual){
				idAtual = id;
				m = new MatriculaComponenteEadDTO ();
				
				m.setId(id);
				m.setMediaFinal(l[i] != null ? ((Number) l[i]).floatValue() : null); i++;
				m.setRecuperacao(l[i] != null ? ((Number) l[i]).floatValue() : null); i++;
				m.setIdDiscente(((Number) l[i ++]).intValue());
				m.setIdTurma(((Number) l[i ++]).intValue());
				m.setIdSituacaoMatricula(((Number) l[i ++]).intValue());
				m.setDataCadastro(l[i] != null ? ((Date) l[i]).getTime() : 0); i ++;
				m.setMatricula(((Number) l[i++]).longValue());
				m.setCodigoComponente((String) l[i++]);
				m.setIdMunicipio(((Number) l[i++]).intValue());
				m.setNomeMunicipio((String) l[i++]);
				m.setCodigoPolo((String) l[i++]);
				m.setCpfAluno((String) l[i++]);
				String cpfdocente = (String) l[i++];
				if(ValidatorUtil.isEmpty(cpfdocente))
					cpfdocente = "0";
				m.setCpfsDocentes(new HashSet<String>());
				m.getCpfsDocentes().add(cpfdocente);
				
				rs.add(m);
			} else if (m != null) {
				String cpfdocente = (String) l[13];
				if(ValidatorUtil.isEmpty(cpfdocente))
					cpfdocente = "0";
				m.getCpfsDocentes().add(cpfdocente);
			}
				
		}
		
		return rs;
	}
	
	
	public List <PessoaEadDTO> findDocentesByNivelPeriodoDisciplina (char nivelEnsino, int ano, int periodo, String codigoDisciplina, long inicio) throws DAOException {
		
		Calendar cal = Calendar.getInstance();
		
		String dataInicio = "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		if (inicio > 0){
			cal.setTimeInMillis(inicio);
			dataInicio = sdf.format(cal.getTime());
		}
		
		String sql = "select " +
				"p.id_pessoa, p.nome, p.data_nascimento, p.sexo, p.cpf_cnpj, p.email, p.telefone_fixo as telefone, e.logradouro, e.numero, e.complemento, e.bairro, m.nome as cidade, uf.sigla as estado, e.cep, " +
				"u.id_usuario, u.login, u.senha, u.inativo, " +
				"s.id_servidor, s.siape, s.lotacao, s.id_formacao, s.regime_trabalho, s.dedicacao_exclusiva, " +
				"de.id_docente_externo, de.matricula, " +
				"dt.id_docente_turma, dt.id_turma, cc.codigo, t.ano, t.periodo " +
				"from (select distinct cc.id_disciplina, cc.codigo " +
				"from ensino.componente_curricular cc " +
				"join comum.unidade u on cc.id_unidade = u.id_unidade " +
				"join ensino.componente_curricular_detalhes d on cc.id_detalhe = d.id_componente_detalhes " +
				"join tecnico.modulo_disciplina md using (id_disciplina) " +
				"join tecnico.modulo m using (id_modulo) " +
				"join tecnico.modulo_curricular using (id_modulo) " +
				"join tecnico.estrutura_curricular_tecnica ect using (id_estrutura_curricular) " +
				"where ect.id_curso in (" + getSqlCurso(nivelEnsino, 0) + ") and ect.ativa = true " +
				"union " +
				"select distinct cc.id_disciplina, cc.codigo " +
				"from ensino.componente_curricular cc " +
				"join comum.unidade u on cc.id_unidade = u.id_unidade " +
				"join ensino.componente_curricular_detalhes d on cc.id_detalhe = d.id_componente_detalhes " +
				"join graduacao.curriculo_componente ccc on ccc.id_componente_curricular = cc.id_disciplina " +
				"join graduacao.curriculo c on c.id_curriculo = ccc.id_curriculo and c.id_curso in (" + getSqlCurso(nivelEnsino, 0) + ")) as cc " +
				"join ensino.turma t on t.id_disciplina = cc.id_disciplina " +
				"join ensino.docente_turma dt using (id_turma) " +
				"left join rh.servidor s on s.id_servidor = dt.id_docente " +
				"left join ensino.docente_externo de using (id_docente_externo) " +
				"join comum.pessoa p on p.id_pessoa = coalesce (s.id_pessoa, de.id_pessoa) " +
				"left join comum.endereco e on e.id_endereco = p.id_endereco_contato " +
				"left join comum.municipio m on m.id_municipio = e.id_municipio " +
				"left join comum.unidade_federativa uf on uf.id_unidade_federativa = e.id_unidade_federativa " +
				"join comum.usuario u on u.id_pessoa = p.id_pessoa " +
				"where 1 = 1 " +
				(ano > 0 ? "and t.ano =" + ano : "") +
				(periodo > 0 ? "and t.periodo =" + periodo + " " : "") + 
				(!codigoDisciplina.equals("0") ? "and cc.codigo = '" + codigoDisciplina + "' " : "") + 
				(!dataInicio.equals("") ? "and dt.data_cadastro >= '" + dataInicio + "' " : "") +
				"order by p.id_pessoa";
		
		@SuppressWarnings("unchecked")
		List <Object []> ls = getSession().createSQLQuery(sql).list();
		
		List <PessoaEadDTO> rs = new ArrayList <PessoaEadDTO> ();

		PessoaEadDTO p = null;
		
		int indiceTurmas = 0;
		
		for (Object [] l : ls){
			int i = 0;
			
			if (p == null || p.getId() != ((Number) l[0]).intValue()){
				if (p != null)
					rs.add(p);
				
				p = new PessoaEadDTO ();
				
				p.setId(((Number) l[i ++]).intValue());
				p.setNome((String) l[i ++]);
				p.setDataNascimento(l[i] != null ? ((Date) l[i]).getTime() : 0); i ++;
				p.setSexo((Character) l[i ++]);
				p.setCpf(l[i] != null ? ((Number) l[i]).longValue() : null); i++;
				p.setEmail((String) l[i ++]);
				p.setTelefone((String) l[i ++]);
				p.setLogradouro((String) l[i ++]);
				p.setNumero((String) l[i ++]);
				p.setComplemento((String) l[i ++]);
				p.setBairro((String) l[i ++]);
				p.setCidade((String) l[i ++]);
				p.setEstado((String) l[i ++]);
				p.setCep((String) l[i ++]);
				
				UsuarioEadDTO u = new UsuarioEadDTO ();
				u.setId(((Number) l[i ++]).intValue());
				u.setLogin((String) l[i ++]);
				u.setSenha((String) l[i ++]);
				u.setInativo((Boolean) l[i ++]);
				
				p.setUsuario(u);
				
				ServidorEadDTO s = null;
				
				if (l[i] != null){
					s = new ServidorEadDTO ();
					
					s.setId(((Number) l[i ++]).intValue());
					s.setSiape(((Number) l[i ++]).intValue());
					s.setLotacao((String) l[i ++]);
					s.setIdFormacao(((Number) l[i ++]).intValue());
					s.setRegimeTrabalho(((Number) l[i ++]).intValue());
					s.setDedicacaoExclusiva((Boolean) l[i ++]);
					
					p.setServidor(s);
				} else
					i += 6;
					
				p.setServidor(s);

				DocenteExternoEadDTO de = null;
				
				if (l[i] != null){
					de = new DocenteExternoEadDTO();
					de.setId(((Number) l[i ++]).intValue());
					de.setMatricula((String) l[i ++]);
				} else
					i += 2;
				
				p.setDocenteExterno(de);
				
				indiceTurmas = i;
				
				p.setDocenteTurmas(new ArrayList <DocenteTurmaEadDTO> ());
			}
			
			i = indiceTurmas;
			
			DocenteTurmaEadDTO dt = new DocenteTurmaEadDTO ();
			dt.setId(((Number) l[i ++]).intValue());
			dt.setIdTurma(((Number) l[i ++]).intValue());
			dt.setCodigo((String) l[i ++]);
			dt.setAno(((Number) l[i ++]).intValue());
			dt.setPeriodo(((Number) l[i ++]).intValue());
			
			p.getDocenteTurmas().add(dt);
		}
		
		if (p != null)
			rs.add(p);
		
		return rs;
	}

	public List <ParticipantesAcaoExtensaoDTO> findParticipantesAcaoExtensao (int acaoExtensao) throws DAOException {
		
		String sql = "select cpae.cpf, sem_acento(cpae.logradouro) as logradouro, sem_acento(cpae.bairro) as bairro, sem_acento(cpae.numero) as numero," +
				" sem_acento(cpae.cep) as cep, sem_acento(cpae.nome) as nome, sem_acento(cpae.email) as email " +
				" FROM extensao.participante_acao_extensao" +
				" JOIN extensao.cadastro_participante_atividade_extensao cpae using ( id_cadastro_participante_atividade_extensao )" +
				" WHERE id_acao_extensao = " + acaoExtensao;
		
		@SuppressWarnings("unchecked")
		List <Object []> ls = getSession().createSQLQuery(sql).list();
		List <ParticipantesAcaoExtensaoDTO> rs = new ArrayList <ParticipantesAcaoExtensaoDTO> ();
		for (Object [] l : ls){
			int i = 0;
			ParticipantesAcaoExtensaoDTO participante = new ParticipantesAcaoExtensaoDTO();

			participante.setCpf( ((BigInteger) l[i++]).longValue() );
			participante.setLogradouro( (String) l[i++] );
			participante.setBairro( (String) l[i++] );
			participante.setNumero( (String) l[i++] );
			participante.setCep( (String) l[i++] );
			participante.setNome( (String) l[i++] );
			participante.setEmail( (String) l[i++] );
			rs.add(participante);
		}
		
		return rs;
	}

}