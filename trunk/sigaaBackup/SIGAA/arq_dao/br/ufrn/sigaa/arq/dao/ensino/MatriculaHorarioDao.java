/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 26/10/2010
 *
 */
package br.ufrn.sigaa.arq.dao.ensino;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import org.hibernate.Query;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ead.dominio.Polo;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.ComponenteDetalhes;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.MatriculaHorario;
import br.ufrn.sigaa.ensino.dominio.OpcaoHorario;
import br.ufrn.sigaa.ensino.dominio.OpcaoModulo;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.tecnico.dao.ModuloDao;
import br.ufrn.sigaa.ensino.tecnico.dominio.DiscenteTecnico;
import br.ufrn.sigaa.ensino.tecnico.dominio.Modulo;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.processamento.dominio.DiscenteEmProcessamento;

/**
 * Dao com consultas para a matrícula por horário.
 * 
 * @author Leonardo Campos
 *
 */
public class MatriculaHorarioDao extends GenericSigaaDAO {

	private static Map<String, String[]> mapaTurmasModulo = new HashMap<String, String[]>();
	private static Map<Integer, String[]> mapaTurmasModuloProcessamento = new HashMap<Integer, String[]>();
	
	static {
		
		//NATAL
			// MÓDULO AVANÇADO - INFORMÁTICA PARA INTERNET e seus respectivos códigos de turmas
			mapaTurmasModulo.put(96054103 + "_" + 101, new String[]{"T01", "T02", "T03", "T04", "T05", "T06", "T07", "T08", "T09" });
			mapaTurmasModulo.put(96054103 + "_" + 102, new String[]{"T01", "T02", "T03", "T04", "T05", "T06", "T07", "T08", "T09" });
			
			// MÓDULO AVANÇADO - REDES DE COMPUTADORES e seus respectivos códigos de turmas
			mapaTurmasModulo.put(96054138 + "_" + 101, new String[]{"T10", "T11" });
			mapaTurmasModulo.put(96054138 + "_" + 102, new String[]{"T10", "T11" });
		
			// MÓDULO AVANÇADO - ELETRÔNICA e seus respectivos códigos de turmas
			mapaTurmasModulo.put(96054186 + "_" + 101, new String[]{"T12", "T15", "T14", "T13" });
			mapaTurmasModulo.put(96054186 + "_" + 102, new String[]{"T12", "T15", "T14", "T13" });

			// MÓDULO AVANÇADO - AUTOMAÇÃO e suas respectivos códigos de turmas
			mapaTurmasModulo.put(99370344 + "_" + 101, new String[]{"T16" });
			mapaTurmasModulo.put(99370344 + "_" + 102, new String[]{"T16" });
			
		//CENEP
			// MÓDULO AVANÇADO - INFORMÁTICA PARA INTERNET e seus respectivos códigos de turmas
			mapaTurmasModulo.put(96054103 + "_" + 501, new String[]{"T17" });
			
		//MOSSORO
			// MÓDULO AVANÇADO - INFORMÁTICA PARA INTERNET e seus respectivos códigos de turmas
			mapaTurmasModulo.put(96054103 + "_" + 201, new String[]{"T18" });
			mapaTurmasModulo.put(96054103 + "_" + 202, new String[]{"T18" });
			
			// MÓDULO AVANÇADO - REDES DE COMPUTADORES e seus respectivos códigos de turmas
			mapaTurmasModulo.put(96054138 + "_" + 201, new String[]{"T19" });
			mapaTurmasModulo.put(96054138 + "_" + 202, new String[]{"T19" });

		//CAICO
			// MÓDULO AVANÇADO - INFORMÁTICA PARA INTERNET e seus respectivos códigos de turmas
			mapaTurmasModulo.put(96054103 + "_" + 401, new String[]{"T20", "T21" });
			mapaTurmasModulo.put(96054103 + "_" + 402, new String[]{"T20", "T21" });
			
		//ANGICOS
			// MÓDULO AVANÇADO - INFORMÁTICA PARA INTERNET e seus respectivos códigos de turmas
			mapaTurmasModulo.put(96054103 + "_" + 301, new String[]{"T22" });
			mapaTurmasModulo.put(96054103 + "_" + 302, new String[]{"T22" });
			
		
		mapaTurmasModuloProcessamento.put(96054103, new String[]{"T01", "T02", "T03", "T04", "T05", "T06", 
				"T07", "T08", "T09", "T17", "T18", "T20", "T21", "T22" });
		mapaTurmasModuloProcessamento.put(96054138, new String[]{"T10", "T11", "T19" });		
		mapaTurmasModuloProcessamento.put(96054186, new String[]{"T12", "T15", "T14", "T13" });
		mapaTurmasModuloProcessamento.put(99370344, new String[]{"T16" });
	}
	
