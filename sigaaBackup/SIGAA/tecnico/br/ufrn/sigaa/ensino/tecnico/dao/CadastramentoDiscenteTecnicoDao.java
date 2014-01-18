/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 25/10/2012
 *
 */
package br.ufrn.sigaa.ensino.tecnico.dao;

import static br.ufrn.arq.util.UFRNUtils.gerarStringIn;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;

import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ensino.dominio.MovimentacaoAluno;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.ensino.metropoledigital.dominio.ReservaVagaCandidato;
import br.ufrn.sigaa.ensino.metropoledigital.dominio.ReservaVagaGrupo;
import br.ufrn.sigaa.ensino.metropoledigital.dominio.TutorIMD;
import br.ufrn.sigaa.ensino.tecnico.dominio.CancelamentoConvocacaoTecnico;
import br.ufrn.sigaa.ensino.tecnico.dominio.ConvocacaoProcessoSeletivoDiscenteTecnico;
import br.ufrn.sigaa.ensino.tecnico.dominio.ConvocacaoProcessoSeletivoTecnico;
import br.ufrn.sigaa.ensino.tecnico.dominio.DiscenteTecnico;
import br.ufrn.sigaa.ensino.tecnico.dominio.InscricaoProcessoSeletivoTecnico;
import br.ufrn.sigaa.ensino.tecnico.dominio.MotivoCancelamentoConvocacaoTecnico;
import br.ufrn.sigaa.ensino.tecnico.dominio.OpcaoPoloGrupo;
import br.ufrn.sigaa.ensino.tecnico.dominio.OpcaoPreCadastramentoTecnico;
import br.ufrn.sigaa.ensino.tecnico.dominio.ProcessoSeletivoTecnico;
import br.ufrn.sigaa.ensino.tecnico.dominio.ResultadoClassificacaoCandidatoTecnico;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * DAO com consultas utilizadas no cadastramento de discentes aprovados no processo seletivo técnico.
 * 
 * @author Leonardo Campos
 * @author Fred_Castro
 * 
 */
public class CadastramentoDiscenteTecnicoDao extends GenericSigaaDAO {
	
	/**
	 * Busca as convocações de discentes para a matriz curricular e o processo seletivo informados.
	 * @param idMatriz
	 * @param idProcessoSeletivo
	 * @param idStatusDiscente
	 * @return
	 * @throws DAOException
	 */
	public Collection<ConvocacaoProcessoSeletivoDiscenteTecnico> findConvocacoesByPSPoloNomeCpfConvocacao(int idProcessoSeletivo, int idOpcao, String nome, long cpf, int status, int idConvocacao, boolean consultarIndeferimentos) throws DAOException {
		StringBuilder sql = new StringBuilder("SELECT cpsd.id_convocacao_processo_seletivo_discente_tecnico," +
				" cd.descricao, d.id_discente," +
				" d.matricula, " +
				" pessoa.id_pessoa, " +
				" pessoa.cpf_cnpj, " +
				" pessoa.numero_identidade, " +
				" pessoa.nome, " +
				" pessoa.email, " +
				" pessoa.data_nascimento, " + 
				" d.status, " +
				" d.ano_ingresso," +
				" d.periodo_ingresso, " +
				" motivo.descricao as motivo, " +
				"(SELECT cc.id_cancelamento_convocacao" +
				" FROM tecnico.cancelamento_convocacao_tecnico cc " +
				" WHERE cc.id_convocacao_processo_seletivo_discente = cpsd.id_convocacao_processo_seletivo_discente_tecnico" +
				" and cc.id_registro_cadastro IS NOT NULL " +
				" order by cc.data_cancelamento desc" +
				" limit 1) as cancelamento, " +
				" r.classificacao_aprovado, i.reserva_vagas, i.id_opcao, o.descricao descricaoOpcao" +
				(consultarIndeferimentos ? ", c.data_cancelamento, c.observacoes, m.descricao" : "") +
				" FROM tecnico.convocacao_processo_seletivo_discente_tecnico cpsd " +
				" INNER JOIN tecnico.inscricao_processo_seletivo_tecnico i on i.id_inscricao_processo_seletivo_tecnico = cpsd.id_inscricao_processo_seletivo " +
				" INNER JOIN tecnico.opcao_polo_grupo o on o.id_opcao_polo_grupo = i.id_opcao " +
				" INNER JOIN tecnico.resultado_classificacao_candidato_tecnico r on r.id_inscricao_processo_seletivo_tecnico = i.id_inscricao_processo_seletivo_tecnico " +
				" INNER JOIN tecnico.convocacao_processo_seletivo_tecnico cd on cd.id_convocacao_processo_seletivo_tecnico = cpsd.id_convocacao_processo_seletivo"+
				(consultarIndeferimentos ? " JOIN tecnico.cancelamento_convocacao_tecnico c on c.id_convocacao_processo_seletivo_discente = cpsd.id_convocacao_processo_seletivo_discente_tecnico LEFT JOIN tecnico.motivo_cancelamento_convocacao_tecnico m on m.id_motivo_cancelamento_convocacao_tecnico = c.id_motivo_cancelamento_convocacao" : "") +
				" INNER JOIN discente d using (id_discente)" +
				" INNER JOIN comum.pessoa using (id_pessoa)" +
				" INNER JOIN tecnico.discente_tecnico dt on (d.id_discente = dt.id_discente)" +
				" LEFT JOIN tecnico.cancelamento_convocacao_tecnico cancelamento on cancelamento.id_convocacao_processo_seletivo_discente = cpsd.id_convocacao_processo_seletivo_discente_tecnico" +
				" LEFT JOIN tecnico.motivo_cancelamento_convocacao_tecnico motivo on (cancelamento.id_motivo_cancelamento_convocacao = motivo.id_motivo_cancelamento_convocacao_tecnico)" +
				" WHERE cd.id_processo_seletivo = :idProcessoSeletivo ");
		
		if (idOpcao > 0)
			sql.append(" and i.id_opcao = :opcao ");
		if (idConvocacao > 0)
			sql.append(" and cpsd.id_convocacao_processo_seletivo = :convocacao ");
		if (!StringUtils.isEmpty(nome))
			sql.append(" and pessoa.nome_ascii ilike :nome ");
		if (cpf > 0)
			sql.append(" and pessoa.cpf_cnpj = :cpf ");
		if (status > 0)
			sql.append(" and d.status = :status ");
		
		
		sql.append(" ORDER BY cd.id_convocacao_processo_seletivo_tecnico, d.periodo_ingresso, pessoa.nome");

		SQLQuery q = getSession().createSQLQuery(sql.toString());
		q.setInteger("idProcessoSeletivo", idProcessoSeletivo);
		
		if (idOpcao > 0)
			q.setInteger("opcao", idOpcao);
		if (idConvocacao > 0)
			q.setInteger("convocacao", idConvocacao);
		if (!StringUtils.isEmpty(nome))
			q.setString("nome", "%"+StringUtils.toAscii(nome.trim().toUpperCase())+"%");
		if (cpf > 0)
			q.setLong("cpf", cpf);
		if (status > 0)
			q.setLong("status", status);
		
		@SuppressWarnings("unchecked")
		List<Object[]> list = q.list();
		
		Collection<ConvocacaoProcessoSeletivoDiscenteTecnico> result = new ArrayList<ConvocacaoProcessoSeletivoDiscenteTecnico>();
		int col = 0;
		for (Object[] colunas : list) {
			Integer idConvocacaoNew = (Integer) colunas[col++];
			String descricaoConvocacao = (String) colunas[col++];
			Integer idDiscente = (Integer) colunas[col++];
			BigInteger matricula = (BigInteger) colunas[col++];
			Integer idPessoa = (Integer) colunas[col++];
			Long cpfNew = ((BigInteger) colunas[col++]).longValue();
			String rg = (String) colunas[col++];
			String nomeNew = (String) colunas[col++];
			String email = (String) colunas[col++];
			Date dataNascimento = (Date) colunas[col++];
			Short statusNew = (Short) colunas[col++];
			
			DiscenteTecnico d = new DiscenteTecnico();
			d.setId(idDiscente);
			d.getDiscente().setId(idDiscente);
			d.setMatricula((matricula != null ? matricula.longValue() : null));
			d.getDiscente().getPessoa().setId(idPessoa);
			d.getDiscente().getPessoa().setCpf_cnpj(cpfNew);
			d.getDiscente().getPessoa().setRegistroGeral(rg);
			d.getDiscente().getPessoa().setNome(nomeNew);
			d.getDiscente().getPessoa().setEmail(email);
			d.getDiscente().getPessoa().setDataNascimento(dataNascimento);
			d.setStatus((statusNew != null ? statusNew.intValue() : null));
			d.setAnoIngresso((Integer) colunas[col++]);
			d.setPeriodoIngresso((Integer) colunas[col++]);
			d.setOpcaoCadastramento(2);
			
			ConvocacaoProcessoSeletivoTecnico cd = new ConvocacaoProcessoSeletivoTecnico();
			cd.setDescricao(descricaoConvocacao);
			
			ConvocacaoProcessoSeletivoDiscenteTecnico c = new ConvocacaoProcessoSeletivoDiscenteTecnico(idConvocacaoNew);
			c.setDiscente(d);
			c.setConvocacaoProcessoSeletivo(cd);
			
			String motivo = (String) colunas[col++];
			
			Integer idCancelamento = (Integer) colunas[col++];
			if (idCancelamento != null) {
				c.setCancelamento( new CancelamentoConvocacaoTecnico() );
				c.getCancelamento().setId(idCancelamento);
				
				if (!StringUtils.isEmpty(motivo)){
					c.getCancelamento().setMotivo(new MotivoCancelamentoConvocacaoTecnico());
					c.getCancelamento().getMotivo().setDescricao(motivo);
				}
				
			} else if (d.getDiscente().isPendenteCadastro()){
				d.setOpcaoCadastramento(OpcaoPreCadastramentoTecnico.IGNORAR.ordinal());
			}
			
			InscricaoProcessoSeletivoTecnico insc = new InscricaoProcessoSeletivoTecnico();
			
			c.setResultado(new ResultadoClassificacaoCandidatoTecnico());
			c.getResultado().setClassificacaoAprovado((Integer) colunas[col++]);
			
			insc.setReservaVagas((Boolean) colunas[col++]);
			insc.setOpcao(new OpcaoPoloGrupo());
			insc.getOpcao().setId((Integer) colunas[col++]);
			insc.getOpcao().setDescricao((String) colunas[col++]);
			
			c.setInscricaoProcessoSeletivo(insc);
			
			result.add(c);
			col = 0;
		}
		return result;
	}
	
	
	
