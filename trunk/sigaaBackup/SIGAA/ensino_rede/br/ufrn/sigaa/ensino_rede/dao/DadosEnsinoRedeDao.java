/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Criado em: 10/08/2013 
 */
package br.ufrn.sigaa.ensino_rede.dao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ensino_rede.resources.DadosCursoRedeDTO;
import br.ufrn.sigaa.ensino_rede.resources.DiscenteAssociadoDTO;
import br.ufrn.sigaa.ensino_rede.resources.DocenteRedeDTO;
import br.ufrn.sigaa.ensino_rede.resources.MatriculaComponenteRedeDTO;

/**
 * Dao com consultas de informações de ensino em rede.
 * 
 * @author Leonardo Campos
 * 
 */
public class DadosEnsinoRedeDao extends GenericSigaaDAO {

	public List<DadosCursoRedeDTO> findCursosByProgramaCampusIes(String programa, char nivelEnsino, String siglaIes, int idCampusIes,	int idCurso) throws DAOException {
		String sql = "select d.id_dados_curso_rede, p.descricao as programa, ies.sigla, ci.id_campus, sem_acento(ci.nome) as campus, ca.id_curso_associado, sem_acento(ca.nome) as nome_curso" +
				" from ensino_rede.dados_curso_rede d" +
				" join ensino_rede.programa_rede p using (id_programa_rede)" +
				" join ensino_rede.curso_associado ca using (id_curso_associado)" +
				" join comum.campus_ies ci using (id_campus)" +
				" join comum.instituicoes_ensino ies on (ies.id = ci.id_ies)" +
				" where 1 = 1" +
				(!programa.equals("0") ? " and p.descricao = '" + programa + "' " : "") +
				(nivelEnsino != '0' ? " and ca.nivel = '" + nivelEnsino + "' " : "") +
				(!siglaIes.equals("0") ? " and ies.sigla = '" + siglaIes + "' " : "") +
				(idCampusIes > 0 ? " and ci.id_campus =" + idCampusIes + " " : "") + 
				(idCurso > 0 ? " and ca.id_curso_associado =" + idCurso + " " : ""); 
		
		@SuppressWarnings("unchecked")
		List <Object []> ls = getSession().createSQLQuery(sql).list();
		
		List <DadosCursoRedeDTO> rs = new ArrayList<DadosCursoRedeDTO>();
		
		DadosCursoRedeDTO d = null;
		
		for (Object [] l : ls){
			int i = 0;
			
			d = new DadosCursoRedeDTO();
			d.setId(((Number) l[i ++]).intValue());
			d.setPrograma((String) l[i++]);
			d.setIes((String) l[i++]);
			d.setIdCampus(((Number) l[i ++]).intValue());
			d.setNomeCampus((String) l[i++]);
			d.setIdCurso(((Number) l[i ++]).intValue());
			d.setNomeCurso((String) l[i++]);
			
			rs.add(d);
		}
		
		return rs;
	}
	
	public List<MatriculaComponenteRedeDTO> findMatriculasByCampusIes(
			char nivelEnsino, int ano, int periodo, String siglaIes, int idCampusIes, String codigoDisciplina,
			int idCurso, long inicio) throws DAOException {
		
		Calendar cal = Calendar.getInstance();
		
		String dataInicio = "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		if (inicio > 0){
			cal.setTimeInMillis(inicio);
			dataInicio = sdf.format(cal.getTime());
		}
		
