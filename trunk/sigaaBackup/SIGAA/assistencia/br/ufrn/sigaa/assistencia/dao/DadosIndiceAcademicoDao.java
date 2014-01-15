package br.ufrn.sigaa.assistencia.dao;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;

import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.assistencia.dominio.DadosIndiceAcademico;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.ensino.graduacao.dominio.MatrizCurricular;
import br.ufrn.sigaa.pessoa.dominio.Municipio;

public class DadosIndiceAcademicoDao extends GenericSigaaDAO {

	/**
	 * Retorna se existe algum calendário definido para o tipo de bolsa auxílio e municípios informados no ano corrente.
	 * 
	 * @param tipoBolsaAuxilio
	 * @param municipio
	 * @return
	 * @throws DAOException 
	 * @throws  
	 */
	public Collection<DadosIndiceAcademico> findAllIndicesCadastrados() throws DAOException {
		Collection<DadosIndiceAcademico> lista = new ArrayList<DadosIndiceAcademico>();
		String sql = "SELECT distinct ano_referencia, to_date(to_char(dia.data_cadastro, 'DD/MM/YYYY'), 'DD/MM/YYYY'), p.nome" +
				" FROM sae.dados_indice_academico dia" +
				" JOIN comum.registro_entrada re on ( dia.id_registro_cadastro = re.id_entrada )" +
				" JOIN comum.usuario u using ( id_usuario )" +
				" JOIN comum.pessoa p using ( id_pessoa )" +
				" WHERE dia.ativo = trueValue()" +
				" order by ano_referencia desc";
		List<Object[]> result = getSession().createSQLQuery(sql).list();
		for (Object[] objects : result) {
			DadosIndiceAcademico dia = new DadosIndiceAcademico();
			dia.setAnoReferencia( (Integer) objects[0] );
			dia.setDataCadastro( (Date) objects[1] );
			dia.setPessoaCadastro( (String) objects[2] );
			lista.add(dia);
		}
		return lista;
	}
	
	/**
	 * Retorna se existe algum calendário definido para o tipo de bolsa auxílio e municípios informados no ano corrente.
	 * 
	 * @param tipoBolsaAuxilio
	 * @param municipio
	 * @return
	 * @throws DAOException 
	 * @throws  
	 */
	public Collection<DadosIndiceAcademico> findAllIndicesAno( Integer ano ) throws DAOException {
		Collection<DadosIndiceAcademico> lista = new ArrayList<DadosIndiceAcademico>();
		String sql = "SELECT c.id_municipio, m.nome, dia.iepl, dia.iech, " +
				" c.nome||coalesce(' - '||ga.descricao, '')||coalesce(' - '||t.sigla,'')||coalesce(' - '||h.nome,'') as curso" +
				" FROM sae.dados_indice_academico dia" +
				" JOIN graduacao.matriz_curricular mc on ( dia.id_matriz = mc.id_matriz_curricular ) " +
				" JOIN curso c using ( id_curso )" +
				" JOIN comum.municipio m using ( id_municipio )" +
				" JOIN ensino.grau_academico ga on ( c.id_grau_academico = ga.id_grau_academico )" +
				" JOIN graduacao.matriz_curricular mcu ON mcu.id_curso = c.id_curso" +
				" LEFT JOIN graduacao.habilitacao h ON (mcu.id_habilitacao = h.id_habilitacao)" +
				" JOIN ensino.turno t ON t.id_turno = mcu.id_turno" +
				" WHERE dia.ativo = trueValue() and dia.ano_referencia = " + ano +
				" order by c.id_municipio, c.nome";
		List<Object[]> result = getSession().createSQLQuery(sql).list();
		for (Object[] objects : result) {
			DadosIndiceAcademico dia = new DadosIndiceAcademico();
			dia.setMatriz(new MatrizCurricular());
			dia.getMatriz().setCurso(new Curso());
			dia.getMatriz().getCurso().setMunicipio(new Municipio((Integer) objects[0]));
			dia.getMatriz().getCurso().getMunicipio().setNome( (String) objects[1] );
			dia.setIepl( (Double) objects[2] );
			dia.setIech( (Double) objects[3] );
			dia.getMatriz().getCurso().setNome( (String) objects[4] );
			lista.add(dia);
		}
		return lista;
	}
	
	public void inativar(Integer anoReferencia, RegistroEntrada registro) {
		update("update sae.dados_indice_academico set ativo = falseValue(), data_remocao = now(), id_registro_remocao = " + registro.getId() + " where ano_referencia = ? ", new Object[] { anoReferencia });
	}
	
	public boolean haIndiceCadastrado(Integer anoReferencia) throws HibernateException, DAOException {
		String sql = "select count(*) from sae.dados_indice_academico where ativo = trueValue() and ano_referencia =  " + anoReferencia;
		return ((BigInteger) getSession().createSQLQuery(sql).uniqueResult()).doubleValue() > 0;
	}

}