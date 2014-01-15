<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h2> <ufrn:subSistema/> > Sum�rio de Indices Acad�micos por Curso de Gradua��o</h2>
	<a4j:keepAlive beanName="relatorioDiscente"></a4j:keepAlive>
	<h:form id="form">
		<div class="descricaoOperacao">
			<p>Caso Usu�rio,</p>
			<p>Este formul�rio permite emitir o
			relat�rio <b>Sum�rio de �ndices Acad�micos por Curso de Gradua��o</b> que
			lista, por curso, a m�dia do �ndice acad�mico, os trancamentos por
			turma e trancamentos de programa, dentre outras informa��es.</p>
		</div>
		<table class="formulario" >
			<caption class="formulario">Informe os Par�metros do Relat�rio</caption>
			<tbody>
				<tr>
					<th class="required" width="35%">Ano Inicial:</th>
					<td >
						<h:inputText value="#{relatorioDiscente.ano}" size="4" maxlength="4" id="ano" onkeyup="return formatarInteiro(this);" converter="#{ intConverter }" />
					</td>
				</tr>
				<tr>
					<th class="required">Ano Final:</th>
					<td >
						<h:inputText value="#{relatorioDiscente.anoFinal}" size="4" maxlength="4" id="anoFinal" onkeyup="return formatarInteiro(this);" converter="#{ intConverter }" />
					</td>
				</tr>
				<tr>
					<th align="right">
						Centro/Unidade Acad�mica Especializada:
					</th>
					<td >
						<h:selectOneMenu value="#{ relatorioDiscente.unidade.id }" id="unidade" style="max-width: 75%;">
							<f:selectItem itemValue="0" itemLabel="-- TODOS --"/>
							<f:selectItems value="#{ unidade.allCentrosEscolasCombo }"/>
						</h:selectOneMenu>
					</td>
				</tr> 
				<tr>
					<th align="right">Modalidade de Educa��o:</th>
					<td >
						<h:selectOneMenu value="#{ relatorioDiscente.modalidadeEnsino.id }" id="modalidadeEnsino">
							<f:selectItem itemValue="0" itemLabel="-- TODOS --"/>
							<f:selectItems value="#{ modalidadeEducacao.allCombo }"/>
						</h:selectOneMenu>
					</td>
				</tr>
				<tr>
					<td align="right">
						<h:selectBooleanCheckbox value="#{relatorioDiscente.somenteCursosConvenio}" id="somenteCursosConvenio" />
					</td>
					<td >Somente cursos que possuem conv�nio.</td>
				</tr>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="Gerar Relat�rio" action="#{relatorioDiscente.relatorioSumarioIndicesAcademicos}" id="btnGerar"/> 
						<h:commandButton value="Cancelar" action="#{relatorioDiscente.cancelar}" onclick="#{confirm}" id="btnCancelar"/>
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>
  <br />
  <center>
	<html:img page="/img/required.gif" style="vertical-align: top;" />
	<span class="fontePequena">Campos de preenchimento obrigat�rio.</span>
	<br>
	<br>
  </center>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>