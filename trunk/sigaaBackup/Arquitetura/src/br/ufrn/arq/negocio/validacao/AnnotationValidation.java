/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 20/10/2009
 */
package br.ufrn.arq.negocio.validacao;

import static br.ufrn.arq.util.ReflectionUtils.getFieldName;
import static br.ufrn.arq.util.ReflectionUtils.getFieldValue;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
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
import br.ufrn.arq.util.ValidatorUtil;

/**
 * Classe para tratar de validações efetuadas através
 * de anotações nos atributos das classes de domínio.
 * 
 * @author David Pereira
 * @author Gleydson Lima
 *
 */
public class AnnotationValidation extends AnnotationValidationSupport {

	/**
	 * Valida os atributos que tiverem a anotação de validação {@link Email}.
	 * @param obj
	 * @param lista
	 */
	public static void validateEmail(Field field, Object obj, Annotation annotation, ListaMensagens lista) {
		supportedTypes(field, String.class);
		ValidatorUtil.validateEmail((String) getFieldValue(obj, field), getFieldName(field), lista);
	}

	/**
	 * Valida os atributos que tiverem a anotação de validação {@link Future}.
	 * @param obj
	 * @param lista
	 */
	public static void validateFuture(Field field, Object obj, Annotation annotation, ListaMensagens lista) {
		supportedTypes(field, Date.class);		
		ValidatorUtil.validateFuture((Date) getFieldValue(obj, field), getFieldName(field), lista);
	}
	
	/**
	 * Valida os atributos que tiverem a anotação de validação {@link Past}.
	 * @param obj
	 * @param lista
	 */
	public static void validatePast(Field field, Object obj, Annotation annotation, ListaMensagens lista) {
		supportedTypes(field, Date.class);
		ValidatorUtil.validatePast((Date) getFieldValue(obj, field), getFieldName(field), lista);
	}
	
	/**
	 * Valida os atributos que tiverem a anotação de validação {@link Required}.
	 * @param obj
	 * @param lista
	 */
	public static void validateRequired(Field field, Object obj, Annotation annotation, ListaMensagens lista) {
		Object value = getFieldValue(obj, field);
		ValidatorUtil.validateRequired(value, getFieldName(field), lista);
	}
	
	/**
	 * Valida os atributos que tiverem a anotação de validação {@link Length}.
	 * @param obj
	 * @param lista
	 */
	public static void validateLength(Field field, Object obj, Annotation annotation, ListaMensagens lista) {
		supportedTypes(field, String.class);
				
		Length length = (Length) annotation;
		ValidatorUtil.validateMinLength((String) getFieldValue(obj, field), Integer.parseInt(length.min()), getFieldName(field), lista);
		ValidatorUtil.validateMaxLength((String) getFieldValue(obj, field), Integer.parseInt(length.max()), getFieldName(field), lista);
	}
	
	/**
	 * Valida os atributos que tiverem a anotação de validação {@link Max}.
	 * @param obj
	 * @param lista
	 */
	public static void validateMax(Field field, Object obj, Annotation annotation, ListaMensagens lista) {
		supportedTypes(field, Integer.class, Double.class, int.class, double.class);

		Max max = (Max) annotation;
		if (field.getType().isAssignableFrom(Integer.class))
			ValidatorUtil.validateMaxValue(getFieldValue(obj, field), Integer.valueOf(max.value()), getFieldName(field), lista);
		else if (field.getType().isAssignableFrom(Double.class))
			ValidatorUtil.validateMaxValue(getFieldValue(obj, field), Double.valueOf(max.value()), getFieldName(field), lista);
		else if (field.getType().isAssignableFrom(int.class))
			ValidatorUtil.validateMaxValue(getFieldValue(obj, field), Integer.parseInt(max.value()), getFieldName(field), lista);
		else
			ValidatorUtil.validateMaxValue(getFieldValue(obj, field), Double.parseDouble(max.value()), getFieldName(field), lista);
	}
	
