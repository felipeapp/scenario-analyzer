/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 12/03/2012
 */	
package br.ufrn.sigaa.arq.dao.ensino;

import static br.ufrn.arq.util.UFRNUtils.gerarStringIn;

import java.util.ArrayList;
import java.util.Collection;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.pessoa.dominio.Discente;


/**
 * Dao com consultas sobre as matrículas implantadas
 * @author Victor Hugo
 *
 */
public class MatriculaImplantadaDao extends GenericSigaaDAO  { 
	
	/**
	 * Busca a lista de matrículas de um determinado aluno que estão com as situações passadas
	 * como parâmetro. Esse método utiliza projeção para limitar o número de informações trazidas
	 * e ser mais rápido que outros métodos semelhantes e que trazem o grafo de objetos completo.
	 * 
	 * @param discente
	 * @param situacoes
	 * @return
	 * @throws DAOException
	 */
	public Collection<MatriculaComponente> findImplantacoesByDiscente(DiscenteAdapter discente, boolean apenasAtivas) throws DAOException {
		
			Collection<SituacaoMatricula> situacoes = new ArrayList<SituacaoMatricula>(); //situações de implantações ativas
			situacoes.add( SituacaoMatricula.APROVADO );
			situacoes.add( SituacaoMatricula.APROVEITADO_CUMPRIU );
			situacoes.add( SituacaoMatricula.APROVEITADO_DISPENSADO );
			situacoes.add( SituacaoMatricula.APROVEITADO_TRANSFERIDO );
			situacoes.add( SituacaoMatricula.TRANCADO );
			situacoes.add( SituacaoMatricula.REPROVADO_FALTA );
			situacoes.add( SituacaoMatricula.REPROVADO_MEDIA_FALTA );
			situacoes.add( SituacaoMatricula.REPROVADO );
			
		
			StringBuilder hql = new StringBuilder();

            hql.append("SELECT mc.id, mc.ano, mc.anoFim, mc.mes, mc.mesFim, mc.periodo, mc.mediaFinal, mc.numeroFaltas, mc.situacaoMatricula.descricao, " +
            		" mc.componente.codigo, mc.componente.detalhes.nome, mc.componente.detalhes.chTotal, mc.componente.id, mc.tipoIntegralizacao " +
            		" , mc.situacaoMatricula.id, mc.discente.id " +
            		" FROM MatriculaImplantada mi JOIN mi.matriculaComponente mc " +
            		" WHERE mc.discente.id = :discente" );
            
            if( apenasAtivas ){
            	hql.append(" AND mc.situacaoMatricula.id in " + gerarStringIn(situacoes));
            }
            
        	hql.append(" ORDER BY mc.ano, mc.periodo, mc.situacaoMatricula.id, mc.componente.codigo");

            Query q = getSession().createQuery(hql.toString());
            q.setInteger("discente", discente.getId());
            @SuppressWarnings("unchecked")
            Collection<Object[]> res = q.list();
            Collection<MatriculaComponente> matriculas = new ArrayList<MatriculaComponente>(0);
            
            int i = 0;
            if (res != null ) {
            	for (Object[] reg : res) {
            		i = 0;
            				
            		MatriculaComponente mat = new MatriculaComponente((Integer) reg[i++]);
            		mat.setAno((Short)reg[i++]);
            		mat.setAnoFim((Integer)reg[i++]);
            		mat.setMes((Integer)reg[i++]);
            		mat.setMesFim((Integer)reg[i++]);
            		mat.setPeriodo((Byte)reg[i++]);
            		mat.setMediaFinal((Double) reg[i++]);
            		mat.setNumeroFaltas((Integer) reg[i++]);
            		
            		mat.setSituacaoMatricula(new SituacaoMatricula((String) reg[i++]));
            		mat.setComponente(new ComponenteCurricular());
            		mat.getComponente().setCodigo((String) reg[i]);
            		mat.getComponente().getDetalhes().setCodigo((String) reg[i++]);
            		mat.getComponente().setNome((String) reg[i++]);
            		mat.getComponente().setChTotal((Integer) reg[i++]);
            		mat.getComponente().setId((Integer) reg[i++]);
            		
            		mat.setTipoIntegralizacao((String) reg[i++]); 
            		mat.getSituacaoMatricula().setId((Integer) reg[i++]);
            		mat.setDiscente(new Discente((Integer) reg[i++]));
            		
            		mat.setFrequenciaPorNumeroFaltas( mat.getNumeroFaltas() );
            		
            		
            		matriculas.add(mat);
            		
				}
            }
            
            return matriculas;
	}

}