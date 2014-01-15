/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 20/10/2009
 */
package br.ufrn.arq.negocio.validacao.tests;

import junit.framework.TestCase;
import br.ufrn.arq.negocio.validacao.AnnotationValidation;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
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
 * Contém testes para a validação por anotações.
 * 
 * @author David Pereira
 * @author Gleydson Lima
 *
 */
public class AnnotationValidationTests extends TestCase {

	public void testRequired() {
		ListaMensagens lista = new ListaMensagens();
		AnnotationValidation.validate(new EntityMock(), Required.class, lista);
		assertTrue(lista.isErrorPresent());
		assertEquals(2, lista.getErrorMessages().size());
	}
	
	public void testLength() {
		ListaMensagens lista = new ListaMensagens();
		AnnotationValidation.validate(new EntityMock(), Length.class, lista);
		assertTrue(lista.isErrorPresent());
		assertEquals(3, lista.getErrorMessages().size());
	}
	
	public void testEmail() {
		ListaMensagens lista = new ListaMensagens();
		AnnotationValidation.validate(new EntityMock(), Email.class, lista);
		assertTrue(lista.isErrorPresent());
		assertEquals(1, lista.getErrorMessages().size());
	}
	
	public void testUrl() {
		ListaMensagens lista = new ListaMensagens();
		AnnotationValidation.validate(new EntityMock(), Url.class, lista);
		assertTrue(lista.isErrorPresent());
		assertEquals(1, lista.getErrorMessages().size());
	}

	public void testPast() {
		ListaMensagens lista = new ListaMensagens();
		AnnotationValidation.validate(new EntityMock(), Past.class, lista);
		assertTrue(lista.isErrorPresent());
		assertEquals(1, lista.getErrorMessages().size());
	}
	
	public void testFuture() {
		ListaMensagens lista = new ListaMensagens();
		AnnotationValidation.validate(new EntityMock(), Future.class, lista);
		assertTrue(lista.isErrorPresent());
		assertEquals(1, lista.getErrorMessages().size());
	}
	
	public void testMin() {
		ListaMensagens lista = new ListaMensagens();
		AnnotationValidation.validate(new EntityMock(), Min.class, lista);
		assertTrue(lista.isErrorPresent());
		assertEquals(2, lista.getErrorMessages().size());
	}
	
	public void testMax() {
		ListaMensagens lista = new ListaMensagens();
		AnnotationValidation.validate(new EntityMock(), Max.class, lista);
		assertTrue(lista.isErrorPresent());
		assertEquals(2, lista.getErrorMessages().size());
	}
	
	public void testRange() {
		ListaMensagens lista = new ListaMensagens();
		AnnotationValidation.validate(new EntityMock(), Range.class, lista);
		assertTrue(lista.isErrorPresent());
		assertEquals(1, lista.getErrorMessages().size());
	}
	
	public void testPattern() {
		ListaMensagens lista = new ListaMensagens();
		AnnotationValidation.validate(new EntityMock(), Pattern.class, lista);
		assertTrue(lista.isErrorPresent());
		assertEquals(1, lista.getErrorMessages().size());
	}
	
	public void testCpfCnpj() {
		ListaMensagens lista = new ListaMensagens();
		AnnotationValidation.validate(new EntityMock(), CpfCnpj.class, lista);
		assertTrue(lista.isErrorPresent());
		assertEquals(2, lista.getErrorMessages().size());		
	}

}