	/**
	 * Valida os atributos que tiverem a anotação de validação {@link Min}.
	 * @param obj
	 * @param lista
	 */
	public static void validateMin(Field field, Object obj, Annotation annotation, ListaMensagens lista) {
		supportedTypes(field, Integer.class, Double.class, int.class, double.class);

		Min min = (Min) annotation;
		if (field.getType().isAssignableFrom(Integer.class))
			ValidatorUtil.validateMinValue(getFieldValue(obj, field), Integer.valueOf(min.value()), getFieldName(field), lista);
		else if (field.getType().isAssignableFrom(Double.class))
			ValidatorUtil.validateMinValue(getFieldValue(obj, field), Double.valueOf(min.value()), getFieldName(field), lista);
		else if (field.getType().isAssignableFrom(int.class))
			ValidatorUtil.validateMinValue(getFieldValue(obj, field), Integer.parseInt(min.value()), getFieldName(field), lista);
		else
			ValidatorUtil.validateMinValue(getFieldValue(obj, field), Double.parseDouble(min.value()), getFieldName(field), lista);
	}
	
	/**
	 * Valida os atributos que tiverem a anotação de validação {@link Pattern}.
	 * @param obj
	 * @param lista
	 */
	public static void validatePattern(Field field, Object obj, Annotation annotation, ListaMensagens lista) {
		supportedTypes(field, String.class);
				
		Pattern pat = (Pattern) annotation;
		ValidatorUtil.validatePattern((String) getFieldValue(obj, field), pat.value(), getFieldName(field), lista);
	}
	
	/**
	 * Valida os atributos que tiverem a anotação de validação {@link Range}.
	 * @param obj
	 * @param lista
	 */
	public static void validateRange(Field field, Object obj, Annotation annotation, ListaMensagens lista) {
		supportedTypes(field, Integer.class, Double.class, int.class, double.class);

		Range range = (Range) annotation;
		if (field.getType().isAssignableFrom(Integer.class))
			ValidatorUtil.validateRange(getFieldValue(obj, field), Integer.valueOf(range.min()), Integer.valueOf(range.max()), getFieldName(field), lista);
		else if (field.getType().isAssignableFrom(Double.class))
			ValidatorUtil.validateRange(getFieldValue(obj, field), Double.valueOf(range.min()), Double.valueOf(range.max()), getFieldName(field), lista);
		else if (field.getType().isAssignableFrom(int.class))
			ValidatorUtil.validateRange(getFieldValue(obj, field), Integer.parseInt(range.min()), Integer.parseInt(range.max()), getFieldName(field), lista);
		else
			ValidatorUtil.validateRange(getFieldValue(obj, field), Double.parseDouble(range.min()), Double.parseDouble(range.max()), getFieldName(field), lista);
	}
	
	/**
	 * Valida os atributos que tiverem a anotação de validação {@link Url}.
	 * @param obj
	 * @param lista
	 */
	public static void validateUrl(Field field, Object obj, Annotation annotation, ListaMensagens lista) {
		supportedTypes(field, String.class);
		ValidatorUtil.validateUrl((String) getFieldValue(obj, field), getFieldName(field), lista);
	}
	
	/**
	 * Valida os atributos que tiverem a anotação de validação {@link CpfCnpj}.
	 * @param obj
	 * @param lista
	 */
	public static void validateCpfCnpj(Field field, Object obj, Annotation annotation, ListaMensagens lista) {
		supportedTypes(field, String.class, Long.class, long.class);
		
		if (field.getType().isAssignableFrom(String.class)) {
			ValidatorUtil.validateCPF_CNPJ((String) getFieldValue(obj, field), getFieldName(field), lista);	
		} else {
			ValidatorUtil.validateCPF_CNPJ((Long) getFieldValue(obj, field), getFieldName(field), lista);
		}
	}

	/**
	 * Valida os atributos que tiverem a anotação de validação {@link Cep}.
	 * @param obj
	 * @param lista
	 */
	public static void validateCep(Field field, Object obj, Annotation annotation, ListaMensagens lista) {
		supportedTypes(field, String.class);
		ValidatorUtil.validateCEP((String) getFieldValue(obj, field), getFieldName(field), lista);
	}
}
