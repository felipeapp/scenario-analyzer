<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<f:view>
	
	<h2><ufrn:subSistema /> > Relatório de Rendimentos de Componentes</h2>
	<h:form id="formBuscaProjeto">


	<table class="formulario" width="65%">
	<caption>Dados da Busca</caption>
	<tbody>
	    
		 <tr>
		 	<td></td>
	    	<th class="obrigatorio"> <label for="nomeProjeto"> Departamento: </label> </th>
	    	
	    	<td>
	    	<h:selectOneMenu id="selectUnidade" value="#{relatorioRendimentoComponente.departamentoComponente.id}">
				<f:selectItem itemValue="0" itemLabel="-- SELECIONE --"  />
				<f:selectItems value="#{unidade.allDepartamentoUnidAcademicaCombo}"/>
			</h:selectOneMenu>
	    </tr>
	    		    
		 <tr>
		 	<td></td>
	    	<th class="obrigatorio"><label for="anoProjeto"> Período: </label></th>
	    	
	    	<td>
	    		<h:inputText  id="inputAnoInicio" value="#{relatorioRendimentoComponente.ano}" size="6" maxlength="4" onkeyup="formatarInteiro(this)" /> até
	    	
	    		<h:inputText id="inputAnoFim" value="#{relatorioRendimentoComponente.anoFim}" size="6" maxlength="4" onkeyup="formatarInteiro(this)" />
	    		<ufrn:help img="/img/ajuda.gif">Informe o ano inicial e ano final. Ex. 2011 até 2012</ufrn:help>
	    	</td>
	    </tr>
	    
	    <tr>
	    	<td><h:selectBooleanCheckbox value="#{relatorioRendimentoComponente.filtroCodigo}" id="checkCodigo" styleClass="noborder"/> </td>
			<td align="right" ><label for="checkCodigo" onclick="$('formBuscaProjeto:checkCodigo').checked = !$('formBuscaProjeto:checkCodigo').checked;">Código da Disciplina:</label></td>
			<td><h:inputText size="10" value="#{relatorioRendimentoComponente.codigo }"
					onfocus="$('formBusca:checkCodigo').checked = true;" onkeyup="CAPS(this)"  />
			</td>
	    </tr>
	    
	    <tr>
	    	<td><h:selectBooleanCheckbox value="#{relatorioRendimentoComponente.filtroDocente}" id="checkDocente" styleClass="noborder" /> </td>
	    	<td align="right" ><label for="checkDocente" onclick="$('formBuscaProjeto:checkDocente').checked = !$('formBuscaProjeto:checkDocente').checked;">Docente: </label></td>
	    	
	    	<td>
				<h:inputHidden id="idDocente" value="#{relatorioRendimentoComponente.docente.id}" /> 
				<h:inputText id="nomeDocente" value="#{relatorioRendimentoComponente.docente.pessoa.nome}" size="60" />

				<ajax:autocomplete source="formBuscaProjeto:nomeDocente" target="formBuscaProjeto:idDocente" baseUrl="/sigaa/ajaxDocente"
					className="autocomplete" indicator="indicator" minimumCharacters="3" parameters="tipo=ufrn,situacao=ativo"
					parser="new ResponseXmlToHtmlListParser()" /> 
				<span id="indicator" style="display: none;"> <img src="/sigaa/img/indicator.gif" /> </span> 
				<ufrn:help img="/img/ajuda.gif">Apenas os docentes do Quadro Permanente da ${ configSistema['siglaInstituicao'] } serão listados</ufrn:help>
			</td>
	    </tr>
	    
	    			
	    	
	    	
	    		    
	</tbody>
	<tfoot>
		<tr>
			<td colspan="3">
				<h:commandButton value="Gerar Relatório" action="#{ relatorioRendimentoComponente.gerarRelatorioRendimentoComponente }"/>
				<h:commandButton value="Cancelar" action="#{ relatorioRendimentoComponente.cancelar }" onclick="#{confirm}" />
	    	</td>
	    </tr>
	</tfoot>
	</table>	
		<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp"%>
	
	</h:form>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
