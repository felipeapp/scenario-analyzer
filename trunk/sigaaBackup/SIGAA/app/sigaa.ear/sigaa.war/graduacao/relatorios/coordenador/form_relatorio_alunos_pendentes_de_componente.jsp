<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<h2 class="tituloPagina"> <ufrn:subSistema/> &gt; ${relatorioAlunosPendentesDeComponente.nomeRelatorio} </h2>
<f:view>
	<h:form id="form">
		<c:set var="relatorio" value="#{relatorioAlunosPendentesDeComponente}"/>
		<a4j:keepAlive beanName="relatorioAlunosPendentesDeComponente" />
		<table align="center" class="formulario" width="50%">
			<caption class="listagem">Dados do Relatório</caption>
			<tbody>
				<tr>
					<th class="obrigatorio" style="white-space: nowrap;">Componente Curricular: </th>
					<td>
						<h:inputHidden id="idDisciplina"   value="#{relatorio.disciplina.id}"></h:inputHidden>
						<h:inputText   id="nomeDisciplina" value="#{relatorio.disciplina.nome}" size="60" label="Componente Curricular" />
						<ajax:autocomplete
								source="form:nomeDisciplina" target="form:idDisciplina"
								baseUrl="/sigaa/ajaxDisciplina" className="autocomplete"
								indicator="indicatorComponenteCurricular" minimumCharacters="3" parameters="nivel=G"
								parser="new ResponseXmlToHtmlListParser()" />
						<span id="indicatorComponenteCurricular" style="display:none; "> <img src="/sigaa/img/indicator.gif" /> </span>
					</td>
				</tr>
				<c:if test="${not relatorio.coordenadorCurso}">
				<tr>
					<th style="white-space: nowrap;">Curso / Matriz Curricular: </th>
					<td>
						<h:selectOneMenu value="#{relatorio.matrizCurricular.id}" id="matriz" style="width: 450px">
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
							<f:selectItems value="#{matrizCurricular.allCombo}" />
						</h:selectOneMenu>
					</td>
				</tr>
				</c:if>
				<tr>
					<th style="white-space: nowrap;">Ano-Período de Ingresso:</th>
					<td>
						<h:inputText value="#{relatorio.ano}"     id="anoDeIngresso"     size="4" maxlength="4"
								onkeyup="return formatarInteiro(this);"/>&nbsp;
						<h:inputText value="#{relatorio.periodo}" id="periodoDeIngresso" size="1" maxlength="1"
								onkeyup="return formatarInteiro(this);" />
					</td>
				</tr>
				<tr>
					<th></th>
					<td style="text-align: left"> 
						<h:selectBooleanCheckbox value="#{relatorio.filtroPreRequisitos}" styleClass="noborder" id="checkPreRequisitos" />
						<label>	Listar alunos aptos a cursar o componente </label>
					</td>
				</tr>
				<tr>
					<th></th>
					<td style="text-align: left"> 
						<h:selectBooleanCheckbox value="#{relatorio.filtroPossiveisAptos}" styleClass="noborder" id="checkPossiveisAptos" />
						<label>	
							Listar alunos prováveis a aptos para cursar o componente 
							<ufrn:help img="/img/ajuda.gif">
								Lista os alunos matriculados em componentes que sejam pré-requisitos 
								para o componente solicitado no relatório, que se concluírem serão 
								considerados como aptos a pagar o componente.
							</ufrn:help>
						</label>
					</td>
				</tr>
				<tr>
					<th></th>
					<td style="text-align: left"> 
						<h:selectBooleanCheckbox value="#{relatorio.filtroMatriculado}" styleClass="noborder" id="checkMatriculados" />
						<label>	Listar alunos matriculados no componente no período atual </label>
					</td>		
				</tr>
			</tbody>
			
			<tfoot>
				<tr>
					<td colspan="2" >
						<h:commandButton value="Gerar Relatório" action="#{relatorio.gerar}" id="gerarRelatorio" />
						<h:commandButton value="Cancelar" action="#{relatorio.cancelar}" onclick="#{confirm}" id="cancelarRelatorio" />
					</td>
				</tr>
			</tfoot>
		</table>
		<div class="obrigatorio" style="width: 90%">Campos de preenchimento obrigatório.</div>
	</h:form>	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp" %>