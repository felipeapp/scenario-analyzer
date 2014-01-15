/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criação em: 06/11/2007
 */
package br.ufrn.arq.web.tags;

import javax.servlet.jsp.JspException;

/**
 * Tag usada para gerar uma textarea com script para contar a quantidade de
 * caractres digitados e realizar a validação (via javascript) na submissão
 * do form. <br>
 * 
 * Parametro breakLine (default true), indica que um BR deve ser inserido
 * entre o textarea e o contador.
 * 
 * @author Rafael Borja
 * @author Gleydson Lima
 *
 */
public class TextareaTag extends org.apache.struts.taglib.html.TextareaTag {
	
	/** Flag indica se um BR deve ser colocado entre a textarea e o contador */
	private boolean breakLine = true;
	
	/** Flag indica se script de validação deve ser inserido automaticamente */
	private boolean validate = true;
	
	@Override
    protected String renderTextareaElement() throws JspException {
        StringBuffer results = new StringBuffer("<textarea");
                	
        results.append(" name=\"");
        // @since Struts 1.1
        if (indexed) {
            prepareIndex(results, name);
        }
        results.append(property);
        results.append("\"");
        if (accesskey != null) {
            results.append(" accesskey=\"");
            results.append(accesskey);
            results.append("\"");
        }
        if (tabindex != null) {
            results.append(" tabindex=\"");
            results.append(tabindex);
            results.append("\"");
        }
        if (cols != null) {
            results.append(" cols=\"");
            results.append(cols);
            results.append("\"");
        }
        if (rows != null) {
            results.append(" rows=\"");
            results.append(rows);
            results.append("\"");
        }
        
        /* Seta id aleatório caso o tamanho máximo tenha sido espeficado.
         * Método prepareStyles insere o id na tag. */
		if (maxlength != null) {
			if (getStyleId() == null)
					setStyleId("textArea" + (Math.random() * 1000000));
            
			results.append(" maxlength=\"");
            results.append(maxlength);
            results.append("\"");
		}
		
        results.append(prepareEventHandlers());
        results.append(prepareStyles());
        results.append(">");
        results.append(this.renderData());
        results.append("</textarea>");
       
        if (maxlength != null) {
	        // Span
        	if (breakLine == true)
        		results.append("<br>");
        	
	        results.append("<span id=\"" + getStyleId() + ".contador\"");
	        results.append(" style=\"overflow: visible; font-size: 6.4pt;"
					+ (breakLine ? "": " vertical-align: top;") + "\">");
	        	results.append("<span>");
	        	results.append(getMaxlength());
	        	results.append(		"</span>/");
	        results.append("</span>");
	
	        // Script para preparar textarea
	        results.append("<script type=\"text/javascript\" language=\"JavaScript\">");
	        results.append("setMaxLength(document.getElementById('");
	        results.append(getStyleId());
	        results.append("'), " + isValidate() + ");");
	        results.append("</script>"); 
        }
        
        return results.toString();
    }

	@Override
	public void release() {
		this.breakLine = true;
		this.validate = true;
		
		super.release();
	}

	public boolean isBreakLine() {
		return breakLine;
	}

	public void setBreakLine(boolean breakLine) {
		this.breakLine = breakLine;
	}

	public boolean isValidate() {
		return validate;
	}

	public void setValidate(boolean validate) {
		this.validate = validate;
	}
	
	// SCRIPTS PARA A TAG
	/*
	//
	// FUNÕES PARA TAG sipac:textArea
	//
	/**
	 * Função prepara um textArea para contagem de caracteres para 
	 * posterior validação. 
	 * - Seta o m?todo checkMaxLength para chamada onkeyup e onchange do textArea 
	 * passado como parametro.
	 * - Seta a o m?todo validateTextAreas
	 * para a chamada onsubmit do form se este n?o estiver setado.
	 *
	 * @param textArea objeto textArea a ser limitado
	 * @see br.ufrn.sipac.arq.tags.TextareaTag
	 * @author Rafael Borja
	 */
	/*
	function setMaxLength(textArea, validar)
	{
		var contadorDiv = document.getElementById(textArea.id + '.contador');
			if (textArea.getAttribute('maxlength'))
			{
				contadorDiv.innerHTML = '<span>0</span>/'+textArea.getAttribute('maxlength');
				
				textArea.contador = contadorDiv.getElementsByTagName('span')[0];
				textArea.divContador = contadorDiv;
				textArea.onkeyup = textArea.onchange = checkMaxLength;

				if (validar == true)
					textArea.form.onsubmit = validateTextAreas;
					
				textArea.onkeyup();
			}
	}

	/**
	 * Função para contagem de caracteres em um textArea
	 *
	 * @see br.ufrn.sipac.arq.tags.TextareaTag
	 * @author Rafael Borja
	 */
	/*
	function checkMaxLength()
	{
		var currentLength = this.value.length;
		var maxLength = this.getAttribute('maxlength');
		
		// Formata valor exibido (vermelho se ultrapassar maxlength
		if (currentLength > maxLength)
			this.divContador.style.color= 'red';
		else
			this.divContador.style.color= 'black';
			
		// Altera o valor exibido	
		this.contador.tamanhoAtual = currentLength;
		currentLength = currentLength + '';
		for (i=0; i < maxLength.length && currentLength.length < maxLength.length; i++)
			currentLength = '0' + currentLength;
		
		this.contador.firstChild.nodeValue = currentLength;
	}

	/**
	 * Função valida o tamanho de todos os texts areas que possuem
	 * o atributo maxlength.
	 * 
	 * @see setMaxLength
	 * @see checkMaxLength
	 * @auhor Rafael Borja 
	 */
	/*
	function validateTextAreas()
	{
		var x;
		try {
			x = form.getElementsByTagName('textarea');
		} catch (e) {
			form = this;
			x = form.getElementsByTagName('textarea');
		}
		
		for (var i=0;i<x.length;i++)
		{
			var tamanhoMax = x[i].getAttribute('maxlength');
			if (x[i].contador.tamanhoAtual > tamanhoMax) {
				alert('Texto ultrapassa tamanho permitido');
				return false;
			}
			else
				i = i;
		}
		return true;
	}

	//
	// FIM DE FUNÇÕES PARA TAG sipac:textArea
	//
	*/
}
