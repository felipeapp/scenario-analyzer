package br.ufrn.sigaa.ensino.graduacao.dao;

import static br.ufrn.arq.util.UFRNUtils.gerarStringIn;

import java.util.ArrayList;
import java.util.Collection;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.RegistroAtividade;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.TipoComponenteCurricular;
import br.ufrn.sigaa.parametros.dominio.ParametrosStrictoSensu;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * DAO com consultas relativas à consolidacao de atividades de uma Matricula.
 * 
 * @author Diego Jácome
 *
 */
public class ConsolidarMatriculasAtividadesDao extends GenericSigaaDAO {


	/**
	 * Busca todas as matrículas em atividades de um discente que pertencem a
	 * um determinado ano-período e estão com as situações passadas como parâmetro.
	 * 
	 * @param discente
	 * @param situacao
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws DAOException
	 */
	public Collection<MatriculaComponente> findDiscentesEAtividadesByCurso(Curso curso , SituacaoMatricula... situacao) throws DAOException {
        
		boolean disciplinaTipoDissertacao = ParametroHelper.getInstance().getParametroBoolean(ParametrosStrictoSensu.TESE_DEFINIDA_COMO_DISCIPLINA);
	        
        int tipos[] = new int[] {TipoComponenteCurricular.ATIVIDADE};
        if( disciplinaTipoDissertacao )
        	tipos = new int[] {TipoComponenteCurricular.ATIVIDADE, TipoComponenteCurricular.DISCIPLINA};
		
		StringBuilder hql = new StringBuilder();
        hql.append("select mc.id, mc.ano, mc.periodo, cc.id, cc.codigo, cc.detalhes.nome, " +
        		" cc.tipoComponente.id, mc.situacaoMatricula.descricao, mc.situacaoMatricula.id, ra, " +
        		" cc.detalhes.chTotal, ta.id, ta.descricao, mc.discente.id, mc.discente.pessoa.id, " +
        		" mc.discente.pessoa.nome, mc.discente.matricula, mc.discente.status " +
        		" FROM MatriculaComponente mc left join mc.registroAtividade ra " +
        		" left join mc.componente cc left join cc.tipoAtividade ta left join cc.formaParticipacao fp " +
        		" WHERE cc.tipoComponente.id in " + gerarStringIn(tipos) +
        		" AND mc.discente.curso.id = " + curso.getId()
        		);
        
        if( disciplinaTipoDissertacao )
        	hql.append(" AND ta.id > 0 ");
        else
        	hql.append(" AND fp.permiteCriarTurma = falseValue() ");
        hql.append(" AND mc.situacaoMatricula.id in(");
        for (SituacaoMatricula s : situacao)
        	 hql.append(s.getId() + ",");
        hql.replace(hql.length() - 1, hql.length(), ")");
        hql.append(" ORDER BY cc.detalhes.nome, mc.discente.pessoa.nome, mc.ano, mc.periodo, mc.discente.status");
        Query q = getSession().createQuery(hql.toString());
        
        @SuppressWarnings("unchecked")
        Collection<Object[]> res = q.list();
        Collection<MatriculaComponente> matriculas = new ArrayList<MatriculaComponente>(0);
        if (res != null ) {
        	for (Object[] reg : res) {
        		
        		Integer i = 0;
        		
        		MatriculaComponente mat = new MatriculaComponente((Integer) reg[i++]);
        		mat.setAno((Short)reg[i++]);
        		mat.setPeriodo((Byte)reg[i++]);
        		
        		mat.setComponente(new ComponenteCurricular());
        		mat.getComponente().setId((Integer) reg[i++]);
        		mat.getComponente().setCodigo((String) reg[i++]);
        		mat.getComponente().getDetalhes().setCodigo((String) reg[i]);
        		mat.getComponente().setNome((String) reg[i++]);
        		mat.getComponente().getTipoComponente().setId((Integer) reg[i++]);
        		mat.setSituacaoMatricula(new SituacaoMatricula((String) reg[i++]));
        		mat.getSituacaoMatricula().setId( (Integer) reg[i++] );
        		
        		mat.setRegistroAtividade((RegistroAtividade) reg[i++]);
        		mat.getComponente().setChTotal((Integer) reg[i++]);
        		
        		mat.getComponente().getTipoAtividade().setId((Integer) reg[i++]);
        		mat.getComponente().getTipoAtividade().setDescricao((String) reg[i++]);
        		
        		mat.setDiscente(new Discente((Integer) reg[i++]));
        		mat.getDiscente().setPessoa(new Pessoa((Integer) reg[i++]));
        		mat.getDiscente().getPessoa().setNome((String) reg[i++]);
        		mat.getDiscente().setMatricula(((Number) reg[i++]).longValue());
        		mat.getDiscente().setStatus((Integer) reg[i++]);
        		
        		matriculas.add(mat);
			}
        }
        return matriculas;            
	}
	
}
