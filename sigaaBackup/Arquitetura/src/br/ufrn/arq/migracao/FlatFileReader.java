/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 25/08/2009
 */
package br.ufrn.arq.migracao;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;
import java.util.Scanner;

import javax.sql.DataSource;
import javax.swing.JOptionPane;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.object.BatchSqlUpdate;

/**
 * Classe para migração de arquivos de texto com formato fixo.
 * 
 * @author David Pereira
 * @author Gleydson Lima
 *
 */
public abstract class FlatFileReader {

	protected LineSplitter lineSplitter = new LineSplitter();
	
	protected DataSource ds;

	protected JdbcTemplate jt;
	
	public abstract BatchSqlUpdate prepareInsertBatch();
	public abstract BatchSqlUpdate prepareUpdateBatch();
	public abstract int getId(List<String> items);

	public FlatFileReader() {
		
	}
	
	public FlatFileReader(DataSource ds) {
		this.ds = ds;
		jt = new JdbcTemplate(ds);
	}
	
	protected DataSource getDataSource() {
		if (ds == null) {
			BasicDataSource bds = new BasicDataSource();
			bds.setDriverClassName("org.postgresql.Driver");
			bds.setUrl("jdbc:postgresql://127.0.0.1:5432/sistemas_comum");
			bds.setUsername("postgres");
			bds.setPassword(JOptionPane.showInputDialog("Digite a senha para acesso ao banco: ")); 
			
			jt = new JdbcTemplate(bds);
			this.ds = bds;
		}
		return ds;
	}
	
	public void doMigrate(File file, Integer... colunas) throws FileNotFoundException {
		doMigrate(new FileInputStream(file), colunas);
	}
	
	public void doMigrate(InputStream in, Integer... colunas) {
		Scanner sc = new Scanner(in);
		
		BatchSqlUpdate insertBatch = prepareInsertBatch();
		BatchSqlUpdate updateBatch = prepareUpdateBatch();
		
		int i = 0;
		while(sc.hasNextLine()) {
			List<String> items = lineSplitter.split(sc.nextLine(), colunas);
			transformItems(items);
			int id = 0;
			
			try {
				id = getId(items);
				
				System.out.println(++i + " - Update");
				items.add(String.valueOf(id));
				updateBatch.update(items.toArray());
			} catch(EmptyResultDataAccessException e) {
				System.out.println(++i + " - Insert");
				insertBatch.update(items.toArray());
			}
		}
		
		updateBatch.flush();
		insertBatch.flush();
	}
	
	

	protected void transformItems(List<String> items) {
		
	}

}
