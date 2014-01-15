<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h2> <ufrn:subSistema/> > Sumário de Indices Acadêmicos por Curso de Graduação</h2>
	<a4j:keepAlive beanName="relatorioDiscente"></a4j:keepAlive>
	<h:form id="form">
		<div class="descricaoOperacao">
			<p>Caso Usuário,</p>
			<p>Este formulário permite emitir o
			relatório <b>Sumário de Índices Acadêmicos por Curso de Graduação</b> que
			lista, por curso, a média do índice acadêmico, os trancamentos por
			turma e trancamentos de programa, dentre outras informações.</p>
		</div>
		<table class="formulario" >
			<caption class="formulario">Informe os Parâmetros do Relatório</caption>
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
						Centro/Unidade Acadêmica Especializada:
					</th>
					<td >
						<h:selectOneMenu value="#{ relatorioDiscente.unidade.id }" id="unidade" style="max-width: 75%;">
							<f:selectItem itemValue="0" itemLabel="-- TODOS --"/>
							<f:selectItems value="#{ unidade.allCentrosEscolasCombo }"/>
						</h:selectOneMenu>
					</td>
				</tr> 
				<tr>
					<th align="right">Modalidade de Educação:</th>
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
					<td >Somente cursos que possuem convênio.</td>
				</tr>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="Gerar Relatório" action="#{relatorioDiscente.relatorioSumarioIndicesAcademicos}" id="btnGerar"/> 
						<h:commandButton value="Cancelar" action="#{relatorioDiscente.cancelar}" onclick="#{confirm}" id="btnCancelar"/>
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>
  <br />
  <center>
	<html:img page="/img/required.gif" style="vertical-align: top;" />
	<span class="fontePequena">Campos de preenchimento obrigatório.</span>
	<br>
	<br>
  </center>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>