	/**
	 * Retorna a solicitação de matrícula por horário realizada por um discente num determinado ano-período.
	 * 
	 * @param discente
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws DAOException
	 */
	public MatriculaHorario findByAnoPeriodoDiscente(Discente discente, int ano, int periodo) throws DAOException {
		try {
			String hql = "FROM MatriculaHorario WHERE discente.id = :idDiscente AND ano = :ano AND periodo = :periodo";
			Query q = getSession().createQuery(hql);
			q.setInteger("idDiscente", discente.getId());
			q.setInteger("ano", ano);
			q.setInteger("periodo", periodo);
			q.setMaxResults(1);
			return (MatriculaHorario) q.uniqueResult();
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}
	
	/**
	 * Retorna as opções de horário distintas das turmas de um dado módulo num dado ano-período informados.
	 * 
	 * @param modulo
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws DAOException
	 */
	public Collection<OpcaoHorario> findOpcoesByModulo(Modulo modulo, int ano, int periodo, DiscenteTecnico discenteTecnico) throws DAOException{
		try {
			String hql = "SELECT distinct t.id, ht.dia, h.tipo " +
						" FROM Turma t JOIN t.horarios ht JOIN ht.horario h " +
						" 		JOIN t.disciplina d, ModuloDisciplina md " +
						" WHERE md.disciplina.id = d.id" +
						" AND md.modulo.id = :idModulo" +
						" AND t.ano = :ano AND t.periodo = :periodo" +
						" AND t.codigo in " + UFRNUtils.gerarStringIn(
									mapaTurmasModulo.get(modulo.getId() + "_" + discenteTecnico.getOpcaoPoloGrupo().getId())) +
						" GROUP BY t.id, ht.dia, h.tipo" +
						" ORDER BY t.id, ht.dia, h.tipo";
			Query q = getSession().createQuery(hql);
			q.setInteger("idModulo", modulo.getId());
			q.setInteger("ano", ano);
			q.setInteger("periodo", periodo);
			
			@SuppressWarnings("unchecked")
			List<Object[]> lista = q.list();
			
			Collection<OpcaoHorario> opcoes = new ArrayList<OpcaoHorario>();
			Map<Integer, String> turmaOpcao = new HashMap<Integer, String>();
			Integer idTurma = 0;
			Character diaTurma = 0;
			if(lista != null){
				for(Object[] reg: lista){
					Integer id = (Integer) reg[0];
					Character dia = (Character) reg[1];
					Short turno = (Short) reg[2];

					if(!id.equals(idTurma)){
						idTurma = id;
						diaTurma = dia;
						turmaOpcao.put(id, buildOpcao(dia, turno));
					} else {
						if(!dia.equals(diaTurma)){
							diaTurma = dia;
							turmaOpcao.put(id, rebuildOpcao(dia, turno, turmaOpcao.get(id)));
						}else
							turmaOpcao.put(id, rebuildOpcao(turno, turmaOpcao.get(id)));
					}
				}
			}
			Set<String> ops = new HashSet<String>();
			for(String s: turmaOpcao.values())
				ops.add(s);
			
			OpcaoModulo opcaoModulo = new OpcaoModulo();
			opcaoModulo.setModulo(modulo);
			
			for(String s: ops){
				OpcaoHorario op = new OpcaoHorario();
				op.setOpcao(s);
				op.setOpcaoModulo(opcaoModulo);
				opcoes.add(op);
			}
			return opcoes;
		} catch(Exception e){
			throw new DAOException(e.getMessage(), e);
		}
	}

	/**
	 * 
	 * @param ano
	 * @param periodo
	 * @param idModulos
	 * @return
	 * @throws DAOException
	 */
	public HashMap<Modulo, HashMap<String, List<Turma>>> findOfertaModulos(int ano, int periodo, int[] idModulos) throws DAOException {
		ModuloDao mdao = DAOFactory.getInstance().getDAO(ModuloDao.class);
		try {
			String hql = "SELECT distinct t.codigo, t.id, ht.dia, h.tipo, t.capacidadeAluno, d.id, det.id, det.nome, t.descricaoHorario, t.polo.id " +
						" FROM Turma t JOIN t.horarios ht JOIN ht.horario h " +
						" 		JOIN t.disciplina d JOIN d.detalhes det, ModuloDisciplina md " +
						" WHERE md.disciplina.id = d.id" +
						" AND md.modulo.id in " + UFRNUtils.gerarStringIn(idModulos) +
						" AND t.ano = :ano AND t.periodo = :periodo" +
						" GROUP BY t.codigo, t.id, ht.dia, h.tipo, t.capacidadeAluno, d.id, det.id, det.nome, t.descricaoHorario, t.polo.id " +
						" ORDER BY t.codigo, t.id, ht.dia, h.tipo, t.capacidadeAluno, t.polo.id";
			Query q = getSession().createQuery(hql);
			q.setInteger("ano", ano);
			q.setInteger("periodo", periodo);
			
			@SuppressWarnings("unchecked")
			List<Object[]> lista = q.list();
			
			Map<Integer, Turma> turmas = new HashMap<Integer, Turma>();
			Map<Integer, String> turmaOpcao = new HashMap<Integer, String>();
			Integer idTurma = 0;
			Character diaTurma = 0;
			if(lista != null){
				for(Object[] reg: lista){
					String codTurma = (String) reg[0];
					Integer id = (Integer) reg[1];
					Character dia = (Character) reg[2];
					Short turno = (Short) reg[3];
					Integer cap = (Integer) reg[4];
					Integer idDisciplina = (Integer) reg[5];
					Integer idDetalhes = (Integer) reg[6];
					String nome = (String) reg[7];
					String descricaoHorario = (String) reg[8];
					Integer idPolo = (Integer) reg[9];
					
					if(turmas.get(id) == null){
						Turma t = new Turma(id);
						t.setCodigo(codTurma);
						t.setCapacidadeAluno(cap);
						t.setAno(ano);
						t.setPeriodo(periodo);
						t.setDescricaoHorario(descricaoHorario);
						t.setQtdMatriculados(0);
						t.setPolo(new Polo(idPolo));
						ComponenteCurricular cc = new ComponenteCurricular(idDisciplina);
						cc.setDetalhes(new ComponenteDetalhes(idDetalhes));
						if (cc.getDetalhes().getId() != 0)
							cc.getDetalhes().setNome(nome);
						t.setDisciplina(cc);
						t.setMatriculasDisciplina(new ArrayList<MatriculaComponente>());
						turmas.put(id, t);
					}
					
					if(!id.equals(idTurma)){
						idTurma = id;
						diaTurma = dia;
						turmaOpcao.put(id, buildOpcao(dia, turno));
					} else {
						if(!dia.equals(diaTurma)){
							diaTurma = dia;
							turmaOpcao.put(id, rebuildOpcao(dia, turno, turmaOpcao.get(id)));
						}else
							turmaOpcao.put(id, rebuildOpcao(turno, turmaOpcao.get(id)));
					}
				}
			}
			
			
			
			HashMap<Modulo, HashMap<String, List<Turma>>> ofertaModulos = new HashMap<Modulo, HashMap<String, List<Turma>>>();
			
			Map<Integer, Modulo> mapaEnfases = new HashMap<Integer, Modulo>();
			Collection<Modulo> enfases = mdao.findByIdModulos(idModulos, false);
			for (Modulo m : enfases) {
				mapaEnfases.put(m.getId(), m);
			}
			
			for(Entry<Integer, String> en: turmaOpcao.entrySet()) {
				String horario = en.getValue();
				Turma t = turmas.get(en.getKey());
				Modulo m = getModulo(t, mapaEnfases);
				
				if(ofertaModulos.get(m) == null) {
					
					ofertaModulos.put(m, new HashMap<String, List<Turma>>());
					
				} else {
					HashMap<String, List<Turma>> turmasModulo = ofertaModulos.get(m);
					
					if(turmasModulo.get(horario) == null) {
						List<Turma> list = new ArrayList<Turma>();
						list.add(t);
						turmasModulo.put(horario, list);
					} else {
						List<Turma> list = turmasModulo.get(horario);
						list.add(t);
						turmasModulo.put(horario, list);
					}
					
					ofertaModulos.put(m, turmasModulo);
				}
			}
			
			for(HashMap<String, List<Turma>> map: ofertaModulos.values())
				for(List<Turma> list: map.values())
					Collections.sort(list, new Comparator<Turma>(){

						@Override
						public int compare(Turma o1, Turma o2) {
							return o1.getCodigo().compareTo(o2.getCodigo());
						}
						
					});
			
			return ofertaModulos;
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		} finally {
			mdao.close();
		}
		
	}

	/**
	 * Retorna o módulo correspondente de acordo com o código da turma informada.
	 * @param t
	 * @param enfaseWEB 
	 * @param enfaseELE 
	 * @return
	 */
	private Modulo getModulo(Turma t, Map<Integer, Modulo> mapa) {
		for (Entry<Integer, String[]> en : mapaTurmasModuloProcessamento.entrySet()) {
			if(Arrays.asList(en.getValue()).contains(t.getCodigo()))
				return mapa.get(en.getKey());
			}
		return null;
	}

	/**
	 * Reconstrói a opção de horário informada acrescentando o novo turno informado.
	 * 
	 * @param turno
	 * @param opcaoOriginal
	 * @return
	 */
	private String rebuildOpcao(Short turno, String opcaoOriginal) {
		String prefixo = opcaoOriginal.substring(0, opcaoOriginal.length() - 1);
		return prefixo + getChar(turno);
	}

	/**
	 * Reconstrói a opção de horário informada acrescentando os novos dia e turno informados.
	 * 
	 * @param dia
	 * @param turno
	 * @param opcaoOriginal
	 * @return
	 */
	private String rebuildOpcao(Character dia,  Short turno, String opcaoOriginal) {
		String prefixo = opcaoOriginal.substring(0, opcaoOriginal.length() - 1);
		String sufixo = buildOpcao(dia, turno);
		return prefixo + sufixo;
	}

	/**
	 * Monta a string que representa uma opção de horário a partir do dia e turno informados.
	 * 
	 * @param dia
	 * @param turno
	 * @return
	 */
	private String buildOpcao(Character dia, Short turno) {
		StringBuilder sb = new StringBuilder();
		sb.append(dia);
		sb.append(getChar(turno));
		return sb.toString();
	}

	/**
	 * Retorna um caracter representando o turno correspondente ao valor inteiro informado.
	 * @param turno
	 * @return
	 */
	private char getChar(Short turno) {
		switch (turno) {
		case 1:
			return 'M';
		case 2:
			return 'T';
		case 3:
			return 'N';
		default:
			return '\0';
		}
	}
	
	/**
	 * Retorna um mapa com os ids dos alunos e suas respectivas escolhas registradas na solicitação de matrícula por horário.
	 * 
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws DAOException
	 */
	public Map<Integer, MatriculaHorario> findSolicitacoesMatriculaHorario(int ano, int periodo) throws DAOException {
		try {
			String hql = "select mh.discente.id, mh, mh.discente, mh.discente.pessoa from MatriculaHorario mh where mh.ano = :ano and mh.periodo = :periodo";
			
			@SuppressWarnings("unchecked")
			List<Object[]> lista = getSession().createQuery(hql).setInteger("ano", ano).setInteger("periodo", periodo).list();
			
			Map<Integer, MatriculaHorario> map = new HashMap<Integer, MatriculaHorario>();
			for(Object[] reg: lista){
				Integer idDiscente = (Integer) reg[0];
				MatriculaHorario solicitacaoMatricula = (MatriculaHorario) reg[1];
				solicitacaoMatricula.setDiscente( (Discente) reg[2]);
				solicitacaoMatricula.getDiscente().setPessoa( (Pessoa) reg[3]);
				map.put(idDiscente, solicitacaoMatricula);
			}
			
			return map;
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}
	
	/**
	 * Retorna o conjunto de discentes a ser processado já ordenado.
	 * @param ano
	 * @param periodo
	 * @param idComponente
	 * @return
	 * @throws DAOException
	 */
	public TreeSet<DiscenteEmProcessamento> findDiscentesAProcessar(int ano, int periodo, Map<Integer, MatriculaHorario> solicitacoes, int idUnidade) throws DAOException {
		try {
			String hql = "select distinct mc.id_discente, mc.media_final, opg.id_polo" +
					" from ensino.matricula_componente mc" +
					" left join tecnico.discente_tecnico dt using (id_discente) " +
					" left join tecnico.opcao_polo_grupo opg on ( dt.id_opcao_polo_grupo = opg.id_opcao_polo_grupo ) " + 
					" where mc.ano = :ano and mc.periodo = :periodo" +
					" and mc.id_componente_curricular = ( " +
					"	select id_disciplina " +
					"	from ensino.componente_curricular " +
					"	where nivel='T' and id_unidade=:idUnidade " +
					"	and codigo='IMD0900')";
			
			@SuppressWarnings("unchecked")
			List<Object[]> lista = getSession().createSQLQuery(hql).setInteger("ano", ano).setInteger("periodo", periodo).setInteger("idUnidade", idUnidade).list();
			
			
			TreeSet<DiscenteEmProcessamento> discentes = new TreeSet<DiscenteEmProcessamento>();
			
			for(Object[] reg: lista){
				int col = 0;
				Integer idDiscente = (Integer) reg[col++];
				
				if (!ValidatorUtil.isEmpty(solicitacoes.get(idDiscente))) {
					BigDecimal media = (BigDecimal) reg[col++];
					Integer idPolo = (Integer) reg[col++];
					
					DiscenteEmProcessamento d = new DiscenteEmProcessamento();
					d.setIdDiscente(idDiscente);
					d.setMediaFinal(media.doubleValue());
					d.setPoloDiscente(idPolo);
					
					// TODO Verificar como obter segundo critério de desempate, a nota da seleção
					d.setNotaSelecao(0.0);
					d.setSolicitacaoMatricula(solicitacoes.get(idDiscente));
					
					discentes.add(d);
					
				}
			}
			
			return discentes;
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}
	
}
