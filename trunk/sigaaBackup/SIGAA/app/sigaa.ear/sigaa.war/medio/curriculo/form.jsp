<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<script type="text/javascript">
	JAWR.loader.script('/javascript/prototype-1.6.0.3.js');
</script>
<style>
	.negativo{ text-align: right; color: red; }
	.positivo{ text-align: right; }
</style>
<f:view>
<a4j:keepAlive beanName="curriculoMedio"/>
<h2> <ufrn:subSistema /> &gt; ${curriculoMedio.obj.id == 0 ? 'Cadastrar' : 'Alterar'} Estrutura Curricular</h2>

<h:form id="form">
	<table class="formulario" style="width: 90%">
	  <caption>Dados da Estrutura Curricular de Ensino Médio</caption>
		<h:inputHidden value="#{curriculoMedio.obj.id}" />
		
			<tr>
				<th class="obrigatorio" width="40%">Código do Currículo:</th>
				<td><h:inputText value="#{curriculoMedio.obj.codigo}" onkeyup="return formatarInteiro(this);" size="10" maxlength="10" /></td>
			</tr>
			<tr>
				<th class="obrigatorio">Curso:</th>
				<td colspan="5">
				<a4j:region>
					<h:selectOneMenu value="#{curriculoMedio.obj.cursoMedio.id}" id="selectCurso"
						valueChangeListener="#{curriculoMedio.carregarSeriesByCurso }" style="width: 95%">
						<f:selectItem itemValue="0" itemLabel=" --- SELECIONE --- " />
				 		<f:selectItems value="#{cursoMedio.allCombo}" /> 
				 		<a4j:support event="onchange" reRender="selectSerie" />
					</h:selectOneMenu>
					<a4j:status>
			                <f:facet name="start"><h:graphicImage  value="/img/indicator.gif"/></f:facet>
		            </a4j:status>
				</a4j:region>
				</td>
			</tr>
			<tr>
				<th class="obrigatorio">Série:</th>
				<td>
					<h:selectOneMenu value="#{ curriculoMedio.obj.serie.id }" style="width: 95%;" id="selectSerie">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{ curriculoMedio.seriesByCurso }" />
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<th class="obrigatorio">Carga Horária Total:</th>
				<td>
					<a4j:region>
					<h:inputText id="carga_horaria" value="#{curriculoMedio.obj.cargaHoraria}" size="5" maxlength="4" onkeyup="formatarInteiro(this);">
					<a4j:support id="ajax_total" event="onkeyup" actionListener="#{curriculoMedio.calcularChTotal}" reRender="chTotal,chRestante"/>
					</h:inputText>
					</a4j:region> 
				</td>
			</tr>
			<tr>
				<th class="obrigatorio">Ano de Entrada em Vigor:</th>
				<td>
					<h:inputText value="#{curriculoMedio.obj.anoEntradaVigor}" size="4" maxlength="4" 
					onkeyup="return formatarInteiro(this);"/> 
				</td>
			</tr>
			<tr>
				<th class="obrigatorio">Prazo de Conclusão:</th>
				<td>
					<h:selectOneMenu value="#{curriculoMedio.obj.unidadeTempo.id}" id="unidadeTempo" >
						<f:selectItem itemValue="0" itemLabel=" --- SELECIONE --- " />
				 		<f:selectItems value="#{curriculoMedio.allUnidadeTempo}" />
				 	</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<th> Ativo: </th>
				<td>
					<h:selectBooleanCheckbox id="checkAtivo" value="#{curriculoMedio.obj.ativo}" styleClass="noborder" /> 
				</td>
			</tr>
			<tr>
				<td colspan="2">
					<table class="subFormulario" style="width: 100%">
						<caption>Adicione Disciplinas a Estrutura Curricular</caption>
							<tr>
								<th width="25%" class="obrigatorio">Disciplina:</th>
								<td>
									<a4j:outputPanel>
										<h:inputHidden id="idDisciplina" value="#{curriculoMedio.curriculoDisciplina.componente.id}"/>
										<h:inputText value="#{curriculoMedio.curriculoDisciplina.componente.nome}" id="nomeComponente" style="width: 500px;"/> 
										<rich:suggestionbox id="sbComponenteCurricular" width="400" height="120" for="nomeComponente" 
											minChars="3" frequency="0" ignoreDupResponses="true" selfRendered="true" requestDelay="100"
											suggestionAction="#{componenteCurricular.autocompleteComponenteCurricular}"  var="_componente" 
											fetchValue="#{_componente.nome}" nothingLabel="Nenhuma Disciplina Encontrada.">
											<h:column>
												<h:outputText value="#{_componente.codigo}"/><h:outputText value="  -  "/>
												<h:outputText value="#{_componente.nome}"/><h:outputText value="  -  "/>
												<h:outputText value="#{_componente.unidade.sigla}"/>
											</h:column>
											<f:param name="apenasDepartamento" value="false"/>
											<f:param name="nivelPermitido" value="M"/>
											<a4j:support event="onselect" reRender="form" actionListener="#{curriculoMedio.selecionarDisciplina}">
												<f:param name="apenasDepartamento" value="false"/>
												<f:param name="nivelPermitido" value="M"/>
												<f:attribute name="componente" value="#{_componente}"/>
											</a4j:support>
										</rich:suggestionbox>	
							            <a4j:status id="statusComponente">
							                <f:facet name="start">&nbsp;</f:facet>
							            </a4j:status>
						           	</a4j:outputPanel>
									<h:commandLink id="addDisciplina" action="#{curriculoMedio.adicionarDisciplina}">
										<h:graphicImage value="/img/adicionar.gif" style="overflow: visible;" title="Adicionar Disciplina"/>
									</h:commandLink>
								</td>
							</tr>
					</table>
				</td>
			</tr>
		
	</table>
	<br/><br/>
	
	<div class="infoAltRem" style="width:90%">
		<html:img page="/img/delete.gif" style="overflow: visible;"/>: Remover Disciplina
	</div>
	
	<table id="tableDisciplinas" class="listagem" style="width: 90%">
		<caption>Disciplinas vinculadas na Estrutura Curricular</caption>
		<thead>
			<tr>
				<th>Disciplinas Adicionadas</th>
				<th style="text-align: right;" class="obrigatorio">CH Anual</th>
				<th></th>
			</tr>
		</thead>
		<c:set var="chTotal" value="0" />
		<tbody>	
			<c:forEach var="linha" items="#{curriculoMedio.curriculoDisciplinas}" varStatus="status">
				<tr class="${ status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
					<td>${linha.componente.codigo} - ${linha.componente.nome}</td>
					<td style="text-align: right;">
						<h:inputText value="#{linha.chAno}" size="4" maxlength="4" style="text-align: right;" onkeyup="formatarInteiro(this);"/> h
						<a4j:support event="onkeyup" actionListener="#{curriculoMedio.calcularChTotal}" reRender="chPreenchida,chRestante,chTotal"/>
					</td>
					<td width="5%" align="right">
						<h:commandLink action="#{curriculoMedio.removerDisciplina}" id="removerDisciplina">
							<h:graphicImage value="/img/delete.gif" style="overflow: visible;" title="Remover Disciplina" />
							<f:param name="id" value="#{linha.componente.id}"/>
						</h:commandLink>
					</td>
				</tr>		
			</c:forEach>
				<tr>
					<td style="text-align: right;"><b>Carga Horária Total:</b></td>
					<td style="text-align: right;"><h:outputText id="chTotal" value="#{curriculoMedio.obj.cargaHoraria}"/> h</td>
					<td></td>
				</tr>
				<tr>
					<td style="text-align: right;"><b>Carga Horária Preenchida:</b></td>
					<td style="text-align: right;"><h:outputText id="chPreenchida" value="#{curriculoMedio.chPreenchida}"/> h </td>
					<td></td>
				</tr>
				<tr>
					<td style="text-align: right;"><b>Carga Horária Restante:</b></td>
					<td style="text-align: right;"><h:outputText styleClass="#{ curriculoMedio.chRestante < 0 ? 'negativo':'positivo' }" id="chRestante" value="#{curriculoMedio.chRestante}"/> h </td>
					<td></td>
				</tr>
		</tbody>		
		<tfoot>
		   	<tr>
				<td colspan="3" align="center">
					<c:if test="${ curriculoMedio.obj.id > 0 }">
						<h:commandButton value="<< Voltar" action="#{curriculoMedio.listar}" id="voltar"/>
					</c:if>
					<h:commandButton value="Cancelar" action="#{curriculoMedio.cancelar}" onclick="#{confirm}" id="cancelar" />
					<h:commandButton value="Avançar >>" action="#{curriculoMedio.submeterCurriculo}" id="avancar" />
				</td>
		   	</tr>
		</tfoot>
	</table>
	
	<br/>
	<div class="obrigatorio"> Campos de preenchimento obrigatório. </div>
	
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>