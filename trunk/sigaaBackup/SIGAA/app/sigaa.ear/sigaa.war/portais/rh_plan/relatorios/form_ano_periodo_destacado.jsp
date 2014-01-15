<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<h:messages />
<h2><ufrn:subSistema /> > Relatório de Acompanhamento de Desempenho Acadêmico de Bolsistas</h2>

<h:form id="form">
<h:inputHidden value="#{relatorioAcompanhamentoBolsas.tipoConsulta}"/>
<table class="formulario" width="80%">
	<caption>Dados do Relatório</caption>
	<tbody>
		<tr>
			<td width="5%"><h:selectBooleanCheckbox value="#{relatorioAcompanhamentoBolsas.checkBolsista}" styleClass="noborder" id="checkBolsista"/></td>
			<td width="18%">Ano-Período:</td>
			<td>
				<h:inputText id="ano" value="#{relatorioAcompanhamentoBolsas.ano}" size="4" maxlength="4" onkeyup="formatarInteiro(this)" 
					converter="#{ intConverter }" onchange="Field.check('form:checkBolsista')"/> -
				<h:inputText id="periodo" value="#{relatorioAcompanhamentoBolsas.periodo}" size="1" maxlength="1" onkeyup="formatarInteiro(this)" 
					converter="#{ intConverter }" onchange="Field.check('form:checkBolsista')"/>
				<ufrn:help>Retorna todos os bolsistas que estão contemplatados até o ano é período informado.</ufrn:help>
			</td>
		</tr>
		<tr>
			<td><h:selectBooleanCheckbox value="#{relatorioAcompanhamentoBolsas.checkIngresso}" styleClass="noborder" id="checkIngresso"/></td>
			<td>Ano-Período Ingresso:</td>
			<td>
				<h:inputText id="anoIngresso" value="#{relatorioAcompanhamentoBolsas.anoIngresso}" size="4" maxlength="4" onkeyup="formatarInteiro(this)" 
					converter="#{ intConverter }" onchange="Field.check('form:checkIngresso')"/> -
				<h:inputText id="periodoIngresso" value="#{relatorioAcompanhamentoBolsas.periodoIngresso}" size="1" maxlength="1" onkeyup="formatarInteiro(this)" 
					converter="#{ intConverter }" onchange="Field.check('form:checkIngresso')"/>
				<ufrn:help>Ano é período que o discente recebeu a bolsa ou o benefício.</ufrn:help>
			</td>
		</tr>
		<tr>
			<td><h:selectBooleanCheckbox value="#{relatorioAcompanhamentoBolsas.checkTipoBolsa}" styleClass="noborder" id="checkTipoBolsa"/></td>
			<td>Tipo Bolsa:</td>
			<td>
				<h:selectOneMenu value="#{ relatorioAcompanhamentoBolsas.tipoBolsaAuxilio.id }" id="idTipoBolsa" onchange="Field.check('form:checkTipoBolsa')">
					<f:selectItem itemLabel="-- SELECIONE -- " itemValue="0"/>
					<f:selectItems value="#{relatorioAcompanhamentoBolsas.allComboBolsaAuxilio}"/>
				</h:selectOneMenu>
			</td>
		</tr>
		<tr>
			<td><h:selectBooleanCheckbox value="#{relatorioAcompanhamentoBolsas.checkSituacaoBolsa}" styleClass="noborder" id="checkSituacaoBolsa"/></td>
			<td>Situação da Bolsa: </td>
			<td>
				<h:selectOneRadio value="#{relatorioAcompanhamentoBolsas.situacaoBolsaAuxilio.id}" layout="pageDirection" 
					onchange="Field.check('form:checkSituacaoBolsa')">
					<f:selectItem itemLabel="Em Análise" itemValue="1" />
					<f:selectItem itemLabel="Bolsa Deferida e Contemplada" itemValue="2" />
					<f:selectItem itemLabel="Fila de Espera" itemValue="3" />
					<f:selectItem itemLabel="Bolsa Indeferida" itemValue="4" />
					<f:selectItem itemLabel="B.A.C E B.M.E" itemValue="6" />
					<f:selectItem itemLabel="Bolsa Finalizada" itemValue="7" />
				</h:selectOneRadio>
			</td>
		</tr>
		<tr>
			<td></td>
			<td>Condição:</td>
			<td colspan="2">
				<t:selectBooleanCheckbox value="#{relatorioAcompanhamentoBolsas.somenteDestacados}"/>
				Listar somente discentes que estão abaixo do IECH ou IEPL do curso.
			</td>
		</tr>
	</tbody>
	<tfoot>
		<tr>
			<td colspan="3" align="center">
				<h:commandButton id="gerarRelatorio" value="Gerar Relatório" action="#{relatorioAcompanhamentoBolsas.gerarRelatorio}"/>
				<h:commandButton value="Cancelar" action="#{relatorioAcompanhamentoBolsas.cancelar}" id="cancelar" 
					onclick="return confirm('Deseja cancelar a operação? Todos os dados digitados não salvos serão perdidos!');"> 
				</h:commandButton>
			</td>
		</tr>
	</tfoot>
</table>
<br/>
<center>
	<html:img page="/img/required.gif" style="vertical-align: top;" /> 
	<span class="fontePequena"> Campos de preenchimento obrigatório. </span>
</center>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>