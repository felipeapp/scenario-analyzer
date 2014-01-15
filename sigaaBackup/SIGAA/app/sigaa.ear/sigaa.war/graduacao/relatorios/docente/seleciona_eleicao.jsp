<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@page import="br.ufrn.arq.seguranca.SigaaSubsistemas"%>

<f:view>
<h2><ufrn:subSistema /> > Relat�rio de Docentes para Elei��o</h2>
<h:form id="form">

<table align="center" class="formulario" width="100%">
	<caption class="listagem">Dados do Relat�rio</caption>
	<tbody>
		<tr>
			<th class="rotulo">Curso: </tg>
			<td><h:outputText value="#{relatorioDiscente.cursoAtualCoordenacao.unidade.sigla}"/> - <h:outputText value="#{relatorioDiscente.cursoAtualCoordenacao.nome}"/></td>
		</tr>
		<tr>
			<th class="obrigatorio">Ano-Per�odo:</th>
			<td>
				<h:inputText value="#{relatorioEleicaoCoordenadoMBean.ano}" onkeyup="return formatarInteiro(this);" 
					id="ano" size="4" maxlength="4" converter="#{ intConverter }"/>-
				<h:inputText value="#{relatorioEleicaoCoordenadoMBean.periodo}" onkeyup="return formatarInteiro(this);"
					id="periodo" size="1" maxlength="1" converter="#{ intConverter }"/>			
			</td>
		</tr>
	</tbody>
	<tfoot>
		<tr>
			<td colspan="2">
				<h:commandButton id="gerarRelatorio" value="Gerar Relat�rio" action="#{relatorioEleicaoCoordenadoMBean.gerarListaDocenteEleicaoCoordenador}"/> 
				<h:commandButton id="Cancelar" value="Cancelar" action="#{relatorioDiscente.cancelar}"  onclick="#{confirm}"/>
			</td>
		</tr>
	</tfoot>
</table>
</h:form>
</f:view>
<br>
	<center>
		<html:img page="/img/required.gif" style="vertical-align: top;" /> 
		<span class="fontePequena"> Campos de preenchimento obrigat�rio. </span> <br>
	</center>
	
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>