	/**
	 * Busca as convocações de discentes para a matriz curricular e o processo seletivo informados.
	 * 
	 * @param idMatriz, idProcessoSeletivo, idStatusDiscente
	 * @return
	 * @throws DAOException
	 */
	public Collection<ConvocacaoProcessoSeletivoDiscenteTecnico> findConvocacoesByPSPoloNomeCpfConvocacaoNew(int idProcessoSeletivo, int idOpcao, String nome, long cpf, int status, int idConvocacao, boolean consultarIndeferimentos, Integer idGrupoReservaVaga) throws DAOException {
		StringBuilder sql = new StringBuilder("SELECT cpsd.id_convocacao_processo_seletivo_discente_tecnico," +
				" cd.descricao, d.id_discente," +
				" d.matricula, " +
				" pessoa.id_pessoa, " +
				" pessoa.cpf_cnpj, " +
				" pessoa.numero_identidade, " +
				" pessoa.nome, " +
				" pessoa.email, " +
				" pessoa.data_nascimento, " + 
				" rvg.id_reserva_vaga_grupo, " +
				" rvg.denominacao, " +
				" rvg.descricao," +
				" d.status, " +
				" d.ano_ingresso," +
				" d.periodo_ingresso, " +
				" motivo.descricao as motivo, " +
				"(SELECT cc.id_cancelamento_convocacao" +
				" FROM tecnico.cancelamento_convocacao_tecnico cc " +
				" WHERE cc.id_convocacao_processo_seletivo_discente = cpsd.id_convocacao_processo_seletivo_discente_tecnico" +
				" and cc.id_registro_cadastro IS NOT NULL " +
				" order by cc.data_cancelamento desc" +
				" limit 1) as cancelamento, " +
				" r.classificacao_aprovado, i.reserva_vagas, i.id_opcao, o.descricao descricaoOpcao" +
				(consultarIndeferimentos ? ", c.data_cancelamento, c.observacoes, m.descricao" : "") +
				" FROM tecnico.convocacao_processo_seletivo_discente_tecnico cpsd " +
				" INNER JOIN tecnico.inscricao_processo_seletivo_tecnico i on i.id_inscricao_processo_seletivo_tecnico = cpsd.id_inscricao_processo_seletivo " +
				" INNER JOIN metropole_digital.reserva_vaga_candidato rvc on rvc.id_inscricao_processo_seletivo_tecnico = i.id_inscricao_processo_seletivo_tecnico " +
				" INNER JOIN metropole_digital.reserva_vaga_grupo rvg on rvg.id_reserva_vaga_grupo = rvc.id_reserva_vaga_grupo " +
				" INNER JOIN tecnico.opcao_polo_grupo o on o.id_opcao_polo_grupo = i.id_opcao " +
				" INNER JOIN tecnico.resultado_classificacao_candidato_tecnico r on r.id_inscricao_processo_seletivo_tecnico = i.id_inscricao_processo_seletivo_tecnico " +
				" INNER JOIN tecnico.convocacao_processo_seletivo_tecnico cd on cd.id_convocacao_processo_seletivo_tecnico = cpsd.id_convocacao_processo_seletivo"+
				(consultarIndeferimentos ? " JOIN tecnico.cancelamento_convocacao_tecnico c on c.id_convocacao_processo_seletivo_discente = cpsd.id_convocacao_processo_seletivo_discente_tecnico LEFT JOIN tecnico.motivo_cancelamento_convocacao_tecnico m on m.id_motivo_cancelamento_convocacao_tecnico = c.id_motivo_cancelamento_convocacao" : "") +
				" INNER JOIN discente d using (id_discente)" +
				" INNER JOIN comum.pessoa using (id_pessoa)" +
				" INNER JOIN tecnico.discente_tecnico dt on (d.id_discente = dt.id_discente)" +
				" LEFT JOIN tecnico.cancelamento_convocacao_tecnico cancelamento on cancelamento.id_convocacao_processo_seletivo_discente = cpsd.id_convocacao_processo_seletivo_discente_tecnico" +
				" LEFT JOIN tecnico.motivo_cancelamento_convocacao_tecnico motivo on (cancelamento.id_motivo_cancelamento_convocacao = motivo.id_motivo_cancelamento_convocacao_tecnico)" +
				" WHERE cd.id_processo_seletivo = :idProcessoSeletivo ");
		
		if (idOpcao > 0) {
			sql.append(" and i.id_opcao = :opcao ");
		}
		if (idConvocacao > 0) {
			sql.append(" and cpsd.id_convocacao_processo_seletivo = :convocacao ");
		}
		if (!StringUtils.isEmpty(nome)){
			sql.append(" and pessoa.nome_ascii ilike :nome ");
		}
		if (cpf > 0) {
			sql.append(" and pessoa.cpf_cnpj = :cpf ");
		}
		if (status > 0) {
			sql.append(" and d.status = :status ");
		}
		if (idGrupoReservaVaga != null) {
			if (idGrupoReservaVaga > 0) {
				sql.append(" and rvg.id_reserva_vaga_grupo = :idGrupoReservaVaga ");
			}
		}
		
		
		sql.append(" ORDER BY cd.id_convocacao_processo_seletivo_tecnico, d.periodo_ingresso, pessoa.nome");

		SQLQuery q = getSession().createSQLQuery(sql.toString());
		q.setInteger("idProcessoSeletivo", idProcessoSeletivo);
		
		if (idOpcao > 0) {
			q.setInteger("opcao", idOpcao);
		}
		if (idConvocacao > 0) {
			q.setInteger("convocacao", idConvocacao);
		}
		if (!StringUtils.isEmpty(nome)) {
			q.setString("nome", "%"+StringUtils.toAscii(nome.trim().toUpperCase())+"%");
		}
		if (cpf > 0) {
			q.setLong("cpf", cpf);
		}
		if (status > 0) {
			q.setLong("status", status);
		}
		if (idGrupoReservaVaga != null) {
			if (idGrupoReservaVaga > 0) {
				q.setLong("idGrupoReservaVaga", idGrupoReservaVaga);
			}
		}
		
		@SuppressWarnings("unchecked")
		List<Object[]> list = q.list();
		
		Collection<ConvocacaoProcessoSeletivoDiscenteTecnico> result = new ArrayList<ConvocacaoProcessoSeletivoDiscenteTecnico>();
		int col = 0;
		for (Object[] colunas : list) {
			Integer idConvocacaoNew = (Integer) colunas[col++];
			String descricaoConvocacao = (String) colunas[col++];
			Integer idDiscente = (Integer) colunas[col++];
			BigInteger matricula = (BigInteger) colunas[col++];
			Integer idPessoa = (Integer) colunas[col++];
			Long cpfNew = ((BigInteger) colunas[col++]).longValue();
			String rg = (String) colunas[col++];
			String nomeNew = (String) colunas[col++];
			String email = (String) colunas[col++];
			Date dataNascimento = (Date) colunas[col++];
			Integer idReservaVagaGrupo = (Integer) colunas[col++];
			String reservaVagaGrupoDenominacao = (String) colunas[col++];
			String descricaoGrupo = (String) colunas[col++];
			Short statusNew = (Short) colunas[col++];
			
			DiscenteTecnico d = new DiscenteTecnico();
			d.setId(idDiscente);
			d.getDiscente().setId(idDiscente);
			d.setMatricula((matricula != null ? matricula.longValue() : null));
			d.getDiscente().getPessoa().setId(idPessoa);
			d.getDiscente().getPessoa().setCpf_cnpj(cpfNew);
			d.getDiscente().getPessoa().setRegistroGeral(rg);
			d.getDiscente().getPessoa().setNome(nomeNew);
			d.getDiscente().getPessoa().setEmail(email);
			d.getDiscente().getPessoa().setDataNascimento(dataNascimento);
			d.setStatus((statusNew != null ? statusNew.intValue() : null));
			d.setAnoIngresso((Integer) colunas[col++]);
			d.setPeriodoIngresso((Integer) colunas[col++]);
			d.setOpcaoCadastramento(2);
			
			ConvocacaoProcessoSeletivoTecnico cd = new ConvocacaoProcessoSeletivoTecnico();
			cd.setDescricao(descricaoConvocacao);
			
			ConvocacaoProcessoSeletivoDiscenteTecnico c = new ConvocacaoProcessoSeletivoDiscenteTecnico(idConvocacaoNew);
			c.setDiscente(d);
			c.setConvocacaoProcessoSeletivo(cd);
			
			String motivo = (String) colunas[col++];
			
			Integer idCancelamento = (Integer) colunas[col++];
			if (idCancelamento != null) {
				c.setCancelamento( new CancelamentoConvocacaoTecnico() );
				c.getCancelamento().setId(idCancelamento);
				
				if (!StringUtils.isEmpty(motivo)){
					c.getCancelamento().setMotivo(new MotivoCancelamentoConvocacaoTecnico());
					c.getCancelamento().getMotivo().setDescricao(motivo);
				}
				
			} else if (d.getDiscente().isPendenteCadastro()){
				d.setOpcaoCadastramento(OpcaoPreCadastramentoTecnico.IGNORAR.ordinal());
			}
			
			InscricaoProcessoSeletivoTecnico insc = new InscricaoProcessoSeletivoTecnico();
			
			c.setResultado(new ResultadoClassificacaoCandidatoTecnico());
			c.getResultado().setClassificacaoAprovado((Integer) colunas[col++]);
			
			insc.setReservaVagas((Boolean) colunas[col++]);
			insc.setOpcao(new OpcaoPoloGrupo());
			insc.getOpcao().setId((Integer) colunas[col++]);
			insc.getOpcao().setDescricao((String) colunas[col++]);
						
			ReservaVagaGrupo grupo = new ReservaVagaGrupo();
			grupo.setId(idReservaVagaGrupo);
			grupo.setDenominacao(reservaVagaGrupoDenominacao);
			grupo.setDescricao(descricaoGrupo);
			
			insc.setGrupo(grupo);
			
			c.setInscricaoProcessoSeletivo(insc);
			
			result.add(c);
			col = 0;
		}
		return result;
	}
	