		String sql = "select m.id_matricula_componente, p.nome_ascii, trim(to_char(p.cpf_cnpj, '00000000000')) as cpfdiscente, p.email" +
				", ies.sigla as ies, sem_acento(ci.nome) as campus, ca.nome as curso, cc.id_disciplina, cc.codigo as codigo_componente, sem_acento(cc.nome) as nome_componente, t.codigo as codigo_turma, sm.descricao as situacao, m.ano, m.periodo, t.data_inicio, t.data_fim" +
				", pe.nome_ascii as docente, trim(to_char(pe.cpf_cnpj, '00000000000')) as cpfdocente, pe.email as emaildocente" +
				" from ensino_rede.matricula_componente_rede m" +
				" join ensino_rede.turma_rede t on (t.id_turma_rede=m.id_turma)" +
				" join ensino_rede.componente_curricular_rede cc on (t.id_componente_curricular=cc.id_disciplina)" +
				" join ensino.situacao_matricula sm on (sm.id_situacao_matricula=m.id_situacao)" +
				" join ensino_rede.discente_associado d on (d.id_discente_associado = m.id_discente)" +
				" join comum.pessoa p using (id_pessoa)" +
				" join ensino_rede.dados_curso_rede dcr on (d.id_dados_curso_rede = dcr.id_dados_curso_rede)" +
				" join ensino_rede.curso_associado ca on (ca.id_curso_associado = dcr.id_curso_associado)" +
				" join comum.campus_ies ci on (dcr.id_campus = ci.id_campus)" +
				" join comum.instituicoes_ensino ies on (ies.id = ci.id_ies)" +
				" left join ensino_rede.docente_turma_rede dt using (id_turma_rede)" +
				" left join ensino_rede.docente_rede dr using (id_docente_rede)" +
				" left join comum.pessoa pe on (pe.id_pessoa=dr.id_pessoa)" +
				" where 1 = 1 " +
				(nivelEnsino != '0' ? " and d.nivel = '" + nivelEnsino + "' " : "") +
				(!siglaIes.equals("0") ? " and ies.sigla = '" + siglaIes + "' " : "") +
				(idCampusIes > 0 ? " and ci.id_campus =" + idCampusIes + " " : "") + 
				(idCurso > 0 ? " and dcr.id_curso_associado =" + idCurso + " " : "") + 
				(ano > 0 ? "and t.ano =" + ano : "") +
				(periodo > 0 ? "and t.periodo =" + periodo + " " : "") + 
				(!codigoDisciplina.equals("0") ? "and cc.codigo = '" + codigoDisciplina + "' " : "") +
				(!dataInicio.equals("") ? "and m.data_cadastro >= '" + dataInicio + "'" : "");
		
		@SuppressWarnings("unchecked")
		List <Object []> ls = getSession().createSQLQuery(sql).list();
		
		List <MatriculaComponenteRedeDTO> rs = new ArrayList <MatriculaComponenteRedeDTO> ();

		MatriculaComponenteRedeDTO m = null;
		
		int idAtual = 0;
		
		for (Object [] l : ls){
			int i = 0;
			
			
			int id = ((Number) l[i ++]).intValue();
			
			if(id != idAtual){
				idAtual = id;
				m = new MatriculaComponenteRedeDTO();
				m.setId(id);
				m.setNomeDiscente((String) l[i++]);
				m.setCpfDiscente((String) l[i++]);
				m.setEmailDiscente((String) l[i++]);
				m.setSiglaIes((String) l[i++]);
				m.setCampusIes((String) l[i++]);
				m.setNomeCurso((String) l[i++]);
				m.setIdComponente(((Number) l[i ++]).intValue());
				m.setCodigoComponente((String) l[i++]);
				m.setNomeComponente((String) l[i++]);
				m.setCodigoTurma((String) l[i++]);
				m.setSituacaoMatricula((String) l[i++]);
				m.setAno(((Number) l[i ++]).intValue());
				m.setPeriodo(((Number) l[i ++]).intValue());
				m.setDataInicio(l[i] != null ? ((Date) l[i]).getTime() : 0); i++;
				m.setDataFim(l[i] != null ? ((Date) l[i]).getTime() : 0); i++;
				
				String nomeDocente = (String) l[i++];
				if(ValidatorUtil.isEmpty(nomeDocente))
					nomeDocente = "";
				m.setNomesDocentes(new ArrayList<String>());
				m.getNomesDocentes().add(nomeDocente);
				
				String cpfdocente = (String) l[i++];
				if(ValidatorUtil.isEmpty(cpfdocente))
					cpfdocente = "0";
				m.setCpfsDocentes(new ArrayList<String>());
				m.getCpfsDocentes().add(cpfdocente);
				
				String emaildocente = (String) l[i++];
				if(ValidatorUtil.isEmpty(emaildocente))
					emaildocente = "0";
				m.setEmailsDocentes(new ArrayList<String>());
				m.getEmailsDocentes().add(emaildocente);
				
				rs.add(m);
			} else if (m != null) {
				String nomeDocente = (String) l[16];
				if(ValidatorUtil.isEmpty(nomeDocente))
					nomeDocente = "";
				m.getNomesDocentes().add(nomeDocente);
				
				String cpfdocente = (String) l[17];
				if(ValidatorUtil.isEmpty(cpfdocente))
					cpfdocente = "0";
				m.getCpfsDocentes().add(cpfdocente);
				
				String emaildocente = (String) l[18];
				if(ValidatorUtil.isEmpty(emaildocente))
					emaildocente = "0";
				m.getEmailsDocentes().add(emaildocente);
			}
				
		}
		
