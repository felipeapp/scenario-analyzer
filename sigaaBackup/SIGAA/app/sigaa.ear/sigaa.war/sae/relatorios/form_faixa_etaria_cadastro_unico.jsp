<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>

<h2><ufrn:subSistema/> > Relat�rio de Discentes Priorit�rios Por Faixa Et�ria</h2>

	<h:form id="form">
	<table class="formulario" style="width: 60%">
			
		<caption>Informe os crit�rios para a emiss�o do relat�rio</caption>

		<tr>
			<th class="required" width="45%">Sexo:</th>
			<td colspan="2"> 
				<h:selectOneMenu value="#{relatorioCadUnicoFaixaEtaria.sexo}">
					<f:selectItem itemLabel="-- Selecione --" itemValue="-1"/>
					<f:selectItem itemLabel="Ambos" itemValue="0"/>
					<f:selectItems value="#{relatorioCadUnicoFaixaEtaria.mascFem}" />
				</h:selectOneMenu>
			</td>
		</tr>
				
		<tr>
			<th class="required">Ano-Per�odo:</th>
			<td>
				<h:inputText id="ano" value="#{relatorioCadUnicoFaixaEtaria.ano}" onkeyup="return formatarInteiro(this);" size="4" maxlength="4" />
				-
				<h:inputText id="periodo" value="#{relatorioCadUnicoFaixaEtaria.periodo}" onkeyup="return formatarInteiro(this);" size="2" maxlength="2" />
			</td>
		</tr>

		<tr>
			<th>Ano conclus�o-Per�odo conclus�o:</th>
			<td>
				<h:inputText id="anoConclusao" value="#{relatorioCadUnicoFaixaEtaria.anoConclusao}" onkeyup="return formatarInteiro(this);" size="4" maxlength="4" />
				-
				<h:inputText id="periodoConclusao" value="#{relatorioCadUnicoFaixaEtaria.periodoConclusao}" onkeyup="return formatarInteiro(this);" size="2" maxlength="2" />
			</td>
		</tr>

		<tr>
			<th>Somente Residentes:</th>
			<td>
				<h:selectBooleanCheckbox value="#{relatorioCadUnicoFaixaEtaria.residente}" />
			</td>
		</tr>

		<tfoot>
			<tr>
				<td colspan="2">
					<h:commandButton action="#{relatorioCadUnicoFaixaEtaria.gerar}" value="Emitir Relat�rio" />
					<h:commandButton action="#{relatorioCadUnicoFaixaEtaria.cancelar}" value="Cancelar" onclick="#{confirm}" immediate="true" />
				</td>
			</tr>
		</tfoot>
	</table>
	
	<center>
		<html:img page="/img/required.gif" style="vertical-align: top;" /> 
		<span class="fontePequena"> Campos de preenchimento obrigat�rio. </span> <br/>
	</center>
		
	</h:form>
	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>