	/**
	 * Busca as convocações de discentes por ID
	 * 
	 * @param idConvocacaoDiscente
	 * @return
	 * @throws DAOException
	 */
	public ConvocacaoProcessoSeletivoDiscenteTecnico findConvocacaoDiscenteById (int idConvocacaoDiscente) throws DAOException {
		StringBuilder sql = new StringBuilder("SELECT cpsd.id_convocacao_processo_seletivo_discente_tecnico," +
				" cd.descricao, d.id_discente," +
				" d.matricula, " +
				" pessoa.id_pessoa, " +
				" pessoa.cpf_cnpj, " +
				" pessoa.numero_identidade, " +
				" pessoa.nome, " +
				" pessoa.email, " +
				" pessoa.data_nascimento, " + 
				" d.status, " +
				" d.ano_ingresso," +
				" d.periodo_ingresso, " +
				"(SELECT cc.id_cancelamento_convocacao" +
				" FROM tecnico.cancelamento_convocacao_tecnico cc " +
				" WHERE cc.id_convocacao_processo_seletivo_discente = cpsd.id_convocacao_processo_seletivo_discente_tecnico" +
				" and cc.id_registro_cadastro IS NOT NULL " +
				" order by cc.data_cancelamento desc" +
				" limit 1) as cancelamento, " +
				" r.classificacao_aprovado, i.reserva_vagas, i.id_opcao, o.descricao descricaoOpcao " +
				" FROM tecnico.convocacao_processo_seletivo_discente_tecnico cpsd " +
				" INNER JOIN tecnico.inscricao_processo_seletivo_tecnico i on i.id_inscricao_processo_seletivo_tecnico = cpsd.id_inscricao_processo_seletivo " +
				" INNER JOIN tecnico.opcao_polo_grupo o on o.id_opcao_polo_grupo = i.id_opcao " +
				" INNER JOIN tecnico.resultado_classificacao_candidato_tecnico r on r.id_inscricao_processo_seletivo_tecnico = i.id_inscricao_processo_seletivo_tecnico " +
				" INNER JOIN tecnico.convocacao_processo_seletivo_tecnico cd on cd.id_convocacao_processo_seletivo_tecnico = cpsd.id_convocacao_processo_seletivo"+
				" INNER JOIN discente d using (id_discente)" +
				" INNER JOIN comum.pessoa using (id_pessoa)" +
				" INNER JOIN tecnico.discente_tecnico dt on (d.id_discente = dt.id_discente)" +
				" WHERE cpsd.id_convocacao_processo_seletivo_discente_tecnico = :idConvocacaoDiscente");
		
		SQLQuery q = getSession().createSQLQuery(sql.toString());
		q.setInteger("idConvocacaoDiscente", idConvocacaoDiscente);
		
		Object [] colunas = (Object[]) q.uniqueResult();
		
		int col = 0;
		Integer idConvocacaoNew = (Integer) colunas[col++];
		String descricaoConvocacao = (String) colunas[col++];
		Integer idDiscente = (Integer) colunas[col++];
		BigInteger matricula = (BigInteger) colunas[col++];
		Integer idPessoa = (Integer) colunas[col++];
		Long cpfNew = ((BigInteger) colunas[col++]).longValue();
		String rg = (String) colunas[col++];
		String nomeNew = (String) colunas[col++];
		String email = (String) colunas[col++];
		Date dataNascimento = (Date) colunas[col++];
		Short statusNew = (Short) colunas[col++];
		
		DiscenteTecnico d = new DiscenteTecnico();
		d.setId(idDiscente);
		d.getDiscente().setId(idDiscente);
		d.setMatricula((matricula != null ? matricula.longValue() : null));
		d.getDiscente().getPessoa().setId(idPessoa);
		d.getDiscente().getPessoa().setCpf_cnpj(cpfNew);
		d.getDiscente().getPessoa().setRegistroGeral(rg);
		d.getDiscente().getPessoa().setNome(nomeNew);
		d.getDiscente().getPessoa().setEmail(email);
		d.getDiscente().getPessoa().setDataNascimento(dataNascimento);
		d.setStatus((statusNew != null ? statusNew.intValue() : null));
		d.setAnoIngresso((Integer) colunas[col++]);
		d.setPeriodoIngresso((Integer) colunas[col++]);
		d.setOpcaoCadastramento(2);
		
		ConvocacaoProcessoSeletivoTecnico cd = new ConvocacaoProcessoSeletivoTecnico();
		cd.setDescricao(descricaoConvocacao);
		
		ConvocacaoProcessoSeletivoDiscenteTecnico c = new ConvocacaoProcessoSeletivoDiscenteTecnico(idConvocacaoNew);
		c.setDiscente(d);
		c.setConvocacaoProcessoSeletivo(cd);
		
		Integer idCancelamento = (Integer) colunas[col++];
		if (idCancelamento != null) {
			c.setCancelamento( new CancelamentoConvocacaoTecnico() );
			c.getCancelamento().setId(idCancelamento);
		} else if (d.getDiscente().isPendenteCadastro()){
			d.setOpcaoCadastramento(OpcaoPreCadastramentoTecnico.IGNORAR.ordinal());
		}
		
		InscricaoProcessoSeletivoTecnico insc = new InscricaoProcessoSeletivoTecnico();
		
		c.setResultado(new ResultadoClassificacaoCandidatoTecnico());
		c.getResultado().setClassificacaoAprovado((Integer) colunas[col++]);
		
		insc.setReservaVagas((Boolean) colunas[col++]);
		insc.setOpcao(new OpcaoPoloGrupo());
		insc.getOpcao().setId((Integer) colunas[col++]);
		insc.getOpcao().setDescricao((String) colunas[col++]);
		
		c.setInscricaoProcessoSeletivo(insc);
		
		return c;
	}
	
