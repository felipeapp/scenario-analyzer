<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>

<h2> <ufrn:subSistema /> > Relat�rio de Docentes Doutores sem Atividades na P�s Gradua��o </h2>

<h:form id="form">

<div class="descricaoOperacao">Este relat�rio lista os docentes com
		titula��o de Doutor que n�o tem orientandos acad�micos e n�o est�o
		cadastrados como docentes de turmas de p�s gradua��o, de acordo com os
		crit�rios informados.</div>

<table class="formulario" style="width: 55%">
<caption> Informe os Crit�rios para a Emiss�o do Relat�rio </caption>
	<tbody>
	<tr>
		<th class="required">Departamento: </th>
		<td>
			<h:selectOneMenu value="#{ relatorioAtividadesDocente.idUnidade }" id="selecaoDepartamento">
				<f:selectItem itemValue="0" itemLabel="TODOS"/>
				<f:selectItems value="#{ unidade.allDeptosEscolasCombo }"/>
			</h:selectOneMenu>
		</td>
	</tr>
	<tr>
		<th class="required">Ano-Per�odo: </th>
		<td>
			<h:inputText value="#{relatorioAtividadesDocente.ano}" onkeyup="return formatarInteiro(this);" 
				id="ano" size="4" maxlength="4" converter="#{ intConverter }"/>-
			<h:inputText value="#{relatorioAtividadesDocente.periodo}" onkeyup="return formatarInteiro(this);"
				id="periodo" size="1" maxlength="1" converter="#{ intConverter }"/>			
		</td>
	</tr>
</tbody>
	<tfoot>
	<tr>
		<td colspan="2">
			<h:commandButton action="#{relatorioAtividadesDocente.gerarRelatorioSemAtividades}" value="Emitir Relat�rio" id="btnGerarRelatorio"/>
			<h:commandButton action="#{relatorioAtividadesDocente.cancelar}" value="Cancelar" id="btnCancelar"/>
		</td>
	</tr>
	</tfoot>
</table>
<br/>
<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp"%>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>