<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<h2> <ufrn:subSistema/> > PID - Relatório por Semestre</h2>
<a4j:keepAlive beanName="relatorioPIDPorSemestre"/>
<h:form id="form">
<table align="center" class="formulario" width="70%">
	<caption class="listagem">Dados do Relatório</caption>
	<tr>
		<th class="obrigatorio"> Ano-Período: </th>
		<td><h:inputText id="ano" value="#{relatorioPIDPorSemestre.ano}" size="4" maxlength="4" 
					onkeyup="return formatarInteiro(this);" converter="#{ intConverter }" /> -
			<h:inputText id="periodo" value="#{relatorioPIDPorSemestre.periodo}" size="1" maxlength="1" 
					onkeyup="return formatarInteiro(this);" converter="#{ intConverter }" />
		</td>
	</tr>	
	
	<tfoot>
		<tr>
			<td colspan="3" align="center">
				<h:commandButton id="gerarRelatorio" value="Gerar Relatório" action="#{relatorioPIDPorSemestre.gerarRelatorio}"/> 
				<h:commandButton value="Cancelar" action="#{relatorioPIDPorSemestre.cancelar}" id="cancelar" onclick="#{confirm}" />
			</td>
		</tr>
	</tfoot>
</table>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>