	/**
	 * Retorna os discentes vinculados à pessoa informada com o nível e status
	 * informado
	 *
	 * @param idPessoa
	 * @param nivel
	 * @param status
	 * @return
	 * @throws DAOException
	 */
	public Collection<DiscenteTecnico> findByPessoaSituacao(int idPessoa, Integer... status) throws DAOException {
		
		String projecao = "id, discente.curso.id  ";
		try {
			StringBuilder hql = new StringBuilder();
			hql.append("select " + projecao + 
					" from DiscenteTecnico where discente.pessoa.id = " + idPessoa);

			if (status != null) {
				hql.append(" and discente.status in " + gerarStringIn(status));
			}

			Query q = getSession().createQuery(hql.toString());
			@SuppressWarnings("unchecked")
			Collection<DiscenteTecnico> lista = HibernateUtils.parseTo(q.list(), projecao, DiscenteGraduacao.class );
			return lista;
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}
	
	/**
	 * Retorna um mapa com o id de cada discente e sua respectiva movimentação de cancelamento, se houver.
	 * @param discentes
	 * @return
	 * @throws DAOException
	 */
	public Map<Integer, MovimentacaoAluno> findMapaMovimentacoesCancelamento(Collection<ConvocacaoProcessoSeletivoDiscenteTecnico> convocacoes) throws DAOException {
		Collection<Discente> discentes = new ArrayList<Discente>();
		for(ConvocacaoProcessoSeletivoDiscenteTecnico c: convocacoes)
			discentes.add(c.getDiscente().getDiscente());
		
		String hql = "SELECT m.id, m.discente.id FROM MovimentacaoAluno m WHERE m.discente.id in " + UFRNUtils.gerarStringIn(discentes);
		
		@SuppressWarnings("unchecked")
		List<Object[]> list = getSession().createQuery(hql).list();
		
		Map<Integer, MovimentacaoAluno> result = new HashMap<Integer, MovimentacaoAluno>();
		
		int col = 0;
		for (Object[] colunas : list) {
			MovimentacaoAluno m = new MovimentacaoAluno((Integer) colunas[col++]);
			result.put((Integer) colunas[col++], m);
			col = 0;
		}
		
		return result;
	}
	
	/**
	 * Atualiza os status dos discentes passados como argumento com o valor do status informado.
	 * @param discentes
	 * @param status
	 * @throws DAOException
	 */
	public void updateStatusDiscentes(Collection<Discente> discentes, int status) throws DAOException {
		getSession().createQuery("UPDATE Discente SET status = :status WHERE id in "+ UFRNUtils.gerarStringIn(discentes)).setInteger("status", status).executeUpdate();
	}

	/**
	 * Gera o relatório geral de classificação analítico
	 * 
	 * @param idProcessoSeletivo, opcao, ordenacaoRelatorio
	 * @return
	 * @throws DAOException
	 */
	public List <Object[]> relatorioGeralDeClassificacaoAnalitico (int idProcessoSeletivo, int opcao, int ordenacaoRelatorio) throws DAOException {
		
		String ordem = "o.descricao,";
		switch (ordenacaoRelatorio){
			case 0: ordem += "c.classificacao_aprovado"; break;
			case 1: ordem += "p.nome"; break;
		}
		
		
		String sql = "select c.classificacao_aprovado, c.argumento_final, p.nome, i.reserva_vagas, o.descricao " +
				" from tecnico.resultado_classificacao_candidato_tecnico c " +
				" join tecnico.inscricao_processo_seletivo_tecnico i using (id_inscricao_processo_seletivo_tecnico) " +
				" join tecnico.pessoa_tecnico p on p.id_pessoa = i.id_pessoa_tecnico " +
				" join tecnico.opcao_polo_grupo o on o.id_opcao_polo_grupo = i.id_opcao " +
				" where i.id_processo_seletivo_tecnico = " + idProcessoSeletivo + " ";
				
		if (opcao > 0 )
			sql += " and i.id_opcao = " + opcao;
		
		sql += " order by " + ordem;
		
		@SuppressWarnings("unchecked")
		List <Object []> rs = getSession().createSQLQuery (sql).list();
		
		return rs;
	}
	
	/**
	 * Gera o relatório geral de classificação analítico por grupos
	 * 
	 * @param idProcessoSeletivo, opcao, ordenacaoRelatorio
	 * @return
	 * @throws DAOException
	 */
	public List <Object[]> relatorioGeralDeClassificacaoAnaliticoPorGrupos (int idProcessoSeletivo, int opcao, int ordenacaoRelatorio, int idGrupo) throws DAOException {
		
		String ordem = "o.descricao,";
		switch (ordenacaoRelatorio){
			case 0: ordem += "c.classificacao_aprovado"; break;
			case 1: ordem += "p.nome"; break;
		}
		
		
		String sql = "select c.classificacao_aprovado, c.argumento_final, p.nome, i.reserva_vagas, o.descricao, rvg.denominacao " +
				" from tecnico.resultado_classificacao_candidato_tecnico c " +
				" join tecnico.inscricao_processo_seletivo_tecnico i using (id_inscricao_processo_seletivo_tecnico) " +
				" join tecnico.pessoa_tecnico p on p.id_pessoa = i.id_pessoa_tecnico " +
				" join tecnico.opcao_polo_grupo o on o.id_opcao_polo_grupo = i.id_opcao " +
				" join metropole_digital.reserva_vaga_candidato rvc on rvc.id_inscricao_processo_seletivo_tecnico = i.id_inscricao_processo_seletivo_tecnico " +
				" join metropole_digital.reserva_vaga_grupo rvg on rvg.id_reserva_vaga_grupo = rvc.id_reserva_vaga_grupo " +
				" where i.id_processo_seletivo_tecnico = " + idProcessoSeletivo + " ";
				
		if (opcao > 0 ) {
			sql += " and i.id_opcao = " + opcao;
		}
		
		if (idGrupo > 0 ) {
			sql += " and rvg.id_reserva_vaga_grupo = " + idGrupo;
		}
		
		sql += " order by " + ordem;
		
		@SuppressWarnings("unchecked")
		List <Object []> rs = getSession().createSQLQuery (sql).list();
		
		return rs;
	}

	/**
	 * Gera o relatório geral de classificação sintético
	 * 
	 * @param idProcessoSeletivo, opcao
	 * @return
	 * @throws DAOException
	 */
	public List <Object[]> relatorioGeralDeClassificacaoSintetico (int idProcessoSeletivo, int opcao) throws DAOException {
		String sql = "select o.descricao, count(case when i.reserva_vagas = true then 1 end) as com_reserva, count(case when i.reserva_vagas = false then 1 end) as sem_reserva " +
				" from tecnico.resultado_classificacao_candidato_tecnico c " +
				" join tecnico.inscricao_processo_seletivo_tecnico i using (id_inscricao_processo_seletivo_tecnico) " +
				" join tecnico.pessoa_tecnico p on p.id_pessoa = i.id_pessoa_tecnico " +
				" join tecnico.opcao_polo_grupo o on o.id_opcao_polo_grupo = i.id_opcao " +
				" where i.id_processo_seletivo_tecnico = " + idProcessoSeletivo + " ";
				
		if (opcao > 0)
			sql += " and i.id_opcao = " + opcao;
		
		sql += " group by o.descricao order by o.descricao";
		
		@SuppressWarnings("unchecked")
		List <Object []> rs = getSession().createSQLQuery (sql).list();
		
		return rs;
	}
	
	/**
	 * Gera o relatório geral de classificação sintético por divisão de grupos
	 * 
	 * @param idProcessoSeletivo, opcao
	 * @return
	 * @throws DAOException
	 */
	public List<Object[]> relatorioGeralDeClassificacaoSinteticoPorGrupos (int idProcessoSeletivo, int opcao, int idGrupo) throws DAOException {

		
		String projecao = " op.descricao, rvg.denominacao, COUNT(rvg.id) ";
		
		//CONSULTA GERAL
		String hql = "SELECT " + projecao +
				" FROM ResultadoClassificacaoCandidatoTecnico res, InscricaoProcessoSeletivoTecnico ips, " +
				" PessoaTecnico pes, OpcaoPoloGrupo op, ReservaVagaCandidato rvc, ReservaVagaGrupo rvg " +
				" WHERE ips.id = res.inscricaoProcessoSeletivo.id AND pes.id = ips.pessoa.id AND " +
				" ips.opcao.id = op.id AND rvc.candidato.id = ips.id AND rvg.id = rvc.tipo.id AND ips.processoSeletivo.id = " + idProcessoSeletivo; 
		
		if (opcao > 0) {
			hql += " AND op.id = " + opcao;
		}
		
		if (idGrupo > 0) {
			hql += " AND rvg.id = " + idGrupo;
		}
		
		hql +=  " GROUP BY op.descricao, rvg.denominacao " + 
				" ORDER BY op.descricao, rvg.denominacao";
		
		Query q = getSession().createQuery(hql);
		
		@SuppressWarnings("unchecked")
		List<Object[]> res = q.list();
		
		return res;
	}
	
	/**
	 * Gera o relatório geral de convocação
	 * 
	 * @param idProcessoSeletivo, idConvocacao, opcao
	 * @return
	 * @throws DAOException
	 */
	public List <Object[]> relatorioDeConvocacao (int idProcessoSeletivo, int idConvocacao, int opcao) throws DAOException {
		
		String sql = "select conv.descricao, c.classificacao_aprovado, c.argumento_final, p.nome, p.cpf_cnpj, i.reserva_vagas, o.descricao as opcao " +
				" from tecnico.convocacao_processo_seletivo_tecnico conv " +
				" join tecnico.convocacao_processo_seletivo_discente_tecnico convd on convd.id_convocacao_processo_seletivo = conv.id_convocacao_processo_seletivo_tecnico " +
				" join tecnico.inscricao_processo_seletivo_tecnico i on i.id_inscricao_processo_seletivo_tecnico = convd.id_inscricao_processo_seletivo " +
				" join tecnico.resultado_classificacao_candidato_tecnico c using (id_inscricao_processo_seletivo_tecnico) " +
				" join tecnico.pessoa_tecnico p on p.id_pessoa = i.id_pessoa_tecnico " +
				" join tecnico.opcao_polo_grupo o on o.id_opcao_polo_grupo = i.id_opcao " +
				" where i.id_processo_seletivo_tecnico = " + idProcessoSeletivo + " ";
		
		if (idConvocacao > 0)
			sql += " and conv.id_convocacao_processo_seletivo_tecnico = "+idConvocacao+" ";
		
		if (opcao > 0)
			sql += " and i.id_opcao = "+opcao+" ";
		
		sql += " order by conv.descricao, o.descricao, c.classificacao_aprovado";
		
		@SuppressWarnings("unchecked")
		List <Object []> rs = getSession().createSQLQuery (sql).list();
		
		return rs;
	}
	
	
	/**
	 * Gera o relatório geral de convocação por grupos de reserva de vagas
	 * 
	 * @param idProcessoSeletivo, idConvocacao, opcao, idGrupo
	 * @return
	 * @throws DAOException
	 */
	public List <Object[]> relatorioDeConvocacaoPorGrupos (int idProcessoSeletivo, int idConvocacao, int opcao, int idGrupo) throws DAOException {
		
		String sql = "select conv.descricao, c.classificacao_aprovado, c.argumento_final, p.nome, p.cpf_cnpj, i.reserva_vagas, o.descricao as opcao, rvg.denominacao " +
				" from tecnico.convocacao_processo_seletivo_tecnico conv " +
				" join tecnico.convocacao_processo_seletivo_discente_tecnico convd on convd.id_convocacao_processo_seletivo = conv.id_convocacao_processo_seletivo_tecnico " +
				" join tecnico.inscricao_processo_seletivo_tecnico i on i.id_inscricao_processo_seletivo_tecnico = convd.id_inscricao_processo_seletivo " +
				" join tecnico.resultado_classificacao_candidato_tecnico c using (id_inscricao_processo_seletivo_tecnico) " +
				" join tecnico.pessoa_tecnico p on p.id_pessoa = i.id_pessoa_tecnico " +
				" join tecnico.opcao_polo_grupo o on o.id_opcao_polo_grupo = i.id_opcao " +
				" join metropole_digital.reserva_vaga_candidato rvc on rvc.id_inscricao_processo_seletivo_tecnico = i.id_inscricao_processo_seletivo_tecnico " +
				" join metropole_digital.reserva_vaga_grupo rvg on rvc.id_reserva_vaga_grupo = rvg.id_reserva_vaga_grupo " +
				" where i.id_processo_seletivo_tecnico = " + idProcessoSeletivo + " ";
		
		if (idConvocacao > 0){
			sql += " and conv.id_convocacao_processo_seletivo_tecnico = "+idConvocacao+" ";
		}
		
		if (opcao > 0){
			sql += " and i.id_opcao = "+opcao+" ";
		}
		
		if (idGrupo > 0){
			sql += " and rvg.id_reserva_vaga_grupo = " + idGrupo + " ";
		}
		
		sql += " order by o.descricao, conv.descricao, c.classificacao_aprovado";
		
		@SuppressWarnings("unchecked")
		List <Object []> rs = getSession().createSQLQuery (sql).list();
		
		return rs;
	}
	
	
	/**
	 * Gera o relatório geral de cadastramento
	 * 
	 * @param idProcessoSeletivo, idConvocacao, opcao, reservaVagas
	 * @return
	 * @throws DAOException
	 */
	public List <Object[]> relatorioDeCadastramento (int idProcessoSeletivo, int opcao, int status, Boolean reservaVagas) throws DAOException {
		
		String sql = "select conv.descricao, p.nome, p.cpf_cnpj, i.reserva_vagas, o.descricao as opcao, d.status " +
				" from tecnico.convocacao_processo_seletivo_tecnico conv " +
				" join tecnico.convocacao_processo_seletivo_discente_tecnico convd on convd.id_convocacao_processo_seletivo = conv.id_convocacao_processo_seletivo_tecnico " +
				" join tecnico.discente_tecnico dt on dt.id_discente = convd.id_discente " +
				" join discente d on d.id_discente = dt.id_discente " +
				" join tecnico.inscricao_processo_seletivo_tecnico i on i.id_inscricao_processo_seletivo_tecnico = convd.id_inscricao_processo_seletivo " +
				" join tecnico.resultado_classificacao_candidato_tecnico c using (id_inscricao_processo_seletivo_tecnico) " +
				" join tecnico.pessoa_tecnico p on p.id_pessoa = i.id_pessoa_tecnico " +
				" join tecnico.opcao_polo_grupo o on o.id_opcao_polo_grupo = i.id_opcao " +
				" where i.id_processo_seletivo_tecnico = " + idProcessoSeletivo + " ";
		
		
		if (opcao > 0)
			sql += " and i.id_opcao = "+opcao+" ";
		
		if (status > 0)
			sql += " and d.status = "+status+" ";
		
		if (reservaVagas != null)
			sql += " and i.reserva_vagas = "+(reservaVagas  ? "true" : "false")+" ";
		
		sql += " order by conv.descricao, o.descricao, c.classificacao_aprovado";
		
		@SuppressWarnings("unchecked")
		List <Object []> rs = getSession().createSQLQuery (sql).list();
		
		for (Object [] r : rs)
			r[5] = StatusDiscente.getDescricao(((Short)r[5]).intValue());
				
		return rs;
	}
	
	/**
	 * Gera o relatório geral de cadastramento
	 * 
	 * @param idProcessoSeletivo, idConvocacao, opcao, reservaVagas
	 * @return
	 * @throws DAOException
	 */
	public List <Object[]> relatorioDeCadastramentoPorGrupos (int idProcessoSeletivo, int opcao, int status, int idGrupo) throws DAOException {
		
		String sql = "select conv.descricao, p.nome, p.cpf_cnpj, i.reserva_vagas, o.descricao as opcao, d.status, rvg.denominacao " +
				" from tecnico.convocacao_processo_seletivo_tecnico conv " +
				" join tecnico.convocacao_processo_seletivo_discente_tecnico convd on convd.id_convocacao_processo_seletivo = conv.id_convocacao_processo_seletivo_tecnico " +
				" join tecnico.discente_tecnico dt on dt.id_discente = convd.id_discente " +
				" join discente d on d.id_discente = dt.id_discente " +
				" join tecnico.inscricao_processo_seletivo_tecnico i on i.id_inscricao_processo_seletivo_tecnico = convd.id_inscricao_processo_seletivo " +
				" join tecnico.resultado_classificacao_candidato_tecnico c using (id_inscricao_processo_seletivo_tecnico) " +
				" join tecnico.pessoa_tecnico p on p.id_pessoa = i.id_pessoa_tecnico " +
				" join tecnico.opcao_polo_grupo o on o.id_opcao_polo_grupo = i.id_opcao " +
				" join metropole_digital.reserva_vaga_candidato rvc on rvc.id_inscricao_processo_seletivo_tecnico = i.id_inscricao_processo_seletivo_tecnico " +
				" join metropole_digital.reserva_vaga_grupo rvg on rvg.id_reserva_vaga_grupo = rvc.id_reserva_vaga_grupo " +
				" where i.id_processo_seletivo_tecnico = " + idProcessoSeletivo + " ";
		
		
		if (opcao > 0) {
			sql += " and i.id_opcao = "+opcao+" ";
		}
		
		if (status > 0) {
			sql += " and d.status = "+status+" ";
		}
		
		if (idGrupo > 0) {
			sql += " and rvg.id_reserva_vaga_grupo = " + idGrupo + " ";
		}
		
		sql += " order by o.descricao, conv.descricao, c.classificacao_aprovado";
		
		@SuppressWarnings("unchecked")
		List <Object []> rs = getSession().createSQLQuery (sql).list();
		
		for (Object [] r : rs) {
			r[5] = StatusDiscente.getDescricao(((Short)r[5]).intValue());
		}
				
		return rs;
	}

	/**
	 * Gerar o relatório de candidatos ordenado por nome
	 * 
	 * @param nome
	 * @return
	 * @throws DAOException
	 */
	public List <Object[]> findCandidatosByNome(String nome) throws DAOException {
		String sql = "select p.nome, p.cpf_cnpj from tecnico.pessoa_tecnico p where p.nome_ascii ilike :nome";
		Query q = getSession().createSQLQuery (sql);
		q.setString("nome", "%"+StringUtils.toAscii(nome.trim().toUpperCase())+"%");

		@SuppressWarnings("unchecked")
		List <Object []> rs = q.list();
		
		return rs;
	}
	
	/**
	 * Lista a inscrição a partir do idPessoa e do ano PS
	 * 
	 * @param idPessoa, anoPS
	 * @return InscricaoProcessoSeletivoTecnico
	 * @throws DAOException
	 */
	public InscricaoProcessoSeletivoTecnico findInscricaoByPessoaAndAnoPS(int idPessoa) throws DAOException{
		try {
			Criteria cPS = getSession().createCriteria(ProcessoSeletivoTecnico.class);
			cPS.add(Expression.eq("anoEntrada", 2014));
			
			ProcessoSeletivoTecnico ps = (ProcessoSeletivoTecnico) cPS.uniqueResult();
			
			Criteria cInsc = getSession().createCriteria(InscricaoProcessoSeletivoTecnico.class);
			Criteria cPSInsc = cInsc.createCriteria("processoSeletivo");
			Criteria cPessoa = cInsc.createCriteria("pessoa");
			cPSInsc.add(Expression.eq("id", ps.getId()));
			cPessoa.add(Expression.eq("id", idPessoa));

			
			return (InscricaoProcessoSeletivoTecnico) cInsc.uniqueResult();
			
		} catch (DAOException e) {
			 throw new DAOException(e);
		}
	}
	
	/**
	 * Lista a Reserva Vaga Candidato a partir da inscricao
	 * 
	 * @param idInscricao
	 * @return ReservaVagaCandidato
	 * @throws DAOException
	 */
	public ReservaVagaCandidato findReservaVagaCandidatoByInscricao(int idInscricao) throws DAOException{
		try {

			Criteria cReserva = getSession().createCriteria(ReservaVagaCandidato.class);
			Criteria cInsc = cReserva.createCriteria("candidato");
			cInsc.add(Expression.eq("id", idInscricao));

			return (ReservaVagaCandidato) cReserva.uniqueResult();
			
		} catch (DAOException e) {
			 throw new DAOException(e);
		}
	}
	
	/**
	 * Lista a Convocação do discente a partir da inscricao
	 * 
	 * @param idInscricao
	 * @return ReservaVagaCandidato
	 * @throws DAOException
	 */
	public ConvocacaoProcessoSeletivoDiscenteTecnico findConvocacaoByInscricao(int idInscricao) throws DAOException{
		try {

			Criteria cConv = getSession().createCriteria(ConvocacaoProcessoSeletivoDiscenteTecnico.class);
			Criteria cInsc = cConv.createCriteria("inscricaoProcessoSeletivo");
			cInsc.add(Expression.eq("id", idInscricao));

			return (ConvocacaoProcessoSeletivoDiscenteTecnico) cConv.uniqueResult();
			
		} catch (DAOException e) {
			 throw new DAOException(e);
		}
	}
	
	
	/**
	 * Gera o relatório sintético da quantidade de vagas do processo seletivo e quantidade de discentes convocados
	 * 
	 * @param idProcessoSeletivo, opcao
	 * @return
	 * @throws DAOException
	 */
	public List<Object[]> relatorioSinteticoVagasConvocados (int idProcessoSeletivo) throws DAOException {

		
		String projecao = " DISTINCT op.descricao, rvg.denominacao, COUNT(rvg.id), rvps.vagas, op.id  ";
		
		//CONSULTA GERAL
		String hql = "SELECT " + projecao +
				" FROM ResultadoClassificacaoCandidatoTecnico res, InscricaoProcessoSeletivoTecnico ips, " +
				" PessoaTecnico pes, OpcaoPoloGrupo op, ReservaVagaCandidato rvc, ReservaVagaGrupo rvg, ReservaVagaProcessoSeletivo rvps  " +
				" WHERE ips.id = res.inscricaoProcessoSeletivo.id AND pes.id = ips.pessoa.id AND rvps.tipoReserva.id = rvc.tipo.id AND rvps.processo.id = ips.processoSeletivo.id AND rvps.opcao.id = op.id AND " +
				" ips.opcao.id = op.id AND rvc.candidato.id = ips.id AND rvg.id = rvc.tipo.id AND ips.processoSeletivo.id = " + idProcessoSeletivo; 
		
		
		hql +=  " GROUP BY op.descricao, rvg.denominacao, rvps.vagas, op.id " + 
				" ORDER BY op.descricao, rvg.denominacao";
		
		/*String sql = "SELECT " + projecao + 
					 " FROM tecnico.inscricao_processo_seletivo_tecnico ips INNER JOIN tecnico.resultado_classificacao_candidato_tecnico res ON (res.id_inscricao_processo_seletivo_tecnico = ips.id_inscricao_processo_seletivo_tecnico)" +
					 " INNER JOIN tecnico.pessoa_tecnico pes ON (pes.id_pessoa = ips.id_pessoa_tecnico) INNER JOIN tecnico.opcao_polo_grupo op ON (op.id_opcao_polo_grupo = ips.id_opcao)" +
					 " INNER JOIN metropole_digital.reserva_vaga_candidato rvc ON (rvc.id_inscricao_processo_seletivo_tecnico = ips.id_inscricao_processo_seletivo_tecnico)" +
					 " INNER JOIN metropole_digital.reserva_vaga_grupo rvg ON (rvg.id_reserva_vaga_grupo = rvc.id_reserva_vaga_grupo)" +
					 " FULL JOIN metropole_digital.reserva_vaga_processo_seletivo rvps ON (rvps.id_reserva_vaga_grupo = rvc.id_reserva_vaga_grupo AND rvps.id_processo_seletivo_tecnico = ips.id_processo_seletivo_tecnico" +
					 " AND rvps.id_opcao_polo_grupo = op.id_opcao_polo_grupo)" +
					 " WHERE ips.id_processo_seletivo_tecnico = :idPS";
		
		sql +=  " GROUP BY op.descricao, rvg.denominacao, rvps.vagas, op.id_opcao_polo_grupo " + 
				" ORDER BY op.descricao, rvg.denominacao";*/
		
		Query q = getSession().createQuery(hql);
		//q.setInteger("idProcessoSeletivo", processoSeletivo.getId());
		
		@SuppressWarnings("unchecked")
		List<Object[]> res = q.list();
		
		return res;
	}
	
	
	
}