		return rs;
		
	}
	
	public List<DiscenteAssociadoDTO> findDadosPessoaisAlunosByCampusIes(char nivelEnsino, String siglaIes, int idCampusIes,	int idCurso) throws DAOException {
	
		String sql = "select d.id_discente_associado, trim(to_char(p.cpf_cnpj, '00000000000')) as cpfdiscente, p.nome_ascii, p.email, p.data_nascimento, p.sexo, sem_acento(coalesce(m.nome, p.municipio_naturalidade_outro)) as naturalidade, u.sigla as uf" +
				", p.numero_identidade, p.orgao_expedicao_identidade, ui.sigla as ufidentidade, p.data_expedicao_identidade, p.telefone_fixo, p.telefone_celular" +
				", sem_acento(e.logradouro || ', ' || e.numero || coalesce(' - ' || e.bairro, '') || coalesce(' - ' || me.nome, '') || coalesce(' - ' || ue.sigla, '')) as endereco, ies.sigla as ies, sem_acento(ci.nome) as campus, sem_acento(ca.nome) as curso" +
				" from ensino_rede.discente_associado d" +
				" join comum.pessoa p using (id_pessoa)" +
				" join ensino_rede.dados_curso_rede dcr on (d.id_dados_curso_rede = dcr.id_dados_curso_rede)" +
				" join ensino_rede.curso_associado ca on (ca.id_curso_associado = dcr.id_curso_associado)" +
				" join comum.campus_ies ci on (dcr.id_campus = ci.id_campus)" +
				" join comum.instituicoes_ensino ies on (ies.id = ci.id_ies)" +
				" left join comum.municipio m on (m.id_municipio = p.id_municipio_naturalidade)" +
				" left join comum.unidade_federativa u on (u.id_unidade_federativa = p.id_uf_naturalidade)" +
				" left join comum.unidade_federativa ui on (ui.id_unidade_federativa = p.id_uf_identidade)" +
				" left join comum.endereco e on (e.id_endereco = p.id_endereco_contato)" +
				" left join comum.municipio me on (me.id_municipio = e.id_municipio)" +
				" left join comum.unidade_federativa ue on (ue.id_unidade_federativa = e.id_unidade_federativa)" +
				" where d.id_status <> 3" +
				(nivelEnsino != '0' ? " and ca.nivel = '" + nivelEnsino + "' " : "") +
				(!siglaIes.equals("0") ? " and ies.sigla = '" + siglaIes + "' " : "") +
				(idCampusIes > 0 ? " and ci.id_campus =" + idCampusIes + " " : "") + 
				(idCurso > 0 ? " and ca.id_curso_associado =" + idCurso + " " : ""); 
		
		@SuppressWarnings("unchecked")
		List <Object []> ls = getSession().createSQLQuery(sql).list();
		
		List <DiscenteAssociadoDTO> rs = new ArrayList<DiscenteAssociadoDTO>();
		
		DiscenteAssociadoDTO d = null;
		
		for (Object [] l : ls){
			int i = 0;
			
			d = new DiscenteAssociadoDTO();
			d.setId(((Number) l[i ++]).intValue());
			d.setCpf((String) l[i++]);
			d.setNome((String) l[i++]);
			d.setEmail((String) l[i++]);
			d.setDataNascimento(l[i] != null ? ((Date) l[i]).getTime() : 0); i++;
			d.setSexo(((Character) l[i++]).toString());
			d.setMunicipioNaturalidade((String) l[i++]);
			d.setUfNaturalidade((String) l[i++]);
			d.setNumeroIdentidade((String) l[i++]);
			d.setOrgaoIdentidade((String) l[i++]);
			d.setUfIdentidade((String) l[i++]);
			d.setDataExpedicaoIdentidade(l[i] != null ? ((Date) l[i]).getTime() : 0); i++;
			d.setTelefoneFixo((String) l[i++]);
			d.setTelefoneCelular((String) l[i++]);
			d.setEndereco((String) l[i++]);
			d.setIes((String) l[i++]);
			d.setCampus((String) l[i++]);
			d.setCurso((String) l[i++]);
			
			rs.add(d);
		}
		
		return rs;
	}
	
	public List<DocenteRedeDTO> findDadosPessoaisDocentesByCampusIes(char nivelEnsino, String siglaIes, int idCampusIes,	int idCurso) throws DAOException {
		
		String sql = "select d.id_docente_rede, trim(to_char(p.cpf_cnpj, '00000000000')) as cpfdocente, p.nome, p.email, p.data_nascimento, p.sexo, coalesce(m.nome, p.municipio_naturalidade_outro) as naturalidade, u.sigla as uf" +
				", p.numero_identidade, p.orgao_expedicao_identidade, ui.sigla as ufidentidade, p.data_expedicao_identidade, p.telefone_fixo, p.telefone_celular" +
				", e.logradouro || ', ' || e.numero || coalesce(' - ' || e.bairro, '') || coalesce(' - ' || me.nome, '') || coalesce(' - ' || ue.sigla, '') as endereco, ies.sigla as ies, ci.nome as campus, ca.nome as curso" +
				" from ensino_rede.docente_rede d" +
				" join comum.pessoa p using (id_pessoa)" +
				" join ensino_rede.dados_curso_rede dcr on (dcr.id_dados_curso_rede = d.id_dados_curso_rede)" +
				" join ensino_rede.curso_associado ca on (ca.id_curso_associado = dcr.id_curso_associado)" +
				" join comum.campus_ies ci on (dcr.id_campus = ci.id_campus)" +
				" join comum.instituicoes_ensino ies on (ies.id = ci.id_ies)" +
				" left join comum.municipio m on (m.id_municipio = p.id_municipio_naturalidade)" +
				" left join comum.unidade_federativa u on (u.id_unidade_federativa = p.id_uf_naturalidade)" +
				" left join comum.unidade_federativa ui on (ui.id_unidade_federativa = p.id_uf_identidade)" +
				" left join comum.endereco e on (e.id_endereco = p.id_endereco_contato)" +
				" left join comum.municipio me on (me.id_municipio = e.id_municipio)" +
				" left join comum.unidade_federativa ue on (ue.id_unidade_federativa = e.id_unidade_federativa)" +
				" where 1 = 1" +
				(nivelEnsino != '0' ? " and ca.nivel = '" + nivelEnsino + "' " : "") +
				(!siglaIes.equals("0") ? " and ies.sigla = '" + siglaIes + "' " : "") +
				(idCampusIes > 0 ? " and ci.id_campus =" + idCampusIes + " " : "") + 
				(idCurso > 0 ? " and ca.id_curso_associado =" + idCurso + " " : ""); 
		
		@SuppressWarnings("unchecked")
		List <Object []> ls = getSession().createSQLQuery(sql).list();
		
		List <DocenteRedeDTO> rs = new ArrayList<DocenteRedeDTO>();
		
		DocenteRedeDTO d = null;
		
		for (Object [] l : ls){
			int i = 0;
			
			d = new DocenteRedeDTO();
			d.setId(((Number) l[i ++]).intValue());
			d.setCpf((String) l[i++]);
			d.setNome((String) l[i++]);
			d.setEmail((String) l[i++]);
			d.setDataNascimento(l[i] != null ? ((Date) l[i]).getTime() : 0); i++;
			d.setSexo(((Character) l[i++]).toString());
			d.setMunicipioNaturalidade((String) l[i++]);
			d.setUfNaturalidade((String) l[i++]);
			d.setNumeroIdentidade((String) l[i++]);
			d.setOrgaoIdentidade((String) l[i++]);
			d.setUfIdentidade((String) l[i++]);
			d.setDataExpedicaoIdentidade(l[i] != null ? ((Date) l[i]).getTime() : 0); i++;
			d.setTelefoneFixo((String) l[i++]);
			d.setTelefoneCelular((String) l[i++]);
			d.setEndereco((String) l[i++]);
			d.setIes((String) l[i++]);
			d.setCampus((String) l[i++]);
			d.setCurso((String) l[i++]);
			
			rs.add(d);
		}
		
		return rs;
	}
}
