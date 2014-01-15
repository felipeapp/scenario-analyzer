<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<style>
	.colIcone{width: 25px !important;}
</style>

<f:view>
	<h2><ufrn:subSistema /> > Cadastro de Processo Seletivo</h2>
	
	<c:if test="${processoSeletivo.acessoProcessoSeletivoStricto}">
		<div class="descricaoOperacao"> 
		 	Caro Usuário,
		 	<p>
		 	Caso clique em "Salvar", o Processo Seletivo ficará aberto para modificações. Já se "Submeter" 
		 	será enviado para avaliação da PPG, não podendo mais ser alterado pelo programa.
		 	</p>
		</div>		
	</c:if>

	<h:form id="formCursos" prependId="true">
		
		<table class="formulario" style="width: 100%;">
			<caption class="listagem">Cursos do Processo Seletivo</caption>
			<tbody>
			<tr>
				<th width="15%" valign="bottom" class="required">Curso:</th>
				<td >					
					<h:selectOneMenu id="matrizCurricular" value="#{processoSeletivo.cursoSel}" style="width:95%;"
						valueChangeListener="#{matrizCurricular.carregarMatrizCurricular}" onchange="submit();">
							<f:selectItem itemLabel="-- SELECIONE --" itemValue="0" />
							<f:selectItems value="#{curso.allCursosUsuarioCombo}"/>
					</h:selectOneMenu>
				</td>
				
			</tr>
			
			<c:if test="${not empty matrizCurricular.matrizesCurriculares}">
				<tr>
					<th class="required" width="15%" valign="bottom">Matriz Curricular:</th>
					<td align="left" width="370px" colspan="3">
						<h:selectOneMenu value="#{processoSeletivo.matrizCurricularSel}"
						  style="width:99%;" id="idMatrizCurricular" >
						<f:selectItem itemLabel="-- SELECIONE --" itemValue="0" />
						<f:selectItems value="#{matrizCurricular.matrizesCurriculares}"/>
						</h:selectOneMenu>
					</td>
					
				</tr>
			</c:if>
			
			<c:set var="possiveisQuestionarios" value="#{processoSeletivo.possiveisQuestionarios}" />
			<c:if test="${ fn:length(possiveisQuestionarios) > 0}">
				<tr>
					<th>Questionário:</th>
					<td>
						<h:selectOneMenu value="#{processoSeletivo.questionarioSel}" id="questionarioEspecifico" style="width:95%;">
						<f:selectItems value="#{possiveisQuestionarios}"/>
					</h:selectOneMenu>
					</td>				
				</tr>
			</c:if>
			<tr>
				<th class="required">Nº de Vagas:</th>
				<td>
					<h:inputText id="vagas" size="3" maxlength="3" 
					value="#{processoSeletivo.vagasInput}" onkeyup="return formatarInteiro(this);" converter="#{ intConverter }" 
					readonly="#{processoSeletivo.possivelAlterarNumVagas}"/>
				</td>
			</tr>
			</tbody>
			<tfoot>	
				<tr>
					<td colspan="6" align="center">
						<h:commandButton value="Adicionar à lista" action="#{processoSeletivo.adicionarProcessos}" />
					</td>
				</tr>
			</tfoot>	
		</table>
		<br clear="all"/>
				
		<%-- INÍCIO DA LISTAGEM DAS MATRIZES SELECIONADAS --%>
		
		<div class="infoAltRem">
			<h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover 
		</div>
		<h:dataTable id="listaOfertaCursoVaga" styleClass="listagem" columnClasses="colTitulo,colTitulo,colTitulo,colTitulo,colIcone" rowClasses="linhaPar, linhaImpar" 
			value="#{processoSeletivo.processosSeletivos}" var="item">
		
			<f:facet name="caption"><f:verbatim>Lista de Cursos</f:verbatim></f:facet>
		
			<t:column rendered="#{item.ativo}">
				<f:facet name="header"><f:verbatim>Curso</f:verbatim></f:facet>
				<h:outputText value="#{item.matrizCurricular.descricao} (#{item.matrizCurricular.curso.municipio.nome})" 
					rendered="#{not empty item.matrizCurricular}"/>
				<h:outputText value="#{item.curso.nome}" rendered="#{not empty item.curso}"/>
			</t:column>
			
			<t:column rendered="#{item.ativo}">
				<f:facet name="header"><f:verbatim>Nível</f:verbatim></f:facet>
				<h:outputText value="#{item.curso.nivelDescricao}"/>
			</t:column>
			
			<t:column rendered="#{item.ativo}">
				<f:facet name="header"><f:verbatim>Questionário</f:verbatim></f:facet>
				<h:outputText value="#{item.questionario.titulo}"/>
			</t:column>
			
			<t:column rendered="#{item.ativo}">
				<f:facet name="header"><f:verbatim>Vagas</f:verbatim></f:facet>
				<h:inputText id="itemVagas" size="3" maxlength="3" value="#{item.vaga}" readonly="#{processoSeletivo.latoSensu}" 
					onkeyup="return formatarInteiro(this);" converter="#{ intConverter }" >
				</h:inputText>
			</t:column>
			
			<t:column styleClass="colIcone" rendered="#{item.ativo}">
				<f:facet name="header"><f:verbatim>&nbsp;</f:verbatim></f:facet>
				
				<h:commandLink actionListener="#{processoSeletivo.removerProcessos}" onclick="#{confirmDelete}" >
					<h:graphicImage value="/img/delete.gif" style="overflow: visible;" title="Remover" />
					<f:attribute name="itemProcessoSeletivo" value="#{item}"/>
					<a4j:support reRender="listaOfertaCursoVaga" />
				</h:commandLink>
				
			</t:column>
			
			<f:facet name="footer"></f:facet>
		</h:dataTable>

	<%-- FIM DA LISTAGEM DAS MATRIZES SELECIONADAS --%>
		<table class="formulario" style="width: 100%;">	
			<tfoot>
				<tr>
					<td colspan="4">
						<c:choose>
							<c:when test="${processoSeletivo.acessoProcessoSeletivoStricto || processoSeletivo.acessoProcessoSeletivoLato}">
								<h:commandButton value="Salvar" action="#{processoSeletivo.salvar}" />
								<h:commandButton value="Salvar e Submeter" action="#{processoSeletivo.submeter}" />
							</c:when>
							<c:otherwise>
								<h:commandButton value="#{processoSeletivo.confirmButton}" 
								action="#{processoSeletivo.cadastrar}" />
							</c:otherwise>
						</c:choose>	
						
						<c:choose>
							<c:when test="${processoSeletivo.acessoProcessoSeletivoGraduacao && processoSeletivo.obj.editalProcessoSeletivo.possuiAgendamento}">
								<h:commandButton value="<< Períodos de Agendamento " immediate="true"
								 action="#{processoSeletivo.formPeriodoAgenda}" />
							</c:when>
							<c:otherwise>
								<h:commandButton value="<< Dados do Processo Seletivo"
								 action="#{processoSeletivo.formDadosProcessoSeletivo}" />
							</c:otherwise>
						</c:choose>						
						<h:commandButton value="Cancelar" onclick="#{confirm}" immediate="true"
							 action="#{processoSeletivo.cancelar}" />
					</td>
				</tr>
			</tfoot>
		</table>
		
	</h:form>
	<br clear="all"/>
	
	<center>
		<h:graphicImage  url="/img/required.gif" style="vertical-align: top;"/> <span class="fontePequena"> Campos de preenchimento obrigatório. </span>
	</center>
	
</f:view>

<script type="text/javascript">
	// Quem quiser usar, deve re-escrever no final da sua jsp
	function cursoOnFocus() {
	}

	function buscarCursoPor(radio) {
		$('nivelAjaxCurso').value = $(radio).value;
		marcaCheckBox(radio);
		$('paramAjaxCurso').focus();
	}
	
	buscarCursoPor('buscaAjaxCursoMedio');
</script>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>