<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<h:messages />
<h2><ufrn:subSistema /> > Relat�rio de Acompanhamento de Desempenho Acad�mico de Bolsistas</h2>

<h:form id="form">
<h:inputHidden value="#{relatorioAcompanhamentoBolsas.tipoConsulta}"/>
<table class="formulario" width="80%">
	<caption>Dados do Relat�rio</caption>
	<tbody>
		<tr>
			<td width="5%"><h:selectBooleanCheckbox value="#{relatorioAcompanhamentoBolsas.checkBolsista}" styleClass="noborder" id="checkBolsista"/></td>
			<td width="18%">Ano-Per�odo:</td>
			<td>
				<h:inputText id="ano" value="#{relatorioAcompanhamentoBolsas.ano}" size="4" maxlength="4" onkeyup="formatarInteiro(this)" 
					converter="#{ intConverter }" onchange="Field.check('form:checkBolsista')"/> -
				<h:inputText id="periodo" value="#{relatorioAcompanhamentoBolsas.periodo}" size="1" maxlength="1" onkeyup="formatarInteiro(this)" 
					converter="#{ intConverter }" onchange="Field.check('form:checkBolsista')"/>
				<ufrn:help>Retorna todos os bolsistas que est�o contemplatados at� o ano � per�odo informado.</ufrn:help>
			</td>
		</tr>
		<tr>
			<td><h:selectBooleanCheckbox value="#{relatorioAcompanhamentoBolsas.checkIngresso}" styleClass="noborder" id="checkIngresso"/></td>
			<td>Ano-Per�odo Ingresso:</td>
			<td>
				<h:inputText id="anoIngresso" value="#{relatorioAcompanhamentoBolsas.anoIngresso}" size="4" maxlength="4" onkeyup="formatarInteiro(this)" 
					converter="#{ intConverter }" onchange="Field.check('form:checkIngresso')"/> -
				<h:inputText id="periodoIngresso" value="#{relatorioAcompanhamentoBolsas.periodoIngresso}" size="1" maxlength="1" onkeyup="formatarInteiro(this)" 
					converter="#{ intConverter }" onchange="Field.check('form:checkIngresso')"/>
				<ufrn:help>Ano � per�odo que o discente recebeu a bolsa ou o benef�cio.</ufrn:help>
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
			<td>Situa��o da Bolsa: </td>
			<td>
				<h:selectOneRadio value="#{relatorioAcompanhamentoBolsas.situacaoBolsaAuxilio.id}" layout="pageDirection" 
					onchange="Field.check('form:checkSituacaoBolsa')">
					<f:selectItem itemLabel="Em An�lise" itemValue="1" />
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
			<td>Condi��o:</td>
			<td colspan="2">
				<t:selectBooleanCheckbox value="#{relatorioAcompanhamentoBolsas.somenteDestacados}"/>
				Listar somente discentes que est�o abaixo do IECH ou IEPL do curso.
			</td>
		</tr>
	</tbody>
	<tfoot>
		<tr>
			<td colspan="3" align="center">
				<h:commandButton id="gerarRelatorio" value="Gerar Relat�rio" action="#{relatorioAcompanhamentoBolsas.gerarRelatorio}"/>
				<h:commandButton value="Cancelar" action="#{relatorioAcompanhamentoBolsas.cancelar}" id="cancelar" 
					onclick="return confirm('Deseja cancelar a opera��o? Todos os dados digitados n�o salvos ser�o perdidos!');"> 
				</h:commandButton>
			</td>
		</tr>
	</tfoot>
</table>
<br/>
<center>
	<html:img page="/img/required.gif" style="vertical-align: top;" /> 
	<span class="fontePequena"> Campos de preenchimento obrigat�rio. </span>
</center>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>