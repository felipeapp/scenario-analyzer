/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 20/10/2009
 */
package br.ufrn.arq.negocio.validacao.tests;

import java.util.Date;

import br.ufrn.arq.negocio.validacao.annotations.CpfCnpj;
import br.ufrn.arq.negocio.validacao.annotations.Email;
import br.ufrn.arq.negocio.validacao.annotations.Future;
import br.ufrn.arq.negocio.validacao.annotations.Length;
import br.ufrn.arq.negocio.validacao.annotations.Max;
import br.ufrn.arq.negocio.validacao.annotations.Min;
import br.ufrn.arq.negocio.validacao.annotations.Past;
import br.ufrn.arq.negocio.validacao.annotations.Pattern;
import br.ufrn.arq.negocio.validacao.annotations.Range;
import br.ufrn.arq.negocio.validacao.annotations.Required;
import br.ufrn.arq.negocio.validacao.annotations.Url;

/**
 * Classe auxiliar para realizar testes da validação
 * por anotações.
 * @author David Pereira
 * @author Gleydson Lima
 *
 */
@SuppressWarnings("deprecation")
public class EntityMock {
		
	@Required
	public String att3;
	
	@Required @Email
	public String att4 = "";
		
	@Length(min="10")
	public String att5 = "1234567";
		
	@Length(max="5")
	public String att6 = "1234567";
	
	@Length(min="2", max="10")
	public String att7 = "1234567";
	
	@Length(min="10", max="50")
	public String att8 = "1234567";
	
	@Length(min="2")
	public String att9 = "1234567";
	
	@Length(max="50")
	public String att10 = "1234567";

	@Email
	public String att11 = "gwerwerwerw";
	
	@Email
	public String att12 = "desenv@info.ufrn.br";
	
	@Url
	public String att13 = "wegeiowgiegoeiwgo";
	
	@Url
	public String att14 = "http://www.ufrn.br";
	
	@Past
	public Date att15 = new Date("01/01/3000");
	
	@Past
	public Date att16 = new Date("01/01/1970");
	
	@Future
	public Date att17 = new Date("01/01/3000");
	
	@Future
	public Date att18 = new Date("01/01/1970");
	
	@Min("1")
	public int att19 = 1;
	
	@Min("5")
	public int att20 = 1;
	
	@Min("0")
	public int att21 = 1;
	
	@Min("1.5")
	public double att22 = 1.5;
	
	@Min("2.5")
	public double att23 = 1.5;
	
	@Min("0.5")
	public double att24 = 1.5;
	
	@Max("1")
	public int att25 = 1;
	
	@Max("5")
	public int att26 = 1;
	
	@Max("0")
	public int att27 = 1;
	
	@Max("1.5")
	public double att28 = 1.5;
	
	@Max("2.5")
	public double att29 = 1.5;
	
	@Max("0.5")
	public double att30 = 1.5;
	
	@Range(min="2", max="10")
	public int att31 = 5;

	@Range(min="1", max="4")
	public int att32 = 5;
	
	@Pattern("a*b")
	public String att33 = "aaaab";
	
	@Pattern("a*b")
	public String att34 = "aaaabb";

	@CpfCnpj
	public String att35 = "123.456.789-10";
	
	@CpfCnpj
	public String att36 = "111111111111";
	
	@CpfCnpj
	public String att37 = "119.471.265-71";
	
}
