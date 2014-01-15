<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h2> <ufrn:subSistema/> > Relatório de Projetos com Redução de Bolsas </h2>
	<h:form id="formBusca">
		<table class="formulario" width="65%">
			<caption class="formulario">Dados da Busca</caption>
			<tbody>
				<tr>
			<td>
				<h:selectBooleanCheckbox value="#{relatorioReducaoBolsa.checkBuscaTitulo}" id="selectBuscaProjeto"/>
			</td>
		
			<td><label> Projeto: </label></td>
			<td>
				 <h:inputText id="tituloProjeto" value="#{ relatorioReducaoBolsa.buscaNomeProjeto }" size="60"  onchange="javascript:$('formBusca:selectBuscaProjeto').checked = true;"/>
			</td>
		</tr>	
		
		<tr>
			<td>
			<h:selectBooleanCheckbox value="#{relatorioReducaoBolsa.checkBuscaAno}" id="selectBuscaAno" />

			</td>
	    	<td> <label for="anoProjeto"> Ano do Projeto: </label> </td>
	    	<td> <h:inputText value="#{relatorioReducaoBolsa.buscaAnoProjeto}" size="5" onchange="javascript:$('formBusca:selectBuscaAno').checked = true;" 
	    			onkeyup="return formatarInteiro(this);" maxlength="4" title="Ano do Projeto" />
	    	</td>
	    </tr>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="3">
						<h:commandButton value="Buscar" action="#{relatorioReducaoBolsa.geraRelatorio}" /> 
						<h:commandButton value="Cancelar" action="#{relatorioReducaoBolsa.cancelar}" immediate="true" onclick="#{confirm}"/>
					</td>
				</tr>
			</tfoot>
		</table>
		
		<br />
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>